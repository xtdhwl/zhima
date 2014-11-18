package com.zhima.ui.diary.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.DiaryProtocolHandler.ForwardDirayProtocol;
import com.zhima.base.protocol.DiaryProtocolHandler.NewDiaryProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetBindSpacesProtocolHandler;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMSpace;
import com.zhima.data.service.DiaryService;
import com.zhima.data.service.UserService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.diary.controller.ShareSpaceButtom;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.usercenter.data.controller.CheckBoxSingleManage;

/**
 * @ClassName: ShareActivity
 * @Description: 分享
 * @author luqilong
 * @date 2013-1-18 上午9:46:56
 */
public class DiaryShareActivity extends BaseActivity {

	protected static final String TAG = DiaryShareActivity.class.getSimpleName();

	//新日志
	private ZMDiary mNewZMDiary;
	//被转发日志 
	private ZMDiary mForWardZMDiary;
	//用户id
	private long mUserId;
	//日志id
	private long mDiaryId;
	//空间id
	private long mSpaceId;
	private boolean mIsForwardDiary;
	private boolean mIsSpaceDiary;

	private ViewGroup mSpaceHeadView;
	private ViewGroup mSpaceGroupView;

	private CheckBoxSingleManage radioManager;
	private CheckBox mQQCheckView;
	private CheckBox mRenrenCheckView;
	private CheckBox mSinaCheckView;

	/** 用户开通的空间 */
	private ZMSpaceService mZMSpaceService;
	/** 发日志服务 */
	private DiaryService mDiaryService;
//	private User mUser;

	private RefreshListData<ZMSpace> mCacheUserSpaceList;

