package com.example.aspects;

import com.example.services.WsService.Song;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;


@Aspect
@Component
public class defaultSearchAspect {
    @Around("execution(* com.example.services.WsService.defaultSearch(..))")
    public Object aroundDefaultSearch(ProceedingJoinPoint joinPoint) {
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            System.out.println("Uh oh, something went horribly wrong within WsService.defaultSearch()!" + e);
            return null;
        }
        System.out.println("success");
        return result;
    }
}
