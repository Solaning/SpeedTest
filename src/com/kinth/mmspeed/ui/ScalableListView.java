package com.kinth.mmspeed.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ListView;

/**
 * 有最大高度的LinearLayout，小于最大高度时包裹内容
 * 
 * @author Sola
 * 
 */
public class ScalableListView extends ListView {
	private Context mContext;
	private int maxHeight;

	/**
	 * @param context
	 * @param attrs
	 */
	public ScalableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	/**
	 * @param context
	 */
	public ScalableListView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	private void init(){
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		maxHeight = (int) (metrics.heightPixels * 0.4);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode == MeasureSpec.UNSPECIFIED) {
			return;
		}
		int height = getMeasuredHeight();
		int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
		if (height > maxHeight) {
			setMeasuredDimension(specWidthSize, maxHeight);
		} else {
			setMeasuredDimension(specWidthSize, height);
		}
	}
}
