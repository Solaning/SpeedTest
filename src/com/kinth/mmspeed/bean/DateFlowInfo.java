package com.kinth.mmspeed.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 某一天的流量数据总和
 * @author Sola
 */
@Deprecated
public class DateFlowInfo implements Parcelable{
	
	@Id
	@NoAutoIncrement
	@Column(column = "date")
	private String date;//日期
	
	@Column(column = "totalUsedFlow")
	private double totalUsedFlow;//该天使用的总流量
	
	@Column(column = "totalSaveFlow")
	private double totalSaveFlow;//该天节省的总流量
	
	public static final Parcelable.Creator<DateFlowInfo> CREATOR = new Creator<DateFlowInfo>() {  
		@Override  
		public DateFlowInfo createFromParcel(Parcel parcel) {
			DateFlowInfo dateFlow = new DateFlowInfo();
			dateFlow.date = parcel.readString();
			return dateFlow;  
		}  
		
		@Override  
		public DateFlowInfo[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new DateFlowInfo[size];
		}  
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
