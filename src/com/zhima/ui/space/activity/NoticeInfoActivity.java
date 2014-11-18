package com.zhima.ui.space.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetOfficeNoticeProtocol;
import com.zhima.data.model.Notice;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.HttpImageLoader.ImageDownloadListener;

/**
 * @ClassName:NoticeInfoActivity
 * @Description: 公告详情
 * @author liqilong
 * @date 2012-8-14 下午7:25:02
 * 
 */
public class NoticeInfoActivity extends BaseActivity {
	public static final String ACTIVITY_EXTRA_ZMCODE = "zmCode";
	private TextView mTitleText;
	private TextView mTimeText;
	private TextView mContentText;
	private ImageView mImageView;

	private Notice mNotice;

	private ZMSpaceService mZMSpaceService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_noticeinfo_activity);
		findId();
		setTopbar();
		//根据类型显示官方或商家公告
		Intent intent = getIntent();
		long noticeId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
//		String zmCode = intent.getStringExtra(ACTIVITY_EXTRA_ZMCODE);
//		if (zmCode != null) {
			startWaitingDialog("", R.string.loading);
			ZMSpaceService.getInstance(this).getOfficialNotice(noticeId, this);
//		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_OFFICIAL_NOTICE_PROTOCOL) {
				// 获取官方公告
				if (protocol.isHandleSuccess()) {
					//访问成功
					GetOfficeNoticeProtocol p = (GetOfficeNoticeProtocol) protocol;
					mNotice = p.getNotice();
					setView();
				} else {
					ErrorManager.showErrorMessage(getApplicationContext());
				}
			}
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {

	}

	private void setView() {
		if (mNotice != null) {
			mTitleText.setText(mNotice.getTitle());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String date = dateFormat.format(new Date(mNotice.getPostTime()));
			mTimeText.setText(date);
			mContentText.setText(mNotice.getContent());
			String url = mNotice.getImageUrl();
			if (TextUtils.isEmpty(url)) {
				mImageView.setVisibility(View.GONE);
			} else {
				mImageView.setVisibility(View.VISIBLE);
				HttpImageLoader.getInstance(this).downloadImage(url, ImageScaleType.ORIGINAL, imageDownListener);
			}
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}

	}

	private ImageDownloadListener imageDownListener = new ImageDownloadListener() {

		@Override
		public void onReady(String uri, String localFilePath) {
			// TODO Auto-generated method stub
			if (localFilePath != null && !localFilePath.equals("")) {
				mImageView.setImageBitmap(GraphicUtils.getScreenBitmap(NoticeInfoActivity.this, localFilePath));
			}
		}

		@Override
		public void onError(String msg, String uri) {
			// TODO Auto-generated method stub

		}
	};

	private void findId() {
		mTitleText = (TextView) findViewById(R.id.txt_title);
		mTimeText = (TextView) findViewById(R.id.txt_time);
		mContentText = (TextView) findViewById(R.id.txt_content);
		mImageView = (ImageView) findViewById(R.id.img_photo);
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.notice);
	}
}
