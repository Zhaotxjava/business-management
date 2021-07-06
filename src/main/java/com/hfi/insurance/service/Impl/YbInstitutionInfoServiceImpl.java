package com.hfi.insurance.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfi.insurance.mapper.YbInstitutionInfoMapper;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
        return baseMapper.selectPage(page, queryWrapper);
    }
}
