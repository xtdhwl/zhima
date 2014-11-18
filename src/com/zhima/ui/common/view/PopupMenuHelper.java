package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @ClassName:PopupHelper
 * @Description:PopupMenu中的PopupWindow.对PopuMenu进行扩展
 * @author liqilong
 * @date 2012-8-9 上午10:49:43
 *
 */
//对PopupWindow的行为进行控制
public class PopupMenuHelper extends PopupWindow {

	private static final String TAG = "PopupHelper";
	private Context mContext;
	private Rect mTempRect;
	private int mDropDownWidth;



	public PopupMenuHelper(View contentView, int width, int height) {
		super(contentView, width, height);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
	}

	/**
     * Sets the width of the popup window by the size of its content. The final width may be
     * larger to accommodate styled window dressing.
     *
     * @param width Desired width of content in pixels.
     */
    public void setContentWidth(int width) {
    	//XXX
       /* Drawable popupBackground = getBackground();
        if (popupBackground != null) {
            popupBackground.getPadding(mTempRect);
            mDropDownWidth = mTempRect.left + mTempRect.right + width;
        } else {
            setWidth(width);
        }*/
    	setWidth(width);
    }


}