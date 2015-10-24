package com.kinth.mmspeed.singleton;



public class ShareSingleton {
	//参数1 statType : shareAboutus(关于我们),shareAppdownload(应用下载),shareSpeettest(测速后)，shareFreeflow(免流量专区)
	public static final String FROM_ABOUTUS = "shareAboutus";
	public static final String FROM_APP_DOWN_LOAD = "shareAppdownload";
	public static final String FROM_SPEED_TEST = "shareSpeettest";
	public static final String FROM_FREE_FLOW = "shareFreeflow";
	//参数2 keyValue : weixin (微信), weixingroup(微信朋友圈)，sinaweibo(新浪微博), tencentweibo(腾讯微博), sms (短信)
	public static final String TYPE_WEIXIN = "weixin";
	public static final String TYPE_WEIXIN_GROUP = "weixingroup";
	public static final String TYPE_SINA_WEIBO = "sinaweibo";
	public static final String TYPE_TENCENT_WEIBO = "tencentweibo";
	public static final String TYPE_SHORT_MSG = "sms";
	
	private static ShareSingleton shareSingleton;
	private String shareWhere = "";
	private String shareType = "";
	
	public static ShareSingleton getInstance() {
		 if (shareSingleton == null){
			  shareSingleton = new ShareSingleton();
		 }
		 return shareSingleton;
	}

	public String getShareWhere() {
		return shareWhere;
	}

	public void setShareWhere(String shareWhere) {
		this.shareWhere = shareWhere;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}
	
}
