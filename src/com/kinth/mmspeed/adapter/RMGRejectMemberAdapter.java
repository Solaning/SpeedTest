package com.kinth.mmspeed.adapter;

import java.util.List;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.ChgRichManMemberCallBack;
import com.kinth.mmspeed.ui.CommomDialog;
import com.kinth.mmspeed.util.ViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 剔除成员的adapter
 * 
 * @author Sola
 * 
 */
public class RMGRejectMemberAdapter extends BaseAdapter {
	private Context mContext;
	private String mobile;// 群主的号码
	private List<RichManMember> richManMemberList;

	/**
	 * @param mContext
	 * @param richmanmembers
	 */
	public RMGRejectMemberAdapter(Context mContext, String currentPhone,
			List<RichManMember> richmanmembers) {
		super();
		this.mContext = mContext;
		this.mobile = currentPhone;
		this.richManMemberList = richmanmembers;
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
		final Button delete = ViewHolder.get(convertView, R.id.btn_rmg_operation);// 按钮操作
		final TextView state = ViewHolder.get(convertView,
				R.id.tv_rmg_operation_state);// 状态
		Log.e(""+position,""+richManMemberList.get(position).getMobilenumber());
		// 如果是群主号码
		if (richManMemberList.get(position).getMobilenumber().equals(mobile)) {
			phone.setText(richManMemberList.get(position).getMobilenumber()
					+ "（群主）");
			phone.setTextColor(mContext.getResources().getColor(R.color.red));
			delete.setVisibility(View.INVISIBLE);
			state.setVisibility(View.INVISIBLE);
		} else {
			if (richManMemberList.get(position).getNextmonthstate()) {// 下个月没有给踢的
				phone.setTextColor(mContext.getResources().getColor(
						R.color.black));
				delete.setVisibility(View.VISIBLE);
				state.setVisibility(View.GONE);
			} else {
				phone.setTextColor(mContext.getResources().getColor(
						R.color.black));
				delete.setVisibility(View.GONE);
				state.setVisibility(View.VISIBLE);
				state.setText("已移除");
			}
			phone.setText(richManMemberList.get(position).getMobilenumber());
		}

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 群组踢人
				CustomProgressDialogUtil.startProgressDialog(mContext,
						"正在处理中，请稍后...",true);
				// TODO
				new AsyncNetworkManager().chgRichManMember(mContext,
						APPConstant.ServiceID.I_AM_RICH_MAN.getValue(), mobile,
						2, null, null, richManMemberList.get(position)
								.getMobilenumber(), null,
						new ChgRichManMemberCallBack() {

							@Override
							public void onChgRichManMemberCallBack(int rtn,String error) {
								CustomProgressDialogUtil.stopProgressDialog();
								if (rtn == 0) {
									CommomDialog.Builder dialog = new CommomDialog.Builder(
											mContext);
									dialog.setTitle("提示");
									dialog.setMessage(error);
									dialog.setPositiveButton("确认",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog,
														int which) {
												}
									});
									dialog.create().show();
									return;
								}
								if (rtn == 1) {
									delete.setVisibility(View.GONE);
									state.setVisibility(View.VISIBLE);
									state.setText("已移除");
									richManMemberList.get(position).setNextmonthstate(false);
									Toast.makeText(mContext, "取消流量转赠成功，取消成功后该用户将不再获得您的转赠流量使用限额，下月1日失效。", Toast.LENGTH_LONG).show();
								}
							}
						});
			}
		});
		return convertView;
	}

}
