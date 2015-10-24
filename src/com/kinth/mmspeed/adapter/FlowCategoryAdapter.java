package com.kinth.mmspeed.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.FlowCategoryBean;

/**
 * 流量分类Adapter
 * 
 * @author Sola
 * 
 */
public class FlowCategoryAdapter extends BaseAdapter {
	// private static final int DIAL_NUMBER = 8;// 表盘的数量
	// private String[] DIAL_NAME_ARRAY =
	// SingletonFlowDetail.getDialNameArray();
//	private DecimalFormat format = new DecimalFormat("#0.00");  //流量的格式
	private Context mContext;
	private List<FlowCategoryBean> flowCategoryList;
	// 指针背景
	private int[] backgrounds = new int[] { R.drawable.half_point_bg_green,
			R.drawable.half_point_bg_orange, R.drawable.half_point_bg_red };

	/**
	 * @param mContext
	 * @param smsProdInfoList
	 */
	public FlowCategoryAdapter(Context mContext,
			List<FlowCategoryBean> flowCategoryList) {
		super();
		this.mContext = mContext;
		this.flowCategoryList = flowCategoryList;
	}

	@Override
	public int getCount() {
		return flowCategoryList.size();
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
		
		MyViewHolder viewHolder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_flow_category_gridview_item, parent, false);
			viewHolder = new MyViewHolder();
			viewHolder.lltMain = (LinearLayout) convertView
					.findViewById(R.id.ll_total_item);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tvPercent = (TextView) convertView
					.findViewById(R.id.tv_remain_percent);
			viewHolder.ivDialBg = (ImageView) convertView
					.findViewById(R.id.iv_dial_backgroud);
			viewHolder.ivPointerBg = (ImageView) convertView
					.findViewById(R.id.iv_dial_pointer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyViewHolder) convertView.getTag();
		}

		if (flowCategoryList.get(position).isValid()) {// 该套餐有数据
			if (position == 0) {
				viewHolder.lltMain.setBackgroundResource(R.drawable.grid_view_item_pressed_bg);
			} else {
				viewHolder.lltMain.setBackgroundResource(0);
			}
			viewHolder.tvTitle.setText(flowCategoryList.get(position).getProdType() + ":"
					+ flowCategoryList.get(position).getMonthAllFlow()/1024 + "MB");
			viewHolder.tvPercent.setText("本月已用："
					+ String.format("%.2f", flowCategoryList.get(position)
							.getRemainPercent()) + "%");
			// 转动指针，改变背景颜色
			float rotate = (float) flowCategoryList.get(position)
					.getRemainPercent();
			if (rotate < 70) {
				viewHolder.ivDialBg.setBackgroundResource(backgrounds[0]);
				viewHolder.ivPointerBg.setBackgroundResource(R.drawable.half_clock_neddle);
			} else if (70 <= rotate && rotate < 100) {
				viewHolder.ivDialBg.setBackgroundResource(backgrounds[1]);
				viewHolder.ivPointerBg.setBackgroundResource(R.drawable.half_clock_neddle);
			} else if (100 <= rotate) {
				viewHolder.ivDialBg.setBackgroundResource(backgrounds[2]);
				viewHolder.ivPointerBg.setBackgroundResource(R.drawable.half_clock_neddle);
			}
			rotate = rotate * 200 / 100;
			RotateAnimation rotateAnim = new RotateAnimation(0, rotate,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			viewHolder.ivPointerBg.setAnimation(rotateAnim);
			rotateAnim.setFillAfter(true);
		} else {// 没有数据
			viewHolder.tvTitle.setText(flowCategoryList.get(position).getProdType());
			viewHolder.tvPercent.setText("");
			viewHolder.ivDialBg.setBackgroundResource(backgrounds[0]);// 绿色背景
			viewHolder.ivPointerBg.setBackgroundResource(R.drawable.half_clock_neddle);
			RotateAnimation rotateAnim = new RotateAnimation(0, 0,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			viewHolder.ivPointerBg.setAnimation(rotateAnim);
			rotateAnim.setFillAfter(true);
		}
		return convertView;
	}
	private final class MyViewHolder {
		LinearLayout lltMain;
		TextView tvTitle;// 标题：套餐名和总量
		TextView tvPercent ;// 剩余百分比
		ImageView ivDialBg;// 表盘背景，改变
		ImageView ivPointerBg;// 表盘指针，旋转
	}
}
