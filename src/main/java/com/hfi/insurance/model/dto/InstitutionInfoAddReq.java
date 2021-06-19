package com.hfi.insurance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/6/18 14:30
 * @Description:
 */
@Data
public class InstitutionInfoAddReq {

    @ApiModelProperty("机构编码")
    private String number;

    @ApiModelProperty("组织机构编码")
    private String orgInstitutionCode;

    @ApiModelProperty("法人姓名")
    private String legalName;

    @ApiModelProperty("法人身份证")
    private String legalIdCard;

    @ApiModelProperty("法人手机号")
    private String legalPhone;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("联系人身份证")
    private String contactIdCard;

    @ApiModelProperty("联系人手机号")
    private String contactPhone;
}
