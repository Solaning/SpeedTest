package com.kinth.mmspeed.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.bean.InstancySpeedInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.hk.SingletonBitmapClass;
import com.kinth.mmspeed.hk.SingletonSharedPreferences;
import com.kinth.mmspeed.ping.Ping;
import com.kinth.mmspeed.ping.Ping.Backend;
import com.kinth.mmspeed.ping.PingArguments;
import com.kinth.mmspeed.ping.PingResult;
import com.kinth.mmspeed.ui.BreathView;
import com.kinth.mmspeed.ui.MyTestView;
import com.kinth.mmspeed.ui.PingView;
import com.kinth.mmspeed.util.DownloadManager;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.UploadManager;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 测速的主Fragment
 * 
 * @author Sola
 */
public class SpeedTestMainFragment extends Fragment {
	// 常量
	private final static int PING_ANIMATOR = 131401;// ping动画
	private final static int SHOW_DIAL = 131402;// 显示出仪表盘
	private final static int DOWN_LOAD = 131403;// 下载测速
	public final static int UPDATE_PROGRESS_BAR = 131404;// 更新下载进度
	public final static int INSTANCY_SPEED = 131405;// 瞬时速度
	private final static int UP_LOAD = 131406;// 上传测速
	public final static int UP_LOAD_COMPLETE = 131407;// 上传测速完成
	public final static int DOWN_LOAD_COMPLETE = 131408;// 下载完成

	interface OnSpeedTestEndListener{
		public void onEnd();
	}
	
	// 参数
	private Context context;
	private BitmapFactory.Options bfoOptions;
	private OnSpeedTestEndListener listener;
	// 控制变量
	private float Scale_Rate;// 缩放比例
	// 组件
	private View rootView;

	private boolean running = true;

	@ViewInject(R.id.dial_layout)
	private RelativeLayout dial_layout;// 表盘layout

	private MyTestView myView;// 表盘view

	@ViewInject(R.id.speed_test_ping_result)
	private TextView Ping_Result;// ping的结果

	@ViewInject(R.id.speed_test_ping_word)
	private TextView Ping_Word;//

	@ViewInject(R.id.speed_test_download_result)
	private TextView Download_Result;// 下载速度的结果

	@ViewInject(R.id.speed_test_download_word)
	private TextView Download_Word;

	@ViewInject(R.id.speed_test_upload_result)
	private TextView Upload_Resutl;// 上传速度的结果

	@ViewInject(R.id.speed_test_upload_word)
	private TextView Upload_Word;

	@ViewInject(R.id.restart_test)
	private Button restart_test;// 重新测速

	@ViewInject(R.id.share_test_result)
	private RelativeLayout share;

	@ViewInject(R.id.pick_unit)
	private TextView pick_unit;// 选单位

	@ViewInject(R.id.download_progress_bar)
	private ProgressBar download_progress_bar;// 下载进度条

	@ViewInject(R.id.progress_layout)
	private LinearLayout progress_layout;// 进度条的layout

	public SpeedTestMainFragment(Context context,
			OnSpeedTestEndListener listener) {
		super();
		this.context = context;
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.sola_speed_test_main_layout, null);
		ViewUtils.inject(this, rootView);
		
