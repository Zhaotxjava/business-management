package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.Management;
import com.hfi.insurance.model.YbOrgTd;
import com.hfi.insurance.mapper.YbOrgTdMapper;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.service.IYbOrgTdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class YbOrgTdServiceImpl extends ServiceImpl<YbOrgTdMapper, YbOrgTd> implements IYbOrgTdService {


    @Autowired
    private YbOrgTdMapper  ybOrgTdMapper;

    @Override
    public Page<YbOrgTd> getOrgTdList(OrgTdQueryReq req) {
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("AAA027", "3301");
        if (StringUtils.isNotBlank(req.getNumber())) {
            queryWrapper.eq("AKB020", req.getNumber());
        }
        if (StringUtils.isNotBlank(req.getInstitutionName())) {
            queryWrapper.eq("AKB021", req.getInstitutionName());
        }
        if (CollectionUtils.isNotEmpty(req.getInstitutionTypes()) && CollectionUtils.isNotEmpty(req.getInstitutionLevels())
                && CollectionUtils.isNotEmpty(req.getAreas()) && CollectionUtils.isNotEmpty(req.getProfits())) {
            queryWrapper.in("AKA101", req.getInstitutionLevels())
                    .in("AKB022", req.getInstitutionTypes())
                    .in("AAA027", req.getAreas())
                    .in("BKA938", req.getProfits());
        }
        Page<YbOrgTd> page = new Page<>(req.getPageNum(), req.getPageSize());
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<YbOrgTd> getYbOrgTdList(List<String> number) {
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("AKB020", number);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public YbOrgTd getYbOrgTdByNumber(String number) {
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("AKB020", number);
        List<YbOrgTd> list = baseMapper.selectList(queryWrapper);
        log.info("查询结果：{}", JSONObject.toJSONString(list));
        if (list.isEmpty()) {
            return new YbOrgTd();
        } else {
            return list.get(0);
        }
    }

    @Override
    public ApiResponse getInstitutionsInformation(String number) {

        YbOrgTd ybOrgTd = ybOrgTdMapper.selectByIdYbOrgTd(number);
        Management management = new Management();
        management.setNumber(ybOrgTd.getAkb020());
        management.setInstitutionName(ybOrgTd.getAkb021());
        management.setYbInstitutionType(ybOrgTd.getAkb022());
        management.setYbInstitutionState(ybOrgTd.getBkb012());
        management.setYbInstitutionCoding(ybOrgTd.getAaa027());
        return ApiResponse.success(management);
    }

    @Override
    public ApiResponse addInstitutionsInformation(Management management) {
        QueryWrapper<YbOrgTd> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("AKB020", management.getNumber());
        Integer integer = ybOrgTdMapper.selectCount(queryWrapper);
        if (integer==0){
            YbOrgTd ybOrgTd = new YbOrgTd();
            ybOrgTd.setAkb020(management.getNumber());
            ybOrgTd.setAkb021(management.getInstitutionName());
            ybOrgTd.setAkb022(management.getYbInstitutionType());
            ybOrgTd.setBkb012(management.getYbInstitutionState());
            ybOrgTd.setAaa027(management.getYbInstitutionCoding());
            try {
                baseMapper.insert(ybOrgTd);
            } catch (Exception e) {
                return ApiResponse.fail("406","新增机构失败!");
            }
            return ApiResponse.success("新增成功!");
        }
        return  ApiResponse.fail("200","机构已存在!");
    }

    @Override
    public ApiResponse updateInstitutionsInformation(Management management) {
        YbOrgTd ybOrgTd = new YbOrgTd();
        ybOrgTd.setAkb020(management.getNumber());
        ybOrgTd.setAkb021(management.getInstitutionName());
        ybOrgTd.setAkb022(management.getYbInstitutionType());
        ybOrgTd.setBkb012(management.getYbInstitutionState());
        ybOrgTd.setAaa027(management.getYbInstitutionCoding());
        try {
            ybOrgTdMapper.updateById(ybOrgTd);
        } catch (Exception e) {
            return ApiResponse.fail("406","新增机构修改失败!");
        }
        return  ApiResponse.success("机构修改成功!");
    }

}
