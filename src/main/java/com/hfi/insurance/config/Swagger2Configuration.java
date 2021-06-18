package com.hfi.insurance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Slf4j
@Configuration
@EnableSwagger2
public class Swagger2Configuration implements WebMvcConfigurer {
    //api接口包扫描路径
    public static final String SWAGGER_SCAN_BASE_PACKAGE = "com.hengtiansoft.springbootthymeleaf";
    public static final String VERSION = "1.0.0";

    @Value("${swagger.enable:false}")
    private boolean enableSwagger;

    //加载swagger路径在MvcInterceptorConfig类中

    @Bean
    public Docket createRestApi() {
        return new Docket(SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                .select()
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
                .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("后端服务") //设置文档的标题
                .description("") // 设置文档的描述
                .version(VERSION) // 设置文档的版本信息-> 1.0.0 Version information
                .build();
    }
}
