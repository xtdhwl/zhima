package com.zhima.ui.usercenter.watchdog.activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.adapter.ZhimaAdapter;

public class TextCardGalleryAdapter extends ZhimaAdapter<String> {
	private int selectItem;

	public TextCardGalleryAdapter(Context context, int layoutId, List<String> array) {
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
		return viewHolder;
	}

	@Override
	public void bindView(String data, int position, View view) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		viewHolder.mText.setText(data);
		if (position == selectItem) {
			viewHolder.mText.setTextColor(mContext.getResources().getColor(R.color.card_text_gallery));
		} else {
			viewHolder.mText.setTextColor(mContext.getResources().getColor(R.color.light_gray));
		}
	}

	private static class ViewHolder {
		public TextView mText;;
	}

}
