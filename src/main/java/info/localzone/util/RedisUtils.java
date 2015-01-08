package info.localzone.util;

import info.localzone.pref.Pref;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

public class RedisUtils {
	public static String createKey (StringRedisTemplate redisTemplate) {
		RedisAtomicInteger counter = new RedisAtomicInteger(Pref.REDIS_KEY_COUNTER, redisTemplate.getConnectionFactory());
		int key = counter.incrementAndGet();
		return Integer.toString(key);

	}
	
	public static void putToCache (StringRedisTemplate redisTemplate, String key, String value){	
		 redisTemplate.opsForValue().set( key,value);
		 redisTemplate.expire(key, 1, TimeUnit.MINUTES);
	}
	
	public static String getFromCache (RedisTemplate<String, String> redisTemplate, String key){	
		 return redisTemplate.opsForValue().get( key);
	}	
}