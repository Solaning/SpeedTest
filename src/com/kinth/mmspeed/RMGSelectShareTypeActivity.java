package com.kinth.mmspeed;

import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 流量共享--->选择发起还是加入共享
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_rmg_select_share_type)
public class RMGSelectShareTypeActivity extends BaseActivity {
	public static final String INTENT_SERVICE_ID = "INTENT_SERVICE_ID";//区分是流量共享还是流量转赠
	public static final String INTENT_PHONE = "INTENT_PHONE";
	
	private Context mContext;
	private String phone;
	private ServiceID serviceId;// enum类型
	
	@ViewInject(R.id.iv_back)
	private ImageView back;
	
	@ViewInject(R.id.tv_title)
	private TextView title;//标题
	
	@ViewInject(R.id.tv_price)
	private TextView price;//价格
	
	@ViewInject(R.id.tv_inaugurate_state)
	private TextView inaugurateState;//开通状态
	
	@ViewInject(R.id.ll_total_share)
	private LinearLayout totalShare;
	
	@ViewInject(R.id.btn_initiate_share)
	private Button initiateShare;// 发起共享
	
	@ViewInject(R.id.btn_join_share)
	private Button joinShare;// 加入共享
	
	@ViewInject(R.id.btn_inaugurate_donation)
	private Button inaugurateDonation;//开通流量转赠
	
	@OnClick(R.id.iv_back)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.btn_initiate_share)// 发起共享，即创建群组
	public void fun_2(View v){
		Intent intent = new Intent(mContext,RMGConfirmTypeActivity.class);
		intent.putExtra(RMGConfirmTypeActivity.INTENT_TYPE, RMGConfirmTypeActivity.TYPE_CREATE_RICH_MAN_GROUP);
		intent.putExtra(RMGConfirmTypeActivity.INTENT_PHONE, phone);//电话号码
		startActivity(intent);
		finish();
		rightInAnimation();
	}
	
	@OnClick(R.id.btn_join_share)// 加入共享
	public void fun_3(View v){
		Intent intent1 = new Intent(mContext,RMGConfirmTypeActivity.class);
		intent1.putExtra(RMGConfirmTypeActivity.INTENT_TYPE, RMGConfirmTypeActivity.TYPE_INVITE_MEMBER);
		intent1.putExtra(RMGConfirmTypeActivity.INTENT_PHONE, phone);//电话号码
		startActivity(intent1);
		finish();
		rightInAnimation();
	}
	
	@OnClick(R.id.btn_inaugurate_donation)
	public void fun_4(View v){//开通流量转赠
		Intent intent1 = new Intent(mContext,RMGConfirmTypeActivity.class);
		intent1.putExtra(RMGConfirmTypeActivity.INTENT_TYPE, RMGConfirmTypeActivity.TYPE_INAUGURATE_DONATION);
		intent1.putExtra(RMGConfirmTypeActivity.INTENT_PHONE, phone);//电话号码
		startActivity(intent1);
		finish();
		rightInAnimation();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		serviceId = (ServiceID) getIntent().getSerializableExtra(INTENT_SERVICE_ID);
		phone = getIntent().getStringExtra(INTENT_PHONE);
		if(TextUtils.isEmpty(phone)){
			return;
		}
		ViewUtils.inject(this);
		switch (serviceId) {
		case DO_RICH_MAN_TOGETHER:
			title.setText("流量共享套餐");
			inaugurateState.setText("您暂未参加流量共享套餐");
			price.setText("价        格：0-20元/月");
			inaugurateDonation.setVisibility(View.GONE);
			totalShare.setVisibility(View.VISIBLE);
			break;
		case I_AM_RICH_MAN:
			title.setText("流量转赠套餐");
			inaugurateState.setText("您暂未开通流量转赠套餐");
			price.setText("价        格：5元/月");
			inaugurateDonation.setVisibility(View.VISIBLE);
			totalShare.setVisibility(View.GONE);
			break;
		case NOVALUE:
		default:
			break;
		}
	}
}
