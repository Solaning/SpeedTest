package com.kinth.mmspeed.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * 朋友圈发动态时的自定义checkbox，屏蔽OnCheckedChangeListener，添加OnMyCheckedChangeListener
 * @author Sola
 * @2014-09-10
 * 
 */
public class CheckableImageView extends CheckBox{
	private boolean mBroadcasting;
	private OnMyCheckedChangeListener mOnMyCheckedChangeListener;
	
	public CheckableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CheckableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CheckableImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
//		if (mBroadcasting) {
//            return;
//        }
//        mBroadcasting = true;
//        if (mOnMyCheckedChangeListener != null) {
//        	mOnMyCheckedChangeListener.onMyCheckedChanged(this, checked);
//        }
//        mBroadcasting = false;
	}

	void setMyChecked(boolean checked,boolean isCallback){
		setChecked(checked);
	}
	
    public static interface OnMyCheckedChangeListener {
        /**
         * 只
         */
        void onMyCheckedChanged(CompoundButton buttonView, boolean isChecked);
    }
    
    public void setOnMyCheckedChangeListener(OnMyCheckedChangeListener listener) {
        mOnMyCheckedChangeListener = listener;
    }
}
