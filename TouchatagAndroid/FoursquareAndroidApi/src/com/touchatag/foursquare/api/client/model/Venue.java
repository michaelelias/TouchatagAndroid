package com.touchatag.foursquare.api.client.model;

public class Venue {

	private String id;
	private String name;
	private String address;
	private String postalCode;
	private String state;
	private String city;
	private String distance;
	private String icon;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getAddressAsFormattedString(){
		String formatted = "";
		if(address != null){
			formatted += address;
		}
		String area = "";
		if(postalCode != null){
			area += postalCode + " ";
		}
		if(city != null){
			area += city + " ";
		}
		if(state != null){
			area += state + " ";
		}
		formatted += ", " + area;
		return formatted;
	}

}
