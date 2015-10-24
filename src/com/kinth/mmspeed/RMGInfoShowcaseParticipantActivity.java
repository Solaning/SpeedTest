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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.kinth.mmspeed.adapter.RMGMemberAdapter;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.ChgRichManMemberCallBack;
import com.kinth.mmspeed.ui.CommomDialog;
import com.kinth.mmspeed.ui.TextProgressBar;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 群信息展示页面，针对的是参与者和获赠流量者
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_rmg_info_showcase)
public class RMGInfoShowcaseParticipantActivity extends BaseActivity {
	public static final String INTENT_SERVICE_ID = "INTENT_SERVICE_ID";// 区分是流量共享还是流量转赠
	public static final String INTENT_PHONE = "CURRENT_PHONE";

	private Context mContext;
	private RMGMemberAdapter adapter;
	private RichManGroup richManGroup;
	private String currentPhone;
	private PopupWindow popupWindow;
	private String selectProd;// 变更套餐时的选择，默认套餐
	private long realDrawvalue = 0;// 真实的套餐流量
	private String currentProd;// 用户当前的套餐
	private ServiceID serviceId;// enum类型的serviceId

	private Button changePackage;// 变更套餐
	private Button exitGroup;// 退出群组

	private View headerView;
	private TextView participantName;// 发起者号码
	private TextView groupNumber;// 群成员数目
	private TextView leftGroupFlowText;//剩余群流量文字
	private TextProgressBar progressBarWithText;// 剩余群流量progressBar
	private TextView totalShareFlow;// 共享流量总量
	private TextView textOfPackageState;// 套餐类型的描述文字
	private LinearLayout totalPackage;//本月套餐显隐布局
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
		if (popupWindow == null) {
			// 引入窗口配置文件
			View view = LayoutInflater.from(this).inflate(
					R.layout.popupwindow_rmg_group_info_participant, null);
			changePackage = (Button) view
					.findViewById(R.id.btn_participant_change_package);
			exitGroup = (Button) view
					.findViewById(R.id.btn_participant_exit_group);
			changePackage.setOnClickListener(listener);
			exitGroup.setOnClickListener(listener);

			// 创建PopupWindow对象
			popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, false);
			// 需要设置一下此参数，点击外边可消失
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			popupWindow.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			popupWindow.setFocusable(true);
		}
		// 显示窗口
		popupWindow.showAsDropDown(v);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);

		currentPhone = getIntent().getStringExtra(INTENT_PHONE);// 号码
		serviceId = (ServiceID) getIntent().getSerializableExtra(
				INTENT_SERVICE_ID);
		richManGroup = SingletonData.getInstance().getRichManGroupInfo();
		if (richManGroup == null || TextUtils.isEmpty(currentPhone)) {// 没有数据
			return;
		}

		headerView = LayoutInflater.from(mContext).inflate(
				R.layout.header_of_rmg_listview, null);
		participantName = (TextView) headerView
				.findViewById(R.id.tv_participant_name);
		groupNumber = (TextView) headerView
				.findViewById(R.id.tv_sponsor_group_number);
		leftGroupFlowText = (TextView) headerView.findViewById(R.id.tv_text_of_left_group_flow);
		progressBarWithText = (TextProgressBar) headerView
				.findViewById(R.id.progressBarWithText);
		totalShareFlow = (TextView) headerView
				.findViewById(R.id.tv_total_share_flow);
		textOfPackageState = (TextView) headerView.findViewById(R.id.tv_text_of_package_state);
		totalPackage = (LinearLayout) headerView.findViewById(R.id.ll_total_package);
		packageThisMonth = (TextView) headerView
				.findViewById(R.id.tv_package_this_month);
		listView.addHeaderView(headerView);

		initView(richManGroup);

		adapter = new RMGMemberAdapter(mContext,serviceId,
				richManGroup.getRichmanmembers(), currentPhone, realDrawvalue);
		listView.setAdapter(adapter);
	}

	private void initView(RichManGroup rmgInfo) {
		switch (serviceId) {
		case DO_RICH_MAN_TOGETHER:
			title.setText("流量共享");
			structRMGData(rmgInfo);
			break;
		case I_AM_RICH_MAN:
			title.setText("流量转赠");
			options.setVisibility(View.GONE);//隐藏右上的操作按钮
			structDonationData(rmgInfo);
			break;
		case NOVALUE:
		default:
			return;
		}
	}
	
	/**
	 * 规整共享数据
	 * @param rmgInfo
	 */
	private void structRMGData(RichManGroup rmgInfo){
		if (rmgInfo.getNextmonthstate()) {
			participantName.setText("群主是" + rmgInfo.getMainmembermobile());
		} else {
			options.setVisibility(View.GONE);
			participantName
					.setText("群主是"
							+ rmgInfo.getMainmembermobile()
							+ "，你已退出群组，下个自然月1日生效。流量共享套餐取消生效前，套餐内剩余流量（含私有和共享）仍可使用。当月不能再次办理流量共享业务。");
		}
		groupNumber.setText(UtilFunc.turnStringToRedSpannable(mContext,
				"群成员共有", rmgInfo.getMembercount() + "", "人"));

		realDrawvalue = rmgInfo.getLeftamt();// 真实的总流量 = 剩余的流量 + 每个成员使用的流量
		for (RichManMember member : rmgInfo.getRichmanmembers()) {// 递归查找
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
		progressBarWithText.setMax((int) (realDrawvalue/1048576));
		progressBarWithText.setProgress(rmgInfo.getLeftamt()/1048576);
		progressBarWithText.setText(String.format("%.2f",
				rmgInfo.getLeftamt() / 1048576.0f) + "MB");// byte转成M
		// 1024*1024 = 1048576
		totalShareFlow.setText("共享流量总量为"
				+ String.format("%.2f", realDrawvalue / 1048576.0f) + "MB");
	}

	/**
	 * 规整转赠数据
	 * @param rmgInfo
	 */
	private void structDonationData(RichManGroup rmgInfo){
		if (rmgInfo.getNextmonthstate()) {
			participantName.setText("群主是" + rmgInfo.getMainmembermobile());
		} else {
			participantName
					.setText("群主是"
							+ rmgInfo.getMainmembermobile()
							+ "，你已被移出群组，下个自然月1日生效。流量转赠套餐取消生效前，转赠流量仍可使用。");
		}
		groupNumber.setText(UtilFunc.turnStringToRedSpannable(mContext,
				"群成员共有", rmgInfo.getMembercount() + "", "人"));

		realDrawvalue = rmgInfo.getLeftamt();// 真实的总流量 = 剩余的流量 + 每个成员（除开群主）使用的流量
		for (RichManMember member : rmgInfo.getRichmanmembers()) {// 递归查找
			if(member.getIsmainnumber()){//若是群组就跳过，下一次循环
				continue;
			}
			realDrawvalue += member.getUsedlimit();
		}
		textOfPackageState.setVisibility(View.GONE);//隐藏本月套餐信息
		totalPackage.setVisibility(View.GONE);
		leftGroupFlowText.setVisibility(View.GONE);
		progressBarWithText.setVisibility(View.GONE);
		totalShareFlow.setVisibility(View.GONE);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (popupWindow == null)
				return;
			if (popupWindow.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
				popupWindow.dismiss();
			}
			if (!richManGroup.getNextmonthstate()) {// 如果群组已经解散，提示返回
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
			case R.id.btn_participant_change_package:// 变更套餐
				final CommomDialog.Builder myDialog = new CommomDialog.Builder(
						mContext);
				myDialog.setTitle("变更套餐");
				// myDialog.setMessage("message");
				// 可以复用中间的content页面
				LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(
						R.layout.dialog_content_change_rmg_package, null);
				TextView phoneText = (TextView) view
						.findViewById(R.id.tv_change_rmg_package_phone);// 显示号码
				phoneText.setText(currentPhone);
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
										RMGInfoShowcaseParticipantActivity.class.getName());
								startActivityForResult(
										intent,
										RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE);
								overridePendingTransition(R.anim.in_from_right,
										R.anim.out_to_left);
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
			case R.id.btn_participant_exit_group:// 退出群组
				final CommomDialog.Builder myDialog2 = new CommomDialog.Builder(
						mContext);
				myDialog2.setTitle("退出群组");
				myDialog2.setMessage("尊敬的用户，退出群组将在下个自然月1号生效。");
				myDialog2.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 本月套餐，需要转成 prodid
								CustomProgressDialogUtil.startProgressDialog(
										mContext, "正在退出群组...",true);
								new AsyncNetworkManager().chgRichManMember(mContext,APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(),
										richManGroup.getMainmembermobile(), 2,
										currentProd,null, currentPhone, null,
										new ChgRichManMemberCallBack() {

											@Override
											public void onChgRichManMemberCallBack(
													int rtn,String error) {
												CustomProgressDialogUtil
														.stopProgressDialog();
												switch (rtn) {
												case 1:
													Toast.makeText(mContext,
															"退出群组成功",
															Toast.LENGTH_SHORT)
															.show();
													break;
												case 0:
												default:
													CommomDialog.Builder dialog = new CommomDialog.Builder(
															mContext);
													dialog.setTitle("提示");
													dialog.setMessage("错误信息："+error);
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
					selectProd, null,currentPhone, codeStr,
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
