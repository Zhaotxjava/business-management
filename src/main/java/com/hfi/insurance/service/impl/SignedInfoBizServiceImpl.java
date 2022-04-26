package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.Cons;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.YbCoursePlMapper;
import com.hfi.insurance.model.GetArecordReq;
import com.hfi.insurance.model.YbCoursePl;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.FinishDocUrlBean;
import com.hfi.insurance.model.sign.req.GetRecordInfoBatchReq;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.res.SignRecordsRes;
import com.hfi.insurance.model.sign.res.SignUrlRes;
import com.hfi.insurance.model.sign.res.SingerInfoRes;
import com.hfi.insurance.service.*;
import com.hfi.insurance.utils.GuuidUtil;
import lombok.SneakyThrows;
import com.hfi.insurance.model.sign.res.*;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.SignedInfoBizService;
import com.hfi.insurance.service.SignedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author ChenZX
 * @Date 2021/7/19 18:21
 * @Description:
 */
@Slf4j
@Service
public class SignedInfoBizServiceImpl implements SignedInfoBizService {

    @Resource
    private IYbFlowInfoService flowInfoService;

    @Resource
    private SignedService signedService;

    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @Resource
    private YbCoursePlMapper ybCoursePlMapper;

    @Resource
    private Cache<String, String> caffeineCache;


    @Resource
    private OrganizationsService rganizationsService;

    @Value("${esignpro.urls}")
    private String urls;

