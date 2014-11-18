/* 
 * @Title: ZMConsts.java
 * Created by liubingsr on 2012-7-3 下午1:08:54 
 * Copyright (c) 2012 Zhima Information Technology Co., Ltd. All rights reserved.
 */
package com.zhima.base.consts;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.zhima.R;
import com.zhima.app.ZhimaApplication;
import com.zhima.plugin.ViewPluginDefine;
import com.zhima.plugin.ViewPluginDefine.PluginType;

/**
 * @ClassName: ZMConsts
 * @Description: 常量定义
 * @author liubingsr
 * @date 2012-7-3 下午1:08:54
 * 
 */
public class ZMConsts {
	public final static int ALL_ID = -1;
	/**
	 * 无效Id值
	 */
	public final static int INVALID_ID = -1;
	/**
	 * 无效经纬度
	 */
	public final static double INVALID_LATITUDE = 1024;
	/**
	 * 无效经纬度
	 */
	public final static double INVALID_LONGITUDE = 1024;

	/**
	 * @ClassName: ZMStats
	 * @Description: 统计项目定义
	 * @author liubingsr
	 * @date 2012-10-20 下午1:05:40
	 * 
	 */
	public final static class ZMStats {
		/**
		 * 扫码点击事件
		 */
		public final static String SCANNING_EVENT = "scanning_event";
		/**
		 * 非自有码点击知天使事件
		 */
		public final static String UNKNOW_IDOL_EVENT = "unknow_idol_event";
		/**
		 * 个人管家的点击事件
		 */
		public final static String MYZHIMA_EVENT = "myzhima_event";
		/**
		 * 通讯录点击事件
		 */
		public final static String CONTACT_EVENT = "contact_event";
		/**
		 * 短信分享点击事件
		 */
		public final static String SMS_SHARE_EVENT = "sms_share_event";
	}

	/**
	 * @ClassName: ContactType
	 * @Description: 通讯录类型
	 * @author liubingsr
	 * @date 2013-1-25 下午4:57:26
	 * 
	 */
	public final static class ContactType {
		/** 所有通讯录 */
		public final static int ALL = 0;
		/** 个人通讯录 */
		public final static int PERSONAL = 1;
		/** 空间通讯录 */
		public final static int SPACE = 2;
	}

	/**
	 * @ClassName: AccessTokenType
	 * @Description: token类型定义
	 * @author liubingsr
	 * @date 2013-1-17 上午11:35:00
	 * 
	 */
	public final static class AccessTokenType {
		/** 用户手动注册 */
		public final static int MANUAL = 1;
		/** 系统自动注册 */
		public final static int AUTO = 2;
		/** 第三方绑定 */
		public final static int THIRD = 3;
	}
	/**
	* @ClassName: LoginType
	* @Description: 登录方式
	* @author liubingsr
	* @date 2013-2-1 下午9:50:03
	*
	*/
	public final static class LoginType {
		/** 用户手动注册 */
		public final static String MANUAL = "manual";
		/** 系统自动注册 */
		public final static String AUTO = "auto";
	}
	/**
	 * @ClassName: ZMHttpHeader
	 * @Description: 需要添加的http header值
	 * @author liubingsr
	 * @date 2012-10-10 下午4:26:49
	 * 
	 */
	public final static class ZMHttpHeader {
		public final static String UA_VALUE = "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;zhima 1.3.0) AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1";
		public final static String ZM_PLATFORM = "zm-platform";
		public final static String ZM_INSTALLED_VERSION = "zm-installed-version";
		public final static String ZM_ACCESS_TOKEN_HEADER = "zm-access-token";
	}

	/**
	 * @ClassName: ImageScaleType
	 * @Description: 图片尺寸类型定义
	 * @author liubingsr
	 * @date 2012-10-9 下午5:02:27
	 * 
	 */
	public final class ImageScaleType {
		/**
		 * 小缩略图
		 */
		public final static String SMALL = "s";
		/**
		 * 中图
		 */
		public final static String MEDIUM = "m";
		/**
		 * sm小缩略图
		 */
		public final static String SM = "sm";
		/**
		 * 大图
		 */
		public final static String LARGE = "l";
		/**
		 * 超大图
		 */
		public final static String LARGE_LARGE = "xl";
		/**
		 * 原始图片
		 */
		public final static String ORIGINAL = "";
	}

	/**
	 * @ClassName: ImageQuality
	 * @Description: 图片质量
	 * @author liubingsr
	 * @date 2012-9-24 下午3:39:58
	 * 
	 */
	public final class ImageQuality {
		/**
		 * 高清
		 */
		public final static int HIGH = 0;
		/**
		 * 普通
		 */
		public final static int NORMAL = HIGH + 1;
		/**
		 * 低画质
		 */
		public final static int LOW = NORMAL + 1;
	}

	/**
	 * @ClassName: PersonRecordType
	 * @Description: 记录类型 1 (收藏) | 2 (个人空间，即日志+未知庶) | 3 (扫码记录) | 4 (浏览记录) | 5
	 *               (记账) | 6 (通讯录)|7(知天使)
	 * @author liubingsr
	 * @date 2012-9-23 下午2:58:32
	 * 
	 */
	public final class PersonRecordType {
		/**
		 * 收藏
		 */
		public final static int FAVORITE = 1;
		/**
		 * 个人管理(发表、转发过的内容)
		 */
		public final static int POST_CONTENT = 2;
		/**
		 * 扫码记录
		 */
		public final static int SCANNING = 3;
		/**
		 * 浏览记录
		 */
		public final static int VIEW_HISTORY = 4;
		/**
		 * 记账
		 */
		public final static int KEEP_ACCOUNT = 5;
		/**
		 * 通讯录
		 */
		public final static int CONTACT_ENTRY = 6;
		/**
		 * 知天使
		 */
		public final static int IDOL = 7;
		/**
		 * 喜印
		 */
		public final static int WEDDING = 8;
	}

	/**
	 * @ClassName: OrderBy
	 * @Description: 排序类型
	 * @author liubingsr
	 * @date 2012-7-3 下午1:20:28
	 * 
	 */
	public final class OrderBy {
		/**
		 * 最新
		 */
		public final static String NEW = "new";
		/**
		 * 最热
		 */
		public final static String HOT = "hot";
		/**
		 * 距离最近
		 */
		public final static String NEAR = "near";
	}

	/**
	 * @ClassName: ForwardFromType
	 * @Description: 转发来源类型
	 * @author liubingsr
	 * @date 2012-8-31 下午5:48:36
	 * 
	 */
	public final class ForwardFromType {
		/**
		 * 日记
		 */
		public final static int DIARY = 1;
		/**
		 * 未知庶帖子
		 */
		public final static int TRUEMAN_POST = 2;
		/**
		 * 公告
		 */
		public final static int NOTICE = 3;
	}

	/**
	 * @ClassName: NoticeType
	 * @Description: 公告类型
	 * @author liubingsr
	 * @date 2012-7-3 下午1:22:17
	 */
	public final static class NoticeKind {
		/**
		 * 官方公告
		 */
		public final static int OFFICAL = 1;
		/**
		 * 商家公告
		 */
		public final static int MERCHANT = 2;
		private final static Map<String, Integer> kindMap;

		static {
			kindMap = new HashMap<String, Integer>();
			kindMap.put("offical", OFFICAL);
			kindMap.put("space", MERCHANT);
		}

		public static int getNoticeType(String kind) {
			if (kindMap.containsKey(kind)) {
				return kindMap.get(kind);
			} else {
				return 0;
			}
		}
	}

