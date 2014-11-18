/* 
 * @Title: BytesBody.java
 * Created by liubingsr on 2012-5-16 下午8:10:34 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ClassName: BytesBody
 * @Description: 字节流Body
 * @author liubingsr
 * @date 2012-5-16 下午8:10:34
 * 
 */
public class BytesBody extends MutilPartBody {
	// private static final String CONTENT_TYPE =
	// "Content-Type: application/octet-stream";

	BytesBody(String fieldName, byte[] bodyBytes, String contentType) {
		StringBuffer sb = new StringBuffer();
		sb.append("Content-Disposition: form-data; name=\"" + fieldName + "\"");
		sb.append("; filename=\"\"");
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.CONTENT_TYPE + contentType);
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.NEW_LINE);
		
		byte[] headerBytes = sb.toString().getBytes();		
		int len = bodyBytes.length + headerBytes.length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(len);
		
		byteBuffer.put(headerBytes);
		byteBuffer.put(bodyBytes);
		this.mBytes = byteBuffer.array();
		mLength = this.mBytes.length;
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
	public void write(DataOutputStream dos) throws FileUploadException {
		try {
			dos.write(mBytes, 0, mBytes.length);
		} catch (IOException e) {
			throw new FileUploadException(e);
		}
	}
}
