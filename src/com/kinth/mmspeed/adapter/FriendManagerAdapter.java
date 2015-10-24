package com.kinth.mmspeed.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.activity.billboard.UserFriendState;
import com.kinth.mmspeed.activity.friendmanager.RecomandLLyAty;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.util.AnimateFirstDisplayListener;
import com.kinth.mmspeed.util.AnimationUtil;
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
public class FriendManagerAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Context context;
	private List<ContractInfo> contractInfos;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private String userPhone;
	public FriendManagerAdapter(Context context,List<ContractInfo> contractInfos,String userPhone) {
		super();
		this.contractInfos = contractInfos;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		initDisplayOptions();
		this.userPhone = userPhone;
	}
	@Override
	public int getCount() {
		return contractInfos.size();
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
			NOHandleViewHolder holder3;
    		if (convertView == null) {
    			convertView = mInflater.inflate(R.layout.item_friend_manager_notfriend,
    					parent, false);
    			holder3 = new NOHandleViewHolder();
    			holder3.phoneteTextView = (TextView) convertView.findViewById(R.id.tv_item_phone);
    			holder3.nameTextView = (TextView) convertView.findViewById(R.id.tv_item_name);
    			holder3.tvText = (TextView) convertView.findViewById(R.id.tv_friend);
    			holder3.btnRecomand = (ImageView) convertView.findViewById(R.id.btn_recomand);
    			holder3.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
    			convertView.setTag(holder3);
    		} else {
    			holder3 = (NOHandleViewHolder) convertView.getTag();
    		}
    		mImageLoader.displayImage(contractInfos.get(position).getIconURL(), holder3.ivIcon, options, animateFirstListener);
    		holder3.phoneteTextView.setText(contractInfos.get(position).getMobile()+"");
    		
    		if (contractInfos.get(position).getContractName()==null||contractInfos.get(position).getContractName().equals("")) {
    			if (contractInfos.get(position).getNickName()!=null&&!contractInfos.get(position).getNickName().equals("")) {
    				holder3.nameTextView.setText(contractInfos.get(position).getNickName()+"");
        			holder3.phoneteTextView.setVisibility(View.VISIBLE);
    			}else {
    				holder3.nameTextView.setText(contractInfos.get(position).getMobile()+"");
        			holder3.phoneteTextView.setVisibility(View.INVISIBLE);
				}
    			
			}else {
				holder3.nameTextView.setText(contractInfos.get(position).getContractName()+"");
				holder3.phoneteTextView.setVisibility(View.VISIBLE);
			}
    		
    		if (contractInfos.get(position).getState()==UserFriendState.IS_FRIEND) {
    			holder3.btnRecomand.setVisibility(View.INVISIBLE);
    			holder3.tvText.setVisibility(View.VISIBLE);
			}else {
				holder3.tvText.setVisibility(View.INVISIBLE);
				holder3.btnRecomand.setVisibility(View.VISIBLE);
				holder3.btnRecomand.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(context,RecomandLLyAty.class);
						intent.putExtra(RecomandLLyAty.INTENT_PHONE, contractInfos.get(position).getMobile()+"");
						intent.putExtra(RecomandLLyAty.INTENT_USERPHONE, userPhone+"");
						AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
						context.startActivity(intent);
					}
				});
				
			}
    		
		
		return convertView;
	}
	public final class NOHandleViewHolder {
		public TextView phoneteTextView; //
		public TextView nameTextView; //
		public TextView tvText;
		public ImageView btnRecomand;
		public ImageView ivIcon;
	}
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		
		.showStubImage(R.drawable.default_avatar)	//设置正在加载图片
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
