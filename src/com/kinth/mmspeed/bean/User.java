package com.kinth.mmspeed.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;


@Table(name = "User") 
public class User {
	@Id
	@NoAutoIncrement
	private String mobile;
	@Column(column = "nickName")
	private String nickName;
	@Column(column = "iconUrl")
	private String iconUrl;
	@Column(column = "uid")
	private String uid;
	@Column(column = "password")
	private String password;
	@Column(column = "status")
	private int status;
	public User() {
		super();
	}
	public User(String nickName, String iconUrl, String mobile, String uid,String password,int status) {
		super();
		this.nickName = nickName;
		this.iconUrl = iconUrl;
		this.mobile = mobile;
		this.uid = uid;
		this.password = password;
		this.status = status;
	}
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
