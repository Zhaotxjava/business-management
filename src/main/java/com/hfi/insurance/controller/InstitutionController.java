package com.hfi.insurance.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.ExcelSheetPO;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.YbInstitutionInfoChange;
import com.hfi.insurance.model.dto.ArecordQueReq;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.model.dto.InstitutionInfoQueryReq;
import com.hfi.insurance.model.dto.YbInstitutionInfoChangeReq;
import com.hfi.insurance.model.dto.res.CheckImportInstitutionRes;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.utils.ImportExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author ChenZX
 * @Date 2021/7/5 14:54
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping(value = "/institution")
@Api(tags = {"【机构信息管理接口】"})
@CrossOrigin
public class InstitutionController {

    @Resource
    private IYbInstitutionInfoService institutionInfoService;

    @PostMapping("getInstitutionInfoList")
    @ApiOperation("分页查询外部机构信息")
    public ApiResponse getInstitutionInfoList(@RequestBody InstitutionInfoQueryReq req, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("token");
        if (StringUtils.isBlank(token)) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), ErrorCodeEnum.PARAM_ERROR.getMessage());
        }
        return institutionInfoService.getInstitutionInfoList(token, req.getNumber(), req.getInstitutionName(), req.getPageNum(), req.getPageSize());
    }


    @PostMapping("/getInstitutionInfoChangeList")
    @ApiOperation("分页查询外部机构变更表信息")
    public ApiResponse getInstitutionInfoChangeList(@RequestBody YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq) {

        return institutionInfoService.getInstitutionInfoChangeList(ybInstitutionInfoChangeReq);
    }

    @PostMapping("getInstitutionInfoByNumber")
    @ApiOperation("根据机构编号获取机构信息")
    public ApiResponse getClinicInfoByNumber(@RequestParam(value = "number") String number) {
        return new ApiResponse(institutionInfoService.getInstitutionInfo(number));
    }

    @PostMapping("updateInstitutionInfo")
    @ApiOperation("更新机构信息")
    public ApiResponse updateInstitutionInfo(@RequestBody InstitutionInfoAddReq req) {
        // 1> 基础数据校验
        if (StringUtils.isBlank(req.getOrgInstitutionCode())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "组织机构编码不能为空");
        }
        if (StringUtils.isBlank(req.getNumber())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "机构编码不能为空");
        }
        if (StringUtils.isBlank(req.getLegalIdCard())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "法人身份证不能为空");
        }
        if (StringUtils.isBlank(req.getLegalPhone())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "法人手机号不能为空");
        }
        if (StringUtils.isBlank(req.getLegalName())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "法人姓名不能为空");
        }
        if (StringUtils.isBlank(req.getContactName())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "联系人姓名不能为空");
        }
        if (StringUtils.isBlank(req.getContactIdCard())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "联系人身份证不能为空");
        }
        if (StringUtils.isBlank(req.getContactPhone())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "联系人手机号不能为空");
        }
        return institutionInfoService.newUpdateInstitutionInfo(req);
    }

    @PostMapping("checkImportInstitution")
    @ApiOperation("批量导入机构，并检查合法性")
    public ApiResponse checkImportInstitution(@RequestParam("file") MultipartFile multipartFile) {
        log.info("checkImportInstitution 开始：");
        List<ExcelSheetPO> excelSheetList = new ArrayList<>();
        try {
            excelSheetList = ImportExcelUtil.readExcel(multipartFile);
            log.info("文件解析出参：{}", JSONObject.toJSONString(excelSheetList));
        } catch (IOException e) {
            e.printStackTrace();
        }

        CheckImportInstitutionRes res = new CheckImportInstitutionRes();
        if(!excelSheetList.isEmpty()){
            ExcelSheetPO excelSheetPO = excelSheetList.get(0);
//            List<Object> numberRow = excelSheetPO.getDataList().get(0);
            Set<String> allNumber = new HashSet<>();
            for (int i = 0; i < excelSheetPO.getDataList().size(); i++) {
                //去掉重复
//                log.info("-----------------------------{}",excelSheetPO.getDataList().get(i).get(0));
                allNumber.add(String.valueOf(excelSheetPO.getDataList().get(i).get(0)));
            }
//            log.info("-----------------------------{}",allNumber.toString());
            //todo 判断是否符合正确的机构
            List<YbInstitutionInfo> list = institutionInfoService.findLegalInstitution(allNumber);

            list.forEach(y->{
                res.getSuccessSet().add(y.getNumber());
            });
            allNumber.forEach(number ->{
                if(!res.getSuccessSet().contains(number)){
                    res.getFailSet().add(number);
                }
            });
//            log.info("-----------------------------{}",JSONObject.toJSONString(res));
        }else {
            return ApiResponse.fail(ErrorCodeEnum.PARAM_ERROR," 请检查入参文件是否正确");
        }
        return ApiResponse.success(res);
    }

    @PostMapping("import")
    public void importData(@RequestParam("file") MultipartFile multipartFile) {
        List<ExcelSheetPO> excelSheetList = new ArrayList<>();
        try {
            excelSheetList = ImportExcelUtil.readExcel(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<YbInstitutionInfo> list = new ArrayList<>();
        excelSheetList.forEach(excelSheetPO -> {
            List<List<Object>> dataList = excelSheetPO.getDataList();
            dataList.forEach(valueList -> {
                YbInstitutionInfo institutionInfo = new YbInstitutionInfo();
                for (int i = 1; i < valueList.size(); i++) {
                    String value = "";
                    if (valueList.get(i) != null) {
                        value = String.valueOf(valueList.get(i)).trim();
                    }
                    switch (i) {
                        case 2:
                            institutionInfo.setNumber(value);
                            break;
                        case 3:
                            institutionInfo.setInstitutionName(value);
                            break;
                        case 4:
                            institutionInfo.setOrgInstitutionCode(value);
                            break;
                        case 5:
                            institutionInfo.setLegalName(value);
                            break;
                        case 6:
                            institutionInfo.setContactName(value);
                            break;
                        case 7:
                            institutionInfo.setContactPhone(value);
                            break;
                        case 9:
                            institutionInfo.setLegalIdCard(value);
                            break;
                        case 10:
                            institutionInfo.setLegalPhone(value);
                            break;
                        case 11:
                            institutionInfo.setContactIdCard(value);
                            break;
                        default:
                            break;
                    }
                }
                list.add(institutionInfo);
            });
        });
        institutionInfoService.saveBatch(list);
    }


    @PostMapping("/addYbInstitutionInfoChange")
    @ApiOperation("插入机构信息变更记录")
    public void addYbInstitutionInfoChange(@RequestBody YbInstitutionInfoChange ybInstitutionInfoChange) {

        institutionInfoService.addYbInstitutionInfoChange(ybInstitutionInfoChange);
    }


    @SneakyThrows
    @GetMapping("/exportExcel")
    @ApiOperation("导出机构信息变更记录表")
    public void exportExcel(String number, String institutionName, String minupdateTime, String maxupdateTime, HttpServletResponse response) {
        YbInstitutionInfoChangeReq ybInstitutionInfoChangeReq = new YbInstitutionInfoChangeReq();
        ybInstitutionInfoChangeReq.setNumber(number);
        ybInstitutionInfoChangeReq.setInstitutionName(institutionName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isEmpty(minupdateTime) && !StringUtils.isEmpty(maxupdateTime)) {
            ybInstitutionInfoChangeReq.setMaxupdateTime(sdf.parse(maxupdateTime));
            ybInstitutionInfoChangeReq.setMinupdateTime(sdf.parse(minupdateTime));
        }
        institutionInfoService.exportExcel(ybInstitutionInfoChangeReq, response);
    }


    @GetMapping("/exportExcel2")
    @ApiOperation("测试不用管")
    public void exportExcel2(HttpServletResponse response) {

        institutionInfoService.exportExcel2(response);
    }


    @PostMapping("getInstitutionInfobxList")
    @ResponseBody
    @ApiOperation("筛选保险公司按钮")
    public ApiResponse getInstitutionInfobxList(InstitutionInfoQueryReq institutionInfoQueryReq,HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("token");
        if (StringUtils.isBlank(token)) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), ErrorCodeEnum.PARAM_ERROR.getMessage());
        }
        return institutionInfoService.getInstitutionInfobxList(institutionInfoQueryReq,token);
    }





    @PostMapping("/getArecordList")
    @ApiOperation("发起记录分页查询")
    public ApiResponse getArecordList(@RequestBody ArecordQueReq  arecordQueReq) {

        return institutionInfoService.getArecordList(arecordQueReq);
    }


    @SneakyThrows
    @GetMapping("/exportExcel3")
    @ApiOperation("批量发起的记录表格导出")
    public void exportExcel3(String mindateTime, String maxdateTime,HttpServletResponse response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArecordQueReq arecordQueReq = new ArecordQueReq();
        if (!StringUtils.isEmpty(mindateTime) && !StringUtils.isEmpty(maxdateTime)) {
            arecordQueReq.setMaxdateTime(sdf.parse(maxdateTime));
            arecordQueReq.setMindateTime(sdf.parse(mindateTime));
        }
         institutionInfoService.exportExcel3(arecordQueReq,response);
    }

}
