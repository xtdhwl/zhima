package com.zhima.base.gdi.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.zhima.base.logger.Logger;

/**
 * 滤镜管理类
 * 
 * @ClassName:ZhiMaFilterManager
 * @Description:TODO
 * @author liqilong
 * @date 2012-6-25 上午11:53:19
 * 
 */
public class ZhiMaFilterManager {
	public static final String TAG = "ZhiMaFilterManager";
	public final static int SUCCEED = 0;
	// 滤镜不存在
	public final static int ERROR_NO_FILTER = 1;
	// 处理产生异常
	public final static int ERROR_EXCEPTION = 2;

	private Context mContext;
	private Bitmap mSrcBitmap;
	private Bitmap mDestBitmap;
	public Image image;
	private OnCompletionListener mCompletionListener;

	public OnErrorListener getmErrorListener() {
		return mErrorListener;
	}

	public void setmErrorListener(OnErrorListener mErrorListener) {
		this.mErrorListener = mErrorListener;
	}

	private OnErrorListener mErrorListener;
	private boolean mIsError = false;
	private boolean isRuning;

	public ZhiMaFilterManager(Context context) {
		this.mContext = context;
		mDestBitmap = null;
		isRuning = false;
	}

	private Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (SUCCEED == msg.what) {
				mIsError = false;
				if (!mIsError && mCompletionListener != null) {
					if (mCompletionListener != null) {
						mCompletionListener.onCompletion((Bitmap) msg.obj);
					}
				}
			} else {
				mIsError = true;
				if (mErrorListener != null) {
					mErrorListener.onError(msg.what);
				}
			}
			recycle();

		}
	};

	//---------------------------------------------
	//method
	/**
	 * @Title: isRuning
	 * @Description:是否在滤镜中
	 * @return 
	 * boolean true在滤镜中,false没有在滤镜
	 */
	public boolean isRuning() {
		return isRuning;
	}

	public void filter(Bitmap bitmap, FilterType filterType) {
		isRuning = true;
		mSrcBitmap = Bitmap.createBitmap(bitmap);
		IImageFilter filter = getFilter(filterType);
		if (filter != null) {
			new ProcessImageTask(filter).execute();
		} else {
			mHandler.sendEmptyMessage(ERROR_NO_FILTER);
		}
	}

	private IImageFilter getFilter(FilterType filterType) {
		IImageFilter filter = null;
		switch (filterType) {

		// case GaussianBlurFilter:
		//
		// break;
		// case GradientFilter:
		//
		// break;
		// case GradientMapFilter:
		//
		// break;
		//
		// case SaturationModifyFilter:
		//
		// break;
		//
		// case VignetteFilter:
		//
		// break;

		case ColorToneFilter:
			filter = new ColorToneFilter(Color.rgb(33, 168, 254), 192);
			break;
		case FilmFilter:
			filter = new FilmFilter(80f);
			break;
		case SceneFilter:
			filter = new SceneFilter(5f, Gradient.Scene3());
			break;
		case PaintBorderFilter_1:
			filter = new PaintBorderFilter(0x00FF00);
			break;
		case PaintBorderFilter_2:
			filter = new PaintBorderFilter(0x00FFFF);
			break;
		case PaintBorderFilter_3:
			filter = new PaintBorderFilter(0xFF0000);
			break;
		case HslModifyFilter_1:
			filter = new HslModifyFilter(20f);
			break;
		case HslModifyFilter_2:
			filter = new HslModifyFilter(60f);
			break;
		case HslModifyFilter_3:
			filter = new HslModifyFilter(250f);
			break;
		// --------------------
		case NightVisionFilter:
			filter = new NightVisionFilter();
			break;
		case SepiaFilter:
			filter = new SepiaFilter();
			break;
		case VintageFilter:
			filter = new VintageFilter();
			break;
		case NoiseFilter:
			filter = new NoiseFilter();
			break;
		case BigBrotherFilter:
			filter = new BigBrotherFilter();
			break;
		case BlackWhiteFilter:
			filter = new BlackWhiteFilter();
			break;
		case FocusFilter:
			filter = new FocusFilter();
			break;
		}
		return filter;
	}

	/**
	 * @ClassName:Filter
	 * @Description:滤镜类型
	 * @author liqilong
	 * @date 2012-6-25 下午12:08:46
	 * 
	 */
	public enum FilterType {

		// 高斯模糊
		// GaussianBlurFilter,

		// Vignette相机特效
		// GradientFilter,

		/***/
		//GradientMapFilter,

		/** 色采饱和度特效 */
		//SaturationModifyFilter,

		/***/
		//VignetteFilter,

		/** 电影特效 */
		FilmFilter,

		/***/
		SceneFilter,

		/** 颜色增强 */
		ColorToneFilter,

		/** lomo效果 */
		PaintBorderFilter_1,
		/** lomo效果 */
		PaintBorderFilter_2,
		/** lomo效果 */
		PaintBorderFilter_3,
		/***/
		HslModifyFilter_1,
		/***/
		HslModifyFilter_2,
		/***/
		HslModifyFilter_3,
		// -------------------
		/** 夜景 */
		NightVisionFilter,
		/** 深褐色 */
		SepiaFilter,
		/** 过去 旧 特效 */
		VintageFilter,
		/** 噪点 */
		NoiseFilter,
		/** 自动校正效果 */
		BigBrotherFilter,
		/** 黑白 */
		BlackWhiteFilter,
		/** 聚焦特效 */
		FocusFilter;

	}

	/**
	 * @Title: setOnC
	 * @Description: 设置完成监听
	 * @param l
	 *            void
	 */
	public void setOnCompletionListener(OnCompletionListener l) {
		mCompletionListener = l;
	}

	/**
	 * @Title: setOnErrorListener
	 * @Description: 异常监听
	 * @param l
	 *            void
	 */
	public void setOnErrorListener(OnErrorListener l) {
		mErrorListener = l;
	}

	private final class ProcessImageTask extends AsyncTask<Void, Void, Bitmap> {
		private IImageFilter filter;

		public ProcessImageTask(IImageFilter imageFilter) {
			this.filter = imageFilter;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		public Bitmap doInBackground(Void... params) {
			Bitmap resultBitmap = null;
			try {
				if (filter != null) {
					if (image != null) {
						image.recycle();
						image = null;
					}
					image = new Image(mSrcBitmap);
					image = filter.process(image);
					image.copyPixelsFromBuffer();
					resultBitmap = image.getImage();
					return resultBitmap;
				}
			} catch (Exception e) {
				mHandler.sendEmptyMessage(ERROR_EXCEPTION);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			Logger.getInstance(TAG).debug("ZhiMaFilter--> ProcessImageTask onPostExecute");
			mDestBitmap = result;
			mHandler.obtainMessage(SUCCEED, mDestBitmap).sendToTarget();
			// if (filter != null) {
			// filter.release();
			// }
			mIsError = false;
		}
	}

	public void recycle() {
		if (mSrcBitmap != null) {
			if (!mSrcBitmap.isRecycled()) {
				mSrcBitmap.recycle();
				mSrcBitmap = null;
				System.gc();
//				Runtime.getRuntime().gc();
			}
		}
		if (mDestBitmap != null) {
			if (!mDestBitmap.isRecycled()) {
				mDestBitmap.recycle();
				mDestBitmap = null;
				System.gc();
			}
		}
		if (image != null) {
			image.recycle();
			image = null;
			System.gc();
		}
	}

	/**
	 * 完成监听
	 * 
	 * @ClassName:OnCompletionListener
	 * @Description:TODO
	 * @author liqilong
	 * @date 2012-6-25 上午11:13:14
	 * 
	 */
	public interface OnCompletionListener {
		void onCompletion(Bitmap bitmap);
	}

	/**
	 * 异常监听
	 * 
	 * @ClassName:OnErrorListener
	 * @Description:TODO
	 * @author liqilong
	 * @date 2012-6-25 上午11:15:36
	 * 
	 */
	public interface OnErrorListener {
		void onError(int what);
	}
}
