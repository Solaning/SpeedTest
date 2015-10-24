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

/**
 * 选择号码作为主账号
 * @author Sola
 *
 */
public class SelectMainMobileListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context context;
	private List<UserPhone> userPhones;

	public SelectMainMobileListAdapter(Context context,
			List<UserPhone> userPhones) {
		super();
		this.userPhones = userPhones;
		this.context = context;
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
			convertView = mInflater.inflate(R.layout.item_phones1, parent,
					false);
			holder = new ViewHolder();
			holder.phoneteTextView = (TextView) convertView
					.findViewById(R.id.tv_item_phone);
			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.tv_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nameTextView.setTextColor(context.getResources().getColor(
				R.color.black));
		holder.phoneteTextView.setTextColor(context.getResources().getColor(
				R.color.black));

		holder.phoneteTextView.setText(userPhones.get(position).getPhoneStr()
				+ "");
		holder.nameTextView.setText(userPhones.get(position).getNickName());
		return convertView;
	}

	public final class ViewHolder {
		public TextView phoneteTextView; //
		public TextView nameTextView; //
	}
}
