package com.kinth.mmspeed.ui;

import com.kinth.mmspeed.R;
import com.kinth.mmspeed.friend.CircleAnimation;
import com.kinth.mmspeed.friend.LoadState;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 朋友圈的自定义listview
 * 
 * @author Sola
 *
 */
public class MomentsListView extends ListView implements OnScrollListener {
	private final int DECREASE_HEADVIEW_PADDING = 100;// 减少headview的padding
	private final int LOAD_DATA = 101;// 加载数据
	private final int DISMISS_CIRCLE = 102;// 使刷新消失

	private View headView;// 头部
	private ImageView circle;// 刷新图标
	private float lastDownY;// 上次按下的Y坐标
	private int deltaCount;// 保存着当前HeadView的已经离顶端的距离
	private int currentState;// 当前的状态
	private int firstVisibleItem;// 第一个可见item项
	private int currentScrollState;// 当前的滚动状态
	private int CircleMarginTop;// 刷新距离顶部的最大距离
	private int headViewHeight;
	private OnLoadCallback callback;

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case DECREASE_HEADVIEW_PADDING:
				setHeadViewPaddingTop(deltaCount);
				setCircleMargin();
				break;
			case LOAD_DATA:
				// clearCircleViewMarginTop();
				Thread thread = new Thread(new DismissCircleThread());
				thread.start();
				currentState = LoadState.LOADSTATE_IDLE;
				if (callback != null) {
					callback.onLoadFinish();
				}
				break;
			case DISMISS_CIRCLE:
				int margin = msg.arg1;
				setCircleMargin(margin);
				if (margin == 0) {
					CircleAnimation.stopRotateAnmiation(circle);
				}
				break;
			}
			return false;
		}
	});

	public MomentsListView(Context context, OnLoadCallback callback) {
		this(context);
		this.callback = callback;
	}

	public MomentsListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeadView(context);
	}

	public MomentsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeadView(context);
	}

	public MomentsListView(Context context) {
		super(context);
		initHeadView(context);
	}

	/**
	 * 加载完成的回调接口
	 *
	 */
	public interface OnLoadCallback {
		/**
		 * 加载完成，刷新listview
		 */
		void onLoadFinish();
	}

	private void initHeadView(Context context) {
		headView = LayoutInflater.from(context).inflate(
				R.layout.header_of_moments, null);
		addHeaderView(headView);
		circle = (ImageView) headView.findViewById(R.id.circleprogress);
		headView.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						if (headView.getMeasuredHeight() > 0) {
							headViewHeight = headView.getMeasuredHeight();
							headView.getViewTreeObserver()
									.removeOnPreDrawListener(this);
						}
						return true;
					}
				});
		setOnScrollListener(this);
		currentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
		currentState = LoadState.LOADSTATE_IDLE;
		firstVisibleItem = 0;
		CircleMarginTop = 76;
		setSelector(new ColorDrawable(Color.TRANSPARENT));
		setItemsCanFocus(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float downY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (deltaCount > 0 && currentState != LoadState.LOADSTATE_LOADING
					&& firstVisibleItem == 0
					&& headView.getBottom() >= headViewHeight
					&& currentScrollState == SCROLL_STATE_TOUCH_SCROLL) {// 符合条件，刷新
				decreasePadding(deltaCount);
				loadDataForThreeSecond();// 加载更多
				startCircleAnimation();
			}
			break;
		case MotionEvent.ACTION_DOWN:
			lastDownY = downY;
			break;
		case MotionEvent.ACTION_MOVE:
			int nowDeltaCount = (int) ((downY - lastDownY) / 3.0);// 移动的间隔
			int grepDegree = nowDeltaCount - deltaCount;// 与上次的移动间隔的差值
			deltaCount = nowDeltaCount;
			if (deltaCount > 0 && currentState != LoadState.LOADSTATE_LOADING
					&& firstVisibleItem == 0
					&& headView.getBottom() >= headViewHeight
					&& currentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
				setHeadViewPaddingTop(deltaCount);
				setCircleViewStay();
				CircleAnimation.startCWAnimation(circle,
						5 * (deltaCount - grepDegree), 5 * deltaCount);// 上次间隔
																		// --->新的间隔
			}
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 距离顶部的padding
	 * 
	 * @param deltaY
	 */
	private void setHeadViewPaddingTop(int deltaY) {
		headView.setPadding(0, deltaY, 0, 0);
	}

	/**
	 * 逐步减少headview到顶部的距离
	 * 
	 * @param count
	 */
	private void decreasePadding(int count) {
		Thread thread = new Thread(new DecreaseThread(count));
		thread.start();
	}

	protected void setCircleMargin(int margin) {
		MarginLayoutParams lp = (MarginLayoutParams) circle.getLayoutParams();
		lp.topMargin = margin;
		circle.setLayoutParams(lp);
	}

	// 设置circle的margin，保持距离
	protected void setCircleMargin() {
		// TODO Auto-generated method stub
		MarginLayoutParams lp = (MarginLayoutParams) circle.getLayoutParams();
		lp.topMargin = CircleMarginTop - headView.getPaddingTop();
		circle.setLayoutParams(lp);
	}

	private class DecreaseThread implements Runnable {
		private final static int TIME = 25;// 次数
		private int decrease_length;// 每次恢复的长度

		public DecreaseThread(int count) {
			decrease_length = count / TIME;
		}

		@Override
		public void run() {
			for (int i = 0; i < TIME; i++) {
				if (i == 24) {
					deltaCount = 0;
				} else {
					deltaCount = deltaCount - decrease_length;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
				Message msg = Message.obtain();
				msg.what = DECREASE_HEADVIEW_PADDING;
				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * circle消失线程
	 *
	 */
	private class DismissCircleThread implements Runnable {
		private final int COUNT = 10;
		private final int deltaMargin;

		public DismissCircleThread() {
			deltaMargin = CircleMarginTop / COUNT;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int temp = 0;
			for (int i = 0; i <= COUNT; i++) {
				if (i == 10) {
					temp = 0;
				} else {
					temp = CircleMarginTop - deltaMargin * i;
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
				Message msg = Message.obtain();
				msg.what = DISMISS_CIRCLE;
				msg.arg1 = temp;
				handler.sendMessage(msg);
			}

		}
	}

	/**
	 * 设置刷新图标的状态
	 */
	private void setCircleViewStay() {
		if (headView.getPaddingTop() > (CircleMarginTop)) {
			MarginLayoutParams lp = (MarginLayoutParams) circle
					.getLayoutParams();
			lp.topMargin = CircleMarginTop - headView.getPaddingTop();
			circle.setLayoutParams(lp);
		}
	}

	private void startCircleAnimation() {
		// TODO Auto-generated method stub
		CircleAnimation.startRotateAnimation(circle);

	}

	// 模拟加载数据
	private void loadDataForThreeSecond() {
		currentState = LoadState.LOADSTATE_LOADING;
		Message msg = Message.obtain();
		msg.what = LOAD_DATA;
		handler.sendMessageDelayed(msg, 3000);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_FLING:
			currentScrollState = SCROLL_STATE_FLING;
			break;
		case SCROLL_STATE_IDLE:
			currentScrollState = SCROLL_STATE_IDLE;
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			currentScrollState = SCROLL_STATE_TOUCH_SCROLL;
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}
}
