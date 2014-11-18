/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhima.ui.scancode.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.zhima.base.logger.Logger;
import com.zhima.ui.scancode.activity.ScanningActivity;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 480;
	private static final int MAX_FRAME_HEIGHT = 480;// 设置为600(HTCG12)报错

	private volatile static CameraManager cameraManager;
	private static final Object mLock = new Object();

	/** 开始预览状态 */
	public static final int STOP_PREVIE_STATE = 0x1;
	/** 停止预览状态 */
	public static final int PREVIEWING_STATE = 0x2;
	/** 关闭Camera */
	public static final int CLOSE_STATE = 0x3;
	public static final int OPEN_STATE = 0x4;

	static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT
	static {
		int sdkInt;
		try {
			sdkInt = Integer.parseInt(Build.VERSION.SDK);
		} catch (NumberFormatException nfe) {
			// Just to be safe
			sdkInt = 7;
		}
		SDK_INT = sdkInt;
	}

	private final Context context;
	private final CameraConfigurationManager configManager;
	private Camera camera;
	private Rect framingRect;
	private Rect framingRectInPreview;
	private boolean initialized;
	private boolean previewing;
	private boolean mForMeiZum9 = false;
	private final boolean useOneShotPreviewCallback;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;
	/**
	 * Autofocus callbacks arrive here, and are dispatched to the Handler which
	 * requested them.
	 */
	private final AutoFocusCallback autoFocusCallback;
	private Point mScreenResolution;
	private int mFramingRectTop = 50;

	/**
	 * Initializes this static object with the Context of the calling Activity.
	 * 
	 * @param context
	 *            The Activity which wants to use the camera.
	 */
	public static void init(Context context, ScanningActivity activity) {
		if (cameraManager == null) {
			cameraManager = new CameraManager(context, activity);
		}
		cameraManager.setTimeout(false);
	}

	/**
	 * Gets the CameraManager singleton instance.
	 * 
	 * @return A reference to the CameraManager singleton.
	 */
	public static CameraManager get() {
		return cameraManager;
	}

	private CameraManager(Context context, ScanningActivity activity) {

		this.context = context;
		this.configManager = new CameraConfigurationManager(context);
		// Camera.setOneShotPreviewCallback() has a race condition in Cupcake,
		// so we use the older
		// Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later,
		// we need to use
		// the more efficient one shot callback, as the older one can swamp the
		// system and cause it
		// to run out of memory. We can't use SDK_INT because it was introduced
		// in the Donut SDK.
		// useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) >
		// Build.VERSION_CODES.CUPCAKE;
		useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3
																				// =
																				// Cupcake
		previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
		autoFocusCallback = new AutoFocusCallback();

		String strPhoneModel = Build.MODEL;
		if (strPhoneModel.equalsIgnoreCase("M9")) {
			mForMeiZum9 = true;
		}
	}

	public void setActivity(ScanningActivity activity) {
		if (previewCallback != null)
			previewCallback.setActivity(activity);
	}

	public void setTimeout(boolean timeout) {
		if (previewCallback != null)
			previewCallback.setTimeout(timeout);
	}

	public void setScreenResolution(int x, int y) {
		mScreenResolution = new Point(x, y);
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public void openDriver(SurfaceHolder holder) throws IOException {
		if (camera == null) {
			camera = Camera.open();
			if (camera == null) {
				throw new IOException();
			}

			camera.setPreviewDisplay(holder);
			if (!initialized) {
				initialized = true;
				configManager.initFromCameraParameters(camera, mScreenResolution);
			}
			configManager.setDesiredCameraParameters(camera);

			Camera.Parameters params = camera.getParameters();
			params.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
			// params.setSceneMode(Parameters.SCENE_MODE_BARCODE);
			camera.setParameters(params);

			// SharedPreferences prefs =
			// PreferenceManager.getDefaultSharedPreferences(context);
			// �Ƿ�ʹ��ǰ��
			// if (prefs.getBoolean(PreferencesActivity.KEY_FRONT_LIGHT, false))
			// {
			// FlashlightManager.enableFlashlight();
			// }
			// FlashlightManager.enableFlashlight();
		}
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public void closeDriver() {
		if (camera != null) {
			FlashlightManager.disableFlashlight();
			camera.release();
			camera = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public void startPreview() {
		if (camera != null && !previewing) {
			camera.startPreview();
			previewing = true;
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public void stopPreview() {
		if (camera != null && previewing) {
			if (!useOneShotPreviewCallback) {
				camera.setPreviewCallback(null);
			}
			camera.stopPreview();
			previewCallback.setHandler(null, 0);
			autoFocusCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	public boolean isPreviewing() {
		return previewing;
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 * 
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public void requestPreviewFrame(Handler handler, int message) {
		if (camera != null && previewing) {
			previewCallback.setHandler(handler, message);
			if (useOneShotPreviewCallback) {
				camera.setOneShotPreviewCallback(previewCallback);
			} else {
				camera.setPreviewCallback(previewCallback);
			}
		}
	}

	/**
	 * Asks the camera hardware to perform an autofocus.
	 * 
	 * @param handler
	 *            The Handler to notify when the autofocus completes.
	 * @param message
	 *            The message to deliver.
	 */
	public void requestAutoFocus(Handler handler, int message) {
		if (camera != null && previewing) {
			autoFocusCallback.setHandler(handler, message);
			try {
				camera.autoFocus(autoFocusCallback);
			} catch (Exception e) {
				Logger.getInstance(TAG).error("Unexpected exception while focusing:" + e);
			}
		}
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public Rect getFramingRect() {
		// 手机屏幕的宽与高
		Point screenResolution = configManager.getScreenResolution();
		// 相机的宽与高
		Point cameraResolution = cameraManager.configManager.getCameraResolution();
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}
			int width = cameraResolution.y * 3 / 4;
			if (width < MIN_FRAME_WIDTH) {
				width = MIN_FRAME_WIDTH;
			} else if (width > MAX_FRAME_WIDTH) {
				width = MAX_FRAME_WIDTH;
			}
			int height = cameraResolution.x * 3 / 4;
			if (height < MIN_FRAME_HEIGHT) {
				height = MIN_FRAME_HEIGHT;
			} else if (height > MAX_FRAME_HEIGHT) {
				height = MAX_FRAME_HEIGHT;
			}
			// 如果屏幕宽度小于或等于320（320*480一下『包含320*480』），高度为一半，防止遮挡其他控件
			if (screenResolution.x <= 320) {
				height = cameraResolution.x * 3 / 6;
				if (height < MIN_FRAME_HEIGHT) {
					height = MIN_FRAME_HEIGHT;
				}
			}
			// int width = MIN_FRAME_WIDTH;
			// int height = MIN_FRAME_HEIGHT;
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;// mFramingRectTop;//
			// Logger.getInstance(TAG).debug("leftOffset:" + leftOffset +
			// "    topOffset:" + topOffset);
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
			// framingRect = new Rect(0, topOffset, leftOffset+leftOffset +
			// width, topOffset + height);
			// Log.d(TAG, "Calculated framing rect: " + framingRect);
		}
		return framingRect;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 */
	public Rect getFramingRectInPreview() {
		if (framingRectInPreview == null) {
			Rect rect = new Rect(getFramingRect());
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			// rect.left = rect.left * cameraResolution.x / screenResolution.x;
			// rect.right = rect.right * cameraResolution.x /
			// screenResolution.x;
			// rect.top = rect.top * cameraResolution.y / screenResolution.y;
			// rect.bottom = rect.bottom * cameraResolution.y /
			// screenResolution.y;
			// rect.left = rect.left * cameraResolution.y / screenResolution.x;
			// rect.right = rect.right * cameraResolution.y /
			// screenResolution.x;
			// rect.top = rect.top * cameraResolution.x / screenResolution.y;
			// rect.bottom = rect.bottom * cameraResolution.x /
			// screenResolution.y;
			framingRectInPreview = rect;
		}
		return framingRectInPreview;
	}

	/**
	 * Converts the result points from still resolution coordinates to screen
	 * coordinates.
	 * 
	 * @param points
	 *            The points returned by the Reader subclass through
	 *            Result.getResultPoints().
	 * @return An array of Points scaled to the size of the framing rect and
	 *         offset appropriately so they can be drawn in screen coordinates.
	 */
	/*
	 * public Point[] convertResultPoints(ResultPoint[] points) { Rect frame =
	 * getFramingRectInPreview(); int count = points.length; Point[] output =
	 * new Point[count]; for (int x = 0; x < count; x++) { output[x] = new
	 * Point(); output[x].x = frame.left + (int) (points[x].getX() + 0.5f);
	 * output[x].y = frame.top + (int) (points[x].getY() + 0.5f); } return
	 * output; }
	 */

	/**
	 * A factory method to build the appropriate LuminanceSource object based on
	 * the format of the preview buffers, as described by Camera.Parameters.
	 * 
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
		/*
		 * Rect rect = getFramingRectInPreview(); int previewFormat =
		 * configManager.getPreviewFormat(); String previewFormatString =
		 * configManager.getPreviewFormatString(); switch (previewFormat) { //
		 * This is the standard Android format which all devices are REQUIRED to
		 * // support. // In theory, it's the only one we should ever care
		 * about. case PixelFormat.YCbCr_420_SP: // This format has never been
		 * seen in the wild, but is compatible as // we only care // about the Y
		 * channel, so allow it. case PixelFormat.YCbCr_422_SP: return new
		 * PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
		 * rect.width(), rect.height()); default: // The Samsung Moment
		 * incorrectly uses this variant instead of the // 'sp' version. //
		 * Fortunately, it too has all the Y data up front, so we can read //
		 * it. if ("yuv420p".equals(previewFormatString)) { return new
		 * PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
		 * rect.width(), rect.height()); } } throw new
		 * IllegalArgumentException("Unsupported picture format: " +
		 * previewFormat + '/' + previewFormatString);
		 */

		// TODO 修改为一下代码,测试http://www.shendu.com/android/rom-818.html me525 cm9系统
		// .
		// 10-08 09:32:33.499: ERROR/AndroidRuntime(5971):
		// java.lang.IllegalArgumentException: Unsupported picture format:
		// 20/yuv422i-yuyv
		Rect rect = getFramingRectInPreview();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.
		return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
	}

	/**
	 * @Title: doSetTorch
	 * @Description: 开启闪光灯
	 * @param @param bl true为开,false为关
	 * @param @return 操作成功返回true
	 */
	public boolean doSetFlash(boolean bl) {
		boolean result = false;
		if (camera != null) {
			Parameters parameters = camera.getParameters();
			String mode = configManager.getFlashMode(parameters, bl);
			if (mode != null) {
				parameters.setFlashMode(mode);
				camera.setParameters(parameters);
				startPreview();
				result = true;
			}
		}
		return result;
	}

	/**
	 * @Title: isFlashOn
	 * @Description: 获取当前闪光灯状态
	 * @return boolean true为开,false为关
	 * @throws
	 */
	public boolean isFlashOn() {
		boolean result = false;
		if (camera != null) {
			Parameters parameters = camera.getParameters();
			String mode = parameters.getFlashMode();
			if (Parameters.FLASH_MODE_ON.equals(mode) || Parameters.FLASH_MODE_TORCH.equals(mode)) {
				result = true;
			} else if (Parameters.FLASH_MODE_OFF.equals(mode)) {
				result = false;
			}
		}
		return result;
	}

	private void printInfo(Camera camera) {
		// get camera info
		Parameters parameters = camera.getParameters();
		Logger.getInstance(TAG)
				.debug("===============================Camera info=====================================");
		Logger.getInstance(TAG).debug("getAntibanding :" + parameters.getAntibanding());
		Logger.getInstance(TAG).debug("getColorEffect :" + parameters.getColorEffect());
		Logger.getInstance(TAG).debug("getExposureCompensation :" + parameters.getExposureCompensation());
		Logger.getInstance(TAG).debug("getExposureCompensationStep :" + parameters.getExposureCompensationStep());
		Logger.getInstance(TAG).debug("getFlashMode :" + parameters.getFlashMode());
		Logger.getInstance(TAG).debug("getFocalLength :" + parameters.getFocalLength());
		Logger.getInstance(TAG).debug("getFocusMode :" + parameters.getFocusMode());
		Logger.getInstance(TAG).debug("getHorizontalViewAngle :" + parameters.getHorizontalViewAngle());
		Logger.getInstance(TAG).debug("getJpegQuality :" + parameters.getJpegQuality());
		Logger.getInstance(TAG).debug("getJpegThumbnailQuality :" + parameters.getJpegThumbnailQuality());
		Logger.getInstance(TAG).debug("getMaxExposureCompensation :" + parameters.getMaxExposureCompensation());
		Logger.getInstance(TAG).debug("getMaxZoom :" + parameters.getMaxZoom());
		Logger.getInstance(TAG).debug("getMinExposureCompensation :" + parameters.getMinExposureCompensation());
		Logger.getInstance(TAG).debug("getPictureFormat :" + parameters.getPictureFormat());
		Logger.getInstance(TAG).debug("getPictureSize :" + parameters.getPictureSize());
		Logger.getInstance(TAG).debug("getSceneMode :" + parameters.getSceneMode());
		Logger.getInstance(TAG).debug("getSupportedAntibanding :" + parameters.getSupportedAntibanding());
		Logger.getInstance(TAG).debug("getSupportedColorEffects :" + parameters.getSupportedColorEffects());
		Logger.getInstance(TAG).debug("getSupportedFlashModes :" + parameters.getSupportedFlashModes());
		Logger.getInstance(TAG).debug("getSupportedFocusModes :" + parameters.getSupportedFocusModes());

		List<Size> supportedJpegThumbnailSizes = parameters.getSupportedJpegThumbnailSizes();
		for (Size size : supportedJpegThumbnailSizes) {
			Logger.getInstance(TAG).debug(
					"getSupportedJpegThumbnailSizes :" + "width:" + size.width + ",height" + size.height);
		}

		List<Integer> supportedPictureFormats = parameters.getSupportedPictureFormats();
		for (int pictureFormats : supportedPictureFormats) {
			Logger.getInstance(TAG).debug("getSupportedPictureFormats :" + pictureFormats);
		}

		List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
		for (Size size : supportedPictureSizes) {
			Logger.getInstance(TAG).debug(
					"getSupportedPictureSizes :" + "width:" + size.width + ",height" + size.height);
		}

		List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
		for (int previewFormats : supportedPreviewFormats) {
			Logger.getInstance(TAG).debug("getSupportedPreviewFormats :" + previewFormats);
		}

		List<Integer> supportedPreviewFrameRates = parameters.getSupportedPreviewFrameRates();
		for (int previewFrameRates : supportedPreviewFrameRates) {
			Logger.getInstance(TAG).debug("getSupportedPreviewFrameRates :" + previewFrameRates);
		}

		List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
		for (Size previewSizes : supportedPreviewSizes) {
			Logger.getInstance(TAG).debug(
					"getSupportedPreviewSizes :" + "width:" + previewSizes.width + ",height" + previewSizes.height);
		}

		List<String> supportedSceneModes = parameters.getSupportedSceneModes();
		for (String sceneModes : supportedSceneModes) {
			Logger.getInstance(TAG).debug("getSupportedSceneModes :" + sceneModes);
		}

		List<String> supportedWhiteBalance = parameters.getSupportedWhiteBalance();
		for (String witeBalance : supportedWhiteBalance) {
			Logger.getInstance(TAG).debug("getSupportedWhiteBalance :" + witeBalance);
		}

		Logger.getInstance(TAG).debug("============================================================================");
	}
}
