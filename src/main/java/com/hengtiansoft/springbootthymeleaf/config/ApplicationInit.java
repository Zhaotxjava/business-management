package com.hengtiansoft.springbootthymeleaf.config;


import com.hengtiansoft.springbootthymeleaf.utils.AppConfigUtil;
import com.hengtiansoft.springbootthymeleaf.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(ApplicationArguments args) {
        log.info("==================================================");
        log.info("| 项目启动初始化");
        log.info("| 当前项目环境为：{}", AppConfigUtil.getActiveProfile());

//        if (AppConfigUtil.isDevEnv()) {
//            if (enableSwagger) {
//                log.info("| Swagger API http://"
//                        + WebUtil.getServerIP()
//                        + ":"
//                        + port
//                        + contextPath
//                        + "swagger-ui.html#/");
//                log.info("| Doc API http://"
//                        + WebUtil.getServerIP()
//                        + ":"
//                        + port
//                        + contextPath
//                        + "doc.html");
//            }
//        }
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

        log.info("| 项目启动成功");
        log.info("==================================================");
    }

}
