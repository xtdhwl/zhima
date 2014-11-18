package com.zhima.plugin.space;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.protocol.CommerceProtocolHandler.GetCommercePromotionListProtocol;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.model.CommerceObject;
import com.zhima.data.model.CommercePromotion;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.CommerceService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.SpaceVPAdapter;
import com.zhima.ui.common.view.SpaceViewPager.OnItemClickListener;
import com.zhima.ui.space.activity.PromotionInfoActivity;
import com.zhima.ui.tools.HttpImageLoader;

public class BusinessPromotionPluginController extends BaseViewPluginController {
	
	private int preview_size = SystemConfig.VIEW_PLUGIN_PREVIEW_COUNT;
	private int item_count = SystemConfig.VIEW_PLUGIN_LIST_MAX_COUNT;
	
	
	private CommerceService mCommerceService;
	private RefreshListData<CommercePromotion> mPromotionRefreshList;
	//	private ProductViewPlugin mViewPlugin;
	private CommerceObject commerce;

	public BusinessPromotionPluginController(BaseActivity activity, BusinessPromotionViewPlugin plugin) {
		super(activity, plugin);
	}

	private void setPromotionView(ArrayList<CommercePromotion> dataList) {
		//XXX 需要接藕

		//		PromotionPagerAdapter adapter = new PromotionPagerAdapter(mParentActivity,
		//				R.layout.space_business_promotion_item, list_size, item_count, dataList);
		SpaceVPAdapter<CommercePromotion> adapter = new SpaceVPAdapter<CommercePromotion>(mParentActivity, dataList,
				preview_size, item_count) {
			@Override
			public Object getView(ViewGroup container, CommercePromotion data, int position) {
				// TODO Auto-generated method stub
				View view = View.inflate(mParentActivity, R.layout.space_business_promotion_item, null);
				ImageView mPhotoImg = (ImageView) view.findViewById(R.id.img_photo);
				TextView mNameText = (TextView) view.findViewById(R.id.txt_name);
				TextView mTimeText = (TextView) view.findViewById(R.id.txt_time);
				TextView mDescriptionText = (TextView) view.findViewById(R.id.txt_description);

				mNameText.setText(data.getName());
				//结束时间
				long deadline = data.getDeadlineTime();
				//XXX 这里会影响业务层
				if (data.isDateless()) {
					mTimeText.setText(R.string.dateless);
				} else {
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
					String beginTime = dateFormat.format(new Date(data.getBeginTime()));
					String deadlineTime = dateFormat.format(new Date(deadline));
					mTimeText.setText(beginTime + "-" + deadlineTime);
				}

				mDescriptionText.setText(data.getDescription());

				HttpImageLoader.getInstance(mParentActivity).loadImage(data.getImageUrl(), mPhotoImg,
						mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);

				container.addView(view, 0);
				return view;
			}
		};
//		mViewPlugin.setData(dataList);
		((BusinessPromotionViewPlugin) mViewPlugin).setAdapter(adapter);
		((BusinessPromotionViewPlugin) mViewPlugin).setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(ViewGroup parent, View view, int position) {
				Intent promotionIntent = new Intent(mParentActivity, PromotionInfoActivity.class);
				promotionIntent.putExtra(BaseActivity.ACTIVITY_EXTRA, commerce.getId());
				promotionIntent.putExtra("position", position);
				mParentActivity.startActivity(promotionIntent);
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
			// 获取商户产品列表结果
			if (protocol.isHandleSuccess()) {
				// 更新产品view
				GetCommercePromotionListProtocol p = (GetCommercePromotionListProtocol) protocol;
				mPromotionRefreshList = p.getDataList();
				setPromotionView(mPromotionRefreshList.getDataList());
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
		//请求服务器数据
		// 获取商品活动
		commerce = (CommerceObject) zmObject;
		mCommerceService = CommerceService.getInstance(mParentActivity);
		//???对象参数如果传过来 
		// 获取商家活动列表
		mPromotionRefreshList = mCommerceService.getCacheCommercePromotionList(commerce);
		if (mPromotionRefreshList.isEmpty()) {
			mCommerceService.getCommercePromotionList(commerce, refreshed, this);
		} else {
			setPromotionView(mPromotionRefreshList.getDataList());
		}
	}
}
