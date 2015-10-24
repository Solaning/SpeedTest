package com.kinth.mmspeed.friend;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.kinth.mmspeed.BaseActivity;
import com.kinth.mmspeed.GM_LoginActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.MomentsAdapter;
import com.kinth.mmspeed.adapter.MomentsAdapter.ClickCallBack;
import com.kinth.mmspeed.bean.Active;
import com.kinth.mmspeed.bean.ActiveWithComments;
import com.kinth.mmspeed.bean.Comments;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.Praisers;
import com.kinth.mmspeed.bean.UserFriend;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.getActiveTimeLineCallBack;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UserFriendUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 朋友圈
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_moments)
public class MomentsActivity extends BaseActivity {
	public static final String IS_GET_My_ACTIVE = "IsGetMyActive";// 是否获取我的动态
	public static final int DEFAULT_NUM_OF_ACTIVE = 30;// 默认取的动态条数
	private static final int REQUEST_CODE_PICK_PHOTO = 9979;//选图片的请求码
	private static final int REQUEST_CODE_SEND_ACTIVE = 9980;//发动态请求码

	private Context mContext;
	private MomentsAdapter adapter;
	private int firstVisible;// 第一项的位置
	private String mobile;
	private DbUtils db;
	private boolean isGetMyActive;
	private boolean isRefreshing = false;//是否在刷新

	@ViewInject(R.id.nav_left)
	private View back;

	@ViewInject(R.id.nav_right_image)
	private ImageView right;// 右侧按钮
	
	@ViewInject(R.id.nav_tittle)
	private TextView title;

	@ViewInject(R.id.listview_moments)
	private PullToRefreshListView listview;

