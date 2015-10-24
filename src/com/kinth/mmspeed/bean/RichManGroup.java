package com.kinth.mmspeed.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 土豪群
 */
public class RichManGroup implements Serializable {
	private static final long serialVersionUID = 771053325973110150L;

	/**
	 * 是否已订购
	 */
	private boolean isorder;
	
	/**
	 * 本月状态
	 */
	private boolean currentmonthstate;

	/**
	 * 下个月状态
	 */
	private boolean nextmonthstate;

	/**
	 * 组内成员数量
	 */
	private int membercount;

	/**
	 * 当月剩余流量，单位byte
	 */
	private int leftamt;

	/**
	 * 本期限额总量单位byte
	 */
	private int drawvalue;

	/**
	 * 群主号
	 */
	private String mainmembermobile;

	/**
	 * 当前登录用户是否为群主号
	 */
	private boolean ismainmember;

	/**
	 * 成员列表
	 */
	private List<RichManMember> richmanmembers = new ArrayList<RichManMember>();

	public boolean getIsorder() {
		return isorder;
	}

	public void setIsorder(boolean isorder) {
		this.isorder = isorder;
	}

	public boolean getCurrentmonthstate() {
		return currentmonthstate;
	}

	public void setCurrentmonthstate(boolean currentmonthstate) {
		this.currentmonthstate = currentmonthstate;
	}

	public boolean getNextmonthstate() {
		return nextmonthstate;
	}

	public void setNextmonthstate(boolean nextmonthstate) {
		this.nextmonthstate = nextmonthstate;
	}

	public int getMembercount() {
		return membercount;
	}

	public void setMembercount(int membercount) {
		this.membercount = membercount;
	}

	public int getLeftamt() {
		return leftamt;
	}

	public void setLeftamt(int leftamt) {
		this.leftamt = leftamt;
	}

	public int getDrawvalue() {
		return drawvalue;
	}

	public void setDrawvalue(int drawvalue) {
		this.drawvalue = drawvalue;
	}

	public String getMainmembermobile() {
		return mainmembermobile;
	}

	public void setMainmembermobile(String mainmembermobile) {
		this.mainmembermobile = mainmembermobile;
	}

	public boolean getIsmainmember() {
		return ismainmember;
	}

	public void setIsmainmember(boolean ismainmember) {
		this.ismainmember = ismainmember;
	}

	public List<RichManMember> getRichmanmembers() {
		return richmanmembers;
	}

	public void setRichmanmembers(List<RichManMember> richmanmembers) {
		this.richmanmembers = richmanmembers;
	}

}