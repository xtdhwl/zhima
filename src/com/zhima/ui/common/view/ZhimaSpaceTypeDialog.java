package com.zhima.ui.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.zhima.R;
import com.zhima.data.model.Spacekind;
import com.zhima.data.service.SpaceKindService;

/**
 * @ClassName:ZhimaSpaceTypeDialog
 * @Description 类型的选择框
 * @author jiangwei
 * @date 2012-8-13 9:02:47
 */
public class ZhimaSpaceTypeDialog extends CustomDialog {

	public int mItemCount = 1;
	private Context mContext;
	private ArrayList<Spacekind> mSpaceTypeList, mSonTypeList;
	private WheelView mFirstWheel, mSecondWheel;
	private int mFirstIndex, mSecondIndex;
	private View mView;

	public ZhimaSpaceTypeDialog(Context context, View parentView, int itemCount, long spaceMainId) {
		super(context, parentView);
		this.mContext = context;

		if (itemCount == 2) {
			this.mItemCount = 2;
		} else {
			this.mItemCount = 1;
		}

		setTitle(R.string.type_choose);
		mSpaceTypeList = SpaceKindService.getInstance(mContext).getMainTypeList(spaceMainId);
		mView = View.inflate(mContext, R.layout.zhima_typepicker_layout, null);

		setView();
		setMiddleLayout(getRegionView());
	}

	private void setView() {
		mFirstWheel = (WheelView) mView.findViewById(R.id.type_first);
		mSecondWheel = (WheelView) mView.findViewById(R.id.type_second);

		int textSize = 30;
		mFirstWheel.setVisibleItems(3);
		mFirstIndex = mFirstWheel.getCurrentItem();
		mFirstWheel.TEXT_SIZE = textSize;

		if (mItemCount == 1) {
			mSecondWheel.setVisibility(View.GONE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(60, 0, 60, 0);
			mFirstWheel.setLayoutParams(lp);
		} else {
			mSecondIndex = mSecondWheel.getCurrentItem();
			mSecondWheel.TEXT_SIZE = textSize;
			mSecondWheel.setVisibleItems(3);
		}

		if (mSpaceTypeList != null) {
			mFirstWheel.setAdapter(new SpaceTypeWheelAdapter(mSpaceTypeList));
			mFirstWheel.setCurrentItem(0);
		}

		if (mSpaceTypeList != null && !mSpaceTypeList.isEmpty() && mSpaceTypeList.size() >= 0 && mItemCount != 1) {
			Spacekind spaceType = mSpaceTypeList.get(0);
			mSonTypeList = SpaceKindService.getInstance(mContext).getSubTypeList(spaceType.getId());
			mSecondWheel.setAdapter(new SpaceTypeWheelAdapter(mSonTypeList));
		}

		mFirstWheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mFirstIndex = newValue;
				if (mItemCount != 1) {
					Spacekind spaceType = mSpaceTypeList.get(newValue);
					mSonTypeList = SpaceKindService.getInstance(mContext).getSubTypeList(spaceType.getId());
					mSecondWheel.setAdapter(new SpaceTypeWheelAdapter(mSonTypeList));
					if (mSonTypeList != null) {
						mSecondWheel.setCurrentItem(mSonTypeList.size() / 2);
					}
				}
			}
		});
	}

	private View getRegionView() {
		return mView;
	}

	/**
	 * @Title: setDateList
	 * @Description:单选模式下使用
	 * @param spaceTypeList
	 * @return void
	 */
	public void setDataList(ArrayList<Spacekind> spaceTypeList) {
		if (mItemCount == 1) {
			this.mSpaceTypeList = spaceTypeList;
			setView();
		}
	}

	/**
	 * @Title: getFirstObject
	 * @Description:获取首个选择项的对象
	 * @param
	 * @return Spacekind
	 */
	public Spacekind getFirstObject() {
		mFirstIndex = mFirstWheel.getCurrentItem();
		if (mSpaceTypeList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mSpaceTypeList.size()) {
				Spacekind firstObject = mSpaceTypeList.get(mFirstIndex);
				return firstObject;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * @Title: getSecondObject
	 * @Description:获取第二个选项的对象
	 * @param
	 * @return Spacekind
	 */
	public Spacekind getSecondObject() {
		mSecondIndex = mSecondWheel.getCurrentItem();
		if (mSonTypeList != null) {
			if (mSecondIndex >= 0 && mSecondIndex < mSonTypeList.size()) {
				Spacekind secondObject = mSonTypeList.get(mSecondIndex);
				return secondObject;
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	/**
	 * @Title: setFirstItemPosition
	 * @Description:TODO
	 * @param
	 * @return void
	 */
	public void setFirstCurrentItem(int positon) {
		if (mFirstWheel != null) {
			if (positon >= 0) {
				mFirstWheel.setCurrentItem(positon);
			}
		}
	}

	/**
	 * @Title: getFirstCurrentItem
	 * @Description:获取第一列当前item的position
	 * @param
	 * @return void
	 */
	public int getFirstCurrentItem() {
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
	 * @Title: getSecondCurrentItem
	 * @Description:获取第二列当前item的position
	 * @param
	 * @return void
	 */
	public int getSecondCurrentItem() {
		if (mSecondWheel != null) {
			if (mSecondWheel.getCurrentItem() >= 0) {
				return mSecondWheel.getCurrentItem();
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * @Title: getFirstItemString
	 * @Description:获取首个选择项的字符串
	 * @param
	 * @return String
	 */
	public String getFirstItemString() {
		mFirstIndex = mFirstWheel.getCurrentItem();
		if (mSpaceTypeList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mSpaceTypeList.size()) {
				String firstStr = mSpaceTypeList.get(mFirstIndex).getName();
				return firstStr;
			} else {
				return null;
			}
		}
		return null;

	}

	/**
	 * @Title: getsecondItemString
	 * @Description:返回第二个选择项的字符串
	 * @param
	 * @return String
	 */
	public String getsecondItemString() {
		mSecondIndex = mSecondWheel.getCurrentItem();
		if (mSonTypeList != null) {
			if (mSecondIndex >= 0 && mSecondIndex < mSonTypeList.size()) {
				String secondStr = mSonTypeList.get(mSecondIndex).getName();
				return secondStr;
			} else {
				return null;
			}
		}
		return null;
	}

}
