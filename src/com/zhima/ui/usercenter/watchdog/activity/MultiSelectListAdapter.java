package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.ui.adapter.ZhimaAdapter;

/**
 * @ClassName MultiSelectListAdapter
 * @Description 多选listview的adapter
 * @author jiang
 * @date 2013-1-3 上午11:11:27
 */
public class MultiSelectListAdapter extends ZhimaAdapter<String> {

	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;

	// 构造器
	public MultiSelectListAdapter(Context context, int layoutId, ArrayList<String> list) {
		super(context, layoutId, list);
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate(list);

	}

	// 初始化isSelected的数据
	private void initDate(ArrayList<String> list) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				getIsSelected().put(i, false);
			}
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		MultiSelectListAdapter.isSelected = isSelected;
	}

	@Override
	public Object createViewHolder(View view, String data) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		holder.layout = (LinearLayout) view.findViewById(R.id.layout_list_item);
		holder.tv = (TextView) view.findViewById(R.id.item_tv);
		holder.cb = (CheckBox) view.findViewById(R.id.item_cb);
		// 为view设置标签
		view.setTag(holder);

		return holder;
	}

	@Override
	public void bindView(String data, int position, View view) {
		// TODO Auto-generated method stub
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		if (getIsSelected().get(position)) {
			holder.layout.setBackgroundResource(R.drawable.list_selector_background_pressed);
		} else {
			holder.layout.setBackgroundColor(Color.TRANSPARENT);
		}
		// 设置list中TextView的显示
		holder.tv.setText(mArray.get(position));
		// 根据isSelected来设置checkbox的选中状况
		holder.cb.setChecked(getIsSelected().get(position));
	}

	public static class ViewHolder {
		TextView tv;
		CheckBox cb;
		LinearLayout layout;
	}

}
