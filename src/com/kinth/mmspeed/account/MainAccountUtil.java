package com.kinth.mmspeed.account;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.util.JUtil;

/**
 * 主账号
 * @author Sola
 *
 */
public class MainAccountUtil {
	
	/**
	 * 获取主账号
	 * @param mContext
	 * @return
	 */
	public static UserAccount getCurrentAccount(Context mContext) {
		UserAccount userAccount = null;
		if (mContext != null) {
			String phoneString = JUtil.getSharePreStr(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_MOBILE);
			if (!phoneString.equals("")) {
				userAccount = new UserAccount();
				userAccount.setMobile(phoneString);
				userAccount.setNickName(JUtil.getSharePreStr(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_NICKNAME));
				userAccount.setIconURL(JUtil.getSharePreStr(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_ICONURL));
				return userAccount;
			}else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 获取主账号的号码
	 * @param mContext
	 * @return
	 */
	public static String getMainAccountPhone(Context mContext){
		if(mContext == null)
			return "";
		return JUtil.getSharePreStr(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_MOBILE);
	}
	
	/**
	 * 保存主账号
	 * @param mContext
	 * @param userAccount
	 */
	public static void saveUserAccount(Context mContext,UserAccount userAccount) {
		if (userAccount!=null&&mContext!=null) {
			JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_MOBILE, userAccount.getMobile()+"");
			//设置昵称
			JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_NICKNAME, userAccount.getNickName()+"");
			if (TextUtils.isEmpty(userAccount.getIconURL())) {
				JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_ICONURL, userAccount.getIconURL()+"");
			}
		}
	}
	
	/**
	 * 删除主账号
	 * @param mContext
	 */
	public static void deleteUserAccount(Context mContext){
		UserAccount userAccount = getCurrentAccount(mContext);
		
		JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.SP_LAST_GETFRIEND_TIME+"_"+userAccount.getMobile()+"", "");
		JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.SP_LAST_FRIEND_MANAGE_TIME+"_"+userAccount.getMobile()+"", "");
		
		JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_MOBILE, "");
		//设置昵称
		JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_NICKNAME, "");
		JUtil.putSharePre(mContext, APPConstant.SP_NAME, APPConstant.ACCOUNT_ICONURL, "");
	}
	
	public static  void setHaveRegister(Context mContext) {
		JUtil.putShareBool(mContext, APPConstant.SP_NAME, APPConstant.HAVE_REGISTER, true);
	}
	
	/**
	 * 号码取流量仪主帐号号码或者无帐号时用号码绑定列表排行第一的号码
	 * @2014-09-12
	 */
	public static String getAvailablePhone(Context mContext) {
		String mainPhone = JUtil.getSharePreStr(mContext,APPConstant.SP_NAME, APPConstant.ACCOUNT_MOBILE);//主账号
		String currentPhone;
		if (!TextUtils.isEmpty(mainPhone)) {
			return mainPhone;
		}else if(!TextUtils.isEmpty(currentPhone = JUtil.getSharePreStr(mContext,APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE))){//当前查询流量那个号码
			return currentPhone;
		}else{
			UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
			List<UserPhone> userPhones = userPhoneDBService.getAllUserPhones();
			if (userPhones != null && userPhones.size() > 0) {
				return userPhones.get(0).getPhoneStr() + "";
			}else {
				return "";
			}
		}
	}
}
