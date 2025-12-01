package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，方法用来创建AliOssUtil对象
 * 只负责对象创建
 */

@Configuration //--标记为配置类，Spring 会扫描其中的 @Bean 方法。
@Slf4j
public class OssConfiguration {
    /**
     * //@Bean--作用：将方法返回的对象注册为 Bean，放入容器。
     * 关键点：方法参数 AliOssProperties aliOssProperties 会被 Spring 自动注入（容器中已有该 Bean）
     * @param aliOssProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean //表示保证整个spring容器里面只有一个util对象（对于工具类对象一般只要创建一个1个）
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
