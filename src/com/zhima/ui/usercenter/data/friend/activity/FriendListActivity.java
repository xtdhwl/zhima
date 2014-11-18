package com.zhima.ui.usercenter.data.friend.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.FriendsProtocolHandler.DeleteFriendProtocol;
import com.zhima.base.protocol.FriendsProtocolHandler.GetFriendsListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.service.FriendService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.tools.UserUtils;
import com.zhima.ui.usercenter.data.friend.adapter.FriendListAdapter;

/**
 * @ClassName: MyFriendActivity
 * @Description:我的好友列表
 * @author luqilong
 * @date 2013-1-15 下午1:56:26
 */
public class FriendListActivity extends BaseActivity {

	protected static final String TAG = FriendListActivity.class.getSimpleName();

	private PullToRefreshListView mPullListView;
	private ListView mListView;

	private RefreshListData<User> mCacheFriendsList;
	private ArrayList<User> mUserLists;
	private FriendListAdapter mFriendAdapter;

	private FriendService mFriendService;
	private long mUserId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_center_data_friendlist_activity);
		setTopbar();
		findView();
		init();

		Intent it = getIntent();
		mUserId = it.getLongExtra(ACTIVITY_EXTRA, -1);

		getServiceData();
		setListener();

		checkUser(UserService.getInstance(this).isMySelf(mUserId));
	}

	//-------------------------------------------------------------------
	//init

	private void init() {
		mFriendService = FriendService.getInstance(this);
	}

	private void getServiceData() {
		//知天使频道列表
		mCacheFriendsList = mFriendService.getCacheFriendsList(mUserId);
		if (mCacheFriendsList.isEmpty()) {
			startWaitingDialog(null, R.string.loading);
			mFriendService.getFriendsList(mUserId, false, this);
		} else {
			setFriendView();
		}
	}

	//-----------------------------------------------------------------------
	//http
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_FRIENDS_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// TODO 获取好友列表
					GetFriendsListProtocol p = (GetFriendsListProtocol) protocol;
					mCacheFriendsList = p.getDataList();
					setFriendView();
					mPullListView.setLastPage(mCacheFriendsList.isLastPage());
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
		//TODO 请求服务器前
	}

	private class delectUserHttpCallBack implements IHttpRequestCallback {
		private User user;

		public delectUserHttpCallBack(User user) {
			super();
			this.user = user;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			//TODO
		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 删除好友成功
				if (protocol.isHandleSuccess()) {
					DeleteFriendProtocol p = (DeleteFriendProtocol) protocol;
					if (p.isFriendDeleted()) {
						HaloToast.show(getApplicationContext(), R.string.delete_success);
						mUserLists.remove(user);
						if (mUserLists.size() <= 0) {
							finish();
						} else {
							setFriendView();
						}
					} else {
						//删除好友失败
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
					}
				} else {
					//删除好友失败
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}
	}

	//----------------------------------------------------------------------------------
	//event

	private void setListener() {
		mPullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mFriendService.getFriendsList(mUserId, mPullListView.hasPullFromTop(), FriendListActivity.this);
			}
		});
		mListView.setOnItemClickListener(openFriendClick);
	}

	/**
	 * 进入好友空间
	 */
	private OnItemClickListener openFriendClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//TODO 进入个人空间
			User user = mFriendAdapter.getItem(position);
			if (user != null) {
				final UserUtils userutils = new UserUtils(FriendListActivity.this);
				userutils.switchAcitivity(user.getUserId(), false);
			} else {
				HaloToast.show(getApplicationContext(), R.string.load_failed);
			}
		}
	};
	/** 删除好友 */
	private OnItemLongClickListener deteleFriendClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			MessageDialog dialog = new MessageDialog(FriendListActivity.this, view);
			dialog.setTitle(R.string.dialog_title);
			dialog.setMessage(R.string.delete_msg);
			dialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					//删除好友
					User user = mFriendAdapter.getItem(position);
					if (user != null) {
						startWaitingDialog(null, R.string.loading);
						mFriendService.deleteFriend(user.getUserId(), new delectUserHttpCallBack(user));
					} else {
						HaloToast.show(getApplicationContext(), R.string.load_failed);
					}
				}

				@Override
				public void onLeftBtClick() {
				}
			});
			dialog.show();
			return false;
		}
	};

	//-------------------------------------------------------------------------
	//view

	private void setFriendView() {
		mUserLists = mCacheFriendsList.getDataList();
		if (mFriendAdapter == null) {
			mFriendAdapter = new FriendListAdapter(this, R.layout.user_center_data_friendlist_item, mUserLists);
			mListView.setAdapter(mFriendAdapter);
		} else {
			mFriendAdapter.setData(mUserLists);
			mFriendAdapter.notifyDataSetChanged();
		}
	}

	//-------------------------------------------------------------------------
	//private method
	private void checkUser(boolean bl) {
		if (bl) {
			mListView.setOnItemLongClickListener(deteleFriendClick);
		} else {
			mListView.setOnItemLongClickListener(null);
		}
	}

	private void findView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.refresh_lstv);
		mListView = mPullListView.getRefreshableView();
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

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("好友列表");
	}
}