    @Override
    @LogAnnotation
    public ApiResponse getSignedRecord(String token, GetRecordInfoReq req) {
        String jsonStr = caffeineCache.asMap().get(token);
        log.info("用户信息:{}", jsonStr);
        if (StringUtils.isBlank(jsonStr)) {
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");
        Page<YbFlowInfo> flowInfoPage = flowInfoService.getSignedRecord(institutionNumber, req);
//        Integer signedRecordCount = flowInfoService.getSignedRecordCount(institutionNumber, req);
        Page<SignRecordsRes> signRecordsResPage = new Page<>();
        BeanUtils.copyProperties(flowInfoPage, signRecordsResPage);
        List<YbFlowInfo> flowInfos = flowInfoPage.getRecords();
        List<SignRecordsRes> recordResList = new ArrayList<>();
        for (YbFlowInfo ybFlowInfo : flowInfos) {
            SignRecordsRes recordsRes = new SignRecordsRes();
            BeanUtils.copyProperties(ybFlowInfo, recordsRes);
            //20211103 去除抄送人
            recordsRes.setCopyViewers(" ");
            String signFlowId = ybFlowInfo.getSignFlowId();
            JSONObject signDetail = signedService.getSignDetail(Integer.valueOf(signFlowId));
            log.info("流程id：{}，详情：{}", signFlowId, signDetail);
            if (signDetail.containsKey("errCode")) {
                log.error("查询流程详情错误，错误原因：{}", signDetail.getString("msg"));
                continue;
            }
            String singers = signDetail.getString("signers");
            List<SingerInfoRes> singerInfos = JSON.parseArray(singers, SingerInfoRes.class);
            //String signDocDetails = signDetail.getString("signDocDetails");
            //List<StandardSignDetailSignDoc> standardSignDetailSignDocs = JSON.parseArray(signDocDetails, StandardSignDetailSignDoc.class);
            //StandardSignDetailSignDoc signDetailSignDoc = CollectionUtils.firstElement(standardSignDetailSignDocs);
            //匹配用户，填充信息
            String number = ybFlowInfo.getNumber();
            YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(number);
            log.info("机构信息：{}", JSON.toJSONString(institutionInfo));
            if (institutionInfo != null && institutionInfo.getAccountId() != null) {
//                recordsRes.setAccountId(institutionInfo.getAccountId());
                //此处两行原institutionInfo.getAccountId(),现在改为getLegalAccountId
                if (!number.startsWith(Cons.NumberStr.BX)) {
                    //如果不是保险机构，则用法人来查看状态
                    Optional<SingerInfoRes> any2 = singerInfos.stream().filter(singerInfoRes -> institutionInfo.getLegalAccountId().equals(singerInfoRes.getAccountId())).findAny();
                    if (any2.isPresent()) {
                        SingerInfoRes singerInfoRes = any2.get();
                        recordsRes.setSignStatus(singerInfoRes.getSignStatus());
//                    recordsRes.setFileKey();
                        recordsRes.setAccountId(institutionInfo.getLegalAccountId());
                    }
                } else {
                    Optional<SingerInfoRes> any = singerInfos.stream().filter(singerInfoRes -> institutionInfo.getAccountId().equals(singerInfoRes.getAccountId())).findAny();
                    if (any.isPresent()) {
                        SingerInfoRes singerInfoRes = any.get();
//                        recordsRes.setSubject(signDetail.getString("subject"));
                        recordsRes.setSignStatus(singerInfoRes.getSignStatus());
//                    recordsRes.setFileKey();
                        recordsRes.setAccountId(institutionInfo.getAccountId());
                    }
                }
                recordsRes.setAccountType(2);
                recordsRes.setSubject(signDetail.getString("subject"));
            }
            recordsRes.setFlowStatus(signDetail.getInteger("flowStatus"));
            recordsRes.setInitiateTime(ybFlowInfo.getInitiatorTime());
            recordsRes.setRecentHandleTime(ybFlowInfo.getHandleTime());
            recordResList.add(recordsRes);
        }
        BeanUtils.copyProperties(flowInfoPage, signRecordsResPage);
        signRecordsResPage.setRecords(recordResList);

        return new ApiResponse(signRecordsResPage);
    }


    @SneakyThrows
    @Override
    @LogAnnotation
    public ApiResponse getSignedRecordBatch(String token, GetRecordInfoBatchReq req) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        //获取统筹区编码
        String institutionNumber = req.getAreaCode();
        if (institutionNumber.equals("")){
            return   ApiResponse.fail("234","统筹区编码,不能为空!");
        }
        //机构编号  机构名称  模板编号  三选一必填
        if (StringUtils.isEmpty(req.getTemplateId()) && req.getNumbers().isEmpty()){
            return ApiResponse.fail("234","模板编号、机构编号、机构名称必填其中一个!");
        }
        if (StringUtils.isEmpty(req.getBeginInitiateTime()) && StringUtils.isEmpty(req.getEndInitiateTime())){
            return  ApiResponse.fail("234","条件时间必填!");
        }
        GetSignedRecordBatchRes result = flowInfoService.getSignedRecord(institutionNumber, req);
        log.info("result = {}",JSONObject.toJSONString(result));
        if (req.getPlType().equals("PLPD")){
            Map<String, Set<String>> List=new HashMap<>();
            List.put("successSet",result.getSuccessSet());
            List.put("failSet",result.getFailSet());
            return new ApiResponse("404",null,List);
        }
        if (result.getSignFlowIdSet().isEmpty()){
            return ApiResponse.fail("234","该筛选条件下没有流程，请重新选择筛选条件!");
        }
        //拼成文件名称 统筹区编码-导出时间.zip   生产id
        String fileName = institutionNumber + "-" + sdf.format(new Date());
        //存入数据库
        YbCoursePl ybCoursePl = new YbCoursePl();
        String id = String.valueOf(GuuidUtil.getUUID());
        ybCoursePl.setOrderId(id);
        ybCoursePl.setCourseId(id);
        ybCoursePl.setCourseFileName(fileName);
        ybCoursePl.setCourseFileDate(sdf3.parse(sdf3.format(new Date())));
        ybCoursePl.setMbNumber(req.getTemplateId());
        ybCoursePl.setAgreeDate(sdf2.format(sdf2.parse(req.getBeginInitiateTime()))+"~"+sdf2.format(sdf2.parse(req.getEndInitiateTime())));
        if (req.getQueryType().equals("SIGNLE_NUMBER") || req.getQueryType().equals("NUMBERS")){
            ybCoursePl.setNumber(testList(req.getNumbers()));
        }else if (req.getQueryType().equals("SIGNLE_NAME") || req.getQueryType().equals("NAMES")){
            ybCoursePl.setInstitutionName(testList(req.getNumbers()));
        }
        if (result.getFailSet().size()>0){
            ybCoursePl.setRemarks("有"+result.getFailSet().size()+"家机构不存在完成部署的文件。"+testList2(result.getSuccessSet())+"存在完成部署的文件。");
        }else {
            ybCoursePl.setRemarks("完成全部部署!");
        }

        ybCoursePl.setCourseStatus("0");
        ybCoursePl.setCreateTime(sdf3.parse(sdf3.format(new Date())));
        ybCoursePl.setAreaCode(institutionNumber);
        ybCoursePl.setUpdateTime(sdf3.parse(sdf3.format(new Date())));
        ybCoursePlMapper.insertybCoursePl(ybCoursePl);
        Set<String> signFlowIdSet = result.getSignFlowIdSet();
        JSONObject jsonObject = rganizationsService.processBatchDownload(ybCoursePl.getCourseId(), ybCoursePl.getCourseFileName(),signFlowIdSet);
        return new ApiResponse(jsonObject);
    }


    public  String  testList(List<String> list){
        String  lists ="";
        for (String s:list){
            lists += s +",";
        }
        String substring = lists.substring(0, lists.length() - 1);

        return  substring;
    }

    public  String  testList2(Set<String> list){
        String  lists ="";
        int i=0;
        for (String s:list){
            if(i<=100){
                lists += s +",";
            }
            i++;
        }
        return  lists.substring(0, lists.length() - 1);
    }