	/**
	 * @ClassName: SendStatus
	 * @Description: 发送状态
	 * @author liubingsr
	 * @date 2012-8-29 下午2:47:51
	 * 
	 */
	public final class SendStatus {
		/**
		 * 发送成功
		 */
		public final static int SENT = 1;
		/**
		 * 发送失败
		 */
		public final static int FAILURE = 2;
	}

	/**
	 * @ClassName: DiaryPrivacyStatus
	 * @Description: 日志隐私设置
	 * @author liubingsr
	 * @date 2012-7-3 下午1:29:37
	 * 
	 */
	public final class DiaryPrivacyStatus {
		/**
		 * 私密(仅自己可见)
		 */
		public final static int PRIVATE = 1;
		/**
		 * 公开(好友可见)
		 */
		public final static int PUBLIC = 2;
//		/**
//		 * 公开(所有人可见)
//		 */
//		public final static int ALL = 3;
	}

	/**
	 * @ClassName:GenderType
	 * @Description:性别
	 * @author liqilong
	 * @date 2012-7-26 下午4:15:48
	 */
	public final static class GenderType {
		/** 男 */
		public final static String MALE = "M";
		/** 女 */
		public final static String FEMALE = "F";
		/** 未知 */
		public final static String UNKNOWN = "U";
		private final static Map<String, String> typeMap;
		static {
			typeMap = new HashMap<String, String>();
			typeMap.put(MALE, ZhimaApplication.getContext().getString(R.string.gender_male));
			typeMap.put(FEMALE, ZhimaApplication.getContext().getString(R.string.gender_female));
			typeMap.put(UNKNOWN, ZhimaApplication.getContext().getString(R.string.gender_unknown));
		}

		/**
		 * @Title: getGenderLabel
		 * @Description: 性别显示内容
		 * @param gender
		 * @return String
		 */
		public static String getGenderLabel(String gender) {
			if (TextUtils.isEmpty(gender)) {
				return typeMap.get(UNKNOWN);
			}
			if (typeMap.containsKey(gender.toUpperCase())) {
				return typeMap.get(gender.toUpperCase());
			} else {
				return typeMap.get(UNKNOWN);
			}
		}
		/**
		* @Title: getGenderImage
		* @Description: 性别类型得到代表图片
		* @param gender
		* @return
		* int
		*/
		public static int getGenderImage(String gender) {
			if (gender.equals(MALE)) {
				return R.drawable.male;
			} else if (gender.equals(FEMALE)) {
				return R.drawable.female;
			}
			return R.drawable.male;
		}
	}

	/**
	 * @ClassName: BloodGroup
	 * @Description: 血型
	 * @author liubingsr
	 * @date 2012-7-3 下午4:16:36
	 * 
	 */
	public final static class BloodGroup {
		public final static String A = "A";
		public final static String B = "B";
		public final static String AB = "AB";
		public final static String O = "O";
		public final static String UNKNOWN = "U";
		private final static Map<String, String> typeMap;

		static {
			typeMap = new HashMap<String, String>();
			typeMap.put(A, ZhimaApplication.getContext().getString(R.string.blood_a));
			typeMap.put(B, ZhimaApplication.getContext().getString(R.string.blood_b));
			typeMap.put(AB, ZhimaApplication.getContext().getString(R.string.blood_ab));
			typeMap.put(O, ZhimaApplication.getContext().getString(R.string.blood_o));
			typeMap.put(UNKNOWN, ZhimaApplication.getContext().getString(R.string.blood_unknown));
		}

		/**
		 * @Title: getBloodLabel
		 * @Description: 血型显示内容
		 * @param blood
		 * @return String
		 */
		public static String getBloodLabel(String blood) {
			if (TextUtils.isEmpty(blood)) {
				return typeMap.get(UNKNOWN);
			}
			if (typeMap.containsKey(blood.toUpperCase())) {
				return typeMap.get(blood.toUpperCase());
			} else {
				return typeMap.get(UNKNOWN);
			}
		}
	}

	/**
	 * @ClassName: SMSVerifyType
	 * @Description: 短信验证码类型
	 * @author liubingsr
	 * @date 2012-8-24 上午10:33:35
	 * 
	 */
	public final class SMSVerifyType {
		/**
		 * 注册
		 */
		public final static int REGISTER = 1;
		/**
		 * 重置密码
		 */
		public final static int RESET_PASSWORD = 2;
		/**
		 * 变更绑定邮箱
		 */
		public final static int CHANGE_EMAIL = 3;
		/**
		 * 变更绑定手机号
		 */
		public final static int CHANGE_MOBILE = 4;
	}

	/**
	 * @ClassName: ProtocolType
	 * @Description: 协议类型
	 * @author liubingsr
	 * @date 2012-5-31 上午11:11:19
	 * 
	 */
	public final class ProtocolType {
		/**
		 * 获取rest root url
		 */
		public final static int GET_SERVER_CONFIG_PROTOCOL = 0;
		/**
		 * 版本检查协议
		 */
		public final static int CHECK_APP_UPGRADE_PROTOCOL = GET_SERVER_CONFIG_PROTOCOL + 1;
		/**
		 * 检查字典更新协议
		 */
		public final static int CHECK_DICT_UPDATE_PROTOCOL = CHECK_APP_UPGRADE_PROTOCOL + 1;
		/**
		 * 下载文件协议
		 */
		public final static int DOWN_FILE_PROTOCOL = CHECK_DICT_UPDATE_PROTOCOL + 1;
		/**
		 * app启动上传信息
		 */
		public final static int APP_STARTUP_PROTOCOL = DOWN_FILE_PROTOCOL + 1;
		/**
		 * 提交意见反馈信息
		 */
		public final static int ADD_FEEDBACK_PROTOCOL = APP_STARTUP_PROTOCOL + 1;

//		/**
//		 * 手机号码是否已被注册
//		 */
//		public final static int MOBILE_IS_REGISTERED_PROTOCOL = LOGIN_PROTOCOL + 1;

//		/**
//		 * 请求发送短信验证码
//		 */
//		public final static int REQUEST_SEND_VERIFYCODE_PROTOCOL = USERNAME_IS_REGISTERED_PROTOCOL + 1;
//		/**
//		 * 修改密码
//		 */
//		public final static int CHANGE_PASSWORD_PROTOCOL = RESET_NEW_PASSWORD_PROTOCOL + 1;
//		/**
//		 * 重置密码_请求发送验证码
//		 */
//		public final static int REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL = CHANGE_PASSWORD_PROTOCOL + 1;

