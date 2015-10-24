package com.kinth.mmspeed.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.ActiveWithComments;
import com.kinth.mmspeed.bean.AdArticleInfo;
import com.kinth.mmspeed.bean.ArticleInfo;
import com.kinth.mmspeed.bean.Billboard4GItem;
import com.kinth.mmspeed.bean.BillboardFlowItem;
import com.kinth.mmspeed.bean.Comments;
import com.kinth.mmspeed.bean.ContractFriend;
import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.bean.ContractLocalInfo;
import com.kinth.mmspeed.bean.FlowUseInfo;
import com.kinth.mmspeed.bean.RankItem4G;
import com.kinth.mmspeed.bean.VersionInfo;
import com.kinth.mmspeed.bean.RichManGroup;
import com.kinth.mmspeed.bean.SMSProdDetailInfo;
import com.kinth.mmspeed.bean.SMSProdInfo;
import com.kinth.mmspeed.bean.SMSTotalInfo;
import com.kinth.mmspeed.bean.User;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.bean.UserFlowInfo;
import com.kinth.mmspeed.bean.UserFriend;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.util.AES;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JSONUtils;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.TransObject;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.sola.code.ydlly_assist.bean.KeyValue;

public class AsyncNetworkManager {

	public AsyncNetworkManager() {
	}

	public String PostUrl(String urlPath, String parameter) {
		String responseData = "";
		try {
			String BOUNDARY = "---------7d4a6d158c9"; //
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(13000);// 13s超时时间
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// DataOutputStream out = new
			// DataOutputStream(conn.getOutputStream());
			
			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream());
			out.write(parameter);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String rdata = "";
			String line = "";
			while ((line = reader.readLine()) != null) {
				rdata += line;
			}
			if (rdata.equals("")) {
				System.out.println("Post null ");
			} else {
				responseData = rdata;
			}
		}catch(ConnectException e){
			e.printStackTrace();
			responseData = "{\"rtn\":-2}";
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			responseData = "{\"rtn\":-3}";   //超时
		}catch (Exception e) {
			System.out.println("Post Exception" + e);
			e.printStackTrace();
			//responseData = "";  这个地方应该改掉，否则  出了异常会为空，则data解析之后也会为空
			responseData = "{\"rtn\":-1}";
		}
		return responseData;
	}

	public byte[] PostGetImageUrl(String urlPath, String parameter) {
		String responseData = "";
		byte[] buf = new byte[512];
		try {
			String BOUNDARY = "---------7d4a6d158c9"; //
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(13000);// 13s超时时间
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// DataOutputStream out = new
			// DataOutputStream(conn.getOutputStream());

			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream());
			out.write(parameter);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			// String rdata = "";
			// String line = "";
			// while ((line = reader.readLine()) != null) {
			// rdata += line;
			// }
			// if (rdata.equals("")) {
			// System.out.println("Post null ");
			// } else {
			// responseData = rdata;
			// }
			// }
			// catch (Exception e) {
			// System.out.println("Post Exception" + e);
			// e.printStackTrace();
			// responseData = "";
			// }

			BufferedInputStream in = new BufferedInputStream(
					conn.getInputStream());

			// 存放地址
			// File file = new
			// File(JUtil.getSDPath()+"/LiuLiangYi/Data/img"+UUID.randomUUID()+".jpg");
			// // 生成图片
			// BufferedOutputStream out1 = new BufferedOutputStream(
			// new FileOutputStream(file));

			int length = in.read(buf);
			while (length != -1) {
				// out1.write(buf, 0, length);
				length = in.read(buf);
			}
			in.close();
			// out1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf;
	}

	/**
	 * 统计反馈--参数默认传号码和运营商
	 * @param context
	 * @param keyValue
	 * @param callback
	 */
	public void asynSendData(final Context context,final List<KeyValue> keyValue) {
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				addParameters(msg,keyValue);
				
				msg.putJSONObject("networkOperator", ApplicationController.getInstance()
						.getNetworkOperator());// 网络运营商
				msg.putJSONObject("mobile", MainAccountUtil.getAvailablePhone(context));// 传当前号码
				
				PostUrl(APPConstant.SEND_DATA, msg.getJSONString());
			}
		}.start();
	}

	/**
	 * 服务器获取该号码流量套餐
	 * 
	 * @param context
	 * @param uid
	 * @param callback
	 * 
	 * 返回增加参数 @2015-01-11
	 * “rtnAnalyze”:int
	 * “flowAnalyze”:List<FlowUseInfo>
	 */
	public void asyncQueryGPRS(final Context context, final String uid,
			final String phone, final QueryGPRSCallback callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.queryGPRSCallback(msg.arg1, (UserFlowInfo) msg.obj);
				return true;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", phone);
				String data = PostUrl(ApplicationController.QUERY_GPRS_URL,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				UserFlowInfo userFlowInfo = new UserFlowInfo();
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn")); // 返回结果
					if (rtn == 0) { // 没有套餐
						
					} else if (rtn == 1) { // 有套餐
						List<SMSProdDetailInfo> smsProdDetailInfos = new ArrayList<SMSProdDetailInfo>();
						List<SMSProdInfo> smsProdInfos = new ArrayList<SMSProdInfo>();
						List<FlowUseInfo> flowUseInfos = new ArrayList<FlowUseInfo>();
						smsProdDetailInfos = recvmsg.getClassObjArrayFromJson(
								"SMSProdDetailList", SMSProdDetailInfo.class);
						smsProdInfos = recvmsg.getClassObjArrayFromJson(
								"SMSProdList", SMSProdInfo.class);
						int rtnAnalyze = TransObject.getInt(recvmsg.getJSONObject("rtnAnalyze"));
						if(rtnAnalyze == 1){
							flowUseInfos = recvmsg.getClassObjArrayFromJson("flowAnalyze", FlowUseInfo.class);
						}else{
							flowUseInfos = null;
						}
						
						Object jsonObject = recvmsg
								.getJSONObject("SMSTotalInfo");
						SMSTotalInfo smsTotalInfo = JSONUtils.fromJson(
								jsonObject.toString(), SMSTotalInfo.class);
						Object jsonObject1 = recvmsg.getJSONObject("SMSADInfo");
						ArticleInfo articleInfo = JSONUtils.fromJson(
								jsonObject1.toString(), ArticleInfo.class);

						userFlowInfo.setSmsProdDetailInfos(smsProdDetailInfos);
						userFlowInfo.setSmsProdInfos(smsProdInfos);
						userFlowInfo.setSmsTotalInfo(smsTotalInfo);
						userFlowInfo.setArticleInfo(articleInfo);
						userFlowInfo.setUsedFlowPerDay(flowUseInfos);
					}
				} catch (Exception e) {
					e.printStackTrace();
					rtn = -1;
				}
				Message message = handler.obtainMessage(0);
				message.arg1 = rtn;
				message.obj = userFlowInfo;// 整个信息打包
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * 从服务器获取更新信息
	 * 
	 * @param context
	 * @param nickName
	 * @param netCallback
	 */
	public void asynUpdateFromServer(final Context context,
			final UpdateCallback updateCallback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				updateCallback.updateCallback(message.what,
						(VersionInfo) message.getData().getParcelable("VersionInfo"));
			}
		};
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();

				String data = PostUrl(APPConstant.UPDATE_URL, msg.getJSONString());
				/**
				 * 示例数据
				 * {"versionInfo":{"dev":"","issueTime":"2014-10-23 15:06:19","url":"http://120.196.123.19/ydlly/lly.apk","versionDesc":"1、新增网络管理-测速地图，我附近的网速有多快，三大运营商谁更快，一目了然。\r\n2、流量朋友圈可以主动发图文消息啦！","versionNo":"22"},"hasNew":1}
				 */
