package com.kinth.mmspeed;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.util.B64Code;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ShareAwardResultActivity extends BaseActivity {
	private String url = APPConstant.HOST + "MMService/page/result.jsp?s=";
	@ViewInject(R.id.webview)
	private WebView mWebView;
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	@ViewInject(R.id.nav_right_text)
	private TextView tvRight;
	private boolean firstIn = true;
	public static final String INTENT_FIRSTIN= "intent";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_firstin);
		ViewUtils.inject(this);
		tvTittle.setText("推荐结果");
		
		tvRight.setVisibility(View.INVISIBLE);
		
		String uid = UtilFunc.getAndroidDeviceUUID(ShareAwardResultActivity.this);
		final String mUrl = url +String.valueOf(B64Code.encode(uid.getBytes()));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
//		webView.setDownloadListener(new MyWebViewDownLoadListener());
		mWebView.loadUrl(mUrl);
	}
	
}
