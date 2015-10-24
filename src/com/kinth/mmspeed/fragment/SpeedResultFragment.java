package com.kinth.mmspeed.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.ShareGridAdapter;
import com.kinth.mmspeed.bean.ShareBean;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.Constants;
import com.kinth.mmspeed.hk.SingletonBitmapClass;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.ui.MMAlert;
import com.kinth.mmspeed.ui.MyTestView;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

/**
 * 测速结果的Fragment
 * 
 * @author Sola
 */
public class SpeedResultFragment extends Fragment {
	private Context mContext;
	private OnRestartTestListener listener;//监听器
	
	public SpeedResultFragment(Context context, OnRestartTestListener listener) {
		super();
		this.mContext = context;
		this.listener = listener;
	}
	
	private BitmapFactory.Options bfoOptions;
	// 控制变量
	private float Scale_Rate;// 缩放比例
	// 组件
	private View rootView;

	interface OnRestartTestListener{
		public void onRestart();
	}
	
	// add @2014-09-12 微信、微博分享
	private IWXAPI api;
	private IWeiboShareAPI mWeiboShareAPI;
	private boolean isInstalledWeibo;
	private PopupWindow mPopuWindow;

	@ViewInject(R.id.dial_layout)
	private RelativeLayout dial_layout;// 表盘layout

	private MyTestView myView;// 表盘view

	@ViewInject(R.id.speed_test_ping_result)
	private TextView Ping_Result;// ping的结果

	@ViewInject(R.id.speed_test_ping_word)
	private TextView Ping_Word;//

	@ViewInject(R.id.speed_test_download_result)
	private TextView Download_Result;// 下载速度的结果

	@ViewInject(R.id.speed_test_download_word)
	private TextView Download_Word;

	@ViewInject(R.id.speed_test_upload_result)
	private TextView Upload_Resutl;// 上传速度的结果

	@ViewInject(R.id.speed_test_upload_word)
	private TextView Upload_Word;

	@ViewInject(R.id.restart_test)
	private Button restart_test;// 重新测速

	@ViewInject(R.id.share_test_result)
	private RelativeLayout share;

	@ViewInject(R.id.pick_unit)
	private TextView pick_unit;// 选单位

	@OnClick(R.id.restart_test)// 重新测速
	public void fun_1(View v) {
		if(listener != null){
			listener.onRestart();
		}
	}
	
	@OnClick(R.id.share_test_result)// 分享按钮
	public void fun_2(View v) {
		// File filePath = new File(Global.SDCARD_DIRECTORY
		// + File.separator + "LiuLiangYi", "share.png");// 保存到这里
		// ScreenShot.shoot(getActivity(), filePath);// 保存图片

		showPopuWindow();
		// @2014-09-12
		// shareMsg(context, "标题", "Activity标题","我在用移动流量仪测速，下载速度"+
		// Download_Result.getText().toString()+"，上传速度"+Upload_Resutl.getText().toString()+"，来试一下跟我比比吧！访问http://ydlly.cn即可下载。",
		// "");
		// share();
	}
	
	@OnClick(R.id.pick_unit)// 切换单位
	public void fun_3(View v) {
		boolean unitMode = SingletonSharedPreferences.getInstance()
				.getUnitMode();
		Bitmap clockBitmap = null;
		if (unitMode) {// Mbps模式，切换到kB/s
			pick_unit.setText("切换单位\nkB/s");
			clockBitmap = SingletonBitmapClass.getInstance().getClockBitmap(
					!unitMode);
		} else {// kB/s，切换到Mbps
			pick_unit.setText("切换单位\nMbps");
			clockBitmap = SingletonBitmapClass.getInstance().getClockBitmap(
					!unitMode);
		}
		SingletonSharedPreferences.getInstance().setUnitMode(!unitMode);
		Download_Result.setText(UtilFunc.getSpanResult(mContext,
				SingletonSharedPreferences.getInstance().getValueOfDownload()));
		Upload_Resutl.setText(UtilFunc.getSpanResult(mContext,
				SingletonSharedPreferences.getInstance().getValueOfUpload()));
		myView.changeChassisDrawable(UtilFunc.Bitmap2Drawable(clockBitmap,
				mContext));
		// 刷新方法
		myView.invalidate();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.sola_speed_test_main_layout, null);
		ViewUtils.inject(this, rootView);

