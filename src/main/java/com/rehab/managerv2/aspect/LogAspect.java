package com.rehab.managerv2.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rehab.managerv2.annotation.Log;
import com.rehab.managerv2.entity.SysLog;
import com.rehab.managerv2.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 终极版：全局操作日志切面处理类（上帝之眼）
 * 融合了 MySQL 持久化与 SLF4J 控制台打印
 */
@Aspect
@Component
@Slf4j // 召唤 SLF4J 门面，用于在 IDEA 控制台打印炫酷日志
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 1. 定义切点：告诉系统，只要方法上贴了 @Log 注解，就被纳入监控范围
     */
    @Pointcut("@annotation(com.rehab.managerv2.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 2. 环绕通知：在目标方法执行的前后，进行拦截和时间统计
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();

        // 🔥 核心：让目标方法（比如真正的删除操作）继续执行
        Object result = point.proceed();

        long time = System.currentTimeMillis() - beginTime; // 计算耗时

        // 执行完毕后，异步/同步保存日志
        saveLog(point, time);

        return result;
    }

    /**
     * 3. 提取信息并保存日志的具体逻辑
     */
    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();

        // 提取注解上的描述（例如："删除人才档案"）
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            sysLog.setOperation(logAnnotation.value());
        }

        // 提取被调用的类名和方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        // 提取请求参数，并转换为 JSON 格式
        Object[] args = joinPoint.getArgs();
        try {
            if (args != null && args.length > 0) {
                // 将第一个参数转为 JSON 字符串。
                // 注意：这里为了严谨加了 try-catch，因为有些参数（如 HttpServletResponse）是不能被转成 JSON 的
                String params = new ObjectMapper().writeValueAsString(args[0]);
                sysLog.setParams(params);
            }
        } catch (Exception e) {
            sysLog.setParams("参数解析异常或包含无法序列化的对象");
        }

        // 提取客户端 IP 地址
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                sysLog.setIp(request.getRemoteAddr());
            }
        } catch (Exception e) {
            sysLog.setIp("获取IP失败");
        }

        // 提取当前操作人的账号（直接从 Spring Security 的上下文中拿，绝对安全可靠！）
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            sysLog.setUsername(username);
        } catch (Exception e) {
            sysLog.setUsername("未知用户");
        }

        // 设置耗时
        sysLog.setTime(time);

        // 🌟 SLF4J 发力点：在控制台打印一条极具大厂范儿的日志
        log.info("【上帝之眼 - 操作审计】用户: [{}] | 动作: [{}] | 目标方法: [{}] | 耗时: {}ms",
                sysLog.getUsername(), sysLog.getOperation(), sysLog.getMethod(), time);

        // 🌟 MySQL 发力点：将组装好的对象持久化到数据库中，作为永久审计凭证
        sysLogService.save(sysLog);
    }
}