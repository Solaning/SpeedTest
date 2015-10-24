package com.kinth.mmspeed.bean;

/**
 * 4G排行榜
 * @author Sola
 *
 */
public class RankItem4G {

	private String mobile;
	private String nickname;
	private String iconURL;
	private String prodName; // 套餐名
	private int day; // 套餐查询时间（当月号数），这个参数只是后台用

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIconURL() {
		return iconURL;
	}

	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
