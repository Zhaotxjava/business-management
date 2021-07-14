package com.hfi.insurance.model.sign;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/14 17:03
 * @Description:
 */
@ApiModel("模板表单信息")
@Data
public class TemplateFormBean {
    private String font;
    private String fontColor;
    private Float fontSize;
    private String fontStyle;
    private Integer formId;
    private String formName;
    /**
     * 表单格式类型，1-文本格式 2-日期格式 3-数字格式
     */
    private Integer formStyle;
    /**
     * 签署页码
     */
    private Integer pageNo;
    private Integer posX;
    private Integer posY;
}
