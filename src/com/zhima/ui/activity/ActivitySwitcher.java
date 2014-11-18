package com.zhima.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.data.model.UnknownObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.service.AccountService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.ui.common.view.HaloToast;
import com.zhima.ui.login.activity.LoginMainActivity;
import com.zhima.ui.main.activity.MainActivity;
import com.zhima.ui.space.activity.BusinessActivity;
import com.zhima.ui.space.activity.ElevatorActivity;
import com.zhima.ui.space.activity.IdolActivity;
import com.zhima.ui.space.activity.NoNetWorkActivity;
import com.zhima.ui.space.activity.UnknownActivity;
import com.zhima.ui.space.activity.VehicleActivity;
import com.zhima.ui.space.activity.ViewPluginBaseActivity;
import com.zhima.ui.space.activity.ZMProductActivity;

/**
 * @ClassName: ActivitySwitcher
 * @Description: 切换到新的activity
 * @author liubingsr
 * @date 2012-6-12 下午3:47:29
 * 
 */
public class ActivitySwitcher {

	public static void switchMainActivity(Context c) {
		Intent it = new Intent(c, MainActivity.class);
		c.startActivity(it);
	}

	/**
	 * @Title: switchActivity
	 * @Description: 切换到指定的页面
	 * @param c
	 * @param activity
	 * @return void
	 */
	public static void switchActivity(Context c, BaseActivity activity) {
		Intent it = new Intent(c, activity.getClass());
		c.startActivity(it);
	}

	/**
	 * @Title: openSpaceActivity
	 * @Description: 页面跳转
	 * @param context
	 * @param targetType
	 * @param zmCode
	 * @return void
	 */
	public static void openSpaceActivity(Context context, int targetType, String zmCode) {
		UnknownObject zmObject = new UnknownObject();
		zmObject.setZMCode(zmCode);
		zmObject.setZMObjectType(targetType);
		openSpaceActivity(context, zmObject, false);
	}

	/**
	 * @Title: openSpaceActivity
	 * @Description: 扫码页面跳转
	 * @param context
	 * @param zmObject
	 * @return void
	 */
	public static void openSpaceActivity(Context context, ZMObject zmObject, boolean isFinish) {
		//TODO 添加到缓存。如果在检索进入并没有添加到缓存
		ZMObject obj = ScanningcodeService.getInstance(context).getCacheZMObject(zmObject.getZMCode());
		if (obj == null) {
			ScanningcodeService.getInstance(context).addZMObject(zmObject);
		}
		doOpenSpaceActivity(context, zmObject.getRemoteId(), zmObject.getZMObjectType(), zmObject.getZMCode(), null,
				isFinish);
	}

	public static void openSpaceActivity(Context context, String zmCode, long targetId, int targetType,
			final boolean isFinish) {
		BaseActivity activity = (BaseActivity) context;
		ZMObject cacheZMObject = ScanningcodeService.getInstance(activity).getCacheZMObject(zmCode);
		if (cacheZMObject != null) {
			openSpaceActivity(activity, cacheZMObject, isFinish);
		} else {
			openSpaceActivity(activity, targetId, targetType, null, isFinish);
		}
	}

	public static void openSpaceActivity(final BaseActivity activity, long targetId, int targetType,
			final Bundle bundle, final boolean isFinish) {
		ZMObject cacheZMObject = ScanningcodeService.getInstance(activity).getCacheZMObject(targetId, targetType);
		if (cacheZMObject != null) {
			openSpaceActivity(activity, cacheZMObject, bundle, isFinish);
		} else {
			ScanningcodeService.getInstance(activity).getScanningInfo(targetId, targetType, new IHttpRequestCallback() {

				@Override
				public void onHttpStart(ProtocolHandlerBase protocol) {
					activity.startWaitingDialog(null, R.string.loading);
				}

				@Override
				public void onHttpResult(ProtocolHandlerBase protocol) {
					activity.dismissWaitingDialog();
					if (protocol.isHttpSuccess()) {
						if (protocol.isHandleSuccess()) {
							GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
							ZMObject zmObject = getzmProtocol.getZMObject();
							openSpaceActivity(activity, zmObject, bundle, isFinish);
						} else {
							HaloToast.show(activity, protocol.getProtocolErrorMessage());
						}
					} else {
						ErrorManager.showErrorMessage(activity);
					}
				}
			});
		}

	}

