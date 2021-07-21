package com.hfi.insurance.model.sign;

import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/21 19:59
 * @Description:
 */
@Data
public class StandardSignDetailSigner {
    private String accountId;// 签署人accountId
    private String accountName;// 签署人用户名
    private String accountType;//帐号的类别。1为 内部用户，2为外 部用户 string
}
