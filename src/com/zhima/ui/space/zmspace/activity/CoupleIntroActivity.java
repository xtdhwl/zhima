package com.zhima.ui.space.zmspace.activity;

import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.base.utils.ImageHelper;
import com.zhima.data.model.ZMCardObject;
import com.zhima.data.model.ZMCouplesObject;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.ContactUtil;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.ViewInitTools;
import com.zhima.ui.tools.HttpImageLoader.ImageDownloadListener;

/**
 * 喜印简介
 * @ClassName: FameSpaceIntroActivity
 * @Description: TODO
 * @author yusonglin
 * @date 2013-1-28 上午11:14:58
*/
public class CoupleIntroActivity extends BaseActivity implements OnClickListener {

	/** logo */
	private ImageView mLogoImage;
	private ImageView mDownloadImage;
	
//	/** 姓名 */
//	private TextView mNameText;
//	/** 电话 */
//	private TextView mPhoneText;
//	/** email */
//	private TextView mEmailText;
//	/** 公司 */
//	private TextView mOrgText;
//	/** 职位 */
//	private TextView mPositionText;
//	/** qq */
//	private TextView mQqText;
//	/** 微博 */
//	private TextView mMicroblogText;
//	/** 介绍 */
	
	private TextView mMaleNameText;
	private TextView mMaleCityText;
	private TextView mMaleBirthText;
	private TextView mMaleConstellationText;
	private TextView mMaleBloodTypeText;
	
	private TextView mFemaleNameText;
	private TextView mFemaleCityText;
	private TextView mFemaleBirthText;
	private TextView mFemaleConstellationText;
	private TextView mFemaleBloodTypeText;
	
//	private RelativeLayout mPhoneLayout;
//	private RelativeLayout mEmailLayout;
//	private RelativeLayout mOrgLayout;
	
	private TextView mIntroduceText;
	
