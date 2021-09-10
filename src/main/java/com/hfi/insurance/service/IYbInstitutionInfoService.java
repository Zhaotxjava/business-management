package com.hfi.insurance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.common.PageDto;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hfi.insurance.model.YbInstitutionInfoChange;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.model.dto.YbInstitutionInfoChangeReq;
import com.hfi.insurance.model.dto.res.InstitutionInfoRes;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 定点机构信息 服务类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
public interface IYbInstitutionInfoService extends IService<YbInstitutionInfo> {
    /**
     * 分页查询外部机构信息
     * @param number
     * @param institutionName
     * @param current
     * @param limit
     * @return
     */
    ApiResponse getInstitutionInfoList(String token,String number, String institutionName, int current, int limit);

    Page<InstitutionInfoRes> getOrgTdListForCreateFlow(OrgTdQueryReq req);

    YbInstitutionInfo getInstitutionInfo(String number);

    ApiResponse updateInstitutionInfo(InstitutionInfoAddReq req);

    void addYbInstitutionInfoChange(YbInstitutionInfoChange ybInstitutionInfoChange);

    ApiResponse getInstitutionInfoChangeList(YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq);
}
