package com.zhima.ui.space.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.zhima.R;
import com.zhima.base.cache.WeakReferenceCache;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.HttpImageLoader.ImageDownloadListener;

public class IdolAlbumAdatper extends BaseAdapter {
	private static final String TAG = "IdolAlbumAdatper";
	private WeakReferenceCache mImageCache;
	private List<ZMObjectImage> mArray;
	private Context mContext;

	private int mDefault = R.drawable.default_image;
	private int mPosition;
	private HttpImageLoader mHttpImageLoader;
	private Drawable mDefultDrawable;
	private Animation mInAnimation;
	private int mOdlPosition;
	//true向前 false向后
	private boolean mIsForward;

	public IdolAlbumAdatper(Context context, List<ZMObjectImage> mArray) {
		super();
		this.mArray = mArray;
		mContext = context;
		mImageCache = new WeakReferenceCache();
		mPosition = mArray.size() + Integer.MAX_VALUE / 2;
		mHttpImageLoader = HttpImageLoader.getInstance(context);
		mInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.album_in);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return mArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv;
		if (convertView != null) {
			iv = (ImageView) convertView;
		} else {
			iv = new ImageView(mContext);
			iv.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
					Gallery.LayoutParams.FILL_PARENT));
			iv.setScaleType(ScaleType.FIT_XY);
		}

		Bitmap bitmap = null;

		int size = mArray.size();
		if (size > 0) {
			//判断方向
			mIsForward = mOdlPosition > position ? true : false;
			mOdlPosition = position;
			bitmap = getBitmap(iv);
			if (bitmap != null) {
				iv.setImageBitmap(bitmap);
			} else {
				iv.setImageDrawable(mDefultDrawable);
			}
		} else {
			iv.setImageDrawable(mDefultDrawable);
		}
		return iv;

	}

	//设置默认背景图片
	public void setDefaultResource(int data) {
		mDefault = data;
		mDefultDrawable = mContext.getResources().getDrawable(mDefault);
	}

	private/* synchronized */Bitmap getBitmap(ImageView view) {
		int size = mArray.size();
		Bitmap bitmap = null;
		for (int i = 0; i < size; i++) {
			int index = mPosition % size;
			String url = mArray.get(index).getImageUrl();
			bitmap = mImageCache.get(url);
			if (mIsForward) {
				++mPosition;
			} else {
				--mPosition;
			}
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			} else {
				mHttpImageLoader.downloadImage(url, ImageScaleType.LARGE, new MyImageDownloadListener(url, view));
			}
		}
		return bitmap;
	}

	private class MyImageDownloadListener implements ImageDownloadListener {
		String url;
		ImageView iv;

		public MyImageDownloadListener(String url, ImageView iv) {
			this.url = url;
			this.iv = iv;
		}

		@Override
		public void onReady(String uri, String localFilePath) {
			Bitmap bitmap = GraphicUtils.getImageThumbnailByPixel(mContext, localFilePath, iv.getMeasuredWidth(),
					iv.getMeasuredHeight());
			if (bitmap != null) {
				mImageCache.add(url, bitmap);
				Drawable draw = iv.getDrawable();
				if (draw == mDefultDrawable) {
					iv.setImageBitmap(bitmap);
					iv.startAnimation(mInAnimation);
				}
			}
		}

		@Override
		public void onError(String msg, String uri) {
		}

	}

	public void setData(ArrayList<ZMObjectImage> arrayList) {
		mArray = arrayList;
	}

	public void recycle() {
		mImageCache.clear();
	}

}
