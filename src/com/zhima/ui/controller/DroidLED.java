package com.zhima.ui.controller;

import java.lang.reflect.Method;

import android.os.IBinder;

import com.zhima.base.mobile.SystemInfo;

/**
 * @ClassName: DroidLED
 * @Description: motorola
 * @author luqilong
 * @date 2012-11-6 下午2:55:56
 */
public class DroidLED {

	private Object svc = null;
	private Method getFlashlightEnabled = null;
	private Method setFlashlightEnabled = null;

	@SuppressWarnings("unchecked")
	public DroidLED() throws Exception {
		try {
			// call ServiceManager.getService("hardware") to get an IBinder for
			// the service.
			// this appears to be totally undocumented and not exposed in the
			// SDK whatsoever.
			Class sm = Class.forName("android.os.ServiceManager");
			Object hwBinder = sm.getMethod("getService", String.class).invoke(null, "hardware");

			// get the hardware service stub. this seems to just get us one step
			// closer to the proxy
			Class hwsstub = Class.forName("android.os.IHardwareService$Stub");
			Method asInterface = hwsstub.getMethod("asInterface", android.os.IBinder.class);
			svc = asInterface.invoke(null, (IBinder) hwBinder);

			// grab the class (android.os.IHardwareService$Stub$Proxy) so we can
			// reflect on its methods
			Class proxy = svc.getClass();

			// save methods
			getFlashlightEnabled = proxy.getMethod("getFlashlightEnabled");
			setFlashlightEnabled = proxy.getMethod("setFlashlightEnabled", boolean.class);
		} catch (Exception e) {
			throw new Exception("LED could not be initialized");
		}
	}

	public boolean isFlashOn() {
		try {
			return getFlashlightEnabled.invoke(svc).equals(true);
		} catch (Exception e) {
			return false;
		}
	}

	public void doSetFlash(boolean tf) {
		try {
			setFlashlightEnabled.invoke(svc, tf);
		} catch (Exception e) {
		}
	}

	/**
	 * @Title: isDroidLED
	 * @Description: 是否手机是否为DroidLED(测试MB525,开启闪光灯闪)
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isDroidLED() {
		String manuName = SystemInfo.getManufacturer();
		String modelNmae = SystemInfo.getProductModel();
		if ("Motorola".equalsIgnoreCase(manuName) && "MB525".equalsIgnoreCase(modelNmae)) {
			return true;
		}
		return false;
	}
}
