package com.hfi.insurance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/5 15:44
 * @Description:
 */
@Data
public class InstitutionInfoQueryReq extends PageReq{

    @ApiModelProperty("机构编号")
    private String number;

    @ApiModelProperty("机构名称")
    private String institutionName;
    @ApiModelProperty("保险编号")
    private  String  hospitalid;

}
