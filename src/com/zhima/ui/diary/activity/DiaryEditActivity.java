package com.zhima.ui.diary.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.DiaryPrivacyStatus;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.FaceAdapter;
import com.zhima.ui.common.view.CircleFlowIndicator;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.SelectListDialog;
import com.zhima.ui.common.view.SelectListDialog.OnBtItemClicklistener;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.FaceHelper.FaceHolder;
import com.zhima.ui.tools.IntentHelper;
import com.zhima.ui.tools.LocalImageLoader;
import com.zhima.ui.tools.UserUtils;

/**
 * @ClassName: DiaryEditeActivity
 * @Description: 日志编辑
 * @author luqilong
 * @date 2013-1-14 下午2:40:42
 */
public class DiaryEditActivity extends BaseActivity {

	protected static final String TAG = DiaryEditActivity.class.getSimpleName();

	/** 默认日志状态 */
	private static final int DEFALUT_DIARY_STATUE = DiaryPrivacyStatus.PUBLIC;

	private static final int REQUEST_GALLERY_CODE = 0;
	private static final int REQUEST_CAMERA_CODE = 2;
	private static final int REQUEST_CROP_CODE = 3;
	private static final int request_send_code = 4;
	//剪切头像大小
	private static final int CROP_ASPECTX = 80;
	private static final int CROP_ASPECTY = 80;

	/** 保存照相机拍照结果本地路径 */
	private String mPhotoPath;
	private ZMDiary mZMDiary = null;
	private long mSpaceId;

