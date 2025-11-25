package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，通过反射为公共字段赋值（实现公共字段自动填充逻辑）
 */
@Aspect  //声明当前类为Aspect切面
@Component //标识，说明这个类也是交给spring容器来处理
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */

    // 第一个*表示返回值任意，Pointcut统一拦截加入了AutoFill注解的方法
    // 表示要访问com.sky.mapper的包下的所有的类或接口下的所有方法(.*.*)，
    // 通过逻辑表达式&& @annotation来指定，所以就变成了这下面的所有的update和insert方法。
    // ..表示参数可以任意类型，任意数量。annotation（里面是全类名）

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值（实现逻辑），(通知加切入点构成切面)
     */ //Before表示在执行方法前执行autoFIll里面的增强逻辑，将通知和切入点绑定

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前被拦截的方法（update，insert）的参数--实体对象
        Object[] args = joinPoint.getArgs(); //一个道理不知道参数是什么类型，所以用Object类型的数组封装
        if(args == null || args.length == 0){
            return;
        }
          //约定把实体类型的参数放在参数的第一个位置（比如Employee employee）
        Object entity = args[0];//因为方法里面的实体类型不确定（可能是employee，category等），所以用Object来接收

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为四个公共字段来赋值
            try {
                Method setCreateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射来为对象属性赋值
                setCreateTimes.invoke(entity,now); //指定对象是entity，值是获取的准备赋值的数据
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else if(operationType == OperationType.UPDATE) {
            //为两个公共字段来赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
