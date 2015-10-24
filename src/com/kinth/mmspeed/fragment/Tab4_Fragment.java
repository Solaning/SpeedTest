package com.kinth.mmspeed.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kinth.mmspeed.AccountManageActivity;
import com.kinth.mmspeed.GM_LoginActivity;
import com.kinth.mmspeed.GM_SelectMobileAsMainAccountActivity;
import com.kinth.mmspeed.MyFamilyActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.SettingActivity;
import com.kinth.mmspeed.UserPhonesActivity;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.activity.friendmanager.FriendManagerAty;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.friend.MomentsActivity;
import com.kinth.mmspeed.hk.SingletonData;
import com.kinth.mmspeed.util.AnimationUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我
 */
public class Tab4_Fragment extends Fragment {
	private Context mContext;
	private UserPhoneDBService userPhoneDBService;
	private List<UserPhone> userPhones;
	private DbUtils userLocalFirendsDbUtils;
	
	@ViewInject(R.id.tv_title)
	private TextView title;
	
	// -------------------onClick-------------
	@OnClick(R.id.llt_myactive)
	public void fun_2(View v){//我的动态
		UserAccount user = MainAccountUtil.getCurrentAccount(mContext);
		userPhones = userPhoneDBService.getAllUserPhones();
		Intent intent;
		if (user == null) {
			if (userPhones!=null && userPhones.size()>0) {
				intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, MomentsActivity.class.getName());//传递需要跳转的类名
				bundle.putBoolean(MomentsActivity.IS_GET_My_ACTIVE, true);
				intent.putExtras(bundle);
			}else {
				intent = new Intent(mContext, GM_LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, MomentsActivity.class.getName());//传递需要跳转的类名
				bundle.putBoolean(MomentsActivity.IS_GET_My_ACTIVE, true);
				intent.putExtras(bundle);
			}
		}else {
			intent = new Intent(mContext, MomentsActivity.class);
			intent.putExtra(MomentsActivity.IS_GET_My_ACTIVE, true);
		}
		startActivity(intent);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	//我的账号
	@OnClick(R.id.llt_myacount)
	public void fun_3(View v) {
		userPhones = userPhoneDBService.getAllUserPhones();
		UserAccount user = MainAccountUtil.getCurrentAccount(getActivity());
		Intent intent ;
		if (user==null||user.getMobile().equals("")) { //没有主账号
			if (userPhones!=null&&userPhones.size()>0) {
				intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, AccountManageActivity.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}else {
				intent = new Intent(mContext, GM_LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, AccountManageActivity.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}
		}else{
			intent = new Intent(mContext, AccountManageActivity.class);
		}
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	// 号码管理
	@OnClick(R.id.llt_bindPhone)
	public void fun_1(View v) {
		Intent intent = new Intent(mContext, UserPhonesActivity.class);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	//我的好友
	@OnClick(R.id.llt_myfriend)
	public void fun_4(View v) {
		UserAccount user = MainAccountUtil.getCurrentAccount(getActivity());
		userPhoneDBService = new UserPhoneDBService(mContext);
		userPhones = userPhoneDBService.getAllUserPhones();
		Intent intent;
		if (user==null) {
			if (userPhones!=null && userPhones.size()>0) {
				intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, FriendManagerAty.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}else {
				intent = new Intent(mContext, GM_LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, FriendManagerAty.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}
		}else {
			intent = new Intent(mContext, FriendManagerAty.class);
		}
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	//我的家庭
	@OnClick(R.id.llt_myfamily)
	public void fun_5(View v){
		UserAccount user = MainAccountUtil.getCurrentAccount(mContext);//没有主账号要先绑定主账号
		Intent intent;
		if (user == null) {
			userPhones = userPhoneDBService.getAllUserPhones();//是否有绑定其他号码
			if (userPhones != null && userPhones.size() > 0) {
				intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, MyFamilyActivity.class.getName());//传递需要跳转的类名
				intent.putExtras(bundle);
			}else {
				intent = new Intent(mContext, GM_LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, MyFamilyActivity.class.getName());
				intent.putExtras(bundle);
			}
		}else {
			intent = new Intent(mContext, MyFamilyActivity.class);
		}
		startActivity(intent);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	//设置
	@OnClick(R.id.llt_setting)
	public void fun_6(View v) {
		Intent intent = new Intent(mContext, SettingActivity.class);
		AnimationUtil.setLayout(R.anim.in_from_right, R.anim.out_to_left);
		startActivity(intent);
	}
	
	// -----------------end OnClick------------
	static Tab4_Fragment newInstance() {
		Tab4_Fragment newFragment = new Tab4_Fragment();
		return newFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		UserAccount user = MainAccountUtil.getCurrentAccount(getActivity()); //TODO 
		if (user != null && !user.getMobile().equals("")) {
			userLocalFirendsDbUtils =  DbUtils.create(mContext, user.getMobile()+".friends");//建立数据库？？ TODO
		}
		userPhoneDBService = new UserPhoneDBService(mContext);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab4_fragment_layout, container, false);
		ViewUtils.inject(this, view);
		title.setText(getResources().getString(R.string.text_me));
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}

}
