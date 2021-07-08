package com.hfi.insurance.model.sign.req;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/8 15:27
 * @Description:
 */
@Data
public class TemplateFormValueParam {
    //表单Id
    private Integer formId;
    //表单内容
    private String formValue;
}
