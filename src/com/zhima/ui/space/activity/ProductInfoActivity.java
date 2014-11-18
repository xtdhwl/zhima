package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommerceProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.CommerceService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ViewFlow;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.space.adapter.ProductInfoAdapter;

/**
 * @ClassName:ProductInfoActivity
 * @Description:商品详细信息
 * @author liqilong
 * @date 2012-7-23 下午3:15:45
 * 
 */
public class ProductInfoActivity extends BaseActivity {
	/** 传递商品在列表第几个 */
	public static final String PRODUCT_POSITION = "position";

	private ViewFlow mProductView;
	private ProductInfoAdapter mProductInfoAdapter;

	private CommerceObject mCommerceObject;
	private RefreshListData<CommerceProduct> mProductList;

	private int position = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_product_info_activity);
		findId();
		setTopbar();

		Intent intent = getIntent();
		long zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		position = intent.getIntExtra(PRODUCT_POSITION, 0);
		mCommerceObject = (CommerceObject) ScanningcodeService.getInstance(this).getCacheZMObject(zmObjectId);
		if (mCommerceObject != null) {
			mProductList = CommerceService.getInstance(this).getCacheCommerceProductList(mCommerceObject);
			if (!mProductList.isEmpty()) {
				ArrayList<CommerceProduct> array = mProductList.getDataList();
				mProductInfoAdapter = new ProductInfoAdapter(this, R.layout.space_productinfo_item, array);
				mProductInfoAdapter.setOnClickerListener(previewClick);
				position = Math.min(position, array.size() - 1);
				mProductView.setAdapter(mProductInfoAdapter, position);
			}
		}
	}

	//预览大图
	private View.OnClickListener previewClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CommerceProduct data = (CommerceProduct) v.getTag();
			Intent it = new Intent(ProductInfoActivity.this, PreviewActivity.class);
			it.putExtra(PreviewActivity.ACTIVITY_URL, data.getImageUrl());
			startActivity(it);
		}
	};
	
	private void findId() {
		mProductView = (ViewFlow) findViewById(R.id.flow_production);
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.product_particular);
	}
}
