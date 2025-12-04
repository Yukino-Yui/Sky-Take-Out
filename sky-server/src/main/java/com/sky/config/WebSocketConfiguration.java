package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 * 一个标准的 @Configuration类,作用就是把 ServerEndpointExporter 这个必需的WebSocket扫描器注册进 Spring 容器，让 @ServerEndpoint 生效。
 * Spring Boot 启动 →看见容器里有 ServerEndpointExporter →动执行它的逻辑 →扫描所有：@ServerEndpoint("/ws")
 */
@Configuration
public class WebSocketConfiguration {

    /**
     * ServerEndpointExporter 的作用非常单一：扫描并注册所有 @ServerEndpoint 注解的 WebSocket 端点。
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
