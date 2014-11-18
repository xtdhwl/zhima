package com.zhima.plugin.space;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.lbs.ZMLocationManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetNoticeDigestListProtocol;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.Notice;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.space.activity.NoticeActivity;
import com.zhima.ui.space.activity.NoticeInfoActivity;

/**
 * @ClassName: NoticePluginController
 * @Description: 公告插件控制器
 * @author luqilong
 * @date 2013-1-14 上午10:43:10
 */
public class NoticePluginController extends BaseViewPluginController {

	private ZMObject mZMObject;
	private NoticeViewPlugin mViewPlugin;
	private GeoCoordinate mGeoCoordinate;

	private ScanningcodeService mScanningcodeService;
	private RefreshListData<Notice> mNoticeRefreshList;

	public NoticePluginController(BaseActivity activity, NoticeViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;
		mScanningcodeService = ScanningcodeService.getInstance(activity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		mZMObject = zmObject;

		setListener();
		mGeoCoordinate = ZMLocationManager.getInstance().getLastKnownGeoCoordinate();
		/** 获取公告列表 */
//		mScanningcodeService.getNoticeDigestList(zmObject, zmObject.getCityId(), zmObject.getGeo(), true, this);
	}

	private void setListener() {
		mViewPlugin.setArrowOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mZMObject != null) {
					Intent noticeIt = new Intent(mParentActivity, NoticeActivity.class);
////				noticeIt.putExtra(NoticeActivity.NOTICE_TYPE, notice.getNoticeKind());
//					noticeIt.putExtra(NoticeActivity.REGION_ID, mZMObject.getCityId());
//					noticeIt.putExtra(BaseActivity.ACTIVITY_EXTRA, mZMObject.getZMCode());
//					noticeIt.putExtra(NoticeActivity.ACTIVITY_TITLE, mParentActivity.getText(R.string.commerce_notice)
//							.toString());
					startActivity(noticeIt);
				}
			}
		});
		mViewPlugin.setNoticeOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Notice notice = (Notice) v.getTag();
				if (notice != null) {
					Intent noticeIt = new Intent(mParentActivity, NoticeInfoActivity.class);
					noticeIt.putExtra(BaseActivity.ACTIVITY_EXTRA, notice.getId());
					noticeIt.putExtra(NoticeInfoActivity.ACTIVITY_EXTRA_ZMCODE, mZMObject.getZMCode());
					startActivity(noticeIt);
				}
			}
		});
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_NOTICE_DIGEST_LIST_PROTOCOL) {
				// 获取公告列表
				if (protocol.isHandleSuccess()) {
					GetNoticeDigestListProtocol p = (GetNoticeDigestListProtocol) protocol;
					mNoticeRefreshList = p.getDataList();
					setNoticeView(mNoticeRefreshList.getDataList());
				} else {

				}
			}
		} else {
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}
	}

	private void setNoticeView(ArrayList<Notice> dataList) {
		if (dataList.size() > 0) {
			Notice notice = dataList.get(0);
			mViewPlugin.setNoticeView(notice);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog();
	}
}
