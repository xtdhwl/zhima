package com.zhima.ui.usercenter.watchdog.activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.adapter.ZhimaAdapter;

public class TextGalleryAdapter extends ZhimaAdapter<String> {
	private int selectItem;

	public TextGalleryAdapter(Context context, int layoutId, List<String> array) {
		super(context, layoutId, array);
		// TODO Auto-generated constructor stub
	}

	public void setSelectItem(int selectItem) {

		if (this.selectItem != selectItem) {
			this.selectItem = selectItem;
			notifyDataSetChanged();
		}
	}

	@Override
	public Object createViewHolder(View view, String data) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mText = (TextView) view.findViewById(R.id.text_space_name);
		viewHolder.mView = (View) view.findViewById(R.id.view_empty);
		return viewHolder;
	}

	@Override
	public void bindView(String data, int position, View view) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		viewHolder.mText.setText(data);
		if (position == selectItem) {
			viewHolder.mText.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_12));
			viewHolder.mView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.mText.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_10));
			viewHolder.mView.setVisibility(View.GONE);
		}
	}

	private static class ViewHolder {
		public TextView mText;;
		public View mView;
	}

}
