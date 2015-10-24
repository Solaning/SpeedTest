package com.kinth.mmspeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * rmg的介绍
 * @author Sola
 *
 */
@ContentView(R.layout.activity_rmg_flow_presentation)
public class RMGFlowPresentationActivity extends BaseActivity {
	public static final String INTENT_SERVICE_ID = "INTENT_SERVICE_ID";
	public static final String INTENT_SHOW_NEXT = "INTENT_SHOW_NEXT";//是否隐藏右侧下一步按钮
	private Context mContext;
	private ServiceID serviceId;// enum类型

	@ViewInject(R.id.iv_back)
	private ImageView back;

	@ViewInject(R.id.tv_title)
	private TextView title;

	@ViewInject(R.id.tv_next)
	private TextView next;

	@ViewInject(R.id.WebViewProgress)
	private ProgressBar mProgressBar;

	@ViewInject(R.id.webview)
	private WebView webView;

	@OnClick(R.id.iv_back)
	public void fun_1(View v) {
		finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.tv_next)
	public void fun_2(View v) {// 跳过
		switch (serviceId) {
		case DO_RICH_MAN_TOGETHER:
			SingletonSharedPreferences.getInstance()
					.setShowRMGFlowPresentation(true);// 写入sp
			Intent intent = new Intent(mContext, RMGCommonUserPhonesActivity.class);
			intent.putExtra(RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
					APPConstant.ServiceID.DO_RICH_MAN_TOGETHER);
			startActivity(intent);
			finish();
			rightInAnimation();
			break;
		case I_AM_RICH_MAN:
			SingletonSharedPreferences.getInstance()
					.setShowRMGDonationPresentation(true);// 写入sp
			Intent intent2 = new Intent(mContext, RMGCommonUserPhonesActivity.class);
			intent2.putExtra(RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
					APPConstant.ServiceID.I_AM_RICH_MAN);
			startActivity(intent2);
			finish();
			rightInAnimation();
			break;
		case NOVALUE:
		default:
			break;
		}
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		serviceId = (ServiceID) getIntent().getSerializableExtra(
				INTENT_SERVICE_ID);
		boolean showNext = getIntent().getBooleanExtra(INTENT_SHOW_NEXT, false);

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
		switch (serviceId) {
		case DO_RICH_MAN_TOGETHER:
			title.setText("流量共享套餐详情");
			webView.loadUrl("file:///android_asset/page_share.html");
			break;
		case I_AM_RICH_MAN:
			title.setText("流量转赠套餐详情");
			webView.loadUrl("file:///android_asset/page_donation.html");
			break;
		case NOVALUE:
		default:
			break;
		}
		
		if(showNext){
			next.setVisibility(View.VISIBLE);
		}else{
			next.setVisibility(View.GONE);
		}
		
	}
}
