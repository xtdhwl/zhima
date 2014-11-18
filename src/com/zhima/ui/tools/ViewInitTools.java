package com.zhima.ui.tools;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;

/**
 * 包装UI View初始化的一些工具函数
 * @ClassName: ViewInitTools
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-10 下午3:55:39
*/
public class ViewInitTools {

	/**
	 * 设置标题栏（没有侧边栏）
	 * @Title: setTopBar
	 * @Description: TODO
	 * @param activity
	 * @param title 页面标题
	 * @param pullIconVisible 下拉菜单小三角是否显示
	 * @param onClickListener 标题点击事件
	 */
	public static void setTopBar(final BaseActivity activity,String title,int pullIconVisible,OnClickListener onClickListener) {
		ZhimaTopbar topbar = activity.getTopbar();
		View view = View.inflate(activity, R.layout.topbar_leftview, null);
		RelativeLayout backLayout = (RelativeLayout) view.findViewById(R.id.layout_titlebar_leftButton);
		TextView titleText = (TextView) view.findViewById(R.id.txt_topbar_title);
		titleText.setText(title);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
		
		view.findViewById(R.id.left_txt_icon).setVisibility(pullIconVisible);
		if(onClickListener!=null){
			view.findViewById(R.id.layout_topbar_left_title).setOnClickListener(onClickListener);
		}
		
		topbar.setLeftLayoutVisible(true);
		topbar.addLeftLayoutView(view);
	}
	
	/**
	 * 设置标题栏（有侧边栏）
	 * @Title: setSidebar
	 * @Description: TODO
	 * @param activity
	 * @param mSideBarView
	 * @param mChildrenView
	 */
	public static void setSidebar(BaseActivity activity,final SideBarView mSideBarView,View mChildrenView) {

		mSideBarView.setChildView(mChildrenView);
		activity.setContentView(mSideBarView.getContentView());

		final View tranView = (View) mSideBarView.getContentView().findViewById(R.id.view_transparent);
		tranView.setVisibility(View.GONE);
		tranView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSideBarView.scrollView();
			}
		});
		tranView.setClickable(false);
		mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {

			@Override
			public void onStateChange(boolean isMenuOut) {
				if (isMenuOut) {
					tranView.setVisibility(View.VISIBLE);
					tranView.setClickable(true);
				} else {
					tranView.setVisibility(View.GONE);
					tranView.setClickable(false);
				}
			}
		});
	}
	
}
