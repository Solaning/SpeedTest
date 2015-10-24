package com.kinth.mmspeed.bean;

import java.io.Serializable;

public class UserAccount implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;
	private String uid;
	private String mobile = "";
	private String nickName = "";
	private String iconURL = "";
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getIconURL() {
		return iconURL;
	}
	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
	public UserAccount(String mobile, String nickName, String iconURL) {
		super();
		this.mobile = mobile;
		this.nickName = nickName;
		this.iconURL = iconURL;
	}
	public UserAccount() {
		super();
	}
}