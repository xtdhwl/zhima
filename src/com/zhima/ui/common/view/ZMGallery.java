package com.zhima.ui.common.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * @ClassName: MyGallery
 * @Description: 图片浏览 
 * @author yusonglin
 * @date 2012-8-20 下午6:17:35
*/
public class ZMGallery extends Gallery {

	public ZMGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ZMGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ZMGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
