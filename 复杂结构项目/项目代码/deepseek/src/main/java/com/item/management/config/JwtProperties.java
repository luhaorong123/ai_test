// JwtProperties.java - JWT配置属性类
package com.item.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtProperties {

    private String secret;
    private Long expiration = 86400000L; // 24小时，单位毫秒
    private Long refreshExpiration = 604800000L; // 7天，单位毫秒
    private String header = "Authorization";
    private String prefix = "Bearer ";
}