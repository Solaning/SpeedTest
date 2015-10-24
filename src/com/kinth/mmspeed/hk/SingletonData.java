package com.kinth.mmspeed.hk;

import android.content.Context;

import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.RichManMember;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.util.JUtil;

/**
 * 
 * @author Sola
 * 
 */
public class SingletonData {
	private static SingletonData instance = new SingletonData();

	private RichManGroup richManGroupInfo;// 流量共享的群信息

	private SingletonData() {
	}

	public static SingletonData getInstance() {
		if (instance == null)
			instance = new SingletonData();
		return instance;
	}

	public RichManGroup getRichManGroupInfo() {
		return richManGroupInfo;
	}

	public void setRichManGroupInfo(RichManGroup richManGroupInfo) {
		this.richManGroupInfo = richManGroupInfo;
	}

	/**
	 * 测试数据
	 */
	private void addExtra() {
		for (int i = 0; i < 50; i++) {
			RichManMember object = new RichManMember();
			object.setCurrentmonthstate(true);
			object.setIsmainnumber(false);
			object.setLimit(1000 + i);
			object.setMobilenumber("16666+" + i);
			object.setNextmonthstate(false);
			object.setProdid(APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue());
			object.setUsedlimit(10000 + i);
			richManGroupInfo.getRichmanmembers().add(object);
		}
	}

	public void setAsMainAccount(Context mContext){
		UserAccount user = new UserAccount("13922201015", "杨文洲", "");
		String phoneString = "13922201015";
		UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
		MainAccountUtil.saveUserAccount(mContext, user);
		UserPhone userPhone = userPhoneDBService
				.getUserPhoneByPhone(phoneString);

		if (userPhone == null
				|| !userPhone.getPhoneStr().equals(
						phoneString)) {
			userPhoneDBService
					.insertOrReplaceDetetion(new UserPhone(
							phoneString, phoneString,
							""));
			JUtil.putSharePre(mContext,	APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE,
					phoneString);
		}
		MainAccountUtil.setHaveRegister(mContext);
	}
	
	/**
	 * 数据库测试数据
	 */
	public void initDataBase(Context mContext){
//		String [][] jj = new String[][]{{"15813314458","sola","yhk"},{"13922201015","文洲","群主"},{"13922200741","胜勇",""},{"18820095299","转赠","成员"},{"13560316889","转赠","成员"},
//				{"18826466610","转赠","成员"},{"13922201949","问题","测试"},{"14715004120","全球通","测试"},{"15013141414","神州行","测试"},{"13570900462","动感地带","测试"}
//				,{"13570908947","动感地带","测试"},{"13760851817","曹溢",""}};
		String [][] jj = new String[][]{{"13922201015","文洲","文洲"}};
		UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
		for(int i = 0;i<jj.length;i++){
			userPhoneDBService.insertOrReplaceDetetion(new UserPhone(
					jj[i][0], jj[i][1], jj[i][2]));
		}
		
		// 如果用户手机号码不存在，则第一次绑定的号码作为用户的手机号码
		if (userPhoneDBService.getAllPhoneAmount() <= 0) {
			JUtil.putSharePre(
					mContext,
					APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE,
					jj[0][0]);
		}
	}
}
