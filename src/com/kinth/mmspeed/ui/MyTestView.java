package com.kinth.mmspeed.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.util.ApplicationController;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * 测速的表盘view
 * @author Sola
 */
public class MyTestView extends RelativeLayout {
	private Drawable mChassisdrawable; // 底盘
	private int chassisWidth;
	private int chassisHeight;
	private Context context;
	private PointerView pointerView;// 指针
	private TextView speedText;

	// 底盘的位置（相对于视图）
	private int ChassisX = 0, ChassisY = 0;

	// 底盘中心点位置（相对于视图）
	private int ChassisCenterX = 0, ChassisCenterY = 0;

	// 指针指向12点钟方向时指针向下的偏移量
	private int mPointOffsetY = 0;

	// 指针位置（相对于底盘中心点）
	private int mPointPosX = 0, mPointPosY = 0;

	// 是否初始化完毕
	private boolean bInitComplete = false;

	public MyTestView(Context context) {
		super(context);
		this.context = context;
		this.setWillNotDraw(false);// 让ViewGroup执行onDraw
		pointerView = new PointerView(context);// 表示指针
		// 指针设置
		addView(pointerView);// 添加单独的指针
	}

	public MyTestView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyTestView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param clock
	 * @param hour
	 * @param minute
	 *            初始化视图
	 */
	public void init(Drawable chassis, Drawable pointer, float scale_reat,
			int chassisWidth, int chassisHeight, int pointerWidth,
			int pointerHeight) {
		mChassisdrawable = chassis;// 表盘视图
		this.chassisWidth = chassisWidth;
		this.chassisHeight = chassisHeight;
		// 其他初始化
		pointerView.init(pointer, pointerWidth, pointerHeight);// 初始化指针视图
		// 计算指针相对于中心点的位置
		pointerView.calcPointerPosition();
		calcCenter();
		speedText = new TextView(context);// 添加显示速度值的文本框
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);//
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.topMargin = (int) (ChassisCenterY + 180 * scale_reat);// 文字显示位置向下的偏移量140
//		speedText.setTextSize(26 * scale_reat);
		speedText.setTextColor(getResources().getColor(R.color.base_blue));
		speedText.setGravity(Gravity.CENTER);
		speedText.setText("");

