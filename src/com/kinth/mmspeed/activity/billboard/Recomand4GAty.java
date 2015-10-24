package com.kinth.mmspeed.activity.billboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.AccountManageActivity;
import com.kinth.mmspeed.BaseActivity;
import com.kinth.mmspeed.FirstLoginActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.ContractLocalInfo;
import com.kinth.mmspeed.bj.SingleActivity;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author BJ
 * @version 2.0
 * @since 2014.7.15 推荐4G的Activity，短信发送，成功返回到界面
 */
@ContentView(R.layout.aty_recomand_4g)
public class Recomand4GAty extends BaseActivity {
	public static final String INTENT_PHONE = "INTENT_PHONE";
	public static final String INTENT_USERPHONE = "INTENT_USERPHONE";
	
	public static final String INTENT_RESULT_PHONE = "RESULT_PHONE";
	private static final int REQUEST_CODE = 7003;
	public static final String TAG = "Recomand4GAty";
	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private ProgressDialog mProgressDialog = null;
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	@ViewInject(R.id.nav_right)
	private ImageButton btnRight;
	@ViewInject(R.id.edt_content)
	private EditText edtShareContent;
	@ViewInject(R.id.edt_phone)
	private EditText edtPhone;
	private String intentPhone ;
	private String userPhone ;
	private DbUtils mDbUtils;
	private 
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.arg1) {
			case 1:
				AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
				asyncNetworkManager.recomand4G(Recomand4GAty.this, userPhone, intentPhone, new AsyncNetworkManager.IRecomand4G() {
					@Override
					public void IRecomand4GCallBack(int rtn) {
						JUtil.dismissDialog(mProgressDialog);
						if (rtn==1) {
							List<ContractInfo>mContractInfos = new ArrayList<ContractInfo>();
							try {
								mContractInfos = mDbUtils.findAll(Selector.from(ContractInfo.class).where("phoneNumber", "=", intentPhone));
							} catch (DbException e) {
								e.printStackTrace();
							}
							if (mContractInfos!=null&&mContractInfos.size()>0) {
								ContractInfo contractInfo = mContractInfos.get(0);
								contractInfo.setState(UserFriendState.RECOMAND);
								try {
									mDbUtils.update(contractInfo);
								} catch (DbException e) {
									e.printStackTrace();
								}
							}
							JUtil.showMsg(Recomand4GAty.this, "推荐成功");
							finish();
							rightOutFinishAnimation();
						}else if(rtn==0){
							JUtil.showMsg(Recomand4GAty.this, "推荐失败");
						}else if (rtn==-1) {
							JUtil.showMsg(Recomand4GAty.this, "已经推荐过");
						}
					}
				});
				
				break;
			case 0:
				JUtil.showMsg(Recomand4GAty.this, "分享失败");
				break;

			default:
				break;
			}
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mDbUtils = DbUtils.create(this,APPConstant.DATABASE_NAME);
		intentPhone = getIntent().getStringExtra(INTENT_PHONE);
		userPhone =  getIntent().getStringExtra(INTENT_USERPHONE);
		edtPhone.setText(intentPhone);
		edtPhone.setSelection(intentPhone.length());
		btnRight.setVisibility(View.INVISIBLE);
		tvTittle.setText("4G推荐");
		registerReceiver(receiver, new IntentFilter(SENT_SMS_ACTION));
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	public void shareOnClick(View v) {
		if (!"".equals( edtShareContent.getText().toString())&&!"".equals(edtPhone.getText().toString())) {
			String content = edtShareContent.getText().toString();
			int length = content.length();
			sendSMS(edtPhone.getText().toString(), edtShareContent.getText().toString().trim(), 1l);
			mProgressDialog = JUtil.showDialog(Recomand4GAty.this,"正在推荐...");
		}else if("".equals( edtShareContent.getText().toString())){
			JUtil.showMsg(Recomand4GAty.this, "短信内容不能为空！");
		}else if("".equals( edtPhone.getText().toString())){
			JUtil.showMsg(Recomand4GAty.this, "电话不能为空！");
		}
		
	}
	public void choiceContractOnClick(View v) {
		Intent intent = new Intent(Recomand4GAty.this,SingleActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		rightInAnimation();
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("SENT_SMS_ACTION")) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					String code= intent.getStringExtra("SEND_SMS_CODE");
					String sum= intent.getStringExtra("SEND_SMS_NUM");
					String content= intent.getStringExtra("SEND_SMS_CONTENT");
//					Log.i(TAG,
//							"短信发送成功！------>编号=【"
//									+ intent.getStringExtra("SEND_SMS_CODE")
//									+ "】<------>号码=【"
//									+ intent.getStringExtra("SEND_SMS_NUM")
//									+ "】<------>内容=【"
//									+ intent.getStringExtra("SEND_SMS_CONTENT")
//									+ "】");
					Message message = handler.obtainMessage();
					message.arg1 =1;
					handler.sendMessage(message);
					break;
				default:
					Message message1 = handler.obtainMessage();
					message1.arg1 = 0;
					handler.sendMessage(message1);
					break;
				}
			}
		}
	};

	private void sendSMS(String toAddress, String body, Long id) {
		// ---sends an SMS message to another device---
		SmsManager sms = SmsManager.getDefault();
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		// create the sentIntent parameter
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		sentIntent.putExtra("id", id);
		PendingIntent sentPI = PendingIntent.getBroadcast(
		Recomand4GAty.this, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 如果短信内容超过70个字符 将这条短信拆成多条短信发送出去
		System.out.println("body====" + body);
		sms.sendTextMessage(toAddress, null, body, sentPI, null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != REQUEST_CODE) {// 请求码不同，直接返回
			return;
		}
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			// String nickName = data.getStringExtra(INTENT_RESULT_NICK_NAME);
			String phone = data.getStringExtra(INTENT_RESULT_PHONE);
			if (TextUtils.isEmpty(phone)) {
				Log.e("选取号码失败", "选取号码失败");
				return;
			}
			edtPhone.setText(phone);
			break;
		default:
			break;
		}
	}
}
