package com.zhima.ui.sidebar.callback;

import android.view.View;

import com.zhima.base.logger.Logger;

/**
 * @ClassName: SizeCallBackForMenu
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-20 上午10:45:05
*/
public class SizeCallBackForMenu implements SizeCallBack {

	private static final String TAG = "SizeCallBackForMenu";
	private View menu;
	private int menuWidth;
	
	
	public SizeCallBackForMenu(View menu){
		super();
		this.menu = menu;
	}
	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		Logger.getInstance(TAG).debug("onGlobalLayout") ;
		this.menuWidth = this.menu.getMeasuredWidth() + 30;
	}

	@Override
	public void getViewSize(int idx, int width, int height, int[] dims) {
		// TODO Auto-generated method stub
		Logger.getInstance(TAG).debug("getViewSize") ;
		dims[0] = width;
		dims[1] = height;
		
		/*视图不是中间视图*/
		if(idx != 1){
			dims[0] = width - this.menuWidth;
		}
	}
}
