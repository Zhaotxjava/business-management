package com.hfi.insurance.model.sign;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:06
 * @Description:
 */

public class InstitutionBaseInfo {

    @ApiModelProperty("机构编号")
    private String number;
    @ApiModelProperty("机构名称")
    private String institutionName;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
    //    @ApiModelProperty("统一社会信用代码")
//    private String orgInstitutionCode;  //统一社会信用代码
//    private String legalName;
//    private String legalIdCard;
//    private String legalPhone;
//    private String contactName;
//    private String contactIdCard;
//    private String contactPhone;
//    @ApiModelProperty("用户id")
//    private String accountId;  //用户id
//    private String organizeId;  //机构id
//    private String updateTime;
}
