package com.zhima.ui.usercenter.data.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.cache.SoftReferenceCache;
import com.zhima.base.config.ResourceServerConfig;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.data.model.UserAlbumImage;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.HttpImageLoader.ImageDownloadListener;

public class MyAlbumPreviewAdapter extends BaseAdapter {
	protected static final String TAG = "PhotoAdapter";
	private Context mContext;
	private List<UserAlbumImage> mArray;
	private SoftReferenceCache mImageCache;
	private Bitmap mDefaBitmap;
	private String mImageScaleType;

	public MyAlbumPreviewAdapter(Context context, List<UserAlbumImage> array, String imageScaleType) {
		mContext = context;
		mArray = array;
		mImageCache = new SoftReferenceCache();
		mImageScaleType = imageScaleType;
		mDefaBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_1_image);
	}

	@Override
	public int getCount() {
		return mArray.size();
	}

	@Override
	public UserAlbumImage getItem(int position) {
		return mArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		UserAlbumImage data = mArray.get(position);
		String url = data.getImageUrl();
		View view = null;
		if (convertView == null) {
			view = View.inflate(mContext, R.layout.photo_activity_item, null);
		} else {
			view = convertView;
		}
		view.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
				Gallery.LayoutParams.FILL_PARENT));
		//注意.修改这个位置也需要修改PhotoActivity保存路径位置.
		String realUrl = ResourceServerConfig.getInstance().getRealImageUrl(url, mImageScaleType);

		Bitmap bitmap = mImageCache.get(realUrl);
		if (bitmap != null) {
			view.findViewById(R.id.load).setVisibility(View.GONE);
			ImageView img = (ImageView) view.findViewById(R.id.img_photo);
			img.setVisibility(View.VISIBLE);
			img.setImageBitmap(bitmap);
		} else {
			HttpImageLoader.getInstance(mContext)
					.downloadImage(url, mImageScaleType, new MyImageDownloadListener(view));
		}
		return view;
	}

	private class MyImageDownloadListener implements ImageDownloadListener {
		private View view;

		public MyImageDownloadListener(View view) {
			this.view = view;
		}

		@Override
		public void onReady(String uri, String localFilePath) {
			view.findViewById(R.id.load).setVisibility(View.GONE);

			ImageView img = (ImageView) view.findViewById(R.id.img_photo);
			Bitmap bitmap = GraphicUtils.getScreenBitmap(mContext, localFilePath);
			if (bitmap != null) {
				mImageCache.add(uri, bitmap);
				img.setVisibility(View.VISIBLE);
				img.setTag(localFilePath);
				img.setImageBitmap(bitmap);
			} else {
				mImageCache.add(uri, mDefaBitmap);
				img.setVisibility(View.VISIBLE);
				img.setImageBitmap(mDefaBitmap);
			}

		}

		@Override
		public void onError(String msg, String uri) {
			view.findViewById(R.id.load).setVisibility(View.GONE);
			mImageCache.add(uri, mDefaBitmap);
			ImageView img = (ImageView) view.findViewById(R.id.img_photo);
			img.setVisibility(View.VISIBLE);
			img.setImageBitmap(mDefaBitmap);
		}

	}

	public void recycle() {
		mImageCache.clear();
	}
}
