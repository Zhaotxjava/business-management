package com.hfi.insurance.service;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.sign.req.QueryInnerAccountsReq;

import java.util.List;

public interface OrganizationsService {

    JSONObject createAccounts(String name, String idCode, String mobile,String moblieType);

    JSONObject listAccounts(String idCode, String mobile);

    JSONObject queryAccounts(String accountId, String uniqueId);

    JSONObject deleteAccounts(String accountId);

    JSONObject updateAccounts(String accountId, String name, String idCode, String mobile,String moblieType);

    JSONObject createOrgans(InstitutionInfo institutionInfo);

    JSONObject queryOrgans(String organizeId, String organizeNo);

    JSONObject updateOrgans(InstitutionInfo institutionInfo);

    JSONObject bindAgent(String organizeId, String organizeNo, String accountId, String uniqueId);

    JSONObject bindAgent(String organizeId, String organizeNo, String accountId, String uniqueId, String isDefault);

    JSONObject unbindAgent(String organizeId, String organizeNo, String accountId, String uniqueId);

    JSONObject queryInnerAccounts(QueryInnerAccountsReq req);

    JSONObject queryInnerOrgans(String organizeNo);

    String queryByOrgName(String organizeName,int pageIndex);

    JSONObject processBatchDownload(String bizNo,String fileName, List<String> processIds);

    JSONObject findProcessBatchDownload(String bizNo);

}
