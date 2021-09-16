package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.config.PicUploadConfig;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.enums.PicType;
import com.hfi.insurance.mapper.YbInstitutionPicPathMapper;
import com.hfi.insurance.model.PicPathRes;
import com.hfi.insurance.model.YbInstitutionInfoChange;
import com.hfi.insurance.model.YbInstitutionPicPath;
import com.hfi.insurance.model.dto.GetPicByNumbers;
import com.hfi.insurance.model.dto.PicCommit;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.IYbInstitutionPicPathService;
import com.hfi.insurance.utils.PicUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author jthealth-NZH
 * @Date 2021/9/8 11:44
 * @Describe
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping(value = "/pic")
@Api(tags = {"【图片上传、查看接口】"})
@CrossOrigin
public class PicController {
    @Resource
    private PicUploadConfig picUploadConfig;

    @Autowired
    private IYbInstitutionPicPathService iYbInstitutionPicPathService;

    @Autowired
    private IYbInstitutionInfoService iYbInstitutionInfoService;

    @Autowired
    private YbInstitutionPicPathMapper ybInstitutionPicPathMapper;

    @RequestMapping(value = "/upload/batch", method = RequestMethod.POST)
    @ApiOperation("上传批量图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "header", name = "orgInstitutionCode", value = "机构", dataType = "String", required = false)
    })
    @ResponseBody
    //file要与表单上传的名字相同
    @LogAnnotation
    public ApiResponse<PicPathRes> uploadFiles(MultipartFile[] xkz, MultipartFile[] yyzz, HttpServletRequest request) {
        String number = request.getHeader("number");
        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
        log.info("/upload/batch 上传图片入参 xkz.length={},yyzz.length={},number={},orgInstitutionCode={}"
                , xkz.length, yyzz.length, number, orgInstitutionCode);
        if (xkz.length <= 0 && yyzz.length <= 0) {
            log.info("未获取到图片");
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR.getCode(), "未获取到图片");
        }
        ApiResponse check = PicUploadUtil.checkFile(xkz);
        if (!check.isSuccess()) {
            return check;
        }
        check = PicUploadUtil.checkFile(yyzz);
        if (!check.isSuccess()) {
            return check;
        }
        //返回的图片列表
        ApiResponse<PicPathRes> res = PicUploadUtil.uploadFiles2(xkz, yyzz, picUploadConfig.getUploadPathImg(),number);
        if (res.isSuccess()) {
            PicPathRes data = res.getData();
            YbInstitutionPicPath picPath = new YbInstitutionPicPath();
            picPath.setNumber(number);
            picPath.setPicPath(JSONObject.toJSONString(data));
            boolean b = iYbInstitutionPicPathService.saveOrUpdate(picPath);
            if (b) {
                YbInstitutionInfoChange change = new YbInstitutionInfoChange();
                change.setLicensePicture(JSONObject.toJSONString(data.getXkzList()));
                change.setBusinessPicture(JSONObject.toJSONString(data.getYyzzList()));
                change.setNumber(picPath.getNumber());
                change.setOrgInstitutionCode(orgInstitutionCode);
                iYbInstitutionInfoService.addYbInstitutionInfoChange(change);
                return res;
            } else {
                log.info("图片保存失败");
                return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), "图片保存失败");
            }
        }
        return res;
    }

    @RequestMapping(value = "/upload/one", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("上传单个图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "header", name = "orgInstitutionCode", value = "机构", dataType = "String", required = false),
            @ApiImplicitParam(paramType = "header", name = "operationId", value = "操作码", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "header", name = "picType", value = "图片类型", dataType = "String", required = true)
    })
    //file要与表单上传的名字相同
    public ApiResponse<PicPathRes> cacheFile(MultipartFile file, HttpServletRequest request) {
        String number = request.getHeader("number");
        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
        String operationId = request.getHeader("operationId");
        String picType = request.getHeader("picType");
        PicType p = PicType.getPicType(picType);
        if(p.equals(PicType.UNKNOW)){
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR," picType非法："+picType);
        }

        log.info("/upload/one 上传单个图片入参operationId={} file.isEmpty={},number={},orgInstitutionCode={}"
                , operationId, file.isEmpty(), number, orgInstitutionCode);
        ApiResponse apiResponse = null;
        try {
            apiResponse = PicUploadUtil.cacheFile(file, operationId, picType,picUploadConfig.getUploadPathImg(),number);
        } catch (IOException e) {
            log.error("",e);
           return ApiResponse.fail(ErrorCodeEnum.FILE_UPLOAD_ERROR);
        }
        return apiResponse;
    }

