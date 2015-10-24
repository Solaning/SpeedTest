package com.kinth.mmspeed.adapter;

import java.io.File;
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
import com.kinth.mmspeed.bean.BillboardFlowItem;
import com.kinth.mmspeed.util.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 4G排行榜
 * @author admin
 */
public class BillboardFlowAdapter extends BaseAdapter{
	private  LayoutInflater mInflater;
	private Context context;
	private List<BillboardFlowItem> billboardFlows;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public BillboardFlowAdapter(Context context,List<BillboardFlowItem> billboardFlows) {
		super();
		this.billboardFlows = billboardFlows;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		 initDisplayOptions();
	}
	
	@Override
	public int getCount() {
		return billboardFlows.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_billboard_flow,
					parent, false);
			holder = new ViewHolder();
			holder.tvMobile = (TextView) convertView.findViewById(R.id.tv_item_phone);
			holder.tvFlow = (TextView) convertView.findViewById(R.id.tv_flow);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (billboardFlows.get(position)!=null) {
			mImageLoader.displayImage(billboardFlows.get(position).getIconUrl(), holder.ivIcon, options, animateFirstListener);
			holder.tvMobile.setText(billboardFlows.get(position).getMobile().replace(" ", "")+"");
			holder.tvFlow.setText(billboardFlows.get(position).getUseflow()/1024+"Mb");
//			holder.tvName.setText(billboardFlows.get(position).getNickName()+"");
			if (billboardFlows.get(position).getNickName()==null||billboardFlows.get(position).getNickName().equals("")) {
				holder.tvName.setText(billboardFlows.get(position).getMobile().replace(" ", "")+"");
				holder.tvMobile.setVisibility(View.INVISIBLE);
			}else {
				holder.tvName.setText(billboardFlows.get(position).getNickName()+"");
				holder.tvMobile.setVisibility(View.VISIBLE);
			}
		}
		
		return convertView;
	}
	public final class ViewHolder {
		public TextView tvMobile; //
		public TextView tvName; //
		public TextView tvFlow; //
		public ImageView ivIcon; //
	}
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		
//		.showStubImage(R.drawable.user_photo)	//设置正在加载图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//		.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
//		.showImageForEmptyUri(R.drawable.user_photo)	
//		.showImageOnFail(R.drawable.user_photo)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisc(true)
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
