package com.hfi.insurance.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;

@Configuration
@MapperScan("com.hfi.insurance.mapper")
public class PageConfig {
    /**
     * 逻辑删除用
     * @return com.baomidou.mybatisplus.core.injector.ISqlInjector
     */
//    @Bean
//    public ISqlInjector sqlInjector() {
//        return new LogicSqlInjector();
//    }

    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
