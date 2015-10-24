package com.kinth.mmspeed.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.SMSProdDetailInfo;
import com.kinth.mmspeed.util.ViewHolder;

public class PackageDetailAdapter extends BaseAdapter {
	private Context context;
	private List<SMSProdDetailInfo> packageDetails;
	private DecimalFormat format = new DecimalFormat("#0.00");  //流量的格式
	
	public PackageDetailAdapter(Context context, List<SMSProdDetailInfo> details) {
		super();
		this.context = context;
		this.packageDetails = details;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return packageDetails.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.package_detail_listview_item, parent, false);
		}
		TextView nameView = ViewHolder.get(convertView, R.id.package_name_tv);
		nameView.setText(packageDetails.get(arg0).getProdname());
		TextView totalFlow = ViewHolder.get(convertView,R.id.package_total_flow);
		totalFlow.setText(format.format(packageDetails.get(arg0).getContain()/1024) + "MB");
		TextView leftFlow = ViewHolder.get(convertView, R.id.package_left_flow);
		leftFlow.setText(format.format(packageDetails.get(arg0).getRelay()/1024) + "MB");
		TextView usedFlow = ViewHolder.get(convertView, R.id.package_used_flow);
		usedFlow.setText(format.format(packageDetails.get(arg0).getUsed()/1024) + "MB");

		return convertView;
	}

}
