package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/2 16:46
 * @Description:
 */
@Data
public class GetSignUrlsReq {
    @ApiModelProperty("流程id")
    private String signFlowId;
    @ApiModelProperty("用户类型;1内部，2外部")
    private String accountType;
    @ApiModelProperty("用户id")
    private String accountId;
    @ApiModelProperty("用户唯一标识")
    private String uniqueId;
    //默认返回网页签地址，一次只能传一个值。
    @ApiModelProperty("签署平台，0-网页签署，1-支付宝小程序，2-e签宝APP，3-钉钉微应用")
    private String signPlatform;
    //默认不返回签署链接二维码。如果需要返回，将签署链接转成二维码base64进行返回
    @ApiModelProperty("是否返回签署链接二维码,true/false")
    private String qrCode;
}
