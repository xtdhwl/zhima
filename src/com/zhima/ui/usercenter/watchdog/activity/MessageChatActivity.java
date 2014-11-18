package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.InboxProtocolHandler.GetFriendTalkListByUserProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.MessageEntry;
import com.zhima.data.model.PositiveOrderComparator;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.InboxService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaEditView;
import com.zhima.ui.common.view.ZhimaMenuItem;
import com.zhima.ui.common.view.ZhimaPopupMenu;
import com.zhima.ui.common.view.ZhimaPopupMenu.OnMenuItemClickListener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.tools.TemporaryStorage;
import com.zhima.ui.usercenter.watchdog.adapter.MessageChatAdapter;
import com.zhima.ui.usercenter.watchdog.adapter.MessageChatAdapter.OnContentClickListener;
import com.zhima.ui.usercenter.watchdog.adapter.MessageChatAdapter.OnPhotoClickListener;

/**
 * @ClassName MessageInfoActivity
 * @Description 站内信对话界面
 * @author jiang
 * @date 2013-1-21 下午04:40:58
 */
public class MessageChatActivity extends BaseActivity implements OnClickListener {

	/** 最大输入文字个数 */
	protected static final int MAX_TEXT_NUM = 140;
	/** 好友的id */
	private long friendId;

	private PullToRefreshListView mPullUpView;

	/** 输入内容的空间 */
	private ZhimaEditView editView;
	private EditText mContentText;
	/** 表情布局 */
	private ViewGroup mFaceGroup;
	private Button sendButton;
	private ListView chatListView;
	private List<MessageEntry> chatList = null;
	private MessageChatAdapter chatAdapter = null;
	private RefreshListData<MessageEntry> mRefreshListData;

	private InboxService mInboxService;
	private Comparator<MessageEntry> mComp;

	private long timestamp;

	/** 临时存储服务 */
	private TemporaryStorage mStorage;
	/** 光标位置 */
	private int selectionStart;

	private String chatFromName = "";

	// private Handler handler = new Handler();
	// private Runnable runnable = new Runnable() {
	// public void run() {
	// this.update();
	// handler.postDelayed(this, 1000 * 120);// 间隔120秒
	// }
	//
	// void update() {
	// // 刷新msg的内容
	// mInboxService.getFriendTalkListByUser(friendId, timestamp, false,
	// MessageChatActivity.this);
	// }
	// };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		setContentView(R.layout.user_center_message_chat_activity);

