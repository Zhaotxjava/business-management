package com.hfi.insurance.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.mapper.YbInstitutionInfoMapper;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.utils.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 定点机构信息 服务实现类
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Slf4j
@Service
public class YbInstitutionInfoServiceImpl extends ServiceImpl<YbInstitutionInfoMapper, YbInstitutionInfo> implements IYbInstitutionInfoService {

    @Resource
    private YbInstitutionInfoMapper institutionInfoMapper;

    @Autowired
    private OrganizationsService organizationsService;

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

    @Override
    public YbInstitutionInfo getInstitutionInfo(String number) {
        QueryWrapper<YbInstitutionInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("number",number);
        return institutionInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public ApiResponse updateInstitutionInfo(InstitutionInfoAddReq req) {
        YbInstitutionInfo cacheInfo = this.getInstitutionInfo(req.getNumber());
        // 2>通过天印系统查询联系人是否已存在于系统，不存在则调用创建用户接口，得到用户的唯一编码，存在则直接跳到第4步
        boolean accountExist = true;
        boolean organExist = true;
        boolean isSameAccount = true;  //法人和经办人是否同一个
        String accountId = "";
        String defaultAccountId = ""; //法人默认经办人
        String organizeId = "";
        if (!req.getLegalIdCard().equals(req.getContactIdCard())) {
            isSameAccount = false;
        }
        // 判定法人是否已存在系统用户
        JSONObject accountObj = organizationsService.queryAccounts("", req.getLegalIdCard());
        if (accountObj.containsKey("errCode")) {
            if ("-1".equals(accountObj.getString("errCode"))) {
                accountExist = false;
            } else {
                log.error("查询外部用户（法人）信息异常，{}", accountObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), accountObj.getString("msg"));
            }
        }
        if (!accountExist) { //不存在则创建用户
            JSONObject resultObj = organizationsService.createAccounts(req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());

            if (resultObj.containsKey("errCode")) {
                log.error("创建外部用户（法人）信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            defaultAccountId = resultObj.getString("accountId");
        } else {
            defaultAccountId = accountObj.getString("accountId");
            JSONObject resultObj = organizationsService.updateAccounts(defaultAccountId, req.getLegalName(), req.getLegalIdCard(), req.getLegalPhone());
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户（法人）信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
        }
        if (!isSameAccount) {
            log.info("法人和经办人信息不一致，再创建联系人为经办人");
            accountObj = organizationsService.queryAccounts("", req.getContactIdCard());
            if (accountObj.containsKey("errCode")) {
                if ("-1".equals(accountObj.getString("errCode"))) {
                    accountExist = false;
                } else {
                    log.error("查询外部用户（联系人）信息异常，{}", accountObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), accountObj.getString("msg"));
                }
            }
            if (!accountExist) { //不存在则创建用户
                JSONObject resultObj = organizationsService.createAccounts(req.getContactName(), req.getContactIdCard(), req.getContactPhone());
                if (resultObj.containsKey("errCode")) {
                    log.error("创建外部用户（联系人）信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
                accountId = resultObj.getString("accountId");
            } else {
                accountId = accountObj.getString("accountId");
                JSONObject resultObj = organizationsService.updateAccounts(accountId, req.getContactName(), req.getContactIdCard(), req.getContactPhone());
                if (resultObj.containsKey("errCode")) {
                    log.error("更新外部用户（联系人）信息异常，{}", resultObj);
                    return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
                }
            }
        }
        // 3>调用天印系统查询该机构是否已存在系统，不存在则调用创建外部机构接口，存在则调用更新外部机构信息接口
        JSONObject organObj = organizationsService.queryOrgans("", req.getNumber());
        if (organObj.containsKey("errCode")) {
            if ("-1".equals(organObj.getString("errCode"))) {
                organExist = false;
            } else {
                log.error("查询外部机构信息异常，{}", organObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), organObj.getString("msg"));
            }
        }
        InstitutionInfo institutionInfo = new InstitutionInfo();
        BeanUtils.copyProperties(req, institutionInfo);
        institutionInfo.setInstitutionName(cacheInfo.getInstitutionName());
        if (!organExist) { //不存在则创建机构
            institutionInfo.setAccountId(defaultAccountId); //创建默认经办人
            JSONObject resultObj = organizationsService.createOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("创建外部机构信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
            organizeId = resultObj.getString("organizeId");
        } else {
            //更新机构信息
            organizeId = organObj.getString("organizeId");
            if (defaultAccountId.equals(organObj.getString("agentAccountId"))) {
                log.error("法人信息已变更，系统暂不支持接口");
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), "法人信息已变更，系统暂不支持接口更新");
            }
            institutionInfo.setOrganizeId(organizeId);
            JSONObject resultObj = organizationsService.updateOrgans(institutionInfo);
            if (resultObj.containsKey("errCode")) {
                log.error("更新外部用户信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
        }
        if (!isSameAccount) {
            JSONObject resultObj = null;
            if (StringUtils.isNotBlank(cacheInfo.getAccountId()) && !StringUtils.equals(cacheInfo.getAccountId(), accountId)) {
                resultObj = organizationsService.unbindAgent(organizeId, institutionInfo.getNumber(), cacheInfo.getAccountId(), "");
                if (resultObj.containsKey("errCode")) {
                    log.error("外部机构解绑经办人信息异常，{}", resultObj);
                }
            }
            resultObj = organizationsService.bindAgent(organizeId, institutionInfo.getNumber(), accountId, institutionInfo.getContactIdCard());
            if (resultObj.containsKey("errCode")) {
                log.error("外部机构绑定经办人信息异常，{}", resultObj);
                return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), resultObj.getString("msg"));
            }
        } else {
            accountId = defaultAccountId;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        institutionInfo.setAccountId(accountId);
        institutionInfo.setOrganizeId(organizeId);
        //institutionInfo.setOrganizeId("12312");
        institutionInfo.setUpdateTime(df.format(new Date()));
        // 4>机构创建完成以后，更新数据库

        return new ApiResponse(ErrorCodeEnum.SUCCESS);
    }
}
