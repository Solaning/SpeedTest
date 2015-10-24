package com.kinth.mmspeed;

import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bj.SingleActivity;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.GetMemberMaxCountCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.InviteMemberCallBack;
import com.kinth.mmspeed.ui.CommomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 邀请加入流量共享
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_rmg_invite_member_layout)
public class RMGInviteMemberActivity extends BaseActivity {
	public static final String INTENT_RESULT_NICK_NAME = "RESULT_NICK_NAME";
	public static final String INTENT_RESULT_PHONE = "RESULT_PHONE";
	public static final String INTENT_PHONE = "PHONE";
	private static final int REQUEST_CODE = 7003;

	private Context mContext;
	private String currentPhone;
	private RichManGroup richManGroup;

	@ViewInject(R.id.iv_back)
	private ImageView back;

	@ViewInject(R.id.et_input_phone_number)
	private EditText inputPhoneNumber;

	@ViewInject(R.id.ib_select_contact)
	private ImageButton selectContact;

	@ViewInject(R.id.tv_receive_sms_from_phone)
	private TextView receiveSMSFromPhone;// 接收邀请短信的

	@ViewInject(R.id.btn_confirm)
	private Button confirm;

	@OnClick(R.id.iv_back)
	public void fun_1(View v) {// 返回
		finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.ib_select_contact)
	public void fun_2(View v) {// 选择通讯录
		Intent intent = new Intent(mContext, SingleActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		rightInAnimation();
	}

	@OnClick(R.id.btn_confirm)
	public void fun_3(View v) {// 发送请求
		final String inviteNumberStr = inputPhoneNumber.getText().toString();
		if (TextUtils.isEmpty(inviteNumberStr)) {
			return;
		}
		CustomProgressDialogUtil.startProgressDialog(mContext, "正在邀请成员...",true);
		// 获取群组网最大成员数目
		new AsyncNetworkManager().getMemberMaxCount(mContext,APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(),
				new GetMemberMaxCountCallBack() {

					@Override
					public void onGetMemberMaxCountCallBack(int rtn,
							int maxnumber) {
						if (rtn == 1) {
							SingletonSharedPreferences.getInstance()
									.setRMGMemberMaxCount(maxnumber);
						} else {
							maxnumber = SingletonSharedPreferences
									.getInstance().getRMGMemberMaxCount();
						}
						if (maxnumber < 0) {
							CustomProgressDialogUtil.stopProgressDialog();
							Toast.makeText(mContext, "邀请失败", Toast.LENGTH_SHORT)
									.show();
							return;
						}
						if (richManGroup.getMembercount() < maxnumber) {
							// 邀请成员
							new AsyncNetworkManager().inviteMember(mContext,APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(),
									currentPhone,
									APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue(),//TODO
									inviteNumberStr,// TODO 要邀请的号码
									new InviteMemberCallBack() {

										@Override
										public void onInviteMemberCallBack(
												int rtn,String error) {
											CustomProgressDialogUtil
													.stopProgressDialog();
											switch (rtn) {
											case 1:
												Toast.makeText(mContext,
														"邀请短信发送成功",
														Toast.LENGTH_SHORT)
														.show();
												break;
											case 0:
											default:
												CommomDialog.Builder dialog = new CommomDialog.Builder(
														mContext);
												dialog.setTitle("提示");
												dialog.setMessage(error);
												dialog.setPositiveButton("确认",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(DialogInterface dialog,
																	int which) {
															}
												});
												dialog.create().show();
												break;

											}
										}
									});
						} else {
							CustomProgressDialogUtil.stopProgressDialog();
							Toast.makeText(mContext, "超过群成员人数限制",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		currentPhone = getIntent().getStringExtra(INTENT_PHONE);
		if (TextUtils.isEmpty(currentPhone)) {
			return;
		}
		richManGroup = SingletonData.getInstance().getRichManGroupInfo();
		receiveSMSFromPhone.setText("将会收到" + currentPhone + "的“流量共享群组”邀请短信!");
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
			inputPhoneNumber.setText(phone);
			break;
		default:
			break;
		}
	}
}
