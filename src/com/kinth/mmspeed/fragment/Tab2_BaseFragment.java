package com.kinth.mmspeed.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.MyFragmentPagerAdapter;
import com.kinth.mmspeed.map.FlowMapFragment;
import com.kinth.mmspeed.ui.CustomViewPager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Tab2_BaseFragment extends BaseFragment {
	static final int NUM_ITEMS = 2;

	@ViewInject(R.id.tab2_base_radio_group)
	private RadioGroup radioGroup;

	@ViewInject(R.id.tab2_base_radio_button1)
	private RadioButton radioButton1;// 网络测速

	@ViewInject(R.id.tab2_base_radio_button2)
	private RadioButton radioButton2;// 测速地图

	@ViewInject(R.id.tab2_base_container)
	private CustomViewPager mPager;

	private Context mContext;
	private MyFragmentPagerAdapter mAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab2_base_fragment, container,
				false);
		ViewUtils.inject(this, view);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(new Tab2_Fragment());
		fragments.add(new FlowMapFragment());
		mAdapter = new MyFragmentPagerAdapter(getFragmentManager(), fragments);
		mPager.setAdapter(mAdapter);
		mPager.setScrollable(false);
		mPager.setOffscreenPageLimit(0);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab2_base_radio_button1:
					mPager.setCurrentItem(0,false);
					break;
				case R.id.tab2_base_radio_button2:
					mPager.setOffscreenPageLimit(1);
					mPager.setCurrentItem(1,false);
					break;
				}
			}
		});
		return view;
	}
}
