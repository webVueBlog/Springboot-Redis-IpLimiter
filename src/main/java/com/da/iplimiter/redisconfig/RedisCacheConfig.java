package com.da.iplimiter.redisconfig;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description: config
 * Configuration 作用是对容器进行配置
 */
@Configuration
public class RedisCacheConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);//设置Redis连接工厂

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

        // ObjectMapper 作用是
        // 1. 对象的所有字段（无论是否可见）都能被序列化
        // 2. 所有字段都能被反序列化
        // 3. 所有字段都能被序列化和反序列化
        // 示例ObjectMapper值为
        // public class User {
        //     private String name;
        //     private int age;
        // }
        // 序列化结果为{"name":"张三","age":18}
        // 反序列化结果为User(name=张三, age=18)
        // 注意：如果使用Jackson2JsonRedisSerializer，那么存储到Redis中的key和value的序列化方式均为json
        ObjectMapper mapper = new ObjectMapper();
        // PropertyAccessor
        // 1. NONE 默认值，不使用任何注解
        // 2. CREATOR 只使用@JsonCreator注解
        // 3. FIELD 只使用@JsonProperty注解
        // 4. GETTER 只使用@JsonGetter注解
        // 5. SETTER 只使用@JsonSetter注解
        // 6. ALL 所有注解都能使用
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);//设置序列化可见性
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);//允许序列化空值
        serializer.setObjectMapper(mapper);

        template.setValueSerializer(serializer);//设置redisTemplate的value序列化策略
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        // afterPropertiesSet作用是
        // 初始化RedisTemplate
        // 设置数据存入redis的序列化方式为json格式
        template.afterPropertiesSet();
        LOGGER.info("Springboot RedisTemplate 加载完成");
        return template;
    }
}