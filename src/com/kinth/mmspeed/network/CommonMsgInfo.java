package com.kinth.mmspeed.network;

import java.util.HashMap;

import com.kinth.mmspeed.util.ApplicationController;

public class CommonMsgInfo {
	public static HashMap<String, String> getCommonMsgInfo() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("dev", "Android,"
				+ ApplicationController.getInstance().getANDROID_API_VERSION()
				+ "," + ApplicationController.getInstance().getDev());
		params.put("resolution", ApplicationController.getInstance()
				.getResolution());
		params.put("mac", ApplicationController.getInstance().getMac());
		params.put("version", ApplicationController.getInstance().getVersion()
				+ "");
		params.put("uid", ApplicationController.getInstance().getUid());
		params.put("api", ApplicationController.getInstance().getInterfaceApi());// 接口的版本号
		params.put("password", ApplicationController.getInstance()
				.getPassword());
		return params;
	}
}
