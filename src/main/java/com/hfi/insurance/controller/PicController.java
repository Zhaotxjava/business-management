package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfi.insurance.aspect.anno.LogAnnotation;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.config.PicUploadConfig;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.enums.PicType;
import com.hfi.insurance.mapper.YbInstitutionPicPathMapper;
import com.hfi.insurance.model.*;
import com.hfi.insurance.model.dto.PicCommitPath;
import com.hfi.insurance.service.*;
import com.hfi.insurance.utils.PicUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private SignedService signedService;

    @Autowired
    private YbInstitutionPicPathMapper ybInstitutionPicPathMapper;

    @Autowired
    private IYbOrgTdService iYbOrgTdService;

    //
//    @RequestMapping(value = "/upload/batch", method = RequestMethod.POST)
//    @ApiOperation("上传批量图片")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
//            @ApiImplicitParam(paramType = "header", name = "orgInstitutionCode", value = "机构", dataType = "String", required = false)
//    })
//    @ResponseBody
//    //file要与表单上传的名字相同
//    @LogAnnotation
//    public ApiResponse<PicPathRes> uploadFiles(MultipartFile[] xkz, MultipartFile[] yyzz, HttpServletRequest request) {
//        String number = request.getHeader("number");
//        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
//        log.info("/upload/batch 上传图片入参 xkz.length={},yyzz.length={},number={},orgInstitutionCode={}"
//                , xkz.length, yyzz.length, number, orgInstitutionCode);
//        if (xkz.length <= 0 && yyzz.length <= 0) {
//            log.info("未获取到图片");
//            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR.getCode(), "未获取到图片");
//        }
//        ApiResponse check = PicUploadUtil.checkFile(xkz);
//        if (!check.isSuccess()) {
//            return check;
//        }
//        check = PicUploadUtil.checkFile(yyzz);
//        if (!check.isSuccess()) {
//            return check;
//        }
//        //返回的图片列表
//        ApiResponse<PicPathRes> res = PicUploadUtil.uploadFiles2(xkz, yyzz, picUploadConfig.getUploadPathImg(),number);
//        if (res.isSuccess()) {
//            PicPathRes data = res.getData();
//            YbInstitutionPicPath picPath = new YbInstitutionPicPath();
//            picPath.setNumber(number);
//            picPath.setPicPath(JSONObject.toJSONString(data));
//            boolean b = iYbInstitutionPicPathService.saveOrUpdate(picPath);
//            if (b) {
//                YbInstitutionInfo ybInstitutionInfo = iYbInstitutionInfoService.getInstitutionInfo(number);
//                YbInstitutionInfoChange change = new YbInstitutionInfoChange();
//                BeanUtils.copyProperties(ybInstitutionInfo,change);
//                change.setLicensePicture(JSONObject.toJSONString(data.getXkzList()));
//                change.setBusinessPicture(JSONObject.toJSONString(data.getYyzzList()));
////                change.setNumber(picPath.getNumber());
////                change.setOrgInstitutionCode(orgInstitutionCode);
//                iYbInstitutionInfoService.addYbInstitutionInfoChange(change);
//                return res;
//            } else {
//                log.info("图片保存失败");
//                return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), "图片保存失败");
//            }
//        }
//        return res;
//    }
//
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
        if (p.equals(PicType.UNKNOW)) {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, " picType非法：" + picType);
        }

        log.info("/upload/one 上传单个图片入参operationId={} file.isEmpty={},number={},orgInstitutionCode={}"
                , operationId, file.isEmpty(), number, orgInstitutionCode);
        ApiResponse apiResponse = null;
        try {
            apiResponse = PicUploadUtil.cacheFile(file, operationId, picType, picUploadConfig.getUploadPathImg(), number);
        } catch (IOException e) {
            log.error("/upload/one 上传单个图片异常", e);
            return ApiResponse.fail(ErrorCodeEnum.FILE_UPLOAD_ERROR);
        }
        log.info("/upload/one 上传单个图片出参",JSONObject.toJSONString(apiResponse));
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
        String institutionName = request.getHeader("institutionName");
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
                    YbInstitutionInfo ybInstitutionInfo = iYbInstitutionInfoService.getInstitutionInfo(number);

                    if (Objects.nonNull(ybInstitutionInfo) || StringUtils.isNotBlank(ybInstitutionInfo.getInstitutionName())) {
                        BeanUtils.copyProperties(ybInstitutionInfo, change);
                    } else {
                        YbOrgTd orgTd = iYbOrgTdService.getYbOrgTdByNumber(number);
                        log.info("ybInstitutionInfo 查询结果：{} orgTd = {}"
                                ,JSONObject.toJSONString(ybInstitutionInfo),JSONObject.toJSONString(orgTd));
                        change.setNumber(picPath.getNumber());
                        change.setInstitutionName(orgTd.getAkb021());
                        change.setInstitutionName(institutionName);
                        change.setOrgInstitutionCode(orgInstitutionCode);
                    }
                    change.setLicensePicture(JSONObject.toJSONString(data.getXkzList()));
                    change.setBusinessPicture(JSONObject.toJSONString(data.getYyzzList()));
                    iYbInstitutionInfoService.addYbInstitutionInfoChange(change);
                    return res;
                } else {
                    log.info("/upload/commit 提交图片失败operationId={} number={}"
                            , operationId, number );
                    return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), "图片保存失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("/upload/commit 提交图片出参res={}", JSONObject.toJSONString(res));
        return res;
    }

    @RequestMapping(value = "/upload/getPicByNumberList", method = RequestMethod.POST)
    @ApiOperation("获取机构图片地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "number", value = "机构代码", dataType = "String", allowMultiple = true, required = true),})
    //file要与表单上传的名字相同
    @LogAnnotation
    public ApiResponse<List<YbInstitutionPicPath>> getPicByNumber(@RequestBody Set<String> number) {
        QueryWrapper<YbInstitutionPicPath> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.in("number", number);
        List<YbInstitutionPicPath> ybInstitutionPicPaths = ybInstitutionPicPathMapper.selectList(objectQueryWrapper);
        log.info("查询结果：{}", JSONObject.toJSONString(ybInstitutionPicPaths));
        //返回的图片列表
        return ApiResponse.success(ybInstitutionPicPaths);
    }

    @RequestMapping(value = "/upload/getPicByNumber", method = RequestMethod.POST)
    @ApiOperation("获取机构图片地址")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "number", value = "机构代码", dataType = "String", required = true),})
    //file要与表单上传的名字相同
    @LogAnnotation
    public ApiResponse<List<YbInstitutionPicPath>> getPicByNumber(@RequestParam String number) {
        QueryWrapper<YbInstitutionPicPath> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("number", number);
        YbInstitutionPicPath ybInstitutionPicPath = ybInstitutionPicPathMapper.selectOne(objectQueryWrapper);
        log.info("查询结果：{}", JSONObject.toJSONString(ybInstitutionPicPath));
        //返回的图片列表
        return ApiResponse.success(ybInstitutionPicPath);
    }

    @RequestMapping(value = "/upload/getPicBase64", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "number", value = "机构代码", dataType = "String", required = true),})
    @ApiOperation("获取机构图片BASE64")
