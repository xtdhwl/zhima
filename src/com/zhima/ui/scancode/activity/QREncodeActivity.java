package com.zhima.ui.scancode.activity;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.scancode.encode.QRCodeEncoder;
import com.zhima.ui.scancode.encode.VCardContactEncoder;

/**
 * @ClassName: QRCodeEncoderTest
 * @Description: 生成二维码
 * @author luqilong
 * @date 2012-12-24 上午11:40:07
 */
public class QREncodeActivity extends BaseActivity {

	private static final String TAG = QREncodeActivity.class.getSimpleName();
	private ImageView iv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		ImageView iv = new ImageView(this);
		iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(iv);

		try {
			
//			ArrayList<String> names = new ArrayList<String>();
//			ArrayList<String> addresses = new ArrayList<String>();
//			ArrayList<String> phones = new ArrayList<String>();
//			ArrayList<String> emails = new ArrayList<String>();
//			names.add("姓名_余松霖");
//			addresses.add("地址_北京朝阳区望京阜通东大街6号");
//			phones.add("phone_eruoht");
//			emails.add("yusonglin_@msn.com");
			
//			String[] encode = vCardContactEncoder.encode(names, "公司_北京知迅码爱u哈额瑞虎他热爱圦i鄀", addresses, phones, emails, "url_erut", "备注_备注","工作_工程师","element_1","element_2");
			
			VCardContactEncoder vCardContactEncoder = new VCardContactEncoder();
			String content = vCardContactEncoder.encode("姓名_余松霖", "公司_北京知迅码爱u哈额瑞虎他热爱圦i鄀hiuhiu会", "地址_北京朝阳区望京阜通东大街6号", "phone_eruoht", "yusonglin_@msn.com", "url_erut", "备注_备注","工作_工程师","element_1","element_2");
			
			Bitmap encodeAsBitmap = QRCodeEncoder.encodeAsBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
			iv.setImageBitmap(encodeAsBitmap);
			
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	
//	private void encode() {
//		//		intentEncode();
//		try {
//			//			vcardEncode();
//			Bitmap encodeAsBitmap = encodeAsBitmap(
//					"N:芝麻客知迅码ADR:朝阳望京TEL:01098888EMAIL:anroid@zhima.netURL:http://zhima.netNOTE:开创数码时代",
//					BarcodeFormat.QR_CODE, 400, 400);
//			iv.setImageBitmap(encodeAsBitmap);
//			//			Result result = decodeBitmap(encodeAsBitmap);
//			//			Logger.getInstance(TAG).debug("扫描结果:" + result.getText());
//			//			Logger.getInstance(TAG).debug("扫描结果:" + BarcodeUtils.converStr(result.getText(), "ISO8859_1"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private void vcardEncode() throws UnsupportedEncodingException {
//		VCardContactEncoder vcard = new VCardContactEncoder();
//		String[] encode = vcard.encode(Collections.singleton("芝麻客"), "知迅码", Collections.singleton("朝阳望京"),
//				Collections.singleton("010988888"), Collections.singleton("anroid@zhima.net"), "http://zhima.net",
//				"开创数码时代");
//		String content = encode[0];
//		//		QRCodeEncoder encoder = new QRCodeEncoder(this, content, BarcodeFormat.QR_CODE, 400);
//		try {
//			Logger.getInstance(TAG).debug("原始内容:" + content);
//			//BEGIN:VCARD\nN:芝麻客\n知迅码\nADR:朝阳望京\nTEL:010988888\nEMAIL:anroid@zhima.net\nURL:http://zhima.net\nNOTE:开创数码时代\nEND:VCARD
//			Bitmap bitmap = encodeAsBitmap(
//					"N:芝麻客知迅码ADR:朝阳望京TEL:01098888EMAIL:anroid@zhima.netURL:http://zhima.netNOTE:开创数码时代",
//					BarcodeFormat.QR_CODE, 400, 400);
//			iv.setImageBitmap(bitmap);
//
//			Result result = decodeBitmap(bitmap);
//			//			Logger.getInstance(TAG).debug("扫描结果:" + result.getText());
//			//			Logger.getInstance(TAG).debug("扫描结果:" + BarcodeUtils.converStr(result.getText(), "ISO8859_1"));
//			Logger.getInstance(TAG).debug("扫描结果:" + result.getText());
//
//		} catch (WriterException e) {
//			HaloToast.show(getApplicationContext(), "生成失败");
//			e.printStackTrace();
//		}
//	}
//
//	private void intentEncode() {
//		Intent intent = new Intent();
//		//这里
//		intent.setAction(Intents.Encode.ACTION);
//		//intent.setAction(Intent.ACTION_SEND);
//		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE);
//
//		//	    intent.putExtra(Intents.Encode.DATA, "测试");
//		//	    intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
//		//	    
//		//		intent.putExtra(Intents.Encode.TYPE, QRContents.Type.TEXT);
//		//		intent.putExtra(Intents.Encode.TYPE, QRContents.Type.EMAIL);
//		//		intent.putExtra(Intents.Encode.TYPE, QRContents.Type.PHONE);
//		//		intent.putExtra(Intents.Encode.TYPE, QRContents.Type.SMS);
//
//		intent.putExtra(Intents.Encode.TYPE, QRContents.Type.CONTACT);
//		Bundle bundler = new Bundle();
//		bundler.putString(ContactsContract.Intents.Insert.NAME, "芝麻客");
//		bundler.putString(ContactsContract.Intents.Insert.COMPANY, "知迅码");
//		bundler.putString(ContactsContract.Intents.Insert.POSTAL, "中国朝阳望京");
//
//		bundler.putString(ContactsContract.Intents.Insert.PHONE, "010-888888");
//		bundler.putString(ContactsContract.Intents.Insert.SECONDARY_PHONE, "13800000000");
//		bundler.putString(ContactsContract.Intents.Insert.TERTIARY_PHONE, "138888888");
//
//		bundler.putString(ContactsContract.Intents.Insert.EMAIL, "luqilong@zhima.net");
//		//		bundler.putString(ContactsContract.Intents.Insert.SECONDARY_EMAIL,"");
//		//		bundler.putString(ContactsContract.Intents.Insert.TERTIARY_EMAIL,"");
//
//		bundler.putString(QRContents.URL_KEY, "http://zhima.net");
//		bundler.putString(QRContents.NOTE_KEY, "这是一次测试");
//		//位置
//		//		intent.putExtra(Intents.Encode.TYPE, QRContents.Type.LOCATION);
//		//		bundler.putFloat("LAT", 35.888f);
//		//		bundler.putFloat("LONG", 163.256f);
//
//		intent.putExtra(Intents.Encode.DATA, bundler);
//
//		try {
//			QRCodeEncoder encoder = new QRCodeEncoder(this, intent, 100, true);
//			Bitmap encodeAsBitmap = encoder.encodeAsBitmap(null);
//			iv.setImageBitmap(encodeAsBitmap);
//
//			Result decodeBitmap = decodeBitmap(encodeAsBitmap);
//			Logger.getInstance(TAG).debug("扫描结果:");
//		} catch (WriterException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/** DecodeImageHandler进行解码 */
//	private Result decodeBitmap(Bitmap bitmap) {
//		DecodeImageHandler mDecodehandler = new DecodeImageHandler(this);
//		Result result = mDecodehandler.decode(bitmap);
//		return result;
//	}
//
//	public void file() {
//		//		String str ="test";//二维码内容
//		//		String path = "d:/test";
//		//		ByteMatrix byteMatrix = null;
//		//		try {
//		//		byteMatrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, 200, 200);
//		//		File file = new File(path + “.png”); 
//		//		MatrixToImageWriter.writeToFile(byteMatrix, “png”, file);
//		//		} catch (IOException e) {
//		//		e.printStackTrace();
//		//		} catch (WriterException e1) {
//		//		e1.printStackTrace();
//		//		}
//	}
//
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight)
			throws WriterException {
//		Hashtable<EncodeHintType, Object> hints = null;
//		String encoding = guessAppropriateEncoding(contents);
//		if (encoding != null) {
//			hints = new Hashtable<EncodeHintType, Object>(2);
//			hints.put(EncodeHintType.CHARACTER_SET, encoding);
//		}
		
		Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "GBK");
		
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(contents, format, desiredWidth, desiredHeight, hints);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// All are 0, or black, by default
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}
}

