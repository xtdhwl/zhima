package com.zhima.ui.sidebar.callback;

/**
 * @ClassName: SizeCallBack
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-20 下午9:06:24
*/
public interface SizeCallBack {

	public void onGlobalLayout();

	public void getViewSize(int idx, int width, int height, int[] dims);
}
