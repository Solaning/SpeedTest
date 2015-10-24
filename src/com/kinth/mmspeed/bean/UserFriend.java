package com.kinth.mmspeed.bean;

public class UserFriend {
	int id;
	private String mobile = "";
	private String nickName = "";
	private String iconURL = "";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	public UserFriend() {
		super();
	}

	public UserFriend(String mobile, String nickName, String iconURL) {
		super();
		this.mobile = mobile;
		this.nickName = nickName;
		this.iconURL = iconURL;
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

	
	
	
	
}
