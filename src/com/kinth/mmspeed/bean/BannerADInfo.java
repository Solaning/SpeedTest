package com.kinth.mmspeed.bean;

public class BannerADInfo {
	/**
	 * 广告ID
	 */
	private int adID;

	/**
	 * 广告图片下载地址
	 */
	private String adImageURLString;

	/**
	 * 广告标题，如果是文字类型广告则显示标题
	 */
	private String adTitle;

	/**
	 * 开始时间
	 */
	private String beginTime;
	/**
	 * 结束时间
	 */
	private String endTime;

	/**
	 * 轮换时间，单位：秒
	 */
	private int rotationSecond;

	/**
	 * 用户点击后跳转的URL
	 */
	private String clickURL;

	/**
	 * 广告类型：text(文字)/normalImage(普通静态图片)/gifImage(GIF动态图)
	 */
	private String adType;

	public int getAdID() {
		return adID;
	}

	public void setAdID(int adID) {
		this.adID = adID;
	}

	public String getAdImageURLString() {
		return adImageURLString;
	}

	public void setAdImageURLString(String adImageURLString) {
		this.adImageURLString = adImageURLString;
	}

	public String getAdTitle() {
		return adTitle;
	}

	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getRotationSecond() {
		return rotationSecond;
	}

	public void setRotationSecond(int rotationSecond) {
		this.rotationSecond = rotationSecond;
	}

	public String getClickURL() {
		return clickURL;
	}

	public void setClickURL(String clickURL) {
		this.clickURL = clickURL;
	}

	public String getAdType() {
		return adType;
	}

	public void setAdType(String adType) {
		this.adType = adType;
	}
}
