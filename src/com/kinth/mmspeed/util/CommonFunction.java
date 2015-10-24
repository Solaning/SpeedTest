package com.kinth.mmspeed.util;

import java.util.HashMap;

import org.json.JSONObject;

import android.util.Log;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.CommonMsgInfo;

public class CommonFunction {
	/**
	 * 激活处理
	 */
	public void activateHandle(final Button view) {
		if (SingletonSharedPreferences.getInstance().getActivated()) {// 已经激活，登陆
			HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
			params.put("networkOperator", ApplicationController.getInstance().getNetworkOperator());// 网络运营商
			JsonObjectRequest req = new JsonObjectRequest(APPConstant.LOGIN_URL, new JSONObject(params),  
			       new Response.Listener<JSONObject>() {

					@Override
						public void onResponse(JSONObject response) {
							int rtn = 0;
							int isOpenFlowRedPacket = 0;//是否打开流量红包
							int canSkip = 0;
							try {
								rtn = response.getInt("rtn");
								isOpenFlowRedPacket = response.getInt("isOpenFlowRedPacket");
								canSkip = response.getInt("canSkip");
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (rtn == 1) {// 激活成功
								SingletonSharedPreferences.getInstance().setIsOpenFlowRedPacket(isOpenFlowRedPacket);//是否打开流量红包
								ApplicationController.getInstance().setHasActiveOrLogin(true);
								ApplicationController.getInstance().setCanSkip(canSkip);
								if(view != null){
									if(canSkip == 1){
										view.setEnabled(true);
									}else{
										view.setEnabled(false);
									}
								}
							}
						}
			       }, new Response.ErrorListener() {
			           @Override  
			           public void onErrorResponse(VolleyError error) {  
			               VolleyLog.e("Error: ", error.getMessage());  
			               Log.e("response error",""+error.getMessage());
			           }  
			       });  
			ApplicationController.getInstance().addToRequestQueue(req);
			
		} else {// 没有激活
			HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
			params.put("networkOperator", ApplicationController.getInstance().getNetworkOperator());// 网络运营商
			JsonObjectRequest req = new JsonObjectRequest(APPConstant.ACTIVE_DEVICE_URL, new JSONObject(params),  
			       new Response.Listener<JSONObject>() {

					@Override
						public void onResponse(JSONObject response) {
							int rtn = 0;
							String password = "";
							int isOpenFlowRedPacket = 0;//是否打开流量红包
							int canSkip = 0;
							try {
								rtn = response.getInt("rtn");
								password = response.getString("password");
								isOpenFlowRedPacket = response.getInt("isOpenFlowRedPacket");
								canSkip = response.getInt("canSkip");
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (rtn == 1) {// 激活成功
								ApplicationController.getInstance().setHasActiveOrLogin(true);
								ApplicationController.getInstance().setCanSkip(canSkip);
								if(view != null){
									if(canSkip == 1){
										view.setEnabled(true);
									}else{
										view.setEnabled(false);
									}
								}
								SingletonSharedPreferences.getInstance()
										.setActivated(true);// 已经激活
								SingletonSharedPreferences.getInstance()
										.setPassword(password);// 保存密码
								SingletonSharedPreferences.getInstance().setIsOpenFlowRedPacket(isOpenFlowRedPacket);//是否打开流量红包
							}
						}
			       }, new Response.ErrorListener() {
			           @Override  
			           public void onErrorResponse(VolleyError error) {  
			               VolleyLog.e("Error: ", error.getMessage());  
			               Log.e("response error",""+error.getMessage());
			           }  
			       });  
			ApplicationController.getInstance().addToRequestQueue(req);
		}
	}
}
