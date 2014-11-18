package com.zhima.ui.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.zhima.app.ZhimaApplication;
import com.zhima.base.cache.ImageCache;
import com.zhima.base.cache.ResImageCache;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;
import com.zhima.base.task.ITask;
import com.zhima.base.task.TaskManager;

public class LocalImageLoader {
	private final static String TAG = "LocalImageLoader";
	private final static int POOLSIZE = SystemConfig.MAX_TASK_SIZE;
	//	private final static int MAX_WIDTH = 480;
	//	private final static int MAX_HEIGHT = 640;
	private static LocalImageLoader mHttpImageLoader = null;
	private static final int FETCH_IMAGE_MSG = 1;
	private static final int DOWNLOAD_IMAGE_MSG = FETCH_IMAGE_MSG + 1;
	private static final int ERROR_MSG = DOWNLOAD_IMAGE_MSG + 1;
	private static ExecutorService sImageFetchThreadPool;
	private static TaskManager mTaskManager = new TaskManager(5);

	private volatile boolean mStop = false;
	private ImageLoadHandler mHandler;
	private TreeMap<Long, ArrayList<ImageHolder>> mViewHolders = new TreeMap<Long, ArrayList<ImageHolder>>();
	private ArrayList<ImageHolder> mMissedHolders = new ArrayList<ImageHolder>();
//	private HashSet<String> mDownload = new HashSet<String>();
	private ImageCache mImageCache = null;

	public long mCurrentBelongId = 0;
	public long mLastBelongId = 0;
	public LocalImageListener mListener = null;
//	private FileCache mFileCache = FileCache.getInstance();
	private Context mContext = null;

	public LocalImageLoader() {
		mHandler = new ImageLoadHandler(Looper.getMainLooper());
		mImageCache = ResImageCache.getInstance();
		mContext = ZhimaApplication.getContext();
	}

	public static LocalImageLoader getInstance(Context c) {
		if (mHttpImageLoader == null) {
			mHttpImageLoader = new LocalImageLoader();
		}
		//		mHttpImageLoader.mContext = c;
		return mHttpImageLoader;
	}

	public interface LocalImageListener {
		public abstract void localImageReady(View v, String url, Bitmap bmp);
	}

	/**
	 * @ClassName: ImageDownloadListener
	 * @Description: 图片下载监听
	 * @author liubingsr
	 * @date 2012-9-23 上午10:59:03
	 * 
	 */
//	public interface ImageDownloadListener {
//		/**
//		 * @Title: onReady
//		 * @Description: 下载完成
//		 * @param uri
//		 *            图片真实url地址
//		 * @param localFilePath
//		 *            本地图片文件地址
//		 * @return void
//		 */
//		public abstract void onReady(String uri, String localFilePath);
//
//		/**
//		 * @Title: onError
//		 * @Description: 下载失败
//		 * @param msg
//		 *            错误信息
//		 * @param uri
//		 *            图片真实url地址
//		 * @return void
//		 */
//		public abstract void onError(String msg, String uri);
//	}

	private Handler mCallbackHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case FETCH_IMAGE_MSG:
				callbackStruct s = (callbackStruct) message.obj;
				if (s.mListener != null) {
					s.mListener.localImageReady(s.mView, s.mUrl, s.mBmp);
				} else if (mListener != null) {
					mListener.localImageReady(s.mView, s.mUrl, s.mBmp);
				}
				break;
//			case DOWNLOAD_IMAGE_MSG:
//				downloadCallbackStruct downloadCallback = (downloadCallbackStruct) message.obj;
//				if (downloadCallback.mListener != null) {
//					downloadCallback.mListener.onReady(downloadCallback.mUrl, downloadCallback.mLocalFilePath);
//				}
//				break;
//			case ERROR_MSG:
//				if (message.obj instanceof downloadCallbackStruct) {
//					downloadCallbackStruct callback = (downloadCallbackStruct) message.obj;
//					if (callback.mListener != null) {
//						callback.mListener.onError(callback.mError, callback.mUrl);
//					}
//				}
//				break;
			}
		}
	};

