/* 
 * @Title: FileUploadException.java
 * Created by liubingsr on 2012-5-16 下午7:57:04 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

/**
 * @ClassName: FileUploadException
 * @Description: 文件上传异常
 * @author liubingsr
 * @date 2012-5-16 下午7:57:04
 * 
 */
public class FileUploadException extends Exception {
	/**
	* @Fields serialVersionUID : TODO(描述这个变量表示什么)
	*/
	private static final long serialVersionUID = 1L;

	public FileUploadException() {
		super();
	}

	public FileUploadException(Exception e) {
		super(e);
	}

	public FileUploadException(String str) {
		super(str);
	}
}
