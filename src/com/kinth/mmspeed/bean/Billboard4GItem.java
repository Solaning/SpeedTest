package com.kinth.mmspeed.bean;

import java.util.ArrayList;
import java.util.List;

public class Billboard4GItem {
	private List<ContractInfo>friend4GList;
	private List<ContractInfo>recommendedFriend;
	private List<RankItem4G> prodRankList;//@2015-01-12
	
	public List<RankItem4G> getProdRankList() {
		return prodRankList;
	}
	
	public void setProdRankList(List<RankItem4G> prodRankList) {
		this.prodRankList = prodRankList;
	}
	
	public List<ContractInfo> getFriend4GList() {
		return friend4GList;
	}
	
	public void setFriend4GList(List<ContractInfo> friend4gList) {
		this.friend4GList = new ArrayList<ContractInfo>();
		this.friend4GList.addAll(friend4gList);
	}
	
	public List<ContractInfo> getRecommendedFriend() {
		return recommendedFriend;
	}
	
	public void setRecommendedFriend(List<ContractInfo> recommendedFriend) {
		this.recommendedFriend = new ArrayList<ContractInfo>();
		this.recommendedFriend.addAll(recommendedFriend);
	}
	
	public Billboard4GItem(List<ContractInfo> friend4gList, List<ContractInfo> recommendedFriend) {
		super();
		friend4GList = friend4gList;
		this.recommendedFriend = recommendedFriend;
	}
	
	public Billboard4GItem() {
		super();
	}

}
