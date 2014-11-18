package com.zhima.ui.common.view;

import java.util.List;

import com.zhima.data.model.Spacekind;

/**
 * @ClassName SpaceTypeWheelAdapter
 * @Description 选择空间类型的adpter
 * @author jiang
 * @date 2012-9-21 下午07:56:38
 */
public class SpaceTypeWheelAdapter extends MultiWheelAdapter<Spacekind>{
	
	private List<Spacekind> mSpaceList;
	private int mLength;

	public SpaceTypeWheelAdapter(List<Spacekind> spaceList, int length) {
		super(spaceList, length);
		// TODO Auto-generated constructor stub
		this.mSpaceList = spaceList;
		this.mLength = length;
	}
	
	public SpaceTypeWheelAdapter(List<Spacekind> spaceList){
		this(spaceList, spaceList == null ? 0 : spaceList.size());
	}
	
	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		if(mSpaceList != null) {
			if(index >= 0 && index < mSpaceList.size()) {
				return mSpaceList.get(index).getName();
			}
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		if(mSpaceList != null) {
			return mSpaceList.size();
		} else {
			return -1;
		}
		
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return mLength;
	}	
}
