package com.kinth.mmspeed.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.Checkable;

public class CheckableButton extends Button implements Checkable {
	private boolean checked;
	private static final int[] CheckedStateSet = { android.R.attr.state_checked };

	public CheckableButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CheckableButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return checked;
	}

	@Override
	public void setChecked(boolean checked) {
		this.checked = checked;
		refreshDrawableState();
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		setChecked(!checked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CheckedStateSet);
		}
		return drawableState;
	}

	@Override
	public boolean performClick() {
		toggle();
		return super.performClick();
	}

}