    @Override
    //@LogAnnotation
    public ApiResponse getSignUrls(GetSignUrlsReq req) {
        log.info("获取签署地址列表请求参数：{}", JSON.toJSONString(req));
        JSONObject signUrls = signedService.getSignUrls(req);
        log.info("获取签署地址列表响应参数：{}", signUrls);
        if ("-1".equals(signUrls.getString("errCode"))) {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), signUrls.getString("msg"));
        } else {
            String signUrlsStr = signUrls.getString("signUrlList");
            List<SignUrlRes> signUrlRes = JSON.parseArray(signUrlsStr, SignUrlRes.class);
            return new ApiResponse(signUrlRes);
        }
    }

    @Override
    //@LogAnnotation
    /**
     * 为了将bumber为bx开头的机构入参accountId置换成LegalAccountId
     */
    public ApiResponse getSignUrls(GetSignUrlsReq req, String token) {
        if (StringUtils.isBlank(token)) {
            return getSignUrls(req);
        }
        String jsonStr = caffeineCache.asMap().get(token);
        log.info("用户信息:{}", jsonStr);
        if (StringUtils.isBlank(jsonStr)) {
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(), ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");
        if (!(StringUtils.isNotBlank(institutionNumber) && institutionNumber.startsWith("bx"))) {
            YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(institutionNumber);
            if (Objects.nonNull(institutionInfo) && StringUtils.isNotBlank(institutionInfo.getLegalAccountId())) {
                log.info("getSignUrls 更改非保险机构[{}]的accountId [{}] 为legalAccountId [{}] ,"
                        , institutionNumber, req.getAccountId(), institutionInfo.getLegalAccountId());
                req.setAccountId(institutionInfo.getLegalAccountId());
            }
        }
        return getSignUrls(req);
    }

    @Override
    @LogAnnotation
    public ApiResponse getPreviewUrl(String fileKey, String docId) {
        JSONObject previewUrl = signedService.getPreviewUrl(fileKey, docId);
        log.info("获取文档预览的URL响应参数：{}", previewUrl);
        if ("-1".equals(previewUrl.getString("errCode"))) {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR.getCode(), previewUrl.getString("msg"));
        } else {
            String url = previewUrl.getString("url");
            String[] split = url.split("/doc-web");
            log.info("更新后url" + urls + "/doc-web" + split[1]);
            return new ApiResponse(urls + "/doc-web" + split[1]);
        }
    }

    @Override
    public ApiResponse getSignFlowDocUrls(String signFlowId) {
        JSONObject signFlowDocUrls = signedService.getSignFlowDocUrls(signFlowId);
        if (signFlowDocUrls.containsKey("errCode")) {
            log.error("获取签署流程文档下载地址异常，{}", signFlowDocUrls);
            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), signFlowDocUrls.getString("msg"));
        }
        String signDocUrlListStr = signFlowDocUrls.getString("signDocUrlList");
        List<FinishDocUrlBean> flowDocBeans = JSON.parseArray(signDocUrlListStr, FinishDocUrlBean.class);
        List<String> urls2 = flowDocBeans.stream().map(FinishDocUrlBean::getDownloadDocUrl).collect(Collectors.toList());
        log.info(urls2.toString());
        //http://192.20.97.42:8030/rest/file-system/operation/download?
        List<String> urlsList = new ArrayList<>();
        urls2.stream().forEach(x -> {
                    String[] split = x.split("/rest");
                    urlsList.add(urls + "/rest" + split[1]);
                }
        );
        log.info("获取签署流程文档下载更新后url" + urlsList);
        return new ApiResponse(urlsList);
    }

    @SneakyThrows
    @Override
    public ApiResponse getSignInfoRecordBatchList(String token, GetRecordInfoBatchReq req) {
        //获取统筹区编码
    /*    if (StringUtils.isEmpty(req.getAreaCode())){
            return ApiResponse.fail("352","统筹区编码不能为空");
        }*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH,-7);
        req.setPageNum((req.getPageNum() - 1) * req.getPageSize());
        List<YbCoursePl> YbCoursePlList= ybCoursePlMapper.selectSignInfoList(req.getAreaCode(),req.getTemplateId(),sdf.parse(sdf.format(new Date())),sdf.parse(sdf.format(now.getTime())),req.getOrderId(),req.getPageNum(),req.getPageSize());
       Integer count = ybCoursePlMapper.selectCounts(req.getAreaCode(),req.getTemplateId(),sdf.parse(sdf.format(new Date())),sdf.parse(sdf.format(now.getTime())),req.getOrderId());
        Page<YbCoursePl> page = new Page<>();
        page.setRecords(YbCoursePlList);
        page.setTotal(count);
        return ApiResponse.success(page);
    }

}
