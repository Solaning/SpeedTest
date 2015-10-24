package com.kinth.mmspeed;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.AdArticleInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.CommonFunction;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.Md5Util;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 广告启动界面
 * @author admin
 */
@ContentView(R.layout.activity_adstart)
public class AdStartImageActivity extends BaseActivity {
	
	@ViewInject(R.id.iv_ad)
	private ImageView mImageView;
	
	private Context mContext;
	private DbUtils mDbUtils;
	private AdArticleInfo adArticleInfo;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private UserPhoneDBService userPhoneDBService;
	private boolean ifClickAd =false; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		
		mDbUtils = DbUtils.create(mContext, APPConstant.DATABASE_NAME);
		initDisplayOptions();
		userPhoneDBService = new UserPhoneDBService(mContext);
		
		try {
			adArticleInfo = mDbUtils.findFirst(AdArticleInfo.class);
		} catch (DbException e1) {
			e1.printStackTrace();
			adArticleInfo = null;
		}
		if (adArticleInfo==null) {   //为空
			
		}else {
			String pathString = JUtil.getSDPath()+"/LiuLiangYi/Data/Ad/"+Md5Util.md5s(adArticleInfo.getTitleURLString())+"._jpg";
			
			File file = new File(pathString);
			if (file.exists()) {
				mImageLoader.displayImage("file:///"+pathString, mImageView, options, animateFirstListener);
				mImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ifClickAd = true;
						Intent intent = new Intent(mContext,CommonWebViewActivity.class);
						String clientHandle = adArticleInfo.getClientHandle();
						String clickUrl = adArticleInfo.getClickURL();
						if (clientHandle.equals("4GAD")) {
							String phone = MainAccountUtil.getAvailablePhone(mContext);
							if (!TextUtils.isEmpty(phone)) {
								intent.putExtra(CommonWebViewActivity.INTENT_URL, clickUrl+"&mobile="+phone);
							}else {
								intent.putExtra(CommonWebViewActivity.INTENT_URL, clickUrl+"");
							}
						}else {
							if(clickUrl.contains(APPConstant.URL_WITH_MOBILE_GMCC_OPEN) || clickUrl.contains(APPConstant.URL_WITH_MOBILE_GDMOBILE)){
								String phone = MainAccountUtil.getAvailablePhone(mContext);
								if(TextUtils.isEmpty(phone)){
									intent.putExtra(CommonWebViewActivity.INTENT_URL, clickUrl+"");
								}else{
									intent.putExtra(CommonWebViewActivity.INTENT_URL, clickUrl+"&mobile="+phone);
								}
							}else {
								intent.putExtra(CommonWebViewActivity.INTENT_URL, clickUrl+"");
							}
						}
						
						intent.putExtra(CommonWebViewActivity.INTENT_TITLE, adArticleInfo.getArticleName());
						intent.putExtra(CommonWebViewActivity.INTENT_TARGET_CLASS_NAME, SpeedMainActivity.class.getName());
						//数据统计
						AsyncNetworkManager asyncNetworkManager= new AsyncNetworkManager();
						asyncNetworkManager.statisticsClick(AdStartImageActivity.this, "clickLoginAD", adArticleInfo.getId()+"", new AsyncNetworkManager.StatisticsClickImp() {
							@Override
							public void StatisticsClickImpCallBack(int rtn) {
								
							}
						});
						startActivity(intent);
						rightInAnimation();
						AdStartImageActivity.this.finish();
					}
				});
			}
		}
		
		if(!ApplicationController.getInstance().isHasActiveOrLogin()){//判断是否激活or登陆
			new CommonFunction().activateHandle(null);
		}
		new Handler().postDelayed(new Runnable() {
            public void run() {
            	if (!ifClickAd) {
            		 Intent mainIntent = new Intent(AdStartImageActivity.this,  
                             SpeedMainActivity.class);  
                     AdStartImageActivity.this.startActivity(mainIntent);  
                     AdStartImageActivity.this.finish();  
                     fadeInAnimation();
				}
            }
        }, 4700);  //4.7s的广告
	}
	
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		
//		.showStubImage(R.drawable.default_icon)	//设置正在加载图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//		.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
//		.showImageForEmptyUri(R.drawable.default_icon)	
//		.showImageOnFail(R.drawable.default_icon)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.displayer(new RoundedBitmapDisplayer(0))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
		
		//缓存目录
		//有SD卡 path=/sdcard/Android/data/com.example.universalimageloadertest/cache
		//无SD卡 path=/data/data/com.example.universalimageloadertest/cache
//		File cacheDir = StorageUtils.getCacheDirectory(UserPhotoDetailActivity.this);
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 300);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mImageLoader != null) {
			mImageLoader.clearMemoryCache();
			mImageLoader.clearDiskCache();
		}
	}
}
