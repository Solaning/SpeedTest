package com.kinth.mmspeed.bj;

import java.util.HashMap;

import com.kinth.mmspeed.bean.UserFlowInfo;

/**
 * 流量管理的单例类
 *
 */
public class FlowSingleton {
	private static FlowSingleton flowSingleton;
	private static HashMap<String, UserFlowInfo> flowHashMap;
	private static int currentPosition = 0;//流量查询页面当前tab的位置

	public static FlowSingleton getInstance() {
		if (flowSingleton == null) {
			flowHashMap = new HashMap<String, UserFlowInfo>();
			flowSingleton = new FlowSingleton();
		}
		return flowSingleton;
	}

	public UserFlowInfo getSmsFlowInfo(String phone) {
		return flowHashMap.get(phone);
	}

	public void putSmsFlowInfo(String phone, UserFlowInfo userFlowInfo) {
		flowHashMap.put(phone, userFlowInfo);
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		FlowSingleton.currentPosition = currentPosition;
	}
}
