package com.hfi.insurance.model.sign;

import lombok.Data;

import java.util.Date;

/**
 * @Author ChenZX
 * @Date 2021/7/22 18:43
 * @Description:
 */
//内部机构信息
@Data
public class StandardDepartment {
    private String defaultSealId;//默认印章id
    private String legalName;//法人名称
    private String mobile;//联系电话
    private Date modifyTime;//修改时间
    private String organizeId;//机构id
    private String organizeName;//机构名称
    private String organizeNo;//机构唯一标识
    private Integer organizeType;//机构类型 1-法人公司 2-机构
}
