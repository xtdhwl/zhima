package com.zhima.ui.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.Notice;

public class ListCardView extends LinearLayout {

	private TextView mTitleText;
	private ImageView mArrowImag;
	private TextView mNoticeOneText;
//	private TextView mNoticeTwoText;
	private String mTitle;

	private OnClickListener mOnClickListener;
	private ArrayList<Notice> mNoticesList;

	public ListCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.listcard_view, this);

		mTitleText = (TextView) findViewById(R.id.txt_title);
		mNoticeOneText = (TextView) findViewById(R.id.txt_notice_one);
//		mNoticeTwoText = (TextView) findViewById(R.id.txt_notice_two);
		mArrowImag = (ImageView) findViewById(R.id.arrow);

		mArrowImag.setOnClickListener(clickListener);
		mNoticeOneText.setOnClickListener(clickListener);
//		mNoticeTwoText.setOnClickListener(clickListener);

//		mNoticeTwoText.setVisibility(View.GONE);
		
		initCardView();
		Resources resoutces = context.getResources();
		TypedArray a = resoutces.obtainAttributes(attrs, R.styleable.CardView);
		mTitle = a.getString(R.styleable.CardView_title);
		if (mTitle != null) {
			mTitleText.setText(mTitle);
		}
		a.recycle();
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		mOnClickListener = listener;
	}

	public ListCardView(Context context) {
		this(context, null);
	}

	public void setData(ArrayList<Notice> notices) {
		initCardView();
		mNoticesList = notices;
//		int count = notices.size() - 1;
		if(notices.size() >= 1){
			Notice notice = notices.get(0);
			if (mNoticeOneText.getTag() == null) {
				mNoticeOneText.setTag(notice);
				mNoticeOneText.setText(getContent(notice));
				mNoticeOneText.setClickable(true);
			}
			if(notices.size() > 1){
				mArrowImag.setTag(notice);
				mArrowImag.setVisibility(View.VISIBLE);
				mArrowImag.setClickable(true);
			}
		}else{
			mNoticeOneText.setClickable(false);
		}
		
//		for (int i = 0; i <= count; i++) {
//			 /*else {
//				if (mNoticeTwoText.getTag() == null) {
//					mNoticeTwoText.setText(getContent(notice));
//					mNoticeTwoText.setTag(notice);
//					mNoticeTwoText.setClickable(true);
//
//					
//				}
//			}*/
//		}
	}

	private void initCardView() {
		mArrowImag.setTag(null);
		mNoticeOneText.setTag(null);
//		mNoticeTwoText.setTag(null);

		mArrowImag.setClickable(false);
		mNoticeOneText.setClickable(false);
//		mNoticeTwoText.setClickable(false);

		mArrowImag.setVisibility(View.INVISIBLE);
		mNoticeOneText.setText("");
//		mNoticeTwoText.setText("");
	}

	private String getContent(Notice notice) {
		String result = notice.getTitle();
		if (result == null || result.trim().equals("")) {
			result = notice.getContent();
		}
		return result;
	}

	public void setTitle(String str) {
		mTitleText.setText(str);
	}

	public void setVisibility(int id, int state) {
		findViewById(id).setVisibility(state);
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mOnClickListener != null) {
				mOnClickListener.onClick(v);
			}
		}
	};

}
