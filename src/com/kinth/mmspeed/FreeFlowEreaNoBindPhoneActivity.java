package com.kinth.mmspeed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 免流量专区没有绑定号码页面
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_free_flow_erea_no_bind_phone)
public class FreeFlowEreaNoBindPhoneActivity extends BaseActivity{
	private Context mContext;
	
	@ViewInject(R.id.iv_back)
	private ImageView back;
	
	@ViewInject(R.id.tv_title)
	private TextView title;
	
	@ViewInject(R.id.tv_content_description)
	private TextView contentDescription;//内容描述
	
	@ViewInject(R.id.btn_go_to_bind_phone)
	private Button goToBindPhone;//确定按钮
	
	@OnClick(R.id.iv_back)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}
	
	@OnClick(R.id.btn_go_to_bind_phone)
	public void fun_2(View v){//前往绑定号码页面
		Intent intent = new Intent(mContext, RMGCommonUserPhonesActivity.class);
		intent.putExtra(RMGCommonUserPhonesActivity.INTENT_SERVICE_ID, ServiceID.FREE_FLOW);
		startActivity(intent);
		rightInAnimation();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		title.setText("免流量专区");
		contentDescription.setText("您尚未绑定手机号码，点击确认前往“号码管理”页面进行绑定");
		goToBindPhone.setText("确认");
	}
	
}
