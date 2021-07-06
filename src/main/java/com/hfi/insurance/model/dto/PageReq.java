package com.hfi.insurance.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageReq  {
	/**
	 * 当前页
	 */
	@JSONField(name="pageNum")
	private Integer pageNum = 1;
	/**
	 * 每页大小
	 */
	@JSONField(name="pageSize")
	private Integer pageSize = 10;
}
