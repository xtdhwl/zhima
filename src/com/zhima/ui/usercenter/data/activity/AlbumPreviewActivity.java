package com.zhima.ui.usercenter.data.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserAlbumImage;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.ZMGallery;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.usercenter.data.adapter.MyAlbumPreviewAdapter;

/**
 * @ClassName:PhotoActivity
 * @Description:全屏预览相册.
 * @author liqilong
 * @date 2012-8-22 下午6:38:11
 * 
 */
public class AlbumPreviewActivity extends BaseActivity {
	//FOMIX 优化查看相册通用一个 
	protected static final String TAG = "PhotoActivity";
	// 传递ZMObjectImage 的id
	// 传递位置
	// 传递类型
	public static final String ACTIVITY_POSITION = "position";
	public static final String ACTIVITY_TYPE = "type";

	private ZMGallery mGallery;

	private RefreshListData<UserAlbumImage> mZMObjectImageListData;
	private ArrayList<UserAlbumImage> mImageList;
	private MyAlbumPreviewAdapter mPhotoAdapter;

	private String mImageScaleType = ImageScaleType.ORIGINAL;

	private int position;
	private long zmObjectId;

	private TextView mTitleText;

	private UserService mUserService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_photo_activity);
		setTopbar();
		init();
		mGallery = (ZMGallery) findViewById(R.id.gal_photo);
		//传递 id 和 位置
		Intent intent = getIntent();
		//id
		zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		//位置
		position = intent.getIntExtra(ACTIVITY_POSITION, 0);
		//类型
		String scaleType = intent.getStringExtra(ACTIVITY_TYPE);
		if (!TextUtils.isEmpty(scaleType)) {
			mImageScaleType = scaleType;
		}

		mZMObjectImageListData = mUserService.getCacheUserAlbumImage(zmObjectId);
		mImageList = mZMObjectImageListData.getDataList();

		setAlbumImageView();
		setSelection(position);
		setListener();
		checkUser(zmObjectId);
	}

	private void setListener() {
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setTopbarTitle(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private class delectAlbumImageProductHttpCallBack implements IHttpRequestCallback {
		private UserAlbumImage albumImg;

		public delectAlbumImageProductHttpCallBack(UserAlbumImage lattice) {
			super();
			this.albumImg = lattice;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 删除相册成功
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.delete_success);
					mImageList.remove(albumImg);
					if (mImageList.size() <= 0) {
						finish();
					} else {
						setAlbumImageView();
						setTopbarTitle(mGallery.getSelectedItemPosition());
					}
				} else {
					//删除相册失败
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mPhotoAdapter != null) {
			mPhotoAdapter.recycle();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPhotoAdapter != null) {
			mPhotoAdapter = null;
		}
	}

	//------------------------------------------------------------------------
	//event
	/**
	 * 保存图片
	 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean saveFalg = false;
			int position = mGallery.getSelectedItemPosition();
			if (mPhotoAdapter.getCount() > position) {
				UserAlbumImage zmobj = mPhotoAdapter.getItem(position);
				String imageUrl = zmobj.getImageUrl();
				String localImagePath = HttpImageLoader.getInstance(AlbumPreviewActivity.this).getLocalImagePath(
						imageUrl, ImageScaleType.ORIGINAL);
				if (localImagePath != null) {
					String filePath = null;
					long dateTaken = System.currentTimeMillis();
					String filename = dateTaken + "." + FileHelper.getFileExtension(imageUrl);
					filePath = FileHelper.getSysDcmiPath(filename);
					if (filePath != null) {
						saveFalg = FileHelper.copyFile(localImagePath, filePath);
						if (saveFalg) {
							/* Uri bitmapUri = */MediaStoreHelper.insertImage(getContentResolver(), filename, filename,
									dateTaken, FileHelper.getMIME(imageUrl), filePath);
						}
					} else {
						HaloToast.show(getApplicationContext(), R.string.sd_error);
					}
				}
			}

			if (saveFalg) {
				HaloToast.show(getApplicationContext(), R.string.save_success);
			} else {
				//其他失败
				HaloToast.show(getApplicationContext(), R.string.msg_failed, 0);
			}
		}
	};

	/**
	 * 删除相册
	 */
	private View.OnClickListener deleteTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MessageDialog dialog = new MessageDialog(AlbumPreviewActivity.this, v);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					UserAlbumImage albumImage = getCureenAlbumImage();
					if (albumImage != null) {
						mUserService.deleteImageOfAlbum(albumImage.getId(), new delectAlbumImageProductHttpCallBack(
								albumImage));
					} else {
						HaloToast.show(getApplicationContext(), R.string.load_failed);
					}
				}

				@Override
				public void onLeftBtClick() {
				}
			});

			dialog.show();
		}
	};

	//---------------------------------------------------------------------
	//method

	private void init() {
		mUserService = UserService.getInstance(this);
	}

	/**
	 * 设置相册view
	 */
	private void setAlbumImageView() {
		if (mPhotoAdapter == null) {
			mPhotoAdapter = new MyAlbumPreviewAdapter(this, mImageList, mImageScaleType);
			mGallery.setAdapter(mPhotoAdapter);
		} else {
			mPhotoAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 设置位置
	 */
	private void setSelection(int position) {
		if (mImageList.size() > position) {
			mGallery.setSelection(position);
		}
	}

	/**
	 * 获得当前的UserAlbumImage
	 */
	private UserAlbumImage getCureenAlbumImage() {
		int item = mGallery.getSelectedItemPosition();
		if (mImageList.size() > item) {
			return mImageList.get(item);
		}
		return null;
	}

	/**
	 * 刷新标题栏:图片 1/4
	 */
	private void setTopbarTitle(int position) {
		mTitleText.setText(getString(R.string.album) + " " + (position + 1) + "/"
				+ mZMObjectImageListData.getDataList().size());
	}

	private void checkUser(long userId) {
		if (mUserService.isMySelf(userId)) {
			ZhimaTopbar topbar = getTopbar();
			topbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(deleteTopbarClick);
			ImageView img2 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton2);
			img2.setImageResource(R.drawable.topbar_remove);
			topbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
		}
	}

	private void setTopbar() {
		// 标题栏
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});

		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		ImageView img1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		img1.setImageResource(R.drawable.topbar_save);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		mTitleText = ((TextView) mTopbar.findViewById(R.id.txt_topbar_title));
		mTitleText.setText(R.string.album);

	}
}
