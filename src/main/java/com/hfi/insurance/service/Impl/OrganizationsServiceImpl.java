package com.hfi.insurance.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.utils.HmacSHA256Utils;
import com.hfi.insurance.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrganizationsServiceImpl implements OrganizationsService {

    @Value("${esignpro.url}")
    private String url;
    @Value("${esignpro.projectId}")
    private String projectId;
    @Value("${esignpro.secret}")
    private String secret;

    private void convertHead(Map<String, String> headMap, String message) {
        headMap.put("x-timevale-project-id", projectId);
        String signature = HmacSHA256Utils.hmacSha256(message, secret);
        headMap.put("x-timevale-signature", signature);
    }

    private JSONObject convertResult(String result) {
        JSONObject object = new JSONObject();
        if (StringUtils.isNotEmpty(result)) {
            object = JSONObject.parseObject(result);
            if ("0".equals(object.get("errCode"))) {
                //{"errCode":0,"msg":"success","errShow":true,"data":{"accountId":"8a59fe62-d596-4e53-a56d-22fc732c7642","uniqueId":"220181197708241552","esignAccountId":"54e4e31fbe0a415fba0acf7c39827d75"}}
                return object.getJSONObject("data");
            } else {
                //{"errCode":5006002,"msg":"客户端ip地址非法","errShow":true}
                return object;
            }
        } else {
            object.put("errCode", "9999");
            object.put("msg", "接口响应异常");
            return object;
        }
    }

    @Override
    public JSONObject createAccounts(String name, String idCode, String mobile) {
        Map<String, String> headMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contactsMobile", mobile);
        jsonObject.put("licenseNumber", idCode);
        jsonObject.put("licenseType", "IDCard");
        jsonObject.put("loginMobile", mobile);
        jsonObject.put("name", name);
        jsonObject.put("uniqueId", idCode);
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/accounts/outerAccounts/create", headMap, jsonObject.toJSONString());
        log.info("创建外部用户【{}】接口响应{}", name, result);
        return convertResult(result);
    }

    @Override
    public JSONObject queryAccounts(String accountId, String uniqueId) {
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap, "");
        Map<String, String> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("uniqueId", uniqueId);
        String result = HttpUtil.doGet(url + "/V1/accounts/outerAccounts/query", headMap, params);
        log.info("查询外部用户【{}】接口响应{}", accountId, result);
        return convertResult(result);
    }

    @Override
    public JSONObject updateAccounts(String accountId, String name, String idCode, String mobile) {
        Map<String, String> headMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);
        jsonObject.put("contactsMobile", mobile);
        jsonObject.put("licenseNumber", idCode);
        jsonObject.put("licenseType", "IDCard");
        jsonObject.put("loginMobile", mobile);
        jsonObject.put("name", name);
        jsonObject.put("uniqueId", idCode);
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/accounts/outerAccounts/update", headMap, jsonObject.toJSONString());
        log.info("更新外部用户【{}】接口响应{}", name, result);
        return convertResult(result);
    }

    @Override
    public JSONObject createOrgans(InstitutionInfo institutionInfo) {
        Map<String, String> headMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("agentAccountId", institutionInfo.getAccountId());
        jsonObject.put("legalLicenseNumber", institutionInfo.getLegalIdCard());
        jsonObject.put("legalLicenseType", "IDCard");
        jsonObject.put("legalMobile", institutionInfo.getLegalPhone());
        jsonObject.put("legalName", institutionInfo.getLegalName());
        jsonObject.put("licenseNumber", institutionInfo.getOrgInstitutionCode());
        jsonObject.put("licenseType", "ORANO");
        jsonObject.put("organizeName", institutionInfo.getInstitutionName());
        jsonObject.put("organizeNo", institutionInfo.getNumber());
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/organizations/outerOrgans/create", headMap, jsonObject.toJSONString());
        log.info("创建外部机构【{}】接口响应{}", institutionInfo.getInstitutionName(), result);
        return convertResult(result);
    }

    @Override
    public JSONObject queryOrgans(String organizeId, String organizeNo) {
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap, "");
        Map<String, String> params = new HashMap<>();
        params.put("organizeId", organizeId);
        params.put("organizeNo", organizeNo);
        String result = HttpUtil.doGet(url + "/V1/organizations/outerOrgans/query", headMap, params);
        log.info("查询外部用户【{}】接口响应{}", organizeId, result);
        return convertResult(result);
    }

    @Override
    public JSONObject updateOrgans(InstitutionInfo institutionInfo) {
        Map<String, String> headMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("legalLicenseNumber", institutionInfo.getLegalIdCard());
        jsonObject.put("legalLicenseType", "IDCard");
        jsonObject.put("legalMobile", institutionInfo.getLegalPhone());
        jsonObject.put("legalName", institutionInfo.getLegalName());
        jsonObject.put("licenseNumber", institutionInfo.getOrgInstitutionCode());
        jsonObject.put("licenseType", "ORANO");
        jsonObject.put("organizeName", institutionInfo.getInstitutionName());
        jsonObject.put("organizeId", institutionInfo.getOrganizeId());
        jsonObject.put("organizeNo", institutionInfo.getNumber());
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/organizations/outerOrgans/update", headMap, jsonObject.toJSONString());
        log.info("更新外部机构【{}】接口响应{}", institutionInfo.getInstitutionName(), result);
        return convertResult(result);
    }

    @Override
    public JSONObject bindAgent(String organizeId, String organizeNo, String accountId, String uniqueId) {
        Map<String, String> headMap = new HashMap<>();

        List<JSONObject> agentList = new ArrayList<>();
        JSONObject agent = new JSONObject();
        agent.put("accountId", accountId);
        agent.put("uniqueId", uniqueId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("organizeId", organizeId);
        jsonObject.put("organizeNo", organizeNo);
        jsonObject.put("agentList", agentList);

        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/organizations/outerOrgans/bindAgent", headMap, jsonObject.toJSONString());
        log.info("外部机构【{}】绑定经办人【{}】接口响应{}", organizeId, accountId, result);
        return convertResult(result);
    }

    @Override
    public JSONObject unbindAgent(String organizeId, String organizeNo, String accountId, String uniqueId) {
        Map<String, String> headMap = new HashMap<>();

        List<JSONObject> agentList = new ArrayList<>();
        JSONObject agent = new JSONObject();
        agent.put("accountId", accountId);
        agent.put("uniqueId", uniqueId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("organizeId", organizeId);
        jsonObject.put("organizeNo", organizeNo);
        jsonObject.put("agentList", agentList);

        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/organizations/outerOrgans/unbindAgent", headMap, jsonObject.toJSONString());
        log.info("外部机构【{}】解绑经办人【{}】接口响应{}", organizeId, accountId, result);
        return convertResult(result);
    }
}