	private long mZmObjectId;
	private ZMCouplesObject mZMObject;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_zmspace_couple_intro_activity);
		
		initData();
		setTopBar();
		findView();
		setUpView();
	}

	private void initData() {
		mZmObjectId = getIntent().getLongExtra(ACTIVITY_EXTRA, -1);
		mZMObject = (ZMCouplesObject) ScanningcodeService.getInstance(this).getCacheZMObject(mZmObjectId);
	}

	private void setTopBar() {
		//TODO
		ViewInitTools.setTopBar(this, "喜印简介", View.GONE, null);
		
//		ZhimaTopbar topbar = getTopbar();
//
//		View topbarRightView = View.inflate(this, R.layout.topbar_rightview,
//				null);
//		RelativeLayout rightLayout1 = (RelativeLayout) topbarRightView.findViewById(R.id.layout_topbar_rightButton1);
//		ImageView rightBt1 = (ImageView) topbarRightView.findViewById(R.id.img_topbar_rightButton1);
//
//		rightBt1.setImageResource(R.drawable.topbar_save);
//		rightLayout1.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO
//				if(mZMObject != null){
//					ContactUtil.saveToContact(CoupleIntroActivity.this, mZMObject.getName(), 
//							mZMObject.getTelephone(), mZMObject.getEmail(), 
//							mZMObject.getCompanyName(), mZMObject.getJob(), 
//							mZMObject.getAddress(),null);
//				}else{
//					HaloToast.show(getApplicationContext(), "保存失败");
//				}
//			}
//		});
//
//		rightLayout1.setVisibility(View.VISIBLE);
//		topbar.setRightLayoutVisible(true);
//		topbar.addRightLayoutView(topbarRightView);
	}

	private void findView() {
		//TODO
		
//		mNameText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_name);
//		mPhoneText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_phone);
//		mEmailText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_email);
//		mOrgText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_org);
//		mPositionText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_position);
//		mQqText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_qq);
//		mMicroblogText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_microblog);
//		
//		mPhoneLayout = (RelativeLayout) this.findViewById(R.id.layout_zmspace_fame_intro_phone);
//		mEmailLayout = (RelativeLayout) this.findViewById(R.id.layout_zmspace_fame_intro_email);
//		mOrgLayout = (RelativeLayout) this.findViewById(R.id.layout_zmspace_fame_intro_org);
		
		mLogoImage = (ImageView) this.findViewById(R.id.img_zmspace_fame_intro_logo);
		mDownloadImage = (ImageView) this.findViewById(R.id.img_zmspace_fame_intro_download);
		
		mMaleNameText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_maleName);
		mMaleCityText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_maleCity);
		mMaleBirthText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_maleBirth);
		mMaleConstellationText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_maleConstellation);
		mMaleBloodTypeText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_maleBloodType);
		
		mFemaleNameText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_femaleName);
		mFemaleCityText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_femaleCity);
		mFemaleBirthText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_femaleBirth);
		mFemaleConstellationText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_femaleConstellation);
		mFemaleBloodTypeText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_femaleBloodType);
		
		mIntroduceText = (TextView) this.findViewById(R.id.txt_zmspace_fame_intro_introduce);
	}
	
	private void setUpView() {
		//TODO
		if(mZMObject!=null){
			
			HttpImageLoader.getInstance(this).loadImage(mZMObject.getImageUrl(),mLogoImage,
					getActivityId(), R.drawable.default_image, ImageScaleType.SMALL);
			
			
			mMaleNameText.setText(mZMObject.getMaleName());
			mMaleCityText.setText(mZMObject.getMaleCityName());
			mMaleBirthText.setText(new SimpleDateFormat("yyyy-MM-dd").format(mZMObject.getMaleBirthday()));
			mMaleConstellationText.setText(mZMObject.getMaleAstro());
			mMaleBloodTypeText.setText(mZMObject.getMaleBloodType());
			
			mFemaleNameText.setText(mZMObject.getFemaleName());
			mFemaleCityText.setText(mZMObject.getFemaleCityName());
			mFemaleBirthText.setText(new SimpleDateFormat("yyyy-MM-dd").format(mZMObject.getFemaleBirthday()));
			mFemaleConstellationText.setText(mZMObject.getFemaleAstro());
			mFemaleBloodTypeText.setText(mZMObject.getFemaleBloodType());
			
			mIntroduceText.setText(mZMObject.getDescription());
			
			mDownloadImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startWaitingDialog("", "正在下载...");
					
					HttpImageLoader.getInstance(CoupleIntroActivity.this).downloadImage(mZMObject.getImageUrl(), 
							ImageScaleType.ORIGINAL, new ImageDownloadListener() {
						
						@Override
						public void onReady(String uri, String localFilePath) {
							//TODO
								boolean saveFalg = false;
								if (localFilePath != null) {
									String filePath = null;
									long dateTaken = System.currentTimeMillis();
									String filename = dateTaken + "." + FileHelper.getFileExtension(localFilePath);
									filePath = FileHelper.getSysDcmiPath(filename);
									if (filePath != null) {
										saveFalg = FileHelper.copyFile(localFilePath, filePath);
										if (saveFalg) {
											/*Uri bitmapUri =*/MediaStoreHelper.insertImage(getContentResolver(), filename, filename,
													dateTaken, FileHelper.getMIME(localFilePath), filePath);
										}
									} else {
										HaloToast.show(getApplicationContext(), R.string.sd_error);
									}
								}

								if (saveFalg) {
									HaloToast.show(getApplicationContext(), R.string.save_success);
								} else {
									//其他失败
									HaloToast.show(getApplicationContext(), R.string.msg_failed, 0);
								}
								dismissWaitingDialog();
						}
						
						@Override
						public void onError(String msg, String uri) {
							//TODO
							dismissWaitingDialog();
							HaloToast.show(getApplicationContext(), R.string.msg_failed, 0);
						}
					});
				}
			});
			
//			mNameText.setText(mZMObject.getName());
//			mPhoneText.setText(mZMObject.getTelephone());
//			mEmailText.setText(mZMObject.getEmail());
//			mOrgText.setText(mZMObject.getCompanyName());
//			mPositionText.setText(mZMObject.getJob());
//			mQqText.setText(mZMObject.getQQ());
//			mMicroblogText.setText(mZMObject.getWeibo());
			
//			mPhoneLayout.setOnClickListener(this);
//			mEmailLayout.setOnClickListener(this);
//			mOrgLayout.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
//		//TODO
//		switch (v.getId()) {
//		case R.id.layout_zmspace_fame_intro_phone://电话
//			HaloToast.show(getApplicationContext(), "打电话");
//			break;
//		case R.id.layout_zmspace_fame_intro_email://邮箱
//			HaloToast.show(getApplicationContext(), "发邮件");
//			break;
//		case R.id.layout_zmspace_fame_intro_org://公司
//			HaloToast.show(getApplicationContext(), "看地图");
//			break;
//		}
	}
}
