package com.zhima.ui.retrieval.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.SearchProtocolHandler.QueryBusinessPromotionProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.QueryZMObjectProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.SearchBusinessPromotionProtocol;
import com.zhima.base.protocol.SearchProtocolHandler.SearchZMObjectProtocol;
import com.zhima.base.utils.ImeHelper;
import com.zhima.base.utils.SettingHelper;
import com.zhima.base.utils.SettingHelper.Field;
import com.zhima.data.model.BaseData;
import com.zhima.data.model.CouponQueryResult;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.IdolJob;
import com.zhima.data.model.Orderkind;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.Region;
import com.zhima.data.model.SpaceQueryResult;
import com.zhima.data.model.Spacekind;
import com.zhima.data.service.SearchService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomDialog.OnOnlyBtClickListener;
import com.zhima.ui.common.view.CustomListView;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaIdolJobDialog;
import com.zhima.ui.common.view.ZhimaOrderDialog;
import com.zhima.ui.common.view.ZhimaRegionDialog;
import com.zhima.ui.common.view.ZhimaSpaceTypeDialog;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.space.activity.RetriConponInfoActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 检索结果
 * @ClassName: RetrievalResultActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-9-22 下午4:08:53
*/
public class RetrievalResultActivity extends BaseActivity implements OnClickListener {

	private ZhimaTopbar mTopbar;

	private LinearLayout mWholeLayout;
	private RelativeLayout mTypeLayout;
	private RelativeLayout mAreaLayout;
	private RelativeLayout mSortLayout;
	private TextView mTypeBtn;
	private TextView mAreaBtn;
	private TextView mSortBtn;

	private CustomListView<BaseData> mResultList;

	private boolean isInputState = false;
	
	/** 搜索框布局 */
	private RelativeLayout mEditLayout;
	/** 右边搜索按钮 */
	private ImageView rightButton1;
	/** 页面标题 */
	private TextView titleText;

	/** 搜索框 */
	private EditText mRetrievalEdit;
	/** 提示布局 */
	private LinearLayout mEditHintLayout;
	/** 删除关键字按钮 */
	private ImageView mRemoveImage;

	/** 搜索关键字 popup布局view */
	private LinearLayout mRetrievalKeyLayout;
	/**  */
	private TextView mRetrievalKeyText;

	/** 搜索框下边搜索view */
	private PopupWindow mRetrievalPop;

	private LinearLayout mNoResultLayout;
	
	private LinearLayout mTypeSortLayout;

	private ZhimaSpaceTypeDialog mTypeDialog;
	private int mTypeCurrentItem;
	private ZhimaRegionDialog mAreaDialog;
	private int mAreaCurrentItem;
	private ZhimaOrderDialog mOrderDialog;
	private int mOrderCurrentItem;
	
	private ZhimaIdolJobDialog mIdoJobDialog;
	private int mIdoJobCurrentItem;
	
	/** 搜索类型 */
	protected Spacekind mSpacekind;
	/** 排序类型 */
	protected Orderkind mOrderkind;

	/** 搜索关键字 */
	protected String mRetrievalText;

	/** 是否是检索模式（检索、搜索） */
	private boolean isRetrieval = true;
	private boolean isResultSearch = false;
	/** 是否是第一次加载  */
	private boolean isFirstLoad = true;
	
	/** 省份Id */
	private long mProvinceId = -1;
	private long mRetrievalType = -1;

	private int mSearchKind;
	private long mSpacekindId = -1;
	private long mCityId = -1;
	/** 如果知天使类型 需要有职业选择 */
	private long mJobKindId = -1;
	private String mCityName;
	private String mTypeName;
	private boolean isCoupon;
	
	private ZMLocationManager mZMLocationManager;
	private GeoCoordinate mLastKnownGeoCoordinate;
	private TextView mNoResultText;
	
	/** 是否最后一页 */
	private boolean isLastPage = false;
	/** 是否结束请求服务器 */
	private boolean isRequestFinish = false;
	
