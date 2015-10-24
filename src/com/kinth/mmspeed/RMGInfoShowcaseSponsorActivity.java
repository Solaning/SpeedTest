package com.kinth.mmspeed;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.adapter.RMGMemberAdapter;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.CancelRichManGroupCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.ChgRichManMemberCallBack;
import com.kinth.mmspeed.ui.CommomDialog;
import com.kinth.mmspeed.ui.TextProgressBar;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 群信息展示页面，针对的是发起者
 * 
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_rmg_info_showcase)
public class RMGInfoShowcaseSponsorActivity extends BaseActivity {
	public static final String INTENT_SERVICE_ID = "INTENT_SERVICE_ID";
	public static final String INTENT_PHONE = "CURRENT_PHONE";

	private Context mContext;
	private RMGMemberAdapter adapter;
	private RichManGroup richManGroup;// 共享组的数据
	private String currentPhone;// 群主号
	private PopupWindow morePopupWindow;// 更多弹出窗口
	private String selectProd;// 变更套餐时的选择，默认套餐
	private long realDrawvalue = 0;
	private String currentProd;// 用户当前的套餐

	private Button changePackage;// 变更套餐
	private Button relieveGroup;// 解散群组
	private Button inviteMember;// 邀请成员

	private View headerView;// listView的header
	private TextView sponsorName;// 参与者号码
	private TextView groupNumber;// 群成员数目
	private TextProgressBar progressBarWithText;// 剩余群流量progressBar
	private TextView totalShareFlow;// 共享流量总量
	private TextView packageThisMonth;// 本月套餐

	@ViewInject(R.id.iv_back)
	private ImageView back;// 返回

	@ViewInject(R.id.tv_title)
	private TextView title;
	
	@ViewInject(R.id.iv_options)
	private ImageView options;// 设置

	@ViewInject(R.id.listview_group_member)
	private ListView listView;

	@OnClick(R.id.iv_back)
	public void fun_1(View v) {// 返回
		finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.iv_options)
	public void fun_2(View v) {// 设置操作
		if (morePopupWindow == null) {
			// 引入窗口配置文件
			View view = LayoutInflater.from(this).inflate(
					R.layout.popupwindow_rmg_group_info_sponsor, null);
			changePackage = (Button) view
					.findViewById(R.id.btn_sponsor_change_package);
			relieveGroup = (Button) view
					.findViewById(R.id.btn_sponsor_relieve_group);
			inviteMember = (Button) view
					.findViewById(R.id.btn_sponsor_invite_member);
			changePackage.setOnClickListener(listener);
			relieveGroup.setOnClickListener(listener);
			inviteMember.setOnClickListener(listener);

			// 创建PopupWindow对象
			morePopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, false);
			// 需要设置一下此参数，点击外边可消失
			morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			morePopupWindow.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			morePopupWindow.setFocusable(true);
		}
		// 显示窗口
		morePopupWindow.showAsDropDown(v);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		title.setText("流量共享");

		currentPhone = getIntent().getStringExtra(INTENT_PHONE);// 号码
		richManGroup = SingletonData.getInstance().getRichManGroupInfo();
		if (richManGroup == null || TextUtils.isEmpty(currentPhone)) {// 没有数据
			return;
		}

		headerView = LayoutInflater.from(mContext).inflate(
				R.layout.header_of_rmg_listview, null);
		sponsorName = (TextView) headerView.findViewById(R.id.tv_participant_name);
		groupNumber = (TextView) headerView
				.findViewById(R.id.tv_sponsor_group_number);
		progressBarWithText = (TextProgressBar) headerView
				.findViewById(R.id.progressBarWithText);
		totalShareFlow = (TextView) headerView
				.findViewById(R.id.tv_total_share_flow);
		packageThisMonth = (TextView) headerView
				.findViewById(R.id.tv_package_this_month);
		listView.addHeaderView(headerView);

		initView(richManGroup);

