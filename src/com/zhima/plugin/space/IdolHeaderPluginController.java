package com.zhima.plugin.space;

import com.zhima.base.config.SystemConfig;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.plugin.space.common.viewplugin.SpaceHeaderViewPlugin;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: IdolHeaderPluginController
 * @Description: 知天使头部展示
 * @author luqilong
 * @date 2013-1-6 下午3:16:41
 * 
 */
public class IdolHeaderPluginController extends BaseViewPluginController {

	private int item_count = SystemConfig.VIEW_PLUGIN_LIST_MAX_COUNT;
	private RefreshListData<ZMObjectImage> mAlbumRefreshList;
	private SpaceHeaderViewPlugin viewPlugin;
	private HttpImageLoader mHttpImageLoader;
	private ScanningcodeService mScanningcodeService;

	private int mDefaultId = 0;
	private ZMIdolObject mZMIdolObject;

	public IdolHeaderPluginController(BaseActivity activity, SpaceHeaderViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		this.viewPlugin = viewPlugin;
		mHttpImageLoader = HttpImageLoader.getInstance(mParentActivity);
		mScanningcodeService = ScanningcodeService.getInstance(mParentActivity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
//		mZMIdolObject = (ZMIdolObject) zmObject;
//
//		setView();
//
//		mAlbumRefreshList = mScanningcodeService.getCacheZMObjectAlbumList(mZMIdolObject.getId());
//		if (mAlbumRefreshList.isEmpty()) {
//			mScanningcodeService.getZMObjectAlbumList(mZMIdolObject, refreshed, this);
//		} else {
//			setAlbumView(mAlbumRefreshList.getDataList());
//		}

	}
//
//	//设置基本信息
//	private void setView() {
//		viewPlugin.setName(mZMIdolObject.getName());
//		viewPlugin.setAge(mZMIdolObject.getAge() + "");
//		viewPlugin.setLoveCount(mZMIdolObject.getLoveCount() + "");
//
//		HttpImageLoader.getInstance(mParentActivity).loadImage(mZMIdolObject.getImageUrl(), viewPlugin.getPhotoView(),
//				mParentActivity.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
//	}
//
//	//设置图片展示区
//	private void setAlbumView(ArrayList<ZMObjectImage> dataList) {
//		//设置头部
//		if (GenderType.MALE.equals(mZMIdolObject.getGender())) {
//			mDefaultId = R.drawable.male_default;
//		} else {
//			mDefaultId = R.drawable.female_default;
//		}
//
//		SpaceVPAdapter<ZMObjectImage> adapter = new SpaceVPAdapter<ZMObjectImage>(mParentActivity, dataList, 1,
//				item_count) {
//			@Override
//			public Object getView(ViewGroup container, ZMObjectImage data, int position) {
//				//XXX 这里需要适配（如何得到view的高度）
//				int widht = viewPlugin.getViewPager().getWidth();
//				int height = viewPlugin.getViewPager().getHeight();
//
//				ImageView iv = new ImageView(mParentActivity);
//				iv.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
//				//XXX ImageScaleType类型需要修改
//				mHttpImageLoader.loadImage(data.getImageUrl(), iv, mParentActivity.getActivityId(), mDefaultId,
//						ImageScaleType.LARGE);
//
//				container.addView(iv, 0);
//				return iv;
//			}
//
//		};
//		viewPlugin.getViewPager().setAdapter(adapter);
//	}
//
//	@Override
//	public void onHttpStart(ProtocolHandlerBase protocol) {
//		mHandler.sendEmptyMessage(IntentMassages.START_WAITING_DIALOG);
//	}
//
//	@Override
//	public void onHttpResult(ProtocolHandlerBase protocol) {
//		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);
//		if (protocol.isHttpSuccess()) {
//			//			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
//			if (protocol.isHandleSuccess()) {
//				// 设置知天使相册
//				GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
//				mAlbumRefreshList = p.getDataList();
//				setAlbumView(mAlbumRefreshList.getDataList());
//			} else {
//
//			}
//			//			}
//		} else {
//			// 网络访问错误
//		}
//	}
//
//	//-------------------------------------------
//	//屏幕大小
//	private int getScreenHeight(int height) {
//		switch (height) {
//		case 480:
//			return 480;
//		case 800:
//			return 800;
//		case 850:
//			return 850;
//		case 900:
//			return 900;
//		case 1100:
//			return 110;
//		default:
//			break;
//		}
//		return 800;
//	}

}
