package com.hfi.insurance.mapper;

import com.hfi.insurance.model.YbOrgTd;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfi.insurance.model.dto.InstitutionInfoQueryReq;

import java.util.List;

/**
 * <p>
 * 定点医疗服务机构信息 Mapper 接口
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
public interface YbOrgTdMapper extends BaseMapper<YbOrgTd> {
    List<YbOrgTd> getorgTdbxList(InstitutionInfoQueryReq institutionInfoQueryReq);

    Integer selectorgTdbxCount(InstitutionInfoQueryReq institutionInfoQueryReq);

    YbOrgTd selectByIdYbOrgTd(String number);
}
