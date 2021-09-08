package com.hfi.insurance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.ExcelSheetPO;
import com.hfi.insurance.model.YbInstitutionInfo;
import com.hfi.insurance.model.YbInstitutionInfoChange;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.model.dto.InstitutionInfoQueryReq;
import com.hfi.insurance.service.IYbInstitutionInfoService;
import com.hfi.insurance.utils.ImportExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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



//    @PostMapping("/getInstitutionInfoChangeList")
//    @ApiOperation("分页查询外部机构信息")
//    public ApiResponse getInstitutionInfoChangeList(@RequestBody InstitutionInfoQueryReq req, HttpServletRequest httpRequest) {
//
//        return institutionInfoService.getInstitutionInfoChangeList(token, req.getNumber(), req.getInstitutionName(), req.getPageNum(), req.getPageSize());
//    }
//
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
        return institutionInfoService.updateInstitutionInfo(req);
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
    public void addYbInstitutionInfoChange(@RequestBody YbInstitutionInfoChange  ybInstitutionInfoChange) {

        institutionInfoService.addYbInstitutionInfoChange(ybInstitutionInfoChange);
    }










}
