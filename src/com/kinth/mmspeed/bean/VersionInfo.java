package com.kinth.mmspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 版本升级实体
 * @author Sola
 *
 */
public class VersionInfo implements Parcelable {
	
	private String issueTime;
	private int versionNo;
	private String versionDesc;
	private String url;
	private String dev;
	private int isForce;//是否强制升级
	
	public static final Parcelable.Creator<VersionInfo> CREATOR = new Creator<VersionInfo>() {  
		@Override  
		public VersionInfo createFromParcel(Parcel parcel) {
			VersionInfo version = new VersionInfo();  
			version.issueTime = parcel.readString();  
			version.versionNo = parcel.readInt();  
			version.versionDesc = parcel.readString();
			version.url = parcel.readString();
			version.dev = parcel.readString();
			version.isForce = parcel.readInt();
			return version;  
		}  
		
		@Override  
		public VersionInfo[] newArray(int size) {  
		// TODO Auto-generated method stub  
		return new VersionInfo[size];  
		}  
	};

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}

	public String getDev() {
		return dev;
	}

	public void setDev(String dev) {
		this.dev = dev;
	}

	public String getIssueTime() {
		return issueTime;
	}

	public void setIssueTime(String issueTime) {
		this.issueTime = issueTime;
	}

	public int getIsForce() {
		return isForce;
	}

	public void setIsForce(int isForce) {
		this.isForce = isForce;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.issueTime);
		dest.writeInt(this.versionNo);
		dest.writeString(this.versionDesc);
		dest.writeString(this.url);
		dest.writeString(this.dev);
		dest.writeInt(this.isForce);
	}

}
