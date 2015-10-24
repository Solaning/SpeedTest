package com.kinth.mmspeed.constant;

import java.io.File;

import android.os.Environment;

public class APPConstant {
	public static final String HOST = "http://42.121.0.186:8080/";//服务器地址  测试
	private static final String TEST_HOST = "http://192.168.1.202:8080/";//作旋测试服务器
	private static final String MY_HOST = "http://192.168.1.107:8080/";//Sola的pc地址
	public static final boolean ISDBUG = false;
	
	public static final String SP_NAME = "SP_SPEEDTEST";// SharedPreferences名字
	public static final String SPEEND_TEST_SHARED_PREFERENCES = "SPEEND_TEST_SHARED_PREFERENCES";// sharedPreferences名称

	//图片上传地址
	public static final String UPLOAD_URL = "http://42.121.194.76:8001/UploadFile.aspx";
	
	public static final String NETWORK_SETTING = "NETWORK_SETTING";
	public static final String LOCATION_SETTING = "LOCATION_SETTING";
	public static final String IDLE_SETTING = "IDLE_SETTING";
	public static final String SP_SHARE_FIRSTIN = "SP_FIRSTIN";// 月结日
	public static final String SP_IF_181_BEFORE_VERSION = "SP_IF_181_BEFORE_VERSION";// 月结日
	public static final String SP_IF_FIRST_BIND = "SP_IF_FIRST_BIND";
	
	public static final String SP_FIELD_ENDDATE = "SP_FIELD_ENDDATE";// 月结日
	public static final String SP_FIELD_ALLFLOW = "SP_FIELD_ALLFLOW";// 所有流量
	public static final String SP_FIELD_LEFTFLOW = "SP_FIELD_LEFTFLOW";// 剩余流量
	public static final String SP_FIELD_CLEAR_DATE = "SP_FIELD_CLEAR_DATE";
	public static final String SP_IF_FIRST_TIME = "SP_IF_FIRST_TIME";
	// TODO
	public static final String SP_FIELD_CURRENT_PHONE = "SP_FIELD_CURRENT_PHONE";// 当前查询流量的号码
	//主账号
	public static final String ACCOUNT_NICKNAME = "ACCOUNT_NAME";//主账号名称
	public static final String ACCOUNT_ICONURL = "ACCOUNT_ICONURL";//主账号头像url
	public static final String ACCOUNT_MOBILE = "ACCOUNT_MOBILE";//主账号号码
	
	public static final String SP_LAST_GETFRIEND_TIME = "SP_LAST_GETFRIEND_TIME";
	public static final String SP_LAST_FRIEND_MANAGE_TIME = "SP_LAST_FRIEND_MANAGE_TIME";
	
	
	public static final String FIELD_BIND_PASS = "FIELD_BIND_PASS";
	public static final String REGISTER_PASS = "REGISTER_PASS";
	
	public static final String SP_FIELD_SEND_DATA_FLAG = "SP_FIELD_SEND_DATA_FLAG";

	public static final String SP_FIELD_CLEARDATE = "SP_FIELD_CLEARDATE";
	public static final String SP_FIELD_FLOW_PACKAGE = "SP_FIELD_FLOW_PACKAGE";// 缓存在本地的套餐详情
	public static final String DATABASE_NAME = "SPEEDTEST.DB";
	//@2015-01-08
	public static final String ALL_BIND_PHONE_TABLE_NAME = "USERPHONE";//已绑定号码表

	public static final String DATABASE_FRIEND = "DATABASE_FRIEND.DB";
	
	public static final String SP_FIELD_GETPHONE_FLAG = "SP_FIELD_GETPHONE_FLAG";

	@Deprecated
	public static final String USER_PHONE = "SP_USER_PHONE";// 准备废弃 deprecation
															// 兼容之前版本的
	/**
	 * SHARE_STATISTICS 分享统计
	 */
	public static final String SHARE_STATISTICS = HOST + "MMService/services/user/dataStat.jsp";
	public static final String URL_GET_USERINFO = HOST + "MMService/services/account/getUserInfo.jsp";
	public static final String URL_RECOMAND_4G = HOST + "MMService/services/recommend4G/recommendFriend4G.jsp";
	