	private ArrayList<ZMSpace> mDataList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_share_activity);

		findView();
		setTopbar();
		init();

		Intent it = getIntent();
		mNewZMDiary = (ZMDiary) it.getSerializableExtra(DiaryConsts.diary);

		mUserId = it.getLongExtra(DiaryConsts.user_Id, -1);
		mDiaryId = it.getLongExtra(DiaryConsts.diary_Id, -1);
		mSpaceId = it.getLongExtra(DiaryConsts.space_id, -1);
		mIsForwardDiary = it.getBooleanExtra(DiaryConsts.is_forward, false);
		mIsSpaceDiary = mSpaceId > 0 ? true : false;
		//如果mDiaryId！=-1--》转发
		if (mUserId == -1) {
			mUserId = UserService.getInstance(this).getMyself().getUserId();
		}
		if (mDiaryId != -1) {
			if (mIsSpaceDiary) {
				mForWardZMDiary = mDiaryService.getCacheDiaryList(mSpaceId, mIsSpaceDiary).getData(mDiaryId);
			} else {
				mForWardZMDiary = mDiaryService.getCacheDiaryList(mUserId, mIsSpaceDiary).getData(mDiaryId);
			}
		}

		if (mNewZMDiary != null) {
			getSpaceServiceData();
		} else {
			HaloToast.show(getApplicationContext(), R.string.load_failed);
			finish();
		}
	}

	private void getSpaceServiceData() {
		mCacheUserSpaceList = mZMSpaceService.getCacheUserSpaceList(mUserId);
		if (mCacheUserSpaceList.isEmpty()) {
//			mZMSpaceService.getUserBundlingSpaces(this);
			mZMSpaceService.getBundlingSpaces(mUserId, this);
		} else {
			mDataList = mCacheUserSpaceList.getDataList();
			setSpaceView(mDataList);
		}
	}

	private void init() {
		mZMSpaceService = ZMSpaceService.getInstance(this);
		mDiaryService = DiaryService.getInstance(this);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_USER_BINDING_SPACES) {
				// 获取空间列表
				if (protocol.isHandleSuccess()) {
					GetBindSpacesProtocolHandler p = (GetBindSpacesProtocolHandler) protocol;
					mDataList = p.getSpaceList();
					setSpaceView(mDataList);
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			} else if (protocol.getProtocolType() == ProtocolType.DIARY_NEW_PROTOCOL) {
				// 发日志
				if (protocol.isHandleSuccess()) {
					NewDiaryProtocol p = (NewDiaryProtocol) protocol;
					long diaryId = p.getDiaryId();
					//TODO 跳转到日志空间

					HaloToast.show(getApplicationContext(), "发送成功!");
					Intent it = new Intent();
					it.putExtra("id", diaryId);
					setResult(RESULT_OK, it);
					finish();
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			} else if (protocol.getProtocolType() == ProtocolType.FORWARD_DIARY_PROTOCOL) {
				// 转发日志
				if (protocol.isHandleSuccess()) {
					// 更新产品view
					ForwardDirayProtocol p = (ForwardDirayProtocol) protocol;
					long diaryId = p.getDiaryId();
					//TODO 跳转到日志空间

					HaloToast.show(getApplicationContext(), "转发成功!");
					Intent it = new Intent();
					it.putExtra("id", diaryId);
					setResult(RESULT_OK, it);
					finish();
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());

				}
			}
		} else {
			// TODO 网络访问错误
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}

	/**
	 * 发送
	 */
	private OnClickListener sendTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//微薄分享
			ArrayList<ZMSpace> sycnSpace = getSycnSpace();
			mNewZMDiary.setSpaceList(sycnSpace);
			// 判断是否转发 | 写日志
			startWaitingDialog(null, "正在发送...");
			if (mIsForwardDiary) {
				mDiaryService.forwardDiary(mForWardZMDiary, mNewZMDiary, mIsSpaceDiary, DiaryShareActivity.this);
			} else {
				mDiaryService.newDiary(mNewZMDiary.getTitle(), mNewZMDiary.getContent(), mNewZMDiary.getImageUrl(),
						mNewZMDiary.getPrivacyStatus(), mNewZMDiary.getSpaceList(), DiaryShareActivity.this);
			}

			CheckBox selectRadioButton = radioManager.getSelectCheckButton();
			if (selectRadioButton != null) {
				String sms_message = "";
				String shareContent = "";
				User mUser = UserService.getInstance(DiaryShareActivity.this).getMyself();
				if(mUser != null){
					if (mIsForwardDiary) {
						sms_message = String.format(getString(R.string.diary_sms_message), mUser.getNickname(),
								mUser.getZMUserId());
						shareContent = sms_message;
					} else {
						sms_message = String.format(getString(R.string.diary_share_content), mUser.getNickname(),
								mUser.getZMUserId());
						shareContent = sms_message;
					}
					
					int radioId = selectRadioButton.getId();
					switch (radioId) {
					case R.id.check_qq:
						Logger.getInstance(TAG).debug("分享至：" + "腾讯");
						SharePopupMenu.qqShare(DiaryShareActivity.this, DiaryShareActivity.this, v, sms_message,
								shareContent);
						break;
					case R.id.check_sina:
						Logger.getInstance(TAG).debug("分享至：" + "新浪");
						SharePopupMenu.sinaShare(DiaryShareActivity.this, DiaryShareActivity.this, v, sms_message,
								shareContent);
						break;
					case R.id.check_renren:
						Logger.getInstance(TAG).debug("分享至：" + "人人");
						SharePopupMenu.renrenShare(DiaryShareActivity.this, DiaryShareActivity.this, v, sms_message,
								shareContent);
						break;
					}
				}else{
					ErrorManager.showErrorMessage(getApplicationContext());
				}
			}
		}
	};

	//刷新日志分享
	private ArrayList<ZMSpace> getSycnSpace() {
		ArrayList<ZMSpace> array = new ArrayList<ZMSpace>();
		if (mSpaceHeadView.getVisibility() == View.VISIBLE) {
			int count = mSpaceGroupView.getChildCount();
			for (int i = 0; i < count; i++) {
				View view = mSpaceGroupView.getChildAt(i);
				if (view instanceof ShareSpaceButtom) {
					ShareSpaceButtom spaceView = (ShareSpaceButtom) view;
					if (spaceView.isChecked()) {
						ZMSpace space = (ZMSpace) spaceView.getTag();
						array.add(space);
					}
				}
			}
		}
		return array;
	}

	private void setSpaceView(ArrayList<ZMSpace> zmSpaceList) {
		if (zmSpaceList.size() > 0) {
			for (ZMSpace zmSpace : zmSpaceList) {
				ShareSpaceButtom buttom = new ShareSpaceButtom(this);
				buttom.setTag(zmSpace);
				buttom.setSpaceId(zmSpace.getId());
				buttom.setSpaceName(zmSpace.getName());
				if (mSpaceId == zmSpace.getId()) {
					buttom.setChecked(true);
				} else {
					buttom.setChecked(false);
				}
				mSpaceGroupView.addView(buttom);
			}
			mSpaceHeadView.setVisibility(View.VISIBLE);
		} else {
			mSpaceHeadView.setVisibility(View.GONE);
		}

	}

	private void findView() {
		mSpaceHeadView = (ViewGroup) findViewById(R.id.layout_head);
		mSpaceGroupView = (ViewGroup) findViewById(R.id.layout_space);
		mQQCheckView = (CheckBox) findViewById(R.id.check_qq);
		mRenrenCheckView = (CheckBox) findViewById(R.id.check_renren);
		mSinaCheckView = (CheckBox) findViewById(R.id.check_sina);

		radioManager = new CheckBoxSingleManage();
		radioManager.addCheckButton(mQQCheckView);
		radioManager.addCheckButton(mRenrenCheckView);
		radioManager.addCheckButton(mSinaCheckView);

	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.send);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(sendTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("发送至");
	}
}
