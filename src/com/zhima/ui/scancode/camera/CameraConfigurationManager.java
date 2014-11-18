/*
 * Copyright (C) 2010 ZXing authors
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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;

import com.zhima.base.logger.Logger;

final class CameraConfigurationManager {

	private static final String TAG = CameraConfigurationManager.class.getSimpleName();

	private static final int TEN_DESIRED_ZOOM = 0;
	private static final int DESIRED_SHARPNESS = 30;

	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

	private final Context mContext;
	private Point mScreenResolution;
	private Point mCameraResolution;
	private int mPreviewFormat;
	private String mPreviewFormatString;

	CameraConfigurationManager(Context context) {
		this.mContext = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 * 
	 * @param screenResolution
	 */
	void initFromCameraParameters(Camera camera, Point screenResolution) {
		// Camera.Parameters parameters = camera.getParameters();
		// previewFormat = parameters.getPreviewFormat();
		// previewFormatString = parameters.get("preview-format");
		// Log.d(TAG, "Default preview format: " + previewFormat + '/' +
		// previewFormatString);
		// WindowManager manager = (WindowManager)
		// context.getSystemService(Context.WINDOW_SERVICE);
		// Display display = manager.getDefaultDisplay();
		// screenResolution = new Point(display.getWidth(),
		// display.getHeight());
		// Log.d(TAG, "Screen resolution: " + screenResolution);
		// cameraResolution = getCameraResolution(parameters, screenResolution);
		// Log.d(TAG, "Camera resolution: " + screenResolution);
		Camera.Parameters parameters = camera.getParameters();
		mPreviewFormat = parameters.getPreviewFormat();
		mPreviewFormatString = parameters.get("preview-format");
		// WindowManager manager = (WindowManager)
		// mContext.getSystemService(Context.WINDOW_SERVICE);
		// Display display = manager.getDefaultDisplay();
		// mScreenResolution = new Point(display.getWidth(),
		// display.getHeight());
		mScreenResolution = screenResolution;
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = mScreenResolution.x;
		screenResolutionForCamera.y = mScreenResolution.y;
		// preview size is always something like 480*320, other 320*480
		if (mScreenResolution.x < mScreenResolution.y) {
			screenResolutionForCamera.x = mScreenResolution.y;
			screenResolutionForCamera.y = mScreenResolution.x;
		}
		mCameraResolution = getCameraResolution(parameters, screenResolutionForCamera);
	}

	/**
	 * Sets the camera up to take preview images which are used for both preview
	 * and decoding. We detect the preview format here so that
	 * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
	 * In the future we may want to force YUV420SP as it's the smallest, and the
	 * planar Y can be used for barcode scanning without a copy in some cases.
	 */
	void setDesiredCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		if (Integer.parseInt(Build.VERSION.SDK) >= 8)
			setDisplayOrientation(camera, 90);
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
		parameters.setPreviewSize(mCameraResolution.x, mCameraResolution.y);
		// setPreviewSize(camera,parameters);
		// setPictureSize(camera,parameters);
		setFlash(parameters);
		setZoom(parameters);
		camera.setParameters(parameters);
	}

	private void setPreviewSize(Camera camera, Camera.Parameters parameters) {
		List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
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
			if (pos < 0)
				pos = 0;
			if (pos >= sizes.size())
				pos = sizes.size() - 1;
			parameters.setPreviewSize(sizes.get(pos).width, sizes.get(pos).height);
		}
		List<Integer> formats = camera.getParameters().getSupportedPreviewFormats();
		if (formats.size() > 0) {
			parameters.setPreviewFormat(formats.get(0));
		}
	}

	private void setPictureSize(Camera camera, Camera.Parameters parameters) {
		List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
		// List<Integer> formats =
		// mCamera.getParameters().getSupportedPictureFormats();
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

		if (pos < 0)
			pos = 0;
		if (pos >= sizes.size())
			pos = sizes.size() - 1;

		parameters.setPictureSize(sizes.get(pos).width, sizes.get(pos).height);
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

	Point getCameraResolution() {
		return mCameraResolution;
	}

	Point getScreenResolution() {
		return mScreenResolution;
	}

	int getPreviewFormat() {
		return mPreviewFormat;
	}

	String getPreviewFormatString() {
		return mPreviewFormatString;
	}

	private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {

		String previewSizeValueString = parameters.get("preview-size-values");
		// saw this on Xperia
		if (previewSizeValueString == null) {
			previewSizeValueString = parameters.get("preview-size-value");
		}

		Point cameraResolution = null;

		if (previewSizeValueString != null) {
			cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
		}
		int x = (screenResolution.x >> 3) << 3, y = (screenResolution.y >> 3) << 3;
		if (cameraResolution == null) {
			// Ensure that the camera resolution is a multiple of 8, as the
			// screen may not be.
			cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
		}
		// int tmp = cameraResolution.x;
		// cameraResolution.x = cameraResolution.y;
		// cameraResolution.y = tmp;
		return cameraResolution;
	}

	private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
		int bestX = 0;
		int bestY = 0;
		int diff = Integer.MAX_VALUE;
		for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

			previewSize = previewSize.trim();
			int dimPosition = previewSize.indexOf('x');
			if (dimPosition < 0) {
				Logger.getInstance(TAG).debug("Bad preview-size: " + previewSize);
				continue;
			}

			int newX;
			int newY;
			try {
				newX = Integer.parseInt(previewSize.substring(0, dimPosition));
				newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
			} catch (NumberFormatException nfe) {
				Logger.getInstance(TAG).debug("Bad preview-size: " + previewSize);
				continue;
			}

			int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
			if (newDiff == 0) {
				bestX = newX;
				bestY = newY;
				break;
			} else if (newDiff < diff) {
				bestX = newX;
				bestY = newY;
				diff = newDiff;
			}

		}

		if (bestX > 0 && bestY > 0) {
			return new Point(bestX, bestY);
		}
		return null;
	}

	private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
		int tenBestValue = 0;
		for (String stringValue : COMMA_PATTERN.split(stringValues)) {
			stringValue = stringValue.trim();
			double value;
			try {
				value = Double.parseDouble(stringValue);
			} catch (NumberFormatException nfe) {
				return tenDesiredZoom;
			}
			int tenValue = (int) (10.0 * value);
			if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
				tenBestValue = tenValue;
			}
		}
		return tenBestValue;
	}

	/**
	* @Title: getFlashMode 
	* @Description: 获取闪光灯模式
	* @param @param parameters 
	* @param @param start true为开,false为关
	* @param @return 支持返回模式,不支持返回空
	 */
	public String getFlashMode(Camera.Parameters parameters, boolean start) {
		String flashMode;
		if (start) {
			//设置闪光灯
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_TORCH,
					Camera.Parameters.FLASH_MODE_ON);
		} else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_OFF);
		}
		return flashMode;
	}

	private String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		return result;
	}

	private void setFlash(Camera.Parameters parameters) {
		// FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
		// And this is a hack-hack to work around a different value on the
		// Behold II
		// Restrict Behold II check to Cupcake, per Samsung's advice
		// if (Build.MODEL.contains("Behold II") &&
		// CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
		if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) { // 3
																				// =
																				// Cupcake
			parameters.set("flash-value", 1);
		} else {
			parameters.set("flash-value", 2);
		}
		// This is the standard setting to turn the flash off that all devices
		// should honor.
		String flashMode = findSettableValue(parameters.getSupportedFlashModes(), Parameters.FLASH_MODE_OFF);
		if(flashMode != null){
			parameters.set("flash-mode", flashMode);
		}
	}

	private void setZoom(Camera.Parameters parameters) {

		String zoomSupportedString = parameters.get("zoom-supported");
		if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
			return;
		}

		int tenDesiredZoom = TEN_DESIRED_ZOOM;

		String maxZoomString = parameters.get("max-zoom");
		if (maxZoomString != null) {
			try {
				int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (NumberFormatException nfe) {
				Logger.getInstance(TAG).debug("Bad max-zoom: " + maxZoomString);
			}
		}

		String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
		if (takingPictureZoomMaxString != null) {
			try {
				int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
				if (tenDesiredZoom > tenMaxZoom) {
					tenDesiredZoom = tenMaxZoom;
				}
			} catch (NumberFormatException nfe) {
				Logger.getInstance(TAG).debug("Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
			}
		}

		String motZoomValuesString = parameters.get("mot-zoom-values");
		if (motZoomValuesString != null) {
			tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
		}

		String motZoomStepString = parameters.get("mot-zoom-step");
		if (motZoomStepString != null) {
			try {
				double motZoomStep = Double.parseDouble(motZoomStepString.trim());
				int tenZoomStep = (int) (10.0 * motZoomStep);
				if (tenZoomStep > 1) {
					tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
				}
			} catch (NumberFormatException nfe) {
				// continue
			}
		}

		// Set zoom. This helps encourage the user to pull back.
		// Some devices like the Behold have a zoom parameter
		if (maxZoomString != null || motZoomValuesString != null) {
			parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
		}

		// Most devices, like the Hero, appear to expose this zoom parameter.
		// It takes on values like "27" which appears to mean 2.7x zoom
		if (takingPictureZoomMaxString != null) {
			parameters.set("taking-picture-zoom", tenDesiredZoom);
		}
	}

	/*
	 * private void setSharpness(Camera.Parameters parameters) {
	 * 
	 * int desiredSharpness = DESIRED_SHARPNESS;
	 * 
	 * String maxSharpnessString = parameters.get("sharpness-max"); if
	 * (maxSharpnessString != null) { try { int maxSharpness =
	 * Integer.parseInt(maxSharpnessString); if (desiredSharpness >
	 * maxSharpness) { desiredSharpness = maxSharpness; } } catch
	 * (NumberFormatException nfe) { Log.w(TAG, "Bad sharpness-max: " +
	 * maxSharpnessString); } }
	 * 
	 * parameters.set("sharpness", desiredSharpness); }
	 */
}