//    @RequestMapping(value = "/upload/one2", method = RequestMethod.POST)
//    @ResponseBody
//    //file要与表单上传的名字相同
//    public ApiResponse<PicPathRes> cacheFile2(MultipartFile file, HttpServletRequest request) {
//        String number = request.getHeader("number");
//        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
//        String operationId = request.getHeader("operationId");
//        String picType = request.getHeader("picType");
//
//        log.info("上传图片入参operationId={} file.isEmpty={},number={},orgInstitutionCode={}"
//                , operationId, file.isEmpty(), number, orgInstitutionCode);
//        ApiResponse apiResponse = PicUploadUtil.cacheFile(file, operationId, picType,picUploadConfig.getUploadPathImg());
//
//        return apiResponse;
//    }

    @RequestMapping(value = "/upload/commit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("按照operationId将之前上传的图片地址存入数据库")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
            @ApiImplicitParam(paramType = "header", name = "orgInstitutionCode", value = "机构", dataType = "String", required = false),
            @ApiImplicitParam(paramType = "header", name = "operationId", value = "操作码", dataType = "String", required = true),
    })
    //file要与表单上传的名字相同
    @LogAnnotation
    public ApiResponse<PicPathRes> commit(HttpServletRequest request) {
        String number = request.getHeader("number");
        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
        String operationId = request.getHeader("operationId");
//        String picType = request.getHeader("picType");

        log.info("/upload/commit 提交图片入参operationId={} number={},orgInstitutionCode={}"
                , operationId, number, orgInstitutionCode);
        ApiResponse<PicPathRes> res = new ApiResponse<>();
        try {
            res = PicUploadUtil.fileCommit(operationId, picUploadConfig.getUploadPathImg());
            if (res.isSuccess()) {
                PicPathRes data = res.getData();
                YbInstitutionPicPath picPath = new YbInstitutionPicPath();
                picPath.setNumber(number);
                picPath.setPicPath(JSONObject.toJSONString(data));
                boolean b = iYbInstitutionPicPathService.saveOrUpdate(picPath);
                if (b) {
                    YbInstitutionInfoChange change = new YbInstitutionInfoChange();
                    change.setLicensePicture(JSONObject.toJSONString(data.getXkzList()));
                    change.setBusinessPicture(JSONObject.toJSONString(data.getYyzzList()));
                    change.setNumber(picPath.getNumber());
                    change.setOrgInstitutionCode(orgInstitutionCode);
                    iYbInstitutionInfoService.addYbInstitutionInfoChange(change);
                    return res;
                } else {
                    log.info("图片保存失败");
                    return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), "图片保存失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @RequestMapping(value = "/upload/getPicByNumber", method = RequestMethod.POST)
    @ApiOperation("获取机构图片")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "number", value = "机构代码", dataType = "Set<String>", required = true),})
    //file要与表单上传的名字相同
    @LogAnnotation
    public ApiResponse<List<YbInstitutionPicPath>> getPicByNumber(@RequestBody Set<String> number ) {
        QueryWrapper<YbInstitutionPicPath> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.in("number",number);
        List<YbInstitutionPicPath> ybInstitutionPicPaths = ybInstitutionPicPathMapper.selectList(objectQueryWrapper);
        //返回的图片列表
        return ApiResponse.success(ybInstitutionPicPaths);
    }

}