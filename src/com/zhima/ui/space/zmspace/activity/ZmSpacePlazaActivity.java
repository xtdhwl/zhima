package com.zhima.ui.space.zmspace.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.OrderBy;
import com.zhima.base.consts.ZMConsts.ProtocolStatus;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.protocol.ListProtocolHandlerBase;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.SearchProtocolHandler.QueryZMObjectProtocol;
import com.zhima.base.protocol.ZMSpaceProtocolHandler.GetSelfRecommendedZMObjectListProtocol;
import com.zhima.data.model.BaseData;
import com.zhima.data.model.Orderkind;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.SpaceQueryResult;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMSpaceKind;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.SearchService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ZhimaAdapter;
import com.zhima.ui.common.view.CustomListView;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;

/**
 * 芝麻空间_广场
 * @ClassName: ZmSpacePlazaActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-25 下午3:28:57
*/
public class ZmSpacePlazaActivity extends BaseActivity implements OnClickListener {

	private TextView mNewestBtn;
	private TextView mHottestBtn;
	private TextView mRecommendBtn;
	
	private CustomListView<BaseData> mSpacelist;
	
//	private long spacekindId;
	private long cityId;
	private ZMSpaceKind ZMSpaceKind;
	private int zmObjectType;
	
	private boolean isLastPage;
	private boolean isRequestFinish;
	/** 是否是第一次加载  */
	private boolean isFirstLoad = true;
	private ZMObject mZMObject;
	private ArrayList<BaseData> mDataList;
	
	private static final int NEW = 1;
	private static final int HOT = 2;
	private static final int RECOMMENT = 3;
	
	/** 当前检索排序方式  NEW.最新 HOT.最热 Recommend.推荐 */
	private int mCurrentSort = NEW;
	private long spacekindId;
	
	private View mEmptyLayout;
	private boolean isLoadingData;
//	private Timer timer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_zmspace_plaza_activity);
		
		initData();
		setTopBar();
		findView();
		setUpView();
	}

	/**
	 * 初始化数据
	 * @Title: initData
	 * @Description: TODO
	 */
	private void initData() {
		//TODO
		Intent intent = getIntent();
		long mZmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = ScanningcodeService.getInstance(this).getCacheZMObject(mZmObjectId);
		
		if(mZMObject!=null){
			ZMSpaceKind = mZMObject.getSpaceKind();
			zmObjectType = mZMObject.getZMObjectType();
			if(ZMSpaceKind!=null){
				spacekindId = ZMSpaceKind.getId();
			}
			cityId = mZMObject.getCityId();
			
//			if(mZMObject.getZMObjectType() == ZMObjectKind.IDOL_OBJECT){
//				
//			}
		}
		
//		timer = new Timer();
		
//		order = new Orderkind();
//		
//		orderBy = "new";
//		createOrderKind("最新", orderBy);
		
//		getSpaceData(true);
	}

