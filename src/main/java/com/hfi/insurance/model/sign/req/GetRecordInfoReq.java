package com.hfi.insurance.model.sign.req;

import com.hfi.insurance.model.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ChenZX
 * @Date 2021/7/19 18:10
 * @Description:
 */
@Data
public class GetRecordInfoReq extends PageReq {

    @ApiModelProperty("流程id")
    private Integer flowId;

    @ApiModelProperty("流程名称")
    private String subject;

    @ApiModelProperty("流程状态")
    private Integer flowStatus;

    @ApiModelProperty("签署状态")
    private String signStatus;

    @ApiModelProperty("发起日期")
    private String beginInitiateTime;

    @ApiModelProperty("结束日期")
    private String endInitiateTime;

}
