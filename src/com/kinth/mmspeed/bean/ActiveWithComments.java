package com.kinth.mmspeed.bean;

import java.util.List;

/**
 * 动态和评论
 * 
 * @author Sola
 *
 */
public class ActiveWithComments {
	private Active active;//动态
	private List<Comments> comments;//评论
	private List<Praisers> praisers;//赞
	private boolean praiseByMe;//是否已赞

	public Active getActive() {
		return active;
	}

	public void setActive(Active active) {
		this.active = active;
	}

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	public boolean isPraiseByMe() {
		return praiseByMe;
	}

	public void setPraiseByMe(boolean praiseByMe) {
		this.praiseByMe = praiseByMe;
	}

	public List<Praisers> getPraisers() {
		return praisers;
	}

	public void setPraisers(List<Praisers> praisers) {
		this.praisers = praisers;
	}

}
