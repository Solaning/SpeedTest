package com.kinth.mmspeed.bean;

import java.io.Serializable;

public class SMSProdDetailInfo implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;

	// 开始时间
	private String begintime;
	// 结束时间
	private String endtime;
	// 总流量 KB
	private int contain;
	// 已使用流量
	private int used;
	// 剩余流量
	private int relay;
	// 类型
	private int type;
	// 产品ID
	private String prodid;
	// 产品名称
	private String prodname = "";

	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public int getContain() {
		return contain;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}

	public int getRelay() {
		return relay;
	}

	public void setRelay(int relay) {
		this.relay = relay;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProdid() {
		return prodid;
	}

	public void setProdid(String prodid) {
		this.prodid = prodid;
	}

	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}

	public void setContain(int contain) {
		this.contain = contain;
	}

	
}