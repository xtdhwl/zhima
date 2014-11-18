package com.zhima.ui.diary.adapter;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.zhima.R;
import com.zhima.data.model.ZMDiary;
import com.zhima.ui.common.view.SpacePagerAdapter;
import com.zhima.ui.common.view.VideoWebView;
import com.zhima.ui.diary.activity.DiaryInfoActivity;
import com.zhima.ui.usercenter.data.lattice.adapter.LatticePagerAdapter;

public class DiaryInfoViewPager extends SpacePagerAdapter {

	private static final String TAG = LatticePagerAdapter.class.getSimpleName();

	private DiaryInfoActivity activity;
	private List<ZMDiary> mArray;
	private OnClickListener mOnClickListener;
	private int mLoadDataPosition = -1;

	public DiaryInfoViewPager(DiaryInfoActivity activity, List<ZMDiary> array) {
		this.activity = activity;
		mArray = array;
	}

	public Object getView(ViewGroup container, ZMDiary data, int position) {
		VideoWebView webView = new VideoWebView(activity);
		webView.setId(position);
		webView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		webView.setBackgroundColor(activity.getResources().getColor(R.color.white_color));
		webView.setTag(data);
		webView.setWebViewClient(new WebViewClient());
		if (mLoadDataPosition == position) {
			activity.setWebViewContent(webView, data, true);
		}
		container.addView(webView, 0);
		return webView;
	}

	public void setLoadDataItem(int position) {
		mLoadDataPosition = position;
	}

	public void setOnClickListener(OnClickListener listener) {
		mOnClickListener = listener;
	}

	public void setData(List<ZMDiary> mLatticeProductList) {
		mArray = mLatticeProductList;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ZMDiary data = mArray.get(position);
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
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mArray.size();
	}

}
