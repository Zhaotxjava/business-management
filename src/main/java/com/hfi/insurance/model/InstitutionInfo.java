package com.hfi.insurance.model;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/6/16 15:06
 * @Description:
 */
@Data
public class InstitutionInfo {

    private String number;
    private String institutionName;
    private String orgInstitutionCode;  //组织机构编码
    private String legalName;
    private String legalIdCard;
    private String legalPhone;
    private String contactName;
    private String contactIdCard;
    private String contactPhone;
    private String accountId;  //用户id
    private String organizeId;  //机构id


}
