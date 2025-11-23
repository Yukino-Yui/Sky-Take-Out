package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识需要进行公共字段自动填充处理的方法
 */
@Target(ElementType.METHOD) //代表这个AutoFill注解只能加在方法上
@Retention(RetentionPolicy.RUNTIME) // 这个注解表明实在运行时生效
public @interface AutoFill {
    //OperationType是已经定义好的枚举类型，
    //数据库操作类型：UPDATE INSERT

    OperationType value();//定义一个OperationType类型的属性，value,加()是因为在注解里面定义的格式

}
