package com.kinth.mmspeed.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kinth.mmspeed.CMCCDownloadActivity;
import com.kinth.mmspeed.FreeFlowEreaNoBindPhoneActivity;
import com.kinth.mmspeed.RMGCommonUserPhonesActivity;
import com.kinth.mmspeed.CommonWebViewActivity;
import com.kinth.mmspeed.FreeFlowEreaActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.SpeedMainActivity;
import com.kinth.mmspeed.account.MainAccountUtil;
import com.kinth.mmspeed.bean.ActiveWithComments;
import com.kinth.mmspeed.bean.Comments;
import com.kinth.mmspeed.bean.Praisers;
import com.kinth.mmspeed.bean.UserAccount;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.friend.ActiveGalleryAdapter;
import com.kinth.mmspeed.friend.MomentsGridPreviewActivity;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.commentActiveTimeLineCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.praiseActiveTimeLineCallBack;
import com.kinth.mmspeed.ui.MyGridView;
import com.kinth.mmspeed.ui.SmiliesEditText;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.Md5Util;
import com.kinth.mmspeed.util.TimeTrans;
import com.kinth.mmspeed.util.ViewHolder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 朋友圈的适配器
 * 
 * @author Sola
 */
public class MomentsAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<ActiveWithComments> activeList;
	private String mobile;
	private boolean isGetMyActive;
	
	private DisplayImageOptions options;
