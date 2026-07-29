package com.gjq.train.common.aspect;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LogAspect {

    private static final Logger LOG =
            LoggerFactory.getLogger(LogAspect.class);

    public LogAspect() {
        System.out.println("LogAspect");
    }

    /**
     * 定义一个切点。
     *
     * 匹配 com.gjq.train.member 包及其子包中，
     * 类名以 Controller 结尾的类的所有 public 方法。
     */
    @Pointcut(
            "execution(public * " +
                    "com.gjq.train.member..*Controller.*(..))"
    )
    public void controllerPointcut() {
    }

    /**
     * Controller 方法执行前打印请求信息。
     */
    @Before("controllerPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        // 增加日志流水号
        String logId = System.currentTimeMillis()
                + RandomUtil.randomString(3);
        MDC.put("LOG_ID", logId);

        // 获取当前请求
        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            LOG.warn("当前线程中不存在 HTTP 请求信息");
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();

        // 打印请求信息
        LOG.info("------------- 开始 -------------");
        LOG.info(
                "请求地址: {} {}",
                request.getRequestURL(),
                request.getMethod()
        );
        LOG.info(
                "类名方法: {}.{}",
                signature.getDeclaringTypeName(),
                methodName
        );
        LOG.info("远程地址: {}", request.getRemoteAddr());
        printRequestArguments(joinPoint.getArgs());
    }

    /**
     * 环绕 Controller 方法，记录返回结果和方法执行时间。
     */
    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = proceedingJoinPoint.proceed();
            printResponseResult(result);
            LOG.info(
                    "------------- 结束，耗时：{} ms -------------",
                    System.currentTimeMillis() - startTime
            );
            return result;
        } finally {
            MDC.remove("LOG_ID");
        }
    }

    /**
     * 打印请求参数。
     *
     * ServletRequest、ServletResponse 和 MultipartFile
     * 不适合直接进行 JSON 序列化，因此需要排除。
     */
    private void printRequestArguments(Object[] args) {
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (isUnsupportedArgument(args[i])) {
                continue;
            }
            arguments[i] = args[i];
        }
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter =
                createExcludeFilter();
        LOG.info(
                "请求参数: {}",
                JSONObject.toJSONString(arguments, excludeFilter)
        );
    }

    /**
     * 打印 Controller 的返回结果。
     */
    private void printResponseResult(Object result) {
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter =
                createExcludeFilter();
        LOG.info(
                "返回结果: {}",
                JSONObject.toJSONString(result, excludeFilter)
        );
    }

    /**
     * 判断参数是否属于不适合进行 JSON 序列化的特殊类型。
     */
    private boolean isUnsupportedArgument(Object argument) {
        return argument instanceof ServletRequest
                || argument instanceof ServletResponse
                || argument instanceof MultipartFile;
    }

    /**
     * 创建字段过滤器。
     *
     * 手机号等敏感字段不写入日志。
     */
    private PropertyPreFilters.MySimplePropertyPreFilter
    createExcludeFilter() {
        String[] excludeProperties = {"mobile"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludeFilter =
                filters.addFilter();
        excludeFilter.addExcludes(excludeProperties);
        return excludeFilter;
    }
}
