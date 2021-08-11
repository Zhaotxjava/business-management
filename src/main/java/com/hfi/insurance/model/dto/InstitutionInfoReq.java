package com.hfi.insurance.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/8/10 14:29
 * @Description:
 */
@Data
public class InstitutionInfoReq {
    /**
     * 机构编号
     */
    @ApiModelProperty("机构编号")
    private String number;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String institutionName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty("统一社会信用代码")
    private String orgInstitutionCode;

    /**
     * 机构法人姓名
     */
    @ApiModelProperty("机构法人姓名")
    private String legalName;

    /**
     * 机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    @ApiModelProperty("机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard")
    private String legalCardType;

    /**
     * 机构法人证件号
     */
    @ApiModelProperty("机构法人证件号")
    private String legalIdCard;

    /**
     * 机构法人手机号
     */
    @ApiModelProperty("机构法人手机号")
    private String legalPhone;

    /**
     * 经办人姓名
     */
    @ApiModelProperty("经办人姓名")
    private String contactName;

    /**
     * 经办人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    @ApiModelProperty("经办人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard")
    private String contactCardType;

    /**
     * 经办人证件号
     */
    @ApiModelProperty("经办人证件号")
    private String contactIdCard;

    /**
     * 经办人手机号
     */
    @ApiModelProperty("经办人手机号")
    private String contactPhone;
}
