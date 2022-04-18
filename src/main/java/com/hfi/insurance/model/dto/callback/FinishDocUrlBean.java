package com.hfi.insurance.model.dto.callback;

import lombok.Data;

/**
 * @Version 1.0
 * @Since JDK1.8
 * @Author HYK
 * @Date 2022/4/18 10:27
 */
@Data
public class FinishDocUrlBean {

	/**
	 * 文档id
	 */
	private Long docId;

	/**
	 * 文档原始filekey
	 */
	private String docFileKey;

	/**
	 * 签署完成filekey
	 */
	private String finishFileKey;

	/**
	 * 文档下载url
	 */
	private String downloadDocUrl;

}
