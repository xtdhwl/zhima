package com.zhima.ui.space.activity;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetJokeProtocol;
import com.zhima.data.model.Joke;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.share.SharePopupMenu;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName JokeInfoActivity
 * @Description 随机趣闻的详情
 * @author jiang
 * @date 2012-9-21 上午11:06:32
 */
public class JokeInfoActivity extends BaseActivity {
	TextView textTitle, textTime, textContent;
	ImageView mPhotoImg;
	RefreshListData<Joke> jokeRefList;
	private Joke mJoke;
	public final static String SPACE_NAME = "spaceName";
	public final static String SPACE_ID = "spaceId";
	public final static String SPACE_HOMEPAGE = "spaceHomePage";
	private String spaceName = "";
	private String spaceId = "";
	private String spaceHomePage = "";
	private String jokeContent = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joke_info_activity);
		findView();
		setTopbar();

		Intent intent = getIntent();
		long jokeId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		spaceName = intent.getStringExtra(SPACE_NAME);
		spaceId = intent.getStringExtra(SPACE_ID);
		spaceHomePage = intent.getStringExtra(SPACE_HOMEPAGE);
		startWaitingDialog(null, R.string.loading);
		ScanningcodeService.getInstance(this).getJoke(jokeId, this);
	}

	// 预览大图
	private View.OnClickListener previewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mJoke != null) {
				Intent it = new Intent(JokeInfoActivity.this, PreviewActivity.class);
				it.putExtra(PreviewActivity.ACTIVITY_URL, mJoke.getImageUrl());
				startActivity(it);
			}
		}
	};

	private void initView(Joke joke) {
		textTitle.setText(joke.getTitle());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateStr = dateFormat.format(joke.getCreatedTime());
		textTime.setText(dateStr);
		textContent.setText(joke.getContent());
		if (joke.getImageUrl() != null) {
			HttpImageLoader.getInstance(this).loadImage(joke.getImageUrl(), mPhotoImg, this.getActivityId(),
					R.drawable.default_image, ImageScaleType.SMALL);
			mPhotoImg.setVisibility(View.VISIBLE);
			mPhotoImg.setOnClickListener(previewClick);
		} else {
			mPhotoImg.setVisibility(View.GONE);
		}

	}

	private void findView() {
		textTitle = (TextView) findViewById(R.id.txt_name);
		textTime = (TextView) findViewById(R.id.txt_price);
		textContent = (TextView) findViewById(R.id.txt_description);
		mPhotoImg = (ImageView) findViewById(R.id.img_photo);
		mPhotoImg.setVisibility(View.GONE);
		mPhotoImg.setOnClickListener(previewClick);
	}

	/** 分享 */
	private View.OnClickListener shareTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String sms_message = String.format(JokeInfoActivity.this.getResources().getString(R.string.joke_sms_message),
					spaceName, jokeContent);
			String shareContent = String.format(
					JokeInfoActivity.this.getResources().getString(R.string.joke_share_content), spaceName, spaceId,
					spaceHomePage, jokeContent);
			if (shareContent.length() >= 140) {
				shareContent = shareContent.substring(0, 139) + "...";
			}
//			SharePopupMenu sharePopupMenu = new SharePopupMenu(JokeInfoActivity.this,JokeInfoActivity.this, v, sms_message, shareContent);
			SharePopupMenu.show(JokeInfoActivity.this,JokeInfoActivity.this, v, sms_message, shareContent);
		}
	};

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		View rightView = View.inflate(this, R.layout.topbar_rightview, null);
		RelativeLayout buttonLayout1 = (RelativeLayout) rightView.findViewById(R.id.layout_topbar_rightButton1);
		ImageView button1 = (ImageView) rightView.findViewById(R.id.img_topbar_rightButton1);
		button1.setImageResource(R.drawable.topbar_share);
		buttonLayout1.setVisibility(View.VISIBLE);
		mTopbar.setRightLayoutVisible(true);
		mTopbar.addRightLayoutView(rightView);
		buttonLayout1.setOnClickListener(shareTopbarClick);
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.joke);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_JOKE_PROTOCOL) {
				// 获取知趣
				if (protocol.isHandleSuccess()) {
					// 访问成功
					GetJokeProtocol p = (GetJokeProtocol) protocol;
					mJoke = p.getJoke();
					if (mJoke != null && mJoke.getContent() != null) {
						jokeContent = mJoke.getContent();
					}
					initView(mJoke);
				} else {
					ErrorManager.showErrorMessage(getApplicationContext());
				}
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}
}
