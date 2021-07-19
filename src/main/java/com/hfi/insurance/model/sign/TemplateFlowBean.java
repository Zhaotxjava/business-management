package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/14 14:33
 * @Description:
 */
@Data
public class TemplateFlowBean {
    private Integer flowId;
    private String flowName;
    /**
     * 签署位置信息
     */
    private PredefineBean predefine;
    private Integer routingOrder;
    private SignDateBean signDate;
    private SignatoryBean signatory;
}
