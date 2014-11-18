package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetJokeListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNoticeDigestListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficialNoticeListProtocol;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Joke;
import com.zhima.data.model.Notice;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.PublicPlaceObject;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ContactService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.PhotoShowCardAdapter;
import com.zhima.ui.common.view.CardView;
import com.zhima.ui.common.view.CardView.OnCardItemClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ListCardView;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.map.activity.GeoMapLocation;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.space.adapter.JokeAdapter;
import com.zhima.ui.space.controller.NormalCardView;
import com.zhima.ui.space.controller.NormalCardView.OnCardClickListener;
import com.zhima.ui.space.controller.PraiseView;
import com.zhima.ui.space.controller.PraiseView.Praise;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * @ClassName ElevatorActivity
 * @Description 公共空间
 * @author jiang
 * @date 2012-9-20 下午01:46:29
 */
public class ElevatorActivity extends BaseActivity {
	protected static final String TAG = "ElevatorActivity";
	private View mChildrenView;
	private PublicPlaceObject mPublicPlaceObject;
	/** 第一个元素 */
	private View firstListItemView;
	/** 整个列表 */
//	private CustomListView<Joke> jokeListView;
	private PullToRefreshListView mPullListView;
	private ListView jokeListView;
	private JokeAdapter mJokeAdapter;
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
	/** 公告View */
	private ListCardView mNoticeView;
	/** 图片展示 */
	private CardView mPhotoCardView;
	/** 赞信息 */
	private PraiseInfo mPraiseInfo = null;

	/** 公告列表 */
	private RefreshListData<Notice> mNoticeRefreshList;
	/** 图片列表 */
	private RefreshListData<ZMObjectImage> mPhotoList;
	/** 推荐趣闻 */
	private RefreshListData<Joke> mRecommendedJokeList;
	private ArrayList<Joke> mJokeList;

	private ScanningcodeService mScanningcodeService;
	private HttpImageLoader mHttpImageLoader;
	// private long cityId;
	private long zmObjectId;
	private GeoCoordinate mGeoCoordinate;
	/** 是否最后一页 */
	private boolean isLastPage = false;
	/** 是否结束请求服务器 */
	private boolean isRequestFinish = false;

//	private View elseView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChildrenView = View.inflate(this, R.layout.space_elevator_activity, null);

		firstListItemView = View.inflate(this, R.layout.space_elevator_first_list, null);
//		elseView = View.inflate(this, R.layout.customlist_else_view, null);
		setSidebar();
		findView();
		init();
		setTopbar();
		setListener();

		Intent intent = getIntent();

		zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		int zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);
		mPublicPlaceObject = (PublicPlaceObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId,
				zmObjectType);

		if (mPublicPlaceObject != null) {
			startWaitingDialog("", R.string.loading);
			refreshView();
		} else {
			startWaitingDialog("", R.string.loading);
			ScanningcodeService.getInstance(this).getScanningInfo(zmObjectId, zmObjectType, this);
		}
	}

	private void setListener() {

		jokeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = parent.getItemAtPosition(position);
				if (obj instanceof Joke) {
					Joke joke = (Joke) obj;
					Intent intent = new Intent(ElevatorActivity.this, JokeInfoActivity.class);
					intent.putExtra(ACTIVITY_EXTRA, joke.getId());
					intent.putExtra(JokeInfoActivity.SPACE_NAME, mPublicPlaceObject.getName());
					intent.putExtra(JokeInfoActivity.SPACE_ID, mPublicPlaceObject.getZMID());
					intent.putExtra(JokeInfoActivity.SPACE_HOMEPAGE, mPublicPlaceObject.getSpaceHomepage());
					startActivity(intent);
				}
			}
		});
		mPullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mScanningcodeService.getJokeList(mPullListView.hasPullFromTop(), ElevatorActivity.this);
			}
		});
	}

	/**
	 * @Title: findView
	 * @Description: 初始化控件
	 * @param
	 * @return void
	 */
	private void findView() {

		mNameText = (TextView) firstListItemView.findViewById(R.id.txt_name);
		mZMIDText = (TextView) firstListItemView.findViewById(R.id.txt_zmid);
		mPhotoImage = (ImageView) firstListItemView.findViewById(R.id.img_photo);
		mPraiseView = (PraiseView) firstListItemView.findViewById(R.id.praise);
		mPraiseView.setOnItemClickListener(praiseClick);
		mTypeText = (TextView) firstListItemView.findViewById(R.id.txt_type);
		mAddressView = (NormalCardView) firstListItemView.findViewById(R.id.card_address);
		mPhoneView = (NormalCardView) firstListItemView.findViewById(R.id.card_phone);

		mNoticeView = (ListCardView) firstListItemView.findViewById(R.id.card_notice);

		mPhotoCardView = (CardView) firstListItemView.findViewById(R.id.productCard);

//		jokeListView = (CustomListView<Joke>) this.findViewById(R.id.list_elevator);
//		jokeListView.addHeadView(firstListItemView);
//		jokeListView.setVisibility(View.VISIBLE);
//		jokeListView.setOnItemClickListener(this);

		//上拉滑动
		mPullListView = (PullToRefreshListView) this.findViewById(R.id.pull_list_elevator);
		jokeListView = mPullListView.getRefreshableView();
		jokeListView.addHeaderView(firstListItemView);

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
//		setListBatchLoad();
	}

	private void refreshView() {
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
		ZMSpaceService.getInstance(this).getOfficialNoticeList(mPublicPlaceObject.getCityId(),
				mPublicPlaceObject.getZMObjectType(), mNoticeRefreshList, this);

		// 获取照片列表
		mPhotoList = mScanningcodeService.getCacheZMObjectAlbumList(zmObjectId);
		if (mPhotoList.isEmpty()) {
			mScanningcodeService.getZMObjectAlbumList(mPublicPlaceObject, true, this);
		} else {
			setAlbum(mPhotoList.getDataList());
		}

		// 赞信息
		mScanningcodeService.getPraiseCount(mPublicPlaceObject, this);

		// 随机趣闻
		mRecommendedJokeList = mScanningcodeService.getCacheRecommendedJokeList();
		mJokeList = mRecommendedJokeList.getDataList();
		if (mJokeList.isEmpty()) {
			mScanningcodeService.getJokeList(true, this);
		} else {
			setJokeView();
		}
//		if(){
//			
//		}
//		jokeListView.addLoading();
//		if (mJokeList.isEmpty()) {
//			jokeListView.mAlreadyLoadData.clear();
//			mScanningcodeService.getJokeList(true, this);
//		} else {
//			ArrayList<Joke> newDataList = mJokeList.getDataList();
//			for (int i = jokeListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
//				jokeListView.mAlreadyLoadData.add(newDataList.get(i));
//			}
//			if (newDataList.size() < 5) {
//				jokeListView.deleteLoading();
//			}
//			jokeListView.updateListView();
//		}

	}

	private void setJokeView() {
		if (mJokeAdapter == null) {
			mJokeAdapter = new JokeAdapter(this, R.layout.space_joke_list_item, mJokeList);
			jokeListView.setAdapter(mJokeAdapter);
		} else {
			//TODO 设置数据
			mJokeAdapter.notifyDataSetChanged();
		}
	}

