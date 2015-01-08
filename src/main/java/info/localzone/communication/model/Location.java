package info.localzone.communication.model;


public class Location {
	
	public Location(double latitude, double longitude, Integer accuracy) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
	}
	public Location() {
		super();
		
	}
	double latitude;
	double longitude;
	Integer accuracy;
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Integer getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}
}
