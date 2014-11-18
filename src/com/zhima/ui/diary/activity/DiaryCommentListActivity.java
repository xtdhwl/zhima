package com.zhima.ui.diary.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.DiaryProtocolHandler.GetDiaryReplyListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.ZMDiary;
import com.zhima.data.model.ZMDiaryReply;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.DiaryService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.diary.adapter.DiaryCommentAdapter;
import com.zhima.ui.diary.controller.DiaryConsts;
import com.zhima.ui.login.activity.LoginMainActivity;
import com.zhima.ui.tools.UserUtils;

/**
 * @ClassName: DiaryReplyListActivity
 * @Description:评论列表
 * @author luqilong
 * @date 2013-1-19 下午8:57:25
 */
public class DiaryCommentListActivity extends BaseActivity {

	protected static final String TAG = DiaryCommentListActivity.class.getSimpleName();
	private static final int request_comment_code = 1000;
//	private ZMDiary mZMDiary;
	//用户id
	private long mUserId;
	//日志id
	private long mDiaryId;
	//空间id
	private long mSpaceId;
	private boolean isSpaceDiary;

	private ZMDiary mZMDiary;

	private PullToRefreshListView mPullListView;
	private ListView mCommentListView;

	private RefreshListData<ZMDiaryReply> mCacheDiaryReplyList;
	private ArrayList<ZMDiaryReply> mDataDiaryReplyList;
	private DiaryCommentAdapter mCommentAdapter;

