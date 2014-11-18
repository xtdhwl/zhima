package com.zhima.ui.usercenter.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.base.consts.ZMConsts;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryListProtocol;
import com.zhima.base.protocol.FriendsProtocolHandler.ApplicationFriendProtocol;
import com.zhima.base.protocol.FriendsProtocolHandler.DeleteFriendProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetUserHavingsProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.UserHavings;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMSpace;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.DiaryService;
import com.zhima.data.service.FriendService;
import com.zhima.data.service.RegionService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomListView;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.diary.activity.DiaryEditActivity;
import com.zhima.ui.diary.activity.DiaryInfoActivity;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.space.activity.PreviewActivity;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.tools.UserUtils.CheckFriendListener;
import com.zhima.ui.tools.ViewInitTools;
import com.zhima.ui.usercenter.data.activity.AlbumListActivity;
import com.zhima.ui.usercenter.data.activity.ZmSpaceManagerActivity;
import com.zhima.ui.usercenter.data.friend.activity.FriendListActivity;
import com.zhima.ui.usercenter.data.lattice.activity.LatticeListActivity;
import com.zhima.ui.usercenter.data.profile.activity.MyProfileActivity;
import com.zhima.ui.usercenter.data.profile.activity.MyProfileEditActivity;
import com.zhima.ui.usercenter.watchdog.activity.MessageChatActivity;

/**
 * 个人资料主页面
 * @ClassName: PersonalDataActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-21 下午5:26:58
 */
public class PersonalDataActivity extends BaseActivity implements OnClickListener {

	public static final String USER_ID = "userId";
	public static final String IS_MYSELF = "isMyself";
	public static final String IS_MYFRIEND = "isMyfriend";
	
	public static final int EDIT_DATA_REQUESTCODE = 101;
	
	public static boolean PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = false;

	private CustomListView<ZMDiary> mDiaryList;
	private ZhimaAdapter<ZMDiary> mPCenterAdapter;

	private View mHeadView;
	private TextView mNoResultText;
	private LinearLayout mDataLayout;
	/** 头像 */
	private ImageView mHeadImage;
	/** 名字 */
	private TextView mNameText;
	private ImageView mGenderImage;
	/** UID */
	private TextView mUIDText;
	/** 性别 */
	private TextView mGenderAreaText;
	/** 修改资料button */
	private Button mUpdateDataBt;
	/** 简介 */
	private TextView mIntroductionText;

	/** 芝麻控件 */
	private LinearLayout mZhimaSpaceLayout;
	private TextView mZhimaSpaceText;
	private TextView mZhimaSpaceCountText;
	/** 照片 */
	private LinearLayout mPhotographLayout;
	private TextView mPhotographText;
	private TextView mPhotographCountText;
	/** 格子铺 */
	private LinearLayout mInBoxLayout;
	private TextView mInBoxText;
	private TextView mInBoxCountText;
	/** 好友 */
	private LinearLayout mFriendLayout;
	private TextView mFriendText;
	private TextView mFriendCountText;

	private TextView mDiaryCountText;

	private boolean isLastPage = false;
	private boolean isRequestFinish = false;
	/** 是否是第一次加载 */
	private boolean isFirstLoad = true;

	private long mUserId;
	private boolean isMyself;
	private boolean isMyfriend;
	/** 是否是匿名用户 */
	private boolean isGuest;

	private List<ZMDiary> diaryDataList;
	private User mUser;

	/** 删除添加好友 */
	private ImageView rightBt1;
	/** 删除添加好友 */
	private RelativeLayout rightLayout1;
	private DiaryService mDiaryService;
	
	/** activity是否是重新可见  主要刷新列表用 */
	private boolean isResume = false;
	
