package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNearZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNoticeDigestListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.VehicleObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ContactService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.DrawableCardAdatper;
import com.zhima.ui.adapter.PhotoShowCardAdapter;
import com.zhima.ui.common.view.CardView;
import com.zhima.ui.common.view.CardView.OnCardItemClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ListCardView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.map.activity.GeoMapLocation;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.space.controller.NormalCardView;
import com.zhima.ui.space.controller.NormalCardView.OnCardClickListener;
import com.zhima.ui.space.controller.PraiseView;
import com.zhima.ui.space.controller.PraiseView.Praise;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * @ClassName VehicleActivity
 * @Description 交通工具扫码结果
 * @author jiang
 * @date 2012-9-19 下午04:28:25
 */
public class VehicleActivity extends BaseActivity {
	private final String TAG = "VehicleActivity";

	private View mChildrenView;
	// 左边推荐个数
	private final static int RECOMMENDED_COUNT = 3;

	private VehicleObject mVehicleObject;
	/** 标题 */
	private TextView mNameText;;
	private TextView mZMIDText;
	/** 类型 */
	private TextView mTypeText;
	/** 电话 */
	private NormalCardView mPhoneView;
	/** 地址 */
	private NormalCardView mAddressView;
	/** 图片 */
	private ImageView mPhotoImage;
	/** 赞 */
	private PraiseView mPraiseView;
	/** 图片展示 */
	private CardView mPhotoCardView;
	/** 周边推荐列表 */
	private CardView mRecommendedCardView;

	/*** 赞信息 */
	private PraiseInfo mPraiseInfo = null;

	/** 公告View */
	private ListCardView mNoticeView;
	/** 公告列表 */
	private RefreshListData<Notice> mNoticeRefreshList;

	/** 图片列表 */
	private RefreshListData<ZMObjectImage> mPhotoList;
	/** 周边推荐列表 */
	private RefreshListData<ZMObject> mRecommendedList;

	private ScanningcodeService mScanningcodeService;
	private HttpImageLoader mHttpImageLoader;
	private long cityId;
	private long zmObjectId;
	private int zmObjectType;
	private GeoCoordinate mGeoCoordinate;
	private boolean mIsFirstFlag = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChildrenView = View.inflate(this, R.layout.space_vehicle_activity, null);
		setSidebar();

