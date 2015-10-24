package com.kinth.mmspeed;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.kinth.mmspeed.util.AnimateFirstDisplayListener;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
/**
 * 关于我们  进去的时候有滑动界面
 * @author admin
 */
public class AboutWhatsNewActivity extends BaseActivity {
	public static final String INTENT_SOURCE = "source";// 调用的Activity

	private ViewPager mViewPager;
	private ImageView mPage0;
	private ImageView mPage1;
	private ImageView mPage2;

	private String source;// 来源，判断是否AboutUs来的
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageView ivPage0;
	private ImageView ivPage1;
	private ImageView ivPage2;
	
	private BitmapUtils bitmapUtils ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whatsnew_viewpager_about);
		initDisplayOptions();
		source = getIntent().getStringExtra(INTENT_SOURCE);

		mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mPage0 = (ImageView) findViewById(R.id.page0);
		mPage1 = (ImageView) findViewById(R.id.page1);
		mPage2 = (ImageView) findViewById(R.id.page2);
//		mPage3 = (ImageView) findViewById(R.id.page3);
		// mPage4 = (ImageView)findViewById(R.id.page4);
		// mPage5 = (ImageView)findViewById(R.id.page5);
		
		LayoutInflater mLi = LayoutInflater.from(this);
		View view1 = mLi.inflate(R.layout.whats1, null);
		View view2 = mLi.inflate(R.layout.whats2, null);
		View view3 = mLi.inflate(R.layout.whats3_about, null);
		// View view4 = mLi.inflate(R.layout.whats4, null);
		// View view5 = mLi.inflate(R.layout.whats5, null);
//		View view6 = mLi.inflate(R.layout.whats6, null);

		ivPage0 = (ImageView)view1.findViewById(R.id.iv_page1);
		ivPage1 = (ImageView)view2.findViewById(R.id.iv_page2);
		ivPage2 = (ImageView)view3.findViewById(R.id.iv_page3_about);
		
//		mImageLoader.displayImage("drawable://"+R.drawable.v1, ivPage0, options, animateFirstListener);
//		mImageLoader.displayImage("drawable://"+R.drawable.v2, ivPage1, options, animateFirstListener);
//		mImageLoader.displayImage("drawable://"+R.drawable.v3, ivPage2, options, animateFirstListener);
		
		bitmapUtils = new BitmapUtils(this);
		// 加载assets中的图片(路径以assets开头)
		bitmapUtils.display(ivPage0, "assets/v1.jpg");
		bitmapUtils.display(ivPage1, "assets/v2.jpg");
		bitmapUtils.display(ivPage2, "assets/v3.jpg");
		
		final ArrayList<View> views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
//		views.add(view6);

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};

		mViewPager.setAdapter(mPagerAdapter);
	}


	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				mPage0.setImageDrawable(getResources().getDrawable(
						R.drawable.page));
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		
//		.showStubImage(R.drawable.default_icon)	//设置正在加载图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//		.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
//		.showImageForEmptyUri(R.drawable.default_icon)	
//		.showImageOnFail(R.drawable.default_icon)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(0))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
		//缓存目录
		//有SD卡 path=/sdcard/Android/data/com.example.universalimageloadertest/cache
		//无SD卡 path=/data/data/com.example.universalimageloadertest/cache
//		File cacheDir = StorageUtils.getCacheDirectory(UserPhotoDetailActivity.this);
	}
	
	public void startButton(View v) {
			Intent intent = new Intent(AboutWhatsNewActivity.this, AboutUsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			finish();
	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    if (bitmapUtils!=null) {
	    	 bitmapUtils.clearCache();
		}
	   
	    if (mImageLoader!=null) {
	      mImageLoader.clearMemoryCache();
	      mImageLoader.clearDiscCache();
	    }
	  }
}
