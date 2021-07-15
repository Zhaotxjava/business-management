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

    @ApiModelProperty("机构编号")
    private String number;

    @ApiModelProperty("用户类型;1:内部,2外部")
    private Integer accountType;

    @ApiModelProperty("关键词")
    private String key;

    @ApiModelProperty("签署方式 0-自由签署 4-关键字签署")
    private Integer signType;

    @ApiModelProperty("机构签署区域 甲方,丙方,乙方...")
    private String flowName;
}
