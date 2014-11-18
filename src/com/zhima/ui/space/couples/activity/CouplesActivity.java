package com.zhima.ui.space.couples.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetRecommendedZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetJournalListProtocol;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetMultimediaListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMCouplesService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.DrawableCardAdatper;
import com.zhima.ui.common.view.CardView;
import com.zhima.ui.common.view.CardView.OnCardItemClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZMGallery;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.sidebar.SideBarView.OnStateChangeListener;
import com.zhima.ui.space.activity.IdoPhotoActivity;
import com.zhima.ui.space.adapter.AcqierementAdapter;
import com.zhima.ui.space.adapter.IdolAlbumAdatper;
import com.zhima.ui.space.adapter.MultimediaAdapter;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * @ClassName CouplesActivity
 * @Description 情侣知天使空间
 * @author jiang
 * @date 2012-12-3 上午11:15:16
 */
public class CouplesActivity extends BaseActivity {
	protected static final String TAG = "CouplesActivity";

	private View mChildrenView;

	/** 其他情侣空间个数 */
	private static final int OTHER_IDOL_COUNT = 3;
	/** 控制显示日志个数 */
	private static final int ACQIEREMENT_COUNT = 3;

	private ImageView mPhotoImage;
	// private TextView mBirthdayText;
	// private TextView mBloodTypeText;
	// private TextView mAstroText;
	private TextView mLoveText;
	// private ImageView mGenderImg;
	// private TextView mDescriptionText;
	private TextView mNameText;
	// private TextView mNameText1;
	private ImageView mAcqierementMoreImg;
	// private TextView mAgeText;
	private ZMGallery mAlbumGallery;
	private ImageView mAddPraiseImg;
	// private ImageView mArrowInfo;;

	private long remoteId;
	private int zmObjectType;
	private ZMCouplesObject mZMCouplesObject;

	private ZMCouplesService mZMCouplesService;
	private ScanningcodeService mScanningcodeService;
	private HttpImageLoader mHttpImageLoader;

	private RefreshListData<ZMObjectImage> mAlbumRefreshList;
	private RefreshListData<ZMObject> mOtherIdolRefreshList;
	private RefreshListData<IdolAcqierement> mMultimediaRefreshList;
	private RefreshListData<IdolAcqierement> mAcqierementRefreshList;
	private ArrayList<ZMObject> mCacheZMObjectList;

	/** 其他情侣知天使 */
	private DrawableCardAdatper otherIdolAdapter;
	/** 日志 */
	private AcqierementAdapter mAcqierementAdapter;
	/** 影像 */
	private MultimediaAdapter mMultimediaAdapter;
	/** 相册 */
	private IdolAlbumAdatper mImageAdapter;

	/** 其他推荐CardView */
	private CardView mOtherCardView;
	/** 影像CardView */
	private CardView mMultimediaCardView;
	/** 日志View */
	private ListView mAcqierementListView;

	// -------------------------------
	private Animation mArrowUpAnim;
	private Animation mArrowDownAnim;
	private Animation mPraiseAnim;
	private Animation mInfoInAnimation;
	private int mDefalutAlbumDrawable = R.drawable.couples_album_default;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChildrenView = View.inflate(this, R.layout.space_couples_activity, null);
		// setContentView(mChildrenView);
		setSidebar();
		setTopbar();
		findView();
		init();

