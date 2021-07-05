package com.hfi.insurance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 定点机构信息 服务类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
public interface IYbInstitutionInfoService extends IService<YbInstitutionInfo> {
    Page<YbInstitutionInfo> getInstitutionInfoList(String number,String institutionName,int current,int limit);

}
