package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/8/10 17:20
 * @Description:
 */
@Data
public class BindedAgentBean {
    private String agentId; //经办人id string
    private String agentName; //经办人姓名 string
    private String agentUniqueId; // 经办人唯一标识 string
    private Integer defaultFlag; // 是否为默认经办人,0-非默认,1-默认 int32
}
