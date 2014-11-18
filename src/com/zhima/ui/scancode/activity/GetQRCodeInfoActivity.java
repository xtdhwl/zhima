package com.zhima.ui.scancode.activity;

import android.content.Intent;
import android.os.Bundle;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.data.model.UnknownObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.activity.ActivitySwitcher;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

/**
* @ClassName: GetQRCodeInfoActivity
* @Description: 通过Intent获取Uri，跳转到对应的空间
* @author luqilong
* @date 2012-12-28 上午10:31:19
*
 */
public class GetQRCodeInfoActivity extends BaseActivity {

	private String mContent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mContent = intent.getDataString();
		if(mContent != null){
			startWaitingDialog(null, R.string.loading);
			getServerData(mContent);
		}else{
			HaloToast.show(getApplicationContext(), "内容不能为空!");
			finish();
		}
		//		Toast.makeText(getApplicationContext(), "Actiont:" + intent.getAction() + ",Url:" + intent.getDataString(), 1)
		//				.show();
	}
	
//	/**解析结果处理*/
//	public void handleDecode(Result result) {
//		//判断是VCard则跳到VcardActivity，显现
//		if (VCardHandler.isVCardResult(result)) {
//			//包裹vcard并传递
//			VCardHandler vcard = new VCardHandler();
//			vcard.setResult(result);
////			vcard.setCharacterSet("ISO88591");
//			VCardStore.getInstance().setVCardHandler(vcard);
//
//			Intent vcardIntent = new Intent(this, VcardActivity.class);
//			startActivity(vcardIntent);
//			finish();
//			return;
//		}
//		
//	}
	
	private void getServerData(String context){
		ZMObject zmObject = ScanningcodeService.getInstance(this).getCacheZMObject(context);
		if (zmObject == null) {
			startWaitingDialog(null, getString(R.string.loading));
			ScanningcodeService.getInstance(this).getScanningInfo(context, this);
		} else {
			openSpaceActivity(this, zmObject);
		}
	}

	/**打开对应的空间*/
	private void openSpaceActivity(BaseActivity scanningActivity, ZMObject zmObject) {
		ActivitySwitcher.openSpaceActivity(this, zmObject,true);
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
						zmObject.setZMCode(mContent);
					}
				} else {
					// 解析失败 作为非自有码
					zmObject = new UnknownObject();
					zmObject.setZMCode(mContent);
				}
			}
		} else {
			// 网络错误
			zmObject = new ZMObject();
			zmObject.setZMCode(mContent);
			zmObject.setZMObjectType(ZMObjectKind.NONETWORK);
		}
		if (zmObject == null) {
			zmObject = new ZMObject();
			zmObject.setZMCode(mContent);
			zmObject.setZMObjectType(ZMObjectKind.NONETWORK);
		}
		// 跳转到相应页面
		openSpaceActivity(this, zmObject);
	}

	@Override
	public void onHttpStart(ProtocolHandlerBase protocol) {
		// TODO 请求服务器前
	}
}
