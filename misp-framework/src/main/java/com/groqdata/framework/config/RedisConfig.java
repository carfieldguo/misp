package com.groqdata.framework.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 * 
 * @author MISP TEAM
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		FastJson2JsonRedisSerializer<Object> serializer = new FastJson2JsonRedisSerializer<>(Object.class);

		// 使用StringRedisSerializer来序列化和反序列化redis的key值
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);

		// Hash的key也采用StringRedisSerializer的序列化方式
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}


	@Bean
	public DefaultRedisScript<Long> limitScript() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptText(limitScriptText());
		redisScript.setResultType(Long.class);
		return redisScript;
	}

	/**
	 * 限流脚本
	 */
	private String limitScriptText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("local key = KEYS[1]\n");
        stringBuilder.append("local count = tonumber(ARGV[1])\n");
        stringBuilder.append("local time = tonumber(ARGV[2])\n");
        stringBuilder.append("local current = redis.call('get', key);\n");
        stringBuilder.append("if current and tonumber(current) > count then\n");
        stringBuilder.append("    return tonumber(current);\n");
        stringBuilder.append("end\n");
        stringBuilder.append("current = redis.call('incr', key)\n");
        stringBuilder.append("if tonumber(current) == 1 then\n");
        stringBuilder.append("    redis.call('expire', key, time)\n");
        stringBuilder.append("end\n");
        stringBuilder.append("return tonumber(current);");
        return stringBuilder.toString();
	}
}
