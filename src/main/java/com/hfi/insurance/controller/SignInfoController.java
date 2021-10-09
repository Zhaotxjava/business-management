package com.hfi.insurance.controller;


import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.sign.req.GetRecordInfoReq;
import com.hfi.insurance.model.sign.req.GetSignUrlsReq;
import com.hfi.insurance.service.SignedInfoBizService;
import com.hfi.insurance.service.SignedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
@CrossOrigin
public class SignInfoController {

    @Resource
    private SignedService signedService;

    @Resource
    private SignedInfoBizService signedInfoBizService;

    @ApiOperation("获取签署流程记录")
    @PostMapping("/getSignInfoRecord")
    public ApiResponse getSignInfoRecord(@RequestBody GetRecordInfoReq req, HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), ErrorCodeEnum.PARAM_ERROR.getMessage());
        }
        return signedInfoBizService.getSignedRecord(token, req);
    }

    @GetMapping("getSignDetail/{signFlowId}")
    @ApiOperation("获取签署流程进度详情-不用联调")
    public ApiResponse getSignDetail(@PathVariable Integer signFlowId) {
        JSONObject signDetail = signedService.getSignDetail(signFlowId);
        return new ApiResponse(signDetail);
    }


    @GetMapping("getPreviewUrl")
    @ApiOperation("预览")
    public ApiResponse getPreviewUrl(@RequestParam("fileKey") String fileKey, @RequestParam(value = "docId", required = false) String docId) {
        return signedInfoBizService.getPreviewUrl(fileKey, docId);
    }

    @GetMapping("getSignFlowDocUrls")
    @ApiOperation("查看")
    public ApiResponse getSignFlowDocUrls(@RequestParam("signFlowId") String signFlowId) {
        return signedInfoBizService.getSignFlowDocUrls(signFlowId);
    }

    @PostMapping("getSignUrl")
    @ApiOperation("获取二维码")
    public ApiResponse getSignUrl(@RequestBody GetSignUrlsReq req) {
        if (req.getAccountId().isEmpty()) {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, "未检测到商户信息注册信息");
        }
        return signedInfoBizService.getSignUrls(req);
    }
}