//	private static class downloadCallbackStruct {
//		public String mUrl;
//		public String mLocalFilePath;
//		public ImageDownloadListener mListener = null;
//		public String mError = null;
//
//		public downloadCallbackStruct(String url, String localFilePath) {
//			this(url, localFilePath, null);
//		}
//
//		public downloadCallbackStruct(String url, String localFilePath, ImageDownloadListener listener) {
//			mUrl = url;
//			mLocalFilePath = localFilePath;
//			mListener = listener;
//		}
//
//		public void setError(String error) {
//			mError = error;
//		}
//	}

	private static class callbackStruct {
		public View mView;
		public String mUrl;
		public Bitmap mBmp;
		public LocalImageListener mListener = null;

		public callbackStruct(View view, String url, Bitmap bmp) {
			this(view, url, bmp, null);
		}

		public callbackStruct(View view, String url, Bitmap bmp, LocalImageListener listener) {
			mView = view;
			mUrl = url;
			mBmp = bmp;
			mListener = listener;
		}
	}

	private void sendFetchMessage(final View view, final String url, final Bitmap bmp) {
		sendFetchMessage(view, url, bmp, null);
	}

	private void sendFetchMessage(final View view, final String url, final Bitmap bmp, LocalImageListener listener) {
		Message message = new Message();
		message.what = FETCH_IMAGE_MSG;
		message.obj = new callbackStruct(view, url, bmp, listener);
		mCallbackHandler.sendMessage(message);
	}

//	private void sendDownloadMessage(final String url, final String localFilePath, ImageDownloadListener listener,
//			String error) {
//		downloadCallbackStruct obj = new downloadCallbackStruct(url, localFilePath, listener);
//		obj.setError(error);
//		Message message = new Message();
//		if (TextUtils.isEmpty(error)) {
//			message.what = DOWNLOAD_IMAGE_MSG;
//		} else {
//			message.what = ERROR_MSG;
//		}
//		message.obj = obj;
//		mCallbackHandler.sendMessage(message);
//	}

	public LocalImageListener getListener() {
		return mListener;
	}

	public void setListener(LocalImageListener listener) {
		this.mListener = listener;
	}

	public long getCurrentBelongId() {
		return mCurrentBelongId;
	}

	public void setCurrentBelongId(long mCurrentBelongId) {
		this.mCurrentBelongId = mCurrentBelongId;
	}

	public long getLastBelongId() {
		return mLastBelongId;
	}

	public void setLastBelongId(long mLastBelongId) {
		this.mLastBelongId = mLastBelongId;
	}

	/**
	 * @Title: getLocalImagePath
	 * @Description: 本地缓存的图片路径
	 * @param uri 服务器端返回的图片url地址
	 * @param maxWidth
	 * @return null 如果图片文件不存在
	 */
	/*
	 * public String getLocalImagePath(String uri, int maxWidth) { String
	 * realUrl = ResourceServerConfig.getInstance().getRealImageUrl(uri,
	 * maxWidth); return mFileCache.getCacheFile(realUrl); }
	 */
//	public String getLocalImagePath(String uri, String scaleType) {
//		String realUrl = ResourceServerConfig.getInstance().getRealImageUrl(uri, scaleType);
//		return mFileCache.getCacheFile(realUrl);
//	}

	/**
	 * @Title: getLocalImagePath
	 * @Description: 本地缓存的图片路径
	 * @param realUrl 真实的图片url地址
	 * @return null 如果图片文件不存在
	 */
//	public String getLocalImagePath(String realUrl) {
//		return mFileCache.getCacheFile(realUrl);
//	}

