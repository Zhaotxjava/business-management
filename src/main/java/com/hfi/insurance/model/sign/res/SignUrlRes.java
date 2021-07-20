package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/20 10:36
 * @Description:
 */
@Data
public class SignUrlRes {

    @ApiModelProperty("签署人的用户id")
    private String accountId;

    @ApiModelProperty("帐号的类别。1为内部用户，2为外部用户")
    private Integer accountType;

    @ApiModelProperty("签署系统页面地址")
    private String signUrl;

    @ApiModelProperty("签署链接二维码")
    private String signUrlQRCode;

    @ApiModelProperty("签署人的唯一标识（用户uniqueId）")
    private String uniqueId;
}
