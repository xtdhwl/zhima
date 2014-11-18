package com.zhima.ui.diary.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.R.color;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.logger.Logger;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.SpaceGallery;
import com.zhima.ui.tools.LocalImageLoader;

/**
 * @ClassName: LocalAlbumActivity
 * @Description: 本地相册
 * @author luqilong
 * @date 2013-1-14 下午2:41:37
 */
public class LocalAlbumActivity extends BaseActivity {

	protected static final String TAG = LocalAlbumActivity.class.getSimpleName();

	private GridView mGridView;
	private SpaceGallery mPreviewView;

	private MyPhotoAdapter mPhotAdapter;
	private SelectAdapter mSelectAdapter;

	private static ArrayList<String> mSelectPaths;
	private ArrayList<String> mPhotoPaths;

	private static Drawable mSelectDrawable = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.diary_album_activity);

		findView();

		mPhotoPaths = new ArrayList<String>();
		mSelectPaths = new ArrayList<String>();
//		mSelectDrawable = new ColorDrawable(R.color.blue);
		mSelectDrawable = new ColorDrawable(color.blue);
		//1:得到所有图片的路径
		//2:使用LocalImageLoader加载图片
		//3:使用mViewList记录选择的图片
		//3:如果是选择的图片设置背景为蓝色
		mPhotoPaths = MediaStoreHelper.getAllImage(getContentResolver());
		mPhotAdapter = new MyPhotoAdapter(this, R.layout.space_album_item, mPhotoPaths);
		mSelectAdapter = new SelectAdapter(this, R.layout.diary_album_select_item, mSelectPaths);

		mPreviewView.setAdapter(mSelectAdapter);

		mGridView.setAdapter(mPhotAdapter);
		mGridView.setOnItemClickListener(photoClickListener);
		mGridView.setOnItemSelectedListener(photoSelectedListener);
	}

	/**
	 * 点击选择
	 */
	private OnItemClickListener photoClickListener = new OnItemClickListener() {
		//当点击图片需要先判断是否已经选择
		//如果-->已经选择  背景还原 mgroup移除view
		//  1:mViewList移除
		//  2:刷新选择的view
		//  3:把背景恢复
		//如果-->没有选择
		//  1:添加到list
		//  2:刷新选择的view
		//  3:把背景还原

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String str = mPhotAdapter.getItem(position);
			if (mSelectPaths.contains(str)) {
				//已经选择
				view.setBackgroundDrawable(null);
				refreshSelectView(msg_remove, view, str);
			} else {
				//没有选择
//				view.setBackgroundDrawable(mSelectDrawable);
				view.setBackgroundColor(getResources().getColor(R.color.topbar_button_select_bg));
				refreshSelectView(msg_add, view, str);
			}
		}

	};

	/**
	 * 选择
	 */
	private OnItemSelectedListener photoSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Logger.getInstance(TAG).debug("position:" + position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	};

	//
	private static final int msg_refresh = 0;
	private static final int msg_remove = 1;
	private static final int msg_add = 2;

	private void refreshSelectView(int msg, View view, String path) {
		// TODO Auto-generated method stub
		switch (msg) {
		case msg_refresh:

			break;
		case msg_remove:
			mSelectPaths.remove(path);
			mSelectAdapter.notifyDataSetChanged();
			break;
		case msg_add:
			mSelectPaths.add(path);
			mSelectAdapter.notifyDataSetChanged();
			break;
		default:
			throw new IllegalArgumentException("参数非法");
		}

	}

	private void findView() {
		mGridView = (GridView) findViewById(R.id.grid_album);
		mPreviewView = (SpaceGallery) findViewById(R.id.spgaly_preview);
	}

	private static class MyPhotoAdapter extends ZhimaAdapter<String> {

		private LocalImageLoader mImageLoader;
		private BaseActivity mActivity;

		public MyPhotoAdapter(BaseActivity activity, int layoutId, List<String> array) {
			super(activity, layoutId, array);
			// TODO Auto-generated constructor stub
			mImageLoader = LocalImageLoader.getInstance(activity);
			mActivity = activity;
		}

		@Override
		public Object createViewHolder(View view, String data) {
			ViewHolder viewHolder = new ViewHolder();
			ImageView photoImage = (ImageView) view.findViewById(R.id.img_photo);
			viewHolder.mView = view;
			viewHolder.mPhotoImage = photoImage;
			//保存背景颜色
			return viewHolder;
		}

		@Override
		public void bindView(String data, int position, View view) {
			ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
			if (mSelectPaths.contains(data)) {
				view.setBackgroundDrawable(mSelectDrawable);
				view.setBackgroundColor(mActivity.getResources().getColor(R.color.topbar_button_select_bg));
			} else {
				view.setBackgroundDrawable(null);
			}

			mImageLoader.loadImage(data, viewHolder.mPhotoImage, mActivity.getActivityId(), R.drawable.default_image,
					ImageScaleType.MEDIUM);
		}

		private static class ViewHolder {
			public View mView;
			public ImageView mPhotoImage;

		}

		public void setData(List<String> mPhotoList) {
			mArray = mPhotoList;
		}

	}

	private class SelectAdapter extends ZhimaAdapter<String> {
		private BaseActivity activity;
		private LocalImageLoader mImageLoader;

		public SelectAdapter(BaseActivity activity, int layoutId, List<String> array) {
			super(activity, layoutId, array);
			this.activity = activity;
			mImageLoader = LocalImageLoader.getInstance(activity);
		}

		@Override
		public Object createViewHolder(View view, String data) {
			return null;
		}

		@Override
		public void bindView(String data, int position, View view) {
//			ViewHolder viewHolder = (ViewHolder) getViewHolder(view, data);
			ImageView iv = (ImageView) view.findViewById(R.id.img_select_photo);

			mImageLoader.loadImage(data, iv, activity.getActivityId(), R.drawable.default_image,"");
		}

	}

}