	private boolean isgetUserMixedQuantity = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		((ZhimaApplication)getApplication()).popAllActivity();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_center_data_activity);

		PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = false;
		
		initData();
		setTopbar();
		findView();
		initAdapter();
		setUpVivew();
	}

	private void initData() {
		//TODO
		mDiaryService = DiaryService.getInstance(this);
		Intent intent = getIntent();
		mUserId = intent.getLongExtra(USER_ID, ZMConsts.INVALID_ID);
		isMyself = intent.getBooleanExtra(IS_MYSELF, false);
		isMyfriend = intent.getBooleanExtra(IS_MYFRIEND, false);
		isGuest = AccountService.getInstance(this).isGuest();

		if (isMyself) {
			mUser = UserService.getInstance(this).getMyself();
		} else {
			if (mUserId != -1) {
				mUser = UserService.getInstance(this).getUser(mUserId);
			}
		}

		diaryDataList = new ArrayList<ZMDiary>();
	}

	/**
	 * 获取用户芝麻空间、相册、格子铺、好友、日子数量：
	 * @Title: getUserMixedQuantity
	 */
	private void getUserMixedQuantity() {
		UserService.getInstance(this).getUserMixedQuantity(mUserId, this);
		isgetUserMixedQuantity = true;
	}

	private void setTopbar() {
		ViewInitTools.setTopBar(this, "个人资料", View.GONE, null);
		if(isMyself){
			ZhimaTopbar topbar = getTopbar();
			View topbarRightView = View.inflate(this, R.layout.topbar_rightview, null);
			rightLayout1 = (RelativeLayout) topbarRightView.findViewById(R.id.layout_topbar_rightButton1);
			rightBt1 = (ImageView) topbarRightView.findViewById(R.id.img_topbar_rightButton1);
			
			rightBt1.setImageResource(R.drawable.user_center_add_friend);
			rightLayout1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//TODO
					Intent intent = new Intent(PersonalDataActivity.this,DiaryEditActivity.class);
//					Intent inboxIntent = new Intent(this, DiaryEditActivity.class);
					startActivity(intent);
				}
			});
			
			rightLayout1.setVisibility(View.VISIBLE);
			topbar.setRightLayoutVisible(true);
			topbar.addRightLayoutView(topbarRightView);
		}else if (!isMyself && !isGuest) {
			ZhimaTopbar topbar = getTopbar();

			View topbarRightView = View.inflate(this, R.layout.topbar_rightview, null);
			rightLayout1 = (RelativeLayout) topbarRightView.findViewById(R.id.layout_topbar_rightButton1);
			rightBt1 = (ImageView) topbarRightView.findViewById(R.id.img_topbar_rightButton1);

//			if(isMyfriend){
//				rightBt1.setImageResource(R.drawable.user_center_add_friend);
//				rightLayout1.setOnClickListener(DeleteFriendListener);
//			}else{
//				rightBt1.setImageResource(R.drawable.rubbish);
//				rightLayout1.setOnClickListener(AddFriendListener);
//			}

			setTopBarFriendBt();

			rightLayout1.setVisibility(View.VISIBLE);
			topbar.setRightLayoutVisible(true);
			topbar.addRightLayoutView(topbarRightView);
		}
	}

	private void setTopBarFriendBt() {
		if (isMyfriend) {
			rightBt1.setImageResource(R.drawable.rubbish);
			rightLayout1.setOnClickListener(DeleteFriendListener);
		} else {
			rightBt1.setImageResource(R.drawable.user_center_add_friend);
			rightLayout1.setOnClickListener(AddFriendListener);
		}
	}

	/**
	 * 获取日志列表
	 * @Title: getDiaryData
	 * @Description: TODO
	 * @param refreshed 是否刷新
	 */
	public void getDiaryData(boolean refreshed) {
		if(refreshed){
			mDiaryList.mAlreadyLoadData.clear();
		}
		mDiaryService.getDiaryList(mUserId, refreshed, this);
	}

	private void findView() {
		mDiaryList = (CustomListView) this.findViewById(R.id.lstv_personal_data_diaryList);

		mHeadView = View.inflate(this, R.layout.personal_center_data_headview, null);
		mNoResultText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_noResult);
		
		mDataLayout = (LinearLayout) mHeadView.findViewById(R.id.layout_personal_center_user_data);
		mHeadImage = (ImageView) mHeadView.findViewById(R.id.img_personal_data_headImage);
		mNameText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_name);
		mGenderImage = (ImageView) mHeadView.findViewById(R.id.img_personal_data_gender);
		mUIDText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_uid);
		mGenderAreaText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_genderArea);
		mUpdateDataBt = (Button) mHeadView.findViewById(R.id.btn_personal_data_updateData);
		mIntroductionText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_introduction);

		mZhimaSpaceLayout = (LinearLayout) mHeadView.findViewById(R.id.layout_personal_data_zhimaSpace);
		mZhimaSpaceText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_zhimaSpace);
		mZhimaSpaceCountText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_zhimaSpaceCount);

		mPhotographLayout = (LinearLayout) mHeadView.findViewById(R.id.layout_personal_data_photograph);
		mPhotographText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_photograph);
		mPhotographCountText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_photographCount);

		mInBoxLayout = (LinearLayout) mHeadView.findViewById(R.id.layout_personal_data_inBox);
		mInBoxText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_inBox);
		mInBoxCountText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_inBoxCount);

		mFriendLayout = (LinearLayout) mHeadView.findViewById(R.id.layout_personal_data_friend);
		mFriendText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_friend);
		mFriendCountText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_friendCount);

		mDiaryCountText = (TextView) mHeadView.findViewById(R.id.txt_personal_data_diaryCount);
	}

	private void setUpVivew() {

		if (isGuest) {
			mUpdateDataBt.setVisibility(View.GONE);
		}
		if (!isMyself) {
			mUpdateDataBt.setText("发站内信");
		}
		mUpdateDataBt.setOnClickListener(this);

		//设置个人简要资料
		setPersonalData();
		
		mDataLayout.setOnClickListener(this);

		mZhimaSpaceText.getPaint().setFakeBoldText(true);
		mPhotographText.getPaint().setFakeBoldText(true);
		mInBoxText.getPaint().setFakeBoldText(true);
		mFriendText.getPaint().setFakeBoldText(true);

		mZhimaSpaceLayout.setOnClickListener(this);
		mPhotographLayout.setOnClickListener(this);
		mInBoxLayout.setOnClickListener(this);
		mFriendLayout.setOnClickListener(this);

		mDiaryList.addHeadView(mHeadView);
//		mDiaryList.addFooterView(mFooterView);
		mDiaryList.addLoading();
		mDiaryList.setVisibility(View.GONE);
		mDiaryList.setBatchLoad(mPCenterAdapter);
	}

	/**
	 * 设置个人简要资料
	 * @Title: setPersonalData
	 * @Description: TODO
	 */
	private void setPersonalData() {
		//TODO
		if (mUser != null) {
			mNameText.setText(mUser.getNickname());
			mGenderImage.setImageResource(GenderType.getGenderImage(TextUtils.isEmpty(mUser.getGender()) ? "M" : mUser
					.getGender()));
			mUIDText.setText("UID:" + mUser.getZMUserId());
//			RegionService.getInstance(this).get
			mGenderAreaText.setText(RegionService.getInstance(this).getRegionStr((mUser.getCityId())));
			mIntroductionText.setText("简介:" + mUser.getSignature());
			HttpImageLoader.getInstance(this).loadImage(mUser.getImageUrl(), mHeadImage, getActivityId(),
					R.drawable.default_image, ImageScaleType.MEDIUM);
		}
	}

	@Override
	public void onClick(View v) {
		//TODO
		switch (v.getId()) {
		case R.id.btn_personal_data_updateData://编辑个人资料
			if (isMyself) {
				Intent intent = new Intent(this, MyProfileEditActivity.class);
				intent.putExtra(ACTIVITY_EXTRA, mUserId);
//				startActivityForResult(intent, requestCode);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, MessageChatActivity.class);
				intent.putExtra(ACTIVITY_EXTRA, mUserId);
				intent.putExtra(ACTIVITY_EXTRA2, mUser.getNickname());
				startActivity(intent);
			}
			break;
		case R.id.layout_personal_center_user_data://展示个人资料
			Intent dataIntent = new Intent(this, MyProfileActivity.class);
			dataIntent.putExtra(ACTIVITY_EXTRA, mUserId);
			startActivity(dataIntent);
			break;
		case R.id.layout_personal_data_zhimaSpace://芝麻空间
			if(isMyself){
				Intent spaceIntent = new Intent(this, ZmSpaceManagerActivity.class);
				spaceIntent.putExtra(ACTIVITY_EXTRA, mUserId);
				startActivity(spaceIntent);
			}else{
				HaloToast.show(this, "不允许查看别人的芝麻空间");
			}
			break;
		case R.id.layout_personal_data_photograph://照片
			Intent photoIntent = new Intent(this, AlbumListActivity.class);
			photoIntent.putExtra(ACTIVITY_EXTRA, mUserId);
			startActivity(photoIntent);
			break;
		case R.id.layout_personal_data_inBox://格子铺
			Intent inBoxIntent = new Intent(this, LatticeListActivity.class);
			inBoxIntent.putExtra(ACTIVITY_EXTRA, mUserId);
			startActivity(inBoxIntent);
			break;
		case R.id.layout_personal_data_friend://好友
			Intent friendIntent = new Intent(this, FriendListActivity.class);
			friendIntent.putExtra(ACTIVITY_EXTRA, mUserId);
			startActivity(friendIntent);
			break;
		}
	}

	private void initAdapter() {

		mPCenterAdapter = new ZhimaAdapter<ZMDiary>(this, R.layout.personal_center_data_diary_item,
				mDiaryList.mAlreadyLoadData) {

			@Override
			public Object createViewHolder(View view, ZMDiary data) {
				ViewHolder holder = new ViewHolder();
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_title);
				holder.mDateText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_date);
				holder.mContentText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_content);
				holder.mDiaryImage = (ImageView) view.findViewById(R.id.img_personalData_diaryItem_image);
				holder.mToSpaceText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_toSpace);
				holder.mforwordText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_forwordCount);
				holder.mcommentText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_commentCount);

				holder.mForwardContentLayout = (LinearLayout) view
						.findViewById(R.id.layout_personalData_diaryItem_forwardContent);
				holder.mFromAndDateLayout = (LinearLayout) view
						.findViewById(R.id.layout_personalData_diaryItem_fromAndDate);
				holder.mFromText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_from);
				holder.mPublishDateText = (TextView) view.findViewById(R.id.txt_personalData_diaryItem_publishedDate);
				return holder;
			}

			/*
			 * (non-Javadoc)
			 * @see com.zhima.ui.adapter.ZhimaAdapter#bindView(java.lang.Object,
			 * int, android.view.View)
			 */
			@Override
			public void bindView(ZMDiary data, final int position, View view) {
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);

				final ZMDiary diary = data;
				//TODA 设置日志内容

