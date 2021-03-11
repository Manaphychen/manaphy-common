package com.cgp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止并发请求注解
 *
 * @author Manaphy
 * @date 2020-04-28
 */
@Target(ElementType.METHOD) // 作用到方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface NoConcurrentSubmit {
    /**
     * 防止重复提交标记注解
     * 设置请求锁定时间
     *
     * @return int
     */
    int lockTime() default 10;
}