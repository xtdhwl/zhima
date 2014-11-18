package com.zhima.data.service;

import java.util.Set;
import java.util.TreeMap;

import android.content.Context;

import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.protocol.UserProtocolHandler.AddLatticeProductProtocol;
import com.zhima.base.protocol.UserProtocolHandler.DeleteAlbumImageProtocol;
import com.zhima.base.protocol.UserProtocolHandler.DeleteLatticeProductProtocol;
import com.zhima.base.protocol.UserProtocolHandler.DeleteMyCouponProtocol;
import com.zhima.base.protocol.UserProtocolHandler.DeleteUserRecordProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetAlbumImageListProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetLatticeProductListProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetLatticeProductProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetMyCouponListProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetUserHavingsProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetUserInfoProtocol;
import com.zhima.base.protocol.UserProtocolHandler.GetUserRecordListProtocol;
import com.zhima.base.protocol.UserProtocolHandler.SearchCardProtocol;
import com.zhima.base.protocol.UserProtocolHandler.SearchUserRecordProtocol;
import com.zhima.base.protocol.UserProtocolHandler.UpdateLatticeProductProtocol;
import com.zhima.base.protocol.UserProtocolHandler.UpdateUserAvatarProtocol;
import com.zhima.base.protocol.UserProtocolHandler.UpdateUserInfoProtocol;
import com.zhima.base.protocol.UserProtocolHandler.UploadAlbumImageProtocol;
import com.zhima.data.model.LatticeProduct;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.User;
import com.zhima.data.model.UserAlbumImage;
import com.zhima.data.model.UserCoupon;
import com.zhima.data.model.UserRecordEntry;
import com.zhima.db.dbcontroller.UserDbController;

public class UserService extends BaseService {
	private final static String TAG = "UserService";
	private static UserService mInstance = null;

	private UserDbController mUserDbController;
	private RefreshListData<User> mUserList = new RefreshListData<User>();
	private RefreshListData<UserRecordEntry> mUserRecordList = new RefreshListData<UserRecordEntry>();
	RefreshListData<UserCoupon> mUserCouponList = new RefreshListData<UserCoupon>();

	private TreeMap<Long, RefreshListData<UserCoupon>> mMyCouponMap;
	private TreeMap<Long, RefreshListData<UserAlbumImage>> mUserAlbumImageMap;
	private TreeMap<Long, RefreshListData<LatticeProduct>> mUserLatticeProductMap;

	private TreeMap<Integer, RefreshListData<UserRecordEntry>> mUserRecordMap;

	private UserService(Context c) {
		super(c);
		mUserDbController = new UserDbController(mContext);
		mMyCouponMap = new TreeMap<Long, RefreshListData<UserCoupon>>();
		mUserAlbumImageMap = new TreeMap<Long, RefreshListData<UserAlbumImage>>();
		mUserLatticeProductMap = new TreeMap<Long, RefreshListData<LatticeProduct>>();
		mUserRecordMap = new TreeMap<Integer, RefreshListData<UserRecordEntry>>();
	}

	public static UserService getInstance(Context c) {
		if (mInstance == null) {
			mInstance = new UserService(c);
			mInstance.onCreate();
		}
		return mInstance;
	}

	public void logout() {
	}

	@Override
	public void onCreate() {
		mUserList = new RefreshListData<User>(mUserDbController.reloadAllData());
	}

	/**
	 * @Title: addUser
	 * @Description: 添加到列表和sqlite
	 * @param user
	 * @return void
	 */
	public void addUser(User user) {
		mUserList.add(user);
		if (this.isMySelf(user.getUserId())) {
			AccountService.getInstance(mContext).saveMyAccountData(user);
		}
		// mUserDbController.updateData(user);
	}

	/**
	 * @Title: updateUser
	 * @Description: 更新本地缓存的用户信息
	 * @param user
	 *            用户
	 * @return void
	 */
	public void updateUser(User user) {
		mUserList.add(user);
	}

	public User getUser(long userId) {
		return mUserList.getData(userId);
	}

