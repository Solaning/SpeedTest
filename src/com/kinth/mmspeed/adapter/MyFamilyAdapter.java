package com.kinth.mmspeed.adapter;

import java.util.ArrayList;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.DHQLWMember;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 家庭网短号的适配器
 * @author Sola
 * 
 */
public class MyFamilyAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<DHQLWMember> memberList;

	public MyFamilyAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
	}

	public MyFamilyAdapter(Context mContext, ArrayList<DHQLWMember> memberList) {
		super();
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
		this.memberList = memberList;
	}

	public ArrayList<DHQLWMember> getMemberList() {
		return memberList;
	}

	public void setMemberList(ArrayList<DHQLWMember> memberList) {
		this.memberList = memberList;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_my_family, parent,false);
		}
		TextView shortNumber = ViewHolder.get(convertView,
				R.id.tv_shortNumber);
		TextView phone = ViewHolder.get(convertView,R.id.tv_number);
		TextView leftFlow = ViewHolder.get(convertView, R.id.tv_left_flow);
		TextView queryTime = ViewHolder.get(convertView,R.id.tv_query_time);
		
		shortNumber.setText(memberList.get(position).getShortNumber());
		phone.setText(memberList.get(position).getNumber());
		leftFlow.setText(String .format("%.2f", memberList.get(position).getLeftFlow() / 1024.0f)+"MB");
		queryTime.setText(memberList.get(position).getQueryTime());
		
		return convertView;
	}

}
