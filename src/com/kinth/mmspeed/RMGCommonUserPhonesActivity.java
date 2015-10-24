package com.kinth.mmspeed;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.adapter.SwipeAdapter;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.QueryRichManGroupCallBack;
import com.kinth.mmspeed.ui.SwipeListView;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 号码管理
 * 只在流量共享、流量转赠2个tab中如果没有绑定手机时调用
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_rmg_user_phones)
public class RMGCommonUserPhonesActivity extends BaseActivity {
	public static final String INTENT_SERVICE_ID = "INTENT_SERVICE_ID";
	private Context mContext;
	private ServiceID serviceId;// enum类型

//	 private SwipeAdapter adapter;
	private List<UserPhone> userPhones;
	private UserPhoneDBService userPhoneDBService;

	@ViewInject(R.id.swipelistview_phone_list)
	private SwipeListView mListView;

	@ViewInject(R.id.iv_back)
	private ImageView back;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.iv_add_phone)
	private ImageView addPhone;

	@ViewInject(R.id.tv_tip)
	private TextView tvTips;

	@OnClick(R.id.iv_back)
	public void fun_1(View v) {//返回
		RMGCommonUserPhonesActivity.this.finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.iv_add_phone)
	public void fun_2(View v) {//添加号码
		Intent intent = new Intent(RMGCommonUserPhonesActivity.this,
				BindPhoneNumActivity.class);
		startActivity(intent);
		rightInAnimation();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		serviceId = (ServiceID) getIntent().getSerializableExtra(
				INTENT_SERVICE_ID);

		TextView textView = (TextView) LayoutInflater.from(mContext).inflate(
				R.layout.simple_textview_item, null);
		textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		textView.getPaint().setAntiAlias(true);// 抗锯齿
		switch (serviceId) {
		case DO_RICH_MAN_TOGETHER:
			title.setText("流量共享");
			textView.setText("查看流量共享业务介绍");
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							RMGFlowPresentationActivity.class);
					intent.putExtra(
							RMGFlowPresentationActivity.INTENT_SERVICE_ID,
							APPConstant.ServiceID.DO_RICH_MAN_TOGETHER);
					intent.putExtra(
							RMGFlowPresentationActivity.INTENT_SHOW_NEXT, false);
					startActivity(intent);
					rightInAnimation();
				}
			});
			mListView.addFooterView(textView);
			break;
		case I_AM_RICH_MAN:
			title.setText("流量转赠");
			textView.setText("查看流量转赠业务介绍");
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							RMGFlowPresentationActivity.class);
					intent.putExtra(
							RMGFlowPresentationActivity.INTENT_SERVICE_ID,
							APPConstant.ServiceID.I_AM_RICH_MAN);
					intent.putExtra(
							RMGFlowPresentationActivity.INTENT_SHOW_NEXT, false);
					startActivity(intent);
					rightInAnimation();
				}
			});
			mListView.addFooterView(textView);
			break;
		case FREE_FLOW://免流量专区
			title.setText("免流量专区");
			break;
		case NOVALUE:
		default:
			break;
		}
		userPhoneDBService = new UserPhoneDBService(RMGCommonUserPhonesActivity.this);
		userPhoneDBService.createTable();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		userPhones = userPhoneDBService.getAllUserPhones();
		if (userPhones == null || userPhones.size() == 0) {
			tvTips.setVisibility(View.VISIBLE);
		} else {
			tvTips.setVisibility(View.GONE);
		}
		SwipeAdapter adapter = new SwipeAdapter(this,
				mListView.getRightViewWidth(), userPhones, listener);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {// 点击表示选用这个号码发起流量共享

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String serviceIdStr = null;
						switch (serviceId) {
						case DO_RICH_MAN_TOGETHER:// 流量共享
							serviceIdStr = ServiceID.DO_RICH_MAN_TOGETHER
									.getValue();
							break;
						case I_AM_RICH_MAN:// 流量转赠
							serviceIdStr = ServiceID.I_AM_RICH_MAN.getValue();
							break;
						case FREE_FLOW:
							Intent intent = new Intent(mContext,FreeFlowEreaActivity.class);
							intent.putExtra(FreeFlowEreaActivity.INTENT_PHONE_NUMBER, userPhones.get(arg2).getPhoneStr());
							startActivity(intent);
							rightInAnimation();
							finish();
							return;
						case NOVALUE:
						default:
							return;// 没有收到正常的数据
						}
						final String phone = userPhones.get(arg2).getPhoneStr();
						CustomProgressDialogUtil.startProgressDialog(mContext,
								"正在加载中...",true);// 菊花转
						// 查询该号码的状态
						new AsyncNetworkManager().queryRichManGroup(mContext,
								serviceIdStr, phone,
								new QueryRichManGroupCallBack() {

									@Override
									public void onQueryRichManGroupCallBack(
											int rtn, RichManGroup info) {
										CustomProgressDialogUtil
												.stopProgressDialog();// 取消菊花
										switch (rtn) {
										case 0:// 查询失败
											Log.e("查询失败", "查询失败？？？");
											Toast.makeText(mContext, "查询失败",
													Toast.LENGTH_LONG).show();
											return;
										case 1:// 成功
											if (info == null) {
												Log.e("error", "RichManGroup为空");
												return;
											}
											// 拿到群组的信息
											SingletonData.getInstance()
													.setRichManGroupInfo(info);
											if (info.getIsmainmember()) {// 当前登录者为群主，分离出来
												switch (serviceId) {
												case DO_RICH_MAN_TOGETHER:// 流量共享
													Intent intent = new Intent(
															mContext,
															RMGInfoShowcaseSponsorActivity.class);
													intent.putExtra(
															RMGInfoShowcaseSponsorActivity.INTENT_PHONE,
															phone);
													startActivity(intent);
													rightInAnimation();
													break;
												case I_AM_RICH_MAN:// 流量转赠
													Intent intent1 = new Intent(
															mContext,
															RMGInfoShowcaseDonationSponsorActivity.class);
													intent1.putExtra(
															RMGInfoShowcaseDonationSponsorActivity.INTENT_PHONE,
															phone);
													startActivity(intent1);
													rightInAnimation();
													break;
												case NOVALUE:
												default:
													return;
												}
												break;
											} else {// 当前登录者非群主
												Intent intent = new Intent(
														mContext,
														RMGInfoShowcaseParticipantActivity.class);
												intent.putExtra(
														RMGInfoShowcaseParticipantActivity.INTENT_PHONE,
														phone);
												switch (serviceId) {
												case DO_RICH_MAN_TOGETHER:// 流量共享
													intent.putExtra(
															RMGInfoShowcaseParticipantActivity.INTENT_SERVICE_ID,
															ServiceID.DO_RICH_MAN_TOGETHER);
													break;
												case I_AM_RICH_MAN:// 流量转赠
													intent.putExtra(
															RMGInfoShowcaseParticipantActivity.INTENT_SERVICE_ID,
															ServiceID.I_AM_RICH_MAN);
													break;
												case NOVALUE:
												default:
													return;
												}
												startActivity(intent);
												rightInAnimation();
											}
											return;
										case 2:// 未订购套餐
											Log.e("result", "未订购");
											// 没有订购，前往订购页面
											Intent intent = new Intent(
													mContext,
													RMGSelectShareTypeActivity.class);
											intent.putExtra(
													RMGSelectShareTypeActivity.INTENT_PHONE,
													phone);
											switch (serviceId) {
											case DO_RICH_MAN_TOGETHER:
												intent.putExtra(
														RMGSelectShareTypeActivity.INTENT_SERVICE_ID,
														ServiceID.DO_RICH_MAN_TOGETHER);// 共享群
												break;
											case I_AM_RICH_MAN:// 开通套餐
												intent.putExtra(
														RMGSelectShareTypeActivity.INTENT_SERVICE_ID,
														ServiceID.I_AM_RICH_MAN);// 转赠群
												break;
											case NOVALUE:
											default:
												return;
											}
											startActivity(intent);
											rightInAnimation();
										}
									}
								});
					}
				});
	}

	/**
	 * 左滑删除的监听
	 */
	private SwipeAdapter.IOnItemRightClickListener listener = new SwipeAdapter.IOnItemRightClickListener() {
		@Override
		public void onRightClick(View v, int position) {
			String phoneString = userPhones.get(position).getPhoneStr();
			userPhoneDBService.deleteOnPhone(phoneString);// 删除该号码信息
			UtilFunc.deleteCacheFile(phoneString);// 删除对应于该号码的缓存文件
			userPhones = userPhoneDBService.getAllUserPhones();
			SwipeAdapter adapter = new SwipeAdapter(mContext,
					mListView.getRightViewWidth(), userPhones, listener);
			mListView.setAdapter(adapter);// 更新界面
			// 默认号码 当前号码的改变
			String currentPhone = JUtil.getSharePreStr(
					RMGCommonUserPhonesActivity.this, APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE);
			if (currentPhone.equals(phoneString) && userPhones.size() > 0) {// 刚刚删除的是默认号码，剩下还有别的号码，选第一个作为默认号码
				JUtil.putSharePre(RMGCommonUserPhonesActivity.this,
						APPConstant.SP_NAME,
						APPConstant.SP_FIELD_CURRENT_PHONE, userPhones.get(0)
								.getPhoneStr());
			} else if (userPhones.size() == 0) {// 全部删除完了
				tvTips.setVisibility(View.VISIBLE);
				JUtil.putSharePre(RMGCommonUserPhonesActivity.this,
						APPConstant.SP_NAME,
						APPConstant.SP_FIELD_CURRENT_PHONE, "");
			}
		}
	};
}
