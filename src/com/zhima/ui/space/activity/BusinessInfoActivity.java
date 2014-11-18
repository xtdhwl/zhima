package com.zhima.ui.space.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.utils.NetUtils;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.ImageCardAdapter;
import com.zhima.ui.common.view.CardView;
import com.zhima.ui.common.view.CardView.OnCardItemClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;

/**
 * @ClassName:BusinessInfoActivity
 * @Description:商业空间简介
 * @author liqilong
 * @date 2012-8-1 下午2:49:46
 * 
 */
public class BusinessInfoActivity extends BaseActivity {

//	private static final String TAG = "BusinessInfoActivity";
	private TextView mDescriptionText;
	private TextView mNameText;

	private ZMObject mZMObject;
	private ScanningcodeService mScanningcodeService;

	private CardView mPhotoView;
	private RefreshListData<ZMObjectImage> mCommercePhotoList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_business_info_activity);
		setTopbar();
		findView();
		mScanningcodeService = ScanningcodeService.getInstance(this);
		
		Intent intent = getIntent();
		long mCommerceObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = ScanningcodeService.getInstance(this).getCacheZMObject(mCommerceObjectId);
		if(mZMObject != null){
			setView();
			getServiceData();
		}else{
			if(NetUtils.isNetworkAvailable(this)){
				HaloToast.show(this, R.string.load_failed);
			}else{
				HaloToast.show(this, R.string.network_request_failed);
				finish();
			}
		}
		
	}

	private void getServiceData() {
		// 获取商户相册
		mCommercePhotoList = mScanningcodeService.getCacheZMObjectAlbumList(mZMObject.getId());
		if (mCommercePhotoList.isEmpty()) {
			startWaitingDialog("", R.string.loading);
			mScanningcodeService.getZMObjectAlbumList(mZMObject, false, this);
		} else {
			setPhotoView();
		}
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
				// 获取协议结果
				if (protocol.isHandleSuccess()) {
					// TODO 设置相册
					GetZMObjectAlbumListProtocol p = (GetZMObjectAlbumListProtocol) protocol;
					mCommercePhotoList = p.getDataList();
					setPhotoView();
				} else {

				}
			}
		} else {
			// TODO 网络访问失败
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 访问网络前
		
	}

	/**
	 * 设置商户相册
	 */
	private void setPhotoView() {
		ImageCardAdapter imageCardAdapter = new ImageCardAdapter(this, R.layout.space_album_item,
				mCommercePhotoList.getDataList());
		mPhotoView.setAdapter(imageCardAdapter);
		mPhotoView.setOnItemClickListener(new OnCardItemClickListener() {
			@Override
			public void onItemClickListener(View view, int position, boolean isArrow) {
				if (isArrow) {
					Intent it = new Intent(BusinessInfoActivity.this, AlbumActivity.class);
					it.putExtra(ACTIVITY_EXTRA, mZMObject.getId());
					startActivity(it);
				} else {
					Intent it = new Intent(BusinessInfoActivity.this, PhotoActivity.class);
					it.putExtra(ACTIVITY_EXTRA, mZMObject.getId());
					it.putExtra(PhotoActivity.ACTIVITY_POSITION, position);
					startActivity(it);
				}
			}
		});
	}

	private void setView() {
		mDescriptionText.setText(mZMObject.getDescription());
		mNameText.setText(mZMObject.getName());
	}

	private void findView() {
		mDescriptionText = (TextView) findViewById(R.id.txt_description);
		mPhotoView = (CardView) findViewById(R.id.card_photo);
		mNameText = (TextView) findViewById(R.id.txt_name);
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
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.intro);
	}
}
