package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/14 10:43
 * @Description:
 */
@Data
public class SingerInfo {

    @ApiModelProperty("机构名称")
    private String institutionName;

    @ApiModelProperty("关键词")
    private String key;

    @ApiModelProperty("签署方式 0-自由签署 4-关键字签署")
    private Integer signType;
}
