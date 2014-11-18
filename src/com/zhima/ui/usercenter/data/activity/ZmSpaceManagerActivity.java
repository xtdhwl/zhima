package com.zhima.ui.usercenter.data.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetBindSpacesProtocolHandler;
import com.zhima.data.model.ZMSpace;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 个人资料_芝麻空间绑定管理。
 * 
 * @ClassName: ZmSpaceManagerActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-18 下午9:13:54
 */
public class ZmSpaceManagerActivity extends BaseActivity {

	private ListView mSpaceList;

	private List<ZMSpace> mZMSpaceList;

	private View headView;

	private ZhimaAdapter<ZMSpace> mZhimaAdapter;

	private long mUserId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center_zmspace_manager_activity);

		initData();

		setTopbar();
		findView();
		setUpView(mZMSpaceList);
		getSpaceData();
	}

	private void getSpaceData() {
		// TODO
//		AccountService service = AccountService.getInstance(this);
//		if (service.isLogin()) {
//			ZMSpaceService.getInstance(this).getUserBundlingSpaces(this);
//		}
		ZMSpaceService.getInstance(this).getBundlingSpaces(mUserId, this);
	}

	private void initData() {
		mUserId = getIntent().getLongExtra(ACTIVITY_EXTRA, -1);
		mZMSpaceList = new ArrayList<ZMSpace>();
	}

	private void setTopbar() {
		ViewInitTools.setTopBar(this, "芝麻空间", View.GONE, null);
	}

	private void findView() {
		mSpaceList = (ListView) this.findViewById(R.id.lstv_userCenter_zmSpace);

		headView = View.inflate(this, R.layout.user_center_zmspace_headitem, null);
	}

	private void setUpView(List<ZMSpace> list) {

		// mSpaceList.setAdapter(new SpaceAdapter(this,
		// R.layout.retrieval_search_resultlist_item, mZMSpaceList));
//		if (mZhimaAdapter == null) {
//			mSpaceList.addHeaderView(headView);
//		}
		mZhimaAdapter = new ZhimaAdapter<ZMSpace>(this, R.layout.user_center_zmspace_list_item, list) {

			@Override
			public Object createViewHolder(View view, ZMSpace data) {
				ViewHolder holder = new ViewHolder();
				holder.mResultImage = (ImageView) view.findViewById(R.id.img_retrieval_resultItem_image);
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_title);
				holder.mAddressText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_address);
				return holder;
			}

			@Override
			public void bindView(ZMSpace data, int position, View view) {
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				HttpImageLoader.getInstance(mContext).loadImage(data.getImageUrl(), holder.mResultImage,
						ZmSpaceManagerActivity.this.getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
				holder.mTitleText.setText(data.getName());
				holder.mAddressText.setText(data.getDescription());
			}

			class ViewHolder {
				ImageView mResultImage;
				TextView mTitleText;
				TextView mAddressText;
			}

		};
		mSpaceList.setAdapter(mZhimaAdapter);
		mSpaceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (!mZMSpaceList.isEmpty()) {
					ActivitySwitcher.openSpaceActivity(ZmSpaceManagerActivity.this, mZMSpaceList.get(position)
							.getId(), (int) mZMSpaceList.get(position).getSpaceKind(), null, false);
				}
			}
		});
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等...");
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			int protocolType = protocol.getProtocolType();
			if (protocolType == ProtocolType.GET_USER_BINDING_SPACES) {
				GetBindSpacesProtocolHandler diaryProtocol = (GetBindSpacesProtocolHandler) protocol;
				mZMSpaceList = diaryProtocol.getSpaceList();
				setUpView(mZMSpaceList);
			}else if(protocolType == ProtocolType.GET_BINDING_SPACES){
				GetBindSpacesProtocolHandler diaryProtocol = (GetBindSpacesProtocolHandler) protocol;
				mZMSpaceList = diaryProtocol.getSpaceList();
				setUpView(mZMSpaceList);
			}
		} else {
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}

		dismissWaitingDialog();
	}

	class SpaceAdapter extends BaseAdapter {

		private List<ZMSpace> mDataList;

		private List<ZMSpace> mOpenedList;
		private List<ZMSpace> mNoOpenList;

		private Context mContext;
		private int mLayoutId;

		private boolean isShowNoOpenView = false;

		public SpaceAdapter(Context context, int layoutId, List<ZMSpace> dataList) {
			this.mDataList = dataList;
			this.mContext = context;
			this.mLayoutId = layoutId;

			mOpenedList = new ArrayList<ZMSpace>();
			mNoOpenList = new ArrayList<ZMSpace>();

			excuteData();
		}

		private void excuteData() {
			for (int i = 0; i < mDataList.size(); i++) {
				if (mDataList.get(i).isEnabled()) {
					mOpenedList.add(mDataList.get(i));
				} else {
					mNoOpenList.add(mDataList.get(i));
				}
			}
		}

		@Override
		public int getCount() {
			return mDataList.size() + 2;

		}

		@Override
		public Object getItem(int position) {
			// TODO
			return isShowNoOpenView ? mDataList.get(position - 1) : mDataList.get(position - 2);

		}

		@Override
		public long getItemId(int position) {
			// TODO
			return position;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				TextView openTextview = new TextView(mContext);
				openTextview.setText("已开通空间");
				openTextview.setBackgroundResource(R.color.white_color);
				openTextview.setPadding(10, 10, 10, 10);
				return openTextview;
			} else if (position == mOpenedList.size() + 1) {
				TextView textview = new TextView(mContext);
				textview.setText("未开通空间");
				textview.setBackgroundResource(R.color.white_color);
				textview.setPadding(10, 10, 10, 10);
				return textview;
			} else {

				View view = View.inflate(mContext, mLayoutId, null);
				ViewHolder holder = new ViewHolder();
				holder.mResultImage = (ImageView) view.findViewById(R.id.img_retrieval_resultItem_image);
				holder.mTitleText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_title);
				holder.mAddressText = (TextView) view.findViewById(R.id.txt_retrieval_resultItem_address);

				if (position <= mOpenedList.size()) {
					ZMSpace openSpace = mOpenedList.get(position - 1);
					holder.mTitleText.setText(openSpace.getName());
					holder.mAddressText.setText(openSpace.getDescription());
				} else if (position > mOpenedList.size() + 1) {
					ZMSpace noOpenSpace = mNoOpenList.get(position - mOpenedList.size() - 2);
					holder.mTitleText.setText(noOpenSpace.getName());
					holder.mAddressText.setText(noOpenSpace.getDescription());
				}

				return view;
			}
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO
			if (position == 0 || position == mOpenedList.size() + 1) {
				return false;
			}
			return super.isEnabled(position);
		}

		class ViewHolder {
			ImageView mResultImage;
			TextView mTitleText;
			TextView mAddressText;
		}
	}
}
