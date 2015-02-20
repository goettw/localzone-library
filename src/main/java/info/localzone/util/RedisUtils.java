package info.localzone.util;

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
	// private static String REDIS_AUTHOR_SENT ="AUTHOR_SENT_LIST:";
	private static String REDIS_OPENSTREETMAP_CACHEENTRY = "REDIS_OPENSTREETMAP_CACHEENTRY:";
	private static String REDIS_PLACE_HM = "PLACE-HM:";
	private static String REDIS_PLACE_FIELD_DISPLAYNAME = "display-name";
	private static String REDIS_PLACE_FIELD_LON = "lon";
	private static String REDIS_PLACE_FIELD_LAT = "lat";
	private static String REDIS_PLACE_FIELD_JSON = "json";
	private static String REDIS_PLACE_BY_ORGIN = "PLACE-BY-ORIGN:";
	private static String REDIS_HOMEZONE = "HOMEZONE:";
	private static String REDIS_HOMEZONE_LIST = "HOMEZONELIST:";
	private static String REDIS_OVERPASS_QUERY_LOCK = "OVERPASS-QUERY-LOCK";
	private static String REDIS_HOMEZONE_TYPES = "HOMEZONE_TYPES:";
	// private static String PLACES_INZONE = "PLACES-INZONE";
	// private static String PLACES_INZONE_TYPES = "PLACES-INZONE_TYPES";
	private static String PLACES_VISITOR = "001PLACE_VISITOR";
	private static String REDIS_CURRENTTIMESTAMP = "THIS-TIMESTAMP";
	private static String REDIS_NEXTTIMESTAMP = "NEXT-TIMESTAMP";
	private static String REDIS_SESSION_HASHMAP = "SESSION_HASHMAP";
	private static String REDIS_SESSION_HASHMAP_TIMESTAMP = "TIMESTAMP";
	private static String REDIS_SESSION_HASHMAP_CHANNELCODE = "CHANNELCODE";
	private static long REDIS_VISITOR_TIMEOUT_MINUTES = 10;
	private static long REDIS_SESSION_INACTIVITY_TIMEOUT_MINUTES = 20;
	private static String REDIS_TIMESTAMP_COUNTER = "TIMESTAMP-COUNTER";
	public static String PLACE_ID_COUNTER = "PLACE_ID_COUNTER"; // used for
																// generation of
																// all keys

	static private void refreshTimestamps(StringRedisTemplate redisTemplate) {
		String currentTimestamp = getCurrentVisitorTimeStamp(redisTemplate);
		String nextTimestamp = getNextVisitorTimeStamp(redisTemplate);

		if (currentTimestamp == null) {

			if (nextTimestamp == null) {
				redisTemplate.opsForValue().set(REDIS_CURRENTTIMESTAMP, createKey(redisTemplate, REDIS_TIMESTAMP_COUNTER));
			} else {
				redisTemplate.opsForValue().set(REDIS_CURRENTTIMESTAMP, nextTimestamp);
			}

			redisTemplate.expire(REDIS_CURRENTTIMESTAMP, REDIS_VISITOR_TIMEOUT_MINUTES, TimeUnit.MINUTES);
			redisTemplate.opsForValue().set(REDIS_NEXTTIMESTAMP, createKey(redisTemplate, REDIS_TIMESTAMP_COUNTER));
		}

	}

	static private String getCurrentVisitorTimeStamp(StringRedisTemplate redisTemplate) {
		return redisTemplate.opsForValue().get(REDIS_CURRENTTIMESTAMP);
	}

	static private String getNextVisitorTimeStamp(StringRedisTemplate redisTemplate) {
		return redisTemplate.opsForValue().get(REDIS_NEXTTIMESTAMP);
	}

	static void setVisitor(StringRedisTemplate redisTemplate, String sessionId, String geoHash, String channelId) {
		refreshTimestamps(redisTemplate);

		String currentTimeStamp = getCurrentVisitorTimeStamp(redisTemplate);
		String nextTimeStamp = getNextVisitorTimeStamp(redisTemplate);
		String sessionTimeStamp = getSessionInfoTimeStamp(redisTemplate, sessionId);
		String sessionChannelCode = getSessionInfoChannelCode(redisTemplate, sessionId);
		String channelCode = geoHash + channelId;

		if (sessionChannelCode != null && sessionTimeStamp != null && sessionTimeStamp.equals(currentTimeStamp) && channelCode.equals(sessionChannelCode))
			return;

		redisTemplate.opsForValue().increment(PLACES_VISITOR + channelCode + currentTimeStamp, 1);

		if (sessionChannelCode != null)
			redisTemplate.opsForValue().increment(PLACES_VISITOR + sessionChannelCode + currentTimeStamp, -1);

		redisTemplate.expire(PLACES_VISITOR + channelCode + currentTimeStamp, REDIS_VISITOR_TIMEOUT_MINUTES, TimeUnit.MINUTES);

		if (sessionTimeStamp != null && sessionTimeStamp.equals(currentTimeStamp)) {
			redisTemplate.opsForValue().increment(PLACES_VISITOR + channelCode + nextTimeStamp, 1);
			if (sessionChannelCode != null)
				redisTemplate.opsForValue().increment(PLACES_VISITOR + sessionChannelCode + nextTimeStamp, -1);
		}
		writeSessionInfo(redisTemplate, sessionId, channelCode, currentTimeStamp);

	}

	static int getNumberOfVisitors(StringRedisTemplate redisTemplate, String hash) {
		return Integer.parseInt(redisTemplate.opsForValue().get(hash));
	}

	private static String createKey(StringRedisTemplate redisTemplate, String counterId) {
		RedisAtomicInteger counter = new RedisAtomicInteger(counterId, redisTemplate.getConnectionFactory());
		int key = counter.incrementAndGet();
		return Integer.toString(key);
	}

	public static void putToOpenStreetResultCache(StringRedisTemplate redisTemplate, String osmId, String value) {
		String key = REDIS_OPENSTREETMAP_CACHEENTRY + osmId;
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, 10, TimeUnit.MINUTES);
	}

	public static String getFromOpenStreetResultCache(StringRedisTemplate redisTemplate, String id) {
		String key = REDIS_OPENSTREETMAP_CACHEENTRY + id;
		return redisTemplate.opsForValue().get(key);
	}

	public static String getPlaceId(StringRedisTemplate redisTemplate) {
		return createKey(redisTemplate, PLACE_ID_COUNTER);
	}

	public static Set<String> readPlacesInRadius(StringRedisTemplate redisTemplate, List<String> zoneIdList, String channel) {
		String key = REDIS_HOMEZONE + zoneIdList.get(0) + ":" + channel;
		ArrayList<String> otherKeys = new ArrayList<String>();

		for (String otherKeyEntry : zoneIdList.subList(1, zoneIdList.size())) {
			otherKeys.add(REDIS_HOMEZONE + otherKeyEntry + ":" + channel);
		}
		return redisTemplate.opsForSet().union(key, otherKeys);
	}
	static private String getRedisKeyFromHashCodeAndChannel (String geoHash, String channel) {
		return REDIS_HOMEZONE + geoHash + ":" + channel;
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
			keys.add(REDIS_HOMEZONE_TYPES + hashcode);
		}
		if (keys.size() == 0)
			return new HashSet<String>();
		if (keys.size() == 1)
			redisTemplate.opsForSet().members(keys.get(0));
		return redisTemplate.opsForSet().union(keys.get(0), keys.subList(1, keys.size()));
	}

	public static boolean getOverpassQueryLock(StringRedisTemplate redisTemplate, String hashCode) {
		RedisAtomicInteger counter = new RedisAtomicInteger(REDIS_OVERPASS_QUERY_LOCK + hashCode, redisTemplate.getConnectionFactory());
		counter.incrementAndGet();
		if (counter.intValue() == 1)
			return true;
		return false;
	}

	private static void writeSessionInfo(StringRedisTemplate redisTemplate, String sessionId, String channelCode, String timestamp) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(REDIS_SESSION_HASHMAP + sessionId);
		ops.put(REDIS_SESSION_HASHMAP_TIMESTAMP, timestamp);
		ops.put(REDIS_SESSION_HASHMAP_CHANNELCODE, channelCode);
		ops.expire(REDIS_SESSION_INACTIVITY_TIMEOUT_MINUTES, TimeUnit.MINUTES);
	}

	private static String getSessionInfoChannelCode(StringRedisTemplate redisTemplate, String sessionId) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(REDIS_SESSION_HASHMAP + sessionId);
		return ops.get(REDIS_SESSION_HASHMAP_CHANNELCODE);
	}

	private static String getSessionInfoTimeStamp(StringRedisTemplate redisTemplate, String sessionId) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(REDIS_SESSION_HASHMAP + sessionId);
		return ops.get(REDIS_SESSION_HASHMAP_TIMESTAMP);
	}

	/**
	 * @param redisTemplate
	 * @param place
	 * @param json
	 */
	public static void writePlace(StringRedisTemplate redisTemplate, Place place, String json, String geohash) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(REDIS_PLACE_HM + place.getId());

		ops.put(REDIS_PLACE_FIELD_DISPLAYNAME, place.getDisplay_name());
		ops.put(REDIS_PLACE_FIELD_LAT, Double.toString(place.getLat()));
		ops.put(REDIS_PLACE_FIELD_LON, Double.toString(place.getLon()));
		ops.put(REDIS_PLACE_FIELD_JSON, json);

		BoundSetOperations<String, String> setOps = redisTemplate.boundSetOps(REDIS_HOMEZONE + geohash + ":" + place.getType());
		setOps.add(place.getId());

		BoundSetOperations<String, String> setOpsTypes = redisTemplate.boundSetOps(REDIS_HOMEZONE_TYPES + geohash);
		setOpsTypes.add(place.getType());
		redisTemplate.opsForSet().add(REDIS_HOMEZONE_LIST, geohash);
	}

	public static Set<String> readHomezoneTypes(StringRedisTemplate redisTemplate, String geohash) {
		return redisTemplate.opsForSet().members(REDIS_HOMEZONE_TYPES + geohash);
	}

	static boolean isPopulated(StringRedisTemplate redisTemplate, String homezoneId) {
		return (redisTemplate.keys(REDIS_HOMEZONE + homezoneId + "*")).size() > 0;
	}


	static int channelMemberCount(StringRedisTemplate redisTemplate, String homezoneId, String channelId) {
		if (homezoneId.length() == Pref.GEOHASH_LENGTH)
			return redisTemplate.opsForSet().size(REDIS_HOMEZONE+homezoneId + ":" + channelId).intValue();
		ArrayList<String> zoneIdList = new ArrayList<String>(redisTemplate.keys(REDIS_HOMEZONE + homezoneId + "*" + ":" + channelId));
		System.out.println("pattern="+REDIS_HOMEZONE + homezoneId + "*" + ":" + channelId + "->" + zoneIdList.size());
		
		if (zoneIdList == null || zoneIdList.size() == 0)
			return 0;
		if (zoneIdList.size() == 1) {
			return redisTemplate.opsForSet().size(zoneIdList.get(0)).intValue();
		}
		System.out.println("size>1");
		String key = REDIS_HOMEZONE + zoneIdList.get(0) + ":" + channelId;
		ArrayList<String> otherKeys = new ArrayList<String>();

		for (String otherKeyEntry : zoneIdList.subList(1, zoneIdList.size())) {
			otherKeys.add(REDIS_HOMEZONE + otherKeyEntry + ":" + channelId);
		}
		return redisTemplate.opsForSet().union(key, otherKeys).size();
	}

	static String readPlace(StringRedisTemplate redisTemplate, String id) {
		return readPlaceByKey(redisTemplate, REDIS_PLACE_HM + id);
	}

	private static String readPlaceByKey(StringRedisTemplate redisTemplate, String key) {
		if (!redisTemplate.hasKey(key))
			return null;
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(key);
		return ops.get(REDIS_PLACE_FIELD_JSON);

	}

	public static void writeToPlaceOriginLookup(StringRedisTemplate redisTemplate, String originId, String placeId) {
		redisTemplate.opsForValue().set(REDIS_PLACE_BY_ORGIN + originId, placeId);
	}

	public static String lookupPlaceByOriginId(StringRedisTemplate redisTemplate, String originId) {
		return redisTemplate.opsForValue().get(REDIS_PLACE_BY_ORGIN + originId);
	}

	public static List<String> getAllPlacesAsJsonStrings(StringRedisTemplate redisTemplate) {
		Set<String> placeKeys = redisTemplate.keys(REDIS_PLACE_HM + "*");
		ArrayList<String> placeList = new ArrayList<String>();
		for (String key : placeKeys) {

			placeList.add(readPlaceByKey(redisTemplate, key));
		}
		return placeList;
	}

}