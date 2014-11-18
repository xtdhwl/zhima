package com.zhima.ui.space.zmspace.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.NoticeKind;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.CommerceProtocolHandler.GetOwnerNoticeListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceImagePhotoListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceImageVideoListProtocol;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.model.ZMSpaceVideo;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshGridView;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.activity.PhotoActivity;
import com.zhima.ui.space.adapter.NoticeAdapter;
import com.zhima.ui.space.zmspace.adapter.ZmSpaceImageAdapter;
import com.zhima.ui.space.zmspace.adapter.ZmSpaceVedioAdapter;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 芝麻空间影像照片列表
 * 
 * @ClassName: ZmSpaceVedioImageActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-31 下午4:28:22
 */
public class ZmSpaceVedioImageActivity extends BaseActivity implements OnClickListener {

	private PullToRefreshGridView mVedioPullView;
	private PullToRefreshGridView mImagePullView;

	private GridView mVedioGridView;
	private GridView mImageGridView;

	/** 芝麻空间影像 */
	private ZmSpaceVedioAdapter mZmSpaceVedioAdapter;
	/** 芝麻空间照片 */
	private ZmSpaceImageAdapter mZmSpaceImageAdapter;

	/** 芝麻空间影像 */
	private RefreshListData<ZMSpaceVideo> mVedioList;
	/** 芝麻空间照片 */
	private RefreshListData<ZMObjectImage> mImageList;

	// /** 芝麻空间照片 */
	// private Button mCommerceButton;
	// /** 芝麻空间照片 */
	// private Button mOfficeButton;

	// // 传递.公告默认显示那种公告.
	// public static final String NOTICE_TYPE = "notice_type";
	// public static final String REGION_ID = "region_id";
	// public static final String ACTIVITY_TITLE = "activity_title";

	public final static int IMAGE_TYPE = 10;
	public final static int VEDIO_TYPE = 11;
	/** 类型表示 */
	private static int type = IMAGE_TYPE;

	// public boolean mIsShowCommerceView;

	/** 商户 */
	private ScanningcodeService mScanningcodeService;
	private ZMSpaceService mZMSpaceService;

	private ZMObject mZMObject;
	private long mZMObjectId;

	private boolean mIsCommerceNoticeData;
	private boolean mIsOfficeNoticeData;

	// private String mCommerceButtonText;

