package com.zhima.plugin.space.reputationseal.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.ZMObject;
import com.zhima.ui.adapter.ZhimaAdapter;

public class ReputationSpaceAdapter extends ZhimaAdapter<ZMObject> {

	private int selectItem;

	public ReputationSpaceAdapter(Context context, int layoutId, List<ZMObject> array) {
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
		viewHolder.mText.setText(str);
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
