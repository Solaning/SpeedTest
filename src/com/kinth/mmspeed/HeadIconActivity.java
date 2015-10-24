package com.kinth.mmspeed;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HeadIconActivity extends BaseActivity {
	@ViewInject(R.id.iv_icon)
	private ImageView ivIcon;
	private String url = "";
	private BitmapUtils mBitmapUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		  WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.big_head);
		ViewUtils.inject(this);
		
		url = getIntent().getStringExtra("url");
		mBitmapUtils = new BitmapUtils(this);
		// 加载assets中的图片(路径以assets开头)
		if (url!=null&&!url.equals("")) {
			mBitmapUtils = new BitmapUtils(this);
			mBitmapUtils.display(ivIcon,url);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBitmapUtils.clearCache();
	}
	
	
}
