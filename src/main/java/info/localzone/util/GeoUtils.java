package info.localzone.util;

import info.localzone.communication.model.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.queries.GeoHashCircleQuery;
import ch.hsr.geohash.util.BoundingBoxGeoHashIterator;
import ch.hsr.geohash.util.TwoGeoHashBoundingBox;

public class GeoUtils {

	
	static public double latitudeSize (BoundingBox box) {
		return distance (box.getMinLat(),box.getMinLon(),box.getMaxLat(),box.getMinLon(),"K");
		
	}

	static public double longitudeSize (BoundingBox box) {
		return distance (box.getMinLat(),box.getMinLon(),box.getMinLat(),box.getMaxLon(),"K");
		
	}
	
	
	static public double distance (Location loc1, Location loc2) {
		return distance(loc1.getLatitude(),loc1.getLongitude(),loc2.getLatitude(),loc2.getLongitude(),"K");
	}
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::                                                                         :*/
	/*::  This routine calculates the distance between two points (given the     :*/
	/*::  latitude/longitude of those points). It is being used to calculate     :*/
	/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
	/*::                                                                         :*/
	/*::  Definitions:                                                           :*/
	/*::    South latitudes are negative, east longitudes are positive           :*/
	/*::                                                                         :*/
	/*::  Passed to function:                                                    :*/
	/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
	/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
	/*::    unit = the unit you desire for results                               :*/
	/*::           where: 'M' is statute miles                                   :*/
	/*::                  'K' is kilometers (default)                            :*/
	/*::                  'N' is nautical miles                                  :*/
	/*::  Worldwide cities and other features databases with latitude longitude  :*/
	/*::  are available at http://www.geodatasource.com                          :*/
	/*::                                                                         :*/
	/*::  For enquiries, please contact sales@geodatasource.com                  :*/
	/*::                                                                         :*/
	/*::  Official Web site: http://www.geodatasource.com                        :*/
	/*::                                                                         :*/
	/*::           GeoDataSource.com (C) All Rights Reserved 2015                :*/
	/*::                                                                         :*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

	static public double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
	  double theta = lon1 - lon2;
	  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	  dist = Math.acos(dist);
	  dist = rad2deg(dist);
	  dist = dist * 60 * 1.1515;
	  if (unit == "K") {
	    dist = dist * 1.609344;
	  } else if (unit == "N") {
	  	dist = dist * 0.8684;
	    }
	  return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	static private double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	static private double rad2deg(double rad) {
	  return (rad * 180 / Math.PI);
	}
	
	public static List<GeoHash> getHashcodes(String hashBinary, double radius) {
		GeoHash geoHash = GeoHash.fromBinaryString(hashBinary);
		GeoHashCircleQuery geoHashCircleQuery = new GeoHashCircleQuery(geoHash.getPoint(), radius);
		return geoHashCircleQuery.getSearchHashes();
	}	
	/*
	 * returns the hashes of certain precision within the given radius
	 */
	
	public static List<GeoHash> getHashcodes(String hashBinary, double radius, int precision) {
		GeoHash geoHash = GeoHash.fromBinaryString(hashBinary);
		GeoHashCircleQuery geoHashCircleQuery = new GeoHashCircleQuery(geoHash.getPoint(), radius);
		
		List<GeoHash> courseGrainedHashes = geoHashCircleQuery.getSearchHashes();
		ArrayList<GeoHash> fineGrainedHashes = new ArrayList<GeoHash>();
		//returnList.add(geoHashCircleQuery.getWktBox());
		for (Iterator<GeoHash> it = courseGrainedHashes.iterator(); it.hasNext();) {
			GeoHash hash = it.next();
			
			BoundingBoxGeoHashIterator bbIterator = new BoundingBoxGeoHashIterator(TwoGeoHashBoundingBox.withBitPrecision(hash.getBoundingBox(),
					precision));
			while (bbIterator.hasNext()) {
				GeoHash fineGrainedGeoHash = bbIterator.next();
				fineGrainedHashes.add(fineGrainedGeoHash);
			}
		}
		return fineGrainedHashes;
	}

}