	//获取套餐办理的数据信息
	public static final String QUERY_FLOW_PACKAGE = HOST + "MMService/services/queryFlowPackage.jsp";
	public static final String SEND_DATA = HOST + "MMService/services/user/dataStat.jsp";
	public static final String SP_MAIN_TAB_POSITION = "SP_MAIN_TAB_POSITION";

	// ----add by sola at 5.27 -----
	public static final String FLOW_SHARE = "http://183.63.138.178:19001/nlpt/h5/flows!index.action?channelId=ydhl0000";// 流量共享网页版地址
	public static final String GET_RMG_MEMBER_MAX_COUNT = HOST + "MMService/services/GroupFlowShare/getMemberMaxCount.jsp";// 获取群组网最大成员数目
	public static final String QUERY_RMG_ORDER_STATUS = HOST + "MMService/services/GroupFlowShare/queryRichManGroup.jsp";// 查询群组订购情况
	public static final String CREATE_RICH_MAN_GROUP = HOST + "MMService/services/GroupFlowShare/createRichManGroup.jsp";// 创建群组
	public static final String CANCEL_RICH_MAN_GROUP = HOST + "MMService/services/GroupFlowShare/cancelRichManGroup.jsp";// 取消群组
	public static final String MANAGE_RMG_MEMBER = HOST + "MMService/services/GroupFlowShare/chgRichManMember.jsp";// 群组成员管理
	public static final String INVITE_RMG_MEMBER = HOST + "MMService/services/GroupFlowShare/inviteMember.jsp";// 邀请成员
	public static final String GET_SMS_VERIFY_CODE_COMMON = HOST + "MMService/services/sendSMSVerifyCodeCommon.jsp";// 通用的获取验证码
	
	//------add by sola @2014-07-28   朋友圈------
	public static final String GEN_ACTIVE_TIME_LINE = HOST + "MMService/services/active/genActiveTimeLine.jsp";//产生动态信息
	public static final String GET_ACTIVE_TIME_LINE = HOST + "MMService/services/active/getActiveTimeLine.jsp";//	获取动态
	public static final String LIKE_ACTIVE_TIME_LINE = HOST + "MMService/services/active/praiseActiveTimeLine.jsp";//对某个动态点赞
	public static final String COMMENT_ACTIVE_TIME_LINE = HOST + "MMService/services/active/commentActiveTimeLine.jsp";//对某个动态评论
	public static final String GET_ACTIVE_COMMENT = HOST + "MMService/services/active/getActiveComment.jsp";//获取某个动态的评论
	public static final String UPLOAD_TEST_RESULT = HOST + "MMService/services/user/speedTestDataStat.jsp";// 上传测速结果与经纬度信息
	
	//-------add by sola @2014-09-02 测速地图 ------
	public static final String MAP_GET_NEAR_SPEED = HOST + "MMService/services/map/getNearSpeed.jsp";//获取附近的网速
	//@2014-8-2
	public static final String _4GBUHUANKA = "http://h5.gmccopen.com/h5/portal!index.action?channelId=mobile_flow_instrument&id=4gyindao";//4G补换卡
	public static final String FLOW_RED_PACKETS = "http://t.cn/RvQuFvF";//流量红包
	public static final String IPHONE5S_5C = "http://h5.gmccopen.com/h5/mobile.action?channelId=mobile_flow_instrument";//iPhone5s/5c
	//@2014-08-16
	public static final String MESSAGE_CENTRE_HAS_NEW = HOST + "MMService/services/sys/checkIfHasNewFlowPreference.jsp";//查看消息中心是否有新消息
	public static final String MOMENTS_HAS_NEW = HOST + "MMService/services/active/checkIfHasNewActive.jsp";//查看是否有新的动态
	public static final String UPDATE_URL = HOST + "MMService/services/detectVersion.jsp";// 检测更新
	//@2014-10-30
	public static final String TEST_URL = MY_HOST + "SolaService/Sola";//sola的测试servlet
	//@2014-11-05
	public static final String DEFECT_RIVAL = HOST + "MMService/services/notice/pushNotice.jsp";//主动弹窗推送通知
	//@2014-11-11
	public static final String USER_FEEDBACK_URL = HOST + "MMService/services/user/userFeedback.jsp";// 用户反馈地址
	public static final String ACTIVE_DEVICE_URL = HOST + "MMService/services/user/activeDevice.jsp";// 设备激活
	public static final String LOGIN_URL = HOST + "MMService/services/user/login.jsp";// 登陆接口
	public static final String SERVER_URL = HOST + "MMService/services/querySpeedTestServerURL.jsp";// 服务器获取地址
	public static final String AD_CLICK_URL = HOST + "MMService/services/user/dataStat.jsp";// 点击广告的统计
	//@2014-09-11 
	@Deprecated
	public static final String APP_10086 = "http://gd.10086.cn/app";//app互推，10086app
	//@2014-09-12 
	public static final String URL_WITH_MOBILE_GMCC_OPEN = "h5.gmccopen.com";//带号码跳转，含有下面两个域名的链接添加 “&mobile=号码”
	public static final String URL_WITH_MOBILE_GDMOBILE = "gdmobi.com";
	//网络探针
	public static final String NETWORK_PROBE = "http://h5.gmccopen.com/h5/logprobe!record.action?channelId=mobile_flow_instrument&appName=mobile_flow_instrument&module=app&operation=launch";
	// 获取广告url @2014-11-10
	public static final String BANNER_AD_URL = HOST + "MMService/services/user/getBannerADList.jsp";
	//流量团购的网址
	public static final String FLOW_GROUPON_URL = "http://gd.10086.cn/zhushou/?c=flowgroup&a=rand&from=mobile_flow_instrument";
	