		findView();
		setTopbar();
		init();
		Intent intent = getIntent();
		zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);
		mVehicleObject = (VehicleObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId,
				zmObjectType);
		if (mVehicleObject != null) {
			startWaitingDialog("", R.string.loading);
			refreshView();
		} else {
			startWaitingDialog("", R.string.loading);
			ScanningcodeService.getInstance(this).getScanningInfo(zmObjectId, zmObjectType, this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * @Title: findView
	 * @Description: 初始化控件
	 * @param
	 * @return void
	 */
	private void findView() {

		mNameText = (TextView) mChildrenView.findViewById(R.id.txt_name);
		mZMIDText = (TextView) mChildrenView.findViewById(R.id.txt_zmid);
		mPhotoImage = (ImageView) mChildrenView.findViewById(R.id.img_photo);
		mPraiseView = (PraiseView) mChildrenView.findViewById(R.id.praise);
		mPraiseView.setOnItemClickListener(praiseClick);
		mTypeText = (TextView) mChildrenView.findViewById(R.id.txt_type);
		mAddressView = (NormalCardView) mChildrenView.findViewById(R.id.card_address);
		mPhoneView = (NormalCardView) mChildrenView.findViewById(R.id.card_phone);

		mNoticeView = (ListCardView) mChildrenView.findViewById(R.id.card_notice);

		mPhotoCardView = (CardView) mChildrenView.findViewById(R.id.productCard);
		mRecommendedCardView = (CardView) mChildrenView.findViewById(R.id.recommendedCard);

		mPraiseView.addPraiseView(PraiseKind.ENVIRONMENT, PraiseKind.SERVICE, PraiseKind.QUALITY);
	}

	/**
	 * @Title: init
	 * @Description:初始化一些变量
	 * @param
	 * @return void
	 */
	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mHttpImageLoader = HttpImageLoader.getInstance(this);
	}

	private void refreshView() {
		cityId = mVehicleObject.getCityId();
		requestServiceData();
		setView();
	}

	/**
	 * @Title: requestServiceData
	 * @Description:请求服务器数据
	 * @param
	 * @return void
	 */
	private void requestServiceData() {
		mGeoCoordinate = ZMLocationManager.getInstance().getLastKnownGeoCoordinate();
		/** 获取公告列表 */
		mNoticeRefreshList = new RefreshListData<Notice>();
		ZMSpaceService.getInstance(this).getOfficialNoticeList(mVehicleObject.getCityId(), mVehicleObject.getZMObjectType(), mNoticeRefreshList, this);

		// 获取照片列表
		mPhotoList = mScanningcodeService.getCacheZMObjectAlbumList(zmObjectId);
		if (mPhotoList.isEmpty()) {
			mScanningcodeService.getZMObjectAlbumList(mVehicleObject, true, this);
		} else {
			setAlbum(mPhotoList.getDataList());
		}
		// 赞信息
		mScanningcodeService.getPraiseCount(mVehicleObject, this);

		// 周边推荐
		mRecommendedList = mScanningcodeService.getCacheRecommendedZMObjectList(ZMObjectKind.PUBLICPLACE_OBJECT); //
		if (mRecommendedList.isEmpty()) {
			mScanningcodeService.getNearZMObject(ZMObjectKind.PUBLICPLACE_OBJECT, mVehicleObject, cityId,
					mGeoCoordinate, this);//

		} else {
			setRecommendedView(mRecommendedList.getDataList());
		}

		mScanningcodeService.getNearZMObject(ZMObjectKind.VEHICLE_OBJECT, mVehicleObject, cityId, mGeoCoordinate, this);

	}

	/** 初始化View */
	private void setView() {

		mNameText.setText(mVehicleObject.getName());
		mZMIDText.setText("ZMID:" + mVehicleObject.getZMID());
		mTypeText.setText(getText(R.string.type) + ":" + mVehicleObject.getType());
		// 设置商户头图片
		mHttpImageLoader.loadImage(mVehicleObject.getImageUrl(), mPhotoImage, getActivityId(),
				R.drawable.default_image, ImageScaleType.SMALL);

		mAddressView.setContent(mVehicleObject.getDrivingRoute());
		mAddressView.setOnCradClickListener(null);
		mAddressView.setClickable(false);

		String phone = mVehicleObject.getPhone();
		if (TextUtils.isEmpty(phone)) {
			mPhoneView.setClickable(false);
			//			mPhoneView.setOnCradClickListener(null);
		} else {
			mPhoneView.setContent(phone);
			mPhoneView.setClickable(true);
			mPhoneView.setOnCradClickListener(phoneClick);
		}

		mPraiseView.setPraise(PraiseKind.ENVIRONMENT, mVehicleObject.getSpeedCount());
		mPraiseView.setPraise(PraiseKind.SERVICE, mVehicleObject.getServiceCount());
		mPraiseView.setPraise(PraiseKind.QUALITY, mVehicleObject.getSecurityCount());

		mPhotoImage.setOnClickListener(photoClick);
	}

	/**
	 * @Title: setAlbum
	 * @Description:设置图片展示
	 * @param
	 * @return void
	 */
	private void setAlbum(ArrayList<ZMObjectImage> albumList) {
		PhotoShowCardAdapter photoShowCardAdapter = new PhotoShowCardAdapter(this, R.layout.space_darwable_card_item,
				albumList);
		mPhotoCardView.setAdapter(photoShowCardAdapter);
		mPhotoCardView.setOnItemClickListener(new OnCardItemClickListener() {

			@Override
			public void onItemClickListener(View view, int position, boolean isArrow) {
				// TODO Auto-generated method stub
				if (isArrow) {
					Intent productIntent = new Intent(VehicleActivity.this, SpaceImageShowListActivity.class);
					productIntent.putExtra(ACTIVITY_EXTRA, mVehicleObject.getId());
					startActivity(productIntent);
				} else {
					// 点击的不是箭头 -->详情
					Intent productIntent = new Intent(VehicleActivity.this, SpaceImageShowInfoActivity.class);
					productIntent.putExtra(ACTIVITY_EXTRA, mVehicleObject.getId());
					productIntent.putExtra(SpaceImageShowInfoActivity.PRODUCT_POSITION, position);
					startActivity(productIntent);
				}
			}
		});
	}

	/**
	 * 设置公告列表
	 */
	private void setNoticeView(ArrayList<Notice> notices) {
		mNoticeView.setOnClickListener(noticeClick);
		mNoticeView.setData(notices);
	}

	/**
	 * @Title: setRecommendedView
	 * @Description:周边推荐的展示
	 * @param
	 * @return void
	 */
	private void setRecommendedView(final ArrayList<ZMObject> recommendedObjects) {
		DrawableCardAdatper drawableCardAdapter = new DrawableCardAdatper(this, R.layout.space_darwable_card_item,
				recommendedObjects);
		mRecommendedCardView.setAdapter(drawableCardAdapter);
		mRecommendedCardView.setOnItemClickListener(new OnCardItemClickListener() {
			@Override
			public void onItemClickListener(View view, int position, boolean isArrow) {
				if (!isArrow) {
					Bundle bundleExtra = getIntent().getBundleExtra(BaseActivity.ACTIVITY_BUNDLE);
					boolean isFinish = false;

					if (bundleExtra != null) {
						isFinish = bundleExtra.getBoolean("flag");
					}

					Bundle flagBundle = new Bundle();
					flagBundle.putBoolean("flag", true);
					ActivitySwitcher.openSpaceActivity(VehicleActivity.this, recommendedObjects.get(position),
							flagBundle, isFinish);
				}
			}
		});
	}

	/** 点击公告 */
	private View.OnClickListener noticeClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			Notice notice = (Notice) v.getTag();
			switch (v.getId()) {
			case R.id.txt_notice_one:
			case R.id.arrow:
				if (mVehicleObject != null) {
					Intent noticeIt = new Intent(VehicleActivity.this, NoticeActivity.class);
					noticeIt.putExtra(ACTIVITY_EXTRA, mVehicleObject.getZMCode());
					startActivity(noticeIt);
				} else {
					ErrorManager.showErrorMessage(getApplicationContext());
				}
				break;
//			case R.id.txt_notice_two:
//				if (notice.getNoticeKind() == NoticeKind.OFFICAL) {
//					openNoticeActivity(notice.getNoticeId(), null);
//				} else if (notice.getNoticeKind() == NoticeKind.MERCHANT) {
//					openNoticeActivity(notice.getNoticeId(), mVehicleObject.getZMCode());
//				}
//				break;
			}
		}
	};

	/** 进入公告详情 */
	private void openNoticeActivity(long noticeId, String code) {
		Intent noticeIt = new Intent(VehicleActivity.this, NoticeInfoActivity.class);
		noticeIt.putExtra(ACTIVITY_EXTRA, noticeId);
		if (code != null) {
			noticeIt.putExtra(NoticeInfoActivity.ACTIVITY_EXTRA_ZMCODE, code);
		}
		startActivity(noticeIt);
	}

	/** 空间介绍Image点击事件 */
	private View.OnClickListener photoClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mVehicleObject != null) {
				Intent intent = new Intent(VehicleActivity.this, PublicSpaceInfoActivity.class);
				mScanningcodeService.addZMObject(mVehicleObject);
				intent.putExtra(ACTIVITY_EXTRA, mVehicleObject.getId());
				startActivity(intent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 地址单击事件 */
	private OnCardClickListener addressClick = new OnCardClickListener() {
		@Override
		public void onCardClickListener(View view, boolean isArrow) {
			if (mVehicleObject != null) {
				Intent intent = new Intent(VehicleActivity.this, GeoMapLocation.class);
				intent.putExtra(ACTIVITY_EXTRA, mVehicleObject.getId());
				intent.putExtra(GeoMapLocation.ACTIVITY_LOCATION, mVehicleObject.getDrivingRoute());
				startActivity(intent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};
	/** 电话单击事件 */
	private OnCardClickListener phoneClick = new OnCardClickListener() {
		@Override
		public void onCardClickListener(View view, boolean isArrow) {
			if (mVehicleObject != null) {
				Uri uri = Uri.parse("tel:" + mVehicleObject.getPhone());
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}

	};

	/** 标题栏:保存 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 添加到收藏夹
			if (mVehicleObject != null) {
				ContactEntry entry = new ContactEntry();
				entry.setTitle(mVehicleObject.getName());
				entry.setObjectType(mVehicleObject.getZMObjectType());
				entry.setObjectId(mVehicleObject.getRemoteId());
				entry.setTelephone(mVehicleObject.getPhone());
				entry.setImageUrl(mVehicleObject.getImageUrl());
				ContactService.getInstance(VehicleActivity.this).addContact(entry, false, VehicleActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mVehicleObject != null) {
				String sms_message = String.format(getString(R.string.business_sms_message), mVehicleObject.getName(),
						mVehicleObject.getZMID(), mVehicleObject.getSpaceHomepage());
				String shareContent = String.format(getString(R.string.share_content), mVehicleObject.getName(),
						mVehicleObject.getZMID(), mVehicleObject.getSpaceHomepage());
//				SharePopupMenu sharePopupMenu = new SharePopupMenu(VehicleActivity.this,VehicleActivity.this,  v, sms_message, shareContent);
				SharePopupMenu.show(VehicleActivity.this, VehicleActivity.this, v, sms_message, shareContent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}

		}
	};
	// 赞单击事件
	private PraiseView.OnItemClickListener praiseClick = new PraiseView.OnItemClickListener() {
		@Override
		public void onItemClickListener(Praise praise) {
			if (mVehicleObject != null) {
				mScanningcodeService.doPraise(mVehicleObject, praise.type, new qraiseAddCallback(praise.getType()));
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 添加赞 */
	private class qraiseAddCallback implements IHttpRequestCallback {
		int mPraiseType;

		public qraiseAddCallback(int praiseType) {
			mPraiseType = praiseType;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			if (protocol.isHttpSuccess()) {
				if (protocol.isHandleSuccess()) {
					// 更新赞信息view
					DoPraiseProtocol p = (DoPraiseProtocol) protocol;
					mPraiseInfo = p.getPraiseInfo();
					if (mPraiseInfo != null) {
						mPraiseView.addPraiseInfo(mPraiseInfo, mPraiseType);
						HaloToast.show(getApplicationContext(), R.string.praise_success);
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				} else {
					// 添加失败getProtocolErrorMessage
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}

	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_OFFICIAL_NOTICE_LIST_PROTOCOL) {
				// 获取公告列表
				if (protocol.isHandleSuccess()) {
					GetOfficialNoticeListProtocol p = (GetOfficialNoticeListProtocol) protocol;
					mNoticeRefreshList = p.getDataList();
					setNoticeView(mNoticeRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PRAISE_PROTOCOL) {
				// 获取赞信息
				if (protocol.isHandleSuccess()) {
					// 更新赞信息view
					GetPraiseCountProtocol p = (GetPraiseCountProtocol) protocol;
					mPraiseInfo = p.getPraiseInfo();
					if (mPraiseInfo != null) {
						mPraiseView.addPraiseInfo(mPraiseInfo);
					} else {
						HaloToast.show(VehicleActivity.this, protocol.getProtocolErrorMessage());
					}
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
				// 获取图片展示列表
				if (protocol.isHandleSuccess()) {
					GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
					mPhotoList = p.getDataList();
					setAlbum(mPhotoList.getDataList());
				} else {

				}

			} else if (protocol.getProtocolType() == ProtocolType.GET_NEAR_ZMOBJECT_LIST_PROTOCOL) {
				// 获取周边推荐
				if (protocol.isHandleSuccess()) {
					GetNearZMObjectListProtocol p = (GetNearZMObjectListProtocol) protocol;
					mRecommendedList = p.getDataList();
					setRecommendedView(mRecommendedList.getDataList());
				} else {

				}

			} else if (protocol.getProtocolType() == ProtocolType.ADD_CONTACT_PROTOCOL) {
				// 收藏
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.save_success);
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取zmobject
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
					mVehicleObject = (VehicleObject) getzmProtocol.getZMObject();
					refreshView();
				}
			}
		} else {
			// TODO 网络失败
		}

	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
		// 请求服务器前

	}

	private void setTopbar() {
		ZhimaTopbar topBar = getTopbar();
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		topBar.addRightLayoutView(ll_right);

		topBar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(saveTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton3).setOnClickListener(shareTopbarClick);

		topBar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
		topBar.findViewById(R.id.layout_topbar_rightButton3).setVisibility(View.VISIBLE);
	}

	/**
	 * @Title: setSidebar
	 * @Description:设置侧边栏
	 * @param
	 * @return void
	 */
	private void setSidebar() {

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "交通工具", View.GONE, null);

		//		mSideBarView.setChildView(mChildrenView);
//		setContentView(mSideBarView.getContentView());
//		final View tranView = (View) mSideBarView.getContentView().findViewById(R.id.view_transparent);
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
	}

}
