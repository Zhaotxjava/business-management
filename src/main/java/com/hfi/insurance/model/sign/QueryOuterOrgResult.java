package com.hfi.insurance.model.sign;

import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/8/10 17:18
 * @Description:
 */
@Data
public class QueryOuterOrgResult {

    private List<BindedAgentBean> agentAccounts;

    private String organizeId;// 机构id string

    private String organizeName;// 机构名称

    private String organizeNo;
}
