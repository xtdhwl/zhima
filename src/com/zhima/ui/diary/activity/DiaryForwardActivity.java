package com.zhima.ui.diary.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.DiaryPrivacyStatus;
import com.zhima.base.utils.DateUtils;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.service.DiaryService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.usercenter.activity.PersonalDataActivity;

/**
 * @ClassName: DiaryForwardActivity
 * @Description: 转发日志
 * @author luqilong
 * @date 2013-1-21 上午10:38:41
 */
public class DiaryForwardActivity extends BaseActivity {

	private static final int request_forward_Code = 0;

	private ZMDiary mForwardZMDiary;
	//用户id
	private long mUserId;
	//日志id
	private long mDiaryId;
	//空间id
	private long mSpaceId;
	private boolean isSpaceDiary;

	/** 编辑标题 */
	private EditText mTitleEditView;
	/** 转发日志的内容 */
	private TextView mContentView;
	private TextView mTimeView;
//	private TextView mFromView;

	private DiaryService mDiaryService;

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImeHelper.showIME(mTitleEditView);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.diary_forward_activity);
		setTopBar();
		findView();
		init();

		Intent it = getIntent();
		mUserId = it.getLongExtra(DiaryConsts.user_Id, -1);
		mDiaryId = it.getLongExtra(DiaryConsts.diary_Id, -1);
		mSpaceId = it.getLongExtra(DiaryConsts.space_id, -1);
		isSpaceDiary = mSpaceId > 0 ? true : false;

		if (isSpaceDiary) {
			mForwardZMDiary = mDiaryService.getCacheDiaryList(mSpaceId, isSpaceDiary).getData(mDiaryId);
		} else {
			mForwardZMDiary = mDiaryService.getCacheDiaryList(mUserId, isSpaceDiary).getData(mDiaryId);
		}
		if (mForwardZMDiary != null) {
			setView();
		} else {
			HaloToast.show(getApplicationContext(), R.string.load_failed);
			finish();
		}

	}

	private void init() {
		mDiaryService = DiaryService.getInstance(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 100);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public void onBackPressed() {
		ImeHelper.hideIME(mTitleEditView);
		super.onBackPressed();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == request_forward_Code) {
				UserUtils userUtils = new UserUtils(DiaryForwardActivity.this);
				userUtils.switchAcitivity(mUserId, true);
			}
		}
	}

	//--------------------------------------------------
	//event
	private OnClickListener sendTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ImeHelper.hideIME(mTitleEditView);
			String title = mTitleEditView.getText().toString().trim();
			if (TextUtils.isEmpty(title)) {
				HaloToast.show(getApplicationContext(), "标题不能为空");
			} else {
				//TODO 转发发送
				ZMDiary zmDiary = new ZMDiary();
				zmDiary.setDiaryId(mForwardZMDiary.getDiaryId());
				zmDiary.setOrginalDiaryId(mForwardZMDiary.getOrginalDiaryId());
				zmDiary.setOriginal(mForwardZMDiary.isOriginal());
//				zmDiary.setAuthor(author);
//				zmDiary.setContent("");
//				zmDiary.setDiaryId(diaryId);
//				zmDiary.setForwardCount(forwardCount);
//				zmDiary.setHtml(html);
//				zmDiary.setImageUrl(imageUrl);
//				zmDiary.setOrginalExists(exists);
//				zmDiary.setPostTime(postTime);
				zmDiary.setPrivacyStatus(DiaryPrivacyStatus.PUBLIC);
//				zmDiary.setRawAuthor(zmDiary.getRawAuthor());
//				zmDiary.setRawCreatedOn(zmDiary.getRawCreatedOn());
//				zmDiary.setStatus(status);
//				zmDiary.setSyncId(syncId);
				zmDiary.setSync(isSpaceDiary);
				zmDiary.setTitle(title);
//				zmDiary.setViewCount(viewCount);
				Intent it = new Intent(DiaryForwardActivity.this, DiaryShareActivity.class);
				it.putExtra(DiaryConsts.user_Id, mUserId);
				it.putExtra(DiaryConsts.diary_Id,
						isSpaceDiary ? mForwardZMDiary.getSyncId() : mForwardZMDiary.getDiaryId());
				it.putExtra(DiaryConsts.diary, zmDiary);
				it.putExtra(DiaryConsts.space_id, mSpaceId);
				it.putExtra(DiaryConsts.is_forward, true);
				startActivityForResult(it, request_forward_Code);
			}
		}
	};

	private void setView() {

		//如果原日志已经删除
		if (mForwardZMDiary.isOrginalExists()) {
			mContentView
					.setText(FaceHelper.getInstance(this).getThumbnailSpannableString(mForwardZMDiary.getContent()));
		} else {
			//原始内容已删除
			mContentView.setText(getText(R.string.orginal_not_exists));
		}

		long time = mForwardZMDiary.getPostTime();
		String dataStr = null;
		if (DateUtils.isToday(new Date(time))) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			dataStr = "今天" + " " + dateFormat.format(new Date(time));
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			dataStr = dateFormat.format(new Date(mForwardZMDiary.getPostTime()));
		}

		//来自:xxxxx 2012-12-5
		User author = mForwardZMDiary.getAuthor();
		String title = "";
		if (author != null) {
			title = "来自:" + author.getNickname() + "  " + dataStr;
		} else {
			title = dataStr;
		}
		mTimeView.setText(title);
	}

	private void findView() {
		mTitleEditView = (EditText) findViewById(R.id.edt_title);
		mContentView = (TextView) findViewById(R.id.txt_content);
//		mFromView = (TextView) findViewById(R.id.txt_from);
		mTimeView = (TextView) findViewById(R.id.txt_time);
	}

	private void setTopBar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ImeHelper.hideIME(mTitleEditView);
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.send);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(sendTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("转发日志");
	}
}
