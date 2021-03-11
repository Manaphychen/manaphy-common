package com.cgp.common.annotation;

import java.lang.annotation.*;

/**
 * 接口防刷注解类
 *
 * @author Manaphy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limiter {

    /**
     * 从第一次访问接口的时间到cycle周期时间内，无法超过frequency次
     */
    int frequency() default 20;

    /**
     * 周期时间,单位ms：
     * 默认周期时间为一分钟
     */
    long cycle() default 60 * 1000;

    /**
     * 返回的错误信息
     */
    String message() default "请求过于频繁";

    /**
     * 到期时间,单位s：
     * 如果在cycle周期时间内超过frequency次，则默认1分钟内无法继续访问
     */
    long expireTime() default 60;
}