	public void reloadAllUser() {
		mUserDbController = new UserDbController(mContext);
		mUserList = new RefreshListData<User>();// mUserDbController.reloadAllData()
	}
	//
	/**
	 * @Title: getUserInfo
	 * @Description: 获取用户信息
	 * @param userId
	 *            用户id
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getUserInfo(long userId, IHttpRequestCallback callBack) {
		GetUserInfoProtocol protocol = new GetUserInfoProtocol(mContext, callBack);
		protocol.getUserInfo(userId);
	}

	/**
	 * @Title: getMyself
	 * @Description: 得到本机登录用户对应的User对象
	 * @return User
	 */
	public User getMyself() {
		long userId = AccountService.getInstance(mContext).getUserId();
		return mUserList.getMap().get(userId);
	}

	/**
	 * @Title: isMySelf
	 * @Description: 是否是用户自己
	 * @param userId
	 * @return boolean
	 */
	public boolean isMySelf(long userId) {
		return userId == AccountService.getInstance(mContext).getUserId();
	}

	/**
	 * @Title: getMyself
	 * @Description: 获取当前登录用户信息
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getMyself(IHttpRequestCallback callBack) {
		GetUserInfoProtocol protocol = new GetUserInfoProtocol(mContext, callBack);
		protocol.getMyself();
	}

	/**
	 * @Title: updateMyselfInfo
	 * @Description: 更新当前登录用户信息
	 * @param user
	 *            用户信息
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void updateMyselfInfo(User user, IHttpRequestCallback callBack) {
		if (user == null) {
			return;
		}
		UpdateUserInfoProtocol protocol = new UpdateUserInfoProtocol(mContext, callBack);
		protocol.updateMyselfInfo(user);
	}

	/**
	 * @Title: updateAvatar
	 * @Description: 更新本地缓存的用户头像
	 * @param avatarUrl
	 *            新头像url
	 * @return void
	 */
	public void updateAvatar(String avatarUrl) {
		User my = getMyself();
		if (my != null) {
			my.setImageUrl(avatarUrl);
			AccountService.getInstance(mContext).setImageUrl(avatarUrl);
		}
	}

	/**
	 * @Title: updateUserAvatar
	 * @Description: 更新用户头像
	 * @param imagePath
	 *            本地图片文件地址
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void updateUserAvatar(String imagePath, IHttpRequestCallback callBack) {
		UpdateUserAvatarProtocol protocol = new UpdateUserAvatarProtocol(mContext, callBack);
		protocol.updateAvatar(imagePath);
	}

	/**
	 * @Title: getMyCacheCouponList
	 * @Description: 从本地缓存获取我的优惠券列表
	 * @return
	 */
	public RefreshListData<UserCoupon> getMyCacheCouponList() {
		long userId = AccountService.getInstance(mContext).getUserId();
		if (mMyCouponMap.containsKey(userId)) {
			return mMyCouponMap.get(userId);
		} else {
			return new RefreshListData<UserCoupon>();
		}
	}

	/**
	 * @Title: getMyCouponList
	 * @Description: 获取我的优惠券列表
	 * @param refreshed
	 *            是否刷新数据
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getMyCouponList(boolean refreshed, IHttpRequestCallback callBack) {
		long userId = AccountService.getInstance(mContext).getUserId();
		RefreshListData<UserCoupon> list = null;
		if (!mMyCouponMap.containsKey(userId)) {
			list = new RefreshListData<UserCoupon>();
			mMyCouponMap.put(userId, list);
		} else {
			list = mMyCouponMap.get(userId);
		}
		GetMyCouponListProtocol protocol = new GetMyCouponListProtocol(mContext, refreshed, list, callBack);
		protocol.getMyCouponList();
	}

	public void clearCacheCoupon() {
		mMyCouponMap.clear();
	}

	public void removeCacheCoupon(long couponId) {
		long userId = AccountService.getInstance(mContext).getUserId();
		if (mMyCouponMap.containsKey(userId)) {
			mMyCouponMap.get(userId).removeObject(couponId);
		}
	}

	/**
	 * @Title: deleteMyCoupon
	 * @Description: 删除优惠券
	 * @param couponId
	 *            要删除的优惠券id
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void deleteMyCoupon(long couponId, IHttpRequestCallback callBack) {
		DeleteMyCouponProtocol protocol = new DeleteMyCouponProtocol(mContext, callBack);
		protocol.deleteMyCoupon(couponId);
	}

	// ==================================
	// 个人管理接口
	/**
	 * @Title: getCacheUserRecordList
	 * @Description:从本地缓存获取个人记录的列表
	 * @param recordType
	 *            记录的类型
	 * @return RefreshListData<UserRecordEntry>
	 */
	public RefreshListData<UserRecordEntry> getCacheUserRecordList(int recordType) {
		if (mUserRecordMap.containsKey(recordType)) {
			return mUserRecordMap.get(recordType);
		} else {
			return new RefreshListData<UserRecordEntry>();
		}
	}

