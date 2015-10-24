package com.kinth.mmspeed.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.ShareBean;
import com.kinth.mmspeed.util.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 分享popup adapter
 */
public class ShareGridAdapter extends BaseAdapter {
	private List<ShareBean> shareBeans = new ArrayList<ShareBean>();
	private Context context;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	public ShareGridAdapter(Context context, List<ShareBean> shareBeans) {
		super();
		this.context = context;
		this.shareBeans = shareBeans;
		inflater = LayoutInflater.from(context);
		initDisplayOptions();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return shareBeans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = inflater.inflate(
					R.layout.item_share, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.iv_item);
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.tv_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		mImageLoader.displayImage("drawable://"
				+ shareBeans.get(position).getShareRes(),
				viewHolder.iconImageView, options, animateFirstListener);
		viewHolder.tvName.setText(shareBeans.get(position).getName());
		return convertView;
	}

	private final class ViewHolder {
		ImageView iconImageView;
		TextView tvName;
	}

	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.user_photo) //设置正在加载图片
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				// .showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
				// .showImageForEmptyUri(R.drawable.user_photo)
				// .showImageOnFail(R.drawable.user_photo) //设置加载失败图片
				.cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(0)) // 设置图片角度,0为方形，360为圆角
				.build();

		mImageLoader = ImageLoader.getInstance();
		// 缓存目录
		// 有SD卡
		// path=/sdcard/Android/data/com.example.universalimageloadertest/cache
		// 无SD卡 path=/data/data/com.example.universalimageloadertest/cache
		File cacheDir = StorageUtils.getCacheDirectory(context);
		// Log.i("LatestChatAdapter",
		// "cacheDir path="+cacheDir.getAbsolutePath());
	}
}
