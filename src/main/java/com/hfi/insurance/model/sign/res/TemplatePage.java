package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/2 11:33
 * @Description:
 */
@Data
public class TemplatePage {

    @ApiModelProperty("创建人名称")
    private String createBy;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("模板编号")
    private String templateId;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("类型名称")
    private String typeName;
}
