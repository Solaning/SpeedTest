package com.kinth.mmspeed;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.mmspeed.bean.UserPhone;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.db.UserPhoneDBService;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.VerificationCodeCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.compareVerificationCodeCallBack;
import com.kinth.mmspeed.util.JUtil;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 添加绑定号码--已去除图形验证码
 */
@ContentView(R.layout.activity_bind_phone)
public class BindPhoneNumActivity extends BaseActivity {
	public static final String INTENT_TARGET_CLASS_NAME = APPConstant.INTENT_TARGET_CLASS_NAME;//最终需要放到intent中跳转的activity
	public static final String ACTION_FIRST_BIND = "ACTION_BIND";//绑定号码的广播
	public static final String BIND_MOBILE = "BIND_MOBILE";
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;//返回
	
	@ViewInject(R.id.nav_tittle)
	private TextView title;
	
	@ViewInject(R.id.et_phone)
	private EditText et_phoneNum;
	
	@ViewInject(R.id.et_identifying_code)
	private EditText et_code;
	
	@ViewInject(R.id.et_remark)
	private EditText et_remark;
	
	@ViewInject(R.id.et_recommender)
	private EditText edtRecommender;
	
	@ViewInject(R.id.llt_recommender)
	private LinearLayout lltRecommender;
	
	@ViewInject(R.id.ed_nickName)
	private EditText ed_nickName;
	
	@ViewInject(R.id.bind_phone_rlt0)
	private LinearLayout phoneRlt;
	
	@ViewInject(R.id.bind_phone_rlt1)
	private RelativeLayout codeRlt;
	
	@ViewInject(R.id.btn_getCode)
	private Button getCodeBtn;
	
	@ViewInject(R.id.iv_code)
	private ImageView mImageView;
	
	@ViewInject(R.id.et_imagecode)
	private EditText edtImageCode;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){// 左上返回
		back();
	}
	
	private Context mContext;
	private String targetClassName;//目标跳转activity名称
	private String phone;
	private String recommendMobile = "";
	private TimeCount time;
	private UserPhoneDBService userPhoneDBService;
	private ProgressDialog mProgressDialog = null;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		
		title.setText(getResources().getString(R.string.tittle_add_ph));
		targetClassName = getIntent().getStringExtra(INTENT_TARGET_CLASS_NAME);
		initDisplayOptions();
		userPhoneDBService = new UserPhoneDBService(mContext);
		int count = userPhoneDBService.getAllPhoneAmount();
		if (count > 0) {//有号码的时候把推荐人隐去
			lltRecommender.setVisibility(View.GONE);  
		}
		time = new TimeCount(90000, 1000);
	}
		//隐去图形验证码
