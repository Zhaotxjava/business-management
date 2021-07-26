package com.hfi.insurance.model.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author ChenZX
 * @Date 2021/7/26 16:14
 * @Description:
 */
@Data
public class InstitutionInfoRes{

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

    /**
     * 天印系统经办人用户标识
     */
    @ApiModelProperty("天印系统经办人用户标识")
    private String accountId;

    /**
     * 天印系统机构标记
     */
    @ApiModelProperty("天印系统机构标记")
    private String organizeId;

    private String area;

    private String level;

    private String profit;

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createTime;

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date updateTime;
}
