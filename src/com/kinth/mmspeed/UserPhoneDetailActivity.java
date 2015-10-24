package com.kinth.mmspeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 编辑号码、昵称、备注等
 */
@ContentView(R.layout.activity_edit_phones)
public class UserPhoneDetailActivity extends Activity {
	public static final String INTENT_PHONE = "phone";

	private Context mContext;
	private UserPhoneDBService userPhoneDBService;
	private String phoneStr;
	private UserPhone userPhone;
	private PopupWindow mPopupWindow;

	@ViewInject(R.id.et_nickName)
	private TextView et_nickName;

	@ViewInject(R.id.et_remark)
	private TextView et_remark;

	@ViewInject(R.id.et_phone)
	private TextView tv_phone;

	@ViewInject(R.id.btn_recharge)
	private Button recharge;// 充值

	@OnClick(R.id.btn_recharge)
	public void fun_1(View v) {
		View popupView = getLayoutInflater().inflate(
				R.layout.dialog_recharge_layout, null);
		LinearLayout popDialogBg = (LinearLayout) popupView
				.findViewById(R.id.pop_dialog_bg);
		Button rechargeOnline = (Button) popupView
				.findViewById(R.id.btn_recharge_online);
		Button rechargeCard = (Button) popupView
				.findViewById(R.id.btn_recharge_card);

		popDialogBg.setOnClickListener(listener);
		rechargeOnline.setOnClickListener(listener);
		rechargeCard.setOnClickListener(listener);

		mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		// Dialog dialog = new Dialog(mContext, R.style.recharge_dialog);
		// //设置它的ContentView
		// dialog.setContentView(R.layout.dialog_recharge_layout);
		// dialog.show();
	}

	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pop_dialog_bg:
				dismissPopupWindow();
				break;
			case R.id.btn_recharge_online:// 在线充值
				String urlStr = "http://wap.gd.10086.cn/nwap/recharge/recharge/recharge.jsps?mobile=";//网址
				Intent intent = new Intent(mContext,
						CommonWebViewActivity.class);
				intent.putExtra(CommonWebViewActivity.INTENT_TITLE, "在线充值");
				intent.putExtra(CommonWebViewActivity.INTENT_URL, urlStr + phoneStr);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				dismissPopupWindow();
				break;
			case R.id.btn_recharge_card:// 充值卡充值
				// 13800138000
				startActivity(new Intent("android.intent.action.CALL",
						Uri.parse("tel:" + "13800138000")));
				dismissPopupWindow();
				break;
			}
		}
	};

	private void dismissPopupWindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);

		phoneStr = getIntent().getStringExtra(INTENT_PHONE);
		userPhoneDBService = new UserPhoneDBService(
				UserPhoneDetailActivity.this);

		if (!"".equals(phoneStr)) {
			userPhone = userPhoneDBService.getUserPhoneByPhone(phoneStr);// 如果没有数据默认值为
																			// ""
			tv_phone.setText(phoneStr);
			et_nickName.setText(userPhone.getNickName());
			et_remark.setText(userPhone.getRemark());
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			UserPhoneDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void modifyNickNameOnClick(View v) {
		Intent intent = new Intent(UserPhoneDetailActivity.this,
				ModifyNickNameActivity.class);
		intent.putExtra("phone", phoneStr);
		intent.putExtra("name", et_nickName.getText().toString());

		startActivityForResult(intent,
				ModifyNickNameActivity.NickName_RequestCode);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public void modifyRemarkOnClick(View v) {
		Intent intent = new Intent(UserPhoneDetailActivity.this,
				ModifyRemarkActivity.class);
		intent.putExtra("phone", phoneStr);
		intent.putExtra("remark", et_remark.getText().toString());
		startActivityForResult(intent, ModifyRemarkActivity.Remark_RequestCode);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ModifyNickNameActivity.NickName_RequestCode:
			String nickName = data.getStringExtra("name");
			if (nickName != null && !nickName.equals("")) {
				et_nickName.setText(nickName);
			}
			break;
		case ModifyRemarkActivity.Remark_RequestCode:
			String remark = data.getStringExtra("remark");
			if (remark != null && !remark.equals("")) {
				et_remark.setText(remark);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void leftBtnOnClick(View v) {
		UserPhoneDetailActivity.this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