//	private void createOrderKind(String title,String o) {
//		//TODO
//		if(order!=null){
//			order.setTitle(title);
//			order.setValue(o);
//		}
//	}
	
	private void getSpaceData(boolean refreshed) {
		if(refreshed){
			mSpacelist.mAlreadyLoadData.clear();
		}
		SearchService.getInstance(this).queryZMObject(zmObjectType, spacekindId, OrderBy.NEW, refreshed, this);
	}
	
	private void getSpaceByHot(boolean refreshed){
		if(refreshed){
			mSpacelist.mAlreadyLoadData.clear();
		}
		SearchService.getInstance(this).getPlazaSpaceByHot(zmObjectType, refreshed, this);
	}
	
	private void getSpaceByRecommend(boolean refreshed){
		if(refreshed){
			mSpacelist.mAlreadyLoadData.clear();
		}
		ZMSpaceService.getInstance(this).getSquareRecommendedSpaceList(mZMObject, refreshed, this);
	}
	
	private void setTopBar() {
		//TODO
		ViewInitTools.setTopBar(this, "广场", View.GONE, null);
	}

	private void findView() {
		//TODO
		mNewestBtn = (TextView) this.findViewById(R.id.btn_zmspace_plaza_newest);
		mHottestBtn = (TextView) this.findViewById(R.id.btn_zmspace_plaza_hottest);
		mRecommendBtn = (TextView) this.findViewById(R.id.btn_zmspace_plaza_recommend);
		
		mSpacelist = (CustomListView<BaseData>) this.findViewById(R.id.clv_zmspace_plaza_list);
	
		mEmptyLayout = this.findViewById(R.id.layout_empty);
	}

	private void setUpView() {
		mNewestBtn.setOnClickListener(this);
		mHottestBtn.setOnClickListener(this);
		mRecommendBtn.setOnClickListener(this);
		
		mSpacelist.addLoading();
		mSpacelist.setVisibility(View.GONE);
//		mSpacelist.setVisibility(View.GONE);
		initAdapter();
	}

	@Override
	public void onClick(View v) {
		if(isLoadingData){
			return ;
		}
		//TODO
		switch (v.getId()) {
		case R.id.btn_zmspace_plaza_newest:
			mCurrentSort = NEW;
			getSpaceData(true);
			isLoadingData = true;

			break;
		case R.id.btn_zmspace_plaza_hottest:
			mCurrentSort = HOT;
			getSpaceByHot(true);
			isLoadingData = true;

			break;
		case R.id.btn_zmspace_plaza_recommend:

			mCurrentSort = RECOMMENT;
			getSpaceByRecommend(true);
			isLoadingData = true;
			break;
		}
	}
	
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		startWaitingDialog("", "请稍等");
		isLastPage = false;
		isRequestFinish = false;
		mEmptyLayout.setVisibility(View.GONE);
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			updateListView(protocol);
		}else{
			mSpacelist.deleteLoading();
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
		
		if(mSpacelist.mAlreadyLoadData.size() == 0){
			mSpacelist.setVisibility(View.GONE);
			mEmptyLayout.setVisibility(View.VISIBLE);
		}
		
		isLoadingData = false;
		dismissWaitingDialog();
	}
	
	
	private void updateListView(ProtocolHandlerBase protocol){
		
		ListProtocolHandlerBase p = (ListProtocolHandlerBase) protocol;
		if(p.isHandleSuccess()){
			if (protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS_EMPTY) {
				//没数据
				mSpacelist.deleteLoading();
				mSpacelist.updateListView();
			}else{
				RefreshListData<BaseData> dataList = p.getDataList();
				ArrayList<BaseData> diaryDataList = dataList.getDataList();
				
				int loadSize = mSpacelist.mAlreadyLoadData.size();
				int size = diaryDataList.size();
				
				for (int i = loadSize; i < size; i++) {
					mSpacelist.mAlreadyLoadData.add(diaryDataList.get(i));
				}
				
				if (diaryDataList.size() < 8) {
					mSpacelist.deleteLoading();
				}
				
				mSpacelist.updateListView();
				
				if (dataList.isLastPage()){
					isLastPage  = true;
					mSpacelist.deleteLoading();
				}
			}
		}
		
		if(mSpacelist.mAlreadyLoadData.size() == 0){
			mSpacelist.setVisibility(View.GONE);
		}
		
		mSpacelist.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onDestroy() {
		//TODO
		super.onDestroy();
		isLastPage = false;
		isRequestFinish  = false;
		
//		if(timer!=null){
//			timer.cancel();
//		}
		
	}
	
	private void initAdapter(){
		mDataList = new ArrayList<BaseData>();
//		arrayList.add(new SpaceQueryResult());
//		arrayList.add(new SpaceQueryResult());
//		arrayList.add(new SpaceQueryResult());
//		arrayList.add(new SpaceQueryResult());
//		arrayList.add(new SpaceQueryResult());
//		arrayList.add(new SpaceQueryResult());
//		arrayList.add(new SpaceQueryResult());
		mSpacelist.setBatchLoad(new ZhimaAdapter<BaseData>(this, R.layout.space_zmspace_plaza_item, mSpacelist.mAlreadyLoadData) {
			
			@Override
			public Object createViewHolder(View view, BaseData data) {
				ViewHolder holder = new ViewHolder();
				holder.mHeadImage = (ImageView) view.findViewById(R.id.img_zmspace_plaza_item_headImage);
				holder.mSpaceNameText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_spaceName);
				holder.mSpaceTypeText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_spaceType);
				holder.mDescriptionText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_description);
				holder.mPopularityText = (TextView) view.findViewById(R.id.txt_zmspace_plaza_item_popularity);
				return holder;
			}
			
			@Override
			public void bindView(BaseData data, int position, View view) {
				ViewHolder holder = (ViewHolder) getViewHolder(view, data);
				
				if(data instanceof ZMObject){
					final ZMObject mData = (ZMObject) data;
					HttpImageLoader.getInstance(ZmSpacePlazaActivity.this).loadImage(mData.getImageUrl(),holder.mHeadImage,
							getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
					holder.mSpaceNameText.setText(mData.getName());
					com.zhima.data.model.ZMSpaceKind spaceKind = mData.getSpaceKind();
					holder.mSpaceTypeText.setText("类型："+(spaceKind!=null?spaceKind.getName():""));
					holder.mDescriptionText.setText("简介："+mData.getDescription());
					holder.mPopularityText.setVisibility(View.GONE);
					
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ActivitySwitcher.openSpaceActivity(ZmSpacePlazaActivity.this, mData.getId(), mData.getZMObjectType(), null, false);
						}
					});
				}else if(data instanceof SpaceQueryResult){
					final SpaceQueryResult mData = (SpaceQueryResult) data;
					HttpImageLoader.getInstance(ZmSpacePlazaActivity.this).loadImage(mData.getImageUrl(),holder.mHeadImage,
							getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
					holder.mSpaceNameText.setText(mData.getName());
					holder.mSpaceTypeText.setText("类型："+mData.getKindName());
					holder.mDescriptionText.setText("地址："+mData.getAddress());
					holder.mPopularityText.setText("人气："+mData.getPraiseCount());
					
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ActivitySwitcher.openSpaceActivity(ZmSpacePlazaActivity.this, mData.getTargetId(), mData.getTargetType(), null, false);
						}
					});
				}
			}

			@Override
			public void getFirstData() {
				super.getFirstData();
				isFirstLoad = true;
				mSpacelist.isLoading(true);
				
				requestData(true);
			}

			@Override
			public void getData() {
				isFirstLoad = false;
				
				if(isLastPage){
					if(!isRequestFinish){
						HaloToast.show(getApplicationContext(),R.string.no_more_data);
					}
					isRequestFinish = true;
					return;
				}
				
				mSpacelist.addLoading();
				mSpacelist.isLoading(true);
				
				requestData(false);
			}
			
			private void requestData(boolean refreshed){
				switch (mCurrentSort) {
				case NEW:
					getSpaceData(refreshed);
					break;
				case HOT:
					getSpaceByHot(refreshed);
					break;
				case RECOMMENT:
					getSpaceByRecommend(refreshed);
					break;
				}
			}
			
			class ViewHolder{
				ImageView mHeadImage;
				TextView mSpaceNameText;
				TextView mSpaceTypeText;
				TextView mDescriptionText;
				TextView mPopularityText;//人气
			}
		});
	}
}
