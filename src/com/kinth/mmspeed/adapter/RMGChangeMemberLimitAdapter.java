package com.kinth.mmspeed.adapter;

import java.util.List;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * 修改成员限额的adapter
 * 
 * @author Sola
 * 
 */
public class RMGChangeMemberLimitAdapter extends BaseAdapter {
	private Context mContext;
	private String mobile;// 群主的号码
	private List<RichManMember> richManMemberList;
	private onChangeMemberLimitCallback callback;

	/**
	 * @param mContext
	 * @param richmanmembers
	 */
	public RMGChangeMemberLimitAdapter(Context mContext, String currentPhone,
			List<RichManMember> richmanmembers,
			onChangeMemberLimitCallback callback) {
		super();
		this.mContext = mContext;
		this.mobile = currentPhone;
		this.richManMemberList = richmanmembers;
		this.callback = callback;
	}

	public interface onChangeMemberLimitCallback {
		public void onChangeMemberLimit(int position,String phone);
	}

	@Override
	public int getCount() {
		if (richManMemberList == null) {
			return 0;
		}
		return richManMemberList.size();// 去掉群主号
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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_rmg_reject_member, parent, false);
		}
		TextView phone = ViewHolder.get(convertView, R.id.tv_rmg_phone_number);
		Button setting = ViewHolder.get(convertView, R.id.btn_rmg_operation);
		TextView state = ViewHolder.get(convertView, R.id.tv_rmg_operation_state);
		setting.setText("设置限额");
		state.setVisibility(View.GONE);
		// 如果是群主号码
		if (richManMemberList.get(position).getMobilenumber().equals(mobile)) {
			phone.setText(richManMemberList.get(position).getMobilenumber()+"（群主）");
			phone.setTextColor(mContext.getResources().getColor(R.color.red));
			setting.setVisibility(View.INVISIBLE);
			state.setVisibility(View.GONE);
		} else {
			if (richManMemberList.get(position).getNextmonthstate()) {// 下个月没有给踢的
				phone.setTextColor(mContext.getResources().getColor(
						R.color.black));
				setting.setVisibility(View.VISIBLE);
				state.setVisibility(View.GONE);
			} else {
				phone.setTextColor(mContext.getResources().getColor(
						R.color.black));
				setting.setVisibility(View.GONE);
				state.setVisibility(View.VISIBLE);
				state.setText("已移除");
			}
			phone.setText(richManMemberList.get(position).getMobilenumber());
		}
		
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callback.onChangeMemberLimit(position,richManMemberList.get(position).getMobilenumber());
			}
		});
		return convertView;
	}

}
