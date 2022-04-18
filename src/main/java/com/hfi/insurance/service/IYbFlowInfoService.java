package com.hfi.insurance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoBatchReq;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.res.GetSignedRecordBatchRes;

import java.util.List;

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

    GetSignedRecordBatchRes getSignedRecord(String institutionNumber, GetRecordInfoBatchReq req);

    List<YbFlowInfo> getSignedRecordByAreaCode(String institutionNumber);

    Page<YbFlowInfo> getSignedRecordByBatchDownload(String institutionNumber, GetRecordInfoReq req);

	Integer getSignedRecordCount(String institutionNumber, GetRecordInfoReq req);

	int updateFlowStatusBySignFlowId(Integer status, String signFlowId);

}