		Intent intent = getIntent();
		remoteId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);
		mCacheZMObjectList = new ArrayList<ZMObject>();

		mZMCouplesObject = (ZMCouplesObject) mScanningcodeService.getCacheZMObject(remoteId, zmObjectType);

		// 请求服务器数据
		if (mZMCouplesObject != null) {
			startWaitingDialog("", R.string.loading);
			refreshView();
		} else {
			startWaitingDialog("", R.string.loading);
			ScanningcodeService.getInstance(this).getScanningInfo(remoteId, zmObjectType, this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mImageAdapter != null) {
			mImageAdapter.recycle();
			mImageAdapter = null;
		}
		mCacheZMObjectList.clear();
		mCacheZMObjectList = null;
	}

	private void refreshView() {
		// 请求服务器数据
		if (NetUtils.isNetworkAvailable(this)) {
			setDefalutAlbumView();
			getServiceData();
			setView();
		} else {
			HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
		}
	}

	private void setView() {
		mHttpImageLoader.loadImage(mZMCouplesObject.getImageUrl(), mPhotoImage, getActivityId(),
				R.drawable.default_image, ImageScaleType.SMALL);

		String name = mZMCouplesObject.getName();

		mNameText.setText(name);
		mLoveText.setText(mZMCouplesObject.getBlessingCount() + "人祝福");
		if (mZMCouplesObject.isPublic()) {
			getTopbar().findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
		} else {
			getTopbar().findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.GONE);
		}
	}

	private void getServiceData() {
		// ------------------------
		// 影像 --
		// 日志 --
		// 其他情侣知天使 --
		// 获取情侣相册

		mAlbumRefreshList = mScanningcodeService.getCacheZMObjectAlbumList(mZMCouplesObject.getId());
		if (mAlbumRefreshList.isEmpty()) {
			mScanningcodeService.getZMObjectAlbumList(mZMCouplesObject, false, this);
		} else {
			setAlbumView(mAlbumRefreshList.getDataList());
		}

		// 获取情侣知天使影像
		mMultimediaRefreshList = mZMCouplesService.getCacheMultimediaList(mZMCouplesObject);
		if (mMultimediaRefreshList.isEmpty()) {
			mZMCouplesService.getMultimediaList(mZMCouplesObject, false, this);
		} else {
			setMultimediaView(mMultimediaRefreshList.getDataList());
		}

		// 情侣知天使日志列表
		mAcqierementRefreshList = mZMCouplesService.getCacheCouplesJournalList(mZMCouplesObject);
		if (mAcqierementRefreshList.isEmpty()) {
			mZMCouplesService.getCouplesJournalList(mZMCouplesObject, false, this);
		} else {
			setAcqierementView(mAcqierementRefreshList.getDataList());
		}

		// 赞信息
		mScanningcodeService.getPraiseCount(mZMCouplesObject, this);

		// 获取其它情侣知天使
		mScanningcodeService.getRecommendedZMObjectList(mZMCouplesObject.getZMObjectType(), mZMCouplesObject,
				OTHER_IDOL_COUNT, true, mZMCouplesObject.getCityId(), this);

	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 设置情侣知天使相册
					GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
					mAlbumRefreshList = p.getDataList();
					setAlbumView(mAlbumRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_COUPLES_MULTIMEDIA_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 获取情侣知天使影音列表
					GetMultimediaListProtocol p = (GetMultimediaListProtocol) protocol;
					mMultimediaRefreshList = p.getDataList();
					setMultimediaView(mMultimediaRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_COUPLES_JOURNAL_CHANNEL_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到情侣知天使日志列表
					GetJournalListProtocol p = (GetJournalListProtocol) protocol;
					mAcqierementRefreshList = p.getDataList();
					setAcqierementView(mAcqierementRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 其它情侣知天使周边推荐列表
					GetRecommendedZMObjectListProtocol p = (GetRecommendedZMObjectListProtocol) protocol;
					mOtherIdolRefreshList = p.getDataList();
					addCacheZMObjectList(mOtherIdolRefreshList.getDataList());
					setOtherIdolView(mCacheZMObjectList);
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PRAISE_PROTOCOL) {
				// 获取赞信息
				if (protocol.isHandleSuccess()) {
					GetPraiseCountProtocol p = (GetPraiseCountProtocol) protocol;
					PraiseInfo mPraiseInfo = p.getPraiseInfo();
					setPraiseView(mPraiseInfo);
				} else {
					HaloToast.show(CouplesActivity.this, protocol.getProtocolErrorMessage());
				}
			} else if (protocol.getProtocolType() == ProtocolType.ADD_FAVORITE_PROTOCOL) {
				// 收藏
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.favorite_success);
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else if (protocol.getProtocolType() == ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL) {
				// 加赞view
				if (protocol.isHandleSuccess()) {
					DoPraiseProtocol p = (DoPraiseProtocol) protocol;
					PraiseInfo mPraiseInfo = p.getPraiseInfo();
					if (mPraiseInfo != null) {
						mAddPraiseImg.startAnimation(mPraiseAnim);
						setPraiseView(mPraiseInfo);
						HaloToast.show(CouplesActivity.this, R.string.praise_success);
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				} else if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_CODE_ERROR_WITH_EXCEPTION) {
					HaloToast.show(getApplicationContext(), getString(R.string.like_resubmit), 0);
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取zmobject
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol p = (GetZMObjectProtocol) protocol;
					mZMCouplesObject = (ZMCouplesObject) p.getZMObject();
					refreshView();
				}
			}
		} else {
			// 网络访问错误
			if (protocol.getProtocolType() == ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL) {
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	private void setPraiseView(PraiseInfo praiseInfo) {
		mLoveText.setText(praiseInfo.getPraiseCount(PraiseKind.LIKE) + "人喜欢");

	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// 请求服务器前
	}

	private void addCacheZMObjectList(ArrayList<ZMObject> dataList2) {
		for (ZMObject obj : dataList2) {
			if (mCacheZMObjectList.size() >= ACQIEREMENT_COUNT) {
				break;
			}
			mCacheZMObjectList.add(obj);
		}
	}

	/** 其他情侣知天使 */
	private void setOtherIdolView(final ArrayList<ZMObject> arrayList) {
		if (otherIdolAdapter == null) {
			otherIdolAdapter = new DrawableCardAdatper(this, R.layout.space_idol_recommend_item, arrayList);
			//			otherIdolAdapter.setImageType(ImageScaleType.SM);
			mOtherCardView.setAdapter(otherIdolAdapter);
			mOtherCardView.setOnItemClickListener(new OnCardItemClickListener() {
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
						ActivitySwitcher.openSpaceActivity(CouplesActivity.this, arrayList.get(position), flagBundle,
								isFinish);
					}
				}
			});
		} else {
			otherIdolAdapter.setData(arrayList);
			otherIdolAdapter.notifyDataSetChanged();
		}
		mOtherCardView.setArrowVisibility(View.INVISIBLE);
	}

	/** 设置相册 */
	private void setAlbumView(ArrayList<ZMObjectImage> arrayList) {
		if (mImageAdapter == null) {
			mImageAdapter = new IdolAlbumAdatper(this, arrayList);
			mAlbumGallery.setAdapter(mImageAdapter);
			mImageAdapter.setDefaultResource(mDefalutAlbumDrawable);
			mAlbumGallery.setSelection(Integer.MAX_VALUE / 2);
		} else {
			mImageAdapter.setData(arrayList);
			mImageAdapter.notifyDataSetChanged();
		}

	}

	private void setDefalutAlbumView() {
		DefaulAlbumBaseAdatper adapter = new DefaulAlbumBaseAdatper(this, mDefalutAlbumDrawable);
		mAlbumGallery.setAdapter(adapter);
		mAlbumGallery.setSelection(Integer.MAX_VALUE / 2);
	}

	/** 设置日志View */
	private void setAcqierementView(final ArrayList<IdolAcqierement> arrayList) {
		if (arrayList.size() > ACQIEREMENT_COUNT) {
			mAcqierementMoreImg.setVisibility(View.VISIBLE);
		} else {
			mAcqierementMoreImg.setVisibility(View.GONE);
		}
		if (mAcqierementAdapter == null) {
			mAcqierementAdapter = new AcqierementAdapter(this, R.layout.space_acqierement_item, arrayList);
			// 设置显示个数
			mAcqierementAdapter.setItemtCount(ACQIEREMENT_COUNT);
			mAcqierementListView.setAdapter(mAcqierementAdapter);
			mAcqierementListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// 情侣知天使日志详情
					ArrayList<String> urlArray = new ArrayList<String>();
					for (int i = 0; i < arrayList.size(); i++) {
						urlArray.add(arrayList.get(i).getContentUrl());
					}
					Intent acqierementIntent = new Intent(CouplesActivity.this, IdoPhotoActivity.class);
					acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
					acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
					acqierementIntent.putExtra(IdoPhotoActivity.TOPBAR_BACKGROUND, R.color.red_couples_topbar);
					acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE, getText(R.string.diary_info));
					startActivity(acqierementIntent);
				}
			});
			setListViewHeightBasedOnChildren(mAcqierementListView);
		} else {
			mAcqierementAdapter.setData(arrayList);
			mAcqierementAdapter.notifyDataSetChanged();
		}
	}

	/** 设置影像View */
	private void setMultimediaView(final ArrayList<IdolAcqierement> arrayList) {
		if (mMultimediaAdapter == null) {
			mMultimediaAdapter = new MultimediaAdapter(this, R.layout.space_idol_img_item, arrayList);
			mMultimediaCardView.setAdapter(mMultimediaAdapter);
			mMultimediaCardView.setOnItemClickListener(new OnCardItemClickListener() {
				@Override
				public void onItemClickListener(View view, int position, boolean isArrow) {
					if (isArrow) {
						Intent multimediaListIntent = new Intent(CouplesActivity.this, CouplesMediaListActivity.class);
						multimediaListIntent.putExtra(ACTIVITY_EXTRA, mZMCouplesObject.getId());
						startActivity(multimediaListIntent);
					} else {
						ArrayList<String> urlArray = new ArrayList<String>();
						for (int i = 0; i < arrayList.size(); i++) {
							urlArray.add(arrayList.get(i).getContentUrl());
						}
						Intent acqierementIntent = new Intent(CouplesActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
						acqierementIntent.putExtra(IdoPhotoActivity.TOPBAR_BACKGROUND, R.color.red_couples_topbar);
						acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE, getText(R.string.multimedia_info));
						startActivity(acqierementIntent);
					}
				}
			});
		} else {
			mMultimediaAdapter.setData(arrayList);
			mMultimediaAdapter.notifyDataSetChanged();
		}
	}

	/** 赞单击事件 */
	private View.OnClickListener addPraiseClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMCouplesObject != null) {
				mScanningcodeService.doPraise(mZMCouplesObject, PraiseKind.LIKE, CouplesActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};
	/** 进入日志列表 */
	private View.OnClickListener acqierementMoreClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMCouplesObject != null) {
				Intent acqierementListIntent = new Intent(CouplesActivity.this, CouplesDiaryListActivity.class);
				acqierementListIntent.putExtra(ACTIVITY_EXTRA, mZMCouplesObject.getId());
				acqierementListIntent.putExtra(CouplesDiaryListActivity.TOPBAR_TITLE, getText(R.string.diary_list));
				startActivity(acqierementListIntent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/** 标题栏:添加到藏夹 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMCouplesObject != null) {
				FavoriteEntry entry = new FavoriteEntry();
				entry.setImageUrl(mZMCouplesObject.getImageUrl());
				entry.setObjectType(mZMCouplesObject.getZMObjectType());
				entry.setObjectId(mZMCouplesObject.getRemoteId());
				entry.setTitle(mZMCouplesObject.getName());
				entry.setContent(mZMCouplesObject.getSpaceHomepage());
				mScanningcodeService.addFavorite(entry, CouplesActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}

		}
	};

	private void init() {
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mHttpImageLoader = HttpImageLoader.getInstance(this);
		mZMCouplesService = ZMCouplesService.getInstance(this);

		// 初始化动画
		mArrowUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_up);
		mArrowDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_down);
		mPraiseAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.idol_praise_scale);
		mInfoInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.idol_in);

	}

	/**
	 * 处理scollview中 嵌套listview的问题
	 */
	private static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	private void setTopbar() {
		ZhimaTopbar topBar = getTopbar();
		topBar.setBackgroundResource(R.color.red_couples_topbar);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		topBar.addRightLayoutView(ll_right);
		ImageView image1 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.couples_topbar_bless);
		topBar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		topBar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mZMCouplesObject != null) {
					Intent intent = new Intent(CouplesActivity.this, CouplesBlessActivity.class);
					intent.putExtra(ACTIVITY_EXTRA, mZMCouplesObject.getId());
					intent.putExtra(ACTIVITY_EXTRA2, mZMCouplesObject.isAllowComment());
					startActivity(intent);
				}
			}
		});

		ImageView image2 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton2);
		image2.setImageResource(R.drawable.topbar_share);
		topBar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(shareTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);

		ImageView image3 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton3);
		image3.setImageResource(R.drawable.couples_topbar_favorite);
		topBar.findViewById(R.id.layout_topbar_rightButton3).setOnClickListener(saveTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton3).setVisibility(View.VISIBLE);
	}

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMCouplesObject != null) {
				String shareContent = String.format(getString(R.string.share_content), mZMCouplesObject.getName(),
						mZMCouplesObject.getZMID(), mZMCouplesObject.getSpaceHomepage());
				String sms_message = String.format(getString(R.string.couple_sms_message), mZMCouplesObject.getName(),
						mZMCouplesObject.getZMID(), mZMCouplesObject.getSpaceHomepage());
//				SharePopupMenu sharePopupMenu = new SharePopupMenu(CouplesActivity.this, CouplesActivity.this, v,
//						sms_message, shareContent);
				SharePopupMenu.show(CouplesActivity.this, CouplesActivity.this, v, sms_message, shareContent);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};

	/**
	 * @Title: setSidebar
	 * @Description:设置侧边栏
	 * @param
	 * @return void
	 */
	private void setSidebar() {
		mChildrenView = View.inflate(this, R.layout.space_zmproduct_activity, null);
		setContentView(mChildrenView);
		ViewInitTools.setTopBar(this, "知相印—喜印", View.GONE, null);
	}

	private void findView() {
		mNameText = (TextView) mChildrenView.findViewById(R.id.txt_name);
		mPhotoImage = (ImageView) mChildrenView.findViewById(R.id.img_photo);
		mLoveText = (TextView) mChildrenView.findViewById(R.id.txt_loveCount);
		mAlbumGallery = (ZMGallery) mChildrenView.findViewById(R.id.gal_album);
		mAcqierementMoreImg = (ImageView) mChildrenView.findViewById(R.id.img_arrow);

		mOtherCardView = (CardView) mChildrenView.findViewById(R.id.otherCard);
		mOtherCardView.getTitleView().setBackgroundResource(R.color.couples_cardview_bg_color);
		mOtherCardView.getTitleView().setTextColor(getResources().getColor(R.color.couples_cardview_text_color));
		mOtherCardView.getDivideLineView().setBackgroundResource(R.color.couples_card_top_line);
		mOtherCardView.getArrowView().setImageResource(R.drawable.couples_more_arrow);
		mAcqierementListView = (ListView) mChildrenView.findViewById(R.id.lstv_acqierement);
		mMultimediaCardView = (CardView) mChildrenView.findViewById(R.id.multimediaCard);
		mMultimediaCardView.getTitleView().setBackgroundResource(R.color.couples_cardview_bg_color);
		mMultimediaCardView.getTitleView().setTextColor(getResources().getColor(R.color.couples_cardview_text_color));
		mMultimediaCardView.getDivideLineView().setBackgroundResource(R.color.couples_card_top_line);
		mMultimediaCardView.getArrowView().setImageResource(R.drawable.couples_more_arrow);

		mAddPraiseImg = (ImageView) mChildrenView.findViewById(R.id.img_add);
		mAddPraiseImg.setOnClickListener(addPraiseClick);
		mAcqierementMoreImg.setOnClickListener(acqierementMoreClick);
	}

	private class AlbumBaseAdatper extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class DefaulAlbumBaseAdatper extends BaseAdapter {

		private Drawable mDefultDrawable;

		public DefaulAlbumBaseAdatper(Context context, int def) {
			mDefultDrawable = context.getResources().getDrawable(def);
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv;
			if (convertView != null) {
				iv = (ImageView) convertView;
			} else {
				iv = new ImageView(CouplesActivity.this);
				iv.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
						Gallery.LayoutParams.FILL_PARENT));
				iv.setScaleType(ScaleType.FIT_XY);
			}
			iv.setImageDrawable(mDefultDrawable);
			return iv;
		}
	}
}
