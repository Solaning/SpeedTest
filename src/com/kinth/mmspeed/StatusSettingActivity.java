package com.kinth.mmspeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.IdleSetting;
import com.kinth.mmspeed.constant.LocationSetting;
import com.kinth.mmspeed.constant.NetWorkState;
import com.kinth.mmspeed.constant.NetworkSetting;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 自定义设置状态
 *
 */
@ContentView(R.layout.activity_setting_status)
public class StatusSettingActivity extends BaseActivity implements OnClickListener{
	public static final String ACTION_STATE_CHANGE = "ACTION_STATE_CHANGE";
	public static final String INTENT_POSITION = "INTENT_POSITION";
	public static final String INTENT_NETWORK = "INTENT_NETWORK";
	public static final String INTENT_IDLE_TIME = "INTENT_IDLE_TIME";
	//位置
	@ViewInject(R.id.rgp_position)
	private RadioGroup PositionRadioGroup;
	@ViewInject(R.id.rbt_position_auto)
	private RadioButton rbtPositionAuto; 
	@ViewInject(R.id.rbt_position_in_gd)
	private RadioButton rbtPositionInGd;
	@ViewInject(R.id.rbt_position_notin_gd)
	private RadioButton rbtPositionNotInGd;
	//网络状态
	@ViewInject(R.id.rgp_network)
	private RadioGroup networkRadioGroup;
	@ViewInject(R.id.rbt_network_auto)
	private RadioButton rbtNetworkAuto; 
	@ViewInject(R.id.rbt_network_2g)
	private RadioButton rbt2G; 
	@ViewInject(R.id.rbt_network_3g)
	private RadioButton rbt3G;
	@ViewInject(R.id.rbt_network_4g)
	private RadioButton rbt4G;
	@ViewInject(R.id.rbt_network_wifi)
	private RadioButton rbtWifi;
	//是否闲时状态
	@ViewInject(R.id.rgp_idle)
	private RadioGroup rgpIdleTime;
	@ViewInject(R.id.rbt_idletime_auto)
	private RadioButton rbtIdleTimeAuto; 
	@ViewInject(R.id.rbt_idletime_idle)
	private RadioButton rbtIdleTimeIdle;
	@ViewInject(R.id.rbt_idletime_not_idle)
	private RadioButton rbtIdleTimeNotIdle;
	private int locationSetting = 0;
	private int networkSetting = 0;
	private int idleTimeSetting = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_status);
		ViewUtils.inject(this);
		initView();
		rbtPositionAuto.setOnClickListener(this); 
		rbtPositionInGd.setOnClickListener(this); 
		rbtPositionNotInGd.setOnClickListener(this); 
		//网络状态
		rbtNetworkAuto.setOnClickListener(this); 
		rbt2G.setOnClickListener(this); 
		rbt3G.setOnClickListener(this); 
		rbt4G.setOnClickListener(this); 
		rbtWifi.setOnClickListener(this); 
		//是否闲时状态
		rgpIdleTime.setOnClickListener(this); 
		rbtIdleTimeAuto.setOnClickListener(this); 
		rbtIdleTimeIdle.setOnClickListener(this); 
		rbtIdleTimeNotIdle.setOnClickListener(this); 
	}
	private void initView() {
		locationSetting  = JUtil.getSharePreInt(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.LOCATION_SETTING);
		networkSetting  = JUtil.getSharePreInt(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.NETWORK_SETTING);
		idleTimeSetting  = JUtil.getSharePreInt(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.IDLE_SETTING);
		switch (locationSetting) {
		case LocationSetting.AUTO:
			rbtPositionAuto.setChecked(true);
			break;
		case LocationSetting.IN_GD:
			rbtPositionInGd.setChecked(true);
			break;
		case LocationSetting.OUT_GD:
			rbtPositionNotInGd.setChecked(true);
			break;
		default:
			break;
		}
		switch (networkSetting) {
		case NetworkSetting.AUTO:
			rbtNetworkAuto.setChecked(true);
			break;
		case NetworkSetting._2G:
			rbt2G.setChecked(true);
			break;
		case NetworkSetting._3G:
			rbt3G.setChecked(true);
			break;
		case NetworkSetting._4G:
			rbt4G.setChecked(true);
			break;
		case NetworkSetting.WIFI:
			rbtWifi.setChecked(true);
			break;
		default:
			break;
		}
		switch (idleTimeSetting) {
		case IdleSetting.AUTO:
			rbtIdleTimeAuto.setChecked(true);
			break;
		case IdleSetting.IDLE:
			rbtIdleTimeIdle.setChecked(true);
			break;
		case IdleSetting.NOT_IDLE:
			rbtIdleTimeNotIdle.setChecked(true);
			break;
		default:
			break;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		//位置设置
		case R.id.rbt_position_auto:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.LOCATION_SETTING, LocationSetting.AUTO);
			break;
		case R.id.rbt_position_in_gd:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.LOCATION_SETTING, LocationSetting.IN_GD);
			break;
		case R.id.rbt_position_notin_gd:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.LOCATION_SETTING, LocationSetting.OUT_GD);
			break;
		//网络设置
		case R.id.rbt_network_auto:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, NetWorkState.AUTO);
			break;
		case R.id.rbt_network_2g:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, NetWorkState._2G);
			break;
		case R.id.rbt_network_3g:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, NetWorkState._3G);
			break;
		case R.id.rbt_network_4g:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, NetWorkState._4G);
			break;
		case R.id.rbt_network_wifi:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, NetWorkState.WIFI);
			break;
		//闲时设置
		case R.id.rbt_idletime_auto:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.IDLE_SETTING, IdleSetting.AUTO);
			break;
		case R.id.rbt_idletime_idle:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.IDLE_SETTING, IdleSetting.IDLE);
			break;
		case R.id.rbt_idletime_not_idle:
			JUtil.putSharePre(StatusSettingActivity.this, APPConstant.SP_NAME, APPConstant.IDLE_SETTING, IdleSetting.NOT_IDLE);
			break;
		default:
			break;
		}
		intent.setAction(ACTION_STATE_CHANGE);
		sendBroadcast(intent);
	}
	@Override
	public void leftBtnOnClick(View v) {
		StatusSettingActivity.this.finish();
		overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}
}
