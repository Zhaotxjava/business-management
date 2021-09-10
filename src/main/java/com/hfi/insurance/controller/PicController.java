package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.config.PicUploadConfig;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.YbInstitutionPicPath;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.service.IYbInstitutionPicPathService;
import com.hfi.insurance.utils.PicUploadUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private static final String SPILE="#";

    @Autowired
    private IYbInstitutionPicPathService iYbInstitutionPicPathService;

    @Autowired
    private IYbInstitutionInfoService iYbInstitutionInfoService;

    @RequestMapping(value = "/upload/batch", method = RequestMethod.POST)
    @ResponseBody
    //file要与表单上传的名字相同
    public ApiResponse<List<String>> uploadFiles(MultipartFile[] file,HttpServletRequest request) {
         ApiResponse<List<String>> res = PicUploadUtil.uploadFiles(file, picUploadConfig.getUploadPathImg());
         if(res.isSuccess()){
             List<String> data = res.getData();
             YbInstitutionPicPath picPath = new YbInstitutionPicPath();
             picPath.setNumber(request.getHeader("number"));
             picPath.setPicPath(JSONObject.toJSONString(data));
             boolean b = iYbInstitutionPicPathService.save(picPath);
             if (b){
                 return ApiResponse.success();
             }else {
                 return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(),"图片保存失败");
             }
         }
        return res;
    }

}