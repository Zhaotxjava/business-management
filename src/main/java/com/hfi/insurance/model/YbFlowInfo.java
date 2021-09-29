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
 * @author NZH
 * @since 2021-09-29
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
     * 流程状态（0草稿，1 签署中，2完成，3 撤销，4终止，5过 期，6删除，7拒 签，8作废，9已归 档，10预盖章）
     */
    private Integer flowStatus;

    /**
     * 机构编号
     */
    private String number;

    /**
     * 内部用户唯一标识
     */
    private String uniqueId;

    /**
     * 用户类型（1:内部,2外部）
     */
    private Integer accountType;

    /**
     * 签署状态（0待签 署，1签署中，2 完成，3中止/失败）
     */
    private String signStatus;

    /**
     * 发起人
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

    /**
     * 抄送人
     */
    private String copyViewers;

    /**
     * Common-普通签署， Cancellation-作废签署
     */
    private String flowType;

    private String fileKey;

    private Date createTime;

    private Date updateTime;

    /**
     * 批次ID，用于区别每次请求
     */
    private String batchNo;

    /**
     * 机构签署区域 甲方,丙方,乙方……
     */
    private String flowName;


}
