package com.hfi.insurance.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.mapper.YbOrgTdMapper;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.service.IYbOrgTdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
        if (StringUtils.isNotBlank(req.getAKB020())){
            queryWrapper.eq("AKB020",req.getAKB020());
        }
        if (StringUtils.isNotBlank(req.getAKB021())){
            queryWrapper.eq("AKB021",req.getAKB021());
        }
        if (StringUtils.isNotBlank(req.getAKB022()) && StringUtils.isNotBlank(req.getAKA101())
        && StringUtils.isNotBlank(req.getAAA027()) && StringUtils.isNotBlank(req.getBKA938())){
            queryWrapper.in("AKA101",req.getAKA101())
                    .in("AKB022",req.getAKB022())
                    .in("AAA027",req.getAAA027())
                    .in("BKA938",req.getBKA938());
        }
        Page<YbOrgTd> page = new Page<>(req.getPageNum(),req.getPageSize());
        return baseMapper.selectPage(page, queryWrapper);
    }
}
