package com.kinth.mmspeed.adapter;

import java.util.List;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.constant.APPConstant.ServiceID;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * rmg群成员列表
 * 
 * @author Sola
 * 
 */
public class RMGMemberAdapter extends BaseAdapter {
	private Context mContext;
	private List<RichManMember> richManMemberList;
	private String mobile;// 群主的号码
	private long realDrawvalue;
	private ServiceID serviceId;//流量共享or转赠

	/**
	 * 
	 * @param mContext
	 * @param richManMemberList
	 * @param mobile
	 *            当前用户号码
	 * @param realDrawvalue
	 *            真实的流量值
	 */
	public RMGMemberAdapter(Context mContext,ServiceID serviceId,
			List<RichManMember> richManMemberList, String mobile,
			long realDrawvalue) {
		super();
		this.mContext = mContext;
		this.serviceId = serviceId;
		this.richManMemberList = richManMemberList;
		this.mobile = mobile;
		this.realDrawvalue = realDrawvalue;
	}

	@Override
	public int getCount() {
		if (richManMemberList == null)
			return 0;
		return richManMemberList.size();
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
					R.layout.item_rich_man_group_member, parent, false);
		}
		TextView serialNumber = ViewHolder.get(convertView,
				R.id.tv_rmg_serial_number);
		TextView phone = ViewHolder.get(convertView, R.id.tv_rmg_phone_number);
		TextView usedLimit = ViewHolder.get(convertView, R.id.tv_rmg_usedlimit);
		TextView limit = ViewHolder.get(convertView, R.id.tv_rmg_limit);

		serialNumber.setText((position + 1) + "");
		phone.setText(richManMemberList.get(position).getMobilenumber());
		usedLimit.setText(String.format("%.2f",richManMemberList.get(position).getUsedlimit()
				/ 1048576.0f) + "MB");

		if (richManMemberList.get(position).getIsmainnumber()) {// 群主
			if (richManMemberList.get(position).getLimit() < 0) {
				switch(serviceId){
				case DO_RICH_MAN_TOGETHER:
					limit.setText(realDrawvalue / 1048576 + "MB");
					break;
				case I_AM_RICH_MAN:
					limit.setText("无限额");
					break;
				case NOVALUE:
					limit.setText("---");
					break;
				}
			} else {//TODO
				limit.setText(String.format("%.2f",richManMemberList.get(position).getLimit()+0.0f) + "MB");
			}
		} else {// 成员
			if (richManMemberList.get(position).getLimit() < 0){//成员也不限额度，用满额的
				limit.setText("无限额");
//				limit.setText(String.format("%.2f", realDrawvalue / 1048576.0f) + "MB");
			}else{//成员有具体的限额，转赠的话不用保留小数
				limit.setText(richManMemberList.get(position).getLimit() + "MB");
			}
		}

		// 如果是当前用户的号码，标红
		if (richManMemberList.get(position).getMobilenumber().equals(mobile)) {
			serialNumber.setTextColor(mContext.getResources().getColor(
					R.color.red));
			phone.setTextColor(mContext.getResources().getColor(R.color.red));
			usedLimit.setTextColor(mContext.getResources()
					.getColor(R.color.red));
			limit.setTextColor(mContext.getResources().getColor(R.color.red));
		} else {
			serialNumber.setTextColor(mContext.getResources().getColor(
					R.color.black));
			phone.setTextColor(mContext.getResources().getColor(R.color.black));
			usedLimit.setTextColor(mContext.getResources().getColor(
					R.color.black));
			limit.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		return convertView;
	}

}
