package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author ChenZX
 * @Date 2021/7/2 15:41
 * @Description:
 */
@Data
public class AddFlowV2Result {
    @ApiModelProperty("第三方业务码")
    private String bizNo;

    private List<FileResult> signDocs;

    @ApiModelProperty("流程id")
    private Integer signFlowId;

    private List<SignUrlBeanV2> signUrls;

}
