package com.zhima.ui.login.activity;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.data.model.User;
import com.zhima.data.service.RegionService;
import com.zhima.data.service.UserService;
import com.zhima.ui.common.view.CustomDialog.OnBtClickListener;
import com.zhima.ui.common.view.CustomDialog.OnOnlyBtClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.SelectListDialog;
import com.zhima.ui.common.view.ZhimaRegionThreeDialog;
import com.zhima.ui.common.view.SelectListDialog.OnBtItemClicklistener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.IntentHelper;
import com.zhima.ui.tools.ViewInitTools;
import com.zhima.ui.usercenter.data.profile.activity.ProfileEditNameActivity;

/**
 * @className PersonalDataSetupActivity
 * @author yusognlin
 * @date 2012-07-24 下午
 */
public class PersonalDataSetupActivity extends LoginBaseActivity implements OnClickListener {

	private static final int request_gallery_Code = 0;
	private static final int request_camera_Code = 2;
	private static final int request_Crop_Code = 3;
	private static final int request_name_Code = 4;
	
	// 剪切头像大小
	private static final int Crop_aspectX = 80;
	private static final int Crop_aspecty = 80;

	private TextView mUidText;
	
	/** 性别 */
	private RadioGroup mGenderGroup;

	/** 头像 */
	private ImageView mPhotoView;
	/** 姓名 */
	private TextView mNameView;
	/** 地址 */
	private TextView mAddressView;
	
	private String mPhotoPath;
	private Bitmap mPhotoBitmap;

