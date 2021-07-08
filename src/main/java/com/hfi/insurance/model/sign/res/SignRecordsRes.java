package com.hfi.insurance.model.sign.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author ChenZX
 * @Date 2021/7/7 11:09
 * @Description:
 */
@Data
public class SignRecordsRes {

    @ApiModelProperty("流程名称")
    private String subject;

    @ApiModelProperty("发起人")
    private String initiator;

    @ApiModelProperty("签署方")
    private String signers;

    @ApiModelProperty("最近处理时间")
    private Date recentHandleTime;

    @ApiModelProperty("抄送方")
    private String copyViewers;

    @ApiModelProperty("发起时间")
    private String initiateTime;

    @ApiModelProperty("流程ID")
    private Integer signFlowId;

    @ApiModelProperty("流程状态")
    private Integer flowStatus;

    @ApiModelProperty("签署状态")
    private String signStatus;
}
