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
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 没有套餐的界面
 * @author admin
 */
public class QueryFlowFailFragment extends BaseFragment {
	@ViewInject(R.id.order_package_button)
	private Button orderButton;
	
	private Context mContext;
	public static Fragment newInstance() {
		QueryFlowFailFragment ft = new QueryFlowFailFragment();
		return ft;
	}
	private QueryFailOrderFlowPackage orderCallback;// 接口
	public interface QueryFailOrderFlowPackage {
		public void queryFailOderPackage();
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		try {
			orderCallback = (QueryFailOrderFlowPackage) activity;
		} catch (Exception e) {
			orderCallback = null;
			e.printStackTrace();
		}
	}
	
	@OnClick(R.id.order_package_button)
	public void orderPackageOnClick(View v){
		orderPackage();
	}
	
	/**
	 * 跳转去订购套餐页面
	 */
	private void orderPackage() {
		if (orderCallback != null)
			orderCallback.queryFailOderPackage();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_no_flow, container, false);
		ViewUtils.inject(this,view);
		return view;
	}
}
