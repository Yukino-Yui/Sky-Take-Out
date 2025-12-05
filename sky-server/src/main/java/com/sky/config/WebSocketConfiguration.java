package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类，一个标准的 @Configuration类,作用就是把 ServerEndpointExporter 这个必需的 WebSocket 扫描器注册进 Spring 容器
 * 让 @ServerEndpoint 生效。Spring Boot 启动 →看见容器里有 ServerEndpointExporter →自动执行它的逻辑
 * →扫描所有带@ServerEndpoint("/ws")的类，把它们注册为 WebSocket 端点（交给 Tomcat）
 */
@Configuration
public class WebSocketConfiguration {

    /**
     * ServerEndpointExporter 的作用非常单一：扫描并注册所有带@ServerEndpoint 注解的 WebSocket 端点。
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
