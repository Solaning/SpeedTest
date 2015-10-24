package com.kinth.mmspeed.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.FlowPackageInfo;
import com.kinth.mmspeed.util.ViewHolder;

/**
 * 套餐办理列表BaseAdapter 特别为了适配2G，3G，4G分开显示
 * 
 * @author Sola
 */
public class TransactPackageAdapter extends BaseAdapter {
	private Context context;
	private List<FlowPackageInfo> flowPackageList;

	public TransactPackageAdapter(Context context,List<FlowPackageInfo> list) {
		super();
		this.context = context;
		if (list != null && list.size() != 0) {
			this.flowPackageList = list;
		} else {
			this.flowPackageList = new ArrayList<FlowPackageInfo>();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return flowPackageList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.tab3_special_choice_info_item, parent, false);// 特殊为了区分2G/3G/4G
		}
		TextView packageType = ViewHolder.get(convertView,
				R.id.special_package_type_name);
		TextView flowName = ViewHolder.get(convertView,
				R.id.special_type_choice_info_name);
		TextView costTextView = ViewHolder.get(convertView,
				R.id.special_type_choice_info_cost);

		String showType = flowPackageList.get(position).getShowType();
		if (showType == null || "".equals(showType)) {//其实这个判断可以不要
			if (position == 0) {// 第一项
				packageType.setVisibility(View.VISIBLE);
				packageType.setText("套餐列表");
			} else {
				packageType.setVisibility(View.GONE);
			}
		} else {
			if (position == 0) {
				packageType.setVisibility(View.VISIBLE);
				packageType.setText(showType);
			} else {
				String pre_nick_name = flowPackageList.get(position - 1)
						.getShowType();//上一个
				if (pre_nick_name != null && !pre_nick_name.equals("")) {
					if (showType.equalsIgnoreCase(pre_nick_name)) {//跟上一个相对就隐藏
						packageType.setVisibility(View.GONE);
					} else {
						packageType.setVisibility(View.VISIBLE);//不等就显示
						packageType.setText(showType);
					}
				}
			}
		}
		flowName.setText(flowPackageList.get(position).getFlow());
		costTextView.setText(flowPackageList.get(position).getPrice());
		return convertView;
	}
}
