package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.YbFlowInfo;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.service.impl.YbFlowInfoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author jthealth-NZH
 * @Date 2021/9/17 14:22
 * @Describe
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping(value = "/test")
@Api(tags = {"【测试接口】"})
public class TestController {
    @Autowired
    private YbFlowInfoServiceImpl ybFlowInfoService;

    @RequestMapping(value = "/sign/get", method = RequestMethod.POST)
    @ApiOperation("getSign")
    @LogAnnotation
    public ApiResponse getSign(GetRecordInfoReq req) {
        log.info("{}", JSONObject.toJSONString(req));
        if (Objects.isNull(req.getPageNum())) {
            req.setPageNum(1);
        }
        if (Objects.isNull(req.getPageSize())) {
            req.setPageSize(10);
        }
        Page<YbFlowInfo> page = ybFlowInfoService.getSignedRecord("", req);
        return ApiResponse.success(page);
    }
}
