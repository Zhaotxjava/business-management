package com.hfi.insurance.service;

import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.GetRecordInfoBatchReq;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;

/**
 * @Author ChenZX
 * @Date 2021/7/19 17:34
 * @Description:
 */
public interface SignedInfoBizService {

    ApiResponse getSignedRecord(String token,GetRecordInfoReq req);

    @LogAnnotation
    ApiResponse getSignedRecordBatch(String token, GetRecordInfoBatchReq req);

    ApiResponse getSignUrls(GetSignUrlsReq req);

    ApiResponse getSignUrls(GetSignUrlsReq req,String token);

    ApiResponse getPreviewUrl(String fileKey,String docId);

    ApiResponse getSignFlowDocUrls(String signFlowId);
}
