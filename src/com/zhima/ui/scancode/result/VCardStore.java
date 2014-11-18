package com.zhima.ui.scancode.result;

public class VCardStore {

	private static VCardStore instance;
	private VCardHandler mVCardHandler;

	private VCardStore() {

	}

	public synchronized static VCardStore getInstance() {
		if (instance == null) {
			instance = new VCardStore();
		}
		return instance;
	}

	public VCardHandler getVCardHandler() {
		return mVCardHandler;
	}

	public void setVCardHandler(VCardHandler mVCardHandler) {
		this.mVCardHandler = mVCardHandler;
	}
}
