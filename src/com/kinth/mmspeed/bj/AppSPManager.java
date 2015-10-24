package com.kinth.mmspeed.bj;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.kinth.mmspeed.bean.User;
import com.kinth.mmspeed.bean.UserFriend;

public class AppSPManager {
	private List<UserFriend>userFriends = new ArrayList<UserFriend>();
	private static final String SP_NAME = "SP_MANAGER";
	private  static AppSPManager instance = new AppSPManager();
	private Context context;
	
	public AppSPManager() {
		super();
	}
	public static AppSPManager getInstance(){
		return instance;
	}
	public void putUserFriendUpdateTime(Context context,String userMobile) {
		
	}
	/**
	 * 取出whichSp中field字段对应的string类型的值
	 * @param mContext
	 * @param whichSp
	 * @param field
	 * @return
	 */
	public static String getSharePreStr(Context mContext,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(SP_NAME, 0);
		return sp.getString(field, "");// 没有则返回“”
	}
	/**
	 *  取出whichSp中field字段对应的int类型的值
	 * @param mContext
	 * @param whichSp
	 * @param field
	 * @return
	 */
	//
	public static int getSharePreInt(Context mContext, 
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(SP_NAME, 0);
		return sp.getInt(field, 0);// 没有则返回0
	}
	// 取出whichSp中field字段对应的int类型的值
	public static boolean getSharePreBool(Context mContext,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(SP_NAME, 0);
		return sp.getBoolean(field, false);// 没有则返回false
	}

	// 保存string类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, 
			String field, String value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(field, value).commit();
	}
	public static void putShareBool(Context mContext, 
			String field, boolean value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(SP_NAME, 0);
		sp.edit().putBoolean(field, value).commit();
	}
	// 保存int类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, 
			String field, int value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(SP_NAME, 0);
		sp.edit().putInt(field, value).commit();
	}

}
