package com.hfi.insurance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/5 15:44
 * @Description:
 */
@Data
public class InstitutionInfoQueryReq {

    @ApiModelProperty("机构编号")
    private String number;

    @ApiModelProperty("机构编码")
    private String institutionName;

    @ApiModelProperty("当前页")
    private Integer pageNum;

    @ApiModelProperty("每页数量")
    private Integer pageSize;
}
