package info.localzone.util;

import info.localzone.communication.model.Place;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

public class RedisUtils {
	//private static String REDIS_AUTHOR_SENT ="AUTHOR_SENT_LIST:";
	private static String REDIS_OPENSTREETMAP_CACHEENTRY="REDIS_OPENSTREETMAP_CACHEENTRY:";
	private static String REDIS_PLACE_HM ="PLACE-HM:";
	private static String REDIS_PLACE_FIELD_DISPLAYNAME ="display-name";
	private static String REDIS_PLACE_FIELD_LON ="lon";
	private static String REDIS_PLACE_FIELD_LAT ="lat";
	private static String REDIS_PLACE_FIELD_JSON ="json";
	private static String REDIS_PLACE_BY_ORGIN="PLACE-BY-ORIGN:";
	private static String REDIS_HOMEZONE="HOMEZONE30:";
	private static String PLACES_INZONE="PLACES-INZONE";
	
	
	public static String PLACE_ID_COUNTER="PLACE_ID_COUNTER"; // used for generation of all keys

	private static String createKey (StringRedisTemplate redisTemplate, String counterId) {
		RedisAtomicInteger counter = new RedisAtomicInteger(counterId, redisTemplate.getConnectionFactory());
		int key = counter.incrementAndGet();
		return Integer.toString(key);

	}
	
	public static void putToOpenStreetResultCache (StringRedisTemplate redisTemplate, String id, String value){	
		String key = REDIS_OPENSTREETMAP_CACHEENTRY + id;
		redisTemplate.opsForValue().set( key,value);
		redisTemplate.expire(key, 1, TimeUnit.MINUTES);
	}
	
	public static String getFromOpenStreetResultCache (StringRedisTemplate redisTemplate, String id){
		String key = REDIS_OPENSTREETMAP_CACHEENTRY + id;
		 return redisTemplate.opsForValue().get( key);
	}	

	public static String getPlaceId (StringRedisTemplate redisTemplate) {
		return createKey (redisTemplate, PLACE_ID_COUNTER) ;
	}

	/**
	 * @param redisTemplate
	 * @param place
	 * @param json
	 */
	public static void writePlace (StringRedisTemplate redisTemplate, Place place, String json, String geohash) {
		BoundHashOperations<String , String, String> ops = redisTemplate.boundHashOps(REDIS_PLACE_HM+place.getId());
		
		ops.put(REDIS_PLACE_FIELD_DISPLAYNAME, place.getDisplay_name());
		ops.put(REDIS_PLACE_FIELD_LAT, Double.toString(place.getLat()));
		ops.put(REDIS_PLACE_FIELD_LON, Double.toString(place.getLon()));
		ops.put(REDIS_PLACE_FIELD_JSON, json);
	
		BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(REDIS_HOMEZONE+geohash);
		setOps.add(place.getId());
		
		
	}
	
	private static String getInzoneKey (double radius, int precision, String hashcode) {
		String radiusString = Double.toString(radius);
		String precisionString = Integer.toString(precision);
		return PLACES_INZONE+"-"+radiusString+"-"+precisionString+"-"+hashcode;
	}
	
	
	public static void writeToInzoneCache (StringRedisTemplate redisTemplate, String placeId, List<String> hashList, double radius, int precision) {
		for (String hashcode : hashList) {
			redisTemplate.opsForSet().add(getInzoneKey(radius,precision,hashcode), placeId);
		}
	}
	
	public static Set<String> readInzonePlacesCache (StringRedisTemplate redisTemplate,String hashcode,double radius, int precision) {
		return  redisTemplate.opsForSet().members(getInzoneKey(radius, precision, hashcode));
	}
	
	public static String readPlace (StringRedisTemplate redisTemplate, String id) {
		if (!redisTemplate.hasKey(REDIS_PLACE_HM+id))
			return null;
		BoundHashOperations<String , String, String> ops = redisTemplate.boundHashOps(REDIS_PLACE_HM+id);
		return ops.get(REDIS_PLACE_FIELD_JSON);
	}
	
	public static void writeToPlaceOriginLookup (StringRedisTemplate redisTemplate, String originId, String placeId) {
		 redisTemplate.opsForValue().set(REDIS_PLACE_BY_ORGIN+originId,placeId);
	}
	
	public static String lookupPlaceByOriginId (StringRedisTemplate redisTemplate, String originId) {
		return redisTemplate.opsForValue().get(REDIS_PLACE_BY_ORGIN+originId);
	}
	
}