		// 缩放比率
		Scale_Rate = SingletonSharedPreferences.getInstance().getScaleRate();
		// 计算图片的缩放大小
		bfoOptions = new BitmapFactory.Options();
		bfoOptions.inScaled = false;// 真实大小不变形
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 开始呼吸动画
		final BreathView breathView = new BreathView(context);
		Bitmap breath_bitmap = UtilFunc.ScaleBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.ping_progress_line, bfoOptions),
				Scale_Rate);
		breathView.setParameter(
				UtilFunc.Bitmap2Drawable(breath_bitmap, context),
				breath_bitmap.getWidth(), breath_bitmap.getHeight(),
				Scale_Rate);
		breathView
				.setBitmapOffset((SingletonSharedPreferences.getInstance()
						.getScreenWidth() - breath_bitmap.getWidth()) / 2,
						SingletonSharedPreferences.getInstance()
								.getScreenCenterHeight(getActivity())
								- breath_bitmap.getHeight()
								/ 2
								- (int) (180 * Scale_Rate));// 设置放在屏幕中心位置
		dial_layout.addView(breathView);

		AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
				context, R.animator.ping_animation);// 控制文字若隐若现
		set.setTarget(breathView);
		set.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {// 应该使用Handler来实现好分离开来
				// 开始划线的动画
				if (SpeedTestMainFragment.this.isAdded()) {
					Ping_Word.setTextColor(getResources().getColor(
							R.color.base_blue));
					dial_layout.removeView(breathView);
					handler.sendEmptyMessage(PING_ANIMATOR);
				}
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
				if (SpeedTestMainFragment.this.isAdded()) {
					PingTask pingTask = new PingTask();// 开始ping
					pingTask.execute(ApplicationController.getInstance()
							.getPingIPAddress());
				}
			}
		});
		set.start();
	}

	private Handler handler = new Handler(new Handler.Callback() {

		@SuppressLint({ "InlinedApi", "NewApi" })
		@Override
		public boolean handleMessage(final Message msg) {
			if (!running) {// 没有在运行就返回
				return false;
			}
			switch (msg.what) {
			case PING_ANIMATOR:// ping动画开始
				final PingView pingView = new PingView(context);
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
					pingView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);// 关闭硬件加速
				}
				Bitmap ping_bitmap = UtilFunc.ScaleBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.ping_progress_bg, bfoOptions),
						Scale_Rate);
				pingView.setBitmapOffset((SingletonSharedPreferences
						.getInstance().getScreenWidth() - ping_bitmap
						.getWidth()) / 2, SingletonSharedPreferences
						.getInstance().getScreenCenterHeight(getActivity())
						- ping_bitmap.getHeight()
						/ 2
						- (int) (180 * Scale_Rate));// 设置放在屏幕中心位置
				pingView.setBitmap(
						UtilFunc.Bitmap2Drawable(ping_bitmap, context),
						ping_bitmap.getWidth(), ping_bitmap.getHeight(),
						Scale_Rate);
				dial_layout.addView(pingView);
				ObjectAnimator pingAnimator = ObjectAnimator.ofFloat(pingView,
						"sweepAngle", -240, 60);
				pingAnimator.setDuration(2000);
				pingAnimator.addListener(new AnimatorListener() {// ping动画结束时

							@Override
							public void onAnimationCancel(Animator arg0) {
							}

							@Override
							public void onAnimationEnd(Animator arg0) {// 开始测速的动作
								if (SpeedTestMainFragment.this.isAdded()) {
									AnimatorSet set = (AnimatorSet) AnimatorInflater
											.loadAnimator(context,
													R.animator.alpha_animation);// 从xml资源加载动画
									set.setTarget(Ping_Result);
									Ping_Result.setText(UtilFunc
											.getPingSpannable(context,
													SingletonSharedPreferences
															.getInstance()
															.getValueOfPing()));// 从sharedpreferences取ping值，设置ping值
									Ping_Word.setTextColor(getResources()
											.getColor(R.color.gray_font));
									set.start();
									dial_layout.removeView(pingView);
									handler.sendEmptyMessage(SHOW_DIAL);
								}
							}

							@Override
							public void onAnimationRepeat(Animator arg0) {

							}

							@Override
							public void onAnimationStart(Animator arg0) {

							}
						});
				pingAnimator.start();
				break;
			case SHOW_DIAL:// ping动画结束，显示仪表盘
				progress_layout.setVisibility(View.VISIBLE);// 进度条可见
				download_progress_bar.setProgress(0);
				download_progress_bar.setProgressDrawable(getResources()
						.getDrawable(R.drawable.progress_bar_color_rtl));// 左到右
				Bitmap clockBitmap = SingletonBitmapClass.getInstance()
						.getClockBitmap(
								SingletonSharedPreferences.getInstance()
										.getUnitMode());// 显示的Mb单位
				Bitmap hourBitmap = SingletonBitmapClass.getInstance()
						.getHourBitmap();
				if (myView == null) {
					myView = new MyTestView(context);
					// 设置底盘相对于视图坐标为
					myView.setChassisOffset((SingletonSharedPreferences
							.getInstance().getScreenWidth() - clockBitmap
							.getWidth()) / 2, SingletonSharedPreferences
							.getInstance().getScreenCenterHeight(getActivity())
							- clockBitmap.getHeight()
							/ 2
							- (int) (196 * Scale_Rate));
					myView.init(UtilFunc.Bitmap2Drawable(clockBitmap, context),
							UtilFunc.Bitmap2Drawable(hourBitmap, context),
							Scale_Rate, clockBitmap.getWidth(),
							clockBitmap.getHeight(), hourBitmap.getWidth(),
							hourBitmap.getHeight());
					myView.setCenterOffset((int) (-2 * Scale_Rate),
							(int) (96 * Scale_Rate));// 中心点的偏移
					myView.setPointOffset((int) (53 * Scale_Rate)); // 12点钟方向指针向下偏移20像素
					myView.downloadPrepare(); //
				}
				// 初始化
				Animation alphaAnimation = new AlphaAnimation(0f, 1.0f);
				// 设置动画时间
				alphaAnimation.setDuration(700);
				alphaAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						if (SpeedTestMainFragment.this.isAdded()) {
							Download_Word.setTextColor(getResources().getColor(
									R.color.base_blue));
							handler.sendEmptyMessage(DOWN_LOAD);// 开始下载
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
					}
				});
				myView.startAnimation(alphaAnimation);
				dial_layout.addView(myView);
				break;
			case DOWN_LOAD:// 下载测速
				DownloadManager downloadThread = new DownloadManager(context,
						handler, ApplicationController.getInstance().getDownloadServerURL());
				downloadThread.start();// 开始下载
				break;
			case DOWN_LOAD_COMPLETE:// 下载完成
				if (myView != null) {
					myView.downloadComplete();
				}
				AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
						context, R.animator.alpha_animation);// 从xml资源加载动画
				set.setTarget(Download_Result);
				set.start();
				SpannableString msp = (SpannableString) msg.obj;
				Download_Result.setText(msp);// 下载速度的结果
				Download_Word.setTextColor(getResources().getColor(
						R.color.gray_font));
				download_progress_bar.setProgress(0);
				download_progress_bar.setProgressDrawable(getResources()
						.getDrawable(R.drawable.progress_bar_color_ltr));
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {// 延迟1s开始测上传速度

							@Override
							public void run() {
								handler.sendEmptyMessage(UP_LOAD);
							}
						}, 1000);
				break;
			case UPDATE_PROGRESS_BAR:// 更新下载进度
				int progress = msg.arg1;
				download_progress_bar.setProgress(progress);
				break;
			case INSTANCY_SPEED:// 瞬时速度，控制指针转动
				InstancySpeedInfo info = (InstancySpeedInfo) msg.obj;
				myView.calcDegree(info.getSpeed(), info.getAngle(), true);
				break;
			case UP_LOAD:// 开始上传测速
				Upload_Word.setTextColor(getResources().getColor(
						R.color.base_blue));
				UploadManager uploadThread = new UploadManager(context,
						handler, ApplicationController.getInstance().getUploadServerURL(),
						APPConstant.PROGRAM_DIRECTORY + "/upload_file");
				uploadThread.start();// 开始下载
				break;
			case UP_LOAD_COMPLETE:// 上传测速完成
				ApplicationController.getInstance().setHasRecord(true);// 测完一次，设置有记录了
				progress_layout.setVisibility(View.INVISIBLE);// 进度条不可见
				myView.uploadComplete();// 上传结束后界面整理
				if(listener !=null){
					listener.onEnd();
				}
				
				break;
			}
			return true;
		}
	});

	public void shareShortMsgOnClick(String download, String upload) {
		String shareText = getString(R.string.share_speed_test_result,
				download, upload);
		Uri smsToUri = Uri.parse("smsto:");
		Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		mIntent.putExtra("sms_body", shareText);
		startActivity(mIntent);
	}

	/**
	 * 异步ping
	 */
	class PingTask extends AsyncTask<String, Void, PingResult> {

		@Override
		protected PingResult doInBackground(String... params) {
			PingArguments arguments = new PingArguments.Builder()
					.url(params[0]).timeout(5000).count(4).bytes(32).build();
			return Ping.ping(arguments, Backend.UNIX);
		}

		@Override
		protected void onPostExecute(PingResult results) {
			super.onPostExecute(results);
			String avg = "";
			if (results != null) {
				avg += (int) results.rtt_avg();// (这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000)
			}
			SingletonSharedPreferences.getInstance().setValueOfPing(avg);// 保存ping的值
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	@Override
	public void onDestroy() {
		running = false;
		super.onDestroy();
	}
}