//	public void downloadImage(String uri, int maxWidth, ImageDownloadListener listener) {
//		String realUrl = ResourceServerConfig.getInstance().getRealImageUrl(uri, maxWidth);
//		doDownloadImage(realUrl, listener);
//	}
//
//	public void downloadImage(String uri, String scaleType, ImageDownloadListener listener) {
//		String realUrl = ResourceServerConfig.getInstance().getRealImageUrl(uri, scaleType);
//		doDownloadImage(realUrl, listener);
//	}
//
//	private void doDownloadImage(String realUrl, ImageDownloadListener listener) {
//		if (TextUtils.isEmpty(realUrl) || "null".equalsIgnoreCase(realUrl)) {
//			sendDownloadMessage(realUrl, null, listener, "image url is null");
//			return;
//		}
//		Logger.getInstance(TAG).debug("downloadImage:" + realUrl);
//		sendDownloadImageMessage(realUrl, listener);
//	}

	public Bitmap loadImage(String uri, ImageView view, long belongId, int defaultId) {
		return loadImage(uri, view, belongId, defaultId, null);
	}

	public Bitmap loadImage(String uri, ImageView view, long belongId, int defaultId, String scaleType) {
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp == null || lp.width == LayoutParams.WRAP_CONTENT || lp.height == LayoutParams.WRAP_CONTENT
				|| lp.width == LayoutParams.FILL_PARENT || lp.height == LayoutParams.FILL_PARENT) {
			throw new IllegalArgumentException("ImageView 未指定大小或getLayoutParams 为null");
		}
		return loadImage(uri, view, false, lp.width, lp.height, belongId, defaultId, false, scaleType);
	}

	public Bitmap loadImage(String uri, ImageView view, int maxWidth, int maxHeight, long belongId, int defaultId) {
		return loadImage(uri, view, maxWidth, maxHeight, belongId, defaultId, null);
	}

	public Bitmap loadImage(String uri, ImageView view, int maxWidth, int maxHeight, long belongId, int defaultId,
			LocalImageListener listener) {
		//		Bitmap bmp = loadImage(uri, view, false, maxWidth, maxHeight, belongId, defaultId, false, listener);
		//		if (bmp != null && view != null) {
		//			view.setImageBitmap(bmp);
		//		}
		return loadImage(uri, view, false, maxWidth, maxHeight, belongId, defaultId, false, listener);
	}

	public Bitmap loadImage(String uri, ImageView view, boolean isBackground, int maxWidth, int maxHeight,
			long belongId, int defaultId, boolean reload) {
		return loadImage(uri, view, isBackground, maxWidth, maxHeight, belongId, defaultId, reload, "");
	}

	public Bitmap loadImage(String uri, ImageView view, boolean isBackground, int maxWidth, int maxHeight,
			long belongId, int defaultId, boolean reload, String scaleType) {
		return loadImage(uri, view, isBackground, maxWidth, maxHeight, belongId, defaultId, reload, null, scaleType);
	}

	public Bitmap loadImage(String uri, ImageView view, boolean isBackground, int maxWidth, int maxHeight,
			long belongId, int defaultId, boolean reload, LocalImageListener listener) {
		return loadImage(uri, view, isBackground, maxWidth, maxHeight, belongId, defaultId, reload, listener, "");
	}

	public Bitmap loadImage(String uri, ImageView view, boolean isBackground, int maxWidth, int maxHeight,
			long belongId, int defaultId, boolean reload, LocalImageListener listener, String scaleType) {
		//		Bitmap bmp = doLoadImage(uri, view, isBackground, maxWidth, maxHeight, belongId, defaultId, reload, listener, scaleType);
		//		if (bmp != null && view != null) {
		//			view.setImageBitmap(bmp);
		//		}
		return doLoadImage(uri, view, isBackground, maxWidth, maxHeight, belongId, defaultId, reload, listener,
				scaleType);
	}

	private synchronized Bitmap doLoadImage(String uri, ImageView view, boolean isBackground, int maxWidth,
			int maxHeight, long belongId, int defaultId, boolean reload, LocalImageListener listener, String scaleType) {
		if (maxWidth <= 0 || maxHeight <= 0) {
			throw new IllegalArgumentException("图片尺寸无效。width:" + maxWidth + ",height:" + maxHeight);
		}
		if (TextUtils.isEmpty(uri) || "null".equalsIgnoreCase(uri) || "\"\"".equalsIgnoreCase(uri)) {
			if (defaultId != 0) {
				view.setImageResource(defaultId);
			} else {
				view.setImageBitmap(null);
			}
			setImageHolderUriNull(view, isBackground, maxWidth, maxHeight, belongId, defaultId, reload, listener,
					scaleType);
			Logger.getInstance(TAG).debug(belongId + ":imageUrl is null");
			return null;
		}

		//		Logger.getInstance(TAG).debug("load image:" + uri + ",real uri:" + realUrl);
		if (uri != null && view != null) {
			if (defaultId != 0) {
				view.setImageResource(defaultId);
			} else {
				view.setImageBitmap(null);
			}
			if (belongId == 0) {
				sendFetchMessage(view, null, null);
				return null;
			}
			Bitmap bmp = mImageCache.getBitmapFromSoft(uri);
			ImageHolder imageHolder = getImageHolderByView(view, belongId);
			if (imageHolder == null) {
				imageHolder = new ImageHolder(uri, view, maxWidth, maxHeight, belongId, defaultId);
				imageHolder.mIsBackground = isBackground;
				imageHolder.mReload = reload;
				addViewHolder(imageHolder);
			} else {
				imageHolder.mBelongId = belongId;
				imageHolder.mDefaultPicId = defaultId;
				imageHolder.mUri = uri;
				imageHolder.mIsBackground = isBackground;
				imageHolder.mReload = reload;
			}
			//			removeImageHolderByView(view, belongId);
			if (bmp != null && !reload) {
				//				addViewHolder(imageHolder);
				sendFetchMessage(view, uri, bmp);
				view.setImageBitmap(bmp);
				Logger.getInstance(TAG).debug("belong id:" + belongId + ",(" + uri + ") in cache");
				return bmp;
			}
			if (mStop) {
				mMissedHolders.add(imageHolder);
			} else {
				//				addViewHolder(imageHolder);
				sendFetchImageMessage(imageHolder, uri, listener);
			}
			return null;
		}
		return null;
	}

	/**
	 * @Title: setImageHolderUriNull
	 * @Description: url为null，设置ImageHolder为空
	 */
	private void setImageHolderUriNull(ImageView view, boolean isBackground, int maxWidth, int maxHeight,
			long belongId, int defaultId, boolean reload, LocalImageListener listener, String scaleType) {
		ImageHolder imageHolder = getImageHolderByView(view, belongId);
		if (imageHolder != null) {
			imageHolder.mUri = "";
			imageHolder.mImageView = view;
			imageHolder.mBelongId = belongId;
			imageHolder.mDefaultPicId = defaultId;
			imageHolder.mImageHeight = maxWidth;
			imageHolder.mImageWidth = maxHeight;
			imageHolder.mIsBackground = isBackground;
			imageHolder.mListener = listener;
			imageHolder.mReload = reload;
		}

		if (listener != null) {
			listener.localImageReady(view, null, null);
		}
	}

	public void start() {
		mStop = false;
		for (ImageHolder holder : mMissedHolders) {
			if (holder.mUri != null && holder.mImageView != null) {
				loadImage(holder.mUri, holder.mImageView, holder.mImageWidth, holder.mImageHeight, holder.mBelongId,
						holder.mDefaultPicId);
			}
		}
		mMissedHolders.clear();
	}

	public void stop() {
		mStop = true;
	}

	public void clearImageFetching() {
		// mStop = true;
		if (sImageFetchThreadPool != null) {
			sImageFetchThreadPool.shutdownNow();
			sImageFetchThreadPool = null;
		}
		mHandler.removeMessage();
//		mDownload.clear();
		mTaskManager.clear();
	}

	/**
	 * @ClassName: ImageFetcher
	 * @Description: 获取图片文件。如果磁盘上没有此文件，那么使用http协议从服务器下载并存成磁盘文件
	 * @author liubingsr
	 * @date 2012-6-18 下午8:44:11
	 * 
	 */
	private class ImageFetcher implements Runnable, ITask {
		private String mUri;
		private Bitmap mBitmap;
		private ImageView mImageView;
		private int mImageWidth;
		private int mImageHeight;
		private String mError;
//		private boolean mOnlyDownload = false;
//		private ImageDownloadListener mDownloadListener = null;
		private LocalImageListener mFetchListener = null;

		public ImageFetcher(String uri) {
			this(uri, false);
		}

		public ImageFetcher(String uri, boolean onlyDownload) {
			mUri = uri;
			mBitmap = null;
			mImageView = null;
			mError = null;
//			mOnlyDownload = onlyDownload;
		}

		public void setImageWidth(int width) {
			mImageWidth = width;
		}

		public void setImageHeight(int height) {
			mImageHeight = height;
		}

		public void setImageView(ImageView view) {
			mImageView = view;
		}

//
//		public void setDownloadListener(ImageDownloadListener listener) {
//			mDownloadListener = listener;
//		}

		public void setFetchListener(LocalImageListener listener) {
			mFetchListener = listener;
		}

//		public boolean isOnlyDownload() {
//			return mOnlyDownload;
//		}

		public String getError() {
			return mError;
		}

		public void run() {
			try {
				if (Thread.interrupted()) {
					mError = "线程被中止";
					handleError(this);
					mTaskManager.removeTask(this);
					return;
				}
//				String imageFile = mUri;
//				synchronized (mFileCache) {
//					imageFile = mFileCache.getCacheFile(mUri);
//				}
//				if (imageFile == null /* && mUri.startsWith("http") */) {
//					// 磁盘文件不存在
//					RequestInfo reqInfo = new RequestInfo(mUri);
//					reqInfo.setRequestType(RequestType.DOWNLOAD);
//					byte[] bytes = HttpNetwork.getInstance(mContext).downloadFile(reqInfo);
//					if (reqInfo.getResultCode() == ErrorManager.NO_ERROR && bytes != null) {
//						synchronized (mFileCache) {
//							// 写入磁盘文件
//							mFileCache.writeCacheFile(mUri, bytes);
//						}
//						imageFile = mFileCache.getCacheFile(mUri);
//					} else {
//						mError = "下载文件失败！";
//						Logger.getInstance(TAG).debug("下载图片:<" + mUri + ">失败!Error:" + reqInfo.getResultCode());
//						handleError(this);
//					}
//				}
				//				else {
				//					Logger.getInstance(TAG).debug("ImageFetcher:<" + imageFile + "> in local storage");
				//				}
				if (mUri != null) {
//					if (!mOnlyDownload) {
					//						int maxWidth = getMaxWidth(mUri, mCurrentBelongId);
					//						int maxHeight = getMaxHeight(mUri, mCurrentBelongId);
					//						Logger.getInstance(TAG).debug("imageWidth:" + mImageWidth + ",imageHeight:" + mImageHeight + "。ImageFetcher:" + imageFile);
					try {
						mBitmap = GraphicUtils.getImageThumbnailByPixel(mContext, mUri, mImageWidth, mImageHeight);
					} catch (OutOfMemoryError e) {
						//							Logger.getInstance(TAG).debug("ImageFetcher OutOfMemoryError:" + e.getMessage());
						recycleUnuseImage();
						mError = e.getMessage();
						System.gc();
						handleError(this);
					} catch (Exception e) {
						//							Logger.getInstance(TAG).debug("ImageFetcher Exception:" + e.getMessage());
						recycleUnuseImage();
						mError = e.getMessage();
						System.gc();
						handleError(this);
					}
					if (mBitmap == null) {
						recycleUnuseImage();
						mError = "bitmap is null";
						System.gc();
						mTaskManager.removeTask(this);
						handleError(this);
						return;
					}
//					}
				} else {
					mError = "imageFile is null";
					Logger.getInstance(TAG).debug(mUri + ":imageFile is null");
					mTaskManager.removeTask(this);
					handleError(this);
					return;
				}
				if (Thread.interrupted()) {
					mError = "线程被中止！";
					Logger.getInstance(TAG).debug(mError + mUri);
					//					if (mBitmap != null) {
					//						this.mImageView.setImageBitmap(mBitmap);
					//					}
					//					handleError(this);
					//					mTaskManager.removeTask(this);
					//					return;
				}
				if (mBitmap != null /* || mOnlyDownload */) {
					complete();
				}
				//				complete();
				mTaskManager.removeTask(this);
			} catch (OutOfMemoryError e) {
				recycleUnuseImage();
				System.gc();
				mError = e.getMessage();
				mTaskManager.removeTask(this);
				handleError(this);
			}
		}

		private void complete() {
			mHandler.obtainMessage(FETCH_IMAGE_MSG, this).sendToTarget();
		}

		@Override
		public void execute() {
			if (sImageFetchThreadPool == null) {
				sImageFetchThreadPool = Executors.newFixedThreadPool(POOLSIZE);
			}
			sImageFetchThreadPool.execute(this);
		}

		@Override
		public boolean isEqual(Object o) {
			if (o instanceof ImageFetcher) {
				ImageFetcher temp = (ImageFetcher) o;
				return this == temp;
			} else {
				return false;
			}
		}
	}

	private class ImageLoadHandler extends Handler {
		public ImageLoadHandler(Looper mainLooper) {
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message message) {
			ImageFetcher fetcher = (ImageFetcher) message.obj;
//			mDownload.remove(fetcher.mUri);
			if (fetcher.mError == null) {
//				if (fetcher.isOnlyDownload()) {
//					// TODO 仅下载图片
//					downloadReady(fetcher);
//					return;
//				}
				if (fetcher.mBitmap != null) {
					bitmapReady(fetcher);
				}
			}
		}

		public void removeMessage() {
			removeMessages(FETCH_IMAGE_MSG);
			removeMessages(DOWNLOAD_IMAGE_MSG);
			removeMessages(ERROR_MSG);
		}
	}

	private void handleError(ImageFetcher fetcher) {
//		mDownload.remove(fetcher.mUri);
		sendFetchMessage(null, fetcher.mUri, null);
	}

