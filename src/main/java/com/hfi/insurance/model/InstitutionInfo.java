package com.hfi.insurance.model;

import com.hfi.insurance.enums.LicenseType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:06
 * @Description:
 */
@Data
public class InstitutionInfo {

    @ApiModelProperty("机构编号")
    private String number;
    @ApiModelProperty("机构名称")
    private String institutionName;
    @ApiModelProperty("统一社会信用代码")
    private String orgInstitutionCode;  //统一社会信用代码
    private String legalName;
    private String legalIdCard;
    private String legalPhone;
    private String contactName;
    private String contactIdCard;
    private String contactPhone;
    @ApiModelProperty("用户id")
    private String accountId;  //用户id
    @ApiModelProperty("天印系统法人用户标识")
    private String legalAccountId;
    private String organizeId;  //机构id
    private String updateTime;
    @ApiModelProperty("证件类型 SOCNO=社会信用代码，OTHERNO=许可证")
    private LicenseType licenseType;


}
