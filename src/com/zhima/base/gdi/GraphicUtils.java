package com.zhima.base.gdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.zhima.base.logger.Logger;
import com.zhima.base.storage.FileCache;

public final class GraphicUtils {
	private final static String TAG = "GraphicUtils";
	public static int mScreenWidth;
	public static int mScreenHeight;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Bitmap createRoundImage(Context context, Bitmap originalImage, Bitmap mask) {
		RectF clipRect = new RectF();
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Bitmap roundBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(roundBitmap);
		canvas.drawBitmap(originalImage, 0, 0, null);
		clipRect.set(0, 0, width, height);
		canvas.drawBitmap(mask, null, clipRect, null);
		return roundBitmap;
	}

	public static Bitmap createRoundImage(Context context, Bitmap originalImage, int dp) {
		final int CONNER = dip2px(context, dp);
		RectF clipRect = new RectF();
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Bitmap roundBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(roundBitmap);
		Path path = new Path();
		clipRect.set(0, 0, width, height);
		path.addRoundRect(clipRect, CONNER, CONNER, Path.Direction.CCW);
		canvas.clipPath(path);
		canvas.drawBitmap(originalImage, null, clipRect, null);
		return roundBitmap;
	}

	/**
	* @Title: rotateImage
	* @Description: 旋转bitmap
	* @param srcBitmap 原bitmap
	* @param rotate 旋转角度
	 */
	private static Bitmap rotateImage(Bitmap srcBitmap, int rotate) {
		int width = srcBitmap.getWidth();
		int height = srcBitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.setRotate(rotate);

		Bitmap rotateBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);
		return rotateBitmap;
	}

	public static Bitmap createReflectedImage(Context context, Bitmap originalImage, Bitmap mask) {
		final int reflectionGap = 1;
		final int CONNER = dip2px(context, 5);
		Path path = new Path();
		RectF clipRect = new RectF();

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap
				.createBitmap(originalImage, 0, height * 3 / 4, width, height / 4, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 4), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(originalImage, 0, 0, null);
		clipRect.set(0, 0, width, height);
		canvas.drawBitmap(mask, null, clipRect, null);

		canvas.save();
		clipRect.set(0, height, width, (height + height / 4 + CONNER) + reflectionGap);
		path.addRoundRect(clipRect, CONNER, CONNER, Path.Direction.CCW);
		canvas.clipPath(path);

		Paint point = new Paint();
		point.setColor(Color.LTGRAY);
		canvas.drawRect(clipRect, point);
		canvas.restore();

		canvas.save();
		clipRect.set(1, height + 1, width - 1, (height + height / 4 + CONNER) + reflectionGap);
		path.reset();
		path.addRoundRect(clipRect, CONNER, CONNER, Path.Direction.CCW);
		canvas.clipPath(path);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		canvas.restore();

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, height, 0, bitmapWithReflection.getHeight() + reflectionGap,
				0x50000000, 0x00000000, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	public final static int LOCATION_LEFT_TOP = 0;
	public final static int LOCATION_BOTTOM_RIGHT = 1;

	public static final Point getViewPosition(View view, int type) {
		int[] locations = new int[2];
		view.getLocationInWindow(locations);
		Point point = new Point(locations[0], locations[1]);
		if (type == LOCATION_LEFT_TOP)
			return point;
		if (type == LOCATION_BOTTOM_RIGHT) {
			int width = view.getWidth();
			int height = view.getHeight();
			point.x += width;
			point.y += height;
			return point;
		}
		return new Point(0, 0);
	}

	public static Bitmap getBitmapByBitmap(Bitmap backBitmap, Bitmap mask, int x, int y) {
		Bitmap output = Bitmap.createBitmap(backBitmap.getWidth(), backBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawBitmap(mask, x, y, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(backBitmap, rect, rect, paint);

		return output;
	}

	public final static boolean pointInView(Point point, View view) {
		Point left_top = GraphicUtils.getViewPosition(view, GraphicUtils.LOCATION_LEFT_TOP);
		Point bottom_right = GraphicUtils.getViewPosition(view, GraphicUtils.LOCATION_BOTTOM_RIGHT);
		return (point.x >= left_top.x && point.x <= bottom_right.x && point.y >= left_top.y && point.y <= bottom_right.y);
	}

	// 图形配比
	public static Bitmap getImageScale(Bitmap bitmap, int width, int height) {
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		float scaleWidth = ((float) width) / srcWidth;
		float scaleHeight = ((float) height) / srcHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap destbmp = Bitmap.createBitmap(bitmap, 0, 0, srcWidth, srcHeight, matrix, true);
		return destbmp;
	}

	public final static Bitmap getImageScaleMessage(Context context, Bitmap bmp, int width, int height) {
		if (bmp == null) {
			return null;
		}
		int widthMax = GraphicUtils.dip2px(context, width);
		int heightMax = GraphicUtils.dip2px(context, height);
		Bitmap resultBmp = null;

		try {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;

			int sampleSize = 1;
			option.inJustDecodeBounds = false;
			int widthRatio = option.outWidth / widthMax;
			int heightRatio = option.outHeight / heightMax;
			if (widthRatio > 1 || heightRatio > 1) {
				sampleSize = Math.max(widthRatio, heightRatio);
				option.inSampleSize = sampleSize;
			}
			int newWidth = bmp.getWidth();
			int newHeight = bmp.getHeight();
			if (newWidth <= widthMax && newHeight <= heightMax) {
				return bmp;
			}

			float scaleWidth = ((float) widthMax) / newWidth;
			float scaleHeight = ((float) heightMax) / newHeight;
			float scale = Math.min(scaleWidth, scaleHeight);

			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);

			resultBmp = Bitmap.createBitmap(bmp, 0, 0, newWidth, newHeight, matrix, true);

			if (bmp != null) {
				bmp.recycle();
			}
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			return null;
		}
		return resultBmp;
	}
	/**
	* @Title: getImageThumbnail
	* @Description: 得到缩略图(单位是像素值)
	* @param context
	* @param uri
	* @param width 
	* @param height
	* @return
	* Bitmap
	*/
	public final static Bitmap getImageThumbnail(Context context, Uri uri, int width, int height) {
		if (uri == null) {
			return null;
		}
		return getImageThumbnail(context, uri.getPath(), width, height);
	}

	/**
	 * 创建缩略图
	 * 
	 * @Title: getImageThumbnail
	 * @Description: TODO
	 * @param context
	 *            上下文
	 * @param bitmap
	 *            原Bitmap
	 * @param width
	 *            缩略图的宽度
	 * @param height
	 *            缩略图的高度
	 * @return 返回缩略图 Bitmap
	 */
	public final static Bitmap getImageThumbnail(Context context, Bitmap bitmap, int width, int height) {
		Bitmap resultBmp = null;
		int widthMax = GraphicUtils.dip2px(context, width);
		int heightMax = GraphicUtils.dip2px(context, height);

		int newWidth = bitmap.getWidth();
		int newHeight = bitmap.getHeight();
		if (newWidth <= widthMax && newHeight <= heightMax) {
			return bitmap;
		}
		float scaleWidth = ((float) widthMax) / newWidth;
		float scaleHeight = ((float) heightMax) / newHeight;
		float scale = Math.min(scaleWidth, scaleHeight);

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		resultBmp = Bitmap.createBitmap(bitmap, 0, 0, newWidth, newHeight, matrix, true);

		if (bitmap != null) {
			bitmap.recycle();
		}
		return resultBmp;
	}

	/**
	 * @Title: getImageThumbnail
	 * @Description: 创建缩略图
	 * @param context
	 * @param uri
	 * @param width
	 * @param height
	 * @return Bitmap
	 */
	// public final static Bitmap getImageThumbnail(Context context,
	// String filePath, int width, int height) {
	// if (TextUtils.isEmpty(filePath)) {
	// return null;
	// }
	// int widthMax = GraphicUtils.dip2px(context, width);
	// int heightMax = GraphicUtils.dip2px(context, height);
	//
	// Bitmap resultBmp = null;
	// try {
	// BitmapFactory.Options option = new BitmapFactory.Options();
	// option.inJustDecodeBounds = true;
	// BitmapFactory.decodeFile(filePath, option);
	//
	// int sampleSize = 1;
	// option.inJustDecodeBounds = false;
	// int widthRatio = option.outWidth / widthMax;
	// int heightRatio = option.outHeight / heightMax;
	// if (widthRatio > 1 || heightRatio > 1) {
	// sampleSize = Math.max(widthRatio, heightRatio);
	// option.inSampleSize = sampleSize;
	// }
	//
	// Bitmap bmp = BitmapFactory.decodeFile(filePath, option);
	//
	// int newWidth = bmp.getWidth();
	// int newHeight = bmp.getHeight();
	// if (newWidth <= widthMax && newHeight <= heightMax) {
	// return bmp;
	// }
	// float scaleWidth = ((float) widthMax) / newWidth;
	// float scaleHeight = ((float) heightMax) / newHeight;
	// float scale = Math.min(scaleWidth, scaleHeight);
	//
	// Matrix matrix = new Matrix();
	// matrix.postScale(scale, scale);
	//
	// resultBmp = Bitmap.createBitmap(bmp, 0, 0, newWidth, newHeight,
	// matrix, true);
	//
	// if (bmp != null) {
	// bmp.recycle();
	// }
	// } catch (Exception e) {
	// Logger.getInstance(TAG).debug(e.getMessage());
	// return null;
	// }
	// return resultBmp;
	// }
	/**
	* @Title: getImageThumbnail
	* @Description: 得到缩略图(单位是dp值)
	* @param context
	* @param filePath
	* @param width
	* @param height
	* @return
	* Bitmap
	*/
	@Deprecated
	public final static Bitmap getImageThumbnail(Context context, String filePath, int width, int height) {
		int widthMax = GraphicUtils.dip2px(context, width);
		int heightMax = GraphicUtils.dip2px(context, height);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDither = false;
		if (widthMax > 0 && heightMax > 0) {
			//			opts.inTempStorage = new byte[2 * 1024 * 1024];
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			// 计算图片缩放比例
			final int minSideLength = Math.min(widthMax, heightMax);
			opts.inSampleSize = computeSampleSize(opts, minSideLength, widthMax * heightMax);
		} else {
			opts.inSampleSize = 4;
		}
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inInputShareable = true;
		opts.inPurgeable = true;
		//		opts.inTempStorage = new byte[32 * 1024];
		Logger.getInstance(TAG).debug(filePath + ":" + opts.inSampleSize);
		File file = new File(filePath);
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		Bitmap bmp = null;
		if (fs != null) {
			try {
				bmp = BitmapFactory.decodeStream(fs, null, opts);//decodeFileDescriptor(fs.getFD(), null, opts)
			} catch (OutOfMemoryError e) {
				System.gc();
				Logger.getInstance(TAG).debug(e.getMessage());
			} finally {
				try {
					fs.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}
		if (bmp == null) {
			System.gc();
		}
		return bmp;
	}

	public final static Bitmap getImageThumbnailByDip(Context context, String filePath, int width, int height) {
		int widthMax = GraphicUtils.dip2px(context, width);
		int heightMax = GraphicUtils.dip2px(context, height);
		return getImageThumbnailByPixel(context, filePath, widthMax, heightMax);
	}

	public final static Bitmap getImageThumbnailByPixel(Context context, String filePath, int width, int height) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDither = false;
		if (width > 0 && height > 0) {
			//			opts.inTempStorage = new byte[2 * 1024 * 1024];
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			// 计算图片缩放比例
			final int minSideLength = Math.min(width, height);
			opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
		} else {
			opts.inSampleSize = 4;
		}
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inInputShareable = true;
		opts.inPurgeable = true;
		//		opts.inTempStorage = new byte[32 * 1024];
		Logger.getInstance(TAG).debug(filePath + ":" + opts.inSampleSize);
		File file = new File(filePath);
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		Bitmap bmp = null;
		if (fs != null) {
			try {
				bmp = BitmapFactory.decodeStream(fs, null, opts);//decodeFileDescriptor(fs.getFD(), null, opts)
			} catch (OutOfMemoryError e) {
				System.gc();
				Logger.getInstance(TAG).debug(e.getMessage());
			} finally {
				try {
					fs.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}
		if (bmp == null) {
			System.gc();
		}
		return bmp;
	}

	//	public final static Bitmap getImageThumbnail(Context context, String filePath, int width, int height) {
	//		int widthMax = GraphicUtils.dip2px(context, width);
	//		int heightMax = GraphicUtils.dip2px(context, height);
	//		Bitmap bmp = null;
	//		BitmapFactory.Options opts = null;
	//		if (widthMax > 0 && heightMax > 0) {
	//			opts = new BitmapFactory.Options();
	//			opts.inJustDecodeBounds = true;
	//			BitmapFactory.decodeFile(filePath, opts);
	//			// 计算图片缩放比例
	//			final int minSideLength = Math.min(widthMax, heightMax);
	//			opts.inSampleSize = computeSampleSize(opts, minSideLength, widthMax * heightMax);
	//			opts.inJustDecodeBounds = false;
	//			opts.inPreferredConfig = Bitmap.Config.RGB_565;
	//			opts.inInputShareable = true;
	//			opts.inPurgeable = true;
	//		}
	//		try {
	//			return BitmapFactory.decodeFile(filePath, opts);
	//		} catch (OutOfMemoryError e) {
	//			System.gc();
	//			Logger.getInstance(TAG).debug(e.getMessage());
	//		}
	//		return bmp;
	//	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;// <= 1 ? 2 : roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public final static int AT_TOP = 0;
	public final static int AT_BOTTOM = 1;
	public final static int OTHER = 2;

	public static final int PULL_TO_REFRESH = 3;
	public static final int RELEASE_TO_REFRESH = 4;
	public static final int REFRESHING = 5;
	public static final int FRESH_OTHER = 6;

	/**
	 * @Title: toGrayscale
	 * @Description: 灰度图
	 * @param bmpOriginal
	 * @return Bitmap
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);

		return bmpGrayscale;
	}

	/**
	 * @Title: getColor
	 * @Description: 得到RGB颜色值
	 * @param color
	 * @return int
	 */
	public static int getColor(int color) {
		return Color.rgb(color >> 16, (color & 0x00ffff) >> 8, color & 0x0000ff);
	}

	/**
	 * @Title: getImage
	 * @Description: 加载图片
	 * @param context
	 * @param uri
	 * @return Bitmap
	 */
	public final static Bitmap getImage(Context context, Uri uri) {
		Bitmap resultBmp = null;
		try {
			String path = uri.getPath();
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			Bitmap bmp;
			option.inJustDecodeBounds = false;
			option.inSampleSize = 2;
			bmp = BitmapFactory.decodeFile(path, option);
			if (bmp == null) {
				bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, option);
			}
			resultBmp = bmp;
			return resultBmp;
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			return null;
		} catch (OutOfMemoryError e) {
			System.gc();
			Logger.getInstance(TAG).debug(e.getMessage());
			return null;
		}
	}

	/**
	 * @Title: combineImage
	 * @Description: 图片拼接(将多张图片拼接成一个图片)
	 * @param imagePaths
	 *            要拼接的图片路径
	 * @param format
	 *            文件格式(jpg、png)
	 * @param quality
	 *            压缩质量
	 * @param destFileName
	 *            拼接后图片文件名
	 * @return boolean
	 */
	public static boolean combineImage(String[] imagePaths, CompressFormat format, int quality, String destFileName) {
		boolean result = false;
		int outHeight = 0;
		int outWidth = 0;
		ArrayList<Bitmap> srcBitmapList = new ArrayList<Bitmap>(imagePaths.length);
		// 计算拼接后图片的高度
		for (int index = 0; index < imagePaths.length; ++index) {
			Bitmap srcBitmap = BitmapFactory.decodeFile(imagePaths[index]);
			if (srcBitmap.getWidth() > outWidth) {
				outWidth = srcBitmap.getWidth();
			}
			outHeight += srcBitmap.getHeight();
			srcBitmapList.add(srcBitmap);
		}
		// 创建拼接结果的缩略图
		Bitmap destBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(destBitmap);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		// 对位图进行滤波处理
		paint.setFilterBitmap(true);

		int top = 0;
		for (int index = 0; index < imagePaths.length; ++index) {
			Bitmap srcBitmap = srcBitmapList.get(index);
			Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
			Rect destRect = new Rect();
			destRect.set(0, top, srcBitmap.getWidth(), srcBitmap.getHeight() + top);
			// 把图片srcBit上srcRect画到destRect的区域内
			canvas.drawBitmap(srcBitmap, srcRect, destRect, paint);
			top += srcBitmap.getHeight();
			srcBitmap.recycle();
		}
		srcBitmapList.clear();
		File destFile = new File(destFileName);
		if (destFile != null) {
			// 经过图像变换之后的Bitmap里的数据可以保存到图像压缩文件里（JPG/PNG）。
			// 参数format可设置JPEG或PNG格式；
			// quality可选择压缩质量；fOut是输出流（OutputStream）
			result = saveBitmapFile(destBitmap, format, quality, destFileName);
		}
		return result;
	}

	/**
	 * @Title: saveBitmapFile
	 * @Description: 保存bitmap到磁盘文件
	 * @param bitmap
	 * @param format
	 *            图片格式
	 * @param quality
	 *            压缩质量
	 * @param destFileName
	 *            文件名
	 * @return boolean true:成功
	 */
	public static boolean saveBitmapFile(Bitmap bitmap, CompressFormat format, int quality, String destFileName) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(destFileName);
			bitmap.compress(format, quality, fos);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
		} catch (IOException e) {
			Logger.getInstance(TAG).debug(e.getMessage());
		}
		return false;
	}

	/**
	* @Title: getScreenBitmap 
	* @Description: 根据屏幕大小比例返回Bitmap
	* @param @param path
	* @param @return
	* @return Bitmap
	* @throws
	 */
	//TODO 测试
	public static Bitmap getScreenBitmap(Context context, String path) {
		String imageFile = FileCache.getInstance().getCacheFile(path);
		if (imageFile != null) {
			Bitmap bmp = null;
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			int fullWidth = width;
			int fullHeight = height;
			if (width > GraphicUtils.mScreenWidth || height > GraphicUtils.mScreenHeight) {
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				GraphicUtils.mScreenWidth = display.getWidth();
				GraphicUtils.mScreenHeight = display.getHeight();
				float scaleWidth = width / (float) GraphicUtils.mScreenWidth;
				float scaleHeight = height / (float) GraphicUtils.mScreenHeight;
				float scale = Math.max(scaleWidth, scaleHeight);
				fullWidth = (int) (width / scale);
				fullHeight = (int) (height / scale);
			}
			final int minSideLength = Math.min(fullWidth, fullHeight);
			opts.inSampleSize = computeSampleSize(opts, minSideLength, fullWidth * fullHeight);
			opts.inJustDecodeBounds = false;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inInputShareable = true;
			opts.inPurgeable = true;
			bmp = BitmapFactory.decodeFile(imageFile, opts);
			if (bmp != null) {
				if (fullWidth != width || fullHeight != height) {
					//TODO 添加比例是否符合
					Bitmap result = Bitmap.createBitmap(fullWidth, fullHeight, bmp.getConfig());
					Canvas canvas = new Canvas(result);
					Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
					Rect dst = new Rect(0, 0, fullWidth, fullHeight);
					Paint paint = new Paint();
					paint.setAntiAlias(true);
					paint.setDither(true);
					paint.setFilterBitmap(true);
					canvas.drawBitmap(bmp, src, dst, paint);
					bmp.recycle();
					bmp = null;
					return result;
				}
			}
			return bmp;
		}
		return null;
	}
}
