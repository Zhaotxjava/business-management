package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/2 16:16
 * @Description:
 */
@Data
@ApiModel("签署文档信息")
public class StandardSignDocBean {

    @ApiModelProperty("签署文档fileKey")
    private String docFilekey;

    @ApiModelProperty("签署位置信息")
    private List<SignInfoBeanV2> signPos;
}
