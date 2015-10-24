package com.kinth.mmspeed.bean;

import java.io.Serializable;

public class FlowPackageInfo implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;

	/**
	 * 流量套餐ID
	 */
	private int flowID;
	// 2G/3G还是4G
	private String showType;
	// 具体流量
	private String flow;
	// 所需价格
	private String price;
	// Wap页面的URL
	private String wapURL;
	// 业务办理ID
	private String operationID;

	public String getOperationID() {
		return operationID;
	}

	public void setOperationID(String operationID) {
		this.operationID = operationID;
	}

	public String getFlow() {
		return flow;
	}

	public void setFlow(String flow) {
		this.flow = flow;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getWapURL() {
		return wapURL;
	}

	public void setWapURL(String wapURL) {
		this.wapURL = wapURL;
	}

	public int getFlowID() {
		return flowID;
	}

	public void setFlowID(int flowID) {
		this.flowID = flowID;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}
}