//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.HashMap;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.ImageView;
//import android.widget.ImageView.ScaleType;
//import android.widget.LinearLayout;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.WriterException;
//import com.google.zxing.common.BitMatrix;
//import com.zhima.R;
///**
// * 
// * Android中二维码编码及图像生成
// * 
// */
//public class QREncodeActivity extends Activity {
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
////        ImageView iv = new ImageView(this);
////		iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
////		setContentView(iv);
//        
//        String content = "rl;tke的杀人狂截个图热";
//        final int desiredWidth = 120;
//        final int desiredHeight = 120;
//        final String imageFileName = "orgcent.com.png";
//        FileOutputStream fos = null;
//        Bitmap bitmap = null;
//        try {//生成二维码图像
//            bitmap = encodeAsBitmap(content, BarcodeFormat.QR_CODE, desiredWidth, desiredHeight);
//            if(null != bitmap) {//将二维码图像保存到文件
//                File file = new File(Environment.getExternalStorageDirectory(), imageFileName);
//                fos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(null != fos) {
//                try {
//                    fos.close();
//                } catch (Exception e) {}
//            }
//        }
//
//        //显示QRCode
//        if(null != bitmap) {
//            ImageView iv = new ImageView(this);
//            iv.setImageBitmap(bitmap);
//            iv.setScaleType(ScaleType.FIT_CENTER);
//            setContentView(iv,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        }
//    }
//
//    static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
//            int desiredWidth, int desiredHeight) throws WriterException {
//        final int WHITE = 0xFFFFFFFF; //可以指定其他颜色，让二维码变成彩色效果
//        final int BLACK = 0xFF000000;
//        
//        HashMap<EncodeHintType, String> hints = null;
//        String encoding = guessAppropriateEncoding(contents);
//        if (encoding != null) {
//            hints = new HashMap<EncodeHintType, String>(2);
//            hints.put(EncodeHintType.CHARACTER_SET, encoding);
//        }
//        MultiFormatWriter writer = new MultiFormatWriter();
//        BitMatrix result = writer.encode(contents, format, desiredWidth,
//                desiredHeight, hints);
//        int width = result.getWidth();
//        int height = result.getHeight();
//        int[] pixels = new int[width * height];
//        // All are 0, or black, by default
//        for (int y = 0; y < height; y++) {
//            int offset = y * width;
//            for (int x = 0; x < width; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//            }
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        return bitmap;
//    }
//
//    private static String guessAppropriateEncoding(CharSequence contents) {
//        // Very crude at the moment
//        for (int i = 0; i < contents.length(); i++) {
//            if (contents.charAt(i) > 0xFF) {
//                return "UTF-8";
//            }
//        }
//        return null;
//    }
//}
