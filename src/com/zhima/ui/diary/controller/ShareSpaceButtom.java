package com.zhima.ui.diary.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;

public class ShareSpaceButtom extends LinearLayout {

	/**
	 * 空间Id
	 */
	private long mSpaceId;
	/**
	 * 空间名称
	 */
	private TextView mNameView;
	/**
	 * 是否选中
	 */
	private CheckBox mCheckBoxView;

	public ShareSpaceButtom(Context context) {
		this(context, null);
	}

	public ShareSpaceButtom(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.diary_share_button, this);

		mNameView = (TextView) findViewById(R.id.txt_space_name);
		mCheckBoxView = (CheckBox) findViewById(R.id.cb_space);
	}

	public void setSpaceName(String spaceName) {
		mNameView.setText(spaceName);
	}

	public String getSpaceName() {
		return mNameView.getText().toString();
	}

	public long getSpaceId() {
		return mSpaceId;
	}

	public void setSpaceId(long mSpaceId) {
		this.mSpaceId = mSpaceId;
	}

	public boolean isChecked() {
		return mCheckBoxView.isChecked();
	}

	public void setChecked(boolean checked) {
		mCheckBoxView.setChecked(checked);
	}
}
