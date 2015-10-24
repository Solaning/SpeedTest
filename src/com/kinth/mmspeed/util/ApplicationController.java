package com.kinth.mmspeed.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.LocationSetting;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Administrator
 */
public class ApplicationController extends Application {
	// **************************************************************************************************************************
	private static final String TAG = "ApplicationController";
	private static ApplicationController instance;
	private String currentPhone = "";
	public static String DATABASE_NAME = "sixin.db";
	private int networkSetting = 0;   //默认为自动
	private int locationSetting = 0;   //
	private int idleSetting = 0;
	private int shareWXType = 1;
	
	public static final int AcountState = 0;
	private int currentPosition = LocationSetting.LOADING ;   //0,自动，1.获取中； 2广东  3.广东外 -1  获取地理位置失败
	
	public static synchronized ApplicationController getInstance() {
		if (instance == null)
			instance = new ApplicationController();
		return instance;
	}
	
	/** 
	 * Global request queue for Volley 
	 * @2014-11-10
     */  
    private RequestQueue mRequestQueue;  
    
    /** 
     * @return The Volley Request queue, the queue will be created if it is null 
     */  
    public RequestQueue getRequestQueue() {  
        // lazy initialize the request queue, the queue instance will be  
        // created when it is accessed for the first time  
        if (mRequestQueue == null) {  
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());  
        }  
        return mRequestQueue;  
    }  
  
    /** 
     * Adds the specified request to the global queue, if tag is specified 
     * then it is used else Default TAG is used. 
     *  
     * @param req 
     * @param tag 
     */  
    public <T> void addToRequestQueue(Request<T> req, String tag) {  
        // set the default tag if tag is empty  
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);  
  
        VolleyLog.d("Adding request to queue: %s", req.getUrl());  
  
        getRequestQueue().add(req);  
    }  
  
    /** 
     * Adds the specified request to the global queue using the Default TAG. 
     *  
     * @param req 
     * @param tag 
     */  
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty  
        req.setTag(TAG);  

        getRequestQueue().add(req);  
    }  
  
    /** 
     * Cancels all pending requests by the specified TAG, it is important 
     * to specify a TAG so that the pending/ongoing requests can be cancelled. 
     *  
     * @param tag 
     */  
    public void cancelPendingRequests(Object tag) {  
        if (mRequestQueue != null) {  
            mRequestQueue.cancelAll(tag);  
        }  
    } 
	
	public int getShareWXType() {
		return shareWXType;
	}

	public void setShareWXType(int shareWXType) {
		this.shareWXType = shareWXType;
	}

	public int getCurrentPosition() {
		int m =getLocationSetting();  //获取地理位置
		if (m==LocationSetting.AUTO) {
			//自动就不赋值currentPosition是什么就是什么
			
		}else if (m==LocationSetting.IN_GD) {
			currentPosition = LocationSetting.IN_GD;
		}else if (m==LocationSetting.OUT_GD) {
			currentPosition = LocationSetting.OUT_GD;
		}
		return currentPosition;
	}
	
	/**
	 * 设置当前地理位置
	 * @param currentPosition
	 */
	public synchronized void setCurrentPosition(int currentPosition) {
		if (this.currentPosition!=currentPosition) {
			this.currentPosition= currentPosition;
			//发送广播   通知位置已经改变
		}
	}

	public int getLocationSetting() {
		int  locationSetting= JUtil.getSharePreInt(getApplicationContext(), APPConstant.SP_NAME, APPConstant.LOCATION_SETTING);
		return locationSetting;
	}

	public void setLocationSetting(int locationSetting) {
		JUtil.putSharePre(getApplicationContext(), APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, APPConstant.LOCATION_SETTING);
		this.locationSetting = locationSetting;
	}

	public int getIdleSetting() {
		int  idleSetting= JUtil.getSharePreInt(getApplicationContext(), APPConstant.SP_NAME, APPConstant.IDLE_SETTING);
		return idleSetting;
	}

	public void setIdleSetting(int idleSetting) {
		JUtil.putSharePre(getApplicationContext(), APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, networkSetting);
		this.idleSetting = idleSetting;
	}
	
	public  int getNetworkSetting() {
		int network= JUtil.getSharePreInt(getApplicationContext(), APPConstant.SP_NAME, APPConstant.NETWORK_SETTING);
		return network;
	}
	
	public  void setNetworkSetting(int networkSetting) {
		JUtil.putSharePre(getApplicationContext(), APPConstant.SP_NAME, APPConstant.NETWORK_SETTING, networkSetting);
		this.networkSetting = networkSetting;
	}
	
	public String getCurrentPhone() {
		return JUtil.getSharePreStr(getApplicationContext(),
				APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
	}

	public synchronized void setCurrentPhone(String currentPhone) {
		this.currentPhone = currentPhone;
		JUtil.putSharePre(getApplicationContext(), APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE, currentPhone);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initImageLoader(getApplicationContext());
		//网络探针
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, APPConstant.NETWORK_PROBE,
				new RequestCallBack<String>() {
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
//						Log.e("responseInfo.result", "" + responseInfo.result);
					}

					@Override
					public void onStart() {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
		// 在使用百度 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		instance = null;
	}

	//@2014-08-16 sola
	private int numOfMoments;//朋友圈的消息数
	private int numOfMessageCentre;//消息中心的消息数
	private boolean hasActiveOrLogin;// 已经激活或者登陆
	private int canSkip;//绑定号码是否可以跳过
	
	public int getNumOfMoments() {
		return numOfMoments;
	}

	public void setNumOfMoments(int numOfMoments) {
		this.numOfMoments = numOfMoments;
	}

	public int getNumOfMessageCentre() {
		return numOfMessageCentre;
	}

	public void setNumOfMessageCentre(int numOfMessageCentre) {
		this.numOfMessageCentre = numOfMessageCentre;
	}

	public boolean isHasActiveOrLogin() {
		return hasActiveOrLogin;
	}

	public void setHasActiveOrLogin(boolean hasActiveOrLogin) {
		this.hasActiveOrLogin = hasActiveOrLogin;
	}
	
	public int getCanSkip() {
		return canSkip;
	}

	public void setCanSkip(int canSkip) {
		this.canSkip = canSkip;
	}

	// **************************************************************************************************************************
	// start客户端通信的公共信息
	private String dev = "";
	private String resolution = "";
	private String mac = "";
	private int version;
	private String uid = "";
	private String ANDROID_API_VERSION = android.os.Build.VERSION.SDK;
	private String NETWORK_OPERATOR = "";// 网络运营商
	private final String interfaceApi = "3.1";

	public String getInterfaceApi() {
		return interfaceApi;
	}
	
	public String getANDROID_API_VERSION() {
		return ANDROID_API_VERSION;
	}

	public String getDev() {
		return dev;
	}

	public void setDev(String Dev) {
		dev = Dev;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String Resolution) {
		resolution = Resolution;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String Mac) {
		mac = Mac;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int Version) {
		version = Version;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String Uid) {
		uid = Uid;
	}
	
	public String getNetworkOperator() {
		return UtilFunc.encode(NETWORK_OPERATOR);
	}

	public void setNetworkOperator(String network_operator) {
		NETWORK_OPERATOR = network_operator;
	}

	public static final boolean isTestingStage = false;

	public static final String projectName = "speedTest";
	
	public static final String DEFAULT_PING_ADDRESS = "42.121.0.186";// 默认ping地址
	public static final String DEFAULT_UPLOAD_URL = "http://112.96.28.236/speedtest/upload.asp";
	public static final String DEFAULT_DOWNLOAD_URL = "http://speedtest.telin.hk/speedtest/random4000x4000.jpg";
	public static final String QUERY_GPRS_URL = APPConstant.HOST + "MMService/services/queryGPRS.jsp";
	public static final String FLOW_PREFERENTIAL_URL = APPConstant.HOST + "MMService/services/sys/getFlowPreferentialList.jsp";//获取流量优惠文章列表
	
	private String downloadServerURL = "";
	private String uploadServerURL = "";
	private String pingIPAddress = "";

	public static final float ScaleRate = 0.84f;// 图片的大小为屏幕宽的0.75f
	public static final int PerAngleIncrement = 30;// 每个刻度区间对应的角度值
	public static final float MinDegree = -120f;// 最小角度
	public static final float MaxDegree = 120f;// 最大角度
	private String password;// 用户密码
	private boolean hasRecord = false;// 是否曾经测过，有记录了

	public String getPassword() {
		if (password == null || password.equals("")) {
			password = SingletonSharedPreferences.getInstance().getPassword();
		}
		return password;
	}

	public synchronized String getDownloadServerURL() {
		if (downloadServerURL == null || downloadServerURL.equals("")
				|| downloadServerURL.equals("null")) {// 没有就去SharedPreferences取
			downloadServerURL = SingletonSharedPreferences.getInstance()
					.getDownloadServerURL();
		}
		if (downloadServerURL == null || downloadServerURL.equals("")
				|| downloadServerURL.equals("null")) {
			downloadServerURL = DEFAULT_DOWNLOAD_URL;
		}
		return downloadServerURL;
	}

	public synchronized void setDownloadServerURL(String downloadServerURL) {
		this.downloadServerURL = downloadServerURL;
	}

	public synchronized String getUploadServerURL() {
		if (uploadServerURL == null || uploadServerURL.equals("")
				|| uploadServerURL.equals("null")) {// 没有就去SharedPreferences取
			uploadServerURL = SingletonSharedPreferences.getInstance()
					.getUploadServerURL();
		}
		if (uploadServerURL == null || uploadServerURL.equals("")
				|| uploadServerURL.equals("null")) {
			uploadServerURL = DEFAULT_UPLOAD_URL;
		}
		return uploadServerURL;
	}

	public synchronized void setUploadServerURL(String uploadServerURL) {
		this.uploadServerURL = uploadServerURL;
	}

	public synchronized String getPingIPAddress() {
		if (pingIPAddress == null || pingIPAddress.equals("")
				|| pingIPAddress.equals("null")) {// 没有就去SharedPreferences取
			pingIPAddress = SingletonSharedPreferences.getInstance()
					.getPingIPAddress();
		}
		if (pingIPAddress == null || pingIPAddress.equals("")
				|| pingIPAddress.equals("null")) {
			pingIPAddress = DEFAULT_PING_ADDRESS;
		}
		return pingIPAddress;
	}

	public synchronized void setPingIPAddress(String pingIPAddress) {
		this.pingIPAddress = pingIPAddress;
	}

	public boolean isHasRecord() {
		return hasRecord;
	}

	public void setHasRecord(boolean hasRecord) {
		this.hasRecord = hasRecord;
	}

	// ----------------------------------------------------------
	/**
	 * 缓存框架注册
	 * @author BJ
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// Create default options which will be used for every
		// displayImage(...) call if no options will be passed to this method
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPoolSize(3)// default
				.threadPriority(Thread.NORM_PRIORITY - 1)// default
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSizePercentage(13) // default
				.defaultDisplayImageOptions(defaultOptions)
//				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
