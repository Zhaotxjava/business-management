package com.hfi.insurance.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.mapper.YbInstitutionInfoMapper;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 定点机构信息 服务实现类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Service
public class YbInstitutionInfoServiceImpl extends ServiceImpl<YbInstitutionInfoMapper, YbInstitutionInfo> implements IYbInstitutionInfoService {

    @Resource
    private YbInstitutionInfoMapper institutionInfoMapper;
    @Override
    public Page<YbInstitutionInfo> getInstitutionInfoList(String number, String institutionName, int current, int limit) {
        QueryWrapper<YbInstitutionInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(number)){
            queryWrapper.eq("number",number);
        }
        if (StringUtils.isNotBlank(institutionName)){
            queryWrapper.eq("institution_name",institutionName);
        }
        Page<YbInstitutionInfo> page = new Page<>(current,limit);
        IPage<YbInstitutionInfo> infoPage = baseMapper.selectPage(page, queryWrapper);
        //Page<YbInstitutionInfo> pageRes = new Page<>();
        //BeanUtils.copyProperties(page,pageRes);
        page.setTotal(infoPage.getTotal());
        page.setRecords(infoPage.getRecords());
        return page;
    }
}
