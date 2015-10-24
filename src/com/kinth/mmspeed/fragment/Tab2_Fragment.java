package com.kinth.mmspeed.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.GifTextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.BannerADInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.fragment.PrepareFragment.TestStartCallback;
import com.kinth.mmspeed.fragment.SpeedResultFragment.OnRestartTestListener;
import com.kinth.mmspeed.fragment.SpeedTestMainFragment.OnSpeedTestEndListener;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.Md5Util;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sola.code.ydlly_assist.bean.KeyValue;

public class Tab2_Fragment extends BaseFragment {
	private static final int HANDLER_SWITCH_AD = 0;//切换广告
	private static final int HANDLER_RESTART_TEST = 1;//重新测速
	private static final int HANDLER_TEST_END = 2;//测速完成
	// 广告相关的控制
	public static final int Type_PNG = 131411;// PNG格式
	public static final int Type_GIF = 131412;// GIF格式
	public static final int BUFFER_SIZE = 1024;
	private Context context;
	private FragmentManager fragmentManager;

	private GifImageView gif;
	private GifTextView giftext;

	@ViewInject(R.id.ad_layout)
	private LinearLayout adLayout;

	private int adNum;// 广告的个数
	private int adPosition;// 当前处在第几个广告
	private List<BannerADInfo> AdList = new ArrayList<BannerADInfo>();// 获取的广告list
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			// .showImageOnLoading(R.drawable.image_download_loading_icon)
			// .showImageForEmptyUri(R.drawable.image_download_loading_icon)
			.showImageOnFail(R.drawable.error).cacheInMemory(true)
			.cacheOnDisk(true).build();

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(final Message msg) {
			if (getActivity() == null) {// 没有在运行就返回
				return false;
			}
			switch (msg.what) {
			case HANDLER_SWITCH_AD://切换广告
				// 轮换广告
				adPosition++;// 显示下一个广告，根据类型来判断显示的是哪种广告
				if (adPosition >= adNum) {
					adPosition = 0;
				}
				String adType = AdList.get(adPosition).getAdType();// 广告类型
				if (TextUtils.isEmpty(adType)) {
					handler.sendEmptyMessage(0);// 不存在就下一个广告
					return false;
				}
				if (adType.equals("text")) {// 文字广告，直接显示
					initGifTextView();// 初始化
					giftext.setText("" + AdList.get(adPosition).getAdTitle());
					giftext.setOnClickListener(clickListener);
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							adLayout.removeView(giftext);// 移除回收该广告
							giftext = null;
							handler.sendEmptyMessage(0);// 切换
						}
					}, AdList.get(adPosition).getRotationSecond() * 1000);
				} else if (adType.equals("normalImage")) {// 普通广告
					initGifImageView();
					downloadAD(AdList.get(adPosition).getAdImageURLString(),
							Type_PNG);// 下载广告
				} else if (adType.equals("gifImage")) {// gif广告
					initGifImageView();
					downloadAD(AdList.get(adPosition).getAdImageURLString(),
							Type_GIF);// 下载广告
				}
				break;
			case HANDLER_RESTART_TEST://重新测速
				showTestingPage();
				break;
			case HANDLER_TEST_END://测速完成
				showSpeedResultPage();
				// 上传测速结果
				HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
				params.put("pingResult", SingletonSharedPreferences
						.getInstance().getValueOfPing());// ping值
				params.put("uploadSpeed", SingletonSharedPreferences
						.getInstance().getValueOfUpload() + "");// 上传速度
				params.put("downloadSpeed", SingletonSharedPreferences
						.getInstance().getValueOfDownload() + "");// 下载速度
				params.put("longitude", SingletonSharedPreferences
						.getInstance().getLontitude());// 经度
				params.put("latitude", SingletonSharedPreferences
						.getInstance().getLatitude());// 纬度
				params.put("networkOperator", ApplicationController.getInstance()
						.getNetworkOperator());// 网络运营商
				params.put("networkType", UtilFunc.encode(UtilFunc.getNetworkType(context)));// 网络类型
				JsonObjectRequest req = new JsonObjectRequest(APPConstant.UPLOAD_TEST_RESULT, new JSONObject(params),  
				       new Response.Listener<JSONObject>() {

						@Override
							public void onResponse(JSONObject response) {
//									VolleyLog.v("Response:%n %s", response.toString(4));
//									Log.e("response", "" + response.toString());
							}
				       }, new Response.ErrorListener() {
				           @Override  
				           public void onErrorResponse(VolleyError error) {  
				               VolleyLog.e("Error: ", error.getMessage());  
				               Log.e("response error",""+error.getMessage());
				           }  
				       });  
				ApplicationController.getInstance().addToRequestQueue(req);
				//发动态
				String mobile = null;
				if (MainAccountUtil.getCurrentAccount(context) != null
						&& !TextUtils.isEmpty(mobile = MainAccountUtil
								.getCurrentAccount(context).getMobile())) {
					ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
					keyValue.add(new KeyValue("mobile", mobile));
					keyValue.add(new KeyValue("activeType",
							ActiveType.SpeedTest.getValue()));//发动态--测速类型
					keyValue.add(new KeyValue("speed", ""
							+ SingletonSharedPreferences.getInstance()
									.getValueOfDownload() / 128.0));

					new AsyncNetworkManager().genActiveTimeLine(context, keyValue,
							new genActiveTimeLineCallBack() {

								@Override
								public void onGenActiveTimeLineCallBack(int rtn,int activeId) {
								}
							});
				}
				break;
			}
			return true;
		}
	});

	static Tab2_Fragment newInstance() {
		Tab2_Fragment newFragment = new Tab2_Fragment();
		return newFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		context = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tab2_fragment, container,
				false);
		ViewUtils.inject(this, rootView);
		
		// Post params to be sent to the server  
		HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
		JsonObjectRequest req = new JsonObjectRequest(APPConstant.BANNER_AD_URL, new JSONObject(params),  
		       new Response.Listener<JSONObject>() {

				@Override
					public void onResponse(JSONObject response) {
						try {
							Gson gson = new Gson();
							Type fooType = new TypeToken<List<BannerADInfo>>() {}.getType();
							List<BannerADInfo> bannerList = gson.fromJson(response
									.getJSONArray("BannerADList").toString(), fooType);

							if (bannerList == null)
								return;
							AdList.addAll(bannerList);
							if (AdList != null && AdList.size() > 0) {// 至少有一个广告
								adNum = AdList.size();
								adPosition = 0;// 显示第一个广告，根据类型来判断显示的是哪种广告
								String adType = AdList.get(adPosition)
										.getAdType();// 广告类型
								if (TextUtils.isEmpty(adType)) {
									return;
								}
								if (adType.equals("text")) {// 文字广告，直接显示
									initGifTextView();// 初始化
									giftext.setText(""
											+ AdList.get(adPosition)
													.getAdTitle());
									giftext.setOnClickListener(clickListener);
									if (adNum > 1) {// 只有一个的话就不切换广告
										handler.postDelayed(new Runnable() {

											@Override
											public void run() {
												adLayout.removeView(giftext);// 移除回收该广告
												giftext = null;
												handler.sendEmptyMessage(HANDLER_SWITCH_AD);// 切换
											}
										}, AdList.get(adPosition)
												.getRotationSecond() * 1000);
									}
								} else if (adType.equals("normalImage")) {// 普通广告
									initGifImageView();
									downloadAD(AdList.get(adPosition)
											.getAdImageURLString(), Type_PNG);// 下载广告
								} else if (adType.equals("gifImage")) {// gif广告
									initGifImageView();
									downloadAD(AdList.get(adPosition)
											.getAdImageURLString(), Type_GIF);// 下载广告
								}
							}
//							VolleyLog.v("Response:%n %s", response.toString(4));
//							Log.e("response", "" + response.toString());
						} catch (JSONException e) {
							e.printStackTrace();
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

		return rootView;
	}

	@Override
	public void onResume() {
		// 加入Fragment
		fragmentManager = getFragmentManager();
		if (ApplicationController.getInstance().isHasRecord()) {// 已经测过速了，进入的是表盘的页面
			showSpeedResultPage();
		} else {// 还没测过速，进入的是开始测速按钮的页面
			PrepareFragment pingFragment = new PrepareFragment();// 默认进入按钮页面，判断是否已经获取了url，没有的话就回调获取url
			pingFragment.setPrepareCallbacks(new TestStartCallback() {
				
				// 回调，取服务器url开始测速
				@Override
				public void onTestStartCallback() {
					// 本地没有url，服务器查询数据，然后测速
					ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
					keyValue.add(new KeyValue("statType", "speedTest"));
					AsyncNetworkManager activeNetwork = new AsyncNetworkManager();
					activeNetwork.asynSendData(context, keyValue);
					
					// 从服务器获得测速网址
					HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
					params.put("latitude ", SingletonSharedPreferences
							.getInstance().getLatitude());// 经纬度
					params.put("longitude ", SingletonSharedPreferences
							.getInstance().getLontitude());
					JsonObjectRequest req = new JsonObjectRequest(APPConstant.SERVER_URL, new JSONObject(params),  
					       new Response.Listener<JSONObject>() {

							@Override
								public void onResponse(JSONObject response) {
									String downloadServerURL = null, uploadServerURL = null, pingIPAddress = null;
									try {
										downloadServerURL = response
												.getString("downloadServerURL");
										uploadServerURL = response
												.getString("uploadServerURL");
										pingIPAddress = response
												.getString("pingIPAddress");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (!TextUtils.isEmpty(downloadServerURL)) {
										SingletonSharedPreferences
												.getInstance()
												.setDownloadServerURL(
														downloadServerURL);// 临时保存网址
										ApplicationController.getInstance()
												.setDownloadServerURL(
														downloadServerURL);
									}
									if (!TextUtils.isEmpty(uploadServerURL)) {
										SingletonSharedPreferences
												.getInstance()
												.setUploadServerURL(
														uploadServerURL);// 临时保存网址
										ApplicationController.getInstance()
												.setUploadServerURL(
														uploadServerURL);
									}
									if (!TextUtils.isEmpty(pingIPAddress)) {
										SingletonSharedPreferences
												.getInstance()
												.setPingIPAddress(pingIPAddress);
										ApplicationController
												.getInstance()
												.setPingIPAddress(pingIPAddress);
									}
								}
					       }, new Response.ErrorListener() {
					           @Override  
					           public void onErrorResponse(VolleyError error) {
					               VolleyLog.e("Error: ", error.getMessage());  
					               Log.e("response error",""+error.getMessage());
					           }  
					       });  
					ApplicationController.getInstance().addToRequestQueue(req);
					showTestingPage();
				}
			});
			pingFragment.setTargetFragment(this, 0);
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.replace(R.id.sola_speed_test_container,
					pingFragment);
			fragmentTransaction.commit();
		}
		super.onResume();
	}

	// 广告相关----------------------------
	/**
	 * 初始化gif显示控件，加入到adLayout中
	 */
	private void initGifImageView() {
		if (gif == null) {
			gif = new GifImageView(context);
			// Resources resources = context.getResources();
			// int heightInPx = Math.round(TypedValue.applyDimension(
			// TypedValue.COMPLEX_UNIT_SP, 50,
			// resources.getDisplayMetrics()));// 根据屏幕不同获取50sp的高度，然后根据高度计算出宽
			// int widthInPx = (int) (heightInPx / Global.ratio);
			// if (widthInPx > SingletonSharedPreferences.getInstance()
			// .getScreenWidth()) {
			// widthInPx = SingletonSharedPreferences.getInstance()
			// .getScreenWidth();// 超过屏幕就等于屏幕大小
			// }

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					SingletonSharedPreferences.getInstance().getScreenWidth(),
					Math.round(SingletonSharedPreferences.getInstance()
							.getScreenWidth() / 6.4f));
			params.gravity = Gravity.CENTER;
			adLayout.addView(gif, params);
		}
	}

	/**
	 * OnClickListener
	 */
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 广告的点击统计
			HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
			params.put("statType", "clickBannerAD");
			params.put("keyValue", "" + AdList.get(adPosition).getAdID());
			params.put("mobile", MainAccountUtil.getAvailablePhone(context));
			JsonObjectRequest req = new JsonObjectRequest(APPConstant.AD_CLICK_URL, new JSONObject(params),  
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
			
			Intent intent = new Intent();
			String clickUrl = AdList.get(adPosition).getClickURL();
			if (clickUrl.contains(APPConstant.URL_WITH_MOBILE_GMCC_OPEN)
					|| clickUrl.contains(APPConstant.URL_WITH_MOBILE_GDMOBILE)) {
				String phone = MainAccountUtil.getAvailablePhone(context);
				if (!TextUtils.isEmpty(phone)) {
					clickUrl += "&mobile=" + phone;
				}
			}
			try {
				intent.setData(Uri.parse(clickUrl));
				intent.setAction(Intent.ACTION_VIEW);
				context.startActivity(intent); // 启动浏览器
			} catch (Exception e) {

			}
		}
	};

	/**
	 * 下载广告的图片，建立映射关系等
	 * 
	 * @param url
	 */
	private void downloadAD(final String url, final int type) {
		final String md5Path = APPConstant.AD_DIRECTORY + File.separator
				+ Md5Util.md5s(url);// 取md5后的图片路径
		File imageFile = new File(md5Path);
		if (imageFile.exists()) {// 存在
			displayAd(md5Path, type);
		} else {// 本地不存在，去下载，然后显示
			if (!TextUtils.isEmpty(url)) {
				HttpUtils http = new HttpUtils();
				HttpHandler<File> httpHandler = http.download(url, md5Path,
						true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
						false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
						new RequestCallBack<File>() {

							@Override
							public void onStart() {
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {
								displayAd(md5Path, type);
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								handler.sendEmptyMessage(HANDLER_SWITCH_AD);// 不存在就下一个广告
							}
						});
			} else {
				handler.sendEmptyMessage(HANDLER_SWITCH_AD);// 不存在就下一个广告
			}
		}
	}

	private void displayAd(String md5Path, final int type) {
		// 加载图片
		switch (type) {
		case Type_PNG:
			ImageLoader.getInstance().displayImage("file:///" + md5Path, gif,
					options, null);
			break;
		case Type_GIF:
			GifDrawable gifFromPath = null;
			try {
				gifFromPath = new GifDrawable(md5Path);
			} catch (IOException e) {
				e.printStackTrace();
			}
			gif.setImageDrawable(gifFromPath);
			break;
		}
		gif.setOnClickListener(clickListener);
		if (adNum > 1) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (getActivity() != null) {
						switch (type) {
						case Type_PNG:
							break;
						case Type_GIF:
							GifDrawable gifFromPath = (GifDrawable) gif
									.getDrawable();
							if (gifFromPath != null) {
								gifFromPath.recycle();
								gifFromPath.setCallback(null);
							}
							break;
						}
						handler.sendEmptyMessage(HANDLER_SWITCH_AD);// 轮换广告
					}
				}
			}, AdList.get(adPosition).getRotationSecond() * 1000);// 定时器
		}
	}

	/**
	 * 初始化gifText显示控件，加入到adLayout中
	 */
	private void initGifTextView() {
		if (giftext == null) {
			giftext = new GifTextView(context);
			giftext.setTextColor(context.getResources().getColor(
					R.color.ad_text));
			// giftext.setTextSize(50 * SingletonSharedPreferences.getInstance()
			// .getScaleRate());
			// giftext.setGravity(Gravity.CENTER);
			// giftext.setBackgroundColor(context.getResources().getColor(
			// R.color.ad_text_bg));
			// Resources resources = getResources();
			// int heightInPx = Math.round(TypedValue.applyDimension(
			// TypedValue.COMPLEX_UNIT_SP, 50,
			// resources.getDisplayMetrics()));// 根据屏幕不同获取50sp的高度，然后根据高度计算出宽
			// int widthInPx = (int) (heightInPx / Global.ratio);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					SingletonSharedPreferences.getInstance().getScreenWidth(),
					Math.round(SingletonSharedPreferences.getInstance()
							.getScreenWidth() / 6.4f));
			params.gravity = Gravity.CENTER;

			adLayout.addView(giftext, params);
		}
	}

	/**
	 * 将文件生成位图
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public BitmapDrawable getImageDrawable(String path) throws IOException {
		// 打开文件
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] bt = new byte[BUFFER_SIZE];
		// 得到文件的输入流
		InputStream in = new FileInputStream(file);
		// 将文件读出到输出流中
		int readLength = in.read(bt);
		while (readLength != -1) {
			outStream.write(bt, 0, readLength);
			readLength = in.read(bt);
		}
		// 转换成byte 后 再格式化成位图
		byte[] data = outStream.toByteArray();
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// 生成位图
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		in.close();
		return bd;
	}

	/**
	 * 显示测速的结果页面
	 */
	private void showSpeedResultPage() {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		SpeedResultFragment mainFragment = new SpeedResultFragment(context,
				new OnRestartTestListener() {

					@Override
					public void onRestart() {
						handler.sendEmptyMessage(HANDLER_RESTART_TEST);
					}
				});
		fragmentTransaction.replace(R.id.sola_speed_test_container,
				mainFragment);
		fragmentTransaction.commit();
	}
	
	/**
	 * 显示测速页面
	 */
	private void showTestingPage(){
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		SpeedTestMainFragment mainFragment = new SpeedTestMainFragment(context,new OnSpeedTestEndListener() {
			
			@Override
			public void onEnd() {//测速完成 
				handler.sendEmptyMessage(HANDLER_TEST_END);
			}
		});
		fragmentTransaction.replace(R.id.sola_speed_test_container,
				mainFragment);
		fragmentTransaction.commit();
	}
	
	@Override
	public void onDestroy() {
		if (gif != null) {
			gif = null;
		}
		if (giftext != null) {
			giftext = null;
		}
		super.onDestroy();
	}
}
