package com.zhima.ui.sidebar;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

import com.zhima.base.logger.Logger;
import com.zhima.ui.sidebar.callback.SizeCallBack;

/**
 * @ClassName: MenuHorizontalScrollView
 * @Description: 侧边栏自定义控件
 * @author yusonglin
 * @date 2012-9-20 下午9:05:57
*/
public class MenuHorizontalScrollView extends HorizontalScrollView {

	private static final String TAG = "MenuHorizontalScrollView"; 
	
	/* 当前控件 */
	private MenuHorizontalScrollView mCurrentView;

	private Context mContext;
	
	/* 菜单 */
	private View mMenu;

	/* 菜单状态 */
	public static boolean isMenuOut;

	/* 扩展宽度 */
	private final int ENLARGE_WIDTH = 30;
	
	/* 菜单的宽度 */
	private int menuWidth;

	/* 手势动作最开始时的x坐标 */
	private float lastMotionX = -1;

	/* 按钮 */
	private View menuBtn;

	/* 当前滑动的位置 */
	private int current;

	private int scrollToViewPos;
	
	private OnStateChangeListener mOnStateChangeListener;
	private OnStateChangeListener1 mOnStateChangeListener1;
//	private OnTouchListener mOnTouchListener;
	private OnSidebarScrollistener mOnSidebarScrollistener;
	private boolean isSetWeith = true;
	
	private boolean isAreadyScroll = false;

	public MenuHorizontalScrollView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public MenuHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MenuHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	private void init() {
		Logger.getInstance(TAG).debug("init()") ;
		this.setHorizontalFadingEdgeEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.mCurrentView = this;
		this.mCurrentView.setVisibility(View.INVISIBLE);
		
		mOnStateChangeListener1 = new OnStateChangeListener1();

		isMenuOut = false;
	}

	public void initViews(View[] children, SizeCallBack sizeCallBack, View menu) {
		Logger.getInstance(TAG).debug("initViews(View[] children, SizeCallBack sizeCallBack, View menu)") ;
		this.mMenu = menu;
		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (int i = 0; i < children.length; i++) {
			children[i].setVisibility(View.INVISIBLE);
			parent.addView(children[i]);
		}

		OnGlobalLayoutListener onGlLayoutistener = new MenuOnGlobalLayoutListener(
				parent, children, sizeCallBack);
		getViewTreeObserver().addOnGlobalLayoutListener(onGlLayoutistener);
	}

	/**
	 * 设置按钮
	 * 
	 * @param btn
	 */
	public void setMenuBtn(View btn) {
		Logger.getInstance(TAG).debug("setMenuBtn ") ;
		this.menuBtn = btn;
	}

	/******************************************************************/

	
	public interface OnStateChangeListener{
		void onStateChange(boolean isMenuout);
	}
	
	public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener){
//		Logger.getInstance(TAG).debug("setOnStateChangeListener ") ;
		this.mOnStateChangeListener = onStateChangeListener;
	}
	
	private class OnStateChangeListener1{
		void onStateChange(boolean isMenuout){
//			Logger.getInstance(TAG).debug("OnStateChangeListener1 isMenuout:" + isMenuout) ;
			if(mOnStateChangeListener!=null){
				mOnStateChangeListener.onStateChange(isMenuout);
			}
//			if(isMenuout){
//				((ViewGroup) mMenu).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//				mMenu.setFocusable(false);
//			}else{
//				mMenu.setClickable(false);
//			}
		}
	}
	
//	private OnClickListener mOnClickListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			clickMenuBtn();
//		}
//	};
	
//	public interface OnTouchListener{
//		void onTouch();
//	}
//	
//	public void setOnTouchListener(OnTouchListener onTouchListener){
////		this.mOnTouchListener = onTouchListener;
//	}
	
	public interface OnSidebarScrollistener{
		void onSidebarScrol(int menuWidth);
	}
	
	public void setOnSidebarScrollistener(OnSidebarScrollistener onSidebarScrollistener){
		Logger.getInstance(TAG).debug("setOnSidebarScrollistener");
		this.mOnSidebarScrollistener = onSidebarScrollistener;
	}
	
	
	/******************************************************************/
	public void clickMenuBtn() {
//		Logger.getInstance(TAG).debug("clickMenuBtn()") ;
		if (!isMenuOut) {
			this.menuWidth = 0;
		} else {
			this.menuWidth = this.mMenu.getMeasuredWidth()
					- this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH;
//			this.menuWidth = mSideBarView.getWidth();
		}
		menuSlide();
	}

	/**
	 * 滑动出菜单
	 */
	private void menuSlide() {
		if (this.menuWidth == 0) {
			isMenuOut = true;
		} else {
			isMenuOut = false;
		}

		if(mOnStateChangeListener1!=null){
			mOnStateChangeListener1.onStateChange(isMenuOut);
		}
		
		if(isSetWeith && mOnSidebarScrollistener!=null){
			mOnSidebarScrollistener.onSidebarScrol(this.mMenu.getMeasuredWidth() - this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH);
			isSetWeith = false;
		}
		
//		Logger.getInstance(TAG).debug("menuSlide()_menuWidth:"+menuWidth) ;
		mCurrentView.smoothScrollTo(menuWidth, 0);
		
		// if(menuOut == true)
		// this.menuBtn.setBackgroundResource(R.drawable.menu_fold);
		// else
		// this.menuBtn.setBackgroundResource(R.drawable.menu_unfold);
	}

	// public void smoothScroll1(int x, int y) {
	// smoothScrollBy(x - mScrollX, y - mScrollY);
	// }
	//
	// public void smoothScroll2(int dx, int dy) {
	// if (getChildCount() == 0) {
	// // Nothing to do.
	// return;
	// }
	// long duration = AnimationUtils.currentAnimationTimeMillis() -
	// mLastScroll;
	// if (duration > ANIMATED_SCROLL_GAP) {
	// final int width = getWidth() - mPaddingRight - mPaddingLeft;
	// final int right = getChildAt(0).getWidth();
	// final int maxX = Math.max(0, right - width);
	// final int scrollX = mScrollX;
	// dx = Math.max(0, Math.min(scrollX + dx, maxX)) - scrollX;
	//
	// mScroller.startScroll(scrollX, mScrollY, dx, 0);
	// invalidate();
	// } else {
	// if (!mScroller.isFinished()) {
	// mScroller.abortAnimation();
	// }
	// scrollBy(dx, dy);
	// }
	// mLastScroll = AnimationUtils.currentAnimationTimeMillis();
	// }

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		Logger.getInstance(TAG).debug("onScrollChanged()") ;
		// TODO Auto-generated method stub
