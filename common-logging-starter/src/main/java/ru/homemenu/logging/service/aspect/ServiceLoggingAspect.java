package ru.homemenu.logging.service.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import ru.homemenu.logging.config.property.ServiceLoggingProperty;
import ru.homemenu.logging.service.event.ServiceLogEvent;
import ru.homemenu.logging.structure.LogField;

import java.util.ArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Aspect
public class ServiceLoggingAspect {

    private final ServiceLoggingProperty serviceLoggingProperty;

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceLoggingPointcut() {
    }

    @Around("serviceLoggingPointcut()")
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
        Logger log = LoggerFactory.getLogger(targetClass);

        String className = joinPoint.getTarget().getClass().getSimpleName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String args = getArgs(signature, joinPoint.getArgs());

        log.atLevel(serviceLoggingProperty.level())
                .addKeyValue(LogField.EVENT, ServiceLogEvent.ENTRY)
                .addKeyValue(LogField.CLASS, className)
                .addKeyValue(LogField.METHOD, methodName)
                .addKeyValue(LogField.ARGS, args)
                .log("Entry {}.{}", className, methodName);

        long started = System.nanoTime();
        try {
            Object result = joinPoint.proceed();

            log.atLevel(serviceLoggingProperty.level())
                    .addKeyValue(LogField.EVENT, ServiceLogEvent.EXIT)
                    .addKeyValue(LogField.CLASS, className)
                    .addKeyValue(LogField.METHOD, methodName)
                    .addKeyValue(LogField.RESULT, result == null ? "null" : result.toString())
                    .addKeyValue(LogField.DURATION_MS, getDurationMs(started))
                    .log("Exit {}.{}", className, methodName);

            return result;
        } catch (Throwable throwable) {
            if (serviceLoggingProperty.enableError()) {
                log.atLevel(serviceLoggingProperty.errorLevel())
                        .addKeyValue(LogField.EVENT, ServiceLogEvent.ERROR)
                        .addKeyValue(LogField.CLASS, className)
                        .addKeyValue(LogField.METHOD, methodName)
                        .addKeyValue(LogField.DURATION_MS, getDurationMs(started))
                        .addKeyValue(LogField.ERROR_CLASS, throwable.getClass().getName())
                        .setCause(throwable)
                        .log("Error {}.{}", className, methodName);
            }

            throw throwable;
        }
    }

    private String getArgs(MethodSignature signature, Object[] args) {
        String[] names = signature.getParameterNames();
        ArrayList<String> params = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String name = names != null && i < names.length ? names[i] : "arg_" + i;
            params.add(name + "=" + args[i]);
        }
        return params.stream()
                .collect(Collectors.joining(",", "{", "}"));
    }

    private long getDurationMs(long started) {
        return (System.nanoTime() - started) / 1_000_000L;
    }
}
