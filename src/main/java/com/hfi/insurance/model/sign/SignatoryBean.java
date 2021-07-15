package com.hfi.insurance.model.sign;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/14 16:57
 * @Description:
 */
@ApiModel("签署人信息")
@Data
public class SignatoryBean {
    /**
     * 签署人id
     */
    private String accountId;

    /**
     * 签署人名称
     */
    private String accountName;


}
