package com.zhima.ui.space.controller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;

public class NormalCardView extends RelativeLayout {

	private TextView contentView;
	private ImageView iconView;
	private ImageView arrowView;

	private String content;
	private Drawable drawable;
	private int arrowVisibility;
	private OnCardClickListener mOnCardClickListener;

	public NormalCardView(Context context) {
		this(context, null);
	}

	public NormalCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		inflater.inflate(R.layout.space_normal_card_view, this);
		inflater.inflate(R.layout.space_normal_card_view, this ,true);
		contentView = (TextView) findViewById(R.id.txt_content);
		iconView = (ImageView) findViewById(R.id.img_icon);
		arrowView = (ImageView) findViewById(R.id.img_arrow);
		this.setOnClickListener(cardClick);
		this.setBackgroundResource(R.drawable.topbar_button_bg_selecter);
		
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NormalCardView);
		arrowVisibility = a.getInt(R.styleable.NormalCardView_arrow, View.VISIBLE);
		arrowView.setVisibility(arrowVisibility);
		drawable = a.getDrawable(R.styleable.NormalCardView_icon);
		if (drawable != null) {
			iconView.setImageDrawable(drawable);
		} else {
			iconView.setVisibility(View.GONE);
		}
		content = a.getString(R.styleable.NormalCardView_content);
		if (content != null) {
			contentView.setText(content);
		}
		a.recycle();
	}

	private View.OnClickListener cardClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mOnCardClickListener != null) {
				mOnCardClickListener.onCardClickListener(v, false);
			}
		}
	};

	public void setContent(String content) {
		contentView.setText(content);
	}

	public void setOnCradClickListener(OnCardClickListener listener) {
		mOnCardClickListener = listener;
	}

	public interface OnCardClickListener {
		void onCardClickListener(View view, boolean isArrow);
	}
}
