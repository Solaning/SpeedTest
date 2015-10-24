package com.kinth.mmspeed.adapter;

import java.util.ArrayList;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 邀请多人开通流量共享
 * @author Sola
 *
 */
public class RMGInviteMultiMemberAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<String> memberList;
	private SparseBooleanArray checkMap;
	
	public RMGInviteMultiMemberAdapter(Context mContext,
			ArrayList<String> memberList,SparseBooleanArray checkMap) {
		super();
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
		this.memberList = memberList;
		this.checkMap = checkMap;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return memberList == null ? 0 : memberList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_rmg_invite_multi_member, parent,false);
		}
		TextView number = ViewHolder.get(convertView, R.id.tv_mobile);
		final CheckBox checkBox = ViewHolder.get(convertView, R.id.checkBox);
		number.setText(memberList.get(position));
		if(checkMap.get(position, true)){
			checkBox.setChecked(true);
		}else{
			checkBox.setChecked(false);
		}
		checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkBox.isChecked()) {// 如果选中
					checkMap.put(position, true);
				}else{
					checkMap.put(position, false);
				}
			}
		});
		return convertView;
	}

}
