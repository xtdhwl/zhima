package com.zhima.ui.space.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SpaceScrollView extends ScrollView {

	private int byFlag;
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public SpaceScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SpaceScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SpaceScrollView(Context context) {
		super(context);
		init();
	}

	public void init() {
		byFlag = 0;
	}

	//去掉scrollBy and scrollTO 的作用:在空间展示中GridView使scroolView会自动滚动的缺点
	@Override
	public void scrollBy(int x, int y) {
		if (byFlag > 25) {
			super.scrollBy(x, y);
		} else {
			byFlag++;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}
		}

		return super.onInterceptTouchEvent(ev);
	}
	//	@Override
	//	public void scrollTo(int x, int y) {
	//		Logger.getInstance(TAG).debug("SpaceScrollView scrollTo()功能以去掉.请忽略");
	//	}

}
