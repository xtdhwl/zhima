package com.zhima.ui.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.zhima.R;
import com.zhima.base.logger.Logger;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: CameraController
 * @Description: 拍照
 * @author liubingsr
 * @date 2012-6-19 下午2:24:37
 * 
 */
public class CameraController {
	private static final String TAG = "CameraController";
	private Context mContext;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mPreviewHolder;
	private Camera mCamera;
	private Bitmap mBitmap;
	private boolean isVertical = false;
	private byte[] mImageBytes;
	private ICameraHelper mCameraHelper;
	private boolean mIsLandscape;
	private boolean mIsDisplayBad = false;
	private WindowManager mWM;

	public CameraController(Context c, ICameraHelper cameraHelper,
			SurfaceView surface, boolean isLandscape) {
		mContext = c;
		mCameraHelper = cameraHelper;
		mSurfaceView = surface;
		mPreviewHolder = mSurfaceView.getHolder();
		mPreviewHolder.addCallback(mSurfaceCallback);
		mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		String strPhoneModel = Build.MODEL;
		this.mIsLandscape = isLandscape;
		mImageBytes = null;
		mBitmap = null;
	}

	private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				mCamera.stopPreview();
				finish();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mPreviewHolder = holder;
			try {
				mCamera = Camera.open();
			} catch (NoSuchMethodError e) {
				e.printStackTrace();
				mIsDisplayBad = true;
			} catch (Exception e) {
				e.printStackTrace();
				mCameraHelper.exception();
				return;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mCamera == null) {
				return;
			}

			try {
				if (mCamera != null) {
					mCamera.setPreviewDisplay(holder);
				}
			} catch (IOException exception) {
				mCameraHelper.exception();
			}

			setCameraParameters();

		}

	};

	private void setPreviewSize(Parameters parameters) {
		List<Camera.Size> sizes = mCamera.getParameters()
				.getSupportedPreviewSizes();
		if (sizes != null && sizes.size() > 0) {
			int pos;
			int maxWidth = 400;
			if (sizes.get(0).width < sizes.get(sizes.size() - 1).width) {
				pos = 0;
				for (int i = sizes.size() - 1; i >= 0; i--) {
					if (sizes.get(i).width <= maxWidth) {
						pos = i;
						break;
					}
				}
			} else {
				pos = sizes.size() - 1;
				for (int i = 0; i < sizes.size(); i++) {
					if (sizes.get(i).width <= maxWidth) {
						pos = i;
						break;
					}
				}
			}
			if (pos < 0) {
				pos = 0;
			}
			if (pos >= sizes.size()) {
				pos = sizes.size() - 1;
			}
			parameters.setPreviewSize(sizes.get(pos).width,
					sizes.get(pos).height);
		}
		List<Integer> formats = mCamera.getParameters()
				.getSupportedPreviewFormats();
		if (formats.size() > 0) {
			parameters.setPreviewFormat(formats.get(0));
		}
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

	private void setPictureSize(Parameters parameters) {
		List<Camera.Size> sizes = mCamera.getParameters()
				.getSupportedPictureSizes();
		int pos;
		final int MAXWIDTH = 800;
		if (sizes.get(0).width < sizes.get(sizes.size() - 1).width) {
			for (pos = sizes.size() - 1; pos >= 0; pos--) {
				if (sizes.get(pos).width <= MAXWIDTH) {
					break;
				}
			}
		} else {
			for (pos = 0; pos < sizes.size(); pos++) {
				if (sizes.get(pos).width <= MAXWIDTH) {
					break;
				}
			}
		}

		if (pos < 0) {
			pos = 0;
		}
		if (pos >= sizes.size()) {
			pos = sizes.size() - 1;
		}

		parameters.setPictureSize(sizes.get(pos).width, sizes.get(pos).height);
	}

	public abstract interface ICameraHelper {
		public void takePicture();

		public Uri savePicture();

		public void retake();

		public void cancel();

		public void exception();
	}

	public PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera mCamera) {
			if (data != null) {
				mImageBytes = data;
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (!mIsLandscape && mBitmap.getWidth() > mBitmap.getHeight()) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
							mBitmap.getWidth(), mBitmap.getHeight(), matrix,
							false);
				}
				mCameraHelper.takePicture();
				mCamera.stopPreview();
			}
		}
	};

	public void takePicture() {
		try {
			mCamera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					try {
						mCamera.takePicture(null, null, mPictureCallback);
					} catch (RuntimeException e) {
						HaloToast.show(mContext,
								R.string.camera_auto_focus_exception);
					}
				}
			});
		} catch (RuntimeException e) {
			e.printStackTrace();
			HaloToast.show(mContext, R.string.camera_auto_focus_exception);
			mCamera.stopPreview();
			mCamera.release();
		}
	}

	/**
	 * 设置摄像头
	 * 
	 * @Title: setCameraParameters
	 * @Description: TODO void
	 */
	private void setCameraParameters() {
		Camera.Parameters parameters = mCamera.getParameters();
		// parameters.setPreviewFrameRate(3);
		// parameters.setPreviewFormat(PixelFormat.JPEG);
		// parameters.set("jpeg-quality", 85);
		// 摄像头竖屏调整 SDK版本选择，兼容
		if (Integer.parseInt(Build.VERSION.SDK) >= 8)
			setDisplayOrientation(mCamera, 90);
		else {
			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				parameters.set("orientation", "portrait");
				parameters.set("rotation", 90);
			}
			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "landscape");
				parameters.set("rotation", 90);
			}
		}
		// Display display=mWM.getDefaultDisplay();
		// parameters.setPreviewSize(display.getWidth(),display.getHeight());

		try {
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage());
		}

		try {
			setPreviewSize(parameters);
			setPictureSize(parameters);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			mCameraHelper.exception();
		}
	}

	public void retake() {
		if (mCamera == null) {
			mCamera = Camera.open();
			setCameraParameters();
		}
		mCamera.startPreview();
		mCameraHelper.retake();
	}

	public Bitmap getTakedPic() {
		return mBitmap;
	}

	public byte[] getImageBytes() {
		return mImageBytes;
	}

	public void setPic(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public void finish() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
			mCameraHelper.cancel();
		}
	}
}
