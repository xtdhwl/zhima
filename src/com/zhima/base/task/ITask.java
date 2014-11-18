/* 
 * @Title: ITask.java
 * Created by liubingsr on 2012-5-28 下午2:09:03 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.task;

/**
 * @ClassName: ITask
 * @Description: 任务接口
 * @author liubingsr
 * @date 2012-5-28 下午2:09:03
 * 
 */
public interface ITask {
	public abstract void execute();

	public abstract boolean isEqual(Object object);
}
