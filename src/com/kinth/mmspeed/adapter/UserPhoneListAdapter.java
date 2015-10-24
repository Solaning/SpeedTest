package com.kinth.mmspeed.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinth.mmspeed.GM_ValidateVerificationCodeActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.util.JUtil;

public class UserPhoneListAdapter extends BaseAdapter{
	private  LayoutInflater mInflater;
	private Context context;
	private List<UserPhone> userPhones;
	private String userAcount;
	private ProgressDialog mProgressDialog = null;
	public UserPhoneListAdapter(Context context,List<UserPhone> userPhones,String userAcount) {
		super();
		this.userPhones = userPhones;
		this.context = context;
		this.userAcount = userAcount;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return userPhones.size();
	}
	@Override
	public Object getItem(int position) {
		return position;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_phones,
					parent, false);
			holder = new ViewHolder();
			holder.phoneteTextView = (TextView) convertView.findViewById(R.id.tv_item_phone);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.tvSetMain = (TextView) convertView.findViewById(R.id.tv_set_main);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (!userAcount.equals("")&&userPhones.get(position).getPhoneStr().equals(userAcount)) {
			holder.nameTextView.setTextColor(context.getResources().getColor(R.color.red));
			holder.phoneteTextView.setTextColor(context.getResources().getColor(R.color.red));
		}else {
			holder.nameTextView.setTextColor(context.getResources().getColor(R.color.black));
			holder.phoneteTextView.setTextColor(context.getResources().getColor(R.color.black));
		}
		
		holder.phoneteTextView.setText(userPhones.get(position).getPhoneStr()+"");
		holder.nameTextView.setText(userPhones.get(position).getNickName());
		
		holder.tvSetMain.setOnClickListener(new View.OnClickListener() {//设为主账号
			@Override
			public void onClick(View arg0) {
				mProgressDialog = JUtil.showDialog(context,"正在验证...");
				setUserAccount(context,userPhones.get(position).getPhoneStr()+"");
			}
		});
		return convertView;
	}
	
	public final class ViewHolder {
		public TextView phoneteTextView; //
		public TextView nameTextView; //
		public TextView tvSetMain;
	}
	
	private void setUserAccount(final Context context,final String phone) {//把某个号码设为主账号
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getSMSCode(context, phone, new AsyncNetworkManager.GetSMSCode() {
			@Override
			public void getSMSCallBack(int rtn) {
				JUtil.dismissDialog(mProgressDialog);
				if (rtn==1) {
					Intent intent = new Intent(context, GM_ValidateVerificationCodeActivity.class);
					intent.putExtra(GM_ValidateVerificationCodeActivity.INTENT_PHONE, phone);
					context.startActivity(intent);
				}else if (rtn==-2) {
					JUtil.showMsg(context, "获取验证码失败,请检查您的网络连接！");
				}else if (rtn==-3) {
					JUtil.showMsg(context, "获取验证码失败超时,请稍后重试！");
				}else {
					JUtil.showMsg(context, "获取验证码失败,请重新获取！");
				}
			}
		});
	}
}
