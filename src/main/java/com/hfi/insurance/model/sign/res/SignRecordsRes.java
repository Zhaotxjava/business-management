package com.hfi.insurance.model.sign.res;

import com.alibaba.fastjson.annotation.JSONField;
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

    private Integer flowId;

    @ApiModelProperty("流程名称")
    private String subject;

    @ApiModelProperty("发起人")
    private String initiator;

    @ApiModelProperty("签署方")
    private String signers;

    @ApiModelProperty("最近处理时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recentHandleTime;

    @ApiModelProperty("抄送方")
    private String copyViewers;

    @ApiModelProperty("发起时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date initiateTime;

    @ApiModelProperty("流程ID")
    private String signFlowId;

    @ApiModelProperty("流程状态")
    private Integer flowStatus;

    @ApiModelProperty("签署状态")
    private String signStatus;

    @ApiModelProperty("类别")
    private String flowType;

    private String accountId;

    @ApiModelProperty("用户类型;1:内部,2外部")
    private Integer accountType;

    private String uniqueId;

    private String fileKey;
}
