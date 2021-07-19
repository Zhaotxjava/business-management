package com.hfi.insurance.service;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;

/**
 * @Author ChenZX
 * @Date 2021/7/19 17:34
 * @Description:
 */
public interface SignedInfoBizService {

    ApiResponse getSignedRecord(GetRecordInfoReq req);
}
