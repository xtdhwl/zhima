package com.zhima.ui.usercenter.data.lattice.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.data.model.LatticeProduct;
import com.zhima.data.service.UserService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.SelectListDialog;
import com.zhima.ui.common.view.SelectListDialog.OnBtItemClicklistener;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.IntentHelper;

/**
 * @ClassName: LatticeEditorActivity
 * @Description: 格子铺编辑
 * @author luqilong
 * @date 2013-1-19 上午11:25:24
 */
public class LatticeEditActivity extends BaseActivity implements OnClickListener {

	public static final String LATTICE = "lattice";

	private static final int request_gallery_Code = 100;
	private static final int request_camera_Code = 101;
	private static final int bitmap_width = 80;
	private static final int bitmap_heigh = 80;

	private LatticeProduct mLatticeProduct;

	/** 编辑名称 */
	private TextView mNameiew;
	/** 编辑价格 */
	private TextView mPriceView;
	/** 编辑数量 */
	private TextView mAmountView;
	/** 编辑交易方式 */
	private TextView mTradeModeView;
	/** 编辑描述 */
	private TextView mDescriptionView;

	private ImageView mPhotoView;

	private String mPhotoPath;

	private UserService mUserService;

	//是否为更新
	private boolean mIsUpdate = false;

	private String mHttpImagePath = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_center_lattice_edit_activity);

		setTopbar();
		findView();
		init();

		Intent it = getIntent();
		mLatticeProduct = (LatticeProduct) it.getSerializableExtra(ACTIVITY_EXTRA);
		setView(mLatticeProduct);
	}

	private void setView(LatticeProduct latticeProduct) {
		//初始化内容
		if (latticeProduct != null) {
			mIsUpdate = true;
			mNameiew.setText(latticeProduct.getTitle());
			mPriceView.setText("￥ " + latticeProduct.getPrice() + "元");
			mPriceView.setTag(latticeProduct.getPrice());

			mAmountView.setText(String.valueOf(latticeProduct.getAmount()));
			mTradeModeView.setText(latticeProduct.getTradeMode());
			mDescriptionView.setText(latticeProduct.getDescription());

			mHttpImagePath = latticeProduct.getImageUrl();
			if (!TextUtils.isEmpty(mHttpImagePath)) {
				HttpImageLoader.getInstance(this).loadImage(mHttpImagePath, mPhotoView, getActivityId(),
						R.drawable.default_image, ImageScaleType.SMALL);
			}
		}
	}

	private void init() {
		mUserService = UserService.getInstance(this);
	}

	//--------------------------------------------------------------------

	private class MyLatticeProductHttpCallback implements IHttpRequestCallback {
		private LatticeProduct lattice;

		public MyLatticeProductHttpCallback(LatticeProduct lattice) {
			super();
			this.lattice = lattice;
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {

		}

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			dismissWaitingDialog();
			if (protocol.isHttpSuccess()) {
				if (protocol.getProtocolType() == ProtocolType.ADD_USER_LATTICE_PRODUCT_PROTOCOL) {
					// 添加格子铺成功
					if (protocol.isHandleSuccess()) {
						HaloToast.show(getApplicationContext(), "添加成功!");
						Intent it = new Intent();
						it.putExtra("data", lattice);
						setResult(RESULT_OK, it);
						finish();
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
					}
				} else if (protocol.getProtocolType() == ProtocolType.UPDATE_USER_LATTICE_PRODUCT_PROTOCOL) {
					// 更新格子铺成功
					if (protocol.isHandleSuccess()) {
						HaloToast.show(getApplicationContext(), "修改成功!");
						Intent it = new Intent();
						it.putExtra("data", lattice);
						setResult(RESULT_OK, it);
						finish();
					} else {
						HaloToast.show(getApplicationContext(), protocol.getProtocolErrorMessage());
					}
				}
			} else {
				// TODO 网络访问错误
				ErrorManager.showErrorMessage(getApplicationContext());
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case LatticeEditItemActivity.lattice_edit_name:
				String content = data.getStringExtra("content");
				mNameiew.setText(content);
				break;
			case LatticeEditItemActivity.lattice_edit_price:
				String price = data.getStringExtra("content");
				double p = Double.parseDouble(price);
				String priceStr = String.format("%.2f", p);
				mPriceView.setText("￥ " + priceStr + "元");
				mPriceView.setTag(priceStr);
				break;
			case LatticeEditItemActivity.lattice_edit_amount:
				String amount = data.getStringExtra("content");
				mAmountView.setText(amount);
				break;
			case LatticeEditItemActivity.lattice_edit_trade_mode:
				String tradeMode = data.getStringExtra("content");
				mTradeModeView.setText(tradeMode);
				break;
			case LatticeEditItemActivity.lattice_edit_description:
				String description = data.getStringExtra("content");
				mDescriptionView.setText(description);
				break;
			case request_gallery_Code:
				//得到照片路径
				Uri uri = data.getData();
				if (uri != null) {
					mPhotoPath = MediaStoreHelper.getIntentImagePath(getContentResolver(), uri);
					setPhotoView(mPhotoPath);
				} else {
					setPhotoView(null);
				}
				break;
			case request_camera_Code:
				setPhotoView(mPhotoPath);
			}
		}
	}