//    @LogAnnotation
    public ApiResponse getPicBase64(@RequestParam String filePath) {
        log.info("获取机构图片BASE64 filePath={}", filePath);
        ApiResponse response;
        if (StringUtils.isBlank(filePath)) {
            response = ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, " 入参为空");
        } else {
            response = ApiResponse.success(PicUploadUtil.getBase64(filePath));
        }
        log.info("获取机构图片BASE64结果={}",response.getCode());
        return response;

    }

    @RequestMapping(value = "/upload/getPicBase64List", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(allowMultiple = true, name = "filePaths", value = "机构代码", dataType = "String", required = true),})
    @ApiOperation("获取机构图片BASE64列表")
    @LogAnnotation
    public ApiResponse getPicBase64List(@RequestBody List<String> filePaths) {
        if (filePaths.isEmpty()) {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, " 入参为空");
        }
        List<String> pathList = new ArrayList<>();
        filePaths.forEach(path -> {
            if (StringUtils.isNotBlank(path)) {
                pathList.add(PicUploadUtil.getBase64(path));
            }

        });
        return ApiResponse.success(pathList);
    }

//    @GetMapping("/loadimg")
//    public void getImg2(HttpServletResponse response, String imgPath ) {
//
//        //这里省略掉通过id去读取图片的步骤。
//        File file = new File("imgPath");//imgPath为服务器图片地址
//
//        if(file.exists() && file.isFile()){
//
//            FileInputStream fis = null;
//            OutputStream os = null;
//
//            try {
//                fis = new FileInputStream(file);
//                os = response.getOutputStream();
//                int count = 0;
//                byte[] buffer = new byte[1024 * 8];
//                while ((count = fis.read(buffer)) != -1) {
//                    os.write(buffer, 0, count);
//                    os.flush();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    fis.close();
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    @RequestMapping(value = "/upload/toSystem", method = RequestMethod.POST)
//    @ResponseBody
//    @ApiOperation("上传单个图片到E签宝")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
//            @ApiImplicitParam(paramType = "header", name = "orgInstitutionCode", value = "机构", dataType = "String", required = false),
//            @ApiImplicitParam(paramType = "header", name = "operationId", value = "操作码", dataType = "String", required = true),
//            @ApiImplicitParam(paramType = "header", name = "picType", value = "图片类型", dataType = "String", required = true)
//    })
//    //file要与表单上传的名字相同
//    public ApiResponse<PicPathRes> uploadToFileSystem(MultipartFile file, HttpServletRequest request) {
//        ApiResponse apiResponse = PicUploadUtil.checkOneFile(file);
//        if (!apiResponse.isSuccess()) {
//            return apiResponse;
//        }
//        String number = request.getHeader("number");
//        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
//        String operationId = request.getHeader("operationId");
//        String picType = request.getHeader("picType");
//        PicType p = PicType.getPicType(picType);
//        if (p.equals(PicType.UNKNOW)) {
//            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR, " picType非法：" + picType);
//        }
//        PicCommitPath picCommit = PicUploadUtil.picCommitPath.get(operationId);
//        if (Objects.isNull(picCommit)) {
//            picCommit = new PicCommitPath();
//        }
//
//        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        DateFormat df = new SimpleDateFormat("yyyyMMdd");
//        Calendar calendar = Calendar.getInstance();
//        String dateName = df.format(calendar.getTime());
//        String fileName = picType + "_" + number + "_" + dateName + "_"
//                + UUID.randomUUID().toString().replace("-", "").substring(0, 11) + fileSuffix;
//        log.info("/upload/one 上传单个图片入参operationId={} file.isEmpty={},number={},orgInstitutionCode={},fileName={}"
//                , operationId, file.isEmpty(), number, orgInstitutionCode, fileName);
//        JSONObject uploadJson = signedService.upload(file, fileName);
//        ApiResponse response = signedService.isResultSuccess(uploadJson);
//        if (!response.isSuccess()) {
//            return response;
//        }
//        log.info("上传单个图片返回：{}", uploadJson);
//        String fileKey = uploadJson.getString("fileKey");
//        if (StringUtils.isBlank(fileKey)) {
//            return new ApiResponse(ErrorCodeEnum.NETWORK_ERROR.getCode(), "上传单个图片未获取到key：" + uploadJson.getString("msg"));
//        }
////        JSONObject downloadUrlJson=  signedService.getDownloadUrl(null,fileKey);
////        log.info("上传单个图片返回：{}",downloadUrlJson);
////        response = signedService.isResultSuccess(downloadUrlJson);
////        if (!response.isSuccess()) {
////            return response;
////        }
////        String downloadUrl = downloadUrlJson.getString("downloadUrl");
//
//        switch (p) {
//            case XKZ:
//                picCommit.getXkz().add(fileKey);
//                break;
//            case YYZZ:
//                picCommit.getYyzz().add(fileKey);
//                break;
//            default:
//                picCommit.getXkz().add(fileKey);
//        }
//        PicUploadUtil.picCommitPath.put(operationId, picCommit);
//        return apiResponse;
//    }