	/** 存储省份 */
	private String mSaveProvince;
	/** 存储城市 */
	private String mSaveCity;
	/** 存储区域  */
	private String mSaveArea;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.retrieval_result_activity);

		getIntentData();
		createAreaDialog();
		setTopbar();
		findView();
		getSaveData();
		setUpView();
//		setListnerd();
	}
	
//	private void setListnerd() {
//		
//		final View view = findViewById(R.id.layout_retrieval_result);
//		
//		final View decorView = getWindow().getDecorView();
//		final int decorHeight = decorView.getHeight();
//		decorView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//				// TODO Auto-generated method stub
//				int viewHeight = view.getHeight();
//				
//				if (!((decorHeight - viewHeight) > 100)) {
//					if(isInputState){
//						ImeHelper.hideIME(mRetrievalEdit);
//						mEditLayout.setVisibility(View.GONE);
//						titleText.setVisibility(View.VISIBLE);
//						rightButton1.setVisibility(View.VISIBLE);
//						mTopbar.setRightLayoutVisible(true);
//						dismissPop();
//						mRetrievalEdit.setText("");
//						isInputState = false;
//						return;
//					}
//				}
//			}
//		});
//		ImeHelper.setSoftInputMode(this,false);
//	}

	/**
	 * 创建 area 对话框
	 */
	private void createAreaDialog() {
		if (mAreaDialog == null) {
			mAreaDialog = new ZhimaRegionDialog(this, mAreaBtn, 2, mCityId, true);
			mAreaDialog.setTitle(R.string.select_area);
			mAreaDialog.setOnOnlyBtClickListener("完成", new OnOnlyBtClickListener() {
				@Override
				public void onOnlyBtClick() {
					getAreaData(true);
				}
			});
		}
	}

	/**
	 * 从对话框对象获取 地区数据 
	 */
	private void getSaveData() {
		mSaveProvince = SettingHelper.getString(this, Field.CURRENT_PROVINCE, "");
		mSaveCity = SettingHelper.getString(this, Field.CURRENT_CITY, "");
		mSaveArea = SettingHelper.getString(this, Field.CURRENT_AREA, "");
		
		if(mCityName.equals(mSaveProvince)){
			mCityName = mSaveArea;
				mAreaDialog.setFirstCurrentItem(mSaveCity);
				mAreaDialog.setSecondCurrtentItem(mSaveArea);
			getAreaData(false);
		}
	}
	
	/**
	 * area对话框点击操作
	 * @param flag 是否请求服务器
	 */
	private void getAreaData(boolean flag) {
			Region area = mAreaDialog.getSecondObject();
			Region city = mAreaDialog.getFirstObject();
			
		SettingHelper.setString(this, Field.CURRENT_CITY,city!=null&&!TextUtils.isEmpty(city.getName())?city.getName():"");
		SettingHelper.setString(this, Field.CURRENT_AREA,area!=null&&!TextUtils.isEmpty(area.getName())?area.getName():"");
		
		if(area != null && area.getId()!=-1){
			if(!TextUtils.isEmpty(area.getName())){
				mAreaBtn.setText(area.getName());
				if(area.getId() != mCityId){
					mCityId = area.getId()!=-1?area.getId():mProvinceId;
					if(flag){
						getSortData();
					}
				}
			}
		} else if (city != null) {
			if (!TextUtils.isEmpty(city.getName())) {
				mAreaBtn.setText(city.getName());
				if(city.getId() != mCityId){
					mCityId = city.getId()!=-1?city.getId():mProvinceId;
					if(flag){
						getSortData();
					}
				}
			}
		}
		
	}

	/**
	 * 获取传过来的数据
	 */
	private void getIntentData() {
		Intent intent = getIntent();
		mSearchKind = intent.getIntExtra("mSearchKind", ZMObjectKind.BUSINESS_OBJECT);
		mRetrievalType = mSpacekindId = mSearchKind;
		mCityId = intent.getLongExtra("mCityId", -1);
		mProvinceId = mCityId;
		mCityName = intent.getStringExtra("cityName");
		mTypeName = intent.getStringExtra("typeName");
		isCoupon = intent.getBooleanExtra("isCoupon", isCoupon);
		
		
		mZMLocationManager = ZMLocationManager.getInstance();
		mLastKnownGeoCoordinate = mZMLocationManager.getLastKnownGeoCoordinate();
	}

	private void startWaitDialog() {
		if (isFirstLoad) {
			startWaitingDialog("", "请稍等");
		}
	}
	
	/**
	 * 从服务器获取检索数据   刷新
	 */
	private void getRetrievalData(boolean isRefresh) {
		
		if (isRefresh)
			mResultList.mAlreadyLoadData.clear();
		
		if(isCoupon){
			SearchService.getInstance(this).queryBusinessPromotion(mSpacekindId, mCityId, mLastKnownGeoCoordinate, mOrderkind, isRefresh, this);
		}else{
			SearchService.getInstance(this).queryZMObject(mSearchKind, mSpacekindId, mCityId, mLastKnownGeoCoordinate, mOrderkind,mJobKindId, isRefresh,this);
		}
		startWaitDialog();
	}

	/**
	 * 从服务器获取搜索数据   刷新
	 */
	private void getSearchData(boolean isRefresh) {
		if (isRefresh){
			mResultList.mAlreadyLoadData.clear();
		}
		if(isCoupon){
			SearchService.getInstance(this).searchBusinessPromotion(mRetrievalText, isRefresh, this);
		}else{
			SearchService.getInstance(RetrievalResultActivity.this).searchZMObject(mRetrievalText, mSearchKind, isRefresh,
					RetrievalResultActivity.this);
		}
		startWaitDialog();
	}

	//	/**
	//	 * 如果空间类型没有选择的话，给它赋一个初始值.
	//	 */
	//	private void setDefaultData() {
	//		if(mSpacekindId == -1){
	//			ArrayList<Spacekind> mainTypeList = SpaceKindService.getInstance(this).getMainTypeList();
	//			if(mainTypeList!=null && mainTypeList.size()>0){
	//				mSpacekindId = mainTypeList.get(0).getId();
	//			}
	//		}
	//	}

	/**
	 * 设置顶部标题栏
	 */
	private void setTopbar() {
		mTopbar = getTopbar();
		mTopbar.setLeftLayoutVisible(true);
		mTopbar.setRightLayoutVisible(true);

		View leftView = View.inflate(this, R.layout.topbar_leftview, null);
		View rightView = View.inflate(this, R.layout.topbar_rightview, null);

		mTopbar.addLeftLayoutView(leftView);
		mTopbar.addRightLayoutView(rightView);

		final RelativeLayout backLayout = (RelativeLayout) leftView.findViewById(R.id.layout_titlebar_leftButton);
		titleText = (TextView) leftView.findViewById(R.id.txt_topbar_title);
		titleText.setText(mTypeName + "检索");
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(isResultSearch){
					mTypeSortLayout.setVisibility(View.VISIBLE);
					isRetrieval = true;
					isResultSearch = false;
					setNoresultView();
					return ;
				}
				
				if(isInputState){
					ImeHelper.hideIME(mRetrievalEdit);
					mEditLayout.setVisibility(View.GONE);
					titleText.setVisibility(View.VISIBLE);
					rightButton1.setVisibility(View.VISIBLE);
					mTopbar.setRightLayoutVisible(true);
					dismissPop();
					mRetrievalEdit.setText("");
					isInputState = false;
					return;
				}
				finish();
			}

		}); 

		mEditLayout = (RelativeLayout) leftView.findViewById(R.id.layout_retrieval_edit_box);
		
		mEditHintLayout = (LinearLayout) leftView.findViewById(R.id.layout_retrieval_edit_hint);
		TextView mHintText = (TextView) leftView.findViewById(R.id.txt_hint); 
		mHintText.setText("搜索"+mTypeName);
		mRetrievalEdit = (EditText) leftView.findViewById(R.id.edt_retrieval);
		mRemoveImage = (ImageView) leftView.findViewById(R.id.img_retrieval_remove);

		mRetrievalEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mRetrievalText = mRetrievalEdit.getText().toString();

				if (mRetrievalText == null || "".equals(mRetrievalText)) {
					mEditHintLayout.setVisibility(View.VISIBLE);
					mRemoveImage.setVisibility(View.GONE);
					dismissPop();
				} else {
					mEditHintLayout.setVisibility(View.GONE);
					mRemoveImage.setVisibility(View.VISIBLE);
					mRetrievalKeyText.setText("搜索   \"" + mRetrievalText + "\"");
					mRetrievalPop.setFocusable(false);
					showPopwindow();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
//		mRetrievalEdit.setOnEditorActionListener(new OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					mTypeSortLayout.setVisibility(View.GONE);
//					isRetrieval = false;
//					isResultSearch = true;
//					dismissPop();
//					ImeHelper.hideIME(mRetrievalEdit);
//					mEditLayout.setVisibility(View.GONE);
//					titleText.setVisibility(View.VISIBLE);
//					rightButton1.setVisibility(View.VISIBLE);
//					mTopbar.setRightLayoutVisible(true);
//					isInputState = false;
//					getSearchData(true);
//					mRetrievalEdit.setText("");
//				}
//				return false;
//			}
//		});

		mRemoveImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRetrievalEdit.setText("");
			}
		});

		RelativeLayout rightButtonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		rightButton1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		rightButtonLayout1.setVisibility(View.VISIBLE);
		rightButton1.setImageResource(R.drawable.search_icon);
		rightButtonLayout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRetrievalEdit.setFocusable(true);
				mEditLayout.setVisibility(View.VISIBLE);
				titleText.setVisibility(View.GONE);
				rightButton1.setVisibility(View.GONE);
				mTopbar.setRightLayoutVisible(false);
				ImeHelper.showIME(mRetrievalEdit);
				mRetrievalEdit.setText("");
				isInputState = true;
			}
		});

	}
	private void setNoresultView() {
		if(isRetrieval){
			mNoResultText.setText(R.string.noResult_retrieval);
		}else{
			mNoResultText.setText(R.string.noResult_search);
		}
	}

	/**
	 * 寻找控件
	 */
	private void findView() {
		mWholeLayout = (LinearLayout)this.findViewById(R.id.layout_retrieval_result);
		
		mTypeLayout = (RelativeLayout) this.findViewById(R.id.layout_retrieval_result_type);
		mAreaLayout = (RelativeLayout) this.findViewById(R.id.layout_retrieval_result_area);
		mSortLayout = (RelativeLayout) this.findViewById(R.id.layout_retrieval_result_sort);

		mTypeBtn = (TextView) this.findViewById(R.id.btn_retrieval_result_type);
		mAreaBtn = (TextView) this.findViewById(R.id.btn_retrieval_result_area);
		mSortBtn = (TextView) this.findViewById(R.id.btn_retrieval_result_sort);

		mResultList = (CustomListView<BaseData>) this.findViewById(R.id.clv_retrieval_result_resultList);

		mNoResultLayout = (LinearLayout) this.findViewById(R.id.layout_retrieval_result_noResult);
		mNoResultText = (TextView) this.findViewById(R.id.txt_retrieval_noResult_title);
		
		mTypeSortLayout = (LinearLayout) this.findViewById(R.id.layout_retrieval_result_typeSort);
		
		if (!TextUtils.isEmpty(mCityName)) {
			mAreaBtn.setText(mCityName);
		}
		if (!TextUtils.isEmpty(mTypeName)) {
			mTypeBtn.setText(mTypeName);
		}
	}

	/**
	 * 设置控件
	 */
	private void setUpView() {
		
		if(mSearchKind == ZMObjectKind.VCARD_OBJECT || mSearchKind == ZMObjectKind.WEDDING_OBJECT){
			mSortLayout.setVisibility(View.GONE);
			findViewById(R.id.view_interval_line2).setVisibility(View.GONE);
		}
		
		if(mSearchKind == ZMObjectKind.IDOL_OBJECT){
			mSortBtn.setText("职业");
		}
		
		mTypeLayout.setOnClickListener(this);
		mAreaLayout.setOnClickListener(this);
		mSortLayout.setOnClickListener(this);

		View view = View.inflate(this, R.layout.retrieval_popupwindow_view, null);
		mRetrievalPop = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);
		mRetrievalPop.setBackgroundDrawable(new ColorDrawable(0x00000000));
		mRetrievalPop.setAnimationStyle(R.style.customDialog_anim_style);
		mRetrievalPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		mRetrievalPop.setOutsideTouchable(false);

		mRetrievalKeyLayout = (LinearLayout) view.findViewById(R.id.layout_retrieval_key);
		mRetrievalKeyText = (TextView) view.findViewById(R.id.txt_retrieval_key);

		mRetrievalKeyLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTypeSortLayout.setVisibility(View.GONE);
				isRetrieval = false;
				isResultSearch = true;
				dismissPop();
				ImeHelper.hideIME(mRetrievalEdit);
				mEditLayout.setVisibility(View.GONE);
				titleText.setVisibility(View.VISIBLE);
				rightButton1.setVisibility(View.VISIBLE);
				mTopbar.setRightLayoutVisible(true);
				isInputState = false;
				getSearchData(true);
				mRetrievalEdit.setText("");
			}
		});

		mResultList.addLoading();
		mResultList.setVisibility(View.GONE);
		mResultList.setBatchLoad(new ZhimaAdapter<BaseData>(RetrievalResultActivity.this,
				R.layout.retrieval_search_resultlist_item, mResultList.mAlreadyLoadData) {

			@Override
			public Object createViewHolder(View view, BaseData data) {
				ViewHolder holder = new ViewHolder();
				holder.mResultImage = (ImageView) view.findViewById(R.id.img_retrieval_resultItem_image);
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_title);
				holder.mPhoneText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_phone);
				holder.mAddressText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_address);
				
				holder.mDistanceText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_distance);
				return holder;
			}

			@Override
			public void bindView(final BaseData data, int position, View view) {
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				
				if(!isCoupon){
					final SpaceQueryResult spaceQueryResult = (SpaceQueryResult) data;
					
					
					if(mSearchKind == ZMObjectKind.IDOL_OBJECT || mSearchKind == ZMObjectKind.WEDDING_OBJECT
							||mSearchKind == ZMObjectKind.VCARD_OBJECT || mSearchKind == ZMObjectKind.ORGANIZATION_OBJECT){
						HttpImageLoader.getInstance(mContext).loadImage(spaceQueryResult.getImageUrl(), holder.mResultImage,
								RetrievalResultActivity.this.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
					}else{
						HttpImageLoader.getInstance(mContext).loadImage(spaceQueryResult.getImageUrl(), holder.mResultImage,
								RetrievalResultActivity.this.getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
					}
					
					holder.mTitleText.setText(spaceQueryResult.getName());
					holder.mAddressText.setText("人气:"+spaceQueryResult.getPraiseCount());
					holder.mAddressText.setVisibility(View.GONE);
					if(mSearchKind == ZMObjectKind.IDOL_OBJECT){
						holder.mPhoneText.setText(spaceQueryResult.getAge()+"岁");
					}else if(mSearchKind == ZMObjectKind.BUSINESS_OBJECT || mSearchKind == ZMObjectKind.PUBLICPLACE_OBJECT){
						holder.mPhoneText.setText(spaceQueryResult.getAddress());
						holder.mAddressText.setText("电话："+spaceQueryResult.getTelephone());
						holder.mAddressText.setVisibility(View.VISIBLE);
					}else {
						holder.mPhoneText.setText("ZMID:"+spaceQueryResult.getZMId());
					}
					
//					holder.mTitleText.setText(spaceQueryResult.getName());
//					if (mSearchKind != ZMObjectKind.VEHICLE_OBJECT) {
//						holder.mAddressText.setText(spaceQueryResult.getAddress());
//					} else {
//						holder.mAddressText.setText(spaceQueryResult.getTelephone());
//					}
//					
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ActivitySwitcher.openSpaceActivity(RetrievalResultActivity.this, spaceQueryResult.getTargetId(),
									spaceQueryResult.getTargetType(),null,false);
						}
					});
				}else{
					final CouponQueryResult conponQueryResult = (CouponQueryResult) data;
					
					HttpImageLoader.getInstance(mContext).loadImage(conponQueryResult.getImageUrl(), holder.mResultImage,
							RetrievalResultActivity.this.getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
					
					holder.mTitleText.setText(conponQueryResult.getName());
					holder.mPhoneText.setText(conponQueryResult.getDescription());
					holder.mAddressText.setText(new SimpleDateFormat("yyyy-MM-dd").format(conponQueryResult.getBeginTime())+"——"
							+new SimpleDateFormat("yyyy-MM-dd").format(conponQueryResult.getDeadlineTime()));
					
					holder.mDistanceText.setText(conponQueryResult.getDistance()+"m");
					holder.mDistanceText.setVisibility(View.VISIBLE);
					
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(RetrievalResultActivity.this,RetriConponInfoActivity.class);
							
							intent.putExtra("targetId", conponQueryResult.getTtargetId());
							intent.putExtra("targetType", conponQueryResult.getTargetType());
							intent.putExtra("conponId", conponQueryResult.getId());
							intent.putExtra("title", conponQueryResult.getName());
							intent.putExtra("beginTime", conponQueryResult.getBeginTime());
							intent.putExtra("endTime", conponQueryResult.getDeadlineTime());
							intent.putExtra("content", conponQueryResult.getDescription());
							intent.putExtra("remain", conponQueryResult.getRemainCount());
							intent.putExtra("pophoPath", conponQueryResult.getImageUrl());
							startActivity(intent);
						}
					});
					
				}
			}

			@Override
			public void getFirstData() {
				isFirstLoad = true;
				mResultList.isLoading(true);
				getRetrievalData(true);
			}

			@Override
			public void getData() {
				isFirstLoad = false;

				if(isLastPage){
					if(!isRequestFinish){
						HaloToast.show(getApplicationContext(),R.string.no_more_data);
					}
					isRequestFinish = true;
					return;
				}

				mResultList.addLoading();

				mResultList.isLoading(true);
				if (isRetrieval) {
					getRetrievalData(false);
				} else {
					getSearchData(false);
				}
			}

			class ViewHolder {
				ImageView mResultImage;
				TextView mTitleText;
				TextView mPhoneText;
				TextView mAddressText;
				
				TextView mDistanceText;
			}
		});

	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_retrieval_result_type://搜索类型
			if (mTypeDialog == null) {
				if(mSearchKind == ZMObjectKind.BUSINESS_OBJECT||mSearchKind == ZMObjectKind.PUBLICPLACE_OBJECT||mSearchKind == ZMObjectKind.VEHICLE_OBJECT){
					mTypeDialog = new ZhimaSpaceTypeDialog(this, mTypeBtn, 2, mSearchKind);
				}else{
					mTypeDialog = new ZhimaSpaceTypeDialog(this, mTypeBtn, 1, mSearchKind);
				}
				mTypeDialog.setTitle(R.string.select_type);
				mTypeDialog.setOnOnlyBtClickListener("完成", new OnOnlyBtClickListener() {
					@Override
					public void onOnlyBtClick() {
						mSpacekind = mTypeDialog.getSecondObject();
						if (mSpacekind != null && mSpacekind.getId()!=-1) {
							mTypeBtn.setText(mSpacekind.getName());
						} else {
							mSpacekind = mTypeDialog.getFirstObject();
							if (mSpacekind != null) {
								mTypeBtn.setText(mSpacekind.getName());
							}
						}
						
						if(mSpacekind != null){
							if(mSpacekindId != mSpacekind.getId()){
								mSpacekindId = mSpacekind.getId()!=-1?mSpacekind.getId():mRetrievalType;
								getSortData();
							}
						}
					}
				});
			}
			mTypeDialog.show();
			break;
		case R.id.layout_retrieval_result_area://地区
			
				mAreaDialog.showAtLocation(mAreaBtn, Gravity.CENTER, 0, 0);
			break;
		case R.id.layout_retrieval_result_sort://排序
			if(mSearchKind == ZMObjectKind.IDOL_OBJECT){
				if(mIdoJobDialog == null){
					mIdoJobDialog = new ZhimaIdolJobDialog(this, mSortBtn);
					mIdoJobDialog.setTitle("选择职业");
					mIdoJobDialog.setCurrentItem(mIdoJobCurrentItem);
					mIdoJobDialog.setOnOnlyBtClickListener("完成", new OnOnlyBtClickListener() {
						
						@Override
						public void onOnlyBtClick() {
							//TODO
							IdolJob mIdoJobKind = mIdoJobDialog.getFirstObject();
							
							if(mIdoJobKind!=null){
								mSortBtn.setText(mIdoJobKind.getJob());
								if(mJobKindId!=mIdoJobKind.getId()){
									mJobKindId = mIdoJobKind.getId();
									getSortData();
								}
								mIdoJobCurrentItem = mIdoJobDialog.getCurrentItem();
							}
						}
					});
				}
				
				mIdoJobDialog.show();
			}else{
				if (mOrderDialog == null) {
					mOrderDialog = new ZhimaOrderDialog(this, mSortBtn);
					mOrderDialog.setTitle(R.string.select_sort);
					mOrderDialog.setCurrentItem(mOrderCurrentItem);
					mOrderDialog.setOnOnlyBtClickListener("完成", new OnOnlyBtClickListener() {
						
						@Override
						public void onOnlyBtClick() {
							
							mOrderkind = mOrderDialog.getFirstObject();
							if (mOrderkind != null) {
								mSortBtn.setText(mOrderkind.getTitle());
								if(mOrderCurrentItem != mOrderDialog.getCurrentItem()){
									getSortData();	
								}
								mOrderCurrentItem = mOrderDialog.getCurrentItem();
							}
						}
					});
				}
				mOrderDialog.show();
			}
			break;
		}
	}

	private void getSortData(){
		if(isRetrieval){
			getRetrievalData(true);
		}else{
			getSearchData(true);
		}
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
		
		mNoResultLayout.setVisibility(View.GONE);
		isLastPage = false;
		isRequestFinish = false;
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		super.onHttpResult(protocol);
		if (protocol.isHttpSuccess()) {
			int protocolType = protocol.getProtocolType();
			if (protocolType == ProtocolType.SEARCH_SPACE_PROTOCOL) {
				SearchZMObjectProtocol p = (SearchZMObjectProtocol) protocol;
				if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					mResultList.deleteLoading();
					mResultList.updateListView();
				} else {
					RefreshListData<SpaceQueryResult> dataList = p.getDataList();
					ArrayList<SpaceQueryResult> mQueryResult = dataList.getDataList();

					int loadSize = mResultList.mAlreadyLoadData.size();
					int size = mQueryResult.size();

					for (int i = loadSize; i < size; i++) {
						mResultList.mAlreadyLoadData.add(mQueryResult.get(i));
					}

					if (mQueryResult.size() < SystemConfig.PAGE_SIZE) {
						mResultList.deleteLoading();
					}

					mResultList.updateListView();
					if (p.getDataList().isLastPage()){
						isLastPage = true;
						mResultList.deleteLoading();
					} 
				}
			}else if (protocolType == ProtocolType.SEARCH_BUSINESS_COUPON_PROTOCOL) {
				SearchBusinessPromotionProtocol p = (SearchBusinessPromotionProtocol) protocol;
				if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					mResultList.deleteLoading();
					mResultList.updateListView();
				} else {
					RefreshListData<CouponQueryResult> dataList = p.getDataList();
					ArrayList<CouponQueryResult> mQueryResult = dataList.getDataList();

					int loadSize = mResultList.mAlreadyLoadData.size();
					int size = mQueryResult.size();

					for (int i = loadSize; i < size; i++) {
						mResultList.mAlreadyLoadData.add(mQueryResult.get(i));
					}

					if (mQueryResult.size() < SystemConfig.PAGE_SIZE) {
						mResultList.deleteLoading();
					}

					mResultList.updateListView();
					if (p.getDataList().isLastPage()){
						isLastPage = true;
						mResultList.deleteLoading();
					} 
				}
			} else if (protocolType == ProtocolType.QUERY_SPACE_PROTOCOL) {
				QueryZMObjectProtocol p = (QueryZMObjectProtocol) protocol;
				if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					mResultList.deleteLoading();
					mResultList.updateListView();
				} else {
					RefreshListData<SpaceQueryResult> dataList = p.getDataList();
					ArrayList<SpaceQueryResult> spaceQueryResult = dataList.getDataList();
					
					int loadSize = mResultList.mAlreadyLoadData.size();
					int size = spaceQueryResult.size();

					for (int i = loadSize; i < size; i++) {
						mResultList.mAlreadyLoadData.add(spaceQueryResult.get(i));
					}
					
					if (spaceQueryResult.size() < 8) {
						mResultList.deleteLoading();
					}
					mResultList.updateListView();
					
					if (p.getDataList().isLastPage()){
						isLastPage = true;
						mResultList.deleteLoading();
					} 
				}
			}else if(protocolType == ProtocolType.QUERY_BUSINESS_COUPON_PROTOCOL){
				QueryBusinessPromotionProtocol p = (QueryBusinessPromotionProtocol) protocol;
				if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					mResultList.deleteLoading();
					mResultList.updateListView();
					HaloToast.show(getApplicationContext(), "没数据了");
				} else {
					RefreshListData<CouponQueryResult> dataList = p.getDataList();
					ArrayList<CouponQueryResult> conponQueryResult = dataList.getDataList();
					
					int loadSize = mResultList.mAlreadyLoadData.size();
					int size = conponQueryResult.size();
					
					for (int i = loadSize; i < size; i++) {
						mResultList.mAlreadyLoadData.add(conponQueryResult.get(i));
					}
					
					if (conponQueryResult.size() < 8) {
						mResultList.deleteLoading();
					}
					
					mResultList.updateListView();
					
					if (p.getDataList().isLastPage()){
						isLastPage = true;
						mResultList.deleteLoading();
					}
				}
			} 
			mResultList.setVisibility(View.VISIBLE);
		}else {
			mResultList.deleteLoading();
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}

		if (mResultList.mAlreadyLoadData.size() == 0) {
			setNoresultView();
			mNoResultLayout.setVisibility(View.VISIBLE);
		}

		dismissWaitingDialog();
		mResultList.isLoading(false);
	}

	/**
	 * 显示popwindow
	 */
	private void showPopwindow() {
		if (mRetrievalPop != null && !mRetrievalPop.isShowing()) {
			mRetrievalPop.showAsDropDown(mTopbar);
		}
	}

	/**
	 * 销毁popwindow
	 */
	private void dismissPop() {

		if (mRetrievalPop != null && mRetrievalPop.isShowing()) {
			mRetrievalPop.dismiss();
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			if(isResultSearch){
				mTypeSortLayout.setVisibility(View.VISIBLE);
				isRetrieval = true;
				isResultSearch = false;
				setNoresultView();
				return false;
			}
			
			if (isInputState) {
				dismissPop();
				mEditLayout.setVisibility(View.GONE);
				titleText.setVisibility(View.VISIBLE);
				rightButton1.setVisibility(View.VISIBLE);
				mTopbar.setRightLayoutVisible(true);
				isInputState = false;
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		dismissPop();
		if (mTypeDialog != null) {
			mTypeDialog.dismiss();
		}
		if (mOrderDialog != null) {
			mOrderDialog.dismiss();
		}
		if (mAreaDialog != null) {
			mAreaDialog.dismiss();
		}
		if(mIdoJobDialog != null){
			mIdoJobDialog.dismiss();
		}
		
		isLastPage = false;
		isRequestFinish = false;
		
		super.onDestroy();
	}
}
