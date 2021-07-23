package com.hfi.insurance.controller;


import com.alibaba.fastjson.JSON;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.IYbOrgTdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 定点医疗服务机构信息 前端控制器
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@Slf4j
@RestController
@RequestMapping("/orgTd")
@Api(tags = {"【签署机构管理接口】"})
public class OrgTdController {
    @Resource
    private IYbOrgTdService orgTdService;
    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @PostMapping("getOrgTdInfo")
    @ApiOperation("查询签署机构信息")
    public ApiResponse getOrgTdInfo(@RequestBody OrgTdQueryReq req){
        log.info("查询签署机构信息入参：{}", JSON.toJSONString(req));
        return new ApiResponse(institutionInfoService.getOrgTdListForCreateFlow(req));
    }
}

