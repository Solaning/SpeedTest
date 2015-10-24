package com.kinth.mmspeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_flow_favorable_detail)
public class FlowFavorableDetailActivity extends BaseActivity {
	@ViewInject(R.id.wv_detail)
	private WebView webView;
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	@ViewInject(R.id.nav_right)
	private ImageButton btnNavRight;
	private String url;
	public static final String INTENT_URL = "INTENT_URL";
	public static final String INTENT_TITTLE = "INTENT_TITTLE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		url = getIntent().getStringExtra(INTENT_URL);
		String tittleStr = getIntent().getStringExtra(INTENT_TITTLE);
		tvTittle.setText(tittleStr+"");
		btnNavRight.setVisibility(View.INVISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
		webView.addJavascriptInterface(new Object() {
			@JavascriptInterface
			public void sendSMS(String test) {
				Message msg = mHandler.obtainMessage(0,test);
				mHandler.sendMessage(msg);
			}
		}, "bridge");
		webView.loadUrl(url);
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what == 0){
			    Uri uri = Uri.parse("smsto:10086");            
			    Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
			    it.putExtra("sms_body", (String)msg.obj);
			    startActivity(it);  
			}
			return false;
		}
	});

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();// 返回前一个页面
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			FlowFavorableDetailActivity.this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
