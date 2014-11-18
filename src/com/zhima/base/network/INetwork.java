/* 
* @Title: INetwork.java
* Created by liubingsr on 2012-5-15 下午2:30:13 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.network;

/**
 * @ClassName: INetwork
 * @Description: 网络处理接口
 * @author liubingsr
 * @date 2012-5-15 下午2:30:13
 *
 */
public interface INetwork {
    /**
    * @Title: sendGetRequest
    * @Description: http请求处理
    * @param info
    * void
    */
    public abstract void sendRequest(RequestInfo info);
    /**
    * @Title: downloadFile
    * @Description: 下载文件
    * @param url
    * @return byte[] 文件字节流
    */
    public abstract byte[] downloadFile(RequestInfo info);
}
