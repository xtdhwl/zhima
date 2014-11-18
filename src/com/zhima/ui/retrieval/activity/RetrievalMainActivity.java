package com.zhima.ui.retrieval.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.lbs.ZMLocationManager.AddressLocationListener;
import com.zhima.base.logger.Logger;
import com.zhima.base.utils.NetUtils;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Region;
import com.zhima.data.service.RegionService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.CustomDialog.OnOnlyBtClickListener;
import com.zhima.ui.common.view.MsgDialog;
import com.zhima.ui.common.view.MsgDialog.OnBtClickListener;
import com.zhima.ui.common.view.SearchBoxView;
import com.zhima.ui.common.view.SearchBoxView.OnKeyClickListener;
import com.zhima.ui.common.view.ZhimaRegionDialog;
import com.zhima.ui.sidebar.SideBarView;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 检索
 * 
 * @ClassName: RetrievalMainActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-22 上午11:20:13
 */
public class RetrievalMainActivity extends BaseActivity implements
		OnClickListener {

	protected static final String TAG = "RetrievalMainActivity";

	/** 此activity是否正在焦点状态 */
	private boolean isActivityFocusble;

	/** 选择区域 */
	private LinearLayout mSelectAreaLayout;
	private TextView mAreaText;

	/** 商业空间 */
	private LinearLayout mMerchantSpaceLayout;
	private ImageView mMerchantSpaceImage;
	private TextView mMerchantSpaceText;

	/** 公共空间 */
	private LinearLayout mCommonSpaceLayout;
	private ImageView mCommonSpaceImage;
	private TextView mCommonSpaceText;

	/** 交通 */
	private LinearLayout mVehicleLayout;
	private ImageView mVehicleImage;
	private TextView mVehicleText;

	/** 优惠券 */
	private LinearLayout mCouponLayout;
	private ImageView mCouponImage;
	private TextView mCouponText;

	/** 知天使 */
	private LinearLayout mIdoLayout;
	private ImageView mIdoImage;
	private TextView mIdoText;

	/** 知相印-喜印 */
	private LinearLayout mXiyinLayout;
	private ImageView mXiyinImage;
	private TextView mXiyinText;

	/** 知相印-名玺 */
	private LinearLayout mFameLayout;
	private ImageView mFameImage;
	private TextView mFameText;

	/** 知相印-玉玺 */
	private LinearLayout mJadeLayout;
	private ImageView mJadeImage;
	private TextView mJadeText;

	/** 酒吧 */
	private TextView mBarText;
	/** 火锅 */
	private TextView mChafingDishText;
	/** 鲜花 */
	private TextView mFlowerText;
	/** 户外 */
	private TextView mOutdoorsText;
	/** 博物馆 */
	private TextView mMuseumText;
	/** 健身中心 */
	private TextView mFitnessText;

	private View mChildrenView;
	protected Region mRegion;
	private ZhimaRegionDialog mAreaDialog;

	/** 地区对话框 当前选择条目 */
	private int mCurrentItem;
	private long mProvinceId = -1;
	private ZMLocationManager mZMLocationManager;

	/** 选择对话框是否正在显示状态 */
	private boolean isAreaShow = false;

	/** 存储省份 */
	private String mSaveProvince;
	/** 存储城市 */
	private String mSaveCity;
	/** 存储区域 */
	private String mSaveArea;

	// private SideBarView mSideBarView;

	private static final int mRetriCount = 7;
	private ListView mRetrievalList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mZMLocationManager = ZMLocationManager.getInstance();
		getSaveData();
		createAreaDialog();
		setSidebar();
		findView();
		setUpView();

		// test();
		// testCharIndex();

		// checkGps();
	}

	@Override
	protected void onStart() {
		super.onStart();
		getLbs();
	}

	@Override
	public void onStop() {
		super.onStop();
		mZMLocationManager.stopGeoListening();
		mZMLocationManager.stopZhimaListening();
	}

	/**
	 * 测试字母索引导航
	 */
	// private void testCharIndex() {
	// String[] nicks =
	// {"ertret","阿雅","北风","dshfgt","张山","ueruit","李四","*^$*!","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
	// ListView lvContact =
	// (ListView)mChildrenView.findViewById(R.id.lvContact);
	// lvContact.setAdapter(new ZMCharIndexAdapter(this,nicks));
	//
	// CharSideBar indexBar = (CharSideBar)
	// mChildrenView.findViewById(R.id.sideBar);
	// indexBar.setListView(lvContact);
	// }

	private void test() {
		LinearLayout layout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrieval_main_searchBox);
		SearchBoxView view = new SearchBoxView(this, "测试", getTopbar());
		layout.addView(view);
		view.setOnKeyClickListener(new OnKeyClickListener() {

			@Override
			public void onClick() {
				Toast.makeText(getApplicationContext(), "点击了", 0).show();
			}
		});
	}

	private void getSaveData() {
		mSaveProvince = SettingHelper.getString(this, Field.CURRENT_PROVINCE,
				"");
		if (TextUtils.isEmpty(mSaveProvince)) {
			mSaveProvince = "北京市";
			SettingHelper.setString(RetrievalMainActivity.this,
					Field.CURRENT_PROVINCE, mSaveProvince);
		}
		mSaveCity = SettingHelper.getString(this, Field.CURRENT_CITY, "");
		mSaveArea = SettingHelper.getString(this, Field.CURRENT_AREA, "");
	}

	private void createAreaDialog() {
		mAreaDialog = new ZhimaRegionDialog(this, mSelectAreaLayout, 1, 1, true);
		mAreaDialog.setTitle(R.string.select_area);
		mAreaDialog.setDataList(RegionService.getInstance(this)
				.getProvinceList());
		mAreaDialog.setFirstCurrentItem(mSaveProvince);
		mAreaDialog.setOnOnlyBtClickListener("完成", new OnOnlyBtClickListener() {

			@Override
			public void onOnlyBtClick() {
				Region mRegion = mAreaDialog.getFirstObject();
				if (mRegion != null) {
					mProvinceId = mRegion.getId();
					mAreaText.setText(mRegion.getName());
					mSaveProvince = mRegion.getName();
					mCurrentItem = mAreaDialog.getFirstCurrentItem();
					SettingHelper.setString(RetrievalMainActivity.this,
							Field.CURRENT_PROVINCE, mSaveProvince);
				}
			}
		});
		mAreaDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				isAreaShow = false;
			}
		});
	}

	/**
	 * 检查GPS
	 */
	private void checkGps() {
		if (!NetUtils.isGpsEnabled(RetrievalMainActivity.this)) {
			MsgDialog dialog = new MsgDialog(RetrievalMainActivity.this);
			dialog.setTitle(R.string.dialog_title);
			dialog.setLeftBtText(R.string.cancel);
			dialog.setRightBtText(R.string.ok);
			dialog.setMessage(R.string.msg_gps_prompt);
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					try {
						Intent intent = new Intent(
								Settings.ACTION_SECURITY_SETTINGS);
						startActivityForResult(intent, 0);
					} catch (Exception e) {
						Logger.getInstance(TAG).debug(e.toString());
					}
				}

				@Override
				public void onLeftBtClick() {
				}
			});
			dialog.show();
		}
	}

	/**
	 * 设置侧边栏
	 */
	public void setSidebar() {
		mChildrenView = View.inflate(this, R.layout.retrieval_main_activity,
				null);

		setContentView(mChildrenView);

		ViewInitTools.setTopBar(this, "检索", View.GONE, null);

		// mSideBarView = new SideBarView(this, this, "检索");
		// ViewInitTools.setSidebar(this, mSideBarView, mChildrenView);
		// mSideBarView.setChildView(mChildrenView);
		// setContentView(mSideBarView.getContentView());
		//
		// final View tranView = (View)
		// mSideBarView.getContentView().findViewById(R.id.view_transparent);
		// tranView.setVisibility(View.GONE);
		// tranView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mSideBarView.scrollView();
		// }
		// });
		// tranView.setClickable(false);
		// mSideBarView.setOnStateChangeListener(new OnStateChangeListener() {
		//
		// @Override
		// public void onStateChange(boolean isMenuOut) {
		// if (isMenuOut) {
		// tranView.setVisibility(View.VISIBLE);
		// tranView.setClickable(true);
		// } else {
		// tranView.setVisibility(View.GONE);
		// tranView.setClickable(false);
		// }
		// }
		// });
	}

	@Override
	public void onResume() {
		super.onResume();
		isActivityFocusble = true;
	}

	private void getLbs() {
		mZMLocationManager.startZhimaListening(null);
//		mZMLocationManager.startGeoListening(null);
//
		if (((ZhimaApplication) getApplication()).isLbsSucess) {
		
//				mZMLocationManager.startGeoListening(null);
			
		} else {
			Address geoLastKnownAddress = mZMLocationManager.getGeoLastKnownAddress();
			if(geoLastKnownAddress == null){
				mZMLocationManager.getGeoAddress(new AddressLocationListener() {
					@Override
					public void onAddressLocationListener(Address geoAddress) {
						if (geoAddress != null) {
							chack(geoAddress);
						}
					}
				});
			}else{
				chack(geoLastKnownAddress);
			}
		}
	}
	
	
	private void chack(Address geoAddress){
		Logger.getInstance(TAG).debug("国家：" + geoAddress.getCountryName());
		Logger.getInstance(TAG).debug("省份：" + geoAddress.getAdminArea());
		Logger.getInstance(TAG).debug("市区：" + geoAddress.getSubAdminArea());
		Logger.getInstance(TAG).debug("区域：" + geoAddress.getSubLocality());
		Logger.getInstance(TAG).debug("路段：" + geoAddress.getFeatureName());
		
		if (isActivityFocusble && !geoAddress.getAdminArea().equals(mSaveProvince) && !isAreaShow) {
			changeAreaDialog(geoAddress);
		}
	}

	/**
	 * 定位成功切换地区对话框
	 * 
	 * @param geoAddress
	 */
	private void changeAreaDialog(final Address geoAddress) {
		MsgDialog msgDialog = new MsgDialog(this);
		msgDialog.setTitle("切换地区");
		msgDialog.setLeftBtText(R.string.cancel);
		msgDialog.setRightBtText("切换");
		msgDialog.setMessage("地区定位成功,是否需要切换到 " + geoAddress.getAdminArea()
				+ "?");
		msgDialog.setOnBtClickListener(new OnBtClickListener() {

			@Override
			public void onRightBtClick() {

				SettingHelper.setString(RetrievalMainActivity.this,
						Field.CURRENT_PROVINCE, geoAddress.getAdminArea());
				SettingHelper.setString(RetrievalMainActivity.this,
						Field.CURRENT_CITY, geoAddress.getSubAdminArea());
				SettingHelper.setString(RetrievalMainActivity.this,
						Field.CURRENT_AREA, geoAddress.getSubLocality());

				mAreaText.setText(geoAddress.getAdminArea());
				mSaveProvince = geoAddress.getAdminArea();
				mAreaDialog.setFirstCurrentItem(mSaveProvince);
				Region mRegion = mAreaDialog.getFirstObject();
				if (mRegion != null)
					mProvinceId = mRegion.getId();
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		msgDialog.show();
		((ZhimaApplication) getApplication()).isLbsSucess = true;
	}

	private void findView() {
		mSelectAreaLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrieval_main_selectArea);
		mAreaText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrieval_main_area);

		mMerchantSpaceLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_merchantSpace);
		mMerchantSpaceImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_merchantSpace);
		mMerchantSpaceText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_merchantSpace);

		mCommonSpaceLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_commonSpace);
		mCommonSpaceImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_commonSpace);
		mCommonSpaceText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_commonSpace);

		mVehicleLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_vehicle);
		mVehicleImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_vehicle);
		mVehicleText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_vehicle);

		mCouponLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_coupon);
		mCouponImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_coupon);
		mCouponText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_coupon);
		//
		mIdoLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_ido);
		mIdoImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_ido);
		mIdoText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_ido);

		mXiyinLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_xiyin);
		mXiyinImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_xiyin);
		mXiyinText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_xiyin);

		mFameLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_fame);
		mFameImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_fame);
		mFameText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_fame);

		mJadeLayout = (LinearLayout) mChildrenView
				.findViewById(R.id.layout_retrival_main_jade);
		mJadeImage = (ImageView) mChildrenView
				.findViewById(R.id.img_retrival_main_jade);
		mJadeText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_jade);

		mBarText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_bar);
		mChafingDishText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_chafingDish);
		mFlowerText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_flower);
		mOutdoorsText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_outdoors);
		mMuseumText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_museum);
		mFitnessText = (TextView) mChildrenView
				.findViewById(R.id.txt_retrival_main_fitness);

		// ---------------------------------

		mRetrievalList = (ListView) mChildrenView
				.findViewById(R.id.lstv_retrieval_main_List);
	}

	private void setUpView() {
		mSelectAreaLayout.setOnClickListener(this);

		mMerchantSpaceImage.setOnClickListener(this);
		mCommonSpaceImage.setOnClickListener(this);
		mVehicleImage.setOnClickListener(this);
		mCouponImage.setOnClickListener(this);

		mIdoImage.setOnClickListener(this);
		mXiyinImage.setOnClickListener(this);
		mFameImage.setOnClickListener(this);
		mJadeImage.setOnClickListener(this);

		// mCouponLayout.setVisibility(View.GONE);

		mBarText.setOnClickListener(this);
		mChafingDishText.setOnClickListener(this);
		mFlowerText.setOnClickListener(this);
		mOutdoorsText.setOnClickListener(this);
		mMuseumText.setOnClickListener(this);
		mFitnessText.setOnClickListener(this);

		mAreaText.setText(mSaveProvince);

		// -----------------------------

		setRetriListAdapter();
	}

	private void setRetriListAdapter() {
		// //TODO
		// mRetrievalList.setAdapter(new ZhimaAdapter<Object>(this,
		// R.layout.retrieval_main_retri_item, new ArrayList<Object>()) {
		//
		// @Override
		// public Object createViewHolder(View view, Object data) {
		// ViewHolder holder = new ViewHolder();
		// holder.mImage = (ImageView)
		// view.findViewById(R.id.img_retrievalMain_retriItem_image);
		// holder.mTitleText = (TextView)
		// view.findViewById(R.id.txt_retrievalMain_retriItem_title);
		// return holder;
		// }
		//
		// @Override
		// public void bindView(Object data, int position, View view) {
		// ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		// switch (position) {
		// case RetriItem.IDO://知天使
		// setView(holder, R.drawable.retrieval_main_ido, "知天使");
		// break;
		// case RetriItem.bUSINESS_SPACE:
		// setView(holder, R.drawable.retrieval_main_ido, "商户活动");
		// break;
		// case RetriItem.PUBLIC_SPACE:
		// setView(holder, R.drawable.retrieval_main_ido, "公共空间");
		// break;
		// case RetriItem.LOVER:
		// setView(holder, R.drawable.retrieval_main_ido, "知相印-喜印");
		// break;
		// case RetriItem.FAME:
		// setView(holder, R.drawable.retrieval_main_ido, "知相印-名玺");
		// break;
		// case RetriItem.JADE:
		// setView(holder, R.drawable.retrieval_main_ido, "知相印-玉玺");
		// break;
		// case RetriItem.MERCHANT_ACTIVITY:
		// setView(holder, R.drawable.retrieval_main_ido, "商户活动");
		// break;
		// }
		// }
		//
		// @Override
		// public int getCount() {
		// return mRetriCount;
		// }
		//
		// @Override
		// public Object getItem(int position) {
		// //TODO
		// return null;
		// }
		//
		// class ViewHolder{
		// ImageView mImage;
		// TextView mTitleText;
		// }
		//
		// private void setView(ViewHolder holder,int imageResId,String title){
		// holder.mImage.setImageResource(imageResId);
		// holder.mTitleText.setText(title);
		// }
		// });

		mRetrievalList.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder holder = new ViewHolder();

				View view = View.inflate(RetrievalMainActivity.this,
						R.layout.retrieval_main_retri_item, null);

				holder.mImage = (ImageView) view
						.findViewById(R.id.img_retrievalMain_retriItem_image);
				holder.mTitleText = (TextView) view
						.findViewById(R.id.txt_retrievalMain_retriItem_title);

				switch (position) {
				case RetriItem.IDO:// 知天使
					setView(holder, R.drawable.retrieval_main_ido, "知天使");
					break;
				case RetriItem.bUSINESS_SPACE:
					setView(holder, R.drawable.retrieval_main_business_space,
							"商业空间");
					break;
				case RetriItem.PUBLIC_SPACE:
					setView(holder, R.drawable.retrieval_main_public_space,
							"公共空间");
					break;
				case RetriItem.LOVER:
					setView(holder, R.drawable.retrieval_main_lover, "知相印-喜印");
					break;
				case RetriItem.FAME:
					setView(holder, R.drawable.retrieval_main_fame, "知相印-名玺");
					break;
				case RetriItem.JADE:
					setView(holder, R.drawable.retrieval_main_jade, "知相印-誉玺");
					break;
				case RetriItem.MERCHANT_ACTIVITY:
					setView(holder,
							R.drawable.retrieval_main_merchant_activity, "商户活动");
					view.findViewById(R.id.below_view).setVisibility(
							View.VISIBLE);
					break;
				}

				return view;
			}

			@Override
			public long getItemId(int position) {
				// TODO
				return position;

			}

			@Override
			public Object getItem(int position) {
				// TODO
				return null;

			}

			@Override
			public int getCount() {
				// TODO
				return mRetriCount;
			}

			private void setView(ViewHolder holder, int imageResId, String title) {
				holder.mImage.setImageResource(imageResId);
				holder.mTitleText.setText(title);
			}

			class ViewHolder {
				ImageView mImage;
				TextView mTitleText;
			}
		});

		mRetrievalList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case RetriItem.IDO:// 知天使
					setIntentResult(false, ZMObjectKind.IDOL_OBJECT, -1, "知天使");
					break;
				case RetriItem.bUSINESS_SPACE:
					setIntentResult(false, ZMObjectKind.BUSINESS_OBJECT, -1,
							"商业空间");
					break;
				case RetriItem.PUBLIC_SPACE:
					setIntentResult(false, ZMObjectKind.PUBLICPLACE_OBJECT, -1,
							"公共空间");
					break;
				case RetriItem.LOVER:
					setIntentResult(false, ZMObjectKind.WEDDING_OBJECT, -1,
							"喜印");
					break;
				case RetriItem.FAME:
					setIntentResult(false, ZMObjectKind.VCARD_OBJECT, -1, "名玺");
					break;
				case RetriItem.JADE:
					setIntentResult(false, ZMObjectKind.ORGANIZATION_OBJECT,
							-1, "誉玺");
					break;
				case RetriItem.MERCHANT_ACTIVITY:
					setIntentResult(true, ZMObjectKind.VEHICLE_OBJECT, -1,
							"商户活动");
					break;
				}
			}
		});
	}

	private final class RetriItem {
		/** 知天使 */
		public final static int IDO = 0;
		/** 商户空间 */
		public final static int bUSINESS_SPACE = 1;
		/** 公共空间 */
		public final static int PUBLIC_SPACE = 2;
		/** 知天使-喜印 */
		public final static int LOVER = 3;
		/** 知相印-名玺 */
		public final static int FAME = 4;
		/** 知相印-玉玺 */
		public final static int JADE = 5;
		/** 商户活动 */
		public final static int MERCHANT_ACTIVITY = 6;
	}

	@Override
	public void onClick(View v) {

		// 选择区域
		if (v.getId() == R.id.layout_retrieval_main_selectArea) {
			isAreaShow = true;
			mAreaDialog.showAtLocation(mAreaText, Gravity.CENTER, 0, 0);
			return;
		}

		switch (v.getId()) {
		case R.id.img_retrival_main_merchantSpace:// 商业空间
			setIntentResult(false, ZMObjectKind.BUSINESS_OBJECT, -1, "商业空间");
			break;
		case R.id.img_retrival_main_commonSpace:// 公共空间
			setIntentResult(false, ZMObjectKind.PUBLICPLACE_OBJECT, -1, "公共空间");
			break;
		case R.id.img_retrival_main_vehicle:// 交通工具
			setIntentResult(false, ZMObjectKind.VEHICLE_OBJECT, -1, "交通工具");
			break;
		case R.id.img_retrival_main_coupon:// 优惠券
			setIntentResult(true, ZMObjectKind.VEHICLE_OBJECT, -1, "商户活动");
			break;
		case R.id.img_retrival_main_ido:// 知天使
			setIntentResult(false, ZMObjectKind.IDOL_OBJECT, -1, "知天使");
			break;
		case R.id.img_retrival_main_xiyin:// 知相印——喜印
			setIntentResult(false, ZMObjectKind.WEDDING_OBJECT, -1, "喜印");
			break;
		case R.id.img_retrival_main_fame:// 知相印-名玺
			setIntentResult(false, ZMObjectKind.VCARD_OBJECT, -1, "名玺");
			break;
		case R.id.img_retrival_main_jade:// 知天使-玉玺
			setIntentResult(false, ZMObjectKind.ORGANIZATION_OBJECT, -1, "玉玺");
			break;
		// case R.id.txt_retrival_main_bar://酒吧
		//
		// break;
		// case R.id.txt_retrival_main_chafingDish://火锅
		//
		// break;
		// case R.id.txt_retrival_main_flower://鲜花
		//
		// break;
		// case R.id.txt_retrival_main_outdoors://户外
		//
		// break;
		// case R.id.txt_retrival_main_museum://博物馆
		//
		// break;
		// case R.id.txt_retrival_main_fitness://健身中心
		//
		// break;
		default:
			setIntentResult(false, ZMObjectKind.BUSINESS_OBJECT, -1, "商业空间");
			break;
		}
	}

	private void setIntentResult(boolean isCoupon, int mSearchKind,
			long spacekindId, String typeName) {
		Intent intent = new Intent(this, RetrievalResultActivity.class);

		if (!isCoupon) {
			intent.putExtra("mSearchKind", mSearchKind);
		}
		intent.putExtra("spacekindId", spacekindId);
		intent.putExtra("cityName", mSaveProvince);

		intent.putExtra("typeName", typeName);
		intent.putExtra("isCoupon", isCoupon);

		if (mProvinceId != -1) {
			intent.putExtra("mCityId", mProvinceId);
		} else {
			Region mRegion = mAreaDialog.getFirstObject();
			if (mRegion != null)
				mProvinceId = mRegion.getId();
			if (mProvinceId != -1) {
				intent.putExtra("mCityId", mProvinceId);
			} else {
				ArrayList<Region> provinceList = RegionService
						.getInstance(this).getProvinceList();
				if (provinceList != null && provinceList.size() > 0) {
					intent.putExtra("mCityId", provinceList.get(0).getId());
				} else {
					intent.putExtra("mCityId", -1);
				}
			}
		}
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if (mSideBarView.onKeyBack()) {
			// return true;
			// }
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		isActivityFocusble = false;
	}
}
