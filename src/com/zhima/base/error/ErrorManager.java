/* 
 * @Title: ErrorManager.java
 * Created by liubingsr on 2012-5-14 上午9:40:05 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.error;

import android.content.Context;

import com.zhima.R;
import com.zhima.base.utils.NetUtils;
import com.zhima.ui.common.view.HaloToast;

/**
 * @ClassName: ErrorManager
 * @Description: 错误码及错误信息管理
 * @author liubingsr
 * @date 2012-5-14 上午10:00:10
 * 
 */
public class ErrorManager {
	public final static String TAG = ErrorManager.class.getSimpleName();
	//成功
	public final static int NO_ERROR = 0;
	//没有网络连接
	public final static int NOT_NETWORK = NO_ERROR + 1;
	//缺少数据
	public final static int LACKOFDATA_ERROR = NOT_NETWORK + 1;
	//网络连接超时
	public final static int NETWORK_TIMEOUT = LACKOFDATA_ERROR + 1;
	//协议错误
	public final static int PROTOCOL_ERROR = NETWORK_TIMEOUT + 1;
	//数据格式错误
	public final static int DATAFORMAT_ERROR = PROTOCOL_ERROR + 1;
	//网络操作失败
	public final static int NETWORK_FAILURE = DATAFORMAT_ERROR + 1;
	//IO错误
	public final static int IO_ERROR = NETWORK_FAILURE + 1;
	//json解析错误
	public final static int JSONPARSE_ERROR = IO_ERROR + 1;
	//文件不存在错误
	public final static int FILENOTEXISTS_ERROR = JSONPARSE_ERROR + 1;
	//写文件失败
	public final static int WRITEFILE_ERROR = FILENOTEXISTS_ERROR + 1;
	//文件内容为空
	public final static int FILE_IS_EMPTY = WRITEFILE_ERROR + 1;
	/**
	 * 分配给用户的token过期了
	 */
	public final static int TOKEN_TIMEOUT = FILE_IS_EMPTY + 1;

	//未知的错误
	public final static int UNKNOWN_ERROR = 0xFFFFFFFF;

	/**
	 * @Title: getErrorDescByCode
	 * @Description: 由错误码返回错误描述文字信息
	 * @param errorCode 错误码
	 * @return String 错误描述文字
	 */
	public static String getErrorDescByCode(int errorCode) {
		switch (errorCode) {
		case NO_ERROR:
			return "成功";
		case NOT_NETWORK:
			return "没有连接网络";
		case LACKOFDATA_ERROR:
			return "缺少数据";
		case NETWORK_TIMEOUT:
			return "网络连接超时";
		case PROTOCOL_ERROR:
			return "协议错误";
		case DATAFORMAT_ERROR:
			return "数据格式错误";
		case NETWORK_FAILURE:
			return "网络操作失败";
		case IO_ERROR:
			return "IO错误";
		case JSONPARSE_ERROR:
			return "Json解析错误";
		case FILENOTEXISTS_ERROR:
			return "文件不存在";
		case WRITEFILE_ERROR:
			return "写文件失败";
		default:
			return "未知的错误";
		}
	}

	/**
	* @Title: showErrorMessage 
	* @Description: 显示错误信息
	* @return boolean true没有网络,false是有网络
	 */
	public static boolean showErrorMessage(Context context) {
		if (NetUtils.isNetworkAvailable(context)) {
			HaloToast.show(context, R.string.load_failed, 0);
			return true;
		}
		HaloToast.show(context, R.string.network_request_failed, 0);
		return false;
	}
}
