package com.kinth.mmspeed.bean;

import java.io.Serializable;

/**
 * 流量套餐规整后的类型
 * 
 * @author Sola
 * 
 */
public class SMSProdInfo implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;

	// 电话号码
	private String phoneStr;
	// 套餐类型（一种类型一笔数据，有几种类型就几个仪表盘，左上角下拉选择）
	private String prodType = "";
	// 月剩余流量，单位KB
	private double monthRemainFlow = 0;
	// 月总流量，单位KB
	private double monthAllFlow = 0;
	// 距月结日还剩天数
	private int remainDay = 0;

	public SMSProdInfo() {
		super();
	}

	public SMSProdInfo(String prodType, double monthRemainFlow,
			double monthAllFlow, int remainDay, String phoneStr) {
		super();
		this.prodType = prodType;
		this.monthRemainFlow = monthRemainFlow;
		this.monthAllFlow = monthAllFlow;
		this.remainDay = remainDay;
		this.phoneStr = phoneStr;
	}

	public String getPhoneStr() {
		return phoneStr;
	}

	public void setPhoneStr(String phoneStr) {
		this.phoneStr = phoneStr;
	}

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public double getMonthRemainFlow() {
		return monthRemainFlow;
	}

	public void setMonthRemainFlow(double monthRemainFlow) {
		this.monthRemainFlow = monthRemainFlow;
	}

	public double getMonthAllFlow() {
		return monthAllFlow;
	}

	public void setMonthAllFlow(double monthAllFlow) {
		this.monthAllFlow = monthAllFlow;
	}

	public int getRemainDay() {
		return remainDay;
	}

	public void setRemainDay(int remainDay) {
		this.remainDay = remainDay;
	}
}