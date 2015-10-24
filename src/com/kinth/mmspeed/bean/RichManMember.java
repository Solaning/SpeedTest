package com.kinth.mmspeed.bean;

import java.io.Serializable;

/**
 * 土豪群成员
 */
public class RichManMember implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;

	/**
	 * 成员号码
	 */
	private String mobilenumber;

	/**
	 * 成员限额，单位M
	 */
	private int limit;

	/**
	 * 成员产品id
	 */
	private String prodid;

	/**
	 * 已用流量，单位byte
	 */
	private int usedlimit;

	/**
	 * 主副号标识
	 */
	private boolean ismainnumber;

	/**
	 * 成员本月状态
	 */
	private boolean currentmonthstate;

	/**
	 * 成员下个月状态
	 */
	private boolean nextmonthstate;

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getProdid() {
		return prodid;
	}

	public void setProdid(String prodid) {
		this.prodid = prodid;
	}

	public int getUsedlimit() {
		return usedlimit;
	}

	public void setUsedlimit(int usedlimit) {
		this.usedlimit = usedlimit;
	}

	public boolean getIsmainnumber() {
		return ismainnumber;
	}

	public void setIsmainnumber(boolean ismainnumber) {
		this.ismainnumber = ismainnumber;
	}

	public boolean getCurrentmonthstate() {
		return currentmonthstate;
	}

	public void setCurrentmonthstate(boolean currentmonthstate) {
		this.currentmonthstate = currentmonthstate;
	}

	public boolean getNextmonthstate() {
		return nextmonthstate;
	}

	public void setNextmonthstate(boolean nextmonthstate) {
		this.nextmonthstate = nextmonthstate;
	}

}