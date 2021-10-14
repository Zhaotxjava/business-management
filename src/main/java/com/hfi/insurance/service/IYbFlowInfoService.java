package com.hfi.insurance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
/**
 * <p>
 * 签署流程记录 服务类
 * </p>
 *
 * @author NZH
 * @since 2021-09-29
 */
public interface IYbFlowInfoService extends IService<YbFlowInfo> {
    Page<YbFlowInfo> getSignedRecord(String institutionNumber,GetRecordInfoReq req);

    Integer getSignedRecordCount(String institutionNumber, GetRecordInfoReq req);
}
