package com.zhima.ui.space.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.space.adapter.ImageShowInfoAdapter;

/**
 * @ClassName SpaceImageShowInfoActivity
 * @Description 空间图片展示的图片详情
 * @author jiang
 * @date 2012-9-20 下午12:42:26
 */
public class SpaceImageShowInfoActivity extends BaseActivity {

	public static final String PRODUCT_POSITION = "position";
	private ViewFlow mProductView;
	private ScanningcodeService mScanningcodeService;
	private ImageShowInfoAdapter mImageShowInfoAdapter;
	private RefreshListData<ZMObjectImage> mImageShowList;
	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_product_info_activity);
		setTopbar();

		mProductView = (ViewFlow) findViewById(R.id.flow_production);
		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		position = intent.getIntExtra(PRODUCT_POSITION, 0);
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mImageShowList = mScanningcodeService.getCacheZMObjectAlbumList(zmObjectId);
		mImageShowInfoAdapter = new ImageShowInfoAdapter(this, R.layout.space_productinfo_item,
				mImageShowList.getDataList());
		mImageShowInfoAdapter.setOnClickerListener(previewClick);
		mProductView.setAdapter(mImageShowInfoAdapter, position);
	}

	//预览大图
	private View.OnClickListener previewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ZMObjectImage data = (ZMObjectImage) v.getTag();
			Intent it = new Intent(SpaceImageShowInfoActivity.this, PreviewActivity.class);
			it.putExtra(PreviewActivity.ACTIVITY_URL, data.getImageUrl());
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("图片展示");
	}

}
