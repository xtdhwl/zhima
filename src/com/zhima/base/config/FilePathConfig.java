/* 
* @Title: FilePathConfig.java
* Created by liubingsr on 2012-5-11 下午12:03:24 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.config;

/**
 * @ClassName: FilePathConfig
 * @Description: 文件路径配置
 * @author liubingsr
 * @date 2012-5-11 下午12:03:24
 *
 */
public class FilePathConfig {
	// 媒体库根目录
	public static final String SYS_DCMI_DIR = "/DCIM/zhima/";
	// 根目录
	public static final String FILE_ROOT_DIR = "/zhima/";	
	// 缩略图临时文件路径
	public static final String THUMB_DIR = "thumb/";
	// 临时文件路径
	public static final String TEMPORARY_FILE_DIR = "files/";
	// 缓存文件临时路径
	public static final String FILE_CACHE_DIR = "files/cache/";
	// 永久存储文件路径
	public static final String PERSIST_FILE_DIR = "persist/";
	// 快照临时文件路径
	public static final String SNAPSHOT_DIR = "snapshot/";
	// 录音临时文件路径
	public static final String RECORD_DIR = "record/";
	// crash信息文件名
	public static final String CRASH_FILE_NAME = "crash.txt";
	/**
	 * oauth文件名
	 */
	public static final String OAUTH_FILE = "oauth.txt";
}