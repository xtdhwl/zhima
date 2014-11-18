package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.consts.ZMConsts.ZMStats;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNoticeDigestListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetRecommendedZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.base.utils.UmengStatUtil;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMProductObject;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ListCardView;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.setting.activity.CorrectionFeedBackActivity;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * @ClassName: ProductActivity
 * @Description: 3000种商品
 * @author luqilong
 * @date 2012-11-5 上午10:47:30
 */
public class ZMProductActivity extends BaseActivity {
	/** 获取推荐知天使个数 */
	private static final int IDOL_COUNT = 1;
	// 备注显示最大个数
	private static final int MAX_CONTENT = 100;

	private View mChildrenView;
	// private SideBarView mSideBarView;
	/** 商品名称 */
	private TextView mProductName;
	/** 商品条码 */
	private TextView mProductCode;
	/** 商品价格 */
	private TextView mProductPrice;
	/** 商品规格 */
	private TextView mProductType;
	/** 商品备注 */
	private TextView mProductContent;
	/** 商品图片 */
	private ImageView mProductPhotoImg;
	/** 公告 */
	private ListCardView mNoticeView;
	/** 知天使View */
	private ViewGroup mIdolView;
	/** 知天使姓名 */
	private TextView mIdolNameText;
	/** 知天使签名 */
	private TextView mIdolContentText;
	/** 知天使头像 */
	private ImageView mIdolPhotoIamge;
	/** 知天使性别 */
	private ImageView mGenderIamge;
	private ZMProductObject mZMProductObject;
	private RefreshListData<Notice> mNoticeRefreshList;
	private RefreshListData<ZMObject> mRecommendedList;
	private ZMIdolObject mZMIdolObject;
	private ScanningcodeService mScanningcodeService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSidebar();
		setTopbar();
		findView();
		init();

