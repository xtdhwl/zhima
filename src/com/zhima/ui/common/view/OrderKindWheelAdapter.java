package com.zhima.ui.common.view;

import java.util.List;

import com.zhima.data.model.Orderkind;

/**
 * @ClassName OrderKindWheelAdapter
 * @Description 排序类型的adpter
 * @author jiang
 * @date 2012-9-21 下午07:56:38
 */
public class OrderKindWheelAdapter extends MultiWheelAdapter<Orderkind> {

	private List<Orderkind> mSpaceList;
	private int mLength;

	public OrderKindWheelAdapter(List<Orderkind> spaceList, int length) {
		super(spaceList, length);
		// TODO Auto-generated constructor stub
		this.mSpaceList = spaceList;
		this.mLength = length;
	}

	public OrderKindWheelAdapter(List<Orderkind> spaceList) {
		this(spaceList, spaceList == null ? 0 : spaceList.size());
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		if (index >= 0 && index < mSpaceList.size()) {
			return mSpaceList.get(index).getTitle();
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
