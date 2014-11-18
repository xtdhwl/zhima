package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.zhima.base.protocol.CommerceProtocolHandler.CheckMyCouponOfCommerceProtocolHandler;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommerceProductProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommercePromotionListProtocol;
import com.zhima.base.protocol.CommerceProtocolHandler.GetMyCouponOfCommerceListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNearZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNoticeDigestListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommerceProduct;
import com.zhima.data.model.CommercePromotion;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ContactService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.DrawableCardAdatper;
import com.zhima.ui.adapter.ProductCardAdapter;
import com.zhima.ui.adapter.PromotionCardAdapter;
import com.zhima.ui.common.view.CardView;
import com.zhima.ui.common.view.CardView.OnCardItemClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ListCardView;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
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
 * 商业空间
 * 
 * @ClassName:BusinessActivity
 * @Description:TODO
 * @author liqilong
 * @date 2012-7-9 下午4:42:36
 * 
 */
public class BusinessActivity extends BaseActivity {
	// private final String TAG = "BusinessActivity";
	// 左边推荐个数
	private final static int msg_show_message = 10;

	private static final String TAG = "BusinessActivity";

	private View mChildrenView;

	/** 标题 */
	private TextView mNameText;
	private TextView mZMIDText;
	/** 类型 */
	private TextView mTypeText;
	/** 电话 */
	private NormalCardView mPhoneView;
	/** 地址 */
	private NormalCardView mAddressView;
	/** 商家图片 */
	private ImageView mPhotoImage;
	/** 赞 */
	private PraiseView mPraiseView;
	/** 商家商品列表 */
	private CardView mProductCardView;
	/** 商家活动列表 */
	private CardView mPromotionCardView;
	/** 周边推荐列表 */
	private CardView mRecommendedCardView;

	/** 公告View */
	private ListCardView mNoticeView;
	/** 公告列表 */
	private RefreshListData<Notice> mNoticeRefreshList;
	/** 商品列表 */
	private RefreshListData<CommerceProduct> mProductRefreshList;
	/** 商家活动列表 */
	private RefreshListData<CommercePromotion> mPromotionRefreshList;
	/** 周边推荐列表 */
	private RefreshListData<ZMObject> mRecommendedRefreshList;
	private ArrayList<ZMObject> mCacheZMObjectList;
	/*** 赞信息 */
	private PraiseInfo mPraiseInfo = null;

	private CommerceObject mCommerceObject;
	private GeoCoordinate mGeoCoordinate;

	private ScanningcodeService mScanningcodeService;
	private CommerceService mCommerceService;
	private HttpImageLoader mHttpImageLoader;
	private ZMLocationManager mZMLocationManager;

	private boolean mIsFirstFlag = true;

	private long remoteId;
	private int zmObjectType;

