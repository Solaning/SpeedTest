package com.kinth.mmspeed.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class TabHorizontalScrollView extends HorizontalScrollView {
	private View view;
	private ImageView leftImage;
	private ImageView rightImage;
	private int windowWitdh = 0;
	private Activity mContext;

	public TabHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TabHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TabHorizontalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setParams(View view, ImageView leftImage,
			ImageView rightImage, Activity context){
		this.mContext = context;
		this.view = view;
		this.leftImage = leftImage;
		this.rightImage = rightImage;
		DisplayMetrics dm = new DisplayMetrics();
		this.mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
		windowWitdh = dm.widthPixels;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		Log.i("MainActivity", "view width:"+view.getWidth()+"window width:"+windowWitdh+",l:"+l+",oldl:"+oldl);
		
		if(!mContext.isFinishing() && view != null 
				&& leftImage != null && rightImage != null){
			
			if(view.getWidth() < windowWitdh){
				leftImage.setVisibility(View.GONE);
				rightImage.setVisibility(View.GONE);
			}else{
				if(l == 0){
					leftImage.setVisibility(View.GONE);
					rightImage.setVisibility(View.VISIBLE);
				}else if(view.getWidth() - l == windowWitdh){
					leftImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.GONE);
				}else{
					leftImage.setVisibility(View.VISIBLE);
					rightImage.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
