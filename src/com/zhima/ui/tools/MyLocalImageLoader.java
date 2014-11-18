package com.zhima.ui.tools;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

import com.zhima.base.cache.SoftReferenceCache;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;

public class MyLocalImageLoader implements Callback {
	private static final String TAG = LocalImageLoader.class.getSimpleName();
	private static final String LOADER_THREAD_NAME = LocalImageLoader.class.getSimpleName();

	// 保存bitmap
	private final SoftReferenceCache mImageCache = new SoftReferenceCache();
	// 保存任务中的ImageHolder
	private final ConcurrentHashMap<ImageView, ImageHolder> mImageHolderCache = new ConcurrentHashMap<ImageView, MyLocalImageLoader.ImageHolder>();
	// 请求列表
	private final ConcurrentHashMap<ImageView, String> mPendingRequests = new ConcurrentHashMap<ImageView, String>();

	private static MyLocalImageLoader instance;
	private final static int MAX_WIDTH = 400;
	private final static int MAX_HEIGHT = 400;

	private LoaderThread mLoaderThread = null;
	private boolean mLoadingRequested;
	private boolean mPaused;
	private static Context mContext;
	private Handler mMainHandler = new Handler(this);
	private static final int MSG_REQUEST_LOADING = 1;
	private static final int MSG_IMGER_LOADER = 2;
	private static final int MSG_LOAD_FAIL = 3;

	private MyLocalImageLoader() {
		mPaused = false;
		mLoadingRequested = false;
	};

	public static MyLocalImageLoader getInstance(Context context) {
		if (instance == null) {
			instance = new MyLocalImageLoader();
		}
		mContext = context;
		return instance;
	}

	public boolean loadImage(ImageView view, String path, int width, int height, int defaultId) {
		Bitmap bitmap = mImageCache.get(path);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
			return true;
		} else {
			view.setImageResource(defaultId);
		}

		boolean loader = loadCachedImage(view, path);
		if (loader) {
			mPendingRequests.remove(view);
		} else {
			mPendingRequests.put(view, path);
			if (!mPaused) {
				requestLoading();
			}
		}
		return loader;
	}

	private boolean loadCachedImage(ImageView view, String path) {

		ImageHolder holder = mImageHolderCache.get(view);
		if (holder != null) {
			if (holder.state != ImageHolder.NEEDED) {
				Bitmap bitmap = mImageCache.get(path);
				if (bitmap != null) {
					view.setImageBitmap(bitmap);
					return true;
				}
			}
		} else {
			holder = new ImageHolder(view, path);
			holder.state = ImageHolder.NEEDED;
			mImageHolderCache.put(view, holder);
		}
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_REQUEST_LOADING: {
			mLoadingRequested = false;
			if (mLoaderThread == null) {
				mLoaderThread = new LoaderThread();
				mLoaderThread.start();
				Logger.getInstance(LOADER_THREAD_NAME).debug("mLoaderThread start ......");
			}
			mLoaderThread.requestLoading();
			return true;
		}
		case MSG_IMGER_LOADER:
			if (!mPaused) {
				processLoadedImage();
			}
			return true;
		case MSG_LOAD_FAIL:
			mPendingRequests.remove(msg.obj);
		}
		return true;
	}

	private void processLoadedImage() {
		Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
		while (iterator.hasNext()) {
			ImageView view = iterator.next();
			boolean loader = loadCachedImage(view, mPendingRequests.get(view));
			if (loader) {
				iterator.remove();
			}
		}
		if (!mPendingRequests.isEmpty()) {
			requestLoading();
		}
	}

	public void cancelRequest(ImageView view) {
		mPendingRequests.remove(view);
	}

	public void stop() {
		pause();
		if (mLoaderThread != null) {
			mLoaderThread.quit();
			mLoaderThread = null;
		}
		clear();
	}

	public void clear() {
		mPendingRequests.clear();
		mImageCache.clear();
	}

	public void pause() {
		mPaused = true;
	}

	public void resume() {
		mPaused = false;
		if (!mPendingRequests.isEmpty()) {
			requestLoading();
		}
	}

	private void requestLoading() {
		if (!mLoadingRequested) {
			mLoadingRequested = true;
			mMainHandler.sendEmptyMessage(MSG_REQUEST_LOADING);
		}
	}

	private class ImageHolder {
		public static final int NEEDED = 0;
		public static final int LOADING = 1;
		public static final int LOADED = 2;
		public int state;
		private ImageView v;
		private String uri;

		ImageHolder(ImageView v, String uri) {
			this.v = v;
			this.uri = uri;
		}

		public ImageView getImageView() {
			return v;
		}

		public void setImageView(ImageView view) {
			v = view;
		}
	}

	private class LoaderThread extends HandlerThread implements Callback {

		private Handler mLocaderThreadHandler;

		public LoaderThread() {
			super(LOADER_THREAD_NAME);
		}

		public void requestLoading() {
			if (mLocaderThreadHandler == null) {
				mLocaderThreadHandler = new Handler(getLooper(), this);
				Logger.getInstance(LOADER_THREAD_NAME).debug("LoaderTread  LocaderThreadHandler ......");
			}
			mLocaderThreadHandler.sendEmptyMessage(0);
		}

		@Override
		public boolean handleMessage(Message msg) {
			Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
			while (iterator.hasNext()) {
				ImageView view = iterator.next();
				String file = mPendingRequests.get(view);
				ImageHolder holder = mImageHolderCache.get(view);
				if (holder == null) {
					holder = new ImageHolder(view, file);
				}
				holder.state = ImageHolder.LOADING;

				if (file != null) {
					Bitmap bitmap = mImageCache.get(file);
					if (bitmap == null) {
						Bitmap bm = GraphicUtils.getImageThumbnail(mContext, file, TARGET_SIZE_MICRO_THUMBNAIL,
								TARGET_SIZE_MICRO_THUMBNAIL);
						bitmap = createImageThumbnail(bm);
					}
					mImageCache.add(file, bitmap);
				} else {
					mMainHandler.obtainMessage(MSG_LOAD_FAIL, view).sendToTarget();
				}
			}
			mMainHandler.sendEmptyMessage(MSG_IMGER_LOADER);
			return true;
		}
	}

	// ----------------------
	// --------------------------------------------------------------------------------
	public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;
	public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;
	private static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 384;
	private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 128 * 128;
	private static final int UNCONSTRAINED = -1;
	public static final int OPTIONS_RECYCLE_INPUT = 0x2;
	private static final int OPTIONS_NONE = 0x0;
	private static final int OPTIONS_SCALE_UP = 0x1;

	public Bitmap createImageThumbnail(Bitmap bitmap) {
		Bitmap resultBitmap = extractThumbnail(bitmap, TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL,
				OPTIONS_RECYCLE_INPUT);
		return resultBitmap;
	}

	public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap thumbnail = transform(matrix, source, width, height, OPTIONS_SCALE_UP | options);
		return thumbnail;
	}

	private static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, int options) {
		boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
		boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()),
					deltaYHalf + Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
			c.drawBitmap(source, src, dst, null);
			if (recycle) {
				source.recycle();
			}
			c.setBitmap(null);
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		if (recycle && b1 != source) {
			source.recycle();
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

		if (b2 != b1) {
			if (recycle || b1 != source) {
				b1.recycle();
			}
		}

		return b2;
	}
	// ---------------------------------------------------------------------------------------
}
