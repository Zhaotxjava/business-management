package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/2 15:50
 * @Description:
 */
@ApiModel("签署信息")
@Data
public class SignUrlBeanV2 {
    @ApiModelProperty("签署人的id")
    private String accountId;
    @ApiModelProperty("签署人的名称")
    private String accountName;
    @ApiModelProperty("小程序地址")
    private String appletsUrl;
    @ApiModelProperty("签署顺序")
    private Integer signOrder;
    @ApiModelProperty("签署链接地址")
    private String signUrl;
    @ApiModelProperty("签署人的唯一标识")
    private String uniqueId;
}
