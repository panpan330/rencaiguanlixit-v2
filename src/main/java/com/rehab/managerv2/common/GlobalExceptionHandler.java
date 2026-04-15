package com.rehab.managerv2.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：拦截系统中所有的报错
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 在控制台打印真正的报错信息，方便后端查 Bug
        e.printStackTrace();
        // 包装成优雅的 JSON 告诉前端
        return Result.error(500, "系统开小差了：" + e.getMessage());
    }
}