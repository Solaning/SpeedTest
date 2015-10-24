package com.kinth.mmspeed;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.kinth.mmspeed.bean.AdArticleInfo;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * app启动页
 * 
 * @author admin
 */
public class AppStartActivity extends Activity {
	private DbUtils mDbUtils;
	private AdArticleInfo adArticleInfo;
	private List<UserPhone> mUserPhones;
	private UserPhoneDBService userPhoneDBService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 看是否有广告页
		mDbUtils = DbUtils.create(AppStartActivity.this,
				APPConstant.DATABASE_NAME);
		userPhoneDBService = new UserPhoneDBService(this);

		try {
			mDbUtils.createTableIfNotExist(AdArticleInfo.class);
			adArticleInfo = mDbUtils.findFirst(AdArticleInfo.class);
			userPhoneDBService.createTable();
			mUserPhones = userPhoneDBService.getAllUserPhones();
		} catch (DbException e1) {
			e1.printStackTrace();
			adArticleInfo = null;
		}
		// 获取客户端的公共信息,如果还没获取的话
		if (ApplicationController.getInstance().getDev().equals("")) {
			UtilFunc.getTerminalEndCommonInfo(this);
		}

		// 第一次运行 而且不是旧版本 比较版本号
		String preVersion = SingletonSharedPreferences.getInstance()
				.getVersionCode();// 上一个版本号
		int preVersionInt = 0;
		try {
			preVersionInt = Integer.parseInt(preVersion);
		} catch (NumberFormatException e) {

		}

		boolean showGuide = false;// 是否显示引导页
		if (TextUtils.isEmpty(preVersion)) {// 没有上一个的版本记录，说明当前是全新安装或者更早的版本
			showGuide = true;
		} else {
			if (preVersionInt == ApplicationController.getInstance()
					.getVersion()) {// 已经显示过了，不显示
				showGuide = false;
			}
			if (preVersionInt < ApplicationController.getInstance()
					.getVersion()) {
				showGuide = true;
			}
		}

		// 是否是第一次
		if (SingletonSharedPreferences.getInstance().isFirstLoad() || showGuide) {
			// 清理工作
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "SpeedTest");
			UtilFunc.deleteAll(file);

			SingletonSharedPreferences.getInstance().setFirestLoad(false);
			// Intent intent = null; TODO mod @2014-11-07
			// intent = new Intent(this, WhatsnewActivity.class);
			// startActivity(intent);
			// finish();

		}
		/** else{// mod @2014-11-07 去掉新功能介绍页 */
		{
			boolean ifPass = JUtil.getSharePreBool(AppStartActivity.this,
					APPConstant.SP_NAME, APPConstant.REGISTER_PASS);
			// 如果有号码，就判断是否有新闻
			if (mUserPhones != null && mUserPhones.size() > 0) {
				showAdPage(adArticleInfo);
			} else if (ifPass) {
				showAdPage(adArticleInfo);
			} else {
				Intent intent = new Intent(AppStartActivity.this,
						FirstLoginActivity.class);
				intent.putExtra(FirstLoginActivity.INTENT_TARGET_CLASS_NAME, SpeedMainActivity.class.getName());
				startActivity(intent);
				AppStartActivity.this.finish();
			}
		}
	}

	private void showAdPage(AdArticleInfo mAdArticleInfo) {
		if (mAdArticleInfo == null) {
			Intent intent = new Intent(this, SpeedMainActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		if (JUtil.isTimeBetween(adArticleInfo.getShowStartTime(),
				adArticleInfo.getShowEndTime())) {
			Intent intent = new Intent(this, AdStartImageActivity.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(this, SpeedMainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
