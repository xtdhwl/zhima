package com.zhima.base.gdi.filter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * @ClassName: LomoFilter
 * @Description: Lomo滤镜
 * @author liubingsr
 * @date 2012-6-25 上午10:10:09
 * 
 */
public class LomoFilter extends ImageFilter {

	public LomoFilter(Bitmap bitmap) {
		initFilter(bitmap, Config.RGB_565);
	}

	@Override
	public void process() {
		Bitmap bitmap = this.getImage();
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		float scaleValue = 95 * 1.0F / 127;

		ColorMatrix scaleMatrix = new ColorMatrix();
		scaleMatrix.reset();
		scaleMatrix.setScale((float) (scaleValue + 0.2),
				(float) (scaleValue + 0.4), (float) (scaleValue + 0.2), 1);

		ColorMatrix satMatrix = new ColorMatrix();
		satMatrix.reset();
		satMatrix.setSaturation(0.85f);

		ColorMatrix hueMatrix = new ColorMatrix();
		hueMatrix.reset();
		hueMatrix.setRotate(0, 5);
		hueMatrix.setRotate(1, 5);
		hueMatrix.setRotate(2, 5);

		ColorMatrix allMatrix = new ColorMatrix();
		allMatrix.reset();
		allMatrix.postConcat(scaleMatrix);
		allMatrix.postConcat(satMatrix);
		// allMatrix.postConcat(hueMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(allMatrix));
		canvas.drawBitmap(bitmap, 0, 0, paint);

		double radius = (double) (mWidth / 2) * 95 / 100;
		double centerX = mWidth / 2f;
		double centerY = mHeight / 2f;

		int pixels[] = this.getColorArray();

		int currentPos;
		double pixelsFalloff = 3.5;
		for (int y = 0; y < mHeight; y++) {
			for (int x = 0; x < mWidth; x++) {
				double dis = Math.sqrt(Math.pow((centerX - x), 2)
						+ Math.pow(centerY - y, 2));
				currentPos = y * mWidth + x;

				if (dis > radius) {
					int pixelRed = (pixels[currentPos] >> 16) & 0xFF;
					int pixelGreen = (pixels[currentPos] >> 8) & 0xFF;
					int pixelBlue = pixels[currentPos] & 0xFF;

					double scaler = Math.abs(1 - Math.pow(
							(double) ((dis - radius) / pixelsFalloff), 2));

					int newRed = (int) (pixelRed - scaler);
					int newGreen = (int) (pixelGreen - scaler);
					int newBlue = (int) (pixelBlue - scaler);

					newRed = Math.min(255, Math.max(0, newRed));
					newGreen = Math.min(255, Math.max(0, newGreen));
					newBlue = Math.min(255, Math.max(0, newBlue));

					pixels[currentPos] = (255 << 24) | (newRed << 16)
							| (newGreen << 8) | newBlue;

				}
			}
		}
		copyPixelsFromBuffer();
	}
}
