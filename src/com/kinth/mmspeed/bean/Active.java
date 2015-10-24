package com.kinth.mmspeed.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 动态
 * 
 * @author Sola
 * 
 */
@Table(name = "Active")
public class Active implements Parcelable{

	public Active() {
		super();
		// TODO Auto-generated constructor stub
	}

	//主键
	@Id(column = "activeId") // 如果主键没有命名名为id或_id的时，需要为主键添加此注解
    @NoAutoIncrement // int,long类型的id默认自增，不想使用自增时添加此注解
	private int activeId;
	
	@Column(column = "mobile")
	private String mobile;
	
	@Column(column = "content")
	private String content;

	@Column(column = "activeType")
	private String activeType;

	@Column(column = "praiseNum")
	private int praiseNum;

	@Column(column = "createTime")
	private String createTime;// TimeStamp (:“yyyy-MM-dd HH:mm:ss”)
	
	@Column(column = "iconUrl")
	private String iconUrl;
	
	@Column(column = "nickName")
	private String nickName;
	
	@Column(column = "praisers")
	private String praisers;//对该动态点赞的人的昵称  是通过‘,’来进行分割存储的  TODO
	
	@Column(column = "praiseByMe")
	private boolean praiseByMe;//是否已经点赞
	
	//add @2014-08-29 发图片
	@Column(column = "picUrl")
	private String picUrl;
	
	public boolean isPraiseByMe() {
		return praiseByMe;
	}

	public void setPraiseByMe(boolean praiseByMe) {
		this.praiseByMe = praiseByMe;
	}

	public int getActiveId() {
		return activeId;
	}

	public String getPraisers() {
		return praisers;
	}

	public void setPraisers(String praisers) {
		this.praisers = praisers;
	}

	public void setActiveId(int activeId) {
		this.activeId = activeId;
	}

	public String getActiveType() {
		return activeType;
	}

	public void setActiveType(String activeType) {
		this.activeType = activeType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getPraiseNum() {
		return praiseNum;
	}

	public void setPraiseNum(int praiseNum) {
		this.praiseNum = praiseNum;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.activeId);
		dest.writeString(this.mobile);
		dest.writeString(this.content);
		dest.writeString(this.activeType);
		dest.writeInt(this.praiseNum);
		dest.writeString(this.createTime);
		dest.writeString(this.iconUrl);
		dest.writeString(this.nickName);
		dest.writeString(this.praisers);
		dest.writeByte((byte)(praiseByMe ? 1:0));
		dest.writeString(this.picUrl);
	}
	
	public static final Parcelable.Creator<Active> CREATOR = new Parcelable.Creator<Active>() {

		@Override
		public Active createFromParcel(Parcel parcel) {
			return new Active(parcel);
		}

		@Override
		public Active[] newArray(int size) {
			return new Active[size];
		}
	};
	
	public Active(Parcel parcel) {
		this.activeId = parcel.readInt();
		this.mobile = parcel.readString();
		this.content = parcel.readString();
		this.activeType = parcel.readString();
		this.praiseNum = parcel.readInt();
		this.createTime = parcel.readString();
		this.iconUrl = parcel.readString();
		this.nickName = parcel.readString();
		this.praisers = parcel.readString();
		this.praiseByMe = parcel.readByte()!=0;
		this.picUrl = parcel.readString();
	}
}
