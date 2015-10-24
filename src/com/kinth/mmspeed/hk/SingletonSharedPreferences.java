package com.kinth.mmspeed.hk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.util.ApplicationController;

public class SingletonSharedPreferences {
	// 关于界面的
	private static final String Version_Code = "Version_Code";// 版本号
	private static final String Screen_Width = "Screen_Width";// 屏幕宽
	private static final String Screen_Height = "Screen_Height";// 屏幕高
	private static final String Has_Writed_Width = "Has_Writed_Width";// 是否写入了Screen_Width
	private static final String Has_Writed_Height = "Has_Writed_Height";// 是否写入了Screen_Height
	private static final String Scale_Rate = "Scale_Rate";// 针对当前手机的缩放比率
	private static final String Has_Scale_Rate = "Has_Scale_Rate";// 是否写入了Scale_Rate
	private static final String Bitmap_Width = "Bitmap_Width";// 保存一个用于缩放比率的图片的宽
	private static final String Has_Bitmap_Width = "Has_Bitmap_Width";//
	private static final String Has_Status_Bar_Height = "Has_Status_Bar_Height";// 有手机状态栏的高度
	private static final String Status_Bar_Height = "Status_Bar_Height";// 手机状态栏高度
	private static final String Screen_Center_Height = "Screen_Center_Height";// 屏幕中点的高度
	private static final String Has_Screen_Center_Height = "Has_Screen_Center_Height";// 有手机屏幕中点的高度
	private static final String Dial_Style = "Dial_Style";// 仪表盘的显示样式
	private static final String PassWord = "PassWord";// 用户密码
	private static final String Activated = "Activated";// 是否激活
	private static final String DownloadServerURL = "DownloadServerURL";// 下载测速的地址
	private static final String UploadServerURL = "UploadServerURL";// 上传测速的地址
	private static final String PingIPAddress = "PingIPAddress";// ping的地址
	private static final String Value_Of_Ping = "Value_Of_Ping";// 上次ping的值
	private static final String Value_Of_Download = "Value_Of_Download";// 上次下载的值
	private static final String Value_Of_Upload = "Value_Of_Upload";// 上次下载的值
	private static final String Unit_Mode = "Unit_Mode";// 切换单位
	private static final String First_Load = "First_Load";// 第一次运行
	private static final String Added_Phone_Count = "Added_Phone_Count";// 已经添加的手机号码数
	private static final String Lontitude = "Lontitude";// 经度
	private static final String Latitude = "Latitude";// 纬度
	private static final String Location_Address = "Location_Address";// 定位的地址
//	private static final String addMobileExtraInfo = "addMobileExtraInfo";//是否已经上传了额外的数据统计
	//add @14-06-12
	private static final String RMG_MEMBER_MAX_COUNT = "RMG_MEMBER_MAX_COUNT";//流量共享的最大人数限制
	//add @14-06-27
	private static final String RMG_DONATION_MEMBER_MAX_COUNT = "RMG_DONATION_MEMBER_MAX_COUNT";//流量转赠的最大人数限制
	private static final String SHOW_RMG_FLOW_PRESENTATION = "RMG_SHOW_FLOW_PRESENTATION";//是否显示了流量共享的介绍页
	private static final String SHOW_RMG_DONATION_PRESENTATION = "SHOW_RMG_DONATION_PRESENTATION";//是否显示了流量转赠的介绍页
	//add @14-07-04
	private static final String IS_OPEN_FLOW_RED_PACKET = "IS_OPEN_FLOW_RED_PACKET";//是否打开流量红包
	//add @2014-08-06
	private static final String HAS_UPLOAD_CONTACTS = "HAS_UPLOAD_CONTACTS";//是否上传通讯录
	//add @2014-09-04
	private static final String TIME_STAMP_MESSAGE_CENTRE_UPDATE = "TIME_STAMP_MESSAGE_CENTRE_UPDATE";//消息中心更新的时间戳
	private static final String TIME_STAMP_MOMENTS_UPDATE = "TIME_STAMP_MOMENTS_UPDATE";//朋友圈更新的时间戳
	//add @2014-10-08
	private static final String MORE_PACKAGE = "MORE_PACKAGE";//更多业务办理，互推
	private static final String MORE_PACKAGE_URL = "MORE_PACKAGE_URL";
	//add @2014-10-30
	private static final String NEW_VERSION_JSON = "NEW_VERSION_JSON";//新版本json信息
	private static final String IS_NEW_VERSION_AVAILABLE = "IS_NEW_VERSION_AVAILABLE";//是否有新版本
	//add @2014-11-14
	private static final String DEFECT_CHECK_DATE = "DEFECT_CHECK_DATE";//策反检测日期
	private static final String DEFECT_DIALOG_URL = "DEFECT_DIALOG_URL";//策反的调整url
	//add @2014-11-21
	private static final String IS_FLOW_GROUPON_CLICK = "IS_FLOW_GROUPON_CLICK";//流量团购是否已经点击
	//add @2015-01-04
	private static final String MY_FAMILY_MEMBER_QUERY_TIME = "MY_FAMILY_MEMBER_QUERY_TIME";//我的家庭查询成员的时间

