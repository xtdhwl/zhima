package com.zhima.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.logger.Logger;
import com.zhima.ui.tools.FaceHelper.FaceHolder;

/**
 * @ClassName:FaceAdapter
 * @Description:表情Adapter.这里指适合适配表情数据.
 * @author liqilong
 * @date 2012-7-25 下午3:10:20
 * 
 */
public class FaceAdapter extends BaseAdapter {
	private static final String TAG = "FaceAdapter";
	/** 表情行数 */
	private final static int ROW_COUNT = 6;
	/** 表情列数 */
	private final static int COLUMN_COUNT = 4;
	/** 分页个数 */
	private int mPageSize = 0;
	/** 分页索引 */
	private int mPageIndex = 0;
	Context mContext;
	/** 适配数据 */
	ArrayList<FaceHolder> mFaceList;
	private OnClickListener mOnClickListener;

	public FaceAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public FaceAdapter(Context mContext, ArrayList<FaceHolder> mFaceLists) {
		super();
		this.mContext = mContext;
		this.mFaceList = mFaceLists;
		mPageSize = computePage();
	}

	public void setFaceHolder(ArrayList<FaceHolder> faceList) {
		mFaceList = faceList;
		mPageSize = computePage();
	}

	/**
	 * @Title: computePage
	 * @Description: 计算分页个数
	 * @return int
	 */
	private int computePage() {
		// 得到需要几个View
		int faceCount = mFaceList.size();
		int page = faceCount / (ROW_COUNT * COLUMN_COUNT);
		int m = faceCount % (ROW_COUNT * COLUMN_COUNT);
		page = m == 0 ? page : page + 1;
		Logger.getInstance(TAG).debug("face page:" + page);
		return page;
	}

	@Override
	public int getCount() {
		return mPageSize;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridView mGridView = getGridView(position);
		mPageIndex++;
		return mGridView;
	}

	/**
	 * @Title: setOnClickListener
	 * @Description: ImageView单击事件
	 * @param l
	 *            void
	 */
	public void setOnClickListener(OnClickListener l) {
		mOnClickListener = l;
	}

	private GridView getGridView(int page) {
		GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.face_item, null);
		gridView.setNumColumns(ROW_COUNT);
		int start = page * ROW_COUNT * COLUMN_COUNT;
		int count = Math.min(mFaceList.size() - start, ROW_COUNT * COLUMN_COUNT);
		// 根据分页得到集合数据
		ArrayList<FaceHolder> list = new ArrayList<FaceHolder>();
		for (int i = 0; i < count; i++) {
			list.add(mFaceList.get(i + start));
		}
		MyGridViewAdapter gridAdapter = new MyGridViewAdapter(mContext, R.layout.face_image_item, list);
		gridView.setAdapter(gridAdapter);

		return gridView;
	}

	private class MyGridViewAdapter extends ZhimaAdapter<FaceHolder> {

		public MyGridViewAdapter(Context context, int layoutId, List<FaceHolder> array) {
			super(context, layoutId, array);
		}

		// 如果获取网络表情,待优化
		@Override
		public Object createViewHolder(View view, FaceHolder data) {
			return null;
		}

		@Override
		public void bindView(FaceHolder data, int position, View view) {
			ImageView iv = (ImageView) view.findViewById(R.id.img_photo);
			iv.setImageBitmap(data.getBitmap());
			iv.setTag(data);
			if (mOnClickListener != null) {
				iv.setOnClickListener(mOnClickListener);
			}
		}
	}
}
