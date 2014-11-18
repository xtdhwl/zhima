package com.zhima.ui.common.view;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * viewpager Adapter
 * @ClassName: ZmVpAdapter
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-9 上午10:20:58
 * @param <T>
 */
public abstract class ZmVpAdapter<T> extends PagerAdapter {

	protected Context mContext;
	protected float mPreviewCount;
	protected int mMaxListSize;
	protected List<T> mArray;

	private float mPageWidth;

	/**
	 * SpaceViewPager 左右滑动ViewPager adapter
	 * @param context
	 * @param array list数据
	 * @param previewCount 显示预览的个数
	 * @param maxListSize 展示的最大个数
	 */
	public ZmVpAdapter(Context context, List<T> array, int previewCount, int maxListSize) {
		super();
		this.mContext = context;
		this.mArray = array;
		this.mPreviewCount = previewCount;
		this.mPageWidth = 1f / (float) previewCount;
		this.mMaxListSize = maxListSize;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		//XXX 处理越界
		T data = mArray.get(position);
		return getView(container, data, position);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewGroup) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public int getCount() {
		//		mListSize = mListSize > mMaxListSize ? mMaxListSize : mListSize;
		return Math.min(mArray.size(), mMaxListSize);
	}

	//		public void setListSize(int listSize) {
	//			this.mListSize = listSize;
	//		}

	@Override
	public float getPageWidth(int position) {
		return mPageWidth;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
	}

	/**
	 * 填充条目布局
	 * 
	 * @Title: initItems
	 * @Description: TODO
	 * @param container
	 * @param position
	 * @return
	 */
	public abstract Object getView(ViewGroup container, T data, int position);
}