	//程序目录
	public static final String PROGRAM_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + File.separator + "LiuLiangYi";
	// 广告存放的目录
	public static final String AD_DIRECTORY = PROGRAM_DIRECTORY
			+ "/adCache";
	//图片缓存目录名称
	public static final String IMAGE_DIRECTORY = PROGRAM_DIRECTORY + "/Image";
	//图片缓存目录，每次上传完需要删除
	public static final String IMAGE_CACHE = IMAGE_DIRECTORY + "/cache";
	//图片缓存目录，不清空，持久缓存
	public static final String IMAGE_PERSISTENT_CACHE = PROGRAM_DIRECTORY + "/Image2";
	//更新目录
	public static final String UPDATE_DIRECTORY = PROGRAM_DIRECTORY + "/update";

	/**
	 * 朋友圈的消息类型
	 * FlowBeggar:流量少于10%且少于100M时发的动态
	 * IamTuHao:流量大于30%且大于1G时发的动态
	 */
	public enum ActiveType{
		NULL("NULL"),_4G("4G"),iPhone5S_5C("iPhone5S/5C"),DownLoadApp("downLoadApp"),QueryGPRSFlow("queryGPRSFlow"),OpenDonateFlow("openDonateFlow"),
		DonateGPRSFlow("donateGPRSFlow"),CreateShareGroup("createShareGroup"),JoinShareGroup("joinShareGroup"),SpeedTest("speedTest"),FlowGift("flowGift"),
		FreeFlow("freeFlow"),PersonalActive("personalActive"),FlowGroupon("groupBuyFlow"),FlowBeggar("FlowBeggar"),IamTuHao("IamTuHao");

		private String value;

		ActiveType(String value) {
			this.value = value;
		}
		
		public static ActiveType getEnumFromString(String string) {
			if (string != null) {
				for (ActiveType s : ActiveType.values()){
					if (string.equals(s.value)) {
		                return s;
		            }
				}
			}
			return NULL;
		}

		public String getValue() {
			return value;
		}
	}
	
	public enum ServiceID {// 接口常量
		// 通过括号赋值,而且必须带有一个参构造器和一个属性跟方法，否则编译出错
		// 赋值必须都赋值或都不赋值，不能一部分赋值一部分不赋值；如果不赋值则不能写构造器，赋值编译也出错
		DO_RICH_MAN_TOGETHER("DoRichManTogether"), I_AM_RICH_MAN(
				"IAmRichMan"),FREE_FLOW("FREE_FLOW"), NOVALUE("NOVALUE");
				
		private final String value;

		// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
		ServiceID(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

	}
	
