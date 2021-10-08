package com.hfi.insurance.model.sign;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hfi.insurance.config.ExportExcel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 凌田昊
 * @version 1.0
 * @description: TODO
 * @date 2021/10/8 9:47
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("批量导出表")
public class YbFlowDownload implements Serializable {

    private static final long serialVersionUID = 1L;



    @ApiModelProperty("id")
    @TableId(type = IdType.INPUT)
    @ExportExcel(name="id")
    private Long id;

    /**
     * 批次ID，用于区别每次请求
     */
    @ApiModelProperty("批次ID")
    @ExportExcel(name="批次ID")
    private String batchId;

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
     * 证照类型
     */
    @ApiModelProperty("证照类型")
    @ExportExcel(name="证照类型")
    private String institutionCardType;


    /**
     * 证照号码
     */
    @ApiModelProperty("证照号码")
    @ExportExcel(name="证照号码")
    private String institutionCardCode;

    /**
     * 法人姓名
     */
    @ApiModelProperty("法人姓名")
    @ExportExcel(name="法人姓名")
    private String legalName;

    /**
     * 机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    @ApiModelProperty("机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard")
    @ExportExcel(name="法人证件类型")
    private String legalCardType;

    /**
     * 机构法人证件号
     */
    @ApiModelProperty("法人证件号码")
    @ExportExcel(name="法人证件号码")
    private String legalCardCode;



    /**
     * 机构法人手机号
     */
    @ApiModelProperty("法人手机号")
    @ExportExcel(name="法人手机号")
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
    @ApiModelProperty("经办人证件号码")
    @ExportExcel(name="经办人证件号码")
    private String contactCardCode;



    /**
     * 经办人手机号
     */
    @ApiModelProperty("经办人手机号")
    @ExportExcel(name="经办人手机号")
    private String contactPhone;


    /**
     * 是否发起成功
     */
    @ApiModelProperty("是否发起成功")
    @ExportExcel(name="是否发起成功")
    private String signStatus;


    /**
     * 机构签署区域 甲方,丙方,乙方……
     */
//    @ApiModelProperty("机构签署区域")
//    @ExportExcel(name="机构签署区域")
    private String signerType;





















}
