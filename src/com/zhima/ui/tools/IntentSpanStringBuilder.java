package com.zhima.ui.tools;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.zhima.R;

/**
 * @ClassName: IntentSpanStringBuilder
 * @Description: IntentSpanString构造器
 * @author liubingsr
 * @date 2013-1-22 下午8:09:14
 * 
 */
public class IntentSpanStringBuilder {
	ArrayList<SpanStruct> mSpanList = new ArrayList<SpanStruct>();
	ArrayList<ClickSpanStruct> mClickSpanList = new ArrayList<ClickSpanStruct>();
	//TODO 设置默认颜色
	private final static int DEFALUT_COLOR = R.color.couples_cardview_bg_color;

	public class SpanStruct {
		public int startPos;
		public int endPos;
		public Intent it;
	}

	public class ClickSpanStruct {
		public int startPos;
		public int endPos;
		public ClickableSpan clicker;
	}

	public void addSpan(int startPos, int endPos, Intent it) {
		SpanStruct span = new SpanStruct();
		span.startPos = startPos;
		span.endPos = endPos;
		span.it = it;
		mSpanList.add(span);
	}

	public void addClickSpan(int startPos, int endPos, ClickableSpan clicker) {
		ClickSpanStruct span = new ClickSpanStruct();
		span.startPos = startPos;
		span.endPos = endPos;
		span.clicker = clicker;
		mClickSpanList.add(span);
	}

	public SpannableString getSpanString(Context context, String str, int color, boolean isBold) {
		SpannableString spanString = new SpannableString(str);
		for (SpanStruct span : mSpanList) {
			spanString.setSpan(new IntentSpan(context, span.it), span.startPos, span.endPos,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			if (isBold) {
				spanString.setSpan(new StyleSpan(Typeface.BOLD), span.startPos, span.endPos,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (color == -1) {
				spanString.setSpan(new ForegroundColorSpan(DEFALUT_COLOR), span.startPos, span.endPos,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				spanString.setSpan(new ForegroundColorSpan(color), span.startPos, span.endPos,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

		}
		return spanString;
	}

	public SpannableString getClickSpanString(Context context, String str, int color, boolean isBold) {
		SpannableString spanString = new SpannableString(str);
		for (ClickSpanStruct span : mClickSpanList) {
			spanString.setSpan(span.clicker, span.startPos, span.endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			if (isBold) {
				spanString.setSpan(new StyleSpan(Typeface.BOLD), span.startPos, span.endPos,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (color == -1) {
				spanString.setSpan(new ForegroundColorSpan(DEFALUT_COLOR), span.startPos, span.endPos,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				spanString.setSpan(new ForegroundColorSpan(color), span.startPos, span.endPos,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

		}
		return spanString;
	}

	/**
	 * @Title: createIntentSpanString
	 * @Description: 创建可点击跳转的SpannableString
	 * @param rawStr 原始字符串
	 * @param dest 要创建点击事件的字符串
	 * @param color 链接文字颜色（-1为默认颜色）
	 * @param isBold 是为粗体
	 * @param it 点击链接后跳转到的activity
	 * @return SpannableString
	 */
	public static SpannableString createIntentSpanString(Context context, String rawStr, String dest, int color,
			boolean isBold, Intent it) {
		SpannableString spanString = new SpannableString(rawStr);
		if (TextUtils.isEmpty(dest) || it == null) {
			return spanString;
		}
		IntentSpanStringBuilder builder = new IntentSpanStringBuilder();
		int destLen = dest.length();
		int start = 0;
		int pos = rawStr.indexOf(dest, start);
		while (pos >= 0) {
			start = pos + destLen;
			builder.addSpan(pos, start, it);
			pos = rawStr.indexOf(dest, start);
		}
		return builder.getSpanString(context, rawStr, color, isBold);
	}

	public static SpannableString createClickSpanString(Context context, String rawStr, String dest, int color,
			boolean isBold, ClickableSpan span) {
		SpannableString spanString = new SpannableString(rawStr);
		if (TextUtils.isEmpty(dest) || span == null) {
			return spanString;
		}
		IntentSpanStringBuilder builder = new IntentSpanStringBuilder();
		int destLen = dest.length();
		int start = 0;
		int pos = rawStr.indexOf(dest, start);
		while (pos >= 0) {
			start = pos + destLen;
			builder.addClickSpan(pos, start, span);
			pos = rawStr.indexOf(dest, start);
		}
		return builder.getClickSpanString(context, rawStr, color, isBold);
	}

	public static Intent createUrlIntent(Context context, String url) {
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		Uri uri = Uri.parse(url);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		return it;
	}
}
