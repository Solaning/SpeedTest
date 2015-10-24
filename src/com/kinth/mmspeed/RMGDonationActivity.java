package com.kinth.mmspeed;

import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bj.SingleActivity;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.ChgRichManMemberCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.GetMemberMaxCountCallBack;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 给好友转赠流量
 * 
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_rmg_donation_layout)
public class RMGDonationActivity extends BaseActivity {
	public static final String INTENT_RESULT_PHONE = "RESULT_PHONE";
	public static final String INTENT_PHONE = "PHONE";
	private static final int REQUEST_CODE = 7004;

	private Context mContext;
	private String currentPhone;
	private RichManGroup richManGroup;
	private String inviteNumberStr;// 要邀请的手机号
	private String selectedLimit = "50";// 选择的限额，默认是 50M

	@ViewInject(R.id.iv_back)
	private ImageView back;

	@ViewInject(R.id.et_input_phone_number)
	private EditText inputPhoneNumber;

	@ViewInject(R.id.ib_select_contact)
	private ImageButton selectContact;

	@ViewInject(R.id.sp_select_flow_limit)
	private Spinner flowLimitSpinner;// 套餐限额

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
		inviteNumberStr = inputPhoneNumber.getText().toString();
		if (TextUtils.isEmpty(inviteNumberStr)) {
			Toast.makeText(mContext, "请输入要加入流量共享的群号", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(mContext,
				RMGGetVerificationCodeActivity.class);
		intent.putExtra(RMGGetVerificationCodeActivity.INTENT_PHONE,
				currentPhone);
		intent.putExtra(RMGGetVerificationCodeActivity.INTENT_TARGET_CLASS_NAME,
				RMGDonationActivity.class.getName());
		startActivityForResult(intent,
				RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE);
		rightInAnimation();
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
		receiveSMSFromPhone.setText("将会收到土豪" + currentPhone + "的“流量转赠群组”邀请短信!");

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spinner_donation_limit,
				R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		flowLimitSpinner.setAdapter(adapter);
		flowLimitSpinner
				.setOnItemSelectedListener(new SpinnerOnSelectedListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			switch (requestCode) {
			case REQUEST_CODE:
				// String nickName =
				// data.getStringExtra(INTENT_RESULT_NICK_NAME);
				String phone = data.getStringExtra(INTENT_RESULT_PHONE);
				if (TextUtils.isEmpty(phone)) {
					Toast.makeText(mContext, "选取号码失败，请稍后重试", Toast.LENGTH_LONG)
							.show();
					return;
				}
				inputPhoneNumber.setText(phone);
				break;
			case RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE:// 验证码返回
				final String codeStr = data
						.getStringExtra(RMGGetVerificationCodeActivity.INTENT_CODE);
				if (TextUtils.isEmpty(codeStr)) {
					Log.e("回传的验证码为空", "回传的验证码为空");
					Toast.makeText(mContext, "未解析出验证码，请重试", Toast.LENGTH_LONG)
							.show();
					return;
				}
				CustomProgressDialogUtil.startProgressDialog(mContext,
						"正在邀请成员...",true);
				// 获取群组网最大成员数目
				new AsyncNetworkManager().getMemberMaxCount(mContext,
						APPConstant.ServiceID.I_AM_RICH_MAN.getValue(),
						new GetMemberMaxCountCallBack() {

							@Override
							public void onGetMemberMaxCountCallBack(int rtn,
									int maxnumber) {
								Log.e("maxnumber", "" + maxnumber);

								if (rtn == 1) {
									SingletonSharedPreferences.getInstance()
											.setRMGDonationMemberMaxCount(
													maxnumber);
								} else {
									maxnumber = SingletonSharedPreferences
											.getInstance()
											.getRMGDonationMemberMaxCount();
								}
								if (maxnumber < 0) {
									CustomProgressDialogUtil
											.stopProgressDialog();
									Toast.makeText(mContext, "邀请失败",
											Toast.LENGTH_SHORT).show();
									return;
								}
								if (richManGroup.getMembercount() < maxnumber) {
									// 邀请成员
									new AsyncNetworkManager()
											.chgRichManMember(
													mContext,
													APPConstant.ServiceID.I_AM_RICH_MAN
															.getValue(),
													currentPhone,
													1,
													APPConstant.ProdID.PACKAGE_TYPE_DONATION
															.getValue(),selectedLimit,
													inviteNumberStr,// 要邀请的号码
													codeStr,
													new ChgRichManMemberCallBack() {

														@Override
														public void onChgRichManMemberCallBack(
																int rtn,String error) {
															CustomProgressDialogUtil
																	.stopProgressDialog();
															switch (rtn) {
															case 1:
																Toast.makeText(
																		mContext,
																		"邀请成功",
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
									CustomProgressDialogUtil
											.stopProgressDialog();
									Toast.makeText(mContext, "超过群成员人数限制",
											Toast.LENGTH_SHORT).show();
								}

							}
						});
				break;
			}

		default:
			break;
		}
	}

	/**
	 * 这个监听器主要用来监听用户选择列表的动作
	 */
	class SpinnerOnSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0:
				selectedLimit = "50";
				break;
			case 1:
				selectedLimit = "100";
				break;
			case 2:
				selectedLimit = "200";
				break;
			case 3:
				selectedLimit = "-1";
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}
	}
}
