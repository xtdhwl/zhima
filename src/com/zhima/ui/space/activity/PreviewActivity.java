package com.zhima.ui.space.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.config.ResourceServerConfig;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;
import com.zhima.ui.tools.HttpImageLoader.ImageDownloadListener;

public class PreviewActivity extends BaseActivity implements ImageDownloadListener {

	public static final String ACTIVITY_URL = "url";
	private String mUrl;
	private ImageView mPhotoImg;
	private Bitmap mBitmap;
	private String mBitmapPath;
	private String type = ImageScaleType.ORIGINAL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.photo_preview_activity);
		mPhotoImg = (ImageView) findViewById(R.id.img_photo);

		setTopbar();

		findViewById(R.id.load).setVisibility(View.VISIBLE);
		String url = getIntent().getStringExtra(ACTIVITY_URL);
		String stringExtra = getIntent().getStringExtra("type");
		if (!TextUtils.isEmpty(stringExtra)) {
			type = stringExtra;
		}

		mUrl = ResourceServerConfig.getInstance().getRealImageUrl(url, type);//GraphicUtils.mScreenWidth
		HttpImageLoader.getInstance(getApplicationContext()).downloadImage(mUrl, type, this);
	}

	//	/** 保存图片 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean saveFalg = false;
			if (mBitmapPath != null) {
				String filePath = null;
				long dateTaken = System.currentTimeMillis();
				String filename = dateTaken + "." + FileHelper.getFileExtension(mBitmapPath);
				filePath = FileHelper.getSysDcmiPath(filename);
				if (filePath != null) {
					saveFalg = FileHelper.copyFile(mBitmapPath, filePath);
					if (saveFalg) {
						/* Uri bitmapUri = */MediaStoreHelper.insertImage(getContentResolver(), filename, filename,
								dateTaken, FileHelper.getMIME(mBitmapPath), filePath);
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
		}
	};
	private View mSaveView;

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
		image1.setImageResource(R.drawable.topbar_save);
		mSaveView = mTopbar.findViewById(R.id.layout_topbar_rightButton1);
		mSaveView.setOnClickListener(saveTopbarClick);
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		((TextView) mTopbar.findViewById(R.id.txt_topbar_title)).setText(R.string.picture);

	}

	@Override
	public void onReady(String uri, String localFilePath) {
		findViewById(R.id.load).setVisibility(View.GONE);
		mBitmap = GraphicUtils.getScreenBitmap(this, localFilePath);
		mBitmapPath = localFilePath;
		if (mBitmap != null) {
			mPhotoImg.setImageBitmap(mBitmap);
			mPhotoImg.setVisibility(View.VISIBLE);
		} else {
			mPhotoImg.setImageResource(R.drawable.default_1_image);
			mPhotoImg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onError(String msg, String uri) {
		findViewById(R.id.load).setVisibility(View.GONE);
		mPhotoImg.setImageResource(R.drawable.default_1_image);
		mPhotoImg.setVisibility(View.VISIBLE);
	}
}
