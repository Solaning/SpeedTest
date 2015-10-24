package com.kinth.mmspeed.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.InstancySpeedInfo;
import com.kinth.mmspeed.constant.APPConstant.OperatorType;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.hk.SingletonTypefaceClass;

/**
 * 
 * @author BJ, Sola 此类放置通用方法。在其他程序也可以用，不要放置不可通用的方法。
 */
public class UtilFunc {
	private static final String TAG = UtilFunc.class.getSimpleName();
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd  HH:mm:ss:");

	public static String getCurrentDate() {
		return dateFormat.format(new java.util.Date());
	}

	public static void showToastMsg(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static String getCurrentTime() {
		return timeFormat.format(new java.util.Date());
	}

	public static void showProgressDialog(final ProgressDialog progressDialog,
			String msg, OnCancelListener cancelListener) {
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(cancelListener);
		progressDialog.show();
	}

	public static void showProgressDialog(final ProgressDialog progressDialog,
			int msgId, OnCancelListener cancelListener) {
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(ApplicationController.getInstance().getResources()
				.getString(msgId));
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(cancelListener);
		progressDialog.show();
	}

	public static String getExceptionString(Throwable e) {
		if (e == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static void sendBroadcast(Context context, String action) {
		if (context == null) {
			Log.e(TAG, "参数错误，context=null,发送广播" + action + "失败");
			return;
		}
		Intent intent = new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
		Log.i(TAG, "发送广播" + action);
	}

	public static void sendBroadcast(Context context, String action,
			String extraName, int extraValue) {
		if (context == null) {
			Log.e(TAG, "参数错误，context=null,发送广播" + action + "失败");
			return;
		}
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra(extraName, extraValue);
		context.sendBroadcast(intent);
		Log.i(TAG, "发送广播" + action + "，参数" + extraName + "=" + extraValue);
	}

	public static void sendBroadcastImChatResult(Context context,
			String action, String extraResult, int result, String extraTaskId,
			String valueTaskId) {
		if (context == null) {
			Log.e(TAG, "参数错误，context=null,发送广播" + action + "失败");
			return;
		}
		Intent intent = new Intent();
		intent.setAction(action);
		intent.putExtra(extraResult, result);
		intent.putExtra(valueTaskId, valueTaskId);
		context.sendBroadcast(intent);
		// Log.i(TAG, "发送广播" + action + "，参数内容" + extraName + "=" + extraValue);
	}

	public static int getCurrentVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		// return pi.versionName;
		return pi.versionCode;
	}

	/**
	 * UUID
	 * 
	 * @return String UUID
	 */
	public static String getRandomUUID() {
		String s = UUID.randomUUID().toString();
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}

	/**
	 * ��ȡandroid�豸���ͺ�
	 * 
	 * @return
	 */
	public static String getAndroidDev() {
		return Build.MODEL;
	}

	/**
	 * ��ȡandroidϵͳ��sdk�汾������api7-->2.1
	 * 
	 * @return
	 */
	public static String getAndroidSDKVersion() {
		return android.os.Build.VERSION.SDK;
		// int version = 0;
		// try {
		// version = Integer.valueOf(android.os.Build.VERSION.SDK);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// }
		// return version;
	}

	/**
	 * 获取分辨率
	 * 
	 * @param activity
	 * @return ��Ž���list����һ����height���ڶ�����width
	 */
	public static List<String> getWindowResolution(Activity activity) {
		List<String> list = new ArrayList<String>();
		if (activity != null) {
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			list.add(String.valueOf(dm.heightPixels));
			list.add(String.valueOf(dm.widthPixels));
		}
		return list;
	}

	public static void logError(String TAG, String error) {
		if (ApplicationController.isTestingStage) {
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			error += "(" + ste.getFileName() + ": Line " + ste.getLineNumber()
					+ ")";
			Log.e(TAG, error);
			FileOperation.writeFileAppend(UtilFunc.getCurrentTime() + TAG
					+ " : " + error + "\n");
			// if (Global.isDevelopingStage) {
			// showToastMsg(Global.getInstance(), error);
			// }
		}
	}

	/**
	 * 获取mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		if (context == null) {
			return "";
		}
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * �ж�email��ʽ�Ƿ���ȷ
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void logInfo(String TAG, String info) {
		if (ApplicationController.isTestingStage) {
			StackTraceElement ste = new Throwable().getStackTrace()[1];
			info += "(" + ste.getFileName() + ": Line " + ste.getLineNumber()
					+ ")";
			Log.i(TAG, info);
			FileOperation.writeFileAppend(UtilFunc.getCurrentTime() + TAG
					+ " : " + info + "\n");
		}
	}

	/**
	 * 保存bitmap到SDcard
	 * 
	 * @param fileName
	 * @param bmp
	 * @param format
	 *            保存的格式
	 * @param quality
	 *            保存的质量，最差0-100最好
	 * @return
	 */
	public static boolean saveBitMapToSDCard(String fileName, Bitmap bmp,
			CompressFormat format, int quality) {
		boolean rtn = true;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			bmp.compress(format, quality, out);
		} catch (Exception e) {
			e.printStackTrace();
			rtn = false;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out = null;
		}
		return rtn;
	}

	public static boolean saveDrawableToInternalStoragePrivate(Context context,
			String fileName, Drawable d) {
		FileOutputStream fos = null;
		boolean rtn = true;
		try {
			// MODE_PRIVATE creates/replaces a file and makes
			// it private to your application. Other modes:
			// MODE_WORLD_WRITEABLE
			// MODE_WORLD_READABLE
			// MODE_APPEND
			BitmapDrawable bd = (BitmapDrawable) d;
			Bitmap bmp = bd.getBitmap();
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);// 输出到本地
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rtn = false;
		}
		return rtn;
	}

