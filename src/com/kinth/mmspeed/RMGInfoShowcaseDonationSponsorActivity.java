package com.kinth.mmspeed;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import com.kinth.mmspeed.adapter.RMGChangeMemberLimitAdapter;
import com.kinth.mmspeed.adapter.RMGChangeMemberLimitAdapter.onChangeMemberLimitCallback;
import com.kinth.mmspeed.adapter.RMGMemberAdapter;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.CancelRichManGroupCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.ChgRichManMemberCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.ui.CommomDialog;
import com.kinth.mmspeed.ui.TextProgressBar;
import com.kinth.mmspeed.util.UtilFunc;
import com.kinth.mmspeed.adapter.RMGRejectMemberAdapter;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sola.code.ydlly_assist.bean.KeyValue;

/**
 * 流量转赠群信息展示页面，针对的是发起者
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_rmg_info_showcase)
public class RMGInfoShowcaseDonationSponsorActivity extends BaseActivity {
	public static final String INTENT_PHONE = "CURRENT_PHONE";

	private Context mContext;
	private RMGMemberAdapter adapter;
	private RichManGroup richManGroup;// 共享组的数据
	private String currentPhone;// 群主号
	private PopupWindow morePopupWindow;// 更多弹出窗口
	private PopupWindow rejectMemberPopupWindow;// 剔除成员弹窗
	private PopupWindow changeLimitPopupWindow;// 修改成员额度的弹窗
	private long realDrawvalue = 0;
	private String selectedLimit;// 变更额度时的选择

	private Button donationFlow;// 转赠流量
	private Button changeLimit;// 更改额度
	private Button rejectMember;// 剔除成员
	private Button relieveGroup;// 解散群组

	private View headerView;// listView的header
	private TextView sponsorName;// 参与者号码
	private TextView groupNumber;// 群成员数目
	private TextView leftGroupFlowText;// 剩余群流量文字
	private TextProgressBar progressBarWithText;// 剩余群流量progressBar
	private TextView totalShareFlow;// 共享流量总量
	private TextView textOfPackageState;// 套餐类型的描述文字
	private LinearLayout totalPackage;// 本月套餐显隐布局

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
					R.layout.popupwindow_rmg_group_info_donation_sponsor, null);
			donationFlow = (Button) view
					.findViewById(R.id.btn_donation_sponsor_invite_member);
			relieveGroup = (Button) view
					.findViewById(R.id.btn_donation_sponsor_relieve_group);
			changeLimit = (Button) view
					.findViewById(R.id.btn_donation_sponsor_change_limit);
			rejectMember = (Button) view
					.findViewById(R.id.btn_donation_sponsor_delete_member);
			donationFlow.setOnClickListener(listener);
			changeLimit.setOnClickListener(listener);
			rejectMember.setOnClickListener(listener);
			relieveGroup.setOnClickListener(listener);

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
		title.setText("流量转赠");

		currentPhone = getIntent().getStringExtra(INTENT_PHONE);// 号码
		richManGroup = SingletonData.getInstance().getRichManGroupInfo();
		if (richManGroup == null || TextUtils.isEmpty(currentPhone)) {// 没有数据
			return;
		}

		headerView = LayoutInflater.from(mContext).inflate(
				R.layout.header_of_rmg_listview, null);
		sponsorName = (TextView) headerView
				.findViewById(R.id.tv_participant_name);
		groupNumber = (TextView) headerView
				.findViewById(R.id.tv_sponsor_group_number);
		leftGroupFlowText = (TextView) headerView
				.findViewById(R.id.tv_text_of_left_group_flow);
		progressBarWithText = (TextProgressBar) headerView
				.findViewById(R.id.progressBarWithText);
		totalShareFlow = (TextView) headerView
				.findViewById(R.id.tv_total_share_flow);
		textOfPackageState = (TextView) headerView
				.findViewById(R.id.tv_text_of_package_state);
		totalPackage = (LinearLayout) headerView
				.findViewById(R.id.ll_total_package);
		listView.addHeaderView(headerView);

		initView(richManGroup);

		adapter = new RMGMemberAdapter(mContext, ServiceID.I_AM_RICH_MAN,
				richManGroup.getRichmanmembers(), currentPhone, realDrawvalue);
		listView.setAdapter(adapter);
	}

	private void initView(RichManGroup rmgInfo) {
		if (rmgInfo.getNextmonthstate()) {
			sponsorName.setText("您是流量转赠群主");
		} else {
			// 隐藏按钮
			options.setVisibility(View.GONE);
			sponsorName
					.setText("您是流量转赠群主，群组已解散，下个自然月1日生效。流量转赠套餐取消生效前，套餐内剩余转赠流量仍可使用。解散当月不能再次办理流量转赠业务。");
		}
		groupNumber.setText(UtilFunc.turnStringToRedSpannable(mContext,
				"群成员共有", rmgInfo.getMembercount() + "", "人"));

		realDrawvalue = rmgInfo.getLeftamt();// 真实的总流量 = 剩余的流量 + 每个成员（除开群主）使用的流量
		for (RichManMember member : rmgInfo.getRichmanmembers()) {// 递归查找
			if (member.getIsmainnumber()) {// 若是群组就跳过，下一次循环
				continue;
			}
			realDrawvalue += member.getUsedlimit();
		}
		textOfPackageState.setVisibility(View.GONE);// 隐藏本月套餐信息
		totalPackage.setVisibility(View.GONE);
		leftGroupFlowText.setVisibility(View.GONE);
		progressBarWithText.setVisibility(View.GONE);
		totalShareFlow.setVisibility(View.GONE);
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
			case R.id.btn_donation_sponsor_invite_member:// 转赠流量
				Intent intent = new Intent(mContext, RMGDonationActivity.class);
				intent.putExtra(RMGDonationActivity.INTENT_PHONE, currentPhone);
				startActivity(intent);
				rightInAnimation();
				break;
			case R.id.btn_donation_sponsor_relieve_group:// 解散群组，取消群组
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
										mContext,
										APPConstant.ServiceID.I_AM_RICH_MAN
												.getValue(), currentPhone,
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
			case R.id.btn_donation_sponsor_change_limit:// 修改额度
				changeDonationLimit();
				break;
			case R.id.btn_donation_sponsor_delete_member:// 剔除成员
				rejectMember();
				break;
			}
		}
	};

	/**
	 * 剔除成员
	 */
	private void rejectMember() {
		if (rejectMemberPopupWindow == null) {
			View popView = LayoutInflater
					.from(mContext)
					.inflate(
							R.layout.popupwindow_rmg_sponsor_reject_member_layout,
							null);
			ImageView popBack = (ImageView) popView
					.findViewById(R.id.iv_popupwindow_back);
			popBack.setOnClickListener(new OnClickListener() {// 返回按钮

				@Override
				public void onClick(View v) {
					rejectMemberPopupWindow.dismiss();
				}
			});
			ListView rejectMemberListview = (ListView) popView
					.findViewById(R.id.listview_popwindow_reject_member);
			RMGRejectMemberAdapter rejectMemberAdapter = new RMGRejectMemberAdapter(
					mContext, currentPhone, richManGroup.getRichmanmembers());
			rejectMemberListview.setAdapter(rejectMemberAdapter);

			rejectMemberPopupWindow = new PopupWindow(popView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
			rejectMemberPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			rejectMemberPopupWindow.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			rejectMemberPopupWindow.setFocusable(true);
			rejectMemberPopupWindow.setAnimationStyle(R.style.popu_animation);
		}
		rejectMemberPopupWindow.showAtLocation(findViewById(R.id.anchor_view),
				Gravity.TOP, 0, 0);
	}

	/**
	 * 改变成员的额度
	 */
	private void changeDonationLimit() {
		if (changeLimitPopupWindow == null) {
			View popView = LayoutInflater
					.from(mContext)
					.inflate(
							R.layout.popupwindow_rmg_donation_sponsor_change_limit_layout,
							null);
			ImageView popBack = (ImageView) popView
					.findViewById(R.id.iv_popupwindow_back);
			popBack.setOnClickListener(new OnClickListener() {// 返回按钮

				@Override
				public void onClick(View v) {
					changeLimitPopupWindow.dismiss();
				}
			});
			ListView rejectMemberListview = (ListView) popView
					.findViewById(R.id.listview_popwindow_change_limit);
			RMGChangeMemberLimitAdapter changeMemberLimitAdapter = new RMGChangeMemberLimitAdapter(
					mContext, currentPhone, richManGroup.getRichmanmembers(),
					new onChangeMemberLimitCallback() {// 设置额度

						@Override
						public void onChangeMemberLimit(final int position,
								final String phone) {
							// richManGroup.getRichmanmembers().get(position)

							final CommomDialog.Builder dialog = new CommomDialog.Builder(
									mContext);
							dialog.setTitle("变更成员限额");

							// 可以复用中间的content页面
							LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
							View view = inflater
									.inflate(
											R.layout.dialog_content_rmg_change_member_limit,
											null);
							TextView phoneText = (TextView) view
									.findViewById(R.id.tv_change_rmg_member_limit_phone);// 显示号码
							phoneText.setText(currentPhone);
							Spinner flowLimitSpinner = (Spinner) view
									.findViewById(R.id.sp_select_flow_limit);// 选择额度
							selectedLimit = "50";// 设置为初始值
							ArrayAdapter<CharSequence> adapter = ArrayAdapter
									.createFromResource(mContext,
											R.array.spinner_donation_limit,
											R.layout.simple_spinner_item);
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							flowLimitSpinner.setAdapter(adapter);
							flowLimitSpinner
									.setOnItemSelectedListener(new OnItemSelectedListener() {

										@Override
										public void onItemSelected(
												AdapterView<?> parent,
												View view, int position, long id) {
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
										public void onNothingSelected(
												AdapterView<?> parent) {
											// TODO Auto-generated method stub

										}
									});

							dialog.setView(view);
							dialog.setPositiveButton("确认",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (richManGroup
													.getRichmanmembers()
													.get(position).getLimit() == Integer
													.parseInt(selectedLimit)) {
												// 判断选择的限额跟当前一样
												CommomDialog.Builder dialog1 = new CommomDialog.Builder(
														mContext);
												dialog1.setTitle("提示");
												dialog1.setMessage("跟目前转赠的套餐一样，请选择新的套餐！");
												dialog1.setPositiveButton(
														"确认",
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {

															}
														});
												dialog1.create().show();
												return;
											}

											CustomProgressDialogUtil
													.startProgressDialog(
															mContext,
															"正在处理中，请稍后...",true);
											new AsyncNetworkManager()
													.chgRichManMember(
															mContext,
															APPConstant.ServiceID.I_AM_RICH_MAN
																	.getValue(),
															currentPhone,
															5,
															APPConstant.ProdID.PACKAGE_TYPE_DONATION
																	.getValue(),
															selectedLimit,
															phone,
															null,
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
																				"您已成功为成员变更流量转赠套餐限额，下月1日生效最新限额！",
																				Toast.LENGTH_LONG)
																				.show();
																		ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
																		keyValue.add(new KeyValue("mobile",currentPhone));
																		keyValue.add(new KeyValue("activeType",ActiveType.DonateGPRSFlow.getValue()));
																		keyValue.add(new KeyValue("targetMobile",phone));
																		
																		DbUtils db = DbUtils.create(mContext, currentPhone+".friends");
																		ContractInfo item = null;
																		try {
																			item = db.findFirst(Selector.from(ContractInfo.class).where("phoneNumber","=",phone));
																		} catch (DbException e) {
																			// TODO Auto-generated catch block
																			e.printStackTrace();
																		}
																		keyValue.add(new KeyValue("targetNickName", UtilFunc.encode(item != null ? item.getContractName() : "")));
																		keyValue.add(new KeyValue("flow", selectedLimit));
																		
																		new AsyncNetworkManager().genActiveTimeLine(mContext,keyValue,new genActiveTimeLineCallBack() {
																			
																			@Override
																			public void onGenActiveTimeLineCallBack(int rtn,int activeId) {
																				// TODO Auto-generated method stub
																				
																			}
																		});
																		return;
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
							dialog.setNegativeButton("返回",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									});
							dialog.create()
									.setLayout(
											(int) (SingletonSharedPreferences
													.getInstance()
													.getScreenWidth() * 0.9),
											LayoutParams.WRAP_CONTENT).show();
						}
					});
			rejectMemberListview.setAdapter(changeMemberLimitAdapter);

			changeLimitPopupWindow = new PopupWindow(popView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
			changeLimitPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			// 设置点击窗口外边窗口消失
			changeLimitPopupWindow.setOutsideTouchable(true);
			// 设置此参数获得焦点，否则无法点击
			changeLimitPopupWindow.setFocusable(true);
			changeLimitPopupWindow.setAnimationStyle(R.style.popu_animation);
		}
		changeLimitPopupWindow.showAtLocation(findViewById(R.id.anchor_view),
				Gravity.TOP, 0, 0);
	}
}