		friendId = getIntent().getLongExtra(ACTIVITY_EXTRA, 0);
		chatFromName = getIntent().getStringExtra(ACTIVITY_EXTRA2);
		findView();
		setTopbar();
		init();

	}

	/**
	 * @Title: send
	 * @Description:发送消息
	 * @param
	 * @return void
	 */
	private void send(String content) {
		if (!content.equals("")) {
			// 发送消息
			mInboxService.addFriendTalk(friendId, content, this);
		} else {
			HaloToast.show(MessageChatActivity.this, "内容不能为空");
		}
	}

	/**
	 * @Title: findView
	 * @Description:实例化view
	 * @param
	 * @return void
	 */
	private void findView() {
		editView = (ZhimaEditView) findViewById(R.id.edit_view);
		mContentText = editView.getEditText();
		mFaceGroup = editView.getFaceGroup();
		sendButton = editView.getSendBtn();

		mPullUpView = (PullToRefreshListView) findViewById(R.id.chat_listview);
		chatListView = mPullUpView.getRefreshableView();
	}

	/**
	 * @Title: getServiceData
	 * @Description:初始化对话列表数据
	 * @param
	 * @return void
	 */
	private void getServiceData() {
		mRefreshListData = mInboxService.getCacheFriendTalkListByUser(friendId);
		if (mRefreshListData.isEmpty()) {
			mInboxService.getFriendTalkListByUser(friendId, 0, true, this);
		} else {
			setListView(mRefreshListData.getDataList());
		}
	}

	/**
	 * @Title: init
	 * @Description:初始化一些变量
	 * @param
	 * @return void
	 */
	private void init() {

		mInboxService = InboxService.getInstance(this);
		// 标记已读
		mInboxService.markReadLLFriendTalkByUser(friendId, this);
		// 初始化正序比较器
		mComp = new PositiveOrderComparator<MessageEntry>();
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send(mContentText.getText().toString());
			}
		});

		mPullUpView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mInboxService.getFriendTalkListByUser(friendId, 0, !mPullUpView.hasPullFromTop(),
						MessageChatActivity.this);
			}

		});
		mPullUpView.setRefreshingLabel("正在加载");
		getServiceData();
		mStorage = TemporaryStorage.getInstance();
		mContentText.setText(mStorage.getText());
		// 定时刷新
		// handler.postDelayed(runnable, 1000 * 10);
	}

	/**
	 * @Title: setListView
	 * @Description:设置列表的显示
	 * @param
	 * @return void
	 */
	private void setListView(ArrayList<MessageEntry> dataList) {
		Collections.sort(dataList, mComp);
		chatList = dataList;
		if (chatAdapter == null) {
			chatAdapter = new MessageChatAdapter(this, chatList);
			chatListView.setAdapter(chatAdapter);
			chatAdapter.setOnContentClickListener(new OnContentClickListener() {

				@Override
				public void onContentClick(long targetId, View view, String text) {
					// TODO Auto-generated method stub
					final long mId = targetId;
					final String mText = text;
					final ZhimaPopupMenu popupMenu = new ZhimaPopupMenu(MessageChatActivity.this);
					popupMenu.setMenuItems(R.menu.delete);
					popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public void onMenuItemClick(ZhimaMenuItem item, int position) {
							// TODO Auto-generated method stub
							switch (item.getId()) {
							case R.id.delete:
								mInboxService.deleteFriendTalkByUser(friendId, mId, MessageChatActivity.this);
								break;
							case R.id.copy:
								ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
								clipboard.setText(mText);
								HaloToast.show(MessageChatActivity.this, getText(R.string.clipboard_msg).toString());
								break;
							}
						}
					});
					popupMenu.show(view);
				};
			});
			chatAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {

				@Override
				public void onPhotoClick() {
					// TODO Auto-generated method stub

				}
			});
		} else {
			chatAdapter.setNewData(chatList);
			chatAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * @Title: setTopbar
	 * @Description:设置topbar
	 * @param
	 * @return void
	 */
	private void setTopbar() {
		// TODO Auto-generated method stub
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

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("正在与" + chatFromName + "聊天");
	}

	@Override
	public void onBackPressed() {
		int visib = mFaceGroup.getVisibility();
		if (visib == View.VISIBLE) {
			mFaceGroup.setVisibility(View.GONE);
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_FRIENDTALK_LIST_BY_USER_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//
					GetFriendTalkListByUserProtocol p = (GetFriendTalkListByUserProtocol) protocol;
					mRefreshListData = p.getDataList();
					setListView(mRefreshListData.getDataList());
					if (!mRefreshListData.getDataList().isEmpty()) {
						timestamp = mRefreshListData.getDataList().get(0).getTimestamp();
					}
					mPullUpView.setLastPage(mRefreshListData.isLastPage());
				} else {

				}
				mPullUpView.onRefreshComplete(!mPullUpView.hasPullFromTop());
			} else if (protocol.getProtocolType() == ProtocolType.DELETE_FRIEND_TALK_BY_USER_PROTOCOL) {
				mInboxService.getFriendTalkListByUser(friendId, 0, true, this);
			} else if (protocol.getProtocolType() == ProtocolType.ADD_FRIEND_TALK_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					mInboxService.getFriendTalkListByUser(friendId, 0, true, this);
					mContentText.setText("");
				}
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullUpView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 发送前

	}

	@Override
	public void onPause() {
		super.onPause();
		mStorage.setText(mContentText.getText());
		// 保存光标位置
		selectionStart = mContentText.getSelectionStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		mContentText.setText(mStorage.getText());
		if (mContentText.getText().toString().length() >= selectionStart) {
			mContentText.setSelection(selectionStart);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// handler.removeCallbacks(runnable); //停止刷新
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