//	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private ClickCallBack callback;

	public MomentsAdapter(Context mContext, boolean isGetMyActive,
			String mobile, DisplayImageOptions options) {
		super();
		this.mContext = mContext;
		this.isGetMyActive = isGetMyActive;
		this.mobile = mobile;
		this.options = options;
		activeList = new ArrayList<ActiveWithComments>();
	}

	public interface ClickCallBack {
		/**
		 * 点赞成功的时候回调
		 * 
		 * @param activeId
		 *            动态的id
		 * @param mobile
		 *            该用户的手机（用户id）
		 */
		public void onClickLike(int activeId, int praiseNum, String praisers,
				boolean praiseByMe);

		/**
		 * 评论的时候回调，把该条评论写入到数据库---->目前只是存到内存中，不存数据库
		 */
		public void onClickComment(int activeId, String commentMobile,
				String commentContent, String commentNickName);
	}

	public void setActiveWithCommentsList(
			ArrayList<ActiveWithComments> activeList) {
		if(activeList == null){
			return;
		}
		this.activeList = activeList;
		notifyDataSetChanged();
	}

	public void addFirstActiveWithCommentsList(ActiveWithComments active){
		if(this.activeList == null){
			this.activeList = new ArrayList<ActiveWithComments>();
		}
		this.activeList.add(0, active);
		notifyDataSetChanged();
	}
	
	public ArrayList<ActiveWithComments> getActiveWithComments() {
		return activeList;
	}

	public void setCallBack(ClickCallBack callback) {
		this.callback = callback;
	}

	@Override
	public int getCount() {
		if(activeList == null){
			return 0;
		}
		return activeList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_moments, parent, false);
		}
		// 用户头像
		final ImageView userIcon = ViewHolder.get(convertView, R.id.iv_user_icon);
		// 发布者
		TextView publisher = ViewHolder.get(convertView, R.id.tv_publisher);
		// 动态内容
		TextView content = ViewHolder.get(convertView, R.id.tv_content);
		ImageView arrowRight = ViewHolder.get(convertView, R.id.iv_arrow_right);//向右的箭头
		// 显示更多--->隐藏
		TextView more = ViewHolder.get(convertView, R.id.tv_more);
		// 显示图片--->隐藏
		MyGridView gridView = ViewHolder.get(convertView, R.id.gridView_pic);
		
		// 发布时间
		TextView publishTime = ViewHolder
				.get(convertView, R.id.tv_publish_time);
		
		// 交互，点赞和评论按钮
		ImageView interaction = ViewHolder.get(convertView,
				R.id.iv_interaction);
		
		//点赞的布局
		final LinearLayout clickPraise = ViewHolder.get(convertView, R.id.llt_like_nickname);
		// likeIcon
		final ImageView likeIcon = ViewHolder.get(convertView, R.id.iv_likeicon);
		// 集赞的人昵称
		final TextView nameOfLike = ViewHolder.get(convertView,
				R.id.tv_nick_name_of_like);
		
		// 评论的布局，包含点赞
		final LinearLayout commentLayout = ViewHolder.get(convertView,
						R.id.llt_comment_layout);
		// 评论
		final TextView comments = ViewHolder.get(convertView, R.id.tv_comments);

		final ActiveWithComments activeWithComments = activeList.get(position);
		final int activeId = activeWithComments.getActive().getActiveId();

		// -------------跟动态有关的-----------------
		// 加载头像
		final String md5Path = APPConstant.IMAGE_PERSISTENT_CACHE + File.separator + Md5Util.md5s(activeWithComments.getActive()
				.getIconUrl());//取md5后的图片路径
		File imageFile = new File(md5Path);
		if(imageFile.exists()){//存在
			// 加载图片
			ImageLoader.getInstance().displayImage("file:///" + md5Path, userIcon, options, null);
		}else{//本地不存在，去下载，然后显示
			if(!TextUtils.isEmpty(activeWithComments.getActive().getIconUrl())){
				//先显示默认的图片？？ TODO
				HttpUtils http = new HttpUtils();
				HttpHandler<File> handler = http.download(activeWithComments.getActive()
						.getIconUrl(),md5Path,
				    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				    new RequestCallBack<File>() {

				        @Override
				        public void onStart() {
				        }

				        @Override
				        public void onLoading(long total, long current, boolean isUploading) {
				        }

				        @Override
				        public void onSuccess(ResponseInfo<File> responseInfo) {
//				            testTextView.setText("downloaded:" + responseInfo.result.getPath());
				        	ImageLoader.getInstance().displayImage("file:///" + md5Path, userIcon, options, null);
				        }

				        @Override
				        public void onFailure(HttpException error, String msg) {
				        	ImageLoader.getInstance().displayImage(activeWithComments.getActive()
				    				.getIconUrl(), userIcon, options, null);
				        }
				});
			}else{//显示没有url头像
				ImageLoader.getInstance().displayImage(activeWithComments.getActive()
	    				.getIconUrl(), userIcon, options, null);
			}
		}
		userIcon.setOnClickListener(new OnClickListener() {//头像点击后预览头像
			
			@Override
			public void onClick(View arg0) {
				if(TextUtils.isEmpty(activeWithComments.getActive().getIconUrl())){
					return;
				}
				ArrayList<String> url = new ArrayList<String>();
				url.add(activeWithComments.getActive().getIconUrl());
				Intent intent = new Intent(mContext,MomentsGridPreviewActivity.class);
				intent.putStringArrayListExtra(MomentsGridPreviewActivity.INTENT_IMAGES_TO_PREVIEW, url);//图片url
				intent.putExtra(MomentsGridPreviewActivity.INTENT_IMAGE_POSITION, 0);//当前加载位置
				mContext.startActivity(intent);
				
			}
		});

		String publisherMobile = activeWithComments.getActive().getMobile();//发这条动态的号码
		//显示的号码
		String publisherName = activeWithComments.getActive().getNickName();
		
		publisher.setText(TextUtils.isEmpty(publisherName)? publisherMobile : publisherName);
		publishTime.setText(TimeTrans.TransTime(activeWithComments.getActive()
				.getCreateTime(), true));

		content.setText(activeWithComments.getActive().getContent());
		//设置右箭头的可见性
		switch(ActiveType.getEnumFromString(activeWithComments.getActive().getActiveType())){
		case _4G:
		case iPhone5S_5C:
		case DownLoadApp:
		case QueryGPRSFlow:
		case DonateGPRSFlow:
		case OpenDonateFlow:
		case CreateShareGroup:
		case JoinShareGroup:
		case SpeedTest:
		case FlowGift:
		case FreeFlow:
		case FlowBeggar:
		case IamTuHao:
			arrowRight.setVisibility(View.VISIBLE);
			break;
		case NULL:
		case FlowGroupon://流量团购
		case PersonalActive:
		default:
			arrowRight.setVisibility(View.INVISIBLE);
			break;
		}
		//设置超链接为可点击状态
