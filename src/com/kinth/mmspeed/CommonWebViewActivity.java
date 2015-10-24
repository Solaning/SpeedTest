package com.kinth.mmspeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 通用的WebView組件
 * @author Sola
 */
@SuppressLint("SetJavaScriptEnabled")
@ContentView(R.layout.activity_flow_shared)
public class CommonWebViewActivity extends BaseActivity{
	public static final String INTENT_TARGET_CLASS_NAME = "INTENT_TARGET_CLASS_NAME";
	public static final String INTENT_URL = "INTENT_URL";//要去往的url地址
	public static final String INTENT_TITLE = "INTENT_TITLE";//要显示的title内容
	
	private Context mContext;
	private String targetClassName;
	private String url;
	private String title;
	
	@ViewInject(R.id.back)
	private ImageView back;
	
	@ViewInject(R.id.tv_title)
	private TextView titleView;
	
	@ViewInject(R.id.WebViewProgress)
	private ProgressBar mProgressBar;
	
	@ViewInject(R.id.webview)
	private WebView webView;
	
	@ViewInject(R.id.PreviousBtn)
	private ImageView previous;
	
	@ViewInject(R.id.NextBtn)
	private ImageView next;
	
	@ViewInject(R.id.RefreshBtn)
	private ImageView refresh;
	
	@OnClick(R.id.PreviousBtn)
	public void fun_1(View v){//上一个
		if(webView.canGoBack()){
			webView.goBack();
		}else{
			back();
		}
	}
	
	@OnClick(R.id.NextBtn)
	public void fun_2(View v){//下一个
		if(webView.canGoForward()){
			webView.goForward();
		}
	}
	
	@OnClick(R.id.RefreshBtn)
	public void fun_3(View v){//刷新
		webView.reload();
	}
	
	@OnClick(R.id.back)
	public void fun_4(View v){//右上角退出
		back();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		mProgressBar.setMax(100);
		if(getIntent() !=null){
			targetClassName = getIntent().getStringExtra(INTENT_TARGET_CLASS_NAME);
			url = getIntent().getStringExtra(INTENT_URL);
			title = getIntent().getStringExtra(INTENT_TITLE);
			if(!TextUtils.isEmpty(title)){
				titleView.setText(title);
			}
			if(TextUtils.isEmpty(url)){
				webView.loadDataWithBaseURL(null, "无效的链接地址！！","text/html", "utf-8", null);
				return;
			}
		
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
			webView.setDownloadListener(new MyWebViewDownLoadListener());
			webView.loadUrl(url);
		}else{
			titleView.setText("空白页面");
			webView.loadDataWithBaseURL(null, "无效的链接地址！！","text/html", "utf-8", null);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(webView.canGoBack()){
				webView.goBack();
			}else{
				back();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 如果targetClassName有值，就跳转到目标界面
	 * @2015-01-06
	 */
	private void back(){
		Class<?> clazz = UtilFunc.loadClass(mContext, targetClassName);
		if(clazz != null){
			Intent intent = new Intent(mContext, clazz);
			startActivity(intent);
		}
		finish();
		rightOutFinishAnimation();
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
