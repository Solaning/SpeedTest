package com.kinth.mmspeed;

import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.VerificationCodeCallBack;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * RMG获取验证码模块
 * @author Sola
 *
 */
@ContentView(R.layout.activity_common_get_verification_code)
public class RMGGetVerificationCodeActivity extends BaseActivity{
	public static final String INTENT_TARGET_CLASS_NAME = "INTENT_TARGET_CLASS_NAME"; //最终需要放到intent中跳转的activity 
	public static final String INTENT_PHONE = "PHONE";
	public static final String INTENT_CODE = "CODE";
	public static final int INTENT_COMMON_REQUEST_CODE = 9003;//通用的请求码 
	
	private Context mContext;
	private String phoneStr;
	private TimeCount time;
	private String targetClassName;//需要跳转的activity全名
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;//返回
	
	@ViewInject(R.id.nav_tittle)
	private TextView title;
	
	@ViewInject(R.id.btn_getCode)
	private Button getCode;//获取验证码
	
	@ViewInject(R.id.et_input_verification_code)
	private EditText inputVerificationCode;//输入验证码
	
	@ViewInject(R.id.btn_confirm_code)
	private Button confirmCode;//确认验证码按钮
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){//返回
		finish();
		rightOutFinishAnimation();
	}
	
	@OnClick(R.id.btn_getCode)
	public void fun_2(View v){//获取验证码
		CustomProgressDialogUtil.startProgressDialog(mContext, "正在获取验证码...",true);
		new AsyncNetworkManager().getSMSVerifyCodeCommon(mContext,phoneStr,null,new VerificationCodeCallBack() {
			
			@Override
			public void getVerificationCodeCallBack(int rtn) {
				CustomProgressDialogUtil.stopProgressDialog();
				switch(rtn){
				case -1:
					Toast.makeText(mContext,
							"获取验证码失败\n可能是由网络链接问题引起。",
							Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(mContext,
							"90秒内不能重复发送请求", Toast.LENGTH_SHORT)
							.show();
					break;
				case 1:
					time.start();// 开始计时
					Toast.makeText(mContext,
							"验证码已发送到手机："+phoneStr+"，请留意查收。", Toast.LENGTH_SHORT)
							.show();
					break;
				case 2:// mifi
					//TODO 
					break;
				}
			}
		});
	}
	
	@OnClick(R.id.btn_confirm_code)
	public void fun_3(View v){//确认按钮后返回验证码
		String verificationCode = inputVerificationCode.getText().toString();
		if(TextUtils.isEmpty(verificationCode)){
			Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG).show();
			return;
		}
		// 用 setResut() 准备好要回传的数据后，只要使用finish()的方法就能把打包好的数据发给A且运行onActivityResult()部分的代码
		Class<?> clazz = UtilFunc.loadClass(mContext, targetClassName);
		if(clazz != null){
			Intent intent = new Intent(mContext,clazz);
			startActivity(intent);
			intent.putExtra(INTENT_CODE, verificationCode);
			setResult(RESULT_OK, intent);
		}
		finish();
		rightOutFinishAnimation(); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		
		title.setText("获取验证码");
		phoneStr = getIntent().getStringExtra(INTENT_PHONE);
		targetClassName = getIntent().getStringExtra(INTENT_TARGET_CLASS_NAME);
		if(TextUtils.isEmpty(phoneStr)){
			return;//TODO
		}
		time = new TimeCount(90000, 1000);
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			getCode.setText("重新验证");
			getCode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			getCode.setClickable(false);
			getCode.setText(millisUntilFinished / 1000 + "秒");
		}
	}
}
