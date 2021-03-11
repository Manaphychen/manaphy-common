package com.cgp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等提交
 * 只适用于单机部署的应用.
 *
 * @author Manaphy
 * @date 2020-08-27
 */
@Target(ElementType.METHOD) // 作用到方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface IdempotentSubmit {

    /**
     * 延时时间 在延时多久后可以再次提交 单位:毫秒
     *
     * @return int
     */
    int delaySeconds() default 1000;
}
