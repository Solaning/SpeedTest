package com.kinth.mmspeed.test;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.util.ApplicationController;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 测速javascript
 * @author Sola
 *
 */
@ContentView(R.layout.activity_about_us)
public class TestHK extends FragmentActivity {
	@ViewInject(R.id.iv_about_us_back)
	private ImageView back;

	@ViewInject(R.id.activity_about_us_webview)
	private WebView webView;

	@OnClick(R.id.iv_about_us_back)
	public void fun_1(View v) {
		finish();
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle arg0) {
		ViewUtils.inject(this);
		super.onCreate(arg0);
		
		if(loadClockBitmap(ApplicationController.getInstance())==null){
			Toast.makeText(this, "bitmap == null", Toast.LENGTH_LONG).show();
		}
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
//		webView.addJavascriptInterface(new Object() {
//			@JavascriptInterface
//			public void sendSMS(String test) {
//				Log.e("test",""+test);
////				sendSMS("15920194054","测速");
//				Uri uri = Uri.parse("smsto:10086");            
//			    Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
//			    it.putExtra("sms_body", test);
//			    startActivity(it); 
//				mHandler.sendEmptyMessage(0);
//			}
//		}, "bridge");
		webView.loadUrl("file:///android_asset/demo.html");
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			Toast.makeText(TestHK.this, "fuckkk", Toast.LENGTH_LONG).show();
			return false;
		}
	});
	
	private Bitmap loadClockBitmap(Context context) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = null;
		is= context.getResources().openRawResource(
				R.drawable.ic_launcher);//公章图片名称
		return	BitmapFactory.decodeStream(is, null, opt);
	}
}
