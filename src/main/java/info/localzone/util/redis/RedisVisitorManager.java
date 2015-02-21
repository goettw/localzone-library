package info.localzone.util.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author wgoette example, how this manager works - there is alway a current
 *         (currentTimeStamo) and a next timestamp (nextTimeStamp) -
 *         currentTimeStamp expires after REDIS_VISITOR_TIMEOUT_SECONDS seconds
 *         - if it expires, it is set to the value of nextTimeStamp,
 *         nextTimeStamp is increased (using the REDIS_TIMESTAMP_COUNTER) -
 *         there are seperate counters for every channel and geohash. The key is
 *         - VISITORS if a visitor notifies the manager, the goal is to count
 *         him
 */
public class RedisVisitorManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisVisitorManager.class);

	StringRedisTemplate redisTemplate;

	public RedisVisitorManager(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	private String counter(String geoHash, String channelId, String timeStamp) {
		return geoHash + ":" + channelId + ":" + timeStamp;
	}

	public int getNumberOfVisitors(String geoHash, String channelId) {
		LOGGER.debug("getNumberOfVisitors(" + geoHash + ", " + channelId + ")");
		refreshTimestamps();
		String currentTimeStamp = getCurrentTimeStamp();
		LOGGER.debug("currentTimeStamp=" + currentTimeStamp);
		LOGGER.debug("currentChannelCode=" + channelId);

		String val = redisTemplate.opsForValue().get(counter(geoHash,channelId,currentTimeStamp));
		if (val == null)
			return 0;
		return Integer.parseInt(val);
	}

	/**
	 * Sets currentTimeStamp and nextTimeStamp If currentTimestamp is not null,
	 * nothing is done If nextTimeStamp is null, both are set to 2 consecutive
	 * values. If nextTimeStamp is not null, this value is copied to
	 * currentTimeStamp, nextTimeStamp gets a new value
	 */
	private void refreshTimestamps() {
		String currentTimestamp = getCurrentTimeStamp();
		String nextTimestamp = getNextTimeStamp();

		if (currentTimestamp == null) { // current timestamp has expired ?

			if (nextTimestamp == null) { // calculate new current timestamp
				redisTemplate.opsForValue().set(RedisKeyConstants.REDIS_CURRENTTIMESTAMP,
						RedisUtils.createKey(redisTemplate, RedisKeyConstants.REDIS_TIMESTAMP_COUNTER));
			} else {
				// set currentTimeStamp to next timestamp
				redisTemplate.opsForValue().set(RedisKeyConstants.REDIS_CURRENTTIMESTAMP, nextTimestamp);
			}

			// let current timestamp expire
			redisTemplate.expire(RedisKeyConstants.REDIS_CURRENTTIMESTAMP, RedisKeyConstants.REDIS_VISITOR_TIMEOUT_SECONDS, TimeUnit.SECONDS);

			// set next timestamp
			redisTemplate.opsForValue().set(RedisKeyConstants.REDIS_NEXTTIMESTAMP,
					RedisUtils.createKey(redisTemplate, RedisKeyConstants.REDIS_TIMESTAMP_COUNTER));
		}

	}

	private String getCurrentTimeStamp() {
		return redisTemplate.opsForValue().get(RedisKeyConstants.REDIS_CURRENTTIMESTAMP);
	}

	private String getNextTimeStamp() {
		return redisTemplate.opsForValue().get(RedisKeyConstants.REDIS_NEXTTIMESTAMP);
	}

	/**
	 * Notifies the manager about a visit.
	 * 
	 * @param sessionId
	 *            - identifies the visitor
	 * @param geoHash
	 *            - the geoHash (location) of the visitor
	 * @param channelId
	 *            - the channelId that the visitor is tunedIn
	 */
	public void notify(String sessionId, String geoHash, String channelId) {
		LOGGER.debug("notify(" + sessionId + ", " + geoHash + ", " + channelId + ")");
		refreshTimestamps();

		String currentTimeStamp = getCurrentTimeStamp();
		String nextTimeStamp = getNextTimeStamp();
		// get lastValues for session

		LOGGER.debug("currentTimeStamp=" + currentTimeStamp);
		String currentChannelCode = geoHash + channelId;
		LOGGER.debug("currentChannelCode=" + currentChannelCode);

		
		String currentCounter = counter (geoHash, channelId, currentTimeStamp);
		String nextCounter = counter (geoHash, channelId, nextTimeStamp);
		
		countCurrent(sessionId, currentCounter);
		countNext(sessionId, nextCounter);
		redisTemplate.expire(currentCounter, RedisKeyConstants.REDIS_VISITOR_TIMEOUT_SECONDS,TimeUnit.SECONDS);
	
	}

	private void countCurrent(String sessionId, String counter) {
		count (RedisKeyConstants.REDIS_SESSION_HASHMAP_COUNTER1, sessionId, counter);
	}
	private void countNext(String sessionId, String counter) {
		count (RedisKeyConstants.REDIS_SESSION_HASHMAP_COUNTER2, sessionId, counter);
	}
	
	private void count (String counterKey, String sessionId, String counter) {
		BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(RedisKeyConstants.REDIS_SESSION_HASHMAP + sessionId);
		String oldCounter = ops.get(counterKey);
		if (oldCounter != null && oldCounter.equals(counter))
			return;

		redisTemplate.opsForValue().increment(counter, 1);
		if (oldCounter != null)
			redisTemplate.opsForValue().increment(oldCounter, -1);

		ops.put(counterKey, counter);
		ops.expire(RedisKeyConstants.REDIS_SESSION_INACTIVITY_TIMEOUT_MINUTES, TimeUnit.MINUTES);		
	}



}
