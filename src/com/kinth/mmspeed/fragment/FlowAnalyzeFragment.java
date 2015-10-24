package com.kinth.mmspeed.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartGestureListener;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.adapter.AppFlowDetailAdapter;
import com.kinth.mmspeed.bean.AppFlowInfo;
import com.kinth.mmspeed.bean.FlowUseInfo;
import com.kinth.mmspeed.bj.FlowSingleton;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.DateUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 流量分析Fragment
 * @author Sola
 * 
 */
public class FlowAnalyzeFragment extends BaseFragment{
	private Context mContext;
	private AppFlowDetailAdapter adapter;
	private ArrayList<AppFlowInfo> appFlowDetailList;
	private DecimalFormat dFormat = new DecimalFormat("#0.00");  //流量的格式
	
	@ViewInject(R.id.tv_flow_used_that_day)
	private TextView flowUsedThatDay;
	
	@ViewInject(R.id.barChart)
	private BarChart mChart;
	
	@ViewInject(R.id.listview_app_used_flow_detail)
	private ListView appUsedFlowList;
	
	public static Fragment newInstance() {
		FlowAnalyzeFragment ff = new FlowAnalyzeFragment();
		return ff;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		new AsyncGetAppUsedFlowTask().execute();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flow_analyze_layout,
				container, false);
		ViewUtils.inject(this, view);
		
		mContext = getActivity();
		adapter = new AppFlowDetailAdapter(mContext, appFlowDetailList);
		appUsedFlowList.setAdapter(adapter);
		
		mChart.setDrawYValues(false);

        mChart.setDescription("");
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);
        
        // disable 3D
        mChart.set3DEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);
        
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawHorizontalGrid(false);
        mChart.setDrawGridBackground(false);

        XLabels xLabels = mChart.getXLabels();
        xLabels.setPosition(XLabelPosition.BOTTOM);
        xLabels.setCenterXLabelText(true);
        xLabels.setSpaceBetweenLabels(0);

        mChart.setDrawYLabels(false);
        mChart.setDrawLegend(false);
        // setting data
        setDate();
        mChart.setOnChartGestureListener(new OnChartGestureListener() {
			
			@Override
			public void onChartSingleTapped(MotionEvent me) {
			}
			
			@Override
			public void onChartLongPressed(MotionEvent me) {
			}
			
			@Override
			public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX,
					float velocityY) {
			}
			
			@Override
			public void onChartDoubleTapped(MotionEvent me) {
				
			}
		});
        
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			
			@Override
			public void onValueSelected(Entry e, int dataSetIndex) {
				// TODO Auto-generated method stub
				flowUsedThatDay.setText("当天使用流量："+(dFormat.format(e.getVal()/1024.0d))+"MB");
			}
			
			@Override
			public void onNothingSelected() {
				// TODO Auto-generated method stub
				
			}
		});
        // add a nice and smooth animation
        mChart.animateY(1500);
        
		return view;
	}

	private void setDate() {
		String currentPhone = ApplicationController.getInstance().getCurrentPhone();
		List<FlowUseInfo> usedFlowPerDay = FlowSingleton.getInstance().getSmsFlowInfo(currentPhone).getUsedFlowPerDay();
		if(usedFlowPerDay == null || usedFlowPerDay.size() == 0){
			Log.e("没有数据","没有数据");
			usedFlowPerDay = new ArrayList<FlowUseInfo>();
			return;
		}
		
		if(usedFlowPerDay.size() < 15){//少于15天则补齐日期15天 ,取第一天的日期
			int gap = 15 - usedFlowPerDay.size();
			List<FlowUseInfo> gapList = new ArrayList<FlowUseInfo>();
			Calendar calendar = Calendar.getInstance();
			for(int i = 0 ; i < gap; i++){
				FlowUseInfo item = new FlowUseInfo();
				Log.e("usedFlowPerDay.get(0).getStatDate()",""+usedFlowPerDay.get(0).getStatDate());
				calendar.setTime(DateUtil.strToDate(usedFlowPerDay.get(0).getStatDate()));
				calendar.add(Calendar.DAY_OF_YEAR, -(gap - i));
				item.setStatDate(DateUtil.dateToStrLong(calendar.getTime()));
				item.setUseFlow(0);
				gapList.add(item);
			}
			usedFlowPerDay.addAll(0,gapList);
		}

        ArrayList<String> xVals = new ArrayList<String>();//x轴的时间  低的在前
        for (int i = 0; i < usedFlowPerDay.size(); i++) {
            xVals.add(DateUtil.strToStrShort(usedFlowPerDay.get(i).getStatDate()));
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();//y轴的数据
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i < usedFlowPerDay.size(); i++) {
        	float flow = (float) usedFlowPerDay.get(i).getUseFlow();
            yVals1.add(new BarEntry(flow, i));
            if(flow < 102400){//100MB
            	colors.add(getResources().getColor(R.color.flow_green));
            }else if(flow < 307200){//300MB
            	colors.add(getResources().getColor(R.color.flow_yellow));
            }else{//>300MB
            	colors.add(getResources().getColor(R.color.flow_red));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setColors(colors);
        set1.setHighLightColor(getResources().getColor(R.color.flow_blue));//高亮色值
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        mChart.setData(data);
	}

	/**
	 * 获取应用使用的流量
	 */
	class AsyncGetAppUsedFlowTask extends AsyncTask<Void, Void, ArrayList<AppFlowInfo>>{

		@Override
		protected ArrayList<AppFlowInfo> doInBackground(Void... params) {
			ArrayList<AppFlowInfo> appList = new ArrayList<AppFlowInfo>(); //用来存储获取的应用信息数据
			List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(0);
			//取出已安装应用列表
			for(PackageInfo  packageInfo : packages) {
				if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){//系统程序
					continue;
				}
				AppFlowInfo tmpInfo =new AppFlowInfo();
				tmpInfo.setUid(packageInfo.applicationInfo.uid);
				 // Get traffic data
				long recived = TrafficStats.getUidRxBytes(tmpInfo.getUid());
			    if(TrafficStats.UNSUPPORTED  != recived){
			    	tmpInfo.setFlowRx(recived);
			    }else{
			    	tmpInfo.setFlowRx(0);
			    }
			    long send = TrafficStats.getUidTxBytes(tmpInfo.getUid());
			    if(TrafficStats.UNSUPPORTED  != send){
			    	tmpInfo.setFlowTx(send);
			    }else{
			    	tmpInfo.setFlowTx(0);
			    }
				tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
				tmpInfo.setPackageName(packageInfo.packageName);
				tmpInfo.setVersionName(packageInfo.versionName);
				tmpInfo.setVersionCode(packageInfo.versionCode);
				tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(mContext.getPackageManager()));
				appList.add(tmpInfo);
			}
			//排序
			Collections.sort(appList, new Comparator<AppFlowInfo>() {

				@Override
				public int compare(AppFlowInfo lhs, AppFlowInfo rhs) {
					return Long.valueOf(rhs.getFlowTx()).compareTo(
							Long.valueOf(lhs.getFlowTx()));
				}
			});
			return appList;
		}

		@Override
		protected void onPostExecute(ArrayList<AppFlowInfo> result) {
			super.onPostExecute(result);
			adapter.setAppFlowList(result);
			adapter.notifyDataSetChanged();
		}
		
	}
}
