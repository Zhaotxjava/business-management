package com.hfi.insurance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.mapper.YbOrgTdMapper;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.service.IYbOrgTdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 定点医疗服务机构信息 服务实现类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Service
public class YbOrgTdServiceImpl extends ServiceImpl<YbOrgTdMapper, YbOrgTd> implements IYbOrgTdService {

    @Override
    public Page<YbOrgTd> getOrgTdList(OrgTdQueryReq req) {
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("AAA027","3301");
        if (StringUtils.isNotBlank(req.getNumber())){
            queryWrapper.eq("AKB020",req.getNumber());
        }
        if (StringUtils.isNotBlank(req.getInstitutionName())){
            queryWrapper.eq("AKB021",req.getInstitutionName());
        }
        if (CollectionUtils.isNotEmpty(req.getInstitutionTypes()) && CollectionUtils.isNotEmpty(req.getInstitutionLevels())
        && CollectionUtils.isNotEmpty(req.getAreas()) && CollectionUtils.isNotEmpty(req.getProfits())){
            queryWrapper.in("AKA101",req.getInstitutionLevels())
                    .in("AKB022",req.getInstitutionTypes())
                    .in("AAA027",req.getAreas())
                    .in("BKA938",req.getProfits());
        }
        Page<YbOrgTd> page = new Page<>(req.getPageNum(),req.getPageSize());
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<YbOrgTd> getYbOrgTdList(List<String> number) {
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("AKB020",number);
        return baseMapper.selectList(queryWrapper);
    }

}
