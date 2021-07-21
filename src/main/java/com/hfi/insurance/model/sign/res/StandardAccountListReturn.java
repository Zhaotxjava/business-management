package com.hfi.insurance.model.sign.res;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/21 10:33
 * @Description:
 */
@Data
public class StandardAccountListReturn {
    private String accountId;
    private Date createTime; //创建时间
    private String dingUserId;//钉钉userId
    private String email; //邮箱
    private String licenseNumber; //证件号码
    private Integer licenseType; //证件类型 0-其他，1-身份证，2-护照号，3-港澳通行证
    private String mobile; //手机号
    private String name; //用户名
    private List<OrganizedReturn> organizeList; //所属机构列表
    private String uniqueId; //用户唯一标识
}