//				data = "{\"versionInfo\":{\"dev\":\"\",\"isForce\":\"1\",\"issueTime\":\"2014-10-23 15:06:19\",\"url\":\"http://120.196.123.19/ydlly/lly.apk\",\"versionDesc\":\"1、新增网络管理-测速地图，我附近的网速有多快，三大运营商谁更快，一目了然。\r\n2、流量朋友圈可以主动发图文消息啦！\",\"versionNo\":\"22\"},\"hasNew\":1}";
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				
				int hasNew = 0;
				VersionInfo versionInfo = new VersionInfo();
				try {
					hasNew = TransObject
							.getInt(recvmsg.getJSONObject("hasNew"));
					String temp = TransObject.getString(recvmsg
							.getJSONObject("versionInfo"));
					Gson gson = new Gson();
					versionInfo = gson.fromJson(temp,
							new TypeToken<VersionInfo>() {
							}.getType());
					if(hasNew == 1){//有更新
						SingletonSharedPreferences.getInstance().setIsNewVersionAvailable(true);
						SingletonSharedPreferences.getInstance().setNewVersionJson(temp);
					}else{
						SingletonSharedPreferences.getInstance().setIsNewVersionAvailable(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = handler.obtainMessage(hasNew);
				Bundle bundle = new Bundle();
				bundle.putParcelable("VersionInfo", versionInfo);
				message.setData(bundle);
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * 获取验证码
	 */
	public void getVerificationCode(final Context context,
			final String phoneStr, final String nickName, final String remark,
			final String recommendMobile,
			final VerificationCodeCallBack callBack) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callBack.getVerificationCodeCallBack(msg.arg1);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", phoneStr);
				msg.putJSONObject("nickName",UtilFunc.encode(nickName));

				msg.putJSONObject("remark", UtilFunc.encode(remark));
				msg.putJSONObject("recommendMobile", recommendMobile);
				String data = PostUrl(APPConstant.URL_SEND_CODE,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = handler.obtainMessage(0, rtn, 0);
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * 验证验证码--包括图形验证码
	 * 
	 * @param context
	 * @param phoneStr
	 * @param verifyCode
	 * @param callBack
	 */
	public void compareVerificationCode(final Context context,
			final String phoneStr, final String verifyCode,
			final String recommendMobile, final String imageCode,
			final compareVerificationCodeCallBack callBack) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				callBack.getCompareVerificationCodeCallBack(message.arg1);
			}
		};
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", phoneStr);
				msg.putJSONObject("verifyCode", verifyCode);
				msg.putJSONObject("recommendMobile", recommendMobile);
				msg.putJSONObject("imageCode", imageCode);
				String data = PostUrl(APPConstant.URL_COMPARE_CODE,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = handler.obtainMessage(0, rtn, 0);
				handler.sendMessage(message);
			}
		}.start();
	}

	public void shareStatistics(Context context, final String statType,
			final String keyValue) {
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("statType", statType);
				msg.putJSONObject("keyValue", keyValue);
				String data = PostUrl(APPConstant.SHARE_STATISTICS,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
			}
		}.start();
	}

	/**
	 * 首页启动广告
	 * @param context
	 * @param callBack
	 */
	public void getAdPageInfo(Context context, final getAdPageCallBack callBack) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				callBack.getAdPageCallback((AdArticleInfo) message.obj);
			}
		};
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();

				String data = PostUrl(APPConstant.URL_AD_PAGE,
						msg.getJSONString());
				
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				// int rtn = 0;
				// try {
				// rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				// adArticleInfo.setArticleName("测试标题");
				// adArticleInfo.setClickURL("http://42.121.0.186/MMService/page/activity.jsp");
				// adArticleInfo.setClientHandle("SMS");
				// adArticleInfo.setId(12);
				// adArticleInfo.setPublisher("编辑者");
				// adArticleInfo.setShortContent("test ShortContent");
				// adArticleInfo.setShowStartTime("2014-05-15 00:00:00");
				// adArticleInfo.setShowEndTime("2014-06-30 00:00:00");
				// adArticleInfo.setTitleURLString("http://42.121.194.76:8001/DownFile.aspx?filename=notice960.jpg");
				Object jsonObject1 = recvmsg.getJSONObject("LoginNoticeInfo");
				AdArticleInfo adArticleInfo = new AdArticleInfo();
				try {
					adArticleInfo = JSONUtils.fromJson(jsonObject1.toString(),
							AdArticleInfo.class);
				} catch (Exception e) {
					adArticleInfo = null;
					
				}

				Message message = handler.obtainMessage();
				message.obj = adArticleInfo;
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * 获取图形验证码
	 * @param context
	 * @param callBack
	 */
	@Deprecated
	public void getImageCode(Context context,
			final GetImageCodeCallBack callBack) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				callBack.getImageCodeResult((String) message.obj);
			}
		};
		// new Thread() {
		// @Override
		// public void run() {
		// com.kinth.mmspeed.network.Message msg = new
		// com.kinth.mmspeed.network.Message();
		// msg.putCommonMsgInfo();
		// byte[] data = PostGetImageUrl(APPCONSTANT.URL_GET_IMAGE_CODE,
		// msg.getJSONString());
		//
		// }
		//
		// // com.kinth.mmspeed.network.Message recvmsg = new
		// com.kinth.mmspeed.network.Message(
		// // data);
		// // int rtn = 0;
		// // try {
		// // rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
		// // } catch (Exception e) {
		// // e.printStackTrace();
		// // }
		// // Object jsonObject1 = recvmsg.getJSONObject("LoginNoticeInfo");
		// // AdArticleInfo adArticleInfo = new AdArticleInfo();
		// // try {
		// // adArticleInfo= JSONUtils.fromJson(jsonObject1.toString(),
		// AdArticleInfo.class);
		// // } catch (Exception e) {
		// // adArticleInfo =null;
		// // }
		//
		// Message message = handler.obtainMessage();
		// message.obj = data;
		// handler.sendMessage(message);
		// }
		// }.start();
		// 如果是飞行模式 就不下获取了
		FinalHttp fh = new FinalHttp();

		final String pathStr = JUtil.getSDPath() + "/LiuLiangYi/Data/code."
				+ System.currentTimeMillis() + "._jpg";
		if ("".equals(JUtil.getSDPath())) {
			// TODO
			return;
		}

		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("dev", "Android,"
				+ ApplicationController.getInstance().getANDROID_API_VERSION() + ","
				+ ApplicationController.getInstance().getDev());
		ajaxParams.put("resolution", ApplicationController.getInstance().getResolution());
		ajaxParams.put("mac", ApplicationController.getInstance().getMac());
		ajaxParams.put("version", ApplicationController.getInstance().getVersion() + "");
		ajaxParams.put("uid", ApplicationController.getInstance().getUid());//
		ajaxParams.put("api", ApplicationController.getInstance().getInterfaceApi());// 接口的版本号

		fh.download(APPConstant.URL_GET_IMAGE_CODE, ajaxParams, pathStr,
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						// textView.setText("下载进度："+current+"/"+count);
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, strMsg);
						System.out.println(strMsg + "");
					}

					@Override
					public void onSuccess(File t) {
						File file = new File(pathStr);
						if (file.exists()) {
							// mImageLoader.displayImage("file:///"+receRecordPath,
							// mImageView, options, animateFirstListener);
						}
						// File file = new File(t.toString());
						if (file.exists()) {
							System.out.println(file.toString());
							Message msg = handler.obtainMessage();
							msg.obj = pathStr;
							handler.sendMessage(msg);
						}
					}
				});
	}

	/**
	 * 获取流量优惠文章列表
	 * 
	 * @param context
	 * @param callback
	 */
	public void getFlowPreferentialList(Context context,
			final GetNewsCallback callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean handleMessage(Message message) {
				callback.getNewsResult(message.what,
						(List<ArticleInfo>) message.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();

				String data = PostUrl(ApplicationController.FLOW_PREFERENTIAL_URL,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);

				int rtn = 0;
				List<ArticleInfo> articleInfos = new ArrayList<ArticleInfo>();
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					articleInfos = recvmsg.getClassObjArrayFromJson(
							"ArticleList", ArticleInfo.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// articleInfos.add(new ArticleInfo(1, "测试文章", "发行者", "巴拉巴拉简介",
				// "2014-04-25", "", ""));
				Message message = handler.obtainMessage(rtn, articleInfos);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	// --------------朋友圈接口--------------
	/**
	 * 产生动态信息
	 * @param context
	 * @param mobile	产生动态的用户
	 * @param content	动态内容
	 * @param activeType	动态类型
	 * 	1、4G	用上4G网络
	 *	2、iPhone5S/5C	在iPhone5S/5C启动流量仪
	 *	3、downLoadApp	用流量仪下载App
	 *	4、queryGPRSFlow		查询剩余流量，且在好用中排名前5/倒5
	 *	5、donateGPRSFlow	转增流量
	 *	6、createShareGroup	发起流量共享群组
	 *	7、joinShareGroup	加入共享群组
	 *	8、speedTest 测速
	 *	9、groupBuyFlow 流量团购
	 * @param targetMobile	转赠流量/加入共享群组时使用    加传 targetNickName
	 * @param flow 转赠流量时使用
	 * @param speed 测速时使用
	 * @param callback	
	 */
	public void genActiveTimeLine(final Context context,final List<KeyValue> keyValue,final genActiveTimeLineCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onGenActiveTimeLineCallBack(msg.arg1,msg.arg2);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				addParameters(msg,keyValue);
				
//				msg.putJSONObject("mobile",mobile);
//				msg.putJSONObject("content", content);
//				msg.putJSONObject("activeType", activeType.getValue());
//				if(ActiveType.DonateGPRSFlow.equals(activeType)){//流量转赠
//					msg.putJSONObject("targetMobile", targetMobile);
//					DbUtils db = DbUtils.create(context, mobile+".friends");
//					ContractInfo item = null;
//					try {
//						item = db.findFirst(Selector.from(ContractInfo.class).where("phoneNumber","=",targetMobile));
//					} catch (DbException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					msg.putJSONObject("targetNickName", UtilFunc.encode(item != null ? item.getContractName() : ""));
//					msg.putJSONObject("flow", flow);
//				}
//				if(ActiveType.JoinShareGroup.equals(activeType)){//加入流量共享群
//					msg.putJSONObject("targetMobile", targetMobile);
//					DbUtils db = DbUtils.create(context, mobile+".friends");
//					ContractInfo item = null;
//					try {
//						item = db.findFirst(Selector.from(ContractInfo.class).where("phoneNumber","=",targetMobile));
//					} catch (DbException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					msg.putJSONObject("targetNickName", UtilFunc.encode(item != null ? item.getContractName() : ""));
//				}
//				if(ActiveType.SpeedTest.equals(activeType)){//测速
//					msg.putJSONObject("speed", SingletonSharedPreferences
//							.getInstance().getValueOfDownload()/128.0);
//				}
				
				String data = PostUrl(APPConstant.GEN_ACTIVE_TIME_LINE,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				int activeId = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					activeId = TransObject.getInt(recvmsg.getJSONObject("activeId"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(0,rtn,activeId);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * 获取动态
	 * @param context
	 * @param mobile	自己手机号码
	 * @param isGetMyActive	1: 获取自己的动态  0: 获取好友动态（包含自己的）
	 * @param timeStamp	时间戳，服务端返回该时间戳之后的数据，用来做分页
	 * @param timeDir "earlier"表示获取比timeStamp更早前的动态  "latter"表示取比timeStamp更晚发的动态
	 * @param callback
	 */
	public void getActiveTimeLine(final Context context,final String mobile,final int isGetMyActive,final String timeStamp,final String timeDir, final getActiveTimeLineCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onGetActiveTimeLineCallBack(msg.what, (ArrayList<ActiveWithComments>) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile",mobile);
				msg.putJSONObject("isGetMyActive", isGetMyActive);
				msg.putJSONObject("timeStamp", timeStamp);
				msg.putJSONObject("timeDir", timeDir);

				String data = PostUrl(APPConstant.GET_ACTIVE_TIME_LINE,
						msg.getJSONString());
//				data = "{\"rtn\":1,\"activeList\":[{\"active\":{\"activeId\":104615,\"activeType\":\"personalActive\",\"content\":\"\",\"createTime\":\"2014-09-29 10:43:43.0\",\"iconUrl\":\"\",\"mobile\":\"15813314458\",\"nickName\":\"我\",\"picUrl\":\"http://42.121.194.76:8001/DownIcon.aspx?filename=13349654-9b0f-4185-a786-a368013e1a35,http://42.121.194.76:8001/DownIcon.aspx?filename=306e1f8f-6496-46c9-9e9d-a39300d5e298\",\"praiseNum\":0},\"comments\":[],\"praiseByMe\":false,\"praisers\":[]},{\"active\":{\"activeId\":102227,\"activeType\":\"4G\",\"content\":\"用上了4G啦！\",\"createTime\":\"2014-09-28 09:21:30.0\",\"iconUrl\":\"http://42.121.194.76:8001/DownFile.aspx?filename=37554321-214f-42e2-aad7-a38c0117fd86\",\"mobile\":\"13924079979\",\"nickName\":\"本机\",\"picUrl\":\"\",\"praiseNum\":0},\"comments\":[],\"praiseByMe\":false,\"praisers\":[]}]}";
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				List<ActiveWithComments> activeList = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					activeList = recvmsg.getClassObjArrayFromJson("activeList",
							ActiveWithComments.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Message message = handler.obtainMessage(rtn);
				message.obj = activeList;
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * 对某个动态点赞
	 * @param context
	 * @param activeId	动态ID
	 * @param mobile	点赞用户
	 * @param callback
	 */
	public void praiseActiveTimeLine(final Context context,final int activeId,final String mobile,final praiseActiveTimeLineCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onPraiseActiveTimeLineCallBack(msg.what);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("activeId", activeId);
				msg.putJSONObject("mobile",mobile);

				String data = PostUrl(APPConstant.LIKE_ACTIVE_TIME_LINE,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * 对某个动态评论
	 * @param context
	 * @param activeId	动态ID
	 * @param mobile	评论用户
	 * @param comment	评论内容
	 * @param callback
	 */
	public void commentActiveTimeLine(final Context context,final int activeId,final String mobile,final String comment,final commentActiveTimeLineCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onCommentActiveTimeLineCallBack(msg.what);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("activeId", activeId);
				msg.putJSONObject("mobile",mobile);
				try {
					msg.putJSONObject("comment", java.net.URLEncoder.encode(comment,"UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				String data = PostUrl(APPConstant.COMMENT_ACTIVE_TIME_LINE,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * 获取某个动态的评论
	 * @param context
	 * @param activeId	动态ID
	 * @param callback
	 */
	public void getActiveComment(final Context context,final int activeId,final getActiveCommentCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onGetActiveCommentCallBack((List<Comments>) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("activeId", activeId);

				String data = PostUrl(APPConstant.GET_ACTIVE_COMMENT,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				List<Comments> commentList = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					commentList = recvmsg.getClassObjArrayFromJson("commentList",
							Comments.class);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn);
				message.obj = commentList;
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * add @2014-08-20
	 * 添加参数到Message
	 */
	private void addParameters(com.kinth.mmspeed.network.Message msg,List<KeyValue> map){
		for(KeyValue item:map){
			msg.putJSONObject(item.getKey(), item.getValue());
		}
	}
	
	/**
	 * 查看朋友圈是否有新的动态
	 * @param context
	 * @param keyValue ：mobile(String)、timeStamp(String)
	 * @param callback
	 */
	public void cheakMomentsHasNew(final Context context, final List<KeyValue> keyValue, final MomentsHasNewCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onMomentsHasNew(msg.arg1);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				addParameters(msg,keyValue);

				String data = PostUrl(APPConstant.MOMENTS_HAS_NEW,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int count = 0;
				try {
					count = TransObject.getInt(recvmsg.getJSONObject("count"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(0);
				message.arg1 = count;
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * @author sola
	 * add at @2014-08-16 检测新消息
	 */
	/**
	 * 查看消息中心是否有新消息
	 * @param context
	 * @param keyValue ：timeStamp(String)
	 * @param callback
	 */
	public void checkMessageCentreHasNew(final Context context,final List<KeyValue> keyValue,final MessageCentreHasNewCallBack callback){
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onMessageCentreHasNew(msg.arg1);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				addParameters(msg,keyValue);

				String data = PostUrl(APPConstant.MESSAGE_CENTRE_HAS_NEW,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int count = 0;
				try {
					count = TransObject.getInt(recvmsg.getJSONObject("count"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Message message = handler.obtainMessage(0);
				message.arg1 = count;
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * --------------流量共享所有接口-----------
	 * 
	 * @author Sola
	 */
	// 获取群组网最大成员数目
	public void getMemberMaxCount(final Context context,final String serviceid,
			final GetMemberMaxCountCallBack callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onGetMemberMaxCountCallBack(msg.arg1,msg.arg2);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("serviceid",serviceid);

				String data = PostUrl(APPConstant.GET_RMG_MEMBER_MAX_COUNT,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				int maxnumber = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					maxnumber = TransObject.getInt(recvmsg.getJSONObject("maxnumber"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(0,rtn,maxnumber);
				handler.sendMessage(message);
			}
		}.start();
	}

	// 查询群组订购情况
	public void queryRichManGroup(final Context context,final String serviceid,
			final String mobile, final QueryRichManGroupCallBack callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onQueryRichManGroupCallBack(msg.what,
						(RichManGroup) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", mobile);
				msg.putJSONObject("serviceid",serviceid);

				String data = PostUrl(APPConstant.QUERY_RMG_ORDER_STATUS,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				RichManGroup richManGroup = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					richManGroup = recvmsg.getClassObjFromKey("RichManGroup",
							RichManGroup.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn, richManGroup);
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 *  创建群组
	 * @param context
	 * @param mobile
	 * @param prodid
	 * @param verifyCode
	 * @param callback
	 * 入参：
	 * verifyCode:短信验证码（新增）
	 * mobile手机号；
	 * serviceid：DoRichManTogether；
     * prodid 取值说明

	 * prod.10000085803001       10 元
	 * prod.10000085803002       20 元

	 * 返回：
     * rtn 1成功，0失败， -1 短信验证码失败或过期
	 */
	public void createRichManGroup(final Context context,final String serviceid,
			final String mobile, final String prodid,final String verifyCode,
			final CreateRichManGroupCallBack callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onCreateRichManGroupCallBack(msg.what,(String) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", mobile);
				msg.putJSONObject("serviceid",serviceid);
				msg.putJSONObject("prodid", prodid);
				msg.putJSONObject("verifyCode",verifyCode);//验证码

				String data = PostUrl(APPConstant.CREATE_RICH_MAN_GROUP,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				String error = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					error = TransObject.getString(recvmsg.getJSONObject("message"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn,error);
				handler.sendMessage(message);
			}
		}.start();
	}

	// 取消群组
	public void cancelRichManGroup(final Context context,final String serviceid,
			final String mobile, final CancelRichManGroupCallBack callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onCancelRichManGroupCallBack(msg.what,(String) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", mobile);
				msg.putJSONObject("serviceid",serviceid);

				String data = PostUrl(APPConstant.CANCEL_RICH_MAN_GROUP,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				String error = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					error = TransObject.getString(recvmsg.getJSONObject("message"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn,error);
				handler.sendMessage(message);
			}
		}.start();
	}

	/**
	 *  群组成员管理
	 * @param context
	 * @param mobile
	 * @param type
	 * @param prodid
	 * @param memservnumber
	 * @param callback
	 *    
	 * serviceid：DoRichManTogether IAmRichMan
	 * mobile手机号；群主号码
     * type  取值说明
     * 增加副号（加入群组）1
     * 取消副号（退出群组）2
     * 变更套餐（仅支持一起做土豪成员变更套餐时使用）3
     * 取消套餐（暂不使用）4
     * 修改副号限额（仅支持我是土豪）5
     * prodid 取值说明
     * prod.10000085803001     10元
     * prod.10000085803002    20元
     * memservnumber
     * 要管理的成员的手机号
     *
     * 返回：
     *rtn 1成功，0失败
	 */
	public void chgRichManMember(final Context context,final String serviceid,
			final String mobile, final int type, final String prodid,final String limit,
			final String memservnumber, final String verifyCode,final ChgRichManMemberCallBack callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onChgRichManMemberCallBack(msg.what,(String) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", mobile);
				msg.putJSONObject("serviceid",serviceid);
				msg.putJSONObject("type", type);
				if(type == 1 || type == 3){//加入群组、变更套餐时需要传验证码
					msg.putJSONObject("verifyCode", verifyCode);
				}
				msg.putJSONObject("prodid", prodid);
				msg.putJSONObject("limit", limit);
				msg.putJSONObject("memservnumber", memservnumber);//要管理的成员的手机号

				String data = PostUrl(APPConstant.MANAGE_RMG_MEMBER,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				String error = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					error = TransObject.getString(recvmsg.getJSONObject("message"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Message message = handler.obtainMessage(rtn,error);
				handler.sendMessage(message);
			}
		}.start();
	}

	// 邀请成员
	public void inviteMember(final Context context, final String serviceid, final String mobile,
			final String prodid, final String memservnumber,
			final InviteMemberCallBack callback) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callback.onInviteMemberCallBack(msg.what,(String) msg.obj);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", mobile);
				msg.putJSONObject("serviceid",serviceid);
				msg.putJSONObject("prodid", prodid);
				msg.putJSONObject("memservnumber", memservnumber);

				String data = PostUrl(APPConstant.INVITE_RMG_MEMBER,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				String error = null;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					error = TransObject.getString(recvmsg.getJSONObject("message"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = handler.obtainMessage(rtn,error);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	//通用获取验证码
	public void getSMSVerifyCodeCommon(final Context context,
			final String phoneStr,final String recommendMobile,
			final VerificationCodeCallBack callBack) {
		final Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				callBack.getVerificationCodeCallBack(msg.what);
				return false;
			}
		});
		new Thread() {
			@Override
			public void run() {
				com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
				msg.putCommonMsgInfo();
				msg.putJSONObject("mobile", phoneStr);
				msg.putJSONObject("recommendMobile", recommendMobile);
				String data = PostUrl(APPConstant.GET_SMS_VERIFY_CODE_COMMON,
						msg.getJSONString());
				com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
						data);
				int rtn = 0;
				try {
					rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = handler.obtainMessage(rtn);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
     * 账号注册
     *
     * @param
     * mobile 用户账号（手机）
     * verifyCode 手机短信验证码
     * 其它公共参数
     *
     * @return
     * rtn
     * - 1注册失败
     * 0 验证码失败
     * 1 注册成功
     * 2 用户已经注册过，可以直接登录
     */
//	public  void userRegister(final Context context, final String mobile,
//			final String verifyCode,final RegisterMethod registerMethod) {
//			final Handler handler = new Handler(new Handler.Callback() {
//				@Override
//				public boolean handleMessage(Message msg) {
//					registerMethod.registerCallBack(msg.arg1);
//					return false;
//				}
//			});
//			new Thread() {
//				@Override
//				public void run() {
//					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
//					msg.putCommonMsgInfo();
//					msg.putJSONObject("mobile", mobile);
//					msg.putJSONObject("verifyCode",
//							verifyCode);
//					
//					String data = PostUrl(APPConstant.URL_REGISTER,
//							msg.getJSONString());
//					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
//							data);
//					int rtn = 0;
//					try {
//						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
//					} catch (Exception e) {
//						e.printStackTrace();
//						rtn = -1;
//					}
//					Message message = handler.obtainMessage();
//					message.arg1 = rtn;
//					handler.sendMessage(message);
//				}
//		}.start();
//	}
	
	/**
     * 用户登录
     * @param
     * isAutoLogin (int)
     *    1免密码自动登录（后台作设备判断）
     *    0需密码登录
     * mobile 登录账号
     * password md5加密后的密码
     * 其它公共参数
     *
     * @return
     * rtn
     *    1 登录成功
     *    2 登录失败：设备未曾注册绑定过
     *    3 登录失败：密码错误
     *
     */
	public void login(final Context context,final String mobile,
			final String verifyCode,final LoginMethod loginMethod) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					loginMethod.loginCallBack(msg.arg1,(UserAccount) msg.obj);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					
					msg.putJSONObject("verifyCode", verifyCode);
					msg.putJSONObject("mobile", mobile);
					
					String data = PostUrl(APPConstant.URL_LOGIN,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					UserAccount user1 = new UserAccount();
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
						Object jsonObject1 = recvmsg.getJSONObject("user");
						user1 = JSONUtils.fromJson(
								jsonObject1.toString(), UserAccount.class);
					} catch (Exception e) {
						e.printStackTrace();
						rtn = 3;
						user1= new UserAccount();
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					message.obj = user1;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * 上传通讯录
	 * @param context
	 * @param mobile
	 * @param contractInfos
	 * @param getContacts
	 */
	public  void getMatchFriendsFromContacts(final Context context,final String mobile,
			final List<ContractInfo> contractInfos,final MatchFriendsFromContacts getContacts) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					getContacts.MatchFriendsFromContactsCallBack(msg.arg1);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					//将通讯录的号码转成List<String>
					final List<ContractInfo>fiterContractInfos = JUtil.filterUserPhone(contractInfos);
					AES ase = new AES();

					List<ContractFriend>contractFriends = new ArrayList<ContractFriend>();
					
					for (int i = 0; i < fiterContractInfos.size(); i++) {
						ContractFriend contractFriend = new ContractFriend();
						
						if (fiterContractInfos.get(i).getMobile()!=null&&!fiterContractInfos.get(i).getMobile().equals("")) {
							contractFriend.setMobile(ase.encrypt(fiterContractInfos.get(i).getMobile().getBytes()));
//							contractFriend.setMobile(fiterContractInfos.get(i).getMobile());
						}else {
							contractFriend.setMobile("");
						}
						
						if (fiterContractInfos.get(i).getContractName()==null||fiterContractInfos.get(i).getContractName().equals("")) {
							contractFriend.setNickName("");
						}else {
							contractFriend.setNickName(java.net.URLEncoder.encode(fiterContractInfos.get(i).getContractName()));
						}
						contractFriends.add(contractFriend);
					}
					
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
					
					Gson g = new Gson();
					String jsonString = g.toJson(contractFriends);
					String encryptResult = ase.encrypt(jsonString.getBytes());
					msg.putJSONObject("contacts", encryptResult);
					
					String data = CommonFunc.postUrl
							(
									APPConstant.URL_MATCH_URL, 
									msg.getStandardJSONString()
							);
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					List<ContractInfo>mContractInfos = new ArrayList<ContractInfo>();        //用户的好友
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					} catch (Exception e) {
						rtn = -1;
						e.printStackTrace();
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					message.obj = mContractInfos;             //返回用户好友
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * 通过手机号码获取好友
	 * @veision 2.0
	 * @param context
	 * @param mobile  用户手机号码
	 * @param contractInfos   用户通讯录
	 * @param getContacts
	 */
	public void getFriendsFromByPhone(final Context context,final String mobile,
			final IGetUserFriends getContacts) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					getContacts.IGetUserFriendsCallBack(msg.arg1,(List<UserFriend>) msg.obj);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					
					DbUtils mDbUtils = DbUtils.create(context,mobile);
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
					String data = CommonFunc.postUrl
							(
									APPConstant.URL_GET_FRIEND, 
									msg.getJSONString()
							);
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					List<UserFriend> contractFriends = new ArrayList<UserFriend>();
					List<ContractLocalInfo>contractLocalInfos = new ArrayList<ContractLocalInfo>();
//					List<ContractInfo>mContractInfos = new ArrayList<ContractInfo>();        //用户的好友
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
						contractFriends = recvmsg.getClassObjArrayFromJson(
								"friendsBind", UserFriend.class);
					} catch (Exception e) {
						rtn = -1;
						e.printStackTrace();
					}
//					if (rtn==1&&contractFriends!=null&&contractFriends.size()>0) {
//						for (int i = 0; i < contractFriends.size(); i++) {
//							UserFriend userFriend = contractFriends.get(i);
//							for (ContractInfo contractInfo : contractInfos) {
//								if (contractInfo.getMobile().equals(userFriend.getMobile())) {
//									contractLocalInfos.add
//								}
//							}
//						}
//					}
					if (rtn==1&&contractFriends!=null&&contractFriends.size()>0) {
						try {
							mDbUtils.deleteAll(UserFriend.class);
							mDbUtils.saveAll(contractFriends);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					message.obj = contractFriends;             //返回本地好友
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * 获取4G排行榜
	 * @param context
	 * @param type
	 * @param mobile
	 * @param getBillBoardImp
	 */
	public void get4GBillboard(final Context context,final String type,
			final String mobile,final Get4GBillBoardImp getBillBoardImp) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					getBillBoardImp.Get4GBillBoardImpCallBack(msg.arg1,(Billboard4GItem) msg.obj);
					return false;
				}
			});
			
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					
					msg.putJSONObject("type", type);
					msg.putJSONObject("mobile", mobile);
					String string = msg.getJSONString();
//					String data = PostUrl(APPConstant.URL_GET_BILLBOARD,
//							msg.getJSONString());
					String data = CommonFunc.postUrl(APPConstant.URL_GET_BILLBOARD, msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					Billboard4GItem billboard4g = new Billboard4GItem();
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
						
						List<ContractInfo> friend4GList = recvmsg.getClassObjArrayFromJson(
								"friend4GList", ContractInfo.class);
						List<ContractInfo> recommendedFriends = recvmsg.getClassObjArrayFromJson(
								"recommendedFriends", ContractInfo.class);
						List<RankItem4G> prodRankList = recvmsg.getClassObjArrayFromJson("prodRankList",RankItem4G.class);
						
						if (friend4GList!=null&&friend4GList.size()>0) {
							billboard4g.setFriend4GList(friend4GList);
						}
						if (recommendedFriends!=null&&recommendedFriends.size()>0) {
							billboard4g.setRecommendedFriend(recommendedFriends);
						}
						if(prodRankList != null && prodRankList.size() > 0){
							billboard4g.setProdRankList(prodRankList);
						}
					} catch (Exception e) {
						rtn = 0;
						e.printStackTrace();
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					message.obj = billboard4g;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * 获取流量排行榜
	 * @param context
	 * @param type
	 * @param mobile
	 * @param getBillBoardImp
	 */
	public  void getFlowBillboard(final Context context,final String type,
			final String mobile,final GetFlowBillBoardImp getBillBoardImp) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					getBillBoardImp.GetFlowBillBoardImpCallBack(msg.arg1,(List<BillboardFlowItem>) msg.obj);
					return false;
				}
			});
			
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("type", type);
					msg.putJSONObject("mobile", mobile);
//					String data = PostUrl(APPConstant.URL_GET_BILLBOARD,
//							msg.getJSONString());
					String data = CommonFunc.postUrl
							(
									APPConstant.URL_GET_BILLBOARD, 
									msg.getJSONString()
							);
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					List<BillboardFlowItem> billboardFlows = new ArrayList<BillboardFlowItem>();
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
						
						billboardFlows= recvmsg.getClassObjArrayFromJson(
								"flowRank", BillboardFlowItem.class);
						
					} catch (Exception e) {
						rtn = 0;
						e.printStackTrace();
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					message.obj = billboardFlows;
					handler.sendMessage(message);
				}
		}.start();
	}

	/**
	 * 注销账号
	 * 
	 * @param mobile
	 *            手机号 公共参数
	 * @return rtn 
	 * 	1注销成功 
	 * 	2未设置密码，需要用户先进行设置
	 */
	public  void loginOut(final Context context, final String mobile,
			final LogoutMethod logoutMethod) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					logoutMethod.logoutMethodCallBack(msg.arg1);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
					
					String data = PostUrl(APPConstant.URL_LOGOUT,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					} catch (Exception e) {
						e.printStackTrace();
						rtn = -1;
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * 获取验证码
	 * @param context
	 * @param mobile
	 * @param getSMSCode
	 */
	public void getSMSCode(final Context context, final String mobile,
			final GetSMSCode  getSMSCode) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					getSMSCode.getSMSCallBack(msg.arg1);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
//					String phone = mobile;
					String data = PostUrl(APPConstant.URL_GET_MSG_CODE,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					} catch (Exception e) {
						e.printStackTrace();
						rtn = 3;
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
     * 更新用户信息
     * @param
     *    mobile 手机号
     *    nickName 用户昵称
     *    iconUrl 用户头像
     *    password 设置密码
     *    其它公用参数
     * @return
     *     rtn 1设置成功
     *    2 非法操作，修改失败（设备没有注册过）
     */
	public  void updateInfo(final Context context, final String mobile,final String nickName,final String iconUrl,
			final String password,
			final UpdateInfoIml  updateInfoIml) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					updateInfoIml.updateUserInfoCallBack(msg.arg1);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
					msg.putJSONObject("nickName", java.net.URLEncoder.encode(nickName));
					msg.putJSONObject("iconUrl", iconUrl);
					msg.putJSONObject("password", password);
//					String teString = msg.getJSONString();
					String data = PostUrl(APPConstant.URL_UPDATE_USERINFO,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					} catch (Exception e) {
						e.printStackTrace();
						rtn = 3;
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * @param context
	 * @param mobile
	 * @param password
	 * @param startPageClickImp
	 */
	public  void statisticsClick(final Context context, final String statType,
			final String keyValue,final StatisticsClickImp startPageClickImp) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					startPageClickImp.StatisticsClickImpCallBack(msg.arg1);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("statType", statType);
					msg.putJSONObject("keyValue",keyValue);
					
					String data = PostUrl(APPConstant.SHARE_STATISTICS,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					int rtn = 0;
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					} catch (Exception e) {
						e.printStackTrace();
						rtn = 3;
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * @param context
	 * @param mobile
	 * @param password
	 * @param startPageClickImp
	 */
	public  void getUserInfo(final Context context, final String mobile,
			final GetUserInfoImp getUserInfoImp) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					getUserInfoImp.GetUserInfoCallBack(msg.arg1,(User)msg.obj);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
					
					String data = PostUrl(APPConstant.URL_GET_USERINFO,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					
					User user = new User();
					int rtn = 0;
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
						Object jsonObject1 = recvmsg.getJSONObject("user");
						user = JSONUtils.fromJson(
								jsonObject1.toString(), User.class);
					} catch (Exception e) {
						e.printStackTrace();
						rtn = 3;
						user = new User();
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					message.obj = user;
					handler.sendMessage(message);
				}
		}.start();
	}
	
	/**
	 * @param context
	 * @param mobile
	 * @param password
	 * @param startPageClickImp
	 */
	public  void recomand4G(final Context context, final String mobile, final String recommendedMobile,
			final IRecomand4G iRecomand4g) {
			final Handler handler = new Handler(new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					iRecomand4g.IRecomand4GCallBack(msg.arg1);
					return false;
				}
			});
			new Thread() {
				@Override
				public void run() {
					com.kinth.mmspeed.network.Message msg = new com.kinth.mmspeed.network.Message();
					msg.putCommonMsgInfo();
					msg.putJSONObject("mobile", mobile);
					msg.putJSONObject("recommendedMobile", recommendedMobile);
					String data = PostUrl(APPConstant.URL_RECOMAND_4G,
							msg.getJSONString());
					com.kinth.mmspeed.network.Message recvmsg = new com.kinth.mmspeed.network.Message(
							data);
					
					int rtn = 0;
					try {
						rtn = TransObject.getInt(recvmsg.getJSONObject("rtn"));
					} catch (Exception e) {
						e.printStackTrace();
						rtn = 0;
					}
					Message message = handler.obtainMessage();
					message.arg1 = rtn;
					handler.sendMessage(message);
				}
		}.start();
	}

	// 检测更新Callback
	public interface UpdateCallback {
		public void updateCallback(int hasNew, VersionInfo versionInfo);
	}

	public interface NetWorkUpdateNicknameCallback {
		public void updateNickNameCallback(int rtn);
	}

	public interface NetWorkgetNew4GMergeRuleCallback {
		public void getNew4GMergeRuleCallback(String flowData);
	}

	// public interface QueryGPRSCallback {
	// public void queryGPRSCallback(String gprsData);
	// }
	public interface QueryGPRSCallback {
		public void queryGPRSCallback(int rtn, UserFlowInfo userFlowInfo);
	}

	@Deprecated
	public interface NetWorkSendDataCallBack {
		public void NetWorkSendDataCallBack(int rtn);
	}

	public interface VerificationCodeCallBack {
		// uid, password, mobile
		public void getVerificationCodeCallBack(int rtn);
	}

	public interface compareVerificationCodeCallBack {
		// uid, password, mobile
		public void getCompareVerificationCodeCallBack(int rtn);
	}

//	/**
//	 * 上传测速结果的接口
//	 */
//	public interface UploadResultCallBack {
//		public void getUploadResultCallBack(int rtn);
//	}

	public interface GetNewsCallback {
		public void getNewsResult(int rtn, List<ArticleInfo> articleInfos);
	}

	public interface ShareStatistics {
		public void shareStatisticsCallback(int rtn);
	}

	public interface getAdPageCallBack {
		public void getAdPageCallback(AdArticleInfo adArticleInfo);
	}

	public interface GetImageCodeCallBack {
		public void getImageCodeResult(String string);
	}
	
	/**
	 * 朋友圈
	 */
	//1、	产生动态信息
	public interface genActiveTimeLineCallBack{
		public void onGenActiveTimeLineCallBack(int rtn,int activeId);
	}
	
	//2、	获取动态
	public interface getActiveTimeLineCallBack{
		public void onGetActiveTimeLineCallBack(int rtn, ArrayList<ActiveWithComments> activeList);
	}
	
	//3、	对某个动态点赞
	public interface praiseActiveTimeLineCallBack{
		public void onPraiseActiveTimeLineCallBack(int rtn);
	}
	
	//4、	对某个动态评论
	public interface commentActiveTimeLineCallBack{
		public void onCommentActiveTimeLineCallBack(int rtn);
	}
	
	//5、	获取某个动态的评论
	public interface getActiveCommentCallBack{
		public void onGetActiveCommentCallBack(List<Comments> commentList);
	}
	
	//查看消息中心是否有新消息
	public interface MessageCentreHasNewCallBack{
		public void onMessageCentreHasNew(int count);
	}
	
	//查看朋友圈是否有新的动态
	public interface MomentsHasNewCallBack{
		public void onMomentsHasNew(int count);
	}

	/**
	 * 流量共享
	 */
	// 获取群组网最大成员数目
	public interface GetMemberMaxCountCallBack {
		public void onGetMemberMaxCountCallBack(int rtn, int maxnumber);
	}

	// 查询群组订购情况
	public interface QueryRichManGroupCallBack {
		public void onQueryRichManGroupCallBack(int rtn, RichManGroup info);
	}

	// 创建群组
	public interface CreateRichManGroupCallBack {
		public void onCreateRichManGroupCallBack(int rtn,String error);
	}

	// 取消群组
	public interface CancelRichManGroupCallBack {
		public void onCancelRichManGroupCallBack(int rtn,String error);
	}

	// 群组成员管理
	public interface ChgRichManMemberCallBack {
		public void onChgRichManMemberCallBack(int rtn,String error);
	}

	// 邀请成员
	public interface InviteMemberCallBack {
		public void onInviteMemberCallBack(int rtn,String error);
	}
//	public interface RegisterMethod {
//		public void registerCallBack(int rtn);
//	}	
	public interface LoginMethod {
		public void loginCallBack(int rtn , UserAccount user);
	}	
	public interface GetSMSCode {
		public void getSMSCallBack(int rtn);
	}	
	public interface LogoutMethod {
		public void logoutMethodCallBack(int rtn);
	}	
	public interface UpdateInfoIml {
		public void updateUserInfoCallBack(int rtn);
	}	
	public interface StatisticsClickImp {
		public void StatisticsClickImpCallBack(int rtn);
	}	
	public interface GetUserInfoImp {
		public void GetUserInfoCallBack(int rtn,User user);
	}	
	public interface MatchFriendsFromContacts {
		public void MatchFriendsFromContactsCallBack(int rtn);
	}	
	public interface Get4GBillBoardImp {
		public void Get4GBillBoardImpCallBack(int rtn,Billboard4GItem billboard4g);
	}	
	public interface GetFlowBillBoardImp {
		public void GetFlowBillBoardImpCallBack(int rtn,List<BillboardFlowItem>billboardFlows);
	}	
	public interface IRecomand4G {
		public void IRecomand4GCallBack(int rtn);
	}	
	public interface IGetUserFriends {
		public void IGetUserFriendsCallBack(int rtn ,List<UserFriend> friends);
	}	
}
