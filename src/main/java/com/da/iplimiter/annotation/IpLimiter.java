package com.da.iplimiter.annotation;

import java.lang.annotation.*;

/**
 * @Description: IP限流注解*
 * Target是注解的作用目标**
 * ElementType.METHOD是可作用于方法级别**
 * Retention是注解的保留策略**
 * RetentionPolicy.RUNTIME是注解在程序运行期间存在
 * Documented是注解是否将包含在javadoc中
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpLimiter {// 限流注解

    /**
     * 限流ip
     */
    String ipAdress() ;
    /**
     * 单位时间限制通过请求数
     */
    long limit() default 10;

    /**
     * 单位时间，单位秒
     */
    long time() default 1;

    /**
     * 达到限流提示语
     */
    String message();
}
