package com.kinth.mmspeed;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.adapter.RMGInviteMultiMemberAdapter;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.util.ApplicationController;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 流量共享一次邀请多个号码--群主
 * @author Sola
 *
 */
@ContentView(R.layout.activity_rmg_invite_multiple_flow_share_member)
public class RMGInviteMultipleFlowShareMemberActivity extends BaseActivity{
	public static final String INTENT_MAIN_PHONE = "INTENT_MAIN_PHONE";//邀请主账号
	public static final String INTENT_FAMILY_MEMBER_DETAIL = "INTENT_FAMILY_MEMBER_DETAIL";//需要邀请的号码详情
	
	private Context mContext;
	private String mobile;//主号
	private ArrayList<String> memberList;//已去掉本号
	private RMGInviteMultiMemberAdapter adapter;
	private SparseBooleanArray checkedMap;//已选中的位置
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_tittle)
	private TextView title;
	
	@ViewInject(R.id.btn_invite)
	private Button invite;
	
	@ViewInject(R.id.listView_rmg_invite_multiple_flow_share_member)
	private ListView inviteMultiMemberListview;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
		rightOutFinishAnimation();
	}
	
	@OnClick(R.id.btn_invite)
	public void fun_2(View v){//邀请开通
		final ArrayList<String> selectedList = new ArrayList<String>();
		for(int i = 0; i < memberList.size(); i++){
			if(checkedMap.get(i)){//该项是否勾选
				selectedList.add(memberList.get(i));
			}
		}
		if(selectedList == null || selectedList.size() == 0){//勾选项为0
			Toast.makeText(mContext, "已选中0个号码", Toast.LENGTH_LONG).show();
			return;
		}
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(getText(R.string.tips).toString());
		builder.setMessage("是否给所选号码发送邀请信息？");
		builder.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						inviteMultiMember(selectedList);
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	
	/**
	 * 发送邀请
	 */
	protected void inviteMultiMember(ArrayList<String> selectedList) {
		CustomProgressDialogUtil
				.startProgressDialog(mContext, "正在加载中...", true);// 菊花转
		HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
		params.put("mobile", mobile);// 主号
		StringBuffer sb = new StringBuffer();
		for (String inviteMobile : selectedList) {
			sb.append(inviteMobile).append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));//去掉最后一个逗号
		params.put("invitedMobiles", sb.toString());// 被邀请号
		params.put("serviceid", ServiceID.DO_RICH_MAN_TOGETHER.getValue());
		params.put("prodid", APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue());
		JsonObjectRequest req = new JsonObjectRequest(
				APPConstant.INVITE_MULTIPLE_FLOW_SHARE_MEMBER, new JSONObject(
						params),// 查询该号码开通RMG情况
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if (!CustomProgressDialogUtil.stopProgressDialog()) {// 取消菊花
							return;
						}
						int rtn = 0;
						try {
							rtn = response.getInt("rtn");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						switch (rtn) {
						case 0:// 查询失败
							Gson gson = new Gson();
							Type fooType = new TypeToken<List<String>>() {
							}.getType();
							List<String> failMobile = null;
							try {
								failMobile = gson.fromJson(
										response.getString("failMobile"),
										fooType);
							} catch (JsonSyntaxException | JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (failMobile == null || failMobile.size() == 0) {
								Toast.makeText(mContext, "邀请失败，请稍后再试",
										Toast.LENGTH_LONG).show();
							} else {
								StringBuffer sb = new StringBuffer();
								for (String failNumber : failMobile) {
									sb.append(failNumber).append(",");
								}
								sb.deleteCharAt(sb.lastIndexOf(","));
								Toast.makeText(mContext,
										"号码：" + sb.toString() + "邀请失败，请稍后再试",
										Toast.LENGTH_LONG).show();
							}
							break;
						case 1:// 成功
							Toast.makeText(mContext, "邀请成功", Toast.LENGTH_LONG)
									.show();
							break;
						default:
							break;
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (!CustomProgressDialogUtil.stopProgressDialog()) {// 取消菊花
							Toast.makeText(mContext, "邀请失败，请稍后再试", Toast.LENGTH_LONG).show();
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
		mContext = this;
		ViewUtils.inject(this);
		
		title.setText("邀请开通流量共享");
		mobile = getIntent().getStringExtra(INTENT_MAIN_PHONE);
		memberList = getIntent().getStringArrayListExtra(INTENT_FAMILY_MEMBER_DETAIL);
		
		if(memberList == null)
			return;
		checkedMap = new SparseBooleanArray();
		for (int i = 0; i < memberList.size(); i++) {// 拷贝一份作为已选列表
			checkedMap.put(i, true);
		}
		adapter = new RMGInviteMultiMemberAdapter(mContext, memberList, checkedMap);
		inviteMultiMemberListview.setAdapter(adapter);
	}
	
}
