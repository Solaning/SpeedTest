package com.kinth.mmspeed;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.SelectMainMobileListAdapter;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * @author Sola
 * 通用--选择号码作为主账号
 */
@ContentView(R.layout.choice_main_mobile)
public class GM_SelectMobileAsMainAccountActivity extends BaseActivity {
	public static final String INTENT_TARGET_CLASS_NAME = APPConstant.INTENT_TARGET_CLASS_NAME;//最终需要放到intent中跳转的activity
	
	private Context mContext;
	private List<UserPhone> userPhones;
	private UserPhoneDBService userPhoneDBService;
	private SelectMainMobileListAdapter selectMainMobileListAdapter;
	private ProgressDialog mProgressDialog = null;
	private Bundle bundle;
	
	@ViewInject(R.id.lv_select_mobile)
	private ListView mListView;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		tvTittle.setText("登录");
		
		bundle = getIntent().getExtras();
		
		userPhoneDBService = new UserPhoneDBService(mContext);
		userPhones = userPhoneDBService.getAllUserPhones();
		
		selectMainMobileListAdapter = new SelectMainMobileListAdapter(mContext,
				userPhones);
		mListView.setAdapter(selectMainMobileListAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mProgressDialog = JUtil.showDialog(mContext, "正在获取验证码");
				final String phone = userPhones.get(arg2).getPhoneStr();
				AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
				asyncNetworkManager.getSMSCode(mContext, phone,
						new AsyncNetworkManager.GetSMSCode() {
							@Override
							public void getSMSCallBack(int rtn) {
								JUtil.dismissDialog(mProgressDialog);
								if (rtn == 1) {
									Intent intent = new Intent(mContext,
											GM_ValidateVerificationCodeActivity.class);
									if(bundle == null){
										bundle = new Bundle();
									}
									bundle.putString(GM_ValidateVerificationCodeActivity.INTENT_PHONE, phone);//手机号
									bundle.putString(GM_ValidateVerificationCodeActivity.INTENT_SOURCE_CLASS_NAME, GM_SelectMobileAsMainAccountActivity.class.getName());//添加当前类
									intent.putExtras(bundle);
									startActivity(intent);
									rightInAnimation();
									finish();
								} else if (rtn == -2) {
									JUtil.showMsg(mContext,
											"获取验证码失败,请检查您的网络连接！");
								} else if (rtn == -3) {
									JUtil.showMsg(mContext,
											"获取验证码失败超时,请稍后重试！");
								} else {
									JUtil.showMsg(mContext,
											"获取验证码失败,请重新获取！");
								}
							}
						});
			}
		});
	}
}
