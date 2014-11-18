/*
 * Copyright 2009 ZXing authors
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

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * This object extends LuminanceSource around an array of YUV data returned from
 * the camera driver, with the option to crop to a rectangle within the full
 * data. This can be used to exclude superfluous pixels around the perimeter and
 * speed up decoding.
 * 
 * It works for any pixel format where the Y channel is planar and appears
 * first, including YCbCr_420_SP and YCbCr_422_SP.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class PlanarYUVLuminanceSource extends LuminanceSource {
	private static final String TAG = "PlanarYUVLuminanceSource";
	private final byte[] yuvData;
	private final int dataWidth;
	private final int dataHeight;
	private final int left;
	private final int top;

	public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top, int width,
			int height) {
		super(width, height);

		//		Logger.getInstance(TAG).debug(
		//				"left:" + left + "   width" + width + "    dataWidth:" + dataWidth + "    top:" + top + "  height"
		//						+ height + "  dataHeight" + dataHeight);
		//XXX
//		if (left + width > dataWidth || top + height > dataHeight) {
//			throw new IllegalArgumentException("Crop rectangle does not fit within image data:" + "dataWidth:"
//					+ dataWidth + "dataHeight:" + dataHeight + "left:" + left + "top:" + top + "width:" + width
//					+ "Height:" + height);
//		}

		this.yuvData = yuvData;
		this.dataWidth = dataWidth;
		this.dataHeight = dataHeight;
		this.left = left;
		this.top = top;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("Requested row is outside the image: " + y);
		}
		int width = getWidth();
		if (row == null || row.length < width) {
			row = new byte[width];
		}
		int offset = (y + top) * dataWidth + left;
		System.arraycopy(yuvData, offset, row, 0, width);
		return row;
	}

	@Override
	public byte[] getMatrix() {
		int width = getWidth();
		int height = getHeight();

		// If the caller asks for the entire underlying image, save the copy and
		// give them the
		// original data. The docs specifically warn that result.length must be
		// ignored.
		if (width == dataWidth && height == dataHeight) {
			return yuvData;
		}

		int area = width * height;
		byte[] matrix = new byte[area];
		int inputOffset = top * dataWidth + left;

		// If the width matches the full width of the underlying data, perform a
		// single copy.
		if (width == dataWidth) {
			System.arraycopy(yuvData, inputOffset, matrix, 0, area);
			return matrix;
		}

		// Otherwise copy one cropped row at a time.
		byte[] yuv = yuvData;
		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
			inputOffset += dataWidth;
		}
		return matrix;
	}

	@Override
	public boolean isCropSupported() {
		return true;
	}

	public int getDataWidth() {
		return dataWidth;
	}

	public int getDataHeight() {
		return dataHeight;
	}

	public Bitmap renderCroppedGreyscaleBitmap() {
		int width = getWidth();
		int height = getHeight();
		int[] pixels = new int[width * height];
		byte[] yuv = yuvData;
		int inputOffset = top * dataWidth + left;

		for (int y = 0; y < height; y++) {
			int outputOffset = y * width;
			for (int x = 0; x < width; x++) {
				int grey = yuv[inputOffset + x] & 0xff;
				pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
			}
			inputOffset += dataWidth;
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	public static byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {

		int[] argb = new int[inputWidth * inputHeight];

		scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
		byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
		encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
		scaled.recycle();
		return yuv;
	}

	private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
		final int frameSize = width * height;

		int yIndex = 0;
		int uvIndex = frameSize;

		int a, R, G, B, Y, U, V;
		int index = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {

				a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
				R = (argb[index] & 0xff0000) >> 16;
				G = (argb[index] & 0xff00) >> 8;
				B = (argb[index] & 0xff) >> 0;

				// well known RGB to YUV algorithm
				Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
				U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
				V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

				// NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
				//    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
				//    pixel AND every other scanline.
				yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
				//TODO 越界
//				if (j % 2 == 0 && index % 2 == 0) {
//					yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
//					yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
//				}

				index++;
			}
		}
	}
}
