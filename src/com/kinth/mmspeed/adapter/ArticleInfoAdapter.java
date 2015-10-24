package com.kinth.mmspeed.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.ArticleInfo;
import com.kinth.mmspeed.util.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
/**
 * 商业资讯，也就是新闻列表的适配器
 * @author admin
 *
 */
public class ArticleInfoAdapter extends BaseAdapter{
	private List<ArticleInfo>articleInfos = new ArrayList<ArticleInfo>();
	  private Context context;
	    LayoutInflater inflater;
	    private DisplayImageOptions options;
	    private ImageLoader mImageLoader;
	    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	public ArticleInfoAdapter(Context context, List<ArticleInfo>articleInfos) {
		super();
		this.context = context;
		this.articleInfos = articleInfos;
		inflater = LayoutInflater.from(context);
		 initDisplayOptions();
	}
	
	@Override
	public int getCount() {
		return articleInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_item_news, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.articleNameTv = (TextView) convertView
					.findViewById(R.id.tv_news_item_tittle);
			
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.iv_news_item_icon);
			
			viewHolder.createTimeTv = (TextView) convertView
					.findViewById(R.id.tv_news_create_time);
			viewHolder.shortContentTv = (TextView) convertView
					.findViewById(R.id.tv_news_item_content);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		mImageLoader.displayImage(articleInfos.get(position).getTitleURLString(), viewHolder.iconImageView, options, animateFirstListener);
		viewHolder.articleNameTv.setText(articleInfos.get(position).getArticleName());
		viewHolder.createTimeTv.setText(articleInfos.get(position).getCreateTime());
		viewHolder.shortContentTv.setText(articleInfos.get(position).getShortContent());
		return convertView;
	}
	private final class ViewHolder {
		ImageView iconImageView;
		TextView articleNameTv;
		TextView createTimeTv;
		TextView shortContentTv;
	}
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		
//		.showStubImage(R.drawable.user_photo)	//设置正在加载图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//		.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
//		.showImageForEmptyUri(R.drawable.user_photo)	
//		.showImageOnFail(R.drawable.user_photo)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.displayer(new RoundedBitmapDisplayer(15))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
		//缓存目录
		//有SD卡 path=/sdcard/Android/data/com.example.universalimageloadertest/cache
		//无SD卡 path=/data/data/com.example.universalimageloadertest/cache
		File cacheDir = StorageUtils.getCacheDirectory(context);
		Log.i("LatestChatAdapter", "cacheDir path="+cacheDir.getAbsolutePath());
	}
}
