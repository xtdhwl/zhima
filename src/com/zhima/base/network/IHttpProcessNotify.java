/* 
* @Title: IHttpProcessNotify.java
* Created by liubingsr on 2012-5-14 下午4:26:14 
* Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
*/
package com.zhima.base.network;

/**
 * @ClassName: IHttpProcessNotify
 * @Description: Http下载进度通知
 * @author liubingsr
 * @date 2012-5-14 下午4:26:14
 *
 */
public interface IHttpProcessNotify {
    /**
    * @Title: onProcessChange
    * @Description: 进度发生了变化
    * @param data
    * void
     */
    public abstract void onProcessChange(HttpProcessData data);
}
