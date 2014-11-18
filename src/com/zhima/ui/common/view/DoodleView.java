package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhima.base.gdi.GraphicUtils;

/**
 * @ClassName: GraphicsView
 * @Description: 乌涂板
 * @author luqilong
 * @date 2012-6-1 上午9:30:30
 *
 */
public class DoodleView extends View {
	private static final String TAG = "DoodleView";
	private float TOUCH_TOLERANCE = 4;
	// 上图层
	private Bitmap foreBitmap;
	// 下图层
	private Bitmap backBitmap;
	// 单图层
	// private Bitmap mBitmap;

	/**
	 * 画笔颜色
	 */
	private int paintColor = Color.RED;
	/**
	 * 画笔宽度
	 */
	private int strokeWidth = 10;

	private Path mPath;
	private Paint mPaint;
	private Canvas mCanvas;
	private Paint mBitmapPaint;
	private float startX = 0;
	private float startY = 0;
	private float stopX;
	private float stopY;
	private int bitmapWitdh = 480;
	private int bitmapHeight = 629;

	public DoodleView(Context context) {
		super(context);
		init();
	}

	public DoodleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DoodleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setColor(paintColor);
		mPaint.setStrokeWidth(strokeWidth);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		foreBitmap = Bitmap.createBitmap(bitmapWitdh, bitmapHeight, Bitmap.Config.ARGB_8888);
		backBitmap = Bitmap.createBitmap(bitmapWitdh, bitmapHeight, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(foreBitmap);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下
			touchStart(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE: // 移动
			touchMove(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP: // 弹起
			touchUp(x, y);
			invalidate();
			break;
		}
		return true;
	}

	private void touchUp(float x, float y) {
		mPath.lineTo(startX, startY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		// mPath.reset();
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - startX);
		float dy = Math.abs(y - startY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
			startX = x;
			startY = y;
		}
	}

	private void touchStart(float x, float y) {
		// mPath.reset();
		mPath.moveTo(x, y);
		startX = x;
		startY = y;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (backBitmap != null) {
			canvas.drawBitmap(backBitmap, 0, 0, mBitmapPaint);
		}
		canvas.drawPath(mPath, mPaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 设置画笔颜色
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		this.paintColor = color;
		mPaint.setColor(paintColor);
	}

	/**
	 * 设置画笔宽度
	 */
	public void setStrokeWidth(int width) {
		this.strokeWidth = width;
		mPaint.setStrokeWidth(strokeWidth);
	}

	/**
	 * 设置背景图片
	 * 
	 * @param bitmap
	 */
	public void setBackground(Bitmap bitmap) {
		Bitmap b = GraphicUtils.getImageScale(bitmap, foreBitmap.getWidth(), foreBitmap.getHeight());
		if (backBitmap != null) {
			backBitmap.recycle();
		}
		this.backBitmap = b;
		invalidate();
	}

	/**
	 * 获取涂鸦板图片
	 * 
	 * @return
	 */
	public Bitmap getDoodleImage() {
		 Paint paint = new Paint();
		 Bitmap resultBitmap = Bitmap.createBitmap(backBitmap.getWidth(),
				 									backBitmap.getHeight(), backBitmap.getConfig());
		 Canvas canvas = new Canvas(resultBitmap);
		 canvas.drawBitmap(backBitmap, 0, 0,paint);
		 paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER));
		 canvas.drawBitmap(foreBitmap, 0, 0,paint);
		return resultBitmap;
	}
	/**
	 * 清空涂鸦板
	 */
	public void clear(){
		if (foreBitmap != null) {
			foreBitmap.recycle();
			foreBitmap = null;
		}
		if (backBitmap != null) {
			backBitmap.recycle();
			backBitmap = null;
		}
		mPath.reset();
		mPath = null;
		init();
		invalidate();
	}
}