		// 微信、微博 @2014-09-12
		api = WXAPIFactory.createWXAPI(mContext, APPConstant.WEXIN_APP_KEY);
		api.registerApp(APPConstant.WEXIN_APP_KEY);
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext,
				Constants.APP_KEY);
		isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
		if (isInstalledWeibo) {
			mWeiboShareAPI.registerApp();
		}
		// 缩放比率
		Scale_Rate = SingletonSharedPreferences.getInstance().getScaleRate();
		// 计算图片的缩放大小
		bfoOptions = new BitmapFactory.Options();
		bfoOptions.inScaled = false;// 真实大小不变形
		if (myView == null) {
			Bitmap clockBitmap = SingletonBitmapClass.getInstance().getClockBitmap(
					SingletonSharedPreferences.getInstance().getUnitMode());
			Bitmap hourBitmap = SingletonBitmapClass.getInstance().getHourBitmap();
			myView = new MyTestView(mContext);
			// 设置底盘相对于视图坐标为
			myView.setChassisOffset(
					(SingletonSharedPreferences.getInstance().getScreenWidth() - clockBitmap
							.getWidth()) / 2,
					SingletonSharedPreferences.getInstance()
							.getScreenCenterHeight(getActivity())
							- clockBitmap.getHeight()
							/ 2
							- (int) (196 * Scale_Rate));
			myView.init(UtilFunc.Bitmap2Drawable(clockBitmap, mContext),
					UtilFunc.Bitmap2Drawable(hourBitmap, mContext), Scale_Rate,
					clockBitmap.getWidth(), clockBitmap.getHeight(),
					hourBitmap.getWidth(), hourBitmap.getHeight());// 初始化，，
			myView.setCenterOffset((int) (-2 * Scale_Rate),
					(int) (96 * Scale_Rate));// 中心点的偏移
			myView.setPointOffset((int) (53 * Scale_Rate)); // 12点钟方向指针向下偏移53像素
		}
		myView.calcDegree(new SpannableString(""),
				ApplicationController.MinDegree, true);
		dial_layout.addView(myView);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
