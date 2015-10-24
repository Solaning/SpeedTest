package com.kinth.mmspeed.ui;

import com.kinth.mmspeed.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

public class PingView extends View {
	private Context context;
	private float sweepAngle;// 终点角度
	private Drawable drawable;
	private int bitmap_Width;
	private int bitmap_Height;
	private float scale_rate;
	private Paint paint;

	// bitmap图片的位置（相对于视图）
	private int pBitmapX = 0, pBitmapY = 0;
	// 图片中心点位置（相对于视图）
	private int pBitmapCenterX = 0, pBitmapCenterY = 0;

	/**
	 * 计算底盘中心点位置（没有用setCenter设置时采用此默认值）
	 */
	private void calcCenter() {
		pBitmapCenterX = pBitmapX + bitmap_Width / 2;// 相对于视图偏移的真实位置
		pBitmapCenterY = pBitmapY + bitmap_Height / 2;
	}

	/**
	 * 设置底盘相对于视图的偏移量，即底盘相对应父布局的坐标
	 */
	public void setBitmapOffset(int cx, int cy) {
		pBitmapX = cx;
		pBitmapY = cy;
	}

	/**
	 * 设置时钟中心点位置的偏移量
	 * 
	 * @param cx
	 *            相对于时钟图片中心点的X偏移量
	 * @param cy
	 *            相对于时钟图片中心点的Y偏移量
	 */
	public void setCenterOffset(int cx, int cy) {
		pBitmapCenterX += cx;// 原有的值加上偏移量
		pBitmapCenterX += cy;
	}

	public PingView(Context context) {
		super(context);
		this.setWillNotDraw(false);// 让ViewGroup执行onDraw
		this.context = context;
	}

	private void initPaint() {
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.base_blue));
		paint.setAntiAlias(true);
		paint.setTextSize(50 * scale_rate);
	}

	// 设置bitmap
	public void setBitmap(Drawable drawable, int width, int height,
			float scale_rate) {
		this.drawable = drawable;
		bitmap_Width = width;
		bitmap_Height = height;
		this.scale_rate = scale_rate;
		initPaint();
		calcCenter();// 先不调用的
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 下面是获得一个三角形的剪裁区
		canvas.save(); // TODO 文字
		canvas.translate(pBitmapX, pBitmapY);
		canvas.drawText("网络延时", bitmap_Width / 3 + 25 * scale_rate,
				bitmap_Height * 2 / 3, paint);
		canvas.drawText("测试", bitmap_Width / 3 + 70 * scale_rate, bitmap_Height
				* 2 / 3 + 80 * scale_rate, paint);
		canvas.restore();
		getSectorClip(canvas, pBitmapCenterX, pBitmapCenterY, bitmap_Width / 2,
				-240, sweepAngle);
		drawable.setBounds(pBitmapX, pBitmapY, bitmap_Width + pBitmapX,
				bitmap_Height + pBitmapY);
		drawable.draw(canvas);
	}

	/**
	 * 返回一个扇形的剪裁区
	 * 
	 * @param canvas
	 *            画笔
	 * @param center_X
	 *            圆心X坐标
	 * @param center_Y
	 *            圆心Y坐标
	 * @param r
	 *            半径
	 * @param startAngle
	 *            起始角度
	 * @param sweepAngle
	 *            终点角度
	 */
	public void getSectorClip(Canvas canvas, float center_X, float center_Y,
			float r, float startAngle, float sweepAngle) {
		Path path = new Path();
		// 下面是获得一个三角形的剪裁区
		path.moveTo(center_X, center_Y); // 圆心
		path.lineTo(
				(float) (center_X + r * Math.cos(startAngle * Math.PI / 180)), // 起始点角度在圆上对应的横坐标
				(float) (center_Y + r * Math.sin(startAngle * Math.PI / 180))); // 起始点角度在圆上对应的纵坐标
		path.close();
		// //设置一个正方形，内切圆
		RectF rectF = new RectF(center_X - r, center_Y - r, center_X + r,
				center_Y + r);
		// 下面是获得弧形剪裁区的方法
		path.addArc(rectF, startAngle, sweepAngle - startAngle);
		canvas.clipPath(path);
	}

	// 设置终点的角度
	public float getSweepAngle() {
		return sweepAngle;
	}

	//
	public void setSweepAngle(float sweepAngle) {
		this.sweepAngle = sweepAngle;
		invalidate();
	}

}
