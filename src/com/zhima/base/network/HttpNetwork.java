/* 
 * @Title: HttpNetwork.java
 * Created by liubingsr on 2012-5-15 下午2:35:03 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;

import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ZMHttpHeader;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.uploadfile.FileBody;
import com.zhima.base.network.uploadfile.FileUploadConstant;
import com.zhima.base.network.uploadfile.FileUploadException;
import com.zhima.base.network.uploadfile.FormFile;
import com.zhima.base.network.uploadfile.MutilPartBody;
import com.zhima.base.network.uploadfile.MutilPartEntity;
import com.zhima.base.network.uploadfile.StringBody;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.AppLaunchService;

/**
 * @ClassName: HttpNetwork
 * @Description: http协议实现类
 * @author liubingsr
 * @date 2012-5-15 下午2:35:03
 * 
 */
public class HttpNetwork implements INetwork {
	private final static String TAG = HttpNetwork.class.getSimpleName();	
	/**
	 *  缓冲区大小
	 */
	private final static int BUFFERSIZE = 1024;
	/**
	 * 网络超时时间。毫秒
	 */
	private final static int TIMEOUT_CONNECTION = 90 * 1000;
	/**
	 * 重试次数
	 */
	private final static int TRY_COUNT = 3;
	/**
	 * http head
	 */
	private final static Map<String,String> httpHeaders;
	private static HttpNetwork instance = null;
	private Context mContext;
	
	static {
		httpHeaders = new HashMap<String,String>();
		httpHeaders.put("User-Agent", ZMHttpHeader.UA_VALUE);
		httpHeaders.put("Accept-Language", "zh-cn,zh;q=0.5");
		httpHeaders.put("Accept-Charset", "GBK,GB2312,utf-8;q=0.7,*;q=0.7");
		httpHeaders.put("Accept", "*/*");
		httpHeaders.put("connection", "Keep-Alive");		
	}
	
	public HttpNetwork(Context context) {
		mContext = context;
		httpHeaders.put(ZMHttpHeader.ZM_PLATFORM, SystemConfig.PLATFORM);
		httpHeaders.put(ZMHttpHeader.ZM_INSTALLED_VERSION, AppLaunchService.getInstance(context).getVersionName());
	}

	public static HttpNetwork getInstance(Context context) {
		if (instance == null) {
			instance = new HttpNetwork(context);			
		}
//		instance.mContext = context;
		return instance;
	}	
	
