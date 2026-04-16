package com.rehab.managerv2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志注解
 */
@Target(ElementType.METHOD) // 告诉 Java：这个注解只能贴在方法上
@Retention(RetentionPolicy.RUNTIME) // 告诉 Java：在系统运行的时候这个注解也要一直活着，不能被清理掉
public @interface Log {

    // 我们可以给注解加一个属性，用来描述这个方法是干嘛的
    // 比如：@Log("删除人才档案")
    String value() default "";

}