	/**
	 * @Title: getCacheUserLatticeProduct
	 * @Description: 从本地缓存获取格子铺
	 * @param userId
	 *            用户id
	 * @return RefreshListData<LatticeProduct>
	 */
	public RefreshListData<LatticeProduct> getCacheUserLatticeProduct(long userId) {
		if (mUserLatticeProductMap.containsKey(userId)) {
			return mUserLatticeProductMap.get(userId);
		} else {
			return new RefreshListData<LatticeProduct>();
		}
	}

	/**
	 * @Title: getCacheUserAlbumImage
	 * @Description: 从缓存获取用户相册
	 * @param userId
	 *            用户id
	 * @return RefreshListData<UserAlbumImage>
	 */
	public RefreshListData<UserAlbumImage> getCacheUserAlbumImage(long userId) {
		if (mUserAlbumImageMap.containsKey(userId)) {
			return mUserAlbumImageMap.get(userId);
		} else {
			return new RefreshListData<UserAlbumImage>();
		}
	}

	public void deleteCacheUserRecord(int recordType) {
		mUserRecordMap.remove(recordType);
	}

	public void deleteCacheUserRecord(int recordType, long recordId) {
		if (mUserRecordMap.containsKey(recordType)) {
			mUserRecordMap.get(recordType).removeObject(recordId);
		}
	}

	/**
	 * @Title: getUserRecordList
	 * @Description: 查询特定类型个人管理记录列表
	 * @param recordType
	 *            类型
	 * @see com.zhima.base.consts.ZMConsts.PersonRecordType
	 * @param begingTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param lastTimestamp
	 *            上一页最后一项的创建时间。 <= 0，忽略此参数
	 * @param refreshed
	 *            是否刷新数据
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getUserRecordList(int recordType, long begingTime, long endTime, long lastTimestamp, boolean refreshed,
			IHttpRequestCallback callBack) {
		RefreshListData<UserRecordEntry> list = null;
		if (!mUserRecordMap.containsKey(recordType)) {
			list = new RefreshListData<UserRecordEntry>();
			mUserRecordMap.put(recordType, list);
		} else {
			list = mUserRecordMap.get(recordType);
		}
		GetUserRecordListProtocol protocol = new GetUserRecordListProtocol(mContext, refreshed, list, callBack);
		protocol.getUserRecordList(recordType, begingTime, endTime, lastTimestamp);
	}

	/**
	 * @Title: deleteUserRecord
	 * @Description: 删除个人管理记录
	 * @param recordType
	 * @see com.zhima.base.consts.ZMConsts.PersonRecordType
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void deleteUserRecord(int recordType, long entryId, IHttpRequestCallback callBack) {
		DeleteUserRecordProtocol protocol = new DeleteUserRecordProtocol(mContext, callBack);
		protocol.deleteUserRecord(recordType, entryId);
	}

	/**
	 * @Title: deleteAllRecords
	 * @Description: 删除指定类型的所有个人管理记录
	 * @param recordType
	 * @see com.zhima.base.consts.ZMConsts.PersonRecordType
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void deleteAllRecords(int recordType, IHttpRequestCallback callBack) {
		DeleteUserRecordProtocol protocol = new DeleteUserRecordProtocol(mContext, callBack);
		protocol.deleteAllRecords(recordType);
	}

	/**
	 * @Title: searchUserRecord
	 * @Description:通过关键字查询个人管理记录
	 * @param
	 * @return void
	 */
	public void searchUserRecord(String keyword, int recordType, long lastTime, boolean refreshed,
			IHttpRequestCallback callBack) {
		SearchUserRecordProtocol protocol = new SearchUserRecordProtocol(mContext, refreshed, mUserRecordList, callBack);
		protocol.searchUserRecord(recordType, keyword, lastTime);
	}

