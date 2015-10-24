package com.kinth.mmspeed.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kinth.mmspeed.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 正在加载的界面
 * @author BJ
 * @version 1.8
 */
public class LoadingFragment extends BaseFragment {
	@ViewInject(R.id.order_package_button)
	private Button orderButton;
	
	private Context mContext;
	public static Fragment newInstance() {
		LoadingFragment ft = new LoadingFragment();
		return ft;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		ViewUtils.inject(this,view);
		return view;
	}
}
