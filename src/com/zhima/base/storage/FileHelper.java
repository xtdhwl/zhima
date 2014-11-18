/* 
* @Title: FileHelper.java
* Created by liubingsr on 2012-6-13 下午2:17:24 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.zhima.base.config.FilePathConfig;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: FileHelper
 * @Description: 文件辅助类
 * @author liubingsr
 * @date 2012-6-13 下午2:17:24
 *
 */
public class FileHelper {
	private final static String TAG = "FileHelper";
	private static int BUFFERSIZE = 1024;
	
	/**
	* @Title: deleteFile
	* @Description: 删除单个文件
	* @param file
	* @return void
	*/
	public static void deleteFile(String file) {
		if (!sdCardExist()) {
			return;
		}
		File f = new File(file);
		if (f != null && f.exists()) {
			f.delete();
		}
	}
	/**
	* @Title: createFolder
	* @Description: 新建文件夹
	* @param folderPath
	* @return
	* boolean
	*/
	public static boolean createFolder(String folderPath) {
		if (!sdCardExist()) {
			return false;
		}
		if (!TextUtils.isEmpty(folderPath)) {
			File dir = new File(folderPath);
			if (!dir.exists()) {
				return dir.mkdirs();
			}
		}
		return  false;
	}
	/**
	* @Title: deleteFolder
	* @Description: 删除文件夹(递归删除此文件下所有文件)
	* @param rootFolderPath
	* @return void
	*/
	public static void deleteFolder(String rootFolderPath) {
		if (!sdCardExist()) {
			return;
		}
		File f = new File(rootFolderPath);
		if (f == null || !f.exists()) {
			return;
		}
		if (!f.delete()) {
			File[] fs = f.listFiles();
			for (int index = 0; index < fs.length; index++) {
				if (fs[index].isDirectory()) {
					deleteFolder(fs[index].getAbsolutePath());
				} else {
					fs[index].delete();
				}
			}
			f.delete();
		}
	}
	public static boolean fileExists(String fileFullName) {
		if (!sdCardExist()) {
			return false;
		}
		File f = new File(fileFullName);
		return f != null && f.exists();
	}
	/**
	* @Title: getRootDir
	* @Description: zhimaApp文件根目录
	* @return
	* String
	*/
	public static String getRootDir() {		
		if (sdCardExist()) {
			String root = null;
			File sdDir = null;
			sdDir = Environment.getExternalStorageDirectory();
			root = sdDir.toString() + FilePathConfig.FILE_ROOT_DIR;
			createFolder(root);
			return root;
		} else {
			return null;
		}		
	}
	
