package com.kinth.mmspeed.fragment;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.mmspeed.CMCCDownloadActivity;
import com.kinth.mmspeed.FlowFavorableDetailActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.StatusSettingActivity;
import com.kinth.mmspeed.bean.SMSProdInfo;
import com.kinth.mmspeed.bean.UserFlowInfo;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.bj.FlowSingleton;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.IdleSetting;
import com.kinth.mmspeed.constant.LocationSetting;
import com.kinth.mmspeed.constant.NetWorkState;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 智能看板界面
 * @author BJ
 * @version 1.8
 */
public class SmartKanBanFragment extends BaseFragment {
	@ViewInject(R.id.current_remain_flow)
	private TextView tvRemainFlow;   //当前剩余流量
	@ViewInject(R.id.everyday_flow)
	private TextView tvEverydayFlow;    //日均剩余
	@ViewInject(R.id.tab1_tv_month_all_flow)
	private TextView tvMonthAllFlow;     //本月所有流量
	@ViewInject(R.id.tab1_tv_exceed_flow)
	private TextView tvExceedFlow;    //超出流量
	@ViewInject(R.id.tab1_tv_remain_days)
	private TextView tvRemainDays;   //剩余天数
	@ViewInject(R.id.percent_tv)	
	private TextView tvPercent;       //今日所用百分比
	@ViewInject(R.id.tvCurrentState)        
	private TextView tvCurrentState;   // 当前状态
	
	@ViewInject(R.id.percent_text)
	private TextView tv_percent;     //百分比显示数据
	
	@ViewInject(R.id.tab1_clock_needle)
	private ImageView needle;    //指针
	
	@ViewInject(R.id.tab1_clock_iv)
	private ImageView clockBg;   //表盘
	
	@ViewInject(R.id.iv_tips)
	private ImageView ivTips;
	
	private Context mContext; // 上下文
	private int currentPosition = 0;    //地理位置
	private boolean ifIdleTime = false;    //是否在闲时

	private int networkStatue =NetWorkState.NO_NETWORK;    //默认  无网络
	
