package com.kinth.mmspeed.map;

import com.baidu.mapapi.map.MarkerOptions;

public class MarkerOptionsWithSpeed {
	private MarkerOptions markerOptions;
	private double[] speed;
	private double[] upspeed;

	public MarkerOptionsWithSpeed(MarkerOptions markerOptions, double[] speed,double[] upspeed) {
		super();
		this.markerOptions = markerOptions;
		this.speed = speed;
		this.upspeed = upspeed;
	}

	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}

	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}

	public double[] getSpeed() {
		return speed;
	}

	public void setSpeed(double[] speed) {
		this.speed = speed;
	}

	public double[] getUpspeed() {
		return upspeed;
	}

	public void setUpspeed(double[] upspeed) {
		this.upspeed = upspeed;
	}

}