	private DiaryService mDiaryService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_commentlist_activity);
		setTopbar();
		findView();
		init();
		Intent it = getIntent();
		//日志用户id
		mUserId = it.getLongExtra(DiaryConsts.user_Id, -1);
		//日志id
		mDiaryId = it.getLongExtra(DiaryConsts.diary_Id, -1);
		//空间id
		mSpaceId = it.getLongExtra(DiaryConsts.space_id, -1);
		isSpaceDiary = mSpaceId > 0 ? true : false;

		getServiceData();
		setListener();
		checkUser(UserService.getInstance(this).isMySelf(mUserId));
	}

	private void init() {
		mDiaryService = DiaryService.getInstance(this);
	}

	private void setListener() {
		mPullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mDiaryService.getDiaryReplyList(mDiaryId, mPullListView.hasPullFromTop(), isSpaceDiary,
						DiaryCommentListActivity.this);
			}
		});
	}

	private void getServiceData() {
		//被评论的日志
		if (isSpaceDiary) {
			mZMDiary = mDiaryService.getCacheDiaryList(mSpaceId, isSpaceDiary).getData(mDiaryId);
		} else {
			mZMDiary = mDiaryService.getCacheDiaryList(mUserId, isSpaceDiary).getData(mDiaryId);
		}
		//评论列表
		mCacheDiaryReplyList = mDiaryService.getCacheZMDiaryReplyList(mDiaryId, isSpaceDiary);
		if (mCacheDiaryReplyList.isEmpty()) {
			startWaitingDialog(null, R.string.loading);
			mDiaryService.getDiaryReplyList(mDiaryId, isSpaceDiary, false, this);
		} else {
			setDiaryCommentView();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == request_comment_code) {
				//请求服务器数据，不应该缓存数据不然显示顺序会有问题
				//比如已经回复了5个，写用时5分钟。发送后可能已经有回复的 
				startWaitingDialog(null, R.string.loading);
				mDiaryService.getDiaryReplyList(mDiaryId, true, isSpaceDiary, this);
			}
		}
	}

	//----------------------------------------------------------------------
	//http

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_DIARY_REPLY_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//获取评论列表
					GetDiaryReplyListProtocol p = (GetDiaryReplyListProtocol) protocol;
					mCacheDiaryReplyList = p.getDataList();
					if (mCacheDiaryReplyList.isEmpty()) {
						HaloToast.show(getApplicationContext(), "还没有人留下评论");
					}
					setDiaryCommentView();
					mPullListView.setLastPage(mCacheDiaryReplyList.isLastPage());
				} else {

				}
			}
			mPullListView.onRefreshComplete(mPullListView.hasPullFromTop());
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullListView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
	}

	private class delectDiaryReplyHttpCallBack implements IHttpRequestCallback {
		private ZMDiaryReply zmDiaryReply;

		public delectDiaryReplyHttpCallBack(ZMDiaryReply zmDiaryReply) {
			super();
			this.zmDiaryReply = zmDiaryReply;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 删除日志成功
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.delete_success);
					mDataDiaryReplyList.remove(zmDiaryReply);
					if (mDataDiaryReplyList.size() <= 0) {
						finish();
					} else {
						setDiaryCommentView();
					}
					mPullListView.setLastPage(mCacheDiaryReplyList.isLastPage());
				} else {
					//删除日志失败
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
				mPullListView.onRefreshComplete(mPullListView.hasPullFromTop());
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed);
				mPullListView.setEmptyView();
			}
		}
	}

	//--------------------------------------------------------------
	//
	/**
	 * 设置日志评论列表
	 */
	private void setDiaryCommentView() {
		mDataDiaryReplyList = mCacheDiaryReplyList.getDataList();
		if (mCommentAdapter == null) {
			mCommentAdapter = new DiaryCommentAdapter(this, R.layout.diary_comment_item, mDataDiaryReplyList);
			mCommentListView.setAdapter(mCommentAdapter);
			mCommentAdapter.setOnClickerListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int id = v.getId();
					switch (id) {
					case R.id.img_photo:
						// 点击头像 --》个人详情
						ZMDiaryReply zmDiaryReply = (ZMDiaryReply) v.getTag();
						if (zmDiaryReply != null) {
							startPersonalDataActivity(zmDiaryReply);
						} else {
							HaloToast.show(getApplicationContext(), R.string.load_failed);
						}
						break;
					case R.id.img_reply:
						// 点击回复  --》回复日志
						ZMDiaryReply replyObj = (ZMDiaryReply) v.getTag();
						if (replyObj != null) {
							boolean isLogin = AccountService.getInstance(DiaryCommentListActivity.this).isLogin();
							if (isLogin) {
								Intent it = new Intent(DiaryCommentListActivity.this, DiaryReplyActivity.class);
								it.putExtra(DiaryConsts.user_Id, mUserId);
								it.putExtra(DiaryConsts.diary_Id, mDiaryId);
								it.putExtra(DiaryConsts.space_id, mSpaceId);
								it.putExtra(DiaryConsts.reply_id, replyObj.getId());
								startActivityForResult(it, request_comment_code);
							} else {
								Intent it = new Intent(DiaryCommentListActivity.this, LoginMainActivity.class);
								startActivity(it);
							}

						} else {
							HaloToast.show(getApplicationContext(), R.string.load_failed);
						}

						break;
					}
				}
			});
		} else {
			mCommentAdapter.setData(mDataDiaryReplyList);
			mCommentAdapter.notifyDataSetChanged();
		}
	}

	//------------------------------------------------------------------
	//event
	/**
	 * 删除评论
	 */
	private OnItemLongClickListener deteleCommentClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			//如果日志是本人删除评论
			//用户自己发送的评论删除
			ZMDiaryReply diaryReply = mCommentAdapter.getItem(position);
			if (diaryReply != null) {
				//评论留言id
				long zmDiaryUserId = mZMDiary.getAuthor().getId();
				if (UserService.getInstance(DiaryCommentListActivity.this).isMySelf(zmDiaryUserId)) {
					//评论是自己
					showDeleteMessageDialog(view, diaryReply);
				} else if (UserService.getInstance(DiaryCommentListActivity.this).isMySelf(mUserId)) {
					//日志是自己
					showDeleteMessageDialog(view, diaryReply);
				}
			} else {
				HaloToast.show(getApplicationContext(), R.string.load_failed);
			}
			return false;
		}
	};

	/**
	 * 回复
	 */
	private OnClickListener commentTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			//  进入回复
			Intent it = new Intent(DiaryCommentListActivity.this, DiaryCommentActivity.class);
			it.putExtra(DiaryConsts.user_Id, mUserId);
			it.putExtra(DiaryConsts.diary_Id, mDiaryId);
			it.putExtra(DiaryConsts.space_id, mSpaceId);
			startActivityForResult(it, request_comment_code);
		}
	};

	//-----------------------------------------------------
	//private method

	private void startPersonalDataActivity(ZMDiaryReply zmDiaryReply) {
		User user = UserService.getInstance(this).getMyself();
		UserUtils userUtils = new UserUtils(this);
		userUtils.switchAcitivity(user.getUserId(), false);
	}

	/**
	 * 删除评论
	 */
	protected void showDeleteMessageDialog(View view, final ZMDiaryReply zmDiaryReply) {
		MessageDialog dialog = new MessageDialog(DiaryCommentListActivity.this, view);
		dialog.setTitle(R.string.dialog_title);
		dialog.setMessage(R.string.delete_msg);

		dialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {
				//TODO 删除评论
				startWaitingDialog(null, R.string.load_failed);
				mDiaryService.deleteDiaryReply(zmDiaryReply.getId(), new delectDiaryReplyHttpCallBack(zmDiaryReply));
			}

			@Override
			public void onLeftBtClick() {
			}
		});

		dialog.show();
	}

	/**
	 * 用户权限(删除|评论)
	 */
	private void checkUser(boolean bl) {
		if (bl) {
			mCommentListView.setOnItemLongClickListener(deteleCommentClick);
		} else {
			mCommentListView.setOnItemLongClickListener(null);
		}
		refreshTopbar(bl);
	}

	private void refreshTopbar(boolean bl) {
		boolean isLogin = AccountService.getInstance(DiaryCommentListActivity.this).isLogin();
		if (isLogin) {
			ZhimaTopbar topbar = getTopbar();
			ImageView image1 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton1);
			image1.setImageResource(R.drawable.send);
			topbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(commentTopbarClick);
			topbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		}
	}

	private void findView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.refresh_lstv);
		mCommentListView = mPullListView.getRefreshableView();
//		mCommentListView.setSelector(new ColorDrawable(R.color.transparent));

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

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("日志评论列表");
	}

}
