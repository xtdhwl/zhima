package com.zhima.ui.common.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.zhima.R;
import com.zhima.ui.adapter.ZhimaAdapter;

/**
 * @ClassName: CustomListView
 * @Description: 自定义分批加载ListView
 * @param <T>
 *            listview item要显示的数据类型
 * @author yusonglin
 * @date 2012-7-20 下午
 */
public class CustomListView<T> extends ListView {

	private Context mContext;
	/** 适配器 */
	private ZhimaAdapter<T> mZhimaAdapter;
	/** 是否正在加载 */
	private boolean isLoading = false;
	/** 是否存在底部加载进度条 */
	private boolean isHaveLoading = false;
	private boolean isHaveHeaderView = false;

	/** 已经加载的数据,ListView需要适配的数据.需要调用者去实时更新 */
	public List<T> mAlreadyLoadData;

	private View mLoadingView;
	protected int mLastVisiblePosition;
	protected int mListViewSize;
	
	private boolean isAddSecondFooterView = false;
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	public CustomListView(Context context) {
		this(context, null);
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
		mLoadingView = View.inflate(mContext, R.layout.loading_view, null);
		mLoadingView.setEnabled(false);
		mLoadingView.setClickable(false);
		mLoadingView.setFocusable(false);
		if (mAlreadyLoadData == null) {
			mAlreadyLoadData = new ArrayList<T>();
		}
		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return false;
			}
		});
	}

	/**
	 * 添加头部View
	 * 
	 * @param view
	 */
	public void addHeadView(View view) {
		if (!isHaveHeaderView) {
			addHeaderView(view, null, false);
		}
		isHaveHeaderView = true;
	}

	/**
	 * 删除头部view
	 * 
	 * @param view
	 */
	public void deleteHeadView(View view) {
		if (isHaveHeaderView) {
			if (getHeaderViewsCount() >= 1 && this != null) {
				removeHeaderView(view);
			}
		}
		isHaveHeaderView = false;
	}

	/**
	 * 添加加载进度条
	 */
	public void addLoading() {
		if (!isHaveLoading) {
			addFooterView(mLoadingView, null, false);
		}
		isHaveLoading = true;
	}

	/**
	 * 删除加载进度条
	 */
	public void deleteLoading() {

		if (isHaveLoading) {
			removeFooterView(mLoadingView);
		}
		isHaveLoading = false;
	}

	/**
	 * 状态初始化
	 */
	public void init() {
		isLoading = false;
		isHaveLoading = false;
		isHaveHeaderView = false;
		if (mAlreadyLoadData != null) {
			mAlreadyLoadData.clear();
		}
	}

	/**
	 * 设置是否正在加载数据
	 * 
	 * @param isLoading
	 */
	public void isLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void isAddSecondFooterView(boolean isAdd){
		this.isAddSecondFooterView = isAdd;
	}
	
	/**
	 * 更新listview
	 */
	public void updateListView() {

		if (mZhimaAdapter != null) {
			mZhimaAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 设置 ListView 适配和 分批大小
	 * 
	 * @param zhimaAdapter
	 *            <T> ListView适配
	 * @param pageSize
	 *            分页大小
	 */
	public void setBatchLoad(final ZhimaAdapter<T> zhimaAdapter) {
		this.mZhimaAdapter = zhimaAdapter;
		mZhimaAdapter.getFirstData();

		setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 最后一个可见条目的位置
					mLastVisiblePosition = view.getLastVisiblePosition() + 1;
					if (isHaveLoading) {
						mListViewSize = isHaveHeaderView ? mAlreadyLoadData.size() + 2 : mAlreadyLoadData.size() + 1;
					} else {
						mListViewSize = isHaveHeaderView ? mAlreadyLoadData.size() + 1 : mAlreadyLoadData.size();
					}
					
					mListViewSize = isAddSecondFooterView ? mListViewSize+1 : mListViewSize;
					
					
					
					
					if (mLastVisiblePosition == mListViewSize) {
						if (isLoading) {
							return;
						}
						mZhimaAdapter.getData();
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// int listViewSize;
				// // 最后一个可见条目的位置
				// int lastVisiblePosition = view.getLastVisiblePosition()+1;
				// if(isHaveLoading){
				// listViewSize =
				// isHaveHeaderView?mAlreadyLoadData.size()+2:mAlreadyLoadData.size()+1;
				// }else{
				// listViewSize =
				// isHaveHeaderView?mAlreadyLoadData.size()+1:mAlreadyLoadData.size();
				// }
				// if (lastVisiblePosition == listViewSize) {
				// if (isLoading) {
				// return;
				// }
				// mZhimaAdapter.getData();
				// }
			}
		});

		setAdapter(mZhimaAdapter);
	}
//	
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			xDistance = yDistance = 0f;
//			xLast = ev.getX();
//			yLast = ev.getY();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			final float curX = ev.getX();
//			final float curY = ev.getY();
//
//			xDistance += Math.abs(curX - xLast);
//			yDistance += Math.abs(curY - yLast);
//			xLast = curX;
//			yLast = curY;
//
//			if (xDistance > yDistance) {
//				return false;
//			}
//		}
//
//		return super.onInterceptTouchEvent(ev);
//	}
}
