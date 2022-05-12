package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.enums.NotifyAction;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.dto.FlowStatusNotifyReq;
import com.hfi.insurance.model.dto.callback.CallBackAccountInfo;
import com.hfi.insurance.service.IYbFlowInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

		// 设置返回报文
		JSONObject backJson = new JSONObject();
		backJson.put("errCode", "0");

		// 获取最新状态以及天印系统的签署流程id
		Integer status = req.getStatus();
		String signFlowId = String.valueOf(req.getFlowId());

		CallBackAccountInfo accountInfo = req.getAccountInfo();
		String number;
		if (accountInfo != null) {
			number = accountInfo.getOrganizeNo();
		}

		String action = req.getAction();

		// 定义受影响的行数
		int effectRows;

		YbFlowInfo updateInfo = new YbFlowInfo();
		if (NotifyAction.SIGN_FLOW_FINISH.getCode().equals(action)) {
			log.info("流程状态改变异步回调接口==>修改FLOW_STATUS.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】", action, status, signFlowId, number);
			updateInfo.setFlowStatus(status);
			if (StringUtils.isBlank(number)) {
				log.info("流程状态改变异步回调接口==>修改FLOW_STATUS,ACTION为SIGN_FLOW_FINISH,NUMBER为空,根据SIGN_FLOW_ID更新.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】", action, status, signFlowId, number);
				effectRows = iYbFlowInfoService.updateBySignFlowId(updateInfo, signFlowId);
			} else {
				log.info("流程状态改变异步回调接口==>修改FLOW_STATUS,ACTION为SIGN_FLOW_FINISH,NUMBER不为空,根据SIGN_FLOW_ID和NUMBER更新.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】", action, status, signFlowId, number);
				effectRows = iYbFlowInfoService.updateBySignFlowIdAndNumber(updateInfo, signFlowId, number);
			}
		} else if (NotifyAction.SIGN_FLOW_UPDATE.getCode().equals(action)) {
			if (StringUtils.isBlank(number)) {
				log.info("流程状态改变异步回调接口==>修改SIGN_STATUS,ACTION为SIGN_FLOW_UPDATE,NUMBER为空,停止更新.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】", action, status, signFlowId, number);
				return backJson;
			}
			log.info("流程状态改变异步回调接口==>修改SIGN_STATUS,ACTION为SIGN_FLOW_UPDATE,NUMBER不为空,根据SIGN_FLOW_ID和NUMBER更新.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】", action, status, signFlowId, number);
			updateInfo.setSignStatus(String.valueOf(status));
			effectRows = iYbFlowInfoService.updateBySignFlowIdAndNumber(updateInfo, signFlowId, number);
		} else {
			log.info("流程状态改变异步回调接口==>回调类型ACTION不需要更新数据.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】", action, status, signFlowId, number);
			return backJson;
		}

		log.info("流程状态改变异步回调接口==>更新数据结束.回调类型:【{}】,状态:【{}】,签署流程ID:【{}】,机构编码:【{}】,修改数据量:【{}】", action, status, signFlowId, number, effectRows);

		return backJson;
	}

}
