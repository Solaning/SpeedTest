package com.kinth.mmspeed.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 评论
 * @author Sola
 */
@Table(name = "Comment")
public class Comments {
	
	@Id(column = "commentId") // 如果主键没有命名名为id或_id的时，需要为主键添加此注解
    @NoAutoIncrement // int,long类型的id默认自增，不想使用自增时添加此注解
	private String commentId;
	
	@Column(column = "activeId")//??外键引用 TODO
	private int activeId;
	
	@Column(column = "commentContent")
	private String commentContent;
	
	@Column(column = "commentMobile")
	private String commentMobile;
	
	@Column(column = "nickName")
	private String nickName;
	
	@Column(column = "createTime")
	private String createTime;//(TimeStamp:“yy-MM-dd HH:mm:ss.m”)
	
	public int getActiveId() {
		return activeId;
	}
	public void setActiveId(int activeId) {
		this.activeId = activeId;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getCommentMobile() {
		return commentMobile;
	}
	public void setCommentMobile(String commentMobile) {
		this.commentMobile = commentMobile;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
}
