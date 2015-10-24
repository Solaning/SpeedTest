package com.kinth.mmspeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.bean.VersionInfo;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.UpdateCallback;
import com.kinth.mmspeed.util.UpdateManager;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.setting)
public class SettingActivity extends BaseActivity {
	
	@ViewInject(R.id.nav_tittle)
	private TextView tittle;
	
	@ViewInject(R.id.tv_version)
	private TextView version;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_right_image)
	private ImageView right;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		this.finish();
		rightOutFinishAnimation();
	}
	
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mContext = this;
		
		right.setVisibility(View.GONE);
		tittle.setText(getResources().getString(R.string.menu_settings));
		version.setText("新版本检测 V" + UtilFunc.getCurrentVersionName(mContext));
		
	}
	
	@OnClick(R.id.About_Us_Layout)
	public void fun_3(View v) {// 关于我们 ---->先展示用户引导页面，再进入  TODO
		Intent intent = new Intent(mContext, AboutUsActivity.class);  
		startActivity(intent);
		rightInAnimation();
	}

	@OnClick(R.id.Feedback_Layout)
	public void fun_4(View v) {// 意见反馈
		Intent intent = new Intent(mContext, FeedbackActivity.class);
		startActivity(intent);
		rightInAnimation();
	}
	
	@OnClick(R.id.Update_Layout)
	public void fun_5(View v) {// 检测更新
		checkUpDate();
	}
	
	private void checkUpDate() {
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.asynUpdateFromServer(mContext, new UpdateCallback() {
			@Override
			public void updateCallback(int hasNew, VersionInfo versionInfo) {
				if (hasNew == 1) {// 有新版本
					UpdateManager update = new UpdateManager(mContext,versionInfo,true);//可以取消
					update.start();
				} else {// 没有新版本
					Toast.makeText(mContext, "暂时没有更新", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}
}
