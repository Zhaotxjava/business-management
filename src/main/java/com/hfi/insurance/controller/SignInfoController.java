package com.hfi.insurance.controller;


import com.hfi.insurance.common.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 签署流程记录 前端控制器
 * </p>
 *
 * @author ChenZX
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/signInfo")
@Api(tags = {"【签署管理】"})
public class SignInfoController {

    @ApiOperation("获取签署流程记录")
    @PostMapping("/getSignInfoRecord")
    public ApiResponse getSignInfoRecord(){

        return new ApiResponse(null);
    }
}