	/**
	 * @Title: openSpaceActivity
	 * @Description: 扫码页面跳转
	 * @param @param context
	 * @param @param zmObject
	 * @param @param bl 是否把当前页finish.
	 * @return void
	 * @throws
	 */
	public static void openSpaceActivity(Context context, ZMObject zmObject, Bundle bundle, boolean isFinish) {
		//TODO 添加到缓存。如果在检索进入并没有添加到缓存
//		ScanningcodeService.getInstance(context).addZMObject(zmObject);
		ZMObject obj = ScanningcodeService.getInstance(context).getCacheZMObject(zmObject.getZMCode());
		if (obj == null) {
			ScanningcodeService.getInstance(context).addZMObject(zmObject);
		}
		doOpenSpaceActivity(context, zmObject.getRemoteId(), zmObject.getZMObjectType(), zmObject.getZMCode(), bundle,
				isFinish);
	}

	private static void doOpenSpaceActivity(Context context, long targetId, int targetType, String zmCode,
			Bundle bundle, Boolean isFinish) {
		Intent it = null;
		switch (targetType) {
		case ZMObjectKind.BUSINESS_OBJECT:
			it = new Intent(context, BusinessActivity.class);
			break;
		case ZMObjectKind.IDOL_OBJECT:
			it = new Intent(context, IdolActivity.class);
			break;
		case ZMObjectKind.WEDDING_OBJECT:
			it = new Intent(context, ViewPluginBaseActivity.class);
			break;
		case ZMObjectKind.PUBLICPLACE_OBJECT:
			it = new Intent(context, ElevatorActivity.class);
			break;
		case ZMObjectKind.VEHICLE_OBJECT:
			it = new Intent(context, VehicleActivity.class);
			break;
		case ZMObjectKind.ZMPRODUCT_OBJECT:
			it = new Intent(context, ZMProductActivity.class);
			break;
		case ZMObjectKind.ORGANIZATION_OBJECT:
			it = new Intent(context, ViewPluginBaseActivity.class);
			break;
		case ZMObjectKind.VCARD_OBJECT:
			it = new Intent(context, ViewPluginBaseActivity.class);
			break;
		case ZMObjectKind.UNKNOWN_OBJECT:
			it = new Intent(context, UnknownActivity.class);
			it.putExtra(BaseActivity.ACTIVITY_EXTRA, zmCode);
			context.startActivity(it);
			if (bundle != null) {
				it.putExtra(BaseActivity.ACTIVITY_BUNDLE, bundle);
			}
			if (isFinish) {
				((Activity) context).finish();
			}
			return;
		default:
			//			if (!TextUtils.isEmpty(zmCode)) {
			// 无网络或解析失败
			it = new Intent(context, NoNetWorkActivity.class);
			it.putExtra(BaseActivity.ACTIVITY_EXTRA, zmCode);
			if (bundle != null) {
				it.putExtra(BaseActivity.ACTIVITY_BUNDLE, bundle);
			}
			context.startActivity(it);
			if (isFinish) {
				((Activity) context).finish();
			}
			return;
			//			}
		}
		if (it != null) {
			it.putExtra(BaseActivity.ACTIVITY_EXTRA, targetId);
			it.putExtra(BaseActivity.ACTIVITY_EXTRA2, targetType);
			if (bundle != null) {
				it.putExtra(BaseActivity.ACTIVITY_BUNDLE, bundle);
			}
			context.startActivity(it);
			if (isFinish) {
				((Activity) context).finish();
			}
		}
	}

	/**
	 * 检测是否登录 如果没有 自动跳进登录页面
	 * @Title: loginSwitch
	 * @Description: TODO
	 * @param context
	 * @return true 已经登录 false 没有登录 跳进登录页面
	 */
	public static boolean loginSwitch(Context context) {
		if (AccountService.getInstance(context).isLogin()) {
			return true;
		} else {
			Intent intent = new Intent(context, LoginMainActivity.class);
			context.startActivity(intent);
			return false;
		}
	}
}