	private static SingletonSharedPreferences instance = new SingletonSharedPreferences();
	private Context context = null;
	private static SharedPreferences mainSharedPreferences;
	private static Editor mainEditor;

	public Editor getMainEditor() {
		return mainEditor;
	}

	private SingletonSharedPreferences() {
		context = ApplicationController.getInstance();
		mainSharedPreferences = context.getSharedPreferences(
				APPConstant.SPEEND_TEST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		mainEditor = mainSharedPreferences.edit();
	}

	public static SingletonSharedPreferences getInstance() {
		if (instance == null)
			instance = new SingletonSharedPreferences();
		return instance;
	}

	/**
	 * 获得屏幕的宽，并且写入到SharedPrefrenecs
	 */
	public int getScreenWidth() {
		if (mainSharedPreferences.getBoolean(Has_Writed_Width, false)) {// 曾经写入过
			int width = mainSharedPreferences.getInt(Screen_Width, 0);
			if (width > 0) {
				return width;
			}
		}
		// 屏幕分辨率
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels; // 屏幕宽度（PX）
		mainEditor.putInt(Screen_Width, screenWidth).commit();
		mainEditor.putBoolean(Has_Writed_Width, true).commit();
		return screenWidth;
	}

	/**
	 * 获得屏幕的高，并且写入到SharedPrefrenecs
	 */
	public int getScreenHeight() {
		if (mainSharedPreferences.getBoolean(Has_Writed_Height, false)) {// 曾经写入过
			int height = mainSharedPreferences.getInt(Screen_Height, 0);
			if (height > 0) {
				return height;
			}
		}
		// 屏幕分辨率
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metric);
		int screenHeight = metric.heightPixels; // 屏幕宽度（PX）
		mainEditor.putInt(Screen_Height, screenHeight).commit();
		mainEditor.putBoolean(Has_Writed_Height, true).commit();
		return screenHeight;
	}

	/**
	 * 获得用于计算比率的图片的宽度
	 * 
	 * @return
	 */
	public int getCompareBitmapWidth() {
		int bitmap_width;
		if (mainSharedPreferences.getBoolean(Has_Bitmap_Width, false)) {
			bitmap_width = mainSharedPreferences.getInt(Bitmap_Width, 0);
			if (bitmap_width > 0) {
				return bitmap_width;
			}
		}
		// 计算图片的缩放大小
		BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
		bfoOptions.inJustDecodeBounds = true;
		bfoOptions.inScaled = false;// 真实大小不变形
		BitmapFactory.decodeResource(context.getResources(),
				R.drawable.gauge_bg_style1, bfoOptions);// 取时钟背景那张图片
		bitmap_width = bfoOptions.outWidth;
		mainEditor.putInt(Bitmap_Width, bitmap_width).commit();
		mainEditor.putBoolean(Has_Bitmap_Width, true).commit();
		return bitmap_width;
	}

	/**
	 * 获得针对当前手机的缩放比率
	 * 
	 * @return
	 */
	public float getScaleRate() {
		if (mainSharedPreferences.getBoolean(Has_Scale_Rate, false)) {// 曾经写入过
			return mainSharedPreferences.getFloat(Scale_Rate, 0);
		}
		float rate = getScreenWidth() * 0.86f / getCompareBitmapWidth();
		mainEditor.putFloat(Scale_Rate, rate).commit();
		mainEditor.putBoolean(Has_Scale_Rate, true).commit();
		// 不曾写入过
		return rate;
	}

	/**
	 * 获得手机通知栏高度
	 */
	public int getStatusBarHeight(Activity activity) {
		if (mainSharedPreferences.getBoolean(Has_Status_Bar_Height, false)) {
			return mainSharedPreferences.getInt(Status_Bar_Height, 0);
		}
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		mainEditor.putInt(Status_Bar_Height, rect.top).commit();
		mainEditor.putBoolean(Has_Status_Bar_Height, true).commit();
		return rect.top;
	}

