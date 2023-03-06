package com.mulan.fengwo_backend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置（官网的配置useClusterServers()是分布式集群redis）
        String address = String.format("redis://%s:%s", host, port);
        Config config = new Config();
        config.useSingleServer().setAddress(address).setPassword(password).setDatabase(3);
        // 2. 创建对象
        return Redisson.create(config);
    }
}
