package com.kinth.mmspeed.bean;

/**
 * 地图上某个点
 * @author Sola
 * 
 */
public class MarkerPoint {
	private String geoStr;//
	private double latitude;// 纬度
	private double longitude;// 经度
	private String networkOperator;//运营商
	private double speed;
	private double upspeed;

	public String getGeoStr() {
		return geoStr;
	}

	public void setGeoStr(String geoStr) {
		this.geoStr = geoStr;
	}

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

	public String getNetworkOperator() {
		return networkOperator;
	}

	public void setNetworkOperator(String networkOperator) {
		this.networkOperator = networkOperator;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getUpspeed() {
		return upspeed;
	}

	public void setUpspeed(double upspeed) {
		this.upspeed = upspeed;
	}

}
