package com.kinth.mmspeed.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用信息及所用流量bean
 * @author Sola
 * 
 */
public class AppFlowInfo {
	private int uid;//uid
	private String appName;//应用名
	private String packageName;//包名
	private String versionName;//版本名
	private int versionCode;//版本号
	private Drawable appIcon;//图标的路径--存在本地
	private long flowRx;//接收的流量
	private long flowTx;//发送的流量 
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public long getFlowRx() {
		return flowRx;
	}

	public void setFlowRx(long flowRx) {
		this.flowRx = flowRx;
	}

	public long getFlowTx() {
		return flowTx;
	}

	public void setFlowTx(long flowTx) {
		this.flowTx = flowTx;
	}

}
