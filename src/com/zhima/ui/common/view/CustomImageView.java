/**  
 * MyImageView.java
 * @version 1.0
 * @author Haven
 * @createTime 2011-12-9 下午03:12:30
 * 此类代码是根据android系统自带的ImageViewTouchBase代码修改
 */
package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.zhima.base.gdi.GraphicUtils;

/**
 * @ClassName: MyImageView
 * @Description: 自定义ImageView 可伸缩
 * @author yusonglin
 * @date 2012-8-20 下午6:20:32
*/
public class CustomImageView extends ImageView {

	protected Matrix mBaseMatrix = new Matrix();

	protected Matrix mSuppMatrix = new Matrix();

	private final Matrix mDisplayMatrix = new Matrix();

	private final float[] mMatrixValues = new float[9];

	protected Bitmap mImage = null;

	int mThisWidth = -1, mThisHeight = -1;

	/** 最大缩放比例 */
	float mMaxZoom = 2.0f;
	/** 最小缩放比例  */
	float mMinZoom ;

	/** 图片的原始宽度 */
	private int mImageWidth;
	/** 图片的原始高度 */
	private int mImageHeight;

	/** 图片适应屏幕的缩放比例 */
	private float mScaleRate;

	public CustomImageView(Context context) {
		super(context);
		init();
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 计算图片要适应屏幕需要缩放的比例
	 */
	private void arithScaleRate() {
		float scaleWidth = GraphicUtils.mScreenWidth / (float) mImageWidth;
		float scaleHeight = GraphicUtils.mScreenHeight / (float) mImageHeight;
		mScaleRate = Math.min(scaleWidth, scaleHeight);
	}

	public float getScaleRate() {
		return mScaleRate;
	}

	public int getImageWidth() {
		return mImageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.mImageWidth = imageWidth;
	}

	public int getImageHeight() {
		return mImageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.mImageHeight = imageHeight;
	}


	protected Handler mHandler = new Handler();

	/* (non-Javadoc)
	 * @see android.widget.ImageView#setImageBitmap(android.graphics.Bitmap)
	 * 设置图片
	 */
	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		mImage = bitmap;
		// 计算适应屏幕的比例
		this.mImageHeight = bitmap.getHeight();
		this.mImageWidth = bitmap.getWidth();
		arithScaleRate();
		
		zoomTo(getScaleRate(), GraphicUtils.mScreenWidth / 2, GraphicUtils.mScreenHeight / 2, 0.1f);
		
		//居中
		layoutToCenter();
	}

