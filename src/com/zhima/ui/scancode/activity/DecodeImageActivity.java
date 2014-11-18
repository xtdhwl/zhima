package com.zhima.ui.scancode.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.gdi.GraphicUtils;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.storage.FileHelper;
import com.zhima.base.storage.MediaStoreHelper;
import com.zhima.data.model.UnknownObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.common.view.ZhimaTopbar;
import com.zhima.ui.scancode.decoding.DecodeImageHandler;
import com.zhima.ui.scancode.result.VCardHandler;
import com.zhima.ui.scancode.result.VCardStore;
import com.zhima.ui.space.activity.VcardActivity;

/**
 * @ClassName: DecodeImageActivity
 * @Description: 解析图片二维码
 * @author luqilong
 * @date 2012-11-20 下午1:12:14
 */
public class DecodeImageActivity extends BaseActivity {

	private static final String TAG = DecodeImageActivity.class.getSimpleName();
	private static final int REQUEST_SELECT_PICTURE = 0;
	private static final int REQUEST_CROP_IMAGE = 1;

	/**解析失败*/
	private static final int MSG_DECODE_FAILE = 201;
	private static final int MSG_LOCAD_FAILE = 202;
	private static final int MSG_FILE_FAILE = 203;

	/** 图片View */
	private ImageView mBarcodeView;
	/**扫描结果*/
	private Result mDecodeResult;
	private Bitmap mBitmap;
	/**解码工具*/
	private DecodeImageHandler mDecodehandler;

