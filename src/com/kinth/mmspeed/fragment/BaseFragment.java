package com.kinth.mmspeed.fragment;


import android.support.v4.app.Fragment;
import android.view.View;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.util.AnimationUtil;

public class BaseFragment extends Fragment{
	
	public void rightInLeftOutAnimation() {
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
	}
	public void bottomInAnimation() {
		AnimationUtil.setLayout(R.anim.push_bottom_in, R.anim.none_anim);
	}
	public void bottomOutAnimation() {
		AnimationUtil.setLayout(R.anim.push_bottom_out, R.anim.none_anim);
	}
	public void rightButtonOnClick(View v) {
		
	}
//	public void rightTextOnClick(View v) {
//		
//	}
	public void leftButtonOnClick(View v) {
	
	}
//	public void leftTextOnClick(View v) {
//	
//	}
	private int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