//				holder.mTitleText.getPaint().setFakeBoldText(true);

				if (diary != null) {

					holder.mTitleText.setText(diary.getTitle());
					holder.mDateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(diary.getPostTime()));
					if(!diary.isOrginalExists()){
						holder.mContentText.setText("原始内容已删除");
					}else{
						holder.mContentText.setText(FaceHelper.getInstance(PersonalDataActivity.this).getSpannableString(
								diary.getContent()));
					}

					if (diary.getImageUrl() != null && !"".equals(diary.getImageUrl())) {
						HttpImageLoader.getInstance(mContext).loadImage(diary.getImageUrl(), holder.mDiaryImage,
								mActivityId, R.drawable.default_image, ImageScaleType.MEDIUM);
						holder.mDiaryImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								//TODO 进入图片浏览
//								HaloToast.show(getApplicationContext(), "进入图片大图浏览");
								Intent it = new Intent(PersonalDataActivity.this, PreviewActivity.class);
								it.putExtra(PreviewActivity.ACTIVITY_URL, diary.getImageUrl());
								it.putExtra("type",  ImageScaleType.LARGE);
								startActivity(it);
							}
						});
						holder.mDiaryImage.setVisibility(View.VISIBLE);
					} else {
						holder.mDiaryImage.setVisibility(View.GONE);
					}

					List<ZMSpace> spaceList = diary.getSpaceList();
					if (spaceList != null) {
						StringBuffer buffer = new StringBuffer();
						for (ZMSpace space : spaceList) {
							buffer.append("@" + space.getName() + " ");
						}
						holder.mToSpaceText.setText(buffer.toString());
						holder.mToSpaceText.setVisibility(View.VISIBLE);
					} else {
						holder.mToSpaceText.setVisibility(View.GONE);
					}
					holder.mforwordText.setText(diary.getForwardCount() + "");
					holder.mcommentText.setText(diary.getReplyCount() + "");

					if (diary.isOriginal()) {
						holder.mForwardContentLayout.setBackgroundResource(R.color.transparent);
						holder.mFromAndDateLayout.setVisibility(View.GONE);
					} else {
						holder.mForwardContentLayout.setBackgroundResource(R.color.user_center_forword_diary_bg);
						holder.mFromAndDateLayout.setVisibility(View.VISIBLE);
						if(diary.getRawAuthor()!=null){
							final User rawAuthor = diary.getRawAuthor();
							holder.mFromText.setText(rawAuthor.getNickname());
							holder.mFromText.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO
//								HaloToast.show(getApplicationContext(), "二货");
									final boolean isMySelf = UserService.getInstance(PersonalDataActivity.this).isMySelf(rawAuthor.getUserId());
									if(!isMySelf){
//										UserUtils util = new UserUtils(PersonalDataActivity.this);
//										util.checkIsMyfriend(rawAuthor.getUserId(),new CheckFriendListener() {
//											
//											@Override
//											public void checkFriend(boolean isFriend) {
//												Intent intent = new Intent(PersonalDataActivity.this,PersonalDataActivity.class);
//												intent.putExtra(USER_ID, rawAuthor.getUserId());
//												intent.putExtra(IS_MYSELF, isMySelf);
//												intent.putExtra(IS_MYFRIEND, isFriend);
//												startActivity(intent);
//											}
//										});
										UserUtils util = new UserUtils(PersonalDataActivity.this);
										util.switchAcitivity(diary.getRawAuthor().getUserId(), false);
									}else{
										Intent intent = new Intent(PersonalDataActivity.this,PersonalCenterMainActivity.class);
										intent.putExtra(USER_ID, rawAuthor.getUserId());
										intent.putExtra(IS_MYSELF, isMySelf);
										startActivity(intent);
									}
								}
							});
						}
						holder.mPublishDateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(diary
								.getRawCreatedOn()));
					}
				}/*
				 * else{ if(position == 2 || position == 5 || position == 6){
				 * holder.mForwardContentLayout.setBackgroundResource(R.color.
				 * user_center_forword_diary_bg);
				 * holder.mFromAndDateLayout.setVisibility(View.VISIBLE); }else{
				 * holder
				 * .mForwardContentLayout.setBackgroundResource(R.color.transparent
				 * ); holder.mFromAndDateLayout.setVisibility(View.GONE); } }
				 */

				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//TODO
						Intent intent = new Intent(PersonalDataActivity.this, DiaryInfoActivity.class);
						intent.putExtra(DiaryConsts.user_Id, mUserId);
						intent.putExtra(DiaryConsts.position, position);
						startActivity(intent);
					}
				});
			}

			@Override
			public void getFirstData() {
				isFirstLoad = true;
				mDiaryList.isLoading(true);
				getDiaryData(true);
			}

			@Override
			public void getData() {
				
				if(mUserId == -1){
					return;
				}
				
				isFirstLoad = false;
				if (isLastPage) {
					if (!isRequestFinish) {
						HaloToast.show(getApplicationContext(), R.string.no_more_data);
					}
					isRequestFinish = true;
					return;
				}
				getDiaryData(false);
				mDiaryList.addLoading();
				mDiaryList.isLoading(true);
			}

			class ViewHolder {
				TextView mTitleText;//标题
				TextView mDateText;//日期
				TextView mContentText;//主内容
				ImageView mDiaryImage;
				TextView mToSpaceText;//发往哪几个空间
				TextView mforwordText;//转发数
				TextView mcommentText;//评论数

				LinearLayout mForwardContentLayout;
				LinearLayout mFromAndDateLayout;
				TextView mFromText;
				TextView mPublishDateText;
			}
		};
	}

	private void setUserHavings(UserHavings userHavings) {
		//TODO
		if (userHavings != null) {
			mZhimaSpaceCountText.setText(userHavings.getSpaceCount() + "");
			mPhotographCountText.setText(userHavings.getAlbumCount() + "");
			mInBoxCountText.setText(userHavings.getLatticeProductCount() + "");
			mFriendCountText.setText(userHavings.getFriendCount() + "");
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		if (isFirstLoad && !isgetUserMixedQuantity) {
			startWaitingDialog("", "请稍等...");
		}else if(isgetUserMixedQuantity){
			isgetUserMixedQuantity = false;
		}
		mNoResultText.setVisibility(View.GONE);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			int protocolType = protocol.getProtocolType();
			if (protocolType == ProtocolType.GET_DIARY_LIST_PROTOCOL) {
				GetDiaryListProtocol diaryProtocol = (GetDiaryListProtocol) protocol;
				if (diaryProtocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
					//没数据
					mDiaryList.deleteLoading();
					mDiaryList.updateListView();
				} else {
					//有数据
					RefreshListData<ZMDiary> dataList = diaryProtocol.getDataList();
					diaryDataList = dataList.getDataList();

//					System.out.println("diaryDataList:"+diaryDataList.size());

					int loadSize = mDiaryList.mAlreadyLoadData.size();
					int size = diaryDataList.size();

					for (int i = loadSize; i < size; i++) {
						mDiaryList.mAlreadyLoadData.add(diaryDataList.get(i));
					}

					if (diaryDataList.size() < 8) {
						mDiaryList.deleteLoading();
					}

					mDiaryCountText.setText("共" + mDiaryList.mAlreadyLoadData.size() + "条日志");
					mDiaryList.updateListView();
					if (dataList.isLastPage()) {
						isLastPage = true;
						mDiaryList.deleteLoading();
					}
				}
				
				if (mDiaryList.mAlreadyLoadData.size() == 0) {
					mDiaryList.deleteLoading();
					mNoResultText.setVisibility(View.VISIBLE);
				}
				
			} else if (protocolType == ProtocolType.GET_USER_HAVINGS_PROTOCOL) {
				GetUserHavingsProtocol p = (GetUserHavingsProtocol) protocol;
				UserHavings userHavings = p.getUserHavings();
				setUserHavings(userHavings);
			} else if (protocolType == ProtocolType.APPLICATION_FRIEND_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					ApplicationFriendProtocol p = (ApplicationFriendProtocol) protocol;
					if(p.getResult() == -2){
						HaloToast.show(getApplicationContext(), "对不起,您已经申请过好友");
						rightLayout1.setVisibility(View.GONE);
					}else if(p.getResult() == 0){
						HaloToast.show(getApplicationContext(), "对不起,"+mUser.getNickname()+"已经是您的好友");
						isMyfriend = true;
						setTopBarFriendBt();
					}else if(p.getResult() > 0){
						HaloToast.show(this, R.string.apply_add_friend_success);
						rightLayout1.setVisibility(View.GONE);
					}else {
						HaloToast.show(this, R.string.apply_add_friend_failed);
					}
				} else {
					HaloToast.show(this, R.string.apply_add_friend_failed);
				}
			} else if (protocolType == ProtocolType.DELETE_FRIEND_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					DeleteFriendProtocol p = (DeleteFriendProtocol) protocol;
					if(p.isFriendDeleted()){
						HaloToast.show(this, R.string.delete_friend_success);
						isMyfriend = false;
						setTopBarFriendBt();
					}else{
						HaloToast.show(this, R.string.delete_friend_failed);
					}
				} else {
					HaloToast.show(this, R.string.delete_friend_failed);
				}
			}
			
		} else {
			mDiaryList.deleteLoading();
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
		mDiaryList.setVisibility(View.VISIBLE);

		dismissWaitingDialog();
		mDiaryList.isLoading(false);
	}
	
	@Override
	public void onResume() {
		//TODO
		super.onResume();
		if(isMyself){
			mUser = UserService.getInstance(this).getMyself();
//			if (mUser == null) {
//				// TODO跳到首页面
//				finish();
//				return;
//			}
			setPersonalData();
		}
		getUserMixedQuantity();
		
//		System.out.println("PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST:"+PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST);
		
		if(PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST){
			getDiaryData(true);
			PERSONAL_DATA_IS_NEED_REFRESH_DIARYLIST = false;
		}
	}
	
	@Override
	public void onStop() {
		//TODO
		super.onStop();
		isResume = true;
	}
	
	@Override
	public void onDestroy() {
		//TODO
		super.onDestroy();
		isLastPage = false;
		isRequestFinish = false;
	}

	private OnClickListener AddFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			FriendService.getInstance(PersonalDataActivity.this).applicationFriend(mUserId, PersonalDataActivity.this);
		}
	};

	private OnClickListener DeleteFriendListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			FriendService.getInstance(PersonalDataActivity.this).deleteFriend(mUserId, PersonalDataActivity.this);
		}
	};

}