//	private void downloadReady(ImageFetcher fetcher) {
//		String localPath = getLocalImagePath(fetcher.mUri);
//		sendDownloadMessage(fetcher.mUri, localPath, fetcher.mDownloadListener, null);
//	}

	private synchronized void bitmapReady(ImageFetcher fetcher) {
		Bitmap bmp = fetcher.mBitmap;
		String uri = fetcher.mUri;
		ImageView imageView = fetcher.mImageView;
		boolean isSet = false;
		ArrayList<ImageHolder> holders = mViewHolders.get(mCurrentBelongId);
		if (holders != null) {
			for (ImageHolder holder : holders) {
				if (holder.mUri.equals(uri)) { //holder.mImageView == imageView
					if (!holder.mIsBackground) {
						holder.mImageView.setImageBitmap(bmp);
					} else {
						if (holder.mImageView.getContext() != null
								&& holder.mImageView.getContext().getResources() != null) {
							Drawable drawable = new BitmapDrawable(holder.mImageView.getContext().getResources(), bmp);
							holder.mImageView.setBackgroundDrawable(drawable);
						}
					}
					sendFetchMessage(holder.mImageView, uri, bmp, fetcher.mFetchListener);
					mImageCache.add(uri, bmp);
					//					Logger.getInstance(TAG).debug(holder.mImageView.getId() + ",bmp isSet:<" + uri + ">");
					isSet = true;
					//					break;
				}
			}
		}
		if (!isSet) {
			bmp.recycle();
		}
	}

	private ImageHolder getImageHolderByView(ImageView view, long belongId) {
		ArrayList<ImageHolder> holders = mViewHolders.get(belongId);
		if (holders != null) {
			Logger.getInstance(TAG).debug("ViewHolders count:" + holders.size());
			for (ImageHolder holder : holders) {
				if (holder.mImageView == view)
					return holder;
			}
		}
//		for (ImageHolder holder : mMissedHolders) {
//			if (holder.mImageView == view) {
//				return holder;
//			}
//		}
		return null;
	}

	private synchronized void removeImageHolderByView(ImageView view, long belongId) {
		ArrayList<ImageHolder> holders = mViewHolders.get(belongId);
		if (holders != null) {
			for (int index = holders.size() - 1; index >= 0; --index) {
				ImageHolder holder = holders.get(index);
				if (holder.mImageView == view) {
					holders.remove(index);
				}
			}
		}
		for (int index = mMissedHolders.size() - 1; index >= 0; --index) {
			ImageHolder holder = mMissedHolders.get(index);
			if (holder.mImageView == view) {
				mMissedHolders.remove(index);
			}
		}
	}

