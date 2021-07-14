package com.hfi.insurance.model.sign;

import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/14 17:56
 * @Description:
 */
@Data
public class TemplateInfoBean {

    private String fileKey;
    /**
     * 签署流程信息
     */
    private List<TemplateFlowBean> templateFlows;
    /**
     * 模板表单信息
     */
    private List<TemplateFormBean> templateForms;

    private String templateId;

    private String templateName;
}
