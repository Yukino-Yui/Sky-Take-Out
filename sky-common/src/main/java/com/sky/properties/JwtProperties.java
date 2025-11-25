package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


//通过把yml字段里的值封装成一个properties对象
@Component //该注解标注当前类为Bean类，Bean = Spring 自动创建并统一管理的对象
@ConfigurationProperties(prefix = "sky.jwt") //表示该类是"配置属性类"，即配置文件yml里的值，自动注入到该类的字段中
@Data //自动实现get set方法
public class JwtProperties {

    /**
     * 注意驼峰会自动对应（由框架实现了自动转换）这种形式 kebab-case: big-title
     * 管理端员工生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    /**
     * 用户端微信用户生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
