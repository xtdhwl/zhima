package com.zhima.ui.space.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.Joke;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.tools.HttpImageLoader;

public class JokeAdapter extends ZhimaAdapter<Joke> {

	public JokeAdapter(Context context, int layoutId, List<Joke> array) {
		super(context, layoutId, array);
	}

	@Override
	public Object createViewHolder(View view, Joke data) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		holder.mImageView = (ImageView) view.findViewById(R.id.img_joke_image);
		holder.mTitleText = (TextView) view.findViewById(R.id.txt_joke_title);
		holder.mContentText = (TextView) view.findViewById(R.id.txt_joke_content);
		holder.mDateText = (TextView) view.findViewById(R.id.txt_joke_time);
		holder.mReplyText = (RelativeLayout) view.findViewById(R.id.layout_reply);

		return holder;
	}

	@Override
	public void bindView(Joke data, int position, View view) {
		ViewHolder holder = (ViewHolder) getViewHolder(view, data);
		Joke joke = (Joke) data;
		if (joke.getTitle().trim().equals("")) {
			holder.mTitleText.setText(" " + position);
		} else {
			holder.mTitleText.setText(joke.getTitle());
		}
		holder.mTitleText.setVisibility(View.GONE);
		holder.mReplyText.setVisibility(View.GONE);
		holder.mDateText.setVisibility(View.GONE);
		HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mImageView,
				((BaseActivity) mContext).getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

		String content = joke.getContent().trim();
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(content);
		content = m.replaceAll("");
		if ("".equals(content)) {
			holder.mContentText.setText("无内容" + position);
		} else {
			holder.mContentText.setText(content + "...");
		}
		holder.mContentText.setLines(3);
	}

	class ViewHolder {
		ImageView mImageView;
		TextView mTitleText;
		TextView mContentText;
		TextView mDateText;
		RelativeLayout mReplyText;
	}
}
