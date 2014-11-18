package com.zhima.plugin.space.reputationseal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
import com.zhima.ui.common.view.ThreeList;

/**
 * @ClassName: ReputationAcqierementViewPlugin
 * @Description: 誉玺日志
 * @author yusonglin
 * @date 2013-1-30 下午2:37:55
 */
public class ReputationAcqierementViewPlugin extends BaseViewPlugin {

//	private ListView mListView;
	private ThreeList mThreeList;
	private TextView mTitleView;
	private ImageView mArrowView;
	private ViewGroup mTitleLayoutView;

	public ReputationAcqierementViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_couples_acqierement, null);
//		mListView = (ListView) mPluginView.findViewById(R.id.lstv_couples_acqierement);
		mThreeList = (ThreeList) mPluginView.findViewById(R.id.three_list);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_couples_acqierement_title);
		mArrowView = (ImageView) mPluginView.findViewById(R.id.img_couples_acqierement_arrow);
		mTitleLayoutView = (ViewGroup) mPluginView.findViewById(R.id.layout_title);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mThreeList.setOnItemClickListener(listener);
	}

	public void setArrowClick(OnClickListener listener) {
		mArrowView.setOnClickListener(listener);
	}

	public void setAdapter(ListAdapter adapter) {
		mThreeList.setAdapter(adapter);
	}

//	/**
//	 * 处理scollview中 嵌套listview的问题
//	 */
//	private static void setListViewHeightBasedOnChildren(ListView listView) {
//		ListAdapter listAdapter = listView.getAdapter();
//		if (listAdapter == null) {
//			return;
//		}
//		int totalHeight = 0;
//		for (int i = 0; i < listAdapter.getCount(); i++) {
//			View listItem = listAdapter.getView(i, null, listView);
//			listItem.measure(0, 0);
//			totalHeight += listItem.getMeasuredHeight();
//		}
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//		listView.setLayoutParams(params);
//	}

	@Override
	public void setStyle(TypedArray typedArray) {
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginMoreBackground)) {
			Drawable d = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginMoreBackground);
			mArrowView.setImageDrawable(d);
		}
		if (typedArray.hasValue(R.styleable.ViewPluginStyle_viewPluginTitleBackground)) {
			Drawable d = typedArray.getDrawable(R.styleable.ViewPluginStyle_viewPluginTitleBackground);
			mTitleLayoutView.setBackgroundDrawable(d);
		}
	}
}
