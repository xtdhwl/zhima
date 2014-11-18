package com.zhima.ui.setting.activity;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageQuality;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.AppLaunchProtocolHandler.AutoUpgradeProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.model.AppVersion;
import com.zhima.data.model.ZMSpaceKind;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.LoginService;
import com.zhima.data.service.RequestService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomDialog;
import com.zhima.ui.common.view.CustomDialog.OnBtClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MsgDialog;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.space.zmspace.activity.ReputationSpaceIntroActivity;
import com.zhima.ui.space.zmspace.activity.PassedRecordActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpacePlazaActivity;
import com.zhima.ui.space.zmspace.activity.ZmSpaceVisitedCommentActivity;
import com.zhima.ui.tools.DownLoadNotification;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 设置
 * @ClassName: SettingMainActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-24 上午11:43:28
*/
public class SettingMainActivity extends BaseActivity implements OnItemClickListener {

	private ListView mSettingList;
	private View mChildrenView;
	
	private String[] mTitles = {"扫码设置","绑定账号","图片优化","新版本检测", "数据同步","清除缓存","意见反馈"/*, "帮助指南"*/, "关于芝麻"/*,"设置服务器路径"*/};
	
	private List<String> mTitleArray;
	
	private PopupWindow mImageSettingPop;
	
	/** 图片分辨率 */
	private int mImageQuality;
	
	
	private RadioButton mHighRb;
	private RadioButton mCommonRb;
	private RadioButton mLowRb;
	
	/** 图片优化选择标志 高清1 普通2 低画质3*/
	private int mRadioButtonFlag = 1;
	
	private DownLoadNotification mDownLoadNotification;
	
	private View mPopParentView;
	
	
	private static final int DOWNLOAD_SUCCESS = 1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case DOWNLOAD_SUCCESS:
				File file = (File) msg.obj;
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				startActivity(intent);
				break;
			}
		}
	};
	private View mDialogParentView;
