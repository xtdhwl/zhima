package com.zhima.plugin.space.zmcard.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.ZMObject;
import com.zhima.ui.adapter.ZhimaAdapter;

public class ZMCardGalleryAdapter extends ZhimaAdapter<ZMObject> {

	private int defalutResId;

	public ZMCardGalleryAdapter(Context context, int layoutId, List<ZMObject> array, int defalutResId) {
		super(context, layoutId, array);
		this.defalutResId = defalutResId;
	}

	@Override
	public Object createViewHolder(View view, ZMObject data) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mText = (TextView) view.findViewById(R.id.text_space_name);
		viewHolder.viewLine = (View) view.findViewById(R.id.first_dotted_line);
		return viewHolder;
	}

	@Override
	public void bindView(ZMObject data, int position, View view) {
		ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
		viewHolder.mText.setText(data.getName());
		viewHolder.mText.setTextColor(mContext.getResources().getColor(defalutResId));
		if (position == 0) {
			viewHolder.viewLine.setVisibility(View.VISIBLE);
		} else {
			viewHolder.viewLine.setVisibility(View.INVISIBLE);
		}
	}

	private static class ViewHolder {
		public TextView mText;;
		public View viewLine;
	}

	public void setData(ArrayList<ZMObject> mDataList) {
		mArray = mDataList;
	}

}
