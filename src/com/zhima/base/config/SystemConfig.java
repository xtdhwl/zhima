/* 
 * @Title: SystemConfig.java
 * Created by liubingsr on 2012-5-11 下午12:17:42 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.config;

/**
 * @ClassName: SystemConfig
 * @Description: 系统杂项配置
 * @author liubingsr
 * @date 2012-5-11 下午12:17:42
 * 
 */
public final class SystemConfig {
	public static final String PLATFORM = "android";
	/**
	 * 渠道号关键字
	 */
	public final static String CHANNEL_KEY = "UMENG_CHANNEL";
	/**
	 * 启用debug。当启用debug模式时记录log，否则不记log。正式发布时需要修改成 false
	 */
	public final static boolean DEBUG = true;
	/**
	 * 是否上传crash信息。正式发布时需要修改成 true
	 */
	public final static boolean UPLOAD_CRASH_INFO = true;
	/**
	 * 是否启用统计功能
	 */
	public final static boolean STATS_ENABLE = !DEBUG;
	/**
	 * 固定不变的url
	 */
	public final static String FIXED_SERVER_URL = "http://mrest.zhima.net/";//mrest.zhima.net "http://172.16.16.14:8080/rest/"
	/**
	 * 最大任务数量
	 */
	public final static int MAX_TASK_SIZE = 5;
	/**
	 * 页条目数
	 */
	public final static int PAGE_SIZE = 20;
	/**
	 * viewPlugin 显示预览的个数
	 */
	public final static int VIEW_PLUGIN_PREVIEW_COUNT = 3;
	/**
	 * viewPlugin item最大个数
	 */
	public final static int VIEW_PLUGIN_LIST_MAX_COUNT = 20;
	/**
	 * viewPlugin hread焦点图个数
	 */
	public final static int VIEW_PLUGIN_HEADER_MAX_COUNT = 5;
	/**
	 * 图片压缩质量
	 */
	public final static int IMAGE_QUALITY = 70;
	/**
	 * bitmap缓存数量
	 */
	public final static int IMAGE_CACHE_SIZE = 20;
	/**
	 * token过期时间（单位天，选填，默认值为7天）
	 */
	public final static int DEFAULT_TOKEN_EXPIRE_DAYS = 7;
	/**
	 * Ping间隔时间。单位：秒
	 */
	public final static int PING_INTERVAL = 60;
	/**
	 * Map_key
	 */
	public static String MAP_KEY = "";

	/**
	 * 定位自动关闭时间：40秒
	 */
	public final static int LOCATION_CLOSE_TIME = 1000 * 40;
	/**
	 * 定位超时时间:10分钟;
	 */
	public final static long LOCATION_TIMEOUT = 1000 * 60 * 20;
	/**
	 *  升级包文件名
	 */
	public final static String UPDATE_APK = "update.apk";
	
	public static boolean IS_NEED_GET_MYSELF = false;
	/**
	 * 推荐空间数目
	 */
	public final static int RECOMMEND_COUNT = 20;
}
