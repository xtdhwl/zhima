package com.zhima.ui.usercenter.data.lattice.controller;

import android.content.Context;
import android.widget.EditText;

import com.zhima.plugin.BaseViewPlugin;

public abstract class  BaseLatticeView extends BaseViewPlugin {

	public BaseLatticeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public abstract EditText getEditText();
	public abstract void setContent(String content);
	

}
