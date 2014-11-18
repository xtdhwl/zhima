package com.zhima.plugin.space;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommerceProductProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommerceProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.CommerceService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.SpaceVPAdapter;
import com.zhima.ui.common.view.SpaceViewPager.OnItemClickListener;
import com.zhima.ui.space.activity.ProductInfoActivity;
import com.zhima.ui.tools.HttpImageLoader;

public class BusinessProductPluginController extends BaseViewPluginController {
	//TODO 添加到常量配置信息
	private int preview_size = SystemConfig.VIEW_PLUGIN_PREVIEW_COUNT;
	private int item_count = SystemConfig.VIEW_PLUGIN_LIST_MAX_COUNT;

	private CommerceService mCommerceService;
	private RefreshListData<CommerceProduct> mProductRefreshList;
	private CommerceObject commerce;

	public BusinessProductPluginController(BaseActivity activity, BusinessProductViewPlugin plugin) {
		super(activity, plugin);
	}

	private void setProductView(final ArrayList<CommerceProduct> dataList) {

		//		ProductPagerAdapter adapter = new ProductPagerAdapter(mParentActivity, R.layout.space_darwable_card_item,
		//				list_size, item_count, dataList);
		//		ZMBaseVPAdapter adaptera = new ZMBaseVPAdapter(mParentActivity, item_count, item_count) {
		//			@Override
		//			public Object initItems(ViewGroup container, int position) {
		//				
		//				return null;
		//			}
		//		};
		SpaceVPAdapter<CommerceProduct> adapter = new SpaceVPAdapter<CommerceProduct>(mParentActivity, dataList,
				preview_size, item_count) {

			@Override
			public Object getView(ViewGroup container, CommerceProduct data, int position) {
				View view = View.inflate(mParentActivity, R.layout.space_darwable_card_item, null);
				ImageView productImage = (ImageView) view.findViewById(R.id.img_photo);
				TextView nameText = (TextView) view.findViewById(R.id.txt_name);

				HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), productImage,
						mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.MEDIUM);
				nameText.setText(data.getName() + "  " + data.getPrice());
				container.addView(view, 0);
				return view;
			}

		};

		((BusinessProductViewPlugin) mViewPlugin).setAdapter(adapter);
		((BusinessProductViewPlugin) mViewPlugin).setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(ViewGroup parent, View view, int position) {
				Intent productIntent = new Intent(mParentActivity, ProductInfoActivity.class);
				productIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, commerce.getId());
				productIntent.putExtra(ProductInfoActivity.PRODUCT_POSITION, position);
				mParentActivity.startActivity(productIntent);
			}
		});

	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		mHandler.sendEmptyMessage(IntentMassages.START_WAITING_DIALOG);
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		
		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);

		if (protocol.isHttpSuccess()) {
			//			if (protocol.getProtocolType() == ProtocolType.GET_COMMERCE_PRODUCT_LIST_PROTOCOL) {
			// 获取商户活动列表结果
			if (protocol.isHandleSuccess()) {
				GetCommerceProductProtocol p = (GetCommerceProductProtocol) protocol;
				mProductRefreshList = p.getDataList();
				setProductView(mProductRefreshList.getDataList());
			} else {

			}
			//			}
		} else {
			// TODO 网络访问错误
			//			if (protocol.getProtocolType() == ProtocolType.ADD_CONTACT_PROTOCOL) {
			//			HaloToast.show(mParentActivity.getApplicationContext(), R.string.network_request_failed, 0);
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
			//			}
		}
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		// TODO Auto-generated method stub
		//请求服务器数据
		// 获取商品列表
		commerce = (CommerceObject) zmObject;
		mCommerceService = CommerceService.getInstance(mParentActivity);
		mProductRefreshList = mCommerceService.getCacheCommerceProductList(commerce);
		if (mProductRefreshList.isEmpty()) {
			mCommerceService.getCommerceProductList(commerce, refreshed, this);
		} else {
			setProductView(mProductRefreshList.getDataList());
		}
	}
}