	@ViewInject(R.id.tv_note)
	private TextView note;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}
	
	@OnClick(R.id.nav_right_image)
	public void fun_2(View v){//发动态
		Intent intent = new Intent(mContext,CustomGalleryActivity.class);
		startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
		rightInAnimation();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		ViewUtils.inject(this);

		isGetMyActive = getIntent().getBooleanExtra(IS_GET_My_ACTIVE, false);
		if (isGetMyActive) {
			title.setText(getResources().getString(R.string.my_Dynamic));
			right.setVisibility(View.GONE);
		} else {
			title.setText(getResources().getString(R.string.moments));
			right.setImageResource(R.drawable.comments_camera);
			right.setVisibility(View.VISIBLE);
		}

		if (MainAccountUtil.getCurrentAccount(mContext) == null) {
			note.setVisibility(View.VISIBLE);
			return;
		}
		mobile = MainAccountUtil.getCurrentAccount(mContext).getMobile();
		if (mobile == null || TextUtils.isEmpty(mobile)) {
			Toast.makeText(mContext, "没有绑定账号", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(mContext, GM_LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, MomentsActivity.class.getName());//传递需要跳转的类名
			bundle.putBoolean(MomentsActivity.IS_GET_My_ACTIVE, isGetMyActive);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
			return;
		}

		listview.setMode(Mode.BOTH);
		listview.setScrollingWhileRefreshingEnabled(true);// 在刷新时可以滑动
		// Set a listener to be invoked when the list should be refreshed.
		listview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(// 加载更晚发的动态
					PullToRefreshBase<ListView> refreshView) {
				LogUtils.e("加载更晚发的动态");
				String timeStamp = null;
				String timeDir = null;
				if (adapter.getActiveWithComments() == null
						|| adapter.getActiveWithComments().size() == 0
						|| adapter.getActiveWithComments().get(0).getActive() == null) {
					
				} else {
					// 取第一个动态的时间戳
					timeStamp = adapter.getActiveWithComments().get(0)
							.getActive().getCreateTime();
					timeDir = "latter";
				}
				// 获取更多我的动态
				new AsyncNetworkManager().getActiveTimeLine(mContext, mobile,
						isGetMyActive ? 1 : 0, timeStamp, timeDir,
						new getActiveTimeLineCallBack() {

							@Override
							public void onGetActiveTimeLineCallBack(int rtn,
									ArrayList<ActiveWithComments> activeList) {
								switch (rtn) {
								case 1:// 成功 ------>处理取回的数据
									new SaveDataToDBTask().execute(activeList);
									break;
								case 0:
								default:
									listview.onRefreshComplete();
									break;
								}
							}
						});
			}

			@Override
			public void onPullUpToRefresh(// 加载更早发的动态
					PullToRefreshBase<ListView> refreshView) {
				LogUtils.e("加载更早发的动态");
				String timeStamp = null;
				String timeDir = null;
				int activeWithCommentsListSize;
				if (adapter.getActiveWithComments() == null
						|| (activeWithCommentsListSize = adapter
								.getActiveWithComments().size()) == 0
						|| adapter.getActiveWithComments()
								.get(activeWithCommentsListSize - 1)
								.getActive() == null) {

				} else {
					// 取最后一个动态的时间戳
					timeStamp = adapter.getActiveWithComments()
							.get(activeWithCommentsListSize - 1).getActive()
							.getCreateTime();
					timeDir = "earlier";
				}
				final String finalTimeStamp = timeStamp;
				// 获取更多我的动态
				new AsyncNetworkManager().getActiveTimeLine(mContext, mobile,
						isGetMyActive ? 1 : 0, timeStamp, timeDir,
						new getActiveTimeLineCallBack() {

							@Override
							public void onGetActiveTimeLineCallBack(int rtn,
									ArrayList<ActiveWithComments> activeList) {
								switch (rtn) {
								case 1:// 成功 ------>处理取回的数据
									new SaveDataToDBTask().execute(activeList);
									break;
								case 0:
								default:
									new LoadDataWhenServerFailedTask()
											.execute(finalTimeStamp);
									break;
								}
							}
						});
			}
		});

		ListView actualListView = listview.getRefreshableView();
		actualListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当列表滚动停止，并且当前可见条目不是第一条的话，运行动画。
				if (SCROLL_STATE_IDLE == scrollState && firstVisible > 0) {
					
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				firstVisible = firstVisibleItem;
			}
		});

		// 数据层操作 数据库版本升级
		db = DbUtils.create(mContext,"xUtils.db", 2, new DbUpgradeListener() {
			
			@Override
			public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				try {
					arg0.createTableIfNotExist(Active.class);
					arg0.execNonQuery("alter table Active add picUrl LONGTEXT ");
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		db.configAllowTransaction(true);// 开启事务
		db.configDebug(false);// debug，输出sql语句
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.image_download_loading_icon)
				.showImageForEmptyUri(R.drawable.image_download_loading_icon)
				.showImageOnFail(R.drawable.image_download_fail_icon)
				.cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(10))//圆角
				.build();
		adapter = new MomentsAdapter(mContext, isGetMyActive, mobile,
				options);
		// 设置回调函数
		setCallBack();
		listview.setAdapter(adapter);
		
		
		if(SingletonSharedPreferences.getInstance().getHasUploadContacts()){//已经上传通讯录
			actionAfterUpload();
		}else {//没有上传
			CustomProgressDialogUtil.startProgressDialog(
					mContext, "正在初始化数据...",false);
			new UploadContractTask().execute();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.setActiveWithCommentsList(null);
	}

	private void actionAfterUpload(){
		new LoadDataFromDBTask().execute();// 加载数据库数据
		
		new AsyncNetworkManager().getActiveTimeLine(mContext, mobile,
				isGetMyActive ? 1 : 0, null, null,
				new getActiveTimeLineCallBack() {

					@Override
					public void onGetActiveTimeLineCallBack(int rtn,
							ArrayList<ActiveWithComments> activeList) {
						switch (rtn) {
						case 1:// 成功 ------>处理取回的数据
							new SaveDataToDBTask().execute(activeList);
							break;
						case 0://取数据失败
						default:
							stopRefresh();
							listview.onRefreshComplete();
							break;
						}
					}
				});
	}
	
	class UploadContractTask extends AsyncTask<Void, Void, List<ContractInfo>> {

		@Override
		protected List<ContractInfo> doInBackground(Void... params) {
			final List<ContractInfo> contractInfos = JUtil
					.getPhoneAndSimContracts(mContext);
			return contractInfos;
		}

		@Override
		protected void onPostExecute(final List<ContractInfo> result) {
			super.onPostExecute(result);
			if (result == null || result.size() == 0) {
				if(CustomProgressDialogUtil.stopProgressDialog()){
//					Toast.makeText(mContext, "初始化失败！", Toast.LENGTH_LONG).show();//没有
					SingletonSharedPreferences.getInstance().setHasUploadContacts(true);// 通讯录为空
					actionAfterUpload();
				}
				return;
			}
			// 上传到服务器
			AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
			asyncNetworkManager.getMatchFriendsFromContacts(mContext, mobile,
					result, new AsyncNetworkManager.MatchFriendsFromContacts() {
						@Override
						public void MatchFriendsFromContactsCallBack(int rtn) {
							SingletonSharedPreferences.getInstance()
										.setHasUploadContacts(true);// 已经上传
							// 从服务器下载
							AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
							asyncNetworkManager.getFriendsFromByPhone(mContext,
									mobile, 
									new AsyncNetworkManager.IGetUserFriends() {
										@Override
										public void IGetUserFriendsCallBack(
												int rtn,
												List<UserFriend> friends) {
											if (rtn == 1) {
												new UserFriendUtil.SaveUserFriend()
														.execute(mContext,
																mobile, result,
																friends);
											}
											if (CustomProgressDialogUtil
													.stopProgressDialog()) {
												actionAfterUpload();
											}
										}
									});
						}
					});
		}
	}


	/**
	 * 设置回调函数
	 */
	private void setCallBack() {
		adapter.setCallBack(new ClickCallBack() {// 点赞后更新数据库

			@Override
			public void onClickLike(int activeId, int praiseNum,
					String praisers, boolean praiseByMe) {
				Active active = new Active();
				active.setPraiseNum(praiseNum);
				active.setPraisers(praisers);
				active.setPraiseByMe(praiseByMe);
				// 更新数据库
				try {
					db.update(active,
							WhereBuilder.b("activeId", "=", activeId),
							"praiseNum", "praisers", "praiseByMe");
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onClickComment(int activeId, String commentMobile,
					String commentContent, String commentNickName) {// 评论后更新数据库
			// Comments comment = new Comments();
			// comment.setActiveId(activeId);
			// comment.setCommentMobile(commentMobile);
			// comment.setCommentContent(commentContent);
			// comment.setCommentNickName(commentNickName);
			}
		});
	}

	/**
	 * 当服务器取数据失败时从数据库缓存加载
	 * 
	 * @author Sola
	 *
	 */
	private class LoadDataWhenServerFailedTask extends
			AsyncTask<String, Void, ArrayList<ActiveWithComments>> {

		@Override
		protected ArrayList<ActiveWithComments> doInBackground(String... params) {
			// 从数据库取数据
			ArrayList<ActiveWithComments> activeWithCommentsList = null;
			try {
				List<Active> dbModels;
				if (isGetMyActive) {// 获取我的动态
					dbModels = db.findAll(Selector
							.from(Active.class)
							.where(WhereBuilder.b("mobile", "=", mobile).and(
									"createTime", ">", params[0]))
							.orderBy("createTime", true)
							.limit(DEFAULT_NUM_OF_ACTIVE));
				} else {
					dbModels = db
							.findAll(Selector
									.from(Active.class)
									.where(WhereBuilder.b("createTime", ">",
											params[0]))
									.orderBy("createTime", true)
									.limit(DEFAULT_NUM_OF_ACTIVE));
				}
				if (dbModels == null || dbModels.size() == 0) {
					return null;
				}
				activeWithCommentsList = new ArrayList<ActiveWithComments>(
						dbModels.size());
				// 根据动态的条数来查询
				for (int i = 0; i < dbModels.size(); i++) {
					Active item = dbModels.get(i);
					ActiveWithComments info = new ActiveWithComments();
					info.setActive(item);
					List<Comments> commentsList = db.findAll(Selector
							.from(Comments.class)
							.where(WhereBuilder.b("activeId", "=",
									item.getActiveId())).orderBy("createTime"));
					info.setComments(commentsList);
					activeWithCommentsList.add(info);
				}
			} catch (DbException e1) {
				e1.printStackTrace();
			}
			return activeWithCommentsList;
		}

		@Override
		protected void onPostExecute(ArrayList<ActiveWithComments> result) {
			super.onPostExecute(result);
			if (result == null) {
				return;
			}
			adapter.setActiveWithCommentsList(result);
			listview.onRefreshComplete();
		}

	}

	/**
	 * 从数据库加载缓存数据
	 *
	 */
	private class LoadDataFromDBTask extends
			AsyncTask<Void, Void, ArrayList<ActiveWithComments>> {

		@Override
		protected ArrayList<ActiveWithComments> doInBackground(Void... params) {
			// 从数据库取数据
			ArrayList<ActiveWithComments> activeWithCommentsList = null;
			try {
				List<Active> dbModels;
				if (isGetMyActive) {// 获取我的动态
					dbModels = db.findAll(Selector.from(Active.class)
							.where(WhereBuilder.b("mobile", "=", mobile))
							.orderBy("createTime", true)
							.limit(DEFAULT_NUM_OF_ACTIVE));
				} else {
					dbModels = db.findAll(Selector.from(Active.class)
							.orderBy("createTime", true)
							.limit(DEFAULT_NUM_OF_ACTIVE));
				}
				if (dbModels == null || dbModels.size() == 0) {
					return null;
				}
				activeWithCommentsList = new ArrayList<ActiveWithComments>(
						dbModels.size());
				// 根据动态的条数来查询
				for (int i = 0; i < dbModels.size(); i++) {
					Active item = dbModels.get(i);
					ActiveWithComments info = new ActiveWithComments();
					info.setActive(item);
					List<Comments> commentsList = db.findAll(Selector
							.from(Comments.class)
							.where(WhereBuilder.b("activeId", "=",
									item.getActiveId())).orderBy("createTime"));
					info.setComments(commentsList);
					activeWithCommentsList.add(info);
				}
			} catch (DbException e1) {
				e1.printStackTrace();
			}
			return activeWithCommentsList;
		}

		@Override
		protected void onPostExecute(ArrayList<ActiveWithComments> result) {
			super.onPostExecute(result);
			listview.onRefreshComplete();
			if (result == null) {//本地数据库没有数据的话就出现刷新按钮
				isRefreshing = true;
				return;
			}
			adapter.setActiveWithCommentsList(result);
		}
	}

	/**
	 * 保存数据到数据库，并更新界面
	 */
	private class SaveDataToDBTask
			extends
			AsyncTask<ArrayList<ActiveWithComments>, Void, ArrayList<ActiveWithComments>> {

		@Override
		protected final ArrayList<ActiveWithComments> doInBackground(
				ArrayList<ActiveWithComments>... params) {
			for (ActiveWithComments item : params[0]) {
				if (item.getActive() != null) {// 确保有动态返回
					
					//把点赞的数据转成jsonArray存储
					JSONArray jsonArray=new JSONArray();
					for (Praisers praiser :item.getPraisers()) { //遍历上面初始化的集合数据，把数据加入JSONObject里面
						JSONObject object2 = new JSONObject();// 一个user对象，使用一个JSONObject对象来装
						try {
							object2.put("mobile", praiser.getMobile()); // 从集合取出数据，放入JSONObject里面
																		// JSONObject对象和map差不多用法,以键和值形式存储数据
							object2.put("nickName", praiser.getNickName());
							jsonArray.put(object2); // 把JSONObject对象装入jsonArray数组里面
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					item.getActive().setPraisers(jsonArray.toString()); //把JSONArray转换成json格式的字符串
					item.getActive().setPraiseByMe(item.isPraiseByMe());
					
//					for (Praisers praiser : item.getPraisers()) {// 保存到Activie的praisers字段
//						sb.append(praiser).append(",");
//					}
//					String subStr = sb.toString();
//					if (!TextUtils.isEmpty(subStr)) {
//						item.getActive().setPraisers(
//								subStr.substring(0, subStr.length() - 1));// 去掉最后一个（,）逗号
//					}
					
				}
				try {
					// db.saveBindingId(item.getActive());
					// db.saveBindingIdAll(item.getComments());
					db.saveOrUpdate(item.getActive());
					db.saveOrUpdateAll(item.getComments());
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// 如果返回的数据为空，看请求的是之前还是之后的数据，如果是之前的数据，就返回existing
			/*
			 * 分为4中情况，（0x11）代表现有数据: not null，返回数据: not null （0x10）代表现有数据:not
			 * null，返回数据：null（0x01）代表现有数据:null，返回数据：not null
			 * （0x00）代表现有数据：null，返回数据：null
			 */
			byte state = 0x00;
			if (params[0] == null || params[0].size() == 0
					|| params[0].get(0).getActive() == null) {
				state = (byte) (state | 0x00);
			} else {
				state = (byte) (state | 0x01);
			}

			// 插到现有list的前段或者后端
			ArrayList<ActiveWithComments> existingActiveWithComments = adapter
					.getActiveWithComments();
			if (existingActiveWithComments == null
					|| existingActiveWithComments.size() == 0
					|| existingActiveWithComments.get(0).getActive() == null) {
				state = (byte) (state | 0x00);
			} else {
				state = (byte) (state | 0x10);
			}

			switch (state) {
			case 0x00:// 返回null
			default:
				return null;
			case 0x01:// 现有为空，返回不为空
				return params[0];
			case 0x10:// 现有不为空，返回为空，返回现有的
				return existingActiveWithComments;
			case 0x11://现有不为空，返回不为空
				String serviceFirstData = params[0].get(0).getActive()
						.getCreateTime();
				String localFirstData = existingActiveWithComments.get(0)
						.getActive().getCreateTime();
				// 比较时间的前后
				int j = 0;
				try {
					j = compareDate(localFirstData, serviceFirstData);// 比较生效日与终止日
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (j < 0) {// 本地动态时间比较早，旧，新的加在前面
					existingActiveWithComments.addAll(0, params[0]);
				} else if (j > 0) {// 本地动态比较新，获取的是旧的
					existingActiveWithComments.addAll(
							existingActiveWithComments.size(), params[0]);
				} else { // == 0
					// do nothing
				}
				return existingActiveWithComments;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<ActiveWithComments> result) {
			super.onPostExecute(result);
			stopRefresh();
			adapter.setActiveWithCommentsList(result);
			listview.onRefreshComplete();
		}
	}
	
	/**
	 * 隐藏刷新的按钮
	 */
	private void stopRefresh(){
		if(isRefreshing ){//TODO 后面添加其他条件
			isRefreshing = false;
		}
	}
	
	/**
	 * 两个日期相比，> 0 ：dateEnd在dateStart之后 < 0：dateEnd在dateStart之前
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 * @throws ParseException
	 */
	public static int compareDate(String dateEnd, String dateStart)
			throws ParseException {
		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
		Date dateA = dateFormat.parse(dateEnd);
		Date dateB = dateFormat.parse(dateStart);
		int i = dateA.compareTo(dateB);
		return i;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if (requestCode == REQUEST_CODE_PICK_PHOTO ) {//选图片返回后
			ArrayList<PickedImage> pickedImages = intent.getParcelableArrayListExtra(CustomGalleryActivity.INTENT_IMG_PATH_ARRAY);
			if(pickedImages == null || pickedImages.size() == 0){
				return;
			}
			Intent inten = new Intent(mContext,MomentsPublishActivity.class);
			inten.putParcelableArrayListExtra(CustomGalleryActivity.INTENT_IMG_PATH_ARRAY, pickedImages);
			startActivityForResult(inten, REQUEST_CODE_SEND_ACTIVE);
			return;
//			ArrayList<ImageData> list = intent.getParcelableArrayListExtra(Intent.EXTRA_TEXT);
//			ArrayList<ImagePath> lstPath = new ArrayList<ImagePath>();
//			for (ImageData obj : list) {
//				String uriPath = "content://media/external/images/media/" + Long.toString(obj.getId());
//				
//				Options op = new Options();
//				op.inSampleSize = 4;
//				Bitmap bm = BitmapFactory.decodeFile(obj.getPath(),op);
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); 
//				byte[] b = baos.toByteArray(); 
//				String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);				
//				
//				ImagePath oPath = new ImagePath(obj.getId(), uriPath, obj.getPath(), encodedImage);
//				lstPath.add(oPath);
//			}
//			
//			Type typeImagePath = new TypeToken<ArrayList<ImagePath>>() {}.getType();
//			String sJSON= new com.google.gson.Gson().toJson(lstPath, typeImagePath);
//			
//			
//			myWebView.loadUrl("javascript:KitKatWebviewChooseImgResult(" + sJSON + ")");
		}
		if(requestCode == REQUEST_CODE_SEND_ACTIVE ){//发动态后的结果
			boolean result = intent.getBooleanExtra(MomentsPublishActivity.INTENT_PUBLISH_ACTIVE_FOR_RESULT, false);
			if(result){
				Active active = intent.getParcelableExtra(MomentsPublishActivity.INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE);
				if(active == null){
					//取最新数据
					String timeStamp = null;
					String timeDir = null;
					if (adapter.getActiveWithComments() == null
							|| adapter.getActiveWithComments().size() == 0
							|| adapter.getActiveWithComments().get(0).getActive() == null) {
					} else {
						// 取第一个动态的时间戳
						timeStamp = adapter.getActiveWithComments().get(0)
								.getActive().getCreateTime();
						timeDir = "latter";
					}
					// 获取更多我的动态
					new AsyncNetworkManager().getActiveTimeLine(mContext, mobile,
							isGetMyActive ? 1 : 0, timeStamp, timeDir,
							new getActiveTimeLineCallBack() {

								@Override
								public void onGetActiveTimeLineCallBack(int rtn,
										ArrayList<ActiveWithComments> activeList) {
									switch (rtn) {
									case 1:// 成功 ------>处理取回的数据
										new SaveDataToDBTask().execute(activeList);
										break;
									case 0:
									default:
										listview.onRefreshComplete();
										break;
									}
								}
							});
				}else{//保存active，刷新界面
					try {
						db.saveOrUpdate(active);
					} catch (DbException e) {
						e.printStackTrace();
					}
					ActiveWithComments activeWithComments = new ActiveWithComments();
					activeWithComments.setActive(active);
					adapter.addFirstActiveWithCommentsList(activeWithComments);
				}
			}else{
				Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
			}
			//删除目录下图片缓存文件
			UtilFunc.delete(new File(APPConstant.IMAGE_CACHE));
		}
	}
}