	public enum ProdID{
		// 10元套餐//20元套餐
		RMG_PACKAGE_TYPE_1("prod.10000085803001"), RMG_PACKAGE_TYPE_2(
				"prod.10000085803002"),PACKAGE_TYPE_DONATION("prod.10000085802001");
		private final String value;

		// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
		private ProdID(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
	
	/**
	 * 数据统计类型
	 * @author Sola
	 * @2014-11-20
	 */
	public static String StatTypeFlowGroupon = "groupBuyFlowMenuClick";//流量团购

	
	/**
	 * 三大运营商
	 * @author Sola
	 * 
	 */
	public enum OperatorType{
		MOBILE("MOBILE"),UNICOM("UNICOM"),TELECOM("TELECOM"),OTHER("OTHER");
		
		private final String value;
		
		OperatorType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static OperatorType getEnumFromString(String string) {
			if (string != null) {
				for (OperatorType s : OperatorType.values()){
					if (string.equals(s.value)) {
		                return s;
		            }
				}
			}
			return MOBILE;
		}
	}
	// ----add at 6.18-------
	//免流量下载
	public static final String CMCC_RECOMMEND_DOWNLOAD_PID = "100002";
	public static final String CMCC_RECOMMEND_DOWNLOAD_PASSWORD = "123456";
	public static final String CMCC_RECOMMEND_DOWNLOAD_URL = "http://120.197.89.169:8080/flowportal/page";
	
	//add @2014-12-31--家庭流量计划
	//查询短号群聊网成员
	public static final String QUERY_DHQLW_MEMBERS = HOST + "MMService/services/familyflow/queryDHQLWMembers.jsp";
	
	//add @2015-01-05 自动接收验证码的号码
	public static final String SMS_NUMBER = "106582323";
	
	//用户登录注册
	public static final String URL_REGISTER = HOST + "MMService/services/account/register.jsp";
	public static final String URL_LOGIN = HOST + "MMService/services/account/login.jsp";//用户登录  TODO
	//短信和图形验证码
	public static final String URL_SEND_CODE = HOST + "MMService/services/sendSMSVerifyCode.jsp";//获取验证码
	public static final String URL_COMPARE_CODE = HOST + "MMService/services/checkSMSVerifyCode.jsp";//验证验证码
	public static final String URL_AD_PAGE = HOST + "MMService/services/sys/getLoginNoticeInfo.jsp";//首页启动广告
	public static final String URL_GET_IMAGE_CODE = HOST + "MMService/services/getRandomImage.jsp";//图形验证码
	public static final String URL_GET_MSG_CODE = HOST + "MMService/services/sendSMSVerifyCodeCommon.jsp"; //获取短信验证码 TODO
	
	public static final String URL_LOGOUT = HOST + "MMService/services/account/logout.jsp";//用户登出
	public static final String URL_UPDATE_USERINFO = HOST + "MMService/services/account/updateUserInfo.jsp";//更新用户信息
	
	public static final String WEXIN_APP_KEY = "wx8b6a6000ce074a26";//微信key
	
	public static final String HAVE_REGISTER = "HAVE_REGISTER"; //TODO
	
	public static final String URL_MATCH_URL =HOST + "MMService/services/matchFriends/matchFriendsFromContacts.jsp";
	public static final String URL_GET_FRIEND =HOST + "MMService/services/matchFriends/friendsManage.jsp";
	
	public static final String URL_GET_BILLBOARD =HOST + "MMService/services/rank/getRank.jsp";//排行榜
	//add @2015-01-07
	public static final String INTENT_PHONE = "INTENT_PHONE";
	public static final String INTENT_TARGET_CLASS_NAME = "INTENT_TARGET_CLASS_NAME";//最终需要放到intent中跳转的activity
	public static final String INTENT_SOURCE_CLASS_NAME = "INTENT_SOURCE_CLASS_NAME";//来源的activity，用于后退时跳转
	//add @2015-01-08
	public static final String SEND_ACTIVE_IN_PAST_3DAYS = HOST + "MMService/services/active/hadSendActiveInPast3Days.jsp";//3天内是否发过某种类型的动态
	public static final String INVITE_MULTIPLE_FLOW_SHARE_MEMBER = HOST + "MMService/services/GroupFlowShare/inviteMultipleMembers.jsp";//邀请流量共享成员（多个）
}