		/**
		 * 获取我的优惠券列表
		 */
		public final static int GET_MYCOUPON_LIST_PROTOCOL = ADD_FEEDBACK_PROTOCOL + 1;
		/**
		 * 删除一张优惠券
		 */
		public final static int DELETE_MYCOUPON_PROTOCOL = GET_MYCOUPON_LIST_PROTOCOL + 1;
		/**
		 * 删除所有优惠券
		 */
		public final static int DELETE_MY_ALL_COUPON_PROTOCOL = DELETE_MYCOUPON_PROTOCOL + 1;
		/**
		 * 获取扫码信息
		 */
		public final static int GET_ZMOBJECT_PROTOCOL = DELETE_MY_ALL_COUPON_PROTOCOL + 1;
		/**
		 * 商家介绍
		 */
		public final static int GET_COMPANY_INTRODUCTION_PROTOCOL = GET_ZMOBJECT_PROTOCOL + 1;
		/**
		 * 得到zmobject相册图片列表
		 */
		public final static int GET_ZMOBJECT_ALBUM_PROTOCOL = GET_COMPANY_INTRODUCTION_PROTOCOL + 1;
		/**
		 * 获取赞信息协议
		 */
		public final static int GET_ZMOBJECT_PRAISE_PROTOCOL = GET_ZMOBJECT_ALBUM_PROTOCOL + 1;
		/**
		 * 加"赞"协议
		 */
		public final static int DO_ZMOBJECT_PRAISE_PROTOCOL = GET_ZMOBJECT_PRAISE_PROTOCOL + 1;
		/**
		 * 获取官方公告列表协议
		 */
		public final static int GET_OFFICIAL_NOTICE_LIST_PROTOCOL = DO_ZMOBJECT_PRAISE_PROTOCOL + 1;
		/**
		 * 获取公告摘要列表
		 */
		public final static int GET_NOTICE_DIGEST_LIST_PROTOCOL = GET_OFFICIAL_NOTICE_LIST_PROTOCOL + 1;
		/**
		 * 获得知趣列表
		 */
		public final static int GET_JOKE_LIST_PROTOCOL = GET_NOTICE_DIGEST_LIST_PROTOCOL + 1;
		/**
		 * 获得知趣详情
		 */
		public final static int GET_JOKE_PROTOCOL = GET_JOKE_LIST_PROTOCOL + 1;
		/**
		 * 获取官方公告详情协议
		 */
		public final static int GET_OFFICIAL_NOTICE_PROTOCOL = GET_JOKE_PROTOCOL + 1;
		/**
		 * 获取商户公告列表协议
		 */
		public final static int GET_OWNER_NOTICE_LIST_PROTOCOL = GET_OFFICIAL_NOTICE_PROTOCOL + 1;
		/**
		 * 获取用户自推荐空间列表协议
		 */
		public final static int GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL = GET_OWNER_NOTICE_LIST_PROTOCOL + 1;
		/**
		 * 获取广场推荐空间列表协议
		 */
		public final static int GET_SQUARE_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL = GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL + 1;
		/**
		 * 芝麻空间底部的推荐列表
		 */
		public final static int GET_BOTTOM_RECOMMENDED_LIST_PROTOCOL = GET_SQUARE_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL + 1;
		/**
		 * 获取周边空间协议
		 */
		public final static int GET_NEAR_ZMOBJECT_LIST_PROTOCOL = GET_BOTTOM_RECOMMENDED_LIST_PROTOCOL + 1;
		/**
		 * 获取商户公告详情协议
		 */
		public final static int GET_OWNER_NOTICE_PROTOCOL = GET_NEAR_ZMOBJECT_LIST_PROTOCOL + 1;
		/**
		 * 获取商户商品列表协议
		 */
		public final static int GET_COMMERCE_PRODUCT_LIST_PROTOCOL = GET_OWNER_NOTICE_PROTOCOL + 1;
		/**
		 * 获取商户活动列表
		 */
		public final static int GET_COMMERCE_PROMOTION_LIST_PROTOCOL = GET_COMMERCE_PRODUCT_LIST_PROTOCOL + 1;
		/**
		 * 获取商户活动详情信息
		 */
		public final static int GET_COMMERCE_PROMOTION_PROTOCOL = GET_COMMERCE_PROMOTION_LIST_PROTOCOL + 1;
		/**
		 * 是否有该商户的优惠券
		 */
		public final static int CHECK_MYCOUPON_OF_COMMERCE_PROTOCOL = GET_COMMERCE_PROMOTION_PROTOCOL + 1;
		/**
		 * 领取优惠券协议
		 */
		public final static int GAIN_COUPON_PROTOCOL = CHECK_MYCOUPON_OF_COMMERCE_PROTOCOL + 1;
		/**
		 * 使用优惠券
		 */
		public final static int USE_COUPON_PROTOCOL = GAIN_COUPON_PROTOCOL + 1;
		/**
		 * 获取拥有的来自该商户的优惠券列表
		 */
		public final static int GET_MYCOUPON_OF_COMMERCE_LIST_PROTOCOL = USE_COUPON_PROTOCOL + 1;
		/**
		 * 获取知天使频道列表
		 */
		public final static int GET_IDOL_CHANNEL_LIST_PROTOCOL = GET_MYCOUPON_OF_COMMERCE_LIST_PROTOCOL + 1;
		/**
		 * 获取指定类型的频道列表
		 */
		public final static int GET_IDOL_CHANNEL_LIST_BY_KIND_PROTOCOL = GET_MYCOUPON_OF_COMMERCE_LIST_PROTOCOL + 1;
		/**
		 * 获取知天使影音列表
		 */
		public final static int GET_IDOL_MULTIMEDIA_LIST_PROTOCOL = GET_IDOL_CHANNEL_LIST_BY_KIND_PROTOCOL + 1;
		/**
		 * 评论知天使才艺
		 */
		public final static int ADD_IDOL_ACQIEREMENT_COMMENT_PROTOCOL = GET_IDOL_MULTIMEDIA_LIST_PROTOCOL + 1;
		/**
		 * 获取知天使才艺评论列表
		 */
		public final static int GET_IDOL_ACQIEREMENT_COMMENT_LIST_PROTOCOL = ADD_IDOL_ACQIEREMENT_COMMENT_PROTOCOL + 1;
		/**
		 * 向知天使提供话题线索
		 */
		public final static int PROVIDE_TOPIC_TO_IDOL_PROTOCOL = GET_IDOL_ACQIEREMENT_COMMENT_LIST_PROTOCOL + 1;
		/**
		 * 获取情侣知天使日志列表
		 */
		public final static int GET_COUPLES_JOURNAL_CHANNEL_LIST_PROTOCOL = PROVIDE_TOPIC_TO_IDOL_PROTOCOL + 1;
		/**
		 * 获取情侣知天使影像列表
		 */
		public final static int GET_COUPLES_MULTIMEDIA_LIST_PROTOCOL = GET_COUPLES_JOURNAL_CHANNEL_LIST_PROTOCOL + 1;
		/**
		 * 评论情侣知天使
		 */
		public final static int ADD_COUPLES_COMMENT_PROTOCOL = GET_COUPLES_MULTIMEDIA_LIST_PROTOCOL + 1;
		/**
		 * 获取情侣知天使评论列表
		 */
		public final static int GET_COUPLES_COMMENT_LIST_PROTOCOL = ADD_COUPLES_COMMENT_PROTOCOL + 1;

		/**
		 * 添加收藏协议
		 */
		public final static int ADD_FAVORITE_PROTOCOL = GET_COUPLES_COMMENT_LIST_PROTOCOL + 1;
		/**
		 * 商品纠错协议
		 */
		public final static int POST_CORRECTION_PROTOCOL = ADD_FAVORITE_PROTOCOL + 1;
		//
		/**
		 * 获取个人管理记录列表
		 */
		public final static int GET_USER_RECORD_LIST_PROTOCOL = POST_CORRECTION_PROTOCOL + 1;
		/**
		 * 删除个人管理
		 */
		public final static int DELETE_USER_RECORD_PROTOCOL = GET_USER_RECORD_LIST_PROTOCOL + 1;
		/**
		 * 添加通讯录
		 */
		public final static int ADD_CONTACT_PROTOCOL = DELETE_USER_RECORD_PROTOCOL + 1;
		/**
		 * 删除通讯录
		 */
		public final static int DELETE_CONTACT_PROTOCOL = ADD_CONTACT_PROTOCOL + 1;
		/**
		 * 获取通讯录数据
		 */
		public final static int GET_CONTACT_LIST_PROTOCOL = DELETE_CONTACT_PROTOCOL + 1;
		/**
		 * 同步空间类型字典协议
		 */
		public final static int SYNC_SPACEKIND_DICT_PROTOCOL = GET_CONTACT_LIST_PROTOCOL + 1;
		/**
		 * 同步地区字典协议
		 */
		public final static int SYNC_REGION_DICT_PROTOCOL = SYNC_SPACEKIND_DICT_PROTOCOL + 1;
		/**
		 * 同步知天使职业字典协议
		 */
		public final static int SYNC_IDOLJOB_DICT_PROTOCOL = SYNC_REGION_DICT_PROTOCOL + 1;
		/**
		 * 搜索空间协议
		 */
		public final static int SEARCH_SPACE_PROTOCOL = SYNC_IDOLJOB_DICT_PROTOCOL + 1;
		/**
		 * 搜索商家优惠券协议
		 */
		public final static int SEARCH_BUSINESS_COUPON_PROTOCOL = SEARCH_SPACE_PROTOCOL + 1;
		/**
		 * 检索空间协议
		 */
		public final static int QUERY_SPACE_PROTOCOL = SEARCH_BUSINESS_COUPON_PROTOCOL + 1;
		/**
		 * 检索商家优惠券协议
		 */
		public final static int QUERY_BUSINESS_COUPON_PROTOCOL = QUERY_SPACE_PROTOCOL + 1;
		/**
		 * 1.3芝麻空间广场_检索空间协议
		 */
		public final static int QUERY_ZMSPACE_PLAZASPACE_HOT_PROTOCOL = QUERY_BUSINESS_COUPON_PROTOCOL + 1;

