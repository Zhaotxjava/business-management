package com.hengtiansoft.springbootthymeleaf.controller;

import com.hengtiansoft.springbootthymeleaf.model.ClinicInfo;
import com.hengtiansoft.springbootthymeleaf.service.ClinicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@RestController
@Api(tags = {"【接口】"})
//@RequestMapping("/hfi")
public class IndexController {

    @Resource
    private ClinicService clinicService;
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);


    /**
     * 首页
     *
     * @return
     */
    @GetMapping("home")
    public String index() {
        return "index";
    }

    @PostMapping("importExcel")
    @ApiOperation(value = "导入excel")
    public void importExcel(MultipartFile file){
        clinicService.parseExcel(file);
    }

    @PostMapping("getClinicInfoByNumber")
    public ClinicInfo getClinicInfoByNumber(@RequestParam(value = "number") String number){
        return clinicService.getClinicInfoByNumber(number);
    }
}
