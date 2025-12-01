package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


//Spring 管理对象的本质
//Spring 是一个 IoC 容器（控制反转容器）
//它会 创建、管理、初始化所有标记为组件（@Component）的对象（Bean），然后放在IoC容器里面

/**
 * 只负责配置绑定（yml文件里的具体参数）
 */
@Component //--标记为bean,即这个类就是Bean，直接管理
@ConfigurationProperties(prefix = "sky.alioss") //--自动读取 yml 中 sky.alioss 下的配置，并赋值给类的属性。
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
