package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionV2Model;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.model.sign.req.StandardCreateFlowBO;
import com.hfi.insurance.service.SignedBizService;
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
import javax.servlet.http.HttpSession;

/**
 * @Author ChenZX
 * @Date 2021/6/30 17:38
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping(value = "/sign")
@Api(tags = {"【批量发起接口】"})
public class FlowManageController {
    @Resource
    private SignedService signedService;
    @Resource
    private SignedBizService signedBizService;


    @PostMapping("getTemplate")
    @ApiOperation("分页查询模板列表")
    public ApiResponse getTemplate(@RequestBody GetPageWithPermissionReq req){
        return signedBizService.getPageWithPermission(req);
    }

    @GetMapping("getTemplateDetailInfo")
    @ApiOperation("获取模板详细信息")
    public ApiResponse getTemplateDetailInfo(@RequestParam("templateId") String templateId){
        return signedBizService.getTemplateInfo(templateId);
    }

    @PostMapping("upload")
    @ApiOperation("上传文件")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file){
        return new ApiResponse(signedService.upload(file));
    }

    @PostMapping("createSignFlows")
    @ApiOperation("发起签署-创建流程")
    public ApiResponse createSignFlows(@RequestBody CreateSignFlowReq req, HttpSession session){
        return signedBizService.createSignFlow(req,session);
    }



}
