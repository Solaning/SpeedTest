package com.kinth.mmspeed.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageFactoryUtil {
	private static final String TAG = "ImageFactoryUtil";

	public static Bitmap getDecodeBitmap(Context context, String fileName) { //从本地加载图片
		if (fileName == null || fileName.equals("")) {
			return null;
		}
		InputStream inStream = null;
		Bitmap image = null;
		try{
			inStream = context.openFileInput(fileName);
			image=BitmapFactory.decodeStream(inStream);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			image = null;
		}
		return image;
	}
		
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap getDecodeBitmap(Context context, int id) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(id);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	/**
	 * 加载图片，缩小多少倍
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap getDecodeBitmapCompress(Context context, int id, int time) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize = time;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(id);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static Drawable bitmapToDrawable(Bitmap bmp) {
		if (bmp == null) {
			return null;
		}
		BitmapDrawable bd = new BitmapDrawable(bmp);
		return bd;
	}
}
