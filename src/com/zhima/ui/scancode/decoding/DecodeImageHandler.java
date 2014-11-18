package com.zhima.ui.scancode.decoding;

import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.zhima.ui.scancode.camera.PlanarYUVLuminanceSource;
import com.zhima.ui.scancode.camera.RGBLuminanceSource;

/**
 * @ClassName: DecodeImageHandler
 * @Description: 解析图片二维码
 * @author luqilong
 * @date 2012-11-20 下午1:13:15
 */
public class DecodeImageHandler {
	private static final String TAG = DecodeImageHandler.class.getSimpleName();
	// 解码格式
	private MultiFormatReader multiFormatReader;
	private static final String ISO88591 = "ISO-8859-1";

	// private Context mContext;

	public DecodeImageHandler(Context context) {
		// 解码的参数
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
		// 能解析的编码类型 和 解析时使用的编码。
		Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
		decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
		hints.put(DecodeHintType.CHARACTER_SET, ISO88591);
		init(context, hints);

	}

	public DecodeImageHandler(Context context, Hashtable<DecodeHintType, Object> hints) {
		init(context, hints);
	}

	private void init(Context context, Hashtable<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		// mContext = context;
	}

	public Result decode(Bitmap bitmap) {
		// 首先，要取得该图片的像素数组内容
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		//--------------------------------------------------
		//rgb模式
		int[] data = new int[width * height];
		bitmap.getPixels(data, 0, width, 0, 0, width, height);
		Result rgbResult = rgbModeDecode(data, width, height);
		if (rgbResult != null) {
			data = null;
			return rgbResult;
		}

		//----------------------------------------------------
		//yuv
		byte[] bitmapPixels = new byte[width * height];
		bitmap.getPixels(data, 0, width, 0, 0, width, height);
		// 将int数组转换为byte数组
		for (int i = 0; i < data.length; i++) {
			bitmapPixels[i] = (byte) data[i];
		}
		//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		Result yuvResult = yuvModeDecode(bitmapPixels, width, height);
		bitmapPixels = null;
		return yuvResult;
	}

	//	public Result decode(String path) throws IOException {
	//		// 解析图片高和宽
	//		BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inJustDecodeBounds = true;
	//		BitmapFactory.decodeFile(path, options);
	//
	//		//从图片直接获取byte[]
	//		File file = new File(path);
	//		FileInputStream is = new FileInputStream(file);
	//		ByteArrayOutputStream os = new ByteArrayOutputStream();
	//		int len = -1;
	//		byte[] buf = new byte[512];
	//		while ((len = is.read(buf)) != -1) {
	//			os.write(buf, 0, len);
	//		}
	//		//关闭流
	//		try {
	//			is.close();
	//		} finally {
	//			if (is != null) {
	//				is.close();
	//			}
	//		}
	//
	//		//解析
	//		return decode(os.toByteArray(), options.outWidth, options.outHeight);
	//	}

	public Result rgbModeDecode(int[] data, int width, int height) {
		Result rawResult = null;
		RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			rawResult = multiFormatReader.decodeWithState(bitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			multiFormatReader.reset();
		}
//
//		//转换乱码
//		if (rawResult != null) {
//			return converResult(rawResult);
//		}
		return rawResult;
	}

	public Result yuvModeDecode(byte[] data, int width, int height) {
		Result rawResult = null;
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			rawResult = multiFormatReader.decodeWithState(bitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			multiFormatReader.reset();
		}

		//转换乱码
//		if (rawResult != null) {
//			return converResult(rawResult);
//		}
		return rawResult;
	}

//	/**
//	 * 使用ISO88591进行解码，然后通过ISO88591在进行转换乱码
//	 */
//	private Result converResult(Result rawResult) {
//		//复制一个Result,并转码
//		String str = rawResult.getText();
//		String converText = null;
//		try {
//			converText = BarcodeUtils.converStr(str, ISO88591);
//		} catch (UnsupportedEncodingException e) {
//			Logger.getInstance(TAG).debug(e.toString());
//		}
//
//		//		FIXME 转化失败--》1:结果置空
//		//		             --》2:把未解码的内容返回
//		if (converText != null) {
//			return serResultText(rawResult, converText);
//		} else {
//			return rawResult;
//		}
//	}

//	private Result serResultText(Result rawResult, String converText) {
//		Result resultResult = new Result(converText, rawResult.getRawBytes(), rawResult.getResultPoints(),
//				rawResult.getBarcodeFormat(), System.currentTimeMillis());
//		resultResult.putAllMetadata(rawResult.getResultMetadata());
//		return resultResult;
//	}
	
	public String getCharacter(){
		return ISO88591;
	}

}
