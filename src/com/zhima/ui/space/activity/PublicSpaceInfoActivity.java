package com.zhima.ui.space.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 公共空间 简介
 * 
 * @ClassName:PublicSpaceInfoActivity
 * @Description:TODO
 * @author liqilong
 * @date 2012-7-9 下午4:42:36
 * 
 */
public class PublicSpaceInfoActivity extends BaseActivity {

	private TextView mDescriptionText;
	private TextView mNameText;

	private ZMObject mZMObject;
	private ScanningcodeService mScanningcodeService;
	private ImageView mPhotoView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_space_info_activity);
		setTopbar();
		findView();
		mScanningcodeService = ScanningcodeService.getInstance(this);

		Intent intent = getIntent();
		long mCommerceObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = mScanningcodeService.getCacheZMObject(mCommerceObjectId);
		if (mZMObject != null) {
			setView();
		} else {
			if (NetUtils.isNetworkAvailable(this)) {
				HaloToast.show(this, R.string.load_failed);
			} else {
				HaloToast.show(this, R.string.network_request_failed);
				finish();
			}
		}

	}

	private void setView() {
		mNameText.setText(mZMObject.getName());
		mDescriptionText.setText(mZMObject.getDescription());
		HttpImageLoader.getInstance(this).loadImage(mZMObject.getImageUrl(), mPhotoView,
				((BaseActivity) this).getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
		mPhotoView.setVisibility(View.VISIBLE);
		mPhotoView.setOnClickListener(previewClick);
	}

	private void findView() {
		mDescriptionText = (TextView) findViewById(R.id.txt_description);
		mPhotoView = (ImageView) findViewById(R.id.img_public_log);
		mNameText = (TextView) findViewById(R.id.txt_name);
	}

	//预览大图
	private View.OnClickListener previewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent it = new Intent(PublicSpaceInfoActivity.this, PreviewActivity.class);
			it.putExtra(PreviewActivity.ACTIVITY_URL, mZMObject.getImageUrl());
			startActivity(it);
		}
	};

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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.intro);
	}
}