	/**
	 * @Title: searchCard
	 * @Description:以关键字搜索卡片
	 * @param
	 * @return void
	 */
	public void searchCard(String keyword, boolean refreshed, IHttpRequestCallback callBack) {
		SearchCardProtocol protocol = new SearchCardProtocol(mContext, refreshed, mUserCouponList, callBack);
		protocol.getSearchCard(keyword);

	}

	/**
	 * @Title: getLatticeProduct
	 * @Description: 得到指定格子铺物品信息
	 * @param productId
	 *            物品Id
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getLatticeProduct(long productId, IHttpRequestCallback callBack) {
		GetLatticeProductProtocol protocol = new GetLatticeProductProtocol(mContext, callBack);
		protocol.getLatticeProduct(productId);
	}

	/**
	 * @Title: getLatticeProductList
	 * @Description: 获取用户的格子铺物品列表
	 * @param userId
	 *            用户id
	 * @param refreshed
	 *            是否刷新缓存数据
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getLatticeProductList(long userId, boolean refreshed, IHttpRequestCallback callBack) {
		long myUserId = AccountService.getInstance(mContext).getUserId();
		RefreshListData<LatticeProduct> list = null;
		if (!mUserLatticeProductMap.containsKey(userId)) {
			list = new RefreshListData<LatticeProduct>();
			mUserLatticeProductMap.put(userId, list);
		} else {
			list = mUserLatticeProductMap.get(userId);
		}
		GetLatticeProductListProtocol protocol = new GetLatticeProductListProtocol(mContext, refreshed, list, callBack);
		if (myUserId == userId) {
			protocol.getProducts();
		} else {
			protocol.getProducts(userId);
		}
	}

	/**
	 * @Title: addCacheProduct
	 * @Description: 格子铺物品加入本地缓存
	 * @param product
	 *            要加入的格子铺物品
	 * @return void
	 */
	public void addCacheProduct(LatticeProduct product) {
		if (product == null) {
			return;
		}
		RefreshListData<LatticeProduct> list = null;
		long userId = product.getOwnerId();
		if (!mUserLatticeProductMap.containsKey(userId)) {
			list = new RefreshListData<LatticeProduct>();
			mUserLatticeProductMap.put(userId, list);
		} else {
			list = mUserLatticeProductMap.get(userId);
		}
		list.add(product);
	}

	/**
	 * @Title: addLatticeProduct
	 * @Description: 用户添加格子铺物品
	 * @param product
	 *            要添加的铺物品
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void addLatticeProduct(LatticeProduct product, IHttpRequestCallback callBack) {
		AddLatticeProductProtocol protocol = new AddLatticeProductProtocol(mContext, callBack);
		protocol.addProduct(product);
	}

	/**
	 * @Title: updateLatticeProduct
	 * @Description: 修改格子铺物品
	 * @param product
	 *            要修改的铺物品
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void updateLatticeProduct(LatticeProduct product, IHttpRequestCallback callBack) {
		UpdateLatticeProductProtocol protocol = new UpdateLatticeProductProtocol(mContext, callBack);
		protocol.updateProduct(product);
	}

	public void deleteCahceLatticeProduct(long productId) {
		Set<Long> keys = mUserLatticeProductMap.keySet();
		RefreshListData<LatticeProduct> products;
		for (Long key : keys) {
			products = mUserLatticeProductMap.get(key);
			if (products.getMap().containsKey(productId)) {
				products.getMap().remove(productId);
				break;
			}
		}
	}

	/**
	 * @Title: deleteLatticeProduct
	 * @Description: 删除格子铺物品
	 * @param productId
	 *            格子铺物品Id
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void deleteLatticeProduct(long productId, IHttpRequestCallback callBack) {
		DeleteLatticeProductProtocol protocol = new DeleteLatticeProductProtocol(mContext, callBack);
		protocol.deleteProduct(productId);
	}

	/**
	 * @Title: getAlbumImage
	 * @Description: 得到用户相册
	 * @param userId
	 *            用户信息
	 * @param refreshed
	 *            是否刷新数据
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getAlbumImage(long userId, boolean refreshed, IHttpRequestCallback callBack) {
		RefreshListData<UserAlbumImage> list = null;
		if (!mUserAlbumImageMap.containsKey(userId)) {
			list = new RefreshListData<UserAlbumImage>();
			mUserAlbumImageMap.put(userId, list);
		} else {
			list = mUserAlbumImageMap.get(userId);
		}

		GetAlbumImageListProtocol protocol = new GetAlbumImageListProtocol(mContext, refreshed, list, callBack);
		protocol.getImageList(userId);
	}

	/**
	 * @Title: addAlbumImage
	 * @Description: 相册图片加入本地缓存
	 * @param userId
	 *            用户id
	 * @param image
	 *            图片
	 * @return void
	 */
	public void addAlbumImage(long userId, UserAlbumImage image) {
		RefreshListData<UserAlbumImage> list = null;
		if (!mUserAlbumImageMap.containsKey(userId)) {
			list = new RefreshListData<UserAlbumImage>();
		} else {
			list = mUserAlbumImageMap.get(userId);
		}
		list.add(image);
		mUserAlbumImageMap.put(userId, list);
	}

