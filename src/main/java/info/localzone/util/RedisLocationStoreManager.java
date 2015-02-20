package info.localzone.util;

import info.localzone.communication.model.Place;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisLocationStoreManager {
	StringRedisTemplate redisTemplate;

	public RedisLocationStoreManager(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Set<String> readPlacesByZonesAndChannel(List<String> zoneIdList, String channel) {
		return RedisUtils.readPlacesInRadius(redisTemplate, zoneIdList, channel);
	}

	public void setVisitor(String sessionId, String geoHash, String channelId) {
		RedisUtils.setVisitor(redisTemplate, sessionId, geoHash, channelId);
	}

	public int getNumberOfVisitors(String zoneId) {
		return RedisUtils.getNumberOfVisitors(redisTemplate, zoneId);
	}

	public Set<String> getAllPlaceTypes(List<String> hashCodeList) {
		return RedisUtils.getAllPlaceTypes(redisTemplate, hashCodeList);
	}

	public boolean isPopulated (String homezoneId) {
		return RedisUtils.isPopulated(redisTemplate, homezoneId);
	}
	
	public int channelMemberCount (String homezoneId, String channelId) {
		return RedisUtils.channelMemberCount(redisTemplate, homezoneId, channelId);
	}
	public Set<String> readPlacesByTypeList (List<String> geoHashList, List<String> channelIdList) {
		return RedisUtils.readPlacesByTypeList(redisTemplate, geoHashList, channelIdList);
	}
	
	public String readPlace(String id) {
		return RedisUtils.readPlace(redisTemplate, id);
	}
	
	public  List<String> getAllPlacesAsJsonStrings() {
		return RedisUtils.getAllPlacesAsJsonStrings(redisTemplate);
	}

	public  boolean getOverpassQueryLock(String hashCode) {
		return RedisUtils.getOverpassQueryLock(redisTemplate, hashCode);
	}
	
	public void putToOpenStreetResultCache(String osmId, String value) {
			RedisUtils.putToOpenStreetResultCache(redisTemplate, osmId, value);
	}
	
	public  String getPlaceId() {
		return RedisUtils.getPlaceId(redisTemplate);
	}

	public  void writeToPlaceOriginLookup(String originId, String placeId) {
		RedisUtils.writeToPlaceOriginLookup(redisTemplate, originId, placeId);
	}
	
	public void writePlace(Place place, String json, String geohash) {
		RedisUtils.writePlace(redisTemplate, place, json, geohash);
	}
	
	public String lookupPlaceByOriginId(String originId) {
		return RedisUtils.lookupPlaceByOriginId(redisTemplate, originId);
	}
	public String getFromOpenStreetResultCache (String osmId) {
		return RedisUtils.getFromOpenStreetResultCache(redisTemplate, osmId);
	}
}
