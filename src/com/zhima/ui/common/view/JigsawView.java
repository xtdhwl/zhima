package com.zhima.ui.common.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.zhima.R;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;

/**
 * 拼图JigsawView继承ViewGrop,使用需要设置setAdatpet().与模式setMode(),必须设置DropListener监听:
 * 如果有拼图成功返回原拼图位置与目标拼图位置;
 * 
 * @ClassName:JigsawView
 * @Description:TODO
 * @author liqilong
 * @date 2012-6-18 下午8:33:25
 * 
 */
public class JigsawView extends FrameLayout {
	private static final String TAG = "JigsawView";

	// 设置item的最大数
	private static final int MAX_ITEM_COUNT = 3;
	// 边框与边框的距离
	private static final int FRAME_WITDH = 2;
	/** 设置模版样式 */
	private static int mMode = 0;
	/** 记录现在模版样式子类的位置 */
	private int modePosition = 0;
	/**
	 * 模式定义
	 */
	public static final int MODE_AUTO = 0;
	public static final int MODE_ONE = 10;
	public static final int MODE_TWO_LEFT = 20;
	public static final int MODE_TWO_TOP = 21;
	public static final int MODE_THREE_LEFT = 32;
	public static final int MODE_THREE_RIGHT = 33;
	public static final int MODE_THREE_TOP = 34;
	public static final int MODE_THREE_BOTTOM = 35;
	public static final int MODE_THREE_VERTICAL = 36;
	public static final int MODE_THREE_HORIZONTAL = 37;

	private Context mContext;
	/** 记录画板的信息 */
	private JigsawState jsState;
	private JigsawView.JigsawAttribute pituAttribute;

	/** 适配图片集合 */
	private List<Bitmap> mBitmapList = null;
	/** 存放Item坐标 */
	private ArrayList<Rect> mChilderRects = null;

	/** 当前拖拽的view */
	private View mDragItemView;
	/** 被拖拽的缓存图片 */
//	private Bitmap mDragBitmap;

	/** 当前拖拽对象的缓存图片封装成的ImageView对象 */
	private ImageView mDragView;

	/** 状态栏高度y */
	private int mCoordOffsetY;
	/** 状态栏高度x */
	private int mCoordOffsetX;

	/** ViewGroup的宽度 */
	private int width = -1;
	/** ViewGroup的 高度 */
	private int height = -1;
	// private int left = -1;
	// private int top = -1;
	/** 设置拼接后的宽度,如果不设置默认为View的宽度 */
	private int combineWidth;
	/** 设置拼接后的高度,如果不设置默认为View的高度 */
	private int combineHeight;

	public static final int FLING = 0;
	public static final int SLIDE_RIGHT = 1;
	public static final int SLIDE_LEFT = 2;

	/** 点击时监听对象 */
//	private DropListener mDropListener;
	// /** 拖动时监听对象 */
	// private DragListener mDragListener;
	// /** 移除时监听对象 */
	// private RemoveListener mRemoveListener;

	/** WindowManager对象 */
	private WindowManager mWindowManager;

	/** 拖拽后的背景颜色 */
	private int dragndropBackgroundColor = R.color.white_color;

	public JigsawView(Context context) {
		this(context, null);
	}

