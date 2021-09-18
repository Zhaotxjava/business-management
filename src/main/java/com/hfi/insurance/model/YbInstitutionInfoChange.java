package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfi.insurance.config.ExportExcel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 定点机构信息
 * </p>
 *
 * @author Ztx
 * @since 2021-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("机构信息变更表")
public class YbInstitutionInfoChange implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 变更id
     */
    @ApiModelProperty("id")
    @TableId(type = IdType.INPUT)
    @ExportExcel(name="id")
    private Long id;


    @ApiModelProperty("机构编号")
    @ExportExcel(name="机构编号")
    private String number;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    @ExportExcel(name="机构名称")
    private String institutionName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty("统一社会信用代码")
    @ExportExcel(name="统一社会信用代码")
    private String orgInstitutionCode;

    /**
     * 机构法人姓名
     */
    @ApiModelProperty("机构法人姓名")
    @ExportExcel(name="机构法人姓名")
    private String legalName;

    /**
     * 机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    @ApiModelProperty("机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard")
    @ExportExcel(name="机构法人证件类型")
    private String legalCardType;

    /**
     * 机构法人证件号
     */
    @ApiModelProperty("机构法人证件号")
    @ExportExcel(name="机构法人证件号")
    private String legalIdCard;

    /**
     * 机构法人手机号
     */
    @ApiModelProperty("机构法人手机号")
    @ExportExcel(name="机构法人手机号")
    private String legalPhone;

    /**
     * 经办人姓名
     */
    @ApiModelProperty("经办人姓名")
    @ExportExcel(name="经办人姓名")
    private String contactName;

    /**
     * 经办人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    @ApiModelProperty("经办人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard")
    @ExportExcel(name="经办人证件类型")
    private String contactCardType;

    /**
     * 经办人证件号
     */
    @ApiModelProperty("经办人证件号")
    @ExportExcel(name="经办人证件号")
    private String contactIdCard;

    /**
     * 经办人手机号
     */
    @ApiModelProperty("经办人手机号")
    @ExportExcel(name="经办人手机号")
    private String contactPhone;


    /**
     * 医疗机构执业许可证副本及变更登记记录图片
     */


    /**
     * 天印系统经办人用户标识
     */
    @ApiModelProperty("天印系统经办人用户标识")
    @ExportExcel(name="天印系统经办人用户标识")
    private String accountId;

    @ApiModelProperty("天印系统法人用户标识")
    @ExportExcel(name="天印系统法人用户标识")
    private String legalAccountId;
    /**
     * 天印系统机构标记
     */
    @ApiModelProperty("天印系统机构标记")
    @ExportExcel(name="天印系统机构标记")
    private String organizeId;


    @TableField("update_time")
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @ExportExcel(name="修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;



    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @ApiModelProperty("医疗机构执业许可证副本及变更登记记录图片")
    private String licensePicture;
    /**
     * 营业执照副本及变更登记记录图片
     */
    @ApiModelProperty("营业执照副本及变更登记记录图片")
    private String businessPicture;
}