	/**
	 * Writes drawable to internal storage making the content private to the
	 * application. The method can be easily changed to take the MODE as
	 * argument and let the caller dictate the visibility: MODE_PRIVATE,
	 * MODE_WORLD_WRITEABLE, MODE_WORLD_READABLE, etc.
	 * 
	 * @param fileName
	 *            - the name of the file to create
	 * @param content
	 *            - the content to write
	 */
	public static boolean saveDrawableToInternalStoragePrivate(String fileName,
			Drawable d) {
		FileOutputStream fos = null;
		boolean rtn = true;
		try {
			// MODE_PRIVATE creates/replaces a file and makes
			// it private to your application. Other modes:
			// MODE_WORLD_WRITEABLE
			// MODE_WORLD_READABLE
			// MODE_APPEND
			BitmapDrawable bd = (BitmapDrawable) d;
			Bitmap bmp = bd.getBitmap();
			fos = ApplicationController.getInstance().openFileOutput(fileName,
					Context.MODE_PRIVATE);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rtn = false;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rtn;
	}

	/**
	 * create a drawable from internal storage
	 * 
	 * @param fileName
	 *            the file to read from
	 * @return the file content
	 */
	public static Drawable createDrawableFromInternalStoragePrivate(
			String fileName) {
		FileInputStream fis = null;
		Drawable drawable = null;
		try {
			fis = ApplicationController.getInstance().openFileInput(fileName);
			drawable = Drawable.createFromStream(fis, "src");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return drawable;
	}

	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Resources res, Bitmap bmp) {
		if (bmp == null) {
			return null;
		}
		BitmapDrawable bd = new BitmapDrawable(bmp);
		return bd;
	}

	public static Drawable bitmapToDrawable(Bitmap bmp) {
		BitmapDrawable bd = new BitmapDrawable(bmp);
		return bd;
	}

	// --------------------------------add by sola---------------------------

	/**
	 * 获取android的UUID
	 * 
	 * @param context
	 * @return
	 */
	public static String getAndroidDeviceUUID(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	// 计算返回缩放后的bitmap
	public static Bitmap ScaleBitmap(Bitmap bitmap, float scaleRate) {
		if (bitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(scaleRate, scaleRate);
			// create the new Bitmap object
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBitmap;
		}
		return null;
	}

	/**
	 * 拷贝文件到sd卡上 将assets文件夹下面的文件全部copy到DATA_PATH路径下面
	 * 
	 * @return
	 */
	public static void CopyAssets(Context context, String source_file_name,
			String target_file_path) {
		AssetManager assetManager = context.getAssets();
		InputStream in = null;
		OutputStream out = null;
		File f = new File(target_file_path);
		try {
			if (!f.exists()) {
				in = assetManager.open(source_file_name);
				out = new FileOutputStream(f);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 拷貝文件用到的方法,在CopyAssets()方法中使用到了,只要传入参数：输入流和输出流就ok。
	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * bitmap to drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable Bitmap2Drawable(Bitmap bitmap, Context context) {
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bitmap);
		// 因为BtimapDrawable是Drawable的子类，最终直接使用bd对象即可。
		return bd;
	}

	/**
	 * 获取通知栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 加载图片，裁剪出适合屏幕的宽高
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap getDecodeBitmapCut(Context context, int id,
			int screenWidth, int screenHeight) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inScaled = false;// 真实大小不变形
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				id, opt);
		if (bitmap == null) {
			return null;
		}
		int w = opt.outWidth;// 目标图片的宽和高
		int h = opt.outHeight;
		// 开始裁剪的坐标点
		int x = 0;
		int y = 0;
		if (w >= screenWidth && h >= screenHeight) {// 无需放大，直接裁剪
			x = (w - screenWidth) / 2;
			y = (h - screenHeight) / 2;
			return Bitmap.createBitmap(bitmap, x, y, screenWidth, screenHeight);// 針對無需放大，直接裁剪的情況
		} else if (w >= screenWidth && h < screenHeight) {// 寬不需要放大，高需要拉伸
			x = (w - screenWidth) / 2;
			Bitmap temp = Bitmap.createScaledBitmap(bitmap, w, screenHeight,
					false);// 寬不變，高放大
			return Bitmap.createBitmap(temp, x, y, screenWidth, screenHeight);
		} else if (w < screenWidth && h >= screenHeight) {// 寬需要放大，高不需要
			y = (h - screenHeight) / 2;
			Bitmap temp = Bitmap.createScaledBitmap(bitmap, screenWidth, h,
					false);
			return Bitmap.createBitmap(temp, x, y, screenWidth, screenHeight);
		} else if (w < screenWidth && h < screenHeight) {// 寬和高都需要放大
			Bitmap temp = Bitmap.createScaledBitmap(bitmap, screenWidth,
					screenHeight, false);
			return Bitmap.createBitmap(temp, x, y, screenWidth, screenHeight);
		}
		return null;
	}

	/**
	 * 檢查網絡連接
	 * 
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) ApplicationController.getInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		// use netInfo.isConnected() rather than netInfo.isConnectedOrConnecting
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 针对ping值把String转成SpannableString
	 */
	public static SpannableString getPingSpannable(Context context, String input) {
		// 创建一个 SpannableString对象
		SpannableString msp;
		if (input.equals("")) {
			input = "超时";
			msp = new SpannableString(input);
			msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.base_blue)), 0,
					input.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			msp.setSpan(new RelativeSizeSpan(1.3f), 0, input.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 1.5f表示默认字体大小的倍数
		} else {
			msp = new SpannableString(input + "ms");
			msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.base_blue)), 0,
					input.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			msp.setSpan(new RelativeSizeSpan(1.5f), 0, input.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 1.5f表示默认字体大小的倍数
		}
		msp.setSpan(new CustomTypefaceSpan(SingletonTypefaceClass.getInstance()
				.getFont()), 0, input.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		return msp;
	}

	/**
	 * 针对download和upload的结果值把float转成SpannableString
	 */
	public static SpannableString getSpanResult(Context context, float value) {
		SpannableString msp;
		String input;
		if (SingletonSharedPreferences.getInstance().getUnitMode()) {// Mbps模式
			input = "" + (float) (Math.round(value / 128 * 100)) / 100;
			msp = new SpannableString(input + "Mbps");
		} else {// kB/s模式
			input = "" + (float) (Math.round(value * 100)) / 100;
			msp = new SpannableString(input + "kB/s");
		}
		msp.setSpan(new CustomTypefaceSpan(SingletonTypefaceClass.getInstance()
				.getFont()), 0, input.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.5f), 0, input.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 1.5f表示默认字体大小的倍数
		msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.base_blue)), 0,
				input.length() + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return msp;
	}

	/**
	 * 把测速的瞬时值转成SpannableString
	 */
	public static InstancySpeedInfo getInstancySpan(Context context, float value) {
		InstancySpeedInfo instancySpeed = new InstancySpeedInfo();
		String speed = "";
		// 创建一个 SpannableString对象
		SpannableString ss;
		if (SingletonSharedPreferences.getInstance().getUnitMode()) {
			instancySpeed.setAngle(UtilFunc.Value2Angle2(value));
			speed += (float) (Math.round(value / 128 * 100)) / 100;
			ss = new SpannableString(speed + "\nMbps");
		} else {
			instancySpeed.setAngle(UtilFunc.Value2Angle1(value));
			speed += (float) (Math.round(value * 100)) / 100;
			ss = new SpannableString(speed + "\nkB/s");
		}
		ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(android.R.color.black)), 0, ss.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new CustomTypefaceSpan(SingletonTypefaceClass.getInstance()
				.getFont()), 0, speed.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		ss.setSpan(new RelativeSizeSpan(2.0f), 0, ss.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 2.0f表示默认字体大小的两倍
		instancySpeed.setSpeed(ss);
		return instancySpeed;
	}

	/**
	 * 把输入字符串转成红色，大一号字体，转middle值
	 */
	public static SpannableString turnStringToRedSpannable(Context context,
			String left, String middle, String right) {
		SpannableString msp = new SpannableString(left + middle + right);
		msp.setSpan(new ForegroundColorSpan(Color.RED), left.length(),
				left.length() + middle.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), left.length(), left.length()
				+ middle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 1.5f表示默认字体大小的倍数
		return msp;
	}

	// 常量
	private static int[] Speed_Section_1 = { 0, 100, 200, 300, 500, 1000, 2000,
			3000, 5000 };// 速度区间1
	private static int[] Speed_Section_2 = { 0, 128, 256, 384, 640, 1280, 2560,
			3840, 6400 };// 速度区间2

	/**
	 * 速度值到角度的转变（1） kB/s 都是按KB/s来比较的
	 * 
	 * @param value
	 * @return
	 */
	public static float Value2Angle1(float value) {
		int index = Integer.MAX_VALUE;// 先赋最大值
		for (int i = 0; i < Speed_Section_1.length; i++) {
			if (value <= Speed_Section_1[i]) {
				index = i;
				break;
			}
		}
		if (index == 0) {
			return ApplicationController.MinDegree;// 返回最小角度
		} else if (index == Integer.MAX_VALUE) {
			return ApplicationController.MaxDegree;
		} else {
			int preNum = Speed_Section_1[index - 1];// 前一个刻度值
			int num = Speed_Section_1[index];// 当前刻度值
			float rate = (float) ((value - preNum) / (num * 1.0f - preNum));// 待改进
			return ApplicationController.MinDegree + (rate + index - 1)
					* ApplicationController.PerAngleIncrement;
		}
	}

	/**
	 * 速度值到角度的转变（2） Mbps 都是按KB/s来比较的，即是把Mbps转成了KB/s 1Mbps = 128KB/s
	 * 
	 * @param value
	 * @return
	 */
	public static float Value2Angle2(float value) {
		int index = Integer.MAX_VALUE;// 先赋最大值
		for (int i = 0; i < Speed_Section_2.length; i++) {
			if (value <= Speed_Section_2[i]) {
				index = i;
				break;
			}
		}
		if (index == 0) {
			return ApplicationController.MinDegree;// 返回最小角度
		} else if (index == Integer.MAX_VALUE) {
			return ApplicationController.MaxDegree;
		} else {
			int preNum = Speed_Section_2[index - 1];// 前一个刻度值
			int num = Speed_Section_2[index];// 当前刻度值
			float rate = (float) ((value - preNum) / (num * 1.0f - preNum));// 待改进
			return ApplicationController.MinDegree + (rate + index - 1)
					* ApplicationController.PerAngleIncrement;
		}
	}

	/*
	 * 获取当前程序的名称
	 */
	public static String getCurrentVersionName(Context context) {
		// 获取packagemanager的实例
		PackageManager pm = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return pi.versionName;
	}

	/**
	 * 删除文件与文件夹
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			// file.delete();
		}
	}

	/**
	 * 删除文件与文件夹
	 * 
	 * @param file
	 */
	public static void deleteAll(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * 删除对应号码的缓存文件
	 */
	public static void deleteCacheFile(String phoneString) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "LiuLiangYi" + File.separator + "Data"
				+ File.separator + phoneString);
		if (file.exists() && file.isFile()) {// 文件不存在
			file.delete();
		}
	}

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 *            HttpURLConnection对象
	 * @return 返回头字段的LinkedHashMap
	 */
	public static Map<String, String> getHttpResponseHeader(
			HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>(); // 使用LinkedHashMap保证写入和遍历的时候的顺序相同，而且允许空值存在
		for (int i = 0;; i++) { // 此处为无限循环，因为不知道头字段的数量
			String fieldValue = http.getHeaderField(i); // getHeaderField(int
														// n)用于返回 第n个头字段的值。

			if (fieldValue == null)
				break; // 如果第i个字段没有值了，则表明头字段部分已经循环完毕，此处使用Break退出循环
			header.put(http.getHeaderFieldKey(i), fieldValue); // getHeaderFieldKey(int
																// n)用于返回
																// 第n个头字段的键。
		}
		return header;
	}

