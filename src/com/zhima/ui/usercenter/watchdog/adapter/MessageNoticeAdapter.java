package com.zhima.ui.usercenter.watchdog.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.MessageStatus;
import com.zhima.base.consts.ZMConsts.MessageType;
import com.zhima.data.model.MessageEntry;
import com.zhima.ui.adapter.ZhimaAdapter;

/**
 * @ClassName MessageNoticeAdapter
 * @Description 公告、通知、好友申请的listview的adapter
 * @author jiang
 * @date 2013-1-24 下午09:50:57
 */
public class MessageNoticeAdapter extends ZhimaAdapter<MessageEntry> {
	private OnLayoutLongClickListener mOnLayoutLongClickListener;

	public MessageNoticeAdapter(Context context, int layoutId, List<MessageEntry> array) {
		super(context, layoutId, array);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object createViewHolder(View view, MessageEntry data) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		holder.mImageView = (ImageView) view.findViewById(R.id.message_notice_icon);
		holder.mDateText = (TextView) view.findViewById(R.id.message_notice_time);
		holder.mContentText = (TextView) view.findViewById(R.id.message_notice_content);
		holder.mAgreeBtn = (Button) view.findViewById(R.id.btn_message_notice_agree);
		holder.mDeclineBtn = (Button) view.findViewById(R.id.btn_message_notice_decline);
		holder.mRLayout = (RelativeLayout) view.findViewById(R.id.message_notice_rl);
		return holder;
	}

	@Override
	public void bindView(MessageEntry data, int position, View view) {
		// TODO Auto-generated method stub
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		if (data.getMessageType().equals(MessageType.FRIEND_REQUEST)) {
			holder.mImageView.setImageResource(R.drawable.message_default_avatar);
		} else if (data.getMessageType().equals(MessageType.NOTICE)) {
			holder.mImageView.setImageResource(R.drawable.message_chimark);
		} else if (data.getMessageType().equals(MessageType.SYSTEM)) {
			holder.mImageView.setImageResource(R.drawable.message_sys_notify);
		}
		holder.mDateText.setText(new SimpleDateFormat("MM-dd HH:mm").format(data.getTimestamp()));
		holder.mContentText.setText(data.getContent());

		if (data.getMessageType().equals(MessageType.FRIEND_REQUEST) && data.getMessageStatus() == MessageStatus.UNREAD) {
			holder.mAgreeBtn.setVisibility(View.VISIBLE);
			holder.mDeclineBtn.setVisibility(View.VISIBLE);
		} else {
			holder.mAgreeBtn.setVisibility(View.GONE);
			holder.mDeclineBtn.setVisibility(View.GONE);
		}
		final MessageEntry entry = data;
		holder.mRLayout.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				mOnLayoutLongClickListener.onLayoutLongClick(v, entry);
				return false;
			}
		});
		bindClicker(data, holder.mAgreeBtn);
		bindClicker(data, holder.mDeclineBtn);
		bindClicker(data, holder.mRLayout);

	}

	class ViewHolder {
		ImageView mImageView;
		TextView mDateText;
		TextView mContentText;
		Button mDeclineBtn;
		Button mAgreeBtn;
		RelativeLayout mRLayout;
	}

	public void setOnLayoutLongClickListener(OnLayoutLongClickListener listener) {
		mOnLayoutLongClickListener = listener;
	}

	public static interface OnLayoutLongClickListener {
		public void onLayoutLongClick(View view, MessageEntry messageEntry);
	}

}
