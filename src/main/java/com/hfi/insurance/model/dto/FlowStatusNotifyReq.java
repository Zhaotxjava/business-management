package com.hfi.insurance.model.dto;

import com.hfi.insurance.model.dto.callback.CallBackAccountInfo;
import com.hfi.insurance.model.dto.callback.FinishDocUrlBean;
import lombok.Data;

import java.util.List;

/**
 * @Version 1.0
 * @Since JDK1.8
 * @Author HYK
 * @Date 2022/4/18 10:23
 */
@Data
public class FlowStatusNotifyReq {

	/**
	 * 该参数标记此次回调类型:
	 * SIGN_FLOW_FINISH，终结状态 回调
	 * SIGN_FLOW_UPDATE，过程状态 更新
	 * REALNAME_FOR_SIGN，实名认 证
	 * SIGN_FLOW_NOTIFY，签署通知
	 */
	private String action;

	/**
	 * 签署流程id
	 */
	private Long flowId;

	/**
	 * 业务id
	 */
	private String bizNo;

	/**
	 * Common-普通签署， Cancellation-作废签署
	 */
	private String flowType;

	/**
	 * action 为
	 * SIGN_FLOW_FINISH 时: 2 为签署完成 5 为过期作废 7 为拒签 8 为作废(作废签署完成)
	 * SIGN_FLOW_UPDATE 时：2 为签署完成 3 为冻结 4 为解冻 5为静默签署失败
	 */
	private Integer status;

	/**
	 * 实名回调（预留字段）
	 */
	private Integer realNameChannel;

	/**
	 * 操作人个人详情
	 */
	private CallBackAccountInfo accountInfo;

	/**
	 * 签署文档信息，流程签署完不为 空
	 */
	private List<FinishDocUrlBean> finishDocUrlBeans;

	/**
	 * 待签署人信息。如果是多人顺序 签署，3人按1-3顺序签署，顺序1 签署完成，回调通知顺序2的待签 署人信息
	 */
//	private List<Object> waitingToSignAccount;

	/**
	 * 结果描述
	 */
	private String resultDescription;

	/**
	 * 签署时间
	 */
	private String signTime;

	/**
	 * 合同创建时间
	 */
	private String createTime;

	/**
	 * 合同结束时间
	 */
	private String endTime;

}
