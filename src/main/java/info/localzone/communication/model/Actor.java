package info.localzone.communication.model;


public class Actor {
	public Actor() {
		
	}
	String name;
	public Actor(String name, Location location) {
		super();
		this.name = name;
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	Location location;
}
