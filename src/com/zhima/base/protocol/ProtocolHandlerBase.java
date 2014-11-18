/* 
 * @Title: ProtocolHandlerBase.java
 * Created by liubingsr on 2012-5-18 下午2:52:14 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.protocol;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.LoginType;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.task.ITask;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.LoginHttpRequestCallback;
import com.zhima.data.service.LoginService;
import com.zhima.data.service.RequestService;

/**
 * @ClassName: ProtocolHandlerBase
 * @Description: 协议基类
 * @author liubingsr
 * @date 2012-5-18 下午2:52:14
 * 
 */
public abstract class ProtocolHandlerBase implements IHttpRequestCallback, ITask {
	private final static String TAG = "ProtocolHandlerBase";
	protected final static String JSON_FIELD = "data";
	// 请求服务器的根地址
	protected String mBaseUrl = "";
	protected String mSubUrl = "";
	protected Context mContext = null;
	protected IHttpRequestCallback mRequestCallback = null;
	protected RequestInfo mRequestInfo = null;
	
	protected RequestService mRequestService = null;
	
	protected boolean mDeleted = false;
	/**
	 * 协议类型
	 */
	protected int mProtocolType;
	protected String mJson;
	/**
	 * 协议处理状态
	 */
	protected int mProtocolStatus = ProtocolStatus.RESULT_SUCCESS;	
	/**
	 * 网络请求状态码
	 */
	protected int mHttpResultCode;
	protected JSONObject mResponeVO;
	protected ZMResponseHeader mResponseHeader;
	protected String mProtocolErrorMessage;

	public ProtocolHandlerBase(Context context, IHttpRequestCallback callBack) {
		mContext = context;
		mRequestCallback = callBack;
		mHttpResultCode = ErrorManager.UNKNOWN_ERROR;
		mRequestService = RequestService.getInstance(mContext);
		mBaseUrl = AppLaunchService.getInstance(mContext).getRestRoot();
	}

	public RequestInfo getRequestInfo() {
		return mRequestInfo;
	}
	public void setRequestInfo(RequestInfo requestInfo) {
		this.mRequestInfo = requestInfo;
	}

	public IHttpRequestCallback getHttpRequestCallback() {
		return mRequestCallback;
	}

	public void setHttpRequestCallback(IHttpRequestCallback callback) {
		mRequestCallback = callback;
	}

	public int getProtocolType() {
		return mProtocolType;
	}
	public void setProtocolType(int protocolType) {
		this.mProtocolType = protocolType;
	}

	public final boolean isDeleted() {
		return mDeleted;
	}

	public int getHttpResultCode() {
		return mHttpResultCode;
	}
	public void setHttpResultCode(int errorCode) {
		mHttpResultCode = errorCode;
	}
	
	public Context getContext() {
		return mContext;
	}
	/**
	* @Title: isHttpSuccess
	* @Description: 是否发生了网络错误
	* @return boolean
	*/
	public boolean isHttpSuccess() {
		return mRequestInfo == null ? false
				: mRequestInfo.getResultCode() == ErrorManager.NO_ERROR;
	}
	/**
	* @Title: getProtocolStatus
	* @Description: 协议处理状态
	* @return int 
	* @see com.zhima.base.consts.ZMConsts.ProtocolStatus
	*/
	public int getProtocolStatus() {
		return mProtocolStatus;
	}
	/**
	* @Title: setProtocolStatus
	* @Description: 设置协议处理状态
	* @param status 	
	* @return void
	* @see com.zhima.base.consts.ZMConsts.ProtocolStatus
	*/
	public void setProtocolStatus(int status) {
		mProtocolStatus = status;
	}
	/**
	* @Title:  isHandleSuccess
	* @Description: 协议是否处理成功(调用getProtocolStatus()得到具体的状态值)
	* @return boolean
	*/
	public boolean isHandleSuccess() {
		return mProtocolStatus == ProtocolStatus.RESULT_SUCCESS || mProtocolStatus == ProtocolStatus.RESULT_SUCCESS_EMPTY;
	}
	/**
	* @Title: getProtocolErrorMessage
	* @Description: 服务端处理协议后的结果描述
	* @return
	* String
	*/
	public String getProtocolErrorMessage() {
		return mProtocolErrorMessage;
	}
	public String getHttpResultString() {
		return ErrorManager.getErrorDescByCode(mHttpResultCode);
	}

	@Override
	public void execute() {
		mRequestService.execute(this);
	}

	@Override
	public boolean isEqual(Object object) {
		return this == object;
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase data) {
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase data) {
	}

	/**
	 * @Title: parseData
	 * @Description: 解析服务器端返回的数据包
	 * @return void
	 */
	public void parseData() {
		if (mRequestInfo == null) {
			return;
		}
		if (!mDeleted) {
			if (mProtocolType != ProtocolType.DOWN_FILE_PROTOCOL) {
				// 正常的app协议				
				mJson = mRequestInfo.getRecieveData();
				// 解析服务器返回的json数据包
				try {
					Logger.getInstance(TAG).debug(AccountService.getInstance(mContext).getZMAccessToken() + ",url:<" + mRequestInfo.getUrl() + ">数据包:<" + mJson + ">");
					JSONTokener jsonParser = new JSONTokener(mJson);
					mResponeVO = (JSONObject) jsonParser.nextValue();
					mResponseHeader = ZMResponseHeader.parse(mResponeVO);
					mProtocolStatus = mResponseHeader.getStatus();
					mProtocolErrorMessage = mResponseHeader.getMessage();
					if (mProtocolStatus == ProtocolStatus.RESULT_ERROR_UNAUTHORIZED) {
						// token失效了
						if (AccountService.getInstance(mContext).isGuest()) {
							// 游客。自动登录
							mProtocolErrorMessage = mContext.getString(R.string.general_error_msg);
							Logger.getInstance(TAG).debug("token失效，自动注册用户开始自动登录。<" + mJson + ">");						
							LoginService.getInstance(mContext).login(AccountService.getInstance(mContext).getAccount(), AccountService.getInstance(mContext).getPassword(), LoginType.AUTO, new LoginHttpRequestCallback());
						} else {
							//  其它用户将登录状态修改为“未登录”
							Logger.getInstance(TAG).debug("token失效，非游客置位成“未登录”。<" + mJson + ">");	
							AccountService.getInstance(mContext).clearAccessToken();
							AccountService.getInstance(mContext).setLogin(false);
						}
						return;
					}					
					if (parse()) {
						afterParse();
					}
				} catch (JSONException ex) {
					setProtocolStatus(ProtocolStatus.RESULT_ERROR);
				} catch (Exception ex) {
					setProtocolStatus(ProtocolStatus.RESULT_ERROR);
				}			
			}			
		}
	}

	/**
	 * @Title: parse
	 * @Description: 解析数据包
	 * @return void
	 */
	public abstract boolean parse();

	/**
	 * @Title: afterParse
	 * @Description: 解析成功以后调用
	 * @return void
	 */
	public abstract void afterParse();

	/**
	 * @Title: deleteByCallback
	 * @Description: 移走callback
	 * @param callback
	 * @return void
	 */
	public void deleteByCallback(IHttpRequestCallback callback) {
		if (mRequestCallback == callback) {
			mDeleted = true;
			mRequestCallback = null;
		}
	}
	
	public static String IdListToJson(ArrayList<Long> idList) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"items\":[");
		for (Long id : idList) {
			sb.append(id + ",");
		}
		sb.append("]");
		return sb.toString();
	}
}
