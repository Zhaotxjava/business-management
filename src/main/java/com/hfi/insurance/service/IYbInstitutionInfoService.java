package com.hfi.insurance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;

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
    Page<YbInstitutionInfo> getInstitutionInfoList(String number, String institutionName, int current, int limit, HttpSession session);

    YbInstitutionInfo getInstitutionInfo(String number);

    ApiResponse updateInstitutionInfo(InstitutionInfoAddReq req);

}