		public static final int LOCATION_PROTOCOL = QUERY_ZMSPACE_PLAZASPACE_HOT_PROTOCOL + 1;

		//--------------新版空间接口新增接口
		/**
		 * 空间焦点图
		 */
		public static final int GET_SPACE_FOCUS_IMAGES_PROTOCOL = LOCATION_PROTOCOL + 1;
		/**
		 * 空间影像照片列表
		 */
		public static final int GET_SPACE_IMAGE_PHOTO_LIST_PROTOCOL = LOCATION_PROTOCOL + 1;
		/**
		 * 空间影像视频列表
		 */
		public static final int GET_SPACE_IMAGE_VIDEO_LIST_PROTOCOL = GET_SPACE_IMAGE_PHOTO_LIST_PROTOCOL + 1;
		/**
		 * 看看谁来过 访客记录
		 */
		public static final int GET_SPACE_VISITED_USER_LIST_PROTOCOL = GET_SPACE_IMAGE_VIDEO_LIST_PROTOCOL + 1;
		/**
		 * 看看谁来过 添加留言
		 */
		public static final int ADD_SPACE_VISITE_MESSAGE_PROTOCOL = GET_SPACE_VISITED_USER_LIST_PROTOCOL + 1;
		/**
		 * 看看谁来过 访客留言列表
		 */
		public static final int GET_SPACE_VISITOR_MESSAGE_LIST_PROTOCOL = ADD_SPACE_VISITE_MESSAGE_PROTOCOL + 1;

		//---------------登录-------------------

		/**
		 * 登录
		 */
		public final static int LOGIN_PROTOCOL = GET_SPACE_VISITOR_MESSAGE_LIST_PROTOCOL + 1;
		/**
		 * 用户名是否已经被注册
		 */
		public final static int USERNAME_IS_REGISTERED_PROTOCOL = LOGIN_PROTOCOL + 1;
		/**
		 * 设置新密码
		 */
		public final static int RESET_NEW_PASSWORD_PROTOCOL = USERNAME_IS_REGISTERED_PROTOCOL + 1;

		/**
		 * 用户注册
		 */
		public final static int REGISTER_PROTOCOL = RESET_NEW_PASSWORD_PROTOCOL + 1;
		/**
		 * 请求发送短信验证码
		 */
		public final static int REQUEST_SEND_VERIFYCODE_PROTOCOL = REGISTER_PROTOCOL + 1;
		/**
		 * 重置密码时请求下发短信验证码
		 */
		public final static int REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL = REQUEST_SEND_VERIFYCODE_PROTOCOL + 1;
//		/**
//		 * 请求发送激活邮件
//		 */
//		public final static int REQUEST_SEND_ACTIVATIONEMAIL_PROTOCOL = REQUEST_SEND_VERIFYCODE_PROTOCOL + 1;
//		/**
//		 * 重置密码时请求下发邮件
//		 */
//		public final static int REQUEST_SEND_RESET_EMAIL_PROTOCOL = REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL + 1;
//		/**
//		 * 邮箱是否已经注册
//		 */
//		public final static int EMAIL_IS_REGISTERED_PROTOCOL = MOBILE_IS_REGISTERED_PROTOCOL + 1;
		/**
		 * 请求检测验证码
		 */
		public final static int REQUEST_VALIDATE_VERIFYCODE_PROTOCOL = REQUEST_SEND_RESET_VERIFYCODE_PROTOCOL + 1;
		/**
		 * 检查手机号码是否已经注册
		 */
		public final static int MOBILE_IS_REGISTERED_PROTOCOL = REQUEST_VALIDATE_VERIFYCODE_PROTOCOL + 1;
		/**
		 * 注销登录
		 */
		public final static int LOGOUT_PROTOCOL = MOBILE_IS_REGISTERED_PROTOCOL + 1;
		/**
		 * 修改密码
		 */
		public final static int CHANGE_PASSWORD_PROTOCOL = LOGOUT_PROTOCOL + 1;
		/**
		 * 通过手机号码方式重置密码设置新的密码
		 */
		public final static int RESET_NEW_PASSWORD_BY_MOBILE_PROTOCOL = CHANGE_PASSWORD_PROTOCOL + 1;

		/** 通过第三方平台登录 */
		public final static int LOGIN_FROM3_PROTOCOL = RESET_NEW_PASSWORD_BY_MOBILE_PROTOCOL + 1;

		/** 第三方平台登录成功之后 绑定 芝麻账户 */
		public final static int LOGIN_BIND_EXTERNAL_USER = LOGIN_FROM3_PROTOCOL + 1;

		/** 用户自动注册 */
		public final static int REGISTER_USER_BY_AUTO = LOGIN_BIND_EXTERNAL_USER + 1;

		/** 获取用户（自己）绑定的空间列表 */
		public final static int GET_USER_BINDING_SPACES = REGISTER_USER_BY_AUTO + 1;

		/** 获取用户绑定的空间列表 */
		public final static int GET_BINDING_SPACES = GET_USER_BINDING_SPACES + 1;

//		/**
//		 * 获取用户信息
//		 */
//		public final static int GET_USERINFO_PROTOCOL = RESET_NEW_PASSWORD_BY_MOBILE_PROTOCOL + 1;
//		/**
//		 * 更新用户信息
//		 */
//		public final static int UPDATE_USERINFO_PROTOCOL = GET_USERINFO_PROTOCOL + 1;
//		/**
//		 * 更新用户头像
//		 */
//		public final static int UPDATE_USER_AVATAR_PROTOCOL = UPDATE_USERINFO_PROTOCOL + 1;
//		/**
//		 * 用户上传相册图片
//		 */
//		public final static int UPLOAD_USER_ALBUM_IMAGE_PROTOCOL = UPDATE_USER_AVATAR_PROTOCOL + 1;
//		/**
//		 * 获取用户相册图片
//		 */
//		public final static int GET_USER_ALBUM_IMAGE_PROTOCOL = UPLOAD_USER_ALBUM_IMAGE_PROTOCOL + 1;
//		/**
//		 * 删除用户相册图片
//		 */
//		public final static int DELETE_USER_ALBUM_IMAGE_PROTOCOL = GET_USER_ALBUM_IMAGE_PROTOCOL + 1;
//		/**
//		 * 获取用户格子铺物品列表
//		 */
//		public final static int GET_USER_LATTICESHOP_PRODUCT_LIST_PROTOCOL = DELETE_USER_ALBUM_IMAGE_PROTOCOL + 1;
//		/**
//		 * 获取用户格子铺物品信息
//		 */
//		public final static int GET_USER_LATTICESHOP_PRODUCT_PROTOCOL = GET_USER_LATTICESHOP_PRODUCT_LIST_PROTOCOL + 1;
//		/**
//		 * 添加格子铺物品
//		 */
//		public final static int ADD_USER_LATTICESHOP_PRODUCT_PROTOCOL = GET_USER_LATTICESHOP_PRODUCT_PROTOCOL + 1;
//		/**
//		 * 删除格子铺物品
//		 */
//		public final static int DELETE_USER_LATTICESHOP_PRODUCT_PROTOCOL = ADD_USER_LATTICESHOP_PRODUCT_PROTOCOL + 1;

