package com.zhima.ui.main.activity;



import java.io.File;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMStats;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.AppLaunchProtocolHandler.AutoUpgradeProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.base.utils.UmengStatUtil;
import com.zhima.data.model.AppVersion;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.RequestService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.CustomDialog;
import com.zhima.ui.common.view.CustomDialog.OnBtClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MsgDialog;
import com.zhima.ui.contact.activity.ContactActivity;
import com.zhima.ui.diary.activity.JigsawActivity;
import com.zhima.ui.retrieval.activity.RetrievalMainActivity;
import com.zhima.ui.setting.activity.SettingMainActivity;
import com.zhima.ui.tools.DownLoadNotification;
import com.zhima.ui.usercenter.watchdog.activity.MyZhiMaMainActivity;

/**
 * 首页
 * @ClassName: MainActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-27 上午11:47:10
*/
public class Main1Activity extends BaseActivity implements OnClickListener {
	private static final String TAG = Main1Activity.class.getSimpleName();
	
	private static final int SCANNING = 1;
	private static final int MYZHIMA = SCANNING + 1;
	private static final int RETRIEVAL = MYZHIMA + 1;
	private static final int SETTING = RETRIEVAL + 1;
	private static final int CONTACT = SETTING + 1;
	
	
	/** 扫码 */
	private LinearLayout mScaningBt;
	/** 我的芝麻 */
	private LinearLayout mMyZhimaBt;
	/** 检索 */
	private LinearLayout mRetrievalBt;
	/** 设置 */
	private LinearLayout mSettingBt;
	/** 通讯录 */
	private LinearLayout mContactBt;
	
	private ImageView mScaningImage;
	private ImageView mMyZhimaImage;
	private ImageView mRetrievalImage;
	private ImageView mSettingImage;
	private ImageView mContactImage;
	
	private TextView mStarryskyImage;
	private TextView mMeteorImage;
	private TextView mGrassImage;
	private TextView mLoveImage;
	private TextView mChiMarkImage;
	
	
	private MsgDialog mMsgDialog;
	private ZMLocationManager mZMLocationManager;
	
	private Animation mEnterAnimation;
	
	private boolean isAnimationShowing = false;
	private AnimationDrawable mLoveAnim;
	private AnimationDrawable mMeteorAnim;
	private AnimationDrawable mGrassAnim;
	private AnimationDrawable mStarryskyAnim;
	
	private boolean isMeteorShow = true;
	private AnimationDrawable mChiMarkAnim;
	protected DownLoadNotification mDownLoadNotification;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		findView();
		setUpView();
		updateVersion();
		initQuitDialog();
		mZMLocationManager = ZMLocationManager.getInstance();
		
