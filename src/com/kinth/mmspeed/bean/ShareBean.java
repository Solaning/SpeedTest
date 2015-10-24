package com.kinth.mmspeed.bean;

public class ShareBean {
	private int shareRes;
	private String name;
	
	public ShareBean() {
		super();
	}
	public int getShareRes() {
		return shareRes;
	}
	public void setShareRes(int shareRes) {
		this.shareRes = shareRes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ShareBean(int shareRes, String name) {
		super();
		this.shareRes = shareRes;
		this.name = name;
	}
	
}
