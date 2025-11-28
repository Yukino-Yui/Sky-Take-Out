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
@Component //Bean类标识，把这个类交给spring容器来处理
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */

    // Pointcut统一拦截加入了AutoFill注解的方法
    // @Pointcut("表达式")只拦截 mapper包内的方法，并且这些方法上必须带 @AutoFill 注解
    // 第一个*表示返回值任意，(..)表示参数可以任意类型，任意数量。
    // autoFillPointCut() 是一个“切点方法,唯一作用：给切点表达式起名字

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值（实现逻辑），(通知加切入点构成切面)
     * Before表示切面逻辑在 目标方法执行之前 执行
     * 这里是在执行符合 autoFillPointCut 这个切点规则的方法之前，先执行当前方法
     * 它里面包含了方法名、方法参数、方法所属的类、方法上的注解、甚至目标对象的实例
     * 你可以把它想象成：当我拦截到某个方法时，JoinPoint 就是我手里拿到的这个方法全部信息的包装对象。
     */

    @Before("autoFillPointCut()")
    //Spring AOP 会自动把当前被拦截的方法信息注入到这个参数里
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //1.获取到当前被拦截的方法上的数据库操作类型,MethodSignature子接口才能获取到方法对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //2.获取到当前被拦截的方法（update，insert）的参数--也就是实体对象
        // 这里人为约定把实体类型的参数放在参数的第一个位置（比如Employee employee）
        Object[] args = joinPoint.getArgs(); //不知道参数是什么类型，所以用Object类型的数组封装
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];//获取要填充字段的实体对象

        //3.准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //为四个公共字段来赋值
            try {

                /**
                 * 通过反射获取成员方法对象
                 * getDeclaredMethod()获取成员方法，(Declared表示无论私有，protected还是公共都能获取，不加的话表示只能获取public修饰的)
                 * entity.getClass()通过实体类对象来获取字节码文件对象
                 * (AutoFillConstant.SET_CREATE_TIME，Long.class)表示方法名，和参数类型
                 */

                Class<?> clazz = entity.getClass();
                Method setCreateTimes = clazz.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = clazz.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //运行set方法，来为对象属性赋值(获取的方法名.invoke()表示运行方法)
                setCreateTimes.invoke(entity,now); //指定对象是entity，值是已经获取的准备赋值的数据
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
                Class<?> clazz = entity.getClass();
                Method setUpdateTime = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = clazz.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
