package com.kinth.mmspeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 通用--登陆，绑定主账号
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_gm_login)
public class GM_LoginActivity extends BaseActivity {
	public static final String INTENT_TARGET_CLASS_NAME = APPConstant.INTENT_TARGET_CLASS_NAME;//最终需要放到intent中跳转的activity
	
	private Context mContext;
	private ProgressDialog mProgressDialog = null;
	private Bundle bundle;
	
	@ViewInject(R.id.edt_phone)
	private EditText edtPhone; //账号
	
	@ViewInject(R.id.nav_left)
	private ImageButton tvNavLeft;
	
	@ViewInject(R.id.btn_login)
	private Button btnLogin;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}
	
	@OnClick(R.id.btn_login)
	public void fun_2(View v){
		if (TextUtils.isEmpty(edtPhone.getText().toString())) {
			JUtil.showMsg(mContext,"请输入电话号码");
		}else {
			mProgressDialog = JUtil.showDialog(mContext,"正在获取验证码");
			//发送验证码，发送成功就进入输入验证码界面
			AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
			String phone = edtPhone.getText().toString();
			asyncNetworkManager.getSMSCode(mContext, phone, new AsyncNetworkManager.GetSMSCode() {
				@Override
				public void getSMSCallBack(int rtn) {
					JUtil.dismissDialog(mProgressDialog);
					if (rtn==1) {
						Intent intent = new Intent(mContext, GM_ValidateVerificationCodeActivity.class);
						if(bundle == null){
							bundle = new Bundle();
						}
						bundle.putString(GM_ValidateVerificationCodeActivity.INTENT_PHONE, edtPhone.getText().toString());//手机号
						bundle.putString(GM_ValidateVerificationCodeActivity.INTENT_SOURCE_CLASS_NAME, GM_LoginActivity.class.getName());//添加当前类
						intent.putExtras(bundle);
						startActivity(intent);
						rightInAnimation();
						finish();
					}else if (rtn==-2) {
						JUtil.showMsg(mContext, "获取验证码失败,请检查您的网络连接！");
					}else if (rtn==-3) {
						JUtil.showMsg(mContext, "获取验证码失败超时,请稍后重试！");
					}else {
						JUtil.showMsg(mContext, "获取验证码失败,请重新获取！");
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
		tvTittle.setText("登录");
	}
}