	public JigsawView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public JigsawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mChilderRects = new ArrayList<Rect>();
		mBitmapList = new ArrayList<Bitmap>();
		jsState = new JigsawState();
	}

	private class JigsawState {
		public int mCurrentDragItem;

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		width = r - l;
		/** ViewGroup的 高度 */
		height = b - t;
		//如果没有包含view

		//
		int modeCount = mChilderRects.size();
		for (int i = 0; i < modeCount; i++) {
			// 得到childView坐标
			Rect locationRect = mChilderRects.get(i);
			int x = (int) (locationRect.left * ((float) width / 10));
			int y = (int) (locationRect.top * ((float) height / 10));
			int w = (int) (locationRect.right * ((float) width / 10));
			int h = (int) (locationRect.bottom * ((float) height / 10));
			// 取出View并设置位置
			View child = getChildAt(i);
			Logger.getInstance(TAG).debug(
					"onLayout childView(" + i + ")" + "x:" + x + "  y:" + y + "  w:" + w + "  h:" + h);
			child.setVisibility(View.VISIBLE);
			child.measure(r - l, b - t);
			child.setPadding(FRAME_WITDH, FRAME_WITDH, FRAME_WITDH, FRAME_WITDH);
			child.setBackgroundResource(dragndropBackgroundColor);
			child.layout(x, y, w + x, h + y);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() < 1) {
			return true;
		}

		int x = (int) ev.getX();
		int y = (int) ev.getY();
		int xRaw = (int) ev.getRawX();
		int yRaw = (int) ev.getRawY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mCoordOffsetX = xRaw - x;
			mCoordOffsetY = yRaw - y;
			// 得到被点击的View
			jsState.mCurrentDragItem = getChildViewIndex(x, y);
			if (jsState.mCurrentDragItem >= 0) {
				mDragItemView = getChildAt(jsState.mCurrentDragItem);
				if (mDragItemView != null) {
					mDragItemView.setDrawingCacheEnabled(true);
					Bitmap bm = Bitmap.createBitmap(mDragItemView.getDrawingCache());
					mDragItemView.setDrawingCacheEnabled(false);
					startDragging(bm, xRaw, yRaw);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mDragView != null) {
				dragView(xRaw, yRaw); // 拖动Item
			}
			break;
		case MotionEvent.ACTION_UP:
			// 当Up时:根据坐标判断是否重叠的Item
			int mDragPos = getDropViewPos(xRaw, yRaw);
			if (mDragPos >= 0 && mDragPos != jsState.mCurrentDragItem) {
				Collections.swap(mBitmapList, jsState.mCurrentDragItem, mDragPos);
				Collections.swap(mChilderRects, jsState.mCurrentDragItem, mDragPos);
				requestLayout();
			}
			stopDragging();
			break;
		case MotionEvent.ACTION_CANCEL:
			Logger.getInstance(TAG).debug("action_cancle");
			stopDragging();
			break;
		}
		return true;
	}

	/**
	 * @Title: getPutiBitmap
	 * @Description: 返回拼图后的Bitmap
	 * @return 返回拼图后的Bitmap
	 */
	public Bitmap getJigsawBitmap() {
		// XXX
		// this.setDrawingCacheEnabled(true);
		// Bitmap bm = Bitmap.createBitmap(this.getDrawingCache());
		// this.setDrawingCacheEnabled(false);

		// ===================================
		int width = combineWidth <= 0 ? this.getWidth() : combineWidth;
		int height = combineHeight <= 0 ? this.getHeight() : combineHeight;
		Bitmap bm = combineImage(mBitmapList, mChilderRects, width, height);
		return bm;
	}

	public int getCombineWidth() {
		return combineWidth;
	}

	/**
	 * 设置生成bitmap的宽度
	 */
	public void setCombineWidth(int combineWidth) {
		this.combineWidth = combineWidth;
	}

	public int getCombineHeight() {
		return combineHeight;
	}

	/**
	 * 设置生成bitmap的高度
	 */
	public void setCombineHeight(int combineHeight) {
		this.combineHeight = combineHeight;
	}

	/** 设置模式. */
	public void setMode(int mode) {
		mMode = mode;
		mChilderRects = getJigsawAttribute().getLocations(mMode);
		requestLayout();
	}

	/** 释放资源 */
	public void recycle() {
		for (int i = 0; i < mBitmapList.size(); i++) {
			Bitmap bmp = mBitmapList.get(i);
			if (bmp != null) {
				bmp.recycle();
			}
			bmp = null;
		}
		clean();
	}

	// private void setRemoveListener(RemoveListener l) {
	// mRemoveListener = l;
	// }
	//
	// private void setDragListener(DragListener l) {
	// mDragListener = l;
	// }

	/**
	 * @Title: setAdatpet
	 * @Description: 设置数据
	 * @param bitmaps 拼装的图片路径
	 */
	public void setAdapter(ArrayList<Bitmap> pathList) {
		clean();
		mBitmapList = pathList;
		//XXX 保存bitmap
		int count = pathList.size();
		count = Math.max(MAX_ITEM_COUNT, pathList.size());
		for (int i = 0; i < count; i++) {
			Bitmap bitmap = mBitmapList.get(i);
			ImageView iv = new ImageView(getContext());
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setId(i);
			iv.setImageBitmap(bitmap);
			//
			addView(iv);
			requestLayout();
		}
	}

	public int getSupportItemCount() {
		return MAX_ITEM_COUNT;
	}

	/**
	 * 清除数据
	 */
	private void clean() {
		removeAllViews();
		mChilderRects.clear();
		mBitmapList.clear();
		mMode = MODE_AUTO;
	}

	/**
	 * 检测模式
	 */
	private void check() {
		if (mBitmapList == null) {
			throw new IllegalStateException("拼图adapter为能null");
		}
		if (mChilderRects == null) {
			throw new IllegalStateException("拼图模式mChilderRects为null");
		}
		int bitmapCount = mBitmapList.size();
		int childerCount = mChilderRects.size();
		if (bitmapCount < 1) {
			throw new IllegalStateException("拼图adapter数据不能小于1");
		}
		if (bitmapCount < childerCount) {
			throw new IllegalStateException("拼图adapter数据不能小于模式bitmapCount:" + bitmapCount + ",childerCount"
					+ childerCount + ",mode" + mMode);
		}

	}

	private void startDragging(Bitmap bm, int x, int y) {
		stopDragging();

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

		ImageView v = new ImageView(getContext());
		v.setBackgroundColor(dragndropBackgroundColor);
		v.setImageBitmap(bm);
//		mDragBitmap = bm;

		mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(v, mWindowParams);
		mDragView = v;
	}

	private void dragView(int x, int y) {
		WindowManager.LayoutParams mWindowParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
		mWindowParams.x = x - mDragView.getWidth() / 2;
		mWindowParams.y = y - mDragView.getHeight() / 2;
		mWindowManager.updateViewLayout(mDragView, mWindowParams);
	}

	private void stopDragging() {
		if (mDragView != null) {
			mWindowManager.removeView(mDragView);
			mDragView.setImageDrawable(null);
			mDragView = null;
		}
//		if (mDragBitmap != null) {
//			mDragBitmap.recycle();
//			mDragBitmap = null;
//		}
	}

	/**
	 * @Title: getDropViewPos
	 * @Description: 根据坐标检测当前view
	 * @return 如果有重叠,返回index,否则返回-1
	 */
	private int getDropViewPos(int x, int y) {
		for (int i = 0, count = getChildCount(); i < count; i++) {
			View v = getChildAt(i);
			if (GraphicUtils.pointInView(new Point(x, y), v)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 根据坐标得到Item的Index
	 */
	private int getChildViewIndex(int x, int y) {
		for (int i = 0, count = getChildCount(); i < count; i++) {
			Point p = new Point(x + mCoordOffsetX, y + mCoordOffsetY);
			View v = getChildAt(i);
			if (GraphicUtils.pointInView(p, v)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 图片拼接,按比例拼接.
	 * 
	 * @Title: combineImage
	 * @Description: 图片拼接(将多张图片拼接成一个图片)
	 * @param context 上下文
	 * @param bitmapList 原拼接图像集合
	 * @param imageRectF 拼接图像的位置,按比例.1/10
	 * @param width 返回图片的宽度
	 * @param height 返回图片的高度
	 * @return 返回拼接后的图片 Bitmap
	 */
	private Bitmap combineImage(List<Bitmap> bitmapList, ArrayList<Rect> imageRect, int width, int height) {
		if (width <= 0) {
			throw new IllegalArgumentException("width must be > 0");
		}
		if (height <= 0) {
			throw new IllegalArgumentException("height must be > 0");
		}

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		// 创建一个空的bitmap
		Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 循环bitmap 与 rectF得到位置坐标
		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawRect(0, 0, width, height, paint);
		// 开始画bitmap
		int size = bitmapList.size();
		for (int i = 0; i < size; i++) {
			Rect rect = imageRect.get(i);
			// Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(i));
			float l = rect.left * width / 10;
			float t = rect.top * height / 10;
			float r = rect.right * width / 10;
			float b = rect.bottom * height / 10;
			int thumbnailWidth = (int) (rect.right * (float) width / 10); // 图片的宽度
			int thumbnailHeight = (int) (rect.bottom * (float) height / 10); // 图片的高度

			Bitmap thumbnailBitmap = bitmapList.get(i);

			Rect srcRect = new Rect();
			srcRect.left = 0;
			srcRect.top = 0;
			srcRect.right = thumbnailBitmap.getWidth();
			srcRect.bottom = thumbnailBitmap.getHeight();
			RectF dstRectF = new RectF();
			dstRectF.left = l + FRAME_WITDH;
			dstRectF.top = t + FRAME_WITDH;
			dstRectF.right = l + thumbnailWidth - FRAME_WITDH;
			dstRectF.bottom = t + thumbnailHeight - FRAME_WITDH;
			canvas.drawBitmap(thumbnailBitmap, srcRect, dstRectF, null);
		}
		return resultBitmap;
	}

	/**
	 * @Title: getPituAttribute
	 * @Description: 获得ViewGrop 属性值
	 * @return PintuViewGroup.PituAttribute PintuViewGroup.PituAttribute
	 */
	private JigsawView.JigsawAttribute getJigsawAttribute() {
		if (pituAttribute == null) {
			pituAttribute = new JigsawAttribute();
		}
		return pituAttribute;
	}

	private class JigsawAttribute {
		/**
		 * @Title: getLocations
		 * @Description:返回位置坐标
		 * @return ArrayList<Rect>
		 */
		public ArrayList<Rect> getLocations(int mode) {
			ArrayList<Rect> rects = null;
			int count = JigsawView.this.getChildCount();
			if (mMode == MODE_AUTO) {
				if (count == 1) {
					mMode = MODE_ONE;
				} else if (count == 2) {
					mMode = MODE_TWO_LEFT;
				} else if (count == 3) {
					mMode = MODE_THREE_LEFT;
				}
			}

			switch (mMode) {
			case MODE_TWO_LEFT:
				rects = getMode_tow_left_db();
				break;
			case MODE_TWO_TOP:
				rects = getMode_tow_top_db();
				break;
			case MODE_THREE_LEFT:
				rects = getMode_three_left_db();
				break;
			case MODE_THREE_RIGHT:
				rects = getMode_three_right_db();
				break;
			case MODE_THREE_TOP:
				rects = getMode_three_top_db();
				break;
			case MODE_THREE_BOTTOM:
				rects = getMode_three_bottom_db();
				break;
			case MODE_THREE_VERTICAL:
				rects = getMode_three_vertical();
				break;
			case MODE_THREE_HORIZONTAL:
				rects = getMode_three_horizontal();
				break;
			}
			return rects;
		}

		public ArrayList<Rect> getMode_tow() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			int j = modePosition % 2;
			if (j == 0) {// mode = 20 combinaton_2_1
				Rect r_1 = new Rect(0, 0, 5, 10);
				Rect r_2 = new Rect(5, 0, 5, 10);
				rects.add(r_1);
				rects.add(r_2);
			} else if (j == 1) {// mode = 21 combinaton_2_0
				Rect r_1 = new Rect(0, 0, 10, 5);
				Rect r_2 = new Rect(0, 5, 10, 5);
				rects.add(r_1);
				rects.add(r_2);
			}
			return rects;
		}

		public ArrayList<Rect> getMode_three() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			int j = modePosition % 4;
			Rect r_1 = null;
			Rect r_2 = null;
			Rect r_3 = null;
			if (j == 0) { // mode = 32 combinaton_3_1
				r_1 = new Rect(0, 0, 5, 5);
				r_2 = new Rect(5, 0, 5, 10);
				r_3 = new Rect(0, 5, 5, 5);
			} else if (j == 1) {// mode = 33 combinaton_3_2
				r_1 = new Rect(0, 0, 10, 5);
				r_2 = new Rect(0, 5, 5, 5);
				r_3 = new Rect(5, 5, 5, 5);
			} else if (j == 2) {// mode = 34 combinaton_3_3
				r_1 = new Rect(0, 0, 5, 5);
				r_2 = new Rect(5, 0, 5, 5);
				r_3 = new Rect(0, 5, 10, 5);
			} else if (j == 3) {// mode = 35 combinaton_3_4
				r_1 = new Rect(0, 0, 10, 3);
				r_2 = new Rect(0, 3, 10, 3);
				r_3 = new Rect(0, 6, 10, 4);
			} else if (j == 4) {// mode = 35 combinaton_3_5
				r_1 = new Rect(0, 0, 3, 10);
				r_2 = new Rect(3, 0, 3, 10);
				r_3 = new Rect(6, 0, 4, 10);
			}
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		// |-----|
		// | | |
		// | | |
		// | | |
		// |-----|

		// |-----|
		// | |
		// |-----|
		// | |
		// |-----|
		public ArrayList<Rect> getMode_tow_left_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			int j = modePosition % 2;

			Rect r_1 = new Rect(0, 0, 5, 10);
			Rect r_2 = new Rect(5, 0, 5, 10);
			rects.add(r_1);
			rects.add(r_2);

			return rects;

		}

		public ArrayList<Rect> getMode_tow_top_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			int j = modePosition % 2;

			Rect r_1 = new Rect(0, 0, 10, 5);
			Rect r_2 = new Rect(0, 5, 10, 5);
			rects.add(r_1);
			rects.add(r_2);
			return rects;
		}

		// |--------|
		// | | |
		// |----| |
		// | | |
		// |--------|
		public ArrayList<Rect> getMode_three_left_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			int j = modePosition % 3;

			Rect r_1 = new Rect(0, 0, 5, 5);
			Rect r_2 = new Rect(5, 0, 5, 10);
			Rect r_3 = new Rect(0, 5, 5, 5);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		public ArrayList<Rect> getMode_three_right_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			int j = modePosition % 3;

			Rect r_1 = new Rect(0, 0, 5, 10);
			Rect r_2 = new Rect(5, 0, 5, 5);
			Rect r_3 = new Rect(5, 5, 5, 5);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		// |--------|
		// | |
		// |--------|
		// | | |
		// | | |
		// |--------|getMode_three_bottom_db
		public ArrayList<Rect> getMode_three_bottom_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			Rect r_1 = new Rect(0, 0, 10, 5);
			Rect r_2 = new Rect(0, 5, 5, 5);
			Rect r_3 = new Rect(5, 5, 5, 5);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		// |--------|
		// | | |
		// |--------|
		// | |
		// |--------|
		public ArrayList<Rect> getMode_three_top_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			Rect r_1 = new Rect(0, 0, 5, 5);
			Rect r_2 = new Rect(5, 0, 5, 5);
			Rect r_3 = new Rect(0, 5, 10, 5);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		// |--------|
		// | |
		// |--------|
		// | |
		// |--------|
		// | |
		// |--------|

		public ArrayList<Rect> getMode_three_vertical() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			Rect r_1 = new Rect(0, 0, 10, 3);
			Rect r_2 = new Rect(0, 3, 10, 3);
			Rect r_3 = new Rect(0, 6, 10, 4);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		public ArrayList<Rect> getMode_three_horizontal() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			Rect r_1 = new Rect(0, 0, 3, 10);
			Rect r_2 = new Rect(3, 0, 4, 10);
			Rect r_3 = new Rect(7, 0, 3, 10);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			return rects;
		}

		// |--------|
		// | | |
		// | | |
		// |--------|
		// | |
		// |--------|

		public ArrayList<Rect> getMode_four_grid_db() {
			ArrayList<Rect> rects = new ArrayList<Rect>();
			Rect r_1 = new Rect(0, 0, 5, 5);
			Rect r_2 = new Rect(5, 0, 5, 5);
			Rect r_3 = new Rect(0, 5, 5, 5);
			Rect r_4 = new Rect(5, 5, 5, 5);
			rects.add(r_1);
			rects.add(r_2);
			rects.add(r_3);
			rects.add(r_4);
			return rects;
		}

	}

}
