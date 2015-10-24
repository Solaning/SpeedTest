package com.kinth.mmspeed.activity.billboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.BaseActivity;
import com.kinth.mmspeed.GM_LoginActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.Billboard4GAdapter;
import com.kinth.mmspeed.adapter.BillboardFlowAdapter;
import com.kinth.mmspeed.bean.Billboard4GItem;
import com.kinth.mmspeed.bean.BillboardFlowItem;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.ContractLocalInfo;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserFriend;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.ui.CircularProgressView;
import com.kinth.mmspeed.util.AnimationUtil;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 排行榜
 * @author admin
 */
@Deprecated
@ContentView(R.layout.activity_billboard_deprecated)
public class BillboardActivity_Deprecated extends BaseActivity {
	
	@ViewInject(R.id.rpg_top)
	private RadioGroup rpgTop;
	
	@ViewInject(R.id.rbt_one)
	private RadioButton rbtOne;
	
	@ViewInject(R.id.rbt_two)
	private RadioButton rbtTwo;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;

	@ViewInject(R.id.nav_right_progress)
	private CircularProgressView progressBar;
	
	public Context context;
	List<String> contactList = new ArrayList<String>();
	@ViewInject(R.id.lv)
	private ListView mListView;
	UserAccount userAccount;
	private List<ContractInfo> m4GContractInfoCache; // 保存4G排行榜缓存
	private List<ContractInfo> localUserContractInfos; // 本地好友缓存
	private ProgressDialog mProgressDialog = null;
	private Billboard4GAdapter billboard4gAdapter;
	private DbUtils userDbUtils; // 当前用户的数据库，用于保存用户的数据
	private DbUtils userLocalFirendsDbUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		context = this;
		tvTittle.setText("好友排行榜");
		// 用户账号
		userAccount = MainAccountUtil.getCurrentAccount(this);
		userDbUtils = DbUtils.create(this, userAccount.getMobile() + "");
		userLocalFirendsDbUtils = DbUtils.create(this, userAccount.getMobile()
				+ ".friends");
		userAccount = MainAccountUtil.getCurrentAccount(this);
		m4GContractInfoCache = new ArrayList<ContractInfo>();
		localUserContractInfos = new ArrayList<ContractInfo>();
		try {
			userDbUtils.createTableIfNotExist(ContractInfo.class);
			userDbUtils.createTableIfNotExist(ContractLocalInfo.class);
			userDbUtils.createTableIfNotExist(BillboardFlowItem.class); // 用户流量排行榜
		} catch (DbException e1) {
			e1.printStackTrace();
		}
		// 用户为空,先让用户登录
		if (userAccount == null || userAccount.getMobile().equals("")) { // 一会记得改回来
			Intent intent = new Intent(this, GM_LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, BillboardActivity_Deprecated.class.getName());//传递需要跳转的类名
			intent.putExtras(bundle);
			startActivity(intent);
			this.finish();
			return;
			// 用户不为空
		} else {
			try {
				localUserContractInfos = userLocalFirendsDbUtils
						.findAll(ContractInfo.class);
			} catch (DbException e1) {
				e1.printStackTrace();
			}
			// 本地有好友，就显示好友
			if (localUserContractInfos != null
					&& localUserContractInfos.size() > 0) {
				// uploadContractTosServer(userAccount.getMobile()+"");
				get4GFlow(userAccount.getMobile(), localUserContractInfos);
				try {
					// 获取是否有4G排行榜缓存
					m4GContractInfoCache = userDbUtils
							.findAll(ContractInfo.class);
					if (m4GContractInfoCache != null
							&& m4GContractInfoCache.size() > 0) {
						// 有缓存先显示旧的数据，在异步更新
						billboard4gAdapter = new Billboard4GAdapter(context,
								m4GContractInfoCache, userAccount.getMobile()
										+ "");
						mListView.setAdapter(billboard4gAdapter);
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
			// 没有通讯录 就上传通讯录
			else {
				uploadContractTosServer(userAccount.getMobile() + "");
			}
		}

	}

	/**
	 * 从服务端匹配好友 匹配成功后，查询4G流量
	 * 
	 * @param contractInfos
	 *            手机的通讯录
	 */
	private void uploadContractTosServer(final String userMobile) {
		final List<ContractInfo> contractInfos = JUtil
				.getPhoneAndSimContracts(this);
		if (contractInfos == null || contractInfos.size() == 0) {
			Toast.makeText(this, "无法获取通讯录，或者通讯录为空", Toast.LENGTH_SHORT).show();
			return;
		}
		// 上传到服务器
		mProgressDialog = JUtil.showDialog(BillboardActivity_Deprecated.this,
				"正在获取通讯录好友...");
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getMatchFriendsFromContacts(this, userMobile,
				contractInfos,
				new AsyncNetworkManager.MatchFriendsFromContacts() {
					@Override
					public void MatchFriendsFromContactsCallBack(int rtn) {
						// 上传通讯录，成功
						if (rtn == 1) {
							JUtil.dismissDialog(mProgressDialog);
							getFriendsFromServer(userMobile, contractInfos);
							// 有好友存在
							// 获取4G的流量
							// get4GFlow(userMobile,contractInfos);
						} else {
							Toast.makeText(BillboardActivity_Deprecated.this, "获取通讯录好友失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	/**
	 * 获取4G排行榜
	 * 
	 * @param userPhone
	 *            用户当前账号
	 * @param mContractInfos
	 */
	private void get4GFlow(final String userPhone,
			final List<ContractInfo> mContractInfos) {
		showProgress();
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.get4GBillboard(BillboardActivity_Deprecated.this, "4G",
				userPhone, new AsyncNetworkManager.Get4GBillBoardImp() {
					@Override
					public void Get4GBillBoardImpCallBack(int rtn,
							Billboard4GItem billboard4g) {
						dismissProgress();
						if (rtn == 1) { // 返回成功
							if (billboard4g != null) { // 有数据返回
								new GetContractInfoTask().execute(billboard4g,
										mContractInfos, userPhone);
							} else {
								JUtil.showMsg(BillboardActivity_Deprecated.this,
										"获取4G排行榜失败");
							}
						} else {
							JUtil.showMsg(BillboardActivity_Deprecated.this, "获取4G排行榜失败");
						}
					}
				});
	}

	/**
	 * 获取4G排行榜
	 * 
	 * @param userPhone
	 * @param mContractInfos
	 */
	private void get4GFlowCache() {
		final String userPhone = userAccount.getMobile() + "";
		final List<ContractInfo> mContractInfos = m4GContractInfoCache;
		
		
		if (mContractInfos == null || mContractInfos.size() == 0) {
			mListView.setAdapter(null);
		} else {
			Billboard4GAdapter billboard4gAdapter = new Billboard4GAdapter(
					BillboardActivity_Deprecated.this, mContractInfos, userPhone);
			mListView.setAdapter(billboard4gAdapter);
		}
		showProgress();
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.get4GBillboard(BillboardActivity_Deprecated.this, "4G",
				userPhone, new AsyncNetworkManager.Get4GBillBoardImp() {
					@Override
					public void Get4GBillBoardImpCallBack(int rtn,
							Billboard4GItem billboard4g) {
						dismissProgress();
						if (rtn == 1) { // 返回成功
							if (billboard4g != null) { // 有数据返回
								new GetContractInfoTask().execute(billboard4g,
										JUtil.getPhoneAndSimContracts(BillboardActivity_Deprecated.this), userPhone);
							} else {
								JUtil.showMsg(BillboardActivity_Deprecated.this,
										"获取4G排行榜失败");
							}
						} else {
							JUtil.showMsg(BillboardActivity_Deprecated.this, "获取4G排行榜失败");
						}
					}
				});
	}

	/**
	 * 
	 * @author admin 返回的4G排行榜数据，
	 */
	class GetContractInfoTask extends
			AsyncTask<Object, Void, List<ContractInfo>> {
		private Billboard4GItem billboard4gItem; // 4G排行信息
		private List<ContractInfo> originContacts; // 用户传进来的 本地通讯录
		private List<ContractInfo> finalContractInfos; // 最终排行榜的排序结果
		String userPhone;
		
		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected List<ContractInfo> doInBackground(Object... args) {
			billboard4gItem = (Billboard4GItem) args[0];
			originContacts = (List<ContractInfo>) args[1];
			
			finalContractInfos = new ArrayList<ContractInfo>();
			userPhone = (String) args[2];

			List<ContractInfo> friend4GList = new ArrayList<ContractInfo>(); // 使用4G的好友
			List<ContractInfo> friendreCommendedList = new ArrayList<ContractInfo>(); // 已向他推荐4G的好友
			// 临时数据

			List<ContractInfo> tempLTEFriends = new ArrayList<ContractInfo>(); // 4G好友
			List<ContractInfo> tempRecomandFriends = new ArrayList<ContractInfo>(); // 推荐待处理好友
			List<ContractInfo> tempNoHandleFriends = new ArrayList<ContractInfo>();

			if (billboard4gItem.getFriend4GList() != null
					&& billboard4gItem.getFriend4GList().size() > 0) {
				friend4GList.addAll(billboard4gItem.getFriend4GList());
			}
			if (billboard4gItem.getRecommendedFriend() != null
					&& billboard4gItem.getRecommendedFriend().size() > 0) {
				friendreCommendedList.addAll(billboard4gItem
						.getRecommendedFriend());
			}
			// 遍历本地通讯录
			for (ContractInfo contractInfo : originContacts) {
				String mobile = contractInfo.getMobile() + "";
				String nickName = contractInfo.getContractName() + "";
				boolean exsit = false;
				// 遍历返回的4G好友

				for (int i = 0; i < friend4GList.size(); i++) {
					ContractInfo mContractInfo = friend4GList.get(i);
					// 如果电话一样，4G好友增加
					if (mContractInfo.getMobile().equals(mobile)) {
						mContractInfo.setContractName(nickName);
						mContractInfo.setState(UserFriendState.LET);
						exsit = true;
						tempLTEFriends.add(mContractInfo);
						friend4GList.remove(i);
						break;
					}
				}

				if (exsit == true) {
					continue;
				}

				for (int j = 0; j < friendreCommendedList.size(); j++) {
					ContractInfo mContractInfo = friendreCommendedList.get(j);
					if (mContractInfo.getMobile().equals(mobile)) {
						mContractInfo.setContractName(nickName);
						mContractInfo.setState(UserFriendState.RECOMAND);
						exsit = true;
						tempRecomandFriends.add(mContractInfo);
						friendreCommendedList.remove(j);
						break;
					}
				}

				if (exsit == true) {
					continue;
				} else {
					ContractInfo contactTemp = contractInfo;
					contactTemp.setState(UserFriendState.NOHANDLE);
					tempNoHandleFriends.add(contactTemp);
				}
			}
			if (tempLTEFriends != null && tempLTEFriends.size() > 0) {
				Collections.sort(tempLTEFriends, new PinyinComparator());
				finalContractInfos.addAll(tempLTEFriends);
			}
			//没有匹配到的4G
			if (friend4GList!=null&&friend4GList.size()>0) {
				for (int j = 0; j < friend4GList.size(); j++) {
					friend4GList.get(j).setContractName("");
					friend4GList.get(j).setState(UserFriendState.LET);
				}
				finalContractInfos.addAll(friend4GList);
			}
			
			if (tempRecomandFriends != null && tempRecomandFriends.size() > 0) {
				Collections.sort(tempRecomandFriends, new PinyinComparator());
				finalContractInfos.addAll(tempRecomandFriends);
			}
			
			if (friendreCommendedList!=null&&friendreCommendedList.size()>0) {
				for (int j = 0; j < friendreCommendedList.size(); j++) {
					if (friendreCommendedList.get(j).getContractName()==null) {
						friendreCommendedList.get(j).setContractName(friendreCommendedList.get(j).getMobile()+"");
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
					JUtil.showMsg(BillboardActivity_Deprecated.this, "保存好友失败，请稍后重试！");
				}
			}
			return finalContractInfos;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(List<ContractInfo> contractInfosResult) {
			// Toast.makeText(context,"执行完毕",Toast.LENGTH_SHORT).show();
			if (contractInfosResult != null && contractInfosResult.size() > 0) {
				
				
				Billboard4GAdapter billboard4gAdapter = new Billboard4GAdapter(
						BillboardActivity_Deprecated.this, contractInfosResult, userPhone);
				mListView.setAdapter(billboard4gAdapter);
				
				
				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 == m4GContractInfoCache.size()) {
							uploadContractTosServer(userAccount.getMobile()
									+ "");
						}
					}
				});
				setRadioGroupOnClick();
				m4GContractInfoCache = contractInfosResult;
			}
		}
	}

	/**
	 * 获取流量排行榜 1.如果有排行榜缓存就显示 2.没有就异步加载
	 */
	private void geGetFlowBillboard() {
		final List<ContractInfo> contractInfos = JUtil
				.getPhoneAndSimContracts(this);
		// 本地有数据就先显示本地的 再异步加载
		List<BillboardFlowItem> localItems = new ArrayList<BillboardFlowItem>();
		try {
			localItems = userDbUtils.findAll(BillboardFlowItem.class);
		} catch (DbException e1) {
			e1.printStackTrace();
		}
		// 因为点击按钮了，没有流量数据，就显示为空
		if (localItems == null || localItems.size() == 0) {
			mListView.setAdapter(null);
			mListView.setOnItemClickListener(null);
		}
		// 有书库，就显示数据
		if (localItems != null && localItems.size() > 0) {
			BillboardFlowAdapter billboardFlowAdapter = new BillboardFlowAdapter(
					BillboardActivity_Deprecated.this, localItems);
			mListView.setAdapter(billboardFlowAdapter);
		}
		// 隐藏，进度
		showProgress();
		// 异步更新流量排行榜
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getFlowBillboard(BillboardActivity_Deprecated.this, "flow",
				userAccount.getMobile() + "",
				new AsyncNetworkManager.GetFlowBillBoardImp() {
					@Override
					public void GetFlowBillBoardImpCallBack(int rtn,
							List<BillboardFlowItem> billboardFlows) {
						dismissProgress();
						if (rtn == 1) {
							// 流量数据存在，但是里面的昵称很可能为空，还是需要和本地进行匹配
							if (billboardFlows != null
									&& billboardFlows.size() > 0) {
								for (int i = 0; i < billboardFlows.size(); i++) {
									BillboardFlowItem billboardFlowItem = billboardFlows
											.get(i);
									for (ContractInfo contractInfo : contractInfos) {
										if (contractInfo.getMobile().equals(
												billboardFlows.get(i)
														.getMobile())) {
											billboardFlows.get(i).setNickName(
													contractInfo
															.getContractName()
															+ "");
											break;
										}
									}
								}

								BillboardFlowAdapter billboardFlowAdapter = new BillboardFlowAdapter(
										BillboardActivity_Deprecated.this, billboardFlows);
								mListView.setAdapter(billboardFlowAdapter);
								mListView.setOnItemClickListener(null);
								try {
									userDbUtils
											.deleteAll(BillboardFlowItem.class);
									userDbUtils.saveAll(billboardFlows);
								} catch (DbException e) {
									e.printStackTrace();
								}
							} else {
								JUtil.showMsg(BillboardActivity_Deprecated.this,
										"流量排行榜为空！");
							}
						} else {
							JUtil.showMsg(BillboardActivity_Deprecated.this, "获取流量排行榜失败！");
						}
					}
				});
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

	private void setRadioGroupOnClick() {
		rpgTop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// 获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				switch (radioButtonId) {
				case R.id.rbt_one:
					get4GFlowCache();
					break;
				case R.id.rbt_two:
					geGetFlowBillboard();
					break;
				default:
					break;
				}
			}
		});
	}

	private void showProgress() {
		if (progressBar.getVisibility() == View.INVISIBLE) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	private void dismissProgress() {
		if (progressBar.getVisibility() == View.VISIBLE) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	private void getFriendsFromServer(final String userPhone,
			final List<ContractInfo> contractInfos) {
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getFriendsFromByPhone(BillboardActivity_Deprecated.this,
				userPhone, 
				new AsyncNetworkManager.IGetUserFriends() {
					@Override
					public void IGetUserFriendsCallBack(int rtn,
							List<UserFriend> friends) {
						if (rtn == 1) {
							new SaveUserFriend().execute(contractInfos,
									friends, userPhone);
						} else {
							JUtil.showMsg(BillboardActivity_Deprecated.this, "获取好友失败");
						}
					}
				});
	}

	/**
	 * 保存用户好友。 用户手机命名的数据库中的UserFriend对应的
	 * @author admin
	 */
	class SaveUserFriend extends AsyncTask<Object, Void, List<ContractInfo>> {
		List<ContractInfo> mContractLocalInfos; // 用户传进来的 本地通讯录
		String userPhone;
		List<UserFriend> userFriends; // 用户好友
		List<ContractInfo> tempContractInfos = new ArrayList<ContractInfo>(); // 用户好友

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected List<ContractInfo> doInBackground(Object... args) {
			mContractLocalInfos = (List<ContractInfo>) args[0]; // 本地通讯录
			userFriends = (List<UserFriend>) args[1]; // 好友
			userPhone = (String) args[2];

			// 筛选出非好友用户。就是对边是否有对话一样，一样的话 备份就移除,剩下的

			for (int i = 0; i < mContractLocalInfos.size(); i++) {
				for (int j = 0; j < userFriends.size(); j++) {
					// 如果电话一样
					if (userFriends.get(j).getMobile()
							.equals(mContractLocalInfos.get(i).getMobile())) {
						tempContractInfos.add(new ContractInfo(userFriends.get(
								j).getMobile(), userFriends.get(j)
								.getNickName() + "", userFriends.get(j)
								.getIconURL(), mContractLocalInfos.get(i)
								.getContractName(), UserFriendState.IS_FRIEND));
						userFriends.remove(j);
						break;
					}
				}
			}
			
			if (tempContractInfos != null && tempContractInfos.size() > 0) { // 用户好友
				Collections.sort(tempContractInfos, new PinyinComparator());
			}
			try {
				userLocalFirendsDbUtils.deleteAll(ContractInfo.class);
				userLocalFirendsDbUtils.saveAll(tempContractInfos);
			} catch (DbException e) {
				e.printStackTrace();
			}
			return tempContractInfos;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(List<ContractInfo> contractInfosResult) { // 本地好友
			long time = System.currentTimeMillis();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date d1 = new Date(time);
			String t1 = format.format(d1);
			JUtil.putSharePre(BillboardActivity_Deprecated.this, APPConstant.SP_NAME + "_"
					+ userPhone, APPConstant.SP_LAST_GETFRIEND_TIME, t1);
			get4GFlow(userPhone, contractInfosResult);
		}
	}

}
