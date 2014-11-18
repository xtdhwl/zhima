package com.zhima.base.storage;

import java.io.File;
import java.io.InputStream;

/**
 * @ClassName: FileCache
 * @Description: 文件缓存类(从服务端下载的图片文件使用它存成文件)
 * @author liubingsr
 * @date 2012-6-14 下午4:20:46
 * 
 */
public class FileCache {
	private static final String TAG = "FileCache";

	private final static String TYPE_JPG = "_j";
	private final static String TYPE_PNG = "_p";

	private final static String EXT_JPG = ".jpg";
	private static final String EXT_PNG = ".png";

	private static FileCache mFileCache = null;

	private String mCachePath = null;

	private FileCache() {
		mCachePath = FileHelper.getCacheFileRoot();
		FileHelper.createFolder(mCachePath);
	}

	public static FileCache getInstance() {
		if (mFileCache == null) {
			mFileCache = new FileCache();
		}
		return mFileCache;
	}

	private String makeFileName(String uri) {
		String fileName = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
//		if (fileName.endsWith(EXT_JPG)) {
//			fileName = fileName.substring(0, fileName.length() - EXT_JPG.length());
//			fileName += EXT_JPG;//TYPE_JPG + EXT_JPG;
//		} else if (fileName.endsWith(EXT_PNG)) {
//			fileName = fileName.substring(0, fileName.length() - EXT_PNG.length());
//			fileName += EXT_PNG;//TYPE_PNG + EXT_PNG;
//		}
		return fileName;
	}

	/**
	 * @Title: getCacheFile
	 * @Description: 得到缓存文件
	 * @param uri
	 * @return String 缓存文件路径，null：文件不存在
	 */
	public String getCacheFile(String uri) {
		if (FileHelper.sdCardExist()) {
			if (FileHelper.fileExists(uri)) {
				return uri;
			}
			String fileName = makeFileName(uri);
			createCacheDir();
			fileName = mCachePath + fileName;
			File file = new File(fileName);
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
				return fileName;
			}
		}
		return null;
	}

	/**
	 * @Title: writeCacheFile
	 * @Description: 生成缓存文件
	 * @param uri
	 * @param bytes
	 * @return boolean
	 */
	public boolean writeCacheFile(String uri, byte[] bytes) {
		if (bytes == null || !FileHelper.sdCardExist()) {
			return false;
		}
		String fileName = makeFileName(uri);
		createCacheDir();
		fileName = mCachePath + fileName;
		boolean result = FileHelper.writeFile(fileName, bytes);
		return result;
	}	
	/**
	 * @Title: writeCacheFile
	 * @Description: 生成缓存文件
	 * @param uri
	 *            路径
	 * @param in
	 *            输入流
	 * @return boolean
	 */
	public boolean writeCacheFile(String uri, InputStream in) {
		if (in == null || !FileHelper.sdCardExist()) {
			return false;
		}
		String fileName = makeFileName(uri);
		createCacheDir();
		fileName = mCachePath + fileName;
		return FileHelper.writeFile(fileName, in);
	}

	/**
	 * @Title: deleteCacheFile
	 * @Description: 删除缓存文件
	 * @param uri
	 *            要删除的文件uri
	 * @return void
	 */
	public void deleteCacheFile(String uri) {
		if (FileHelper.sdCardExist()) {
			String fileName = makeFileName(uri);
			fileName = mCachePath + fileName;
			FileHelper.deleteFile(fileName);
		}
	}
	private void createCacheDir() {
		if (!FileHelper.fileExists(mCachePath)) {
			FileHelper.createFolder(mCachePath);
		}
	}
}
