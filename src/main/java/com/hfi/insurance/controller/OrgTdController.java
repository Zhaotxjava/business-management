package com.hfi.insurance.controller;


import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.service.IYbOrgTdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RestController
@RequestMapping("/orgTd")
@Api(tags = {"【签署机构管理接口】"})
public class OrgTdController {
    @Resource
    private IYbOrgTdService orgTdService;

    @PostMapping("getOrgTdInfo")
    @ApiOperation("查询签署机构信息")
    public ApiResponse getOrgTdInfo(@RequestBody OrgTdQueryReq req){
        return new ApiResponse(orgTdService.getOrgTdList(req));
    }
}

