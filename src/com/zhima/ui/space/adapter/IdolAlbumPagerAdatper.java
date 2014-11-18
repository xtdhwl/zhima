package com.zhima.ui.space.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.zhima.R;
import com.zhima.base.cache.WeakReferenceCache;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.HttpImageLoader.ImageDownloadListener;

/**
* @ClassName: AlbumPagerAdatper
* @Description: 相册PagerAdatper
* @author luqilong
* @date 2012-12-13 上午11:39:33
 */
public class IdolAlbumPagerAdatper extends PagerAdapter {
	private static final String TAG = IdolAlbumPagerAdatper.class.getSimpleName();

	private int mDefault = R.drawable.default_image;
	private Drawable mDefultDrawable;
	private Animation mInAnimation;
	private int mOdlPosition;
	//true向前 false向后
	private boolean mIsForward;

	private WeakReferenceCache mImageCache;
	private List<ZMObjectImage> mArray;
	private Context mContext;
	private int mPosition;
	private HttpImageLoader mHttpImageLoader;

	public IdolAlbumPagerAdatper(Context context, List<ZMObjectImage> mArray) {
		super();
		this.mArray = mArray;
		mContext = context;
		mImageCache = new WeakReferenceCache();
		mPosition = mArray.size() + Integer.MAX_VALUE / 2;
		mHttpImageLoader = HttpImageLoader.getInstance(context);
		mInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.album_in);
		mDefultDrawable = mContext.getResources().getDrawable(mDefault);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	//是否使用对象生成视图
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public void startUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		super.startUpdate(container);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView iv = new ImageView(mContext);
		iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

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

		((ViewPager) container).addView(iv);
		return iv;
	}


	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
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

}
