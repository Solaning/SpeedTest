package com.kinth.mmspeed.friend;

import java.io.File;
import java.util.ArrayList;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.util.Md5Util;
import com.kinth.mmspeed.util.UtilFunc;
import com.kinth.mmspeed.util.ViewHolder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 每条动态中显示的图片
 * 
 * @author Sola
 * 
 */
public class ActiveGalleryAdapter extends BaseAdapter {
	private DisplayImageOptions options;
	private Context mContext;
	private ArrayList<String> path;
	private int type;
	private final int PHOTOS = 0;// 不止一张图片
	private final int ONE_PHOTO = 1;// 只有一张图片

	public ActiveGalleryAdapter(Context mContext, ArrayList<String> path) {
		super();
		this.mContext = mContext;
		this.path = path;
		if (path.size() == 1) {
			type = ONE_PHOTO;
		} else {
			type = PHOTOS;
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.image_download_loading_icon)
				.showImageForEmptyUri(R.drawable.image_download_loading_icon)
				.showImageOnFail(R.drawable.image_download_fail_icon)
				.cacheInMemory(true).cacheOnDisk(true).build();
	}

	@Override
	public int getCount() {
		return path.size();
	}

	@Override
	public ArrayList<String> getItem(int arg0) {
		return path;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			switch (type) {
			case ONE_PHOTO:
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.moments_active_gallery_one_image_item, parent,
						false);
				break;
			case PHOTOS:
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.moments_active_gallery_image_item, parent,
						false);
				break;
			}
		}

		final String md5Path = APPConstant.IMAGE_PERSISTENT_CACHE
				+ File.separator + Md5Util.md5s(path.get(position));// 取md5后的图片路径
		File imageFile = new File(md5Path);
		switch (type) {
		case PHOTOS:
			// 图片
			final ImageView activeImg = ViewHolder.get(convertView,
					R.id.iv_active_gallery_image);
			if (imageFile.exists()) {// 存在
				// 加载图片
				ImageLoader.getInstance().displayImage("file:///" + md5Path,
						activeImg, options, null);
			} else {// 本地不存在，去下载，然后显示
				HttpUtils http = new HttpUtils();
				HttpHandler<File> handler = http.download(path.get(position),
						md5Path, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
						false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
						new RequestCallBack<File>() {

							@Override
							public void onStart() {
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {
								ImageLoader.getInstance().displayImage(
										"file:///" + md5Path, activeImg,
										options, null);
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								ImageLoader.getInstance().displayImage(
										path.get(position), activeImg, options,
										null);
							}
						});
			}
			break;
		case ONE_PHOTO:// 单张图片
			final ImageView activeOneImg = ViewHolder.get(convertView,
					R.id.iv_active_gallery_one_image);
			if (imageFile.exists()) {// 存在
				loadAloneImage(md5Path,activeOneImg);
			} else {// 本地不存在，去下载，然后显示
				HttpUtils http = new HttpUtils();
				HttpHandler<File> handler = http.download(path.get(position),
						md5Path, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
						false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
						new RequestCallBack<File>() {

							@Override
							public void onStart() {
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {
								loadAloneImage(md5Path,activeOneImg);
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								ImageLoader.getInstance().displayImage(
										path.get(position), activeOneImg,
										options, null);
							}
						});
			}
			break;
		}
		return convertView;
	}
	
	private void loadAloneImage(String md5Path,ImageView activeOneImg){
		// 加载图片，根据图片的尺寸来显示
		BitmapFactory.Options Options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        Options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(md5Path,Options);//此时返回bm为空  
        Options.inJustDecodeBounds = false;
        int realWidth = Options.outWidth;//图片原始的宽
        int realHeight = Options.outHeight;//图片原始的高
        
        int maxWidthPx = UtilFunc.dip2px(mContext, 360);
        int maxHeightPx = UtilFunc.dip2px(mContext, 180);//180dp的高，只限制最大高度，宽度根据比例来缩放
        
        if(realHeight <= maxHeightPx){//图片的像素高度没有超过最大高度限制，比较高度和宽度
        	if(realWidth <= maxWidthPx){//宽度没有超过，按真实宽度来显示
        		activeOneImg.setLayoutParams(new LayoutParams(realWidth,realHeight));//动态设置宽高
        	}else{//如果宽度超过最大限制，整体缩小，宽按
        		int h = maxWidthPx * realHeight / realWidth;
        		activeOneImg.setLayoutParams(new LayoutParams(maxWidthPx,h));
        	}
        } else {//像素高度超过最大高度限制，
        	if(realWidth <= maxWidthPx){//只有高度超了，宽度没有超
        		int w = maxHeightPx * realWidth / realHeight;
        		activeOneImg.setLayoutParams(new LayoutParams(w,maxHeightPx));
        	}else{//宽度和高度都超了
        		float widthRate = realWidth * 1.0f / maxWidthPx;
        		float heightRate = realHeight * 1.0f / maxHeightPx;
        		if(widthRate > heightRate){//宽度超出多，以宽为基准缩放，求高
        			int h = maxWidthPx * realHeight / realWidth;
        			activeOneImg.setLayoutParams(new LayoutParams(maxWidthPx , h));
        		}else{
        			int w = maxHeightPx * realWidth / realHeight;
        			activeOneImg.setLayoutParams(new LayoutParams(w , maxHeightPx));
        		}
        	}
        }
		ImageLoader.getInstance().displayImage("file:///" + md5Path,
				activeOneImg, options, null);
	}
}
