package com.zhima.ui.diary.controller;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;

import com.zhima.R;
import com.zhima.data.model.ZMDiaryReply;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.FaceHelper;
import com.zhima.ui.tools.IntentSpanStringBuilder;
import com.zhima.ui.tools.UserUtils;

/**
 * @ClassName: ReplyHelper
 * @Description:处理回复内容字符串
 * @author luqilong
 * @date 2013-1-23 上午10:16:53
 */
public class ReplyHelper {

	public final static String DELIMITER_BEGIN = "@";

	public static SpannableString getReplySpannableString(Context context, ZMDiaryReply data) {
		String replyStr = ReplyHelper.getReplyContent(data);
		String replyName = ReplyHelper.getReplyUserNickName(data);

		SpannableString createIntentSpanString = IntentSpanStringBuilder.createClickSpanString(context, replyStr,
				replyName, context.getResources().getColor(R.color.blue_link), true,
				getClickableSpan((BaseActivity) context, data));
		SpannableString faceSpannable = FaceHelper.getInstance(context).getThumbnailSpannableString(
				createIntentSpanString);
		return faceSpannable;
	}

	private static String getReplyContent(ZMDiaryReply data) {
		return DELIMITER_BEGIN + data.getRepliedUserNickname() + ":" + data.getContent();
	}

	private static String getReplyUserNickName(ZMDiaryReply data) {
		return DELIMITER_BEGIN + data.getRepliedUserNickname();
	}

	private static ClickableSpan getClickableSpan(BaseActivity activity, ZMDiaryReply reple) {
		return new MyClickableSpan(activity, reple);
	}

	private static class MyClickableSpan extends ClickableSpan {

		private BaseActivity activity;
		private ZMDiaryReply reple;

		public MyClickableSpan(BaseActivity activity, ZMDiaryReply reple) {
			super();
			this.activity = activity;
			this.reple = reple;
		}

		@Override
		public void onClick(View widget) {

			long replyUserId = reple.getRepliedUserId();
			UserUtils userutils = new UserUtils(activity);
			userutils.switchAcitivity(replyUserId, true);
		}

	}
}
