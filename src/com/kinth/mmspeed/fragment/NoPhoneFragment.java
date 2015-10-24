package com.kinth.mmspeed.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinth.mmspeed.R;

/**
 * 没有绑定电话的界面
 * @author BJ
 */
public class NoPhoneFragment extends BaseFragment {
	
	public static Fragment newInstance() {
		NoPhoneFragment ft = new NoPhoneFragment();
		return ft;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_no_phone, container, false);
		return view;
	}
}
