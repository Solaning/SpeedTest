package com.kinth.mmspeed;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
@ContentView(R.layout.activity_flowweb)
public class FlowWebActivity extends BaseActivity {
	public static final String INTENT_URL = "url";
	
	@ViewInject(R.id.activity_flowweb_webview)
	private WebView webView;
	
	@ViewInject(R.id.iv_activity_flowweb_back)
	private ImageView back;
	
	@ViewInject(R.id.WebViewProgress)
	private ProgressBar mProgressBar;
	
	@OnClick(R.id.iv_activity_flowweb_back)
	public void fun_1(View w) {
		FlowWebActivity.this.finish();
		rightOutFinishAnimation();
	}
	
	String urlString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		urlString = getIntent().getStringExtra(INTENT_URL);
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
		webView.loadUrl(urlString);
	}

}
