package com.kinth.mmspeed.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.FlowCategoryAdapter;
import com.kinth.mmspeed.bean.SMSProdInfo;
import com.kinth.mmspeed.bean.SMSTotalInfo;
import com.kinth.mmspeed.bean.UserFlowInfo;
import com.kinth.mmspeed.bj.FlowSingleton;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.SingletonFlowDetail;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 流量分类Fragment
 * 
 * @author Sola
 * 
 */
public class FlowCategoryFragment extends Fragment {
	private FlowCategoryAdapter adapter;
	@ViewInject(R.id.gridview_flow_category)
	private GridView gridview; 
	
	public static Fragment newInstance() {
		FlowCategoryFragment ff = new FlowCategoryFragment();
		return ff;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flow_category_layout,
				container, false);
		ViewUtils.inject(this, view);
		
		//宏科原代码
//		adapter = new FlowCategoryAdapter(getActivity(),SingletonFlowDetail.getInstance().getStructuredInfoList());
//		gridview.setAdapter(adapter);
		//兼容  宏科的代码
		String currentPhone = JUtil.getSharePreStr(getActivity(), APPConstant.SP_NAME,
				APPConstant.SP_FIELD_CURRENT_PHONE);// 当前号码
		UserFlowInfo userFlowInfo = FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
		
		if (userFlowInfo!=null) {
			SMSTotalInfo smsTotalInfo = userFlowInfo.getSmsTotalInfo();
			if (smsTotalInfo!=null) {
				SingletonFlowDetail.getInstance().setSmsTotalInfo(smsTotalInfo);
			}
			List<SMSProdInfo> smsProdInfoList = userFlowInfo.getSmsProdInfos();
			if (smsProdInfoList!=null && smsProdInfoList.size()!=0) {
				SingletonFlowDetail.getInstance().setSmsProdInfoList(smsProdInfoList);
				adapter = new FlowCategoryAdapter(getActivity(),SingletonFlowDetail.getInstance().getStructuredInfoList());
				gridview.setAdapter(adapter);
			}
		}
		
		return view;
	}

}
