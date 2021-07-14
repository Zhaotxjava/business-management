package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/13 10:43
 * @Description:
 */
@Data
public class CreateSignFlowReq {


    @ApiModelProperty(value = "模板id")
    private String templateId;

    @ApiModelProperty("签署信息")
    private List<SingerInfo> singerInfos;


}
