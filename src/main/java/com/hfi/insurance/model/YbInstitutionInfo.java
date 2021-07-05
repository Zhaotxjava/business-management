package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 定点机构信息
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YbInstitutionInfo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 机构编号
     */
      private String number;

    /**
     * 机构名称
     */
    private String institutionName;

    /**
     * 统一社会信用代码
     */
    private String orgInstitutionCode;

    /**
     * 机构法人姓名
     */
    private String legalName;

    /**
     * 机构法人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    private String legalCardType;

    /**
     * 机构法人证件号
     */
    private String legalIdCard;

    /**
     * 机构法人手机号
     */
    private String legalPhone;

    /**
     * 经办人姓名
     */
    private String contactName;

    /**
     * 经办人证件类型 Other-其他，IDCard身份证号码，Passport-中国护照，HMPass-港澳居民来往内地通行证，MTP-台胞证,默认为IDCard
     */
    private String contactCardType;

    /**
     * 经办人证件号
     */
    private String contactIdCard;

    /**
     * 经办人手机号
     */
    private String contactPhone;

    /**
     * 天印系统经办人用户标识
     */
    private String accountId;

    /**
     * 天印系统机构标记
     */
    private String organizeId;

    private Date createTime;

    @TableField("updateTime")
    private Date updateTime;


}