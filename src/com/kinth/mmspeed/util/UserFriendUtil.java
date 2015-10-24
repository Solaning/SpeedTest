package com.kinth.mmspeed.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.activity.billboard.PinyinComparator;
import com.kinth.mmspeed.activity.billboard.UserFriendState;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserFriend;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class UserFriendUtil {
	public  static synchronized void checkUploadContract(Context context) {
		//如果账户为空，就不处理
		UserAccount user = MainAccountUtil.getCurrentAccount(context);
		if (user==null||user.getMobile()==null||user.getMobile().equals("")) {
			return;
		}
		//如果账户不为空，查看是否上传过通讯录，没有则上传，有得话就看是否超过7天
		String lastTimeGetFriend = JUtil.getSharePreStr(context, APPConstant.SP_NAME, APPConstant.SP_LAST_GETFRIEND_TIME+"_"+user.getMobile());
		//没有上传过，就上传
		if (lastTimeGetFriend.equals("")) {
			uploadContractTosServer(context,user.getMobile());
		}else {
			 long time=System.currentTimeMillis();
		        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");  
		        Date d1=new Date(time);  
		        String t1=format.format(d1);  
		        if (JUtil.getGapCount(lastTimeGetFriend,t1)>=5||JUtil.getGapCount(t1,lastTimeGetFriend)>=5) {
		        	uploadContractTosServer(context,user.getMobile());
				}
		}
	}
	
	/**
	 * 上传通讯录，然后同通讯录获取好友
	 * @param contractInfos  手机的通讯录
	 */
	private static void uploadContractTosServer(final Context context,final String userMobile) {
		final List<ContractInfo>contractInfos = JUtil.getPhoneAndSimContracts(context);
		if (contractInfos==null||contractInfos.size()==0) {
			return;
		}
		//上传到服务器
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getMatchFriendsFromContacts(context,userMobile,
				contractInfos,
				new AsyncNetworkManager.MatchFriendsFromContacts() {
					@Override
					public void MatchFriendsFromContactsCallBack(int rtn) {
						//上传通讯录，成功之后就获取，好友
						if (rtn==1) {
							getFriendsFromServer(context,userMobile,contractInfos);
							
							long time=System.currentTimeMillis();
					        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");  
					        Date d1=new Date(time);  
					        String t1=format.format(d1);  
							JUtil.putSharePre(context, APPConstant.SP_NAME, APPConstant.SP_LAST_GETFRIEND_TIME+"_"+userMobile, t1);
							
						}else {
//							Log.e("Tab4", "上传通讯录好友失败");
						}
					}
		});
	}
	private static void getFriendsFromServer(final Context context,final String userPhone,final List<ContractInfo>contractInfos) {
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getFriendsFromByPhone(context, userPhone, new AsyncNetworkManager.IGetUserFriends() {
			@Override
			public void IGetUserFriendsCallBack(int rtn, List<UserFriend> friends) {
				if (rtn==1) {
					new SaveUserFriend().execute(context,userPhone,contractInfos,friends);
				}else {
					Log.e("Tab4", "获取通讯录好友失败");
				}
			}
		});
	}
	/**
	 * 保存用户好友。
	 * 用户手机命名的数据库中的UserFriend对应的的  通讯录
	 * @author admin
	 */
	public static class SaveUserFriend extends AsyncTask<Object,Void,List<ContractInfo>>{  
        List<ContractInfo>mContractLocalInfos;         //用户传进来的  本地通讯录
        List<ContractInfo>mContractLocalInfosBak;         //用户传进来的  本地通讯录备份用于筛选
        String userPhone ;
        
        List<UserFriend>userFriends;       //用户好友
        List<ContractInfo>finalContractInfos= new ArrayList<ContractInfo>();       //用户好友
        List<ContractInfo>userContractInfos = new ArrayList<ContractInfo>();       //用户好友
        private DbUtils  userLocalFirendsDbUtils;
        private Context context;
		/** 
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法 
         */  
        @Override  
        protected List<ContractInfo> doInBackground(Object... args) {  
        	userPhone =  (String) args[1];
			context =  (Context) args[0];
			
			userLocalFirendsDbUtils =  DbUtils.create(context, userPhone+".friends");
			
        	mContractLocalInfos = (List<ContractInfo>) args[2];   //本地通讯录
        	userFriends = (List<UserFriend>) args[3];   //好友
        	
        	mContractLocalInfosBak = new ArrayList<ContractInfo>();
        	mContractLocalInfosBak.addAll(mContractLocalInfos);
        	
        	finalContractInfos =new ArrayList<ContractInfo>();
        	//筛选出非好友用户。就是对边是否有对话一样，一样的话  备份就移除,剩下的
        	
        	for (int i = 0; i < userFriends.size(); i++) {
				UserFriend userFriend = userFriends.get(i);
				for (ContractInfo contractInfo : mContractLocalInfos) {
					if (contractInfo.getMobile().equals(userFriend.getMobile())) {
						userContractInfos.add(new ContractInfo(userFriend.getMobile(), 
								userFriend.getNickName()+"", userFriend.getIconURL(), contractInfo.getContractName()+"", 
								UserFriendState.IS_FRIEND));
					}
				}
			}
        	if (userContractInfos!=null&&userContractInfos.size()>0) {   //用户好友
        		Collections.sort(userContractInfos, new PinyinComparator());
        		finalContractInfos.addAll(userContractInfos);
			}
        	try {
        		userLocalFirendsDbUtils.deleteAll(ContractInfo.class);
        		userLocalFirendsDbUtils.saveAll(userContractInfos);
			} catch (DbException e) {
				e.printStackTrace();
			}
        	
            return finalContractInfos;  
        }  
  
        /** 
         * 运行在ui线程中，在doInBackground()执行完毕后执行 
         */  
        @Override  
        protected void onPostExecute(List<ContractInfo> contractInfosResult) {   //本地好友
        	long time=System.currentTimeMillis();
	        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");  
	        Date d1=new Date(time);  
	        String t1=format.format(d1);  
			JUtil.putSharePre(context, APPConstant.SP_NAME, APPConstant.SP_LAST_GETFRIEND_TIME+"_"+userPhone, t1);
        }  
    }  
	
}
