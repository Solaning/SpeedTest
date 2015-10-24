package com.kinth.mmspeed.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;


import com.kinth.mmspeed.bean.ContractInfo;
//import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.constant.APPConstant;

public class JUtil {
	/**
	 * 获取当前的sd卡路径
	 *	
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}
	
	public static boolean isIdleTime() {
		Calendar cal = Calendar.getInstance();// 当前日期
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
		int minute = cal.get(Calendar.MINUTE);// 获取分钟
		int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
//		final int start = 17 * 60 + 20;// 起始时间 17:20的分钟数
		final int start1 = 23*60;// 起始时间 23:00的分钟数
		final int end1 = 24 * 60;// 结束时间 19:00的分钟数
		final int start2 = 0;// 起始时间 24:00的分钟数
		final int end2 = 8* 60;// 结束时间 8:00的分钟数
		if (minuteOfDay >= start1 && minuteOfDay <= end1||minuteOfDay >= start2 && minuteOfDay <= end2) {
//		    System.out.println("在外围内");
			return true;
		} else {
//		    System.out.println("在外围外");
			return false;
		}
	}
	public static long getDays(String endDay) throws Exception {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sDateFormat.format(new java.util.Date());
		Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(endDay);
		long l = date1.getTime() - date2.getTime() > 0 ? date1.getTime()
				- date2.getTime() : date2.getTime() - date1.getTime();
		long day = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000) > 0 ? (date1
				.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000)
				: (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
		return day;
	}
	public static boolean isTimeBetween(String startTime,String endTime) {
		try {
			Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
			Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(curDate);
			Date currentData =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentTime);
//			long start =date1.getTime();
//			long end = date2.getTime();
//			long current = currentData.getTime();
			if (date1.getTime()<currentData.getTime()&&currentData.getTime()<date2.getTime()) {
				return true;
			}else {
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	/**
	 * 创建文件夹下
	 * 
	 * @param path
	 */
	public static void createPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
			if (file.exists()) {
				file.mkdir();
			}
		}
	}

	/**
	 * 
	 * @param mContext
	 *            上下文，来区别哪一个activity调用的
	 * @param whichSp
	 *            使用的SharedPreferences的名字
	 * @param field
	 *            SharedPreferences的哪一个字段
	 * @return
	 */
	// 取出whichSp中field字段对应的string类型的值
	public static String getSharePreStr(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = mContext
				.getSharedPreferences(whichSp, 0);
		return sp.getString(field, "");// 没有则返回“”
	}

	// 取出whichSp中field字段对应的int类型的值
	public static int getSharePreInt(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = mContext
				.getSharedPreferences(whichSp, 0);
		return sp.getInt(field, 0);// 没有则返回0
	}
	// 取出whichSp中field字段对应的int类型的值
	public static boolean getSharePreBool(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = mContext
				.getSharedPreferences(whichSp, 0);
		return sp.getBoolean(field, false);// 没有则返回false
	}

	// 保存string类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, String value) {
		SharedPreferences sp = mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putString(field, value).commit();
	}
	public static void putShareBool(Context mContext, String whichSp,
			String field, boolean value) {
		SharedPreferences sp = mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putBoolean(field, value).commit();
	}
	// 保存int类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, int value) {
		SharedPreferences sp = mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putInt(field, value).commit();
	}

	/**
	 * Toast的封装
	 * 
	 * @param mContext
	 *            上下文，来区别哪一个activity调用的
	 * @param msg
	 *            你希望显示的值。
	 */
	public static void showMsg(Context mContext, String msg) {
		Toast toast = new Toast(mContext);
		toast = Toast.makeText(mContext, msg, 300);
//		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);// 设置居中
		toast.show();// 显示,(缺了这句不显示)
	}

	public static void showToastMsg(Context context, String msg) {
		if (APPConstant.ISDBUG) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public void activityAnimationRightToLeft() {

	}

	/**
	 * 判断当前网络链接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	public static boolean isMobileNum(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		// Pattern p = Pattern
		// .compile("^1(34[0-8]|(3[5-9]|5[017-9]|8[278]|47)\\d)\\d{7}$");

		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();

	}

	public static ProgressDialog showDialog(Activity context, String strContent) {
		if (context != null) {
			ProgressDialog infoDialog = new ProgressDialog(context);
			infoDialog.setMessage(strContent);
			infoDialog.show();
			return infoDialog;
		}
		return null;
	}
	public static ProgressDialog showDialog(Context context, String strContent) {
		if (context != null) {
			ProgressDialog infoDialog = new ProgressDialog(context);
			infoDialog.setMessage(strContent);
			infoDialog.show();
			return infoDialog;
		}
		return null;
	}

	public static ProgressDialog showDialog(Activity context,
			String strContent, int max) {
		if (context != null) {
			ProgressDialog dialog = new ProgressDialog(context);
			dialog.setMessage(strContent);
			// 设置风格为条状
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// 设置最大值
			dialog.setMax(max);
			// 设置初始值
			dialog.setProgress(0);
			dialog.show();
			return dialog;
		}
		return null;
	}

	/**
	 * @param infoDialog
	 */
	public static void dismissDialog(ProgressDialog infoDialog) {
		if (infoDialog != null) {
			infoDialog.dismiss();
		}
		infoDialog = null;
	}

	public static float dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * 可接受的电话格式有：
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		/*
		 * 可接受的电话格式有：
		 */
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);

		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}
	
	public static String getUUID(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		return deviceId;
	}
	
	/**
	 * 获取sim卡联系人
	 * @param mContext
	 * @return
	 */
	public static List<ContractInfo> getSimContracts(Context mContext) {
		// 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
		List<ContractInfo> simContractList = new ArrayList<ContractInfo>();

		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, null, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String name = phoneCursor.getString(phoneCursor
						.getColumnIndex("name"));
				String phoneNumber = phoneCursor.getString(phoneCursor
						.getColumnIndex("number"));
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}
				// 以下是我自己的数据封装。
				ContractInfo contractInfo = new ContractInfo();
				contractInfo.setContractName(name);
				contractInfo.setMobile(phoneNumber.replace(" ", ""));
				simContractList.add(contractInfo);
			}
			phoneCursor.close();
		}
		if (simContractList != null && simContractList.size() > 0) {
			return JUtil.filterUserPhone(simContractList);
		}else {
			return null;
		}
	}
	
	/**
	 * 获取手机联系人
	 * @param mContext
	 * @return
	 */
	public static List<ContractInfo> getPhoneContracts(
			Context mContext) {
		List<ContractInfo> phoneList = new ArrayList<ContractInfo>();
		ContentResolver resolver = mContext.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null, null,
				null, null); // 传入正确的uri
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME); // 获取联系人name
				String name = phoneCursor.getString(nameIndex);
				String phoneNumber = phoneCursor.getString(phoneCursor
						.getColumnIndex(Phone.NUMBER)); // 获取联系人number
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}
				// 以下是我自己的数据封装。
				ContractInfo contractInfo = new ContractInfo();
				contractInfo.setContractName(name);
				contractInfo.setMobile(phoneNumber.replace(" ", ""));
				phoneList.add(contractInfo);
			}
			phoneCursor.close();
		}
		if (phoneList != null && phoneList.size()>0) {
			return JUtil.filterUserPhone(phoneList);
		}else {
			return null;
		}
	}
	
	/**
	 * 获取通讯录联系人列表
	 * @param context
	 * @return
	 */
	public static List<ContractInfo> getPhoneAndSimContracts(Context context) {
		List<ContractInfo> contractInfos = new ArrayList<ContractInfo>();
		List<ContractInfo> list1 = getSimContracts(context);
		List<ContractInfo> list2 = getPhoneContracts(context);
		if (list1 != null && list1.size() > 0) {
			contractInfos.addAll(list1);
		}

		if (list2 != null && list2.size() > 0) {
			contractInfos.addAll(list2);
		}
		return contractInfos;
	}
	
	/**
	 * 获取两个日期之间的间隔天数
	 * @return
	 */
	public static int getGapCount(String startDateStr, String endDateStr) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		   Date startDate = null;
		   Date endDate = null;
		try {
			startDate = sdf.parse(startDateStr);
			endDate = sdf.parse(endDateStr);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
        Calendar fromCalendar = Calendar.getInstance();  
        fromCalendar.setTime(startDate);  
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        fromCalendar.set(Calendar.MINUTE, 0);  
        fromCalendar.set(Calendar.SECOND, 0);  
        fromCalendar.set(Calendar.MILLISECOND, 0);  
  
        Calendar toCalendar = Calendar.getInstance();  
        toCalendar.setTime(endDate);  
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
        toCalendar.set(Calendar.MINUTE, 0);  
        toCalendar.set(Calendar.SECOND, 0);  
        toCalendar.set(Calendar.MILLISECOND, 0);  
  
        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * 过滤用户手机号
	 * @param mContractInfos
	 * @return
	 */
	public static List<ContractInfo> filterUserPhone(List<ContractInfo> mContractInfos){
		List<ContractInfo> contractInfos = new ArrayList<ContractInfo>();
		for (ContractInfo contractInfo : mContractInfos) {
			if (!TextUtils.isEmpty(contractInfo.getMobile())) {
				//首先去掉空格
				String phone = contractInfo.getMobile().trim();
				//位数少于11  去掉
				if (phone.length() < 11) {
					continue;
				}
				//大于11
				if (phone.length() > 11 && phone.startsWith("+86")) {
					phone = phone.substring(3, phone.length());
				}
				if (phone.startsWith("1") && phone.length() == 11) {
					contractInfo.setMobile(phone);
					contractInfos.add(contractInfo);
				}
			}
		}
		return contractInfos;
	}
}
