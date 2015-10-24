package com.kinth.mmspeed;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.Constants;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.JUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

@ContentView(R.layout.activity_share_award)
public class ShareAwardActivity extends BaseActivity implements IWeiboHandler.Response{
    /** 微博分享的接口实例 */
    private IWeiboShareAPI mWeiboShareAPI;
	private IWXAPI api;
	
	@ViewInject(R.id.temp_iv)
	private ImageView mImageView;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;
	
	@ViewInject(R.id.nav_right_text)
	private TextView tvRight;
	
	@ViewInject(R.id.tv_instruction)
	private TextView tvInstruction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tvTittle.setText("推荐有礼");
		tvRight.setText("推荐结果");
		tvInstruction.setText(Html.fromHtml("<u>" + "查看活动规则"+ "</u>"));
		api = WXAPIFactory.createWXAPI(this, APPConstant.WEXIN_APP_KEY);
		api.registerApp(APPConstant.WEXIN_APP_KEY);
		
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
		mWeiboShareAPI.registerApp();
		
			// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
	        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
	        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
	        // 失败返回 false，不调用上述回调
	        if (savedInstanceState != null) {
	            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
	        }
	        
	}
	
	/**
	 * 分享到wechat
	 * @param v
	 */
	@OnClick(R.id.rlt_share_wechat)
	public void shareWechatClick(View v) {
		if (api.isWXAppInstalled()
				&& api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = "http://t.cn/Rvzg8je";
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "推荐好友获30元，晒网速再获5元！";
			msg.description = "推荐好友赢礼包，晒网速集赞有奖！";
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.thumbData = Util.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
//			req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
			req.scene =  SendMessageToWX.Req.WXSceneSession;
			ApplicationController.getInstance().setShareWXType(1);
			api.sendReq(req);
		}else {
			JUtil.showMsg(ShareAwardActivity.this, "微信客户端未安装，请先安装微信");
		}
	}
	
	/**
	 * 分享到朋友圈
	 * @param v
	 */
	@OnClick(R.id.rlt_share_wechatmoments)
	public void shareWechatMomentsClick(View v) {
		if (api.isWXAppInstalled()
				&& api.isWXAppSupportAPI()) {
			WXWebpageObject webpage = new WXWebpageObject();
			webpage.webpageUrl = "http://t.cn/Rvzg8je";
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = "推荐好友获30元，晒网速再获5元！";
			msg.description = "推荐好友赢礼包，晒网速集赞有奖！";
			Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			msg.thumbData = Util.bmpToByteArray(thumb, true);
			
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
//			req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
			req.scene =  SendMessageToWX.Req.WXSceneTimeline;
			ApplicationController.getInstance().setShareWXType(0);
			api.sendReq(req);
		}else {
			JUtil.showMsg(ShareAwardActivity.this, "微信客户端未安装，请先安装微信");
		}
		
	}
	/**
	 * 分享到新浪微博
	 * @param v
	 */
	@OnClick(R.id.rlt_share_sina)
	public void shareSinaOnClick(View v) {
//		startActivityForResult(new Intent(Intent.ACTION_PICK,
//				ContactsContract.Contacts.CONTENT_URI), 0);
		 initializeWeibo();
	}
	/**
	 * 分享到短信
	 * @param v
	 */
	@OnClick(R.id.rlt_share_message)
	public void shareShortMsgOnClick(View v) {
		Uri smsToUri = Uri.parse("smsto:");    
	    Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );  
	    mIntent.putExtra("sms_body", "2014.5.20！关心TA，就用“移动流量仪”吧！推荐五个好友安装，即可获得30元移动优惠积分！晒网速再送5元！管理多个号码流量、掌控准确的2G/3G/4G分类流量情况！流量仪，您贴心的流量管家！访问http://ydlly.cn，或在AppStore、MM商城搜索“移动流量仪”即可安装！别忘了注册时在推荐人中填我的手机号哦！ 具体活动内容见http://t.cn/Rvzg8je ");  
	    startActivity( mIntent );
	    
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
     * 初始化 UI 和微博接口实例 。
     */
    private void initializeWeibo() {
        // 创建微博 SDK 接口实例
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
    	
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI(); 
        
        // 如果未安装微博客户端，设置下载微博对应的回调
        if (!isInstalledWeibo) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                    Toast.makeText(ShareAwardActivity.this, 
                           "取消下载", 
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
             // 注册到新浪微博
//             mWeiboShareAPI.registerApp();
             
             try {
                 // 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
                 if (mWeiboShareAPI.checkEnvironment(true)) {                    
                	 sendPageMessage();
                 }
             } catch (WeiboShareException e) {
                 e.printStackTrace();
                 Toast.makeText(ShareAwardActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
             }
        }
    /**
     * 创建多媒体（网页）消息对象。
     * 
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "推荐好友获30元，晒网速再获5元！";
        mediaObject.description = "2014.5.20！关心TA，就用“移动流量仪”吧！推荐五个好友安装，即可获得30元移动优惠积分！晒网速再送5元！管理多个号码流量、掌控准确的2G/3G/4G分类流量情况！流量仪，您贴心的流量管家！";
        
        // 设置 Bitmap 类型的图片到视频对象里
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(ShareAwardActivity.this.getResources(), R.drawable.ic_launcher), 80,80, false);
        
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = "http://t.cn/Rvzg8je";
//        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }
	@Override
	public void onResponse(BaseResponse arg0) {
		 switch (arg0.errCode) {
	        case WBConstants.ErrorCode.ERR_OK:
	            Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
//	            AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
//	            asyncNetworkManager.shareStatistics(ShareAwardActivity.this, "shareActivity ","sinaweibo");
	            break;
	        case WBConstants.ErrorCode.ERR_CANCEL:
	            Toast.makeText(this, "取消分享", Toast.LENGTH_LONG).show();
	            break;
	        case WBConstants.ErrorCode.ERR_FAIL:
	            Toast.makeText(this, 
	                   "分享出错" + "Error Message: " + arg0.errMsg, 
	                    Toast.LENGTH_LONG).show();
	            break;
	        }
	}
	
    /**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
	 /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * 
     */
    private void sendPageMessage() {
        
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
    	  WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
    	  weiboMessage.textObject  = getTextObj();
         weiboMessage.mediaObject = getWebpageObj();
        
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
        
    	
    }
    
     /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "2014.5.20！关心TA，就用“移动流量仪”吧！推荐五个好友安装，即可获得30元移动优惠积分！晒网速再送5元！管理多个号码流量、掌控准确的2G/3G/4G分类流量情况！流量仪，您贴心的流量管家！";
        return textObject;
    }
    @OnClick(R.id.tv_instruction)
    public void instructionOnClick(View v){
    	Intent intent = new Intent(ShareAwardActivity.this,ShareInstructionActivity.class);
    	startActivity(intent);
    	rightInAnimation();
    }
    
    @Override
	public void rightTextOnClick(View v) {
    	Intent intent = new Intent(ShareAwardActivity.this,ShareAwardResultActivity.class);
    	startActivity(intent);
    	rightInAnimation();
    	
	}
    
    public class AutoSendMessageBroadcastReceiver extends BroadcastReceiver { 
    	@Override public void onReceive(Context context, Intent intent) { // TODO Auto-generated method stub 
    		switch (getResultCode()) {   
    			case -1:  Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();  
    				break;   
    			case 0: Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();  
    				break;  
    			default:   
    				break;  
    		} 
    		}  
    }
    private static boolean isWXAppInstalledAndSupported(Context context,
			IWXAPI api) {
		// LogOutput.d(TAG, "isWXAppInstalledAndSupported");
		boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
				&& api.isWXAppSupportAPI();
		if (!sIsWXAppInstalledAndSupported) {
			JUtil.showMsg(context, "微信客户端未安装，请确认");
		}

		return sIsWXAppInstalledAndSupported;
	}
}