		/**
		 * 获取用户信息
		 */
		public final static int GET_USERINFO_PROTOCOL = GET_BINDING_SPACES + 1;
		/**
		 * 更新用户信息
		 */
		public final static int UPDATE_USERINFO_PROTOCOL = GET_USERINFO_PROTOCOL + 1;
		/**
		 * 更新用户头像
		 */
		public final static int UPDATE_USER_AVATAR_PROTOCOL = UPDATE_USERINFO_PROTOCOL + 1;
		/**
		 * 用户上传相册图片
		 */
		public final static int UPLOAD_USER_ALBUM_IMAGE_PROTOCOL = UPDATE_USER_AVATAR_PROTOCOL + 1;
		/**
		 * 获取用户相册图片
		 */
		public final static int GET_USER_ALBUM_IMAGE_PROTOCOL = UPLOAD_USER_ALBUM_IMAGE_PROTOCOL + 1;
		/**
		 * 删除用户相册图片
		 */
		public final static int DELETE_USER_ALBUM_IMAGE_PROTOCOL = GET_USER_ALBUM_IMAGE_PROTOCOL + 1;
		/**
		 * 获取用户格子铺物品列表
		 */
		public final static int GET_USER_LATTICE_PRODUCT_LIST_PROTOCOL = DELETE_USER_ALBUM_IMAGE_PROTOCOL + 1;
		/**
		 * 获取用户格子铺物品信息
		 */
		public final static int GET_USER_LATTICE_PRODUCT_PROTOCOL = GET_USER_LATTICE_PRODUCT_LIST_PROTOCOL + 1;
		/**
		 * 添加格子铺物品
		 */
		public final static int ADD_USER_LATTICE_PRODUCT_PROTOCOL = GET_USER_LATTICE_PRODUCT_PROTOCOL + 1;
		/**
		 * 修改格子铺物品
		 */
		public final static int UPDATE_USER_LATTICE_PRODUCT_PROTOCOL = ADD_USER_LATTICE_PRODUCT_PROTOCOL + 1;
		/**
		 * 删除格子铺物品
		 */
		public final static int DELETE_USER_LATTICESHOP_PRODUCT_PROTOCOL = UPDATE_USER_LATTICE_PRODUCT_PROTOCOL + 1;
		public final static int DELETE_USER_LATTICE_PRODUCT_PROTOCOL = DELETE_USER_LATTICESHOP_PRODUCT_PROTOCOL + 1;
		/**
		 * 获取用户芝麻空间、相册、格子铺、好友、日志数量
		 */
		public final static int GET_USER_HAVINGS_PROTOCOL = DELETE_USER_LATTICE_PRODUCT_PROTOCOL + 1;

		/**
		 * 新日记
		 */
		public final static int DIARY_NEW_PROTOCOL = GET_USER_HAVINGS_PROTOCOL + 1;
		/**
		 * 获取日记详细信息
		 */
		public final static int GET_DIARY_PROTOCOL = DIARY_NEW_PROTOCOL + 1;
		/**
		 * 回复日记
		 */
		public final static int REPLY_DIARY_PROTOCOL = GET_DIARY_PROTOCOL + 1;
		/**
		 * 评论日记
		 */
		public final static int COMMENT_DIARY_PROTOCOL = REPLY_DIARY_PROTOCOL + 1;
		/**
		 * 转发日记
		 */
		public final static int FORWARD_DIARY_PROTOCOL = COMMENT_DIARY_PROTOCOL + 1;
		/**
		 * 删除日记
		 */
		public final static int DELETE_DIARY_PROTOCOL = FORWARD_DIARY_PROTOCOL + 1;
		/**
		 * 删除日记回复
		 */
		public final static int DELETE_DIARY_REPLY_PROTOCOL = DELETE_DIARY_PROTOCOL + 1;
		/**
		 * 获取日记评论列表
		 */
		public final static int GET_DIARY_REPLY_LIST_PROTOCOL = DELETE_DIARY_REPLY_PROTOCOL + 1;
		/**
		 * 获取日记列表
		 */
		public final static int GET_DIARY_LIST_PROTOCOL = GET_DIARY_REPLY_LIST_PROTOCOL + 1;

		/** 获取好友列表 */
		public final static int GET_FRIENDS_LIST_PROTOCOL = GET_DIARY_LIST_PROTOCOL + 1;

		/** 申请好友 */
		public final static int APPLICATION_FRIEND_PROTOCOL = GET_FRIENDS_LIST_PROTOCOL + 1;

		/** 修改好友申请请求状态 */
		public final static int UPDATE_FRIEND_PROTOCOL = APPLICATION_FRIEND_PROTOCOL + 1;

		/** 添加好友 */
		public final static int APPLY_FRIEND_PASSED_PROTOCOL = UPDATE_FRIEND_PROTOCOL + 1;

		/** 删除好友 */
		public final static int DELETE_FRIEND_PROTOCOL = APPLY_FRIEND_PASSED_PROTOCOL + 1;

		/** 查询是否为好友关系 */
		public final static int CHECK_FRIEND_PROTOCOL = DELETE_FRIEND_PROTOCOL + 1;

		/** 获取消息列表 */
		public final static int GET_MESSAGE_LIST_PROTOCOL = CHECK_FRIEND_PROTOCOL + 1;

		/** 获取未读消息个数 */
		public final static int GET_MESSAGE_COUNT_PROTOCOL = GET_MESSAGE_LIST_PROTOCOL + 1;

		/** 获取特定类型消息列表 */
		public final static int GET_MESSAGE_LIST_BY_TYPE_PROTOCOL = GET_MESSAGE_COUNT_PROTOCOL + 1;

		/** 查询当前用户与某好友（用户）的对话列表 */
		public final static int GET_FRIENDTALK_LIST_BY_USER_PROTOCOL = GET_MESSAGE_LIST_BY_TYPE_PROTOCOL + 1;

		/** 向好友（用户）发送站内信 */
		public final static int ADD_FRIEND_TALK_PROTOCOL = GET_FRIENDTALK_LIST_BY_USER_PROTOCOL + 1;

		/** 接受或忽略好友请求 */
		public final static int HANDLE_FRIEND_RELATIONSHIP_PROTOCOL = ADD_FRIEND_TALK_PROTOCOL + 1;

		/** 批量将特定类型的未读消息设为已读 */
		public final static int MARK_READ_MESSAGE_BY_TYPE_PROTOCOL = HANDLE_FRIEND_RELATIONSHIP_PROTOCOL + 1;

		/** 将指定类型的未读消息全部设为已读 */
		public final static int MARK_READ_ALLMESSAGE_BY_TYPE_PROTOCOL = MARK_READ_MESSAGE_BY_TYPE_PROTOCOL + 1;

