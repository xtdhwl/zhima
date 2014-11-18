package com.zhima.ui.usercenter.watchdog.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.MessageEntry;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName MessageChatAdapter
 * @Description 站内信对话adapter
 * @author jiang
 * @date 2013-1-24 下午04:22:57
 */
public class MessageChatAdapter extends BaseAdapter {
	private Context context = null;
	private List<MessageEntry> mChatList = null;
	private LayoutInflater inflater = null;
	private OnContentClickListener mOnContentClickListener;
	private OnPhotoClickListener mOnPhotoClickListener;
	private int COME_MSG = 0;
	private int TO_MSG = 1;

	public MessageChatAdapter(Context context, List<MessageEntry> chatList) {
		this.context = context;
		this.mChatList = chatList;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return mChatList.size();
	}

	@Override
	public Object getItem(int position) {
		return mChatList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		// 区别两种view的类型，标注两个不同的变量来分别表示各自的类型
		MessageEntry entity = mChatList.get(position);
		if (entity.getSender() != null) {
			return COME_MSG;
		} else {
			return TO_MSG;
		}
	}

	@Override
	public int getViewTypeCount() {
		// 这个方法默认返回1，如果希望listview的item都是一样的就返回1，我们这里有两种风格，返回2
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatHolder chatHolder = null;
		if (convertView == null) {
			chatHolder = new ChatHolder();
			if (mChatList.get(position).getSender() != null) {
				convertView = inflater.inflate(R.layout.chat_from_item, null);
			} else {
				convertView = inflater.inflate(R.layout.chat_to_item, null);
			}
			chatHolder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
			chatHolder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);
			chatHolder.userImageView = (ImageView) convertView.findViewById(R.id.iv_user_image);
			convertView.setTag(chatHolder);
		} else {
			chatHolder = (ChatHolder) convertView.getTag();
		}
		String date = new SimpleDateFormat("MM-dd HH:mm").format(mChatList.get(position).getTimestamp());
		chatHolder.timeTextView.setText(date);
		SpannableString contentSpan = FaceHelper.getInstance(context).getSpannableString(
				mChatList.get(position).getContent());
		chatHolder.contentTextView.setText(contentSpan);
		final long targetId = mChatList.get(position).getId();
		final String text = mChatList.get(position).getContent();
		chatHolder.contentTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnContentClickListener != null) {
					mOnContentClickListener.onContentClick(targetId, v, text);
				}
			}
		});
		if (mChatList.get(position).getSender() != null) {
			HttpImageLoader.getInstance(context).loadImage(mChatList.get(position).getSender().getImageUrl(),
					chatHolder.userImageView, ((BaseActivity) context).getActivityId(),
					R.drawable.message_default_avatar, ImageScaleType.SMALL);
			chatHolder.userImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mOnPhotoClickListener != null) {
						mOnPhotoClickListener.onPhotoClick();
					}
				}
			});
		}

		return convertView;
	}

	public void setNewData(List<MessageEntry> chatList) {
		mChatList = chatList;
	}

	/**
	 * @Title: setOnContentClickListener
	 * @Description:设置内容的点击事件
	 * @param
	 * @return void
	 */
	public void setOnContentClickListener(OnContentClickListener listener) {
		mOnContentClickListener = listener;
	}

	/**
	 * @Title: setOnPhotoClickListener
	 * @Description:设置好友头像的点击事件
	 * @param
	 * @return void
	 */
	public void setOnPhotoClickListener(OnPhotoClickListener listener) {
		mOnPhotoClickListener = listener;
	}

	public static interface OnContentClickListener {
		public void onContentClick(long targetId, View view, String text);
	}

	public static interface OnPhotoClickListener {
		public void onPhotoClick();
	}

	private class ChatHolder {
		private TextView timeTextView;
		private ImageView userImageView;
		private TextView contentTextView;
	}

}