	/** 日志状态标题栏 */
	private ImageView mDiaryStateView;
	/** 日志标题view */
	private EditText mTitleView;
	/** 日志内容view */
	private EditText mContentView;
	/** 显示表情view */
	private ImageView mFaceView;
	/** 选择相片view */
	private ImageView mCameraView;
	//XXX 表情（优化封装）
	private ViewFlow mViewFlow;
	private ViewGroup mFaceLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (GraphicUtils.mScreenHeight > 500) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		}
		setContentView(R.layout.diary_edit_activity);
		setTopbar();
		findView();

		//初始化日志
		mZMDiary = getZMDiary();

		Intent intent = getIntent();
		mSpaceId = intent.getLongExtra(DiaryConsts.space_id, -1);

		//初始化缓存内容
		String title = null;
		String content = null;
		String imageUrl = null;
		int status = -1;
		if (savedInstanceState != null) {
			title = savedInstanceState.getString("title");
			content = savedInstanceState.getString("content");
			imageUrl = savedInstanceState.getString("imageUrl");
			status = savedInstanceState.getInt("status");
			mZMDiary.setTitle(title);
			//XXX 包含表情
			mZMDiary.setContent(content);
			mZMDiary.setImageUrl(imageUrl);
			mZMDiary.setPrivacyStatus(status);
		}

		setView();
	}

	/**
	 * 初始化日志
	 */
	private ZMDiary getZMDiary() {
		ZMDiary diary = new ZMDiary();
		diary.setPrivacyStatus(DEFALUT_DIARY_STATUE);
		return diary;
	}

	//---------------------

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_GALLERY_CODE:
				Uri uri = data.getData();
				mPhotoPath = MediaStoreHelper.getIntentImagePath(getContentResolver(), data.getData());
				if (mPhotoPath != null) {
					setPhoto(mPhotoPath);
				} else {
					HaloToast.show(getApplicationContext(), R.string.load_failed);
					setPhoto(null);
				}
				break;
			case REQUEST_CAMERA_CODE:
				setPhoto(mPhotoPath);
				break;
			case request_send_code:
				openActivity();
				break;
			}
		}
	}

	private void openActivity() {
		User user = UserService.getInstance(this).getMyself();
		UserUtils userUtils = new UserUtils(this);
		userUtils.switchAcitivity(user.getId(),true);
	}

	@Override
	public void onBackPressed() {
		if (mFaceLayout.getVisibility() != View.GONE) {
			mFaceLayout.setVisibility(View.GONE);
		} else {
			if (!TextUtils.isEmpty(mTitleView.getText()) || !TextUtils.isEmpty(mContentView.getText())) {
				showQuitMessage();
			} else {
				super.onBackPressed();
			}
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("title", mTitleView.getText().toString());
		outState.putString("content", mContentView.getText().toString());
		outState.putString("imageUrl", mZMDiary.getImageUrl());
		outState.putInt("status", mZMDiary.getPrivacyStatus());
	};

	@Override
	protected void onStart() {
		super.onStart();
		mTitleView.requestFocus();
	}

	//--------------event------------
	/** 表情点击事件 */
	private View.OnClickListener faceOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//当mContentView获取到焦点
			if (mContentView.isFocused()) {
				FaceHolder face = (FaceHolder) v.getTag();
				String symbol = face.getSymbol();
				SpannableString span = new SpannableString(symbol);
				span.setSpan(new ImageSpan(face.getBitmap()), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				mContentView.getText().insert(mContentView.getSelectionStart(), span);
			}
		}
	};

	/**
	 * 滑动发生改变
	 */
	private OnTouchListener contentTouchlistener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mFaceLayout.getVisibility() != View.GONE) {
				mFaceLayout.setVisibility(View.GONE);
			}
			return false;
		}
	};
	/**
	 * 选择图片
	 */
	private OnClickListener cameraClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SelectListDialog modifyDialog = new SelectListDialog(DiaryEditActivity.this);
			modifyDialog.setTitle(R.string.upload_photo);
			String[] selectPhtoget = null;
			if (TextUtils.isEmpty(mZMDiary.getImageUrl())) {
				selectPhtoget = new String[] { "拍照上传", "本地上传" };
			} else {
				selectPhtoget = new String[] { "拍照上传", "本地上传", "删除" };
			}
			modifyDialog.setoptionNames(selectPhtoget);
			modifyDialog.setOnBtItemClickListener(new OnBtItemClicklistener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					switch (position) {
					case 0:
						sendCamreaSelectPhoto();
						break;
					case 1:
						sendGallerySelectPhoto();
						break;
					case 2:
						setPhoto(null);
						break;
					}
				}
			});
			modifyDialog.show();
		}
	};
	/**
	 * 设置是否公开
	 */
	private OnClickListener privacyStatusClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final ZhimaPopupMenu popupMenu = new ZhimaPopupMenu(DiaryEditActivity.this);
			int privacyStatus = mZMDiary.getPrivacyStatus();
			popupMenu.setSelection(privacyStatus == DiaryPrivacyStatus.PUBLIC ? R.id.public_state : R.id.privacy_state);
			popupMenu.setMenuItems(R.menu.privacy_status);
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public void onMenuItemClick(ZhimaMenuItem item, int position) {
					switch (item.getId()) {
					case R.id.privacy_state:
						mZMDiary.setPrivacyStatus(DiaryPrivacyStatus.PRIVATE);
						break;
					case R.id.public_state:
						mZMDiary.setPrivacyStatus(DiaryPrivacyStatus.PUBLIC);
						break;
					}
					refreshTopbar();
				}
			});
			popupMenu.show(v);
		}
	};

	/***
	 * 点击表情
	 */
	private OnClickListener faceClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int visib = mFaceLayout.getVisibility();
			if (visib == View.GONE) {
				hideIME();
				//这是为了解决。闪屏问题
				mFaceLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						mFaceLayout.setVisibility(View.VISIBLE);
					}
				}, 100);
			} else {
				mFaceLayout.setVisibility(View.GONE);
				ImeHelper.showIME(mContentView);
			}
		}
	};

	/***
	 * 下一步
	 */
	private OnClickListener nextTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//TODO 下一步，发送
			if (checkText(true)) {
				//传递zmDiray
				//公开私密在菜单选择的时候设置，图片在设置imgView时设置。
				mZMDiary.setTitle(mTitleView.getText().toString());
				mZMDiary.setContent(mContentView.getText().toString());

				Intent shardIntent = new Intent(DiaryEditActivity.this, DiaryShareActivity.class);
				shardIntent.putExtra(DiaryConsts.diary, mZMDiary);
				shardIntent.putExtra(DiaryConsts.space_id, mSpaceId);
				startActivityForResult(shardIntent, request_send_code);
			}
		}
	};

	//------------------------------------------------------------------
	//private mothed

	private boolean checkText(boolean bl) {
		//是否输入标题
		if (TextUtils.isEmpty(mTitleView.getText().toString().trim())) {
			HaloToast.show(getApplicationContext(), "没有输入标题");
			return false;
		}
		//是否输入内容
		if (TextUtils.isEmpty(mContentView.getText().toString().trim())) {
			HaloToast.show(getApplicationContext(), "没有输入内容");
			return false;
		}
		return true;
	}

	private void setView() {
		mContentView.requestFocus();

		mTitleView.setText(mZMDiary.getTitle());
		mContentView.setText(mZMDiary.getContent());

		String path = mZMDiary.getImageUrl();
		if (!TextUtils.isEmpty(path)) {
			LocalImageLoader.getInstance(this).loadImage(path, mCameraView, getActivityId(), R.drawable.default_image);
		}

		refreshTopbar();
	}

	private void setPhoto(String str) {
		if (str != null) {
			Bitmap image = GraphicUtils.getImageThumbnailByDip(this, str, CROP_ASPECTX, CROP_ASPECTY);
			mZMDiary.setImageUrl(mPhotoPath);
			mCameraView.setImageBitmap(image);
		} else {
			mZMDiary.setImageUrl(null);
			mCameraView.setImageResource(R.drawable.diary_camera);
		}
	}

	private void sendCamreaSelectPhoto() {
		String fileName = System.currentTimeMillis() + ".jpg";
		mPhotoPath = null;
		mPhotoPath = FileHelper.getSysDcmiPath(fileName);
		if (mPhotoPath != null) {
			IntentHelper.sendCamreaPhotoForResult(this, mPhotoPath, REQUEST_CAMERA_CODE);
		} else {
			HaloToast.show(this, R.string.sd_error);
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	private void sendGallerySelectPhoto() {
		IntentHelper.sendGalleryPhotoForResult(this, REQUEST_GALLERY_CODE);
	}

	/**
	 * 退出提示
	 */
	private void showQuitMessage() {
		MessageDialog dialog = new MessageDialog(this, mTitleView);
		dialog.setTitle(R.string.dialog_title);
		dialog.setMessage(R.string.diary_quit_message);
		dialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {
				finish();
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		dialog.show();
	}

	private void findView() {
		// TODO Auto-generated method stub
		mTitleView = (EditText) findViewById(R.id.edt_title);
		mContentView = (EditText) findViewById(R.id.edt_content);
		mFaceView = (ImageView) findViewById(R.id.img_face);
		mCameraView = (ImageView) findViewById(R.id.img_camera);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mFaceLayout = (ViewGroup) findViewById(R.id.layout_face);

		//添加表情
		FaceAdapter faceAdapter = new FaceAdapter(this, FaceHelper.getInstance(this).getFaceHolderList());
		faceAdapter.setOnClickListener(faceOnClickListener);
		mViewFlow.setAdapter(faceAdapter);
		CircleFlowIndicator indicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(indicator);

//		mTitleView.setOnClickListener(contentClick);
//		mContentView.setOnClickListener(contentClick);
		mFaceView.setOnClickListener(faceClick);
		mCameraView.setOnClickListener(cameraClick);

//		mTitleView.setOnFocusChangeListener(contentFocuslistener);
//		mContentView.setOnFocusChangeListener(contentFocuslistener);
		mTitleView.setOnTouchListener(contentTouchlistener);
		mContentView.setOnTouchListener(contentTouchlistener);
	}

	//刷性topbar
	private void refreshTopbar() {
		if (mZMDiary.getPrivacyStatus() == ZMConsts.DiaryPrivacyStatus.PUBLIC) {
			mDiaryStateView.setImageResource(R.drawable.topbar_public_state);
		} else {
			mDiaryStateView.setImageResource(R.drawable.topbar_privacy_status);
		}
	}

	private void hideIME() {
		ImeHelper.hideIME(mTitleView);
		ImeHelper.hideIME(mContentView);
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				hideIME();
				if (!TextUtils.isEmpty(mTitleView.getText()) || !TextUtils.isEmpty(mContentView.getText())) {
					showQuitMessage();
				} else {
					finish();
				}
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_next);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(nextTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		mDiaryStateView = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton2);
		mDiaryStateView.setImageResource(R.drawable.topbar_public_state);
		mTopbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(privacyStatusClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("发表新日志");
//		mTopbar.findViewById(R.id.left_txt_icon).setVisibility(View.VISIBLE);
//		((LinearLayout) mTopbar.findViewById(R.id.layout_topbar_left_title)).setOnClickListener(privacyStatusClick);
	}
}
