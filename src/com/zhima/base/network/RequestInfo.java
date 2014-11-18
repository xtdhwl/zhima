package com.zhima.base.network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zhima.base.error.ErrorManager;
import com.zhima.base.network.uploadfile.FormFile;

/**
* @ClassName: RequestInfo
* @Description: http请求信息类(发送请求信息并保存服务端返回的信息)
* @author liubingsr
* @date 2012-5-15 下午6:17:16
*
 */
public class RequestInfo {
	public enum RequestType {
		/**
		 * 带文件附件的http post
		 */
		UPLOAD,
		/**
		 * 下载文件
		 */
		DOWNLOAD,
		/**
		 * http get 方式获取数据
		 */
		GET,
		/**
		 * HTTP POST方式发送数据
		 */
		POST,
		/**
		 * 删除数据
		 */
		DELETE,
		/**
		 * 修改状态
		 */
		PUT
	}
	/**
	 * 缺省字符编码
	 */
	public final static String defaultCharset = "utf-8";
	/**
	 * 缺省content-type
	 */
	public final static String defaultContentType = "application/json";
	/**
	 * 字符编码格式
	 */
	private String mCharset;
	/**
	 * content-type(mime)
	 */
	private String mContentType;
	/**
	 * 请求的url地址
	 */
    private String mUrl;
    /**
     * 要发送的http body数据包
     */
    private String mBody;
    /**
     * 网络流量字节数(用于统计网络流量)
     */
    private long mNetLength = 0;
    /**
     * response数据
     */
    private String mRecieveData;
    /**
     * 下载的文件
     */
    private File mDownFile;
    /**
     * HTTP Status 结果状态码
     */
    private int mResultCode;
    /**
     * Http form field 参数对
     */
    private Map<String, String> mFormFieldParams;
    /**
     * 要上传的文件列表
     */
    private ArrayList<FormFile> mUploadFileList;
    /**
     * 进度通知
     */
    private IHttpProcessNotify mProcessNotifyCallback;
    /**
     * 请求类型
     */
    private RequestType mRequestType;
//    /**
//     * 附加的http头数据
//     */
//    private String mZMAccessToken ;
    /**
     * 下载文件路径
     */
	private String mDownFilePath;
	/**
	 * 请求完毕是否仍然保持连接
	 */
	private boolean mKeepAlive = true;
    
    public RequestInfo(String url) {
    	mUrl = url;
    	mCharset = defaultCharset;
    	mContentType = defaultContentType;  	
    }
    
    public RequestInfo(String url, String body) {
    	this(url);
    	mBody = body;
    }
    
    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
    
    public void setCharset(String charset) {
        this.mCharset = charset;
    }

    public String getCharset() {
        return mCharset;
    }   

    /**
	 * @return mProcessNotifyCallback
	 */
	public IHttpProcessNotify getProcessNotifyCallback() {
		return mProcessNotifyCallback;
	}

	/**
	 * @param processNotifyCallback 要设置的 mProcessNotifyCallback
	 */
	public void setProcessNotifyCallback(IHttpProcessNotify processNotifyCallback) {
		this.mProcessNotifyCallback = processNotifyCallback;
	}

	public void setRequestType(RequestType requestType) {
        this.mRequestType = requestType;
    }
	
	public RequestType getRequestType() {
        return mRequestType;
    }
	
	public void setContentType(String contentType) {
        this.mContentType = contentType;
    }
	
	public String getContentType() {
        return mContentType;
    }
	
//	public String getZMAccessToken() {
//        return mZMAccessToken;
//    }
//	public void setZMAccessToken(String accessToken) {
//        this.mZMAccessToken = accessToken;
//    }
    
    public void setBody(String body) {
        this.mBody = body;
    }

    public String getBody() {
        return mBody;
    }
    
    public void setNetLength(long length) {
        this.mNetLength += length;
    }

    public long getNetLength() {
        return mNetLength;
    }

    public void setResultCode(int code) {
        this.mResultCode = code;
    }

    public int getResultCode() {
        return mResultCode;
    }
    public boolean isSuccess() {
		return mResultCode == ErrorManager.NO_ERROR;
	}
    
    public boolean isKeepAlive() {
		return mKeepAlive;
	}
	public void setKeepAlive(boolean alive) {
		this.mKeepAlive = alive;
	}

	public void setRecieveData(String recieveData) {
        this.mRecieveData = recieveData;
    }

    public String getRecieveData() {
        return mRecieveData == null ? "" : mRecieveData;
    }

    public void setDownloadFile(String fileName) {
    	this.mDownFilePath = fileName;
        this.mDownFile = new File(mDownFilePath);
        if (!this.mDownFile.exists()) {
            boolean created = this.mDownFile.mkdirs();
        }
    }

    public File getDownFile() {
        return mDownFile;
    }
    
    public String getDownFilePath() {
    	return this.mDownFilePath;
    }
    
    public void addFormFieldParam(String field, String fieldValue) {
    	if (mFormFieldParams == null) {
    		mFormFieldParams = new HashMap<String, String>();
    	}
    	mFormFieldParams.put(field, fieldValue);
    }
    
    public void setFormFieldParams(Map<String, String> params) {
        this.mFormFieldParams = params;
    }

    public Map<String, String> getFormFieldParams() {
        return mFormFieldParams;
    }
    
    public void addUploadFile(FormFile formFile) {
    	if (mUploadFileList == null) {
    		mUploadFileList = new ArrayList<FormFile>();
    	}
    	mUploadFileList.add(formFile);
    }
    public void setUploadFileList(ArrayList<FormFile> formFiles) {
    	mUploadFileList = formFiles;
    }

    public ArrayList<FormFile> getUploadFileList() {
        return mUploadFileList;
    }
}
