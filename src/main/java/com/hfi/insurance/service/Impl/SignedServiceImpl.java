package com.hfi.insurance.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.model.sign.res.GetPageWithPermissionResult;
import com.hfi.insurance.service.SignedService;
import com.hfi.insurance.utils.HmacSHA256Utils;
import com.hfi.insurance.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ChenZX
 * @Date 2021/7/1 17:29
 * @Description:
 */
@Service
public class SignedServiceImpl implements SignedService {

    @Value("${esignpro.url}")
    private String url;
    @Value("${esignpro.projectId}")
    private String projectId;
    @Value("${esignpro.secret}")
    private String secret;
    @Override
    public JSONObject getPageWithPermission(GetPageWithPermissionV2Model getPageWithPermissionV2Model) {
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap,JSON.toJSONString(getPageWithPermissionV2Model));
        String result = HttpUtil.doPost(url + "/esignpro/rest/template/api/getPageWithPermission", headMap, JSON.toJSONString(getPageWithPermissionV2Model));
        return convertResult(result);
    }

    @Override
    public JSONObject createSignFlows(StandardCreateFlowBO standardCreateFlow) {
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap,JSON.toJSONString(standardCreateFlow));
        String result = HttpUtil.doPost(url + "/V1/signFlows/create", headMap, JSON.toJSONString(standardCreateFlow));
        return convertResult(result);
    }

    @Override
    public JSONObject getSignUrls(GetSignUrlsReq req) {
        Map<String, String> headMap = new HashMap<>();
        convertHead(headMap,JSON.toJSONString(req));
        Map<String, String> urlParams = new HashMap<>(16);
        urlParams.put("signFlowId",req.getSignFlowId());
        urlParams.put("accountType",String.valueOf(req.getAccountType()));
        urlParams.put("accountId",req.getAccountId());
        urlParams.put("uniqueId",req.getUniqueId());
        urlParams.put("signPlatform",String.valueOf(req.getSignPlatform()));
        urlParams.put("qrCode",String.valueOf(req.getQrCode()));
        String s = HttpUtil.doGet(url + "/V1/signFlows/signUrls", headMap, urlParams);
        return convertResult(s);
    }

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


}
