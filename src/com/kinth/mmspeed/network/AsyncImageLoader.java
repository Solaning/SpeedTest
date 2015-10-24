package com.kinth.mmspeed.network;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.kinth.mmspeed.util.ImageFactoryUtil;
import com.kinth.mmspeed.util.UtilFunc;

public class AsyncImageLoader {

	private static final String TAG = "AsyncImageLoader";
	public static final int ORIGINAL_IMAGE_TYPE = 0;
	public static final int LARGE_IMAGE_TYPE = 1;
	public static final int MEDIUM_IMAGE_TYPE = 2;
	public static final int SMALL_IMAGE_TYPE = 3;
	private HashMap<String, SoftReference<Drawable>> imageCache;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

//	/**
//	 * 获取存在本地的缓存，，，
//	 */
//	public Drawable getCacheDrawable(String imageUrl){
//		if (imageCache.containsKey(imageUrl)) {
//			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
//			Drawable drawable = softReference.get();
//			if (drawable != null) {
//				return drawable;
//			}
//		}
//		return null;
//	}
	
	/**
	 * 
	 * @param imageUrl
	 * @param imageCallback
	 * @return
	 */
	public Drawable loadDrawable(final Context context, final String imageUrl,
			final String fileName, final int status,
			final ImageCallback imageCallback) {

		if (imageUrl == null || imageUrl.equals("") || fileName == null
				|| fileName.equals("")) {
			return null;
		}
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl,
						status);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = UtilFunc.bitmapToDrawable(ImageFactoryUtil.getDecodeBitmap(context, fileName));//从缓存读取图片
				if (drawable == null) {
					drawable = loadImageFromUrl(imageUrl);
					if (drawable != null) {
						// UtilFunc.logInfo(TAG, "fuck_imageUrl=" +
						// imageUrl);
						if (!UtilFunc.saveDrawableToInternalStoragePrivate(//保存图片到本地，私有
								context, fileName, drawable)) {
							 
						}
					}
				}

				if (drawable != null) {
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}
		}.start();
		return null;
	}

	public void close() {
		Set<String> keySet = imageCache.keySet();
		for (String i : keySet) {
			if (imageCache.get(i).get() != null) {
				imageCache.get(i).get().setCallback(null);
			}
			imageCache.get(i).clear();
		}
		imageCache.clear();
		System.gc();
	}

	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl,
				int status);
	}

}