//	private SideBarView mSideBarView;
	
	private boolean isDownloading = false;
	
	private View mFooterLogoutView;
	private TextView mLogoutText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTitleArray = Arrays.asList(mTitles);
		mImageQuality = SettingHelper.getInt(this, Field.SETTING_IMAGE_OPTIMIZE, 1);
		
		init();
		setSidebar();
		findView();
		setUpView();
	}

	private void init() {
		isDownloading = false;
	}

	private void setSidebar() {   
		mChildrenView = View.inflate(this, R.layout.setting_main_activity,null);
		setContentView(mChildrenView);
		
		ViewInitTools.setTopBar(this, "设置", View.GONE, null);
		
//		mChildrenView = View.inflate(this, R.layout.setting_main_activity,null);
//
//		mSideBarView = new SideBarView(this,this, "设置");
//		mSideBarView.setChildView(mChildrenView);
//		setContentView(mSideBarView.getContentView());
//		
//		/*--------------- 透明View --------------------------------*/
//		
//		final View tranView = (View) mSideBarView.getContentView().findViewById(
//				R.id.view_transparent);
//		tranView.setVisibility(View.GONE);
//		tranView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mSideBarView.scrollView();
//			}
//		});
//		tranView.setClickable(false);
//		mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {
//
//			@Override
//			public void onStateChange(boolean isMenuOut) {
//				if (isMenuOut) {
//					tranView.setVisibility(View.VISIBLE);
//					tranView.setClickable(true);
//				} else {
//					tranView.setVisibility(View.GONE);
//					tranView.setClickable(false);
//				}
//			}
//		});
	
		/*---------------------------------------------------*/
	}

	private void findView() {
		mSettingList = (ListView) mChildrenView.findViewById(R.id.lstv_setting_main_listview);
		
		mFooterLogoutView = View.inflate(this, R.layout.setting_main_item, null);
		mLogoutText = (TextView) mFooterLogoutView.findViewById(R.id.txt_setting_main_item_title);
		mLogoutText.setText("退出登录");
	}

	private void setUpView() {
//		mSettingList.addFooterView(View.inflate(this, R.layout.customlist_else_view, null), null, false);
		
		mSettingList.setOnItemClickListener(this);
		
		if(AccountService.getInstance(this).isLogin()){
			mSettingList.addFooterView(mFooterLogoutView);
		}
		
		mFooterLogoutView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginService.getInstance(SettingMainActivity.this).logout(new IHttpRequestCallback() {
					
					@Override
					public void onHttpStart(ProtocolHandlerBase protocol) {
						startWaitingDialog("", "正在退出...");
					}
					
					@Override
					public void onHttpResult(ProtocolHandlerBase protocol) {
						if(protocol.isHttpSuccess()){
							if(protocol.isHandleSuccess()){
								HaloToast.show(getApplicationContext(), "退出成功");
								mSettingList.removeFooterView(mFooterLogoutView);
								mFooterLogoutView.setVisibility(View.GONE);
								
								
							}else{
								HaloToast.show(getApplicationContext(), "退出失败");
							}
						}else{
							HaloToast.show(getApplicationContext(), R.string.network_request_failed);
						}
						dismissWaitingDialog();
					}
				});
			}
		});
		
		mSettingList.setAdapter(new ZhimaAdapter<String>(this,R.layout.setting_main_item, mTitleArray) {

			@Override
			public Object createViewHolder(View view, String data) {
				ViewHolder holder = new ViewHolder();
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_setting_main_item_title);
				return holder;
			}

			@Override
			public void bindView(String data, int position, View view) {
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				holder.mTitleText.setText(data);
			}
			
			class ViewHolder{
				TextView mTitleText;
			}
		});
		
	}
	private final class SettingOption {
		/**
		 * 扫码设置
		 */
		public final static int SCANNING = 0;
		/**
		 * 分享绑定
		 */
		public final static int SHARE_CONFIG = SCANNING + 1;
		/**
		 * 图片优化
		 */
		public final static int IMAGE_QUALITY = SHARE_CONFIG + 1;
		/**
		 * 版本检测
		 */
		public final static int CHECK_UPGRADE = IMAGE_QUALITY + 1;
		/**
		 * 数据同步
		 */
		public final static int SYNC_DATA = CHECK_UPGRADE + 1;
		
		/**
		 * 清除缓存
		 */
		public final static int CLEAR_CACHE = SYNC_DATA + 1;
		/**
		 * 清除缓存
		 */
		public final static int IDEA_FEEDBACK = CLEAR_CACHE + 1;
//		/**
//		 * 帮助指南
//		 */
//		public final static int HELP = IDEA_FEEDBACK + 1;
		/**
		 * 关于芝麻
		 */
		public final static int ABOUT = IDEA_FEEDBACK + 1;
//		/**
//		 * 退出登录
//		 */
//		public final static int LOGOUT = ABOUT + 1;
		/**
		 * 服务器路径
		 */
		public final static int SERVER_WAY = ABOUT + 1;
	}
	@Override
	public void onItemClick(AdapterView<?> parent,  final View view, int position,
			long id) {
		switch (position) {
		case SettingOption.SCANNING:
			Intent scanIntent = new Intent(this,ScanSettingActivity.class);
			startActivity(scanIntent);
//			CustomLoadDialog sdialog = new CustomLoadDialog(this);
//			sdialog.show();
			break;
		case SettingOption.SHARE_CONFIG:
			Intent shareIntent = new Intent(this,ShareConfigActivity.class);
			shareIntent.putExtra(ShareConfigActivity.QQ_SHARE_TYPE, -1);
			startActivity(shareIntent);
			break;
		case SettingOption.IMAGE_QUALITY:
			setImageOptimizeDialog(view);
			break;
		case SettingOption.CHECK_UPGRADE:	
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
//				AppLaunchService.getInstance(MainActivity.this).checkAppUpdate(MainActivity.this);
				checkVersion(view);			
			}
			break;
		case SettingOption.SYNC_DATA:			
			AppLaunchService.getInstance(this).checkDictUpdate(this);
			startWaitDialog();
			break;
		case SettingOption.CLEAR_CACHE:
			new ClearCacheTask().execute();
//			Intent boutZhimaIntent = new Intent(this,ZmSpaceVisitedCommentActivity.class);
//			startActivity(boutZhimaIntent);
			break;
		case SettingOption.IDEA_FEEDBACK:
			Intent ideaIntent = new Intent(this,IdeaFeedBackActivity.class);
			startActivity(ideaIntent);
			break;
//		case SettingOption.HELP:
//			Intent helpIntent = new Intent(this,HelpGuideActivity.class);
//			startActivity(helpIntent);
//			break;
		case SettingOption.ABOUT:
			Intent aboutZhimaIntent = new Intent(this,AboutZhimaActivity.class);
			startActivity(aboutZhimaIntent);
			break;
//		case SettingOption.LOGOUT:
//			if(AccountService.getInstance(this).isLogin()){
//				view.setVisibility(View.GONE);
//			}else{
//				LoginService.getInstance(this).logout(new IHttpRequestCallback() {
//					
//					@Override
//					public void onHttpStart(ProtocolHandlerBase protocol) {
//						startWaitingDialog("", "正在退出...");
//					}
//					
//					@Override
//					public void onHttpResult(ProtocolHandlerBase protocol) {
//						if(protocol.isHttpSuccess()){
//							if(protocol.isHandleSuccess()){
//								HaloToast.show(getApplicationContext(), "退出成功");
//								view.setVisibility(View.GONE);
//							}else{
//								HaloToast.show(getApplicationContext(), "退出失败");
//							}
//						}else{
//							HaloToast.show(getApplicationContext(), R.string.network_request_failed);
//						}
//					}
//				});
//			}
//			break;
		case SettingOption.SERVER_WAY:
			mPopParentView = view;
			CustomDialog dialog = new CustomDialog(SettingMainActivity.this, mPopParentView); 
			dialog.setTitle("设置服务器路径");
			final EditText editText = new EditText(SettingMainActivity.this);
			editText.setText(AppLaunchService.getInstance(SettingMainActivity.this).getFixedServerUrl());
			dialog.setMiddleLayout(editText);
			dialog.setLeftBtText("取消");
			dialog.setRightBtText("保存");
			dialog.setOnBtClickListener(new CustomDialog.OnBtClickListener() {
				
				@Override
				public void onRightBtClick() {
					String serverWay = editText.getText().toString().trim();
					if(!TextUtils.isEmpty(serverWay)){
						AppLaunchService.getInstance(SettingMainActivity.this).setFixedServerUrl(serverWay);
						AccountService.getInstance(SettingMainActivity.this).clearSetting();
						AccountService.getInstance(SettingMainActivity.this).setFirstStartup(true);
						HaloToast.show(SettingMainActivity.this, "保存成功");
					}
				}
				
				@Override
				public void onLeftBtClick() {
					
				}
			});
			dialog.show();
			break;
		}
	}
	
	public final class ClearCacheTask extends AsyncTask<Void, Void, Void> {
		
		
		@Override
		protected void onPreExecute() {
			startWaitingDialog("", "正在清除...");
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			HaloToast.show(SettingMainActivity.this, "清除完成");
			dismissWaitingDialog();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
			FileHelper.deleteFolder(FileHelper.getCacheFileRoot());
			return null;
		}
		
	}
	
	/**
	 * 检测版本
	 */
	private void checkVersion(View view) {
		mDialogParentView = view;
		AppLaunchService.getInstance(SettingMainActivity.this).checkAppUpdate(SettingMainActivity.this);
		startWaitDialog();
	}

	/**
	 * 图片优化对话框
	 * @param view
	 */
	private void setImageOptimizeDialog(View view){
		if(mImageSettingPop==null){
			View popView = View.inflate(this, R.layout.setting_image_optimize_dialogview, null);
			mImageSettingPop = new PopupWindow(popView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
			RelativeLayout mHighLayout = (RelativeLayout) popView.findViewById(R.id.layout_setting_imageOptimize_highDefinition);
			RelativeLayout mCommonLayout = (RelativeLayout) popView.findViewById(R.id.layout_setting_imageOptimize_common);
			RelativeLayout mLowLayout = (RelativeLayout) popView.findViewById(R.id.layout_setting_imageOptimize_lowQuality);
			
			mHighRb = (RadioButton) popView.findViewById(R.id.rb_setting_imageOptimize_highDefinition);
			mCommonRb = (RadioButton) popView.findViewById(R.id.rb_setting_imageOptimize_common);
			mLowRb = (RadioButton) popView.findViewById(R.id.rb_setting_imageOptimize_lowQuality);
			
			initImageOptimizeState();
			
			mHighRb.setClickable(false);
			mCommonRb.setClickable(false);
			mLowRb.setClickable(false);
			
			mHighLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mImageQuality = ImageQuality.HIGH;
					mRadioButtonFlag = 1;
					changeImageOptimizeState();
				}
			});
			mCommonLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mImageQuality = ImageQuality.NORMAL;
					mRadioButtonFlag = 2;
					changeImageOptimizeState();
				}
			});
			mLowLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mImageQuality = ImageQuality.LOW;
					mRadioButtonFlag = 3;
					changeImageOptimizeState();
				}
			});
			
			mImageSettingPop.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_black_tran)));
			mImageSettingPop.setAnimationStyle(R.style.customDialog_anim_style);
		}else{
			initImageOptimizeState();
		}
		mImageSettingPop.showAtLocation(view, Gravity.CENTER, 0, 0);
	}
	
	private void initImageOptimizeState(){
		switch (mImageQuality) {
		case ImageQuality.HIGH:
			mHighRb.setChecked(true);
			break;
		case ImageQuality.NORMAL:
			mCommonRb.setChecked(true);
			break;
		case ImageQuality.LOW:
			mLowRb.setChecked(true);
			break;
		}
	}
	private void changeImageOptimizeState(){
		mImageSettingPop.dismiss();
		SettingHelper.setInt(SettingMainActivity.this, Field.SETTING_IMAGE_OPTIMIZE,mImageQuality);
		if(mRadioButtonFlag == 1){
			mHighRb.setChecked(true);
			mCommonRb.setChecked(false);
			mLowRb.setChecked(false);
		}else if(mRadioButtonFlag == 2){
			mCommonRb.setChecked(true);
			mHighRb.setChecked(false);
			mLowRb.setChecked(false);
		}else if(mRadioButtonFlag == 3){
			mLowRb.setChecked(true);
			mHighRb.setChecked(false);
			mCommonRb.setChecked(false);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(mImageSettingPop!=null){
				mImageSettingPop.dismiss();
			}
//			if(mSideBarView.onKeyBack()){
//				return false;
//			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void startWaitDialog() {
		startWaitingDialog("", "请稍等...");
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
	}
	/* (non-Javadoc)
	 * @see com.zhima.ui.activity.BaseActivity#onHttpResult(com.zhima.base.protocol.ProtocolHandlerBase)
	 */
	@Override
	public void onHttpResult(final ProtocolHandlerBase protocol) {
		int protocolType = protocol.getProtocolType();
			if (protocol.isHttpSuccess()) {
				if (protocolType == ProtocolType.CHECK_APP_UPGRADE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// TODO 版本检查处理
					AutoUpgradeProtocolHandler p = (AutoUpgradeProtocolHandler)protocol;
					final AppVersion newVersion = p.getAppUpgrade().getLatestVersion();
//					newVersion.setDowloadUrl("http://gdown.baidu.com/data/wisegame/bbd1f7c530a8d201/360weishi.apk");
					if ( AppLaunchService.getInstance(SettingMainActivity.this).canUpgrade(newVersion) ) {
						// TODO 有新版本
//						MessageDialog dialog = new MessageDialog(this, mDialogParentView);
//						dialog.setTitle("提示");
//						dialog.setMessage("检测到新版本,是否更新？");
//						dialog.setLeftBtText("取消");
//						dialog.setRightBtText("确定");
//						dialog.setOnBtClickListener(new OnBtClickListener() {						
//							@Override
//							public void onRightBtClick() {
//								Intent intent = new Intent(SettingMainActivity.this,SettingMainActivity.class);
//								mDownLoadNotification = new DownLoadNotification(SettingMainActivity.this, intent, "正在下载芝麻客...");
//								RequestService.getInstance(SettingMainActivity.this).downLoadUpdateFile(mDownLoadNotification, newVersion.getDowloadUrl(), SettingMainActivity.this);				
//							}						
//							@Override
//							public void onLeftBtClick() {							
//							}
//						});
//						dialog.show();
						
						String version = newVersion.getVersion();
						
						CustomDialog dialog = new CustomDialog(this, mDialogParentView);
						dialog.setIconVisible(true);
						dialog.setIconSrc(R.drawable.tip_icon);
						dialog.setTitle("发现新版本");
						dialog.setLeftBtText("以后再说");
						dialog.setRightBtText("立即更新");
						View view = View.inflate(SettingMainActivity.this, R.layout.version_update_dialog_view, null);
						
						//当前版本
						String oldVersionName = AppLaunchService.getInstance(SettingMainActivity.this).getVersionName();
						//新版本
						String newVersionName = newVersion.getVersion();
						//描述
						String description = newVersion.getDescription();
						
						TextView oldVersionText = (TextView) view.findViewById(R.id.txt_version_update_currentVersion);
						TextView newVersionText = (TextView) view.findViewById(R.id.txt_version_update_newVersion);
						TextView descriptionText = (TextView) view.findViewById(R.id.txt_version_update_updateContent);
						
						LinearLayout mUpdateScroll =  (LinearLayout) view.findViewById(R.id.layout_version_update_desc);
						
						if(description.length()>80){
							mUpdateScroll.setLayoutParams(new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,GraphicUtils.dip2px(SettingMainActivity.this, 150)));
						}
						
						oldVersionText.setText("当前版本："+oldVersionName);
						newVersionText.setText("最新版本："+newVersionName);
						descriptionText.setText(description);
						    
						//是否开启应用提示更新。
						CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_isPrompt_update);
						checkBox.setChecked(!SettingHelper.getBoolean(this, Field.ISPROMPT_UPDATE_VERSION, true));
						checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								SettingHelper.setBoolean(SettingMainActivity.this, Field.ISPROMPT_UPDATE_VERSION, !isChecked);
							}
						});
						
						dialog.setMiddleLayout(view);
						dialog.setOnBtClickListener(new OnBtClickListener() {						
							@Override
							public void onRightBtClick() {
								if(isDownloading){
									HaloToast.show(getApplicationContext(), "最新版本已经在下载中，请下载完后进行安装。");
									return;
								}
								isDownloading = true;
								Intent intent = new Intent(SettingMainActivity.this,SettingMainActivity.class);
								mDownLoadNotification = new DownLoadNotification(SettingMainActivity.this, intent, "正在下载芝麻客...");
								RequestService.getInstance(SettingMainActivity.this).downLoadUpdateFile(SettingMainActivity.this,mDownLoadNotification, newVersion.getDownloadUrl(), SettingMainActivity.this);				
							}						
							@Override
							public void onLeftBtClick() {	
								
							}
						});
						dialog.show();
					} else {
						// TODO 没有新版本
						HaloToast.show(SettingMainActivity.this, R.string.no_new_version, 0);
					}					
				}else{
					HaloToast.show(getApplicationContext(), " 检查版本失败");
				}
			} else if(protocolType == ProtocolType.DOWN_FILE_PROTOCOL){
				if(protocol.isHandleSuccess()){
					
				}else{
					if(mDownLoadNotification!=null){
						mDownLoadNotification.cancel();
						mDownLoadNotification = null;
					}
					HaloToast.show(getApplicationContext(), " 下载文件失败");
					isDownloading = false; 
				}
			}else if(protocolType == ProtocolType.CHECK_DICT_UPDATE_PROTOCOL){
				if(protocol.isHandleSuccess()){
					HaloToast.show(getApplicationContext(), " 数据同步成功");
				}else{
					HaloToast.show(getApplicationContext(), " 数据同步失败");
				}
			}
		}else {
			// TODO 网络连接错误
			if(mDownLoadNotification!=null){
				mDownLoadNotification.cancel();
				mDownLoadNotification = null;
			}
			HaloToast.show(this, R.string.network_request_failed);
			isDownloading = false;
		}
		dismissWaitingDialog();
	}
	
	public void setDownstate(boolean isDownloading){
		this.isDownloading = isDownloading;
	}
	
}
