package com.zhima.ui.space.couples.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMCouplesProtocolHandler.GetWeddingCommentListProtocol;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.WeddingComment;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMCouplesService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.PullToRefreshListView;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.zhima.ui.space.couples.adapter.WeddingCommentAdapter;

/**
 * @ClassName CouplesBlessActivity
 * @Description 祝福语
 * @author jiang
 * @date 2012-12-6 上午11:31:49
 */
public class CouplesBlessActivity extends BaseActivity {

	private ZMCouplesObject mZMCouplesObject;
	private ZMCouplesService mZMCouplesService;
	private PullToRefreshListView mBlessPullView;
	private ListView mBlessListView;
	private RefreshListData<WeddingComment> mWeddingCommentRefList;
	private ScanningcodeService mScanningcodeService;
	private WeddingCommentAdapter mWeddingCommentAdapter;
	private boolean isAllowComment = true;
	private TextView text_no_comment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_couplesblesslist_activity);
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		isAllowComment = intent.getBooleanExtra(ACTIVITY_EXTRA2, true);

		setTopbar();
		findView();
		init();

		mZMCouplesObject = (ZMCouplesObject) mScanningcodeService.getCacheZMObject(id);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mZMCouplesObject != null) {
			getServiceDate();
			setListener();
		} else {
			ErrorManager.showErrorMessage(getApplicationContext());
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_COUPLES_COMMENT_LIST_PROTOCOL) {
				if (protocol.isHandleSuccess()) {
					// 得到知天使频道列表
					GetWeddingCommentListProtocol p = (GetWeddingCommentListProtocol) protocol;
					mWeddingCommentRefList = p.getDataList();
					setBlessList(mWeddingCommentRefList.getDataList());
					if (mWeddingCommentRefList.getDataList().isEmpty()) {
						text_no_comment.setVisibility(View.VISIBLE);
					} else {
						text_no_comment.setVisibility(View.GONE);
					}
					mBlessPullView.setLastPage(mWeddingCommentRefList.isLastPage());
				} else {

				}
				mBlessPullView.onRefreshComplete(mBlessPullView.hasPullFromTop());
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(this, R.string.network_request_failed, 0);
			mBlessPullView.setEmptyView();
		}
	}

	private void setListener() {
		// TODO Auto-generated method stub
		mBlessPullView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mZMCouplesService.getWeddingCommentList(mZMCouplesObject.getRemoteId(),
						mZMCouplesObject.getZMObjectType(), mBlessPullView.hasPullFromTop(), CouplesBlessActivity.this);
			}
		});
	}

	private void getServiceDate() {
		// TODO Auto-generated method stub
		startWaitingDialog("", "正在加载...");
		mZMCouplesService.getWeddingCommentList(mZMCouplesObject.getRemoteId(), mZMCouplesObject.getZMObjectType(),
				true, this);
	}

	private void setBlessList(final ArrayList<WeddingComment> commentList) {
		if (mWeddingCommentAdapter == null) {
			mWeddingCommentAdapter = new WeddingCommentAdapter(this, R.layout.space_wedding_comment_item, commentList);
			mBlessListView.setAdapter(mWeddingCommentAdapter);
			mBlessListView.setSelector(R.drawable.blesslist_selector_background);
		} else {
			mWeddingCommentAdapter.setData(commentList);
			mWeddingCommentAdapter.notifyDataSetChanged();
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		mScanningcodeService = ScanningcodeService.getInstance(this);
		mZMCouplesService = ZMCouplesService.getInstance(this);
	}

	private void findView() {
		// TODO Auto-generated method stub
		mBlessPullView = (PullToRefreshListView) findViewById(R.id.refresh_lstv);
		mBlessListView = mBlessPullView.getRefreshableView();
		text_no_comment = (TextView) findViewById(R.id.no_have_comment);
	}

	private void setTopbar() {
		// TODO Auto-generated method stub
		ZhimaTopbar mTopbar = getTopbar();
		mTopbar.setBackgroundResource(R.color.red_couples_topbar);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addLeftLayoutView(ll_left);
		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		mTopbar.addRightLayoutView(ll_right);
		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.couples_topbar_bless_edit);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mZMCouplesObject != null) {
					if (isAllowComment) {
						Intent intent = new Intent(CouplesBlessActivity.this, BlessEditActivity.class);
						intent.putExtra(ACTIVITY_EXTRA, mZMCouplesObject.getId());
						startActivity(intent);
					} else {
						HaloToast.show(CouplesBlessActivity.this, R.string.not_allow_comment);
					}

				}
			}
		});

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.bless_sentence);
	}

}
