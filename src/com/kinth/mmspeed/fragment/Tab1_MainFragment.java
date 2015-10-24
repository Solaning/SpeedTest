package com.kinth.mmspeed.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kinth.mmspeed.BindPhoneNumActivity;
import com.kinth.mmspeed.MyFamilyActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.SpeedMainActivity;
import com.kinth.mmspeed.StatusSettingActivity;
import com.kinth.mmspeed.TransactPackageActivity;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.SMSProdInfo;
import com.kinth.mmspeed.bean.SMSTotalInfo;
import com.kinth.mmspeed.bean.UserFlowInfo;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.bj.FlowSingleton;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.constant.LocationSetting;
import com.kinth.mmspeed.constant.NetWorkState;
import com.kinth.mmspeed.constant.NetworkSetting;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.hk.Action;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.network.AsyncNetworkManager.QueryGPRSCallback;
import com.kinth.mmspeed.ui.CircularProgressView;
import com.kinth.mmspeed.util.AnimationUtil;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * “流量查询”主界面  管理
 * @author BJ
 */
public class Tab1_MainFragment extends BaseFragment {
	private static final int PAGE_QUERY_FLOW = 0;
	private static final int PAGE_NO_FLOW = 1;
	private static final int PAGE_NO_PHONE= 2;
	private static final int PAGE_LODING = 3;
	private static final int PAGE_QUERY_FAIL = 4;
	private final int HANDLE_NETWORK_CHANGE = 1;
	private final int HANDLE_LOCATION_FINISH = 2;
	private final int HANDLE_STATE_CHANGE = 3;
	private final int HANDLE_FIRST_BIND = 4;//接收到绑定号码
	
	@ViewInject(R.id.tv_main_tittle)
	private TextView tv_tittle;// 标题
	
	@ViewInject(R.id.tv_refresh)
	private TextView tv_fresh;//右上角刷新
	
 	@ViewInject(R.id.nav_right_progress)
	private CircularProgressView circularProgress;//右上角
	
	@ViewInject(R.id.down_arrow)
	private ImageView iv_downArrow;// 向下的箭头
	
	@ViewInject(R.id.llt_popup_4G_exhausted)
	private LinearLayout popup4GExhausted;//提示4G用完
	
	@ViewInject(R.id.tv_4G_exhausted)
	private TextView text4GExhausted;//4G用完文字
	
	@ViewInject(R.id.tv_status)
	private TextView tvState;//左上角状态
	
	@OnClick(R.id.tv_status)
	public void setStatusOnClick(View v) {
		Intent intent = new Intent(getActivity(),StatusSettingActivity.class);
		startActivity(intent);
		bottomInAnimation();
	}
	
	@OnClick(R.id.tv_main_tittle)
	public void fun_2(View v){
		changeUserPhone(); //切换号码
	}
	
	@OnClick(R.id.down_arrow)
	public void fun_3(View v){
		changeUserPhone(); //切换号码
	}
	
	@OnClick(R.id.tv_refresh)
	public void fun_4(View v){
		updateData();
	}
	
