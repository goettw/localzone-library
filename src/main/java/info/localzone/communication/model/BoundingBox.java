package info.localzone.communication.model;

import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.util.VincentyGeodesy;

public class BoundingBox {
	Location topLeft,bottomRight;

	public BoundingBox (Location topLeft, Location bottomRight) {
		this.bottomRight = bottomRight;
		this.topLeft = topLeft;
	}
	
	public BoundingBox (Location center, double radiusKm) {
		WGS84Point wgs84center = new WGS84Point(center.getLatitude(), center.getLongitude()); 

		WGS84Point northWest = VincentyGeodesy.moveInDirection(VincentyGeodesy.moveInDirection(wgs84center, 0, radiusKm*1000), 270,
				radiusKm*1000);

		WGS84Point southEast = VincentyGeodesy.moveInDirection(VincentyGeodesy.moveInDirection(wgs84center, 180, radiusKm*1000),
				90, radiusKm*1000);		

		topLeft = new Location(northWest.getLatitude(),northWest.getLongitude());
		bottomRight = new Location(southEast.getLatitude(),southEast.getLongitude());
	}
	
	public double getMinLat() {
		return (topLeft.getLatitude() < bottomRight.getLatitude() ? (topLeft.getLatitude()):(bottomRight.getLatitude()));
	}	
	public double getMaxLat() {
		return (topLeft.getLatitude() > bottomRight.getLatitude() ? (topLeft.getLatitude()):(bottomRight.getLatitude()));
	}
	public double getMinLon() {
		return (topLeft.getLongitude() < bottomRight.getLongitude() ? (topLeft.getLongitude()):(bottomRight.getLongitude()));
	}	
	public double getMaxLon() {
		return (topLeft.getLongitude() > bottomRight.getLongitude() ? (topLeft.getLongitude()):(bottomRight.getLongitude()));
	}
	public double getTopLeftLat () {
		return topLeft.getLatitude();
	}
	public double getTopLeftLon () {
		return topLeft.getLongitude();
	}
	
	public double getBottomRightLat() {
		return bottomRight.getLatitude();
	}
	
	public double getBottomRightLon() {
		return bottomRight.getLongitude();
	}	
	public String toString () {
		return (getTopLeftLat()+","+getTopLeftLon() + ","+getBottomRightLat() + "," + getBottomRightLon() + ",");
	}
}
