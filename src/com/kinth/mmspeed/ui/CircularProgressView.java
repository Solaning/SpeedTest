package com.kinth.mmspeed.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressView extends View {

	private CircularProgressDrawable mDrawable;

	public CircularProgressView(Context context) {
		this(context, null);
	}

	public CircularProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircularProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mDrawable = new CircularProgressDrawable(Color.WHITE, 5);
		mDrawable.setCallback(this);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if(mDrawable == null){
			return;
		}
		if (visibility == VISIBLE) {
			mDrawable.start();
		} else {
			mDrawable.stop();
		}
	}
	
	public void startProgress(){
		if (getVisibility() == VISIBLE) {
			mDrawable.start();
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mDrawable.setBounds(0, 0, w, h);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		mDrawable.draw(canvas);
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return who == mDrawable || super.verifyDrawable(who);
	}
}
