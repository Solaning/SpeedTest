package com.kinth.mmspeed.hk;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.UtilFunc;

public class SingletonBitmapClass {
	private static SingletonBitmapClass instance = new SingletonBitmapClass();
	private Context context;
	private Bitmap clockBitmap;// 测速的时钟bitmap
	private Bitmap hourBitmap;//指针的bitmap

	private SingletonBitmapClass() {
		context = ApplicationController.getInstance();
	}

	public static SingletonBitmapClass getInstance() {
		if (instance == null)
			instance = new SingletonBitmapClass();
		return instance;
	}
	
	public Bitmap getClockBitmap(boolean unitMode) {
		if (clockBitmap == null || SingletonSharedPreferences.getInstance().getUnitMode() != unitMode || clockBitmap.isRecycled()) {
			loadClockBitmap(unitMode);
		}
		return clockBitmap;
	}
	
	public Bitmap getHourBitmap(){
		if(hourBitmap == null){
			loadHourBitmap();
		}
		return hourBitmap;
	}

	private void loadClockBitmap(boolean unitMode) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = null;
		if(unitMode){
			is= context.getResources().openRawResource(
				R.drawable.gauge_bg_style1);
		}else{
			is= context.getResources().openRawResource(
					R.drawable.gauge_bg_style2);
		}
		clockBitmap = UtilFunc.ScaleBitmap(
				BitmapFactory.decodeStream(is, null, opt),
				SingletonSharedPreferences.getInstance().getScaleRate());
	}

	private void loadHourBitmap(){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(
				R.drawable.gauge_ic_arrow);
		hourBitmap = UtilFunc.ScaleBitmap(
				BitmapFactory.decodeStream(is, null, opt),
				SingletonSharedPreferences.getInstance().getScaleRate());
	}
	
	/**
	 * 资源回收利用
	 */
	public void recycle() {
		if (clockBitmap != null) {
			clockBitmap.recycle();
			clockBitmap = null;
		}
		if(hourBitmap!=null){
			hourBitmap.recycle();
			hourBitmap = null;
		}
	}
}