//	private void sendDownloadImageMessage(String uri, ImageDownloadListener listener) {
//		if (TextUtils.isEmpty(uri)) {
//			return;
//		}
//		if (mDownload.contains(uri)) {
//			return;
//		}
//		mDownload.add(uri);
//		final ImageFetcher fetcher = new ImageFetcher(uri, true);
//		fetcher.setDownloadListener(listener);
//		mTaskManager.addTask(fetcher);
//	}

	private void sendFetchImageMessage(ImageHolder imageHolder, String uri, LocalImageListener listener) {
//		if (mDownload.contains(uri)) {
//			return;
//		}
//		mDownload.add(uri);
		final ImageFetcher fetcher = new ImageFetcher(uri);
		fetcher.setFetchListener(listener);
		fetcher.setImageView(imageHolder.mImageView);
		fetcher.setImageWidth(imageHolder.mImageWidth);
		fetcher.setImageHeight(imageHolder.mImageHeight);
		mTaskManager.addTask(fetcher);
	}

	//	private synchronized int getMaxWidth(String uri, long belongId) {
	//		int maxWidth = -1;
	//		ArrayList<ImageHolder> holders = mViewHolders.get(belongId);
	//		if (holders != null) {
	//			for (ImageHolder holder : holders) {
	//				if (holder.mUri.equals(uri)) {
	//					if (maxWidth < holder.mMaxWidth) {
	//						maxWidth = holder.mMaxWidth;
	//					}
	//				}
	//			}
	//		}
	//		return maxWidth;
	//	}
	//	private synchronized int getMaxHeight(String uri, long belongId) {
	//		int maxHeight = -1;
	//		ArrayList<ImageHolder> holders = mViewHolders.get(belongId);
	//		if (holders != null) {
	//			for (ImageHolder holder : holders) {
	//				if (holder.mUri.equals(uri)) {
	//					if (maxHeight < holder.mMaxHeight) {
	//						maxHeight = holder.mMaxHeight;
	//					}
	//				}
	//			}
	//		}
	//		return maxHeight;
	//	}

	private void reloadImageHolder(ImageHolder holder) {
		Bitmap bmp = loadImage(holder.mUri, holder.mImageView, holder.mIsBackground, holder.mImageWidth,
				holder.mImageHeight, holder.mBelongId, holder.mDefaultPicId, holder.mReload);
		if (bmp != null && holder.mImageView != null) {
			holder.mImageView.setImageBitmap(bmp);
		}
	}

	private void addViewHolder(ImageHolder holder) {
		ArrayList<ImageHolder> holders = mViewHolders.get(holder.mBelongId);
		if (holders == null) {
			holders = new ArrayList<LocalImageLoader.ImageHolder>();
		}
		holders.add(holder);
		mViewHolders.put(holder.mBelongId, holders);
	}

	public void clearCache(String uri) {
		mImageCache.clearCache(uri);
//		mFileCache.deleteCacheFile(uri);
//		mFileCache.remove(uri);
	}

	public void recycle(long id) {
		recycleDestroyByBelongId(id);
	}

	private void removeImageView(ImageView v, long belongId) {
		removeImageHolderByView(v, belongId);
	}

	public synchronized void recycleUnuseImage() {
		ArrayList<String> uriList = mImageCache.getAllUri();
		HashSet<String> hash = new HashSet<String>();
		ArrayList<ImageHolder> holders = mViewHolders.get(mCurrentBelongId);
		if (holders != null) {
			for (ImageHolder holder : holders) {
				hash.add(holder.mUri);
			}
		}
		holders = mViewHolders.get(mLastBelongId);
		if (holders != null) {
			for (ImageHolder holder : holders) {
				hash.add(holder.mUri);
			}
		}
		for (String uri : uriList) {
			if (!hash.contains(uri)) {
				mImageCache.recycle(uri);
			}
		}
	}

	public synchronized void recycleDestroyByBelongId(long id) {
		mViewHolders.remove(id);
		for (int index = mMissedHolders.size() - 1; index >= 0; --index) {
			ImageHolder holder = mMissedHolders.get(index);
			if (holder.mBelongId == id) {
				mMissedHolders.remove(index);
			}
		}
		if (mLastBelongId != mCurrentBelongId) {
			recycleUnuseImage();
		}
	}

	public synchronized void recyclePauseByBelongId(long id) {
		clearImageFetching();
		if (mLastBelongId != id) {
			ArrayList<ImageHolder> holders = mViewHolders.get(mLastBelongId);
			if (holders != null) {
				for (int index = holders.size() - 1; index >= 0; --index) {
					ImageHolder holder = holders.get(index);
					holder.mImageView.setImageResource(holder.mDefaultPicId);
				}
			}
			for (int index = mMissedHolders.size() - 1; index >= 0; --index) {
				ImageHolder holder = mMissedHolders.get(index);
				if (holder.mBelongId == id) {
					holder.mImageView.setImageResource(holder.mDefaultPicId);
				}
			}
			mLastBelongId = id;
		}

	}

	public synchronized void resumeBelongId(long id) {
		if (mCurrentBelongId != id) {
			mCurrentBelongId = id;
//			mDownload.clear();
			ArrayList<ImageHolder> holders = mViewHolders.get(id);
			if (holders != null) {
				for (int index = holders.size() - 1; index >= 0; --index) {
					ImageHolder holder = holders.get(index);
					reloadImageHolder(holder);
				}
			}
			for (int index = mMissedHolders.size() - 1; index >= 0; --index) {
				ImageHolder holder = mMissedHolders.get(index);
				if (holder.mBelongId == id) {
					reloadImageHolder(holder);
				}
			}
			recycleUnuseImage();
			System.gc();
		}
	}

	public static class ImageHolder {
		public ImageView mImageView;
		public String mUri;
		public String mNewUri = null;
		public int mImageWidth;
		public int mImageHeight;
		public long mBelongId;
		public int mDefaultPicId;
		public boolean mReload = false;
		public boolean mIsBackground = false;
		public LocalImageListener mListener = null;

		public ImageHolder(String uri, ImageView view, int maxWidth, int maxHeight, long belongId, int defaultId) {
			this(uri, view, maxWidth, maxHeight, belongId, defaultId, null);
		}

		public ImageHolder(String uri, ImageView view, int maxWidth, int maxHeight, long belongId, int defaultId,
				LocalImageListener listener) {
			mUri = uri;
			mImageView = view;
			mImageWidth = maxWidth;
			mImageHeight = maxHeight;
			mBelongId = belongId;
			mDefaultPicId = defaultId;
			mListener = listener;
		}

		public void setBitmap(Bitmap bmp) {
			mImageView.setImageBitmap(bmp);
		}
	}

	public static class ImageBackgroundHolder extends ImageHolder {
		public ImageBackgroundHolder(String uri, ImageView view, int maxWidth, int maxHeight, long belongId,
				int defaultId) {
			super(uri, view, maxWidth, maxHeight, belongId, defaultId);
		}

		public void setBitmap(Bitmap bmp) {
			Drawable drawable = new BitmapDrawable(mImageView.getContext().getResources(), bmp);
			mImageView.setBackgroundDrawable(drawable);
		}
	}
}
