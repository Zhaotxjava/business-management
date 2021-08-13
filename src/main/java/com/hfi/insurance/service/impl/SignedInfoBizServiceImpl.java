package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.FinishDocUrlBean;
import com.hfi.insurance.model.sign.StandardSignDetailSignDoc;
import com.hfi.insurance.model.sign.req.FlowDocBean;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.res.SignRecordsRes;
import com.hfi.insurance.model.sign.res.SignUrlRes;
import com.hfi.insurance.model.sign.res.SingerInfoRes;
import com.hfi.insurance.model.sign.res.StandardSignDetailResult;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.SignedInfoBizService;
import com.hfi.insurance.service.SignedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private Cache<String, String> caffeineCache;

    @Override
    @LogAnnotation
    public ApiResponse getSignedRecord(String token,GetRecordInfoReq req) {
        String jsonStr = caffeineCache.asMap().get(token);
        log.info("用户信息:{}",jsonStr);
        if (StringUtils.isBlank(jsonStr)){
            return new ApiResponse(ErrorCodeEnum.TOKEN_EXPIRED.getCode(),ErrorCodeEnum.TOKEN_EXPIRED.getMessage());
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String institutionNumber = jsonObject.getString("number");
        Page<YbFlowInfo> flowInfoPage = flowInfoService.getSignedRecord(institutionNumber,req);
        Page<SignRecordsRes> signRecordsResPage = new Page<>();
        BeanUtils.copyProperties(flowInfoPage, signRecordsResPage);
        List<YbFlowInfo> flowInfos = flowInfoPage.getRecords();
        List<SignRecordsRes> recordResList = new ArrayList<>();
        for(YbFlowInfo ybFlowInfo : flowInfos){
            SignRecordsRes recordsRes = new SignRecordsRes();
            BeanUtils.copyProperties(ybFlowInfo, recordsRes);
            String flowId = ybFlowInfo.getSignFlowId();
            JSONObject signDetail = signedService.getSignDetail(Integer.valueOf(flowId));
            log.info("流程id：{}，详情：{}", flowId, signDetail);
            if (signDetail.containsKey("errCode")) {
                log.error("查询流程详情错误，错误原因：{}",signDetail.getString("msg"));
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
                Optional<SingerInfoRes> any = singerInfos.stream().filter(singerInfoRes -> institutionInfo.getAccountId().equals(singerInfoRes.getAccountId())).findAny();
                if (any.isPresent()) {
                    SingerInfoRes singerInfoRes = any.get();
                    recordsRes.setSubject(signDetail.getString("subject"));
                    recordsRes.setSignStatus(singerInfoRes.getSignStatus());
                    recordsRes.setFlowStatus(signDetail.getInteger("flowStatus"));
//                    recordsRes.setFileKey();
                    recordsRes.setAccountType(2);
                    recordsRes.setAccountId(institutionInfo.getAccountId());
                }
            }
            recordsRes.setInitiateTime(ybFlowInfo.getInitiatorTime());
            recordsRes.setRecentHandleTime(ybFlowInfo.getHandleTime());
            recordResList.add(recordsRes);
        }
        signRecordsResPage.setRecords(recordResList);
        signRecordsResPage.setTotal(recordResList.size());
        return new ApiResponse(signRecordsResPage);
    }

    @Override
    //@LogAnnotation
    public ApiResponse getSignUrls(GetSignUrlsReq req) {
        log.info("获取签署地址列表请求参数：{}", JSON.toJSONString(req));
        JSONObject signUrls = signedService.getSignUrls(req);
        log.info("获取签署地址列表响应参数：{}", signUrls);
        if (signUrls.getBoolean("success")) {
            String signUrlsStr = signUrls.getString("signUrlList");
            List<SignUrlRes> signUrlRes = JSON.parseArray(signUrlsStr, SignUrlRes.class);
            return new ApiResponse(signUrlRes);
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
        }
    }

    @Override
    @LogAnnotation
    public ApiResponse getPreviewUrl(String fileKey, String docId) {
        JSONObject previewUrl = signedService.getPreviewUrl(fileKey, docId);
        log.info("获取文档预览的URL响应参数：{}", previewUrl);
        if (previewUrl.getBoolean("success")) {
            String url = previewUrl.getString("url");
            return new ApiResponse(url);
        } else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
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
        List<String> urls = flowDocBeans.stream().map(FinishDocUrlBean::getDownloadDocUrl).collect(Collectors.toList());
        return new ApiResponse(urls);
    }

}
