package com.zhima.ui.usercenter.data.profile.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.consts.ZMConsts.BloodGroup;
import com.zhima.base.consts.ZMConsts.GenderType;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.base.utils.DateUtils;
import com.zhima.data.model.Region;
import com.zhima.data.model.User;
import com.zhima.data.service.RegionService;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.CustomDialog.OnBtClickListener;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.SelectListDialog;
import com.zhima.ui.common.view.SelectListDialog.OnBtItemClicklistener;
import com.zhima.ui.common.view.ZhimaDatePickerDialog;
import com.zhima.ui.common.view.ZhimaRegionThreeDialog;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.IntentHelper;

/**
 * @ClassName: MyEditorInfoActivity
 * @Description: 编辑个人信息
 * @author luqilong
 * @date 2013-1-15 下午1:49:26
 * 
 */
public class MyProfileEditActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = MyProfileEditActivity.class.getSimpleName();

	public static final String USER = "user";
	private static final int REQUEST_GALLERY_CODE = 0;
	private static final int REQUEST_CAMERA_CODE = 2;
	private static final int REQUEST_CROP_CODE = 3;
	private static final int REQUEST_NAME_CODE = 4;
	private static final int REQUEST_SIGNATURE_CODE = 5;

	//默认值
	private static final String defalut_data = "1987-4-1";
	private static final String defalut_address = "北京朝阳";

	//剪切头像大小
	private static final int Crop_aspectX = 80;
	private static final int Crop_aspecty = 80;

	private User mUser;

//	private RadioSingleManage radioManager;
	private RadioGroup mBloodGroup;
	private RadioButton mARadioBloodView;
	private RadioButton mBRadioBloodView;
	private RadioButton mORadioBloodView;
	private RadioButton mABRadioBloodView;

	/** 性别 */
	private RadioGroup mGenderGroup;
	/** 性别男 */
	private RadioButton mMaleRadioButton;
	/** 性别女 */
	private RadioButton mFemaleRadioButton;

	/** 头像 */
	private ImageView mPhotoView;
	/** 姓名 */
	private TextView mNameView;
	/** 性别 */
//	private TextView mGenderView;
	/** 地址 */
	private TextView mAddressView;
	/** 生日 */
	private TextView mBirthdayView;
	/** 星座 */
	private TextView mAstroView;
	/** 血型 */
