package com.zhima.ui.usercenter.watchdog.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.MessageType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.UpdateFriendRequsetStatus;
import com.zhima.base.protocol.InboxProtocolHandler.GetMessageListByTypeProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.MessageEntry;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.UserRecordEntry;
import com.zhima.data.service.InboxService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.usercenter.watchdog.adapter.MessageNoticeAdapter;
import com.zhima.ui.usercenter.watchdog.adapter.MessageNoticeAdapter.OnLayoutLongClickListener;

/**
 * @ClassName MessageNoticeActivity
 * @Description 公告、通知、好友申请 展示的activity
 * @author jiang
 * @date 2013-1-24 下午10:08:26
 */
public class MessageNoticeActivity extends BaseActivity {

	/** 消息类型 */
	private String messageType;

	private PullToRefreshListView mPullUpView;
	private ListView noticeListView;
	private List<MessageEntry> chatList = null;
	private MessageNoticeAdapter mMessageNoticeAdapter = null;
	private RefreshListData<MessageEntry> mRefreshListData;

	private InboxService mInboxService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_message_notice_activity);

		messageType = getIntent().getStringExtra(ACTIVITY_EXTRA);
		findView();
		setTopbar();
		init();

	}

	/**
	 * @Title: findView
	 * @Description:实例化view
	 * @param
	 * @return void
	 */
	private void findView() {
		mPullUpView = (PullToRefreshListView) findViewById(R.id.chat_listview);
		noticeListView = mPullUpView.getRefreshableView();
	}

	/**
	 * @Title: getServiceData
	 * @Description:初始化对话列表数据
	 * @param
	 * @return void
	 */
	private void getServiceData() {
		mRefreshListData = mInboxService.getCacheMessageListByType(messageType);
		if (mRefreshListData.isEmpty()) {
			mInboxService.getMessageListByType(messageType, true, this);
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
		if(messageType != MessageType.FRIEND_REQUEST){
			mInboxService.markReadAllMessageByType(messageType, this);
		}
		mPullUpView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mInboxService.getMessageListByType(messageType, mPullUpView.hasPullFromTop(),
						MessageNoticeActivity.this);
			}
		});
		getServiceData();
	}

	/**
	 * @Title: showDialog
	 * @Description:弹出删除的对话框
	 * @param position
	 *            被点击的item的position
	 * @return void
	 */
	private void showDeleteDialog(final long targetId) {
		MessageDialog dialog = new MessageDialog(this, findViewById(R.id.chat_listview));
		dialog.setTitle(R.string.dialog_title);
		dialog.setMessage(R.string.delete_msg);
		dialog.setRightBtText(R.string.delete);
		dialog.setLeftBtText(R.string.cancel);
		dialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {
				if (mRefreshListData != null) {
					if (!mRefreshListData.isEmpty()) {
						mInboxService.deleteMessageByType(messageType, targetId, MessageNoticeActivity.this);
					}
				}
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		dialog.show();
	}

	/**
	 * @Title: setListView
	 * @Description:设置列表的显示
	 * @param
	 * @return void
	 */
	private void setListView(ArrayList<MessageEntry> dataList) {
		chatList = dataList;
		if (mMessageNoticeAdapter == null) {
			mMessageNoticeAdapter = new MessageNoticeAdapter(this, R.layout.message_notice_list_item, chatList);
			noticeListView.setAdapter(mMessageNoticeAdapter);
			mMessageNoticeAdapter.setOnClickerListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (v.getTag() != null) {
						MessageEntry entry = (MessageEntry) v.getTag();
						if (v.getId() == R.id.btn_message_notice_agree) {
							if (entry.getSender() != null) {
								mInboxService.handleFriendRelationship(entry.getSender().getUserId(), entry.getId(),
										UpdateFriendRequsetStatus.AGREE, MessageNoticeActivity.this);
							}
						} else if (v.getId() == R.id.btn_message_notice_decline) {
							if (entry.getSender() != null) {
								mInboxService.handleFriendRelationship(entry.getSender().getUserId(), entry.getId(),
										UpdateFriendRequsetStatus.DECLINE, MessageNoticeActivity.this);
							}
						}
					}
					v.getTag();
				}
			});
			mMessageNoticeAdapter.setOnLayoutLongClickListener(new OnLayoutLongClickListener() {

				@Override
				public void onLayoutLongClick(View view, MessageEntry messageEntry) {
					// TODO Auto-generated method stub
					if (messageEntry != null) {
						showDeleteDialog(messageEntry.getTargetId());
					}
				}
			});
		} else {
			mMessageNoticeAdapter.notifyDataSetChanged();
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
		TextView titleText = ((TextView) mTopbar.findViewById(R.id.txt_topbar_title));
		if (messageType.equals(MessageType.FRIEND_REQUEST)) {
			titleText.setText(R.string.watchdog_friend_requst);
		} else if (messageType.equals(MessageType.NOTICE)) {
			titleText.setText(R.string.watchdog_notice);
		} else if (messageType.equals(MessageType.SYSTEM)) {
			titleText.setText(R.string.watchdog_system_message);
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_MESSAGE_LIST_BY_TYPE_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					//
					GetMessageListByTypeProtocol p = (GetMessageListByTypeProtocol) protocol;
					mRefreshListData = p.getDataList();
					setListView(mRefreshListData.getDataList());
					mPullUpView.setLastPage(mRefreshListData.isLastPage());
				} else {
					HaloToast.show(this, protocol.getProtocolErrorMessage());
				}
				mPullUpView.onRefreshComplete(mPullUpView.hasPullFromTop());
			} else if (protocol.getProtocolType() == ProtocolType.DELETE_MESSAGE_BY_TYPE_PROTOCOL
					|| protocol.getProtocolType() == ProtocolType.HANDLE_FRIEND_RELATIONSHIP_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					mInboxService.getMessageListByType(messageType, true, this);
					HaloToast.show(this, protocol.getProtocolErrorMessage());
				} else {
					HaloToast.show(this, protocol.getProtocolErrorMessage());
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

}
