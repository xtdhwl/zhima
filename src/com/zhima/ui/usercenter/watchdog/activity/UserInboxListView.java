package com.zhima.ui.usercenter.watchdog.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.MessageStatus;
import com.zhima.base.consts.ZMConsts.MessageType;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.UpdateFriendRequsetStatus;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.InboxProtocolHandler.GetMessageListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.MessageDigest;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.InboxService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomListView;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.MessageDialog;
import com.zhima.ui.common.view.MessageDialog.OnBtClickListener;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName UserInboxListView
 * @Description 个人管家inbox的view
 * @author jiang
 * @date 2013-1-21 下午03:08:41
 */
public class UserInboxListView {
	private Context mContext;
	private View mContentView;
	private CustomListView<MessageDigest> inboxListView;
	private RefreshListData<MessageDigest> refreshListDate;
	private InboxService mInboxService;
	private LinearLayout mLoadingbr;
	private int listCount;
	public static long lastTimestamp = 0;
	/** 是否最后一页 */
	private boolean isLastPage = false;
	/** 是否结束请求服务器 */
	private boolean isRequestFinish = false;

	public UserInboxListView(Context context, View view) {
		// super(context);
		this.mContext = context;
		mContentView = view;
		init();
		requestServiceData();
	}

	/**
	 * @Title: getView
	 * @Description:获取到当前的view
	 * @param
	 * @return View
	 */
	public View getView() {
		return mContentView;
	}

	/**
	 * @Title: getListItemCount
	 * @Description:获取列表条目数
	 * @param
	 * @return int
	 */
	public int getListItemCount() {
		return listCount;
	}

	/**
	 * @Title: init
	 * @Description:初始化变量和view
	 * @param
	 * @return void
	 */
	private void init() {
		mInboxService = InboxService.getInstance(mContext);
		mLoadingbr = (LinearLayout) mContentView.findViewById(R.id.inbox_view_loading);
		inboxListView = (CustomListView<MessageDigest>) mContentView.findViewById(R.id.list_inbox);
		inboxListView.setOnItemClickListener(onClick);
		inboxListView.setOnItemLongClickListener(longClick);
	}

	/**
	 * @Title: requestServiceData
	 * @Description:请求数据并填充
	 * @param
	 * @return void
	 */
	public void requestServiceData() {
		refreshListDate = mInboxService.getCacheMessageList();
		inboxListView.addLoading();
		setListBatchLoad();
		inboxListView.isLoading(false);
		showContentEmpty(false, false);
		if (refreshListDate.isEmpty()) {
			inboxListView.setVisibility(View.GONE);
			mLoadingbr.setVisibility(View.VISIBLE);
			mInboxService.getMessageList(true, callBack);
		} else {
			addCacheData();
		}
	}

	/**
	 * @Title: addCacheData
	 * @Description:添加本地缓存数据到view
	 * @param
	 * @return void
	 */
	private void addCacheData() {
		ArrayList<MessageDigest> newDataList = refreshListDate.getDataList();
		listCount = newDataList.size();
		for (int i = inboxListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
			inboxListView.mAlreadyLoadData.add(newDataList.get(i));
		}
		inboxListView.updateListView();
		inboxListView.setVisibility(View.VISIBLE);
		inboxListView.deleteLoading();
	}

