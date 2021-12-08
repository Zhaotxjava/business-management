package com.hfi.insurance.model.dto;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author ChenZX
 * @Date 2021/6/18 14:30
 * @Description:
 */
@Data
public class InstitutionInfoAddReq {

    @ApiModelProperty("机构编码")
    private String number;

    @ApiModelProperty("组织机构编码")
    private String orgInstitutionCode;

    @ApiModelProperty("法人姓名")
    private String legalName;

    @ApiModelProperty("法人身份证")
    private String legalIdCard;

    @ApiModelProperty("法人手机号")
    private String legalPhone;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("联系人身份证")
    private String contactIdCard;

    @ApiModelProperty("联系人手机号")
    private String contactPhone;

    @ApiModelProperty("法人类型")
    public  String  legalCardType;

    @ApiModelProperty("经办人类型")
    public  String  contactCardType;

    public static ApiResponse checkInstitutionInfoAddReq(InstitutionInfoAddReq req) {
        StringBuilder sb = new StringBuilder();
        //入参已经进行不为空校验
        if(req.getLegalCardType().equals("IDCard") ){
            if (!(18 == req.getLegalIdCard().length())) {
                sb.append("法人身份证号长度应为18位。");
            }
        }
        if (req.getContactCardType().equals("IDCard")){
            if (!(18 == req.getContactIdCard().length())) {
                sb.append("经办人身份证号长度应为18位。");
            }
        }

        if (!(11 == req.getLegalPhone().length())) {
            sb.append("法人手机号长度应为11位。");
        }
        if (!(11 == req.getContactPhone().length())) {
            sb.append("经办人手机号长度应为11位。");
        }

        if (sb.length() > 0) {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, sb.toString());
        } else {
            return ApiResponse.success();
        }
    }
}