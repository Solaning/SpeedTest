package com.kinth.mmspeed;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.UserPhoneHaveMainListAdapter;
import com.kinth.mmspeed.adapter.UserPhoneListAdapter;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * tab4--号码管理
 */
@ContentView(R.layout.activity_user_phones)
public class UserPhonesActivity extends BaseActivity {
	public static final String ACTION_FIRST_BIND = "ACTION_BIND";
	public static final String BIND_MOBILE = "BIND_MOBILE";
	
	@ViewInject(R.id.nav_right_image)
	private ImageView addPhones;//添加号码

	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_tittle)
	private TextView title;
	
	@ViewInject(R.id.lv_phone_list)
	private ListView lvPhones;
	
	@ViewInject(R.id.tv_tip)
	private TextView tvTips;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}
	
	@OnClick(R.id.nav_right_image)
	public void fun_2(View v) {//添加号码
		Intent intent = new Intent(UserPhonesActivity.this, BindPhoneNumActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	private Context mContext;
	private UserPhoneListAdapter userPhoneListAdapter;//没有主账号的适配器
	private UserPhoneHaveMainListAdapter userPhoneHaveMianListAdapter;//有主账号的适配器
	private List<UserPhone> userPhones;
	private UserPhoneDBService userPhoneDBService;
	private UserAccount mUser;
	private String userAcountPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mContext = this;
		addPhones.setImageResource(R.drawable.selector_icon_add);
		title.setText(getResources().getString(R.string.tittle_bind_ph));
		
		mUser = MainAccountUtil.getCurrentAccount(this);
		userPhoneDBService = new UserPhoneDBService(UserPhonesActivity.this);
		userPhoneDBService.createTable();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mUser = MainAccountUtil.getCurrentAccount(this);
		userPhones = userPhoneDBService.getAllUserPhones();
		if (userPhones==null||userPhones.size()==0) {
			tvTips.setVisibility(View.VISIBLE);
		}else {
			tvTips.setVisibility(View.INVISIBLE);
		}
		if (mUser==null) {
			userAcountPhone = "";
			userPhoneListAdapter = new UserPhoneListAdapter(UserPhonesActivity.this, userPhones,userAcountPhone);
			lvPhones.setAdapter(userPhoneListAdapter);
		}else {
			userAcountPhone = mUser.getMobile()+"";
			userPhoneHaveMianListAdapter = new UserPhoneHaveMainListAdapter(UserPhonesActivity.this, userPhones,userAcountPhone);
			lvPhones.setAdapter(userPhoneHaveMianListAdapter);
		}
		
		lvPhones.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(UserPhonesActivity.this, UserPhoneDetailActivity.class);
				intent.putExtra(UserPhoneDetailActivity.INTENT_PHONE, userPhones.get(arg2).getPhoneStr());
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		lvPhones.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final String phoneString = userPhones.get(arg2).getPhoneStr();
				if (phoneString.equals(userAcountPhone)) {
					return false;
				}
				final int position = arg2;
				AlertDialog.Builder normalDia=new AlertDialog.Builder(UserPhonesActivity.this); 
		        normalDia.setIcon(R.drawable.ic_launcher); 
		        normalDia.setTitle("操作"); 
		        normalDia.setMessage("您确定删除么");
		        
		        normalDia.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
		            @Override 
		            public void onClick(DialogInterface dialog, int which) {
		            	userPhoneDBService.deleteOnPhone(phoneString);//删除该号码信息
		            	UtilFunc.deleteCacheFile(phoneString);//删除对应于该号码的缓存文件
						
						userPhones.remove(position);
						
						if (mUser==null) {
							userPhoneListAdapter = new UserPhoneListAdapter(UserPhonesActivity.this, userPhones,userAcountPhone);
							lvPhones.setAdapter(userPhoneListAdapter);
						}else {
							userPhoneHaveMianListAdapter = new UserPhoneHaveMainListAdapter(UserPhonesActivity.this, userPhones,userAcountPhone);
							lvPhones.setAdapter(userPhoneHaveMianListAdapter);
						}
//						userPhoneListAdapter = new UserPhoneListAdapter(UserPhonesActivity.this, userPhones,userAcountPhone);//更新界面
//						lvPhones.setAdapter(userPhoneListAdapter);
						//默认号码   当前号码的改变
						String currentPhone = JUtil.getSharePreStr(UserPhonesActivity.this, APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE);
						if (currentPhone.equals(phoneString) && userPhones.size()>0) {//刚刚删除的是默认号码，剩下还有别的号码，选第一个作为默认号码
							JUtil.putSharePre(UserPhonesActivity.this, APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE, userPhones.get(0).getPhoneStr());
						}else if (userPhones.size()==0) {
							JUtil.putSharePre(UserPhonesActivity.this, APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE, "");
						}
						userDeleyePhoneChange();
		            }
		        }); 
		        normalDia.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
		            @Override 
		            public void onClick(DialogInterface dialog, int which) { 
		            	
		            } 
		        }); 
		        normalDia.create().show(); 
				return false;
			}
		});
	}
	
	private void userDeleyePhoneChange() {
		//当前号码为空，就发广播
			Intent intent = new Intent();
			intent.setAction(ACTION_FIRST_BIND);
			UserPhonesActivity.this.sendBroadcast(intent);
	}
}
