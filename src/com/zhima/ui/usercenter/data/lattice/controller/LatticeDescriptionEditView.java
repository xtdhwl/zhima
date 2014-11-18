package com.zhima.ui.usercenter.data.lattice.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhima.R;

/**
 * @ClassName: LatticeDescriptionEditView
 * @Description: 编辑描述
 * @author luqilong
 * @date 2013-1-21 下午2:12:03
 * 
 */
public class LatticeDescriptionEditView extends BaseLatticeView {

	// 签名输入最大数
	private static final int MAX_LENGTH = 140;

	private EditText mEditView;
	private TextView mPromptView;

	public LatticeDescriptionEditView(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.user_center_lattice_description_edit_view, null);
		mEditView = (EditText) mPluginView.findViewById(R.id.edt_content);
		mPromptView = (TextView) mPluginView.findViewById(R.id.txt_prompt);

		// 设置输入最大数 与 提醒
		mEditView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
		mPromptView.setText(String.format(context.getString(R.string.input_prompt), MAX_LENGTH));

		mEditView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 字符输入个数提示
				mPromptView.setText(String.format("还可以输入%s个字", MAX_LENGTH - s.length()));
			}
		});
	}

	@Override
	public EditText getEditText() {
		return mEditView;
	}

	@Override
	public void setStyle(TypedArray typedArray) {

	}

	@Override
	public void setContent(String content) {
		mEditView.setText(content);
	}

}