	private DrawableCardAdatper drawableCardAdapter;
	private PromotionCardAdapter promotionCardAdapter;
	private ProductCardAdapter productCardAdapter;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == msg_show_message) {
				MessageDialog dialog = new MessageDialog(BusinessActivity.this, mNameText);
				dialog.setTitle(R.string.dialog_title);
				dialog.setMessage(R.string.mycoupon_of_commerce);
				dialog.setRightBtText(R.string.look);
				dialog.setLeftBtText(R.string.cancel);
				dialog.setOnBtClickListener(new OnBtClickListener() {
					@Override
					public void onRightBtClick() {
						startWaitingDialog("", R.string.loading);
						mCommerceService.getMyCouponOfCommerceList(mCommerceObject, true, BusinessActivity.this);
					}

					@Override
					public void onLeftBtClick() {

					}
				});
				dialog.show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChildrenView = View.inflate(this, R.layout.space_business_activity, null);
		setSidebar();
		findId();

		setTopbar();
		init();

		Intent intent = getIntent();
		remoteId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);
		mCacheZMObjectList = new ArrayList<ZMObject>();
		mCommerceObject = (CommerceObject) mScanningcodeService.getCacheZMObject(remoteId, zmObjectType);
		if (mCommerceObject != null) {
			startWaitingDialog("", R.string.loading);
			refreshView();
		} else {
			startWaitingDialog("", R.string.loading);
			ScanningcodeService.getInstance(this).getScanningInfo(remoteId, zmObjectType, this);
		}
	}

	private void refreshView() {
		// 请求服务器数据
		if (NetUtils.isNetworkAvailable(this)) {
			setView();
			getServiceData();
		} else {
			HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
		}
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mCommerceService = CommerceService.getInstance(this);
		mHttpImageLoader = HttpImageLoader.getInstance(this);
		mZMLocationManager = ZMLocationManager.getInstance();
	}

	// -----------------------------------------------------------
	// 请求服务器数据
	// -----------------------------------------------------------
	private void getServiceData() {
		// ------------------------
		// 商家相片 --
		// 商品公告 --
		// 商品列表 --
		// 商品活动 --
		// 周边推荐
		// 获取赞信息
		// ------------------------
		// 获取官方公告列表
		// 检查当前用户是否拥有该商户的优惠券
		if (mIsFirstFlag) { // 注意这个位置不能修改.要在第一位
			mIsFirstFlag = false;
			mCommerceService.checkMyCouponOfCommerce(mCommerceObject, this);
		}

		mGeoCoordinate = mZMLocationManager.getLastKnownGeoCoordinate();
		/** 获取公告列表 */
		mNoticeRefreshList = new RefreshListData<Notice>();
		ZMSpaceService.getInstance(this).getOfficialNoticeList(mCommerceObject.getCityId(),
				mCommerceObject.getZMObjectType(), mNoticeRefreshList, this);

		// 获取商品列表
		mProductRefreshList = mCommerceService.getCacheCommerceProductList(mCommerceObject);
		if (mProductRefreshList.isEmpty()) {
			mCommerceService.getCommerceProductList(mCommerceObject, true, this);
		} else {
			setProductView(mProductRefreshList.getDataList());
		}

		// 获取商家活动列表
		mPromotionRefreshList = mCommerceService.getCacheCommercePromotionList(mCommerceObject);
		if (mPromotionRefreshList.isEmpty()) {
			mCommerceService.getCommercePromotionList(mCommerceObject, true, this);
		} else {
			setPromotionView(mPromotionRefreshList.getDataList());
		}
		// 赞信息
		mScanningcodeService.getPraiseCount(mCommerceObject, this);

		// 周边推荐
		mScanningcodeService.getNearZMObject(ZMObjectKind.BUSINESS_OBJECT, mCommerceObject,
				mCommerceObject.getCityId(), mGeoCoordinate, this);// ;

	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_COMMERCE_PRODUCT_LIST_PROTOCOL) {
				// 获取商户产品列表结果
				if (protocol.isHandleSuccess()) {
					// 更新产品view
					GetCommerceProductProtocol p = (GetCommerceProductProtocol) protocol;
					mProductRefreshList = p.getDataList();
					setProductView(mProductRefreshList.getDataList());
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
					}
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_COMMERCE_PROMOTION_LIST_PROTOCOL) {
				// 获取商户活动列表结果
				if (protocol.isHandleSuccess()) {
					GetCommercePromotionListProtocol p = (GetCommercePromotionListProtocol) protocol;
					mPromotionRefreshList = p.getDataList();
					setPromotionView(mPromotionRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_NEAR_ZMOBJECT_LIST_PROTOCOL) {
				// 获取周边推荐
				if (protocol.isHandleSuccess()) {
					GetNearZMObjectListProtocol p = (GetNearZMObjectListProtocol) protocol;
					mRecommendedRefreshList = p.getDataList();
					addCacheZMObjectList(mRecommendedRefreshList.getDataList());
					setRecommendedView(mCacheZMObjectList);
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_OFFICIAL_NOTICE_LIST_PROTOCOL) {
				// 获取公告列表
				if (protocol.isHandleSuccess()) {
					GetOfficialNoticeListProtocol p = (GetOfficialNoticeListProtocol) protocol;
					mNoticeRefreshList = p.getDataList();
					setNoticeView(mNoticeRefreshList.getDataList());
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
					mCommerceObject = (CommerceObject) getzmProtocol.getZMObject();
					refreshView();
				}
			} else if (protocol.getProtocolType() == ProtocolType.CHECK_MYCOUPON_OF_COMMERCE_PROTOCOL) {
				// 检查当前用户是否拥有该商户的优惠券
				if (protocol.isHandleSuccess()) {
					CheckMyCouponOfCommerceProtocolHandler p = (CheckMyCouponOfCommerceProtocolHandler) protocol;
					if (p.hasCoupon()) {
						mHandler.sendEmptyMessage(msg_show_message);
					}
				} else {
					// 没有
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_MYCOUPON_OF_COMMERCE_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetMyCouponOfCommerceListProtocol p = (GetMyCouponOfCommerceListProtocol) protocol;
					RefreshListData<UserCoupon> data = p.getDataList();
					// 有优惠券
					if (data.getDataList().size() > 0) {
						// 跳转用户优惠券
						Intent promotionIntent = new Intent(BusinessActivity.this, CouponListActivity.class);
						promotionIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
						startActivity(promotionIntent);
					}
				} else {

				}
			}
		} else {
			// TODO 网络访问错误
			if (protocol.getProtocolType() == ProtocolType.ADD_CONTACT_PROTOCOL) {
				HaloToast.show(BusinessActivity.this, R.string.network_request_failed, 0);
			}
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 请求服务器前

	}

	private void addCacheZMObjectList(ArrayList<ZMObject> dataList2) {
		for (ZMObject obj : dataList2) {
			mCacheZMObjectList.add(obj);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCacheZMObjectList.clear();
		mCacheZMObjectList = null;
	}

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
						// 添加失败
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

	// --------------------------------------------------
	// 界面显示处理
	// --------------------------------------------------
	/**
	 * 设置商品列表View
	 */
	private void setProductView(final ArrayList<CommerceProduct> products) {
		if (productCardAdapter == null) {
			productCardAdapter = new ProductCardAdapter(this, R.layout.space_darwable_card_item, products);
			mProductCardView.setAdapter(productCardAdapter);
			mProductCardView.setOnItemClickListener(new OnCardItemClickListener() {
				@Override
				public void onItemClickListener(View view, int position, boolean isArrow) {
					if (isArrow) {
						// 点击的是箭头 -->商品列表
						Intent productIntent = new Intent(BusinessActivity.this, ProductListActivity.class);
						productIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
						startActivity(productIntent);
					} else {
						// 点击的不是箭头 -->商品详情
						Intent productIntent = new Intent(BusinessActivity.this, ProductInfoActivity.class);
						productIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
						productIntent.putExtra(ProductInfoActivity.PRODUCT_POSITION, position);
						startActivity(productIntent);
					}

				}
			});
		} else {
			productCardAdapter.setData(products);
			productCardAdapter.notifyDataSetChanged();
		}

	}

	/** 设置商家活动 */
	private void setPromotionView(final ArrayList<CommercePromotion> promotions) {
		if (promotionCardAdapter == null) {
			promotionCardAdapter = new PromotionCardAdapter(this, R.layout.space_business_promotion_item, promotions,
					false);
			mPromotionCardView.setAdapter(promotionCardAdapter);
			mPromotionCardView.setOnItemClickListener(new OnCardItemClickListener() {
				@Override
				public void onItemClickListener(View view, int position, boolean isArrow) {
					if (isArrow) {
						// 商家活动列表信息
						Intent promotionIntent = new Intent(BusinessActivity.this, PromotionListActivity.class);
						promotionIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
						startActivity(promotionIntent);

					} else {
						// 活动详细信息
						Intent promotionIntent = new Intent(BusinessActivity.this, PromotionInfoActivity.class);
						promotionIntent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
						promotionIntent.putExtra(PromotionInfoActivity.PROMOTION_POSITION, position);
						startActivity(promotionIntent);
					}

				}
			});
		} else {
			promotionCardAdapter.setData(promotions);
			promotionCardAdapter.notifyDataSetChanged();
		}

	}

	/** 设置周边推荐View */
	private void setRecommendedView(final ArrayList<ZMObject> recommendedObjects) {
		if (drawableCardAdapter == null) {
			drawableCardAdapter = new DrawableCardAdatper(this, R.layout.space_darwable_card_item, recommendedObjects);
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
						ActivitySwitcher.openSpaceActivity(BusinessActivity.this,
								drawableCardAdapter.getItem(position), flagBundle, isFinish);
					}
				}
			});
		} else {
			drawableCardAdapter.setData(recommendedObjects);
			drawableCardAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 设置公告列表
	 */
	private void setNoticeView(ArrayList<Notice> notices) {
		mNoticeView.setOnClickListener(noticeClick);
		mNoticeView.setData(notices);
	}

	// -----------------------------------------
	// 事件处理
	// -----------------------------------------

	/** 进入公告详情 */
	private void openNoticeActivity(long noticeId, String code) {
		Intent noticeIt = new Intent(BusinessActivity.this, NoticeInfoActivity.class);
		noticeIt.putExtra(ACTIVITY_EXTRA, noticeId);
		if (code != null) {
			noticeIt.putExtra(NoticeInfoActivity.ACTIVITY_EXTRA_ZMCODE, code);
		}
		startActivity(noticeIt);
	}

	/** 点击公告 */
	private View.OnClickListener noticeClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			Notice notice = (Notice) v.getTag();
			switch (v.getId()) {
			case R.id.txt_notice_one:
			case R.id.arrow:
				if (mCommerceObject != null) {
					Intent noticeIt = new Intent(BusinessActivity.this, NoticeActivity.class);
					noticeIt.putExtra(ACTIVITY_EXTRA, mCommerceObject.getZMCode());
					startActivity(noticeIt);
				} else {
					ErrorManager.showErrorMessage(getApplicationContext());
				}
				break;
//			case R.id.txt_notice_two:
//				if (notice.getNoticeKind() == NoticeKind.OFFICAL) {
//					openNoticeActivity(notice.getNoticeId(), null);
//				} else if (notice.getNoticeKind() == NoticeKind.MERCHANT) {
//					openNoticeActivity(notice.getNoticeId(), mCommerceObject.getZMCode());
//				}
//				break;
			}
		}
	};

	/** 标题栏:保存 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
//			// 添加到收藏夹
//			if (mCommerceObject != null) {
//				ContactEntry entry = new ContactEntry();
//				entry.setTitle(mCommerceObject.getName());
//				entry.setObjectType(mCommerceObject.getZMObjectType());
//				entry.setObjectId(mCommerceObject.getRemoteId());
//				entry.setTelephone(mCommerceObject.getPhone());
//				entry.setImageUrl(mCommerceObject.getImageUrl());
//				ContactService.getInstance(BusinessActivity.this).addContact(entry, false, BusinessActivity.this);
//			} else {
//				ErrorManager.showErrorMessage(getApplicationContext());
//			}

//			Intent intent = new Intent(BusinessActivity.this, ZmSpacePlazaActivity.class);
//			intent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
//			startActivity(intent);
//			// 添加到收藏夹
			if (mCommerceObject != null) {
				ContactEntry entry = new ContactEntry();
				entry.setTitle(mCommerceObject.getName());
				entry.setObjectType(mCommerceObject.getZMObjectType());
				entry.setObjectId(mCommerceObject.getRemoteId());
				entry.setTelephone(mCommerceObject.getPhone());
				entry.setImageUrl(mCommerceObject.getImageUrl());
				ContactService.getInstance(BusinessActivity.this).addContact(entry, false, BusinessActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCommerceObject != null) {
				String shareContent = String.format(getString(R.string.share_content), mCommerceObject.getName(),
						mCommerceObject.getZMID(), mCommerceObject.getSpaceHomepage());
				String sms_message = String.format(getString(R.string.business_sms_message), mCommerceObject.getName(),
						mCommerceObject.getZMID(), mCommerceObject.getSpaceHomepage());
//				SharePopupMenu sharePopupMenu = new SharePopupMenu(BusinessActivity.this, BusinessActivity.this, v,
//						sms_message, shareContent);
				SharePopupMenu.show(BusinessActivity.this, BusinessActivity.this, v, sms_message, shareContent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 空间介绍Image点击事件 */
	private View.OnClickListener photoClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCommerceObject != null) {
				Intent intent = new Intent(BusinessActivity.this, BusinessInfoActivity.class);
				mScanningcodeService.addZMObject(mCommerceObject);
				intent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
				startActivity(intent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 赞单击事件 */
	private PraiseView.OnItemClickListener praiseClick = new PraiseView.OnItemClickListener() {
		@Override
		public void onItemClickListener(Praise praise) {
			if (mCommerceObject != null) {
				mScanningcodeService.doPraise(mCommerceObject, praise.type, new qraiseAddCallback(praise.getType()));
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 地址单击事件 */
	private OnCardClickListener addressClick = new OnCardClickListener() {
		@Override
		public void onCardClickListener(View view, boolean isArrow) {
			if (mCommerceObject != null) {
				Intent intent = new Intent(BusinessActivity.this, GeoMapLocation.class);
				intent.putExtra(ACTIVITY_EXTRA, mCommerceObject.getId());
				intent.putExtra(GeoMapLocation.ACTIVITY_LOCATION, mCommerceObject.getAddress());
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
			if (mCommerceObject != null) {
				Uri uri = Uri.parse("tel:" + mCommerceObject.getPhone());
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	private ZhimaTopbar topBar;

	// 初始化
	private void findId() {
		mNameText = (TextView) mChildrenView.findViewById(R.id.txt_name);
		mZMIDText = (TextView) mChildrenView.findViewById(R.id.txt_zmid);
		mTypeText = (TextView) mChildrenView.findViewById(R.id.txt_type);
		mPhotoImage = (ImageView) mChildrenView.findViewById(R.id.img_photo);
		mPraiseView = (PraiseView) mChildrenView.findViewById(R.id.praise);
		mPraiseView.setOnItemClickListener(praiseClick);

		mAddressView = (NormalCardView) mChildrenView.findViewById(R.id.card_address);
		mPhoneView = (NormalCardView) mChildrenView.findViewById(R.id.card_phone);

		mNoticeView = (ListCardView) mChildrenView.findViewById(R.id.card_notice);

		mProductCardView = (CardView) mChildrenView.findViewById(R.id.productCard);
		mPromotionCardView = (CardView) mChildrenView.findViewById(R.id.promotionCard);
		mRecommendedCardView = (CardView) mChildrenView.findViewById(R.id.recommendedCard);

		mPraiseView.addPraiseView(PraiseKind.ENVIRONMENT, PraiseKind.SERVICE, PraiseKind.QUALITY);
	}

	private void setView() {
		mNameText.setText(mCommerceObject.getName());
		mZMIDText.setText("ZMID:" + mCommerceObject.getZMID());
		mTypeText.setText(getText(R.string.type) + ":" + mCommerceObject.getType());
		// 设置商户头图片

		mHttpImageLoader.loadImage(mCommerceObject.getImageUrl(), mPhotoImage, getActivityId(),
				R.drawable.default_image, ImageScaleType.SMALL);

		mAddressView.setContent(mCommerceObject.getAddress());
		mAddressView.setOnCradClickListener(addressClick);

		String phone = mCommerceObject.getPhone();
		if (TextUtils.isEmpty(phone)) {
			mPhoneView.setClickable(false);
		} else {
			mPhoneView.setContent(phone);
			mPhoneView.setClickable(true);
			mPhoneView.setOnCradClickListener(phoneClick);
		}

		mPraiseView.setPraise(PraiseKind.ENVIRONMENT, mCommerceObject.getEnvCount());
		mPraiseView.setPraise(PraiseKind.SERVICE, mCommerceObject.getServiceCount());
		mPraiseView.setPraise(PraiseKind.QUALITY, mCommerceObject.getQualityCount());
		mPhotoImage.setOnClickListener(photoClick);
	}

	private void setTopbar() {
		// 标题栏
		topBar = getTopbar();
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

		ViewInitTools.setTopBar(this, "商业空间", View.GONE, null);

//		mSideBarView = new SideBarView(this, this, getText(R.string.business_space).toString());
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
