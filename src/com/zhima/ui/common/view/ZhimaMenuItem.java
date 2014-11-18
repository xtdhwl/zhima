package com.zhima.ui.common.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;

public class ZhimaMenuItem {
	private int mId;
	private String mTitle;
	private Drawable mIcon;
	private Context mContext;
	private View mView;

	private boolean mIsSelect;

	public ZhimaMenuItem(Context context) {
		mContext = context;
	}

	public void setSelection(boolean b) {
		// TODO Auto-generated method stub
		mIsSelect = b;
	}

	public boolean getSelection() {
		return mIsSelect;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public Drawable getIcon() {
		return mIcon;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public void setIcon(Drawable mIcon) {
		this.mIcon = mIcon;
	}

	public void initView() {
		initializedView();
	}

	private void initializedView() {
		View view = View.inflate(mContext, R.layout.popupmenu_textview, null);
		TextView tv = (TextView) view.findViewById(R.id.txt_title);
		ImageView iconImg = (ImageView) view.findViewById(R.id.img_icon);
		view.setId(mId);
		tv.setText(mTitle);

		if (mIsSelect) {
			iconImg.setVisibility(View.VISIBLE);
		}else{
			iconImg.setVisibility(View.INVISIBLE);
		}

		if (mIcon == null) {
			iconImg.setVisibility(View.GONE);
		} else {
			iconImg.setImageDrawable(mIcon);
		}

		mView = view;
	}

	public View getActionView() {
		return mView;
	}

}
