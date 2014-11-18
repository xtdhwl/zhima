/* 
* @Title: FileUploadConstant.java
* Created by liubingsr on 2012-5-16 下午8:43:35 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.network.uploadfile;

/**
 * @ClassName: FileUploadConstant
 * @Description: 常量定义
 * @author liubingsr
 * @date 2012-5-16 下午8:43:35
 *
 */
public class FileUploadConstant {
	public static final String CONTENT_TYPE = "Content-Type: ";
	public static final String BOUNDARY = "----------------tj2iHyt4Gn9dL0bm7aR6";
	public static final String ITEM_BOUNDARY = "--" + BOUNDARY;
	public final static String NEW_LINE = "\r\n";
	public static final String BODY_END = "--" + BOUNDARY + "--" + NEW_LINE;
}
