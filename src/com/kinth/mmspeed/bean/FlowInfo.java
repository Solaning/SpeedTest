package com.kinth.mmspeed.bean;

public class FlowInfo {
	private float totalFlow;
	private float remainFlow;
	private String endDate;
	
	public FlowInfo() {
		super();
	}
	public FlowInfo(float totalFlow, float remainFlow, String endDate) {
		super();
		this.totalFlow = totalFlow;
		this.remainFlow = remainFlow;
		this.endDate = endDate;
	}
	public float getTotalFlow() {
		return totalFlow;
	}
	public void setTotalFlow(float totalFlow) {
		this.totalFlow = totalFlow;
	}
	public float getRemainFlow() {
		return remainFlow;
	}
	public void setRemainFlow(float remainFlow) {
		this.remainFlow = remainFlow;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
	
}
