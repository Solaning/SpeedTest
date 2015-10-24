package com.kinth.mmspeed.bean;

/**
 * 流量分类的实体类，跟SMSProdInfo类似
 * 
 * @author Sola
 * 
 */
public class FlowCategoryBean {
	// 套餐类型（一种类型一笔数据，有几种类型就几个仪表盘，左上角下拉选择）
	private String prodType = "";
	// 月总流量，单位KB
	private double monthAllFlow = 0;
	// 已经使用的占总的百分比
	private double remainPercent = 0;
	// 是否有效
	private boolean valid = false;

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public double getMonthAllFlow() {
		return monthAllFlow;
	}

	public void setMonthAllFlow(double monthAllFlow) {
		this.monthAllFlow = monthAllFlow;
	}

	public double getRemainPercent() {
		return remainPercent;
	}

	public void setRemainPercent(double remainPercent) {
		this.remainPercent = remainPercent;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