	/**
	* @Title: getCacheFileRoot
	* @Description: 缓存文件根目录
	* @return String
	*/
	public static String getCacheFileRoot() {
		String root = getRootDir();
		if (!TextUtils.isEmpty(root)) {
			root += FilePathConfig.FILE_CACHE_DIR;
			File f = new File(root);
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		return root;
	}	
	/**
	* @Title: getSysDcmiPath
	* @Description: 媒体库文件根目录
	* @param fileName
	* @return String
	*/
	public static String getSysDcmiPath(String fileName) {		
		if (sdCardExist()) {
			String root = null;
			File sdDir = null;
			sdDir = Environment.getExternalStorageDirectory();
			root = sdDir.toString() + FilePathConfig.SYS_DCMI_DIR;
			File out = new File(root);
			if (!out.exists()) {
				out.mkdirs();
			}
			return root + fileName;
		} else {
			return null;
		}
	}
	
	/**
	* @Title: getPersistFilePath
	* @Description: 得到持久化文件跟根路径
	* @return String
	*/
	public static String getPersistFilePath() {
		String rootPath = getRootDir();
		if (!TextUtils.isEmpty(rootPath)) {
			String filePath = rootPath + FilePathConfig.PERSIST_FILE_DIR;
			File out = new File(filePath);
			if (!out.exists()) {
				out.mkdirs();
			}
			return filePath;
		} else {
			return null;
		}		
	}
	private static String getFilePathForGroup(String root) {
		if (!root.endsWith("/"))
			root += "/";
		File f = new File(root);
		if (!f.exists()) {
			f.mkdirs();
		}
		return root;
	}
	
	/**
	* @Title: getThumbFullPath
	* @Description: 缩略图文件名
	* @param fileName
	* @return String
	*/
	public static String getThumbFullPath(String fileName) {
		String rootPath = getRootDir() + FilePathConfig.THUMB_DIR;
		rootPath = getFilePathForGroup(rootPath);
		return rootPath + fileName;
	}

	public static String getFileFullPath(String fileName) {
		String rootPath = getRootDir();
		if (!TextUtils.isEmpty(rootPath)) {
			rootPath += FilePathConfig.TEMPORARY_FILE_DIR;
			rootPath = getFilePathForGroup(rootPath);
			return rootPath + fileName;
		} else {
			return null;
		}		
	}
	/**
	* @Title: getCacheFileFullPath
	* @Description: 缓存文件名
	* @param fileName
	* @return String
	*/
	public static String getCacheFileFullPath(String fileName) {
		String rootPath = getCacheFileRoot();
		if (!TextUtils.isEmpty(rootPath)) {
			return rootPath + fileName;
		} else {
			return null;
		}	
	}	
	/**
	* @Title: getSnapshotFullPath
	* @Description: 快照文件名
	* @param fileName
	* @return
	* String
	*/
	public static String getSnapshotFullPath(String fileName) {
		String rootPath = getRootDir();
		if (!TextUtils.isEmpty(rootPath)) {
			rootPath += FilePathConfig.SNAPSHOT_DIR;
			File out = new File(rootPath);
			if (!out.exists()) {
				out.mkdirs();
			}
			return rootPath + fileName;
		} else {
			return null;
		}
	}
	/**
	* @Title: getRecordFullPath
	* @Description: 录音文件名
	* @param fileName
	* @return
	* String
	*/
	public static String getRecordFullPath(String fileName) {
		String rootPath = getRootDir();
		if (!TextUtils.isEmpty(rootPath)) {
			rootPath += FilePathConfig.RECORD_DIR;
			File out = new File(rootPath);
			if (!out.exists()) {
				out.mkdirs();
			}
			return rootPath + fileName;
		} else {
			return null;
		}
	}
	/**
	* @Title: getFileName
	* @Description: 从全路径中得到文件名
	* @param fullPath
	* @return
	* String
	*/
	public static String getFileName(String fullPath) {
		if (TextUtils.isEmpty(fullPath)) {
			return fullPath;
		}
		String fileName = "";
		int lastSeperator = fullPath.lastIndexOf('/');
		if (lastSeperator >= 0 && fullPath.length() > lastSeperator + 1) {
			fileName = fullPath.substring(lastSeperator + 1);
		} else {
			fileName = fullPath;
		}
		return fileName;
	}
	/**
	* @Title: getFileExtension
	* @Description: 取文件扩展名
	* @param fullPath
	* @return
	* String
	*/
	public static String getFileExtension(String fullPath) {
		if (TextUtils.isEmpty(fullPath)) {
			return fullPath;
		}
		int pos = fullPath.lastIndexOf('.');
        if ((pos > 0) && (pos < (fullPath.length() - 1))) {
            return fullPath.substring(pos + 1);
        }
        return fullPath;
	}
	/**
	* @Title: sdCardExist
	* @Description: 检测手机是否存在sd card
	* @return boolean
	*/
	public static boolean sdCardExist() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}
	
