package com.zhima.plugin.space;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zhima.R;
import com.zhima.plugin.BaseViewPlugin;
import com.zhima.plugin.LayoutBaseViewPlugin;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;

/**
 * @ClassName: ElevatorJokeLayoutViewPlugin
 * @Description: 公共空间
 * @author luqilong
 * @date 2013-1-7 下午5:54:40
 */
public class ElevatorJokeLayoutViewPlugin extends LayoutBaseViewPlugin {

	private PullToRefreshListView mPullListView;
	private ListView mListView;

	public ElevatorJokeLayoutViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_elevator_joke_layout, null);
		mPullListView = (PullToRefreshListView) mPluginView.findViewById(R.id.pull_list_elevator);
		mListView = mPullListView.getRefreshableView();
	}

	@Override
	public ViewGroup getLayout() {
		return (ViewGroup) mPluginView;
	}

	@Override
	public void layoutViewPlugins(List<BaseViewPlugin> viewPlugins) {
		View view = View.inflate(mContext, R.layout.plugin_elevator_joke, null);
		ViewGroup viewGrop = (ViewGroup) view.findViewById(R.id.layout_elevator_joke);
		
		for (BaseViewPlugin viewPlugin : viewPlugins) {
			viewGrop.addView(viewPlugin.getPluginView());
		}

		//TODO 动态添加textView
		mListView.addHeaderView(view);
		mListView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
	}

	public void setAdapter(ListAdapter adapter) {
		mListView.setAdapter(adapter);
	}

	public void setOnItemClickLisntener(OnItemClickListener listener) {
		mListView.setOnItemClickListener(listener);
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		mPullListView.setOnRefreshListener(listener);
	}

	public void setTitle(String title) {
		//TODO 动态添加textView
	}

	public void setEmptyView() {
		mPullListView.setEmptyView();
	}

	public void setLastPage(boolean isLastPage) {
		mPullListView.setLastPage(isLastPage);
	}

	public boolean hasPullFromTop() {
		return mPullListView.hasPullFromTop();
		//		return false;
	}

	public void onRefreshComplete(boolean hasPullFromTop) {
		mPullListView.onRefreshComplete(hasPullFromTop);
	}

	@Override
	public void setStyle(TypedArray typedArray) {
		// TODO Auto-generated method stub

	}

}
