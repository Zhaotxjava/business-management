package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:26
 * @Description:
 */
@Data
@ApiModel("app跳转信息")
public class ProcessAppInfoBean {

    @ApiModelProperty("安卓APP设备标识")
    private String androidAppName;
    @ApiModelProperty("app跳转协议（Android")
    private String androidAppSchema;
    @ApiModelProperty("苹果APP设备标识")
    private String iosAppName;
    @ApiModelProperty("app跳转协议（ios）")
    private String iosAppSchema;
}