//		getImageCode();
//		mImageView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				getImageCode();
//			}
//		});
	
	/**
	 * 获取手机验证码
	 * @param v
	 */
	@OnClick(R.id.btn_sendCode)
	public void fun_3(View v) {
		// addPhoneTest();//测试用的
		phone = et_phoneNum.getText().toString();
		if (TextUtils.isEmpty(phone)) {// 手机号为空
			JUtil.showMsg(v.getContext(), "手机号不能为空！");
		} else if (TextUtils.isEmpty(ed_nickName.getText().toString())) {// 昵称为空
			JUtil.showMsg(v.getContext(), "昵称不能为空！");
		} else if (JUtil.isPhoneNumberValid(phone)) {
			if (edtRecommender.getVisibility()== View.GONE) {
				recommendMobile = "";
			}else {
				recommendMobile = edtRecommender.getText().toString();
			}
			getAuthCode();
		} else {
			Toast.makeText(BindPhoneNumActivity.this, "请输入合法手机号码！",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 返回键
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 再次获取验证码
	 * 
	 * @param v
	 */
	@OnClick(R.id.btn_getCode)
	public void getCodeAgainOnclick(View v) {
		getAuthCode();
	}
	
	private void getAuthCode(){
		mProgressDialog = JUtil.showDialog(BindPhoneNumActivity.this,"正在获取验证码");
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.getVerificationCode(BindPhoneNumActivity.this,
				phone, ed_nickName.getText()
						.toString(), et_remark.getText().toString(),recommendMobile,
				new VerificationCodeCallBack() {
					@Override
					public void getVerificationCodeCallBack(int rtn) {
						JUtil.dismissDialog(mProgressDialog);
						// 返回：rtn(1 发送成功，0 发送失败，-1 90秒内不能重复发送请求）
						switch (rtn) {
						case -1:
							Toast.makeText(BindPhoneNumActivity.this,
									"获取验证码失败\n可能是网络链接问题引起。",
									Toast.LENGTH_SHORT).show();
							break;
						case 0:
							Toast.makeText(BindPhoneNumActivity.this,
							"90秒内不能重复发送请求", Toast.LENGTH_SHORT).show();
							break;
						case 1:
							time.start();// 开始计时
							phoneRlt.setVisibility(View.GONE);// 成功以后，隐藏上面布局，显示下面输入验证码的布局，输入验证码
							codeRlt.setVisibility(View.VISIBLE);
							break;
						case 2: // mifi
							boolean result = userPhoneDBService
									.insertOrReplaceDetetion(new UserPhone(
											phone, ed_nickName.getText()
													.toString(), et_remark
													.getText().toString()));
							if (result) {
								// 如果用户手机号码不存在，则第一次绑定的号码作为用户的手机号码
//								ifFirstBind();
								userPhoneChange(phone);
//								if (SingletonSharedPreferences
//										.getInstance().getAddedPhoneCount() <= 0) {
//									JUtil.putSharePre(
//											BindPhoneNumActivity.this,
//											APPConstant.SP_NAME,
//											APPConstant.SP_FIELD_CURRENT_PHONE,
//											phone);
//								}
								
								Toast.makeText(BindPhoneNumActivity.this,
										"添加成功", Toast.LENGTH_SHORT).show();
								// 添加成功，返回到电话列表页
//								JUtil.putShareBool(BindPhoneNumActivity.this, APPConstant.SP_NAME,APPConstant.FIELD_BIND_PASS, true);
								BindPhoneNumActivity.this.finish();
							} else {
								Toast.makeText(BindPhoneNumActivity.this,"添加失败，请稍后重试！", Toast.LENGTH_SHORT).show();
							}
							break;
						}
					}
				});
	}

	/**
	 * 输入验证码后的确认按钮
	 * 
	 * @param v
	 */
	@OnClick(R.id.compare_code)
	public void compareCodeOnClick(View v) {
//		if (edtImageCode.getText().toString().equals("")) {
//			JUtil.showMsg(BindPhoneNumActivity.this, "请输入短信验证码！");
//			return;
//		}
		mProgressDialog = JUtil
				.showDialog(BindPhoneNumActivity.this, "正在验证...");
		AsyncNetworkManager asyncNetworkManager = new AsyncNetworkManager();
		asyncNetworkManager.compareVerificationCode(BindPhoneNumActivity.this,
				phone, et_code.getText().toString(),recommendMobile, "",
				new compareVerificationCodeCallBack() {
					@Override
					public void getCompareVerificationCodeCallBack(int rtn) {
						JUtil.dismissDialog(mProgressDialog);
						switch (rtn) {
						case -2:
							Toast.makeText(BindPhoneNumActivity.this,
									"图形验证码错误，请重新输入！", Toast.LENGTH_SHORT).show();
							break;
						case -1:
							Toast.makeText(BindPhoneNumActivity.this,
									"验证码超时，请稍后重试！", Toast.LENGTH_SHORT).show();
							break;
						case 0:
							Toast.makeText(BindPhoneNumActivity.this,
									"短信验证失败，请稍后重试！", Toast.LENGTH_SHORT).show();
							break;
						case 1:
							boolean result = userPhoneDBService
									.insertOrReplaceDetetion(new UserPhone(
											phone, ed_nickName.getText()
													.toString(), et_remark
													.getText().toString()));
//							ifFirstBind();
							if (result) {
								// 如果用户手机号码不存在，则第一次绑定的号码作为用户的手机号码
//								if (SingletonSharedPreferences.getInstance()
//										.getAddedPhoneCount() <= 0) {
//									JUtil.putSharePre(
//											BindPhoneNumActivity.this,
//											APPConstant.SP_NAME,
//											APPConstant.SP_FIELD_CURRENT_PHONE,
//											phone);
//								}
								userPhoneChange(phone);
								Toast.makeText(BindPhoneNumActivity.this,
										"添加成功", Toast.LENGTH_SHORT).show();
								// 添加成功，返回到电话列表页
								JUtil.putShareBool(BindPhoneNumActivity.this, APPConstant.SP_NAME,APPConstant.FIELD_BIND_PASS, true);
								BindPhoneNumActivity.this.finish();
							}
							break;
						}
					}
				});
	}

	public void clearPhoneOnClick(View v) {
		et_phoneNum.setText("");
	}

	public void clearRemakOnClick(View v) {
		et_remark.setText("");
	}

	public void clearNameOnClick(View v) {
		ed_nickName.setText("");
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			getCodeBtn.setText("重新验证");
			getCodeBtn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			getCodeBtn.setClickable(false);
			getCodeBtn.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	// 测试用，直接添加手机号
	/*
	public void addPhoneTest() {
		phone = et_phoneNum.getText().toString();
		if ("".equals(phone)) {
//			ConfirmDialog confirmDialog = new ConfirmDialog(this,
//					R.style.mystyle, R.layout.confirmdialog);
//			confirmDialog.show();
//			confirmDialog.setMessage("手机号不能为空！");
//			confirmDialog.setTitle("提示");
			JUtil.showMsg(BindPhoneNumActivity.this, "手机号不能为空！");
		} else if (phone.equals("")) {
//			ConfirmDialog confirmDialog = new ConfirmDialog(this,
//					R.style.mystyle, R.layout.confirmdialog);
//			confirmDialog.show();
//			confirmDialog.setMessage("昵称不能为空！");
//			confirmDialog.setTitle("提示");
			JUtil.showMsg(BindPhoneNumActivity.this, "昵称不能为空！");
		} else if (JUtil.isPhoneNumberValid(phone)) {// 添加号码
			boolean result = userPhoneDBService
					.insertOrReplaceDetetion(new UserPhone(phone, ed_nickName
							.getText().toString(), et_remark.getText()
							.toString()));
			if (result) {
				// 如果用户手机号码不存在，则第一次绑定的号码作为用户的手机号码
//				ifFirstBind();
				if (SingletonSharedPreferences.getInstance()
						.getAddedPhoneCount() <= 0) {
					JUtil.putSharePre(BindPhoneNumActivity.this,
							APPCONSTANT.SP_NAME,
							APPCONSTANT.SP_FIELD_CURRENT_PHONE, phone);
				}
				SingletonSharedPreferences.getInstance().autoPlusPhoneCount();// 手机号数量自增
				Toast.makeText(BindPhoneNumActivity.this, "添加成功",
						Toast.LENGTH_SHORT).show();
				// 添加成功，返回到电话列表页
				BindPhoneNumActivity.this.finish();
			}
		} else {
			Toast.makeText(BindPhoneNumActivity.this, "请输入移动号码！",
					Toast.LENGTH_SHORT).show();
		}
	}
	*/
	
	/**
	 * 第一次绑定号码，切换到主界面，要刷新
	 */
	private void ifFirstBind() {
//		if ("".equals(JUtil.getSharePreStr(BindPhoneNumActivity.this,
//				APPConstant.SP_NAME,
//				APPConstant.SP_FIELD_CURRENT_PHONE))) {
			JUtil.putSharePre(BindPhoneNumActivity.this,APPConstant.SP_NAME,APPConstant.SP_IF_FIRST_BIND,1);
			
			Intent intent = new Intent();
			intent.setAction(ACTION_FIRST_BIND);
			BindPhoneNumActivity.this.sendBroadcast(intent);
//		}
	}
	
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		
//		.showStubImage(R.drawable.default_icon)	//设置正在加载图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//		.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
//		.showImageForEmptyUri(R.drawable.default_icon)	
//		.showImageOnFail(R.drawable.default_icon)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(0))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
		
		//缓存目录
		//有SD卡 path=/sdcard/Android/data/com.example.universalimageloadertest/cache
		//无SD卡 path=/data/data/com.example.universalimageloadertest/cache
//		File cacheDir = StorageUtils.getCacheDirectory(UserPhotoDetailActivity.this);
	}
	
	@Deprecated
	private void getImageCode() {
		AsyncNetworkManager ayAsyncNetworkManager = new AsyncNetworkManager();
		ayAsyncNetworkManager.getImageCode(BindPhoneNumActivity.this, new AsyncNetworkManager.GetImageCodeCallBack() {
			@Override
			public void getImageCodeResult(String filePath) {
				if (!"".equals(filePath)) {
					File file = new File(filePath);
					if (file.exists()) {
						mImageLoader.displayImage("file:///"+filePath, mImageView, options, animateFirstListener);
					}
				}
			}
		});
	}
	
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
	 * 用户号码变动
	 * 1.如果用户是第一次绑定，就要发广播，让用户，更新
	 * 2.如果用户删除号码，而删除的号码是正在显示的号码，就要刷新界面
	 */
	private void userPhoneChange(String phone) {
		//当前号码为空，就发广播
		if ("".equals(JUtil.getSharePreStr(BindPhoneNumActivity.this,
				APPConstant.SP_NAME,
				APPConstant.SP_FIELD_CURRENT_PHONE))) {
			JUtil.putSharePre(BindPhoneNumActivity.this,APPConstant.SP_NAME,APPConstant.SP_IF_FIRST_BIND,1);
			JUtil.putSharePre(BindPhoneNumActivity.this,
					APPConstant.SP_NAME,
					APPConstant.SP_FIELD_CURRENT_PHONE,phone);
			Intent intent = new Intent();
			intent.setAction(ACTION_FIRST_BIND);
			BindPhoneNumActivity.this.sendBroadcast(intent);
		}else {
			Intent intent = new Intent();
			intent.setAction(ACTION_FIRST_BIND);
			intent.putExtra(BIND_MOBILE, phone);
			BindPhoneNumActivity.this.sendBroadcast(intent);
		}
	}
	
	/**
	 * 返回上一个页面
	 */
	private void back(){
		Class<?> clazz = UtilFunc.loadClass(mContext, targetClassName);
		finish();
		if (clazz == SpeedMainActivity.class) {//从首页进来,用不同的动画
			overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
		}else {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	
}
