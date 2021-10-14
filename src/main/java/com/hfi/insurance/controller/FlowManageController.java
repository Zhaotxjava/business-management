package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.sign.req.CreateSignFlowReq;
import com.hfi.insurance.model.sign.req.GetPageWithPermissionReq;
import com.hfi.insurance.service.SignedBizService;
import com.hfi.insurance.service.SignedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author ChenZX
 * @Date 2021/6/30 17:38
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping(value = "/sign")
@Api(tags = {"【批量发起接口】"})
@CrossOrigin
public class FlowManageController {
    @Resource
    private SignedService signedService;
    @Resource
    private SignedBizService signedBizService;


    @PostMapping("getTemplate")
    @ApiOperation("分页查询模板列表")
    public ApiResponse getTemplate(@RequestBody GetPageWithPermissionReq req, HttpServletRequest request) {
        String token = request.getHeader("token");
 /*       if (StringUtils.isNotBlank(token)) {
            return signedBizService.getPageWithPermission(req, token);
        } else {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), ErrorCodeEnum.PARAM_ERROR.getMessage());
        }*/
        return signedBizService.getPageWithPermission(req, token);
    }

    @GetMapping("getTemplateDetailInfo")
    @ApiOperation("获取模板详细信息")
    public ApiResponse getTemplateDetailInfo(@RequestParam("templateId") String templateId) {
        return signedBizService.getTemplateInfo(templateId);
    }

//    @PostMapping("upload")
//    @ApiOperation("上传文件")
//    public void uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response){
//        JSONObject upload = signedService.upload(file);
//        String fileKey = upload.getString("fileKey");
//        response.setContentType("text/html; charset=utf-8");
//        try {
//            response.getWriter().write(fileKey);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @PostMapping(value = "upload", produces = "text/html;charset=UTF-8")
    @ApiOperation("上传文件")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        JSONObject upload = signedService.upload(file);
        if (upload.containsKey("errCode")) {
            return JSON.toJSONString(new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), upload.getString("msg")));
        }
//        String fileKey = upload.getString("fileKey");
        upload.put("fileName", file.getOriginalFilename());
        return JSON.toJSONString(new ApiResponse(upload));
    }

    @PostMapping("createSignFlows")
    @ApiOperation("发起签署-创建流程")
    public ApiResponse createSignFlows(@RequestBody CreateSignFlowReq req, HttpServletRequest request) {
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (requestAttributes != null){
//            HttpServletRequest request = requestAttributes.getRequest();
//            HttpSession session =  request.getSession();
//            return signedBizService.createSignFlow(req,session);
//        }else {
//            return new ApiResponse(ErrorCodeEnum.SYSTEM_ERROR);
//        }
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            return signedBizService.createSignFlow(req, token);
        } else {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), ErrorCodeEnum.PARAM_ERROR.getMessage());
        }
    }


}
