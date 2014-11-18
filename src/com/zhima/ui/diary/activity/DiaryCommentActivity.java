package com.zhima.ui.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.DiaryProtocolHandler.ReplyDiaryProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.utils.ImeHelper;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.service.DiaryService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.FaceAdapter;
import com.zhima.ui.common.view.CircleFlowIndicator;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.FaceHelper.FaceHolder;

/**
 * @ClassName: DiaryCommentActivity
 * @Description: 日志评论
 * @author luqilong
 * @date 2013-1-19 下午9:46:05
 */
public class DiaryCommentActivity extends BaseActivity {
	private static final int MAX_LENGTH = 140;
	private ZMDiary mZMDiary;
	//用户id
	private long mUserId;
	//日志id
	private long mDiaryId;
	//空间id
	private long mSpaceId;
	private boolean isSpaceDiary;

	private EditText mContentView;
	private TextView mPromptView;
	private ImageView mFaceView;
	private ViewFlow mViewFlow;
	private ViewGroup mFaceLayout;

	private DiaryService mDiaryService;

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showIME(mContentView);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.diary_comment_activity);
		setTopbar();
		findView();
		init();

		Intent it = getIntent();
		mUserId = it.getLongExtra(DiaryConsts.user_Id, -1);
		mDiaryId = it.getLongExtra(DiaryConsts.diary_Id, -1);
		mSpaceId = it.getLongExtra(DiaryConsts.space_id, -1);
		isSpaceDiary = mSpaceId > 0 ? true : false;

		if (isSpaceDiary) {
			mZMDiary = mDiaryService.getCacheDiaryList(mSpaceId, isSpaceDiary).getData(mDiaryId);
		} else {
			mZMDiary = mDiaryService.getCacheDiaryList(mUserId, isSpaceDiary).getData(mDiaryId);
		}

		if (mZMDiary != null) {
			//
		} else {
			HaloToast.show(getApplicationContext(), R.string.load_failed);
			finish();
		}
	}

	private void init() {
//		mUserService = UserService.getInstance(this);
		mDiaryService = DiaryService.getInstance(this);
	}

	//--------------------------------------------------
	//http

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.COMMENT_DIARY_PROTOCOL) {
				// 发表评论
				if (protocol.isHandleSuccess()) {
					ReplyDiaryProtocol p = (ReplyDiaryProtocol) protocol;
					HaloToast.show(getApplicationContext(), "评论成功");
					setResult(RESULT_OK);
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
	}

	//-------------------------------------------------------------
	@Override
	public void onResume() {
		super.onResume();
		mhandler.sendEmptyMessageDelayed(0, 100);
	}

	@Override
	public void onBackPressed() {
		if (mFaceLayout.getVisibility() != View.GONE) {
			mFaceLayout.setVisibility(View.GONE);
		} else {
			ImeHelper.hideIME(mContentView);
			super.onBackPressed();
		}
	}

	//------------------------------------------------------------
	//event

	/** 发送 */
	private OnClickListener sendTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (checkText()) {
				ImeHelper.hideIME(mContentView);
				startWaitingDialog(null, R.string.loading);
				mDiaryService.commentDiary(mDiaryId, mContentView.getText().toString(), isSpaceDiary,
						DiaryCommentActivity.this);
			}
		}
	};

	/**
	 * 滑动发生改变
	 */
	private OnTouchListener contentTouchlistener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mFaceLayout.getVisibility() != View.GONE) {
				mFaceLayout.setVisibility(View.GONE);
			}
			return false;
		}
	};

	private TextWatcher textChangedListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			// 字符输入个数提示
			mPromptView.setText(String.format(getString(R.string.input_prompt), MAX_LENGTH - s.length()));
		}
	};

	//------------------------------------------------------------------
	//处理表情显示隐藏
	/** 表情点击事件 */
	private View.OnClickListener faceOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//当mContentView获取到焦点
			FaceHolder face = (FaceHolder) v.getTag();
			String symbol = face.getSymbol();
			SpannableString span = new SpannableString(symbol);
			span.setSpan(new ImageSpan(face.getBitmap()), 0, symbol.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			mContentView.getText().insert(mContentView.getSelectionStart(), span);
		}
	};
	/***
	 * 点击表情
	 */
	private OnClickListener faceClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int visib = mFaceLayout.getVisibility();
			if (visib == View.GONE) {
				showFace(mContentView);
			} else {
				showIME(mContentView);
			}
		}
	};

	private void showIME(View view) {
		if (mFaceLayout.getVisibility() != View.GONE) {
			mFaceLayout.setVisibility(View.GONE);
		}
		ImeHelper.showIME(mContentView);
	}

	private void showFace(View view) {
		ImeHelper.hideIME(view);
		//这是为了解决。闪屏问题
		mFaceLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mFaceLayout.setVisibility(View.VISIBLE);
			}
		}, 100);
	}

	private boolean checkText() {
		//是否输入内容
		if (TextUtils.isEmpty(mContentView.getText().toString().trim())) {
			HaloToast.show(getApplicationContext(), "没有输入内容!");
			return false;
		}
		return true;
	}

	private void findView() {
		mContentView = (EditText) findViewById(R.id.edt_content);
		mPromptView = (TextView) findViewById(R.id.txt_prompt);
		mFaceView = (ImageView) findViewById(R.id.img_face);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mFaceLayout = (ViewGroup) findViewById(R.id.layout_face);

		//添加表情
		FaceAdapter faceAdapter = new FaceAdapter(this, FaceHelper.getInstance(this).getFaceHolderList());
		faceAdapter.setOnClickListener(faceOnClickListener);
		mViewFlow.setAdapter(faceAdapter);
		CircleFlowIndicator indicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(indicator);

		mFaceView.setOnClickListener(faceClick);
		mContentView.setOnTouchListener(contentTouchlistener);
		mContentView.addTextChangedListener(textChangedListener);
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
				ImeHelper.hideIME(mContentView);
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.send);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(sendTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("日志评论");
	}
}
