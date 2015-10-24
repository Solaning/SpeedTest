package com.kinth.mmspeed;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.CommonFunction;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 第一次登陆，绑定主账号
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_login)
public class FirstLoginActivity extends BaseActivity {
	public static final String INTENT_TARGET_CLASS_NAME = APPConstant.INTENT_TARGET_CLASS_NAME; //最终需要放到intent中跳转的activity
	
	private Context mContext;
	private ProgressDialog mProgressDialog = null;
	public static final String INTENT_PHONE = "phone";
	private List<UserPhone> userPhones;
	private UserPhoneDBService userPhoneDBService;
	private Bundle bundle;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	@ViewInject(R.id.nav_right_btn)
	private Button rightBtn;
	
	@ViewInject(R.id.nav_left)
	private ImageButton tvNavLeft;
	
	@ViewInject(R.id.edt_phone)
	private EditText edtPhone; // 账号
	
	@ViewInject(R.id.btn_login)
	private Button btnLogin;
	
	@OnClick(R.id.nav_right_text)
	public void fun_1(View v){// 跳过，设置 跳过标示
		JUtil.putShareBool(mContext, APPConstant.SP_NAME,
				APPConstant.REGISTER_PASS, true);
		Intent intent = new Intent(mContext,
				SpeedMainActivity.class);

		// userPhoneDBService.insertOrReplaceDetetion(new
		// UserPhone("13927079979", "我", ""));
		// JUtil.putSharePre(LoginActivity.this, APPConstant.SP_NAME,
		// APPConstant.SP_FIELD_CURRENT_PHONE,"13927079979");
		// UserAccount userAccount = new UserAccount("13927079979", "heheh",
		// "");
		// UserUtil.saveUserAccount(LoginActivity.this,userAccount);
		startActivity(intent);
		rightInAnimation();
		finish();
	}
	
	@OnClick(R.id.btn_login)
	public void fun_2(View v){
		if ("".equals(edtPhone.getText().toString())) {
			JUtil.showMsg(mContext, "请输入电话号码");
		} else {
			mProgressDialog = JUtil.showDialog(mContext,
					"正在获取验证码");
			// 发送验证码，发送成功就进入输入验证码界面
			AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
			String phone = edtPhone.getText().toString();
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
								bundle.putString(GM_ValidateVerificationCodeActivity.INTENT_PHONE, edtPhone.getText().toString());//手机号
								bundle.putString(GM_ValidateVerificationCodeActivity.INTENT_SOURCE_CLASS_NAME, FirstLoginActivity.class.getName());//添加当前类
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
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		
		bundle = getIntent().getExtras();
		rightBtn.setText("跳过");
		tvTittle.setText("登录");
		tvNavLeft.setVisibility(View.INVISIBLE);
		
		userPhoneDBService = new UserPhoneDBService(this);
		userPhones = userPhoneDBService.getAllUserPhones();
		if (userPhones != null && userPhones.size() > 0) {
			selectPhone(userPhones);
		}
		if(!ApplicationController.getInstance().isHasActiveOrLogin()){//没有激活or登陆
			Log.e("判断是否激活or登陆","LoginActivity 判断是否激活or登陆");
			new CommonFunction().activateHandle(rightBtn);
		}else{//已经登陆or激活，查看结果
			if(ApplicationController.getInstance().getCanSkip() == 1){//为1可跳过
				rightBtn.setEnabled(true);
			}else{
				rightBtn.setEnabled(false);
			}
		}
	}

	private void selectPhone(final List<UserPhone> userPhones) {
		if (userPhones == null || userPhones.size() == 0) {
			return;
		}
		String[] sArray = new String[userPhones.size()];// 昵称组
		for (int i = 0; i < userPhones.size(); i++) {
			sArray[i] = userPhones.get(i).getNickName();
		}
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
		alertBuilder.setTitle("请选择一个号码作为主账号:");
		alertBuilder.setItems(sArray,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// 获取当前电话
						String mPhone = userPhones.get(arg1).getPhoneStr();
						// 提示当前电话
						edtPhone.setText(mPhone);
					}
				});
		alertBuilder.show();
	}
}