		adapter = new RMGMemberAdapter(mContext,ServiceID.DO_RICH_MAN_TOGETHER,
				richManGroup.getRichmanmembers(), currentPhone, realDrawvalue);
		listView.setAdapter(adapter);

	}

	private void initView(RichManGroup rmgInfo) {
		if (rmgInfo.getNextmonthstate()) {
			sponsorName.setText("您是流量共享群主");
		} else {
			options.setVisibility(View.GONE);
			sponsorName
					.setText("您是流量共享群主，群组已解散，下个自然月1日生效。流量共享套餐取消生效前，套餐内剩余流量（含私有和共享）仍可使用。解散当月不能再次办理流量共享业务。");
		}
		groupNumber.setText(UtilFunc.turnStringToRedSpannable(mContext,
				"群成员共有", rmgInfo.getMembercount() + "", "人"));

		realDrawvalue = rmgInfo.getLeftamt();// 真实的总流量 = 剩余的流量 + 每个成员使用的流量
		for (RichManMember member : rmgInfo.getRichmanmembers()) {// 遍历查找
			realDrawvalue += member.getUsedlimit();
			if (member.getMobilenumber().equals(currentPhone)) {// 找到当前号码
				if (member.getProdid().equals(APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue())) {// 当前号码套餐
					currentProd = APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue();
					packageThisMonth.setText("本月套餐10元100M");
				} else if (member.getProdid().equals(
						APPConstant.ProdID.RMG_PACKAGE_TYPE_2.getValue())) {
					currentProd = APPConstant.ProdID.RMG_PACKAGE_TYPE_2.getValue();
					packageThisMonth.setText("本月套餐20元300M");
				} else {
					packageThisMonth.setText("未知套餐种类");
				}
			}
		}

		progressBarWithText.setMax((int) (realDrawvalue/1048576));// 限额
		progressBarWithText.setProgress(rmgInfo.getLeftamt()/1048576);// 剩余
		progressBarWithText.setText(String.format("%.2f", rmgInfo.getLeftamt() / 1048576.0f) + "MB");// byte转成M
		// 1024*1024 = 1048576
		totalShareFlow.setText("共享流量总量为" + String.format("%.2f",realDrawvalue / 1048576.0f) + "MB");
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (morePopupWindow == null)
				return;
			if (morePopupWindow.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
				morePopupWindow.dismiss();
			}
			if(!richManGroup.getNextmonthstate()){//如果群组已经解散，提示返回
				final CommomDialog.Builder dialog = new CommomDialog.Builder(
						mContext);
				dialog.setTitle("提示");
				dialog.setMessage("尊敬的用户，该群组已经解散，无法执行该操作。");
				dialog.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
				});
				dialog.create().show();
				return;
			}
			switch (v.getId()) {
			case R.id.btn_sponsor_change_package:// 变更套餐
				final CommomDialog.Builder myDialog = new CommomDialog.Builder(
						mContext);
				myDialog.setTitle("变更套餐");
				// myDialog.setMessage("message");
				// 可以复用中间的content页面
				LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(
						R.layout.dialog_content_change_rmg_package, null);
				TextView changePackagePhone = (TextView) view
						.findViewById(R.id.tv_change_rmg_package_phone);
				changePackagePhone.setText(currentPhone);
				final TextView packageDescribe = (TextView) view
						.findViewById(R.id.tv_describe_of_rmg_package);
				packageDescribe.setText("您是否确认变更为10元100M共享流量套餐，变更套餐下个自然月生效？");
				selectProd = APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue();
				RadioGroup radioGroup = (RadioGroup) view
						.findViewById(R.id.radiogroup);
				radioGroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								switch (checkedId) {
								case R.id.radiobutton1:
									selectProd = APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue();
									packageDescribe
											.setText("您是否确认变更为10元100M共享流量套餐，变更套餐下个自然月生效？");
									break;
								case R.id.radiobutton2:
									selectProd = APPConstant.ProdID.RMG_PACKAGE_TYPE_2.getValue();
									packageDescribe
											.setText("您是否确认变更为20元300M共享流量套餐，变更套餐下个自然月生效？");
									break;
								}
							}
						});
				myDialog.setView(view);
				myDialog.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (selectProd.equals(currentProd)) {
									Toast.makeText(mContext,
											"当前套餐与所选套餐一致，无需变更",
											Toast.LENGTH_LONG).show();
									return;
								}
								Intent intent = new Intent(mContext,
										RMGGetVerificationCodeActivity.class);
								intent.putExtra(
										RMGGetVerificationCodeActivity.INTENT_PHONE,
										currentPhone);
								intent.putExtra(
										RMGGetVerificationCodeActivity.INTENT_TARGET_CLASS_NAME,
										RMGInfoShowcaseSponsorActivity.class.getName());
								startActivityForResult(
										intent,
										RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE);
								overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
							}
						});
				myDialog.setNegativeButton("返回",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				myDialog.create().show();
				break;
			case R.id.btn_sponsor_relieve_group:// 解散群组，取消群组
				final CommomDialog.Builder myDialog2 = new CommomDialog.Builder(
						mContext);
				myDialog2.setTitle("解散群组");
				myDialog2.setMessage("尊敬的用户，群组解散将在下个自然月1号生效。");
				myDialog2.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								CustomProgressDialogUtil.startProgressDialog(
										mContext, "正在解散群组...",true);
								// 取消群组
								new AsyncNetworkManager().cancelRichManGroup(
										mContext,APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(), currentPhone,
										new CancelRichManGroupCallBack() {

											@Override
											public void onCancelRichManGroupCallBack(
													int rtn,String error) {
												CustomProgressDialogUtil
														.stopProgressDialog();
												switch (rtn) {
												case 1:
													Toast.makeText(mContext,
															"解散群组成功",
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
							}
						});
				myDialog2.setNegativeButton("返回",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				myDialog2.create().show();
				break;
			case R.id.btn_sponsor_invite_member:// 邀请成员
				Intent intent = new Intent(mContext,
						RMGInviteMemberActivity.class);
				intent.putExtra(RMGInviteMemberActivity.INTENT_PHONE,
						currentPhone);
				startActivity(intent);
				rightInAnimation();
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE) {// 请求码不同，直接返回
			return;
		}
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			String codeStr = data
					.getStringExtra(RMGGetVerificationCodeActivity.INTENT_CODE);
			if (TextUtils.isEmpty(codeStr)) {
				Log.e("回传的验证码为空", "回传的验证码为空");
				Toast.makeText(mContext, "未知错误，请重试", Toast.LENGTH_LONG).show();
				return;
			}

			CustomProgressDialogUtil.startProgressDialog(mContext, "正在变更套餐...",true);
			new AsyncNetworkManager().chgRichManMember(mContext,APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(), currentPhone, 3,
					selectProd,null, currentPhone, codeStr,
					new ChgRichManMemberCallBack() {

						@Override
						public void onChgRichManMemberCallBack(int rtn,String error) {
							CustomProgressDialogUtil.stopProgressDialog();
							CommomDialog.Builder dialog = new CommomDialog.Builder(
									mContext);
							dialog.setTitle("提示");
							dialog.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
							});
							switch (rtn) {
							case 1:
								Toast.makeText(mContext, "变更套餐成功",
										Toast.LENGTH_LONG).show();
								dialog = null;
								return;
							case -1:
								dialog.setMessage("短信验证码失败或过期");
								break;
							case 0:
							default:
								dialog.setMessage(error);
								break;
							}
							dialog.create().show();
						}
					});
			break;
		default:
			break;
		}
	}
}