	/**
	 * @Title: setListBatchLoad
	 * @Description:设置分批加载
	 * @param
	 * @return void
	 */
	private void setListBatchLoad() {
		inboxListView.setBatchLoad(new ZhimaAdapter<MessageDigest>(mContext, R.layout.user_center_inbox_list_item,
				inboxListView.mAlreadyLoadData) {

			@Override
			public Object createViewHolder(View view, MessageDigest data) {
				// TODO Auto-generated method stub
				ViewHolder holder = new ViewHolder();
				holder.mImageView = (ImageView) view.findViewById(R.id.img_inbox_item);
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_inbox_item_title);
				holder.mDateText = (TextView) view.findViewById(R.id.txt_inbox_item_time);
				holder.mContentText = (TextView) view.findViewById(R.id.txt_inbox_item_content);
				holder.mAgreeBtn = (Button) view.findViewById(R.id.btn_inbox_item_agree);
				holder.mDeclineBtn = (Button) view.findViewById(R.id.btn_inbox_item_decline);
				holder.mButtonLayout = (RelativeLayout) view.findViewById(R.id.layout_button);
				return holder;
			}

			@Override
			public void bindView(final MessageDigest data, int position, View view) {
				// TODO Auto-generated method stub
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				if (data.getMessageType().equals(MessageType.FRIEND_REQUEST)
						|| data.getMessageType().equals(MessageType.CONVERSATION)) {
					if (data.getSender() != null) {
						HttpImageLoader.getInstance(mContext).loadImage(data.getSender().getImageUrl(),
								holder.mImageView, ((BaseActivity) mContext).getActivityId(),
								R.drawable.message_default_avatar, ImageScaleType.SMALL);
					} else {
						holder.mImageView.setImageResource(R.drawable.message_default_avatar);
					}

				} else if (data.getMessageType().equals(MessageType.NOTICE)) {
					holder.mImageView.setImageResource(R.drawable.message_chimark);
				} else if (data.getMessageType().equals(MessageType.SYSTEM)) {
					holder.mImageView.setImageResource(R.drawable.message_sys_notify);
				}
				String title = "";
				if (data.getSender() != null) {
					title = data.getSender().getNickname();
				}
				holder.mTitleText.setText(title);
				holder.mDateText.setText(new SimpleDateFormat("MM-dd HH:mm").format(data.getTimestamp()));
				holder.mContentText.setText(data.getContent());

				if (data.getMessageType().equals(MessageType.FRIEND_REQUEST)
						&& data.getMessageStatus() == MessageStatus.UNREAD) {
					holder.mButtonLayout.setVisibility(View.VISIBLE);
				} else {
					holder.mButtonLayout.setVisibility(View.GONE);
				}
				// 同意事件
				holder.mAgreeBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mInboxService.handleFriendRelationship(data.getSender().getUserId(), data.getMessageId(),
								UpdateFriendRequsetStatus.AGREE, callBack);
					}
				});
				// 拒绝事件
				holder.mDeclineBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mInboxService.handleFriendRelationship(data.getSender().getUserId(), data.getMessageId(),
								UpdateFriendRequsetStatus.DECLINE, callBack);
					}
				});

			}

			@Override
			public void getFirstData() {
				// TODO Auto-generated method stub
			}

			@Override
			public void getData() {
				// TODO Auto-generated method stub
				if (isLastPage) {
					if (!isRequestFinish) {
						HaloToast.show(mContext, R.string.no_more_data);
					}
					isRequestFinish = true;
					return;
				}
				inboxListView.isLoading(true);
				inboxListView.addLoading();

				mInboxService.getMessageList(false, callBack);
			}

			class ViewHolder {
				ImageView mImageView;
				TextView mTitleText;
				TextView mDateText;
				TextView mContentText;
				Button mDeclineBtn;
				Button mAgreeBtn;
				RelativeLayout mButtonLayout;
			}

		});
	}

	/**
	 * @Title: showDialog
	 * @Description:弹出删除的对话框
	 * @param position
	 *            被点击的item的position
	 * @return void
	 */
	private void showDialog(final int position) {
		MessageDialog dialog = new MessageDialog(mContext, mContentView);
		dialog.setTitle(R.string.dialog_title);
		dialog.setMessage(R.string.delete_msg);
		dialog.setRightBtText(R.string.delete);
		dialog.setLeftBtText(R.string.cancel);
		dialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {
				if (refreshListDate != null) {
					if (!refreshListDate.isEmpty()) {
						MessageDigest messageDigest = refreshListDate.getDataList().get(position);
						if (messageDigest.getMessageType().equals(MessageType.CONVERSATION)) {
							mInboxService.deleteAllFriendTalkByUser(messageDigest.getSender().getUserId(), callBack);
						} else {
							mInboxService.deleteAllMessageByType(messageDigest.getMessageType(), callBack);
						}
					}
				}
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		dialog.show();
	}

	/** item事件的点击监听 */
	private OnItemClickListener onClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			// TODO Auto-generated method stub
			if (refreshListDate != null) {
				if (!refreshListDate.isEmpty()) {
					if (position >= 0 && position < refreshListDate.getDataList().size()) {
						MessageDigest messageDigest = refreshListDate.getDataList().get(position);
						if (messageDigest.getMessageType().equals(MessageType.CONVERSATION)) {
							Intent intent = new Intent(mContext, MessageChatActivity.class);
							intent.putExtra(BaseActivity.ACTIVITY_EXTRA, messageDigest.getSender().getUserId());
							intent.putExtra(BaseActivity.ACTIVITY_EXTRA2, messageDigest.getSender().getNickname());

							mContext.startActivity(intent);
						} else {
							Intent intent = new Intent(mContext, MessageNoticeActivity.class);
							intent.putExtra(BaseActivity.ACTIVITY_EXTRA, messageDigest.getMessageType());
							mContext.startActivity(intent);
						}
					}
				}
			}
		}
	};

	/** item的长按响应 */
	private OnItemLongClickListener longClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
			// TODO Auto-generated method stub
			showDialog(position);
			return false;
		}
	};

	/** 服务器请求的回调 */
	private IHttpRequestCallback callBack = new IHttpRequestCallback() {

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub
			isLastPage = false;
			isRequestFinish = false;
		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub
			if (protocol.isHttpSuccess()) {
				if (protocol.getProtocolType() == ProtocolType.GET_MESSAGE_LIST_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						GetMessageListProtocol p = (GetMessageListProtocol) protocol;
						if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
							inboxListView.deleteLoading();
							inboxListView.updateListView();
							isLastPage = true;
						} else {
							refreshListDate = p.getDataList();
							ArrayList<MessageDigest> newDataList = refreshListDate.getDataList();
							listCount = newDataList.size();
							((WatchdogMainActivity) mContext).setResultCount(listCount);
							for (int i = inboxListView.mAlreadyLoadData.size(); i < newDataList.size(); i++) {
								inboxListView.mAlreadyLoadData.add(newDataList.get(i));
								if (i != 0 && i == newDataList.size() - 1) {
									lastTimestamp = newDataList.get(i).getTimestamp();
								}
							}
							inboxListView.updateListView();

							if (p.getDataList().isLastPage()) {
								inboxListView.deleteLoading();
								isLastPage = true;
							}
						}

					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
						inboxListView.deleteLoading();
					}
					if (inboxListView.mAlreadyLoadData.isEmpty()) {
						showContentEmpty(true, false);
					} else {
						showContentEmpty(false, false);
					}
				} else if (protocol.getProtocolType() == ProtocolType.DELETE_ALL_MESSAGE_BY_TYPE_PROTOCOL
						|| protocol.getProtocolType() == ProtocolType.DELETE_ALL_FRIEND_TALK_BY_USER_PROTOCOL
						|| protocol.getProtocolType() == ProtocolType.HANDLE_FRIEND_RELATIONSHIP_PROTOCOL) {
					if (protocol.isHandleSuccess()) {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
						inboxListView.mAlreadyLoadData.clear();
						inboxListView.addLoading();
						mInboxService.getMessageList(true, callBack);
					} else {
						HaloToast.show(mContext, protocol.getProtocolErrorMessage());
					}
				}

			} else {
				inboxListView.deleteLoading();
				if (inboxListView.mAlreadyLoadData.isEmpty()) {
					showContentEmpty(true, true);
				} else {
					showContentEmpty(false, false);
				}
			}
			mLoadingbr.setVisibility(View.GONE);
			inboxListView.setVisibility(View.VISIBLE);
			inboxListView.isLoading(false);
			inboxListView.setVisibility(View.VISIBLE);
		}
	};

	/**
	 * @Title: showContentEmpty
	 * @Description:如果数据为空的时候，显示提示
	 * @param bl
	 *            是否显示提示 currentViewType 当前view的类型
	 * @return void
	 */
	private void showContentEmpty(boolean bl, boolean noNet) {
		TextView txt = (TextView) mContentView.findViewById(R.id.inbox_txt_empty);
		txt.setVisibility(bl ? View.VISIBLE : View.GONE);
		if (noNet) {
			txt.setText(R.string.myzhima_request_failed);
		} else {
			txt.setText(R.string.no_inbox_data);
		}

		inboxListView.setVisibility(bl ? View.GONE : View.VISIBLE);
	}
}
