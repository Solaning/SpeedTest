package com.kinth.mmspeed.bean;

import java.io.Serializable;
import java.util.List;

public class FlowPackageTypeInfo implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;

	// 套餐简介      省略了套餐类型名称：月套餐，叠加包，闲时套餐
	private String typeName;
	private String introduction;
	private List<FlowPackageInfo> list;

	public List<FlowPackageInfo> getList() {
		return list;
	}

	public void setList(List<FlowPackageInfo> list) {
		this.list = list;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}