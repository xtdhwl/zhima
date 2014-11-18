package com.zhima.ui.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.zhima.R;
import com.zhima.data.model.Region;
import com.zhima.data.service.RegionService;

/**
 * @ClassName:ZhimaRegionDialog
 * @Description 通用的省市区选择框
 * @author jiangwei
 * @date 2012-8-13 9:02:47
 */
public class ZhimaRegionThreeDialog extends CustomDialog {

	private Context mContext;
	private ArrayList<Region> mProvinceList, mCitysList, mRegionList;
	private ShortWheelView mFirstWheel, mSecondWheel, mThirdWheel;
	private int mFirstIndex, mSecondIndex, mThirdIndex;
	private View mView;
	private RegionService mReginService;
	/** 是否需要“全部”这个选项 */
	private boolean mNeedItemAll = true;

	/**
	 * @param context
	 * @param parentView
	 * @param itemCount
	 *            需要显示的列数
	 * @param provinceId
	 *            省份的id
	 */
	public ZhimaRegionThreeDialog(Context context, View parentView, boolean isNeedItemAll) {
		super(context, parentView);
		this.mContext = context;
		this.mNeedItemAll = isNeedItemAll;
		mReginService = RegionService.getInstance(mContext);
		mProvinceList = mReginService.getProvinceList();
		// mCitysList = mReginService.getCityList(mProvinceId);

		mView = View.inflate(mContext, R.layout.zhima_region_three_layout, null);

		setView();
		setMiddleLayout(getRegionView());
	}

	private void setView() {
		mFirstWheel = (ShortWheelView) mView.findViewById(R.id.province);
		mSecondWheel = (ShortWheelView) mView.findViewById(R.id.city);
		mThirdWheel = (ShortWheelView) mView.findViewById(R.id.region);

		int textSize = 30;
		mFirstWheel.setVisibleItems(3);
		mFirstIndex = mFirstWheel.getCurrentItem();
		mFirstWheel.TEXT_SIZE = textSize;

		mSecondWheel.TEXT_SIZE = textSize;
		mSecondWheel.setVisibleItems(3);
		if (mProvinceList != null && !mProvinceList.isEmpty()) {
			mFirstWheel.setAdapter(new RegionWheelAdapter(mProvinceList));
			mFirstWheel.setCurrentItem(0);
			Region city = mProvinceList.get(0);
			if (city != null) {
				mCitysList = mReginService.getCityList(city.getId(), mNeedItemAll);

			}
			if (mCitysList != null && !mCitysList.isEmpty()) {
				mSecondWheel.setAdapter(new RegionWheelAdapter(mCitysList));
				mSecondIndex = mSecondWheel.getCurrentItem();
				if (mCitysList.size() > 0) {
					Region region = mCitysList.get(0);
					if (region != null) {
						mRegionList = mReginService.getRegionList(region.getId(), mNeedItemAll);
						mThirdWheel.setAdapter(new RegionWheelAdapter(mRegionList));
						mThirdIndex = mThirdWheel.getCurrentItem();
					}
				}

			}
		}

		mFirstWheel.addChangingListener(new OnShortWheelChangedListener() {
			public void onChanged(ShortWheelView wheel, int oldValue, int newValue) {
				mFirstIndex = newValue;
				Region city = mProvinceList.get(newValue);
				mCitysList = mReginService.getCityList(city.getId(), mNeedItemAll);
				mSecondWheel.setAdapter(new RegionWheelAdapter(mCitysList));
				if (mSecondWheel != null) {
					if (mCitysList.size() > 0) {
						mSecondWheel.setCurrentItem(0);
						Region region = mCitysList.get(0);
						if (mThirdWheel != null) {
							mRegionList = mReginService.getRegionList(region.getId(), mNeedItemAll);
//							if (!needItemAll) {
//								if (mRegionList.get(0).getId() == -1) {
//									mRegionList.remove(0);
//								}
//							}
							mThirdWheel.setAdapter(new RegionWheelAdapter(mRegionList));
						}
					}

				}
			}
		});

		mSecondWheel.addChangingListener(new OnShortWheelChangedListener() {

			@Override
			public void onChanged(ShortWheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				mSecondIndex = newValue;
				Region region = mCitysList.get(newValue);
				if (mThirdWheel != null) {
					mRegionList = mReginService.getRegionList(region.getId(), mNeedItemAll);
					mThirdWheel.setAdapter(new RegionWheelAdapter(mRegionList));
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
		if (mFirstWheel != null && mProvinceList != null) {
			if (TextUtils.isEmpty(position) || TextUtils.isEmpty(position.trim())) {
				if (mProvinceList.size() > 1) {
					mFirstWheel.setCurrentItem(1);
				}
				return;
			} else {
				for (int i = 0, count = mProvinceList.size(); i < count; i++) {
					if (mProvinceList.get(i) != null) {
						if (position.trim().equals(mProvinceList.get(i).getName().trim())) {
							mFirstWheel.setCurrentItem(i);
						}
					}
				}
			}
		}

	}

	/**
	 * @Title: setSecondCurrentItem
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
		if (mSecondWheel != null && mCitysList != null) {
			for (int i = 0, count = mCitysList.size(); i < count; i++) {
				if (mCitysList.get(i) != null) {
					if (position.trim().equals(mCitysList.get(i).getName().trim())) {
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
	 * @Title: getFirstObject
	 * @Description:获取首个选择项的对象
	 * @param
	 * @return Region
	 */
	public Region getFirstObject() {
		mFirstIndex = mFirstWheel.getCurrentItem();
		if (mProvinceList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mProvinceList.size()) {
				Region firstObject = mProvinceList.get(mFirstIndex);
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
	 * @return Region
	 */
	public Region getSecondObject() {
		mSecondIndex = mSecondWheel.getCurrentItem();
		if (mCitysList != null) {
			if (mSecondIndex >= 0 && mSecondIndex < mCitysList.size()) {
				Region secondObject = mCitysList.get(mSecondIndex);
				return secondObject;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * @Title: getThirdObject
	 * @Description:获取第三个选项的对象
	 * @param
	 * @return Region
	 */
	public Region getThirdObject() {
		mThirdIndex = mThirdWheel.getCurrentItem();
		if (mRegionList != null) {
			if (mThirdIndex >= 0 && mThirdIndex < mRegionList.size()) {
				Region secondObject = mRegionList.get(mThirdIndex);
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
		if (mProvinceList != null) {
			if (mFirstIndex >= 0 && mFirstIndex < mProvinceList.size()) {
				String firstStr = mProvinceList.get(mFirstIndex).getName();
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
		if (mCitysList != null) {
			if (mSecondIndex >= 0 && mSecondIndex < mCitysList.size()) {
				String secondStr = mCitysList.get(mSecondIndex).getName();
				return secondStr;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * @Title: getThirdItemString
	 * @Description:返回第三个选择项的字符串
	 * @param
	 * @return String
	 */
	public String getThirdItemString() {
		mThirdIndex = mThirdWheel.getCurrentItem();
		if (mRegionList != null) {
			if (mThirdIndex >= 0 && mThirdIndex < mRegionList.size()) {
				String secondStr = mRegionList.get(mThirdIndex).getName();
				return secondStr;
			} else {
				return null;
			}
		}
		return null;
	}

}