//		content.setMovementMethod(LinkMovementMethod.getInstance());
//		CharSequence text = content.getText();

		//mod at @2014-08-08
//		content.setText(setStyle(text, activeWithComments.getActive()
//				.getActiveType()));
		content.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				switch (ActiveType.getEnumFromString(activeWithComments.getActive().getActiveType())) {
				case _4G:// 跳转到4G补换卡页面
					Intent intent = new Intent(mContext,
							CommonWebViewActivity.class);
					intent.putExtra(CommonWebViewActivity.INTENT_TITLE,
							"4G引导");
					intent.putExtra(CommonWebViewActivity.INTENT_URL,
							APPConstant._4GBUHUANKA);
					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
					break;
				case iPhone5S_5C:
					Intent intent1 = new Intent(mContext,
							CommonWebViewActivity.class);
					intent1.putExtra(
							CommonWebViewActivity.INTENT_TITLE,
							"iPhone5S/5c");
					intent1.putExtra(CommonWebViewActivity.INTENT_URL,
							APPConstant.IPHONE5S_5C);
					mContext.startActivity(intent1);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
					break;
				case DownLoadApp:
					Intent intent2 = new Intent(mContext,
							CMCCDownloadActivity.class);
					mContext.startActivity(intent2);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
					break;
				case QueryGPRSFlow:// 查询剩余流量，且在好用中排名前5/倒5,点击“倒数第2位”则跳转到送流量转赠页面。
					Intent intent4 = new Intent(mContext,
							RMGCommonUserPhonesActivity.class);
					intent4.putExtra(
							RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
							APPConstant.ServiceID.I_AM_RICH_MAN);
					mContext.startActivity(intent4);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);

					break;
				case DonateGPRSFlow:// 我向梁七转赠了××M流量”，在陈六向梁七转赠了流量/调整了转赠限额后显示，点击“转赠”两字则跳转到流量转赠页面。
				case OpenDonateFlow:// 跳转到
					Intent intent3 = new Intent(mContext,
							RMGCommonUserPhonesActivity.class);
					intent3.putExtra(
							RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
							APPConstant.ServiceID.I_AM_RICH_MAN);
					mContext.startActivity(intent3);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
					break;
				case CreateShareGroup:// 发起共享，后显示，点击“流量共享”4字跳转到流量共享页面。
				case JoinShareGroup:// 加入共享群
					Intent intent5 = new Intent(mContext,
							RMGCommonUserPhonesActivity.class);
					intent5.putExtra(
							RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
							APPConstant.ServiceID.DO_RICH_MAN_TOGETHER);
					mContext.startActivity(intent5);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
					break;
				case SpeedTest:// 跳转到测速的页面 TODO
					Intent intent6 = new Intent(mContext,
							SpeedMainActivity.class);
					intent6.putExtra("CheckTab2", true);
					mContext.startActivity(intent6);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
					break;
				case FlowGift://流量红包
					Intent intent7 = new Intent(mContext,CommonWebViewActivity.class);
					intent7.putExtra(CommonWebViewActivity.INTENT_TITLE, "流量红包");
					intent7.putExtra(CommonWebViewActivity.INTENT_URL, APPConstant.FLOW_RED_PACKETS);
					mContext.startActivity(intent7);
					((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					break;
				case FreeFlow://免流量专区
					UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
					if (userPhoneDBService.getAllPhoneAmount() >= 1) {
						if (!TextUtils.isEmpty(JUtil.getSharePreStr(mContext,
								APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE))) {// 当前号码不为空
							Intent intent8 = new Intent(mContext, FreeFlowEreaActivity.class);
							mContext.startActivity(intent8);
							((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
							return;
						}
					}
					// 没有绑定号码
					Intent intent9 = new Intent(mContext, FreeFlowEreaNoBindPhoneActivity.class);
					mContext.startActivity(intent9);
					((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					break;
				case FlowGroupon://流量团购
					break;
				case FlowBeggar://流量少于10%且少于100M--点击跳转到流量红包页面
					Intent intent11 = new Intent(mContext,CommonWebViewActivity.class);
					intent11.putExtra(CommonWebViewActivity.INTENT_TITLE, "流量红包");
					intent11.putExtra(CommonWebViewActivity.INTENT_URL, APPConstant.FLOW_RED_PACKETS);
					mContext.startActivity(intent11);
					((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					break;
				case IamTuHao://流量大于30%且大于1G--打开评论，默认显示“亲，我的流量快用完了，土豪给咱转赠点流量吧，在移动流量仪-发现-流量转赠就能轻松搞定啦。”
					String defaultComment = "亲，我的流量快用完了，土豪给咱转赠点流量吧，在移动流量仪-发现-流量转赠就能轻松搞定啦。";
					comment(v,activeId,comments,commentLayout,activeWithComments,defaultComment);
					break;
				case PersonalActive://私人信息
				case NULL:
				default:
					break;
				}
			}
		});
		
		//add @2014-09-28 朋友圈显示图片
		if(TextUtils.isEmpty(activeWithComments.getActive().getPicUrl())){//空
			gridView.setVisibility(View.GONE);
		}else {
			gridView.setVisibility(View.VISIBLE);
			//分割图片地址
			String[] array = activeWithComments.getActive().getPicUrl().split(",");
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < array.length; i++) {
				list.add(array[i]);
			}
			final ActiveGalleryAdapter adapter = new ActiveGalleryAdapter(mContext, list);
			if(list.size() == 1){//只有一项时，去掉gridView限制
				gridView.setGravity(Gravity.LEFT);
				gridView.setNumColumns(1);
			}else{
				gridView.setGravity(Gravity.CENTER_HORIZONTAL);
				gridView.setNumColumns(GridView.AUTO_FIT);
			}
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					ArrayList<String> url = adapter.getItem(arg2);
					Intent intent = new Intent(mContext,MomentsGridPreviewActivity.class);
					intent.putStringArrayListExtra(MomentsGridPreviewActivity.INTENT_IMAGES_TO_PREVIEW, url);//图片url
					intent.putExtra(MomentsGridPreviewActivity.INTENT_IMAGE_POSITION, arg2);//当前加载位置
					mContext.startActivity(intent);
				}
				
			});
		}
		
		//-------------------end--------------------

		// ----------跟交互有关的--------------------
		int state = 0;
		if (activeWithComments.getActive().isPraiseByMe()) {// 本人已经点赞
			state++;
			clickPraise.setVisibility(View.VISIBLE);
			likeIcon.setVisibility(View.VISIBLE);
			likeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
			
			String convertNames = convertMobileToName(activeWithComments.getActive().getPraisers());
			nameOfLike.setText(convertNames);
		} else {// 本人没有点赞，有其他人点赞
			if (activeWithComments.getActive().getPraiseNum() == 0) {// 其他人也没有点赞，把likeIcon隐藏
				clickPraise.setVisibility(View.GONE);
				likeIcon.setVisibility(View.GONE);
				nameOfLike.setText("");
			} else {// 其他人有点赞
				state++;
				clickPraise.setVisibility(View.VISIBLE);
				likeIcon.setVisibility(View.VISIBLE);
				likeIcon.setImageResource(R.drawable.likeicon);
				
				String convertNames = convertMobileToName(activeWithComments.getActive().getPraisers());
				nameOfLike.setText(convertNames);
			}
		}

		if (activeWithComments.getComments() != null) {
			// 设置评论
			StringBuffer sb = new StringBuffer();
			for (Comments comment : activeWithComments.getComments()) {
				state++;
				String resultName = comment.getNickName();
				sb.append(TextUtils.isEmpty(resultName) ? comment.getCommentMobile() : resultName).append("：");
				sb.append(comment.getCommentContent()).append("\n");
			}
			comments.setText(sb.toString());
		}
		if (state == 0) {// 没有人评论，也没有人点赞
			commentLayout.setVisibility(View.GONE);
		} else {
			commentLayout.setVisibility(View.VISIBLE);
		}

		// ----------跟操作有关的----------------------
		if (isGetMyActive) {// 我的动态
			interaction.setVisibility(View.GONE);
		} else {
			interaction.setVisibility(View.VISIBLE);
			interaction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					View view = LayoutInflater.from(mContext).inflate(
							R.layout.popupwindow_friend_comment_layout, null);
					LinearLayout commentLike = (LinearLayout) view
							.findViewById(R.id.llt_comment_like);
					LinearLayout commentWrite = (LinearLayout) view
							.findViewById(R.id.llt_comment_write);
					// 创建PopupWindow对象
					final PopupWindow popupWindow = new PopupWindow(view, 400,
							66, false);
					popupWindow.setBackgroundDrawable(new BitmapDrawable());
					// 设置点击窗口外边窗口消失
					popupWindow.setOutsideTouchable(true);
					// 设置此参数获得焦点，否则无法点击
					popupWindow.setFocusable(true);
					// 点击事件
					commentLike.setOnClickListener(new OnClickListener() {// 点赞

								@Override
								public void onClick(View v) {
									if (popupWindow != null
											&& popupWindow.isShowing()) {
										// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
										popupWindow.dismiss();
									}
									if (activeWithComments.getActive()
											.isPraiseByMe()) {// 已经赞过
										likeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
										return;
									}
									new AsyncNetworkManager().praiseActiveTimeLine(
											mContext, activeId, mobile,
											new praiseActiveTimeLineCallBack() {

												@Override
												public void onPraiseActiveTimeLineCallBack(
														int rtn) {
													switch (rtn) {
													case 1:// 点赞成功，更新数据库和页面
														if (commentLayout
																.getVisibility() == View.GONE) {
															commentLayout
																	.setVisibility(View.VISIBLE);
														}
														clickPraise.setVisibility(View.VISIBLE);
														likeIcon.setVisibility(View.VISIBLE);
														likeIcon.setImageResource(R.drawable.friendactivity_comment_likeicon_havon);
														
														// 更新praisers名单
														String nickName = null;
														UserAccount account = MainAccountUtil.getCurrentAccount(mContext);
														if(account != null){
															nickName = TextUtils.isEmpty(account.getNickName()) ? mobile : account.getNickName();
														}else{
															nickName = mobile;
														}
														
														if (activeWithComments.getActive().getPraiseNum() == 0) {// 目前没有人点
															JSONArray json = new JSONArray();
															JSONObject object2 = new JSONObject();
															try {
																object2.put("mobile", mobile); 
																object2.put("nickName",nickName);
																json.put(object2);
															} catch (JSONException e) {
																e.printStackTrace();
															}
															activeWithComments.getActive().setPraisers(json.toString());
														} else {// 目前已经有人点
															JSONArray jsonArray = null;
															try {
																jsonArray = new JSONArray(activeWithComments.getActive()
																		.getPraisers());
															} catch (JSONException e1) {
																jsonArray = new JSONArray();
																e1.printStackTrace();
															}
															JSONObject object2 = new JSONObject();
															try {
																object2.put("mobile", mobile); 
																object2.put("nickName",nickName);
															} catch (JSONException e) {
																e.printStackTrace();
															}
															jsonArray.put(object2);
															activeWithComments.getActive().setPraisers(jsonArray.toString());
														}
														
														// 点赞人数加1
														activeWithComments.getActive().setPraiseNum(activeWithComments.getActive().getPraiseNum()+1);
														// 设置已经赞过
														activeWithComments.getActive().setPraiseByMe(true);
														nameOfLike.setText((TextUtils.isEmpty(nameOfLike.getText()) ? "" : nameOfLike.getText()+",")+(TextUtils.isEmpty(nickName) ? mobile : nickName));
														
														if (callback != null) {// 保存到数据库
															callback.onClickLike(
																	activeId,
																	activeWithComments
																			.getActive()
																			.getPraiseNum(),
																	activeWithComments
																			.getActive()
																			.getPraisers(),
																	true);
														}
														break;
													case 0:// 已经点过赞
														Toast.makeText(
																mContext,
																"已经点赞过",
																Toast.LENGTH_LONG)
																.show();
														break;
													case -1:// -1动态不存在
														Toast.makeText(
																mContext,
																"动态不存在",
																Toast.LENGTH_LONG)
																.show();
														break;
													case -2:// -2其他原因
													default:
														Toast.makeText(
																mContext,
																"点赞失败",
																Toast.LENGTH_LONG)
																.show();
														break;
													}
												}
											});
								}
							});
					commentWrite.setOnClickListener(new OnClickListener() {// 评论

								@Override
								public void onClick(View v) {
									if (popupWindow != null
											&& popupWindow.isShowing()) {
										// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
										popupWindow.dismiss();
									}
									// if(callback != null){
									// callback.onClickComment();
									// }
									comment(v,activeId,comments,commentLayout,activeWithComments,"");
								}
							});

					// 显示窗口
					popupWindow.showAsDropDown(v, -popupWindow.getWidth(),
							-(v.getHeight() + popupWindow.getHeight()) / 2);
				}

			});
		}

		return convertView;
	}

	/**
	 * 不同动态类型设置不同的响应
	 */
	private SpannableStringBuilder setStyle(CharSequence text, String type) {
		SpannableStringBuilder style = new SpannableStringBuilder(text);
		style.clearSpans();// should clear old spans
		
		String compileStr = "^.*$";//文字选择的正则表达式
//		switch (ActiveType.getEnumFromString(type)){
//		case _4G:
//			compileStr = "4G";
//			break;
//		case CreateShareGroup:
//			compileStr = "(流量)?共享";
//			break;
//		case DonateGPRSFlow:
//			compileStr = "转赠";
//			break;
//		case DownLoadApp:
//			compileStr = "App(s)?|(?<=:|：)(.*)";//"[App(s)?,(?<=:|：)(.*)]";
//			break;
//		case iPhone5S_5C:
//			compileStr = "iPhone5S[/5[cC]]?";
//			break;
//		case JoinShareGroup:
//			compileStr = "流量共享";
//			break;
//		case OpenDonateFlow:
//			compileStr = "转赠";
//			break;
//		case QueryGPRSFlow://查询流量
//			compileStr = "倒数第\\d{1,}位";
//			break;
//		case SpeedTest:
//			compileStr = "网络测速";
//			break;
//		case FlowGift://流量红包
//			break;
//		case FreeFlow://免流量专区
//			break;
//		case NULL:
//		default:
//			break;
//		}
		
		Pattern pattern = Pattern.compile(compileStr);
		Matcher matcher = pattern.matcher(style);
		
		while (matcher.find()) {
			style.setSpan(new NoLineClickSpan(type), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return style;
	}

	private class NoLineClickSpan extends ClickableSpan {
		String type;

		public NoLineClickSpan(String type) {
			super();
			this.type = type;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
//			ds.setColor(mContext.getResources().getColor(R.color.blue_letter));//字体颜色
//			if(type.equals(ActiveType.DownLoadApp)){
//				ds.setColor(mContext.getResources().getColor(R.color.black));
//			}
			ds.setUnderlineText(false);
		}

		@Override
		public void onClick(View widget) {
			switch (ActiveType.getEnumFromString(type)) {
			case _4G:// 跳转到4G补换卡页面
				Intent intent = new Intent(mContext,
						CommonWebViewActivity.class);
				intent.putExtra(CommonWebViewActivity.INTENT_TITLE,
						"4G补换卡");
				intent.putExtra(CommonWebViewActivity.INTENT_URL,
						APPConstant._4GBUHUANKA);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				break;
			case iPhone5S_5C:
				Intent intent1 = new Intent(mContext,
						CommonWebViewActivity.class);
				intent1.putExtra(
						CommonWebViewActivity.INTENT_TITLE,
						"iPhone5S/5c");
				intent1.putExtra(CommonWebViewActivity.INTENT_URL,
						APPConstant.IPHONE5S_5C);
				mContext.startActivity(intent1);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				break;
			case DownLoadApp:
				Intent intent2 = new Intent(mContext,
						CMCCDownloadActivity.class);
				mContext.startActivity(intent2);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				break;
			case QueryGPRSFlow:// 查询剩余流量，且在好用中排名前5/倒5,点击“倒数第2位”则跳转到送流量转赠页面。
				Intent intent4 = new Intent(mContext,
						RMGCommonUserPhonesActivity.class);
				intent4.putExtra(
						RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
						APPConstant.ServiceID.I_AM_RICH_MAN);
				mContext.startActivity(intent4);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);

				break;
			case DonateGPRSFlow:// 我向梁七转赠了××M流量”，在陈六向梁七转赠了流量/调整了转赠限额后显示，点击“转赠”两字则跳转到流量转赠页面。
			case OpenDonateFlow:// 跳转到
				Intent intent3 = new Intent(mContext,
						RMGCommonUserPhonesActivity.class);
				intent3.putExtra(
						RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
						APPConstant.ServiceID.I_AM_RICH_MAN);
				mContext.startActivity(intent3);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				break;
			case CreateShareGroup:// 发起共享，后显示，点击“流量共享”4字跳转到流量共享页面。
			case JoinShareGroup:// 加入共享群
				Intent intent5 = new Intent(mContext,
						RMGCommonUserPhonesActivity.class);
				intent5.putExtra(
						RMGCommonUserPhonesActivity.INTENT_SERVICE_ID,
						APPConstant.ServiceID.DO_RICH_MAN_TOGETHER);
				mContext.startActivity(intent5);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				break;
			case SpeedTest:// 跳转到测速的页面 TODO
				Intent intent6 = new Intent(mContext,
						SpeedMainActivity.class);
				intent6.putExtra("CheckTab2", true);
				mContext.startActivity(intent6);
				((Activity) mContext).overridePendingTransition(
						R.anim.in_from_right, R.anim.out_to_left);
				break;
			case FlowGift://流量红包
				Intent intent7 = new Intent(mContext,CommonWebViewActivity.class);
				intent7.putExtra(CommonWebViewActivity.INTENT_TITLE, "流量红包");
				intent7.putExtra(CommonWebViewActivity.INTENT_URL, APPConstant.FLOW_RED_PACKETS);
				mContext.startActivity(intent7);
				((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;
			case FreeFlow://免流量专区
				UserPhoneDBService userPhoneDBService = new UserPhoneDBService(mContext);
				if (userPhoneDBService.getAllPhoneAmount() >= 1) {
					if (!TextUtils.isEmpty(JUtil.getSharePreStr(mContext,
							APPConstant.SP_NAME, APPConstant.SP_FIELD_CURRENT_PHONE))) {// 当前号码不为空
						Intent intent8 = new Intent(mContext, FreeFlowEreaActivity.class);
						mContext.startActivity(intent8);
						((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						return;
					}
				}
				// 没有绑定号码
				Intent intent9 = new Intent(mContext, FreeFlowEreaNoBindPhoneActivity.class);
				mContext.startActivity(intent9);
				((Activity) mContext).overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;
			case NULL:
			default:
				break;
			}
		}
	}
	
	//Android-Universal-Image-Loader  listener
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	/**
	 * 把电话号码转换到通讯录名称---点赞的数据
	 * @param jsonArrayStr
	 */
	private String convertMobileToName(String jsonArrayStr) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(jsonArrayStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(jsonArray == null){
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				Praisers praiser = gson.fromJson(jsonArray.getString(i),
						Praisers.class);
				if(praiser != null){//没有解析到就跳过该人
					//显示的号码
					sb.append(TextUtils.isEmpty(praiser.getNickName())? praiser.getMobile() : praiser.getNickName()).append(",");//通讯录匹配失败就用返回的昵称，昵称也没有就用手机号码
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		String subStr = sb.toString();
		if (!TextUtils.isEmpty(subStr)) {
			subStr = subStr.substring(0, subStr.length() - 1);// 去掉最后一个（,）逗号
		}
		return subStr;
	}
	
//	/**
//	 * 通过号码查询在本地通讯录的名称
//	 */
//	private String queryNameByMobile(String phone){
//		//取本地通讯录名称
//		ContractInfo item = null;
//		try {
//			item = db.findFirst(Selector.from(ContractInfo.class).where("phoneNumber","=",phone));
//		} catch (DbException e) {
//			e.printStackTrace();
//		}
//		//显示的号码
//		return item == null ? null : item.getContractName();//通讯录匹配失败就用返回的昵称，昵称也没有就用手机号码
//	}
	
	/**
	 * 评论
	 * @param v
	 * @param activeId
	 * @param comments
	 * @param commentLayout
	 * @param activeWithComments
	 * @param defaultComment 默认评论
	 */
	private void comment(View v,final int activeId,final TextView comments,final LinearLayout commentLayout,final ActiveWithComments activeWithComments,String defaultComment){
		// 弹出输入的框
		View view = LayoutInflater
				.from(mContext)
				.inflate(R.layout.popupwindow_friend_comment_input,
						null);
		final SmiliesEditText inputComment = (SmiliesEditText) view
				.findViewById(R.id.et_input_comment);
		Button commentWrite = (Button) view
				.findViewById(R.id.bt_send_comment);
		inputComment.setText(defaultComment);
		inputComment
				.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v,
							MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:// 变成激活的状态
							v.setBackgroundResource(R.drawable.input_bar_bg_active);
							break;
						}
						return false;
					}
				});
		// 创建PopupWindow对象
		final PopupWindow popInputComment = new PopupWindow(
				view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		popInputComment
				.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		popInputComment.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		popInputComment.setFocusable(true);
		popInputComment
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popInputComment
				.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		popInputComment.showAtLocation(v,
				Gravity.BOTTOM, 0, 0);

		// 发送的事件
		commentWrite
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (popInputComment != null
								&& popInputComment
										.isShowing()) {
							// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
							popInputComment
									.dismiss();
						}
						final String commentStr = inputComment
								.getText()
								.toString();
						if (TextUtils
								.isEmpty(commentStr)) {
							return;
						}
						// 上传评论
						new AsyncNetworkManager()
								.commentActiveTimeLine(
										mContext,
										activeId,
										mobile,
										commentStr,
										new commentActiveTimeLineCallBack() {

											@Override
											public void onCommentActiveTimeLineCallBack(
													int rtn) {
												switch (rtn) {
												case 1:// 评论成功，更新界面
													if (commentLayout
															.getVisibility() == View.GONE) {
														commentLayout
																.setVisibility(View.VISIBLE);
													}
													StringBuffer sb = new StringBuffer();
													sb.append(comments
															.getText()
															.toString());
													
													String nickName = null;
													if(MainAccountUtil.getCurrentAccount(mContext)!=null){
														nickName= MainAccountUtil
																.getCurrentAccount(
																		mContext)
																.getNickName();
													}
													sb.append(
															TextUtils
																	.isEmpty(nickName) ? mobile
																	: nickName)
															.append("：")
															.append(commentStr);
													comments.setText(sb
															.toString());

													Comments comment = new Comments();
													comment.setActiveId(activeId);
													comment.setCommentMobile(mobile);
													comment.setCommentContent(commentStr);
													comment.setNickName(TextUtils
															.isEmpty(nickName) ? mobile
															: nickName);
													activeWithComments
															.getComments()
															.add(comment);// 更新内存数据
													// if
													// (callback
													// !=
													// null)
													// {
													// callback.onClickComment(activeId,mobile,commentStr,TextUtils.isEmpty(nickName)?mobile:nickName);//
													// 回调
													// }
													break;
												case 0:
												default:
													Toast.makeText(
															mContext,
															"评论失败",
															Toast.LENGTH_LONG)
															.show();
													break;
												}
											}
										});
					}
				});
	
	}
	
}
