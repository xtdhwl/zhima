package com.zhima.ui.space.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ImageScaleType;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.ZMSpaceService;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.adapter.PhotoAdapter;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZMGallery;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.tools.HttpImageLoader;

/**
 * @ClassName:PhotoActivity
 * @Description:全屏预览相册.
 * @author liqilong
 * @date 2012-8-22 下午6:38:11
 * 
 */
public class PhotoActivity extends BaseActivity {
	protected static final String TAG = "PhotoActivity";
	// 传递ZMObjectImage 的id
	public static final String ACTIVITY_POSITION = "activity_position";
	public static final String IMAGE_SCALE_TYPE = "imageScaleType";
	public static final String IS_ZMSPACE = "is_zmspace";
	private ZMGallery mGallery;

	private RefreshListData<ZMObjectImage> mZMObjectImageListData;
	private ArrayList<ZMObjectImage> mImageList;

	private PhotoAdapter mPhotoAdapter;
	private int position;
	private long zmObjectId;

	private TextView mTitleText;
	private boolean isSpace;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_photo_activity);
		setTopbar();
		mGallery = (ZMGallery) findViewById(R.id.gal_photo);
		//传递 id 和 位置
		Intent intent = getIntent();
		zmObjectId = intent.getLongExtra(ACTIVITY_EXTRA, -1);
		position = intent.getIntExtra(ACTIVITY_POSITION, 0);
		isSpace = intent.getBooleanExtra(IS_ZMSPACE, false);

		if(!isSpace){
			mZMObjectImageListData = ScanningcodeService.getInstance(this).getCacheZMObjectAlbumList(zmObjectId);
		}else{
			mZMObjectImageListData = ZMSpaceService.getInstance(this).getCacheImagePhotoList(zmObjectId);
		}
		mImageList = mZMObjectImageListData.getDataList();
		if(isSpace){
			mPhotoAdapter = new PhotoAdapter(this, mImageList, intent.getStringExtra(IMAGE_SCALE_TYPE));
		}else{
			mPhotoAdapter = new PhotoAdapter(this, mImageList);
		}
		mGallery.setAdapter(mPhotoAdapter);
		if (mImageList.size() > position) {
			mGallery.setSelection(position);
		}
		setListener();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mPhotoAdapter != null) {
			mPhotoAdapter.recycle();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPhotoAdapter != null) {
			mPhotoAdapter = null;
		}
	}

	private void setListener() {
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setTopbarTitle(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	//	/** 保存图片 */
	private View.OnClickListener saveTopbarClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean saveFalg = false;
			int position = mGallery.getSelectedItemPosition();
			if (mPhotoAdapter.getCount() > position) {
				ZMObjectImage zmobj = mPhotoAdapter.getItem(position);
				String imageUrl = zmobj.getImageUrl();
				String localImagePath = HttpImageLoader.getInstance(PhotoActivity.this).getLocalImagePath(imageUrl, ImageScaleType.ORIGINAL);
				if (localImagePath != null) {
					String filePath = null;
					long dateTaken = System.currentTimeMillis();
					String filename = dateTaken + "." + FileHelper.getFileExtension(imageUrl);
					filePath = FileHelper.getSysDcmiPath(filename);
					if (filePath != null) {
						saveFalg = FileHelper.copyFile(localImagePath, filePath);
						if (saveFalg) {
							/*Uri bitmapUri =*/MediaStoreHelper.insertImage(getContentResolver(), filename, filename,
									dateTaken, FileHelper.getMIME(imageUrl), filePath);
						}
					} else {
						HaloToast.show(getApplicationContext(), R.string.sd_error);
					}
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

	private void setTopbarTitle(int position) {
		mTitleText.setText(getString(R.string.album) + " " + (position + 1) + "/"
				+ mZMObjectImageListData.getDataList().size());
	}

	private void setTopbar() {
		// 标题栏
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
		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setOnClickListener(saveTopbarClick);

		mTopbar.findViewById(R.id.layout_topbar_rightButton1).setVisibility(View.VISIBLE);
		mTitleText = ((TextView) mTopbar.findViewById(R.id.txt_topbar_title));
		mTitleText.setText(R.string.picture);
	}
}
