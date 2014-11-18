package com.zhima.base.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.zhima.base.logger.Logger;
import com.zhima.base.utils.Base64Helper;

/**
 * @ClassName: zipCompressHelper
 * @Description: zip压缩与解压缩
 * @author liubingsr
 * @date 2012-5-22 上午9:50:25
 * 
 */
public final class zipCompressHelper {
	public final static String TAG = "zipCompressHelper";
	private final static int BUFFERSIZE = 1024;
	private final static String defaultCharsetName = "utf-8";

	public static byte[] compress(byte[] input) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = null;
		byte[] retval = null;
		try {
			bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
			bufos.write(input);
			retval = bos.toByteArray();
		} catch (IOException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			if (bufos != null) {
				try {
					bufos.close();
				} catch (IOException e1) {
					Logger.getInstance(TAG).debug(e1.getMessage(), e1);
				}
			}
			try {
				bos.close();
			} catch (IOException e1) {
				Logger.getInstance(TAG).debug(e1.getMessage(), e1);
			}
		}
		return retval;
	}

	public static byte[] compress(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = null;
		byte[] retval = null;
		try {
			bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
			bufos.write(str.getBytes());
			retval = bos.toByteArray();
		} catch (IOException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			if (bufos != null) {
				try {
					bufos.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
			try {
				bos.close();
			} catch (IOException e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}
		}
		return retval;
	}

	/**
	 * @Title: compress2HexStr
	 * @Description: 压缩成16进制字符串
	 * @param str
	 * @return String
	 */
	public static String compress2Str(String str) {
		byte[] blockcopy = ByteBuffer
	            .allocate(4)
	            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
	            .putInt(str.length())
	            .array();
	    ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());
	    GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);
			gos.write(str.getBytes());
		} catch (IOException e) {
			
		} finally {
			try {
				gos.close();
			} catch (IOException e) {
			}
		    try {
				os.close();
			} catch (IOException e) {
			}
		}
	    
	    byte[] compressed = new byte[4 + os.toByteArray().length];
	    System.arraycopy(blockcopy, 0, compressed, 0, 4);
	    System.arraycopy(os.toByteArray(), 0, compressed, 4, os.toByteArray().length);
	    return Base64Helper.encode(compressed);//StringHelper.byte2HexStr(compressed);
//		byte[] bytes = compress(str);
//		return StringHelper.byte2HexStr(bytes);
//		if (bytes != null) {
//			try {
//				return new String(bytes, defaultCharsetName);
//			} catch (UnsupportedEncodingException e) {
//				Logger.getInstance(TAG).debug(e.getMessage(), e);
//			}
//		}
//		return null;
	}

	/**
	 * @Title: unCompress2String
	 * @Description: 解压成字符串
	 * @param zipStr 源
	 * @return String
	 * @throws IOException
	 */
	public static String unCompressFromStr(String zipStr) throws IOException {		
//		byte[] bytes = StringHelper.hexStr2Bytes(hexStr);
//		return unCompress2String(bytes);
		byte[] compressed = Base64Helper.decode(zipStr);//StringHelper.hexStr2Bytes(zipHexStr);
		if (compressed.length > 4) {
			GZIPInputStream gzipInputStream = new GZIPInputStream(
					new ByteArrayInputStream(compressed, 4,
							compressed.length - 4));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for (int value = 0; value != -1;) {
				value = gzipInputStream.read();
				if (value != -1) {
					baos.write(value);
				}
			}
			gzipInputStream.close();
			baos.close();
			return new String(baos.toByteArray(), defaultCharsetName);
		} else {
			return "";
		}
	}

	public static String unCompress2String(byte[] bytes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		BufferedInputStream bufis = new BufferedInputStream(new GZIPInputStream(bis));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[BUFFERSIZE];
		int len;
		while ((len = bufis.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}

		String retval = bos.toString();
		bis.close();
		bufis.close();
		bos.close();
		return retval;
	}

	public static byte[] unCompress(byte[] bytes) throws IOException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			BufferedInputStream bufis = new BufferedInputStream(
					new GZIPInputStream(bis));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[BUFFERSIZE];
			int len;
			while ((len = bufis.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			return bos.toByteArray();
		} catch (java.lang.OutOfMemoryError e) {
			Logger.getInstance(TAG).debug(e.getMessage());
			return null;
		}
	}
}
