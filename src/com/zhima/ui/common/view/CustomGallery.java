/**  
 * MyGallery.java
 * @version 1.0
 * @author Haven
 * @createTime 2011-12-9 下午03:42:53
 * android.widget.Gallery的子函数。此类很重要。建议仔细看
 */
package com.zhima.ui.common.view;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

import com.zhima.base.gdi.GraphicUtils;

/**
 * @ClassName: MyGallery
 * @Description: 图片浏览 
 * @author yusonglin
 * @date 2012-8-20 下午6:17:35
*/
public class CustomGallery extends Gallery {
	/** 手势识别 */
	private GestureDetector mGestureScanner;
	/** 图片 */
	private CustomImageView mImageView;
	/** 是否放大 */
	private boolean isMagnify = true;
	/** 标签 */
	private boolean mFlag = true;
	
	public CustomGallery(Context context) {
		super(context);

	}
	

	
	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
	}
	
	public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = CustomGallery.this.getSelectedView();
				if (view instanceof CustomImageView) {
					mImageView = (CustomImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						mFlag = false;
						baseValue = 0;
						originalScale = mImageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {						
						if (event.getPointerCount() == 2) {	
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;
								mImageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));
							}
							mFlag = true;
						}
					}
				}
				return false;
			}

		});
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if(mFlag){
			return false;
		}
		View view = CustomGallery.this.getSelectedView();
		if (view instanceof CustomImageView) {
			mImageView = (CustomImageView) view;
			float v[] = new float[9];
			Matrix m = mImageView.getImageMatrix();
			m.getValues(v);
			
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width, height;
			
			width = mImageView.getScale() * mImageView.getImageWidth();
			height = mImageView.getScale() * mImageView.getImageHeight();
			
			if ((int) width <= GraphicUtils.mScreenWidth && (int) height <= GraphicUtils.mScreenHeight){
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				
				left = v[Matrix.MTRANS_X];
				right = left + width;
				
				Rect r = new Rect();
				
				mImageView.getGlobalVisibleRect(r);
				
				if (distanceX > 0){
					if (r.left > 0) {// 判断当前ImageView是否显示完全
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < GraphicUtils.mScreenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						mImageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0){
					if (r.right < GraphicUtils.mScreenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						mImageView.postTranslate(-distanceX, -distanceY);
					}
				}
			}
		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		public boolean onDoubleTap(MotionEvent e) {
			View view = CustomGallery.this.getSelectedView();
			if (view instanceof CustomImageView) {
				mImageView = (CustomImageView) view;
				if (isMagnify) {
					mImageView.zoomTo(2.0f, GraphicUtils.mScreenWidth / 2, GraphicUtils.mScreenHeight / 2, 200f);
					isMagnify = false;
				} else {
					mImageView.zoomTo(mImageView.getScaleRate(), GraphicUtils.mScreenWidth / 2, GraphicUtils.mScreenHeight / 2, 200f);
					isMagnify = true;
				}

			} else {

			}
			return true;
		}
	}
}
