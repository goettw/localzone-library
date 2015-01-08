package info.localzone.communication.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

public class Header {
	public Header() {
		super();
		
	}
	Actor from;
	Date created;
	TimeZone timezone;
	long expiration=0;
	Location location;
	BigDecimal radius;
	public Header(Actor from, Date created, BigDecimal radius) {
		super();
		this.from = from;
		this.created = created;
		this.radius = radius;
	}
	public Actor getFrom() {
		return from;
	}
	public void setFrom(Actor from) {
		this.from = from;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public BigDecimal getRadius() {
		return radius;
	}
	public void setRadius(BigDecimal radius) {
		this.radius = radius;
	}
	public Location getLocation() {
		return location;
	}
	public TimeZone getTimezone() {
		return timezone;
	}
	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}
	public long getExpiration() {
		return expiration;
	}
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
}