	private int networkStatue = NetWorkState.NO_NETWORK; //默认环境  无网络
	private Context mContext;
	private MyBroadcastReceiver myBroadcastReceiver;
	private UserPhoneDBService userPhoneDBService;
	private boolean hasLocated = false;//已经完成定位，无需理会其他定位广播
	private boolean queryFlag = true;

	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			//网络状态改变
			case HANDLE_NETWORK_CHANGE:
				int newNetworkStatue = getCurrentNetWorkState(getActivity());
				//网络变化的处理
//				if (newNetworkStatue!=networkStatue&&ifInGuangDong!=POSITION_DEFAULT) {
				if (newNetworkStatue!=networkStatue) {
					placeView(PAGE_QUERY_FLOW);
					networkStatue = newNetworkStatue;
				}
				break;
			case HANDLE_LOCATION_FINISH://定位完成
//				String position = SingletonSharedPreferences.getInstance().getLocationAddress();
//				if (position.contains("广东")&&POSITION_DEFAULT!=POSITION_INGUANGDONG) {
//					POSITION_DEFAULT = POSITION_INGUANGDONG;
//					placeView(PAGE_QUERY_FLOW);
//				}else if(!position.contains("广东")&&POSITION_DEFAULT!=POSITION_NOTINGUANGDONG){
//					POSITION_DEFAULT = POSITION_INGUANGDONG;
//					placeView(PAGE_QUERY_FLOW);
//				}
				if (ApplicationController.getInstance().getCurrentPosition() != 0 && !queryFlag) {   //状态不是获取中，并且没有在刷新
					placeView(PAGE_QUERY_FLOW);
				}
				break;
			case HANDLE_STATE_CHANGE://状态改变 TODO
				placeView(PAGE_QUERY_FLOW);
				break;
			case HANDLE_FIRST_BIND: //号码变动时重刷数据
				List<UserPhone> userPhones = userPhoneDBService.getAllUserPhones();
				if (userPhones == null || userPhones.size() == 0) {//变成一个号码都没有
					tvState.setVisibility(View.INVISIBLE);
					placeView(PAGE_NO_PHONE);
				}else {
					updateData();
					placeView(PAGE_LODING);
					tvState.setVisibility(View.VISIBLE);
				}
//				if (SingletonSharedPreferences.getInstance().getAddedPhoneCount() >= 1) {// 已经有号码了
//					updateData();
//					placeView(PAGE_LODING);
//				}
				break;
			default:
				break;
			}
			return false;
		}
	});
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		//注册广播
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter myOwnFilter = new IntentFilter();
		myOwnFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		myOwnFilter.addAction(StatusSettingActivity.ACTION_STATE_CHANGE);
		myOwnFilter.addAction(BindPhoneNumActivity.ACTION_FIRST_BIND);
		myOwnFilter.addAction(Action.LOCATION_FINISH);
		getActivity().registerReceiver(myBroadcastReceiver, myOwnFilter);
		
		userPhoneDBService = new UserPhoneDBService(getActivity());
		userPhoneDBService.createTable();
		networkStatue = getCurrentNetWorkState(getActivity());   //获取当前网络
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_flow_main, container, false);
		ViewUtils.inject(this, view);

		List<UserPhone> userPhones = userPhoneDBService.getAllUserPhones();
		if (userPhones == null || userPhones.size()==0) {
			placeView(PAGE_NO_PHONE);
		}else {
			updateData();
			placeView(PAGE_LODING);
		}
		return view;
	}
	
	/**
	 * 从数据库取数据，更新标题上的号码
	 */
	private void updateTittle() {
		List<UserPhone> phones = userPhoneDBService.getAllUserPhones();
		if (!JUtil.getSharePreStr(mContext, APPConstant.SP_NAME,
				APPConstant.SP_FIELD_CURRENT_PHONE).equals("")
				&& phones != null && phones.size() > 0) {
			tv_tittle.setText(userPhoneDBService.getNickNameByPhone(JUtil
					.getSharePreStr(mContext, APPConstant.SP_NAME,
							APPConstant.SP_FIELD_CURRENT_PHONE)));
			tvState.setVisibility(View.VISIBLE);
			tv_fresh.setVisibility(View.INVISIBLE);
			circularProgress.startProgress();
		} else {
			tv_tittle.setVisibility(View.VISIBLE);
			circularProgress.setVisibility(View.INVISIBLE);
			tv_fresh.setVisibility(View.INVISIBLE);
			tvState.setVisibility(View.INVISIBLE);
			tv_tittle.setText("智能查询");
		}
	}
	
	/**
	 * 切换手机号码
	 */
	private void changeUserPhone() {
		final ArrayList<UserPhone> userPhones = userPhoneDBService
				.getAllUserPhones();
		if (userPhones == null || userPhones.size() == 0) {//没有绑定号码直接返回
			return;
		}
		String[] sArray = new String[userPhones.size() + 2];// 昵称组还有一个
		for (int i = 0; i < userPhones.size(); i++) {
			sArray[i] = userPhones.get(i).getNickName();
		}
		sArray[sArray.length - 2] = "+ 增加号码";
		sArray[sArray.length - 1] = "我的家庭";//add @2015-01-08
		final int arrayLength = sArray.length;
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
		alertBuilder.setTitle("切换号码:");
		alertBuilder.setItems(sArray,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int position) {
						if (position == arrayLength - 1) {//我的家庭
							Intent intent = new Intent(mContext, MyFamilyActivity.class);
							startActivity(intent);
							rightInLeftOutAnimation();
						}else if(position == arrayLength - 2){//增加号码
							Intent intent = new Intent(getActivity(), BindPhoneNumActivity.class);
							intent.putExtra(BindPhoneNumActivity.INTENT_TARGET_CLASS_NAME, SpeedMainActivity.class.getName());//只为判断动画选择
							startActivity(intent);
							bottomInAnimation();
						}else {
							//获取当前电话
							String mPhone = userPhones.get(position).getPhoneStr();
							//提示当前电话
							Toast.makeText(mContext, "当前电话号码：" + mPhone,
									Toast.LENGTH_SHORT).show();
							//设置当前电话
							synchronized (this) {
								JUtil.putSharePre(mContext, APPConstant.SP_NAME,
										APPConstant.SP_FIELD_CURRENT_PHONE, mPhone);
							}
							//设置Tittle
							tv_tittle.setText(userPhones.get(position).getNickName());
							getOnePhoneData(mPhone);
						}
					}
		});
		alertBuilder.show();
	}	
	
	private void getOnePhoneData(String phoneNum) {
		String currentPhone = JUtil.getSharePreStr(mContext,
				APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
		if (currentPhone != null && !currentPhone.equals("")) {// 当前号码能用
			UserFlowInfo userFlowInfo = FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
			if (userFlowInfo!=null&&userFlowInfo.getSmsProdInfos() != null && userFlowInfo.getSmsProdInfos()!=null) {
				placeView(PAGE_QUERY_FLOW);
			}else {
				updateData();
				placeView(PAGE_LODING);
			}
		}else{
			placeView(PAGE_NO_PHONE);
		}
//		updateData();
		
	}
	
	/**
	 * 联网查询流量
	 * 获取数据成功的话
	 * 先保存到本地，再提醒界面显示
	 */
	private void updateData() {
		queryFlag = true;
		refeshIng();
		final String currrentPhone = JUtil.getSharePreStr(mContext, APPConstant.SP_NAME,
				APPConstant.SP_FIELD_CURRENT_PHONE);//当前号码
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.asyncQueryGPRS(mContext, ApplicationController.getInstance()
				.getUid(), currrentPhone, new QueryGPRSCallback() {
			@Override
			public void queryGPRSCallback(int rtn,UserFlowInfo userFlowInfo) {
				refeshOver();
				if (rtn == 0) {   //没有套餐
					placeView(PAGE_NO_FLOW);
				}else if (rtn == 1){  //有套餐
					//如果是wifi  就直接显示数据
					FlowSingleton.getInstance().putSmsFlowInfo(currrentPhone, userFlowInfo);
					showData();
				}else{   //查询不到
					placeView(PAGE_QUERY_FAIL);
				}
			}
		});
	}
	
	/**
	 * 联网查询完展示
	 */
	private void showData() {
		//获取网络设置
		int networkSetting = ApplicationController.getInstance().getNetworkSetting();
		//网络是自动
		if (networkSetting == NetworkSetting.AUTO) {
			int currentState = getCurrentNetWorkState(mContext);
			//当前网络是wifi直接显示
			if (currentState == NetWorkState.WIFI) {
				placeView(PAGE_QUERY_FLOW);//wifi情况下，直接显示
			}else {
				//TODO 1？？
				int currentPosition = ApplicationController.getInstance().getCurrentPosition();
				if (currentPosition != LocationSetting.LOADING) {   //不是正在定位的话，就显示
					placeView(PAGE_QUERY_FLOW);
				}
			}
		//不是自动的
		} else {
			if (networkSetting == NetworkSetting.WIFI) {
				placeView(PAGE_QUERY_FLOW); // wifi情况下，直接显示
			} else {
				// TODO 2？？
				int currentPosition = ApplicationController.getInstance()
						.getCurrentPosition();
				if (currentPosition != LocationSetting.LOADING) { // 不是正在定位的话，就显示
					placeView(PAGE_QUERY_FLOW);
				}
			}
		}
	}
	
	/**
	 * 现在正在更新
	 */
	private void refeshIng() {
		tv_tittle.setClickable(false);// 不能点击切换
		tv_fresh.setVisibility(View.INVISIBLE); // 隐藏刷新文字
		circularProgress.setVisibility(View.VISIBLE); // 显示刷新菊花
	}
	
	/**
	 * 更新完成
	 */
	private void refeshOver() {
		queryFlag = false;
		tv_tittle.setClickable(true);// 刷新完成，标题可以点击  ProgressBar
		tv_fresh.setVisibility(View.VISIBLE); // 显示刷新文字“刷新”
		circularProgress.setVisibility(View.INVISIBLE); // 隐藏正在刷新的菊花
	}
	
	/**
	 * 显示各个界面
	 * @param index
	 */
	public void placeView(int index){
//		Fragment fragment = (Fragment)getChildFragmentManager().findFragmentByTag(index+"");// getActivity().getSupportFragmentManager().findFragmentByTag(index+""); 
		//得到一个fragment 事务（类似sqlite的操作）
		Fragment fragment = null ;
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//		if (fragment == null ) {
			switch (index) {
			case PAGE_QUERY_FLOW:
//				fragment = mFragments[index];
				fragment = FlowQueryFragment.newInstance();
				String currentPhone = JUtil.getSharePreStr(mContext, APPConstant.SP_NAME,
						APPConstant.SP_FIELD_CURRENT_PHONE);
				UserFlowInfo userFlowInfo = FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
				//统计4G流量的使用情况--并且判断流量是否过剩或者超标
				new Async4GExhaustedTask().execute(userFlowInfo);
				new AsyncFlowExhaustedOrExcess().setPhoneNumber(currentPhone).execute(userFlowInfo.getSmsTotalInfo());
				break;
			case PAGE_NO_FLOW:
//				fragment = mFragments[index];
				fragment = NoFlowFragment.newInstance();
				break;
			case PAGE_NO_PHONE:
//				fragment = mFragments[index];
				fragment = NoPhoneFragment.newInstance();
				break;
			case PAGE_LODING:
//				fragment = mFragments[index];
				fragment = LoadingFragment.newInstance();
				break;
			case PAGE_QUERY_FAIL:
				fragment = QueryFlowFailFragment.newInstance();
				break;
			default:
				break;
			}
//		}
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		ft.replace(R.id.realtabcontent, fragment,index+"");//将得到的fragment 替换当前的viewGroup内容，add则不替换会依次累加
//		ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);//设置动画效果
		ft.addToBackStack(null);
//		ft.commit();//提交
		ft.commitAllowingStateLoss();//提交
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateTittle();
//		if (JUtil.getSharePreInt(getActivity(), APPCONSTANT.SP_NAME, APPCONSTANT.SP_IF_FIRST_BIND)==1) {
//			updateData();
//			placeView(PAGE_LODING);
//			JUtil.putSharePre(getActivity(), APPCONSTANT.SP_NAME, APPCONSTANT.SP_IF_FIRST_BIND, 0);
//		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(myBroadcastReceiver);
		handler.removeCallbacks(test);
	}
	
	/**
	 * 广播接收
	 */
	public class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
	    	// 网络状态变化的广播
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				List<UserPhone> userPhones = userPhoneDBService.getAllUserPhones();
				if (userPhones==null || userPhones.size()==0) {//没有号码返回
					return;
				}
				State wifiState = null;
			    State mobileState = null;
			    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			    wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
			    mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
			    if (wifiState != null && mobileState != null  
			    		 && State.CONNECTED != wifiState  
			    		 && State.CONNECTED == mobileState) {
			    	 // 手机网络连接成功  
			    } else if (wifiState != null && mobileState != null  
			    		 && State.CONNECTED != wifiState  
			    		 && State.CONNECTED != mobileState) {
			    	 // 手机没有任何的网络  
			    } else if (wifiState != null && State.CONNECTED == wifiState) {
			    	 // 无线网络连接成功
				        	
			    }  
			    Message msg = new Message();
			    msg.what = HANDLE_NETWORK_CHANGE;
			    handler.sendMessage(msg);
			    return;
			}
			if (action.equals(Action.LOCATION_FINISH)) {
				String currentPhone = JUtil.getSharePreStr(mContext,
						APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
				UserFlowInfo userFlowInfo = FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
				int currentPosition = ApplicationController.getInstance().getCurrentPosition();
				if (!hasLocated&&currentPosition!=LocationSetting.LOADING&&userFlowInfo!=null&&userFlowInfo.getSmsProdInfos() != null && userFlowInfo.getSmsProdInfos()!=null) {   //不是正在获取的话，就显示
					hasLocated = true;
					Message msg = handler.obtainMessage(HANDLE_LOCATION_FINISH);
					handler.sendMessage(msg);
					return;
				}
//				if (userFlowInfo!=null&&userFlowInfo.getSmsProdInfos() != null && userFlowInfo.getSmsProdInfos()!=null) {
//					placeView(PAGE_QUERY_FLOW);
//				}
				
//				if(hasLocated || SingletonSharedPreferences.getInstance().getAddedPhoneCount() <= 0){
//					return;
//				}
//				hasLocated = true;
//				Message msg = new Message();
//				msg.what = HANDLE_LOCATION_FINISH;
//				handler.sendMessage(msg);
				return;
			//设置地理位置状态
			}
			if(action.equals(StatusSettingActivity.ACTION_STATE_CHANGE)) {
				Message msg = handler.obtainMessage(HANDLE_STATE_CHANGE);
				handler.sendMessage(msg);
				return;
			}
			if(action.equals(BindPhoneNumActivity.ACTION_FIRST_BIND)){
				Message msg = handler.obtainMessage(HANDLE_FIRST_BIND);
				handler.sendMessage(msg);
				return;
			}
		}
	}
	
	private int getNetWorkType(Context context) {
		int type = NetWorkState.OTHER;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int currentType = telephonyManager.getNetworkType();
		switch (currentType) {
			case TelephonyManager.NETWORK_TYPE_EDGE:        //移动2G
				type = NetWorkState._2G; // ~ 50-100 kbps
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:         //移动2G
				type = NetWorkState._2G; // ~ 100 kbps
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:     //移动3G
				type = NetWorkState._3G; // ~ 2-14 Mbps
				break;
			case 17:     //移动3G
				type = NetWorkState._3G; // ~ 2-14 Mbps
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:             //移动4g
				type = NetWorkState._4G;
				break;
			default:
				break;
		}
		return type;
	}
	
	/**
	 * 获取当前网络状态
	 * 	STATUE_NO_NETWORK = -1;  无网络状态
	 * 	STATUE_WIFI = 1;   wifi状态
	 * 	STATUE_2G = 2;     移动2g网络
	 * 	STATUE_3G = 3;     移动3g网络
	 * 	STATUE_4G = 4;     移动4g网络
	 * 	STATUE_OTHER = 5;    其他网络（联通之类的）
	 * @param context
	 * @return 
	 */
	private int getCurrentNetWorkState(Context context) {
		State wifiState = null;  
	    State mobileState = null;  
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
        if (wifiState != null && mobileState != null  
                && State.CONNECTED != wifiState  
                && State.CONNECTED == mobileState) {  
            // 手机网络连接成功  
        	return  getNetWorkType(context);
        } else if (wifiState != null && mobileState != null  
                && State.CONNECTED != wifiState  
                && State.CONNECTED != mobileState) {  
            // 手机没有任何的网络  
        	return NetWorkState.NO_NETWORK;
        } else if (wifiState != null && State.CONNECTED == wifiState) {  
            // 无线网络连接成功  
        	return NetWorkState.WIFI;
        }  
        return NetWorkState.NO_NETWORK;      //无网络
	}
	
	/**
	 * 4G客户的4G/通用流量用完时提示开通4G叠加包或选择2/3G网络
	 * add @2014-07-03
	 */
	class Async4GExhaustedTask extends AsyncTask<UserFlowInfo, Void, Integer> {
		private final int NOT_4G_USER = 1;// 非4G用户，返回
		// 4G用户
		private final int HAS_4G_LEFT = 2;// 4G流量还有剩余，返回
		private final int NEITHER_4G_NOR_2G_3G_LEFT = 3;// 2/3/4G都用完
		private final int NO_4G_BUT_2G_3G_LEFT = 4;// 4G用完，但2/3G还有剩余
		private final int TIME_TO_SHOW = 10000;// 延迟10s显示

		@Override
		protected Integer doInBackground(UserFlowInfo... params) {
			if(params[0] == null){
				return -1000;//随便返回一个值
			}
			// 判断套餐名称是否包含“4G”字眼
			boolean is4GUser = false;
			for (SMSProdInfo info : params[0].getSmsProdInfos()) {
				if (info.getProdType().contains("4G")) {// 4G用户
					// 国内通用、省内通用、国内4G、省内4G这4种流量类型是否都已用光
					is4GUser = true;
					break;
				}
			}
			if (is4GUser) {// 有4G套餐
				for (SMSProdInfo info : params[0].getSmsProdInfos()) {
					if (info.getProdType().contains("国内通用")
							|| info.getProdType().contains("省内通用")
							|| info.getProdType().contains("国内4G")
							|| info.getProdType().contains("省内4G")) {// 4G用户
						// 国内通用、省内通用、国内4G、省内4G这4种流量类型是否都已用光
						if (info.getMonthRemainFlow() > 0) {
							return HAS_4G_LEFT;// 4G流量还有剩余，直接返回
						}
					}
				}
				// 若没有返回运行到这里，表示套餐内的4G流量已经用完，判断2/3G流量是否还有剩余
				for (SMSProdInfo info : params[0].getSmsProdInfos()) {
					if (info.getProdType().contains("2G")
							|| info.getProdType().contains("3G")
							|| info.getProdType().contains("2/3G")) {
						if (info.getMonthRemainFlow() > 0) {// 还有2/3G的流量
							return NO_4G_BUT_2G_3G_LEFT;
						}
					}
				}
				// 所有2/3/4G都用完了
				return NEITHER_4G_NOR_2G_3G_LEFT;
			}
			return NOT_4G_USER;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch (result) {
			case NEITHER_4G_NOR_2G_3G_LEFT:
				// 您的4G及通用流量已用完，4G功能可能收到影响，建议您办理4G叠加包。
				show4GExhausted("您的4G及通用流量已用完，4G功能可能受到影响，建议您办理4G叠加包。");
				break;
			case NO_4G_BUT_2G_3G_LEFT:
				// 您的4G及通用流量已用完，4G功能可能收到影响，建议您办理4G叠加包，或者切换至2/3G网络
				show4GExhausted("您的4G及通用流量已用完，4G功能可能受到影响，建议您办理4G叠加包，或者切换至2/3G网络。");
				break;
			case NOT_4G_USER:
			case HAS_4G_LEFT:
			default:
				break;
			}
		}

		private void show4GExhausted(String content) {
			popup4GExhausted.setVisibility(View.VISIBLE);
			popup4GExhausted.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							TransactPackageActivity.class);
					AnimationUtil.setLayout(R.anim.in_from_right,
							R.anim.out_to_left);
					startActivity(intent);
				}
			});
			text4GExhausted.setText(content);
			handler.postDelayed(test, TIME_TO_SHOW);
		}
	}
	
	/**
	 * 统计用户的流量是否过剩或者超标，发朋友圈动态 TODO 差时间的判断
	 * <br>add @2015-01-08 
	 * 
	 */
	class AsyncFlowExhaustedOrExcess extends AsyncTask<SMSTotalInfo, Void, Integer> {
		private String phoneNumber;//该查询结果对应的号码
		private final int RETURN_DIRECT = 0;//直接返回
		private final int FLOW_EXHAUSTED = 1;//流量耗尽
		private final int FLOW_EXCESS = 2;//流量过剩
		
		public AsyncFlowExhaustedOrExcess setPhoneNumber(String phone){
			this.phoneNumber = phone;
			return this;
		}
		
		@Override
		protected Integer doInBackground(SMSTotalInfo... params) {
			//比较号码是否与主账号一致
			if(TextUtils.isEmpty(phoneNumber))
				return RETURN_DIRECT;
			if(!phoneNumber.equals(MainAccountUtil.getMainAccountPhone(mContext))){//与主账号不同
				return RETURN_DIRECT;
			}
			if(params[0].getResourcesCount() <= 0){//总流量小于0 没有开套餐
				return RETURN_DIRECT;
			}
			double remainFlowPercent = params[0].getLeavingsCount() / params[0].getResourcesCount()*100;//剩余百分比
			if(remainFlowPercent < 10){//剩余流量小于10%
				int remainFlowAmount = (int) (params[0].getLeavingsCount() / 1024);
				if(remainFlowAmount < 100){//剩余流量小于100M
					return FLOW_EXHAUSTED;
				}else {
					return RETURN_DIRECT;
				}
			}else if(remainFlowPercent > 30){//剩余流量大于30%
				int remainFlowAmount = (int) (params[0].getLeavingsCount() / 1024);
				if(remainFlowAmount > 1024){//剩余流量大于1G
					return FLOW_EXCESS;
				}else{
					return RETURN_DIRECT;
				}
			}
			return RETURN_DIRECT;
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch(result){
			case FLOW_EXHAUSTED://产生一条流量用尽动态
				final HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
				params.put("mobile", phoneNumber);
				params.put("activeType", ActiveType.FlowBeggar.getValue());
				JsonObjectRequest req = new JsonObjectRequest(APPConstant.SEND_ACTIVE_IN_PAST_3DAYS, new JSONObject(params),  
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								int rtn = 0;
								try {
									rtn = response.getInt("rtn");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(rtn == 1){
									sendAvtiveBaseOnFlow(params);
								}
							}
						}, new Response.ErrorListener() {
				           @Override  
				           public void onErrorResponse(VolleyError error) {
				               VolleyLog.e("Error: ", error.getMessage());  
				               Log.e("response error",""+error.getMessage());
				           }  
				       });
				// add the request object to the queue to be executed  
				ApplicationController.getInstance().addToRequestQueue(req);
				break;
			case FLOW_EXCESS://产生一条流量过剩动态
				final HashMap<String, String> param = CommonMsgInfo.getCommonMsgInfo();
				param.put("mobile", phoneNumber);
				param.put("activeType", ActiveType.IamTuHao.getValue());
				JsonObjectRequest request = new JsonObjectRequest(APPConstant.SEND_ACTIVE_IN_PAST_3DAYS, new JSONObject(param),  
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								int rtn = 0;
								try {
									rtn = response.getInt("rtn");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(rtn == 1){
									sendAvtiveBaseOnFlow(param);
								}
							}
						}, new Response.ErrorListener() {
				           @Override  
				           public void onErrorResponse(VolleyError error) {
				               VolleyLog.e("Error: ", error.getMessage());  
				               Log.e("response error",""+error.getMessage());
				           }  
				       });
				// add the request object to the queue to be executed  
				ApplicationController.getInstance().addToRequestQueue(request);  
				break;
			case RETURN_DIRECT:
			default :
				break;
			}
			super.onPostExecute(result);
		}
	}
	
	/**
	 * 查询完流量后满足条件就发送动态--流量过剩或不足
	 * @param phone
	 * @param activeType
	 */
	private void sendAvtiveBaseOnFlow(HashMap<String, String> params){
		JsonObjectRequest req = new JsonObjectRequest(APPConstant.GEN_ACTIVE_TIME_LINE, new JSONObject(params),  
				new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					
				}
			}, new Response.ErrorListener() {
	           @Override  
	           public void onErrorResponse(VolleyError error) {
	               VolleyLog.e("Error: ", error.getMessage());  
	               Log.e("response error",""+error.getMessage());
	           }  
	       });
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	/**
	 * 隐藏提示栏
	 */
	Runnable test = new Runnable() {

		@Override
		public void run() {
			popup4GExhausted.setVisibility(View.GONE);
		}
	};
}
