package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:28
 * @Description:
 */
@ApiModel("附件信息")
@Data
public class AttachmentProcessBean {

    @ApiModelProperty("附件fileKey")
    private String fileKey;

    @ApiModelProperty("附件名称")
    private String fileName;

}
