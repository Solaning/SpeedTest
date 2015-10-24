package com.kinth.mmspeed.bean;

import android.text.SpannableString;

/**
 * 保存显示瞬时速度的相关数据
 * 
 * @author Sola
 * 
 */
public class InstancySpeedInfo {
	private SpannableString speed;
	private float angle;

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public SpannableString getSpeed() {
		return speed;
	}

	public void setSpeed(SpannableString speed) {
		this.speed = speed;
	}

}
