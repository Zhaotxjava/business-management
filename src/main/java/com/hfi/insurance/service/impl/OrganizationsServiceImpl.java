package com.hfi.insurance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.sign.req.QueryInnerAccountsReq;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.utils.HmacSHA256Utils;
import com.hfi.insurance.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
            if ("0".equals(object.getString("errCode"))) {
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
        jsonObject.put("uniqueId", UUID.randomUUID().toString());
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/accounts/outerAccounts/create", headMap, jsonObject.toJSONString());
        log.info("创建外部用户【{}】接口响应{}", name, result);
        return convertResult(result);
    }

    @Override
    public JSONObject listAccounts(String idCode, String mobile) {
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap, "");
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", "1");
        params.put("pageSize", "10");
        if (StringUtils.isNotEmpty(idCode)) {
            params.put("licenseNumber", idCode);
        }
        if (StringUtils.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        String result = HttpUtil.doGet(url + "/V1/accounts/outerAccounts/list", headMap, params);
        log.info("查询外部用户【{} {}】接口响应{}", idCode, mobile, result);
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
    public JSONObject deleteAccounts(String accountId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", accountId);
//        log.info("headMap={};params={}",JSONObject.toJSONString(headMap),JSONObject.toJSONString(params));
//        String result = HttpUtil.doGet(url + "/V1/accounts/outerAccounts/delete", headMap, params);
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/accounts/outerAccounts/delete", headMap, jsonObject.toJSONString());
        log.info("删除外部用户【{}】接口响应{}", accountId, result);
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
        jsonObject.put("legalAccountId", institutionInfo.getLegalAccountId());
        jsonObject.put("legalLicenseNumber", institutionInfo.getLegalIdCard());
        jsonObject.put("legalLicenseType", "IDCard");
        jsonObject.put("legalMobile", institutionInfo.getLegalPhone());
        jsonObject.put("legalName", institutionInfo.getLegalName());
        jsonObject.put("licenseNumber", institutionInfo.getOrgInstitutionCode());
//        jsonObject.put("licenseType", "SOCNO");
        //如果是保险机构，仍使用信用代码
        if (institutionInfo.getNumber().startsWith("bx")) {
            //信用代码
            jsonObject.put("licenseType", "SOCNO");
        } else {
            //营业执照
            jsonObject.put("licenseType", "OTHERNO");
        }
//        jsonObject.put("licenseType", "OTHERNO");
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
        if (StringUtils.isNotBlank(organizeId)) {
            params.put("organizeId", organizeId);
        }
        if (StringUtils.isNotBlank(organizeNo)) {
            params.put("organizeNo", organizeNo);
        }
        String result = HttpUtil.doGet(url + "/V1/organizations/outerOrgans/query", headMap, params);
        log.info("查询外部机构【{}】接口响应{}", organizeId, result);
        return convertResult(result);
    }

    @Override
    public JSONObject updateOrgans(InstitutionInfo institutionInfo) {
        Map<String, String> headMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("legalLicenseNumber", institutionInfo.getLegalIdCard());
        jsonObject.put("legalAccountId", institutionInfo.getLegalAccountId());
        jsonObject.put("legalLicenseType", "IDCard");
        jsonObject.put("legalMobile", institutionInfo.getLegalPhone());
        jsonObject.put("legalName", institutionInfo.getLegalName());
        jsonObject.put("licenseNumber", institutionInfo.getOrgInstitutionCode());
//        jsonObject.put("licenseType", "SOCNO");
        if (institutionInfo.getNumber().startsWith("bx")) {
            jsonObject.put("licenseType", "SOCNO");
        } else {
            jsonObject.put("licenseType", "OTHERNO");
        }
//        jsonObject.put("licenseType", "OTHERNO");
//        jsonObject.put("licenseType", "CRED_ORG_UNKNOWN");
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
        return bindAgent(organizeId, organizeNo, accountId, uniqueId, "0");
    }

    @Override
    public JSONObject bindAgent(String organizeId, String organizeNo, String accountId, String uniqueId, String isDefault) {
        Map<String, String> headMap = new HashMap<>();

        List<JSONObject> agentList = new ArrayList<>();
        JSONObject agent = new JSONObject();
        agent.put("accountId", accountId);
        agent.put("isDefault", isDefault);
        agent.put("uniqueId", uniqueId);
        agentList.add(agent);

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
        agentList.add(agent);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("organizeId", organizeId);
        jsonObject.put("organizeNo", organizeNo);
        jsonObject.put("agentList", agentList);

        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/organizations/outerOrgans/unbindAgent", headMap, jsonObject.toJSONString());
        log.info("外部机构【{}】解绑经办人【{}】接口响应{}", organizeId, accountId, result);
        return convertResult(result);
    }

    @Override
    public JSONObject queryInnerAccounts(QueryInnerAccountsReq req) {
        Map<String, String> headMap = new HashMap<>();

        Map<String, String> param = new HashMap<>();
        param.put("organizeId", req.getOrganizeId());
        //param.put("uniqueId", req.getUniqueId());
        param.put("pageIndex", req.getPageIndex());
        param.put("pageSize", req.getPageSize());
        convertHead(headMap, "");
        String result = HttpUtil.doGet(url + "/V1/accounts/innerAccounts/list", headMap, param);
        log.info("根据用户标识【{}】查询用户列表信息接口响应{}", req.getUniqueId(), result);
        return convertResult(result);
    }

    @Override
    public JSONObject queryInnerOrgans(String organizeNo) {
        Map<String, String> headMap = new HashMap<>();
        Map<String, String> param = new HashMap<>();
        param.put("organizeNo", organizeNo);
        convertHead(headMap, "");
        String result = HttpUtil.doGet(url + "/V1/organizations/innerOrgans/query", headMap, param);
        log.info("根据机构编号【{}】查询内部机构信息接口响应{}", organizeNo, result);
        return convertResult(result);
    }


    @Override
    public String queryByOrgName(String organizeName, int pageIndex) {
        Map<String, String> headMap = new HashMap<>();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("organizeName", organizeName);
        jsonObject.put("pageIndex", pageIndex);
        jsonObject.put("pageSize", 20);
        convertHead(headMap, jsonObject.toJSONString());
        String result = HttpUtil.doPost(url + "/V1/organizations/outerOrgans/queryByOrgname?pageIndex=" + pageIndex + "&pageSize=30", headMap, jsonObject.toJSONString());
        return result;
    }
}
