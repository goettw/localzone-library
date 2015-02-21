package info.localzone.util.redis;

import info.localzone.communication.model.Place;
import info.localzone.pref.Pref;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

class RedisUtils {


	static String createKey(StringRedisTemplate redisTemplate, String counterId) {
		RedisAtomicInteger counter = new RedisAtomicInteger(counterId, redisTemplate.getConnectionFactory());
		int key = counter.incrementAndGet();
		return Integer.toString(key);
	}

	public static void putToOpenStreetResultCache(StringRedisTemplate redisTemplate, String osmId, String value) {
		String key = RedisKeyConstants.REDIS_OPENSTREETMAP_CACHEENTRY + osmId;
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, 10, TimeUnit.MINUTES);
	}

	public static String getFromOpenStreetResultCache(StringRedisTemplate redisTemplate, String id) {
		String key = RedisKeyConstants.REDIS_OPENSTREETMAP_CACHEENTRY + id;
		return redisTemplate.opsForValue().get(key);
	}

	public static String getPlaceId(StringRedisTemplate redisTemplate) {
		return createKey(redisTemplate, RedisKeyConstants.PLACE_ID_COUNTER);
	}

	public static Set<String> readPlacesInRadius(StringRedisTemplate redisTemplate, List<String> zoneIdList, String channel) {
		String key = RedisKeyConstants.REDIS_HOMEZONE + zoneIdList.get(0) + ":" + channel;
		ArrayList<String> otherKeys = new ArrayList<String>();

		for (String otherKeyEntry : zoneIdList.subList(1, zoneIdList.size())) {
			otherKeys.add(RedisKeyConstants.REDIS_HOMEZONE + otherKeyEntry + ":" + channel);
		}
		return redisTemplate.opsForSet().union(key, otherKeys);
	}
	static private String getRedisKeyFromHashCodeAndChannel (String geoHash, String channel) {
		return RedisKeyConstants.REDIS_HOMEZONE + geoHash + ":" + channel;
	}
	static Set<String> readPlacesByTypeList (StringRedisTemplate redisTemplate, List<String> geoHashList, List<String> typeList) {
		ArrayList<String> otherKeys = new ArrayList<String>();
		String key = null;
		for (String geoHash : geoHashList) {
			for (String type : typeList) {
				
				if (key == null) {
					key = getRedisKeyFromHashCodeAndChannel(geoHash,type);
				}
				else {
					otherKeys.add(getRedisKeyFromHashCodeAndChannel(geoHash,type));
				}
			}
		}
		return redisTemplate.opsForSet().union(key, otherKeys);

	}

	static Set<String> getAllPlaceTypes(StringRedisTemplate redisTemplate, List<String> hashCodeList) {
		ArrayList<String> keys = new ArrayList<String>();

		for (String hashcode : hashCodeList) {
			keys.add(RedisKeyConstants.REDIS_HOMEZONE_TYPES + hashcode);
		}
		if (keys.size() == 0)
			return new HashSet<String>();
		if (keys.size() == 1)
			redisTemplate.opsForSet().members(keys.get(0));
		return redisTemplate.opsForSet().union(keys.get(0), keys.subList(1, keys.size()));
	}

	public static boolean getOverpassQueryLock(StringRedisTemplate redisTemplate, String hashCode) {
		RedisAtomicInteger counter = new RedisAtomicInteger(RedisKeyConstants.REDIS_OVERPASS_QUERY_LOCK + hashCode, redisTemplate.getConnectionFactory());
		counter.incrementAndGet();
		if (counter.intValue() == 1)
			return true;
		return false;
	}



	/**
	 * @param redisTemplate
	 * @param place
	 * @param json
	 */
	public static void writePlace(StringRedisTemplate redisTemplate, Place place, String json, String geohash) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(RedisKeyConstants.REDIS_PLACE_HM + place.getId());

		ops.put(RedisKeyConstants.REDIS_PLACE_FIELD_DISPLAYNAME, place.getDisplay_name());
		ops.put(RedisKeyConstants.REDIS_PLACE_FIELD_LAT, Double.toString(place.getLat()));
		ops.put(RedisKeyConstants.REDIS_PLACE_FIELD_LON, Double.toString(place.getLon()));
		ops.put(RedisKeyConstants.REDIS_PLACE_FIELD_JSON, json);

		BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(RedisKeyConstants.REDIS_HOMEZONE + geohash + ":" + place.getType());
		setOps.add(place.getId());

		BoundSetOperations<String, String> setOpsTypes = redisTemplate.boundSetOps(RedisKeyConstants.REDIS_HOMEZONE_TYPES + geohash);
		setOpsTypes.add(place.getType());
		redisTemplate.opsForSet().add(RedisKeyConstants.REDIS_HOMEZONE_LIST, geohash);
	}

	public static Set<String> readHomezoneTypes(StringRedisTemplate redisTemplate, String geohash) {
		return redisTemplate.opsForSet().members(RedisKeyConstants.REDIS_HOMEZONE_TYPES + geohash);
	}

	static boolean isPopulated(StringRedisTemplate redisTemplate, String homezoneId) {
		return (redisTemplate.keys(RedisKeyConstants.REDIS_HOMEZONE + homezoneId + "*")).size() > 0;
	}


	static int channelMemberCount(StringRedisTemplate redisTemplate, String homezoneId, String channelId) {
		if (homezoneId.length() == Pref.GEOHASH_LENGTH)
			return redisTemplate.opsForSet().size(RedisKeyConstants.REDIS_HOMEZONE+homezoneId + ":" + channelId).intValue();
		ArrayList<String> zoneIdList = new ArrayList<String>(redisTemplate.keys(RedisKeyConstants.REDIS_HOMEZONE + homezoneId + "*" + ":" + channelId));
		System.out.println("pattern="+RedisKeyConstants.REDIS_HOMEZONE + homezoneId + "*" + ":" + channelId + "->" + zoneIdList.size());
		
		if (zoneIdList == null || zoneIdList.size() == 0)
			return 0;
		if (zoneIdList.size() == 1) {
			return redisTemplate.opsForSet().size(zoneIdList.get(0)).intValue();
		}
		System.out.println("size>1");
		String key = RedisKeyConstants.REDIS_HOMEZONE + zoneIdList.get(0) + ":" + channelId;
		ArrayList<String> otherKeys = new ArrayList<String>();

		for (String otherKeyEntry : zoneIdList.subList(1, zoneIdList.size())) {
			otherKeys.add(RedisKeyConstants.REDIS_HOMEZONE + otherKeyEntry + ":" + channelId);
		}
		return redisTemplate.opsForSet().union(key, otherKeys).size();
	}

	static String readPlace(StringRedisTemplate redisTemplate, String id) {
		return readPlaceByKey(redisTemplate, RedisKeyConstants.REDIS_PLACE_HM + id);
	}

	private static String readPlaceByKey(StringRedisTemplate redisTemplate, String key) {
		if (!redisTemplate.hasKey(key))
			return null;
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(key);
		return ops.get(RedisKeyConstants.REDIS_PLACE_FIELD_JSON);

	}

	public static void writeToPlaceOriginLookup(StringRedisTemplate redisTemplate, String originId, String placeId) {
		redisTemplate.opsForValue().set(RedisKeyConstants.REDIS_PLACE_BY_ORGIN + originId, placeId);
	}

	public static String lookupPlaceByOriginId(StringRedisTemplate redisTemplate, String originId) {
		return redisTemplate.opsForValue().get(RedisKeyConstants.REDIS_PLACE_BY_ORGIN + originId);
	}

	public static List<String> getAllPlacesAsJsonStrings(StringRedisTemplate redisTemplate) {
		Set<String> placeKeys = redisTemplate.keys(RedisKeyConstants.REDIS_PLACE_HM + "*");
		ArrayList<String> placeList = new ArrayList<String>();
		for (String key : placeKeys) {

			placeList.add(readPlaceByKey(redisTemplate, key));
		}
		return placeList;
	}

}