//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		//因为HttpImageLoad会回收。所以需要再次加载
//		//如果已经设置过图片就使用设置的图片
//		//如果没有设置过图片加载网络图片
//		String path = (String) mPhotoView.getTag();
//		if (TextUtils.isEmpty(path)) {
//			if (!TextUtils.isEmpty(mHttpImagePath)) {
//				HttpImageLoader.getInstance(this).loadImage(mHttpImagePath, mPhotoView, getActivityId(),
//						R.drawable.default_image, ImageScaleType.SMALL);
//			}
//		} else {
//			setPhotoView(path);
//
//		}
//	};

	//--------------------------------------------------------------------------------
	//event

	/**
	 * 头像姓名
	 */
	private void photoClick(View view) {
		//TODO
		SelectListDialog modifyDialog = new SelectListDialog(this);
		modifyDialog.setTitle("上传照片");
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

	private OnClickListener saveTopbarClick = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (checkText()) {

				if (mLatticeProduct == null) {
					mLatticeProduct = new LatticeProduct();
				}
				//是否为更新
				//名称
				mLatticeProduct.setTitle(mNameiew.getText().toString());
				//价格
				mLatticeProduct.setPrice(String.valueOf(mPriceView.getTag()));
				//数量
				mLatticeProduct.setAmount(Integer.parseInt(mAmountView.getText().toString()));
				//描述
				mLatticeProduct.setDescription(mDescriptionView.getText().toString());
				//交易方式
				mLatticeProduct.setTradeMode(mTradeModeView.getText().toString());
				//图片
				mLatticeProduct.setImageUrl(mPhotoView.getTag() == null ? "" : String.valueOf(mPhotoView.getTag()));

				if (mIsUpdate) {
					//更新
					startWaitingDialog(null, R.string.loading);
					mUserService.updateLatticeProduct(mLatticeProduct,
							new MyLatticeProductHttpCallback(mLatticeProduct));
				} else {
					//添加
					startWaitingDialog(null, R.string.loading);
					mUserService.addLatticeProduct(mLatticeProduct, new MyLatticeProductHttpCallback(mLatticeProduct));
				}

			}
		}
	};

	//---------------------------------------------------------------------------------------
	//private method
	private boolean checkText() {
		String name = mNameiew.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			HaloToast.show(this, "名称不能为空");
			return false;
		}

		String price = (String) mPriceView.getTag();
		if (TextUtils.isEmpty(price)) {
			HaloToast.show(this, "价格不能为空");
			return false;
		}

		String amount = mAmountView.getText().toString().trim();
		if (TextUtils.isEmpty(amount)) {
			HaloToast.show(this, "数量不能为空");
			return false;
		}

		String tradeMode = mTradeModeView.getText().toString().trim();
		if (TextUtils.isEmpty(tradeMode)) {
			HaloToast.show(this, "交易方式不能为空");
			return false;
		}

		String description = mDescriptionView.getText().toString().trim();
		if (TextUtils.isEmpty(description)) {
			HaloToast.show(this, "描述不能为空");
			return false;
		}
		//是更新就不检测。因为图片地址有可能为网络地址 
		if (!mIsUpdate) {
			String path = (String) mPhotoView.getTag();
			if (TextUtils.isEmpty(path)) {
				HaloToast.show(this, "照片不能为空");
				return false;
			}
		}
		return true;
	}

	private void sendCamreaSelectPhoto() {
		String fileName = System.currentTimeMillis() + ".jpg";
		mPhotoPath = FileHelper.getSysDcmiPath(fileName);
		if (mPhotoPath != null) {
			IntentHelper.sendCamreaPhotoForResult(this, mPhotoPath, request_camera_Code);
		} else {
			HaloToast.show(this, R.string.sd_error);
		}
	}

	private void setPhotoView(String path) {
		if (TextUtils.isEmpty(path)) {
			mPhotoView.setImageBitmap(null);
			mPhotoView.setTag(null);
		} else {
			Bitmap bitmap = GraphicUtils.getImageThumbnailByDip(this, path, bitmap_width, bitmap_heigh);
			mPhotoView.setImageBitmap(bitmap);
			mPhotoView.setTag(path);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_name:
			Intent nameIntent = new Intent(this, LatticeEditItemActivity.class);
			nameIntent.putExtra("type", LatticeEditItemActivity.lattice_edit_name);
			nameIntent.putExtra("content", mNameiew.getText().toString());
			startActivityForResult(nameIntent, LatticeEditItemActivity.lattice_edit_name);
			break;
		case R.id.layout_price:
			Intent priceIntent = new Intent(this, LatticeEditItemActivity.class);
			priceIntent.putExtra("type", LatticeEditItemActivity.lattice_edit_price);
			priceIntent.putExtra("content", mPriceView.getTag() == null ? "" : String.valueOf(mPriceView.getTag()));
			startActivityForResult(priceIntent, LatticeEditItemActivity.lattice_edit_price);
			break;
		case R.id.layout_amount:
			Intent amountIntent = new Intent(this, LatticeEditItemActivity.class);
			amountIntent.putExtra("type", LatticeEditItemActivity.lattice_edit_amount);
			amountIntent.putExtra("content", mAmountView.getText().toString());
			startActivityForResult(amountIntent, LatticeEditItemActivity.lattice_edit_amount);
			break;
		case R.id.layout_trade_mode:
			Intent tradeModeIntent = new Intent(this, LatticeEditItemActivity.class);
			tradeModeIntent.putExtra("type", LatticeEditItemActivity.lattice_edit_trade_mode);
			tradeModeIntent.putExtra("content", mTradeModeView.getText().toString());
			startActivityForResult(tradeModeIntent, LatticeEditItemActivity.lattice_edit_trade_mode);
			break;
		case R.id.layout_description:
			Intent descriptionIntent = new Intent(this, LatticeEditItemActivity.class);
			descriptionIntent.putExtra("type", LatticeEditItemActivity.lattice_edit_description);
			descriptionIntent.putExtra("content", mDescriptionView.getText().toString());
			startActivityForResult(descriptionIntent, LatticeEditItemActivity.lattice_edit_description);
			break;
		case R.id.layout_photo:
			photoClick(v);
			break;
		}
	}

	private void findView() {
		mNameiew = (TextView) findViewById(R.id.txt_name);
		mPriceView = (TextView) findViewById(R.id.txt_price);
		mAmountView = (TextView) findViewById(R.id.txt_amount);
		mTradeModeView = (TextView) findViewById(R.id.txt_trade_mode);
		mDescriptionView = (TextView) findViewById(R.id.txt_description);
		mPhotoView = (ImageView) findViewById(R.id.img_photo);

		findViewById(R.id.layout_photo).setOnClickListener(this);
		findViewById(R.id.layout_name).setOnClickListener(this);
		findViewById(R.id.layout_price).setOnClickListener(this);
		findViewById(R.id.layout_amount).setOnClickListener(this);
		findViewById(R.id.layout_trade_mode).setOnClickListener(this);
		findViewById(R.id.layout_description).setOnClickListener(this);

//		mNameiew.setOnClickListener(this);
//		mPriceView.setOnClickListener(this);
//		mAmountView.setOnClickListener(this);
//		mTradeModeView.setOnClickListener(this);
//		mDescriptionView.setOnClickListener(this);
//		mPhotoView.setOnClickListener(this);
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

		ImageView image1 = (ImageView) mTopbar.findViewById(R.id.img_topbar_rightButton1);
		image1.setImageResource(R.drawable.topbar_sent);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);

		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText("添加卡片");
	}

}
