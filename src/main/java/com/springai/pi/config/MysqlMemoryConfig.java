package com.springai.pi.config;

import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Mysql存储聊天信息，实现记忆持久化
 * @author stef
 * @date 9/13/25 5:12 PM
 */
//@Configuration
public class MysqlMemoryConfig {

    @Value("${spring.datasource.url}")
    private String mysqlJdbcUrl;
    @Value("${spring.datasource.username}")
    private String mysqlUsername;
    @Value("${spring.datasource..password}")
    private String mysqlPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String mysqlDriverClassName;

    @Bean
    public MysqlChatMemoryRepository mysqlChatMemoryRepository() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(mysqlDriverClassName);
        dataSource.setUrl(mysqlJdbcUrl);
        dataSource.setUsername(mysqlUsername);
        dataSource.setPassword(mysqlPassword);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return MysqlChatMemoryRepository.mysqlBuilder()
                .jdbcTemplate(jdbcTemplate)
                .build();
    }
}
