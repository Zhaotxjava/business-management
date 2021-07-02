package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:42
 * @Description:
 */
@ApiModel("文档二维码相关参数")
@Data
public class QrDocParamBean {
    @ApiModelProperty("二维码位置的X偏移量，非必填，默认0")
    private Integer posX;
    @ApiModelProperty("二维码位置的Y偏移量，非必填，默认0")
    private Integer posY;
    @ApiModelProperty("二维码盖章位置，1-左下角，2-左上角，3-右下角，4-右上角，非必填，默认3")
    private Integer qrPosition;
    @ApiModelProperty("水印强度范围 [15-35]")
    private Integer strength;
    @ApiModelProperty("二维码宽度")
    private Integer width;
}
