package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


//Spring 管理对象的本质
//Spring 是一个 IoC 容器（控制反转容器）
//它会 创建、管理、初始化所有标记为组件（@Component）的对象（Bean）
@Component
@ConfigurationProperties(prefix = "sky.alioss") //该注解表示该类是"配置属性类"
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