//	

	/**
	 * 设置公告列表
	 */
	private void setNoticeView(ArrayList<Notice> notices) {
		mNoticeView.setOnClickListener(noticeClick);
		mNoticeView.setData(notices);
	}

	/**
	 * @Title: setView
	 * @Description:初始化一些基本信息
	 * @param
	 * @return void
	 */
	private void setView() {
		mNameText.setText(mPublicPlaceObject.getName());
		mZMIDText.setText("ZMID:" + mPublicPlaceObject.getZMID());
		// 设置商户头图片
		mHttpImageLoader.loadImage(mPublicPlaceObject.getImageUrl(), mPhotoImage, getActivityId(),
				R.drawable.default_image, ImageScaleType.SMALL);
		mPhotoImage.setOnClickListener(photoClick);

		mAddressView.setContent(mPublicPlaceObject.getAddress());
		mAddressView.setOnCradClickListener(addressClick);

		String phone = mPublicPlaceObject.getPhone();
		if (TextUtils.isEmpty(phone)) {
			mPhoneView.setClickable(false);
			// mPhoneView.setOnCradClickListener(phoneClick);
		} else {
			mPhoneView.setContent(phone);
			mPhoneView.setClickable(true);
			mPhoneView.setOnCradClickListener(phoneClick);
		}
		mTypeText.setText("类型:" + mPublicPlaceObject.getType());

		mPraiseView.setPraise(PraiseKind.ENVIRONMENT, mPublicPlaceObject.getEnvCount());
		mPraiseView.setPraise(PraiseKind.SERVICE, mPublicPlaceObject.getServiceCount());
		mPraiseView.setPraise(PraiseKind.QUALITY, mPublicPlaceObject.getQualityCount());
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
				if (isArrow) {
					Intent productIntent = new Intent(ElevatorActivity.this, SpaceImageShowListActivity.class);
					productIntent.putExtra(ACTIVITY_EXTRA, mPublicPlaceObject.getId());
					startActivity(productIntent);
				} else {
					// 点击的不是箭头 -->详情
					Intent productIntent = new Intent(ElevatorActivity.this, SpaceImageShowInfoActivity.class);
					productIntent.putExtra(ACTIVITY_EXTRA, mPublicPlaceObject.getId());
					productIntent.putExtra(SpaceImageShowInfoActivity.PRODUCT_POSITION, position);
					startActivity(productIntent);
				}
			}
		});
	}

	/** 进入公告详情 */
	private void openNoticeActivity(long noticeId, String code) {
		Intent noticeIt = new Intent(ElevatorActivity.this, NoticeInfoActivity.class);
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
				if (mPublicPlaceObject != null) {
					Intent noticeIt = new Intent(ElevatorActivity.this, NoticeActivity.class);
					noticeIt.putExtra(ACTIVITY_EXTRA, mPublicPlaceObject.getZMCode());
					startActivity(noticeIt);
				} else {
					ErrorManager.showErrorMessage(getApplicationContext());
				}
				break;
//			case R.id.txt_notice_two:
//				if (notice.getNoticeKind() == NoticeKind.OFFICAL) {
//					openNoticeActivity(notice.getNoticeId(), null);
//				} else if (notice.getNoticeKind() == NoticeKind.MERCHANT) {
//					openNoticeActivity(notice.getNoticeId(), mPublicPlaceObject.getZMCode());
//				}
//				break;
			}
		}
	};

	/** 空间介绍Image点击事件 */
	private View.OnClickListener photoClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPublicPlaceObject != null) {
				Intent intent = new Intent(ElevatorActivity.this, PublicSpaceInfoActivity.class);
				mScanningcodeService.addZMObject(mPublicPlaceObject);
				intent.putExtra(ACTIVITY_EXTRA, mPublicPlaceObject.getId());
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
			if (mPublicPlaceObject != null) {
				Intent intent = new Intent(ElevatorActivity.this, GeoMapLocation.class);
				intent.putExtra(ACTIVITY_EXTRA, mPublicPlaceObject.getId());
				intent.putExtra(GeoMapLocation.ACTIVITY_LOCATION, mPublicPlaceObject.getAddress());
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
			if (mPublicPlaceObject != null) {
				Uri uri = Uri.parse("tel:" + mPublicPlaceObject.getPhone());
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
			if (mPublicPlaceObject != null) {
				ContactEntry entry = new ContactEntry();
				entry.setTitle(mPublicPlaceObject.getName());
				entry.setObjectType(mPublicPlaceObject.getZMObjectType());
				entry.setObjectId(mPublicPlaceObject.getRemoteId());
				entry.setTelephone(mPublicPlaceObject.getPhone());
				entry.setImageUrl(mPublicPlaceObject.getImageUrl());
				ContactService.getInstance(ElevatorActivity.this).addContact(entry, false, ElevatorActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mPublicPlaceObject != null) {
				String sms_message = String.format(getString(R.string.business_sms_message),
						mPublicPlaceObject.getName(), mPublicPlaceObject.getZMID(),
						mPublicPlaceObject.getSpaceHomepage());
				String shareContent = String.format(getString(R.string.share_content), mPublicPlaceObject.getName(),
						mPublicPlaceObject.getZMID(), mPublicPlaceObject.getSpaceHomepage());

//				SharePopupMenu sharePopupMenu = new SharePopupMenu(ElevatorActivity.this,ElevatorActivity.this, v, sms_message, shareContent);
				SharePopupMenu.show(ElevatorActivity.this, ElevatorActivity.this, v, sms_message, shareContent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};
	// 赞单击事件
	private PraiseView.OnItemClickListener praiseClick = new PraiseView.OnItemClickListener() {
		@Override
		public void onItemClickListener(Praise praise) {
			if (mPublicPlaceObject != null) {
				mScanningcodeService.doPraise(mPublicPlaceObject, praise.type, new qraiseAddCallback(praise.getType()));
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
			isLastPage = false;
			isRequestFinish = false;
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
						// 以添加
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				} else {
					// 以添加
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
		// TODO Auto-generated method stub
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
						HaloToast.show(this, protocol.getProtocolErrorMessage());
					}
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
				// 获取图片展示列表
				if (protocol.isHandleSuccess()) {
					GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
					mPhotoList = p.getDataList();
					setAlbum(mPhotoList.getDataList());
				} else {

				}

			} else if (protocol.getProtocolType() == ProtocolType.ADD_CONTACT_PROTOCOL) {
				// 收藏
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), getText(R.string.save_success).toString(), 1);
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_JOKE_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetJokeListProtocol p = (GetJokeListProtocol) protocol;
					mRecommendedJokeList = p.getDataList();
					mJokeList = mRecommendedJokeList.getDataList();
					setJokeView();
					mPullListView.setLastPage(mRecommendedJokeList.isLastPage());
				}
				mPullListView.onRefreshComplete(mPullListView.hasPullFromTop());

			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取zmobject
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
					mPublicPlaceObject = (PublicPlaceObject) getzmProtocol.getZMObject();
					refreshView();
				}
			}
		} else {
			mPullListView.setEmptyView();
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}

	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
		// 请求服务器前
		isLastPage = false;
		isRequestFinish = false;
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

		ViewInitTools.setTopBar(this, "公共空间", View.GONE, null);

//		mSideBarView = new SideBarView(this, this, getText(R.string.elevator_space).toString());
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLastPage = false;
		isRequestFinish = false;
	}

	//-------------------------------------------------------------------
	/**
	 * // * @Title: setListBatchLoad // * @Description:设置分批加载 // * @param // * @return
	 * void //
	 */
