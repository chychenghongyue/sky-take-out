package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    //切点表达式（所有的返回值，mapper包下的所有类所有方法的所有参数）
    public void AutoFillPointCut() {
    }

    @Before("AutoFillPointCut()")
    public void AutoFill(JoinPoint joinPoint) {
        log.info("开始公共字段自动填充");
        //获取被拦截的方法的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获取注解对象
        OperationType operationType = annotation.value();//获取数据库操作类型
        //获取到被拦截的方法的参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;
        Object entity = args[0];//获取第一个参数
        //准备复制的数据
        LocalDateTime nowTime=LocalDateTime.now();
        Long userId= BaseContext.getCurrentId();
        //根据不同的操作类型，为对应的属性通过反射来获取值
        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为对象复制
                setCreateTime.invoke(entity,nowTime);
                setCreateUser.invoke(entity,userId);
                setUpdateTime.invoke(entity,nowTime);
                setUpdateUser.invoke(entity,userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射为对象复制
               setUpdateTime.invoke(entity,nowTime);
                setUpdateUser.invoke(entity,userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
