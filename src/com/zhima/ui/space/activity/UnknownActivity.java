package com.zhima.ui.space.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.util.Linkify;
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
import com.zhima.base.utils.NetUtils;
import com.zhima.base.utils.UmengStatUtil;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UnknownObject;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ListCardView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 未知空间
 * 
 * @ClassName:UnknownActivity
 * @Description:TODO
 * @author liqilong
 * @date 2012-7-10 上午11:37:33
 * 
 */
public class UnknownActivity extends BaseActivity {
	protected static final String TAG = "UnknownActivity";
	/** 获取推荐知天使个数 */
	private static final int IDOL_COUNT = 1;

	/** 扫描码 */
	private TextView mZMCodeText;

	private TextView mScanTimeText;

	/** 官方公告 */
	private ListCardView mNoticeView;

	/** 知天使姓名 */
	private TextView mIdolNameText;
	/** 知天使签名 */
	private TextView mIdolContentText;
	/** 知天使头像 */
	private ImageView mIdolPhotoIamge;

	/** 显示没有网络 */
	private TextView mNoNetText;
	// /** 下面的View,包含公告,和知天使 */
	private ViewGroup mIdolView;
	/** 公告 */
	private Notice mNotice;
	/** 知天使 */
	private ZMIdolObject mIdol;
	private UnknownObject mUnknowObject;

	/** 城市id */
	private long mRegionId;
	/** 官方公告列表 */
	private RefreshListData<Notice> mNoticeRefreshList;
	private ArrayList<Notice> mNoticeList;
	private RefreshListData<ZMObject> mRecommendedList;
	ScanningcodeService mScanningcodeService;
	private ImageView mGenderIamge;
	private ZMLocationManager mLocationManager;
	private String mZmCode;

	private View mChildrenView;
	private boolean mIsCache = true;
	private String zmCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setSidebar();
		findView();
		if (NetUtils.isNetworkAvailable(this)) {
			setTopbar(true);
		} else {
			setTopbar(false);
		}

		Intent intent = getIntent();
		zmCode = intent.getStringExtra(ACTIVITY_EXTRA);
		if (!TextUtils.isEmpty(zmCode)) {
			mUnknowObject = (UnknownObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmCode);
		}
		init();
		if (mUnknowObject == null) {
			startWaitingDialog(null, getString(R.string.loading));
			ScanningcodeService.getInstance(this).getScanningInfo(zmCode, this);
		} else {
			startWaitingDialog(null, getString(R.string.loading));
			refreshView();
		}
	}

	private void refreshView() {
		getServiceData();
		setView();
	}

	/**
	 * 设置侧边栏
	 */
	public void setSidebar() {
		mChildrenView = View.inflate(this, R.layout.space_unknown_activity, null);

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "扫码结果", View.GONE, null);

