package com.hfi.insurance.config;


import com.hfi.insurance.service.InstitutionInfoService;
import com.hfi.insurance.utils.AppConfigUtil;
import com.hfi.insurance.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class ApplicationInit implements ApplicationRunner {


    @Value("${swagger.enable:false}")
    private boolean enableSwagger;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String port;

    @Resource
    private InstitutionInfoService institutionInfoService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("==================================================");
        log.info("| 项目启动初始化");
        log.info("| 当前项目环境为：{}", AppConfigUtil.getActiveProfile());
        if (enableSwagger) {
            log.info("| Swagger API http://"
                    + WebUtil.getServerIP()
                    + ":"
                    + port
                    + contextPath
                    + "swagger-ui.html#/");
            log.info("| Doc API http://"
                    + WebUtil.getServerIP()
                    + ":"
                    + port
                    + contextPath
                    + "/doc.html");
        }
        try {
            institutionInfoService.parseExcel();
        } catch (IOException e) {
            log.error("数据初始化失败,{}",e.getMessage());
        }
        log.info("| 项目启动成功");
        log.info("==================================================");
    }

}
