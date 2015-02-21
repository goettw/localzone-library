package info.localzone.util.redis;

public class RedisKeyConstants {
	// private static String REDIS_AUTHOR_SENT ="AUTHOR_SENT_LIST:";
	static String REDIS_OPENSTREETMAP_CACHEENTRY = "REDIS_OPENSTREETMAP_CACHEENTRY:";
	static String REDIS_PLACE_HM = "PLACE-HM:";
	static String REDIS_PLACE_FIELD_DISPLAYNAME = "display-name";
	static String REDIS_PLACE_FIELD_LON = "lon";
	static String REDIS_PLACE_FIELD_LAT = "lat";
	static String REDIS_PLACE_FIELD_JSON = "json";
	static String REDIS_PLACE_BY_ORGIN = "PLACE-BY-ORIGN:";
	static String REDIS_HOMEZONE = "HOMEZONE:";
	static String REDIS_HOMEZONE_LIST = "HOMEZONELIST:";
	static String REDIS_OVERPASS_QUERY_LOCK = "OVERPASS-QUERY-LOCK";
	static String REDIS_HOMEZONE_TYPES = "HOMEZONE_TYPES:";
	// private static String PLACES_INZONE = "PLACES-INZONE";
	// private static String PLACES_INZONE_TYPES = "PLACES-INZONE_TYPES";
	static String PLACES_VISITOR = "001PLACE_VISITOR";
	static String REDIS_CURRENTTIMESTAMP = "THIS-TIMESTAMP";
	static String REDIS_NEXTTIMESTAMP = "NEXT-TIMESTAMP";
	static String REDIS_SESSION_HASHMAP = "SESSION_HASHMAP";
	static String REDIS_SESSION_HASHMAP_COUNTER1 = "COUNTER1";
	static String REDIS_SESSION_HASHMAP_COUNTER2 = "COUNTER2";
	static long REDIS_VISITOR_TIMEOUT_SECONDS = 20;
	static long REDIS_SESSION_INACTIVITY_TIMEOUT_MINUTES = 20;
	static String REDIS_TIMESTAMP_COUNTER = "TIMESTAMP-COUNTER";
	public static String PLACE_ID_COUNTER = "PLACE_ID_COUNTER"; // used for
																// generation of
																// all keys


}