	/**
	 * 读取本地文件中JSON字符串
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getJson(String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		File file = new File(fileName);
		if (!file.exists()) {// 文件不存在
			return "";// 返回""
		}
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line;
			while ((line = bf.readLine()) != null) {
				stringBuilder.append(line);
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取一下手机初始化信息
	 * 
	 * @param activity
	 */
	public static void getTerminalEndCommonInfo(Activity activity) {
		ApplicationController.getInstance().setDev(UtilFunc.getAndroidDev());
		List<String> resolution = UtilFunc.getWindowResolution(activity);
		if (resolution != null && resolution.size() == 2) {
			ApplicationController.getInstance().setResolution(
					resolution.get(0) + "_" + resolution.get(1));
		}
		ApplicationController.getInstance().setMac(UtilFunc.getMacAddress(activity));
		ApplicationController.getInstance().setUid(UtilFunc.getAndroidDeviceUUID(activity));
		ApplicationController.getInstance().setVersion(UtilFunc.getCurrentVersion(activity));
		ApplicationController.getInstance().setNetworkOperator(
				getNetworkOperatorName(activity));// 运营商
	}

	/**
	 * 获取网络运营商的名字
	 * 
	 * @return
	 */
	public static String getNetworkOperatorName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getNetworkOperatorName();
	}
	


	/**
	 * 返回用户手机运营商名称 --区分移动、联通、电信
	 * @add 2014-11-05
	 * @param telephonyManager 
	 * @return
	 */
	public static OperatorType getProvidersName(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = telephonyManager.getSubscriberId();
		if (TextUtils.isEmpty(IMSI))
			return OperatorType.OTHER;

		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。其中
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {//中国移动
			return OperatorType.MOBILE;
		} else if (IMSI.startsWith("46001")) {//中国联通
			return OperatorType.UNICOM;
		} else if (IMSI.startsWith("46003")) {//中国电信
			return OperatorType.TELECOM;
		} else{
			return OperatorType.OTHER;//其他
		}
	}

	/**
	 * URLEncoder 中文转码
	 * add @2014-08-14
	 */
	public static String encode(String content){
		String result = null;
		try {
			result = java.net.URLEncoder.encode(content,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			result = content;
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取手机的网络类型
	 */
	public static String getNetworkType(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
		String networkType = "noNetwork";
		if (networkInfo == null)
			return networkType = "noNetwork";
		switch (networkInfo.getType()) {
		case ConnectivityManager.TYPE_WIFI: // wifi
			networkType = "WIFI";
			break;
		case ConnectivityManager.TYPE_MOBILE:// 手机网络
			TelephonyManager telephoneManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			switch (telephoneManager.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				networkType = "3G";// 3G
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
				networkType = "3G";// 3g
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				networkType = "2G";// 2g
				break;
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				networkType = "3G";// 3g
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_B:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:// 2g
				networkType = "2G";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN:// 3g
				networkType = "3G";
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:// 4g
				networkType = "4G";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:// 2g/3g
				networkType = "2G/3G";
				break;
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				networkType = "noNetwork";
				break;
			}
			// networkType = networkInfo.getSubtypeName();
			// * NETWORK_TYPE_CDMA 网络类型为CDMA
			// * NETWORK_TYPE_EDGE 网络类型为EDGE
			// * NETWORK_TYPE_EVDO_0 网络类型为EVDO0
			// * NETWORK_TYPE_EVDO_A 网络类型为EVDOA
			// * NETWORK_TYPE_GPRS 网络类型为GPRS
			// * NETWORK_TYPE_HSDPA 网络类型为HSDPA
			// * NETWORK_TYPE_HSPA 网络类型为HSPA
			// * NETWORK_TYPE_HSUPA 网络类型为HSUPA
			// * NETWORK_TYPE_UMTS 网络类型为UMTS
			// 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，电信的3G为EVDO

			// switch (networkInfo.getSubtype()) {
			// case TelephonyManager.NETWORK_TYPE_CDMA:
			// networkType = "电信2G网络";
			// break;
			// case TelephonyManager.NETWORK_TYPE_UMTS:
			// case TelephonyManager.NETWORK_TYPE_HSDPA:
			// networkType = "联通3G网络";
			// break;
			// case TelephonyManager.NETWORK_TYPE_GPRS:
			// case TelephonyManager.NETWORK_TYPE_EDGE:
			// networkType = "移动或联通2G网络";
			// break;
			// default:
			// networkType = "其他网络";
			// break;
			// }
		}
		return networkType;
	}
	// -------------------------------end-----------------------------------
	
	/**
	 * 通过类名称来加载类
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static Class<?> loadClass(Context mContext, String className){
		if(TextUtils.isEmpty(className))
			return null;
		Class<?> clazz = null;
		try {
            // 载入这个类 
            clazz = mContext.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return clazz;
	}
}
