/* 
 * @Title: FileBody.java
 * Created by liubingsr on 2012-5-16 下午8:13:52 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network.uploadfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @ClassName: FileBody
 * @Description: 文件Body
 * @author liubingsr
 * @date 2012-5-16 下午8:13:52
 * 
 */
public class FileBody extends MutilPartBody {
	private final static int BUFFERSIZE = 1024;
	
	public FileBody(String fieldName, String fileName, byte[] data, String contentType) {
		StringBuffer sb = new StringBuffer();
		sb.append("Content-Disposition: form-data; name=\"" + fieldName + "\"");
		sb.append("; filename=\"" + fileName + "\"");
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.CONTENT_TYPE + contentType);
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.NEW_LINE);		

		byte[] headerBytes = sb.toString().getBytes();		
		int len = data.length + headerBytes.length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(len);
		
		byteBuffer.put(headerBytes);
		byteBuffer.put(data);
		
		this.mBytes = byteBuffer.array();
		mLength = this.mBytes.length;
	}
	
	public FileBody(String fieldName, File file, String contentType) throws FileUploadException {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Content-Disposition: form-data; name=\"" + fieldName + "\"");
		sb.append("; filename=\"" + file.getName() + "\"");
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.CONTENT_TYPE + contentType);
		sb.append(FileUploadConstant.NEW_LINE);
		sb.append(FileUploadConstant.NEW_LINE);
		
		ByteBuffer byteBuffer = null;	
		int len = 0;
		FileInputStream ins;
		
		try {
			byte[] headerBytes = sb.toString().getBytes();
			ins = new FileInputStream(file);
			DataInputStream dataInputStream = new DataInputStream(ins);
			len = dataInputStream.available() + headerBytes.length;
			byteBuffer = ByteBuffer.allocate(len);
			byteBuffer.put(headerBytes);
			int bytes = 0;
			byte[] bufferOut = new byte[BUFFERSIZE];
			while ((bytes = dataInputStream.read(bufferOut)) != -1) {
				byteBuffer.put(bufferOut, 0, bytes);
			}
			dataInputStream.close();
			ins.close();
			this.mBytes = byteBuffer.array();
			mLength = this.mBytes.length;
		} catch (FileNotFoundException e) {
			throw new FileUploadException(e);
		} catch (IOException e) {
			throw new FileUploadException(e);
		}
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
			byte[] bytes = getBytes();
			dos.write(bytes, 0, bytes.length);
		} catch (IOException e) {
			throw new FileUploadException(e);
		}
	}
}
