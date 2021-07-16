package com.hfi.insurance.service;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionReq;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:39
 * @Description:
 */
public interface SignedBizService {

    ApiResponse createSignFlow(CreateSignFlowReq req);

    ApiResponse getPageWithPermission(GetPageWithPermissionReq req);

    ApiResponse getTemplateInfo(String templateId);
}
