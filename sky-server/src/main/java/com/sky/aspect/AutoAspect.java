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
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，完成自动填充功能
 */
@Aspect
@Component
@Slf4j
public class AutoAspect {
    //execution(* com.sky.mapper.*.*(..)) &&
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("开始进行数据填充");

        MethodSignature Signature = (MethodSignature) joinPoint.getSignature();//获取方法签名
        AutoFill autoFill = Signature.getMethod().getAnnotation(AutoFill.class);//获得方法注解对象
        OperationType value = autoFill.value();//获取数据库操作类型
        //获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        LocalDateTime localDateTime = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        //根据对应的数据库操作类型，为对应的属性赋值(暴力反射)
        if (value == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setCreateTime.invoke(entity,localDateTime);
                setCreateUser.invoke(entity,id);
                setUpdateTime.invoke(entity,localDateTime);
                setUpdateUser.invoke(entity,id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (value == OperationType.UPDATE){
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity,localDateTime);
                setUpdateUser.invoke(entity,id);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