		Intent intent = getIntent();
		long remoteId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		int zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);
		mZMProductObject = (ZMProductObject) mScanningcodeService.getCacheZMObject(remoteId, zmObjectType);
		if (mZMProductObject == null) {
			startWaitingDialog(null, getString(R.string.loading));
			ScanningcodeService.getInstance(this).getScanningInfo(remoteId, zmObjectType, this);
		} else {
			startWaitingDialog(null, getString(R.string.loading));
			refreshView();
		}
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_OFFICIAL_NOTICE_LIST_PROTOCOL) {
				// 获取协议结果
				if (protocol.isHandleSuccess()) {
					GetOfficialNoticeListProtocol p = (GetOfficialNoticeListProtocol) protocol;
					mNoticeRefreshList = p.getDataList();
					setNoticeView(mNoticeRefreshList.getDataList());
					// 访问成功
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				// 获取周边推荐
				if (protocol.isHandleSuccess()) {
					GetRecommendedZMObjectListProtocol p = (GetRecommendedZMObjectListProtocol) protocol;
					mRecommendedList = p.getDataList();
					setIdolView(mRecommendedList.getDataList());
				} else {
					mIdolView.setVisibility(View.GONE);
				}
			} else if (protocol.getProtocolType() == ProtocolType.ADD_FAVORITE_PROTOCOL) {
				// 收藏
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.favorite_success);
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
				if (protocol.isHandleSuccess()) {
					mZMProductObject = (ZMProductObject) getzmProtocol.getZMObject();
				} else {
					ZMObject zmObject = getzmProtocol.getZMObject();
					if (zmObject != null) {
						if (zmObject instanceof ZMProductObject) {
							mZMProductObject = (ZMProductObject) zmObject;
						}
					}
				}
				if (mZMProductObject != null) {
					refreshView();
				}
			}
		} else {
			// TODO 网络访问失败
			if (protocol.getProtocolType() == ProtocolType.ADD_FAVORITE_PROTOCOL) {
				HaloToast.show(ZMProductActivity.this, R.string.network_request_failed, 0);
			}
		}
	}

	// ----------------------------------------------------------------------------------------
	// event
	private void refreshView() {
		getServiceData();
		setView();
	}

	private void getServiceData() {
		// 获取官方公告
		// mScanningcodeService.getOfficialNoticeList(mUnknowObject.getCityId(),
		// true, this);
		long cityId = AccountService.getInstance(this).getCityId();// 68993155076l

		GeoCoordinate geo = ZMLocationManager.getInstance().getLastKnownGeoCoordinate();
		mNoticeRefreshList = new RefreshListData<Notice>();
		ZMSpaceService.getInstance(this).getOfficialNoticeList(mZMProductObject.getCityId(), mZMProductObject.getZMObjectType(), mNoticeRefreshList, this);
//		mScanningcodeService.getNoticeDigestList(geo, true, this);
		// 周边推荐
		mScanningcodeService.getRecommendedZMIdolObjectList(IDOL_COUNT, -1, -1, geo, true, this);
	}

	// ----------------------------------------------------------------
	// 设置view
	private void setView() {
		mProductName.setText(mZMProductObject.getName());
		mProductCode.setText(getText(R.string.commodity_code) + ": " + mZMProductObject.getBarcode());
		mProductPrice.setText(getText(R.string.indication_price) + ": " + mZMProductObject.getPrice());
		mProductType.setText(getText(R.string.product_specification) + ": " + mZMProductObject.getStandard());

		String description = mZMProductObject.getDescription();
		if (TextUtils.isEmpty(description)) {
			mProductContent.setVisibility(View.GONE);
		} else {
			//remark = 备   注：
			//注意由于需要与其他标题对齐所以在限制字符个数的时候需要添加“备   注：“的个数；
			String remark = getText(R.string.product_description).toString() + ": ";
			mProductContent
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter((MAX_CONTENT) + remark.length()) });
			mProductContent.setText(remark + description);
			mProductContent.setVisibility(View.VISIBLE);
		}

		if (TextUtils.isEmpty(mZMProductObject.getImageUrl())) {
			mProductPhotoImg.setVisibility(View.GONE);
		} else {
			mProductPhotoImg.setVisibility(View.VISIBLE);
			HttpImageLoader.getInstance(this).loadImage(mZMProductObject.getImageUrl(), mProductPhotoImg,
					getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
		}
	}

	/**
	 * 设置知天使View
	 */
	private void setIdolView(ArrayList<ZMObject> mZMObjectList) {
		// 周边知天使不为空
		if (!mZMObjectList.isEmpty()) {
			mZMIdolObject = (ZMIdolObject) mZMObjectList.get(0);
			mIdolView.setVisibility(View.VISIBLE);

			mIdolNameText.setText(mZMIdolObject.getName());
			mIdolContentText.setText("签名：" + mZMIdolObject.getSignature());

			if (GenderType.MALE.equals(mZMIdolObject.getGender())) {
				mGenderIamge.setImageResource(R.drawable.male);
			} else if (GenderType.FEMALE.equals(mZMIdolObject.getGender())) {
				mGenderIamge.setImageResource(R.drawable.female);
			} else {
				// 防止人妖
				mGenderIamge.setVisibility(View.INVISIBLE);
			}

			mIdolPhotoIamge.setVisibility(View.VISIBLE);
			HttpImageLoader.getInstance(this).loadImage(mZMIdolObject.getImageUrl(), mIdolPhotoIamge, getActivityId(),
					R.drawable.default_image, ImageScaleType.SMALL);
			mIdolView.setOnClickListener(photoClick);
			mIdolPhotoIamge.setOnClickListener(photoClick);
		}
	}

	/**
	 * 设置公告View
	 */
	private void setNoticeView(ArrayList<Notice> mNoticeList) {
		mNoticeView.setData(mNoticeList);
		mNoticeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Notice notice = (Notice) v.getTag();
				switch (v.getId()) {
				case R.id.txt_notice_one:
				case R.id.arrow:
					if (mZMIdolObject != null) {
						Intent noticeIt = new Intent(ZMProductActivity.this, NoticeActivity.class);
						noticeIt.putExtra(ACTIVITY_EXTRA, mZMIdolObject.getZMCode());
						startActivity(noticeIt);
					} else {
						ErrorManager.showErrorMessage(getApplicationContext());
					}
					break;
//				case R.id.txt_notice_two:
//					Intent officeNoticeIt = new Intent(ZMProductActivity.this, NoticeInfoActivity.class);
//					officeNoticeIt.putExtra(NoticeActivity.ACTIVITY_EXTRA, notice.getNoticeId());
//					startActivity(officeNoticeIt);
//					break;
				}
			}
		});
	}

	// ----------------------------------------------------------------------------------------
	// method

	/** 知天使相片点击事件 */
	private View.OnClickListener photoClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				UmengStatUtil.onEvent(ZMProductActivity.this, ZMStats.UNKNOW_IDOL_EVENT);
				ActivitySwitcher.openSpaceActivity(ZMProductActivity.this, mZMIdolObject.getRemoteId(),
						mZMIdolObject.getZMObjectType(), null, false);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};
	/** 更多(纠错) */
	private View.OnClickListener overFlowTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			ZhimaPopupMenu menu = new ZhimaPopupMenu(ZMProductActivity.this);
			menu.setMenuItems(R.menu.zmproduct);
			menu.show(v);
			menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public void onMenuItemClick(ZhimaMenuItem item, int position) {
					switch (item.getId()) {
					case R.id.correction:
						if (mZMProductObject != null) {
							Intent intent = new Intent(ZMProductActivity.this, CorrectionFeedBackActivity.class);
							intent.putExtra(ACTIVITY_EXTRA, mZMProductObject.getId());
							startActivity(intent);
						} else {
							ErrorManager.showErrorMessage(getApplicationContext());
						}
						break;
					}
				}
			});
		}
	};
	/** 分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String sms_message = "";
			if (mZMProductObject != null) {
				sms_message = String.format(getString(R.string.zmproduct_sms_message), mZMProductObject.getName());
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
			String shareContent = String.format(getString(R.string.share_content_no_id), mZMProductObject.getName(),
					mZMProductObject.getSpaceHomepage());

//			SharePopupMenu sharePopupMenu = new SharePopupMenu(ZMProductActivity.this,ZMProductActivity.this, v, sms_message, shareContent);
			SharePopupMenu.show(ZMProductActivity.this, ZMProductActivity.this, v, sms_message, shareContent);
		}
	};
	/** 收藏 */
	private View.OnClickListener favoriteTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 3000种商品添加到管家
			if (mZMProductObject != null) {
				FavoriteEntry favoriteEntry = new FavoriteEntry();
				favoriteEntry.setTitle(mZMProductObject.getName());
				favoriteEntry.setContent(mZMProductObject.getZMCode());
				favoriteEntry.setImageUrl(mZMProductObject.getImageUrl());
				favoriteEntry.setObjectId(mZMProductObject.getRemoteId());
				favoriteEntry.setObjectType(ZMObjectKind.ZMPRODUCT_OBJECT);
				mScanningcodeService.addFavorite(favoriteEntry, ZMProductActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	private void findView() {
		mProductName = (TextView) mChildrenView.findViewById(R.id.txt_name);
		mProductCode = (TextView) mChildrenView.findViewById(R.id.txt_code);
		mProductPrice = (TextView) mChildrenView.findViewById(R.id.txt_price);
		mProductType = (TextView) mChildrenView.findViewById(R.id.txt_type);
		mProductContent = (TextView) mChildrenView.findViewById(R.id.txt_content);
		mProductPhotoImg = (ImageView) mChildrenView.findViewById(R.id.img_photo);

		mNoticeView = (ListCardView) mChildrenView.findViewById(R.id.card_notice);
		mIdolView = (ViewGroup) mChildrenView.findViewById(R.id.layout_bottom);
		mIdolNameText = (TextView) mChildrenView.findViewById(R.id.txt_idol_name);
		mIdolContentText = (TextView) mChildrenView.findViewById(R.id.txt_idol_content);
		mIdolPhotoIamge = (ImageView) mChildrenView.findViewById(R.id.img_idol_photo);
		mGenderIamge = (ImageView) mChildrenView.findViewById(R.id.img_idol_gender);
		//设置知天使头像为gone，公告只显示一条，第二条设置为gone
//		mNoticeView.setVisibility(R.id.txt_notice_two, View.GONE);
	}

	/**
	 * 设置侧边栏
	 */
	public void setSidebar() {
		mChildrenView = View.inflate(this, R.layout.space_zmproduct_activity, null);

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "3000种商品", View.GONE, null);

		//		mSideBarView = new SideBarView(this, this, getText(R.string.scan_result).toString());
//		mSideBarView.setChildView(mChildrenView);
//		setContentView(mSideBarView.getContentView());
//
//		final View tranView = (View) mSideBarView.getContentView().findViewById(R.id.view_transparent);
//		tranView.setVisibility(View.GONE);
//		tranView.setOnClickListener(new OnClickListener() {
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

	private void setTopbar() {
		ZhimaTopbar topBar = getTopbar();

		View rightView = View.inflate(this, R.layout.topbar_rightview, null);
		RelativeLayout buttonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		RelativeLayout buttonLayout2 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton2);
		RelativeLayout buttonLayout3 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton3);

		ImageView button1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		ImageView button2 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton2);
		ImageView button3 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton3);
		button1.setImageResource(R.drawable.topbar_overflow);
		button2.setImageResource(R.drawable.topbar_share);
		button3.setImageResource(R.drawable.topbar_favorite);

		buttonLayout1.setVisibility(View.VISIBLE);
		buttonLayout2.setVisibility(View.VISIBLE);
		buttonLayout3.setVisibility(View.VISIBLE);

		topBar.setRightLayoutVisible(true);
		topBar.addRightLayoutView(rightView);

		buttonLayout1.setOnClickListener(overFlowTopbarClick);
		buttonLayout2.setOnClickListener(shareTopbarClick);
		buttonLayout3.setOnClickListener(favoriteTopbarClick);
	}

}
