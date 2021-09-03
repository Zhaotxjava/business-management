package com.hfi.insurance.service;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.sign.req.QueryInnerAccountsReq;

public interface OrganizationsService {

    JSONObject createAccounts(String name, String idCode, String mobile);

    JSONObject queryAccounts(String accountId, String uniqueId);

    JSONObject updateAccounts(String accountId, String name, String idCode, String mobile);

    JSONObject createOrgans(InstitutionInfo institutionInfo);

    JSONObject queryOrgans(String organizeId, String organizeNo);

    JSONObject updateOrgans(InstitutionInfo institutionInfo);

    JSONObject bindAgent(String organizeId, String organizeNo, String accountId, String uniqueId);

    JSONObject unbindAgent(String organizeId, String organizeNo, String accountId, String uniqueId);

    JSONObject queryInnerAccounts(QueryInnerAccountsReq req);

    JSONObject queryInnerOrgans(String organizeNo);

    String queryByOrgName(String organizeName,int pageIndex);
}
