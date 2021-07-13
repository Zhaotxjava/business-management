package com.hfi.insurance.controller;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.enums.ErrorCodeEnum;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.service.InstitutionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@Slf4j
@Api(tags = {"【接口】"})
public class InstitutionInfoController {

    @Resource
    private InstitutionInfoService institutionInfoService;

    @GetMapping("home")
    public String index(@RequestParam(name = "hospitalid", required = true) String hospitalid, @RequestParam(name = "platid", required = false) String platid, Model model) {
        model.addAttribute("number", hospitalid); //医院编码
        model.addAttribute("areaCode", platid); ///统筹区编码
        return "index";
    }

    @PostMapping("home")
    @ApiOperation("重定向")
    public String index(String hospitalid, String platid, HttpServletRequest request, Model model) {
        //application/x-www-form-urlencoded;charset=UTF-8
        model.addAttribute("number", hospitalid); //医院编码
        model.addAttribute("areaCode", platid); ///统筹区编码
        int flag = 0;
        if (StringUtils.isNotBlank(hospitalid)){
            flag = 1;
        }
        return "redirect:http://baidu.com/index?number=" + hospitalid + "?areaCode=" + platid + "?flag=" + flag;
    }


    @GetMapping("getInstitutionInfoList")
    @ResponseBody
    public ApiResponse getInstitutionInfoList() {
        return institutionInfoService.getInstitutionList();
    }

    @PostMapping("getInstitutionInfoByNumber")
    @ResponseBody
    public ApiResponse getClinicInfoByNumber(@RequestParam(value = "number") String number) {
        return institutionInfoService.getInstitutionInfoByNumber(number);
    }

    @PostMapping("updateInstitutionInfo")
    @ResponseBody
    public ApiResponse updateInstitutionInfo(@RequestBody InstitutionInfoAddReq req) {
        // 1> 基础数据校验
        log.info("更新机构信息参数：{}", req);
        if (StringUtils.isEmpty(req.getNumber())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "机构编码不能为空");
        }
        if (StringUtils.isEmpty(req.getLegalIdCard())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "法人身份证不能为空");
        }
        if (StringUtils.isEmpty(req.getLegalPhone())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "法人手机号不能为空");
        }
        if (StringUtils.isEmpty(req.getLegalName())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "法人姓名不能为空");
        }
        if (StringUtils.isEmpty(req.getContactName())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "联系人姓名不能为空");
        }
        if (StringUtils.isEmpty(req.getContactIdCard())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "联系人身份证不能为空");
        }
        if (StringUtils.isEmpty(req.getContactPhone())) {
            return new ApiResponse(ErrorCodeEnum.PARAM_ERROR.getCode(), "联系人手机号不能为空");
        }
        return institutionInfoService.updateInstitutionInfo(req);
    }

    @GetMapping("downloadExcel")
    public void downloadExcel(HttpServletResponse response) {
        institutionInfoService.downloadExcel(response);
    }

    @GetMapping("/downloadCSV")
    public void downloadCSV(HttpServletResponse response) throws Exception {
        institutionInfoService.downloadCSV(response);
    }


}
