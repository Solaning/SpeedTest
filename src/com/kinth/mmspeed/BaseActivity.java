package com.kinth.mmspeed;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;

public class BaseActivity extends Activity {
	
	public static final int HANDLE_GET_DATA_FAIL = -1;
	public static final int HANDLE_GET_DATA_SUCC = 1;
	public static final int HANDLE_GET_DATA_TIME_OUT = -2;
	public void backOnClick() {
		this.finish();
		rightOutFinishAnimation();
	}

	public void bottomInFinishAnimation() {
		overridePendingTransition(R.anim.push_bottom_in, R.anim.none_anim);
	}
	/**
	 * 向下结束的动画
	 */
	public void bottomOutFinishAnimation() {
		overridePendingTransition(R.anim.no_anim, R.anim.out_to_bottom);
	}
	/**
	 * 结束向右退出
	 */
	public void rightOutFinishAnimation() {
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	/**
	 * 右进左出
	 */
	public void rightInAnimation() {
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	/**
	 * 右进左出
	 */
	public void fadeInAnimation() {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	/**
	 * 返回键 默认，返回向右退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			rightOutFinishAnimation();
			return false;
		}
		return false;
	}
	
	/**
	 * @param 左边返回按钮事件
	 */
	public void leftBtnOnClick(View v) {
		this.finish();
		rightOutFinishAnimation();
	}
	/**
	 * @param 右边按钮事件
	 */
	public void rightBtnOnClick(View v) {

	}

	public void rightTextOnClick(View v) {
		
	}
	
	public void leftButtonOnClick(View v) {

	}

	public void leftTextOnClick(View v) {

	}

	public void setTittlebar() {

	}

}
