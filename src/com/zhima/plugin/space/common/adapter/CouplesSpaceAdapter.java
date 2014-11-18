package com.zhima.plugin.space.common.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.ZMObject;
import com.zhima.ui.adapter.ZhimaAdapter;

public class CouplesSpaceAdapter extends ZhimaAdapter<ZMObject> {

	private int selectItem;

	public CouplesSpaceAdapter(Context context, int layoutId, List<ZMObject> array) {
		super(context, layoutId, array);
	}

	public void setSelectItem(int selectItem) {

		if (this.selectItem != selectItem) {
			this.selectItem = selectItem;
			notifyDataSetChanged();
		}
	}

	@Override
	public Object createViewHolder(View view, ZMObject data) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mText = (TextView) view.findViewById(R.id.text_space_name);
		viewHolder.mView = (View) view.findViewById(R.id.view_empty);
		return viewHolder;
	}

	@Override
	public void bindView(ZMObject data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		String str = data.getName();

		if (position == selectItem) {
			viewHolder.mText.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_10));
			viewHolder.mView.setVisibility(View.VISIBLE);
			SpannableString spanString = new SpannableString(str);
			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, str.length() - 1,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.mText.setText(spanString);
		} else {
			viewHolder.mText.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_9));
			viewHolder.mView.setVisibility(View.GONE);
			SpannableString spanString = new SpannableString(str);
			spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, str.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.mText.setText(spanString);
		}
	}

	private static class ViewHolder {
		public TextView mText;;
		public View mView;
	}
}
