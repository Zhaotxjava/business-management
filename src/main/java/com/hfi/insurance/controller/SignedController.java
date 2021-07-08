package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.service.SignedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author ChenZX
 * @Date 2021/6/30 17:38
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping(value = "/sign")
@Api(tags = {"【签署管理接口】"})
public class SignedController {
    @Resource
    private SignedService signedService;
    //获取签署地址列表

    @PostMapping("getSignUrl")
    @ApiOperation("获取二维码")
    public ApiResponse getSignUrl(@RequestBody GetSignUrlsReq req){
        JSONObject result = signedService.getSignUrls(req);
        return new ApiResponse(result);
    }

    @PostMapping("getTemplate")
    @ApiOperation("分页查询模板列表")
    public ApiResponse getTemplate(@RequestBody GetPageWithPermissionV2Model req){
        JSONObject pageWithPermission = signedService.getPageWithPermission(req);
        return new ApiResponse(pageWithPermission);
    }

    @PostMapping("upload")
    @ApiOperation("上传文件")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file){
        return new ApiResponse(signedService.upload(file));
    }

    @PostMapping("createSignFlows")
    @ApiOperation("发起签署-创建流程")
    public ApiResponse createSignFlows(@RequestBody StandardCreateFlowBO req){
        JSONObject signFlows = signedService.createSignFlows(req);
        return new ApiResponse(signFlows);
    }

//    @GetMapping("getSignDetail/{signFlowId}")
//    @ApiOperation("获取签署流程进度详情")
//    public ApiResponse getSignDetail(@PathVariable Integer signFlowId){
//        JSONObject signDetail = signedService.getSignDetail(signFlowId);
//        return new ApiResponse(signDetail);
//    }

    @PostMapping("getSignRecords")
    @ApiOperation("获取签署列表")
    public ApiResponse getSignRecords(){
        //发起人、签署方、发起时间入库，签署状态从详情数据中筛选出来
        return  new ApiResponse(null);
    }
}
