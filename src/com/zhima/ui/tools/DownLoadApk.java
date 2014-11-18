package com.zhima.ui.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

public class DownLoadApk {
	
	/**
	 * 下载某个path对应的文件
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static File downloadFile(String path,DownLoadNotification downLoadNotification)throws Exception{
		
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		
		int length = conn.getContentLength();
//		pd.setMax(length);
		
		if(conn.getResponseCode()==200){
			InputStream is = conn.getInputStream();
			String fileName = getFileName(path);
			File file = new File(Environment.getExternalStorageDirectory(),fileName);
			FileOutputStream out = new FileOutputStream(file);
			
			byte[] buffer = new byte[1024];
			int len = 0;
			int count = 0;
			int total = 0;
			int flag = 0;
			while((len=is.read(buffer))!=-1){
				out.write(buffer, 0, len);
				count = count+len;
				total=(int)(((double)count/length)*100);
				if(total-flag>=5){
					flag = total;
					downLoadNotification.setProgress(total);
				}
			}
			out.flush();
			out.close();
			is.close();
			return file;
		}
		
		return null;
	}
	
	private static String getFileName(String path){
		String fileName = path.substring(path.lastIndexOf("/")+1);
		return fileName;
	}
}
