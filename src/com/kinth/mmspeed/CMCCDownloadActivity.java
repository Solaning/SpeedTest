package com.kinth.mmspeed;

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
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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
import android.widget.TextView;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.ShareGridAdapter;
import com.kinth.mmspeed.bean.ShareBean;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
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
 * 应用下载
 */
@ContentView(R.layout.activity_app_download_list)
public class CMCCDownloadActivity extends BaseActivity implements Callback{
	
	@ViewInject(R.id.activity_flowweb_webview)
	private WebView webView;
	
	@ViewInject(R.id.tv_tittle)
	private TextView tvTittle;
	
	@ViewInject(R.id.iv_share)
	private ImageButton btnRight;
	
	@ViewInject(R.id.ibt_back)
	private ImageButton btnBack;
	
	@ViewInject(R.id.WebViewProgress)
	private ProgressBar mProgressBar;
	
	@ViewInject(R.id.PreviousBtn)
	private ImageView previous;

	@ViewInject(R.id.NextBtn)
	private ImageView next;

	@ViewInject(R.id.RefreshBtn)
	private ImageView refresh;
	
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
	
	private Context mContext;
	private Button btnCancel;
	private IWXAPI api;
	private PopupWindow mPopuWindow;      //PopupWindow
	private View parent;          //父布局
	private GridView gridView;
	private View contentView;    
	private List<ShareBean>shareBeans;
	private ShareGridAdapter shareGridAdapter;
	
	private String urlString = "http://a.10086.cn/gz";
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		api = WXAPIFactory.createWXAPI(this, APPConstant.WEXIN_APP_KEY);
		api.registerApp(APPConstant.WEXIN_APP_KEY);
		initView();
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
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
		
		webView.loadUrl(urlString);
		
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
				case 0:     //微信
					shareWechatClick();
					break;
				case 1:     //朋友圈
					shareWechatMomentsClick();
					break;
				case 2:     //短信
					shareShortMsgOnClick();
					break;
				case 3:     //邮件
					shareEmail();
					break;
				default:
					break;
				}
				dismisPopuWindow();
			}
		});
		btnRight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showPopuWindow(arg0);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismisPopuWindow();
			}
		});
		
		//判断是否登录---->产生一条动态，下载应用
		String mobile = null;
		if(MainAccountUtil.getCurrentAccount(mContext) != null && !TextUtils.isEmpty(mobile = MainAccountUtil.getCurrentAccount(mContext).getMobile())){
			//根据定位位置请求数据
			ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
			keyValue.add(new KeyValue("mobile",mobile));
			keyValue.add(new KeyValue("activeType",ActiveType.DownLoadApp.getValue()));
			
			new AsyncNetworkManager().genActiveTimeLine(mContext,keyValue,new genActiveTimeLineCallBack(){

				@Override
				public void onGenActiveTimeLineCallBack(int rtn,int activeId) {
					
			}});
		}
	}
	
	private void initView() {
		tvTittle.setText("应用下载");
		parent = findViewById(R.id.llt_main_free);
		contentView = CMCCDownloadActivity.this.getLayoutInflater().inflate(R.layout.popupwindow_share, null);
		gridView = (GridView) contentView.findViewById(R.id.gridview);
		btnCancel = (Button) contentView.findViewById(R.id.popu_cancel);
		
	}
	public void showPopuWindow(View v){
		if(null == mPopuWindow){
			mPopuWindow = new PopupWindow(contentView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			mPopuWindow.setFocusable(true);
			mPopuWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopuWindow.setAnimationStyle(R.style.popu_animation);
		}
		mPopuWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		CMCCDownloadActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);  
	}
	/**
	 * 微信分享
	 * @param v
	 */
	public void shareWechatClick() {
		if (api.isWXAppInstalled()
				&& api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = urlString;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "应用下载";
			msg.description =  getString(R.string.text_share_free).toString();
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.thumbData = Util.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
//			req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
			req.scene =  SendMessageToWX.Req.WXSceneSession;
			ApplicationController.getInstance().setShareWXType(1);
			api.sendReq(req);
		}else {
			JUtil.showMsg(CMCCDownloadActivity.this, "微信客户端未安装，请先安装微信");
		}
	}
	public void shareWechatMomentsClick() {
		if (api.isWXAppInstalled()
				&& api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = urlString;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "应用下载";
			msg.description =getString(R.string.text_share_free).toString();
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.thumbData = Util.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
//			req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
			req.scene =  SendMessageToWX.Req.WXSceneTimeline;
			ApplicationController.getInstance().setShareWXType(0);
			api.sendReq(req);
		}else {
			JUtil.showMsg(CMCCDownloadActivity.this, "微信客户端未安装，请先安装微信");
		}
	}
	public void shareShortMsgOnClick() {
		String shareText = getString(R.string.text_share_free).toString()+" "+urlString;
		Uri smsToUri = Uri.parse("smsto:");    
	    Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );  
	    mIntent.putExtra("sms_body", shareText);  
	    startActivity( mIntent );
	}
	private void shareEmail() {
		String shareText = getString(R.string.text_share_free).toString()+" "+urlString;
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
//		emailReciver = new String[]{"www.linuxidc.com@www.linuxidc.com.com", "1234567@www.linuxidc.com.com"};
//		emailSubject = "你有一条短信";
//		emailBody = sb.toString();

		//设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		//设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, "应用下载");
		//设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
		//调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			JUtil.showMsg(CMCCDownloadActivity.this, "取消");
			break;
		case 1:
			JUtil.showMsg(CMCCDownloadActivity.this, "成功");
			break;
		case -1:
			JUtil.showMsg(CMCCDownloadActivity.this, "失败");
			break;
		default:
			break;
		}
		return false;
	}
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	public void dismisPopuWindow(){
		if(mPopuWindow!=null&&mPopuWindow.isShowing()){   
			mPopuWindow.dismiss();   
         }   
	}
	
}
