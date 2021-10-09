package com.hfi.insurance.controller;


import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.dto.OrgTdQueryReq;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.IYbOrgTdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@CrossOrigin
public class OrgTdController {
    @Resource
    private IYbOrgTdService orgTdService;
    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @PostMapping("/getOrgTdInfo")
    @ApiOperation("查询签署机构信息")
    public ApiResponse getOrgTdInfo(@RequestBody OrgTdQueryReq req) {
//        if (CollectionUtils.isEmpty(req.getInstitutionTypes())){
//            return new ApiResponse("机构类型不能为空！");
//        }
//        if (CollectionUtils.isEmpty(req.getInstitutionLevels())){
//            return new ApiResponse("机构等级不能为空！");
//        }
//        if (CollectionUtils.isEmpty(req.getAreas())){
//            return new ApiResponse("区域不能为空！");
//        }
//        if (CollectionUtils.isEmpty(req.getProfits())){
//            return new ApiResponse("营利类型不能为空！");
//        }
        return new ApiResponse(institutionInfoService.getOrgTdListForCreateFlow(req));
    }
}