//    @RequestMapping(value = "/upload/getPicUrl", method = RequestMethod.POST)
//    @ResponseBody
//    @ApiOperation("获取图片地址")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
//            @ApiImplicitParam(paramType = "header", name = "fileKey", value = "图片类型", dataType = "String", required = true)
//    })
//    //file要与表单上传的名字相同
//    public ApiResponse<PicPathRes> getPicUrl(HttpServletRequest request) {
//        String number = request.getHeader("number");
//
//        YbInstitutionPicPath ybInstitutionPicPath = ybInstitutionPicPathMapper.selectById(number);
//        String picPath = ybInstitutionPicPath.getPicPath();
//        JSONObject jsonObject1 = JSONObject.parseObject(picPath);
////        JSONArray jsonArray = jsonObject1.getJSONArray("xkzList");
//        PicPathRes picPathRes = JSONObject.toJavaObject(jsonObject1,PicPathRes.class);
//
//        List<String> xkzListRes = new ArrayList<>();
//        List<String> xkzList=picPathRes.getXkzList();
//        for (int i = 0; i < xkzList.size(); i++) {
//            String fileKey = String.valueOf(xkzList.get(i));
//            log.info("fileKey = {}",fileKey);
//            JSONObject uploadJson = signedService.getDownloadUrl(null, fileKey);
//            ApiResponse response = signedService.isResultSuccess(uploadJson);
//            if (response.isSuccess()) {
//                xkzListRes.add(uploadJson.getString("downloadUrl"));
//            }
//        }
//        List<String> yyzzListRes = new ArrayList<>();
//        List<String> yyzzList=picPathRes.getYyzzList();
//
//        for (int i = 0; i < yyzzList.size(); i++) {
//            String fileKey = String.valueOf(yyzzList.get(i));
//            log.info("fileKey = {}",fileKey);
//            JSONObject uploadJson = signedService.getDownloadUrl(null, fileKey);
//            ApiResponse response = signedService.isResultSuccess(uploadJson);
//            if (response.isSuccess()) {
//                yyzzListRes.add(uploadJson.getString("downloadUrl"));
//            }
//        }
//        PicPathRes res = new PicPathRes();
//        res.setXkzList(xkzListRes);
//        res.setYyzzList(yyzzListRes);
//        return ApiResponse.success(res);
//    }


