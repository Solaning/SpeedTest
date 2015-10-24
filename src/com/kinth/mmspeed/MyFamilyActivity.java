package com.kinth.mmspeed;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.adapter.MyFamilyAdapter;
import com.kinth.mmspeed.bean.DHQLWMember;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.DateUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的家庭
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_my_family)
public class MyFamilyActivity extends BaseActivity{
	public static final int INTENT_MY_FAMILY_REQUEST_CODE = 9003;//通用的请求码 
	public static final String RESULT_CREAT_RMG_SUCCEED = "RESULT_CREAT_RMG_SUCCEED";//是否开通流量共享成功
	private Context mContext;
	private MyFamilyAdapter adapter;
	private ArrayList<DHQLWMember> memberList = new ArrayList<DHQLWMember>();
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_tittle)
	private TextView title;
	
	@ViewInject(R.id.listView_my_family_dhqlw)
	private ListView listView;
	
	@ViewInject(R.id.tv_one_key_dredge_rmg_flow_share)
	private TextView dredgeRMG;//一键开通家庭流量共享
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}

	@OnClick(R.id.tv_one_key_dredge_rmg_flow_share)
	public void fun_2(View v){//一键开通家庭流量共享
		final String phone = MainAccountUtil.getMainAccountPhone(mContext);
		if (TextUtils.isEmpty(phone)) {//没有主账号要先绑定主账号
//			UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
//			List<UserPhone> userPhones = userPhoneDBService.getAllUserPhones();//是否有绑定其他号码
//			if (userPhones != null && userPhones.size() > 0) {
//				Intent intent = new Intent(mContext, GM_SelectMobileAsMainAccountActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString(GM_SelectMobileAsMainAccountActivity.INTENT_TARGET_CLASS_NAME, .class.getName());//传递需要跳转的类名
//				intent.putExtras(bundle);
//				startActivity(intent);
//			}else {
//				Intent intent = new Intent(mContext, GM_LoginActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString(GM_LoginActivity.INTENT_TARGET_CLASS_NAME, .class.getName());
//				intent.putExtras(bundle);
//				startActivity(intent);
//			}
			Toast.makeText(mContext, "请先设置主账号", Toast.LENGTH_LONG).show();
			return;
		}
		if(memberList == null || memberList.size() == 0){
			Toast.makeText(mContext, "家庭网中无可邀请号码", Toast.LENGTH_LONG).show();
			return;
		}
		//有主账号，查询群组订购情况
		CustomProgressDialogUtil.startProgressDialog(mContext,
				"正在加载中...", true);// 菊花转
		// 查询该号码的状态
		HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
		params.put("mobile", phone);
		params.put("serviceid", ServiceID.DO_RICH_MAN_TOGETHER.getValue());
		JsonObjectRequest req = new JsonObjectRequest(
				APPConstant.QUERY_RMG_ORDER_STATUS, new JSONObject(params),//查询该号码开通RMG情况
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if(!CustomProgressDialogUtil
								.stopProgressDialog()){// 取消菊花
							return;
						}
						int rtn = 0;
						RichManGroup info = null;
						try {
							rtn = response.getInt("rtn");
							Gson gson = new Gson();
							Type fooType = new TypeToken<RichManGroup>() {}.getType();
							info = gson.fromJson(response.getString("RichManGroup"), fooType);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						switch (rtn) {
						case 0:// 查询失败
							Toast.makeText(mContext, "查询失败，请稍后再试",
									Toast.LENGTH_LONG).show();
							return;
						case 1:// 成功
							if (info == null) {
								Log.e("error", "RichManGroup为空");
								Toast.makeText(mContext, "查询失败，请稍后再试",Toast.LENGTH_LONG).show();
								return;
							}
							// 拿到群组的信息
							if (info.getIsmainmember()) {// 当前登录者为群主
								Intent intent = new Intent(mContext, RMGInviteMultipleFlowShareMemberActivity.class);
								intent.putExtra(RMGInviteMultipleFlowShareMemberActivity.INTENT_MAIN_PHONE, phone);
								ArrayList<String> phoneList = new ArrayList<String>();//单独取出电话
								for(DHQLWMember member : memberList){
									if(!phone.equals(member.getNumber())){
										phoneList.add(member.getNumber());
									}else{
										continue;
									}
								}
								intent.putExtra(RMGInviteMultipleFlowShareMemberActivity.INTENT_FAMILY_MEMBER_DETAIL, phoneList);
								startActivity(intent);
								rightInAnimation();
							} else {// 当前登录者非群主
								Toast.makeText(mContext, "当前号码:" + phone + "已经加入" + info.getMainmembermobile() + "的流量共享群，无法邀请", Toast.LENGTH_LONG).show();
							}
							return;
						case 2:// 未订购套餐 TODO
							Log.e("result", "未订购套餐");
							// 没有订购，前往开通流量共享页面
							Intent intent = new Intent(mContext,RMGConfirmTypeActivity.class);
							intent.putExtra(RMGConfirmTypeActivity.INTENT_TYPE, RMGConfirmTypeActivity.TYPE_CREATE_RICH_MAN_GROUP);
							intent.putExtra(RMGConfirmTypeActivity.INTENT_PHONE, phone);//电话号码
							intent.putExtra(RMGConfirmTypeActivity.INTENT_TARGET_CLASS_NAME, MyFamilyActivity.class.getName());
							startActivityForResult(intent,INTENT_MY_FAMILY_REQUEST_CODE);
							rightInAnimation();
							
							//TODO 测试
//							Intent intent = new Intent(mContext, RMGInviteMultipleFlowShareMemberActivity.class);
//							intent.putExtra(RMGInviteMultipleFlowShareMemberActivity.INTENT_MAIN_PHONE, phone);
//							ArrayList<String> phoneList = new ArrayList<String>();//单独取出电话
//							for(int i = 0;i<100;i++){
//								phoneList.add("15813314450");
//							}
//							intent.putExtra(RMGInviteMultipleFlowShareMemberActivity.INTENT_FAMILY_MEMBER_DETAIL, phoneList);
//							startActivity(intent);
//							rightInAnimation();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if(!CustomProgressDialogUtil
								.stopProgressDialog()){// 取消菊花
							return;
						}
						VolleyLog.e("Error: ", error.getMessage());
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);  
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mContext = this;
		
		title.setText(getResources().getString(R.string.text_my_family));
		dredgeRMG.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		dredgeRMG.setText("一键开通家庭流量共享");
		
		adapter = new MyFamilyAdapter(mContext);
		adapter.setMemberList(memberList);
		listView.setAdapter(adapter);
		
//		test();----------------
		//一周以内，从数据库拿数据
//		DbUtils db2 = DbUtils.create(mContext);
//		try {
//			List<DHQLWMember> DHQLWMemberList = db2.findAll(Selector.from(DHQLWMember.class));
//			if(memberList == null)
//				memberList = new ArrayList<DHQLWMember>();
//			memberList.addAll(DHQLWMemberList);
//			adapter.setMemberList(memberList);
//			adapter.notifyDataSetChanged();
//		} catch (DbException e) {
//			e.printStackTrace();
//			queryFamilyMemberDetail();//没有查询到记录
//		}
		//test-----------------
	
		long time = SingletonSharedPreferences.getInstance().getMyFamilyMemberQueryTime();
		if(DateUtil.daysBetween(time, System.currentTimeMillis()) <= 2){//一周以内，从数据库拿数据
			DbUtils db = DbUtils.create(mContext);
			try {
				List<DHQLWMember> DHQLWMemberList = db.findAll(Selector.from(DHQLWMember.class));
				if(DHQLWMemberList == null || DHQLWMemberList.size() == 0){
					queryFamilyMemberDetail();//没有本地记录
					return;
				}
				memberList.addAll(DHQLWMemberList);
				adapter.notifyDataSetChanged();
			} catch (DbException e) {
				e.printStackTrace();
				queryFamilyMemberDetail();//没有查询到记录
			}
		}else{
			queryFamilyMemberDetail();
		}
	}
	
	/**
	 * 查询家庭组成员的流量情况
	 */
	private void queryFamilyMemberDetail(){
		//一周以上，重新查询
		CustomProgressDialogUtil.startProgressDialog(
				mContext, "正在查询...", false);
		HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
		params.put("mobile", MainAccountUtil.getAvailablePhone(mContext));
		JsonObjectRequest req = new JsonObjectRequest(APPConstant.QUERY_DHQLW_MEMBERS, new JSONObject(params),  
		       new Response.Listener<JSONObject>() {

				@Override
					public void onResponse(JSONObject response) {
						if(!CustomProgressDialogUtil.stopProgressDialog()){
							return;
						}
						int rtn = 0;
						ArrayList<DHQLWMember> tempMemberList = null;
						try {
							rtn = response.getInt("rtn");
							Gson gson = new Gson();
							Type fooType = new TypeToken<ArrayList<DHQLWMember>>() {}.getType();
							tempMemberList = gson.fromJson(response
									.getJSONArray("memerList").toString(), fooType);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						switch (rtn) {
						case 0://没有成员
							Toast.makeText(mContext, "没有查询到家庭网成员数据", Toast.LENGTH_LONG).show();
							return;
						case 1://正常返回
							if (tempMemberList == null || tempMemberList.size() == 0){
								Toast.makeText(mContext, "没有记录", Toast.LENGTH_LONG).show();
								return;
							}
							SingletonSharedPreferences.getInstance().setMyFamilyMemberQueryTime(System.currentTimeMillis());//查询时间
							//自动把家庭网号码导入到已经绑定号码列表中
							insertMember2DB(mContext, tempMemberList);
							//把家庭网查询数据插入到数据库
							saveFamilyMemberDetail(mContext,tempMemberList);
							memberList = tempMemberList;
							adapter.notifyDataSetChanged();
							break;
						default:
							break;
						}
//						VolleyLog.v("Response:%n %s", response.toString(4));
//						Log.e("response", "" + response.toString());
					}
		       }, new Response.ErrorListener() {
		           @Override  
		           public void onErrorResponse(VolleyError error) {
		        	   if(!CustomProgressDialogUtil.stopProgressDialog()){
		        		   return;
		        	   }
		        	   Toast.makeText(mContext, "查询失败，请稍后再试", Toast.LENGTH_LONG).show();
		               VolleyLog.e("Error: ", error.getMessage());  
		           }  
		       });  
		ApplicationController.getInstance().addToRequestQueue(req);  
	}
	
	/**
	 * 保存家庭网的查询信息
	 * @param mContext2
	 * @param tempMemberList
	 */
	protected void saveFamilyMemberDetail(Context mContext2,
			ArrayList<DHQLWMember> tempMemberList) {
		DbUtils db = DbUtils.create(mContext2);
		try {
			db.saveOrUpdateAll(tempMemberList);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 把家庭网成员插入到本地数据库 TODO 代码规整
	 * @param tempMemberList
	 * @throws DbException
	 */
	private void insertMember2DB(Context mContext, ArrayList<DHQLWMember> tempMemberList) {
		DbUtils db = DbUtils.create(mContext, APPConstant.DATABASE_NAME);
		for(DHQLWMember member : tempMemberList){
			try {
				UserPhone userPhone = db.findById(UserPhone.class, member.getNumber());
				if(userPhone != null){
					continue;
				}else{//没有该记录，插入到数据库
					UserPhone newUser = new UserPhone();
					newUser.setPhoneStr(member.getNumber());
					newUser.setNickName(member.getShortNumber());
					db.saveOrUpdate(newUser);
				}
			} catch (DbException e) {
				e.printStackTrace();
				UserPhone newUser = new UserPhone();
				newUser.setPhoneStr(member.getNumber());
				newUser.setNickName(member.getShortNumber());
				try {
					db.saveOrUpdate(newUser);
				} catch (DbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { //TODO
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != INTENT_MY_FAMILY_REQUEST_CODE) {// 请求码不同，直接返回
			return;
		}
		switch (resultCode) { // resultCode为回传的标记，在B中回传的是RESULT_OK
		case RESULT_OK:
			boolean result = data
					.getBooleanExtra(RESULT_CREAT_RMG_SUCCEED, false);
			if(result){//开通成功
				fun_2(null);
			}else{//开通失败
				
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 测试数据
	 */
	@Deprecated
	private void test(){
		ArrayList<DHQLWMember> tempMemberList = new ArrayList<DHQLWMember>();
		for(int i=0;i<50;i++){
			DHQLWMember member = new DHQLWMember();
			member.setNumber(""+(1513314450+i));
			member.setShortNumber(""+(200+i));
			member.setServiceId(""+(300+i));
			member.setNextServiceId(""+(400+i));
			member.setRegion(""+(500+i));
			member.setLeftFlow(3000+i);
			member.setQueryTime(""+System.currentTimeMillis());
			tempMemberList.add(member);
		}
		saveFamilyMemberDetail(mContext,tempMemberList);
//		insertMember2DB(mContext,tempMemberList);
	}
}
