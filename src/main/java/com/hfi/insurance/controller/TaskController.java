package com.hfi.insurance.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.YbFlowInfoMapper;
import com.hfi.insurance.model.*;
import com.hfi.insurance.model.sign.res.SingerInfoRes;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.InstitutionInfoService;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.PicUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
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
    @Autowired
    private YbFlowInfoMapper ybFlowInfoMapper;

    private static List<Integer> flowStatusList = new ArrayList<>();

    static {
        flowStatusList.add(0);
        flowStatusList.add(1);
        flowStatusList.add(9);
        flowStatusList.add(10);

    }

    @RequestMapping(value = "/sign/signedStatusUpdate", method = RequestMethod.POST)
    @ApiOperation("signedStatusUpdate")
    @Scheduled(cron = "0 0/10 * * * * ")
    public void signedStatusUpdate() {
        //流程状态（0草稿，1 签署中，2完成，3 撤销，4终止，5过 期，6删除，7拒 签，8作废，9已归 档，10预盖章）
        QueryWrapper<YbFlowInfo> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.between("update_time",DateUtil.yesterday(),new Date());

        List<YbFlowInfo> list = ybFlowInfoMapper.selectList(objectQueryWrapper);
        for (YbFlowInfo ybFlowInfo : list
        ) {
            JSONObject signDetail = signedService.getSignDetail(ybFlowInfo.getFlowId());

            if (signDetail.containsKey("errCode")) {
                log.warn("查询流程id：{}详情错误，错误原因：{}",ybFlowInfo.getFlowId(),signDetail.getString("msg"));
                continue;
            } else {
                String singers = signDetail.getString("signers");
                List<SingerInfoRes> singerInfos = JSON.parseArray(singers, SingerInfoRes.class);
                log.info("查询流程id：{},signedStatusUpdate List<SingerInfoRes>={}", ybFlowInfo.getFlowId(),JSONObject.toJSONString(singerInfos));
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
                    }else {
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

}