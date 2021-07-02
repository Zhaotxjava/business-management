package com.hfi.insurance.model.sign.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/1 18:37
 * @Description:
 */
@Data
@ApiModel("抄送人信息")
public class CopyViewerInfoBean {
    @ApiModelProperty("用户id;与uniqueId必填其一")
    private String accountId;
    @ApiModelProperty("用户类型;1内部，2外部")
    private Integer accountType;
    private String uniqueId;
}