	/**
	 * @Title: uploadAlbumImage
	 * @Description: 上传相册图片
	 * @param image
	 *            图片
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void uploadAlbumImage(UserAlbumImage image, IHttpRequestCallback callBack) {
		long myUserId = AccountService.getInstance(mContext).getUserId();
		UploadAlbumImageProtocol protocol = new UploadAlbumImageProtocol(mContext, callBack);
		protocol.uploadImage(myUserId, image);
	}

	/**
	 * @Title: deleteCahceImage
	 * @Description: 删除本地缓存的相册图片
	 * @param imageId
	 *            要删除的图片id
	 * @return void
	 */
	public void deleteCahceImage(long imageId) {
		Set<Long> keys = mUserAlbumImageMap.keySet();
		RefreshListData<UserAlbumImage> images;
		for (Long key : keys) {
			images = mUserAlbumImageMap.get(key);
			if (images.getMap().containsKey(imageId)) {
				images.getMap().remove(imageId);
				break;
			}
		}
	}

	/**
	 * @Title: deleteImageOfAlbum
	 * @Description: 删除相册里的图片
	 * @param imageId
	 *            要删除的图片id
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void deleteImageOfAlbum(long imageId, IHttpRequestCallback callBack) {
		DeleteAlbumImageProtocol protocol = new DeleteAlbumImageProtocol(mContext, callBack);
		protocol.deleteImage(imageId);
	}

	/**
	 * @Title: getUserMixedQuantity
	 * @Description: 获取用户芝麻空间、相册、格子铺、好友、日志数量
	 * @param userId
	 *            用户id
	 * @param callBack
	 *            结果通知回调
	 * @return void
	 */
	public void getUserMixedQuantity(long userId, IHttpRequestCallback callBack) {
		GetUserHavingsProtocol protocol = new GetUserHavingsProtocol(mContext, callBack);
		protocol.getUserHavings(userId);
	}

	@Override
	public void onDestroy() {
		clear();
		mUserAlbumImageMap = null;
		mUserLatticeProductMap = null;
		mUserList = null;
		mUserRecordMap = null;
		mUserRecordList = null;
		mUserCouponList = null;
	}

	@Override
	public void clear() {
		if (mUserAlbumImageMap != null) {
			mUserAlbumImageMap.clear();
		}
		if (mUserLatticeProductMap != null) {
			mUserLatticeProductMap.clear();
		}
		if (mMyCouponMap != null) {
			mMyCouponMap.clear();
		}
		if (mUserList != null) {
			mUserList.clear();
		}
		if (mUserRecordMap != null) {
			mUserRecordMap.clear();
		}
		if (mUserRecordList != null) {
			mUserRecordList.clear();
		}
		if (mUserCouponList != null) {
			mUserCouponList.clear();
		}
	}
}
