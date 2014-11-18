package com.zhima.ui.common.view;

import java.util.List;

import com.zhima.data.model.IdolJob;


/**
 * @ClassName IdolJobWheelAdapter
 * @Description 知天使职业的dialog的adapter
 * @author jiang
 * @date 2013-1-29 下午05:41:49
 */
public class IdolJobWheelAdapter extends MultiWheelAdapter<IdolJob> {

	private List<IdolJob> mSpaceList;
	private int mLength;

	public IdolJobWheelAdapter(List<IdolJob> spaceList, int length) {
		super(spaceList, length);
		// TODO Auto-generated constructor stub
		this.mSpaceList = spaceList;
		this.mLength = length;
	}

	public IdolJobWheelAdapter(List<IdolJob> spaceList) {
		this(spaceList, spaceList == null ? 0 : spaceList.size());
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		if (index >= 0 && index < mSpaceList.size()) {
			return mSpaceList.get(index).getJob();
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return mSpaceList.size();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return mLength;
	}

}
