package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.res.SignRecordsRes;
import com.hfi.insurance.model.sign.res.SingerInfoRes;
import com.hfi.insurance.service.IYbFlowInfoService;
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
            Optional<SingerInfoRes> any = singerInfos.stream().filter(singerInfoRes -> ybFlowInfo.getAccountId().equals(singerInfoRes.getAccountId())).findAny();
            if (any.isPresent()){
                SingerInfoRes singerInfoRes = any.get();
                recordsRes.setSubject(signDetail.getString("subject"));
                recordsRes.setSignStatus(singerInfoRes.getSignStatus());
                recordsRes.setFlowStatus(signDetail.getInteger("flowStatus"));
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
        //signUrls.getString("")
        return null;
    }
}