	/**
	* @Title: getMIME
	* @Description: 根据文件扩展名返回对应的MIME
	* @param path 文件名
	* @return String
	*/
	public static String getMIME(String path) {
		if (TextUtils.isEmpty(path)) {
			return "application/octet-stream";
		}
		int pos = path.lastIndexOf(".");
		if (pos > 0 && pos <= (path.length() - 1)) {
			String extension = path.substring(pos + 1).toLowerCase();
			if (extension.equals("gz")) {
				return "application/x-gzip";
			} else if (extension.equals("txt")) {
				return "text/plain";
			} else if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe")) {
				return "image/jpeg";
			} else if (extension.equals("gif")) {
				return "image/gif";
			} else if (extension.equals("png")) {
				return "image/png";
			} else if (extension.equals("mp3")) {
				return "audio/mpeg";
			} else if (extension.equals("mpg") || extension.equals("mpeg") || extension.equals("mpe")) {
				return "video/mpeg";
			} else if (extension.equals("pdf")) {
				return "application/pdf";
			} else if (extension.equals("z")) {
				return "application/x-compress";
			} else if (extension.equals("zip")) {
				return "application/x-zip-compressed";
			} else if (extension.equals("wav")) {
				return "audio/x-wav";
			} else if (extension.equals("aac")) {
				return "audio/aac";
			} else if (extension.equals("m4a")) {
				return "audio/m4a";
			} else if (extension.equals("m4v")) {
				return "video/mp4";
			}
		}
		return "application/octet-stream";
	}
	/**
	* @Title: isImageFile
	* @Description: 是否是图片文件
	* @param filePath
	* @return
	* boolean
	 */
	public static boolean isImageFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		int pos = filePath.lastIndexOf(".");
		if (pos > 0 && pos <= (filePath.length() - 1)) {
			String extension = filePath.substring(pos + 1).toLowerCase();
			if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe")) {
				return true;
			} else if (extension.equals("png")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**
	* @Title: readStreamToByte
	* @Description: 读文件
	* @param is
	* @return
	* byte[]
	*/
	public static byte[] readStreamToByte(InputStream is) {
		byte[] result = null;
		try {
			result = new byte[is.available()];
			is.read(result);
		} catch (IOException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return null;
		}
		return result;
	}
	/**
	* @Title: getFileBytes
	* @Description: 读文件
	* @param fileUri
	* @return
	* byte[]
	*/
	public static byte[] getFileBytes(Uri fileUri) {
		if (fileUri == null) {
			return null;
		}
		return getFileBytes(fileUri.getPath());
	}
	/**
	* @Title: getFileBytes
	* @Description: 读取文件字节流
	* @param fullPath
	* @return byte[]
	*/
	public static byte[] getFileBytes(String fullPath) {
		if (TextUtils.isEmpty(fullPath)) {
			return null;
		}
		byte[] result = null;
		try {
            InputStream is = new FileInputStream(new File(fullPath));            
            result = readStreamToByte(is);
            is.close();
        } catch (FileNotFoundException e) {
        	Logger.getInstance(TAG).debug(e.getMessage(), e);
        } catch (IOException e) {
        	Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		return result;
	}
	/**
	* @Title: writeFile
	* @Description: 写文件
	* @param fullPath
	* @param bytes
	* @return
	* boolean
	*/
	public static boolean writeFile(String fullPath, byte[] bytes) {
		boolean result = false;
		try {
			FileOutputStream os = new FileOutputStream(fullPath);
			os.write(bytes, 0, bytes.length);
			os.close();
			result = true;
		} catch (Exception e) {
			result = false;
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		return result;	
	}
	/**
	* @Title: writeFile
	* @Description: 写文件
	* @param fullPath
	* @param in
	* @return
	* boolean
	*/
	public static boolean writeFile(String fullPath, InputStream in) {
		boolean result = false;
		int len = 0;
		byte[] buffer = new byte[BUFFERSIZE];
		try {
			FileOutputStream os = new FileOutputStream(fullPath);
			while ((len = in.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			os.close();
			result = true;
		} catch (Exception e) {
			result = false;
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * @Title: rename
	 * @Description: 修改文件名
	 * @param oldFilePath
	 * @param newFilePath
	 * @return
	 */
	public static boolean rename(String oldFilePath, String newFilePath) {
		File oldfile = new File(oldFilePath);
		String rootPath = oldfile.getParent();
		File newFile = new File(rootPath + File.separator + SystemConfig.UPDATE_APK);

		if (oldfile.renameTo(newFile)){
			return true;
		} else{
			return false;
		}
	}
	/**
	* @Title: copyFile
	* @Description: 文件复制
	* @param srcFilePath
	* @param destFilePath
	* @return true 成功，false 失败
	*/
	public static boolean copyFile(String srcFilePath, String destFilePath) {
		try {
			File srcFile = new File(srcFilePath);
			File destFile = new File(destFilePath);
			InputStream in = new FileInputStream(srcFile);
			OutputStream out = new FileOutputStream(destFile);

			byte[] buf = new byte[BUFFERSIZE];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (IOException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		return false;
	}

	public static String convertStreamToString(InputStream is, String encoding) throws IOException {
		String line = null;
		if (is != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
				line = reader.readLine();
			} finally {
				is.close();
			}
		}
		return (line == null) ? "" : line;
	}
}
