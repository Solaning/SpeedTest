package com.kinth.mmspeed.ui;

import com.kinth.mmspeed.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 正在准备中的文字  以及圆弧
 * @author Sola
 *
 */
public class BreathView extends View {
	private Drawable drawable;
	private int bitmap_Width;
	private int bitmap_Height;
	private float scale_rate;
	private Context context;
	// bitmap图片的位置（相对于视图）,开始绘制的位置
	private int pBitmapX = 0, pBitmapY = 0;
	private Paint paint;

	public BreathView(Context context) {
		super(context);
		this.context = context;
	}

	public BreathView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public BreathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	private void initPaint() {
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.base_blue));
		paint.setAntiAlias(true);
		paint.setTextSize(50 * scale_rate);
	}
	
	public void setParameter(Drawable drawable, int width, int height,
			float scale_rate) {
		this.drawable = drawable;
		bitmap_Width = width;
		bitmap_Height = height;
		this.scale_rate = scale_rate;
		initPaint();
	}

	/**
	 * 设置底盘相对于视图的偏移量，即底盘相对应父布局的坐标
	 */
	public void setBitmapOffset(int cx, int cy) {
		pBitmapX = cx;
		pBitmapY = cy;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(pBitmapX, pBitmapY);
		canvas.drawText("正在准备中...", bitmap_Width / 3 - 5*scale_rate,
				bitmap_Height * 2 / 3 , paint);
		canvas.restore();
		drawable.setBounds(pBitmapX, pBitmapY, bitmap_Width + pBitmapX,
				bitmap_Height + pBitmapY);
		drawable.draw(canvas);
	}

}