		/** 将未读的某一好友的对话消息全部设为已读 */
		public final static int MARK_READ_LLFRIENDTALK_BY_USER_PROTOCOL = MARK_READ_ALLMESSAGE_BY_TYPE_PROTOCOL + 1;

		/** 单个删除特定类型的消息 */
		public final static int DELETE_MESSAGE_BY_TYPE_PROTOCOL = MARK_READ_LLFRIENDTALK_BY_USER_PROTOCOL + 1;

		/** 清空特定类型的消息 */
		public final static int DELETE_ALL_MESSAGE_BY_TYPE_PROTOCOL = DELETE_MESSAGE_BY_TYPE_PROTOCOL + 1;

		/** 单个删除与某好友（用户）的对话消息 */
		public final static int DELETE_FRIEND_TALK_BY_USER_PROTOCOL = DELETE_ALL_MESSAGE_BY_TYPE_PROTOCOL + 1;

		/** 清空与某好友（用户）的对话消息 */
		public final static int DELETE_ALL_FRIEND_TALK_BY_USER_PROTOCOL = DELETE_FRIEND_TALK_BY_USER_PROTOCOL + 1;

		/** 以关键字搜索个人管理记录 */
		public final static int SEARCH_USER_RECORD_PROTOCOL = DELETE_ALL_FRIEND_TALK_BY_USER_PROTOCOL + 1;

		/** 以关键字搜索卡片 */
		public final static int SEARCH_CARD_PROTOCOL = SEARCH_USER_RECORD_PROTOCOL + 1;

//
//		/**
//		 * 未知庶回帖
//		 */
//		public final static int REPLY_TRUEMAN_POST_PROTOCOL = GET_DIARYS_PROTOCOL + 1;
//		/**
//		 * 删除未知庶帖子
//		 */
//		public final static int DELETE_TRUEMAN_POST_PROTOCOL = REPLY_TRUEMAN_POST_PROTOCOL + 1;
//		/**
//		 * 删除未知庶帖子的回复
//		 */
//		public final static int DELETE_TRUEMAN_REPLY_PROTOCOL = DELETE_TRUEMAN_POST_PROTOCOL + 1;
//		/**
//		 * 获取未知庶板块下的头条列表
//		 */
//		public final static int GET_TRUEMAN_HEADLINE_POST_LIST_PROTOCOL = DELETE_TRUEMAN_REPLY_PROTOCOL + 1;
//		/**
//		 * 获取推荐的未知庶帖子列表
//		 */
//		public final static int GET_TRUEMAN_RECOMMENDED_POST_LIST_PROTOCOL = GET_TRUEMAN_HEADLINE_POST_LIST_PROTOCOL + 1;
//		/**
//		 * 获取未知庶某板块中的帖子
//		 */
//		public final static int GET_TRUEMAN_BOARD_POST_LIST_PROTOCOL = GET_TRUEMAN_RECOMMENDED_POST_LIST_PROTOCOL + 1;
//		/**
//		 * 获取未知庶帖子详情
//		 */
//		public final static int GET_TRUEMAN_POST_INFO_PROTOCOL = GET_TRUEMAN_BOARD_POST_LIST_PROTOCOL + 1;
//		/**
//		 * 获取未知庶帖子的回复
//		 */
//		public final static int GET_TRUEMAN_POST_REPLIES_PROTOCOL = GET_TRUEMAN_POST_INFO_PROTOCOL + 1;

//		/**
//		 * 看看谁来过 添加访问记录
//		 */
//		public final static int ADD_VISITEENTRY_PROTOCOL = GET_OWNER_NOTICE_PROTOCOL + 1;
//		/**
//		 * 看看谁来过 获取访客列表
//		 */
//		public final static int GET_VISITED_USER_LIST_PROTOCOL = ADD_VISITEENTRY_PROTOCOL + 1;
//		/**
//		 * 看看谁来过 获取访客留言列表
//		 */
//		public final static int GET_VISITOR_MESSAGE_LIST_PROTOCOL = GET_VISITED_USER_LIST_PROTOCOL + 1;
//		/**
//		 * 看看谁来过 访客发表留言
//		 */
//		public final static int ADD_VISITE_MESSAGE_PROTOCOL = GET_VISITOR_MESSAGE_LIST_PROTOCOL + 1;
//		/**
//		 * 获取Inbox未读消息总数协议
//		 */
//		public final static int GET_INBOX_MESSAGE_COUNT_PROTOCOL = DELETE_CONTACT_PROTOCOL + 1;
//		/**
//		 * 获取卡片夹中未读消息总数协议
//		 */
//		public final static int GET_CARD_UNREADMESSAGECOUNT_PROTOCOL = GET_INBOX_MESSAGE_COUNT_PROTOCOL + 1;
//		/**
//		 * 获取消息箱中未读消息总数协议
//		 */
//		public final static int GET_MESSAGEBOX_UNREADMESSAGECOUNT_PROTOCOL = GET_CARD_UNREADMESSAGECOUNT_PROTOCOL + 1;
//		/**
//		 * 得到MessageBox最新消息列表协议
//		 */
//		public final static int GET_MESSAGEBOX_MESSAGE_LIST_PROTOCOL = GET_MESSAGEBOX_UNREADMESSAGECOUNT_PROTOCOL + 1;
//		/**
//		 * 得到与某人的全部会话协议
//		 */
//		public final static int GET_CONVERSATIONS_WITH_USER_PROTOCOL = GET_MESSAGEBOX_MESSAGE_LIST_PROTOCOL + 1;
	}

	/**
	 * @ClassName: ProtocolStatus
	 * @Description: 服务器返回的处理结果
	 * @author liubingsr
	 * @date 2012-6-28 下午5:51:46
	 * 
	 */
	public final class ProtocolStatus {
		/**
		 * 结果执行成功: OK
		 */
		public final static int RESULT_SUCCESS = 0;
		/**
		 * 结果执行成功但结果集为空： No Content
		 */
		public final static int RESULT_SUCCESS_EMPTY = 1;
		/**
		 * 结果执行失败: Internal Server Error
		 */
		public final static int RESULT_ERROR = 100;
		/**
		 * 由于接口尚未实现而执行失败: Not Implemented
		 */
		public final static int RESULT_ERROR_NOT_IMPLEMENTED = 101;
		/**
		 * 由于接口被暂时停用而执行失败： Service Unavailable
		 */
		public final static int RESULT_ERROR_UNAVAILABLE = 103;
		/**
		 * 由于请求参数无效而失败：Bad Request
		 */
		public final static int RESULT_ERROR_BAD_REQUEST = 150;
		/**
		 * 由于未登录或没有权限而失败: Unauthorized
		 */
		public final static int RESULT_ERROR_UNAUTHORIZED = 0x151;
		/**
		 * 执行失败,发生异常(内部错误)
		 */
		public static final int RESULT_CODE_ERROR_WITH_EXCEPTION = 0x200;

		/**
		 * 日志被删除
		 */
		public static final int RESULT_DIARY_DELETED = 0x36000;//221184;
		/**
		 * 转发日志对象为空
		 */
		public static final int RESULT_DIARY_FORWARD_EMPTY_FAIL = -1;
		/**
		 * 转发日志对象不匹配
		 */
		public static final int RESULT_DIARY_FORWARD_MATCH_FAIL = -2;
		/**
		 * 转发日志不能被 XXX 引用
		 */
		public static final int RESULT_DIARY_INVALID_DELETED = -3;

		/**
		 * 用户账号尚未激活
		 */
		public final static int RESULT_ERROR_USER_NOT_ACTIVATED = 0x3BFFB;
		/**
		 * 用户不存在(帐号被删除、停用)
		 */
		public final static int RESULT_ERROR_USER_NOT_EXIST = 0x3BFFC;
	}

