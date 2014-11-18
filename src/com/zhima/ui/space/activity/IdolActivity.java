package com.zhima.ui.space.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.PraiseKind;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetRecommendedZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetChannelListByKindProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetMultimediaListProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSpaceFocusImagesProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMIdolService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.DrawableCardAdatper;
import com.zhima.ui.common.view.CardView;
import com.zhima.ui.common.view.CardView.OnCardItemClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZMGallery;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.space.adapter.AcqierementAdapter;
import com.zhima.ui.space.adapter.IdolAlbumAdatper;
import com.zhima.ui.space.adapter.MultimediaAdapter;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * @ClassName:AngelActivity
 * @Description:知天使空间
 * @author liqilong
 * @date 2012-7-9 下午4:42:18
 * 
 */
public class IdolActivity extends BaseActivity {
	protected static final String TAG = "IdolActivity";

	private View mChildrenView;

	/** 其他知天使个数 */
	private static final int OTHER_IDOL_COUNT = 3;
	/** 控制显示频道个数 */
	private static final int ACQIEREMENT_COUNT = 3;

	private ImageView mPhotoImage;
	private TextView mBirthdayText;
	private TextView mBloodTypeText;
	private TextView mAstroText;
	private TextView mLoveText;
	private ImageView mGenderImg;
	private TextView mDescriptionText;
	private TextView mNameText;
	private TextView mNameText1;
	private ImageView mAcqierementMoreImg;
	private TextView mAgeText;
	private ZMGallery mAlbumGallery;
	private ImageView mAddPraiseImg;
	private ImageView mArrowInfo;;

	private long remoteId;
	private int zmObjectType;
	private ZMIdolObject mZMIdolObject;

	private ZMIdolService mZMIdolService;
	private ScanningcodeService mScanningcodeService;
	private ZMSpaceService mZMSpaceService;
	private HttpImageLoader mHttpImageLoader;
	private boolean mIsFirstFlag = true;

	private RefreshListData<ZMObjectImage> mAlbumRefreshList;
	private RefreshListData<ZMObject> mOtherIdolRefreshList;
	private RefreshListData<IdolAcqierement> mMultimediaRefreshList;
	private RefreshListData<IdolAcqierement> mAcqierementRefreshList;

	private ZMLocationManager mLocationManager;

	/** 其他知天使 */
	private DrawableCardAdatper otherIdolAdapter;
	/** 频道 */
	private AcqierementAdapter mAcqierementAdapter;
	/** 影像 */
	private MultimediaAdapter mMultimediaAdapter;
	/** 相册 */
	private IdolAlbumAdatper mImageAdapter;

	/** 其他推荐CardView */
	private CardView mOtherCardView;
	/** 影像CardView */
	private CardView mMultimediaCardView;
	/** 频道View */
	private ListView mAcqierementListView;

	// -------------------------------
	private Animation mArrowUpAnim;
	private Animation mArrowDownAnim;
	private Animation mPraiseAnim;
	private Animation mInfoInAnimation;

	private ArrayList<ZMObject> mCacheZMObjectList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChildrenView = View.inflate(this, R.layout.space_idol_activity, null);
		// setContentView(mChildrenView);
		setSidebar();
		setTopbar();
		findView();
		init();

		Intent intent = getIntent();
		remoteId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		zmObjectType = intent.getIntExtra(ACTIVITY_EXTRA2, -1);

		mCacheZMObjectList = new ArrayList<ZMObject>();

		mZMIdolObject = (ZMIdolObject) mScanningcodeService.getCacheZMObject(remoteId, zmObjectType);

