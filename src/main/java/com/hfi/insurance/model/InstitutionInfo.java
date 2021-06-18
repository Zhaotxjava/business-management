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
    private String clinicName;
    private String orgInstitutionCode;
    private String legalRepresentName;
    private String contactName;
    private String contactPhone;

}
