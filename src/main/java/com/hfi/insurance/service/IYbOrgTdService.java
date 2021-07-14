package com.hfi.insurance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.YbOrgTd;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hfi.insurance.model.dto.OrgTdQueryReq;

import java.util.List;

/**
 * <p>
 * 定点医疗服务机构信息 服务类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
public interface IYbOrgTdService extends IService<YbOrgTd> {

    Page<YbOrgTd> getOrgTdList(OrgTdQueryReq req);

    List<YbOrgTd> getYbOrgTdList(List<String> number);
}