//    @RequestMapping(value = "/upload/commit", method = RequestMethod.POST)
//    @ResponseBody
//    @ApiOperation("按照operationId将之前上传的图片地址存入数据库")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", name = "number", value = "机构代码", dataType = "String", required = true),
//            @ApiImplicitParam(paramType = "header", name = "orgInstitutionCode", value = "机构", dataType = "String", required = false),
//            @ApiImplicitParam(paramType = "header", name = "operationId", value = "操作码", dataType = "String", required = true),
//    })
//    //file要与表单上传的名字相同
//    @LogAnnotation
//    public ApiResponse<PicPathRes> commit(HttpServletRequest request) {
//        String number = request.getHeader("number");
//        String orgInstitutionCode = request.getHeader("orgInstitutionCode");
//        String operationId = request.getHeader("operationId");
////        String picType = request.getHeader("picType");
//
//        log.info("/upload/commit 提交图片入参operationId={} number={},orgInstitutionCode={}"
//                , operationId, number, orgInstitutionCode);
//        ApiResponse<PicPathRes> res = new ApiResponse<>();
//        try {
//            res = PicUploadUtil.fileCommit(operationId, picUploadConfig.getUploadPathImg());
//            if (res.isSuccess()) {
//                PicPathRes data = res.getData();
//                YbInstitutionPicPath picPath = new YbInstitutionPicPath();
//                picPath.setNumber(number);
//                picPath.setPicPath(JSONObject.toJSONString(data));
//                boolean b = iYbInstitutionPicPathService.saveOrUpdate(picPath);
//                if (b) {
//                    YbInstitutionInfoChange change = new YbInstitutionInfoChange();
//                    change.setLicensePicture(JSONObject.toJSONString(data.getXkzList()));
//                    change.setBusinessPicture(JSONObject.toJSONString(data.getYyzzList()));
//                    change.setNumber(picPath.getNumber());
//                    change.setOrgInstitutionCode(orgInstitutionCode);
//                    iYbInstitutionInfoService.addYbInstitutionInfoChange(change);
//                    return res;
//                } else {
//                    log.info("图片保存失败");
//                    return ApiResponse.fail(ErrorCodeEnum.RESPONES_ERROR.getCode(), "图片保存失败");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

}