	/**
	 * @ClassName: ZMObjectKind
	 * @Description: zmobject对象类型
	 * @author liubingsr
	 * @date 2012-7-3 下午2:18:34
	 * 
	 */
	public final static class ZMObjectKind {
		/**
		 * 无网络
		 */
		public final static int NONETWORK = -1;
		/**
		 * 非知码对象
		 */
		public final static int UNKNOWN_OBJECT = TargetType.TARGET_TYPE_UNKNOWN_SPACE;
		/**
		 * 商户对象
		 */
		public final static int BUSINESS_OBJECT = TargetType.TARGET_TYPE_BUSINESS_SPACE;
		/**
		 * 公共场所对象(电梯、洗手间)
		 */
		public static final int PUBLICPLACE_OBJECT = TargetType.TARGET_TYPE_PUBLIC_SPACE;
		/**
		 * 交通工具对象
		 */
		public final static int VEHICLE_OBJECT = TargetType.TARGET_TYPE_VEHICLE_SPACE;
		/**
		 * 知天使对象
		 */
		public final static int IDOL_OBJECT = TargetType.TARGET_TYPE_IDOL_SPACE;
		/**
		 * 情侣知天使对象
		 */
		public final static int WEDDING_OBJECT = TargetType.TARGET_TYPE_WEDDING_SPACE;
		/**
		 * 机构印对象
		 */
		public final static int ORGANIZATION_OBJECT = TargetType.TARGET_TYPE_ORGANIZATION_SPACE;
		/**
		 * 名玺对象
		 */
		public final static int VCARD_OBJECT = TargetType.TARGET_TYPE_CARD_SPACE;
		/**
		 * 3000种商品对象
		 */
		public final static int ZMPRODUCT_OBJECT = TargetType.TARGET_TYPE_PRODUCT_SPACE;
		/**
		 * 801
		 */
		public final static int PERSON_801_OBJECT = TargetType.TARGET_TYPE_USER_INFO;

		private final static Map<String, Integer> kindMap;
		private final static Map<Integer, String> kindMap2;

		static {
			kindMap = new HashMap<String, Integer>();
			kindMap2 = new HashMap<Integer, String>();
			kindMap.put("businessspace", BUSINESS_OBJECT);
			kindMap.put("publicspace", PUBLICPLACE_OBJECT);
			kindMap.put("vehiclespace", VEHICLE_OBJECT);
			kindMap.put("idolspace", IDOL_OBJECT);
			kindMap.put("weddingspace", WEDDING_OBJECT);
			kindMap.put("organizationspace", ORGANIZATION_OBJECT);
			kindMap.put("cardspace", VCARD_OBJECT);
			kindMap.put("productspace", ZMPRODUCT_OBJECT);
			kindMap.put("plainspace", UNKNOWN_OBJECT);
			kindMap.put("801", PERSON_801_OBJECT);
			//
			kindMap2.put(BUSINESS_OBJECT, "business");
			kindMap2.put(PUBLICPLACE_OBJECT, "public");
			kindMap2.put(VEHICLE_OBJECT, "vehicle");
			kindMap2.put(IDOL_OBJECT, "idol");
			kindMap2.put(WEDDING_OBJECT, "wedding");
			kindMap2.put(ORGANIZATION_OBJECT, "organization");
			kindMap2.put(VCARD_OBJECT, "card");
			kindMap2.put(ZMPRODUCT_OBJECT, "product");
			kindMap2.put(UNKNOWN_OBJECT, "plain");
			kindMap2.put(PERSON_801_OBJECT, "user");
		}

		/**
		 * @Title: getZMObjectType
		 * @Description: zmobject对象类型
		 * @param kind
		 * @return int
		 */
		public static int getZMObjectType(String kind) {
			if (kindMap.containsKey(kind.toLowerCase())) {
				return kindMap.get(kind.toLowerCase());
			} else {
				return UNKNOWN_OBJECT;
			}
		}

		public static String getZMObjectType(int kindType) {
			if (kindMap2.containsKey(kindType)) {
				return kindMap2.get(kindType);
			} else {
				return "";
			}
		}
	}

	/**
	 * @ClassName: MessageType
	 * @Description: 消息类型定义
	 * @author liubingsr
	 * @date 2013-1-9 上午11:35:18
	 * 
	 */
	public final static class MessageType {
		/** 公告 */
		public final static String NOTICE = "notice";
		/** 系统通知 */
		public final static String SYSTEM = "system";
		/** 好友申请 */
		public final static String FRIEND_REQUEST = "friendRequest";
		/** 私信 */
		public final static String CONVERSATION = "friend";
	}

	/**
	 * @ClassName: MessageStatus
	 * @Description: 消息状态
	 * @author liubingsr
	 * @date 2013-1-22 下午4:10:25
	 * 
	 */
	public final static class MessageStatus {
		/** 未读 */
		public final static int UNREAD = 1;
		/** 已读 */
		public final static int READ = 2;
		/** 消息已处理 */
		public final static int DONE = 3;
	}

	/**
	 * @ClassName UpdateFriendRequsetStatus
	 * @Description TODO是否已经接受或忽略好友申请，取值为：0 (尚未处理) | 1 (已接受) | 2 (已忽略)
	 * @author jiang
	 * @date 2013-1-24 下午03:32:47
	 */
	public final class UpdateFriendRequsetStatus {
		/** 尚未处理 */
		public final static int UNDEAL = 0;
		/** 已接受 */
		public final static int AGREE = 1;
		/** 已忽略 */
		public final static int DECLINE = 2;
	}

	/**
	 * @ClassName: TargetType
	 * @Description:TargetType字段用于一个接口可以兼容多种业务的情况。比如"外部展示"，即可以服务于知天使空间作为才艺展示，又可以服务于日志作为外链展示
	 * @author liubingsr
	 * @date 2012-7-25 下午2:47:10
	 * 
	 */
	public final static class TargetType {
		// ==== 空间 ====
		/** 知天使职业，虚拟用(不用存数据库) */
		public final static int TARGET_TYPE_IDOL_JOB = 43;
		/** 非自有码空间 */
		public final static int TARGET_TYPE_UNKNOWN_SPACE = 0;
		/** 商业空间 */
		public final static int TARGET_TYPE_BUSINESS_SPACE = 1;
		/** 公共空间 */
		public final static int TARGET_TYPE_PUBLIC_SPACE = 2;
		/** 交通工具空间 */
		public final static int TARGET_TYPE_VEHICLE_SPACE = 3;
		/** 知天使空间 */
		public final static int TARGET_TYPE_IDOL_SPACE = 4;
		/** 情侣知天使空间 */
		public final static int TARGET_TYPE_WEDDING_SPACE = 5;
		/** 机构空间 */
		public final static int TARGET_TYPE_ORGANIZATION_SPACE = 6;
		/** 名玺空间 */
		public final static int TARGET_TYPE_CARD_SPACE = 7;
		/** 商品空间 */
		public final static int TARGET_TYPE_PRODUCT_SPACE = 21;
		// ==== 空间辅助 ====
		/** 产品 */
		public final static int TARGET_TYPE_PRODUCT = 101;
		/** 商业活动 */
		public final static int TARGET_TYPE_BUSINESS_ACTIVITY = 121;
		/** 看看谁来过 */
		public final static int TARGET_TYPE_VISITED_USER = 141;
		/** 官方公告 */
		public final static int TARGET_TYPE_OFFICIAL_NOTICE = 161;
		/** 空间公告 */
		public final static int TARGET_TYPE_SPACE_NOTICE = 162;
		/**
		 * 空间类型
		 */
		public final static int TARGET_TYPE_SPACE_KIND = 191;

