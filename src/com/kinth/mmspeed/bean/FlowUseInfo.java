package com.kinth.mmspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 每天使用的流量数据
 * @author Sola
 * 
 */
public class FlowUseInfo implements Parcelable{
	
	private String statDate;//统计时间
	private double useFlow;//使用了多少流量 KB

	public static final Parcelable.Creator<FlowUseInfo> CREATOR = new Creator<FlowUseInfo>() {  
		@Override  
		public FlowUseInfo createFromParcel(Parcel parcel) {
			FlowUseInfo dateFlow = new FlowUseInfo();
			dateFlow.statDate = parcel.readString();
			dateFlow.useFlow = parcel.readDouble();
			return dateFlow;  
		}  
		
		@Override  
		public FlowUseInfo[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new FlowUseInfo[size];
		}  
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.statDate);
		dest.writeDouble(this.useFlow);
	}

	public String getStatDate() {
		return statDate;
	}

	public void setStatDate(String date) {
		this.statDate = date;
	}

	public double getUseFlow() {
		return useFlow;
	}

	public void setUseFlow(double flow) {
		this.useFlow = flow;
	}

}
