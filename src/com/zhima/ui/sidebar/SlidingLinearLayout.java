package com.zhima.ui.sidebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @ClassName: SlidingLinearLayout
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-26 下午9:11:20
*/
public class SlidingLinearLayout extends LinearLayout {

	/* 手势动作最开始时的x坐标 */
	private float lastMotionX = -1;

	public SlidingLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// return super.onInterceptTouchEvent(ev);
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			this.lastMotionX = (int) ev.getRawX();
		}
		if (this.lastMotionX < 20)
			return true;
		else if (MenuHorizontalScrollView.isMenuOut)
			return true;
		else
			return super.onInterceptTouchEvent(ev);
	}
}
