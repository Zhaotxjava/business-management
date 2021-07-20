package com.hfi.insurance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 签署流程记录
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YbFlowInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "flow_id", type = IdType.AUTO)
    private Integer flowId;

    /**
     * 天印系统的签署流程id
     */
    private String signFlowId;

    /**
     * 流程名称
     */
    private String subject;

    /**
     * 流程状态
     */
    private Integer flowStatus;

    /**
     * 签署状态
     */
    private String signStatus;

    /**
     * 发起人:甲方
     */
    private String initiator;

    /**
     * 发起时间
     */
    private Date initiatorTime;

    /**
     * 最近处理时间
     */
    private Date handleTime;

    /**
     * 签署方
     */
    private String signers;

    private String accountId;

    /**
     * 抄送人
     */
    private String copyViewers;


    /**
     * 类别
     */
    private String flowType;

    private Date createTime;

    private Date updateTime;


}
