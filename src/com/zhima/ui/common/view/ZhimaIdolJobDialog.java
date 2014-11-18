package com.zhima.ui.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

import com.zhima.R;
import com.zhima.data.model.IdolJob;
import com.zhima.data.service.IdolJobService;


/**
 * @ClassName ZhimaIdolJobDialog
 * @Description 知天使职业的选择框
 * @author jiang
 * @date 2013-1-29 下午05:40:48
 */
public class ZhimaIdolJobDialog extends CustomDialog {

	private Context mContext;
	private ArrayList<IdolJob> mIdolJobList;
	private WheelView mFirstWheel;
	private int mFirstIndex;
	private View mView;

	public ZhimaIdolJobDialog(Context context, View parentView) {
		super(context, parentView);
		this.mContext = context;

		setTitle(R.string.type_choose);
		mView = View.inflate(mContext, R.layout.zhima_single_regiond_layout,
				null);
		mIdolJobList = IdolJobService.getInstance(mContext)
				.getIdolJobList();

		setView();
		setMiddleLayout(getRegionView());
	}

	private void setView() {
		mFirstWheel = (WheelView) mView.findViewById(R.id.type_first);

		int textSize = 30;
		mFirstWheel.setVisibleItems(3);
		mFirstIndex = mFirstWheel.getCurrentItem();
		mFirstWheel.TEXT_SIZE = textSize;

		if (mIdolJobList != null) {
			mFirstWheel.setAdapter(new IdolJobWheelAdapter(mIdolJobList));
			mFirstWheel.setCurrentItem(0);
		}

		mFirstWheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mFirstIndex = newValue;
			}
		});
	}

	private View getRegionView() {
		return mView;
	}

	/**
	 * @Title: setCurrentItem
	 * @Description:设置显示第几个item
	 * @param
	 * @return void
	 */
	public void setCurrentItem(int position) {
		if (position >= 0) {
			mFirstWheel.setCurrentItem(position);
		}
	}

	/**
	 * @Title: getCurrentItem
	 * @Description:获取当前item的position
	 * @param
	 * @return void
	 */
	public int getCurrentItem() {
		if (mFirstWheel != null) {
			if (mFirstWheel.getCurrentItem() >= 0) {
				return mFirstWheel.getCurrentItem();
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * @Title: getFirstObject
	 * @Description:获取首个选择项的对象
	 * @param
	 * @return Spacekind
	 */
	public IdolJob getFirstObject() {
		mFirstIndex = mFirstWheel.getCurrentItem();
		if (mIdolJobList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mIdolJobList.size()) {
				IdolJob firstObject = mIdolJobList.get(mFirstIndex);
				return firstObject;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * @Title: getFirstItemString
	 * @Description:获取首个选择项的字符串
	 * @param
	 * @return String
	 */
	public String getFirstItemString() {
		mFirstIndex = mFirstWheel.getCurrentItem();
		if (mIdolJobList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mIdolJobList.size()) {
				String firstStr = mIdolJobList.get(mFirstIndex).getJob();
				return firstStr;
			} else {
				return null;
			}
		}
		return null;

	}

}
