package com.kinth.mmspeed;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.MyFragmentPagerAdapter;
import com.kinth.mmspeed.bean.AdArticleInfo;
import com.kinth.mmspeed.bean.DefectRivalBean;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.VersionInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.Constants;
import com.kinth.mmspeed.constant.LocationSetting;
import com.kinth.mmspeed.fragment.Tab2_BaseFragment;
import com.kinth.mmspeed.fragment.Tab3_Fragment;
import com.kinth.mmspeed.fragment.Tab4_Fragment;
import com.kinth.mmspeed.fragment.Tab1_MainFragment;
import com.kinth.mmspeed.fragment.NoFlowFragment.OrderFlowPackage;
import com.kinth.mmspeed.fragment.QueryFlowFailFragment.QueryFailOrderFlowPackage;
import com.kinth.mmspeed.fragment.SmartKanBanFragment.Tab1Callback;
import com.kinth.mmspeed.hk.Action;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.network.AsyncNetworkManager.UpdateCallback;
import com.kinth.mmspeed.network.AsyncNetworkManager.MessageCentreHasNewCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.MomentsHasNewCallBack;
import com.kinth.mmspeed.ui.CustomViewPager;
import com.kinth.mmspeed.util.AnimationUtil;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.CommonFunction;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.Md5Util;
import com.kinth.mmspeed.util.UpdateManager;
import com.kinth.mmspeed.util.UserFriendUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.kinth.mmspeed.util.DateUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sola.code.ydlly_assist.bean.KeyValue;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

