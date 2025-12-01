package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration //一般都是Configuration(配置类) + @Bean(方法返回的对象是Bean)
public class RedisConfiguration {

    /**
     * RedisTemplate 是 Spring Data Redis 提供的工具类，已经封装了 Redis 操作，不需要再封装
     * @param redisConnectionFactory
     * @return
     */
    @Bean //方法返回一个对象，是bean，会被注入到ioc容器
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();

        //设置redis的连接工厂对象
        //作用：告诉 RedisTemplate 如何连接 Redis。
        //如果不设置：RedisTemplate不知道Redis在哪里，无法建立连接、无法操作Redis
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置redis key的序列化器，作用：把 Java 对象转换成字节数组（或反过来）
        //序列化 = 把 Java 对象转换成字节数组（二进制数据）。Java对象（内存中） → 序列化 → 字节数组（可以存储/传输）
        //Redis 只能存储字节数组，不能直接存储 Java 对象
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
