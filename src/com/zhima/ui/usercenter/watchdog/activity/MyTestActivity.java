package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;

public class MyTestActivity extends BaseActivity implements OnItemSelectedListener {
	// 声明SeekBar对象
	SeekBar mSeekBar2;
	private TextCardGalleryAdapter adapter;
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	ImageView mDragItemView;
	/** WindowManager对象 */
	private WindowManager mWindowManager;

	private boolean isDurationt = false;

	/**
	 * Called when the activity is first created. author:wang date:2012.7.15
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test_organizition_view);
		// 取得SeekBar对象
		initSeekBar();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			list.add("喜印空间");
		}
		Gallery gallery = (Gallery) findViewById(R.id.gallery);
		adapter = new TextCardGalleryAdapter(this, R.layout.gallery_card_text_item, list);
		gallery.setAdapter(adapter);
		gallery.setSpacing(0);
		gallery.setOnItemSelectedListener(this);
	}

	public void initSeekBar() {
		mSeekBar2 = (SeekBar) findViewById(R.id.seek2);
		mSeekBar2.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				// TODO Auto-generated method stub
				int x = (int) ev.getRawX();
				int y = (int) ev.getRawY();
				if (!isDurationt) {
					switch (ev.getAction()) {

					case MotionEvent.ACTION_DOWN:
						xDistance = yDistance = 0f;
						xLast = ev.getX();
						yLast = ev.getY();
						startDragging((int) x, (int) y);
						break;
					case MotionEvent.ACTION_MOVE:
						dragView((int) x, (int) y - 100);

						final float curX = ev.getX();
						final float curY = ev.getY();
						// startDragging(R.drawable.five_key_icon,xRaw,yRaw);
						boolean isUp;
						if (curY - yLast > 0) {
							isUp = false;
						} else {
							isUp = true;
						}
						xDistance += Math.abs(curX - xLast);
						yDistance += Math.abs(curY - yLast);
						xLast = curX;
						yLast = curY;

						if (xDistance > yDistance) {
							// scrollDragging((int) x, (int) y, 300, 0, 0);
							// return false;
						} else if (yDistance > 100) {
							 stopDragging();
							 if(isUp){
							 scrollDragging((int) x, (int) y, 0,300, 0);
							 }else{
							 scrollDragging((int) x, (int) y, 0, 800, 0);
							 }
							//
						}
						break;
					case MotionEvent.ACTION_UP:
						// 当Up时:根据坐标判断是否重叠的Item
						final float xFinal = ev.getX();
						final float yFinal = ev.getY();
						stopDragging();
//						if(yFinal-yLast>-100&&yFinal-yLast<100){
//							scrollDragging((int) x, (int) y, 0, 800, 0);
//						}else if(yFinal-yLast>=100){
//							scrollDragging((int) x, (int) y, 0, 100, 0);
//						}else if(yFinal-yLast<=-100){
//							scrollDragging((int) x, (int) y, 0, 100, 0);
//						}
						break;
					case MotionEvent.ACTION_CANCEL:
						stopDragging();

						break;
					default:
						break;
					}
				}

				return false;
			}
		});
		mSeekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress < 5 || progress > 95) {
					// mSeekBar2.setProgress(50);
				} else {

				}

			}
		});

		// mSeekBar2.setVerticalScrollBarEnabled(true);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		adapter.setSelectItem(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	private void startDragging(int x, int y) {
//		System.out.println("x:" + x + ",y:" + y);
		stopDragging();
		mDragItemView = getDragView();
		WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
		mWindowParams.x = x - mDragItemView.getWidth() / 2;
		mWindowParams.y = y - mDragItemView.getHeight() / 2;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		mWindowParams.format = PixelFormat.TRANSLUCENT;
		mWindowParams.windowAnimations = 0;

		// mDragBitmap = bm;

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(mDragItemView, mWindowParams);
	}

	private ImageView getDragView() {
		ImageView iv = new ImageView(this);
		iv.setBackgroundResource(R.drawable.seekbar_organizition_center_icon);
		return iv;
	}

	private void dragView(int x, int y) {
		// 这里的判断是当我们开启SmoothScrollRunnable，handler在发送异步消息的时候，y条件以满足。会调用stop（）；所以需要判断或优化
		if (mDragItemView != null) {
			WindowManager.LayoutParams mWindowParams = (WindowManager.LayoutParams) mDragItemView.getLayoutParams();
			mWindowParams.x = x - mDragItemView.getWidth() / 2;
			mWindowParams.y = y - mDragItemView.getHeight() / 2;
			mWindowManager.updateViewLayout(mDragItemView, mWindowParams);
		}
	}

	private void stopDragging() {
		if (mDragItemView != null && mWindowManager != null) {
			mWindowManager.removeView(mDragItemView);
			mDragItemView = null;
		}
		isDurationt = false;
	}

	private final Handler mHandler = new Handler();

	private void scrollDragging(int startX, int startY, int dx, int dy, int durationt) {
		startDragging(startX, startY);
		SmoothScrollRunnable mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(mHandler, startX, startY, dx, dy);
		mHandler.post(mCurrentSmoothScrollRunnable);
	}

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DURATION_MS = 1900;
		static final int ANIMATION_FPS = 1000 / 1000;

		private final Interpolator mInterpolator;
		private final int mScrollToX;
		private final int mScrollFromX;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final Handler mHandler;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(Handler handler, int startX, int startY, int dx, int dy) {
			mHandler = handler;
			mScrollFromX = startX;
			mScrollToX = dx;
			mScrollFromY = startY;
			mScrollToY = dy;
			mInterpolator = new AccelerateDecelerateInterpolator();
		}

		@Override
		public void run() {
			isDurationt = true;
			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY)
						* mInterpolator.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				dragView(mScrollFromX, mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY > mCurrentY) {
				mHandler.postDelayed(this, ANIMATION_FPS);
			} else {
				stop();
			}
		}

		public void stop() {

			mContinueRunning = false;
			mHandler.removeCallbacks(this);
			stopDragging();
		}
	}

}