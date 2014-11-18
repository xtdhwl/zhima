package com.zhima.plugin.space;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetJokeListProtocol;
import com.zhima.data.model.Joke;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.plugin.IntentMassages;
import com.zhima.plugin.controller.BaseViewPluginController;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.activity.JokeInfoActivity;
import com.zhima.ui.space.adapter.JokeAdapter;

/**
 * @ClassName: ElevatorJokeViewPluginController
 * @Description: 公共空间知趣ViewPluginController
 * @author luqilong
 * @date 2013-1-7 下午5:42:27
 */
public class ElevatorJokeViewPluginController extends BaseViewPluginController {

	private ElevatorJokeLayoutViewPlugin mViewPlugin;
	private ZMObject mZMObject;

	private RefreshListData<Joke> mRecommendedJokeList;
	private ArrayList<Joke> mJokeList;
	private JokeAdapter mJokeAdapter;

	private ScanningcodeService mScanningcodeService;

	public ElevatorJokeViewPluginController(BaseActivity activity, ElevatorJokeLayoutViewPlugin viewPlugin) {
		super(activity, viewPlugin);
		mViewPlugin = viewPlugin;
		mScanningcodeService = ScanningcodeService.getInstance(activity);
	}

	@Override
	public void loadData(ZMObject zmObject, boolean refreshed) {
		// 随机趣闻
		mZMObject = zmObject;
		mRecommendedJokeList = mScanningcodeService.getCacheRecommendedJokeList();
		if (mRecommendedJokeList.isEmpty()) {
			mScanningcodeService.getJokeList(refreshed, this);
		} else {
			mJokeList = mRecommendedJokeList.getDataList();
			setJokeView();
		}
		setListener();
	}

	private void setListener() {
		mViewPlugin.setOnItemClickLisntener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Object obj = parent.getItemAtPosition(position);
				if (obj instanceof Joke) {
					Joke joke = (Joke) obj;
					Intent intent = new Intent(mParentActivity, JokeInfoActivity.class);
					intent.putExtra(BaseActivity.ACTIVITY_EXTRA, joke.getId());
					intent.putExtra(JokeInfoActivity.SPACE_NAME, mZMObject.getName());
					intent.putExtra(JokeInfoActivity.SPACE_ID, mZMObject.getZMID());
					intent.putExtra(JokeInfoActivity.SPACE_HOMEPAGE, mZMObject.getSpaceHomepage());
					mParentActivity.startActivity(intent);
				}
			}
		});
		mViewPlugin.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mScanningcodeService.getJokeList(false, ElevatorJokeViewPluginController.this);
			}
		});
	}

	private void setJokeView() {
		if (mJokeAdapter == null) {
			mJokeAdapter = new JokeAdapter(mParentActivity, R.layout.space_joke_list_item, mJokeList);
			mViewPlugin.setAdapter(mJokeAdapter);
		} else {
			//TODO 设置数据
			mJokeAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(IntentMassages.DISMISS_WAITING_DIALOG);
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_JOKE_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					GetJokeListProtocol p = (GetJokeListProtocol) protocol;
					mRecommendedJokeList = p.getDataList();
					mJokeList = mRecommendedJokeList.getDataList();
					setJokeView();
					mViewPlugin.setLastPage(mRecommendedJokeList.isLastPage());
				}
				mViewPlugin.onRefreshComplete(mViewPlugin.hasPullFromTop());

			}
		} else {
			mViewPlugin.setEmptyView();
			mHandler.sendEmptyMessage(IntentMassages.SHOW_ERROR_TOAST);
		}

	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		super.onHttpStart(protocol);
		// 请求服务器前
		mHandler.sendEmptyMessage(IntentMassages.START_DIALOG);
	}

}