		addView(speedText, params);
		bInitComplete = true;
	}

	/**
	 * 改变表盘的图片
	 * 
	 * @param chassis
	 */
	public void changeChassisDrawable(Drawable chassis) {
		// if (mChassisdrawable != null) {
		// Bitmap bitmap = ((BitmapDrawable) mChassisdrawable).getBitmap();
		// bitmap.recycle();
		// mChassisdrawable = null;
		// }
		mChassisdrawable = chassis;
	}

	/**
	 * 计算底盘中心点位置（没有用setCenter设置时采用此默认值）
	 */
	private void calcCenter() {
		ChassisCenterX = ChassisX + chassisWidth / 2;// 相对于视图偏移的真实位置，RelativeLayout没有指定大小，canvas默认覆盖整个
		ChassisCenterY = ChassisY + chassisHeight / 2;
	}

	/**
	 * 设置底盘相对于视图的偏移量，即底盘相对应父布局的坐标
	 */
	public void setChassisOffset(int cx, int cy) {
		ChassisX = cx;
		ChassisY = cy;
	}

	/**
	 * 设置时钟中心点位置的偏移量（指针要绕着这个点旋转）
	 * 
	 * @param cx
	 *            中心点相对于时钟图片的X偏移量
	 * @param cy
	 *            中心点相对于时钟图片的Y偏移量
	 */
	public void setCenterOffset(int cx, int cy) {
		ChassisCenterX += cx;// 原有的值加上偏移量
		ChassisCenterY += cy;
	}

	/**
	 * @param hourOffset
	 * @param minuteOffset
	 * 
	 *            设置指针12点钟方向时向下的偏移量
	 */
	public void setPointOffset(int hourOffset) {
		mPointOffsetY = hourOffset;
		pointerView.calcPointerPosition();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!bInitComplete) {
			return;
		}
		// 画底盘
		drawChassis(canvas);
	}

	/**
	 * @param canvas
	 *            绘制底盘
	 */
	public void drawChassis(Canvas canvas) {
		if (mChassisdrawable == null) {
			return;
		}
		mChassisdrawable.setBounds(ChassisX, ChassisY, chassisWidth + ChassisX,
				chassisHeight + ChassisY);
		mChassisdrawable.draw(canvas);
		// canvas.drawBitmap(mChassisBitmap, ChassisX, ChassisY, null);
	}

	/**
	 * 准备下载，复位到初始位置
	 */
	public void downloadPrepare() {
		speedText.setText("");
		ObjectAnimator oa = ObjectAnimator.ofFloat(pointerView, "rotation",
				pointerView.getDegree(), ApplicationController.MinDegree);
		oa.setDuration(0);
		oa.setInterpolator(new myDecelerateInterpolation());
		oa.start();
		pointerView.setDegree(ApplicationController.MinDegree);
	}

	/**
	 * 下载结束时调用，回复到初始位置
	 */
	public void downloadComplete() {
		speedText.setText("");
		ObjectAnimator oa = ObjectAnimator.ofFloat(pointerView, "rotation",
				pointerView.getDegree(), ApplicationController.MinDegree);
		oa.setDuration(500);
		oa.setInterpolator(new myDecelerateInterpolation());
		oa.start();
		pointerView.setDegree(ApplicationController.MinDegree);
	}

	/**
	 * 上传结束时调用，回复到初始位置
	 */
	public void uploadComplete() {
		speedText.setText("");
		ObjectAnimator oa = ObjectAnimator.ofFloat(pointerView, "rotation",
				pointerView.getDegree(), ApplicationController.MinDegree);
		oa.setDuration(1000);
		oa.setInterpolator(new myDecelerateInterpolation());
		oa.start();
		pointerView.setDegree(ApplicationController.MinDegree);
	}

	/**
	 * 更新角度，使指针转动,flag没用到
	 */
	public void calcDegree(SpannableString speed, float degree, boolean flag) {
		// mCurDegree.calcDegree(degree, flag);
		// pointerView.postInvalidate();
		// pointerView.setPointDegree(degree);//更新角度
		// pointerView.startAnimation();

		speedText.setText(speed);
		ObjectAnimator oa = ObjectAnimator.ofFloat(pointerView, "rotation",
				pointerView.getDegree(), degree);
		oa.setDuration(500);
		oa.setInterpolator(new DecelerateInterpolator());
		oa.start();
		pointerView.setDegree(degree);
	}

	/**
	 * 在结束时反弹
	 */
	public class myBounceInterpolator implements Interpolator {
		private BounceInterpolator a;

		public myBounceInterpolator() {
			a = new BounceInterpolator();
		}

		public float getInterpolation(float t) {
			// Log.e("myBounceInterpolator", "" + t);
			return a.getInterpolation(t);
		}

	}

	/**
	 * 加速插值
	 * 
	 * @author Sola
	 * 
	 */
	public class myAccelerateInterpolation implements Interpolator {
		private AccelerateInterpolator a;

		public myAccelerateInterpolation() {
			a = new AccelerateInterpolator();
		}

		public float getInterpolation(float t) {
			return a.getInterpolation(t);
		}
	}

	/**
	 * 变化频率在开始和结尾处慢，而在中间部分加速
	 * 
	 * @author Sola
	 * 
	 */
	public class myAccelerateDecelerateInterpolator implements Interpolator {
		private AccelerateDecelerateInterpolator a;

		public myAccelerateDecelerateInterpolator() {
			a = new AccelerateDecelerateInterpolator();
		}

		public float getInterpolation(float t) {
			// Log.e("myAccelerateDecelerateInterpolator", "" + t);
			return a.getInterpolation(t);
		}
	}

	/**
	 * 向前抛出，并超过目标值，然后再返回
	 * 
	 * @author Sola
	 * 
	 */
	public class myOvershootInterpolation implements Interpolator {
		private OvershootInterpolator a;

		public myOvershootInterpolation() {
			a = new OvershootInterpolator();
		}

		public float getInterpolation(float t) {
			// Log.e("myOvershootInterpolation", "" + t);
			return a.getInterpolation(t);
		}
	}

	/**
	 * 减速插值
	 */
	public class myDecelerateInterpolation implements Interpolator {
		private DecelerateInterpolator a;

		public myDecelerateInterpolation() {
			a = new DecelerateInterpolator();
		}

		public float getInterpolation(float t) {
			return a.getInterpolation(t);
		}
	}

	/**
	 * 指针view
	 * 
	 * @author Sola
	 * 
	 */
	public class PointerView extends View {
		private float degree = 0; // 上次分针偏移量，用于记录上次的角度值
		private Drawable pointerDrawable;
		private int pointerWidth;
		private int pointerHeight;

		public PointerView(Context context) {
			super(context);
		}

		/**
		 * 初始化指针bitmap
		 */
		public void init(Drawable pointer, int width, int height) {
			pointerDrawable = pointer;
			pointerWidth = width;
			pointerHeight = height;
		}

		/**
		 * 计算指针相对于中心点的位置
		 */
		private void calcPointerPosition() {
			mPointPosX = -pointerWidth / 2;// 指针相对与中心点的位置
			mPointPosY = -pointerHeight + mPointOffsetY;// 加上向下的偏移量，暂时为0

			ViewHelper.setPivotX(this, ChassisCenterX);// 设置锚点
			ViewHelper.setPivotY(this, ChassisCenterY);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			drawPoint(canvas);
		}

		/**
		 * 绘制时针
		 * 
		 * @param canvas
		 */
		private void drawPoint(Canvas canvas) {
			if (pointerDrawable == null) {
				return;
			}
			// canvas.save();
			// canvas.translate(ChassisCenterX, ChassisCenterY);
			// canvas.rotate(degree);// 转动角度
			// Paint paint = new Paint();
			// paint.setAntiAlias(true);
			// canvas.drawBitmap(mPointBitmap, mPointPosX, mPointPosY, paint);
			// canvas.restore();
			int x = ChassisCenterX + mPointPosX;
			int y = ChassisCenterY + mPointPosY;
			pointerDrawable
					.setBounds(x, y, pointerWidth + x, pointerHeight + y);
			pointerDrawable.draw(canvas);
		}

		public float getDegree() {
			return degree;
		}

		public void setDegree(float degree) {
			this.degree = degree;
		}
	}

}
