package com.zhima.ui.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.zhima.R;
import com.zhima.data.model.Region;
import com.zhima.data.service.RegionService;

/**
 * @ClassName:ZhimaRegionDialog
 * @Description 通用的省市区选择框
 * @author jiangwei
 * @date 2012-8-13 9:02:47
 */
public class ZhimaRegionDialog extends CustomDialog {

	public int mItemCount = 1;
	private Context mContext;
	private ArrayList<Region> mProvinceList, mCitysList, mRegionList;
	private WheelView mFirstWheel, mSecondWheel;
	private int mFirstIndex, mSecondIndex;
	private View mView;
	private long mProvinceId;
	private boolean mNeedAll = true;

	/**
	 * @param context
	 * @param parentView
	 * @param itemCount
	 *            需要显示的列数
	 * @param provinceId
	 *            省份的id
	 * @param needAll 是否需要"全部"项
	 */
	public ZhimaRegionDialog(Context context, View parentView, int itemCount, long provinceId, boolean needAll) {
		super(context, parentView);
		this.mContext = context;
		this.mProvinceId = provinceId;
		mNeedAll = needAll;

		if (itemCount == 2) {
			this.mItemCount = 2;
		} else {
			this.mItemCount = 1;
		}

		if (itemCount != 1) {
			mCitysList = RegionService.getInstance(mContext).getCityList(mProvinceId, mNeedAll);
		}

		mProvinceList = RegionService.getInstance(mContext).getProvinceList();

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
		if (mCitysList != null) {
			mFirstWheel.setAdapter(new RegionWheelAdapter(mCitysList));
			mFirstWheel.setCurrentItem(0);
		}

		if (mCitysList != null && mItemCount != 1) {
			Region spaceType = mCitysList.get(0);
			mRegionList = RegionService.getInstance(mContext).getRegionList(spaceType.getId(), mNeedAll);
			mSecondWheel.setAdapter(new RegionWheelAdapter(mRegionList));
		}

		mFirstWheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mFirstIndex = newValue;
				if (mItemCount != 1) {
					Region spaceType = mCitysList.get(newValue);
					mRegionList = RegionService.getInstance(mContext).getRegionList(spaceType.getId(), mNeedAll);
					mSecondWheel.setAdapter(new RegionWheelAdapter(mRegionList));
					if (mSecondWheel != null) {
						if (mRegionList.size() >= 0) {
							mSecondWheel.setCurrentItem(0);
						}
					}
				}
			}
		});
	}

	private View getRegionView() {
		return mView;
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
	 * @Title: setFirstCurrentItem
	 * @Description:通过已知的字符串设置显示位置
	 * @param 设置显示的第一个item的字符串
	 * @return void
	 */
	public void setFirstCurrentItem(String position) {
		if (mItemCount == 1) {
			if (position != null && mFirstWheel != null && mProvinceList != null) {
				for (int i = 0, count = mProvinceList.size(); i < count; i++) {
					if (mProvinceList.get(i) != null) {
						if (position.trim().equals(mProvinceList.get(i).getName().trim())) {
							mFirstWheel.setCurrentItem(i);
						}
					}
				}
			}
		} else if (mItemCount == 2) {
			if (mFirstWheel != null && mCitysList != null) {
				if (TextUtils.isEmpty(position) || TextUtils.isEmpty(position.trim())) {
					if (mCitysList.size() > 1) {
						mFirstWheel.setCurrentItem(1);
					}
					return;
				} else {
					for (int i = 0, count = mCitysList.size(); i < count; i++) {
						if (mCitysList.get(i) != null) {
							if (position.trim().equals(mCitysList.get(i).getName().trim())) {
								mFirstWheel.setCurrentItem(i);
							}
						}
					}
				}
			}
		}

	}

	/**
	 * @Title: setFirstItemPosition
	 * @Description:TODO
	 * @param
	 * @return void
	 */
	public void setSecondCurrentItem(int positon) {
		if (mSecondWheel != null) {
			if (positon >= 0) {
				mSecondWheel.setCurrentItem(positon);
			}
		}
	}

	/**
	 * @Title: setSecondCurrtentItem
	 * @Description:通过已知的字符串设置显示位置
	 * @param position
	 *            设置显示的第二个item的字符串
	 * @return void
	 */
	public void setSecondCurrtentItem(String position) {
		if (mSecondWheel != null && mRegionList != null) {
			for (int i = 0, count = mRegionList.size(); i < count; i++) {
				if (mRegionList.get(i) != null) {
					if (position.trim().equals(mRegionList.get(i).getName().trim())) {
						mSecondWheel.setCurrentItem(i);
					}
				}
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
	 * @Title: setDateList
	 * @Description:单选模式下使用
	 * @param spaceTypeList
	 * @return void
	 */
	public void setDataList(ArrayList<Region> regionList) {
		if (mItemCount == 1) {
			this.mCitysList = regionList;
			setView();
		}
	}

	/**
	 * @Title: getFirstObject
	 * @Description:获取首个选择项的对象
	 * @param
	 * @return Spacekind
	 */
	public Region getFirstObject() {
		mFirstIndex = mFirstWheel.getCurrentItem();
		if (mCitysList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mCitysList.size()) {
				Region firstObject = mCitysList.get(mFirstIndex);
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
	public Region getSecondObject() {
		mSecondIndex = mSecondWheel.getCurrentItem();
		if (mRegionList != null) {
			if (mSecondIndex >= 0 && mSecondIndex < mRegionList.size()) {
				Region secondObject = mRegionList.get(mSecondIndex);
				return secondObject;
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
		if (mCitysList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mCitysList.size()) {
				String firstStr = mCitysList.get(mFirstIndex).getName();
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
		if (mRegionList != null) {
			if (mSecondIndex >= 0 && mSecondIndex < mRegionList.size()) {
				String secondStr = mRegionList.get(mSecondIndex).getName();
				return secondStr;
			} else {
				return null;
			}
		}
		return null;
	}

}
