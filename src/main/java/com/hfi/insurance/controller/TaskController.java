package com.hfi.insurance.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.Cons;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.YbCoursePlMapper;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.*;
import com.hfi.insurance.model.dto.SignedStatusUpdateByDateReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionReq;
import com.hfi.insurance.model.sign.res.DownloadDo;
import com.hfi.insurance.model.sign.res.SingerInfoRes;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.InstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.PicUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jthealth-NZH
 * @Date 2021/9/17 10:17
 * @Describe
 * @Version 1.0
 */
@Slf4j
@Api("定时任务")
@RestController
public class TaskController {
    @Autowired
    private SignedService signedService;

    @Resource
    private YbFlowInfoMapper ybFlowInfoMapper;

    @Resource
    private OrganizationsService rganizationsService;

    @Resource
    private YbCoursePlMapper ybCoursePlMapper;


    @Value("${esignpro.port}")
    private String port;

    private static List<Integer> flowStatusList = new ArrayList<>();

    private long MAX_TIMEOUT = 72000000;

    static {
        flowStatusList.add(0);
        flowStatusList.add(1);
        flowStatusList.add(9);
        flowStatusList.add(10);

    }

    @RequestMapping(value = "/sign/signedStatusUpdateByDate", method = RequestMethod.POST)
    @ApiOperation("signedStatusUpdateByDate")
    public void signedStatusUpdateByDate(@RequestBody SignedStatusUpdateByDateReq req) {
        signedStatusUpdate(req);
    }

    @RequestMapping(value = "/sign/signedStatusUpdate", method = RequestMethod.POST)
    @ApiOperation("signedStatusUpdate")
    @Scheduled(cron = "0 0/15 * * * * ")
    public void signedStatusUpdate() {
        SignedStatusUpdateByDateReq req = new SignedStatusUpdateByDateReq();
        req.setStart(DateUtil.yesterday());
        req.setEnd(new Date());
        signedStatusUpdate(req);
    }

    public void signedStatusUpdate(SignedStatusUpdateByDateReq req){
        //流程状态（0草稿，1 签署中，2完成，3 撤销，4终止，5过 期，6删除，7拒 签，8作废，9已归 档，10预盖章）
        QueryWrapper<YbFlowInfo> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.and(i -> i.isNull("batch_status").or().eq("batch_status", Cons.BatchStr.BATCH_STATUS_SUCCESS));
        if(Objects.nonNull(req.getStart()) && Objects.nonNull(req.getStart())){
            objectQueryWrapper.between("update_time", req.getStart(), req.getEnd());
        }
//        objectQueryWrapper.eq("flow_status","0").or().eq("flow_status","1")
//                .or().eq("flow_status","10");
        objectQueryWrapper.in("flow_status",flowStatusList);
        if(StringUtils.isNotBlank(req.getSignFlowId())){
            objectQueryWrapper.eq("sign_flow_id",req.getSignFlowId());
        }

        List<YbFlowInfo> list = ybFlowInfoMapper.selectList(objectQueryWrapper);
        log.info("定时查询签署一共：{}个,入参为：{}", list.size(),JSONObject.toJSONString(req));
        for (YbFlowInfo ybFlowInfo : list
        ) {
            JSONObject signDetail = signedService.getSignDetail(Integer.valueOf(ybFlowInfo.getSignFlowId()));
//            log.debug("signDetail = {}",signDetail);
            if (signDetail.containsKey("errCode")) {
                log.warn("查询流程id：{}详情错误，错误原因：{}",ybFlowInfo.getFlowId(),signDetail.getString("msg"));
                continue;
            } else {
                String singers = signDetail.getString("signers");
                List<SingerInfoRes> singerInfos = JSON.parseArray(singers, SingerInfoRes.class);
//                log.info("查询流程id：{},signedStatusUpdate List<SingerInfoRes>={}", ybFlowInfo.getFlowId(),JSONObject.toJSONString(singerInfos));
//                YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(ybFlowInfo.getNumber());
//
//                if (institutionInfo != null && institutionInfo.getAccountId() != null) {
//                    Optional<SingerInfoRes> any = singerInfos.stream().filter(singerInfoRes -> institutionInfo.getAccountId().equals(singerInfoRes.getAccountId())).findAny();
//                    if (any.isPresent()) {
//                        SingerInfoRes singerInfoRes = any.get();
//                        ybFlowInfo.setFlowStatus(signDetail.getInteger("flowStatus"));
//                        ybFlowInfo.setSignStatus(singerInfos.get(0).getSignStatus());
//                        ybFlowInfo.setSignStatus(singerInfoRes.getSignStatus());
//                    }
//                }

                int count2 = 0;
                String status = "0";
                for (SingerInfoRes singer : singerInfos
                ) {
                    //签署状态（0待签署，1签署中，2 完成，3中止/失败）
                    // 如果有一个人处于0待签署，那先赋值给待签署
                    if ("0".equals(singer.getSignStatus()) && "0".equals(status)) {
                        status = singer.getSignStatus();
                        // 如果有一个人处于1签署中，那整个流程都是签署中
                    } else if ("1".equals(singer.getSignStatus())) {
                        status = singer.getSignStatus();
                        continue;
                    } else if ("2".equals(singer.getSignStatus())) {
//                        status = singer.getSignStatus();
                        count2++;
                    } else if ("3".equals(singer.getSignStatus())) {
                        status = singer.getSignStatus();
                        break;
                    }
                    //所有人都完成签署
                    if (count2 == singerInfos.size()) {
                        ybFlowInfo.setSignStatus("2");
                    } else {
                        ybFlowInfo.setSignStatus(status);
                    }
//                    switch (singer.getSignStatus()) {
//                        case "0":
//                            count0++;
//                            break;
//                        case "1":
//                            count1++;
//                            break;
//                        case "2":
//                            count2++;
//                            break;
//                        case "3":
//                            count3++;
//                            break;
//                    }
//                    if (count2 > 0 && count1 == 0 && count0 == 0) {
//                        ybFlowInfo.setSignStatus("2");
//                    }
                }

                ybFlowInfo.setFlowStatus(signDetail.getInteger("flowStatus"));
                log.info("获取状态后：ybFlowInfo.SignStatus={},ybFlowInfo.FlowStatus={}"
                        , ybFlowInfo.getSignStatus(), ybFlowInfo.getFlowStatus());
                ybFlowInfoMapper.updateById(ybFlowInfo);
            }
        }
    }

