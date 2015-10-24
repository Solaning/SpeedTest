package com.kinth.mmspeed.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询流量的response bean
 * @author Sola
 *
 */
public class UserFlowInfo {
	private SMSTotalInfo smsTotalInfo; //用户的套餐总流量信息
	private List<SMSProdDetailInfo> smsProdDetailInfos; //套餐详情
	private List<SMSProdInfo> smsProdInfos; //套餐分类信息   国内2/3G  之类
	private List<FlowUseInfo> usedFlowPerDay;  //返回30天的流量使用数据
	private ArticleInfo articleInfo ;
	
	public ArticleInfo getArticleInfo() {
		return articleInfo;
	}
	public void setArticleInfo(ArticleInfo articleInfo) {
		this.articleInfo = articleInfo;
	}
	public SMSTotalInfo getSmsTotalInfo() {
		return smsTotalInfo;
	}
	public void setSmsTotalInfo(SMSTotalInfo smsTotalInfo) {
		this.smsTotalInfo = smsTotalInfo;
	}
	public List<SMSProdDetailInfo> getSmsProdDetailInfos() {
		return smsProdDetailInfos;
	}
	public void setSmsProdDetailInfos(List<SMSProdDetailInfo> smsProdDetailInfos) {
		this.smsProdDetailInfos = new ArrayList<SMSProdDetailInfo>();
		this.smsProdDetailInfos.addAll(smsProdDetailInfos);
	}
	public List<SMSProdInfo> getSmsProdInfos() {
		return smsProdInfos;
	}
	public void setSmsProdInfos(List<SMSProdInfo> smsProdInfos) {
		this.smsProdInfos =new ArrayList<SMSProdInfo>();
		this.smsProdInfos.addAll(smsProdInfos);
	}
	public List<FlowUseInfo> getUsedFlowPerDay() {
		return usedFlowPerDay;
	}
	public void setUsedFlowPerDay(List<FlowUseInfo> usedFlowPerDay) {
		this.usedFlowPerDay = usedFlowPerDay;
	}
	
 }
