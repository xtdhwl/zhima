package com.zhima.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Properties;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhima.R;
import com.zhima.base.config.FilePathConfig;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.logger.Logger;
import com.zhima.base.mobile.SystemInfo;
import com.zhima.base.network.HttpNetwork;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.protocol.vo.VoContent;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.DataServiceManager;
import com.zhima.ui.tools.IntentHelper;

/**
* @ClassName: CrashHandler
* @Description: 手机崩溃handler。每次打开app时检测上次崩溃时保存的信息并上传至服务器
* @author liubingsr
* @date 2012-6-25 下午7:21:44
*
*/
public class CrashHandler implements UncaughtExceptionHandler {
	private final static String TAG = "CrashHandler";
	private final static String DELIMITER = "|";
	private static CrashHandler mInstance = null;

	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	private Properties mDeviceCrashInfo = new Properties();

	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	
	private static Gson gson = null;    
    static {  
        if (gson == null) {  
            gson = new Gson();
        }  
    }

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		if (mInstance == null) {
			mInstance = new CrashHandler();
		}
		return mInstance;
	}

	public void init(Context c) {
		mContext = c;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
//		if (ex == null) {
//			return;
//		}
////		if (!handleException(ex) && mDefaultHandler != null) {
////			//如果用户没有处理则让系统默认的异常处理器来处理
////			mDefaultHandler.uncaughtException(thread, ex);
////		}
////		else {
////			android.os.Process.killProcess(android.os.Process.myPid());
////			System.exit(0);
////		}
//		handleException(ex);
//		if (mDefaultHandler != null) {
//			//如果用户没有处理则让系统默认的异常处理器来处理
//			mDefaultHandler.uncaughtException(thread, ex);
//		}
		
		
		if (ex == null) {
			return;
		}
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			
//			Logger.getInstance("CrashHandler--").debug("uncaughtException");
			
			
//			IntentHelper.startAppByPackageName(mContext, "com.zhima");
//			DataServiceManager.getInstance(mContext);
//			SystemConfig.IS_NEED_GET_MYSELF = true;
			
			SettingHelper.setBoolean(mContext, com.zhima.base.utils.SettingHelper.Field.IS_NEED_GET_MYSELF, true);
			
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}
	
	/**
	 * 异常处理
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}		
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				HaloToast.show(mContext, "程序出现异常，即将退出。", 1);
//				Looper.loop();
//			}
//		}.start();
		try {
			// 收集设备参数信息
			collectCrashDeviceInfo(mContext);
			// 保存日志文件
			saveCrashInfoToFile(ex);
			formatCrashInfoFile();

			if (SystemConfig.UPLOAD_CRASH_INFO) {
				Thread runThread = uploadCrashRecord();
				if (runThread != null) {
					runThread.join();
				}
			}
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		return true;
	}
	 
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		Logger.getInstance(TAG).debug("crash:" + result);
		printWriter.close();
		mDeviceCrashInfo.put(STACK_TRACE, result);
		String fileName = FileHelper.getRootDir() + FilePathConfig.CRASH_FILE_NAME;
		try {
			FileOutputStream trace = new FileOutputStream(fileName, false);

			String devInfo = getDeviceInfo();
			trace.write(devInfo.getBytes("utf-8"));
			mDeviceCrashInfo.store(trace, "");
			trace.flush();
			trace.close();
			return fileName;
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		return null;
	}

	private void formatCrashInfoFile() {
		File file = new File(FileHelper.getRootDir() + FilePathConfig.CRASH_FILE_NAME);
		if (file != null && file.exists()) {
			CrashFileInfo fileInfo = readCrashInfo(file);
			if (fileInfo.isValidate()) {
				String crashInfo = fileInfo.content.replace("\\n", "\n");
				crashInfo = crashInfo.replace("\\t", "\t");

				String fileName = FileHelper.getRootDir() + FilePathConfig.CRASH_FILE_NAME;
				try {
					FileOutputStream trace = new FileOutputStream(fileName, false);
					trace.write(crashInfo.getBytes());
					trace.flush();
					trace.close();
				} catch (Exception e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}
	}

	private void collectCrashDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
				mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
			}
		} catch (Exception e) {
			Logger.getInstance(TAG).debug("Error while collect package info", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), "" + field.get(null));
			} catch (Exception e) {
				Logger.getInstance(TAG).debug("Error while collect crash info", e);
			}
		}
	}

	private String formatElapsedTime(long time) {
		String format = "";
		long minMillseconds = 60 * 1000;
		long hourMillseconds = 60 * minMillseconds;
		long dayMillseconds = 24 * hourMillseconds;

		long days = time / dayMillseconds;
		long hours = (time % dayMillseconds) / hourMillseconds;
		long mins = (time % hourMillseconds) / minMillseconds;
		format = String.format("%d days %d hours %d mins ", days, hours, mins);
		return format;
	}

	private String getDeviceInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.currentTimeMillis()).append("\n");

		// 软件版本 read from version.xml: version
		sb.append(mContext.getString(R.string.version));
		// 渠道号
		sb.append(DELIMITER).append(AppLaunchService.getInstance(mContext).getPublishChannel());
		// 登录帐号名
		sb.append(DELIMITER).append(SettingHelper.getString(mContext, SettingHelper.Field.ACCOUNT_NAME, ""));
		// 手机开机时长
		sb.append(DELIMITER).append(formatElapsedTime(SystemClock.elapsedRealtime()));
		// 生产厂商
		sb.append(DELIMITER).append(SystemInfo.getManufacturer());
		// 手机产品ID（手机型号）
		sb.append(DELIMITER).append(SystemInfo.getProductModel());
		// 设备id
		sb.append(DELIMITER).append(SystemInfo.getDeviceId(mContext));
		// 手机IMSI
		sb.append(DELIMITER).append(SystemInfo.getIMSI(mContext));
		// 手机IMEI
		sb.append(DELIMITER).append(SystemInfo.getIMEI(mContext));
		// 手机SDK版本 如：2.0.1
		sb.append(DELIMITER).append(SystemInfo.getSDKVersion());
		// 手机SDK level版本 如：level 7 (android使用)
		sb.append(DELIMITER).append(SystemInfo.getSDKLevel());
		// 国家ID
		sb.append(DELIMITER).append(SystemInfo.getMCC(mContext));
		// 运营商ID（MNC中国移动0,02 (GSM),07 (TD)，联通是01
		sb.append(DELIMITER).append(SystemInfo.getMNC(mContext));
		// 地区ID（位置区域码，它是唯一地识别我国数字PLMN中每个位置区的，是一字节16进制的BCD码）
		sb.append(DELIMITER).append(SystemInfo.getLac(mContext));
		// 当前基站ID
		sb.append(DELIMITER).append(SystemInfo.getCellId(mContext));
		sb.append("\n\n");
		return sb.toString();
	}

	private CrashFileInfo readCrashInfo(File file) {
		CrashFileInfo info = new CrashFileInfo();
		StringBuilder crashInfoBuilder = new StringBuilder();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String logTime = String.valueOf(System.currentTimeMillis());
			String buildNum = mContext.getString(R.string.version);
			String crashInfo = "";
			try {
				logTime = br.readLine();
				crashInfoBuilder.append(logTime).append("\n");
			} catch (Exception e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}
			buildNum = br.readLine();
			crashInfoBuilder.append(buildNum).append("\n\n");

			String line = br.readLine();
			while (line != null) {
				crashInfoBuilder.append(line).append("\n");
				line = br.readLine();
			}
			crashInfo = crashInfoBuilder.toString();

			info.timeStamp = logTime;
			info.version = buildNum;
			info.content = crashInfo;
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}

		return info;
	}

	public Thread uploadCrashRecord() {
		Thread runThread = null;
		if (SystemConfig.UPLOAD_CRASH_INFO) {
			synchronized (uploaderRunning) {
				if (uploaderRunning == 0) {
					uploaderRunning = 1;
					runThread = new CrashRecordUploader();
					runThread.start();
				}
			}
		}
		return runThread;
	}

	private Integer uploaderRunning = 0;

	private class CrashFileInfo {
		String timeStamp = "";
		String version = "";
		String content = "";

		public boolean isValidate() {
			return !TextUtils.isEmpty(timeStamp) || !TextUtils.isEmpty(version)
					|| !TextUtils.isEmpty(content);
		}
	}

	private class CrashRecordUploader extends Thread {
		private boolean postCrashInfo(String logTime, String version, String content) throws Exception {
			boolean success = true;
			String url = AppLaunchService.getInstance(mContext).getRestRoot() + "system/crash/%s";
			url = String.format(url, SystemConfig.PLATFORM);
			RequestInfo info = new RequestInfo(url);
			info.setRequestType(RequestType.POST);
			VoContent vo = new VoContent();
			vo.setContent(content);
			String json = gson.toJson(vo);
			info.setBody(json);
			HttpNetwork.getInstance(mContext).sendRequest(info);
			return success;
		}

		@Override
		public void run() {
			try {
				File file = new File(FileHelper.getRootDir() + FilePathConfig.CRASH_FILE_NAME);
				if (file.exists()) {
					CrashFileInfo fileInfo = readCrashInfo(file);
					if (fileInfo.isValidate()) {
						if (postCrashInfo(fileInfo.timeStamp, fileInfo.version, fileInfo.content)) {
							 if(!SystemConfig.DEBUG) {
								 file.delete();
							 }
						}
					}
				}
			} catch (Exception e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}

			synchronized (uploaderRunning) {
				uploaderRunning = 0;
			}
		}
	}
}
