package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.res.SignRecordsRes;
import com.hfi.insurance.model.sign.res.SignUrlRes;
import com.hfi.insurance.model.sign.res.SingerInfoRes;
import com.hfi.insurance.service.IYbFlowInfoService;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.SignedInfoBizService;
import com.hfi.insurance.service.SignedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public ApiResponse getSignedRecord(GetRecordInfoReq req) {
        Page<YbFlowInfo> flowInfoPage = flowInfoService.getSignedRecord(req);
        Page<SignRecordsRes> signRecordsResPage = new Page<>();
        BeanUtils.copyProperties(flowInfoPage,signRecordsResPage);
        List<YbFlowInfo> flowInfos = flowInfoPage.getRecords();
        List<SignRecordsRes> recordRes = flowInfos.stream().map(ybFlowInfo -> {
            SignRecordsRes recordsRes = new SignRecordsRes();
            BeanUtils.copyProperties(ybFlowInfo,recordsRes);
            Integer flowId = ybFlowInfo.getFlowId();
            JSONObject signDetail = signedService.getSignDetail(flowId);
            log.info("流程id：{}，详情：{}",flowId,signDetail);
            String singers = signDetail.getString("singers");
            List<SingerInfoRes> singerInfos = JSON.parseArray(singers, SingerInfoRes.class);
            //匹配用户，填充信息
            String number = ybFlowInfo.getNumber();
            YbInstitutionInfo institutionInfo = institutionInfoService.getInstitutionInfo(number);
            log.info("机构信息：{}",JSON.toJSONString(institutionInfo));
            if (institutionInfo != null && institutionInfo.getAccountId() != null){
                Optional<SingerInfoRes> any = singerInfos.stream().filter(singerInfoRes -> institutionInfo.getAccountId().equals(singerInfoRes.getAccountId())).findAny();
                if (any.isPresent()){
                    SingerInfoRes singerInfoRes = any.get();
                    recordsRes.setSubject(signDetail.getString("subject"));
                    recordsRes.setSignStatus(singerInfoRes.getSignStatus());
                    recordsRes.setFlowStatus(signDetail.getInteger("flowStatus"));
                }
            }
            recordsRes.setRecentHandleTime(ybFlowInfo.getHandleTime());
            return recordsRes;
        }).collect(Collectors.toList());
        signRecordsResPage.setRecords(recordRes);
        return new ApiResponse(signRecordsResPage);
    }

    @Override
    public ApiResponse getSignUrls(GetSignUrlsReq req) {
        JSONObject signUrls = signedService.getSignUrls(req);
        log.info("获取签署地址列表响应参数：{}", signUrls);
        if (signUrls.getBoolean("success")){
            String signUrlsStr = signUrls.getString("signUrlList");
            List<SignUrlRes> signUrlRes = JSON.parseArray(signUrlsStr, SignUrlRes.class);
            return new ApiResponse(signUrlRes);
        }else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
        }
    }

    @Override
    public ApiResponse getPreviewUrl(String fileKey, String docId) {
        JSONObject previewUrl = signedService.getPreviewUrl(fileKey, docId);
        log.info("获取文档预览的URL响应参数：{}", previewUrl);
        if (previewUrl.getBoolean("success")){
            String url = previewUrl.getString("url");
            return new ApiResponse(url);
        }else {
            return new ApiResponse(ErrorCodeEnum.RESPONES_ERROR);
        }
    }
}