@ContentView(R.layout.activity_main)
public class SpeedMainActivity extends FragmentActivity implements
		Tab1Callback, OrderFlowPackage ,QueryFailOrderFlowPackage ,IWeiboHandler.Response {
	
	@ViewInject(R.id.tab_content)
	private CustomViewPager mPager;
	
	@ViewInject(R.id.bottomRg)
	private RadioGroup bottomRg;
	
	@ViewInject(R.id.rbOne)
	private RadioButton rbOne;
	
	@ViewInject(R.id.rbTwo)
	private RadioButton rbTwo;
	
	@ViewInject(R.id.rbThree)
	private RadioButton rbThree;
	
	@ViewInject(R.id.rbFour)
	private RadioButton rbFour;
	
	@ViewInject(R.id.iv_find_has_new)
	private ImageView findHasNew;
	//----
	public Context mContext;
	private MessageReceiver mReceiver;
	public ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
	private MyFragmentPagerAdapter mAdapter;
	
	// 定位相关
	public boolean m_bKeyRight = true;
	public LocationClient mLocationClient;    //百度定位
	public BDLocationListener myLocationListener = new MyLocationListener();//百度定位监听
	public OnGetGeoCoderResultListener mGeoCoderListener = new MyGeoCoderListener();//反地理编码监听
	public GeoCoder mGeoCoder = null; // 搜索模块，也可去掉地图模块独立使用
	private DbUtils mDbUtils;
	
	/** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    private boolean isInstalledWeibo;
    
    //add @2014-10-30
    private boolean isLowSpeedNetwork;//2g等低网速下可以取消升级
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		ViewUtils.inject(this);
		
		// 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
        isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        if(isInstalledWeibo){
        	mWeiboShareAPI.registerApp();
        }
        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (isInstalledWeibo && savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
		
		//注册监听
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Action.HAS_NEW_MESSAGE);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new MessageReceiver();
		registerReceiver(mReceiver, iFilter);
		
		mDbUtils = DbUtils.create(mContext, APPConstant.DATABASE_NAME);
		
		mFragments.add(new Tab1_MainFragment());
		mFragments.add(new Tab2_BaseFragment());
//		mFragments.add(new Tab2_Fragment());
		mFragments.add(new Tab3_Fragment());
		mFragments.add(new Tab4_Fragment());
		
		if (ApplicationController.getInstance().getDev().equals("")) {
			UtilFunc.getTerminalEndCommonInfo(this);
		}
		
		mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
		mPager.setAdapter(mAdapter);
		mPager.setScrollable(false);
		mPager.setOffscreenPageLimit(0);
		bottomRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbOne:
					mPager.setCurrentItem(0,false);
					break;
				case R.id.rbTwo:
					mPager.setOffscreenPageLimit(4);
					mPager.setCurrentItem(1,false);
					break;
				case R.id.rbThree:
					mPager.setOffscreenPageLimit(4);
					mPager.setCurrentItem(2,false);
					break;
				case R.id.rbFour:
					mPager.setOffscreenPageLimit(4);
					mPager.setCurrentItem(3,false);
					break;
				}
			}
		});
//		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this,
//				mFragments, R.id.tab_content, bottomRg);
//		tabAdapter
//				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
//					@Override
//					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
//							int checkedId, int index) {
////						System.out.println("Extra---- " + index+ " checked!!! ");
//						
//					}
//				});
		
		String userPhone = JUtil.getSharePreStr(SpeedMainActivity.this,
				APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
		if (!TextUtils.isEmpty(userPhone)) {//统计
			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
			keyValue.add(new KeyValue("statType","queryGPRS"));
			keyValue.add(new KeyValue("keyValue",userPhone));
			
			AsyncNetworkManager activeNetwork = new AsyncNetworkManager();
			activeNetwork.asynSendData(SpeedMainActivity.this, keyValue);
		}
		if(!ApplicationController.getInstance().isHasActiveOrLogin()){//判断是否激活or登陆
			new CommonFunction().activateHandle(null);
		}
		
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myLocationListener); // 注册监听函数
		InitLocation();
		mLocationClient.start();// 开启定位
		// 反地理编码
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(mGeoCoderListener);
		
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getAdPageInfo(SpeedMainActivity.this, new AsyncNetworkManager.getAdPageCallBack() {
			@Override
			public void getAdPageCallback(AdArticleInfo articleInfo) {
				if (articleInfo!=null) {
					
					AdArticleInfo mAdArticleInfo = new AdArticleInfo();
					try {
						mAdArticleInfo = mDbUtils.findFirst(AdArticleInfo.class);
						if (mAdArticleInfo!=null) {
							if (mAdArticleInfo.getId()!=articleInfo.getId()) {
								downLoadAdPic(articleInfo);
							}
						}else {
							downLoadAdPic(articleInfo);
						}
					} catch (DbException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		UserFriendUtil.checkUploadContract(SpeedMainActivity.this);
		List<KeyValue> parameters = new ArrayList<KeyValue>();
		parameters.add(new KeyValue("timeStamp",SingletonSharedPreferences.getInstance().getMessageCentreUpdateTimeStamp()));
		//消息中心更新
		asyncNetworkManager.checkMessageCentreHasNew(mContext, parameters, new MessageCentreHasNewCallBack() {
			
			@Override
			public void onMessageCentreHasNew(int count) {
				if(count >= 0){
					ApplicationController.getInstance().setNumOfMessageCentre(count);
					int allNew = ApplicationController.getInstance().getNumOfMoments() + ApplicationController.getInstance().getNumOfMessageCentre();
					if(allNew > 0){
						findHasNew.setVisibility(View.VISIBLE);
					}else{
						if(SingletonSharedPreferences.getInstance().isFlowGrouponClick()){//团购已经看了
							findHasNew.setVisibility(View.GONE);
						}else{
							findHasNew.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});
		
		UserAccount user = MainAccountUtil.getCurrentAccount(mContext);
		if (user != null) {
			List<KeyValue> keyValues = new ArrayList<KeyValue>();
			keyValues.add(new KeyValue("timeStamp",SingletonSharedPreferences.getInstance().getMomentsUpdateTimeStamp()));
			keyValues.add(new KeyValue("mobile",user.getMobile()));
			//朋友圈更新
			asyncNetworkManager.cheakMomentsHasNew(mContext, keyValues, new MomentsHasNewCallBack() {
				
				@Override
				public void onMomentsHasNew(int count) {
					if(count >= 0){
						ApplicationController.getInstance().setNumOfMoments(count);
						int numOfMessage = ApplicationController.getInstance().getNumOfMoments() + ApplicationController.getInstance().getNumOfMessageCentre();
						if(numOfMessage > 0){
							findHasNew.setVisibility(View.VISIBLE);
						}else{
							if(SingletonSharedPreferences.getInstance().isFlowGrouponClick()){//团购已经看了
								findHasNew.setVisibility(View.GONE);
							}else{
								findHasNew.setVisibility(View.VISIBLE);
							}
						}
					}
				}
			});
		}
		new UpdateTask().execute();//更新检测
	}
	
	//广播接收器
	public class MessageReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(mContext, "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置",Toast.LENGTH_LONG).show();
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(mContext, "网络出错",Toast.LENGTH_LONG).show();
			}
			
			//小红点刷新
			if (s.equals(Action.HAS_NEW_MESSAGE)) {
				int numOfMessage = ApplicationController.getInstance().getNumOfMoments() + ApplicationController.getInstance().getNumOfMessageCentre();
				if(numOfMessage > 0){
					findHasNew.setVisibility(View.VISIBLE);
				}else{
					//add @2014-11-21  再判断流量团购是否点击
					if(SingletonSharedPreferences.getInstance().isFlowGrouponClick()){
						findHasNew.setVisibility(View.GONE);
					}else{
						findHasNew.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}
	
	//设置百度定位的参数
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(10000);// 设置发起定位请求的间隔时间为10s
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);// 设置定位参数
	}

	/**
	 * 检测更新
	 * @author Sola
	 * 
	 */
	class UpdateTask extends AsyncTask<Void,Void,VersionInfo>{

		@Override
		protected VersionInfo doInBackground(Void... params) {
			boolean isNewVersionAvailable = SingletonSharedPreferences.getInstance().isNewVersionAvailable();//是否有新版本
			if(isNewVersionAvailable){
				String versionStr = SingletonSharedPreferences.getInstance().getNewVersionJson();
				if(TextUtils.isEmpty(versionStr)){
					isNewVersionAvailable = false;
					return null;
				}
				Gson gson = new Gson();
				VersionInfo versionInfo = null;
				try {
					versionInfo = gson.fromJson(versionStr,
						new TypeToken<VersionInfo>() {
						}.getType());
				}catch(JsonSyntaxException e){
					
				}
				if(versionInfo == null){
					isNewVersionAvailable = false;
					return null;
				}
				if(ApplicationController.getInstance().getVersion() < versionInfo.getVersionNo()){//当前版本小于服务器版本，如果网络是wifi、4g、3g不能取消
					String networkType = UtilFunc.getNetworkType(mContext);
					if(networkType.equals("2G") || networkType.equals("2G/3G")){
						isLowSpeedNetwork = true;
					}
					return versionInfo;
				}else{
					isNewVersionAvailable = false;
					return null;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(VersionInfo result) {
			if(result == null){
				// 检测更新
				final AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
				asyncNetworkManager.asynUpdateFromServer(mContext,
						new UpdateCallback() {
							@Override
							public void updateCallback(
									int hasNew,VersionInfo versionInfo) {
								if (hasNew == 1) {// 有新版本
									UpdateManager update = new UpdateManager(mContext,versionInfo,isLowSpeedNetwork);
									update.start();
								}else{//广告推送通知
									HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo(); 
									params.put("mobile", MainAccountUtil.getAvailablePhone(mContext));
									JsonObjectRequest req = new JsonObjectRequest(APPConstant.DEFECT_RIVAL, new JSONObject(params),  
									       new Response.Listener<JSONObject>() {
											@Override
											public void onResponse(JSONObject response) {
												DefectRivalBean defectRival = null;
												Gson gson = new Gson();
												Type type = new TypeToken<DefectRivalBean>(){}.getType();
												defectRival = gson.fromJson(response.toString(), type);
												if(defectRival != null){
													String lastDate = SingletonSharedPreferences.getInstance().getDefectCheckDate();
													if(TextUtils.isEmpty(lastDate)){//没有记录
														DefectDialog(defectRival.getTitle(),defectRival.getContent(),defectRival.getUrl());
													}else{
														String url = SingletonSharedPreferences.getInstance().getDefectDialogUrl();
														SingletonSharedPreferences.getInstance().setDefectDialogUrl(defectRival.getUrl());//保存这次的url
														if(TextUtils.isEmpty(url)){//上次的url为空，直接显示
															DefectDialog(defectRival.getTitle(),defectRival.getContent(),defectRival.getUrl());
														}else if(url.equals(defectRival.getUrl())){
															//时间转换
															int days = DateUtil.daysOfTwo(DateUtil.strToDate(lastDate), new Date());
															switch(defectRival.getRate()){
															case 0:
																return;
															case 1://一天显示一次  //判断时间
																if(days == 1){
																	DefectDialog(defectRival.getTitle(),defectRival.getContent(),defectRival.getUrl());
																}
																break;
															case 2://两天显示一次
																if(days >= 2){
																	DefectDialog(defectRival.getTitle(),defectRival.getContent(),defectRival.getUrl());
																}
																break;
															}
														}else {//跟上次不一样，直接显示
															DefectDialog(defectRival.getTitle(),defectRival.getContent(),defectRival.getUrl());
														}
													}
												}
												//保存检测的日期
												SingletonSharedPreferences.getInstance().setDefectCheckDate(DateUtil.getStringDateShort());
											}
											}, new Response.ErrorListener() {

												@Override
												public void onErrorResponse(VolleyError error) {
													VolleyLog.e("Error: ",
															error.getMessage());
													Log.e("response error",	"" + error.getMessage());
												}
											});  
									ApplicationController.getInstance().addToRequestQueue(req);
								}
							}
						});
			}else{//已知有更新，强制更新
				UpdateManager update = new UpdateManager(mContext,result,isLowSpeedNetwork);
				update.start();
			}
			super.onPostExecute(result);
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (AnimationUtil.ANIM_IN != 0 && AnimationUtil.ANIM_OUT != 0) {
			super.overridePendingTransition(AnimationUtil.ANIM_IN,
					AnimationUtil.ANIM_OUT);
			AnimationUtil.clear();
		}
	}

	/**
	 * 策反对话框
	 * @param title
	 * @param content
	 * @param url
	 */
	private void DefectDialog(final String title,String content,final String url){
		AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.setTitle(title);
		dialog.setMessage(content);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(TextUtils.isEmpty(url)){
							return;
						}
						Intent intent = new Intent(mContext,CommonWebViewActivity.class);
						intent.putExtra(CommonWebViewActivity.INTENT_TITLE, title);
						intent.putExtra(CommonWebViewActivity.INTENT_URL, url);
						startActivity(intent);
					}
				});
		if(!TextUtils.isEmpty(url)){
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
					new OnClickListener() {
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}
		dialog.show();
	}
	
	/**
	 * 退出程序对话框
	 */
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(SpeedMainActivity.this);
		builder.setMessage(getText(R.string.exit_tip).toString());
		builder.setTitle(getText(R.string.tips).toString());
		builder.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void tab1_callback() {
		rbThree.setChecked(true);
	}

	/**
	 * 分享功能
	 * 
	 * @param context
	 *            上下文
	 * @param activityTitle
	 *            Activity的名字
	 * @param msgTitle
	 *            消息标题
	 * @param msgText
	 *            消息内容
	 * @param imgPath
	 *            图片路径，不分享图片则传null
	 */
	public static void shareMsg(Context context, String activityTitle,
			String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGeoCoder.destroy();
		unregisterReceiver(mReceiver);
		ApplicationController.getInstance().setNumOfMessageCentre(0);
		ApplicationController.getInstance().setNumOfMoments(0);
	}
	

	/**
	 * 下载广告图片
	 * @param articleInfo
	 */
	private void downLoadAdPic(final AdArticleInfo articleInfo) {
		
		FinalHttp fh = new FinalHttp();
		String md5url = Md5Util.md5s(articleInfo.getTitleURLString());
		
		final String receRecordPath = "/LiuLiangYi/Data/Ad/"
				+ md5url + "._jpg";
		File file = new File(receRecordPath) ;
		if (file.exists()) {        //如果文件存在就不需要下载
			return;
		}
		if (JUtil.getSDPath()==null||JUtil.getSDPath().equals("")) {
			return;
		}
		
		fh.download(articleInfo.getTitleURLString(), JUtil.getSDPath() + receRecordPath,
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						// textView.setText("下载进度："+current+"/"+count);
					}
					@Override
					public void onSuccess(File t) {
						try {
							mDbUtils.deleteAll(AdArticleInfo.class);
							mDbUtils.save(articleInfo);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				});
	}
	
	@Override
	public void oderPackage() {
		rbThree.setChecked(true);
	}
	
	@Override
	public void queryFailOderPackage() {
		rbThree.setChecked(true);
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				ApplicationController.getInstance().setCurrentPosition(LocationSetting.FAILE);
				return;
			}
			switch (location.getLocType()) {
			case BDLocation.TypeCacheLocation://缓存定位
				SingletonSharedPreferences.getInstance().setLatitude(
						location.getLatitude());
				SingletonSharedPreferences.getInstance().setLontitude(
						location.getLongitude());
				break;
			case BDLocation.TypeOffLineLocation://离线定位
				SingletonSharedPreferences.getInstance().setLatitude(
						location.getLatitude());
				SingletonSharedPreferences.getInstance().setLontitude(
						location.getLongitude());
				break;
			case BDLocation.TypeGpsLocation://GPS定位
				SingletonSharedPreferences.getInstance().setLatitude(
						location.getLatitude());
				SingletonSharedPreferences.getInstance().setLontitude(
						location.getLongitude());
				
				LatLng ptCenter = new LatLng(location.getLatitude(),location.getLongitude());
				// 反Geo搜索
				mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
						.location(ptCenter));
//				GeoPoint ptCenter = new GeoPoint(
//						(int) (location.getLatitude() * 1e6),
//						(int) (location.getLongitude() * 1e6));
//				// 反Geo搜索
//				mSearch.reverseGeocode(ptCenter);
				break;
			case BDLocation.TypeNetWorkLocation://网络定位
				SingletonSharedPreferences.getInstance().setLatitude(
						location.getLatitude());
				SingletonSharedPreferences.getInstance().setLontitude(
						location.getLongitude());
				SingletonSharedPreferences.getInstance().setLocationAddress(
						location.getAddrStr());
				if(TextUtils.isEmpty(location.getAddrStr())){
					return;
				}
				if (location.getAddrStr().toString().contains("广东")) {
					ApplicationController.getInstance().setCurrentPosition(LocationSetting.IN_GD);
				}else {
					ApplicationController.getInstance().setCurrentPosition(LocationSetting.OUT_GD);
				}
				//网络定位
				Intent intent = new Intent();
				intent.setAction(Action.LOCATION_FINISH);
				sendBroadcast(intent);
				break;
			case BDLocation.TypeNone://都是没有的，定位失败
			case BDLocation.TypeNetWorkException:
			case BDLocation.TypeOffLineLocationFail:
			case BDLocation.TypeCriteriaException:
			case BDLocation.TypeOffLineLocationNetworkFail:
			case BDLocation.TypeServerError:
				Log.e("Type",""+location.getLatitude());
				Log.e("Type",""+location.getLongitude());
				Log.e("location.getAddrStr()",""+location.getAddrStr());
//				JUtil.showMsg(SpeedMainActivity.this, "定位失败");
				ApplicationController.getInstance().setCurrentPosition(LocationSetting.FAILE);
				break;
			default:
//				JUtil.showMsg(SpeedMainActivity.this, "定位失败");
				ApplicationController.getInstance().setCurrentPosition(LocationSetting.FAILE);
				break;
			}
		}
	}
	
	public class MyGeoCoderListener implements OnGetGeoCoderResultListener{

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				//未能找到结果
				return;
			}
			String strInfo = result.getAddress();
			SingletonSharedPreferences.getInstance()
					.setLocationAddress(strInfo);
			Intent intent = new Intent();//发广播通知
			intent.setAction(Action.LOCATION_FINISH);
			sendBroadcast(intent);
			Toast.makeText(mContext, result.getAddress(),
					Toast.LENGTH_LONG).show();
			
//			if (error != 0) {
//				String str = String.format("错误号：%d", error);
//				Log.i("onGetAddrResult", str);
//				return;
//			}
//			if (res.type == MKAddrInfo.MK_GEOCODE) {
//				// 地理编码：通过地址检索坐标点
//				String strInfo = String.format("纬度：%f 经度：%f",
//						res.geoPt.getLatitudeE6() / 1e6,
//						res.geoPt.getLongitudeE6() / 1e6);
//				Log.i("地理编码：通过地址检索坐标点", strInfo);
//			}
//
//			if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
//				// 反地理编码：通过坐标点检索详细地址及周边poi
//				String strInfo = res.strAddr;
//				SingletonSharedPreferences.getInstance()
//						.setLocationAddress(strInfo);
//				Intent intent = new Intent();//发广播通知
//				intent.setAction(Action.LOCATION_FINISH);
//				sendBroadcast(intent);
//				Log.i("MKAddrInfo.MK_REVERSEGEOCODE", strInfo);
//			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.getBooleanExtra("CheckTab2", false)){//选中第二个
			rbTwo.setChecked(true);
			return;
		}
		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
                    getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
		
	}

}
