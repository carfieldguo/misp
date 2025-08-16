package com.groqdata.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.groqdata.common.constant.CacheConstants;
import com.groqdata.common.core.domain.AjaxResult;
import org.apache.commons.lang3.StringUtils;
import com.groqdata.system.domain.SysCache;

/**
 * 缓存监控
 * 
 * @author MISP TEAM
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController {

	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private static final List<SysCache> caches = new ArrayList<>();
	static {
		caches.add(new SysCache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息"));
		caches.add(new SysCache(CacheConstants.SYS_CONFIG_KEY, "配置信息"));
		caches.add(new SysCache(CacheConstants.SYS_DICT_KEY, "数据字典"));
		caches.add(new SysCache(CacheConstants.CAPTCHA_CODE_KEY, "验证码"));
		caches.add(new SysCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交"));
		caches.add(new SysCache(CacheConstants.RATE_LIMIT_KEY, "限流处理"));
		caches.add(new SysCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数"));
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@GetMapping()
	public AjaxResult getInfo() {
		Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
		Properties commandStats = (Properties) redisTemplate
			.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
		Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

		Map<String, Object> result = HashMap.newHashMap(3);
		result.put("info", info);
		result.put("dbSize", dbSize);

		List<Map<String, String>> pieList = new ArrayList<>();
		if (commandStats == null || commandStats.isEmpty()) {
			return AjaxResult.success(result);
		}
		commandStats.stringPropertyNames().forEach(key -> {
			Map<String, String> data = HashMap.newHashMap(2);
			String property = commandStats.getProperty(key);
			data.put("name", StringUtils.removeStart(key, "cmdstat_"));
			data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
			pieList.add(data);
		});
		result.put("commandStats", pieList);
		return AjaxResult.success(result);
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@GetMapping("/getNames")
	public AjaxResult cache() {
		return AjaxResult.success(caches);
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@GetMapping("/getKeys/{cacheName}")
	public AjaxResult getCacheKeys(@PathVariable String cacheName) {
		Set<String> cacheKeys = redisTemplate.keys(cacheName + "*");
		return AjaxResult.success(new TreeSet<>(cacheKeys));
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@GetMapping("/getValue/{cacheName}/{cacheKey}")
	public AjaxResult getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey) {
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		SysCache sysCache = new SysCache(cacheName, cacheKey, cacheValue);
		return AjaxResult.success(sysCache);
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@DeleteMapping("/clearCacheName/{cacheName}")
	public AjaxResult clearCacheName(@PathVariable String cacheName) {
		Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
		if (cacheKeys != null  && !cacheKeys.isEmpty()) {
			redisTemplate.delete(cacheKeys);
		}
		return AjaxResult.success();
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@DeleteMapping("/clearCacheKey/{cacheKey}")
	public AjaxResult clearCacheKey(@PathVariable String cacheKey) {
		if (StringUtils.isNotBlank(cacheKey)) {
			redisTemplate.delete(cacheKey);
		}
		return AjaxResult.success();
	}

	@PreAuthorize("@ss.hasPermi('monitor:cache:list')")
	@DeleteMapping("/clearCacheAll")
	public AjaxResult clearCacheAll() {
		Collection<String> cacheKeys = redisTemplate.keys("*");
		if (cacheKeys != null && !cacheKeys.isEmpty()) {
			redisTemplate.delete(cacheKeys);
		}
		return AjaxResult.success();
	}
}