//		super.onScrollChanged(l, t, oldl, oldt);
//		if (l < (this.mMenu.getMeasuredWidth() - this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH) / 2) {
//			this.menuWidth = 0;
//		} else {
//			this.menuWidth = this.mMenu.getWidth()
//					- this.menuBtn.getMeasuredWidth() - this.ENLARGE_WIDTH;
//		}
//		this.current = l;
	}
	
	
	
	@Override
	public boolean arrowScroll(int direction) {
//		Logger.getInstance(TAG).debug("arrowScroll() direction:" + direction) ;
		return false;
	}
	
	@Override
	public boolean pageScroll(int direction) {
//		Logger.getInstance(TAG).debug("pageScroll() direction:" + direction);
		return false;
	}
	
	@Override
	public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
//		Logger.getInstance(TAG).debug("setSmoothScrollingEnabled() smoothScrollingEnabled:" + smoothScrollingEnabled);
		super.setSmoothScrollingEnabled(false);
	}
	
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		Logger.getInstance(TAG).debug("dispatchKeyEvent ev:" + event.getAction()) ;
//		return false;
//	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		Logger.getInstance(TAG).debug("onInterceptTouchEvent ev:" + ev.getAction()) ;
		return false;
	}
	
	@Override
	public void scrollBy(int x, int y) {
//		Logger.getInstance(TAG).debug("scrollBy ev:") ;
	}
	
	@Override
	public void scrollTo(int x, int y) {
//		Logger.getInstance(TAG).debug("scrollTo isAreadyScroll:") ;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		Logger.getInstance(TAG).debug("onTouchEvent ev:" + ev.getAction());
//		mOnTouchListener.onTouch();
//		// TODO Auto-generated method stub
//		int x = (int) ev.getRawX();
//
//		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//			/* 手指按下时的x坐标 */
//			this.lastMotionX = (int) ev.getRawX();
//		}
//		if ((this.current == 0 && x < this.scrollToViewPos)
//				|| (this.current == this.scrollToViewPos * 2 && x > this.ENLARGE_WIDTH)) {
//			return false;
//		}
//		if (isMenuOut == false && this.lastMotionX > 30) {
//			return true;
//		}
//
//		else {
//			if (ev.getAction() == MotionEvent.ACTION_UP) {
//				menuSlide();
//				return false;
//			}
//		}
		return false;
	}

	/****************************************************/
	/*-												   -*/
	/*-			Class 			Area				   -*/
	/*-												   -*/
	/****************************************************/

	public class MenuOnGlobalLayoutListener implements OnGlobalLayoutListener {

		private ViewGroup parent;
		private View[] children;
		private SizeCallBack sizeCallBack;

		public MenuOnGlobalLayoutListener(ViewGroup parent, View[] children,
				SizeCallBack sizeCallBack) {
			Logger.getInstance(TAG).debug("MenuOnGlobalLayoutListener----");
			this.parent = parent;
			this.children = children;
			this.sizeCallBack = sizeCallBack;
		}

		@Override
		public void onGlobalLayout() {
//			Logger.getInstance(TAG).debug("onGlobalLayout()");
			mCurrentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			this.sizeCallBack.onGlobalLayout();
			this.parent.removeViewsInLayout(0, children.length);
			int width = mCurrentView.getMeasuredWidth();
			int height = mCurrentView.getMeasuredHeight();

			int[] dims = new int[2];
			scrollToViewPos = 0;

			for (int i = 0; i < children.length; i++) {
				this.sizeCallBack.getViewSize(i, width, height, dims);
				children[i].setVisibility(View.VISIBLE);

				parent.addView(children[i], dims[0], dims[1]);
				if (i == 0) {
					scrollToViewPos += dims[0];
				}
			}
			// if(firstLoad){
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					
//					Logger.getInstance(TAG).debug("侧边栏：scrollToViewPos="+scrollToViewPos+"__isAreadyScroll="+isAreadyScroll);
					if(!isAreadyScroll){
						mCurrentView.scroll(scrollToViewPos, 0);
						isAreadyScroll = true;
					}
					/* 视图不是中间视图 */
					mCurrentView.setVisibility(View.VISIBLE);
					mMenu.setVisibility(View.VISIBLE);
				}
			});
		}
	}
	
	private void scroll(int x,int y) {
		super.scrollTo(x, y);
	}
}
