package com.zhima.ui.usercenter.data.lattice.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.EditText;

import com.zhima.R;

/**
 * @ClassName: LatticeNameEdit
 * @Description: 格子铺编辑名称
 * @author luqilong
 * @date 2013-1-21 下午1:45:43
 */
public class LatticeNameEditView extends BaseLatticeView {

	private EditText mEditView;

	public LatticeNameEditView(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.user_center_lattice_name_edit_view, null);
		mEditView = (EditText) mPluginView.findViewById(R.id.edt_content);
	}

	@Override
	public EditText getEditText() {
		return mEditView;
	}


	@Override
	public void setContent(String content) {
		mEditView.setText(content);
	}

	@Override
	public void setStyle(TypedArray typedArray) {

	}
}