	private float fromDegrees = 0l;
	private DecimalFormat dFormat = new DecimalFormat("#0.00");  //流量的格式
	private UserPhoneDBService userPhoneDBService;
	
//	private DbUtils mDbUtils;
	public static Fragment newInstance() {
		SmartKanBanFragment flowQueryFragment = new SmartKanBanFragment();
		return flowQueryFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		try {
			callback = (Tab1Callback) activity;
		} catch (Exception e) {
			callback = null;
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		currentPosition = ApplicationController.getInstance().getCurrentPosition();
		
		mContext = getActivity();
		userPhoneDBService = new UserPhoneDBService(mContext);
		userPhoneDBService.createTable();
		//查看网络是自动还是指定
		int networkSetting = ApplicationController.getInstance().getNetworkSetting();
		//自动的话就获取当前网络
		if (networkSetting==NetWorkState.AUTO) {
			networkStatue = getCurrentNetWorkState(getActivity());   //获取当前网络
		//指定的话的话赋值到指定网络
		}else {
			networkStatue = networkSetting;  //获取当前网络
		}
		// 闲时  
		ifIdleTime = getIfIdleTime();
	}
	
	View.OnClickListener orderPackageListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
				orderPackage();
		}
	};
	View.OnClickListener recomand4GListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
				String currentPhone = JUtil.getSharePreStr(mContext,APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
//				UserFlowInfo userFlowInfo= FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
//				if (userFlowInfo!=null) {
//					ArticleInfo articleInfo = userFlowInfo.getArticleInfo();
//					if (articleInfo!=null&&articleInfo!=null&&!articleInfo.getClickURL().equals("")) {
//						Intent intent = new Intent(getActivity(),FlowFavorableDetailActivity.class);
//						intent.putExtra(FlowFavorableDetailActivity.INTENT_URL, articleInfo.getClickURL());
//						intent.putExtra(FlowFavorableDetailActivity.INTENT_TITTLE, articleInfo.getArticleName());
//						startActivity(intent);
//						rightInLeftOutAnimation();
//					}
//				}
				String urlString = "http://h5.gmccopen.com/h5/portal!index.action?channelId=mobile_flow_instrument&id=4gyindao";
				if (!currentPhone.equals("")) {
					 urlString = urlString + "&mobile="+currentPhone;
				}else {
					List<UserPhone>userPhones = userPhoneDBService.getAllUserPhones();
					if (userPhones!=null&&userPhones.size()>0) {
						String phone = userPhones.get(0).getPhoneStr();
						urlString = urlString+"&mobile="+phone;
					}
				}
				Intent intent = new Intent(getActivity(),FlowFavorableDetailActivity.class);
				intent.putExtra(FlowFavorableDetailActivity.INTENT_URL, urlString);
				intent.putExtra(FlowFavorableDetailActivity.INTENT_TITTLE, "4G推荐");
				startActivity(intent);
				rightInLeftOutAnimation();
		}
	};
	
	View.OnClickListener appDownLoadListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
				Intent intent = new Intent(getActivity(),CMCCDownloadActivity.class);
				startActivity(intent);
				rightInLeftOutAnimation();
		}
	};
	/**
	 * 跳转去订购套餐页面
	 */
	protected void orderPackage() {
		if (callback != null)
			callback.tab1_callback();
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showFlowData();
	}
	
	private void showFlowData() {
		List<UserPhone>userPhones = userPhoneDBService.getAllUserPhones();
		
		if (userPhones!=null&&userPhones.size()>0) {// 有号码
			// 获取号码数据
			String currentPhone = JUtil.getSharePreStr(mContext,
					APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
			if (currentPhone != null && !currentPhone.equals("")) {// 当前号码能用
				UserFlowInfo userFlowInfo = FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
				if (userFlowInfo!=null&&userFlowInfo.getSmsProdInfos() != null && userFlowInfo.getSmsProdInfos()!=null) {
					// 更新界面
					layoutFlowInfos(FlowSingleton.getInstance().getSmsFlowInfo(currentPhone));
				} else {
//					updateData();
//					JUtil.showMsg(mContext, "读取数据失败，请尝试刷新数据！");
				}
			} else {// 没有可用的号码（上一层已经处理）
//				queryPhoneFail();
			}
		} else {// 还没有添加手机号   // 没有可用的号码（上一层已经处理）
//			queryPhoneFail();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_smart_board, container, false);
		ViewUtils.inject(this, view);
		return view;
	}
	
	/**
	 * 显示更新界面
	 * 
	 * @param list
	 */
	private void layoutFlowInfos(UserFlowInfo userFlowInfo) {
		List<SMSProdInfo>smsProdInfos = userFlowInfo.getSmsProdInfos();
		double totalAllFlow = 0;     //总流量
		double totalRemainFlow = 0;   //总剩余流量
		double exceedFlow = userFlowInfo.getSmsTotalInfo().getExceedUsedCount();   //超出套餐流量
		int awayFromEndDay = 0;    
		//如果是wifi  就不管他是不是在广东
		if (networkStatue==NetWorkState.WIFI) {          //#############第1层判断   （1）是否是Wifi
			String wifiTips = getText(R.string.tips_wifi_state).toString();
			tvCurrentState.setText(Html.fromHtml("<u>" + wifiTips+ "</u>")); // 抗锯齿
//			tvCurrentState.setText(wifiTips); // 抗锯齿
			for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
				totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
				totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
				awayFromEndDay = mSmsProdInfo.getRemainDay();
			}
		//4g  情况下
		}else if (networkStatue==NetWorkState._4G) {     //#############第1层判断   （2）是否是4G
			if (currentPosition==LocationSetting.IN_GD) {   //  第1-2-1层判断   ==============广东省，4g
				if (ifIdleTime) {      //  广东省，4g，闲时
					tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省，4G，闲时"+ "</u>")); // 抗锯齿
//					tvCurrentState.setText("当前状态: 广东省，4G，闲时"); // 抗锯齿
				}else {       //  广东省，4g，非闲时
					tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省，4G，非闲时"+ "</u>")); // 抗锯齿
				}
				//广东省，4g，非闲时  的处理
				for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
					if ("省内4G".equals(mSmsProdInfo.getProdType())||"省内通用".equals(mSmsProdInfo.getProdType())
							||"国内通用".equals(mSmsProdInfo.getProdType())||"国内4G".equals(mSmsProdInfo.getProdType())) {
						totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
						totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
						awayFromEndDay = mSmsProdInfo.getRemainDay();
					}
				}
			}else  if(currentPosition==LocationSetting.OUT_GD){           //第1-2-2层判断=================广东省外，4g,（4g没有闲时，所以处理是一样的）
				if (ifIdleTime) {      //  广东省外，4g，闲时
					tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省外，4G，闲时"+ "</u>")); // 抗锯齿
//					tvCurrentState.setText("当前状态: 广东省外，4G，闲时"); // 抗锯齿
				}else {       // 广东省外，4g，非闲时
					//广东省外，4g，非闲时  的处理
					tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省外，4G，非闲时"+ "</u>"));
//					tvCurrentState.setText("当前状态: 广东省外，4G，非闲时"); // 抗锯齿
				}
				for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
					if ("国内通用".equals(mSmsProdInfo.getProdType())||"国内4G".equals(mSmsProdInfo.getProdType())) {
						totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
						totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
						awayFromEndDay = mSmsProdInfo.getRemainDay();
					}
				}
			}else {                                                                    //  //第1-2-3层判断=================地理未位置获取不到
//				tvCurrentState.setText("暂时无法获取您当前状态，您可以在“状态”设置您的状态。您的总流量如下:");
//				tvCurrentState.setText("暂时无法获取当前状态，您的总流量如下:");
				tvCurrentState.setText(Html.fromHtml("<u>" + "暂时无法获取当前状态，您的总流量如下:"+ "</u>"));
				for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
					totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
					totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
					awayFromEndDay = mSmsProdInfo.getRemainDay();
				}
			}
		//2，3g  情况下                                                        //#############第1层判断   （3）2g,2g情况
		}else if (networkStatue==NetWorkState._2G||networkStatue==NetWorkState._3G) {
			if (currentPosition==LocationSetting.IN_GD) {      //第1-3-1层判断============    广东省，2,3g
				if (ifIdleTime) {           //  广东省，2,3g  ,***********闲时
					if (networkStatue==NetWorkState._3G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省内，3G，闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省内，3G，闲时"); // 抗锯齿
						for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
							if ("省内3G".equals(mSmsProdInfo.getProdType())||"省内通用".equals(mSmsProdInfo.getProdType())||"省内2/3G".equals(mSmsProdInfo.getProdType())
									||"国内通用".equals(mSmsProdInfo.getProdType())||"国内2/3G".equals(mSmsProdInfo.getProdType())
										||"省内夜间".equals(mSmsProdInfo.getProdType())) {
								totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
								totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
								awayFromEndDay = mSmsProdInfo.getRemainDay();
							}
						}
					}else if (networkStatue==NetWorkState._2G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省内，2G，闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省内，2G，闲时"); // 抗锯齿
						for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
							if ("省内通用".equals(mSmsProdInfo.getProdType())||"省内2/3G".equals(mSmsProdInfo.getProdType())
									||"国内通用".equals(mSmsProdInfo.getProdType())||"国内2/3G".equals(mSmsProdInfo.getProdType())
										||"省内夜间".equals(mSmsProdInfo.getProdType())) {
								totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
								totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
								awayFromEndDay = mSmsProdInfo.getRemainDay();
							}
						}
					}
					
				}else {         //  广东省，2,3g  ,***********非闲时
					if (networkStatue==NetWorkState._3G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省内，3G，非闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省内，3G，非闲时"); // 抗锯齿
						for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
							if ("省内3G".equals(mSmsProdInfo.getProdType())||"省内通用".equals(mSmsProdInfo.getProdType())||"省内2/3G".equals(mSmsProdInfo.getProdType())
									||"国内通用".equals(mSmsProdInfo.getProdType())||"国内2/3G".equals(mSmsProdInfo.getProdType())) {
								totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
								totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
								awayFromEndDay = mSmsProdInfo.getRemainDay();
							}
						}
					}else if (networkStatue==NetWorkState._2G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省内，2G，非闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省内，2G，非闲时"); // 抗锯齿
						for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
							if ("省内通用".equals(mSmsProdInfo.getProdType())||"省内2/3G".equals(mSmsProdInfo.getProdType())
									||"国内通用".equals(mSmsProdInfo.getProdType())||"国内2/3G".equals(mSmsProdInfo.getProdType())) {
								totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
								totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
								awayFromEndDay = mSmsProdInfo.getRemainDay();
							}
						}
					}
					
				}
			}else if(currentPosition==LocationSetting.OUT_GD){              //===========    广东省外，2,3g
				if (ifIdleTime) {           //  广东省，2,3g  ,******闲时
					if (networkStatue==NetWorkState._2G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省外，2G，闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省外，2G，闲时"); // 抗锯齿
					}else if (networkStatue==NetWorkState._3G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省外，3G，闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省外，3G，闲时"); // 抗锯齿
					}
				}else {         //  广东省，2,3g  ,*******非闲时
					if (networkStatue==NetWorkState._2G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省外，2G，非闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省外，2G，非闲时"); // 抗锯齿
					}else if (networkStatue==NetWorkState._3G) {
						tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 广东省外，3G，非闲时"+ "</u>")); // 抗锯齿
//						tvCurrentState.setText("当前状态: 广东省外，3G，非闲时"); // 抗锯齿
					}
				}
				for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
					if ("国内通用".equals(mSmsProdInfo.getProdType())||"国内2/3G".equals(mSmsProdInfo.getProdType())) {
						totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
						totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
						awayFromEndDay = mSmsProdInfo.getRemainDay();
					}
				}
			}else {                                                                    // =================地理未位置获取不到
				tvCurrentState.setText(Html.fromHtml("<u>" + "暂时无法获取您当前状态，您可以在“状态”设置您的状态。您的总流量如下:"+ "</u>")); // 抗锯齿
//				tvCurrentState.setText("暂时无法获取您当前状态，您可以在“状态”设置您的状态。您的总流量如下:");
				for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
					totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
					totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
					awayFromEndDay = mSmsProdInfo.getRemainDay();
				}
			}
		}else if (networkStatue==NetWorkState.NO_NETWORK) {
//			tvCurrentState.setText("当前状态: 无网络。您的总流量如下:");
			tvCurrentState.setText(Html.fromHtml("<u>" + "当前状态: 无网络。您的总流量如下::"+ "</u>")); // 抗锯齿
			for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
				totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
				totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
				awayFromEndDay = mSmsProdInfo.getRemainDay();
			}
		}else {
			tvCurrentState.setText(Html.fromHtml("<u>" + "暂时无法获取当前状态，您的总流量如下:"+ "</u>")); // 抗锯齿
//			tvCurrentState.setText("暂时无法获取当前状态，您的总流量如下:");
			for (SMSProdInfo mSmsProdInfo : smsProdInfos) {
					totalAllFlow = totalAllFlow + mSmsProdInfo.getMonthAllFlow();
					totalRemainFlow = totalRemainFlow + mSmsProdInfo.getMonthRemainFlow();
					awayFromEndDay = mSmsProdInfo.getRemainDay();
			}
		}
		totalRemainFlow = totalRemainFlow/1024;
		totalAllFlow = totalAllFlow/1024;
		//设置流量套餐的总共剩余
		if (totalRemainFlow!=0) {
			tvRemainFlow.setText(dFormat.format(totalRemainFlow)+"");
		}else {
			tvRemainFlow.setText(0.00+"");
		}
		//设置流量套餐的总共流量
		if (totalAllFlow!=0) {
			tvMonthAllFlow.setText(dFormat.format(totalAllFlow)+"");
		}else {
			tvMonthAllFlow.setText(0.00+"");
		}
		//超出套餐流量
		if (exceedFlow==0) {
			tvExceedFlow.setText("0.00");
		} else {
			tvExceedFlow.setText(dFormat.format(exceedFlow/(1024*1024))+"");
			tvExceedFlow.setTextColor(Color.rgb(255, 0, 0));
		}
		tvRemainDays.setText(awayFromEndDay+"");
		//设置套餐剩余天数
		if (awayFromEndDay==0) {
			tvEverydayFlow.setText(totalRemainFlow+"");
		}else {
			tvEverydayFlow.setText(dFormat.format(totalRemainFlow/awayFromEndDay)+"");
		}
		//设置套餐  本月已用
		float pecert = 0l;
		if (totalAllFlow!=0) {
			pecert = (float) ((totalAllFlow-totalRemainFlow)*100/totalAllFlow);
			tvPercent.setText(dFormat.format(pecert)+"%");
		}else {
			tvPercent.setText("0.00%");
		}
		float rotate1 = 0; // 开始是0度
		// 计算所占百分比
		if (smsProdInfos!=null&&smsProdInfos.size() > 0) {
			rotate1 = pecert;
			if (rotate1>=100) {
				rotate1 = 100;
			}
		showPointView(rotate1);
		rotate1 = rotate1 * 220 / 110;
		RotateAnimation rotateAnim = new RotateAnimation(
				fromDegrees, rotate1, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		fromDegrees = rotate1;
		rotateAnim.setDuration(500);
		needle.setAnimation(rotateAnim);
		needle.startAnimation(rotateAnim);
		rotateAnim.setFillAfter(true);
		
		tvCurrentState.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tvStateOnClick();
			}
		});
		}
	}
	
	private void showPointView(double percent) {
		if (percent < 80) {
			clockBg.setBackgroundResource(R.drawable.point_bg_color);
			needle.setBackgroundResource(R.drawable.clock_green_neddle);
			
			if (networkStatue == NetWorkState._4G) {
//				tvCurrentState.setOnClickListener(appDownLoadListener);
				ivTips.setOnClickListener(appDownLoadListener);
				ivTips.setBackgroundResource(R.drawable.tip_green);
			}else {
//				tvCurrentState.setOnClickListener(recomand4GListener);
				ivTips.setBackgroundResource(R.drawable.tip_ftd);
				ivTips.setOnClickListener(recomand4GListener);
			}
		} else if (80 <= percent && percent < 100) {
			clockBg.setBackgroundResource(R.drawable.point_bg_color);
			needle.setBackgroundResource(R.drawable.clock_green_neddle);
		
			if (networkStatue == NetWorkState._4G) {
//				tvCurrentState.setOnClickListener(orderPackageListener);
				ivTips.setBackgroundResource(R.drawable.tip_orange);
				ivTips.setOnClickListener(orderPackageListener);
			}else {
//				tvCurrentState.setOnClickListener(recomand4GListener);
				ivTips.setBackgroundResource(R.drawable.tip_ftd);
				ivTips.setOnClickListener(recomand4GListener);
			}
		} else if (100 <= percent) {
			clockBg.setBackgroundResource(R.drawable.point_bg_color);
			needle.setBackgroundResource(R.drawable.clock_green_neddle);
			
			if (networkStatue==NetWorkState._4G) {
				ivTips.setBackgroundResource(R.drawable.tip_red);
//				tvCurrentState.setOnClickListener(orderPackageListener);
				ivTips.setOnClickListener(orderPackageListener);
			}else {
				ivTips.setBackgroundResource(R.drawable.tip_ftd);
//				tvCurrentState.setOnClickListener(recomand4GListener);
				ivTips.setOnClickListener(recomand4GListener);
			}
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private int getNetWorkType(Context context) {
		int type = NetWorkState.OTHER;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int currentType = telephonyManager.getNetworkType();
		switch (currentType) {
		
			case TelephonyManager.NETWORK_TYPE_EDGE:        //移动2G
				type =  NetWorkState._2G; // ~ 50-100 kbps
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:         //移动2G
				type =   NetWorkState._2G; // ~ 100 kbps
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:     //移动3G
				type =   NetWorkState._3G; // ~ 2-14 Mbps
				break;
			case 17:     //移动3G
				type =   NetWorkState._3G; // 
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:             //移动4g
				type =   NetWorkState._4G;
				break;
			default:
				break;
		}
		return type;
	}
	/**
	 * 
	 * @param context
	 * @return 网络状态
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
        return NetWorkState.NO_NETWORK;
	}
	private Tab1Callback callback;// 接口
	public interface Tab1Callback {
		public void tab1_callback();
	}
	
	/**
	 * 判定是否是闲时
	 */
	private boolean getIfIdleTime() {
		int idleTimeSetting = ApplicationController.getInstance().getIdleSetting();
		if (idleTimeSetting==IdleSetting.AUTO) {
			return JUtil.isIdleTime();  //获取当前时间是不是闲事
		//指定的话的话赋值到指定网络
		}else {
			//如果是闲时
			if (idleTimeSetting==IdleSetting.IDLE) {
				return true;
			//非闲时
			}else {
				return false;
			}
		}
	}
	
	public void tvStateOnClick() {
		Intent intent = new Intent(getActivity(),StatusSettingActivity.class);
		startActivity(intent);
		bottomInAnimation();
	}
}
