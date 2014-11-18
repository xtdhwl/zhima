package com.zhima.ui.usercenter.data.controller;

import java.util.ArrayList;
import java.util.List;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @ClassName: RadioSingleManage
 * @Description: 解决Radio不能多行多列的问题
 * @author luqilong
 * @date 2013-1-15 下午3:10:19
 */
public class CheckBoxSingleManage implements OnCheckedChangeListener {

	private List<CheckBox> radioList = null;
	private OnCheckedChangeListener mOnCheckedChangeListener = null;
	public CheckBoxSingleManage() {
		super();
		this.radioList = new ArrayList<CheckBox>();
	}

	public void addCheckButton(CheckBox radio) {
		//这里使用setOnCheckedChangeListener（）如果有需要可以
		radio.setOnCheckedChangeListener(this);
		radioList.add(radio);
	}

	public void removeButton(CheckBox radio) {
		radio.setOnCheckedChangeListener(null);
		radioList.remove(radio);
	}

	public void onChanged(int id) {
		for (CheckBox rb : radioList) {
			if(rb.getId() != id){
				rb.setChecked(false);
				if(mOnCheckedChangeListener != null){
					mOnCheckedChangeListener.onCheckedChanged(rb, false);
				}
			}else{
				if(mOnCheckedChangeListener != null){
					mOnCheckedChangeListener.onCheckedChanged(rb, true);
				}
			}
		}
	}
	
	public CheckBox getSelectCheckButton(){
		for (CheckBox rb : radioList) {
			if(rb.isChecked()){
				return rb;
			}
		}
		return null;
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			onChanged(buttonView.getId());
		}
	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
		mOnCheckedChangeListener = listener;
	}

}
