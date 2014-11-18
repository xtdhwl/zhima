package com.zhima.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ZhimaAdapter<T> extends BaseAdapter {
	private static final String TAG = "ZhimaAdapter";
	protected LayoutInflater mInflater;
	protected int mLayoutId;
	protected List<T> mArray;
	protected Context mContext;
	protected View.OnClickListener mClickListener = null;
	
	/** 是否存在第一个条目不相同布局  */
	private boolean isHaveTopView = false;


	public ZhimaAdapter(Context context, int layoutId, List<T> array) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mLayoutId = layoutId;
//		isHaveTopView = false;
		mArray = array;
	}
	
//	/**
//	 * 当列表需要显示第一个条目和其它条目布局不相同的情况时用此构造
//	 * @param context 上下文
//	 * @param layoutId 条目布局
//	 * @param topItemView 第一个条目布局 
//	 * @param array 布局要显示的数据
//	 */
//	public ZhimaAdapter(Context context, int layoutId,View topItemView, List<T> array) {
//		mContext = context;
//		mInflater = LayoutInflater.from(context);
//		mLayoutId = layoutId;
//		mTopItemView = topItemView; 
//		isHaveTopView = true;
//		mArray = array;
//	}
	
	public boolean isHaveTopView() {
		return isHaveTopView;
	}
	
	public void setHaveTopView(boolean isHaveTopView) {
		this.isHaveTopView = isHaveTopView;
	}
	
	@Override
	public int getCount() {
		if(isHaveTopView){
			return mArray.size()+1;
		}
		return mArray.size();
	}

	@Override
	public T getItem(int position) {
		if (position >= mArray.size()) {
			position = mArray.size() - 1;
		}
		return mArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		if(position == 0 && isHaveTopView){
//			return mTopItemView;
//		}
		
		View view = convertView;
		if (view == null) {
			view = createView(position);
		}
		if(isHaveTopView){
			bindView(getItem(position-1), position, view);
		}else{
			bindView(getItem(position), position, view);
		}
		return view;
	}
	
//	public void isHaveTopView(boolean isHaveTopView){
//		this.isHaveTopView = isHaveTopView;
//	}
//	
	@Override
	public boolean isEnabled(int position) {
			if(position >= mArray.size()){
				return false;
			}
		return super.isEnabled(position);
	}

	public abstract Object createViewHolder(View view, T data);
	public abstract void bindView(T data, int position, View view);
	public void getFirstData(){
		
	}
	public void getData() {
		
	}
	
	public int getLayoutId() {
		return mLayoutId;
	}
	
	public Object getViewHolder(View view, T data) {
		Object holder = (Object) view.getTag();
		if (holder == null) {
			holder = createViewHolder(view, data);
			view.setTag(holder);
		}
		return holder;
	}

	/**
	 * @Title: updateData
	 * @Description: 更新数据
	 * @param aArray
	 * @param refreshed
	 *            是否要刷新view界面
	 * @return void
	 */
	public void updateData(List<T> aArray, boolean refreshed) {
		mArray = aArray;
		if (refreshed) {
			notifyDataSetChanged();
		}
	}
	protected View createView(int position) {
		return mInflater.inflate(mLayoutId, null);
	}

	/**
	 * @Title: bindClicker
	 * @Description: 将view的onclick绑定到单击事件处理器
	 * @param data
	 *            View的getTag()函数将返回此数据
	 * @param v
	 * @return void
	 */
	protected void bindClicker(T data, View v) {
		v.setTag(data);
		v.setOnClickListener(mClickListener);
	}

	/**
	 * @Title: setOnClickerListener
	 * @Description: 单击事件处理器
	 * @param listener
	 * @return void
	 */
	public void setOnClickerListener(View.OnClickListener listener) {
		mClickListener = listener;
	}
}