		((ZhimaApplication)getApplication()).mMainActivityId = mActivityId;
	}
	
	private void updateVersion() {
		if(SettingHelper.getBoolean(this, Field.ISPROMPT_UPDATE_VERSION, true)){
			
			final File file = new File(FileHelper.getRootDir()+File.separator + SystemConfig.UPDATE_APK);
			if(file.exists()){
				MsgDialog updateDialog = new MsgDialog(this);
				updateDialog.setTitle("更新版本");
				updateDialog.setMessage("最新版本已下载完成，请安装。");
				updateDialog.setLeftBtText("下次安装");
				updateDialog.setRightBtText("立即安装");
				updateDialog.setOnBtClickListener(new com.zhima.ui.common.view.MsgDialog.OnBtClickListener() {
					
					@Override
					public void onRightBtClick() {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						startActivity(intent);
					}
					
					@Override
					public void onLeftBtClick() {
					}
				});
				updateDialog.show();
			}else{
				//检查是否有新版本！！
				AppLaunchService.getInstance(Main1Activity.this).checkAppUpdate(Main1Activity.this);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mScaningImage.setImageResource(R.drawable.main_scanning01);
		mMyZhimaImage.setImageResource(R.drawable.main_myzhima01);
		mRetrievalImage.setImageResource(R.drawable.main_retrieval01);
		mSettingImage.setImageResource(R.drawable.main_setting01);
		mContactImage.setImageResource(R.drawable.main_contact01);
		
		mScaningImage.setVisibility(View.VISIBLE);
		mMyZhimaImage.setVisibility(View.VISIBLE);
		mRetrievalImage.setVisibility(View.VISIBLE);
		mSettingImage.setVisibility(View.VISIBLE);
		mContactImage.setVisibility(View.VISIBLE);
		
		mLoveImage.setVisibility(View.GONE);
		mChiMarkImage.setVisibility(View.GONE);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
                displayAnimation();                        
        }
	}
	
	private void displayAnimation() {
		new Thread() {

			@Override
			public void run() {
				if(mStarryskyAnim!=null){
					mStarryskyAnim.start();
				}
				if(mGrassAnim!=null){
					mGrassAnim.start();
				}
				if(mMeteorAnim!=null){
					mMeteorAnim.start();
				}
				
//				if(mChiMarkAnim!=null){
//					mChiMarkAnim.start();
//				}
			}

		}.start();
	}

	private void initQuitDialog() {
		
		mMsgDialog = new MsgDialog(this);
		mMsgDialog.setTitle(R.string.quit);
		mMsgDialog.setMessage(R.string.quit_message);
		mMsgDialog.setLeftBtText(R.string.cancel);
		mMsgDialog.setRightBtText(R.string.ok);
		mMsgDialog.setOnBtClickListener(new MsgDialog.OnBtClickListener() {

			@Override
			public void onRightBtClick() {
				overridePendingTransition(R.anim.tran_out, R.anim.tran_in);
				((ZhimaApplication)Main1Activity.this.getApplication()).isLbsSucess = false;
				if (mDownLoadNotification != null) {
					mDownLoadNotification.cancel();
					mDownLoadNotification = null;
				}
				finish();
//				System.exit(0);
				((ZhimaApplication)Main1Activity.this.getApplication()).popAllActivity();
				ZhimaApplication.exitApplication(getWindow().getContext());
			}

			@Override
			public void onLeftBtClick() {

			}
		});
	}

	private void findView() {
		mScaningBt = (LinearLayout) this.findViewById(R.id.layout_main_scaning);
		mMyZhimaBt = (LinearLayout) this.findViewById(R.id.layout_main_myzhima);
		mRetrievalBt = (LinearLayout) this.findViewById(R.id.layout_main_retrieval);
		mSettingBt = (LinearLayout) this.findViewById(R.id.layout_main_setting);
		mContactBt = (LinearLayout) this.findViewById(R.id.layout_main_contact);
		
		mScaningImage = (ImageView) this.findViewById(R.id.img_main_scaning);
		mMyZhimaImage = (ImageView) this.findViewById(R.id.img_main_myzhima);
		mRetrievalImage = (ImageView) this.findViewById(R.id.img_main_retrieval);
		mSettingImage = (ImageView) this.findViewById(R.id.img_main_setting);
		mContactImage = (ImageView) this.findViewById(R.id.img_main_contact);
		
		mStarryskyImage = (TextView) this.findViewById(R.id.img_main_starrysky);
		mMeteorImage = (TextView) Main1Activity.this.findViewById(R.id.img_main_meteor);
		mGrassImage = (TextView) this.findViewById(R.id.img_main_grass);
		mLoveImage = (TextView) this.findViewById(R.id.img_main_love);
		mChiMarkImage = (TextView) this.findViewById(R.id.img_main_chimark);
		
	}

	private void setUpView() {
		mScaningBt.setOnClickListener(this);
		mMyZhimaBt.setOnClickListener(this);
		mRetrievalBt.setOnClickListener(this);
		mSettingBt.setOnClickListener(this);
		mContactBt.setOnClickListener(this);
		
		setOnTouchListener(mScaningBt, SCANNING);
		setOnTouchListener(mMyZhimaBt, MYZHIMA);
		setOnTouchListener(mRetrievalBt, RETRIEVAL);
		setOnTouchListener(mSettingBt, SETTING);
		setOnTouchListener(mContactBt, CONTACT);
		
		mEnterAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.01f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mEnterAnimation.setDuration(300);
		
		mStarryskyAnim = (AnimationDrawable) mStarryskyImage.getBackground();
		mGrassAnim = (AnimationDrawable) mGrassImage.getBackground();
		mMeteorAnim = (AnimationDrawable) mMeteorImage.getBackground();
		mLoveAnim = (AnimationDrawable) mLoveImage.getBackground();
		mChiMarkAnim = (AnimationDrawable) mChiMarkImage.getBackground();
	
	}
	
//	/**
//	 * ViewPager定时任务
//	*/
//	class ViewPagerTask implements Runnable {
//
//		@Override
//		public void run() {
//			handler.sendEmptyMessage(0);
//		}
//	}
//	
//	Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			
//			if(isMeteorShow){
//				mMeteorImage.setVisibility(View.INVISIBLE);
//				isMeteorShow = false;
//			}else{
//				mMeteorImage.setVisibility(View.VISIBLE);
//				isMeteorShow = true;
//			}
//		}
//	};

	@Override
	public void onClick(final View v) {
		
		if(isAnimationShowing){
			return;
		}
		isAnimationShowing = true;
		
		mLoveImage.setVisibility(View.VISIBLE);
		mChiMarkImage.setVisibility(View.VISIBLE);
//		CustomAnimDrawable customAnimDrawable = new CustomAnimDrawable(loveAnim);
//		customAnimDrawable.setAnimationListener(new AnimationDrawableListener() {
//			
//			@Override
//			public void onAnimationStart(AnimationDrawable animation) {
//			}
//			
//			@Override
//			public void onAnimationEnd(AnimationDrawable animation) {
//			}
//		});
		mLoveAnim.setOneShot(true);
		mLoveAnim.start();
		mChiMarkAnim.setOneShot(true);
		mChiMarkAnim.start();
		openIntent(v);
	}

	private void openIntent(View v){
		
		switch (v.getId()) {
		case R.id.layout_main_scaning:
			final Intent scanIntent = new Intent(this,JigsawActivity.class);
			setAnimIntent(mScaningImage, scanIntent);
			UmengStatUtil.onEvent(this, ZMStats.SCANNING_EVENT);
			break;
		case R.id.layout_main_myzhima:
			Intent myZhiMaIntent = new Intent(this,MyZhiMaMainActivity.class);
			setAnimIntent(mMyZhimaImage, myZhiMaIntent);
			UmengStatUtil.onEvent(this, ZMStats.MYZHIMA_EVENT);
//			final ZhimaDatePickerDialog tt = new ZhimaDatePickerDialog(this, mMyZhimaBt);
//			tt.setOnBtClickListener(new OnBtClickListener() {
//				
//				@Override
//				public void onRightBtClick() {
//					// TODO Auto-generated method stub
//					tt.dismiss();
//				}
//				
//				@Override
//				public void onLeftBtClick() {
//					// TODO Auto-generated method stub
//					tt.dismiss();
//				}
//			});
//			tt.show();
			break;
		case R.id.layout_main_retrieval:
			//检索里面开启这里不需要
			Intent retrievalIntent = new Intent(this,RetrievalMainActivity.class);
			setAnimIntent(mRetrievalImage, retrievalIntent);
			break;
		case R.id.layout_main_setting:
			Intent settingIntent = new Intent(this,SettingMainActivity.class);
			setAnimIntent(mSettingImage, settingIntent);
			break;
		case R.id.layout_main_contact:
			Intent contactIntent = new Intent(this,ContactActivity.class);
			setAnimIntent(mContactImage, contactIntent);
			UmengStatUtil.onEvent(this, ZMStats.CONTACT_EVENT);
			break;
		}
	}
	
	
	
	private void setAnimIntent(final View view,final Intent intent){
		mEnterAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				isAnimationShowing = true;
				
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.INVISIBLE);
				Main1Activity.this.startActivity(intent);
				isAnimationShowing = false;
			}
		});
		view.startAnimation(mEnterAnimation);
	}
	
	private void setOnTouchListener(View view,final int viewId){
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(isAnimationShowing){
					return true;
				}
				
				int[] location = new int[2];
				v.getLocationOnScreen(location);
				
				int left = location[0];
				int top = location[1];
				int right = left + v.getWidth();
				int bottom = top + v.getHeight();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					switch (viewId) {
					case SCANNING:
						mScaningImage.setImageResource(R.drawable.main_scanning02);
						mMyZhimaImage.setImageResource(R.drawable.main_myzhima03);
						mRetrievalImage.setImageResource(R.drawable.main_retrieval03);
						mSettingImage.setImageResource(R.drawable.main_setting03);
						mContactImage.setImageResource(R.drawable.main_contact03);
						break;
					case RETRIEVAL:
						mScaningImage.setImageResource(R.drawable.main_scanning03);
						mMyZhimaImage.setImageResource(R.drawable.main_myzhima03);
						mRetrievalImage.setImageResource(R.drawable.main_retrieval02);
						mSettingImage.setImageResource(R.drawable.main_setting03);
						mContactImage.setImageResource(R.drawable.main_contact03);
						break;
					case MYZHIMA:
						mScaningImage.setImageResource(R.drawable.main_scanning03);
						mMyZhimaImage.setImageResource(R.drawable.main_myzhima02);
						mRetrievalImage.setImageResource(R.drawable.main_retrieval03);
						mSettingImage.setImageResource(R.drawable.main_setting03);
						mContactImage.setImageResource(R.drawable.main_contact03);
						break;
					case SETTING:
						mScaningImage.setImageResource(R.drawable.main_scanning03);
						mMyZhimaImage.setImageResource(R.drawable.main_myzhima03);
						mRetrievalImage.setImageResource(R.drawable.main_retrieval03);
						mSettingImage.setImageResource(R.drawable.main_setting02);
						mContactImage.setImageResource(R.drawable.main_contact03);
						break;
					case CONTACT:
						mScaningImage.setImageResource(R.drawable.main_scanning03);
						mMyZhimaImage.setImageResource(R.drawable.main_myzhima03);
						mRetrievalImage.setImageResource(R.drawable.main_retrieval03);
						mSettingImage.setImageResource(R.drawable.main_setting03);
						mContactImage.setImageResource(R.drawable.main_contact02);
						break;
					}
					
					break;
				case MotionEvent.ACTION_MOVE:
					
					float eventX = event.getRawX();
					float eventY = event.getRawY();
					
					if((eventX<left || eventX>right || eventY<top || eventY>bottom)){
						mScaningImage.setImageResource(R.drawable.main_scanning01);
						mMyZhimaImage.setImageResource(R.drawable.main_myzhima01);
						mRetrievalImage.setImageResource(R.drawable.main_retrieval01);
						mSettingImage.setImageResource(R.drawable.main_setting01);
						mContactImage.setImageResource(R.drawable.main_contact01);
					}
					
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}
		});
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
	}
	@Override
	public void onHttpResult(final ProtocolHandlerBase protocol) {
		int protocolType = protocol.getProtocolType();
		if (protocol.isHttpSuccess()) {
			if (protocolType == ProtocolType.CHECK_APP_UPGRADE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// TODO 版本检查处理
					AutoUpgradeProtocolHandler p = (AutoUpgradeProtocolHandler) protocol;
					final AppVersion newVersion = p.getAppUpgrade()
							.getLatestVersion();
					// newVersion.setDowloadUrl("http://gdown.baidu.com/data/wisegame/bbd1f7c530a8d201/360weishi.apk");
					if (AppLaunchService.getInstance(Main1Activity.this)
							.canUpgrade(newVersion)) {
						// TODO 有新版本
//						MsgDialog dialog = new MsgDialog(this);
//						dialog.setTitle("提示");
//						dialog.setMessage("检测到新版本,是否更新？");
//						dialog.setLeftBtText("取消");
//						dialog.setRightBtText("确定");
//						dialog.setOnBtClickListener(new OnBtClickListener() {
//							@Override
//							public void onRightBtClick() {
//								HaloToast.show(getApplicationContext(), "文件下载中，请稍后...");
//								Intent intent = new Intent(MainActivity.this,
//										MainActivity.class);
//								mDownLoadNotification = new DownLoadNotification(MainActivity.this, intent, "正在下载芝麻客...");
//								RequestService.getInstance(MainActivity.this).downLoadUpdateFile(mDownLoadNotification,newVersion.getDowloadUrl(),MainActivity.this);
//							}
//
//							@Override
//							public void onLeftBtClick() {
//							}
//						});
//						dialog.show();
						
						CustomDialog dialog = new CustomDialog(this, mContactBt);
						dialog.setIconVisible(true);
						dialog.setIconSrc(R.drawable.tip_icon);
						dialog.setTitle("发现新版本");
						dialog.setLeftBtText("以后再说");
						dialog.setRightBtText("立即更新");
						View view = View.inflate(Main1Activity.this, R.layout.version_update_dialog_view, null);
						
						//当前版本
						String oldVersionName = AppLaunchService.getInstance(Main1Activity.this).getVersionName();
						//新版本
						String newVersionName = newVersion.getVersion();
						//描述
						String description = newVersion.getDescription();
						
						TextView oldVersionText = (TextView) view.findViewById(R.id.txt_version_update_currentVersion);
						TextView newVersionText = (TextView) view.findViewById(R.id.txt_version_update_newVersion);
						TextView descriptionText = (TextView) view.findViewById(R.id.txt_version_update_updateContent);
						LinearLayout mUpdateScroll =  (LinearLayout) view.findViewById(R.id.layout_version_update_desc);
						
						if(description.length()>80){
							mUpdateScroll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,GraphicUtils.dip2px(Main1Activity.this, 150)));
						}
						
						oldVersionText.setText("当前版本："+oldVersionName);
						newVersionText.setText("最新版本："+newVersionName);  
						descriptionText.setText(description);
						
						CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_isPrompt_update);
						checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								SettingHelper.setBoolean(Main1Activity.this, Field.ISPROMPT_UPDATE_VERSION, !isChecked);
							}
						});
						
						
						dialog.setMiddleLayout(view);
						dialog.setOnBtClickListener(new OnBtClickListener() {						
							@Override
							public void onRightBtClick() {
								HaloToast.show(getApplicationContext(), "文件下载中，请稍侯...");
								Intent intent = new Intent(Main1Activity.this,Main1Activity.class);
								mDownLoadNotification = new DownLoadNotification(Main1Activity.this, intent, "正在下载芝麻客...");
								RequestService.getInstance(Main1Activity.this).downLoadUpdateFile(null,mDownLoadNotification, newVersion.getDownloadUrl(), Main1Activity.this);				
							}						
							@Override
							public void onLeftBtClick() {							
							}
						});
						dialog.show();
						
					} else {
						// TODO 没有新版本
					}
				} else {
				}
			} else if (protocolType == ProtocolType.DOWN_FILE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {

				} else {
					if (mDownLoadNotification != null) {
						mDownLoadNotification.cancel();
						mDownLoadNotification = null;
					}
//					HaloToast.show(getApplicationContext(), " 下载文件失败");
				}
			}
		} else {
			// TODO 网络连接错误
			if (mDownLoadNotification != null) {
				mDownLoadNotification.cancel();
				mDownLoadNotification = null;
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mMsgDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}  
