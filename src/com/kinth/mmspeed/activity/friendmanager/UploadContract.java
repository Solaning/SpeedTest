package com.kinth.mmspeed.activity.friendmanager;

import java.util.List;

import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.network.AsyncNetworkManager;

import android.content.Context;
import android.os.Looper;


public class UploadContract {

	public void VerifitionCode(final Context context,final String mobile,final List<ContractInfo> contractInfos,final UploadContractCallBack verifyCodeCallBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				
				AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
				asyncNetworkManager.getMatchFriendsFromContacts(context, mobile, contractInfos, new AsyncNetworkManager.MatchFriendsFromContacts() {
					
					@Override
					public void MatchFriendsFromContactsCallBack(int rtn) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
				Looper.loop();
			}
		}).start();
	}
	
	public interface UploadContractCallBack{
		void onSuccess();
		void onFail();
	}
	public interface MatchFriendsFromContacts {
		public void MatchFriendsFromContactsCallBack(int rtn);
	}	
}
