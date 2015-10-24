package com.kinth.mmspeed.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinth.mmspeed.CMCCDownloadActivity;
import com.kinth.mmspeed.FreeFlowEreaNoBindPhoneActivity;
import com.kinth.mmspeed.RMGCommonUserPhonesActivity;
import com.kinth.mmspeed.CommonWebViewActivity;
import com.kinth.mmspeed.FlowFavorableActivity;
import com.kinth.mmspeed.FreeFlowEreaActivity;
import com.kinth.mmspeed.GM_LoginActivity;
import com.kinth.mmspeed.GM_SelectMobileAsMainAccountActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.TransactPackageActivity;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.activity.billboard.BillboardActivity;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.friend.MomentsActivity;
import com.kinth.mmspeed.hk.Action;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.util.AnimationUtil;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.DateUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sola.code.ydlly_assist.bean.KeyValue;

/**
 * 发现頻道
 * 
 * @author sola
 */
public class Tab3_Fragment extends BaseFragment {
	private Context mContext;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.llt_red_packets)
	private LinearLayout redPackets;//流量红包
	
	@ViewInject(R.id.tv_moments_has_new)
	private TextView momentsHasNew;//朋友圈是否有更新

	@ViewInject(R.id.rtl_red_point)
	private RelativeLayout redPoint;
	
	@ViewInject(R.id.iv_message_centre_has_new)
	private ImageView messageCentreHasNew;//消息中心是否有更新
	
	@ViewInject(R.id.iv_flow_groupon_has_new)
	private ImageView flowGrouponHasNew;//流量团购红点
	
	private List<UserPhone> userPhones;
	
	// -------------------onClick-------------
	@OnClick(R.id.llt_flow_share)
	public void fun_3(View v) {// 流量共享
//		if (!SingletonSharedPreferences.getInstance()
//				.getShowRMGFlowPresentation()) {// 没有显示过
//			Intent intent = new Intent(mContext,
//					RMGFlowPresentationActivity.class);
//			intent.putExtra(RMGFlowPresentationActivity.INTENT_SERVICE_ID,
//					APPConstant.ServiceID.DO_RICH_MAN_TOGETHER);
//			intent.putExtra(
//					RMGFlowPresentationActivity.INTENT_SHOW_NEXT, true);
//			startActivity(intent);
//			getActivity().overridePendingTransition(R.anim.in_from_right,
//					R.anim.out_to_left);
//
//			// 插入数据库
//			 SingletonData.getInstance().initDataBase(mContext);
//			return;
//		}
//		SingletonData.getInstance().initDataBase(mContext);
		Intent intent = new Intent(mContext, RMGCommonUserPhonesActivity.class);
		intent.putExtra(RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
				APPConstant.ServiceID.DO_RICH_MAN_TOGETHER);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	@OnClick(R.id.llt_flow_donation)
	public void fun_1(View v) {// 流量转赠
//		if (!SingletonSharedPreferences.getInstance()
//				.getShowRMGDonationPresentation()) {
//			Intent intent = new Intent(mContext,
//					RMGFlowPresentationActivity.class);
//			intent.putExtra(RMGFlowPresentationActivity.INTENT_SERVICE_ID,
//					APPConstant.ServiceID.I_AM_RICH_MAN);
//			intent.putExtra(
//					RMGFlowPresentationActivity.INTENT_SHOW_NEXT, true);
//			startActivity(intent);
//			getActivity().overridePendingTransition(R.anim.in_from_right,
//					R.anim.out_to_left);
//			return;
//		}
		Intent intent = new Intent(mContext, RMGCommonUserPhonesActivity.class);
		intent.putExtra(RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
				APPConstant.ServiceID.I_AM_RICH_MAN);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}
	
	@OnClick(R.id.llt_red_packets)
	public void fun_4(View v){//流量红包
		//统计数据
		ArrayList<KeyValue> keyValueStat = new ArrayList<KeyValue>();
		keyValueStat.add(new KeyValue("statType","clickFlowGiftMenu"));
		AsyncNetworkManager activeNetwork = new AsyncNetworkManager();
		activeNetwork.asynSendData(mContext, keyValueStat);
		//判断是否登录---->产生一条动态，流量红包
		String mobile = MainAccountUtil.getAvailablePhone(mContext);
		if (!TextUtils.isEmpty(mobile)) {
			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
			keyValue.add(new KeyValue("mobile",mobile));
			keyValue.add(new KeyValue("activeType",ActiveType.FlowGift.getValue()));
			
			new AsyncNetworkManager().genActiveTimeLine(mContext, keyValue,
					new genActiveTimeLineCallBack() {

						@Override
						public void onGenActiveTimeLineCallBack(int rtn,int activeId) {

						}
					});
		}
		Intent intent = new Intent(mContext,CommonWebViewActivity.class);
		intent.putExtra(CommonWebViewActivity.INTENT_TITLE, "流量红包");
		intent.putExtra(CommonWebViewActivity.INTENT_URL, APPConstant.FLOW_RED_PACKETS);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	@OnClick(R.id.llt_free_erea)
	public void fun_6(View v) {// 免流量专区
		UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
		if (userPhoneDBService.getAllPhoneAmount() >= 1) {
			if (!TextUtils.isEmpty(JUtil.getSharePreStr(mContext,
					APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE))) {// 当前号码不为空
				Intent intent = new Intent(mContext, FreeFlowEreaActivity.class);
				AnimationUtil.setLayout(R.anim.in_from_right,
						R.anim.out_to_left);
				startActivity(intent);
				return;
			}
		}
		// 没有绑定号码
		Intent intent = new Intent(mContext, FreeFlowEreaNoBindPhoneActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	@OnClick(R.id.llt_app_recommend)
	public void fun_2(View v) {// 应用下载
		Intent intent = new Intent(mContext, CMCCDownloadActivity.class);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	@OnClick(R.id.llt_handle_package)
	public void fun_7(View v) {// 套餐办理
		Intent intent = new Intent(mContext, TransactPackageActivity.class);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	@OnClick(R.id.llt_message_center)
	public void fun_8(View v) {// 消息中心
		messageCentreHasNew.setVisibility(View.GONE);
		ApplicationController.getInstance().setNumOfMessageCentre(0);
		SingletonSharedPreferences.getInstance().setMessageCentreUpdateTimeStamp(DateUtil.getStringDate());//消息中心阅读时间戳
		//发广播通知
		mContext.sendBroadcast(new Intent().setAction(Action.HAS_NEW_MESSAGE));
		Intent intent = new Intent(mContext, FlowFavorableActivity.class);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}

	@OnClick(R.id.llt_moments)
	public void fun_9(View v){//朋友圈  //--只能通过判断主账号，不能用绑定号码列表的第一个
		UserAccount user = MainAccountUtil.getCurrentAccount(getActivity());
		Intent intent;
		if (user == null) {
			UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
			userPhones = userPhoneDBService.getAllUserPhones();
			if (userPhones != null && userPhones.size() > 0) {//有绑定号码
				intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);//选择一个号码作主账号
				Bundle bundle = new Bundle();
				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, MomentsActivity.class.getName());//传递需要跳转的类名
				bundle.putBoolean(MomentsActivity.IS_GET_My_ACTIVE, false);
				intent.putExtras(bundle);
			}else {//没有绑定号码
				intent = new Intent(mContext, GM_LoginActivity.class);//登陆
				Bundle bundle = new Bundle();
				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, MomentsActivity.class.getName());//传递需要跳转的类名
				bundle.putBoolean(MomentsActivity.IS_GET_My_ACTIVE, false);
				intent.putExtras(bundle);
			}
		}else {
			redPoint.setVisibility(View.GONE);
			ApplicationController.getInstance().setNumOfMoments(0);//朋友圈已经读了
			SingletonSharedPreferences.getInstance().setMomentsUpdateTimeStamp(DateUtil.getStringDate());//朋友圈阅读时间戳
			//发广播通知
			mContext.sendBroadcast(new Intent().setAction(Action.HAS_NEW_MESSAGE));
			intent = new Intent(mContext, MomentsActivity.class);
			intent.putExtra(MomentsActivity.IS_GET_My_ACTIVE, false);//TODO 是否需要统一成bundle
		}
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	@OnClick(R.id.llt_paihang)
	public void fun_10(View v){//排行榜
		UserAccount user = MainAccountUtil.getCurrentAccount(getActivity());
		Intent intent;
		if (user == null) {
			UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
			userPhones = userPhoneDBService.getAllUserPhones();
			if (userPhones != null && userPhones.size() > 0) {
				intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, BillboardActivity.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}else {
				intent = new Intent(mContext, GM_LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, BillboardActivity.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}
			AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
			startActivity(intent);
		}else {
			intent = new Intent(mContext, BillboardActivity.class);
			AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
			startActivity(intent);
		}
	}
	
	/**
	 * 分享有礼
	 * 
	 * @param v
	@OnClick(R.id.btn_share)
	public void shareOnClick(View v) {//

		int result = JUtil.getSharePreInt(mContext, APPConstant.SP_NAME,
				APPConstant.SP_SHARE_FIRSTIN);
		if (result == 0) { // 第一次进入
			Intent intent = new Intent(mContext, ShareInstructionActivity.class);
			intent.putExtra(ShareInstructionActivity.INTENT_FIRSTIN, 0);

			AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
			startActivity(intent);
		} else {
			Intent intent = new Intent(mContext, ShareAwardActivity.class);
			intent.putExtra(ShareInstructionActivity.INTENT_FIRSTIN, 0);
			AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
			startActivity(intent);
		}
	}
*/
	@OnClick(R.id.llt_flow_groupon)
	public void fun_11(View v){//流量团购点击
		//统计数据
		ArrayList<KeyValue> keyValueStat = new ArrayList<KeyValue>();
		keyValueStat.add(new KeyValue("statType",APPConstant.StatTypeFlowGroupon));
		AsyncNetworkManager activeNetwork = new AsyncNetworkManager();
		activeNetwork.asynSendData(mContext, keyValueStat);
		//发动态
		String mobile = MainAccountUtil.getAvailablePhone(mContext);
		String url = APPConstant.FLOW_GROUPON_URL;
		if (!TextUtils.isEmpty(mobile)) {
			url += "&ph=" + mobile;//url加传电话号码
			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
			keyValue.add(new KeyValue("mobile",mobile));
			keyValue.add(new KeyValue("activeType",ActiveType.FlowGroupon.getValue()));
			
			new AsyncNetworkManager().genActiveTimeLine(mContext, keyValue,
					new genActiveTimeLineCallBack() {

						@Override
						public void onGenActiveTimeLineCallBack(int rtn,int activeId) {

						}
					});
		}
		SingletonSharedPreferences.getInstance().setFlowGrouponClick(true);
		flowGrouponHasNew.setVisibility(View.GONE);
		//发广播通知
		mContext.sendBroadcast(new Intent().setAction(Action.HAS_NEW_MESSAGE));
		
		Intent intent = new Intent(mContext,CommonWebViewActivity.class);
		intent.putExtra(CommonWebViewActivity.INTENT_TITLE, getResources().getString(R.string.flow_groupon));
		intent.putExtra(CommonWebViewActivity.INTENT_URL, url);
		startActivity(intent);
		rightInLeftOutAnimation();
	}
	

	// -----------------end OnClick------------
	static Tab3_Fragment newInstance() {
		Tab3_Fragment newFragment = new Tab3_Fragment();
		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab3_fragment_layout, container,
				false);
		ViewUtils.inject(this,view);
		title.setText(getResources().getString(R.string.text_find));

		switch(SingletonSharedPreferences.getInstance().getIsOpenFlowRedPacket()){//是否打开流量红包
		case 1:
			redPackets.setVisibility(View.VISIBLE);
			break;
		case 0:
		case -1:
		default:
			redPackets.setVisibility(View.GONE);
			break;
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(ApplicationController.getInstance().getNumOfMessageCentre() > 0){
			messageCentreHasNew.setVisibility(View.VISIBLE);
		}else {
			messageCentreHasNew.setVisibility(View.GONE);
		}
		if(ApplicationController.getInstance().getNumOfMoments() > 0 ){
			redPoint.setVisibility(View.VISIBLE);
			if(ApplicationController.getInstance().getNumOfMoments() > 99){
				momentsHasNew.setText("99");
			}else{
				momentsHasNew.setText(ApplicationController.getInstance().getNumOfMoments() + "");
			}
		}else{
			redPoint.setVisibility(View.GONE);
		}
		if(SingletonSharedPreferences.getInstance().isFlowGrouponClick()){
			flowGrouponHasNew.setVisibility(View.GONE);
		}else{
			flowGrouponHasNew.setVisibility(View.VISIBLE);
		}
	}
}
