package com.kinth.mmspeed.activity.billboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.Billboard4GAdapter;
import com.kinth.mmspeed.bean.Billboard4GItem;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.RankItem4G;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.fragment.BaseFragment;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 4g排行榜
 */
public class FTDBillboardFragment extends BaseFragment {
	private Context mContext;
	private List<ContractInfo> mLocalList;//本地通讯录的联系人列表
	private UserAccount userAccount;
	private DbUtils userDbUtils; // 当前用户的数据库，用于保存用户的数据
	private List<ContractInfo> ftdCacheContractInfos;//数据库中存储的联系人信息
	private boolean isRefreshing;//正在刷新
	private On4GRefresh listener;
	
	/**
	 * 刷新的接口
	 */
	public interface On4GRefresh{
		public void onStart();//开始刷新
		public void onStop();//停止
	}
	
	@ViewInject(R.id.lv_ftd)
	private PullToRefreshListView mFTDListView;

	public FTDBillboardFragment(On4GRefresh listener) {
		super();
		this.listener = listener;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		userAccount = MainAccountUtil.getCurrentAccount(mContext);
		mLocalList = JUtil.getPhoneAndSimContracts(mContext);//TODO 太耗时
		userDbUtils =  DbUtils.create(mContext, userAccount.getMobile() + "");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ftd_fragment, container, false);
		ViewUtils.inject(this,view);
		
