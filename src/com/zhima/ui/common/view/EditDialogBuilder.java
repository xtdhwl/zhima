package com.zhima.ui.common.view;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.zhima.R;

public class EditDialogBuilder {
	final private Context mContext;
	final private String mTitle;
	private String mContent = "";
	private int mInputType = InputType.TYPE_CLASS_TEXT;
	private int mMaxLength = 0;
	private int mMinLength = 1;
	private int mPositiveButton = R.string.ok;
	private EditText mEdit;
	private String mOverflowWarning;

	boolean mNoInput = false;

	public EditDialogBuilder(Context c, String title, int maxLength) {
		mContext = c;
		mTitle = title;
		mMaxLength = maxLength;
		mOverflowWarning = c.getString(R.string.edit_overflow);
	}

	public EditDialogBuilder(Context c, int title, int maxLength) {
		this(c, c.getString(title), maxLength);
	}

	public EditDialogBuilder setContent(String content) {
		mContent = content;
		return this;
	}

	public EditDialogBuilder setInputType(int inputType) {
		mInputType = inputType;
		return this;
	}

	public EditDialogBuilder setMaxLength(int maxLength) {
		mMaxLength = maxLength;
		return this;
	}

	public EditDialogBuilder setMinLength(int minLength) {
		mMinLength = minLength;
		return this;
	}

	public EditDialogBuilder setPositiveButton(int positiveButton) {
		mPositiveButton = positiveButton;
		return this;
	}

	public View show(DialogCallback callback) {
		return null;
	}
}
