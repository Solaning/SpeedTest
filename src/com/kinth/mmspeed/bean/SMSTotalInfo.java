package com.kinth.mmspeed.bean;

import java.io.Serializable;

/**
 * 总流量信息
 *
 */
public class SMSTotalInfo implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;
	/**
	 * 套餐总量 单位KB
	 */
	private double resourcesCount = 0;
	
	/**
	 * 已使用总量 单位KB
	 */
	private double usedResCount = 0;
	
	/**
	 * 超出套餐部分使用量 单位KB
	 */
	private double exceedUsedCount = 0;
	
	/**
	 * 剩余累计量  单位KB
	 */
	private double leavingsCount = 0;
	
	/**
	 * 截至时间  格式 ：YYYYMMDDHH24MISS
	 */
	private String endDate = "";

	
	
	public double getResourcesCount() {
		return resourcesCount;
	}

	public void setResourcesCount(double resourcesCount) {
		this.resourcesCount = resourcesCount;
	}

	public double getUsedResCount() {
		return usedResCount;
	}

	public void setUsedResCount(double usedResCount) {
		this.usedResCount = usedResCount;
	}

	public double getExceedUsedCount() {
		return exceedUsedCount;
	}

	public void setExceedUsedCount(double exceedUsedCount) {
		this.exceedUsedCount = exceedUsedCount;
	}

	public double getLeavingsCount() {
		return leavingsCount;
	}

	public void setLeavingsCount(double leavingsCount) {
		this.leavingsCount = leavingsCount;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
