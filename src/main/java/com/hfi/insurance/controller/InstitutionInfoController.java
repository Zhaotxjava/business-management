package com.hfi.insurance.controller;

import com.hfi.insurance.model.InstitutionInfo;
import com.hfi.insurance.service.InstitutionInfoService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@Api(tags = {"【接口】"})
//@RequestMapping("/hfi")
public class InstitutionInfoController {

    @Resource
    private InstitutionInfoService institutionInfoService;
    private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionInfoController.class);


    /**
     * 首页
     *
     * @return
     */
    @GetMapping("home")
    public String index() {
        return "index";
    }

//    @PostMapping("importExcel")
//    @ApiOperation(value = "导入excel")
//    public void importExcel(MultipartFile file){
//        institutionInfoService.parseExcel(file);
//    }

    @PostMapping("getClinicInfoByNumber")
    public InstitutionInfo getClinicInfoByNumber(@RequestParam(value = "number") String number){
        return institutionInfoService.getInstitutionInfoByNumber(number);
    }
}
