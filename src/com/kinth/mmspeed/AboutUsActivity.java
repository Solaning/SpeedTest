package com.kinth.mmspeed;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.kinth.mmspeed.adapter.ShareGridAdapter;
import com.kinth.mmspeed.bean.ShareBean;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.singleton.ShareSingleton;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

/**
 * 关于我们
 * @author admin
 */
public class AboutUsActivity extends BaseActivity implements Callback {
	private ImageView backButton;
	private ImageView shareButton;
	private WebView webView;
	private final String urlString = "http://www.ydlly.cn/";
	private IWXAPI api;
	private PopupWindow popuWindow;      //PopupWindow
	private View parent;          //父布局
	private View contentView;    
	private  GridView gridView;       //分享的布局
	private Button btnCancel;         //取消按钮
	private ShareGridAdapter shareGridAdapter;    //适配器
	private List<ShareBean>shareBeans;       //分享
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, APPConstant.WEXIN_APP_KEY);
		api.registerApp(APPConstant.WEXIN_APP_KEY);
		
		setContentView(R.layout.activity_about_us);
		backButton = (ImageView) findViewById(R.id.iv_about_us_back);
		shareButton = (ImageView) findViewById(R.id.about_us_share_btn);
		backButton.setOnClickListener(listener);
		shareButton.setOnClickListener(listener);
		ShareSingleton.getInstance().setShareWhere(ShareSingleton.FROM_ABOUTUS);  //设置分享是从  about us页面
		webView = (WebView) findViewById(R.id.activity_about_us_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
		
//		webView.setDownloadListener(new MyWebViewDownLoadListener());
		webView.loadUrl(urlString);
		
		parent = findViewById(R.id.rtl_share_main);
		contentView = this.getLayoutInflater().inflate(R.layout.popupwindow_share, null);
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
				case 0:     //微信
					ShareSingleton.getInstance().setShareWhere(ShareSingleton.TYPE_WEIXIN); 
					shareWechatClick();
					break;
				case 1:     //朋友圈
					ShareSingleton.getInstance().setShareWhere(ShareSingleton.TYPE_WEIXIN_GROUP); 
					shareWechatMomentsClick();
					break;
				case 2:     //短信
					ShareSingleton.getInstance().setShareWhere(ShareSingleton.TYPE_SHORT_MSG); 
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
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismisPopuWindow();
			}
		});
	}
	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_about_us_back:
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
				break;
			case R.id.about_us_share_btn:// 分享，截图
				showPopuWindow(v);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 显示pop窗口
	 * @param v
	 */
	public void showPopuWindow(View v){
		if(null == popuWindow){
			popuWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			popuWindow.setFocusable(true);
			popuWindow.setBackgroundDrawable(new BitmapDrawable());
			popuWindow.setAnimationStyle(R.style.popu_animation);
		}
		popuWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		AboutUsActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);  
	}
	
	public static void shareText(Context context) {
		String shareStr = context.getString(R.string.text_share).toString();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain"); // 纯文本
		intent.putExtra(Intent.EXTRA_SUBJECT, "标题");
		intent.putExtra(Intent.EXTRA_TEXT, shareStr);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, "标题"));
	}
	/**
	 * 微信分享
	 * @param v
	 */
	public void shareWechatClick() {
		if (api.isWXAppInstalled()
				&& api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = "http://t.cn/8F7uemq";
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "移动流量仪";
			msg.description =  getString(R.string.text_share).toString();
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
			JUtil.showMsg(AboutUsActivity.this, "微信客户端未安装，请先安装微信");
		}
	}
	/**
	 * 微信朋友圈分享
	 */
	public void shareWechatMomentsClick() {
		if (api.isWXAppInstalled()
				&& api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = "http://t.cn/8F7uemq";
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "移动流量仪";
			msg.description =getString(R.string.text_share).toString();
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
			JUtil.showMsg(AboutUsActivity.this, "微信客户端未安装，请先安装微信");
		}
	}
	/**
	 * 短信分享
	 */
	public void shareShortMsgOnClick() {
		String shareText = getString(R.string.text_share).toString();
		Uri smsToUri = Uri.parse("smsto:");    
	    Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );  
	    mIntent.putExtra("sms_body", shareText);  
	    startActivity( mIntent );
	}
	/**
	 * 邮件分享
	 */
	private void shareEmail() {
		String shareText = getString(R.string.text_share).toString();
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
//		emailReciver = new String[]{"www.linuxidc.com@www.linuxidc.com.com", "1234567@www.linuxidc.com.com"};
//		emailSubject = "你有一条短信";
//		emailBody = sb.toString();
		//设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		//设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, "移动流量仪");
		//设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
		//调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AboutUsActivity.this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			JUtil.showMsg(AboutUsActivity.this, "取消");
			break;
		case 1:
			JUtil.showMsg(AboutUsActivity.this, "成功");
			break;
		case -1:
			JUtil.showMsg(AboutUsActivity.this, "失败");
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
		if(popuWindow.isShowing()){   
			popuWindow.dismiss();   
         }   
	}
}
