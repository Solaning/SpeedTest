package com.kinth.mmspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 策反实体类
 * @author Sola
 *
 */
public class DefectRivalBean implements Parcelable{
	private String title;
	private String content;
	private String url;
	private int rate;//（例如：1就是一天显示一次，2就是两天显示一次）;
	
	public static final Parcelable.Creator<DefectRivalBean> CREATOR = new Creator<DefectRivalBean>() {  
		@Override  
		public DefectRivalBean createFromParcel(Parcel parcel) {
			DefectRivalBean defectRival = new DefectRivalBean();  
			defectRival.title = parcel.readString();  
			defectRival.content = parcel.readString();
			defectRival.url = parcel.readString();
			defectRival.rate = parcel.readInt();
			return defectRival;  
		}  
		
		@Override  
		public DefectRivalBean[] newArray(int size) {  
			// TODO Auto-generated method stub  
			return new DefectRivalBean[size];  
		}  
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.content);
		dest.writeString(this.url);
		dest.writeInt(this.rate);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

}
