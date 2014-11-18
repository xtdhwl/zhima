package com.zhima.plugin.space;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.Notice;
import com.zhima.plugin.BaseViewPlugin;

/**
 * @ClassName: NoticeViewPlugin
 * @Description: 公告插件
 * @author luqilong
 * @date 2013-1-14 上午10:42:49
 */
public class NoticeViewPlugin extends BaseViewPlugin {

	private TextView mContentView;
	private TextView mTitleView;
	private ImageView mArrowView;

	public NoticeViewPlugin(Context context) {
		super(context);
		mPluginView = View.inflate(context, R.layout.plugin_notice_view, null);
		mContentView = (TextView) mPluginView.findViewById(R.id.txt_notice_content);
		mTitleView = (TextView) mPluginView.findViewById(R.id.txt_notice_title);
		mArrowView = (ImageView) mPluginView.findViewById(R.id.img_notice_arrow);
	}

	public void setNoticeView(Notice notice) {
		mContentView.setText(notice.getTitle());
		mContentView.setTag(notice);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	public void setArrowOnClickListener(OnClickListener listener) {
		mArrowView.setOnClickListener(listener);
	}
	
	public void setNoticeOnClickListener(OnClickListener listener){
		mContentView.setOnClickListener(listener);
	}

	@Override
	public void setStyle(TypedArray typedArray) {

	}
}