	/**
	 * 获取屏幕的中心点高度，除去通知栏的高度
	 */
	public int getScreenCenterHeight(Activity activity) {
		if (mainSharedPreferences.getBoolean(Has_Screen_Center_Height, false)) {
			return mainSharedPreferences.getInt(Screen_Center_Height, 0);
		}
		int ScreenCenterHeight = (getScreenHeight() - getStatusBarHeight(activity)) / 2;
		mainEditor.putInt(Screen_Center_Height, ScreenCenterHeight).commit();
		mainEditor.putBoolean(Has_Screen_Center_Height, true).commit();
		return ScreenCenterHeight;
	}

	/**
	 * 获得仪表盘的显示样式，默认是M
	 * 
	 * @return
	 */
	public int getDialStyle() {
		return mainSharedPreferences.getInt(Dial_Style, 0);// 默认是样式0，即M
	}

	/**
	 * 设置仪表盘的显示样式
	 * 
	 * @return
	 */
	public void setDialStyle(int style) {
		mainEditor.putInt(Dial_Style, style).commit();// 默认是样式0，即M
	}

	/**
	 * 获取密码，没有密码返回“”字符串
	 * 
	 * @return
	 */
	public String getPassword() {
		return mainSharedPreferences.getString(PassWord, "");
	}

	/**
	 * 设置用户密码
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		mainEditor.putString(PassWord, password).commit();
	}

	/**
	 * 查询是否激活
	 * 
	 * @return
	 */
	public boolean getActivated() {
		return mainSharedPreferences.getBoolean(Activated, false);
	}

	/**
	 * 已经激活
	 */
	public void setActivated(boolean activited) {
		mainEditor.putBoolean(Activated, activited).commit();
	}

	/**
	 * 下载测速的地址
	 */
	public void setDownloadServerURL(String url) {
		mainEditor.putString(DownloadServerURL, url).commit();
	}

	/**
	 * 取得下载地址
	 * 
	 * @return
	 */
	public String getDownloadServerURL() {
		return mainSharedPreferences.getString(DownloadServerURL, "");
	}

	/**
	 * 上传测速的地址
	 */
	public void setUploadServerURL(String url) {
		mainEditor.putString(UploadServerURL, url).commit();
	}

	/**
	 * 取得上传地址
	 */
	public String getUploadServerURL() {
		return mainSharedPreferences.getString(UploadServerURL, "");
	}

	/**
	 * 取得ping地址
	 * 
	 * @return
	 */
	public String getPingIPAddress() {
		return mainSharedPreferences.getString(PingIPAddress, "");
	}

	/**
	 * 设置ping地址
	 */
	public void setPingIPAddress(String url) {
		mainEditor.putString(PingIPAddress, url).commit();
	}

	/**
	 * 设置上次ping的值
	 */
	public void setValueOfPing(String value) {
		mainEditor.putString(Value_Of_Ping, value).commit();
	}

	/**
	 * 获取上次ping的值
	 * 
	 * @return
	 */
	public String getValueOfPing() {
		return mainSharedPreferences.getString(Value_Of_Ping, "");
	}

	/**
	 * 设置上次下载的值
	 */
	public void setValueOfDownload(float value) {
		mainEditor.putFloat(Value_Of_Download, value).commit();
	}

	/**
	 * 获取上次下载的值
	 * 
	 * @return
	 */
	public float getValueOfDownload() {
		return mainSharedPreferences.getFloat(Value_Of_Download, 0.00f);
	}

	/**
	 * 设置上次上传的值
	 */
	public void setValueOfUpload(float value) {
		mainEditor.putFloat(Value_Of_Upload, value).commit();
	}

	/**
	 * 获取上次上传的值
	 * 
	 * @return
	 */
	public float getValueOfUpload() {
		return mainSharedPreferences.getFloat(Value_Of_Upload, 0.00f);
	}

	/**
	 * 设置单位模式，true代表Mbps，false代表kB/s
	 */
	public void setUnitMode(boolean value) {
		mainEditor.putBoolean(Unit_Mode, value).commit();
	}

	/**
	 * 获取单位模式,true代表Mbps，false代表kB/s
	 */
	public boolean getUnitMode() {
		return mainSharedPreferences.getBoolean(Unit_Mode, true);
	}

	/**
	 * 判定是否第一次运行，默认返回true
	 */
	public boolean isFirstLoad() {
		return mainSharedPreferences.getBoolean(First_Load, true);
	}

