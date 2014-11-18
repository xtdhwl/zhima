/* 
 * @Title: StringBody.java
 * Created by liubingsr on 2012-5-16 下午8:02:43 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @ClassName: StringBody
 * @Description: 字符串内容
 * @author liubingsr
 * @date 2012-5-16 下午8:02:43
 * 
 */
public class StringBody extends MutilPartBody {
	
	public StringBody(String fieldName, String content, String encoding)
			throws FileUploadException {
		StringBuffer sb = new StringBuffer();
		sb.append("Content-Disposition: form-data; name=\"" + fieldName + "\"");
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.CONTENT_TYPE + "text/plain");
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.NEW_LINE);
		byte[] sb_bytes;
		try {
			sb_bytes = sb.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new FileUploadException(e);
		}
		byte[] c_bytes = content.getBytes();
		mLength = sb_bytes.length + c_bytes.length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(mLength);
		byteBuffer.put(sb_bytes);
		byteBuffer.put(c_bytes);
		mBytes = byteBuffer.array();
	}

	/*
	 * (非 Javadoc) <p>Title: write</p> <p>Description: </p>
	 * 
	 * @param os
	 * 
	 * @throws FileUploadException
	 * 
	 * @see
	 * com.zhima.base.network.uploadFile.MutilPartBody#write(java.io.DataOutputStream
	 * )
	 */
	@Override
	public void write(DataOutputStream os) throws FileUploadException {
		try {
			byte[] bytes = getBytes();
			os.write(bytes, 0, bytes.length);
		} catch (IOException e) {
			throw new FileUploadException(e);
		}
	}
}
