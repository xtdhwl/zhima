package com.zhima.ui.space.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.protocol.CommerceProtocolHandler.GainCouponProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.data.service.CommerceService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * 检索优惠券详情！
 * @ClassName: RetriConponInfoActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2012-11-27 上午9:52:14
*/
public class RetriConponInfoActivity extends BaseActivity {

	public static final String PROMOTION_ID = "promotion_id";
	
	private long mCouponId;
	
	private TextView mTitleText;
	private TextView mTimeText;
	private TextView mContentText;
	private ImageView mPhotoImage;
	private TextView mRemainText;

	private String title;
	private long beginTime;
	private long endTime;
	private String content;
	private int remain;
	private String pophoPath;

	private long mTargetId;

	private int mTargetType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_retriconpon_info_activity);
		
		initData();
		setTopbar();
		findView();
		setUpView();
	}

	private void initData() {
		Intent intent = getIntent();
		
		mTargetId = intent.getLongExtra("targetId", 0);
		mTargetType = intent.getIntExtra("targetType", 0);
		mCouponId = intent.getLongExtra("conponId", 0);
		
		title = intent.getStringExtra("title");
		beginTime = intent.getLongExtra("beginTime",new Date().getTime());
		endTime = intent.getLongExtra("endTime",0);
		content = intent.getStringExtra("content");
		remain = intent.getIntExtra("remain", 0);
		pophoPath = intent.getStringExtra("pophoPath");
	}

	private void setTopbar() {
		ZhimaTopbar mTopbar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);

		RelativeLayout ll_right = (RelativeLayout) View.inflate(this, R.layout.topbar_rightview, null);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		mTopbar.addRightLayoutView(ll_right);
		mTopbar.addLeftLayoutView(ll_left);

		mTopbar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_queue);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommerceService.getInstance(RetriConponInfoActivity.this).gainCoupon(mCouponId,RetriConponInfoActivity.this);
				startWaitingDialog("", "请稍等...");
			}
		});
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		mTopbar.findViewById(R.id.layout_topbar_rightButton2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivitySwitcher.openSpaceActivity(RetriConponInfoActivity.this, mTargetId, mTargetType,null,true);
			}
		});
	
		ImageView image2 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton2);
		image2.setImageResource(R.drawable.topbar_enter_business);
		mTopbar.findViewById(R.id.layout_topbar_rightButton2).setVisibility(View.VISIBLE);
		
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.promotion_particular);
	}

	private void findView() {
		mTitleText = (TextView) this.findViewById(R.id.txt_title);
		mTimeText = (TextView) this.findViewById(R.id.txt_time);
		mContentText = (TextView) this.findViewById(R.id.txt_content);
		mPhotoImage = (ImageView) this.findViewById(R.id.img_photo);
		mRemainText = (TextView) this.findViewById(R.id.txt_remain);
	}

	private void setUpView() {
		
		mTitleText.setText(title);
		mTimeText.setText(new SimpleDateFormat("MM-dd").format(new Date(beginTime))+" - "+new SimpleDateFormat("MM-dd").format(new Date(endTime)));
		mContentText.setText(content);
		if(remain<=0){
			mRemainText.setText("剩余：不限");
		}else{
			mRemainText.setText("剩余："+remain+"张");
		}
		HttpImageLoader.getInstance(this).loadImage(pophoPath, mPhotoImage,
				getActivityId(), R.drawable.default_image, ImageScaleType.LARGE);
		
		mPhotoImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(RetriConponInfoActivity.this, PreviewActivity.class);
				it.putExtra(PreviewActivity.ACTIVITY_URL, pophoPath);
				startActivity(it);
			}
		});
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
		super.onHttpStart(protocol);
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		if (protocol.isHttpSuccess()) {
			// 收藏卡片夹
			if (protocol.getProtocolType() == ProtocolType.GAIN_COUPON_PROTOCOL) {
				// 获取优惠券
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), R.string.save_card_success);
					GainCouponProtocolHandler p = (GainCouponProtocolHandler) protocol;
					int count = p.getRemainCount();
					TextView tv = (TextView) RetriConponInfoActivity.this.findViewById(R.id.txt_remain);
					if (count < 0) {
						tv.setText(getApplication().getString(R.string.remain) + ":" + "不限");
					} else {
						tv.setText(getApplication().getString(R.string.remain) + ":" + count);
					}
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage(), 0);
				}
			}
		} else {
			// TODO 网络访问失败
			HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
		}
		dismissWaitingDialog();
	}
}