	/**
	 * 设置非第一次
	 */
	public void setFirestLoad(boolean value) {
		mainEditor.putBoolean(First_Load, value).commit();
	}
	
	/**
	 * 已经添加的手机号码数
	 */
//	public int getAddedPhoneCount() {
//		return mainSharedPreferences.getInt(Added_Phone_Count, 0);
//	}

//	/**
//	 * 记录已经添加的手机号码数
//	 */
//	public void setAddedPhoneCount(int value) {
//		mainEditor.putInt(Added_Phone_Count, value).commit();
//	}
//
//	/**
//	 * 手机号码数自增
//	 * 
//	 * @param value
//	 */
//	public void autoPlusPhoneCount() {
//		mainEditor.putInt(Added_Phone_Count, getAddedPhoneCount() + 1).commit();
//	}
//
//	/**
//	 * 手机号码数自减
//	 * 
//	 * @param value
//	 */
//	public void autoSPhoneCount() {
//		int num = getAddedPhoneCount() - 1;
//		if (num < 0) {
//			num = 0;
//		}
//		mainEditor.putInt(Added_Phone_Count, num).commit();
//	}

	/**
	 * 获取版本号 
	 */
	public String getVersionCode() {
		return mainSharedPreferences.getString(Version_Code, "");
	}

	/**
	 * 写入版本号
	 */
	public void setVersionCode(String value) {
		mainEditor.putString(Version_Code, value).commit();
	}

	/**
	 * 写入经度
	 */
	public void setLontitude(double value) {
		mainEditor.putString(Lontitude, value + "").commit();
	}

	/**
	 * 获取经度
	 */
	public String getLontitude() {
		return mainSharedPreferences.getString(Lontitude, "");
	}

	/**
	 * 写入纬度
	 */
	public void setLatitude(double value) {
		mainEditor.putString(Latitude, value + "").commit();
	}

	/**
	 * 获取纬度
	 */
	public String getLatitude() {
		return mainSharedPreferences.getString(Latitude, "");
	}

	/**
	 * 获取定位的地址
	 */
	public String getLocationAddress() {
		return mainSharedPreferences.getString(Location_Address, "");
	}

	/**
	 * 设置定位的地址
	 */
	public void setLocationAddress(String value) {
		mainEditor.putString(Location_Address, value).commit();
	}
	
//	/**
//	 * 是否已经AddMobileExtraInfo
//	 * @param value
//	 */
//	public void setAddMobileExtraInfo(boolean value){
//		mainEditor.putBoolean(addMobileExtraInfo, value).commit();
//	}
//	
//	public boolean getAddMobileExtraInfo(){
//		return mainSharedPreferences.getBoolean(addMobileExtraInfo, false);
//	}
	
	/**
	 * 获取rmg共享群最大人数限制
	 * @return
	 */
	public int getRMGMemberMaxCount(){
		return mainSharedPreferences.getInt(RMG_MEMBER_MAX_COUNT, -1);
	}
	
	/**
	 * 设置rmg共享群最大人数限制
	 * @param value
	 */
	public void setRMGMemberMaxCount(int value){
		mainEditor.putInt(RMG_MEMBER_MAX_COUNT, value).commit();
	}
	
	/**
	 * 获取流量转赠群最大人数限制
	 * @return
	 */
	public int getRMGDonationMemberMaxCount(){
		return mainSharedPreferences.getInt(RMG_DONATION_MEMBER_MAX_COUNT, -1);
	}
	
	/**
	 * 设置流量转赠群最大人数限制
	 * @param value
	 */
	public void setRMGDonationMemberMaxCount(int value){
		mainEditor.putInt(RMG_DONATION_MEMBER_MAX_COUNT, value).commit();
	}
	
	/**
	 * 是否显示过流量共享介绍页
	 * @return
	 */
	public boolean getShowRMGFlowPresentation(){
		return mainSharedPreferences.getBoolean(SHOW_RMG_FLOW_PRESENTATION, false);
	}
	
	/**
	 * 设置显示流量共享介绍页
	 */
	public void setShowRMGFlowPresentation(boolean value){
		mainEditor.putBoolean(SHOW_RMG_FLOW_PRESENTATION, value).commit();
	}
	
	/**
	 * 是否显示过流量转赠介绍页
	 */
	public boolean getShowRMGDonationPresentation(){
		return mainSharedPreferences.getBoolean(SHOW_RMG_DONATION_PRESENTATION,false);
	}
	
