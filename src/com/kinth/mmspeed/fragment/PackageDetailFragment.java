package com.kinth.mmspeed.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.PackageDetailAdapter;
import com.kinth.mmspeed.bean.SMSProdDetailInfo;
import com.kinth.mmspeed.bean.UserFlowInfo;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.bj.FlowSingleton;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 套餐分类fragment
 * 
 * @author Sola
 * 
 */
public class PackageDetailFragment extends Fragment {
	private Context context;
//	private DbUtils mDbUtils;
	public static Fragment newInstance() {
		PackageDetailFragment ff = new PackageDetailFragment();
		return ff;
	}
	@ViewInject(R.id.package_detail_listview)
	private ListView listView;// 列表
	
	private String currentPhone;
//	private List<SMSProdDetail> packageList;// 套餐详情列表
	private List<SMSProdDetailInfo> smsProdDetailInfos;// 套餐详情列表
	private UserPhoneDBService userPhoneDBService;
	@Override
	public void onAttach(Activity activity) {
		context = getActivity();
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flow_detail, container, false);
		ViewUtils.inject(this, view);
		userPhoneDBService = new UserPhoneDBService(getActivity());
		userPhoneDBService.createTable();
		List<UserPhone>userPhones = userPhoneDBService.getAllUserPhones();
		
		if (userPhones!=null&&userPhones.size()>0) {// 已经有号码了
			currentPhone = JUtil.getSharePreStr(context, APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE);// 当前号码
			UserFlowInfo userFlowInfo= FlowSingleton.getInstance().getSmsFlowInfo(currentPhone);
			if (userFlowInfo!=null) {
				smsProdDetailInfos = userFlowInfo.getSmsProdDetailInfos();
				PackageDetailAdapter adapter = new PackageDetailAdapter(
						context, smsProdDetailInfos);
				listView.setAdapter(adapter);
			}
		}
		return view;
	}


	/**
	 * 异步处理加载套餐详情
	 */
	/*
	class AsyncPackageTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			String data = UtilFunc.getJson(Environment
					.getExternalStorageDirectory()
					+ File.separator
					+ "LiuLiangYi"
					+ File.separator
					+ "Data"
					+ File.separator
					+ currentPhone);
			if (data == null || data.equals("")) {
				return false;
			}
			packageList = getJsonData(data);
			if (packageList == null || packageList.size() == 0) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {// 有数据
				PackageDetailAdapter adapter = new PackageDetailAdapter(
						context, packageList);
				listView.setAdapter(adapter);
			} else {// 没有数据
				Toast.makeText(context, "暂时没有数据", Toast.LENGTH_LONG).show();
			}

		}

	}
	*/
	/**
	 * 解析json为对象
	 * 
	 * @return
	 */
	/*
	public ArrayList<SMSProdDetail> getJsonData(String jsonData) {
		ArrayList<SMSProdDetail> packageList = new ArrayList<SMSProdDetail>();
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONArray jsonArray = jsonObject.getJSONArray("SMSProdDetailList");
			double temp = 0;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				SMSProdDetail detail = new SMSProdDetail();
				detail.setProdName(json.optString("prodname"));// 名称
				temp = json.optDouble("contain") / 1024.0;
				detail.setContain((double) (Math.round(temp * 100)) / 100);// 总流量
				temp = json.optDouble("relay") / 1024.0;
				detail.setRelay((double) (Math.round(temp * 100)) / 100);
				temp = json.optDouble("used") / 1024.0;
				detail.setUsed((double) (Math.round(temp * 100)) / 100);
				packageList.add(detail);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return packageList;
	}
	*/
}