	private String mZMUserId;
	private User myself;
	private String nickName;
	protected String gender;
	private long mCityId = -1;
	private ZhimaRegionThreeDialog threddDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_personaldata_setup_activity);

		initData();
		setTopbar();
		findView();
		setUpView();
	}

	private void setTopbar() {
		//TODO
		ViewInitTools.setTopBar(this, "填写资料", View.GONE, null);
		
		ZhimaTopbar topbar = getTopbar();
		
		View topbarRightView = View.inflate(this, R.layout.topbar_rightview, null);
		RelativeLayout rightLayout1 = (RelativeLayout) topbarRightView.findViewById(R.id.layout_topbar_rightButton1);
		ImageView rightBt1 = (ImageView) topbarRightView.findViewById(R.id.img_topbar_rightButton1);
	
		rightBt1.setImageResource(R.drawable.topbar_save);
		rightLayout1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
//				HaloToast.show(getApplicationContext(), "保存资料");
				if(setUserData()){
					UserService.getInstance(PersonalDataSetupActivity.this).updateMyselfInfo(myself, PersonalDataSetupActivity.this);
				}
			}
		});
		
		rightLayout1.setVisibility(View.VISIBLE);
		topbar.addRightLayoutView(topbarRightView);
		topbar.setRightLayoutVisible(true);
	}

	private void initData() {
		//TODO 这里获取User 
		Intent intent = getIntent();
		mZMUserId = intent.getStringExtra(ACTIVITY_EXTRA);
		
		myself = UserService.getInstance(PersonalDataSetupActivity.this).getMyself();
		
	}
	
	private void createRegionDialog(){
		if(threddDialog == null){
			threddDialog = new ZhimaRegionThreeDialog(this, mAddressView, true);
			threddDialog.setOnBtClickListener(new OnBtClickListener() {
				@Override
				public void onRightBtClick() {
					String adminArea = threddDialog.getFirstItemString();
					String subAdminArea = threddDialog.getsecondItemString();
					String three = threddDialog.getThirdItemString();
					
					mAddressView.setText(adminArea + "-" + subAdminArea + "-" + three);
					
					setCityId();
					
//					mAddressView.setTag(mCityId);
				}
				
				@Override
				public void onLeftBtClick() {
					
				}
			});
			setCityId();
		}/*else{
			setCityId();
		}*/
	}
	
	private void setCityId(){
		if(threddDialog.getThirdObject()!=null && threddDialog.getThirdObject().getId()!=-1){
			mCityId = threddDialog.getThirdObject().getId();
		}else if(threddDialog.getSecondObject()!=null && threddDialog.getSecondObject().getId()!=-1){
			mCityId = threddDialog.getSecondObject().getId();
		}else if(threddDialog.getFirstObject()!=null && threddDialog.getFirstObject().getId()!=-1){
			mCityId = threddDialog.getFirstObject().getId();
		}
	}
	
	private boolean setUserData() {
//		//TODO
//		if(TextUtils.isEmpty(mPhotoPath)){
//			HaloToast.show(this, "请设置您的头像");
//		}
		if(nickName==null || "".equals(nickName)){
			HaloToast.show(this, "请设置您的昵称");
			return false;
		}
		if(gender==null || "".equals(gender)){
			HaloToast.show(this, "请设置您的性别");
			return false;
			
		}
		if(mCityId == -1){
			HaloToast.show(this, "请设置您的城市");
			return false;
		}
//		myself.setImageUrl(mPhotoPath);
		myself.setNickname(nickName);
		myself.setGender(gender);
		if(mCityId != -1){
			myself.setCityId(mCityId);
		}
		return true;
	}

	private void findView() {
		
		mUidText = (TextView) this.findViewById(R.id.txt_login_data_setUp_uid);
		
		mPhotoView = (ImageView) findViewById(R.id.img_photo);
		mNameView = (TextView) findViewById(R.id.txt_name);
//		mGenderView = (TextView) findViewById(R.id.txt_birthday);
		mGenderGroup = (RadioGroup) findViewById(R.id.rdogp_gender);
		mAddressView = (TextView) findViewById(R.id.txt_address);
		
	}

	private void setUpView() {
		
		createRegionDialog();
		
		mUidText.setText("您的UID为"+mZMUserId+",用于登陆时使用");
		
		mGenderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_male:
					gender = GenderType.MALE;
					break;
				case R.id.rb_female:
					gender = GenderType.FEMALE;
					break;
				}
			}
		});
		
		findViewById(R.id.layout_photo).setOnClickListener(this);
		mPhotoView.setOnClickListener(this);
		mNameView.setOnClickListener(this);
		mAddressView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_photo:
		case R.id.img_photo:
			photoClick(v);
			break;
		case R.id.txt_name:
			nameClick(v);
			break;
		case R.id.txt_address:
			addressClick(v);
			break;
		}
	}
	
	/**
	 * 编辑姓名
	 */
	private void nameClick(View view) {
		//TODO
		Intent intent = new Intent(this, ProfileEditNameActivity.class);
		String name = mNameView.getText().toString();
		intent.putExtra("content", name);
		startActivityForResult(intent, request_name_Code);
	}
	
	/**
	 * 选择地址
	 */
	private void addressClick(View view) {
		
//		//TODO 设置默认地址
//		Object obj = mAddressView.getTag();
//		if (obj != null) {
//			Long id = (Long) obj;
//		}
		
		if(threddDialog!=null){
			
			threddDialog.show();
//		final ZhimaRegionThreeDialog threddDialog = new ZhimaRegionThreeDialog(this, view, false);
//		//TODO 设置默认地址
//		Object obj = mAddressView.getTag();
//		if (obj != null) {
//			Long id = (Long) obj;
		}
	}
	
	/**
	 * 头像姓名
	 */
	private void photoClick(View view) {
		//TODO
		SelectListDialog modifyDialog = new SelectListDialog(this);
		modifyDialog.setTitle("选择照片");
		modifyDialog.setoptionNames(new String[] { "拍照", "查看相册" });
		modifyDialog.setOnBtItemClickListener(new OnBtItemClicklistener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					sendCamreaSelectPhoto();
				} else if (position == 1) {
					sendGallerySelectPhoto();
				}
			}
		});
		modifyDialog.show();
	}
	
	private void sendCamreaSelectPhoto() {
		try {
			String fileName = System.currentTimeMillis() + ".jpg";
			mPhotoPath = null;
			mPhotoPath = FileHelper.getSysDcmiPath(fileName);
			if (mPhotoPath != null) {
				IntentHelper.sendCamreaPhotoForResult(this, mPhotoPath, request_camera_Code);
			} else {
				HaloToast.show(this, R.string.sd_error);
			}
		} catch (Exception e) {
			Logger.getInstance(e.toString());
			HaloToast.show(this, R.string.open_camera_fail);
		}
	}
	
	/**
	 * 拍照获取图片
	 * 
	 */
	private void sendGallerySelectPhoto() {
		IntentHelper.sendGalleryPhotoForResult(this, request_gallery_Code);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case request_gallery_Code:
				Uri uri = data.getData();
				if (uri != null) {
					mPhotoPath = MediaStoreHelper.getIntentImagePath(getContentResolver(), data.getData());
					sendCropImageIntent(Uri.fromFile(new File(mPhotoPath)));
				} else {
					HaloToast.show(getApplicationContext(), R.string.load_failed);
				}
				break;
			case request_camera_Code:
				sendCropImageIntent(Uri.fromFile(new File(mPhotoPath)));
				break;
			case request_Crop_Code:
				
				//获得返回的数据
				Bundle extras = data.getExtras();
				//获得实际剪裁的区域的bitmap图形
				Bitmap thePic = extras.getParcelable("data");
				String cropPath = saveBitmapFile(thePic);
				if (cropPath != null) {
					setPhoto(thePic);
					UserService.getInstance(PersonalDataSetupActivity.this).updateUserAvatar(cropPath, PersonalDataSetupActivity.this);
				}
				break;
			case request_name_Code:
				nickName = data.getStringExtra("content");
				mNameView.setText(nickName);
				break;
			}

		}
	}
	
	/**
	 * 保存剪切以后的图片
	 * @Title: saveBitmapFile
	 * @Description: TODO
	 * @param thePic
	 * @return
	 */
	private String saveBitmapFile(Bitmap thePic) {
		String fileName = System.currentTimeMillis() + ".jpg";
		String result = FileHelper.getSysDcmiPath(fileName);
		if (!TextUtils.isEmpty(result)) {
			GraphicUtils.saveBitmapFile(thePic, CompressFormat.JPEG, SystemConfig.IMAGE_QUALITY, result);
			return result;
		}
		return null;
	}

	
	/** 通过Intent剪切图片 */
	// 检测图片：120*120 提高识别率
	private void sendCropImageIntent(Uri scr) {
		IntentHelper.sendCropImageForResult(this, scr, 1, 1, Crop_aspectX, Crop_aspecty, request_Crop_Code);
	}
	
	private void setPhoto(Bitmap bitmap) {
		mPhotoBitmap = bitmap;
		mPhotoView.setImageBitmap(bitmap);
	}
	
	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		//TODO
		super.onHttpStart(protocol);
		startWaitingDialog("", "请稍等...");
	}
	
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		//TODO
		super.onHttpResult(protocol);
		if(protocol.isHttpSuccess()){
			if(protocol.getProtocolType() == ProtocolType.UPDATE_USERINFO_PROTOCOL){
				if(protocol.isHandleSuccess()){
					dismissLoginActivity();
					HaloToast.show(this, "资料上传成功");
				}else{
					HaloToast.show(this, "上传资料失败");
				}
//				if(protocol.getProtocolStatus() == ProtocolStatus.RESULT_SUCCESS){
//				}else{
//				}
			}else if(protocol.getProtocolType() == ProtocolType.UPDATE_USER_AVATAR_PROTOCOL){
				if(protocol.isHandleSuccess()){
//					dismissLoginActivity();
					HaloToast.show(this, "照片上传成功");
				}else{
					HaloToast.show(this, "照片上传失败");
				}
			}
		}else{
			HaloToast.show(this,R.string.network_request_failed);
		}
		dismissWaitingDialog();
	}
	
}
