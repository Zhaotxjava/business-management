package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.model.dto.FlowStatusNotifyReq;
import com.hfi.insurance.service.IYbFlowInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Version 1.0
 * @Since JDK1.8
 * @Author HYK
 * @Date 2022/4/18 10:22
 */
@Slf4j
@RestController
@RequestMapping("callBack")
public class CallBackController {

	@Resource
	private IYbFlowInfoService iYbFlowInfoService;

	@RequestMapping("flowStatusNotify")
	public JSONObject flowStatusNotify(@RequestBody FlowStatusNotifyReq req) {
		log.info("流程状态改变异步回调接口==>入参:【{}】", JSONObject.toJSONString(req));
		// 获取最新状态以及天印系统的签署流程id
		Integer flowStatus = req.getStatus();
		String signFlowId = String.valueOf(req.getFlowId());

		log.info("流程状态改变异步回调接口==>开始更新状态.状态:【{}】,签署流程ID:【{}】", flowStatus, signFlowId);
		// 根据流程ID更新状态并获取受影响的行数
		int effectRows = iYbFlowInfoService.updateFlowStatusBySignFlowId(flowStatus, signFlowId);

		log.info("流程状态改变异步回调接口==>更新状态结束.状态:【{}】,签署流程ID:【{}】,修改数据量:【{}】", flowStatus, signFlowId, effectRows);
		// 返回接收成功
		JSONObject backJson = new JSONObject();
		backJson.put("errCode", "0");
		return backJson;
	}

}