//		The specified child already has a parent. You must call removeView() on the child's parent first.
		// 其他初始化
		restart_test.setVisibility(View.VISIBLE);
		share.setVisibility(View.VISIBLE);
		pick_unit.setVisibility(View.VISIBLE);
		Ping_Result.setText(UtilFunc.getPingSpannable(mContext,
				SingletonSharedPreferences.getInstance().getValueOfPing()));
		Download_Result.setText(UtilFunc.getSpanResult(mContext,
				SingletonSharedPreferences.getInstance().getValueOfDownload()));
		Upload_Resutl.setText(UtilFunc.getSpanResult(mContext,
				SingletonSharedPreferences.getInstance().getValueOfUpload()));
	}

	// 重写分享 @2014-09-12
	private void showPopuWindow() {
		if (mPopuWindow == null) {
			View contentView = LayoutInflater.from(mContext).inflate(
					R.layout.popupwindow_share, null);
			GridView gridView = (GridView) contentView
					.findViewById(R.id.gridview);
			Button btnCancel = (Button) contentView
					.findViewById(R.id.popu_cancel);
			List<ShareBean> shareBeans = new ArrayList<ShareBean>();
			shareBeans.add(new ShareBean(R.drawable.logo_wechat, "微信"));
			shareBeans.add(new ShareBean(R.drawable.logo_wechatmoments, "朋友圈"));
			shareBeans.add(new ShareBean(R.drawable.logo_sinaweibo, "微博"));
			shareBeans.add(new ShareBean(R.drawable.logo_shortmessage, "短信"));

			ShareGridAdapter shareGridAdapter = new ShareGridAdapter(mContext,
					shareBeans);
			gridView.setAdapter(shareGridAdapter);

			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0: // 微信
						shareWechatClick(Download_Result.getText().toString(),
								Upload_Resutl.getText().toString());
						break;
					case 1: // 朋友圈
						shareWechatMomentsClick(Download_Result.getText()
								.toString(), Upload_Resutl.getText().toString());
						break;
					case 2: // 微博
						shareWeibo(Download_Result.getText().toString(),
								Upload_Resutl.getText().toString());
						break;
					case 3: // 短信
						shareShortMsgOnClick(Download_Result.getText()
								.toString(), Upload_Resutl.getText().toString());
						break;
					default:
						break;
					}
					dismisPopuWindow();
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dismisPopuWindow();
				}
			});
			mPopuWindow = new PopupWindow(contentView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mPopuWindow.setFocusable(true);
			mPopuWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopuWindow.setAnimationStyle(R.style.popu_animation);
		}
		mPopuWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
	}
	
	public void dismisPopuWindow() {
		if (mPopuWindow != null && mPopuWindow.isShowing()) {
			mPopuWindow.dismiss();
		}
	}

	/**
	 * 微信分享
	 * 
	 * @param v
	 */
	public void shareWechatClick(String download, String upload) {
		if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
			String text = getString(R.string.share_speed_test_result, download,
					upload);

			final EditText editor = new EditText(mContext);
			editor.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			editor.setText(text);
			MMAlert.showAlert(mContext, "编辑", editor, "分享", "取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String text = editor.getText().toString();
							if (text == null || text.length() == 0) {
								return;
							}

							// 初始化一个WXTextObject对象
							WXTextObject textObj = new WXTextObject();
							textObj.text = text;

							// 用WXTextObject对象初始化一个WXMediaMessage对象
							WXMediaMessage msg = new WXMediaMessage();
							msg.mediaObject = textObj;
							// 发送文本类型的消息时，title字段不起作用
							// msg.title = "Will be ignored";
							msg.description = text;

							// 构造一个Req
							SendMessageToWX.Req req = new SendMessageToWX.Req();
							req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
							req.message = msg;
							req.scene = SendMessageToWX.Req.WXSceneSession;

							// 调用api接口发送数据到微信
							api.sendReq(req);
						}
					}, null);
		} else {
			JUtil.showMsg(mContext, "微信客户端未安装，请先安装微信");
		}
	}

	// 分享朋友圈
	public void shareWechatMomentsClick(String download, String upload) {
		if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
			String text = getString(R.string.share_speed_test_result, download,
					upload);

			final EditText editor = new EditText(mContext);
			editor.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			editor.setText(text);
			MMAlert.showAlert(mContext, "编辑", editor, "分享", "取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String text = editor.getText().toString();
							if (text == null || text.length() == 0) {
								return;
							}

							// 初始化一个WXTextObject对象
							WXTextObject textObj = new WXTextObject();
							textObj.text = text;

							// 用WXTextObject对象初始化一个WXMediaMessage对象
							WXMediaMessage msg = new WXMediaMessage();
							msg.mediaObject = textObj;
							// 发送文本类型的消息时，title字段不起作用
							// msg.title = "Will be ignored";
							msg.description = text;

							// 构造一个Req
							SendMessageToWX.Req req = new SendMessageToWX.Req();
							req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
							req.message = msg;
							req.scene = SendMessageToWX.Req.WXSceneTimeline;

							// 调用api接口发送数据到微信
							api.sendReq(req);
						}
					}, null);
		} else {
			JUtil.showMsg(mContext, "微信客户端未安装，请先安装微信");
		}
	}

	public void shareShortMsgOnClick(String download, String upload) {
		String shareText = getString(R.string.share_speed_test_result,
				download, upload);
		Uri smsToUri = Uri.parse("smsto:");
		Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		mIntent.putExtra("sms_body", shareText);
		startActivity(mIntent);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private void shareWeibo(String download, String upload) {
		String shareText = getString(R.string.share_speed_test_result,
				download, upload);

		// 如果未安装微博客户端，设置下载微博对应的回调
		if (!isInstalledWeibo) {
			mWeiboShareAPI
					.registerWeiboDownloadListener(new IWeiboDownloadListener() {
						@Override
						public void onCancel() {
							Toast.makeText(mContext, "取消下载", Toast.LENGTH_SHORT)
									.show();
						}
					});
		}
		try {
			// 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
			if (mWeiboShareAPI.checkEnvironment(true)) {
				// 1. 初始化微博的分享消息
				// 用户可以分享文本、图片、网页、音乐、视频中的一种
				WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
				TextObject textObject = new TextObject();
				textObject.text = shareText;
				weiboMessage.textObject = textObject;
				// weiboMessage.mediaObject = getWebpageObj();

				// 2. 初始化从第三方到微博的消息请求
				SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
				// 用transaction唯一标识一个请求
				request.transaction = String
						.valueOf(System.currentTimeMillis());
				request.multiMessage = weiboMessage;

				// 3. 发送请求消息到微博，唤起微博分享界面
				mWeiboShareAPI.sendRequest(request);
			}
		} catch (WeiboShareException e) {
			e.printStackTrace();
			Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
