package com.kinth.mmspeed.hk;

import java.util.ArrayList;
import java.util.List;

import com.kinth.mmspeed.bean.FlowCategoryBean;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.SMSProdInfo;
import com.kinth.mmspeed.bean.SMSTotalInfo;

/**
 * 流量详情的单例类
 * 
 * @author Sola
 * 
 */
public class SingletonFlowDetail {
	private static SingletonFlowDetail instance = new SingletonFlowDetail();

	private List<SMSProdInfo> smsProdInfoList;// 原始的流量详情list
	private List<FlowCategoryBean> structuredInfoList;// 规整后的详情list
	private SMSTotalInfo smsTotalInfo;// 套餐总量详情
	
	private SingletonFlowDetail() {
		smsProdInfoList = new ArrayList<SMSProdInfo>();// 初始化
	}

	public static SingletonFlowDetail getInstance() {
		if (instance == null)
			instance = new SingletonFlowDetail();
		return instance;
	}

	public List<SMSProdInfo> getSmsProdInfoList() {
		return smsProdInfoList;
	}

	/**
	 * 获取到流量套餐数据，规整
	 * 
	 * @param smsProdInfoList
	 */
	public void setSmsProdInfoList(List<SMSProdInfo> smsProdInfoList) {
		this.smsProdInfoList = smsProdInfoList;
		structList(this.smsProdInfoList);
	}

	/**
	 * 规整详情
	 */
	private void structList(List<SMSProdInfo> list) {
		// Map<String, SMSProdInfo> typeMapInfo = new HashMap<String,
		// SMSProdInfo>();// 类型和数据实体的对应关系
		// for (SMSProdInfo info : smsProdInfoList) {
		// typeMapInfo.put(info.getProdType(), info);
		// }
		if (structuredInfoList != null) {
			structuredInfoList.clear();
		}
		structuredInfoList = new ArrayList<FlowCategoryBean>();
		// 第0个一定是总流量，单独添加
		FlowCategoryBean bean0 = new FlowCategoryBean();
		bean0.setValid(true);
		bean0.setProdType("总流量");
		// 计算总流量、总剩余百分比
		bean0.setMonthAllFlow(smsTotalInfo.getResourcesCount());
		double percent0 = (smsTotalInfo.getUsedResCount()) / smsTotalInfo.getResourcesCount() * 100;
		bean0.setRemainPercent(percent0);
		this.structuredInfoList.add(bean0);
		
		for (int i = 0; i < list.size(); i++) {
			FlowCategoryBean bean = new FlowCategoryBean();
			bean.setValid(true);
			bean.setProdType(list.get(i).getProdType());
			bean.setMonthAllFlow(list.get(i).getMonthAllFlow());
			double percent = (list.get(i).getMonthAllFlow() - list.get(i)
					.getMonthRemainFlow())
					/ list.get(i).getMonthAllFlow()
					* 100;// 剩余百分比

			bean.setRemainPercent(percent);
			this.structuredInfoList.add(bean);
		}
		
		// 剩余7个
		// for (int i = 1; i < DIAL_NAME_ARRAY.length; i++) {
		// if (typeMapInfo.containsKey(DIAL_NAME_ARRAY[i])) {// 包含了该套餐类型
		// FlowCategoryBean bean = new FlowCategoryBean();
		// SMSProdInfo info = typeMapInfo.get(DIAL_NAME_ARRAY[i]);
		// bean.setValid(true);
		// bean.setProdType(DIAL_NAME_ARRAY[i]);
		// bean.setMonthAllFlow(info.getMonthAllFlow());
		// double percent = (info.getMonthAllFlow() - info
		// .getMonthRemainFlow()) / info.getMonthAllFlow() * 100;// 剩余百分比
		//
		// bean.setRemainPercent(percent);
		// this.structuredInfoList.add(bean);
		// } else {// 建一个空的数据结构
		// FlowCategoryBean bean = new FlowCategoryBean();
		// bean.setProdType(DIAL_NAME_ARRAY[i]);// 设置套餐类型
		// bean.setValid(false);// 表示一个空的套餐
		// this.structuredInfoList.add(bean);
		// }
		// }

	}

	/**
	 * 自动生成空数据项目，都为空
	 */
	public void setSmsProdInfoListAutoNull() {
		this.structuredInfoList = new ArrayList<FlowCategoryBean>();
//		for (int i = 0; i < DIAL_NAME_ARRAY.length; i++) {
//			FlowCategoryBean bean = new FlowCategoryBean();
//			bean.setValid(false);
//			bean.setProdType(DIAL_NAME_ARRAY[i]);
//			this.structuredInfoList.add(bean);
//		}
	}

	/**
	 * 获取规整后的详情，用于显示
	 * 
	 * @return
	 */
	public List<FlowCategoryBean> getStructuredInfoList() {
		if (structuredInfoList == null || structuredInfoList.size() == 0) {
			setSmsProdInfoListAutoNull();
		}
		return structuredInfoList;
	}

//	/**
//	 * 表盘标签名
//	 * 
//	 * @return
//	 */
//	public static String[] getDialNameArray() {
//		return DIAL_NAME_ARRAY;
//	}

	public SMSTotalInfo getSmsTotalInfo() {
		return smsTotalInfo;
	}

	public void setSmsTotalInfo(SMSTotalInfo smsTotalInfo) {
		this.smsTotalInfo = smsTotalInfo;
	}

}