    @RequestMapping(value = "/pic/cleanPicCommitMap", method = RequestMethod.POST)
    @ApiOperation("cleanPicCommitMap")
    @Scheduled(cron = "0 0 */2 * * ?")
    public void cleanPicCommitMap() {
        PicUploadUtil.picCommitPath.forEach((k, v) -> {
            if (System.currentTimeMillis() - v.getCreateTime() > MAX_TIMEOUT) {
                PicUploadUtil.picCommitPath.remove(k);
            }
        });
    }

    @SneakyThrows
    @Scheduled(cron = "0 0/1 * * * * ")
    @ApiOperation("定时任务，10分钟查询一次文件下载地址。")
    public  void   findProcessBatchDownload(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH,-7);
        Date time = now.getTime();
        List<YbCoursePl> YbCoursePlList=ybCoursePlMapper.selectybCoursePlList(sdf.parse(sdf.format(new Date())),sdf.parse(sdf.format(time)));
        if (!YbCoursePlList.isEmpty()){
            YbCoursePlList.stream().forEach(x ->{
                JSONObject processBatchDownload = rganizationsService.findProcessBatchDownload(x.getCourseId());
                System.out.println(processBatchDownload);
                Object processCount = processBatchDownload.get("processCount");
                Object downloadDOList = processBatchDownload.get("downloadDOList");
                List<DownloadDo> downloadDos = JSONObject.parseArray(JSONObject.toJSONString(downloadDOList), DownloadDo.class);
                List<String> urlList=new ArrayList<>();
                downloadDos.stream().forEach(y ->{
                    urlList.add(y.getDownloadUrl());
                });
                if (!urlList.isEmpty()){
                    x.setUrlList(testList(urlList));
                    x.setCourseStatus("1");
                }
                x.setCourseCount(processCount.toString());
                try {
                    x.setUpdateTime(sdf.parse(sdf.format(new Date())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ybCoursePlMapper.updateById(x);
            });
        }

    }
    public  String  testList(List<String> list){
        String  lists ="";
        for (String s:list){
            //http://192.20.97.42:8030/rest
            String[] split = s.split("http://192.20.97.42:8030/rest");
            System.out.println(split[0]+"-------"+split[1]);
            String urls= port+"/rest"+split[1];
            lists += urls +",";
        }
        String substring = lists.substring(0, lists.length() - 1);
        return  substring;
    }

    @SneakyThrows
    @Scheduled(cron = "0 0/10 * * * * ")
    @ApiOperation("定时任务，10分钟删除超过七天无效数据")
    public  void   findProcessBatchDownload2(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH,-7);
        ybCoursePlMapper.delectCoursePlList(sdf.parse(sdf.format(now.getTime())));
    }

}
