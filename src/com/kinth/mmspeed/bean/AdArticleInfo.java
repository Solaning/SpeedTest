package com.kinth.mmspeed.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 套餐优惠
 * @author BJ
 */
@Table(name = "AdArticleInfo") 
public class AdArticleInfo implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//	@Id
	@NoAutoIncrement
//	@Column(column = "id")
	private int id ;
	/**
	 * 新闻标题
	 */
	@Column(column = "articleName")
	private String articleName;
	/**
	 * 发布人
	 */
	@Column(column = "publisher")
	private String publisher;
	/**
	 * 内容摘要
	 */
	@Column(column = "shortContent")
	private String shortContent;
	/**
	 * 创建时间
	 */
	@Column(column = "createTime")
	private String createTime;
	/**
	 * 标题图片
	 */
	@Column(column = "titleURLString")
	private String titleURLString;
	/**
	 * 点击跳转网页链接
	 */
	@Column(column = "clickURL")
	private String clickURL;
	
	@Column(column = "showStartTime")
	private String showStartTime;
	
	@Column(column = "showEndTime")
	private String showEndTime;
	
	@Column(column = "clientHandle")
	private String clientHandle;
	
	
	public AdArticleInfo() {
		super();
	}
	
	public AdArticleInfo(int id, String articleName, String publisher,
			String shortContent, String createTime, String titleURLString,
			String clickURL, String showStartTime, String showEndTime,
			String clientHandle) {
		super();
		this.id = id;
		this.articleName = articleName;
		this.publisher = publisher;
		this.shortContent = shortContent;
		this.createTime = createTime;
		this.titleURLString = titleURLString;
		this.clickURL = clickURL;
		this.showStartTime = showStartTime;
		this.showEndTime = showEndTime;
		this.clientHandle = clientHandle;
	}

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
