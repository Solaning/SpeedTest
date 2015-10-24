package com.kinth.mmspeed.adapter;

import java.util.List;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SwipeAdapter extends BaseAdapter {
	private List<UserPhone> userPhones;
	/**
	 * 上下文对象
	 */
	private Context mContext = null;

	/**
     * 
     */
	private int mRightWidth = 0;

	/**
	 * 单击事件监听器
	 */
	private IOnItemRightClickListener mListener = null;

	/**
	 * 定义一个接口
	 * @author Sola
	 *
	 */
	public interface IOnItemRightClickListener {
		void onRightClick(View v, int position);
	}

	/**
	 * @param mainActivity
	 */
	public SwipeAdapter(Context ctx, int rightWidth,
			List<UserPhone> userPhones, IOnItemRightClickListener l) {
		mContext = ctx;
		mRightWidth = rightWidth;
		this.userPhones = userPhones;
		mListener = l;
	}

	public void setUserPhoneList(List<UserPhone> list){
		this.userPhones = list;
	}
	
	@Override
	public int getCount() {
		return userPhones.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_rmg_user_phone, parent, false);
		}
		RelativeLayout itemLeft = ViewHolder.get(convertView,
				R.id.item_left);
		RelativeLayout itemRight = ViewHolder.get(convertView,R.id.item_right);
		
		TextView phoneteTextView = ViewHolder.get(convertView,R.id.tv_item_phone);
		TextView nameTextView = ViewHolder.get(convertView,R.id.tv_item_name);
		
		TextView itemRightTxt = ViewHolder.get(convertView,R.id.item_right_txt);
		
		final int thisPosition = position;
		
		LinearLayout.LayoutParams lp1 = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		itemLeft.setLayoutParams(lp1);
		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
				LayoutParams.MATCH_PARENT);
		itemRight.setLayoutParams(lp2);
		
		phoneteTextView.setText(userPhones.get(position).getPhoneStr()+"");
		nameTextView.setText(userPhones.get(position).getNickName());
		itemRightTxt.setText("删除");
		itemRightTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightClick(v, thisPosition);
				}
			}
		});
		return convertView;
	}
}
