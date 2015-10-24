package com.kinth.mmspeed.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.UserPhone;

public class UserPhoneHaveMainListAdapter extends BaseAdapter{
	private  LayoutInflater mInflater;
	private Context context;
	private List<UserPhone> userPhones;
	private String userAcount;
	public UserPhoneHaveMainListAdapter(Context context,List<UserPhone> userPhones,String userAcount) {
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
			convertView = mInflater.inflate(R.layout.item_phones1,
					parent, false);
			holder = new ViewHolder();
			holder.phoneteTextView = (TextView) convertView.findViewById(R.id.tv_item_phone);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.tv_item_name);
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
		return convertView;
	}
	public final class ViewHolder {
		public TextView phoneteTextView; //
		public TextView nameTextView; //
	}
}