	/**
	 * 设置显示流量转赠介绍页
	 */
	public void setShowRMGDonationPresentation(boolean value){
		mainEditor.putBoolean(SHOW_RMG_DONATION_PRESENTATION, value).commit();
	}
	
	//add @14-07-04
	/**
	 * 是否打开流量红包
	 * @return
	 */
	public int getIsOpenFlowRedPacket(){
		return mainSharedPreferences.getInt(IS_OPEN_FLOW_RED_PACKET, 0);
	}
	
	/**
	 * 设置流量红包
	 */
	public void setIsOpenFlowRedPacket(int value){
		mainEditor.putInt(IS_OPEN_FLOW_RED_PACKET, value).commit();
	}
	
	//add @2014-08-06
	/**
	 * 是否打开流量红包
	 * @return
	 */
	public boolean getHasUploadContacts(){
		return mainSharedPreferences.getBoolean(HAS_UPLOAD_CONTACTS, false);
	}
	
	/**
	 * 设置流量红包
	 */
	public void setHasUploadContacts(boolean value){
		mainEditor.putBoolean(HAS_UPLOAD_CONTACTS, value).commit();
	}
	
	//add @2014-09-04 
	/**
	 * 消息中心更新的时间戳
	 * @param value
	 */
	public void setMessageCentreUpdateTimeStamp(String value){
		mainEditor.putString(TIME_STAMP_MESSAGE_CENTRE_UPDATE, value);
	}
	
	public String getMessageCentreUpdateTimeStamp(){
		return mainSharedPreferences.getString(TIME_STAMP_MESSAGE_CENTRE_UPDATE, "");
	}
	
	/**
	 * 朋友圈更新的时间戳
	 */
	public void setMomentsUpdateTimeStamp(String value){
		mainEditor.putString(TIME_STAMP_MOMENTS_UPDATE, value);
	}
	
	public String getMomentsUpdateTimeStamp(){
		return mainSharedPreferences.getString(TIME_STAMP_MOMENTS_UPDATE, "");
	}
	
	/**
	 * 更多业务办理
	 */
	public void setMorePackage(String value){
		mainEditor.putString(MORE_PACKAGE,value).commit();
	}
	
	public String getMorePackage(){
		return mainSharedPreferences.getString(MORE_PACKAGE, "");
	}
	
	public void setMorePackageUrl(String value){
		mainEditor.putString(MORE_PACKAGE_URL, value).commit();
	}
	
	public String getMorePackageUrl(){
		return mainSharedPreferences.getString(MORE_PACKAGE_URL, "");
	}
	
	/**
	 * 新版本json数据
	 */
	public void setNewVersionJson(String value){
		mainEditor.putString(NEW_VERSION_JSON, value).commit();
	}
	
	public String getNewVersionJson(){
		return mainSharedPreferences.getString(NEW_VERSION_JSON, "");
	}
	
	/**
	 * 是否有新版本
	 * @param value
	 */
	public void setIsNewVersionAvailable(boolean value){
		mainEditor.putBoolean(IS_NEW_VERSION_AVAILABLE, value).commit();
	}
	
	public boolean isNewVersionAvailable(){
		return mainSharedPreferences.getBoolean(IS_NEW_VERSION_AVAILABLE, false);
	}
	
	/**
	 * 策反检测日期
	 * @param value
	 */
	public void setDefectCheckDate(String value){
		mainEditor.putString(DEFECT_CHECK_DATE, value).commit();
	}
	
	public String getDefectCheckDate(){
		return mainSharedPreferences.getString(DEFECT_CHECK_DATE, "");
	}
	
	public void setDefectDialogUrl(String value){
		mainEditor.putString(DEFECT_DIALOG_URL, value).commit();
	}
	
	public String getDefectDialogUrl(){
		return mainSharedPreferences.getString(DEFECT_DIALOG_URL, "");
	}
	/**
	 * 流量团购红点
	 */
	public boolean isFlowGrouponClick(){
		return mainSharedPreferences.getBoolean(IS_FLOW_GROUPON_CLICK, false);
	}
	
	public void setFlowGrouponClick(boolean value){
		mainEditor.putBoolean(IS_FLOW_GROUPON_CLICK, value).commit();
	}
	
	/**
	 * 我的家庭成员查询时间
	 */
	public void setMyFamilyMemberQueryTime(long value){
		mainEditor.putLong(MY_FAMILY_MEMBER_QUERY_TIME, value).commit();
	}
	
	public long getMyFamilyMemberQueryTime(){
		return mainSharedPreferences.getLong(MY_FAMILY_MEMBER_QUERY_TIME, 0);
	}
}
