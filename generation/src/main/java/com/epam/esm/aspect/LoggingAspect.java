package com.epam.esm.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
    private Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(public int com.epam.esm.creator.*.create(int))")
    public void createMethods() {
    }

    @Before("createMethods()")
    public void logMethodCall(JoinPoint jp) {
        String typeName = jp.getSignature().getDeclaringType().getSimpleName();
        logger.info(typeName + " started entity generation.");
    }

    @AfterReturning(value = "createMethods()", returning = "numberOfGeneratedEntities")
    public void logMethodReturnValue(JoinPoint jp, int numberOfGeneratedEntities) {
        String typeName = jp.getSignature().getDeclaringType().getSimpleName();
        logger.info(typeName + " generated " + numberOfGeneratedEntities + " entities.");
    }
}
