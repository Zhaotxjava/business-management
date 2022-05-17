package com.hfi.insurance.model.dto.callback;

import lombok.Data;

/**
 * @Version 1.0
 * @Since JDK1.8
 * @Author HYK
 * @Date 2022/4/18 10:26
 */
@Data
public class CallBackAccountInfo {

	/**
	 * 用户id
	 */
	private String accountId;

	/**
	 * 用户唯一标识
	 */
	private String accountUid;

	/**
	 * 机构编码
	 */
	private String organizeNo;

	/**
	 * 机构id(签署人代表机构或者企业签署时返回)
	 */
	private String authOrgId;

	/**
	 * 机构名称(签署人代表机构或者企业签署时返回)
	 */
	private String authOrgName;

	/**
	 * 用户类型，1为内部，2为外部
	 */
	private Integer type;

	/**
	 * 用户名称
	 */
	private String name;

	/**
	 * 实名认证流程id
	 */
	private String realnameFlowId;

	/**
	 * 签署url
	 */
	private String signUrl;

}
