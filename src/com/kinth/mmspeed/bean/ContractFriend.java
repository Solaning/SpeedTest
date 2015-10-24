package com.kinth.mmspeed.bean;

import java.io.Serializable;

public class ContractFriend implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2573128411701462409L;
	
	private String mobile;
	private String nickName;
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
	public ContractFriend(String mobile, String nickName) {
		super();
		this.mobile = mobile;
		this.nickName = nickName;
	}
	public ContractFriend() {
		super();
	}
	
}
