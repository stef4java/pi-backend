package com.springai.pi.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.JedisPooled;

import java.util.ArrayList;
import java.util.List;

/**
 * docker run -d --name redis-stack \
 * -p 9379:6379 \
 * redis/redis-stack:latest
 */

@Configuration
@EnableConfigurationProperties({RedisVectorStoreProperties.class})
@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
@Slf4j
public class RedisVectorStoreConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.ai.vectorstore.redis.prefix}")
    private String prefix;
    @Value("${spring.ai.vectorstore.redis.index}")
    private String indexName;


    @Bean
    public JedisPooled jedisPooled() {
        log.info("Redis host: {}, port: {}", host, port);

        // 使用 URI 方式创建 JedisPooled 实例
        String redisUri = String.format("redis://%s:%d", host, port);
        if (password != null && !password.isEmpty()) {
            //在Redis 6.0+版本中，默认用户名是 default，所以URI格式为 redis://default:password@host:port
            redisUri = String.format("redis://default:%s@%s:%d", password, host, port);
        }

        JedisPooled jedisPooled = new JedisPooled(redisUri);

        // 测试连接
        try {
            jedisPooled.ping();
            log.info("Successfully connected to Redis at {}:{}", host, port);
        } catch (Exception e) {
            log.error("Failed to connect to Redis at {}:{}, error: {}", host, port, e.getMessage());
            throw new RuntimeException("Unable to establish Redis connection", e);
        }

        return jedisPooled;
    }

    @Bean
    public RedisVectorStore redisVectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        log.info("create redis vector store");
        // 默认批次大小为10，符合DashScope API限制
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(indexName)
                .prefix(prefix)
                .initializeSchema(true)
                .batchingStrategy(documentList -> {
                    // 默认批次大小为10，符合DashScope API限制
                    int batchSize = 10;
                    List<List<Document>> batches = new ArrayList<>();
                    for (int i = 0; i < documentList.size(); i += batchSize) {
                        int end = Math.min(i + batchSize, documentList.size());
                        batches.add(documentList.subList(i, end));
                    }
                    return batches;
                })
                .build();
    }

}