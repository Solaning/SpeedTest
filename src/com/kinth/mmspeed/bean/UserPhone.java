package com.kinth.mmspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 已绑定号码的bean
 * @author Sola
 *
 */
@Table(name = "USERPHONE")
public class UserPhone implements Parcelable {
	
	@Id
	@NoAutoIncrement
	@Column(column = "phoneStr")
	private String phoneStr;//号码
	
	@Column(column = "nickName")
	private String nickName;//昵称
	
	@Column(column = "remark")
	private String remark;//备注
	
	public static final Parcelable.Creator<UserPhone> CREATOR = new Creator<UserPhone>() {  
		@Override  
		public UserPhone createFromParcel(Parcel parcel) {
			UserPhone user = new UserPhone();
			user.phoneStr = parcel.readString();
			user.nickName = parcel.readString();
			user.remark = parcel.readString();
			return user;  
		}  
		
		@Override  
		public UserPhone[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new UserPhone[size];
		}  
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.phoneStr);
		dest.writeString(this.nickName);
		dest.writeString(this.remark);
	}
	
	public UserPhone() {
		super();
	}
	
	public UserPhone(String phoneStr, String nickName, String remark) {
		super();
		this.phoneStr = phoneStr;
		this.nickName = nickName;
		this.remark = remark;
	}

	public String getPhoneStr() {
		return phoneStr;
	}

	public void setPhoneStr(String phoneStr) {
		this.phoneStr = phoneStr;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
