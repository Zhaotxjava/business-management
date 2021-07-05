package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:45
 * @Description:
 */
@ApiModel("流程文档信息")
@Data
public class FlowDocBean {

    @ApiModelProperty("签署文档fileKey")
    private String docFilekey;

    @ApiModelProperty("文档名称")
    private String docName;

    @ApiModelProperty("文档顺序,用于页面文档顺序显示")
    private String docOrder;


    @ApiModelProperty("文档密码")
    private String docPwd;
}
