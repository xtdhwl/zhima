package com.zhima.service;

import com.zhima.service.ICallback;

interface IServiceConnect
{
	void registerCallback(ICallback callBack);
	void unregisterCallback(ICallback callBack);
}