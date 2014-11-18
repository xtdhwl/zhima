package com.zhima.plugin.space;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;

/**
 * @ClassName: IdolAcqierementViewPlugin
 * @Description: 知天使频道
 * @author luqilong
 * @date 2013-1-7 上午10:59:28
 * 
 */
public class IdolAcqierementViewPlugin extends BaseViewPlugin {

	private ListView mListView;
	private TextView mTitleView;
	private ImageView mArrowView;

	public IdolAcqierementViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_idol_acqierement, null);
		mListView = (ListView) mPluginView.findViewById(R.id.lstv_acqierement);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_title);
		mArrowView = (ImageView) mPluginView.findViewById(R.id.img_arrow);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListView.setOnItemClickListener(listener);
	}

	public void setArrowClick(OnClickListener listener) {
		mArrowView.setOnClickListener(listener);
	}

	public void setAdapter(ListAdapter adapter) {
		mListView.setAdapter(adapter);
		setListViewHeightBasedOnChildren(mListView);
	}

	/**
	 * 处理scollview中 嵌套listview的问题
	 */
	private static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}
}
