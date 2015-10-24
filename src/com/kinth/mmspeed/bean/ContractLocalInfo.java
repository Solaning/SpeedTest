package com.kinth.mmspeed.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "ContractLocalInfo") 
public class ContractLocalInfo {
	 int id;
	 @Column(column = "phoneNumber")
	 String mobile;
	@Column(column = "contractName")
	 String contractName;
	@Column(column = "state")
	 private int state;
	@Column(column = "iconURL")
	 String  iconURL;
	@Column(column = "nickName")
	 String  nickName;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ContractLocalInfo() {
		super();
	}
	
	public String getIconURL() {
		return iconURL;
	}
	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public ContractLocalInfo(String mobile,String nickName,
			 String iconURL, String contractName,int state) {
		super();
		this.mobile = mobile;
		this.contractName = contractName;
		this.state = state;
		this.iconURL = iconURL;
		this.nickName = nickName;
	}
	
	public ContractLocalInfo(String mobile, String contractName,
			int state, String iconURL, String nickName) {
		super();
		
		this.mobile = mobile;
		this.contractName = contractName;
		this.state = state;
		this.iconURL = iconURL;
		this.nickName = nickName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	 
}