//		mSideBarView = new SideBarView(this, this, "扫码结果");
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

	private void setView() {
		mZmCode = mUnknowObject.getZMCode();
		mZMCodeText.setText(mUnknowObject.getZMCode());
		//XXX 时间显示问题。应该现实扫描时间（无网络和3000种商品）
		mScanTimeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

		if (!TextUtils.isEmpty(mZmCode) && mZmCode.length() >= 5 && mZmCode.length() <= 13) {
			mZMCodeText.setAutoLinkMask(Linkify.ALL);
		}
	}

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		//		mNoticeRefreshList = mScanningcodeService.getCacheOfficialNoticeList(mRegionId);
		mLocationManager = ZMLocationManager.getInstance();
	}

	private void getServiceData() {
		// 获取官方公告
		//		mScanningcodeService.getOfficialNoticeList(mUnknowObject.getCityId(), true, this);		
		long cityId = AccountService.getInstance(this).getCityId();//68993155076l
		//		if (cityId <= 0) {
		//			Address address = mLocationManager.getGeoLastKnownAddress();
		//			if (address != null) {
		//				if (!TextUtils.isEmpty(address.getSubLocality())) {
		//					cityId = RegionService.getInstance(this).getCityIdByName(address.getSubLocality());
		//				} else if (!TextUtils.isEmpty(address.getSubAdminArea())) {
		//					cityId = RegionService.getInstance(this).getCityIdByName(address.getSubAdminArea());
		//				} else if (!TextUtils.isEmpty(address.getAdminArea())) {
		//					cityId = RegionService.getInstance(this).getCityIdByName(address.getAdminArea());
		//				}
		//			}
		//		}
		GeoCoordinate geo = mLocationManager.getLastKnownGeoCoordinate();

		mNoticeRefreshList = new RefreshListData<Notice>();
		ZMSpaceService.getInstance(this).getOfficialNoticeList(mUnknowObject.getCityId(),
				mUnknowObject.getZMObjectType(), mNoticeRefreshList, this);
		// 周边推荐		
		mScanningcodeService.getRecommendedZMIdolObjectList(IDOL_COUNT, -1, -1, geo, true, this);
	}

	/**
	 * 设置公告View
	 */
	private void setNoticeView() {
		mNoticeList = mNoticeRefreshList.getDataList();
		mNoticeView.setData(mNoticeList);
		mNoticeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Notice notice = (Notice) v.getTag();
				switch (v.getId()) {
				case R.id.txt_notice_one:
				case R.id.arrow:
					if (mUnknowObject != null) {
						Intent noticeIt = new Intent(UnknownActivity.this, NoticeActivity.class);
						noticeIt.putExtra(ACTIVITY_EXTRA, mUnknowObject.getZMCode());
						startActivity(noticeIt);
					} else {
						ErrorManager.showErrorMessage(getApplicationContext());
					}
					break;
//				case R.id.txt_notice_two:
//					Intent officeNoticeIt = new Intent(UnknownActivity.this, NoticeInfoActivity.class);
//					officeNoticeIt.putExtra(NoticeActivity.ACTIVITY_EXTRA, notice.getNoticeId());
//					startActivity(officeNoticeIt);
//					break;
				}
			}
		});
	}

	/**
	 * 设置知天使View
	 */
	private void setIdolView() {
		if (!mRecommendedList.getDataList().isEmpty()) {
			mIdolView.setVisibility(View.VISIBLE);
			mIdol = (ZMIdolObject) mRecommendedList.getDataList().get(0);

			mIdolNameText.setText(mIdol.getName());
			mIdolContentText.setText("签名：" + mIdol.getSignature());

			if (GenderType.MALE.equals(mIdol.getGender())) {
				mGenderIamge.setImageResource(R.drawable.male);
			} else if (GenderType.FEMALE.equals(mIdol.getGender())) {
				mGenderIamge.setImageResource(R.drawable.female);
			} else {
				// 防止人妖
				mGenderIamge.setVisibility(View.INVISIBLE);
			}
			HttpImageLoader.getInstance(this).loadImage(mIdol.getImageUrl(), mIdolPhotoIamge, getActivityId(),
					R.drawable.default_image, ImageScaleType.SMALL);
			mIdolView.setOnClickListener(photoClick);
			mIdolPhotoIamge.setOnClickListener(photoClick);
		}
	}

	// 知天使相片点击事件
	private View.OnClickListener photoClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mIdol != null) {
				UmengStatUtil.onEvent(UnknownActivity.this, ZMStats.UNKNOW_IDOL_EVENT);
				ActivitySwitcher.openSpaceActivity(UnknownActivity.this, mIdol.getRemoteId(), mIdol.getZMObjectType(),
						null, false);//(UnknownActivity.this, mIdol)
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

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
					setNoticeView();
					// 访问成功
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				// 获取周边推荐
				if (protocol.isHandleSuccess()) {
					GetRecommendedZMObjectListProtocol p = (GetRecommendedZMObjectListProtocol) protocol;
					mRecommendedList = p.getDataList();
					setIdolView();
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
					mUnknowObject = (UnknownObject) getzmProtocol.getZMObject();
				} else {
					ZMObject zmObject = getzmProtocol.getZMObject();
					if (zmObject != null) {
						if (zmObject instanceof UnknownObject) {
							mUnknowObject = (UnknownObject) zmObject;
						}
					}
				}
				if (mUnknowObject != null) {
					refreshView();
				}
			}
		} else {
			// TODO 网络访问失败
//			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
	}

	/** 收藏 */
	private View.OnClickListener favoriteTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			FavoriteEntry favoriteEntry = new FavoriteEntry();
			favoriteEntry.setFavoriteTime(System.currentTimeMillis());
			favoriteEntry.setTitle(mZmCode);
			favoriteEntry.setContent(mZmCode);
			favoriteEntry.setObjectType(ZMObjectKind.UNKNOWN_OBJECT);
			ScanningcodeService.getInstance(UnknownActivity.this).addFavorite(favoriteEntry, UnknownActivity.this);
		}
	};
	/** 分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String sms_message = String.format(UnknownActivity.this.getResources().getString(R.string.sms_message),
					mZmCode);
			String shareContent = String.format(UnknownActivity.this.getResources().getString(R.string.sms_message),
					mZmCode);

//			SharePopupMenu sharePopupMenu = new SharePopupMenu(UnknownActivity.this,UnknownActivity.this, v, sms_message, shareContent);
			SharePopupMenu.show(UnknownActivity.this, UnknownActivity.this, v, sms_message, shareContent);
		}
	};
	/** 复制剪贴板 */
	private View.OnClickListener overFlowTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (zmCode != null) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(zmCode);
				HaloToast.show(UnknownActivity.this, getText(R.string.clipboard_msg).toString());
			}
		}
	};

	private void findView() {
		mZMCodeText = (TextView) mChildrenView.findViewById(R.id.txt_zmcode);

		mScanTimeText = (TextView) mChildrenView.findViewById(R.id.txt_unknownCode_scanTime);

		mNoticeView = (ListCardView) mChildrenView.findViewById(R.id.card_notice);

		mIdolView = (ViewGroup) mChildrenView.findViewById(R.id.layout_bottom);
		mIdolNameText = (TextView) mChildrenView.findViewById(R.id.txt_idol_name);
		mIdolContentText = (TextView) mChildrenView.findViewById(R.id.txt_idol_content);
		mIdolPhotoIamge = (ImageView) mChildrenView.findViewById(R.id.img_idol_photo);
		mGenderIamge = (ImageView) mChildrenView.findViewById(R.id.img_idol_gender);
		mIdolView.setVisibility(View.GONE);
		mNoNetText = (TextView) mChildrenView.findViewById(R.id.txt_no_net);
//		mNoticeView.setVisibility(R.id.txt_notice_two, View.GONE);
	}

	private void setTopbar(boolean bl) {
		ZhimaTopbar topBar = getTopbar();

		View rightView = View.inflate(this, R.layout.topbar_rightview, null);
		RelativeLayout buttonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		RelativeLayout buttonLayout2 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton2);
		RelativeLayout buttonLayout3 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton3);

		ImageView button1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		ImageView button2 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton2);
		ImageView button3 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton3);
		button1.setImageResource(R.drawable.copy);
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
