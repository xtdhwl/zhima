package com.zhima.ui.usercenter.data.profile.activity;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.data.model.User;
import com.zhima.data.service.RegionService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName: MyInfoActivity
 * @Description: 个人信息
 * @author luqilong
 * @date 2013-1-15 下午1:47:10
 * 
 */
public class MyProfileActivity extends BaseActivity {
	private static final String TAG = MyProfileActivity.class.getSimpleName();

	public static final String USER = "user";
	private User mUser;

	/** 姓名 */
	private TextView mNameView;
	/** 性别 */
	private TextView mGenderView;
	/** 地址 */
	private TextView mAddressView;
	/** 生日 */
	private TextView mBirthdayView;
	/** 星座 */
	private TextView mAstroView;
	/** 血型 */
	private TextView mBloodView;
	/** 签名 */
	private TextView mSignatureView;
	private ImageView mPhotoView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myzhima_info_activity);

		findView();
		setTopbar();
		Intent intent = getIntent();
		long id = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		if (id != -1) {
			mUser = UserService.getInstance(this).getUser(id);
		} else {
			mUser = UserService.getInstance(this).getMyself();
		}
		setView();
	}

	private void setView() {
		mNameView.setText(mUser.getNickname());

		mGenderView.setText(GenderType.getGenderLabel(mUser.getGender()));

		String region = RegionService.getInstance(this).getRegionStr(mUser.getCityId());
		String[] split = region.split("-");
		String r = "";
		for (String s : split) {
			if (TextUtils.isEmpty(r)) {
				r += s;
			} else {
				r += "-" + s;
			}
		}
		mAddressView.setText(r);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(mUser.getBirthday());
		mBirthdayView.setText(dateStr);
		mAstroView.setText(mUser.getAstro());
		mBloodView.setText(mUser.getBloodType() + "型");
		mSignatureView.setText(mUser.getSignature());

		HttpImageLoader.getInstance(this).loadImage(mUser.getImageUrl(), mPhotoView, getActivityId(),
				R.drawable.default_image, ImageScaleType.MEDIUM);

	}

	private void findView() {
		mNameView = (TextView) findViewById(R.id.txt_name);
		mGenderView = (TextView) findViewById(R.id.txt_gender);
		mAddressView = (TextView) findViewById(R.id.txt_address);
		mBirthdayView = (TextView) findViewById(R.id.txt_birthday);
		mAstroView = (TextView) findViewById(R.id.txt_astro);
		mBloodView = (TextView) findViewById(R.id.txt_bloodtype);
		mSignatureView = (TextView) findViewById(R.id.txt_signature);
		mPhotoView = (ImageView) findViewById(R.id.img_photo);
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

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("详细资料");
	}
}