		// 请求服务器数据
		if (mZMIdolObject != null) {
			startWaitingDialog("", R.string.loading);
			refreshView();
		} else {
			startWaitingDialog("", R.string.loading);
			ScanningcodeService.getInstance(this).getScanningInfo(remoteId, zmObjectType, this);
		}
	}

	// @Override
	// protected void onRestart() {
	// super.onRestart();
	// if (mImageAdapter != null) {
	// mAlbumGallery.setAdapter(mImageAdapter);
	// mAlbumGallery.setSelection(Integer.MAX_VALUE / 2);
	// }
	// }
	//
	// @Override
	// public void onStop() {
	// super.onStop();
	// if (mImageAdapter != null) {
	// mAlbumGallery.setAdapter(new AlbumBaseAdatper());
	// mImageAdapter.recycle();
	// }
	// }

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
		mHttpImageLoader.loadImage(mZMIdolObject.getImageUrl(), mPhotoImage, getActivityId(), R.drawable.default_image,
				ImageScaleType.SMALL);

		long birthday = mZMIdolObject.getBirthday();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy'年'MM'月'dd" + "日");
		String birthdayStr = dateFormat.format(new Date(birthday));

		String name = mZMIdolObject.getName();

		mNameText.setText(name);
		if (GenderType.MALE.equals(mZMIdolObject.getGender())) {
			mGenderImg.setImageResource(R.drawable.male);
		} else {
			mGenderImg.setImageResource(R.drawable.female);
		}
		mNameText1.setText(getString(R.string.name) + ":  " + name);
		mAgeText.setText(mZMIdolObject.getAge() + "岁");
		mBirthdayText.setText(getString(R.string.birthday) + "  " + birthdayStr);
		mBloodTypeText.setText(getString(R.string.blood_type) + ":  " + mZMIdolObject.getBloodType() + "型");
		mAstroText.setText(getString(R.string.astro) + ":  " + mZMIdolObject.getAstro().toString());
		mLoveText.setText(mZMIdolObject.getLoveCount() + "人喜欢");
		mDescriptionText.setText(mZMIdolObject.getSignature());

	}

	private void getServiceData() {
		// ------------------------
		// 影像 --
		// 频道 --
		// 其他知天使 --
		// 获取知天使相册

//		mAlbumRefreshList = mScanningcodeService.getCacheZMObjectAlbumList(mZMIdolObject.getId());
//		if (mAlbumRefreshList.isEmpty()) {
//			mScanningcodeService.getZMObjectAlbumList(mZMIdolObject, false, this);
//		} else {
//			setAlbumView(mAlbumRefreshList.getDataList());
//		}

		mAlbumRefreshList = mZMSpaceService.getCacheFocusImages(mZMIdolObject);
		if (mAlbumRefreshList.isEmpty()) {
			mZMSpaceService.getFocusImages(mZMIdolObject, false, this);
		} else {
			setAlbumView(mAlbumRefreshList.getDataList());
		}

		// 获取知天使影像
		mMultimediaRefreshList = mZMIdolService.getCacheMultimediaList(mZMIdolObject);
		if (mMultimediaRefreshList.isEmpty()) {
			mZMIdolService.getMultimediaList(mZMIdolObject, false, this);
		} else {
			setMultimediaView(mMultimediaRefreshList.getDataList());
		}

		// 知天使频道列表
		mAcqierementRefreshList = mZMIdolService.getCacheAcqierementList(mZMIdolObject);
		if (mAcqierementRefreshList.isEmpty()) {
			mZMIdolService.getChannelListByKind(mZMIdolObject, false, this);
		} else {
			setAcqierementView(mAcqierementRefreshList.getDataList());
		}

		// 赞信息
		mScanningcodeService.getPraiseCount(mZMIdolObject, this);

		// 获取其它知天使
		GeoCoordinate geo = mLocationManager.getLastKnownGeoCoordinate();
		mScanningcodeService.getRecommendedZMIdolObjectList(OTHER_IDOL_COUNT, mZMIdolObject.getZMObjectType(),
				mZMIdolObject.getRemoteId(), geo, true, this);

	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_SPACE_FOCUS_IMAGES_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 设置知天使相册
					GetSpaceFocusImagesProtocol p = (GetSpaceFocusImagesProtocol) protocol;
					mAlbumRefreshList = p.getDataList();
					setAlbumView(mAlbumRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_IDOL_MULTIMEDIA_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 获取知天使影音列表
					GetMultimediaListProtocol p = (GetMultimediaListProtocol) protocol;
					mMultimediaRefreshList = p.getDataList();
					setMultimediaView(mMultimediaRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_IDOL_CHANNEL_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到知天使频道列表
					GetChannelListByKindProtocol p = (GetChannelListByKindProtocol) protocol;
					mAcqierementRefreshList = p.getDataList();
					setAcqierementView(mAcqierementRefreshList.getDataList());
				} else {

				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 其它知天使周边推荐列表
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
					HaloToast.show(IdolActivity.this, protocol.getProtocolErrorMessage());
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
						HaloToast.show(IdolActivity.this, R.string.praise_success);
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取zmobject
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol p = (GetZMObjectProtocol) protocol;
					mZMIdolObject = (ZMIdolObject) p.getZMObject();
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
			if (mCacheZMObjectList.size() >= OTHER_IDOL_COUNT) {
				break;
			}
			mCacheZMObjectList.add(obj);
		}
	}

	/** 其他知天使 */
	private void setOtherIdolView(final ArrayList<ZMObject> arrayList) {
		if (otherIdolAdapter == null) {
			otherIdolAdapter = new DrawableCardAdatper(this, R.layout.space_idol_recommend_item, arrayList);
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
						ActivitySwitcher.openSpaceActivity(IdolActivity.this, otherIdolAdapter.getItem(position),
								flagBundle, isFinish);
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
			if (GenderType.MALE.equals(mZMIdolObject.getGender())) {
				mImageAdapter.setDefaultResource(R.drawable.male_default);
			} else {
				mImageAdapter.setDefaultResource(R.drawable.female_default);
			}
			mAlbumGallery.setSelection(Integer.MAX_VALUE / 2);
		} else {
			mImageAdapter.setData(arrayList);
			mImageAdapter.notifyDataSetChanged();
		}

	}

	private void setDefalutAlbumView() {
		int defalutDrawable = R.drawable.male_default;
		if (GenderType.MALE.equals(mZMIdolObject.getGender())) {
			defalutDrawable = R.drawable.male_default;
		} else {
			defalutDrawable = R.drawable.female_default;
		}
		DefaulAlbumBaseAdatper adapter = new DefaulAlbumBaseAdatper(this, defalutDrawable);
		mAlbumGallery.setAdapter(adapter);
		mAlbumGallery.setSelection(Integer.MAX_VALUE / 2);
	}

	/** 设置频道View */
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
					// 知天使频道详情
					ArrayList<String> urlArray = new ArrayList<String>();
					for (int i = 0; i < arrayList.size(); i++) {
						urlArray.add(arrayList.get(i).getContentUrl());
					}
					Intent acqierementIntent = new Intent(IdolActivity.this, IdoPhotoActivity.class);
					acqierementIntent.putExtra(ACTIVITY_EXTRA, urlArray);
					acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
					acqierementIntent.putExtra(IdoPhotoActivity.ACTIVITY_TITLE, getText(R.string.acqierement_info));
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
						Intent multimediaListIntent = new Intent(IdolActivity.this, MultimediaListActivity.class);
						multimediaListIntent.putExtra(ACTIVITY_EXTRA, mZMIdolObject.getId());
						startActivity(multimediaListIntent);
					} else {
						ArrayList<String> urlArray = new ArrayList<String>();
						for (int i = 0; i < arrayList.size(); i++) {
							urlArray.add(arrayList.get(i).getContentUrl());
						}
						Intent acqierementIntent = new Intent(IdolActivity.this, IdoPhotoActivity.class);
						acqierementIntent.putStringArrayListExtra(ACTIVITY_EXTRA, urlArray);
						acqierementIntent.putExtra(IdoPhotoActivity.PHOTO_POSITION, position);
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

	/** 显示知天使详细信息 */
	private View.OnClickListener showIdolInfoClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				ViewGroup mIdolInfoLayout = (ViewGroup) mChildrenView.findViewById(R.id.layout_info);
				if (mIdolInfoLayout.getVisibility() == View.GONE) {
					mArrowInfo.setImageResource(R.drawable.group1_sele);
					mIdolInfoLayout.setVisibility(View.VISIBLE);
					mIdolInfoLayout.startAnimation(mInfoInAnimation);
					mArrowInfo.startAnimation(mArrowDownAnim);
				} else {
					mArrowInfo.setImageResource(R.drawable.group2_sele);
					mArrowInfo.startAnimation(mArrowUpAnim);
					mIdolInfoLayout.setVisibility(View.GONE);
				}
			}
		}
	};

	/** 赞单击事件 */
	private View.OnClickListener addPraiseClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				mScanningcodeService.doPraise(mZMIdolObject, PraiseKind.LIKE, IdolActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}
		}
	};
	/** 进入频道列表 */
	private View.OnClickListener acqierementMoreClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				Intent acqierementListIntent = new Intent(IdolActivity.this, AcqierementListActivity.class);
				acqierementListIntent.putExtra(ACTIVITY_EXTRA, mZMIdolObject.getId());
				acqierementListIntent
						.putExtra(AcqierementListActivity.TOPBAR_TITLE, getText(R.string.acqierement_info));
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
			if (mZMIdolObject != null) {
				FavoriteEntry entry = new FavoriteEntry();
				entry.setImageUrl(mZMIdolObject.getImageUrl());
				entry.setObjectType(mZMIdolObject.getZMObjectType());
				entry.setObjectId(mZMIdolObject.getRemoteId());
				entry.setTitle(mZMIdolObject.getName());
				entry.setContent(mZMIdolObject.getSpaceHomepage());
				mScanningcodeService.addFavorite(entry, IdolActivity.this);
			} else {
				ErrorManager.showErrorMessage(getApplicationContext());
			}

		}
	};

	private void init() {
		mLocationManager = ZMLocationManager.getInstance();
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mHttpImageLoader = HttpImageLoader.getInstance(this);
		mZMIdolService = ZMIdolService.getInstance(this);
		mZMSpaceService = ZMSpaceService.getInstance(this);

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
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		topBar.addRightLayoutView(ll_right);
		ImageView image1 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_favorite);
		topBar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		ImageView image2 = (ImageView) topBar.findViewById(R.id.img_topbar_rightButton2);
		image2.setImageResource(R.drawable.topbar_share);
		topBar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(shareTopbarClick);
		topBar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
	}

	/** 标题栏:分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mZMIdolObject != null) {
				String shareContent = String.format(getString(R.string.share_content_no_id), mZMIdolObject.getName(),
						mZMIdolObject.getSpaceHomepage());
				String sms_message = String.format(getString(R.string.idol_sms_message), mZMIdolObject.getName(),
						mZMIdolObject.getSpaceHomepage());
//				SharePopupMenu sharePopupMenu = new SharePopupMenu(IdolActivity.this, IdolActivity.this, v,
//						sms_message, shareContent);
				SharePopupMenu.show(IdolActivity.this, IdolActivity.this, v, sms_message, shareContent);
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

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "知天使", View.GONE, null);

//		mSideBarView = new SideBarView(this, this, getText(R.string.idol).toString());
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

	private void findView() {
		mNameText = (TextView) mChildrenView.findViewById(R.id.txt_name);
		mGenderImg = (ImageView) mChildrenView.findViewById(R.id.img_gender);
		mNameText1 = (TextView) mChildrenView.findViewById(R.id.txt_name1);
		mPhotoImage = (ImageView) mChildrenView.findViewById(R.id.img_photo);
		mBirthdayText = (TextView) mChildrenView.findViewById(R.id.txt_birthday);
		mBloodTypeText = (TextView) mChildrenView.findViewById(R.id.txt_bloodtype);
		mAstroText = (TextView) mChildrenView.findViewById(R.id.txt_astro);
		mLoveText = (TextView) mChildrenView.findViewById(R.id.txt_loveCount);
		mAgeText = (TextView) mChildrenView.findViewById(R.id.txt_age);
		mDescriptionText = (TextView) mChildrenView.findViewById(R.id.txt_description);
		mAlbumGallery = (ZMGallery) mChildrenView.findViewById(R.id.gal_album);
		mAcqierementMoreImg = (ImageView) mChildrenView.findViewById(R.id.img_arrow);

		mOtherCardView = (CardView) mChildrenView.findViewById(R.id.otherCard);
		mAcqierementListView = (ListView) mChildrenView.findViewById(R.id.lstv_acqierement);
		mMultimediaCardView = (CardView) mChildrenView.findViewById(R.id.multimediaCard);

		mArrowInfo = (ImageView) mChildrenView.findViewById(R.id.img_showinfo);
		mArrowInfo.setImageResource(R.drawable.group1_sele);
		mArrowInfo.setOnClickListener(showIdolInfoClick);

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
				iv = new ImageView(IdolActivity.this);
				iv.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
						Gallery.LayoutParams.FILL_PARENT));
				iv.setScaleType(ScaleType.FIT_XY);
			}
			iv.setImageDrawable(mDefultDrawable);
			return iv;
		}
	}
}
