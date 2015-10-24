package com.kinth.mmspeed.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinth.mmspeed.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 开始测速的按钮
 */
public class PrepareFragment extends Fragment {
	
	@ViewInject(R.id.sola_start_button)
	private View startButton;//开始测速按钮
	
	@OnClick(R.id.sola_start_button)
	public void fun_1(View v){
		if (!hasClick) {
			hasClick = true;
			prepareCallbacks.onTestStartCallback();
		}
	}
	
	private TestStartCallback prepareCallbacks;// 接口
	
	public void setPrepareCallbacks(TestStartCallback prepareCallbacks) {
		this.prepareCallbacks = prepareCallbacks;
	}

	private boolean hasClick = false;// 限制只能点击一次

	public interface TestStartCallback {
		public void onTestStartCallback();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.sola_ping_fragment_layout,
				container, false);
		ViewUtils.inject(this, rootView);
		
		// BitmapFactory.Options opt = new BitmapFactory.Options();
		// opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// opt.inInputShareable = true;
		// opt.inPurgeable = true;
		// // 获取资源图片 R.drawable.sola_start_button
		// FileInputStream is = new FileInputStream(null);
		// Bitmap bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
		// opt);
		// if(bitmap == null){
		// Log.e("bitmap == null","fuckk");
		// }
		// startButton.setBackgroundDrawable(new BitmapDrawable(bitmap));
		return rootView;
	}

	@Override
	public void onDestroy() {
		hasClick = false;
		super.onDestroy();
	}

}
