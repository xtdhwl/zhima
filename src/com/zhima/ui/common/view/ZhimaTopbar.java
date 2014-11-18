package com.zhima.ui.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.zhima.R;

/**
* @ClassName: ZhimaTopbar
* @Description: 控制topbar显示内容
* @author liubingsr
* @date 2012-5-14 下午2:25:56
*
 */
public class ZhimaTopbar extends LinearLayout {
	
    public ZhimaTopbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    /**
    * @Title: setLeftLayoutVisible
    * @Description: 设置标题栏左端是否显示
    * @param visible
    * @param cls
    * @return void
     */
    public void setLeftLayoutVisible(boolean visible){
        if(visible) {
            findViewById(R.id.top_left_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.top_left_layout).setVisibility(View.INVISIBLE);
        }
    }
    
    /**
     * @Title: addLeftLayoutView
     * @Description: 设置标题栏左端显示内容
     * @param view--添加的view  
     * @return void
      */
     public void addLeftLayoutView(View view){
         LinearLayout rootLayout = (LinearLayout)findViewById(R.id.top_left_layout);
         LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
         layoutParams.gravity = Gravity.LEFT;
         layoutParams.gravity = Gravity.CENTER_VERTICAL;
         
         rootLayout.removeAllViews();
         rootLayout.setVisibility(View.VISIBLE);
         rootLayout.addView(view, layoutParams);
     }
     
    /**
    * @Title: setRightLayoutVisible
    * @Description: 设置右端是否显示
    * @param visible
    * @return void
     */
    public void setRightLayoutVisible(boolean visible){
        if(visible) {
            findViewById(R.id.top_right_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.top_right_layout).setVisibility(View.GONE);
        }
    }
    
    
    
    /**
    * @Title: addRightLayoutView
    * @Description: 设置标题栏右端显示内容
    * @param view 
    * @return void
     */
    public void addRightLayoutView(View view){
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.right_layout_root);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        
        rootLayout.removeAllViews();
        rootLayout.setVisibility(View.VISIBLE);
        rootLayout.addView(view, layoutParams);
    }
    
}