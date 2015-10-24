package com.kinth.mmspeed;

import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ShareInstructionActivity extends BaseActivity {
	private String url = "http://t.cn/RvzgR3U";
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
		tvTittle.setText("活动说明");
		
		int result = JUtil.getSharePreInt(this, APPConstant.SP_NAME, APPConstant.SP_SHARE_FIRSTIN);
		if (result==0) {  //第一次进入
			tvRight.setText("我要参加");
		}else {
			tvRight.setVisibility(View.INVISIBLE);
			
		}
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
//		webView.setDownloadListener(new MyWebViewDownLoadListener());
		mWebView.loadUrl(url);
		
	}
	@Override
	public void rightTextOnClick(View v) {
		JUtil.putSharePre(ShareInstructionActivity.this, APPConstant.SP_NAME, APPConstant.SP_SHARE_FIRSTIN, 1);
		Intent intent = new Intent(ShareInstructionActivity.this,ShareAwardActivity.class);
//		Intent intent = new Intent(ShareInstructionActivity.this,ShareAwardActivityback.class);
		
		startActivity(intent);
		rightInAnimation();
		
		ShareInstructionActivity.this.finish();
	}
	
}