//	private void setListBatchLoad() {
//		jokeListView.setBatchLoad(new ZhimaAdapter<Joke>(this, R.layout.space_joke_list_item,
//				jokeListView.mAlreadyLoadData) {
//
//		
//
//			@Override
//			public boolean isEnabled(int position) {
//				return super.isEnabled(position);
//			}
//
//			@Override
//			public void getFirstData() {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void getData() {
//				// TODO Auto-generated method stub
//
//				if (isLastPage) {
//					if (!isRequestFinish) {
//						HaloToast.show(mContext, R.string.no_more_data);
//					}
//					isRequestFinish = true;
//					return;
//				}
//				jokeListView.isLoading(true);
//				jokeListView.addLoading();
//				mScanningcodeService.getJokeList(false, ElevatorActivity.this);
//			}
//
//		
//		});
//	}

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		Object obj = parent.getItemAtPosition(position);
//		if (obj instanceof Joke) {
//			Joke joke = (Joke) obj;
//			parent.getItemAtPosition(position);
//			Intent intent = new Intent(ElevatorActivity.this, JokeInfoActivity.class);
//			intent.putExtra(ACTIVITY_EXTRA, joke.getId());
//			if (mPublicPlaceObject == null) {
//				mPublicPlaceObject = new PublicPlaceObject();
//			}
//			intent.putExtra(JokeInfoActivity.SPACE_NAME, mPublicPlaceObject.getName());
//			intent.putExtra(JokeInfoActivity.SPACE_ID, mPublicPlaceObject.getZMID());
//			intent.putExtra(JokeInfoActivity.SPACE_HOMEPAGE, mPublicPlaceObject.getSpaceHomepage());
//			startActivity(intent);
//		}
//	}

	//网络请求
//	 else if (protocol.getProtocolType() == ProtocolType.GET_JOKE_LIST_PROTOCOL) {
//			if (protocol.isHandleSuccess()) {
//				GetJokeListProtocol p = (GetJokeListProtocol) protocol;
//				if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
//					jokeListView.deleteLoading();
//					jokeListView.updateListView();
//					isLastPage = true;
//					jokeListView.addFooterView(elseView, null, false);
//					jokeListView.isAddSecondFooterView(true);
//				} else {
//					mRecommendedJokeList = p.getDataList();
//					ArrayList<Joke> newDataList = mRecommendedJokeList.getDataList();
//					for (int i = jokeListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
//						jokeListView.mAlreadyLoadData.add(newDataList.get(i));
//					}
//					jokeListView.updateListView();
//
//					if (p.getDataList().isLastPage()) {
//						isLastPage = true;
//						jokeListView.deleteLoading();
//						jokeListView.addFooterView(elseView, null, false);
//						jokeListView.isAddSecondFooterView(true);
//					}
//				}
//
//			}

}
