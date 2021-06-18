package com.hfi.insurance.controller;

import com.hfi.insurance.common.ApiResponse;
import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.model.dto.InstitutionInfoAddReq;
import com.hfi.insurance.service.InstitutionInfoService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@Api(tags = {"【接口】"})
@RequestMapping("/hfi")
public class InstitutionInfoController {

    @Resource
    private InstitutionInfoService institutionInfoService;

    /**
     * 首页
     *
     * @return
     */
    @GetMapping("home")
    public String index() {
        return "index";
    }

    @GetMapping("getInstitutionInfoList")
    public ApiResponse getInstitutionInfoList(){
        return institutionInfoService.getInstitutionList();
    }

    @PostMapping("getInstitutionInfoByNumber")
    public ApiResponse getClinicInfoByNumber(@RequestParam(value = "number") String number){
        return institutionInfoService.getInstitutionInfoByNumber(number);
    }

    @PostMapping("appendInstitutionInfo")
    public ApiResponse appendInstitutionInfo(@RequestBody InstitutionInfoAddReq req){
        return institutionInfoService.appendInstitutionInfo(req);
    }

    @PostMapping("downloadExcel")
    public ApiResponse downloadExcel(){
        return institutionInfoService.downloadExcel();
    }


}