	private int mSelectId = R.id.image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_zmspace_vedio_image_list_activity);

		findView();
		setTopbar();
		init();
	}

	// private void setCommerceButtonText() {
	// mCommerceButton.setText(mCommerceButtonText);
	// }

	private void setListener() {
		mImagePullView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMSpaceService.getImagePhotoList(mZMObject, mImagePullView.hasPullFromTop(),
						ZmSpaceVedioImageActivity.this);
			}
		});
		mVedioPullView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mZMSpaceService.getImageVideoList(mZMObject, mVedioPullView.hasPullFromTop(),
						ZmSpaceVedioImageActivity.this);
			}
		});
	}

	/**
	 * 初始化数据
	 * 
	 * @Title: init
	 * @Description: TODO
	 */
	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMSpaceService = ZMSpaceService.getInstance(this);

		Intent intent = getIntent();
		mZMObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mIsOfficeNoticeData = true;

		mZMObject = mScanningcodeService.getCacheZMObject(mZMObjectId);

		// if (mZMObject == null) {
		// mCommerceButton.setVisibility(View.GONE);
		// mOfficeButton.setVisibility(View.GONE);
		// }

		type = IMAGE_TYPE;
		if (type == VEDIO_TYPE) {
			showVedioView();
		} else {
			showImageView();
		}
		setListener();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SPACE_IMAGE_VIDEO_LIST_PROTOCOL) {
				// 获取商户公告
				if (protocol.isHandleSuccess()) {
					// 访问成功
					GetSpaceImageVideoListProtocol p = (GetSpaceImageVideoListProtocol) protocol;
					mVedioList = p.getDataList();
					setVedioGridView(mVedioList.getDataList());
					mVedioPullView.setLastPage(mVedioList.isLastPage());
				} else {

				}
				mVedioPullView.onRefreshComplete(mVedioPullView.hasPullFromTop());
			} else if (protocol.getProtocolType() == ProtocolType.GET_SPACE_IMAGE_PHOTO_LIST_PROTOCOL) {
				// 获取官方公告
				if (protocol.isHandleSuccess()) {
					// 访问成功
					GetSpaceImagePhotoListProtocol p = (GetSpaceImagePhotoListProtocol) protocol;
					mImageList = p.getDataList();
					setImageGridView(mImageList.getDataList());
					mImagePullView.setLastPage(mImageList.isLastPage());
				} else {

				}
				mImagePullView.onRefreshComplete(mImagePullView.hasPullFromTop());
			}
		} else {
			// TODO 网络访问失败
			mVedioPullView.setEmptyView();
			mImagePullView.setEmptyView();
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
		startWaitingDialog("", "正在加载...");
	}

	@Override
	public void onClick(View v) {
		// TODO 事件处理
		switch (v.getId()) {
		case R.id.btn_office:
			showImageView();
			break;
		case R.id.btn_commerce:
			showVedioView();
			break;
		}

	}

	/**
	 * 设置为显示照片
	 */
	private void showImageView() {
		type = IMAGE_TYPE;
		setShowState();
		// setTabLine();
		if (mZmSpaceImageAdapter == null) {
			mImageList = mZMSpaceService.getCacheImagePhotoList(mZMObject);
			if (mImageList.isEmpty()) {
				if (mIsOfficeNoticeData) {
					// startWaitingDialog("", R.string.loading);
					mIsOfficeNoticeData = false;
				}
				mZMSpaceService.getImagePhotoList(mZMObject, true, this);
			} else {
				setImageGridView(mImageList.getDataList());
			}
		}
	}

	private void setImageGridView(final ArrayList<ZMObjectImage> list) {
		if (mZmSpaceImageAdapter == null) {
			mZmSpaceImageAdapter = new ZmSpaceImageAdapter(this, R.layout.space_album_item, list);
			mImageGridView.setAdapter(mZmSpaceImageAdapter);
			mImageGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (list.size() > position) {
						ZMObjectImage notice = mZmSpaceImageAdapter.getItem(position);
						Intent intent = new Intent(ZmSpaceVedioImageActivity.this, PhotoActivity.class);
						intent.putExtra(ACTIVITY_EXTRA, mZMObjectId);
						intent.putExtra(PhotoActivity.ACTIVITY_POSITION, position);
						intent.putExtra(PhotoActivity.IS_ZMSPACE, true);
						intent.putExtra(PhotoActivity.IMAGE_SCALE_TYPE, ImageScaleType.LARGE);
						startActivity(intent);
					}
				}
			});
		} else {
			mZmSpaceImageAdapter.setData(list);
			mZmSpaceImageAdapter.notifyDataSetChanged();
		}
	}

	// /**
	// * 进入照片列表详情
	// * @Title: openNoticeInfoActivity
	// * @Description: TODO
	// * @param id
	// * @param code
	// */
	// private void openNoticeInfoActivity(long id, String code) {
	// Intent noticeIt = new Intent(ZmSpaceVedioImageActivity.this,
	// NoticeInfoActivity.class);
	// noticeIt.putExtra(ACTIVITY_EXTRA, id);
	// if (code != null) {
	// noticeIt.putExtra(NoticeInfoActivity.ACTIVITY_EXTRA_ZMCODE, code);
	// }
	// startActivity(noticeIt);
	// }

	/**
	 * 设置为显示
	 */
	private void showVedioView() {
		type = VEDIO_TYPE;
		setShowState();
		if (mZmSpaceVedioAdapter == null) {
			mZMSpaceService.getImageVideoList(mZMObject, true, this);
		}
	}

	private void setVedioGridView(final ArrayList<ZMSpaceVideo> arrayList) {
		if (mZmSpaceVedioAdapter == null) {
			mZmSpaceVedioAdapter = new ZmSpaceVedioAdapter(this, R.layout.space_album_item, arrayList);
			mVedioGridView.setAdapter(mZmSpaceVedioAdapter);
			mVedioGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (arrayList.size() > position) {
						ArrayList<String> urlArray = new ArrayList<String>();
						for (int i = 0; i < arrayList.size(); i++) {
							urlArray.add(arrayList.get(i).getContentUrl());
						}
						Intent acqierementIntent = new Intent(ZmSpaceVedioImageActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
						acqierementIntent.putExtra(IdoPhotoActivity.TOPBAR_BACKGROUND,
								R.color.orange_organization_topbar);
						acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE, getText(R.string.multimedia_info));
						startActivity(acqierementIntent);
					}
				}
			});
		} else {
			mZmSpaceVedioAdapter.setData(arrayList);
			mZmSpaceVedioAdapter.notifyDataSetChanged();
		}
	}

	// /***
	// * 根据type设置官方公告和商户公告显示下划线
	// */
	// private void setTabLine() {
	// if (type == OFFICE_TYPE) {
	// mOfficeButton.setBackgroundResource(R.drawable.notice_title_bg);
	// mCommerceButton.setBackgroundColor(Color.BLACK);
	// } else {
	// mOfficeButton.setBackgroundColor(Color.BLACK);
	// mCommerceButton.setBackgroundResource(R.drawable.notice_title_bg);
	// }
	// }

	/** 根据type设置pullView是否隐藏 */
	private void setShowState() {
		if (type == IMAGE_TYPE) {
			mImagePullView.setVisibility(View.VISIBLE);
			mImageGridView.setVisibility(View.VISIBLE);
			mVedioPullView.setVisibility(View.GONE);
			mVedioGridView.setVisibility(View.GONE);
		} else {
			mVedioPullView.setVisibility(View.VISIBLE);
			mVedioGridView.setVisibility(View.VISIBLE);
			mImagePullView.setVisibility(View.GONE);
			mImageGridView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void findView() {
		mVedioPullView = (PullToRefreshGridView) findViewById(R.id.refresh_vedio_grid);
		mImagePullView = (PullToRefreshGridView) findViewById(R.id.refresh_image_grid);

		mVedioGridView = mVedioPullView.getRefreshableView();
		mImageGridView = mImagePullView.getRefreshableView();

		// mOfficeButton = (Button) findViewById(R.id.btn_office);
		// mCommerceButton = (Button) findViewById(R.id.btn_commerce);
		//
		// mOfficeButton.setOnClickListener(this);
		// mCommerceButton.setOnClickListener(this);
	}

	private void setTopbar() {
		// ZhimaTopbar mTopbar = (ZhimaTopbar)
		// this.findViewById(R.id.ztop_bar_layout);
		// LinearLayout ll_left = (LinearLayout) View.inflate(this,
		// R.layout.topbar_leftview, null);
		// mTopbar.addLeftLayoutView(ll_left);
		// mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		// // TODO Auto-generated method stub
		// finish();
		// }
		// });
		// ((TextView)
		// mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.notice);

		ViewInitTools.setTopBar(this, "照片列表", View.VISIBLE, topbarLeftOncliClickListener);
	}

	private OnClickListener topbarLeftOncliClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ZhimaPopupMenu menu = new ZhimaPopupMenu(ZmSpaceVedioImageActivity.this);
			menu.setSelection(mSelectId);
			menu.setMenuItems(R.menu.zmspace_image_vedio);
			menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public void onMenuItemClick(ZhimaMenuItem item, int position) {
					switch (item.getId()) {
					case R.id.image:
						mSelectId = R.id.image;
						ViewInitTools.setTopBar(ZmSpaceVedioImageActivity.this, "照片列表", View.VISIBLE,
								topbarLeftOncliClickListener);
						showImageView();
						break;
					case R.id.vedio:
						mSelectId = R.id.vedio;
						ViewInitTools.setTopBar(ZmSpaceVedioImageActivity.this, "视频列表", View.VISIBLE,
								topbarLeftOncliClickListener);
						showVedioView();
						break;
					}
				}
			});
			menu.show(getTopbar());
		}
	};
}
