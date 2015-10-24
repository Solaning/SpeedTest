package com.kinth.mmspeed.map;

import com.baidu.mapapi.map.MarkerOptions;
import com.kinth.mmspeed.bean.MarkerPoint;

/**
 * 该点的位置信息以及其他附加
 * @author Sola
 *
 */
public class MarkerOptionsWithPoint {
	private MarkerOptions markerOptions;
	private MarkerPoint markerPoint;
	
	public MarkerOptionsWithPoint(MarkerOptions markerOptions,
			MarkerPoint markerPoint) {
		super();
		this.markerOptions = markerOptions;
		this.markerPoint = markerPoint;
	}

	public MarkerOptions getMarkerOptions() {
		return markerOptions;
	}

	public void setMarkerOptions(MarkerOptions markerOptions) {
		this.markerOptions = markerOptions;
	}

	public MarkerPoint getMarkerPoint() {
		return markerPoint;
	}

	public void setMarkerPoint(MarkerPoint markerPoint) {
		this.markerPoint = markerPoint;
	}
	
}
