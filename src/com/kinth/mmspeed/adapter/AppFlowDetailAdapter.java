package com.kinth.mmspeed.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.AppFlowInfo;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 每个应用使用流量的适配器
 * @author Sola
 * 
 */
public class AppFlowDetailAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<AppFlowInfo> appFlowList;//数据项
	private DecimalFormat dFormat = new DecimalFormat("#0.00");  //流量的格式

	public ArrayList<AppFlowInfo> getAppFlowList() {
		return appFlowList;
	}

	public void setAppFlowList(ArrayList<AppFlowInfo> appFlowList) {
		this.appFlowList = appFlowList;
	}

	public AppFlowDetailAdapter(Context mContext, ArrayList<AppFlowInfo> data) {
		super();
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
		this.appFlowList = data;
	}

	@Override
	public int getCount() {
		return appFlowList == null ? 0 : appFlowList.size();
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
					R.layout.item_app_used_flow_detail, parent,false);
		}
		ImageView appIcon = ViewHolder.get(convertView, R.id.iv_app_icon);//app 图标
		TextView  appName = ViewHolder.get(convertView, R.id.tv_app_name);//app 名称
		TextView flowUsedThatDay = ViewHolder.get(convertView, R.id.tv_used_flow_that_day);//app 当天使用的流量
		
		appIcon.setImageDrawable(appFlowList.get(position).getAppIcon());
		appName.setText(appFlowList.get(position).getAppName());
		flowUsedThatDay.setText("已使用流量：" + dFormat.format(appFlowList.get(position).getFlowTx()/(1024.0*1024.0d)) + "MB");
		return convertView;
	}

}
