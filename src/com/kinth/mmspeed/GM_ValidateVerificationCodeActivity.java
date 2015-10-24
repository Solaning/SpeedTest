package com.kinth.mmspeed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 通用--验证验证码页面
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_validate_verification_code)
public class GM_ValidateVerificationCodeActivity extends BaseActivity {
	public static final String INTENT_PHONE = APPConstant.INTENT_PHONE;
	public static final String INTENT_TARGET_CLASS_NAME = APPConstant.INTENT_TARGET_CLASS_NAME;; //最终需要放到intent中跳转的activity
	public static final String INTENT_SOURCE_CLASS_NAME = APPConstant.INTENT_SOURCE_CLASS_NAME; //来源的activity，用于后退时跳转
	
	public static final String ACTION_FIRST_BIND = "ACTION_BIND";
	public static final String BIND_MOBILE = "BIND_MOBILE";
	
	private Context mContext;
	private String targetClassName; //需要跳转的Activity名称
	private String sourceClassName; //跳转过来的Activity名称，用于返回时跳转
	private String phoneString;
	private ProgressDialog mProgressDialog = null;
	private UserPhoneDBService userPhoneDBService;
	private Bundle bundle;
	
	//add @2014-10-09
	private BroadcastReceiver smsReceiver;//监听短信
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	
	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message arg0) {
			edtCode.setText((CharSequence) arg0.obj);
			return false;
		}
	});
	
	@ViewInject(R.id.edt_code)
	private EditText edtCode;//验证码
	
	@ViewInject(R.id.btn_next)
	private Button btnNext;//下一步
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;//返回
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;//标题
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		back();
	}
	
	@OnClick(R.id.btn_next)
	public void fun_2(View v){
		if (TextUtils.isEmpty(edtCode.getText().toString())) {
			JUtil.showMsg(mContext,
					"请输入验证码！");
		} else {
			mProgressDialog = JUtil.showDialog(mContext, "正在验证...");
			AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
			asyncNetworkManager.login(mContext, phoneString,
					edtCode.getText().toString(),
					new AsyncNetworkManager.LoginMethod() {
						@Override
						public void loginCallBack(int rtn, UserAccount user) {
							JUtil.dismissDialog(mProgressDialog);
							/**
							 * 1 登陆成功 0 验证码错误
							 */
							if (rtn == 1 && user != null
									&& !user.getMobile().equals("")) {
								MainAccountUtil.saveUserAccount(mContext, user);
								UserPhone userPhone = userPhoneDBService
										.getUserPhoneByPhone(phoneString);

								if (userPhone == null
										|| !userPhone.getPhoneStr().equals(
												phoneString)) {
									userPhoneDBService
											.insertOrReplaceDetetion(new UserPhone(
													phoneString, phoneString,
													""));
									JUtil.putSharePre(mContext,	APPConstant.SP_NAME,
											APPConstant.SP_FIELD_CURRENT_PHONE,
											phoneString);
									ifFirstBind();//TODO
									userPhoneChange(phoneString);//TODO
								}
								MainAccountUtil.setHaveRegister(mContext);

								Class<?> clazz = UtilFunc.loadClass(mContext, targetClassName);
								if(clazz != null){
									Intent intent = new Intent(mContext,clazz);
									intent.putExtras(bundle);
									startActivity(intent);
								}
								finish();
								rightOutFinishAnimation();
							} else if (rtn == 0) {
								JUtil.showMsg(mContext, "验证码错误");
							} else {
								JUtil.showMsg(mContext, "验证失败");
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
		
		userPhoneDBService = new UserPhoneDBService(mContext);
		userPhoneDBService.createTable();
		bundle = getIntent().getExtras();
		phoneString = bundle.getString(INTENT_PHONE);//号码
		targetClassName = bundle.getString(INTENT_TARGET_CLASS_NAME);//目标activity
		sourceClassName = bundle.getString(INTENT_SOURCE_CLASS_NAME);//来源activity
		tvTittle.setText("验证");
		
		IntentFilter filter = new IntentFilter();
		//设置短信拦截参数
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object[] objs = (Object[]) intent.getExtras().get("pdus");
				for (Object obj : objs) {
					byte[] pdu = (byte[]) obj;
					SmsMessage sms = SmsMessage.createFromPdu(pdu);
					String message = sms.getMessageBody();
					String from = sms.getOriginatingAddress();
					if (!TextUtils.isEmpty(from) && from.equals(APPConstant.SMS_NUMBER)) {
						String code = patternCode(message);
						if (!TextUtils.isEmpty(code)) {
							Message msg=handler.obtainMessage();
							msg.obj = code;
							handler.sendMessage(msg);
						}
					}
				}
			}
		};
		registerReceiver(smsReceiver, filter);
	}

	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(smsReceiver);
		super.onDestroy();
	}
	
	/**
	 * 第一次绑定号码，切换到主界面，要刷新
	 */
	private void ifFirstBind() {
		if ("".equals(JUtil.getSharePreStr(mContext, APPConstant.SP_NAME,
				APPConstant.SP_FIELD_CURRENT_PHONE))) {
			JUtil.putSharePre(mContext,
					APPConstant.SP_NAME, APPConstant.SP_IF_FIRST_BIND, 1);
			Intent intent = new Intent();
			intent.setAction("ACTION_BIND");
			sendBroadcast(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
		}
		return false;
	}
	
	/**
	 * 返回
	 */
	private void back(){
		Class<?> clazz = UtilFunc.loadClass(mContext, sourceClassName);
		if(clazz != null){
			Intent intent = new Intent(mContext, clazz);
			intent.putExtras(bundle);
			startActivity(intent);
		}
		rightOutFinishAnimation();
		finish();
	}
	
	private void userPhoneChange(String phone) {
		//当前号码为空，就发广播
		if ("".equals(JUtil.getSharePreStr(mContext,
				APPConstant.SP_NAME,
				APPConstant.SP_FIELD_CURRENT_PHONE))) {
		
			JUtil.putSharePre(mContext,APPConstant.SP_NAME,APPConstant.SP_IF_FIRST_BIND,1);
			JUtil.putSharePre(mContext,
					APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE,phone);
			Intent intent = new Intent();
			intent.setAction(ACTION_FIRST_BIND);
			sendBroadcast(intent);
		}else {
			Intent intent = new Intent();
			intent.setAction(ACTION_FIRST_BIND);
			intent.putExtra(BIND_MOBILE, phone);
			sendBroadcast(intent);
		}
	}
}
