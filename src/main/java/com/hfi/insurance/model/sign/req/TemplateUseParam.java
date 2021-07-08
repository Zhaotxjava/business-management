package com.hfi.insurance.model.sign.req;

import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/8 15:25
 * @Description:
 */
@Data
public class TemplateUseParam {
    /**
     *表单数组信息
     */
    private List<TemplateFormValueParam> templateFormValues;
    /**
     * 模板编号
     */
    private String templateId;

    /**
     * 二维码数组信息
     */
    private List<TemplateQrContentParam> templateQrContents;
}
