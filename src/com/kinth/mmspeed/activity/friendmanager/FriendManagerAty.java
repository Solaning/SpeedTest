package com.kinth.mmspeed.activity.friendmanager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.BaseActivity;
import com.kinth.mmspeed.GM_LoginActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.activity.billboard.PinyinComparator;
import com.kinth.mmspeed.activity.billboard.UserFriendState;
import com.kinth.mmspeed.adapter.FriendManagerAdapter;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserFriend;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.ui.CircularProgressView;
import com.kinth.mmspeed.util.AnimationUtil;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 查看本地是否有缓存好友，有得话，就显示，然后异步更新。
 * 查询本地的用户的数据库，用用户的手机号码，做数据库的名字
 * 每个用户对应一个数据库
 * @author admin
 */
@ContentView(R.layout.aty_friend_manager)
public class FriendManagerAty extends BaseActivity {
	@ViewInject(R.id.lv)
	private ListView mListView;
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	private FriendManagerAdapter friendManagerAdapter;
	private UserAccount userAccount;     //用户
	private DbUtils mDbUtils;
	private List<ContractInfo> userFriends;
	
	@ViewInject(R.id.nav_right_progress)
	private CircularProgressView progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTittle.setText("我的好友");
		userAccount = MainAccountUtil.getCurrentAccount(this);
		if (userAccount==null || TextUtils.isEmpty(userAccount.getMobile())) {
			//账号为空就finish
			Intent intent = new Intent(this,GM_LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, FriendManagerAty.class.getName());//传递需要跳转的类名
			intent.putExtras(bundle);
			startActivity(intent);
			this.finish();
			return;
		}
		//用户手机命名的数据库
		mDbUtils = DbUtils.create(this, userAccount.getMobile()+".friend_manager");
		try {
			//本地保存的用户好友
			userFriends = mDbUtils.findAll(ContractInfo.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		//本地没有好友   就去匹配好友
		if (userFriends==null||userFriends.size()==0) {  
			showProgress();
			getMatchFriendsFromContacts(userAccount.getMobile()+"");
		//有好友就先显示好友，然后异步更新
		}else {
			//异步筛选
			friendManagerAdapter = new FriendManagerAdapter(FriendManagerAty.this, userFriends,userAccount.getMobile()+"");
        	mListView.setAdapter(friendManagerAdapter);
			//异步gengxin
		    upDateFriend(userAccount.getMobile()+"");
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if (AnimationUtil.ANIM_IN != 0 && AnimationUtil.ANIM_OUT != 0) {
			super.overridePendingTransition(AnimationUtil.ANIM_IN,
					AnimationUtil.ANIM_OUT);
			AnimationUtil.clear();
		}
	}
	/**
	 * 从服务端匹配好友
	 * 匹配成功后，查询4G流量
	 * @param contractInfos  手机的通讯录
	 */
	private void getMatchFriendsFromContacts(final String userMobile) {
		final List<ContractInfo>mlocaList = JUtil.getPhoneAndSimContracts(this);
		if (mlocaList==null||mlocaList.size()==0) {
			Toast.makeText(this, "无法获取通讯录，或者通讯录为空", Toast.LENGTH_SHORT).show();
			dismissProgress();
			return;
		}
		//上传到服务器
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getMatchFriendsFromContacts(this,userMobile,
				mlocaList,
				new AsyncNetworkManager.MatchFriendsFromContacts() {
					@Override
					public void MatchFriendsFromContactsCallBack(int rtn) {
						//有好友存在
						//获取4G的流量
						if (rtn==1) {        //成功，就获取好友
							upDateFriend(userMobile);
						}else {
							dismissProgress();
							JUtil.showMsg(FriendManagerAty.this, "获取好友失败");
						}
					}
				});
	}
	
	/**
	 * 更新用户的好友
	 * 通过号码，查询好友，返回 List<UserFriend> friends
	 * @param userPhone
	 * @param contractInfos   用户通讯录  用于做对比
	 */
	private void upDateFriend(String userPhone) {
		final List<ContractInfo>mlocaList = JUtil.getPhoneAndSimContracts(this);
		showProgress();
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getFriendsFromByPhone(FriendManagerAty.this, userPhone, new AsyncNetworkManager.IGetUserFriends() {
			@Override
			public void IGetUserFriendsCallBack(int rtn, List<UserFriend> friends) {
				
				if (rtn==1) {
					new UpdateFriendContractInfoTask().execute(mlocaList,friends);
				}else {
					dismissProgress();
					JUtil.showMsg(FriendManagerAty.this, "获取好友失败");
				}
			}
		});
	}
	
	
	/**
	 * 筛选用户好友，进行排序
	 * @author admin
	 */
	class UpdateFriendContractInfoTask extends AsyncTask<Object,Void,List<ContractInfo>>{  
        List<ContractInfo>mContractLocalInfos;         //用户传进来的  本地通讯录
        List<UserFriend>userFriends;       //用户好友
        
        List<ContractInfo>tempNoFriend = new ArrayList<ContractInfo>();       //排序临时好友
        List<ContractInfo>tempFriends = new ArrayList<ContractInfo>();       //排序临时非用户好友
        
        List<ContractInfo>finalContractInfos= new ArrayList<ContractInfo>();       //排序结果

        @Override  
        protected List<ContractInfo> doInBackground(Object... args) {  
        	mContractLocalInfos = (List<ContractInfo>) args[0];   //本地通讯录
        	userFriends = (List<UserFriend>) args[1];   //好友
        	
        	finalContractInfos =new ArrayList<ContractInfo>();
        	//删除本地好友，再保存
        	try {
        		mDbUtils.deleteAll(UserFriend.class);
				mDbUtils.saveAll(userFriends);
			} catch (DbException e1) {
				e1.printStackTrace();
			}
        	//本地通讯录查找，如果有和userFriends   电话一样的，userFriends就增加进去
        	for (int i = 0; i < mContractLocalInfos.size(); i++) {
        		boolean exsit = false;
        		ContractInfo contractInfo = mContractLocalInfos.get(i);
        		String currentContractPhone = contractInfo.getMobile().replace(" ", "");
        		String userPhone = "";
        		//如果是好友就增加到
				for (int j = 0; j < userFriends.size(); j++) {
					//如果电话一样
					userPhone =  userFriends.get(j).getMobile().replace(" ", "");
					if (userPhone.equals(currentContractPhone)) {
						tempFriends.add(new ContractInfo(currentContractPhone, 
								userFriends.get(j).getNickName()+"", userFriends.get(j).getIconURL()+"", contractInfo.getContractName()+"", 
								UserFriendState.IS_FRIEND));
						userFriends.remove(j);
						  exsit= true;
						  break;
					}
				}
				//没有的话，就显示
				if (exsit == true) {
					  continue;
				}else {
					tempNoFriend.add(new ContractInfo(currentContractPhone, 
							contractInfo.getNickName()+"", "", contractInfo.getContractName()+"", 
								UserFriendState.NOT_FRIEND));
				}
			}
        	PinyinComparator pinyinComparator = new PinyinComparator();
        	if (tempFriends!=null&&tempFriends.size()>0) {   //用户好友
        		Collections.sort(tempFriends, new PinyinComparator());
        		finalContractInfos.addAll(tempFriends);
			}
        	
        	if (userFriends!=null&&userFriends.size()>0) {
				for (int j = 0; j < userFriends.size(); j++) {
					UserFriend userFriend = userFriends.get(j);
					finalContractInfos.add(new ContractInfo(userFriend.getMobile().replace(" ", "")+"", 
							userFriend.getNickName()+"", "", "", 
								UserFriendState.IS_FRIEND));
				}
			}
        	
        	if (tempNoFriend!=null&&tempNoFriend.size()>0) {
        		Collections.sort(tempNoFriend, pinyinComparator);
        		finalContractInfos.addAll(tempNoFriend);
			}
        	//剩余的没排序的
        
        	//保存数据到数据库
        	try {
				mDbUtils.deleteAll(ContractInfo.class);
				mDbUtils.saveAll(finalContractInfos);
			} catch (DbException e) {
				e.printStackTrace();
			}
            return finalContractInfos;  
        }  
  
        @Override  
        protected void onPostExecute(List<ContractInfo> contractInfosResult) {  
        	
        	friendManagerAdapter = new FriendManagerAdapter(FriendManagerAty.this, contractInfosResult, userAccount.getMobile()+"");
        	mListView.setAdapter(friendManagerAdapter);
        	dismissProgress();
        }  
    } 
	
	private void showProgress() {
		if (progressBar.getVisibility()==View.INVISIBLE) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	private void dismissProgress() {
		if (progressBar.getVisibility()==View.VISIBLE) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	
}
