package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用来告诉 AOP 哪些方法需要自动填充公共字段
 */
@Target(ElementType.METHOD) //代表这个AutoFill注解只能加在方法上
@Retention(RetentionPolicy.RUNTIME) // 表示该注解在 运行时依然存在，并且可以被 反射获取。
                                    //如果没有这个，AOP 无法在运行时获取注解值（比如 INSERT / UPDATE），自动填充就完全做不了
public @interface AutoFill {
    //OperationType是已经定义好的枚举类型，
    //数据库操作类型：UPDATE INSERT

    OperationType value();//定义一个OperationType类型的属性，value,加()是因为在注解里面定义的格式

}
