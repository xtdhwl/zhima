package com.zhima.ui.scancode.activity;

import java.io.IOException;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zhima.R;
import com.zhima.base.alarm.SimpleAlarmTimer;
import com.zhima.base.alarm.SimpleAlarmTimer.SimpleAlarmTimerCallback;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.utils.ImeHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.model.UnknownObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnOnlyBtClickListener;
import com.zhima.ui.controller.DroidLED;
import com.zhima.ui.scancode.camera.CameraManager;
import com.zhima.ui.scancode.decoding.CaptureActivityHandler;
import com.zhima.ui.scancode.result.VCardHandler;
import com.zhima.ui.scancode.result.VCardStore;
import com.zhima.ui.scancode.view.ViewfinderView;
import com.zhima.ui.space.activity.VcardActivity;

public class ScanningActivity extends BaseActivity implements Callback {
	private static final String TAG = "ScanningActivity";

	/** 最大扫描时间,超时提醒 70秒*/
	private final static int MAX_SCAN_SECOND = 1000 * 70;

	public final static String MSG_TIMEOUT = "msg_timeout";
	/** 扫描计时 */
	private SimpleAlarmTimer mScanTimer;
	private MessageDialog mTimeOutDialog;

	private static final String UTF8 = "UTF-8";
	private static final String ISO88591 = "ISO-8859-1";
	private static final String GBK = "GBK";
	//	private static final String GB2312 = "GB2312";

	private boolean hasSurface;
	private String characterSet = UTF8;

	private static final float BEEP_VOLUME = 0.10f;

	private CameraManager mCameraManager;
	private CaptureActivityHandler mCaptureHandler;
	private ViewfinderView viewfinderView;
	private Vector<BarcodeFormat> decodeFormats;
//	private InactivityTimer inactivityTimer;
	/** 从图片解码 */
	private ImageView mDecodeImgView;
	private ImageView mFlashImg;

	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	// 扫描结果(注意乱码)
	private String resultStr;

	private DroidLED droidLED;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImeHelper.setNoTitleAndInput(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		String strPhoneModel = Build.MODEL;
		if (strPhoneModel.equalsIgnoreCase("X10i") || strPhoneModel.equalsIgnoreCase("X10")) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		setContentView(R.layout.scanning_activity);

		findId();
		initData();
	}