	/**
	 * 设置图片显示居中
	 * @param horizontal 横向是否居中
	 * @param vertical 竖向是否居中
	 */
	protected void center(boolean horizontal, boolean vertical) {
		if (mImage == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		RectF rect = new RectF(0, 0, mImage.getWidth(), mImage.getHeight());

		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			int viewHeight = getHeight();
			if (height < viewHeight) {
				deltaY = (viewHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < viewHeight) {
				deltaY = getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int viewWidth = getWidth();
			if (width < viewWidth) {
				deltaX = (viewWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) {
				deltaX = viewWidth - rect.right;
			}
		}

		postTranslate(deltaX, deltaY);
		setImageMatrix(getImageViewMatrix());
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
	}
	
	public class MulitPointTouchListener implements OnTouchListener {      
        
        Matrix matrix = new Matrix();      
        Matrix savedMatrix = new Matrix();      
        
        public ImageView image;      
        static final int NONE = 0;      
        static final int DRAG = 1;      
        static final int ZOOM = 2;      
        int mode = NONE;      
        
        PointF start = new PointF();      
        PointF mid = new PointF();      
        float oldDist = 1f;      
        
        
        public MulitPointTouchListener(ImageView image) {      
            super();      
            this.image = image;      
        }      
        
        @Override      
        public boolean onTouch(View v, MotionEvent event) {      
            this.image.setScaleType(ScaleType.MATRIX);      
        
            ImageView view = (ImageView) v;      
        
            switch (event.getAction() & MotionEvent.ACTION_MASK) {    
                
            case MotionEvent.ACTION_DOWN:      
        
                matrix.set(view.getImageMatrix());      
                savedMatrix.set(matrix);      
                start.set(event.getX(), event.getY());      
                mode = DRAG;      
                break;      
            case MotionEvent.ACTION_POINTER_DOWN:      
                oldDist = spacing(event);      
                if (oldDist > 10f) {      
                    savedMatrix.set(matrix);      
                    midPoint(mid, event);      
                    mode = ZOOM;      
                }      
                break;      
            case MotionEvent.ACTION_POINTER_UP:      
                mode = NONE;      
                break;      
            case MotionEvent.ACTION_MOVE:      
                if (mode == DRAG) {      
                    matrix.set(savedMatrix);      
                    matrix.postTranslate(event.getX() - start.x, event.getY()      
                            - start.y);      
                } else if (mode == ZOOM) {      
                    float newDist = spacing(event);      
                    if (newDist > 10f) {      
                        matrix.set(savedMatrix);      
                        float scale = newDist / oldDist;      
                        matrix.postScale(scale, scale, mid.x, mid.y);      
                    }      
                }      
                break;      
            }      
        
            view.setImageMatrix(matrix);      
            return true;    
        }      
        
            
        private float spacing(MotionEvent event) {      
            float x = event.getX(0) - event.getX(1);      
            float y = event.getY(0) - event.getY(1);      
            return FloatMath.sqrt(x * x + y * y);      
        }      
        
        private void midPoint(PointF point, MotionEvent event) {      
            float x = event.getX(0) + event.getX(1);      
            float y = event.getY(0) + event.getY(1);      
            point.set(x / 2, y / 2);      
        }      
    }   
	
	/**
	 * 设置图片居中显示
	 */
	public void layoutToCenter()
	{
		//正在显示的图片实际宽高
		float width = mImageWidth*getScale();
		float height = mImageHeight*getScale();
		
		//空白区域宽高
		float fill_width = GraphicUtils.mScreenWidth - width;
		float fill_height = GraphicUtils.mScreenHeight - height;
		
		//需要移动的距离
		float tran_width = 0f;
		float tran_height = 0f;
		
		if(fill_width>0)
			tran_width = fill_width/2;
		if(fill_height>0)
			tran_height = fill_height/2;
			
		
		postTranslate(tran_width, tran_height);
		setImageMatrix(getImageViewMatrix());
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		mMinZoom =( GraphicUtils.mScreenWidth/2f)/mImageWidth;
		
		return mMatrixValues[whichValue];
	}

	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	protected Matrix getImageViewMatrix() {
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	static final float SCALE_RATE = 1.25F;

	/**
	 * @return
	 */
	protected float maxZoom() {
		if (mImage == null) {
			return 1F;
		}

		float fw = (float) mImage.getWidth() / (float) mThisWidth;
		float fh = (float) mImage.getHeight() / (float) mThisHeight;
		float max = Math.max(fw, fh) * 4;
		return max;
	}

	protected void zoomTo(float scale, float centerX, float centerY) {
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}
	

	protected void zoomTo(float scale) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		zoomTo(scale, cx, cy);
	}

	protected void zoomToPoint(float scale, float pointX, float pointY) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		panBy(cx - pointX, cy - pointY);
		zoomTo(scale, cx, cy);
	}

	protected void zoomIn() {
		zoomIn(SCALE_RATE);
	}

	protected void zoomOut() {
		zoomOut(SCALE_RATE);
	}

	protected void zoomIn(float rate) {
		if (getScale() >= mMaxZoom) {
			return; // Don't let the user zoom into the molecular level.
		} else if (getScale() <= mMinZoom) {
			return;
		}
		if (mImage == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		mSuppMatrix.postScale(rate, rate, cx, cy);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	protected void zoomOut(float rate) {
		if (mImage == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);

		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, cx, cy);
		} else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	public void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}
	float _dy=0.0f;
	protected void postTranslateDur( final float dy, final float durationMs) {
		_dy=0.0f;
		final float incrementPerMs = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				
				postTranslate(0, incrementPerMs*currentMs-_dy);
				_dy=incrementPerMs*currentMs;

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	protected void panBy(float dx, float dy) {
		postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}
}
