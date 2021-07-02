package com.hfi.insurance.service;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;

/**
 * @Author ChenZX
 * @Date 2021/7/1 17:18
 * @Description:
 */
public interface SignedService {

    /**
     * 分页查询模板信息列表，根据机构分页查询有权限的且已发布的模板
     * @param getPageWithPermissionV2Model
     * @return
     */
    JSONObject getPageWithPermission(GetPageWithPermissionV2Model getPageWithPermissionV2Model);

    //ApiResponse queryByOrgName(int pageIndex,int pageSize,String organizeName);


    /**
     * 创建流程
     * @param standardCreateFlow
     * @return
     */

    JSONObject createSignFlows(StandardCreateFlowBO standardCreateFlow);

    /**
     * 获取签署地址列表
     * @param req
     * @return
     */
    JSONObject getSignUrls(GetSignUrlsReq req);

}
