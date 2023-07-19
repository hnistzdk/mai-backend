package com.zdk.mai.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description 获取SpringBean的工具类
 * @Author zdk
 * @Date 2023/3/3 13:35
 */
@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    public static <T> T getBeanIgnoreNotFound(String name) {
        T t = null;

        try {
            t = getBean(name);
        } catch (NoSuchBeanDefinitionException var3) {
            log.error("Spring中不存在名为:{}的类", name);
        }

        return t;
    }

    public static <T> T getBeanIgnoreNotFound(Class<T> clazz) {
        T t = null;

        try {
            t = getBean(clazz);
        } catch (NoSuchBeanDefinitionException var3) {
            log.error("Spring中不存在类型为:{}的类", clazz);
        }

        return t;
    }

    public static <T> T getBeanIgnoreNotFound(String name, Class<T> clazz) {
        T t = null;

        try {
            t = getBean(name, clazz);
        } catch (NoSuchBeanDefinitionException var4) {
            log.error("Spring中不存在名为:{},类型为:{}的类", name, clazz);
        }

        return t;
    }
}
