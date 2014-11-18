package com.zhima.ui.diary.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zhima.R;
import com.zhima.base.config.SystemConfig;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.JigsawView;
import com.zhima.ui.tools.TemporaryStorage;
import com.zhima.ui.usercenter.watchdog.activity.MessageChatActivity;

public class JigsawActivity extends BaseActivity {
	protected static final String TAG = "JigsawActivity";
	/** 画板 */
	private JigsawView mJigsaw;
	/** 改变模式 */
	private ViewGroup mModeGroup;
	/** 确定 */
	private ImageView mProcessImage;
	/** 添加 */
	private ImageView mAddImage;

	private TemporaryStorage mStorage;
	// 拼图模式
	private int[] two = new int[] { R.drawable.diary_combinaton_2_0_sele, R.drawable.diary_combinaton_2_1_sele };
	private int[] three = new int[] { R.drawable.diary_combinaton_3_0_sele, R.drawable.diary_combinaton_3_1_sele,
			R.drawable.diary_combinaton_3_2_sele, R.drawable.diary_combinaton_3_3_sele,
			R.drawable.diary_combinaton_3_4_sele, R.drawable.diary_combinaton_3_5_sele };
	private String filePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_jigsaw_activity);
		findView();
		init();
	}

	//初始化
	private void init() {
		mStorage = TemporaryStorage.getInstance();
		int count = mStorage.getImageList().size();
		//XXX
		count = 2;
		if (count == 2) {
			initPreview(two);
		} else if (count >= 3) {
			initPreview(three);
		} else {
			mJigsaw.setMode(JigsawView.MODE_AUTO);
		}
//		mJigsaw.setAdapter(mStorage.getImageList());
		int spItemCount = mJigsaw.getSupportItemCount();

		ArrayList<Bitmap> lists = new ArrayList<Bitmap>();
		Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
		Bitmap bp2 = BitmapFactory.decodeResource(getResources(), R.drawable.male_default);
		Bitmap bp3 = BitmapFactory.decodeResource(getResources(), R.drawable.female_default);
		lists.add(bp);
		lists.add(bp2);
		lists.add(bp3);
		mJigsaw.setAdapter(lists);
	}

	//初始化预览
	private void initPreview(int[] array) {
		for (int resId : array) {
			ImageView iv = new ImageView(this);
			iv.setImageResource(resId);
			iv.setTag(resId);
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int mode = (Integer) v.getTag();
					onChangeJigsawMode(mode);
				}
			});
			mModeGroup.addView(iv);
		}
	}

	//改变Jigsaw模式
	private void onChangeJigsawMode(int mode) {
		switch (mode) {
		case R.drawable.diary_combinaton_2_0_sele:
			mJigsaw.setMode(JigsawView.MODE_TWO_TOP);
			break;
		case R.drawable.diary_combinaton_2_1_sele:
			mJigsaw.setMode(JigsawView.MODE_TWO_LEFT);
			break;
		case R.drawable.diary_combinaton_3_0_sele:
			mJigsaw.setMode(JigsawView.MODE_THREE_RIGHT);
			break;
		case R.drawable.diary_combinaton_3_1_sele:
			mJigsaw.setMode(JigsawView.MODE_THREE_LEFT);
			break;
		case R.drawable.diary_combinaton_3_2_sele:
			mJigsaw.setMode(JigsawView.MODE_THREE_BOTTOM);
			break;
		case R.drawable.diary_combinaton_3_3_sele:
			mJigsaw.setMode(JigsawView.MODE_THREE_TOP);
			break;

		case R.drawable.diary_combinaton_3_4_sele:
			mJigsaw.setMode(JigsawView.MODE_THREE_VERTICAL);
			break;
		case R.drawable.diary_combinaton_3_5_sele:
			mJigsaw.setMode(JigsawView.MODE_THREE_HORIZONTAL);
			break;

		}
	}

//
//	// -----------------------------------------------------
//	private void cancelClick() {
//		finish();
//	}

	/*
	 * private void addClick() { HaloToast.show(this, "add"); }
	 */

//	private void processClick() {
//		if (mStorage.getImageList().size() < 3) {
//			String packageName = mStorage.getFromPackage();
//			if(CameraActivity.class.getName().equals(packageName)){
//				Intent it = new Intent();
//				it.setClassName(this, packageName);
//				it.putExtra(CameraActivity.ACTIVITY_CENTRE, new ZhimaComponentName(R.drawable.topbar_label,
//	                        new Intent(this, PreviewActivity.class)));
//				startActivity(it);
//				finish();
//			}else{
//				Intent it = new Intent();
//				it.setClassName(this, packageName);
//				it.putExtra(ACTIVITY_EXTRA, PreviewActivity.class.getName());
//				startActivity(it);
//				finish();
//			}
//		}
//	}

	private void positiveClick() {
		Bitmap bitmap = mJigsaw.getJigsawBitmap();
		if (bitmap != null) {
			long dateTaken = System.currentTimeMillis();
			String filename = dateTaken + ".jpg";
			filePath = FileHelper.getSysDcmiPath(filename);
			if (filePath != null) {
				if (GraphicUtils.saveBitmapFile(bitmap, CompressFormat.JPEG, SystemConfig.IMAGE_QUALITY, filePath)) {
					// 保存的照片文件uri
					Uri bitmapUri = MediaStoreHelper.insertImage(getContentResolver(), filename, filename, dateTaken,
							"image/jpeg", filePath);
					mStorage.setPhotoPath(filePath);
				}
			} else {
				HaloToast.show(this, "读取SD卡失败.请检查SD卡是否安装");
			}
		}

		Intent it = new Intent(this, MessageChatActivity.class);
		startActivity(it);
		finish();
	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.img_cancel:
//			cancelClick();
//			break;
//		case R.id.img_process:
//			processClick();
//			break;
//		case R.id.img_positive:
//			positiveClick();
//			break;
//		}
//	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mJigsaw != null){
			mJigsaw.recycle();
		}
	}

	private void findView() {
		mJigsaw = (JigsawView) findViewById(R.id.jsw_jigsaw);
		mModeGroup = (ViewGroup) findViewById(R.id.layout_preview);

		mProcessImage = (ImageView) findViewById(R.id.img_process);
		mAddImage = (ImageView) findViewById(R.id.img_add);

//		mProcessImage.setOnClickListener(this);
//		findViewById(R.id.img_cancel).setOnClickListener(this);
//		findViewById(R.id.img_positive).setOnClickListener(this);
	}
}
