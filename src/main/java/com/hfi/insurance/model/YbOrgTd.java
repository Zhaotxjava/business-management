package com.hfi.insurance.model;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 定点医疗服务机构信息
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YbOrgTd implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 定点机构编号
     */
      private String akb020;

    /**
     * 定点机构名称
     */
    private String akb021;

    /**
     * 定点机构类型
     */
    private String akb022;

    /**
     * 组织机构代码
     */
    private String aae053;

    /**
     * 定点机构等级
     */
    private String aka101;

    /**
     * 医疗机构分类代码
     */
    private String akb023;

    /**
     * 定点机构服务状态
     */
    private String bkb012;

    /**
     * 法定代表人姓名
     */
    private String aae045;

    /**
     * 上级医疗机构编号
     */
    private String bkb001;

    /**
     * 有效标志
     */
    private String aae100;

    /**
     * 执业许可证登记号
     */
    private String bkb117;

    /**
     * 备注
     */
    private String aae013;

    /**
     * 医保等级
     */
    private String bkb301;

    /**
     * 分管医保办负责人
     */
    private String bkb302;

    /**
     * 营利类型
     */
    private String bka938;

    /**
     * 医保联系人
     */
    private String aae004;

    /**
     * 联系电话
     */
    private String aae005;

    /**
     * 地址
     */
    private String aae006;

    /**
     * 营业许可证号
     */
    private String bka968;

    /**
     * 分管医保负责人
     */
    private String bka975;

    /**
     * 手机号码
     */
    private String bka977;

    /**
     * 传真号码
     */
    private String bka978;

    /**
     * 首次批准定点日期
     */
    private String bka983;

    /**
     * 所属统筹区编码
     */
    private String aaa027;

    /**
     * 医保办负责人电话
     */
    private String bka996;

    /**
     * 所属城区
     */
    private String akb141;

    /**
     * 医保评级
     */
    private String bka992;

    /**
     * 医保联系人手机
     */
    private String bkb500;

    /**
     * 医院信息部门联系人
     */
    private String bkb501;

    /**
     * 医院信息部门联系电话
     */
    private String bkb502;

    /**
     * 医保办负责人
     */
    private String bkb503;

    /**
     * 医保办联系电话
     */
    private String bkb504;

    /**
     * 医院信息部门手机
     */
    private String bkb505;


}
