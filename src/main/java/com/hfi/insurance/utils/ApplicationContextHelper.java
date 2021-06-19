package com.hfi.insurance.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    // Spring上下文
    private static ApplicationContext applicationContext;

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取ApplicationContextAware
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 根据Class获取对应实例
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clz) {
        return applicationContext.getBean(clz);
    }
    /**
     * 根据beanName获取对应实例
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }


    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}
