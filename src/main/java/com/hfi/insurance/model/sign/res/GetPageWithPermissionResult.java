package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/2 11:31
 * @Description:
 */
@Data
@ApiModel("分页查询模板列表带权限")
public class GetPageWithPermissionResult {

    private String message;

    private Boolean success;

    @ApiModelProperty("文档模板列表")
    private List<TemplatePage> templateInfos;

    @ApiModelProperty("总数量")
    private Integer totalCount;
}
