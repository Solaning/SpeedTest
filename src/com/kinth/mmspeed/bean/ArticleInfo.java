package com.kinth.mmspeed.bean;

/**
 * 消息中心
 * @author BJ
 */
public class ArticleInfo {
	
	private int id ;
	/**
	 * 新闻标题
	 */
	private String articleName;
	/**
	 * 发布人
	 */
	private String publisher;
	/**
	 * 内容摘要
	 */
	private String shortContent;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 标题图片
	 */
	private String titleURLString;
	/**
	 * 点击跳转网页链接
	 */
	private String clickURL;	
	
	private String showStartTime;
	
	private String showEndTime;
	
	private String clientHandle;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getArticleName() {
		return articleName;
	}
	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getShortContent() {
		return shortContent;
	}
	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}
	 
	public String getClickURL() {
		return clickURL;
	}
	public void setClickURL(String clickURL) {
		this.clickURL = clickURL;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTitleURLString() {
		return titleURLString;
	}
	public void setTitleURLString(String titleURLString) {
		this.titleURLString = titleURLString;
	}
	public String getShowStartTime() {
		return showStartTime;
	}
	public void setShowStartTime(String showStartTime) {
		this.showStartTime = showStartTime;
	}
	public String getShowEndTime() {
		return showEndTime;
	}
	public void setShowEndTime(String showEndTime) {
		this.showEndTime = showEndTime;
	}
	public String getClientHandle() {
		return clientHandle;
	}
	public void setClientHandle(String clientHandle) {
		this.clientHandle = clientHandle;
	}
	
}
