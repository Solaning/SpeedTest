package com.kinth.mmspeed.bean;

import java.util.ArrayList;
import java.util.List;

public class NewSmsFlowInfo {
	private int type = 0;
	private List<SMSProdInfo>mSmsProdInfos;
	private double exceedFlow;
	public NewSmsFlowInfo(int type,List<SMSProdInfo> mSmsProdInfos, double exceedFlow) {
		super();
		this.type = type;
		this.mSmsProdInfos = mSmsProdInfos;
		this.exceedFlow = exceedFlow;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<SMSProdInfo> getmSmsProdInfos() {
		return mSmsProdInfos;
	}
	public NewSmsFlowInfo() {
		this.mSmsProdInfos = new ArrayList<SMSProdInfo>();
//		super();
	}
	public void setmSmsProdInfos(List<SMSProdInfo> mSmsProdInfos) {
		this.mSmsProdInfos = new ArrayList<SMSProdInfo>();
		this.mSmsProdInfos.addAll(mSmsProdInfos);
	}
	public double getExceedFlow() {
		return exceedFlow;
	}
	public void setExceedFlow(double exceedFlow) {
		this.exceedFlow = exceedFlow;
	}
	
}
