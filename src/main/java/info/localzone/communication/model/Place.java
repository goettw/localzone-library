package info.localzone.communication.model;

import java.util.ArrayList;
import java.util.List;


public class Place {
	public String getId() {
		return id;
	}
	public Place() {
		address = new Address();
		phoneNumberList = new ArrayList<PhoneNumber>();
	}
	public void setId(String id) {
		this.id = id;
	}
	double lat,lon;
	String display_name;
	String type;
	String webSite;
	List<PhoneNumber> phoneNumberList;
	String primaryEmailAddress;
	String username;
	String passwordHash;
	Address address;
	String id;
	String originId;
	
	
	
	public String getOriginId() {
		return originId;
	}
	public void setOriginId(String originId) {
		this.originId = originId;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
}
