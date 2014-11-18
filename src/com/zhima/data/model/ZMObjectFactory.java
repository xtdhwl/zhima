/* 
 * @Title: ZMObjectFactory.java
 * Created by liubingsr on 2012-12-18 下午2:06:35 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.logger.Logger;

/**
 * @ClassName: ZMObjectFactory
 * @Description: ZMObject工厂，用于创建ZMObject对象
 * @author liubingsr
 * @date 2012-12-18 下午2:06:35
 * 
 */
public class ZMObjectFactory {
	private final static String TAG = "ZMObjectFactory";

	/**
	 * @Title: create
	 * @Description: 创建ZMObject对象
	 * @param jsonObject
	 * @param objectType 对象类型
	 * @return null 创建失败
	 */
	public static ZMObject create(JSONObject jsonObject, int objectType) {
		ZMObject zmObject = null;
		switch (objectType) {
		case ZMObjectKind.BUSINESS_OBJECT:
			zmObject = new CommerceObject();
			break;
		case ZMObjectKind.ZMPRODUCT_OBJECT:
			zmObject = new ZMProductObject();
			break;
		case ZMObjectKind.PUBLICPLACE_OBJECT:
			zmObject = new PublicPlaceObject();
			break;
		case ZMObjectKind.VEHICLE_OBJECT:
			zmObject = new VehicleObject();
			break;
		case ZMObjectKind.IDOL_OBJECT:
			zmObject = new ZMIdolObject();
			break;
		case ZMObjectKind.WEDDING_OBJECT:
			zmObject = new ZMCouplesObject();
			break;
		case ZMObjectKind.ORGANIZATION_OBJECT:
			zmObject = new ZMOrganizationObject();
			break;
		case ZMObjectKind.VCARD_OBJECT:
			zmObject = new ZMCardObject();
			break;
		case ZMObjectKind.UNKNOWN_OBJECT:
			zmObject = new UnknownObject();
			break;
		default:
			zmObject = null;
			break;
		}
		if (zmObject != null) {
			try {
				zmObject.parse(jsonObject);
			} catch (JSONException e) {
				zmObject = null;
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}
			// if (zmObject != null) {
			// String json = jsonObject.toString();
			// zmObject.setJsonCompressed(false);
			// zmObject.setJson(json);
			// }
		}
		return zmObject;
	}
	public static ZMObject create(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		int objectType = ZMObjectKind.UNKNOWN_OBJECT;
		if (!jsonObject.isNull("spaceType")) {
			try {
				objectType = jsonObject.getInt("spaceType");
				return create(jsonObject, objectType);
			} catch (JSONException e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}			
		}
		return null;
	}
}
