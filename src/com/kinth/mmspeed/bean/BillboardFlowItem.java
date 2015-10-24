package com.kinth.mmspeed.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "BillboardFlowItem") 
public class BillboardFlowItem {
	int id;
	@Column(column = "mobile")
	private String mobile;
	@Column(column = "useflow")
	private int useflow;
	@Column(column = "iconUrl")
	private String iconUrl;
	@Column(column = "nickName")
	private String nickName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public BillboardFlowItem() {
		super();
	}
	public BillboardFlowItem(String mobile, int useflow,String iconUrl,String nickName) {
		super();
		this.mobile = mobile;
		this.useflow = useflow;
		this.iconUrl = iconUrl;
		this.nickName = nickName;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getUseflow() {
		return useflow;
	}
	public void setUseflow(int useflow) {
		this.useflow = useflow;
	}
	
}
