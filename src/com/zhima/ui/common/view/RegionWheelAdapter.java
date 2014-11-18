package com.zhima.ui.common.view;

import java.util.List;

import com.zhima.data.model.Region;

/**
 * @ClassName RegionWheelAdapter
 * @Description 地区选择的adapter
 * @author jiang
 * @date 2012-9-21 下午07:57:34
 */
public class RegionWheelAdapter extends MultiWheelAdapter<Region> {

	private List<Region> mSpaceList;
	private int mLength;

	public RegionWheelAdapter(List<Region> spaceList, int length) {
		super(spaceList, length);
		// TODO Auto-generated constructor stub
		this.mSpaceList = spaceList;
		this.mLength = length;
	}

	public RegionWheelAdapter(List<Region> spaceList) {
		this(spaceList, spaceList == null ? 0 : spaceList.size());
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		if (mSpaceList != null) {
			if (index >= 0 && index < mSpaceList.size()) {
				return mSpaceList.get(index).getName();
			}
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		if (mSpaceList != null) {
			return mSpaceList.size();
		}
		return -1;
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return mLength;
	}

}
