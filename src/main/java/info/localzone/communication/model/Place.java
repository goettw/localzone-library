package info.localzone.communication.model;



public class Place {
	public String getId() {
		return id;
	}
	public Place() {
		address = new Address();
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public double getLat() {
		return position[0];
	}
	
	public double getLon() {
		return position[1];
	}
	public String getPrimaryEmailAddress() {
		return primaryEmailAddress;
	}
	public void setPrimaryEmailAddress(String primaryEmailAddress) {
		this.primaryEmailAddress = primaryEmailAddress;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	double [] position;
	String display_name;
	String type;
	String subType;
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	String webSite;
	String phoneNumber;
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
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

	public double[] getPosition() {
		return position;
	}
	public void setPosition(double[] position) {
		this.position = position;
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
