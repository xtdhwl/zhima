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

package com.zhima.ui.scancode.decoding;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.zhima.ui.scancode.activity.ScanningActivity;

/**
 * This thread does all the heavy lifting of decoding the images.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {
	public static final String BARCODE_BITMAP = "barcode_bitmap";
	/** 默认编码 utf-8 */
	private final static String DEFAULT_CHARSET = "UTF-8";

	private final ScanningActivity activity;
	private final Hashtable<DecodeHintType, Object> hints;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	DecodeThread(ScanningActivity activity,
			Vector<BarcodeFormat> decodeFormats, String characterSet,
			ResultPointCallback resultPointCallback) {

		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);

		hints = new Hashtable<DecodeHintType, Object>(3);

		// // The prefs can't change while the thread is running, so pick them
		// up once here.
		// if (decodeFormats == null || decodeFormats.isEmpty()) {
		// SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(activity);
		// decodeFormats = new Vector<BarcodeFormat>();
		// if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D, true)) {
		// decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
		// }
		// if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
		// decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
		// }
		// if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX,
		// true)) {
		// decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		// }
		// }
		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = new Vector<BarcodeFormat>();
			decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		}

		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
		if (!TextUtils.isEmpty(characterSet)) {
			hints.put(DecodeHintType.CHARACTER_SET, characterSet);
		} else {
			hints.put(DecodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
		}
		hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity, hints);
		handlerInitLatch.countDown();
		Looper.loop();
	}
}