//	private TextView mBloodView;
	/** 签名 */
	private TextView mSignatureView;

	private String mPhotoPath;

	private UserService mUserService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myzhima_info_editor_activity);

		setTopbar();
		findView();
		init();
		mUser = mUserService.getMyself();

		//默认数据
		try {
			setView();
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
	}

	private void init() {
		mUserService = UserService.getInstance(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_GALLERY_CODE:
				Uri uri = data.getData();
				if (uri != null) {
					mPhotoPath = MediaStoreHelper.getIntentImagePath(getContentResolver(), data.getData());
					sendCropImageIntent(Uri.fromFile(new File(mPhotoPath)));
				} else {
					HaloToast.show(getApplicationContext(), R.string.load_failed);
				}
				break;
			case REQUEST_CAMERA_CODE:
				sendCropImageIntent(Uri.fromFile(new File(mPhotoPath)));
				break;
			case REQUEST_CROP_CODE:
				//获得返回的数据
				Bundle extras = data.getExtras();
				//获得实际剪裁的区域的bitmap图形
				Bitmap thePic = extras.getParcelable("data");
				String cropPath = saveBitmapFile(thePic);
				if (cropPath != null) {
					updateAvatar(cropPath);
				}
				break;
			case REQUEST_NAME_CODE:
				String content = data.getStringExtra("content");
				mNameView.setText(content);
				break;
			case REQUEST_SIGNATURE_CODE:
				String signatureContent = data.getStringExtra("content");
				mSignatureView.setText(signatureContent);
				break;
			}

		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (mUser != null) {
			if (!TextUtils.isEmpty(mUser.getImageUrl())) {
				HttpImageLoader.getInstance(this).loadImage(mUser.getImageUrl(), mPhotoView, getActivityId(),
						R.drawable.default_image, ImageScaleType.SMALL);
			}
		}
	}

	private String saveBitmapFile(Bitmap thePic) {
		String fileName = System.currentTimeMillis() + ".jpg";
		String result = FileHelper.getSysDcmiPath(fileName);
		if (!TextUtils.isEmpty(result)) {
			GraphicUtils.saveBitmapFile(thePic, CompressFormat.JPEG, SystemConfig.IMAGE_QUALITY, result);
			return result;
		}
		return null;
	}

	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.UPDATE_USERINFO_PROTOCOL) {
				// 更新资料
				if (protocol.isHandleSuccess()) {
					HaloToast.show(getApplicationContext(), "更新成功!");
//					UpdateUserInfoProtocol p = (UpdateUserInfoProtocol) protocol;
					Intent it = new Intent(MyProfileEditActivity.this, MyProfileActivity.class);
					it.putExtra(ACTIVITY_EXTRA, mUser.getId());
					startActivity(it);
					finish();
				} else {
					HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
				}
			}
		} else {
			// TODO 网络访问错误
			HaloToast.show(getApplicationContext(), R.string.network_request_failed);
		}
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO Auto-generated method stub
	}

	/**
	 * 更新头像
	 */
	private class MyUpdateAvatarHttpCallback implements IHttpRequestCallback {
		private String path;

		public MyUpdateAvatarHttpCallback(String path) {
			super();
			this.path = path;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				// 更新头像
				if (protocol.isHandleSuccess()) {
					//更新头像成功
					HaloToast.show(getApplicationContext(), "更新成功", 0);
					setPhotoView(path);
				} else {
					//更新头像失败
					HaloToast.show(getApplicationContext(), "更新失败", 0);
				}
			} else {
				// 网络访问失败
				HaloToast.show(getApplicationContext(), R.string.network_request_failed, 0);
			}
		}

	}

	//---------------------------------------------------------
	//event

	/** 标题栏:保存 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (checkText()) {
				try {
					User user = (User) mUser.clone();
					//设置昵称
					user.setNickname(mNameView.getText().toString());
					//设置性别
					int genderId = mGenderGroup.getCheckedRadioButtonId();
					String gender = genderId == R.id.rdo_male ? GenderType.MALE : GenderType.FEMALE;
					user.setGender(gender);
					//设置城市
//				long cityId = RegionService.getInstance(MyProfileEditActivity.this).getCityIdByName(mAddressView.getA);
					user.setCityId((Long) mAddressView.getTag());
					//设置生日
					try {
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						user.setBirthday(dateFormat.parse(mBirthdayView.getText().toString()).getTime());
					} catch (ParseException e) {
						Logger.getInstance(TAG).debug(e.getMessage(), e);
					}
					//设置星座
					user.setAstro(mAstroView.getText().toString());
					//设置血型
					user.setBloodType(getBloodTypeStr(mBloodGroup.getCheckedRadioButtonId()));
					//设置签名
					user.setSignature(mSignatureView.getText().toString());

					//----------------------
					user.setSchoolId(mUser.getSchoolId());
					user.setBoardId(mUser.getBoardId());
					//更新资料
					startWaitingDialog(null, R.string.loading);
					mUserService.updateMyselfInfo(user, MyProfileEditActivity.this);
				} catch (CloneNotSupportedException e) {
					Logger.getInstance(TAG).debug(e.getMessage(), e);
				}
			}
		}

	};

	/**
	 * 头像姓名
	 */
	private void photoClick(View view) {
		//TODO
		SelectListDialog modifyDialog = new SelectListDialog(this);
		modifyDialog.setTitle("选择头像");
		modifyDialog.setoptionNames(new String[] { "拍照上传", "本地上传" });
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

	/**
	 * 生日
	 */
	private void birthdayClick(View view) {
		final ZhimaDatePickerDialog pickerDialog = new ZhimaDatePickerDialog(this, view);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			pickerDialog.setDate(dateFormat.parse(mBirthdayView.getText().toString()));
		} catch (Exception e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
		pickerDialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {
				int month = Integer.parseInt(pickerDialog.getMonth());
				int day = Integer.parseInt(pickerDialog.getDay());
				String astro = DateUtils.getAstro(MyProfileEditActivity.this, month, day);

				mBirthdayView.setText(pickerDialog.getYear() + "-" + pickerDialog.getMonth() + "-"
						+ pickerDialog.getDay());
				mAstroView.setText(astro);
			}

			@Override
			public void onLeftBtClick() {

			}
		});
		pickerDialog.show();
	}

	/**
	 * 选择地址
	 */
	private void addressClick(View view) {
		final ZhimaRegionThreeDialog threddDialog = new ZhimaRegionThreeDialog(this, view, true);
		//TODO 设置默认地址
		Object obj = mAddressView.getTag();
		if (obj != null) {
			Long id = (Long) obj;
		}
		threddDialog.setOnBtClickListener(new OnBtClickListener() {
			@Override
			public void onRightBtClick() {

				Region first = threddDialog.getFirstObject();
				Region second = threddDialog.getSecondObject();
				Region third = threddDialog.getThirdObject();

				String r = "";
				long id = -1;
				//第1级
				if (first != null && first.getId() != -1) {
					r += first.getName();
					id = first.getId();
				}
				//第2级
				if (second != null && second.getId() != -1) {
					r += "-" + second.getName();
					id = second.getId();
				}
				//第3级
				if (third != null && third.getId() != -1) {
					r += "-" + third.getName();
					id = third.getId();
				}
				mAddressView.setText(r);
				mAddressView.setTag(id);
			}

			@Override
			public void onLeftBtClick() {

			}
		});

		threddDialog.show();
	}

	/**
	 * 编辑姓名
	 */
	private void nameClick(View view) {
		Intent intent = new Intent(this, ProfileEditNameActivity.class);
		String name = mNameView.getText().toString();
		intent.putExtra("content", name);
		startActivityForResult(intent, REQUEST_NAME_CODE);
	}

	/**
	 * 编辑签名
	 */
	private void signatureClick(View view) {
		Intent intent = new Intent(this, ProfileEditSignatureActivity.class);
		String name = mSignatureView.getText().toString();
		intent.putExtra("content", name);
		startActivityForResult(intent, REQUEST_SIGNATURE_CODE);
	}

	//---------------------------------------------------------
	//private method

	private void setView() {
		//设置默认头像
		if (!TextUtils.isEmpty(mUser.getImageUrl())) {
			HttpImageLoader.getInstance(this).loadImage(mUser.getImageUrl(), mPhotoView, getActivityId(),
					R.drawable.default_image, ImageScaleType.SMALL);
		}

		//设置默认姓名
		if (!TextUtils.isEmpty(mUser.getNickname())) {
			mNameView.setText(mUser.getNickname());
		}

		//设置默认性别
		String gender = mUser.getGender();
		if (GenderType.MALE.equals(gender)) {
			mMaleRadioButton.setChecked(true);
		} else if (GenderType.FEMALE.equals(gender)) {
			mFemaleRadioButton.setChecked(true);
		}

		//设置默认地址
		if (mUser.getCityId() > 0) {
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
			mAddressView.setTag(mUser.getCityId());
		} else {
//			long cityId = RegionService.getInstance(this).getCityIdByName(defalut_address);
//			mAddressView.setTag(cityId);
//			mAddressView.setText(defalut_address);
		}

		Date d = null;
		if (mUser.getBirthday() > 0) {
			d = new Date(mUser.getBirthday());
		} else {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				d = dateFormat.parse(defalut_data);
			} catch (ParseException e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}
		}
		//设置默认生日
		mBirthdayView.setText(formatDate2String(d));
		//设置默认星座
		mAstroView.setText(DateUtils.getAstro(this, d.getMonth() + 1, d.getDate()));

		//设置默认血型
		String bloodType = mUser.getBloodType();
		setBloodTypeView(bloodType);
		//设置默认签名
		mSignatureView.setText(mUser.getSignature());
	}

	private void setPhotoView(String filePath) {
		Bitmap bitmap = GraphicUtils.getImageThumbnailByDip(getApplicationContext(), filePath, Crop_aspectX,
				Crop_aspecty);
		mPhotoView.setImageBitmap(bitmap);
		mPhotoView.setTag(filePath);
	}

	private void updateAvatar(String path) {
		startWaitingDialog(null, "正在更新头像...");
		mUserService.updateUserAvatar(path, new MyUpdateAvatarHttpCallback(path));
	}

	private void sendCamreaSelectPhoto() {
		String fileName = System.currentTimeMillis() + ".jpg";
		mPhotoPath = FileHelper.getSysDcmiPath(fileName);
		if (mPhotoPath != null) {
			IntentHelper.sendCamreaPhotoForResult(this, mPhotoPath, REQUEST_CAMERA_CODE);
		} else {
			HaloToast.show(this, R.string.sd_error);
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	private void sendGallerySelectPhoto() {
		IntentHelper.sendGalleryPhotoForResult(this, REQUEST_GALLERY_CODE);
	}

	/** 通过Intent剪切图片 */
	private void sendCropImageIntent(Uri scr) {
		IntentHelper.sendCropImageForResult(this, scr, 1, 1, Crop_aspectX, Crop_aspecty, REQUEST_CROP_CODE);
	}

	private boolean checkText() {
		//检查用户姓名
		if (TextUtils.isEmpty(mNameView.getText().toString())) {
			//没有输入姓名
			HaloToast.show(getApplicationContext(), "没有输入姓名");
			return false;
		}

		//检查性别
		int genderId = mGenderGroup.getCheckedRadioButtonId();
		if (genderId == -1) {
			HaloToast.show(getApplicationContext(), "未选择性别");
			return false;
		}

		//检查地址
		if (mAddressView.getTag() == null) {
			HaloToast.show(getApplicationContext(), "没有地址");
			return false;
		}
		//检查血型
		if (mBloodGroup.getCheckedRadioButtonId() == -1) {
			HaloToast.show(getApplicationContext(), "没有选择血型");
			return false;
		}

		//检查签名
		String signature = mSignatureView.getText().toString();
		if (TextUtils.isEmpty(signature)) {
			HaloToast.show(getApplicationContext(), "没有输入签名");
			return false;
		}

		return true;
	}

	private String formatDate2String(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	//设置血型
	private void setBloodTypeView(String bloodType) {
		if (bloodType.equalsIgnoreCase(BloodGroup.A)) {
			mARadioBloodView.setChecked(true);
		} else if (bloodType.equalsIgnoreCase(BloodGroup.B)) {
			mBRadioBloodView.setChecked(true);
		} else if (bloodType.equalsIgnoreCase(BloodGroup.AB)) {
			mABRadioBloodView.setChecked(true);
		} else if (bloodType.equalsIgnoreCase(BloodGroup.O)) {
			mORadioBloodView.setChecked(true);
		}
	}

	private String getBloodTypeStr(int bloodId) {
		switch (bloodId) {
		case R.id.rdo_blood_a:
			return BloodGroup.A;
		case R.id.rdo_blood_b:
			return BloodGroup.B;
		case R.id.rdo_blood_ab:
			return BloodGroup.AB;
		case R.id.rdo_blood_o:
			return BloodGroup.O;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_photo:
		case R.id.img_photo:
			photoClick(v);
			break;
		case R.id.layout_name:
			nameClick(v);
			break;
		case R.id.layout_address:
			addressClick(v);
			break;
		case R.id.layout_birthday:
			birthdayClick(v);
			break;
		case R.id.layout_signature:
			signatureClick(v);
			break;
		}
	}

	private void findView() {
		// TODO Auto-generated method stub
		mBloodGroup = (RadioGroup) findViewById(R.id.rdogp_blood);
		mARadioBloodView = (RadioButton) findViewById(R.id.rdo_blood_a);
		mBRadioBloodView = (RadioButton) findViewById(R.id.rdo_blood_b);
		mORadioBloodView = (RadioButton) findViewById(R.id.rdo_blood_o);
		mABRadioBloodView = (RadioButton) findViewById(R.id.rdo_blood_ab);

		findViewById(R.id.layout_photo).setOnClickListener(this);
		findViewById(R.id.layout_name).setOnClickListener(this);
		findViewById(R.id.layout_address).setOnClickListener(this);
		findViewById(R.id.layout_birthday).setOnClickListener(this);
		findViewById(R.id.layout_signature).setOnClickListener(this);

		mPhotoView = (ImageView) findViewById(R.id.img_photo);
		mNameView = (TextView) findViewById(R.id.txt_name);

		mGenderGroup = (RadioGroup) findViewById(R.id.rdogp_gender);
		mFemaleRadioButton = (RadioButton) findViewById(R.id.rdo_female);
		mMaleRadioButton = (RadioButton) findViewById(R.id.rdo_male);

		mAddressView = (TextView) findViewById(R.id.txt_address);
		mBirthdayView = (TextView) findViewById(R.id.txt_birthday);
		mAstroView = (TextView) findViewById(R.id.txt_astro);
		mSignatureView = (TextView) findViewById(R.id.txt_signature);

		mPhotoView.setOnClickListener(this);
//		mNameView.setOnClickListener(this);
//		mBirthdayView.setOnClickListener(this);
//		mAddressView.setOnClickListener(this);
//		mSignatureView.setOnClickListener(this);
//		radioManager.addRadioButton(mARadioBloodView);
//		radioManager.addRadioButton(mBRadioBloodView);
//		radioManager.addRadioButton(mORadioBloodView);
//		radioManager.addRadioButton(mABRadioBloodView);
	}

	private void setTopbar() {

		// TODO Auto-generated method stub
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

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_save);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("修改资料");
	}
}
