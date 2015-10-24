package com.kinth.mmspeed.bean;

/**
 * 保存的测速线程信息
 * @author Sola
 *
 */
public class ThreadInfo {
	private Integer threadId;// 线程ID
	private boolean live;// 线程是否存活
	private int threadFailCount;//线程失败重连的次数

	/**
	 * @param threadId
	 * @param live
	 */
	public ThreadInfo(Integer threadId, boolean live) {
		super();
		this.threadId = threadId;
		this.live = live;
	}

	public Integer getThreadId() {
		return threadId;
	}

	public void setThreadId(Integer threadId) {
		this.threadId = threadId;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public int getThreadFailCount() {
		return threadFailCount;
	}

	public void setThreadFailCount(int threadFailCount) {
		this.threadFailCount = threadFailCount;
	}
	
	/**
	 * 自动累加1
	 * @return
	 */
	public int getThreadFailCountAutoPlus(){
		return ++threadFailCount;
	}

}
