package com.zhima.ui.usercenter.data.lattice.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.UserProtocolHandler.GetLatticeProductListProtocol;
import com.zhima.data.model.LatticeProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.usercenter.data.lattice.adapter.LatticeListAdapter;

/**
 * @ClassName: MyLatticeActivity
 * @Description: 格子铺
 * @author luqilong
 * @date 2013-1-19 上午10:24:50
 */
public class LatticeListActivity extends BaseActivity {

	private static final int request_add_code = 1;

	private UserService mUserService;
	private long mUserId;

	private RefreshListData<LatticeProduct> mCacheUserLatticeProduct;
	private ArrayList<LatticeProduct> mLatticeProduct;

	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private LatticeListAdapter mLatticeAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercenter_mylattice_activity);
		setTopbar();
		findView();
		init();

		Intent it = getIntent();
		mUserId = it.getLongExtra(ACTIVITY_EXTRA, -1);

		getServiceData();
		setListener();
		checkUser(mUserId);
	}

	private void init() {
		mUserService = UserService.getInstance(this);
	}

	private void setListener() {
		mPullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
//				if (!mRefreshLatticeProduct.isLastPage()) {
				mUserService.getLatticeProductList(mUserId, mPullListView.hasPullFromTop(), LatticeListActivity.this);
//				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent it = new Intent(LatticeListActivity.this, LatticeInfoActivity.class);
				it.putExtra(ACTIVITY_EXTRA, mUserId);
				it.putExtra(ACTIVITY_EXTRA2, position);
				startActivity(it);

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mLatticeAdapter != null) {
			setLatticeView();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case request_add_code:
//				setLatticeView();
				startWaitingDialog(null, R.string.loading);
				mUserService.getLatticeProductList(mUserId, true, LatticeListActivity.this);
				break;
			}
		}
	};

	public void getServiceData() {
		mCacheUserLatticeProduct = mUserService.getCacheUserLatticeProduct(mUserId);
		if (mCacheUserLatticeProduct.isEmpty()) {
			startWaitingDialog(null, R.string.loading);
			mUserService.getLatticeProductList(mUserId, false, this);
		} else {
			setLatticeView();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_USER_LATTICE_PRODUCT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到格子铺列表
					GetLatticeProductListProtocol p = (GetLatticeProductListProtocol) protocol;
					mCacheUserLatticeProduct = p.getDataList();
					if (mCacheUserLatticeProduct.isEmpty()) {
						HaloToast.show(getApplicationContext(), "格子铺中还没有添加物品");
					}
					setLatticeView();
					mPullListView.setLastPage(mCacheUserLatticeProduct.isLastPage());
				} else {

				}
				mPullListView.onRefreshComplete(mPullListView.hasPullFromTop());
			}
		} else {
			//TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mPullListView.setEmptyView();
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {

	}

	private void setLatticeView() {
		mLatticeProduct = mCacheUserLatticeProduct.getDataList();
		if (mLatticeAdapter == null) {
			mLatticeAdapter = new LatticeListAdapter(this, R.layout.usercenter_lattice_item, mLatticeProduct);
			mListView.setAdapter(mLatticeAdapter);
		} else {
			mLatticeAdapter.setData(mLatticeProduct);
			mLatticeAdapter.notifyDataSetChanged();
		}
	}

	//添加格子铺
	private OnClickListener addTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(LatticeListActivity.this, LatticeEditActivity.class);
			startActivityForResult(intent, request_add_code);
		}
	};

	private void checkUser(long userId) {
		if (mUserService.isMySelf(userId)) {
			ZhimaTopbar topbar = getTopbar();
			ImageView img1 = (ImageView) topbar.findViewById(R.id.img_topbar_rightButton1);
			img1.setImageResource(R.drawable.topbar_add);
			topbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(addTopbarClick);
			topbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		}
	}

	private void findView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.pull_list_lattice);
		mListView = mPullListView.getRefreshableView();
		mListView.setSelector(new ColorDrawable(getResources().getColor(R.color.transparent)));
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("格子铺");
	}

}
