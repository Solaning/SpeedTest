package com.kinth.mmspeed.activity.billboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.BillboardFlowAdapter;
import com.kinth.mmspeed.bean.BillboardFlowItem;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.fragment.BaseFragment;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 流量排行榜
 */
public class FlowBillboardFragment extends BaseFragment {
	private Context mContext;
	private List<ContractInfo> mLocalList;
	private UserAccount userAccount;
	private DbUtils userDbUtils; // 当前用户的数据库，用于保存用户的数据
	private boolean isRefreshing;//正在刷新
	private OnFlowRefresh listener;
	
	/**
	 * 刷新的接口
	 */
	public interface OnFlowRefresh{
		public void onStart();//开始刷新
		public void onStop();//停止
	}

	@ViewInject(R.id.lv_flow)
	private PullToRefreshListView mListView;
	
	public FlowBillboardFragment(OnFlowRefresh listener) {
		super();
		this.listener = listener;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		userAccount = MainAccountUtil.getCurrentAccount(mContext);
		mLocalList = JUtil.getPhoneAndSimContracts(mContext);
		userDbUtils =  DbUtils.create(mContext, userAccount.getMobile() + "");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flowbillboard, container, false);
		ViewUtils.inject(this,view);
		
		mListView.setMode(Mode.PULL_FROM_START);
		mListView.setScrollingWhileRefreshingEnabled(true);// 在刷新时可以滑动
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				getFlowBillboard(mLocalList);
			}
			
		});
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		geGetFlowBillboard();
	}

	
	/**
	 * 获取流量排行榜 1.如果有排行榜缓存就显示 2.没有就异步加载
	 */
	private void geGetFlowBillboard() {
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
					mContext, localItems);
			mListView.setAdapter(billboardFlowAdapter);
		}
		// 隐藏，进度
		// 异步更新流量排行榜
		isRefreshing = true;
		if(listener != null){
			listener.onStart();
		}
		getFlowBillboard(mLocalList);
	}
	
	private void getFlowBillboard(final List<ContractInfo> contractInfos){
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getFlowBillboard(mContext, "flow",
				userAccount.getMobile() + "",
				new AsyncNetworkManager.GetFlowBillBoardImp() {
					@Override
					public void GetFlowBillBoardImpCallBack(int rtn,
							List<BillboardFlowItem> billboardFlows) {
						if (rtn == 1) {
							// 流量数据存在，但是里面的昵称很可能为空，还是需要和本地进行匹配
							if (billboardFlows != null			
									&& billboardFlows.size() > 0) {
								new GetFlowBillboardInfoTask().execute(contractInfos,billboardFlows);
							} else {
								setRefreshComplete();
								JUtil.showMsg(mContext,"流量排行榜为空！");
								if(listener != null){
									listener.onStop();
								}
							}
						} else {
							if(listener != null){
								listener.onStop();
							}
							setRefreshComplete();
							JUtil.showMsg(mContext, "获取流量排行榜失败！");
						}
					}
				});
	}
	/**
	 * 
	 * @author admin 返回的4G排行榜数据，
	 */
	class GetFlowBillboardInfoTask extends
			AsyncTask<Object, Void, List<BillboardFlowItem>> {
		private List<ContractInfo> originContacts; // 用户传进来的 本地通讯录
		private List<BillboardFlowItem> finalFlowItems; // 最终排行榜的排序结果
		String userPhone;
		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected List<BillboardFlowItem> doInBackground(Object... args) {
			originContacts = (List<ContractInfo>) args[0];
			finalFlowItems= (List<BillboardFlowItem>) args[1];
			for (int i = 0; i < finalFlowItems.size(); i++) {
				for (int j = 0; j < originContacts.size(); j++) {
					if (originContacts.get(j).getMobile().equals(finalFlowItems.get(i).getMobile())) {
						finalFlowItems.get(i).setNickName(originContacts.get(j).getContractName()+"");
					}
				}
			}
			
			try {
				userDbUtils
						.deleteAll(BillboardFlowItem.class);
				userDbUtils.saveAll(finalFlowItems);
			} catch (DbException e) {
				e.printStackTrace();
			}
			return finalFlowItems;
			
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(List<BillboardFlowItem> billboardFlows) {
			setRefreshComplete();
			if(listener != null){
				listener.onStop();
			}
			if (billboardFlows != null && billboardFlows.size() > 0) {
				
				BillboardFlowAdapter billboardFlowAdapter = new BillboardFlowAdapter(
						mContext, billboardFlows);
				mListView.setAdapter(billboardFlowAdapter);
			}
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
		if(mListView.isRefreshing()){
			mListView.onRefreshComplete();
		}
	}
}