	private void findId() {
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		mDecodeImgView = (ImageView) findViewById(R.id.img_decode);
		mFlashImg = (ImageView) findViewById(R.id.img_flash);
		mFlashImg.setOnClickListener(flashClicker);
		//从图片解码
		mDecodeImgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(ScanningActivity.this, DecodeImageActivity.class);
				startActivity(it);
				finish();
			}
		});
	}

	private void initData() {
		mScanTimer = new SimpleAlarmTimer(this, MSG_TIMEOUT, false, MAX_SCAN_SECOND, timerCallback);
		CameraManager.init(getApplication(), this);
		mCameraManager = CameraManager.get();
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		hasSurface = false;
//		inactivityTimer = new InactivityTimer(this);

		Display display = manager.getDefaultDisplay();
		// mCameraManager.setScreenResolution(display.getWidth(),
		// display.getHeight() - btn_cancel.getHeight() - 10);
		mCameraManager.setScreenResolution(display.getWidth(), display.getHeight());
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			mCameraManager.openDriver(surfaceHolder);
			setPromptView();
			mScanTimer.start();
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit(getText(R.string.dialog_title).toString(),
					getText(R.string.msg_camera_framework_bug).toString());
			Logger.getInstance(TAG).debug(ioe.getMessage());
			return;
		} catch (RuntimeException e) {
			displayFrameworkBugMessageAndExit(getText(R.string.dialog_title).toString(),
					getText(R.string.msg_camera_framework_bug).toString());
			Logger.getInstance(TAG).debug(e.getMessage());
			return;
		}
		if (mCaptureHandler == null) {
			mCaptureHandler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	// 解码结果 需要注意中文解码问题
	public void handleDecode(Result result, Bitmap barcode) {
		resultStr = result.getText();
		if (TextUtils.isEmpty(resultStr)) {
			// 停留在继续扫码界面 重新扫码
			mCaptureHandler.sendEmptyMessage(R.id.restart_preview);
		} else {
			mScanTimer.stop();
//			inactivityTimer.onActivity();
			viewfinderView.drawResultBitmap(barcode);
			playBeepSoundAndVibrate();
			//判断是VCard则跳到VcardActivity，显现
			if (VCardHandler.isVCardResult(result)) {
				//包裹vcard并传递
				VCardHandler vcard = new VCardHandler();
				vcard.setResult(result);
//				vcard.setCharacterSet(characterSet);
				VCardStore.getInstance().setVCardHandler(vcard);
				
				Intent vcardIntent = new Intent(this ,VcardActivity.class);
				startActivity(vcardIntent);
				finish();
				return;
			}
			ZMObject zmObject = ScanningcodeService.getInstance(this).getCacheZMObject(resultStr);
			if (zmObject == null) {
				// 本地缓存无数据
				//startWaitingDialog(getString(R.string.dialog_title), getString(R.string.loading));
				startWaitingDialog(null, getString(R.string.loading));
				ScanningcodeService.getInstance(this).getScanningInfo(resultStr, this);
			} else {
				openSpaceActivity(this, zmObject);
			}
		}
	}


	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		ZMObject zmObject = null;
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取扫码结果信息
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
					zmObject = getzmProtocol.getZMObject();
					if (zmObject == null) {
						// 作为非自有码
						zmObject = new UnknownObject();
						zmObject.setZMCode(resultStr);
					}
				} else {
					// 解析失败 作为非自有码
					zmObject = new UnknownObject();
					zmObject.setZMCode(resultStr);
				}
			}
		} else {
			// 网络错误
			zmObject = new ZMObject();
			zmObject.setZMCode(resultStr);
			zmObject.setZMObjectType(ZMObjectKind.NONETWORK);
		}
		if (zmObject == null) {
			zmObject = new ZMObject();
			zmObject.setZMCode(resultStr);
			zmObject.setZMObjectType(ZMObjectKind.NONETWORK);
		}
		// 跳转到相应页面
		openSpaceActivity(this, zmObject);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {

	}

	public void openSpaceActivity(BaseActivity scanningActivity, ZMObject zmObject) {
		Logger.getInstance(TAG).debug("openSpaceActivity zmObjectType:" + zmObject.getZMObjectType());
		ActivitySwitcher.openSpaceActivity(this, zmObject,true);
	}

	private static final long VIBRATE_DURATION = 200L;

	// --------------------------------------------------------------
	// 控制扫描方法
	// --------------------------------------------------------------
	private void initBeepSound() {
		if (mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		//根据设置播放与震动
		boolean isPlayBeep = SettingHelper.getBoolean(getApplicationContext(), Field.SETTING_SCAN_WARNINGTONE, true);
		if (isPlayBeep) {
			if (playBeep && mediaPlayer != null) {
				mediaPlayer.start();
			}
		}

		boolean isVibrate = SettingHelper.getBoolean(getApplicationContext(), Field.SETTING_SCAN_SHAKE, true);
		if (isVibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	//开启闪光灯
	private View.OnClickListener flashClicker = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//兼容检测：是否有特定的闪光灯。
			//XXX 封装到CameraManager
			if (DroidLED.isDroidLED()) {
				try {
					if (droidLED == null) {
						droidLED = new DroidLED();
					}
					droidLED.doSetFlash(!droidLED.isFlashOn());
				} catch (Exception e) {
					HaloToast.show(getApplicationContext(), getText(R.string.camera_flash_erroe).toString());
					Logger.getInstance(TAG).debug(e.getMessage());
				}
			} else {
				boolean flashStart = mCameraManager.isFlashOn();
				if (flashStart) {
					mCameraManager.doSetFlash(false);
				} else {
					if (!mCameraManager.doSetFlash(true)) {
						HaloToast.show(getApplicationContext(), getText(R.string.camera_flash_erroe).toString());
					}
				}
			}
			setFlashViewState();
		}
	};

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public void displayFrameworkBugMessageAndExit(String title, String info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(info);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.show();
	}

	// -------------------------------------------
	// SurfaceView 监听
	// -------------------------------------------
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			decodeFormats = null;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	// ----------------------------------------------------
	// setXxx() getXxx()方法
	// ----------------------------------------------------

	public Handler getHandler() {
		return mCaptureHandler;
	}

	// ----------------------------------------------------
	// 生命周期方法
	// ----------------------------------------------------
	@Override
	public void onPause() {
		super.onPause();
		if (mCaptureHandler != null) {
			mCaptureHandler.quitSynchronously();
			mCaptureHandler = null;
		}
		mCameraManager.stopPreview();
		mCameraManager.closeDriver();
		mScanTimer.stop();
	}

	@Override
	public void onDestroy() {
		dismissWaitingDialog();
//		inactivityTimer.shutdown();
		mScanTimer.stop();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		decodeFormats = null;
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public void onBackPressed() {
		if (isDialogShowing()) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	// ----------------------------------------------------------
	// 显示Dialog,注意Result
	private void showDiaglog(int titleId, int messageId) {
		if (mTimeOutDialog == null) {
			mTimeOutDialog = new MessageDialog(this, viewfinderView);
		}
		if (!mTimeOutDialog.isShowing()) {
			mTimeOutDialog.setMessage(R.string.scanning_fail);
			mTimeOutDialog.setTitle(R.string.dialog_title);
			mTimeOutDialog.setOnlyButton(R.string.ok);
			mTimeOutDialog.setFocusable(true);
			mTimeOutDialog.show();
			mTimeOutDialog.setOnOnlyBtClickListener(new OnOnlyBtClickListener() {
				@Override
				public void onLeftBtClick() {

				}
			});
			mTimeOutDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					mScanTimer.start();
				}
			});
		}
	}

	private SimpleAlarmTimerCallback timerCallback = new SimpleAlarmTimerCallback() {
		@Override
		public void onSimpleAlarmTimer(String message) {
			if (MSG_TIMEOUT.equals(message)) {
				showDiaglog(R.string.dialog_title, R.string.scanning_fail);
			}
		}
	};

	/** 设置提示view的位置 */
	private void setPromptView() {
		Rect mFramingRect = CameraManager.get().getFramingRect();
		if (mFramingRect.top > 0) {
			ViewGroup group = (ViewGroup) findViewById(R.id.layout_prompt);
			RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) group.getLayoutParams();
			lp.setMargins(-1, mFramingRect.bottom + 5, -1, -1);
			group.requestLayout();
			group.setVisibility(View.VISIBLE);
			setFlashViewState();
		}
	}

	private void setFlashViewState() {
		if (DroidLED.isDroidLED()) {
			if (droidLED != null) {
				if (droidLED.isFlashOn()) {
					mFlashImg.setImageResource(R.drawable.flash_off_selet);
				} else {
					mFlashImg.setImageResource(R.drawable.flash_on_selet);
				}
			}
		} else {
			if (mCameraManager.isFlashOn()) {
				mFlashImg.setImageResource(R.drawable.flash_off_selet);
			} else {
				mFlashImg.setImageResource(R.drawable.flash_on_selet);
			}
		}
	}

}