		// ==== SNS ====
		/** 用户Home页 */
		public final static int TARGET_TYPE_USER_PROFILE = 200;
		/** 日志 */
		public final static int TARGET_TYPE_JOURNAL = 201;
		/** 日志回复 */
		public final static int TARGET_TYPE_JOURNAL_REPLY = 202;
		/** 微娱乐帖子 */
		public final static int TARGET_TYPE_MINIJOY_POST = 221;
		/**
		 * 微娱乐回复
		 */
		public final static int TARGET_TYPE_MINIJOY_REPLY = 222;
		/**
		 * 微娱乐版块
		 */
		public final static int TARGET_TYPE_MINIJOY_BOARD = 223;
		// ==== 个人管理 ====
		/**
		 * 记账记录
		 */
		public final static int TARGET_TYPE_BOOKKEEPING = 501;
		// ==== 用户相关 ====
		/**
		 * 用户信息
		 */
		public final static int TARGET_TYPE_USER_INFO = 801;
		// ==== 系统相关 ====
		/**
		 * 区域
		 */
		public final static int TARGET_TYPE_CITY = 901;

		public static String getTargetType(int kind) {
			switch (kind) {
			case TARGET_TYPE_BUSINESS_SPACE:
				return "商业空间";
			case TARGET_TYPE_PUBLIC_SPACE:
				return "公共空间";
			case TARGET_TYPE_VEHICLE_SPACE:
				return "交通工具空间";
			case TARGET_TYPE_IDOL_SPACE:
				return "知天使空间";
			case TARGET_TYPE_PRODUCT_SPACE:
				return "商品空间";
			case TARGET_TYPE_UNKNOWN_SPACE:
				return "非自有码空间";
			default:
				return "未知";
			}
		}
	}

	/**
	 * @ClassName: PraiseKind
	 * @Description: 赞类型定义
	 * @author liubingsr
	 * @date 2012-7-13 下午4:22:27
	 * 
	 */
	public final static class PraiseKind {
		/**
		 * 未知的
		 */
		public final static int UNKNOWN = 0;
		/**
		 * 环境
		 */
		public final static int ENVIRONMENT = UNKNOWN + 1;
		/**
		 * 服务
		 */
		public final static int SERVICE = ENVIRONMENT + 1;
		/**
		 * 质量
		 */
		public static final int QUALITY = SERVICE + 1;
		/**
		 * 喜欢
		 */
		public final static int LIKE = QUALITY + 1;
		/**
		 * 安全
		 */
		public final static int SECURITY = LIKE + 1;

		private final static Map<String, Integer> kindMap;
		// 类型 -->文字
		private final static Map<Integer, Integer> kindMap2;
		static {
			kindMap = new HashMap<String, Integer>();
			kindMap.put("environment", ENVIRONMENT);
			kindMap.put("service", SERVICE);
			kindMap.put("quality", QUALITY);
			kindMap.put("like", LIKE);
			kindMap.put("security", SECURITY);

			//类型对应文字
			kindMap2 = new HashMap<Integer, Integer>();
			kindMap2.put(ENVIRONMENT, R.string.environment);
			kindMap2.put(SERVICE, R.string.service);
			kindMap2.put(QUALITY, R.string.quality);
			kindMap2.put(LIKE, R.string.like);
			kindMap2.put(SECURITY, R.string.security);
		}

		/**
		 * @Title: getPraiseType
		 * @Description: 赞类型
		 * @param kind
		 * @return int
		 */
		public static int getPraiseType(String kind) {
			if (kindMap.containsKey(kind)) {
				return kindMap.get(kind);
			} else {
				return UNKNOWN;
			}
		}

		public static String getPraiseType(int kind) {
			switch (kind) {
			case ENVIRONMENT:
				return "environment";
			case SERVICE:
				return "service";
			case QUALITY:
				return "quality";
			case LIKE:
				return "like";
			default:
				return "unknown";
			}
		}

		public static int getPraiseLabel(int kind) {
			if (kindMap2.containsKey(kind)) {
				return kindMap2.get(kind);
			} else {
				return R.string.unknow;
			}
		}
	}

	/**
	 * @ClassName: PraiseKind
	 * @Description: 模块类型
	 */
	public final static class ViewPluginKind {

		private final static Map<String, ViewPluginDefine.PluginType> kindMap;
		private final static Map<String, Integer> styleMap;

		static {
			kindMap = new HashMap<String, PluginType>();
			//common
			kindMap.put("notice_plugin", PluginType.notice);
			//商业空间
//			kindMap.put("business_topbar_plugin", PluginType.business_topbar);
//			kindMap.put("business_product_plugin", PluginType.business_product);
//			kindMap.put("business_promotion_plugin", PluginType.business_promotion);
//			kindMap.put("business_recommended_plugin", PluginType.business_recommended);
//
//			//知天使空间
//			kindMap.put("idol_album_plugin", PluginType.idol_album);
//			kindMap.put("idol_topbar_plugin", PluginType.idol_topbar);
//			kindMap.put("idol_multimedia_plugin", PluginType.idol_multimedia);
//			kindMap.put("idol_acqierement_plugin", PluginType.idol_acqierement);
//			kindMap.put("idol_recommended_plugin", PluginType.idol_recommended);

//			kindMap.put("elevator_topbar_plugin", PluginType.elevator_topbar);
//			kindMap.put("elevator_joke_plugin", PluginType.elevator_joke);
			//喜印空间
			kindMap.put("couples_topbar_plugin", PluginType.couples_topbar);
			kindMap.put("couples_album_plugin", PluginType.couples_album);
			kindMap.put("couples_space_plugin", PluginType.couples_space);
			kindMap.put("couples_diary_plugin", PluginType.couples_diary);
			kindMap.put("couples_recommended_plugin", PluginType.couples_recommended);


			//誉玺日志——测试
			kindMap.put("reputation_topbar_plugin", PluginType.reputation_topbar);
			kindMap.put("reputation_album_plugin", PluginType.reputation_album);
			kindMap.put("reputation_space_plugin", PluginType.reputation_space);
			kindMap.put("fame_diary_plugin", PluginType.fame_diary);
			kindMap.put("reputation_recommended_plugin", PluginType.reputation_recommended);
			
			//名玺
			kindMap.put("zmcard_topbar_plugin", PluginType.zmcard_topbar);
			kindMap.put("zmcard_album_plugin", PluginType.zmcard_album);
			kindMap.put("zmcard_space_plugin", PluginType.zmcard_space);
			kindMap.put("zmcard_diary_plugin", PluginType.zmcard_diary);
			kindMap.put("zmcard_recommended_plugin", PluginType.zmcard_recommended);
		}

		static {
			styleMap = new HashMap<String, Integer>();
			styleMap.put("couples_style", R.layout.plugin_style_couples);
			styleMap.put("reputation_style", R.layout.plugin_style_reputation);
			styleMap.put("zmcard_style", R.layout.plugin_style_zmcard);
		}

		/**
		 * @Title: getViewPluginType
		 * @Description: 模块类型
		 */
		public static ViewPluginDefine.PluginType getViewPluginType(String kind) {
			if (kindMap.containsKey(kind)) {
				return kindMap.get(kind);
			} else {
				return ViewPluginDefine.PluginType.unknown;
			}
		}

		/**
		 * @Title: getStyle
		 * @Description: 获得样式
		 */
		public static int getStyle(String kind) {
			if (styleMap.containsKey(kind)) {
				return styleMap.get(kind);
			} else {
				return -1;
			}
		}
	}
}