	/*(非 Javadoc)
	* <p>Title: sendRequest</p>
	* <p>Description: </p>
	* @param info
	* @see com.zhima.base.network.INetwork#sendRequest(com.zhima.base.network.RequestInfo)
	*/
	@Override
	public void sendRequest(RequestInfo info) {
		if (!NetUtils.isNetworkAvailable(mContext)) {
			info.setResultCode(ErrorManager.NOT_NETWORK);
			Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + ">network is available");
			return;
		}
		RequestInfo.RequestType requestType = info.getRequestType();
		if (!TextUtils.isEmpty(AccountService.getInstance(mContext).getZMAccessToken())) {
			httpHeaders.put(ZMHttpHeader.ZM_ACCESS_TOKEN_HEADER, AccountService.getInstance(mContext).getZMAccessToken());
		}
		switch(requestType) {
		case UPLOAD:
			uploadFile(info);
			break;
		case DOWNLOAD:
			doDownloadFile(info);
			break;
		case GET:
			sendGetRequest(info);
			break;
		case POST:
			sendPostRequest(info);
			break;
		case DELETE:
			sendDeleteRequest(info);
			break;
		case PUT:
			sendPutRequest(info);
			break;
		default:
			sendGetRequest(info);
			break;
		}
	}
	/**
	* @Title: uploadFile
	* @Description: 带文件附件的请求
	* @param info
	* @return void
	*/
	private void uploadFile(RequestInfo info) {
		if (info.getFormFieldParams() == null && info.getUploadFileList() == null) {
			info.setResultCode(ErrorManager.LACKOFDATA_ERROR);
//			Logger.getInstance(TAG).debug(ErrorManager.getErrorDescByCode(ErrorManager.LACKOFDATA_ERROR));
			return;
		}
		MutilPartEntity entity = new MutilPartEntity();
		// 添加文本部分
		if (info.getFormFieldParams() != null && !info.getFormFieldParams().isEmpty()) {
			Set<Map.Entry<String, String>> entrySet = info.getFormFieldParams().entrySet();
			for (Map.Entry<String, String> entry : entrySet) {
				try {
					StringBody strBody = new StringBody(entry.getKey(), entry.getValue(), info.getCharset());
					entity.addBody(strBody);
				} catch (FileUploadException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}
		// 添加文件部分
		if (info.getUploadFileList() != null && !info.getUploadFileList().isEmpty()) {
			for (FormFile formFile : info.getUploadFileList()) {
				if (formFile.getData() == null) {
					info.setResultCode(ErrorManager.DATAFORMAT_ERROR);
//					Logger.getInstance(TAG).debug("url:<" + info.getUrl() + ">,formFile<" + formFile.getFileName() + "> is null");
					return;
				}
				FileBody fileBody = new FileBody(formFile.getFieldName(), formFile.getFileName(), formFile.getData(), formFile.getContentType());
				fileBody.getLength();
				entity.addBody(fileBody);
			}
		}
		// URL
		URL url = null;
		try {
			url = new URL(info.getUrl());
		} catch (MalformedURLException e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug("url:<" + info.getUrl() + ">不是完整有效的网址," + e.getMessage(), e);
			return;
		}
		// 建立网络连接
		HttpURLConnection conn = null;
		try {
			HttpHost proxyHost = ProxySettings.getProxyHost();
			if (proxyHost != null) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost.getHostName(), proxyHost.getPort()));
				conn = (HttpURLConnection) url.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod("POST");
		} catch (IOException e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug("与<" + info.getUrl() + ">建立httpconnection失败，" + e.getMessage(), e);
			return;
		}

		List<MutilPartBody> bodyList = entity.getBodyList();

		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setReadTimeout(TIMEOUT_CONNECTION);
		conn.setConnectTimeout(TIMEOUT_CONNECTION);
		conn.setRequestProperty("User-Agent", ZMHttpHeader.UA_VALUE);
		conn.setRequestProperty("Accept", "*/*");
//		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty(ZMHttpHeader.ZM_PLATFORM, SystemConfig.PLATFORM);
		conn.setRequestProperty(ZMHttpHeader.ZM_INSTALLED_VERSION, AppLaunchService.getInstance(mContext).getVersionName());
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + FileUploadConstant.BOUNDARY);
		if (!TextUtils.isEmpty(AccountService.getInstance(mContext).getZMAccessToken())) {
			conn.setRequestProperty(ZMHttpHeader.ZM_ACCESS_TOKEN_HEADER, AccountService.getInstance(mContext).getZMAccessToken());
		}
		// 发送数据
		try {
			conn.connect();
			DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
			for (int index = 0, count = bodyList.size(); index < count; ++index) {
				dataOutputStream.writeBytes(FileUploadConstant.ITEM_BOUNDARY);
				dataOutputStream.writeBytes(FileUploadConstant.NEW_LINE);
				MutilPartBody body = bodyList.get(index);
				body.write(dataOutputStream);
				if (index != count) {
					dataOutputStream.writeBytes(FileUploadConstant.NEW_LINE);
				}
			}
			dataOutputStream.writeBytes(FileUploadConstant.BODY_END);
			dataOutputStream.flush();
			dataOutputStream.close();
			int httpStatusCode = conn.getResponseCode();
			InputStream inputStream = null;
			if (httpStatusCode == HttpURLConnection.HTTP_OK) {
				inputStream = conn.getInputStream();				
				info.setResultCode(ErrorManager.NO_ERROR);				
			} else {
				inputStream = conn.getErrorStream();				
				info.setResultCode(ErrorManager.NETWORK_FAILURE);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, info.getCharset()));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			// read response
			while ((inputLine = reader.readLine()) != null) {
				sb.append(inputLine);
			}
			reader.close();
			info.setNetLength(sb.length());
			info.setRecieveData(sb.toString());
			Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + httpStatusCode + ">,data:"+sb.toString());
		} catch (IOException e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug("<" + info.getUrl() + ">，" + e.getMessage(), e);
		} catch (FileUploadException e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug("<" + info.getUrl() + ">，" + e.getMessage(), e);
		} finally {
			conn.disconnect();
			conn = null;
		}
	}
	/**
	* @Title: sendPostRequest
	* @Description: HTTP POST方式向服务器发送数据
	* @param info
	* @return void
	*/
	private void sendPostRequest(RequestInfo info) {
		DefaultHttpClient httpClient = getHttpClient();
		if (httpClient == null) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			return;
		} else {
			httpClient.setHttpRequestRetryHandler(requestRetryHandler);
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
					info.getCharset() == null ? RequestInfo.defaultCharset : info.getCharset());
		}
		HttpPost httpPost = null;		
		try {
			httpPost = new HttpPost(info.getUrl());
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return;
		}
		Set<String> keySet = httpHeaders.keySet();
		for (String key : keySet) {
			httpPost.addHeader(key, httpHeaders.get(key));
		}
		httpPost.addHeader("Content-Type", info.getContentType());
		try {
			if (!TextUtils.isEmpty(info.getBody())) {
				ByteArrayEntity bae = new ByteArrayEntity(info.getBody().getBytes(info.getCharset()));
				httpPost.setEntity(bae);
				info.setNetLength(bae.getContentLength());
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
//			Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + responseCode + "> data<" + info.getBody() + ">");
			if (responseCode == HttpStatus.SC_OK) {
				info.setResultCode(ErrorManager.NO_ERROR);
				HttpEntity httpEntity = httpResponse.getEntity();
				long contentLength = httpEntity.getContentLength();
				info.setNetLength(contentLength);
				String respData = EntityUtils.toString(httpEntity, info.getCharset());
//				Logger.getInstance(TAG).debug("resp:<" + respData + ">");
				info.setRecieveData(respData);
			} else {
				info.setResultCode(ErrorManager.NETWORK_FAILURE);
				HttpEntity httpEntity = httpResponse.getEntity();
				String str = EntityUtils.toString(httpEntity, info.getCharset());
				Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + responseCode + ">req body<" + info.getBody() + ">\nresp data<" + str + ">");
			}
		} catch (ClientProtocolException e) {
			info.setResultCode(ErrorManager.PROTOCOL_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (IOException e) {
			info.setResultCode(ErrorManager.IO_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			abortRequest(httpPost);
			shutdown(httpClient);
		}
	}
	/**
	* @Title: downloadFile
	* @Description: 下载文件
	* @param info
	* @return void
	*/
	private void doDownloadFile(RequestInfo info) {
		doGetRequest(info, true);
	}
	/**
	* @Title: sendGetRequest
	* @Description: HTTP GET方式向服务器请求数据
	* @param info
	* @return void
	*/
	private void sendGetRequest(RequestInfo info) {
		doGetRequest(info, false);
	}
	/**
	* @Title: sendDeleteRequest
	* @Description: http delete 操作
	* @param info
	* @return void
	*/
	private void sendDeleteRequest(RequestInfo info) {				
		HttpDelete httpDelete = new HttpDelete(info.getUrl());
		doOtherRequest(info, httpDelete);
	}
	/**
	* @Title: sendPutRequest
	* @Description: http put 操作
	* @param info
	* @return void
	*/
	private void sendPutRequest(RequestInfo info) {
		HttpPut httpPut = null;	
		try {
			httpPut = new HttpPut(info.getUrl());
			httpPut.addHeader("Content-Type", "application/json;charset=UTF-8");
			httpPut.addHeader("Accept", "application/json");
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return;
		}
		
		if (!TextUtils.isEmpty(info.getBody())) {			
			try {
				StringEntity entity = new StringEntity(info.getBody(), HTTP.UTF_8);
	            entity.setContentType("application/json;charset=UTF-8");//text/plain;charset=UTF-8
	            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
	            httpPut.setEntity(entity); 				
			} catch (UnsupportedEncodingException e) {
				info.setResultCode(ErrorManager.IO_ERROR);
				Logger.getInstance(TAG).debug(e.getMessage(), e);
				return;
			}
		}
		doOtherRequest(info, httpPut);
	}
	private void doOtherRequest(RequestInfo info, HttpRequestBase req) {
		DefaultHttpClient httpClient = getHttpClient();
		if (httpClient == null) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			return;
		} else {
			httpClient.setHttpRequestRetryHandler(requestRetryHandler);
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
					info.getCharset() == null ? RequestInfo.defaultCharset : info.getCharset());
		}
		Set<String> keySet = httpHeaders.keySet();
		for (String key : keySet) {
			req.addHeader(key, httpHeaders.get(key));
		}
		try {
			req.addHeader("Content-Type", "application/json;charset=UTF-8");
			req.addHeader("Accept", "application/json");
			HttpResponse httpResponse = httpClient.execute(req);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				info.setResultCode(ErrorManager.NO_ERROR);
				String str = EntityUtils.toString(httpEntity, info.getCharset());
				info.setRecieveData(str);
				info.setNetLength(str.length());
			} else {				
				info.setResultCode(ErrorManager.NETWORK_FAILURE);
				HttpEntity httpEntity = httpResponse.getEntity();
				String str = EntityUtils.toString(httpEntity, info.getCharset());
				Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + responseCode + ">req body<" + info.getBody() + ">\nresp data<" + str + ">");
				info.setRecieveData(str);
			}
		} catch (ClientProtocolException e) {
			info.setResultCode(ErrorManager.PROTOCOL_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			info.setResultCode(ErrorManager.FILENOTEXISTS_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (IOException e) {
			info.setResultCode(ErrorManager.IO_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			abortRequest(req);
			shutdown(httpClient);
		}
	}
	private void doGetRequest(RequestInfo info, boolean isDownFile) {
		DefaultHttpClient httpClient = getHttpClient();
		if (httpClient == null) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			return;
		} else {
			httpClient.setHttpRequestRetryHandler(requestRetryHandler);
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
					info.getCharset() == null ? RequestInfo.defaultCharset : info.getCharset());
		}
		
		HttpGet httpGet = null;	
		try {
			httpGet = new HttpGet(info.getUrl());
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
			return;
		}
		Set<String> keySet = httpHeaders.keySet();
		for (String key : keySet) {
			httpGet.addHeader(key, httpHeaders.get(key));
		}
		
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
//			Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + responseCode + "> data<" + info.getBody() + ">");
			if (responseCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (!isDownFile) {
					info.setResultCode(ErrorManager.NO_ERROR);
					String str = EntityUtils.toString(httpEntity, info.getCharset());
					info.setRecieveData(str);
					info.setNetLength(str.length());
				} else {
					File downFile = info.getDownFile();
					if (downFile == null) {
						info.setResultCode(ErrorManager.FILENOTEXISTS_ERROR);
					} else {
						downFile.delete();
						byte[] buf = new byte[BUFFERSIZE * 256];
						long contentLength = httpEntity.getContentLength();
						FileOutputStream fos = new FileOutputStream(downFile, true);
						InputStream in = httpEntity.getContent();
						int numRead = in.read(buf);
						long received = numRead;
						IHttpProcessNotify callback = info.getProcessNotifyCallback();
						while (numRead > 0) {
							if (callback != null) {
								// 发送进度通知
								int process = (int) ((received * 100) / contentLength);
								callback.onProcessChange(new HttpProcessData(process));
							}
							fos.write(buf, 0, numRead);
							numRead = in.read(buf);
							received += numRead;
						}
						fos.flush();
						fos.close();
						info.setNetLength(contentLength);
						info.setResultCode(ErrorManager.NO_ERROR);
					}
				}
			} else {
				Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + responseCode + "> data<" + info.getBody() + ">");
				info.setResultCode(ErrorManager.NETWORK_FAILURE);
				HttpEntity httpEntity = httpResponse.getEntity();
				String str = EntityUtils.toString(httpEntity, info.getCharset());
				info.setRecieveData(str);
			}
		} catch (ClientProtocolException e) {
			info.setResultCode(ErrorManager.PROTOCOL_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			info.setResultCode(ErrorManager.FILENOTEXISTS_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (IOException e) {
			info.setResultCode(ErrorManager.IO_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			abortRequest(httpGet);
			if (!info.isKeepAlive()) {
				shutdown(httpClient);
			}
		}
	}

	private DefaultHttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();		
		// 代理设置
		HttpHost proxyHost = ProxySettings.getProxyHost();
		if (proxyHost != null) {
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
		}
		// 设置http头信息
		httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_CONNECTION);
		httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_CONNECTION * 2);
		
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_CONNECTION);	
		
		// 设置我们的HttpClient支持HTTP和HTTPS两种模式
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// ssl
		SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
	    sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schReg.register(new Scheme("https", sslSocketFactory, 443));
		// 使用线程安全的连接管理来创建HttpClient
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
		return new DefaultHttpClient(conMgr, httpParams);
	}

	//
	private void abortRequest(final HttpRequestBase request) {
		if (request != null) {
			request.abort();
		}
	}

	/**
	* @Title: shutdown
	* @Description: 关闭http连接
	* @param httpclient
	* @return void
	*/
	private void shutdown(final HttpClient httpclient) {
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 异常自动恢复处理 使用HttpRequestRetryHandler接口实现请求的异常恢复
	 */
	private HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public synchronized boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试
			if (executionCount > TRY_COUNT) {
				// 超过最大次数则不需要重试
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// 服务停掉则重新尝试连接
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// SSL异常不需要重试
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// 请求内容相同则重试
				return true;
			}
			return false;
		}
	};

	/**
	 * 使用ResponseHandler接口处理响应 HttpClient使用ResponseHandler会自动管理连接的释放 解决了对连接的释放管理
	 */
	private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		// 自定义响应处理
		public synchronized String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String charset = EntityUtils.getContentCharSet(entity) == null ? RequestInfo.defaultCharset : EntityUtils.getContentCharSet(entity);
				return new String(EntityUtils.toByteArray(entity), charset);
			} else {
				return null;
			}
		}
	};

	@Override
	public byte[] downloadFile(RequestInfo info) {
		DefaultHttpClient httpClient = getHttpClient();
		if (httpClient == null) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			return null;
		} else {
			httpClient.setHttpRequestRetryHandler(requestRetryHandler);
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,
					info.getCharset() == null ? RequestInfo.defaultCharset : info.getCharset());
		}		
		HttpGet httpGet = null;
		
		try {
			httpGet = new HttpGet(info.getUrl());
		} catch (IllegalArgumentException e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(info.getUrl(), e);
			return null;
		}
		Set<String> keySet = httpHeaders.keySet();
		for (String key : keySet) {
			httpGet.addHeader(key, httpHeaders.get(key));
		}
		byte[] buffer = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				buffer = EntityUtils.toByteArray(httpEntity);
				info.setNetLength(buffer.length);
				info.setResultCode(ErrorManager.NO_ERROR);
			} else {
				Logger.getInstance(TAG).debug("resp url<" + info.getUrl() + "> http status:<" + responseCode + "> data<" + info.getBody() + ">");
				info.setResultCode(ErrorManager.NETWORK_FAILURE);
				HttpEntity httpEntity = httpResponse.getEntity();
				String str = EntityUtils.toString(httpEntity, info.getCharset());
				info.setRecieveData(str);
			}
		} catch (ClientProtocolException e) {
			info.setResultCode(ErrorManager.PROTOCOL_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			info.setResultCode(ErrorManager.FILENOTEXISTS_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (IOException e) {
			info.setResultCode(ErrorManager.IO_ERROR);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} catch (Exception e) {
			info.setResultCode(ErrorManager.NETWORK_FAILURE);
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			abortRequest(httpGet);
			shutdown(httpClient);
		}
		return buffer;
	}
}
