package com.kinth.mmspeed.map;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.kinth.mmspeed.constant.APPConstant;

public class ClusterMarker {
	private LatLng mCenter;// 中点
	private List<MarkerOptionsWithPoint> mMarkers;// 该聚合中包含的Marker，即是有多少个Marker聚合成该ClusterMarker
	// 该聚合点的各运营商的平均速度值
	private double[] speed;// 0:移动 1:联通 2:电信
	private double[] upspeed;// 0:移动 1:联通 2:电信
	private LatLngBounds mGridBounds;

	public ClusterMarker() {
		if (mMarkers == null) {
			mMarkers = new ArrayList<MarkerOptionsWithPoint>();
			speed = new double[3];
			upspeed = new double[3];
		}
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

	private LatLng calAverageCenter() {
		double latitude = 0, longitude = 0;
		int len = mMarkers.size() == 0 ? 1 : mMarkers.size();
		for (int i = 0; i < len; i++) {
			latitude = latitude
					+ mMarkers.get(i).getMarkerOptions().getPosition().latitude; // TODO
			longitude = longitude
					+ mMarkers.get(i).getMarkerOptions().getPosition().longitude;
		}
		return new LatLng(latitude / len, longitude / len);
	}

	/**
	 * 计算平均速度值
	 */
	private void calAverageSpeed() {
		double maxOfMobile = 0, totalUnicom = 0, totalTelecom = 0;
		double maxOfMobileUpload = 0, totalUnicomUpload = 0, totalTelecomUpload = 0;
		int numUnicom = 0, numTelecom = 0;
		for (MarkerOptionsWithPoint item : mMarkers) {
			if (item.getMarkerPoint()
					.getNetworkOperator()
					.equalsIgnoreCase(
							APPConstant.OperatorType.MOBILE.getValue())) {
				if(item.getMarkerPoint().getSpeed() > maxOfMobile){
					maxOfMobile = item.getMarkerPoint().getSpeed();
				}
				if(item.getMarkerPoint().getUpspeed() > maxOfMobileUpload){
					maxOfMobileUpload = item.getMarkerPoint().getUpspeed();
				}
			}
			if (item.getMarkerPoint()
					.getNetworkOperator()
					.equalsIgnoreCase(
							APPConstant.OperatorType.UNICOM.getValue())) {
				totalUnicom += item.getMarkerPoint().getSpeed();
				totalUnicomUpload += item.getMarkerPoint().getUpspeed();
				numUnicom++;
			}
			if (item.getMarkerPoint()
					.getNetworkOperator()
					.equalsIgnoreCase(
							APPConstant.OperatorType.TELECOM.getValue())) {
				totalTelecom += item.getMarkerPoint().getSpeed();
				totalTelecomUpload += item.getMarkerPoint().getUpspeed();
				numTelecom++;
			}
		}
		speed[0] = maxOfMobile;
		upspeed[0] = maxOfMobileUpload;
		if (numUnicom != 0) {
			speed[1] = totalUnicom / numUnicom;
			upspeed[1] = totalUnicomUpload / numUnicom;
		} else {
			speed[1] = 0;
			upspeed[1] = 0;
		}
		if (numTelecom != 0) {
			speed[2] = totalTelecom / numTelecom;
			upspeed[2] = totalTelecomUpload / numTelecom;
		} else {
			speed[2] = 0;
			upspeed[2] = 0;
		}
	}

	public void AddMarker(MarkerOptionsWithPoint marker) {
		mMarkers.add(marker);
		// 计算平均值
		calAverageSpeed();
		this.mCenter = calAverageCenter();
	}

	public LatLng getmCenter() {
		return this.mCenter;
	}

	public void setmCenter(LatLng mCenter) {
		this.mCenter = mCenter;
	}

	public List<MarkerOptionsWithPoint> getmMarkers() {
		return mMarkers;
	}

	public void setmMarkers(List<MarkerOptionsWithPoint> mMarkers,
			Boolean isAverageCenter) {
		this.mMarkers.addAll(mMarkers);
		if (!isAverageCenter) {
			if (mCenter == null) {
				this.mCenter = mMarkers.get(0).getMarkerOptions().getPosition();
			}
		} else
			this.mCenter = calAverageCenter();
	}

	public LatLngBounds getmGridBounds() {
		return mGridBounds;
	}

	public void setmGridBounds(LatLngBounds mGridBounds) {
		this.mGridBounds = mGridBounds;
	}

}
