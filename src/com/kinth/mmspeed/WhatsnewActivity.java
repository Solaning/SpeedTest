//package com.kinth.mmspeed;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.kinth.mmspeed.bean.UserAccount;
//import com.kinth.mmspeed.bean.UserPhone;
//import com.kinth.mmspeed.constant.APPConstant;
//import com.kinth.mmspeed.db.UserPhoneDBService;
//import com.kinth.mmspeed.hk.SingletonSharedPreferences;
//import com.kinth.mmspeed.util.Global;
//import com.kinth.mmspeed.util.JUtil;
//import com.kinth.mmspeed.util.VeDate;
//import com.lidroid.xutils.BitmapUtils;
//import com.lidroid.xutils.DbUtils;
//
//public class WhatsnewActivity extends Activity {
//	public static final String INTENT_SOURCE = "source";// 调用的Activity
//	private ViewPager mViewPager;
//	private ImageView mPage0;
//	private ImageView mPage1;
//	private ImageView mPage2;
//	private ImageView mPage3;
//	private int state = 0;// 跳转的时候判断跳转到哪个页面
//	private ImageView ivPage0;
//	private ImageView ivPage1;
//	private ImageView ivPage2;
//	private ImageView ivPage3; 
//	private BitmapUtils bitmapUtils ;
//	private DbUtils mDbUtils;
//	private List<UserPhone>mUserPhones;
//	private UserPhoneDBService userPhoneDBService;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setState();// 设置跳转的状态
//		setContentView(R.layout.whatsnew_viewpager);
//		mViewPager = (ViewPager) findViewById(R.id.whatsnew_viewpager);
//		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//		mDbUtils = DbUtils.create(this, APPConstant.DATABASE_NAME);
//		mPage0 = (ImageView) findViewById(R.id.page0);
//		mPage1 = (ImageView) findViewById(R.id.page1);
//		mPage2 = (ImageView) findViewById(R.id.page2);
//		mPage3 = (ImageView) findViewById(R.id.page3);
//		userPhoneDBService =new UserPhoneDBService(this);
//		mUserPhones = userPhoneDBService.getAllUserPhones();
//		
//		LayoutInflater mLi = LayoutInflater.from(this);
//		View view1 = mLi.inflate(R.layout.whats1, null);
//		View view2 = mLi.inflate(R.layout.whats2, null);
//		View view3 = mLi.inflate(R.layout.whats3, null);
//		View view6 = mLi.inflate(R.layout.whats6, null);
//
//		ivPage0 = (ImageView)view1.findViewById(R.id.iv_page1);
//		ivPage1 = (ImageView)view2.findViewById(R.id.iv_page2);
//		ivPage2 = (ImageView)view3.findViewById(R.id.iv_page3);
//		ivPage3 = (ImageView)view6.findViewById(R.id.iv_page6);
//		
//		bitmapUtils = new BitmapUtils(this);
//		// 加载assets中的图片(路径以assets开头)
//		bitmapUtils.display(ivPage0, "assets/v1.jpg");
//		bitmapUtils.display(ivPage1, "assets/v2.jpg");
//		bitmapUtils.display(ivPage2, "assets/v3.jpg");
//		bitmapUtils.display(ivPage3, "assets/v4.jpg");
//		
//		final ArrayList<View> views = new ArrayList<View>();
//		views.add(view1);
//		views.add(view2);
//		views.add(view3);
//		views.add(view6);
//		
//		PagerAdapter mPagerAdapter = new PagerAdapter() {
//			@Override
//			public boolean isViewFromObject(View arg0, Object arg1) {
//				return arg0 == arg1;
//			}
//			@Override
//			public int getCount() {
//				return views.size();
//			}
//			@Override
//			public void destroyItem(View container, int position, Object object) {
//				((ViewPager) container).removeView(views.get(position));
//			}
//			@Override
//			public Object instantiateItem(View container, int position) {
//				((ViewPager) container).addView(views.get(position));
//				return views.get(position);
//			}
//		};
//		mViewPager.setAdapter(mPagerAdapter);
//		//add by sola @2014-09-05 添加默认的初始化时间
//		if(TextUtils.isEmpty(SingletonSharedPreferences.getInstance().getMessageCentreUpdateTimeStamp())){
//			SingletonSharedPreferences.getInstance().setMessageCentreUpdateTimeStamp(VeDate.getStringDate());
//		}
//		if(TextUtils.isEmpty(SingletonSharedPreferences.getInstance().getMomentsUpdateTimeStamp())){
//			SingletonSharedPreferences.getInstance().setMomentsUpdateTimeStamp(VeDate.getStringDate());
//		}
//	}
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//	void setState() {
//		
//		// 检测是否有旧号码，把旧号码添加入数据库--->旧版本的
//		String phone = JUtil.getSharePreStr(this, APPConstant.SP_NAME,
//				APPConstant.USER_PHONE);// 用户号码
//		UserPhoneDBService userPhoneDBService = new UserPhoneDBService(this);
//		userPhoneDBService.createTable();
//		if (phone != null && !phone.equals("")
//				&& JUtil.isPhoneNumberValid(phone)) {// 添加到新的数据库去
//			boolean result = userPhoneDBService
//					.insertOrReplaceDetetion(new UserPhone(phone, phone, ""));// 备注为“”
//			if (result) {// 用户手机号码不存在数据库中，将该号码作为当前号码
//				state = 1;
//				JUtil.putSharePre(this, APPConstant.SP_NAME,
//						APPConstant.SP_FIELD_CURRENT_PHONE, phone);
//			} else {
//				state = 2;
//			}
//		} else {// 没有旧版本遗留的号码，
//			if (userPhoneDBService.getAllPhoneAmount() > 0) {// 数据库中已经存有号码
//				state = 1;
//			} else {
//				state = 2;
//			}
//		}
//	}
//	public class MyOnPageChangeListener implements OnPageChangeListener {
//		@Override
//		public void onPageSelected(int arg0) {
//			switch (arg0) {
//			case 0:
//				mPage0.setImageDrawable(getResources().getDrawable(
//						R.drawable.page_now));
//				mPage1.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				break;
//			case 1:
//				mPage1.setImageDrawable(getResources().getDrawable(
//						R.drawable.page_now));
//				mPage0.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				mPage2.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				break;
//			case 2:
//				mPage2.setImageDrawable(getResources().getDrawable(
//						R.drawable.page_now));
//				mPage1.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				mPage3.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				break;
//			case 3:
//				mPage3.setImageDrawable(getResources().getDrawable(
//						R.drawable.page_now));
//				mPage1.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				mPage2.setImageDrawable(getResources().getDrawable(
//						R.drawable.page));
//				break;
//			}
//		}
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//		}
//		@Override
//		public void onPageScrollStateChanged(int arg0) {
//		}
//	}
//	public void startbutton(View v) {
//		// 保存版本号
//		SingletonSharedPreferences.getInstance().setVersionCode(
//				Global.getInstance().getVersion()+"");// 保存版本号，版本升级用
//		Intent intent = null;
////		switch (state) {
////		case 1:
////			intent = new Intent(WhatsnewActivity.this, SpeedMainActivity.class);
////			startActivity(intent);
////			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
////			finish();
////			break;
////		case 0:
////		case 2:
//		if (mUserPhones==null||mUserPhones.size()==0) {
//			intent = new Intent(WhatsnewActivity.this, LoginActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			finish();
//		}else {
//			intent = new Intent(WhatsnewActivity.this, SpeedMainActivity.class);
//			startActivity(intent);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//			finish();
//		}
//		
//			
//			
////			break;
////		}
//	}
//	@Override
//	  protected void onDestroy() {
//	    super.onDestroy();
//	    if (bitmapUtils!=null) {
//	    	 bitmapUtils.clearCache();
//		}
//	  }
//}
