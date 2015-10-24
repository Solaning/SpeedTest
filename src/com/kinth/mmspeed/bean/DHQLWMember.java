package com.kinth.mmspeed.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 短号群聊网成员bean
 * @author Sola
 *
 */
@Table(name = "DHQLWMember")
public class DHQLWMember implements Parcelable{
	@Id
	@NoAutoIncrement
	@Column(column = "number")
	private String number;//手机号
	
	@Column(column = "shortNumber")
	private String shortNumber;//短号
	
	@Column(column = "serviceId")
	private String serviceId;//当月CRM业务标识,产品编码，为空表示未生效
	
	@Column(column = "nextServiceId")
	private String nextServiceId;//下月CRM业务标识,产品编码，为空表示未生效
	
	@Column(column = "region")
	private String region;//地区
	
	@Column(column = "leftFlow")
	private double leftFlow;//剩余流量
	
	@Column(column = "queryTime")
	private String queryTime;//剩余流量查询日期，如果为””，表示流量查询失败
	
	public static final Parcelable.Creator<DHQLWMember> CREATOR = new Creator<DHQLWMember>() {  
		@Override  
		public DHQLWMember createFromParcel(Parcel parcel) {
			DHQLWMember member = new DHQLWMember();
			member.number = parcel.readString();
			member.shortNumber = parcel.readString();
			member.serviceId = parcel.readString();
			member.nextServiceId = parcel.readString();
			member.region = parcel.readString();
			member.leftFlow = parcel.readDouble();
			member.queryTime = parcel.readString();
			return member;  
		}  
		
		@Override  
		public DHQLWMember[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new DHQLWMember[size];
		}  
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.number);
		dest.writeString(this.shortNumber);
		dest.writeString(this.serviceId);
		dest.writeString(this.nextServiceId);
		dest.writeString(this.region);
		dest.writeDouble(this.leftFlow);
		dest.writeString(this.queryTime);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getShortNumber() {
		return shortNumber;
	}

	public void setShortNumber(String shortNumber) {
		this.shortNumber = shortNumber;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getNextServiceId() {
		return nextServiceId;
	}

	public void setNextServiceId(String nextServiceId) {
		this.nextServiceId = nextServiceId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public double getLeftFlow() {
		return leftFlow;
	}

	public void setLeftFlow(double leftFlow) {
		this.leftFlow = leftFlow;
	}

	public String getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(String queryTime) {
		this.queryTime = queryTime;
	}
}
