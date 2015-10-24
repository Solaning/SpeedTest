package com.kinth.mmspeed.bean;

import java.io.Serializable;

public class MobileExtraInfo  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2351239564332572479L;

	private String uid;
	private String mobile;
	private String nickName;
	private String remark;
	
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
