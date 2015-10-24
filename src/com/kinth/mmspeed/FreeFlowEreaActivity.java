package com.kinth.mmspeed;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.ShareGridAdapter;
import com.kinth.mmspeed.bean.ShareBean;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.Md5Util;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sola.code.ydlly_assist.bean.KeyValue;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

/**
 * 免流量专区
 * 
 * @author Sola
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
@ContentView(R.layout.activity_flow_free_list)
public class FreeFlowEreaActivity extends BaseActivity {
	public static final String INTENT_PHONE_NUMBER = "INTENT_PHONE_NUMBER";
	
	private Context mContext;
	private String urlString;
	private Button btnCancel;
	private IWXAPI api;
	private PopupWindow mPopuWindow; // PopupWindow

	private GridView gridView;
	private View contentView;
	private List<ShareBean> shareBeans;
	private ShareGridAdapter shareGridAdapter;
	private String phonenumber;

	@ViewInject(R.id.rlt_main_download)
	private View parent; // 父布局

	@ViewInject(R.id.ibt_share)
	private ImageButton btnShare;

	@ViewInject(R.id.WebViewProgress)
	private ProgressBar mProgressBar;

	@ViewInject(R.id.activity_cmcc_ad_webview)
	private WebView webView;

	@ViewInject(R.id.PreviousBtn)
	private ImageView previous;

	@ViewInject(R.id.NextBtn)
	private ImageView next;

	@ViewInject(R.id.RefreshBtn)
	private ImageView refresh;

	@ViewInject(R.id.activity_cmcc_ad_back)
	private ImageView back;

	@OnClick(R.id.PreviousBtn)
	public void fun_1(View v) {// 上一个
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			finish();
			rightOutFinishAnimation();
		}
	}

	@OnClick(R.id.NextBtn)
	public void fun_2(View v) {// 下一个
		if (webView.canGoForward()) {
			webView.goForward();
		}
	}

	@OnClick(R.id.RefreshBtn)
	public void fun_3(View v) {// 刷新
		webView.reload();
	}

	@OnClick(R.id.activity_cmcc_ad_back)
	public void fun_4(View w) {// 退出
		FreeFlowEreaActivity.this.finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.ibt_share)
	public void fun_5(View v){
		showPopuWindow();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);

		api = WXAPIFactory.createWXAPI(this, APPConstant.WEXIN_APP_KEY);
		api.registerApp(APPConstant.WEXIN_APP_KEY);
		initShare();
		phonenumber = getIntent().getStringExtra(INTENT_PHONE_NUMBER);
		try {
			initUrl();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			urlString = null;
			return;
		}

		mProgressBar.setMax(100);
		mProgressBar.setProgress(10);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mProgressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgressBar.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				mProgressBar.setProgress(newProgress);
			}

		});
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		webView.loadUrl(urlString);
		
		//判断是否登录---->产生一条动态，免流量专区
		String mobile = null;
		if (MainAccountUtil.getCurrentAccount(mContext) != null
				&& !TextUtils.isEmpty(mobile = MainAccountUtil.getCurrentAccount(
						mContext).getMobile())) {
			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
			keyValue.add(new KeyValue("mobile",mobile));
			keyValue.add(new KeyValue("activeType",ActiveType.FreeFlow.getValue()));
			new AsyncNetworkManager().genActiveTimeLine(mContext, keyValue,
					new genActiveTimeLineCallBack() {

						@Override
						public void onGenActiveTimeLineCallBack(int rtn,int activeId) {

						}
					});
		}
	}

	/**
	 * 生成免流量下载的地址
	 * @throws UnsupportedEncodingException 
	 */
	private void initUrl() throws UnsupportedEncodingException {
		// urlString = "http://a.10086.cn/gz";

		if(TextUtils.isEmpty(phonenumber)){//没有传值就使用默认号码
			phonenumber = JUtil.getSharePreStr(mContext, APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE);
		}
		StringBuffer urlencode = new StringBuffer(phonenumber);
		urlencode.append("+");
		StringBuffer md5 = new StringBuffer();
		md5.append(phonenumber);
		md5.append("+");
		md5.append(System.currentTimeMillis() / 1000);
		md5.append("+");
		md5.append(APPConstant.CMCC_RECOMMEND_DOWNLOAD_PASSWORD);
		urlencode.append(Md5Util.md5s(md5.toString()));
		urlencode.append("+");
		urlencode.append(System.currentTimeMillis() / 1000);
		urlString = APPConstant.CMCC_RECOMMEND_DOWNLOAD_URL + "?uid="
				+ URLEncoder.encode(urlencode.toString(), "utf-8") + "&pid="
				+ APPConstant.CMCC_RECOMMEND_DOWNLOAD_PID;
	}

	private void initShare() {
		contentView = FreeFlowEreaActivity.this.getLayoutInflater().inflate(
				R.layout.popupwindow_share, null);
		gridView = (GridView) contentView.findViewById(R.id.gridview);
		btnCancel = (Button) contentView.findViewById(R.id.popu_cancel);
		shareBeans = new ArrayList<ShareBean>();
		shareBeans.add(new ShareBean(R.drawable.logo_wechat, "微信"));
		shareBeans.add(new ShareBean(R.drawable.logo_wechatmoments, "朋友圈"));
		shareBeans.add(new ShareBean(R.drawable.logo_shortmessage, "短信"));
		shareBeans.add(new ShareBean(R.drawable.logo_email, "邮件"));

		shareGridAdapter = new ShareGridAdapter(this, shareBeans);
		gridView.setAdapter(shareGridAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0: // 微信
					shareWechatClick();
					break;
				case 1: // 朋友圈
					shareWechatMomentsClick();
					break;
				case 2: // 短信
					shareShortMsgOnClick();
					break;
				case 3: // 邮件
					shareEmail();
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
	}

	private void shareEmail() {
		String shareText = getString(R.string.text_share_download).toString()
				+ " " + urlString;
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		// emailReciver = new String[]{"www.linuxidc.com@www.linuxidc.com.com",
		// "1234567@www.linuxidc.com.com"};
		// emailSubject = "你有一条短信";
		// emailBody = sb.toString();

		// 设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, "免流量专区");
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
		// 调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));

	}

	public void shareShortMsgOnClick() {
		String shareText = getString(R.string.text_share_download).toString()
				+ " " + urlString;
		Uri smsToUri = Uri.parse("smsto:");
		Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		mIntent.putExtra("sms_body", shareText);
		startActivity(mIntent);
	}

	public void showPopuWindow() {
		if (null == mPopuWindow) {
			mPopuWindow = new PopupWindow(contentView,
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			mPopuWindow.setFocusable(true);
			mPopuWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopuWindow.setAnimationStyle(R.style.popu_animation);
		}
		mPopuWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		FreeFlowEreaActivity.this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
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
	public void shareWechatClick() {
		if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = urlString;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "免流量专区";
			msg.description = getString(R.string.text_share_download)
					.toString();
			Bitmap thumb = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			msg.thumbData = Util.bmpToByteArray(thumb, true);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			// req.scene = isTimelineCb.isChecked() ?
			// SendMessageToWX.Req.WXSceneTimeline :
			// SendMessageToWX.Req.WXSceneSession;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			ApplicationController.getInstance().setShareWXType(1);
			api.sendReq(req);
		} else {
			JUtil.showMsg(FreeFlowEreaActivity.this, "微信客户端未安装，请先安装微信");
		}
	}

	public void shareWechatMomentsClick() {
		if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = urlString;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "免流量专区";
			msg.description = getString(R.string.text_share_download)
					.toString();
			Bitmap thumb = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			msg.thumbData = Util.bmpToByteArray(thumb, true);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			// req.scene = isTimelineCb.isChecked() ?
			// SendMessageToWX.Req.WXSceneTimeline :
			// SendMessageToWX.Req.WXSceneSession;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			ApplicationController.getInstance().setShareWXType(0);
			api.sendReq(req);
		} else {
			JUtil.showMsg(FreeFlowEreaActivity.this, "微信客户端未安装，请先安装微信");
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);// 使用浏览器下载
			startActivity(intent);
		}
	}

}
