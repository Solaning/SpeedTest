package com.kinth.mmspeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.TransactPackageAdapter;
import com.kinth.mmspeed.bean.FlowPackageTypeInfo;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.ui.CheckableButton;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sola.code.ydlly_assist.bean.KeyValue;

/**
 * 套餐办理
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_transact_package)
public class TransactPackageActivity extends BaseActivity implements
		OnClickListener {
	
	private List<FlowPackageTypeInfo> flowPackageTypeInfos = new ArrayList<FlowPackageTypeInfo>();
	private Context mContext;
	private boolean hasCacheRefresh = false;// 缓存数据是否刷新成功
	
	@ViewInject(R.id.total_ui_layout)
	private RelativeLayout uiLayout;
	
	@ViewInject(R.id.cb_transact_package_tab1)
	private CheckableButton tabButton_1;

	@ViewInject(R.id.cb_transact_package_tab2)
	private CheckableButton tabButton_2;

	@ViewInject(R.id.cb_transact_package_tab3)
	private CheckableButton tabButton_3;

	@ViewInject(R.id.cb_transact_package_tab4)
	private CheckableButton tabButton_4;

	@ViewInject(R.id.tv_tip)
	private TextView tip;

	@ViewInject(R.id.tab3_package_listview)
	private ListView listview;

	private TextView description;

	@ViewInject(R.id.back)
	private ImageView back;

	@ViewInject(R.id.tv_app_recommend)
	private TextView appRecommend;
	
	@OnClick(R.id.back)
	public void fun_1(View v) {
		finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.tv_app_recommend)
	public void fun_2(View v){
		String url = SingletonSharedPreferences.getInstance().getMorePackageUrl();
		if(TextUtils.isEmpty(url)){
			return;
		}
		Intent intent = new Intent();
		try {
			intent.setData(Uri.parse(url));
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent); // 启动浏览器
		} catch (Exception e) {
		}
		rightInAnimation();
//		Intent intent = new Intent(mContext,CommonWebViewActivity.class);
//		intent.putExtra(CommonWebViewActivity.INTENT_URL, APPConstant.APP_10086);
//		startActivity(intent);
//		rightInAnimation();
	}
	
	/**
	 * 示例数据： [{"typeName":"月套餐","list":[{"price":"5元","flow":"国内30M","flowID":1,
	 * "wapURL":
	 * "http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_04.shtml","operationID":""},{"price":"10元","flow":"国内70M+广东省内30M","flowID":2,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_05.shtml","operationID":""},{"price":"20元","flow":"国内150M+广东省内150M","flowID":3,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_06.shtml","operationID":""},{"price":"30元","flow":"国内280M+广东省内220M","flowID":4,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_30.shtml","operationID":""},{"price":"50元","flow":"国内500M+广东省内500M","flowID":5,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_07.shtml","operationID":""},{"price":"80元","flow":"国内1024M+广东省内200M","flowID":6,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_80.shtml","operationID":""},{"price":"100元","flow":"国内2048M","flowID":7,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_08.shtml","operationID":""},{"price":"200元","flow":"国内5120M","flowID":8,"wapURL":"http:\/\/gd.10086.cn\/easy\/GPRS_FUNCTION_09.shtml","operationID":""}],"introduction":"(1)新开通套餐：全球通客户当月未开通套餐，办理套餐24小时内生效，修改、取消下月1日生效；动感地带和神州行客户当月未开通套餐，办理套餐扣费成功后24小时内生效，修改、取消套餐下一月结日生效；\r\n(2)旧套餐改新套餐办理规则：旧的手机流量优惠餐如修改为现有手机流量套餐，将无法办回原优惠。\r\n(3)套餐半月扣费说明：动感地带和神州行客户手机流量套餐半月扣费包含流量情况，请按各地移动公司标准执行。\r\n"},{"typeName":"叠加包","list":[{"price":"5元","flow":"国内30M","flowID":11,"wapURL":"http:\/\/gd.10086.cn\/easy\/GRPSDIEJIA_02.shtml","operationID":""},{"price":"10元","flow":"国内70M+广东省内30M","flowID":12,"wapURL":"http:\/\/gd.10086.cn\/easy\/GRPSDIEJIA_10.shtml","operationID":""},{"price":"20元","flow":"国内150M+广东省内150M","flowID":13,"wapURL":"http:\/\/gd.10086.cn\/easy\/GRPSDIEJIA_20.shtml","operationID":""}],"introduction":"(1)套餐生效规则：手机流量叠加套餐申请成功后立即扣费并生效，一旦生效，不能取消。并且没有折半原则，申请后按全额收取，并享受对应流量。\t\r\n(2)套餐使用说明：全球通客户的手机流量叠加套餐所包含的手机流量只可在当个自然月内使用，即使使用不完也不能留到下月使用。动感地带和神州行客户的手机流量叠加套餐所包含的手机流量只可在当个动态月内使用，即使使用不完也不能留到下一动态月使用。\r\n(3)重复办理规则：每档叠加套餐满足全球通客户在同一自然月内多次办理，动感地带和神州行客户在同一动态月内多次办理。"},{"typeName":"闲时套餐","list":[{"price":"10元","flow":"广东省内闲时2048M","flowID":9,"wapURL":"http:\/\/gd.10086.cn\/easy\/10GPRSXS.shtml","operationID":""}],"introduction":"(1)新开通套餐：全球通客户当月未开通套餐，办理套餐24小时内生效，修改、取消套餐下月1日生效；动感地带和神州行客户当月未开通套餐，办理套餐扣费成功后24小时内生效，修改、取消套餐下一月结日生效。\r\n(2)套餐折半规则：半月租半资源。首次办理手机流量闲时套餐，离下一动态月结日不足15天，套餐24小时内生效按半月租收取，送套餐的一半
	 * 流 量 ， 即 为 扣 取 5 元 ， 包 含 1 0 2 4 M 23: 00—8:
	 * 00省内的流量。\r\n(3)套餐互斥规则：10元夜间套餐（闲时）不可与5元流量闲时套餐同时办理，可与其他任一手机流量套餐叠加办理使用
	 * 。\r\n(4)有效规则：每日23：00-8：00的省内流量计入该套餐。"}]
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);

		View descriptionView = LayoutInflater.from(mContext).inflate(
				R.layout.tab3_package_description_view, null);
		description = (TextView) descriptionView
				.findViewById(R.id.tab3_package_description);
		listview.addFooterView(descriptionView);

		getDataAsync();
		LoadCacheDataTask loadTask = new LoadCacheDataTask();
		loadTask.execute();
	}

	/**
	 * 异步到服务器获取数据
	 */
	private void getDataAsync() {
		// Post params to be sent to the server  
		HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();  
		JsonObjectRequest req = new JsonObjectRequest(APPConstant.QUERY_FLOW_PACKAGE, new JSONObject(params),  
		       new Response.Listener<JSONObject>() {

				@Override
					public void onResponse(JSONObject response) {
						LoadNetworkDataTask networkDataTask = new LoadNetworkDataTask();
						networkDataTask.execute(response.toString());
//							VolleyLog.v("Response:%n %s", response.toString(4));
//							Log.e("response", "" + response.toString());
					}
		       }, new Response.ErrorListener() {
		           @Override  
		           public void onErrorResponse(VolleyError error) {  
		               VolleyLog.e("Error: ", error.getMessage());  
		               Log.e("response error",""+error.getMessage());
		           }
		       });  
		ApplicationController.getInstance().addToRequestQueue(req);
	}

	/**
	 * 异步处理网络下载的套餐数据
	 * 
	 * @author Sola
	 * 
	 */
	class LoadNetworkDataTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				// 解析
				JSONObject jsonObject = new JSONObject(params[0]);
				JSONArray jsonArray = jsonObject
						.getJSONArray("FlowPackageList");
				List<FlowPackageTypeInfo> temp = getPackageInfoListFromJson(jsonArray
						.toString());
				if (temp != null && temp.size() >= 3) {
					flowPackageTypeInfos = temp;
				}
				JUtil.putSharePre(mContext, APPConstant.SP_NAME,
						APPConstant.SP_FIELD_FLOW_PACKAGE, jsonArray.toString());
				//更多业务办理
				SingletonSharedPreferences.getInstance().setMorePackage(jsonObject.getString("morePackage"));
				SingletonSharedPreferences.getInstance().setMorePackageUrl(jsonObject.getString("morePackageUrl"));
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {// boolean没用到
			super.onPostExecute(result);
			// 解析成功
			if (result) {// 默认先加载tab1
				tabButton_1.setOnClickListener(TransactPackageActivity.this);
				tabButton_2.setOnClickListener(TransactPackageActivity.this);
				tabButton_3.setOnClickListener(TransactPackageActivity.this);
				appRecommend.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
				appRecommend.setText(SingletonSharedPreferences.getInstance().getMorePackage());
				
				tabButton_1.setChecked(true);
				switch(flowPackageTypeInfos.size()){//3个tab or 4个tab
				case 3:
					tabButton_1.setTextSize(18.0f);
					tabButton_2.setTextSize(18.0f);
					tabButton_3.setTextSize(18.0f);
					tabButton_1.setText(flowPackageTypeInfos.get(0).getTypeName());
					tabButton_2.setText(flowPackageTypeInfos.get(1).getTypeName());
					tabButton_3.setText(flowPackageTypeInfos.get(2).getTypeName());
					tabButton_4.setVisibility(View.GONE);
					break;
				case 4:
					tabButton_4.setOnClickListener(TransactPackageActivity.this);//加上监听
					tabButton_1.setTextSize(15.0f);
					tabButton_2.setTextSize(15.0f);
					tabButton_3.setTextSize(15.0f);
					tabButton_4.setTextSize(15.0f);
					tabButton_1.setText(flowPackageTypeInfos.get(0).getTypeName());
					tabButton_2.setText(flowPackageTypeInfos.get(1).getTypeName());
					tabButton_3.setText(flowPackageTypeInfos.get(2).getTypeName());
					tabButton_4.setVisibility(View.VISIBLE);
					tabButton_4.setText(flowPackageTypeInfos.get(3).getTypeName());
					break;
				}
				
				description.setText(flowPackageTypeInfos.get(0)
						.getIntroduction());
				TransactPackageAdapter adapter = new TransactPackageAdapter(
						mContext, flowPackageTypeInfos.get(0).getList());
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 >= flowPackageTypeInfos.get(0).getList()
								.size()) {
							return;
						}
						openUrlToBuy(arg2, 0);
					}
				});
				return;
			}
			// 服务器获取失败/解析失败
			if (hasCacheRefresh) {
				Toast.makeText(mContext, "获取最新数据失败，请稍后重试！", Toast.LENGTH_SHORT)
						.show();
			} else {
				tip.setVisibility(View.VISIBLE);
				tip.setText("获取数据失败，请检查网络连接");
			}
		}
	}

	/**
	 * 异步加载缓存数据
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			String string = JUtil.getSharePreStr(mContext, APPConstant.SP_NAME,
					APPConstant.SP_FIELD_FLOW_PACKAGE);// 从SP中取缓存数据
			List<FlowPackageTypeInfo> temp = getPackageInfoListFromJson(string);
			if (temp != null && temp.size() >= 3) {
				flowPackageTypeInfos = temp;
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				tabButton_1.setOnClickListener(TransactPackageActivity.this);
				tabButton_2.setOnClickListener(TransactPackageActivity.this);
				tabButton_3.setOnClickListener(TransactPackageActivity.this);
				appRecommend.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
				appRecommend.setText(SingletonSharedPreferences.getInstance().getMorePackage());
				
				tabButton_1.setChecked(true);
				hasCacheRefresh = true;// 缓存数据刷新成功
				switch(flowPackageTypeInfos.size()){//3个tab or 4个tab
				case 3:
					tabButton_1.setTextSize(18.0f);
					tabButton_2.setTextSize(18.0f);
					tabButton_3.setTextSize(18.0f);
					tabButton_1.setText(flowPackageTypeInfos.get(0).getTypeName());
					tabButton_2.setText(flowPackageTypeInfos.get(1).getTypeName());
					tabButton_3.setText(flowPackageTypeInfos.get(2).getTypeName());
					tabButton_4.setVisibility(View.GONE);
					break;
				case 4:
					tabButton_4.setOnClickListener(TransactPackageActivity.this);//加上监听
					tabButton_1.setTextSize(14.0f);
					tabButton_2.setTextSize(14.0f);
					tabButton_3.setTextSize(14.0f);
					tabButton_4.setTextSize(14.0f);
					tabButton_1.setText(flowPackageTypeInfos.get(0).getTypeName());
					tabButton_2.setText(flowPackageTypeInfos.get(1).getTypeName());
					tabButton_3.setText(flowPackageTypeInfos.get(2).getTypeName());
					tabButton_4.setVisibility(View.VISIBLE);
					tabButton_4.setText(flowPackageTypeInfos.get(3).getTypeName());
					break;
				}
				
				description.setText(flowPackageTypeInfos.get(0)
						.getIntroduction());
				TransactPackageAdapter adapter = new TransactPackageAdapter(
						mContext, flowPackageTypeInfos.get(0).getList());
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 >= flowPackageTypeInfos.get(0).getList()
								.size()) {
							return;
						}
						openUrlToBuy(arg2, 0);
					}
				});
			} else {
				hasCacheRefresh = false;
			}
		}
	}

	/**
	 * 解析json数据
	 * 
	 * @param jsonString
	 * @return
	 */
	public List<FlowPackageTypeInfo> getPackageInfoListFromJson(
			String jsonString) {
		List<FlowPackageTypeInfo> list = new ArrayList<FlowPackageTypeInfo>();
		Gson gson = new Gson();
		list = gson.fromJson(jsonString,
				new TypeToken<List<FlowPackageTypeInfo>>() {
				}.getType());
		return list;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cb_transact_package_tab1:// 分出2G/3G/4G
			tabButton_1.setChecked(true);
			tabButton_2.setChecked(false);
			tabButton_3.setChecked(false);
			tabButton_4.setChecked(false);
			if (flowPackageTypeInfos != null
					&& flowPackageTypeInfos.size() >= 3) {
				description.setText(flowPackageTypeInfos.get(0)
						.getIntroduction());
				TransactPackageAdapter adapter = new TransactPackageAdapter(
						mContext, flowPackageTypeInfos.get(0).getList());
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 >= flowPackageTypeInfos.get(0).getList()
								.size()) {
							return;
						}
						openUrlToBuy(arg2, 0);

					}
				});
			} else {
				Toast.makeText(mContext, "获取数据失败，请稍后重试！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.cb_transact_package_tab2:
			tabButton_1.setChecked(false);
			tabButton_2.setChecked(true);
			tabButton_3.setChecked(false);
			tabButton_4.setChecked(false);
			if (flowPackageTypeInfos != null
					&& flowPackageTypeInfos.size() >= 3) {
				description.setText(flowPackageTypeInfos.get(1)
						.getIntroduction());
				TransactPackageAdapter adapter = new TransactPackageAdapter(
						mContext, flowPackageTypeInfos.get(1).getList());
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 >= flowPackageTypeInfos.get(1).getList()
								.size()) {
							return;
						}
						openUrlToBuy(arg2, 1);
					}
				});
			} else {
				Toast.makeText(mContext, "获取数据失败，请稍后重试！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.cb_transact_package_tab3:
			tabButton_1.setChecked(false);
			tabButton_2.setChecked(false);
			tabButton_3.setChecked(true);
			tabButton_4.setChecked(false);
			if (flowPackageTypeInfos != null
					&& flowPackageTypeInfos.size() >= 3) {
				description.setText(flowPackageTypeInfos.get(2)
						.getIntroduction());
				TransactPackageAdapter adapter = new TransactPackageAdapter(
						mContext, flowPackageTypeInfos.get(2).getList());
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 >= flowPackageTypeInfos.get(2).getList()
								.size()) {
							return;
						}
						openUrlToBuy(arg2, 2);
					}
				});
			} else {
				Toast.makeText(mContext, "获取数据失败，请稍后重试！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.cb_transact_package_tab4:
			tabButton_1.setChecked(false);
			tabButton_2.setChecked(false);
			tabButton_3.setChecked(false);
			tabButton_4.setChecked(true);
			if (flowPackageTypeInfos != null
					&& flowPackageTypeInfos.size() >= 3) {
				description.setText(flowPackageTypeInfos.get(3)
						.getIntroduction());
				TransactPackageAdapter adapter = new TransactPackageAdapter(
						mContext, flowPackageTypeInfos.get(3).getList());
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (arg2 >= flowPackageTypeInfos.get(3).getList()
								.size()) {
							return;
						}
						openUrlToBuy(arg2, 3);
					}
				});
			} else {
				Toast.makeText(mContext, "获取数据失败，请稍后重试！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 打开URL，position代表列表中点击的位置，type代表是第几类
	 * 
	 * @param position
	 */
	private void openUrlToBuy(int position, int type) {
		//add @2014-08-13
		String postfix = "";// 链接后缀，手机号
		UserAccount userAccount = MainAccountUtil.getCurrentAccount(mContext);
		if (userAccount == null || TextUtils.isEmpty(userAccount.getMobile())) {// 没有号码查询绑定的当前号码
			UserPhoneDBService dbService = new UserPhoneDBService(mContext);
			ArrayList<String> mobiles = dbService.getAllPhoneNumber();
			if(mobiles != null && mobiles.size() != 0){
				postfix = "&mp=" + mobiles.get(0);
			}
		} else {
			postfix = "&mp=" + userAccount.getMobile();
		}
		
		Intent intent = new Intent(mContext, FlowWebActivity.class);
		intent.putExtra(FlowWebActivity.INTENT_URL,
				flowPackageTypeInfos.get(type).getList().get(position)
						.getWapURL()+postfix);
		startActivity(intent);
		rightInAnimation();
		
		ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
		keyValue.add(new KeyValue("statType","businessSubmit"));
		keyValue.add(new KeyValue("keyValue",flowPackageTypeInfos.get(type).getList().get(position)
				.getFlowID()+""));
		AsyncNetworkManager activeNetwork = new AsyncNetworkManager();
		activeNetwork.asynSendData(mContext, keyValue);// 统计反馈
	}
}