		mFTDListView.setMode(Mode.PULL_FROM_START);
		mFTDListView.setScrollingWhileRefreshingEnabled(true);// 在刷新时可以滑动
		mFTDListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if(isRefreshing){
					
				}else{
					get4GBillboard(userAccount.getMobile()+"");
				}
			}
			
		});
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		get4GFlow(userAccount.getMobile()+"");
	}
	
	/**
	 * 获取4G排行榜
	 * 
	 * @param userPhone
	 *            用户当前账号
	 * @param mContractInfos
	 */
	private void get4GFlow(String userPhone) {
		try {
			ftdCacheContractInfos = userDbUtils.findAll(ContractInfo.class);
			if (ftdCacheContractInfos != null && ftdCacheContractInfos.size() > 0) {//数据库缓存数据
				Billboard4GAdapter billboard4gAdapter = new Billboard4GAdapter(
						mContext, ftdCacheContractInfos, userPhone);
				mFTDListView.setAdapter(billboard4gAdapter);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		isRefreshing = true;
		if(listener != null){
			listener.onStart();
		}
		get4GBillboard(userPhone);
	}
	
	/**
	 * 联网获取数据
	 * @param userPhone
	 * @param mContractInfos
	 */
	private void get4GBillboard(final String userPhone){
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.get4GBillboard(mContext, "4G",
				userPhone, new AsyncNetworkManager.Get4GBillBoardImp() {
			
					@Override
					public void Get4GBillBoardImpCallBack(int rtn,
							Billboard4GItem billboard4g) {
						if (rtn == 1) { // 返回成功
							if (billboard4g != null) { // 有数据返回
								new GetContractInfoTask().execute(billboard4g, userPhone);
							} else {
								setRefreshComplete();
								JUtil.showMsg(mContext,
										"获取4G排行榜失败");
								if(listener != null){
									listener.onStop();
								}
							}
						} else {
							setRefreshComplete();
							JUtil.showMsg(mContext, "获取4G排行榜失败");
							if(listener != null){
								listener.onStop();
							}
						}
					}
				});
	}
	
	/**
	 * 处理返回的4G排行榜数据，
	 */
	class GetContractInfoTask extends
			AsyncTask<Object, Void, List<ContractInfo>> {
		private Billboard4GItem billboard4gItem; // 后台返回的4G排行信息
		private List<ContractInfo> finalContractInfos; // 最终排行榜的排序结果
		private String userPhone;
		
		@Override
		protected List<ContractInfo> doInBackground(Object... args) {
			billboard4gItem = (Billboard4GItem) args[0];
			userPhone = (String) args[1];
			
			finalContractInfos = new ArrayList<ContractInfo>();

			List<ContractInfo> prodRankList = new ArrayList<ContractInfo>();
			for(RankItem4G item : billboard4gItem.getProdRankList()){//按套餐高低排序
				ContractInfo contract = new ContractInfo();
				contract.setIconURL(item.getIconURL());
				contract.setMobile(item.getMobile());
				contract.setNickName(item.getNickname());
				contract.setState(UserFriendState.HIGH_PROD);
				prodRankList.add(contract);
			}
			List<ContractInfo> friend4GList = billboard4gItem.getFriend4GList(); // 使用4G的好友
			List<ContractInfo> friendreCommendedList = billboard4gItem.getRecommendedFriend(); // 已向他推荐4G的好友
			// 临时数据
			List<ContractInfo> tempProdRankList = new ArrayList<ContractInfo>();//套餐高到低-本地匹配到的
			List<ContractInfo> tempLTEFriends = new ArrayList<ContractInfo>(); // 4G好友
			List<ContractInfo> tempRecomandFriends = new ArrayList<ContractInfo>(); // 推荐待处理好友
			List<ContractInfo> tempNoHandleFriends = new ArrayList<ContractInfo>();//没有匹配到的好友
			
			if(friend4GList == null){
				friend4GList = new ArrayList<ContractInfo>();
			}
			if(friendreCommendedList == null){
				friendreCommendedList = new ArrayList<ContractInfo>();
			}
			
			// 遍历本地通讯录
			for (ContractInfo contractInfo : mLocalList) {
				String mobile = contractInfo.getMobile() + "";//本地的某个号码
				String contractName = contractInfo.getContractName() + "";//本地的某个联系人名称
				boolean exsit = false;
				
				for(int i = 0; i < prodRankList.size(); i++){
					ContractInfo item = prodRankList.get(i);
					// 如果电话一样，4G好友增加
					if ((item.getMobile().replace(" ", "")).equals(mobile)){
						item.setContractName(contractName);
						exsit = true;
						tempProdRankList.add(item);
						prodRankList.remove(i);
						break;
					}
				}
				if (exsit == true) {
					continue;
				}
				
				// 遍历是该号码是否在返回的4G好友
				for (int i = 0 ;i < friend4GList.size(); i++) {
					ContractInfo friendContractInfo = friend4GList.get(i);
					// 如果电话一样，4G好友增加
					if ((friendContractInfo.getMobile().replace(" ", "")).equals(mobile)){
						friendContractInfo.setState(UserFriendState.LET);
						friendContractInfo.setContractName(contractName);
						exsit = true;
						tempLTEFriends.add(friendContractInfo);
						friend4GList.remove(i);
						break;
					}
				}
				if (exsit == true) {
					continue;
				}

				for (int i = 0 ; i < friendreCommendedList.size() ; i++) {
					ContractInfo recommendFriend = friendreCommendedList.get(i);
					if ((recommendFriend.getMobile().replace(" ", "")).equals(mobile)) {
						recommendFriend.setContractName(contractName);
						recommendFriend.setState(UserFriendState.RECOMAND);
						exsit = true;
						tempRecomandFriends.add(recommendFriend);
						friendreCommendedList.remove(recommendFriend);
						break;
					}
				}

				if (exsit == true) {
					continue;
				}else {
					ContractInfo contactTemp = contractInfo;//直接复制过去
					contactTemp.setState(UserFriendState.NOHANDLE);//暂时未有身份的号码
					tempNoHandleFriends.add(contactTemp);
				}
			}
			if(tempProdRankList.size() > 0){//套餐高且有匹配本地姓名
				finalContractInfos.addAll(tempProdRankList);
			}
			if(prodRankList.size() > 0){//套餐高但没有匹配本地姓名
				finalContractInfos.addAll(prodRankList);
			}
			
			if (tempLTEFriends.size() > 0) {//本地匹配到的4G
				Collections.sort(tempLTEFriends, new PinyinComparator());
				finalContractInfos.addAll(tempLTEFriends);
			}
			//本地没有匹配到的4G
			if (friend4GList != null && friend4GList.size() > 0) {
				for (int j = 0; j < friend4GList.size(); j++) {
					friend4GList.get(j).setContractName("");
					friend4GList.get(j).setState(UserFriendState.LET);
				}
				finalContractInfos.addAll(friend4GList);
			}
			
			//临时推荐中的好友
			if (tempRecomandFriends != null && tempRecomandFriends.size() > 0) {
				Collections.sort(tempRecomandFriends, new PinyinComparator());
				finalContractInfos.addAll(tempRecomandFriends);
			}
			//
			if (friendreCommendedList != null && friendreCommendedList.size() > 0) {
				for (int j = 0; j < friendreCommendedList.size(); j++) {
					if (TextUtils.isEmpty(friendreCommendedList.get(j).getContractName())) {
						friendreCommendedList.get(j).setContractName(friendreCommendedList.get(j).getMobile().replace(" ", "")+"");
					}
					friendreCommendedList.get(j).setState(UserFriendState.RECOMAND);
				}
				finalContractInfos.addAll(friendreCommendedList);
			}
			
			if (tempNoHandleFriends != null && tempNoHandleFriends.size() > 0) {
				Collections.sort(tempNoHandleFriends, new PinyinComparator());
				finalContractInfos.addAll(tempNoHandleFriends);
			}
			
			if (finalContractInfos != null && finalContractInfos.size() > 0) {
				try {
					userDbUtils.createTableIfNotExist(ContractInfo.class);
					if (userDbUtils.count(Selector.from(ContractInfo.class)) > 0) {
						userDbUtils.deleteAll(ContractInfo.class);
					}
					// 保存4G排行榜缓存
					userDbUtils.saveAll(finalContractInfos);
				} catch (DbException e) {
					e.printStackTrace();
					JUtil.showMsg(mContext, "保存好友失败，请稍后重试！");
				}
			}
			return finalContractInfos;
		}

		@Override
		protected void onPostExecute(List<ContractInfo> contractInfosResult) {
			if (contractInfosResult != null && contractInfosResult.size() > 0) {
				mFTDListView.invalidate();
				Billboard4GAdapter billboard4gAdapter = new Billboard4GAdapter(
						mContext, contractInfosResult, userPhone);
				mFTDListView.setAdapter(billboard4gAdapter);
			}
			if(listener != null){
				listener.onStart();
			}
			setRefreshComplete();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(listener == null){
			return;
		}
		if(isRefreshing){
			listener.onStart();
		}else{
			listener.onStop();
		}
	}

	private void setRefreshComplete(){
		isRefreshing = false;
		if(mFTDListView.isRefreshing()){
			mFTDListView.onRefreshComplete();
		}
	}
}
