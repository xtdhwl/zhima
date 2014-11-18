package com.zhima.ui.usercenter.data.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetAlbumImageListProtocol;
import com.zhima.base.protocol.UserProtocolHandler.UploadAlbumImageProtocol;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserAlbumImage;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.SelectListDialog;
import com.zhima.ui.common.view.SelectListDialog.OnBtItemClicklistener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.tools.IntentHelper;
import com.zhima.ui.usercenter.data.adapter.MyAlbumAdapter;

/**
 * @ClassName: MyAlbumActivity
 * @Description: 我的相册
 * @author luqilong
 * @date 2013-1-15 下午1:58:08
 */

public class AlbumListActivity extends BaseActivity {

	private static final int request_gallery_Code = 0;
	private static final int request_camera_Code = 2;
	private static final int request_name_Code = 4;

	private long mUserId;
	private PullToRefreshGridView mPullGridView;
	private GridView mGridView;

	private RefreshListData<UserAlbumImage> mCacheAlbumImageList;
	private MyAlbumAdapter mAlbumAdapter;

	private UserService mUserService;
	private String mPhotoPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_my_album);
		findView();
		setTopbar();
		init();

		// Intent 传递id
		Intent intent = getIntent();
		mUserId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		getServiceDate();
		setListener();
		checkUser(mUserId);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case request_gallery_Code:
				Uri uri = data.getData();
				if (uri != null) {
					mPhotoPath = MediaStoreHelper.getIntentImagePath(getContentResolver(), data.getData());
					sendNameEditIntent();
				} else {
					HaloToast.show(getApplicationContext(), R.string.load_failed);
				}
				break;
			case request_camera_Code:
				sendNameEditIntent();
				break;
			case request_name_Code:
				String content = data.getStringExtra("content");
				if (TextUtils.isEmpty(content)) {
					HaloToast.show(getApplicationContext(), R.string.content_not_empty);
				} else {
					uploadPhoto(content, mPhotoPath);
				}
				break;
			}

		}
	}

//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		setAlbumImageView(mCacheAlbumImageList.getDataList());
//	}

	private void setListener() {
		mPullGridView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mUserService.getAlbumImage(mUserId, mPullGridView.hasPullFromTop(), AlbumListActivity.this);
			}
		});
	}

	private void init() {
		mUserService = UserService.getInstance(this);
	}

	private void getServiceDate() {
		mCacheAlbumImageList = mUserService.getCacheUserAlbumImage(mUserId);
		if (mCacheAlbumImageList.isEmpty()) {
			startWaitingDialog(null, R.string.loading);
			mUserService.getAlbumImage(mUserId, false, this);
		} else {
			setAlbumImageView(mCacheAlbumImageList.getDataList());
		}
	}

	private void setAlbumImageView(final ArrayList<UserAlbumImage> arrayList) {
		if (mAlbumAdapter == null) {
			mAlbumAdapter = new MyAlbumAdapter(this, R.layout.user_center_album_item, arrayList);
			mAlbumAdapter.registerDataSetObserver(MyDataSetObserver);
			mGridView.setAdapter(mAlbumAdapter);
			refreshTopbar();
			mGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent it = new Intent(AlbumListActivity.this, AlbumPreviewActivity.class);
					it.putExtra(ACTIVITY_EXTRA, mUserId);
					it.putExtra(AlbumPreviewActivity.ACTIVITY_POSITION, position);
					it.putExtra("type",  ImageScaleType.ORIGINAL);
					startActivity(it);
				}
			});
		} else {
			mAlbumAdapter.setData(arrayList);
			mAlbumAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_USER_ALBUM_IMAGE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetAlbumImageListProtocol p = (GetAlbumImageListProtocol) protocol;
					mCacheAlbumImageList = p.getDataList();
					setAlbumImageView(mCacheAlbumImageList.getDataList());
					mPullGridView.setLastPage(mCacheAlbumImageList.isLastPage());
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			} else if (protocol.getProtocolType() == ProtocolType.UPLOAD_USER_ALBUM_IMAGE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// TODO 添加我的相册 优化上传比
					HaloToast.show(getApplicationContext(), "上传成功");
					UploadAlbumImageProtocol p = (UploadAlbumImageProtocol) protocol;
					setAlbumImageView(mCacheAlbumImageList.getDataList());
					mPullGridView.setLastPage(mCacheAlbumImageList.isLastPage());
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			}
			mPullGridView.onRefreshComplete(mPullGridView.hasPullFromTop());
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullGridView.setEmptyView();
		}
	}

	/**
	 * 添加相册
	 */
	private OnClickListener addTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SelectListDialog modifyDialog = new SelectListDialog(AlbumListActivity.this);
			modifyDialog.setTitle("上传照片");
			modifyDialog.setoptionNames(new String[] { "拍照上传", "本地上传" });
			modifyDialog.setOnBtItemClickListener(new OnBtItemClicklistener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (position == 0) {
						sendCamreaSelectPhoto();
					} else if (position == 1) {
						sendGallerySelectPhoto();
					}
				}
			});
			modifyDialog.show();
		}
	};

	private void uploadPhoto(String content, String photoPath) {
		UserAlbumImage album = new UserAlbumImage();
		album.setTitle(content);
		album.setImageUrl(photoPath);
		album.setUserId(mUserId);
		startWaitingDialog(null, R.string.loading);
		mUserService.uploadAlbumImage(album, this);
	}

	//------------------------------------------------------------------

	private void sendCamreaSelectPhoto() {
		String fileName = System.currentTimeMillis() + ".jpg";
		mPhotoPath = FileHelper.getSysDcmiPath(fileName);
		if (mPhotoPath != null) {
			IntentHelper.sendCamreaPhotoForResult(this, mPhotoPath, request_camera_Code);
		} else {
			HaloToast.show(this, R.string.sd_error);
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	private void sendGallerySelectPhoto() {
		IntentHelper.sendGalleryPhotoForResult(this, request_gallery_Code);
	}

	/**
	 * 编辑照片
	 */
	private void sendNameEditIntent() {
		Intent it = new Intent(this, AlbumTitleEditActivity.class);
		startActivityForResult(it, request_name_Code);
	}

	private void checkUser(long userId) {
		if (mUserService.isMySelf(userId)) {
			ZhimaTopbar topbar = getTopbar();
			ImageView image1 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton1);
			image1.setImageResource(R.drawable.topbar_add);
			topbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(addTopbarClick);
			topbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		}
	}

	private DataSetObserver MyDataSetObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			refreshTopbar();
		}

		@Override
		public void onInvalidated() {
			refreshTopbar();
		}

	};

	private void refreshTopbar() {
		ZhimaTopbar topbar = getTopbar();
		TextView titleView = (TextView) topbar.findViewById(R.id.txt_topbar_title);
		int count = 0;
		if (mAlbumAdapter != null) {
			count = mAlbumAdapter.getCount();
		}

		String title = count > 0 ? " " + count + "/" + "照片" : "照片";
		titleView.setText(title);
	}

	private void findView() {
		mPullGridView = (PullToRefreshGridView) this.findViewById(R.id.refresh_grid);
		mGridView = mPullGridView.getRefreshableView();
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
				finish();
			}
		});

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("照片");
	}
}