	/** 图片真实路径*/
	private String mImagePath;
	/** 图片剪切后的路径*/
	private String mCropImagePath = FileHelper.getCacheFileRoot();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_DECODE_FAILE:
				sendSelectImageIntent();
				HaloToast.show(getApplicationContext(), "解析失败");
				break;
			case MSG_LOCAD_FAILE:
				sendSelectImageIntent();
				HaloToast.show(getApplicationContext(), "加载图片失败");
				break;
			case MSG_FILE_FAILE:
				sendSelectImageIntent();
				HaloToast.show(getApplicationContext(), "图片未找到");
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_decode_activity);
		setTopbar();
		findView();
		setListener();

		// 获取Intent判断是否有img——url
		Intent intent = getIntent();
		mImagePath = intent.getDataString();
		if (mImagePath == null) {
			sendSelectImageIntent();
		} else {
			process(mImagePath);
		}
	}

	//选择图片
	private void setListener() {
		//		mPromptView.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				sendSelectImageIntent();
		//			}
		//		});
	}

	//---------------------------------------------------------------------------
	//网络请求
	@Override
	public void onHttpResult(ProtocolHandlerBase protocol) {
		dismissWaitingDialog();
		ZMObject zmObject = null;
		if (protocol.isHttpSuccess()) {
			if (protocol.getProtocolType() == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
				// 获取扫码结果信息
				if (protocol.isHandleSuccess()) {
					GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
					zmObject = getzmProtocol.getZMObject();
					if (zmObject == null) {
						// 作为非自有码
						zmObject = new UnknownObject();
						zmObject.setZMCode(mDecodeResult.getText());
					}
				} else {
					// 解析失败 作为非自有码
					zmObject = new UnknownObject();
					zmObject.setZMCode(mDecodeResult.getText());
				}
			}
		} else {
			// 网络错误
			zmObject = new ZMObject();
			zmObject.setZMCode(mDecodeResult.getText());
			zmObject.setZMObjectType(ZMObjectKind.NONETWORK);
		}
		if (zmObject == null) {
			zmObject = new ZMObject();
			zmObject.setZMCode(mDecodeResult.getText());
			zmObject.setZMObjectType(ZMObjectKind.NONETWORK);
		}
		// 跳转到相应页面
		openSpaceActivity(this, zmObject);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 请求服务器前
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//选择图片请求码
		if (requestCode == REQUEST_SELECT_PICTURE) {
			if (resultCode == RESULT_OK) {
				// 得到真实路径
				HaloToast.show(this, "正在解析图片...");
				mImagePath = getImagePath(data.getData());
				process(mImagePath);
				//sendCropImageIntent(data.getData(), mCropImagePath);
			} else {
				openScanningActivity();
			}
		}

		//		if (requestCode == REQUEST_CROP_IMAGE) {
		//			if (resultCode == RESULT_OK) {
		//				//获得返回的数据
		//
		//				Bundle extras = data.getExtras();
		//				mBitmap = extras.getParcelable("data");
		//				if (mBitmap != null) {
		//					mBarcodeView.setImageBitmap(mBitmap);
		//					mDecodeResult = decodeBitmap(mBitmap);
		//					//解析成功：跳入扫描结果
		//					//解释失败：提示解析失败
		//					if (mDecodeResult != null) {
		//						handleDecode(mDecodeResult);
		//					} else {
		//						mHandler.sendEmptyMessage(MSG_DECODE_FAILE);
		//					}
		//				} else {
		//					mHandler.sendEmptyMessage(MSG_LOCAD_FAILE);
		//				}
		//			} else {
		//				mHandler.sendEmptyMessage(MSG_FILE_FAILE);
		//			}
		//		}
	}

	//---------------------------------------------------------------------.
	//方法

	/**解析结果处理*/
	public void handleDecode(Result result) {
		//判断是VCard则跳到VcardActivity，显现
		if (VCardHandler.isVCardResult(result)) {
			//包裹vcard并传递
			VCardHandler vcard = new VCardHandler();
			vcard.setResult(result);
//			vcard.setCharacterSet(mDecodehandler.getCharacter());
			VCardStore.getInstance().setVCardHandler(vcard);

			Intent vcardIntent = new Intent(this, VcardActivity.class);
			startActivity(vcardIntent);
			finish();
			return;
		}
		ZMObject zmObject = ScanningcodeService.getInstance(this).getCacheZMObject(result.getText());
		if (zmObject == null) {
			startWaitingDialog(null, getString(R.string.loading));
			ScanningcodeService.getInstance(this).getScanningInfo(result.getText(), this);
		} else {
			openSpaceActivity(this, zmObject);
		}
	}

	/**打开对应的空间*/
	private void openSpaceActivity(BaseActivity scanningActivity, ZMObject zmObject) {
		ActivitySwitcher.openSpaceActivity(this, zmObject,true);
	}

	/**打开扫码*/
	private void openScanningActivity() {
		Intent intent = new Intent(this, ScanningActivity.class);
		startActivity(intent);
		finish();
	}

	/** 通过Uri获取真实路径*/
	private String getImagePath(Uri uri) {
		// 获取图片地址：由于有第三方媒体库所以返回的Data，有两种结果 1:真是路径 2:媒体库数据库Uri 优先判断是否为数据库Uri
		String u = MediaStoreHelper.getImagePath(getContentResolver(), uri);
		if (u != null) {
			if (FileHelper.fileExists(u)) {
				return u;
			}
		}
		return uri.getPath();
	}

	/**如果按Back键打开扫码界面*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			openScanningActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**把文件生成Bitmap并进行解析*/
	private void process(String path) {
		if (path != null) {
			//原始
			Bitmap bp = getBitmap(path, screen_format);
			//如果原始bitmap加载失败就没有必要继续解析
			if (bp != null) {
				mBitmap = bp;
				mBarcodeView.setImageBitmap(mBitmap);
				mDecodeResult = decodeBitmap(mBitmap);
				//灰度
				if (mDecodeResult == null) {
					bp = getBitmap(path, gray_format);
					if (bp != null) {
						mBitmap.recycle();
						mBitmap = bp;
						mBarcodeView.setImageBitmap(mBitmap);
						mDecodeResult = decodeBitmap(mBitmap);
					}
				}
			}

			if (mBitmap == null) {
				mHandler.sendEmptyMessage(MSG_LOCAD_FAILE);
			} else {
				if (mDecodeResult != null) {
					handleDecode(mDecodeResult);
				} else {
					mHandler.sendEmptyMessage(MSG_DECODE_FAILE);
				}
			}
		} else {
			mHandler.sendEmptyMessage(MSG_FILE_FAILE);
		}
	}

	private static final int gray_format = 100;
	private static final int screen_format = 101;
	private static final int default_format = 102;

	//得到bitmap,
	private Bitmap getBitmap(String path, int format) {
		Bitmap formatBitmap = null;
		switch (format) {
		case gray_format:
			Bitmap bp = GraphicUtils.getScreenBitmap(this, path);
			if (bp != null) {
				formatBitmap = GraphicUtils.toGrayscale(bp);
				bp.recycle();
			}
			break;
		case screen_format:
			formatBitmap = GraphicUtils.getScreenBitmap(this, path);
			break;
		default:
			formatBitmap = GraphicUtils.getImage(DecodeImageActivity.this, Uri.parse(path));
		}
		return formatBitmap;
	}

	/**DecodeImageHandler进行解码 */
	private Result decodeBitmap(Bitmap bitmap) {
		mDecodehandler = new DecodeImageHandler(this);
		Result result = mDecodehandler.decode(bitmap);
		return result;
	}

	/**通过Intent剪切图片*/
	// 检测图片：120*120 提高识别率
	private void sendCropImageIntent(Uri scr, String des) {
		//----------------------------------------------
		//指定剪切
		//		File file = new File(scr);
		//		Intent intent = new Intent("com.android.camera.action.CROP");
		//		//		intent.setClassName("com.android.camera", "com.android.camera.CropImage");
		//		intent.setData(Uri.fromFile(file));
		//		intent.putExtra("aspectX", 1);
		//		intent.putExtra("aspectY", 1); // 剪切后的
		//		intent.putExtra("outputX", 96);
		//		intent.putExtra("outputY", 96);
		//		intent.putExtra("scale", true);
		//		intent.putExtra("noFaceDetection", true);
		//		intent.putExtra("output", Uri.parse("file://" + des));
		//		startActivityForResult(intent, REQUEST_CROP_IMAGE);

		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		cropIntent.setDataAndType(scr, "image/*");
		cropIntent.putExtra("crop", "true");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		cropIntent.putExtra("return-data", true);
		startActivityForResult(cropIntent, REQUEST_CROP_IMAGE);
	}

	/**通过Intent选择图片图片路径 */
	private void sendSelectImageIntent() {
		//		Intent it = new Intent();
		//		it.setType("image/*");
		//		it.setAction(Intent.ACTION_GET_CONTENT);
		//		startActivityForResult(it, REQUEST_SELECT_PICTURE);

		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_SELECT_PICTURE);
	}

	private void findView() {
		mBarcodeView = (ImageView) findViewById(R.id.img_photo);
		//		mPromptView = (Button) findViewById(R.id.btn_prompt);
	}

	/**设置标题栏*/
	private void setTopbar() {
		ZhimaTopbar topBar = (ZhimaTopbar) this.findViewById(R.id.ztop_bar_layout);
		LinearLayout ll_left = (LinearLayout) View.inflate(this, R.layout.topbar_leftview, null);
		topBar.addLeftLayoutView(ll_left);
		topBar.findViewById(R.id.layout_titlebar_leftButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				openScanningActivity();
			}
		});
		TextView titleText = ((TextView) topBar.findViewById(R.id.txt_topbar_title));
		titleText.setText("图片识别");
	}

}
