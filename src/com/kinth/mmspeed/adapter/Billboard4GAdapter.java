package com.kinth.mmspeed.adapter;

import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.activity.billboard.Recomand4GAty;
import com.kinth.mmspeed.activity.billboard.UserFriendState;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.util.AnimateFirstDisplayListener;
import com.kinth.mmspeed.util.AnimationUtil;
import com.kinth.mmspeed.util.JUtil;
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
public class Billboard4GAdapter extends BaseAdapter{
	private  LayoutInflater mInflater;
	private Context context;
	private List<ContractInfo> contractInfos;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private String userPhone;
	public Billboard4GAdapter(Context context,List<ContractInfo> contractInfos,String userPhone) {
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
    		ViewHolder holder1;
    		if (convertView == null) {
    			convertView = mInflater.inflate(R.layout.item_billboard4g_4g,
    					parent, false);
    			holder1 = new ViewHolder();
    			holder1.tvPhone = (TextView) convertView.findViewById(R.id.tv_item_phone);
    			holder1.tvName = (TextView) convertView.findViewById(R.id.tv_item_name);
    			holder1.tvLET = (TextView) convertView.findViewById(R.id.tv_let);
    			holder1.tvRecomand = (TextView) convertView.findViewById(R.id.tv_recomand);
    			
    			holder1.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
    			holder1.btnRecomand = (Button) convertView.findViewById(R.id.btn_recomand);
    			
    			convertView.setTag(holder1);
    		} else {
    			holder1 = (ViewHolder) convertView.getTag();
    		}
    		
    		mImageLoader.displayImage(contractInfos.get(position).getIconURL(), holder1.ivIcon, options, animateFirstListener);
    		
    		
//    		if (contractInfos.get(position).getContractName()==null||contractInfos.get(position).getContractName().equals("")) {
//    			holder1.tvName.setText(contractInfos.get(position).getMobile().replace(" ", "")+"");
//    			holder1.tvPhone.setText("");
//    			holder1.tvPhone.setVisibility(View.INVISIBLE);
//			}else {
//				holder1.tvPhone.setVisibility(View.VISIBLE);
//				holder1.tvName.setText(contractInfos.get(position).getContractName()+"");
//				holder1.tvPhone.setText(contractInfos.get(position).getMobile().replace(" ", "")+"");
//			}
    		if (contractInfos.get(position).getNickName()==null||contractInfos.get(position).getNickName().equals("")) {
    			if (contractInfos.get(position).getContractName()!=null&&!contractInfos.get(position).getContractName().equals("")) {
    				holder1.tvName.setText(contractInfos.get(position).getContractName()+"");
	    			holder1.tvPhone.setText(contractInfos.get(position).getMobile());
	    			holder1.tvPhone.setVisibility(View.VISIBLE);
				}else {
					holder1.tvName.setText(contractInfos.get(position).getMobile().replace(" ", "")+"");
	    			holder1.tvPhone.setText("");
	    			holder1.tvPhone.setVisibility(View.INVISIBLE);
				}
    			
			}else {
				holder1.tvPhone.setVisibility(View.VISIBLE);
				holder1.tvName.setText(contractInfos.get(position).getNickName()+"");
				holder1.tvPhone.setText(contractInfos.get(position).getMobile().replace(" ", "")+"");
			}
    		
    		int state = contractInfos.get(position).getState();
    		switch (state) {
    		case UserFriendState.HIGH_PROD:
			case UserFriendState.LET:     //4G
				holder1.tvLET.setVisibility(View.VISIBLE);
				holder1.tvRecomand.setVisibility(View.INVISIBLE);
				holder1.btnRecomand.setVisibility(View.INVISIBLE);
				break;
			case UserFriendState.RECOMAND:     //4G
				holder1.tvLET.setVisibility(View.INVISIBLE);
				holder1.tvRecomand.setVisibility(View.VISIBLE);
				holder1.btnRecomand.setVisibility(View.INVISIBLE);
				break;
			case UserFriendState.NOHANDLE:     //4G
				holder1.tvLET.setVisibility(View.INVISIBLE);
				holder1.tvRecomand.setVisibility(View.INVISIBLE);
				holder1.btnRecomand.setVisibility(View.VISIBLE);
				holder1.btnRecomand.setOnClickListener(new View.OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					Intent intent = new Intent(context,Recomand4GAty.class);
    					intent.putExtra(Recomand4GAty.INTENT_PHONE, contractInfos.get(position).getMobile()+"");
    					intent.putExtra(Recomand4GAty.INTENT_USERPHONE, userPhone+"");
    					AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
    					context.startActivity(intent);
    				}
    			});
				break;
			default:
				break;
			}
		return convertView;
	}
	public final class ViewHolder {
		public TextView tvPhone; //
		public TextView tvName; //昵称
		public TextView tvLET; //昵称
		public TextView tvRecomand; //昵称
		public ImageView ivIcon;     //头像
		public Button btnRecomand;     //头像
		
	}
//	public final class AddcontractViewHolder {
//		public ImageView btnAddContract;
//	}
//	public final class NOHandleViewHolder {
//		public TextView phoneteTextView; //
//		public TextView nameTextView; //
//		public Button btnRecomand;
//		public ImageView ivIcon;
//	}
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
