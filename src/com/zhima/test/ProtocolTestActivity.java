package com.zhima.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zhima.R;
import com.zhima.base.consts.ZMConsts.LoginType;
import com.zhima.base.consts.ZMConsts.ProtocolType;
import com.zhima.base.consts.ZMConsts.TargetType;
import com.zhima.base.consts.ZMConsts.ZMObjectKind;
import com.zhima.base.email.EmailHelper;
import com.zhima.base.error.ErrorManager;
import com.zhima.base.logger.Logger;
import com.zhima.base.network.HttpNetwork;
import com.zhima.base.network.IHttpRequestCallback;
import com.zhima.base.network.RequestInfo;
import com.zhima.base.network.RequestInfo.RequestType;
import com.zhima.base.network.uploadfile.FormFile;
import com.zhima.base.protocol.LoginRegisterProtocolHandler.LoginProtocolHandler;
import com.zhima.base.protocol.ProtocolHandlerBase;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.DoPraiseProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetPraiseCountProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetRecommendedZMObjectListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectAlbumListProtocol;
import com.zhima.base.protocol.ScanningcodeProtocolHandler.GetZMObjectProtocol;
import com.zhima.base.protocol.SpaceKindProtocolHandler.SyncSpacekindDictProtocol;
import com.zhima.base.protocol.ZMIdolProtocolHandler.GetAcqierementListProtocol;
import com.zhima.data.model.ContactEntry;
import com.zhima.data.model.FavoriteEntry;
import com.zhima.data.model.GeoCoordinate;
import com.zhima.data.model.IdolAcqierement;
import com.zhima.data.model.PraiseInfo;
import com.zhima.data.model.RefreshListData;
import com.zhima.data.model.Spacekind;
import com.zhima.data.model.ZMIdolObject;
import com.zhima.data.model.ZMObject;
import com.zhima.data.model.ZMObjectImage;
import com.zhima.data.service.AppLaunchService;
import com.zhima.data.service.LoginService;
import com.zhima.data.service.RegionService;
import com.zhima.data.service.ScanningcodeService;
import com.zhima.data.service.SpaceKindService;
import com.zhima.data.service.UserService;
import com.zhima.data.service.ZMIdolService;
import com.zhima.db.provider.ZhimaDatabase;
import com.zhima.ui.activity.BaseActivity;
import com.zhima.ui.common.view.HaloToast;

public class ProtocolTestActivity extends BaseActivity {
	protected static final String TAG = "ProtocolTestActivity";
	private TextView mBeginRunTest;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initData();
		Parcelable d = getIntent().getParcelableExtra("");
	}

//	RefreshListData<TruemanPost> mPost;
//	RefreshListData<TruemanPostReply> mPostReply;
	private String mUserAccount = "刘兵大";

	private void initData() {
		setContentView(R.layout.protocol_test_activity);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SZ", Locale.CHINA);
		Date now = new Date();
		String str = format.format(now);
		final TextView txtDemo = (TextView) findViewById(R.id.txt_demo);
		txtDemo.setMovementMethod(ScrollingMovementMethod.getInstance());
		txtDemo.setText(str);

		mBeginRunTest = (TextView) findViewById(R.id.txt_begin);
		mBeginRunTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				addLatticeshopProduct(AccountService.getInstance(ProtocolTestActivity.this).getMyself().getId());
//				getLatticeshopProductList(1);
//				deleteAlbumImage(1);
//				updateUserAvatar("/sdcard/pic/c.jpg");
//				uploadAlbumImage(1, "/sdcard/pic/c.jpg");
//				getAlbumImage(1);
//				deleteDiary(373,1);
//				mPost = new RefreshListData<TruemanPost>();
//				getTruemanPost(5, mPost);
//				testLogin(mUserAccount, "123456");
//				addTextDiary();
//				testRegisterByMobile();
//				testRegisterByEmail();
//				getInboxUnReadMessageCount(1);
				String businessUri = "http://zhima.net/m1/KKAKKu3d";
				String angelUri = "http://zhima.net/m1/KKKKuK0m";
				String taxi = "http://zhima.net/m1/kkkkub3f";
				String elevator = "http://zhima.net/m1/KMKKKu3e";
				String publicPlace = "http://zhima.net/m1/kkkku32v";
				checkDictUpdate();
//				checkUpgrade();
//				syncCityDict();
//				getUserRecordList(PersonRecordType.IDOL);
//				syncSpacekindDict();
//				getSanningcodeInfo(publicPlace);
//				getSanningcodeInfo(angelUri);
				//				getDiaryReplies(270, new RefreshListData<DiaryReply>(null,
//						new DiaryReplyDbController(ProtocolTestActivity.this)));
			}
		});

	}

	final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				// resp
				TextView txtDemo = (TextView) findViewById(R.id.txt_demo);
				txtDemo.setMovementMethod(ScrollingMovementMethod.getInstance());
				String temp = txtDemo.getText().toString();
				temp += "\n" + msg.obj.toString();
				txtDemo.setText(temp);
			}
				break;
			}
			super.handleMessage(msg);
		}
	};
	private IHttpRequestCallback callBack = new IHttpRequestCallback() {

		@Override
		public void onHttpResult(ProtocolHandlerBase protocol) {
			Message msg = Message.obtain();
			msg.what = 1;

			RequestInfo info = protocol.getRequestInfo();
			if (protocol.isHttpSuccess()) {
				// 网络请求成功
				int protocolType = protocol.getProtocolType();
				String json = info.getRecieveData();
				if (protocolType == ProtocolType.GET_ZMOBJECT_PROTOCOL) {
					// 获取扫码结果信息
					if (protocol.isHandleSuccess()) {
						GetZMObjectProtocol getzmProtocol = (GetZMObjectProtocol) protocol;
						ZMObject zmObject = getzmProtocol.getZMObject();
						if (zmObject != null) {
							Logger.getInstance(TAG).debug(zmObject.toString());
							getNoticeDigestList(zmObject, zmObject.getCityId());
							getRecommendedJokes();
							if (zmObject.getZMObjectType() == ZMObjectKind.UNKNOWN_OBJECT) {
								FavoriteEntry entry = new FavoriteEntry();
								entry.setTitle(zmObject.getZMCode());
								entry.setObjectType(TargetType.TARGET_TYPE_UNKNOWN_SPACE);
								entry.setObjectId(zmObject.getRemoteId());
								addFavorite(entry);
							} else {
								ContactEntry entry = new ContactEntry();
								entry.setTitle(zmObject.getName());
								entry.setObjectType(zmObject.getZMObjectType());
								entry.setObjectId(zmObject.getRemoteId());
								entry.setTelephone("010-84716033");
								entry.setImageUrl(zmObject.getImageUrl());
								addContactEntry(entry);
							}
							if (zmObject.getZMObjectType() == ZMObjectKind.IDOL_OBJECT) {
								getAcqierementList((ZMIdolObject) zmObject);
							}
//							getNoticeDigestList(zmObject, zmObject.getCityId());
//							getRecommendedZMIdolObjectList(1);
//							doPraise(zmObject, PraiseKind.SERVICE);
//							getPraiseCount(zmObject);
//							getZMObjectAlbum(zmObject);
						} else {
							Logger.getInstance(TAG).debug("没有解析出zmobject");
						}
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取扫码结果失败！", 1);
					}
				} else if (protocolType == ProtocolType.GET_IDOL_CHANNEL_LIST_PROTOCOL) {
					// 知天使才艺
					if (protocol.isHandleSuccess()) {
						GetAcqierementListProtocol p = (GetAcqierementListProtocol) protocol;
						ArrayList<IdolAcqierement> list = p.getDataList().getDataList();
						for (IdolAcqierement item : list) {
							Logger.getInstance(TAG).debug(item.toString());
						}
						HaloToast.show(ProtocolTestActivity.this, "知天使才艺成功！", 1);
					} else {
						HaloToast.show(ProtocolTestActivity.this, "知天使才艺失败！", 1);
					}
				} else if (protocolType == ProtocolType.ADD_FAVORITE_PROTOCOL) {
					// 收藏
					if (protocol.isHandleSuccess()) {
						HaloToast.show(ProtocolTestActivity.this, "收藏成功！", 1);
					} else {
						HaloToast.show(ProtocolTestActivity.this, "收藏失败！", 1);
					}
				} else if (protocolType == ProtocolType.ADD_CONTACT_PROTOCOL) {
					// 通讯录
					if (protocol.isHandleSuccess()) {
						HaloToast.show(ProtocolTestActivity.this, "保存通讯录成功！", 1);
					} else {
						HaloToast.show(ProtocolTestActivity.this, "保存通讯录失败！", 1);
					}
				} else if (protocolType == ProtocolType.GET_SELF_RECOMMENDED_ZMOBJECT_LIST_PROTOCOL) {
					// 获取推荐知天使
					if (protocol.isHandleSuccess()) {
						GetRecommendedZMObjectListProtocol p = (GetRecommendedZMObjectListProtocol) protocol;
						RefreshListData<ZMObject> list = p.getDataList();
						if (list != null) {
							Logger.getInstance(TAG).debug(list.toString());
						} else {
							Logger.getInstance(TAG).debug("没有解析出推荐ZMObject");
						}
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取推荐失败！", 1);
					}
				} else if (protocolType == ProtocolType.DO_ZMOBJECT_PRAISE_PROTOCOL) {
					// 加"赞"操作
					if (protocol.isHandleSuccess()) {
						DoPraiseProtocol praiseProtocol = (DoPraiseProtocol) protocol;
						PraiseInfo praise = praiseProtocol.getPraiseInfo();
						if (praise != null) {
							Logger.getInstance(TAG).debug(praise.toString());
						} else {
							Logger.getInstance(TAG).debug("没有解析出PraiseInfo");
						}
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取赞信息失败！", 1);
					}
				} else if (protocolType == ProtocolType.GET_ZMOBJECT_PRAISE_PROTOCOL) {
					// 获取赞信息
					if (protocol.isHandleSuccess()) {
						GetPraiseCountProtocol praiseProtocol = (GetPraiseCountProtocol) protocol;
						PraiseInfo praise = praiseProtocol.getPraiseInfo();
						if (praise != null) {
							Logger.getInstance(TAG).debug(praise.toString());
						} else {
							Logger.getInstance(TAG).debug("没有解析出PraiseInfo");
						}
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取赞信息失败！", 1);
					}
				} else if (protocolType == ProtocolType.GET_ZMOBJECT_ALBUM_PROTOCOL) {
					// 获取相册信息
					if (protocol.isHandleSuccess()) {
						GetZMObjectAlbumListProtocol praiseProtocol = (GetZMObjectAlbumListProtocol) protocol;
						RefreshListData<ZMObjectImage> imageList = praiseProtocol.getDataList();
						if (imageList != null) {
							Logger.getInstance(TAG).debug(imageList.toString());
						} else {
							Logger.getInstance(TAG).debug("没有解析出ZMObjectAlbum");
						}
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取相册信息失败！", 1);
					}
				}
//				else if (protocolType == ProtocolType.GET_INBOX_MESSAGE_COUNT_PROTOCOL) {
//					// 获取inbox未读消息总数
//					if (protocol.isHandleSuccess()) {
//						GetInboxMessageCountProtocol inboxProtocol = (GetInboxMessageCountProtocol) protocol;
//						Logger.getInstance(TAG).debug("inbox未读消息总数:" + inboxProtocol.getCount());
//						getCardUnReadMessageCount(1);
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "获取消息总数失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.GET_CARD_UNREADMESSAGECOUNT_PROTOCOL) {
//					// 获取Card夹未读消息总数
//					if (protocol.isHandleSuccess()) {
//						GetCardUnreadCountProtocol cardProtocol = (GetCardUnreadCountProtocol) protocol;
//						Logger.getInstance(TAG).debug("card夹未读消息总数:" + cardProtocol.getCount());
//						getMessageBoxUnReadMessageCount(1);
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "获取消息总数失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.GET_MESSAGEBOX_UNREADMESSAGECOUNT_PROTOCOL) {
//					// 获取MessageBox夹未读消息总数
//					if (protocol.isHandleSuccess()) {
//						GetUnReadSysMessageCountProtocol messageBoxProtocol = (GetUnReadSysMessageCountProtocol) protocol;
//						Logger.getInstance(TAG).debug("messageBox夹未读消息总数:" + messageBoxProtocol.getCount());
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "获取消息总数失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.REGISTER_PROTOCOL) {
//					// 用户注册
//					if (protocol.isHandleSuccess()) {
//						RegisterProtocolHandler registerProtocol = (RegisterProtocolHandler) protocol;
//						Logger.getInstance(TAG).debug("新用户的id:" + registerProtocol.getUserId());
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "获取消息总数失败！", 1);
//					}
//				} 
				else if (protocolType == ProtocolType.LOGIN_PROTOCOL) {
					// 用户登录
					if (protocol.isHandleSuccess()) {
						LoginProtocolHandler protocolhandler = (LoginProtocolHandler) protocol;
						testChangePassword("123456", "123456");
						Logger.getInstance(TAG)
								.debug("本次登录用户的token:" + protocolhandler.getLogonUser().getAccessToken());
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取消息总数失败！", 1);
					}
				}
//				else if (protocolType == ProtocolType.CHANGE_PASSWORD_PROTOCOL) {
//					// 修改密码
//					if (protocol.isHandleSuccess()) {
//						ChangePasswordProtocolHandler protocolhandler = (ChangePasswordProtocolHandler)protocol;
//						if (protocolhandler.isChangeSuccessful()) {
//							Logger.getInstance(TAG).debug("修改用户<" + mUserAccount + ">密码成功！");
//						} else {
//							Logger.getInstance(TAG).debug("修改用户<" + mUserAccount + ">密码失败！");
//						}
//						
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "获取消息总数失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.DELETE_DIARY_PROTOCOL) {
//					// 删除日记
//					if (protocol.isHandleSuccess()) {
//						Logger.getInstance(TAG).debug("删除日记成功！");
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "删除日记失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.UPLOAD_USER_ALBUM_IMAGE_PROTOCOL) {
//					// 用户上传相册图片
//					if (protocol.isHandleSuccess()) {
//						Logger.getInstance(TAG).debug("上传相册图片成功");
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "上传相册图片失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.DELETE_USER_ALBUM_IMAGE_PROTOCOL) {
//					// 删除用户相册图片
//					if (protocol.isHandleSuccess()) {
//						Logger.getInstance(TAG).debug("删除用户相册图片成功");
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "获取相册信息失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.UPDATE_USER_AVATAR_PROTOCOL) {
//					// 更新用户头像
//					if (protocol.isHandleSuccess()) {
//						Logger.getInstance(TAG).debug("更新用户头像成功");
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "更新用户头像失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.ADD_USER_LATTICESHOP_PRODUCT_PROTOCOL) {
//					// 添加格子铺物品
//					if (protocol.isHandleSuccess()) {
//						Logger.getInstance(TAG).debug("添加格子铺物品成功");
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "添加格子铺物品列表失败！", 1);
//					}
//				} else if (protocolType == ProtocolType.DELETE_USER_LATTICESHOP_PRODUCT_PROTOCOL) {
//					// 删除格子铺物品
//					if (protocol.isHandleSuccess()) {
//						Logger.getInstance(TAG).debug("删除格子铺物品成功");
//					} else {
//						HaloToast.show(ProtocolTestActivity.this, "删除格子铺物品列表失败！", 1);
//					}
//				} 
				else if (protocolType == ProtocolType.SYNC_SPACEKIND_DICT_PROTOCOL) {
					// 同步空间类型字典
					if (protocol.isHandleSuccess()) {
						SyncSpacekindDictProtocol praiseProtocol = (SyncSpacekindDictProtocol) protocol;
						RefreshListData<Spacekind> lp = praiseProtocol.getDataList();
						if (lp != null) {
							Logger.getInstance(TAG).debug("获取空间类型字典成功:" + lp.toString());
						}
					} else {
						HaloToast.show(ProtocolTestActivity.this, "获取空间类型字典失败！", 1);
					}
				}
				Logger.getInstance(TAG).debug(json);
				msg.obj = json;
			} else {
				// 网络请求出错
				HaloToast.show(ProtocolTestActivity.this, "网络处理失败！", 1);
				String json = info.getRecieveData();
				Logger.getInstance(TAG).debug(json);
				msg.obj = json;
			}
			mHandler.sendMessage(msg);
		}

		@Override
		public void onHttpStart(ProtocolHandlerBase protocol) {
			Message msg = Message.obtain();
			msg.what = 1;
			msg.obj = "开始发送异步请求...";
			mHandler.sendMessage(msg);
		}
	};

//	private RefreshListData<Diary> mRefreshListData = null;
	// 同步城市字典
	private void syncCityDict() {
		new Runnable() {
			public void run() {
				RegionService.getInstance(mContext).sync(0, callBack);
			}
		}.run();
	}

	// 检查新版本
	private void checkUpgrade() {
		new Runnable() {
			public void run() {
				AppLaunchService.getInstance(mContext).checkAppUpdate(callBack);
			}
		}.run();
	}

	// 检查字典更新
	private void checkDictUpdate() {
		new Runnable() {
			public void run() {
				AppLaunchService.getInstance(mContext).checkDictUpdate(callBack);
			}
		}.run();
	}

	// 同步空间类型字典
	private void syncSpacekindDict() {
		new Runnable() {
			public void run() {
				SpaceKindService.getInstance(mContext).sync(0, callBack);
			}
		}.run();
	}

	private void getUserRecordList(final int recordType) {
		new Runnable() {
			public void run() {
				long beginTime = 0;
				try {
					Date sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2012-09-18 01:01:00");
					beginTime = sdf.getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat f = new SimpleDateFormat();
				UserService.getInstance(ProtocolTestActivity.this).getUserRecordList(recordType, beginTime,
						System.currentTimeMillis(), 0, true, callBack);
			}
		}.run();
	}

	/**
	 * @Title: updateUserAvatar
	 * @Description: 更新用户头像
	 * @return void
	 */
	private void updateUserAvatar(final String imagePath) {
//		new Runnable() {
//			public void run() {
//				UserService.getInstance(ProtocolTestActivity.this).updateUserAvatar(imagePath, callBack);
//			}
//		}.run();
	}

	// 邮箱注册
	private void testRegisterByEmail() {
		new Runnable() {
			public void run() {
				LoginService.getInstance(ProtocolTestActivity.this).registerByEmail("liubing1@zhima.net", "password",
						callBack);
			}
		}.run();
	}

	// 手机号码注册
	private void testRegisterByMobile() {
		new Runnable() {
			public void run() {
				LoginService.getInstance(ProtocolTestActivity.this).registerByMobile("+8618611823391", "123456",
						"1234", callBack);
			}
		}.run();
	}

	// 得到inbox未读消息总数
	private void getInboxUnReadMessageCount(final long userId) {
//		new Runnable() {
//			public void run() {
//				InboxMessageService.getInstance(ProtocolTestActivity.this).getInboxMessageCount(userId, callBack);
//			}
//		}.run();
	}

	// 得到Card夹未读消息总数
	private void getCardUnReadMessageCount(final long userId) {
//		new Runnable() {
//			public void run() {
//				InboxMessageService.getInstance(ProtocolTestActivity.this).getCardUnReadMessageCount(userId, callBack);
//			}
//		}.run();
	}

	// 得到MessageBox未读消息总数
	private void getMessageBoxUnReadMessageCount(final long userId) {
//		new Runnable() {
//			public void run() {
//				InboxMessageService.getInstance(ProtocolTestActivity.this).getUnReadSysMessageCount(userId, callBack);
//			}
//		}.run();
	}

	// 获取扫码信息
	private void getSanningcodeInfo(final String code) {
		ZMObject zmObject = ScanningcodeService.getInstance(ProtocolTestActivity.this).getCacheZMObject(code);
		if (zmObject == null) {
			// 本地缓存无数据
			new Runnable() {
				public void run() {
					ScanningcodeService.getInstance(ProtocolTestActivity.this).getScanningInfo(code, callBack);
				}
			}.run();
		} else {
			HaloToast.show(ProtocolTestActivity.this, "从本地获取到的信息:\r\n" + zmObject.toString(), 1);
		}
	}

	// 收藏
	private void addFavorite(final FavoriteEntry entry) {
		new Runnable() {
			public void run() {
				ScanningcodeService.getInstance(ProtocolTestActivity.this).addFavorite(entry, callBack);
			}
		}.run();
	}

	// 通讯录
	private void addContactEntry(final ContactEntry entry) {
		new Runnable() {
			public void run() {
				ScanningcodeService.getInstance(ProtocolTestActivity.this).addContact(entry, callBack);
			}
		}.run();
	}

	// 
	private void getNoticeDigestList(final ZMObject zmObject, final long cityId) {
		new Runnable() {
			public void run() {
//				ScanningcodeService.getInstance(ProtocolTestActivity.this).getNoticeDigestList(zmObject, cityId,
//						new GeoCoordinate(), true, callBack);
			}
		}.run();
	}

	// 获取推荐知天使
	private void getRecommendedZMIdolObjectList(final int count) {
//		new Runnable() {
//			public void run() {
//				ScanningcodeService.getInstance(ProtocolTestActivity.this).getRecommendedZMIdolObjectList(count, true, callBack);
//			}
//		}.run();
	}

	// 从服务器获取赞信息
	private void getPraiseCount(final ZMObject zmObject) {
		new Runnable() {
			public void run() {
				ScanningcodeService.getInstance(ProtocolTestActivity.this).getPraiseCount(zmObject, callBack);
			}
		}.run();
	}

	// 加"赞"操作
	private void doPraise(final ZMObject zmObject, final int praiseType) {
		new Runnable() {
			public void run() {
				ScanningcodeService.getInstance(ProtocolTestActivity.this).doPraise(zmObject, praiseType, callBack);
			}
		}.run();
	}

	// 从服务器获取相册信息
	private void getZMObjectAlbum(final ZMObject zmObject) {
		new Runnable() {
			public void run() {
				ScanningcodeService.getInstance(ProtocolTestActivity.this).getZMObjectAlbumList(zmObject, true,
						callBack);
			}
		}.run();
	}

	private void getRecommendedJokes() {
		new Runnable() {
			public void run() {
				ScanningcodeService.getInstance(ProtocolTestActivity.this).getJokeList(true, callBack);
			}
		}.run();
	}

	private void getAcqierementList(final ZMIdolObject zmObject) {
		new Runnable() {
			public void run() {
				ZMIdolService.getInstance(ProtocolTestActivity.this).getAcqierementList(zmObject, true, callBack);
			}
		}.run();
	}

	private void testAddNewUser() {
		Uri uri = new ZhimaDatabase.UserTable().getContentUri();
		// ContentValues values = new ContentValues();
		// values.put(UserTable.NAME, "张三");
		// values.put(UserTable.NICKNAME, "张三的昵称");
		// values.put(UserTable.PHONE, "18611036862");
		// values.put(UserTable.EMAIL, "zhangsan123@sina.com");
		// values.put(UserTable.GENDER, 0);
		// values.put(UserTable.BIRTHDAY, 1234567890);
		// values.put(UserTable.MARRIAGE, 0);
		// values.put(UserTable.SIGNATURE, "张三的签名");
		// values.put(UserTable.INTRO, "张三的简介");
		// values.put(UserTable.REG_TYPE, 0);
		// values.put(UserTable.LOGIN_ON, System.currentTimeMillis());
		//
		// Uri u = DatabaseUtil.insert(this, uri, values);
		// //查询刚才插入的数据
		// String [] column = new
		// String[]{UserTable.NAME,UserTable.NICKNAME,UserTable.PHONE};
		// String where = UserTable.NAME + " = " + "'张三'";
		// Cursor cursor = DatabaseUtil.query(this, uri,
		// column, where, null, null);
		// StringBuilder sb = new StringBuilder();
		// Message msg = Message.obtain();
		// msg.what = 1;
		// if (cursor != null) {
		// while (cursor.moveToNext()) {
		// sb.append(cursor.getString(0) + "," + cursor.getString(1) + "," +
		// cursor.getString(2));
		// }
		// cursor.close();
		// msg.obj = sb.toString();
		// } else {
		// msg.obj = "查询数据失败！";
		// }
		// mHandler.sendMessage(msg);
	}

	private void testEmail() {
		// EmailHelper.openEmail(this, "abc234@gmail.com");
		// EmailHelper.sendEmail(this, "测试邮件", "这是通过android手机发送的一封文本邮件",
		// "cplusplus2050@gmail.com");
		// 发送带附件的邮件
		File file = new File("/sdcard/pic/c.jpg");
		Uri uri = Uri.fromFile(file);
		EmailHelper.sendEmailWithAttach(this, "带附件的邮件", "这是通过android手机发送的一封带附件的邮件", uri, "cplusplus2050@gmail.com");
	}

	/**
	 * @Title: testLogin
	 * @Description: 测试登录
	 * @param userName
	 * @param password
	 * @return void
	 */
	private void testLogin(final String userName, final String password) {
		new Runnable() {
			public void run() {
				LoginService.getInstance(ProtocolTestActivity.this).login(userName, password, LoginType.AUTO, callBack);
			}
		}.run();
	}

	/**
	 * @Title: testChangePassword
	 * @Description: 测试修改密码
	 * @param oldPassword
	 * @param newPassword
	 * @return void
	 */
	private void testChangePassword(final String oldPassword, final String newPassword) {
		new Runnable() {
			public void run() {
				LoginService.getInstance(ProtocolTestActivity.this).changePassword(oldPassword, newPassword, callBack);
			}
		}.run();
	}

	private void getJson() {
		new Runnable() {
			public void run() {
				HttpNetwork httpNetwork = HttpNetwork.getInstance(mContext);
				RequestInfo info = new RequestInfo("http://zhima.net:8080/web/service/space/code/1");
				info.setCharset("utf-8");
				info.setRequestType(RequestType.GET);
				httpNetwork.sendRequest(info);
				Message msg = Message.obtain();
				msg.what = 1;
				if (info.getResultCode() == ErrorManager.NO_ERROR) {
					msg.obj = info.getRecieveData();
				} else {
					msg.obj = "从服务端没有正确接收到数据";
				}
				mHandler.sendMessage(msg);
			}
		}.run();
	}

	private void addTextSticker() {
		new Runnable() {
			public void run() {
				HttpNetwork httpNetwork = HttpNetwork.getInstance(mContext);
				RequestInfo info = new RequestInfo("http://zhima.net:8080/web/service/space/1/sticker");
				info.setCharset("utf-8");
				String body = "{\"title\":\"新贴条\",\"postOn\":\"2012-05-16T17:02:01.5+0800\",\"content\":\"新贴条内容@Abc45678\",\"author\":{\"id\":\"1\"},\"postIp\":\"192.168.0.100\"}";
				info.setBody(body);
				info.setRequestType(RequestType.POST);
				httpNetwork.sendRequest(info);
				Message msg = Message.obtain();
				msg.what = 1;
				if (info.getResultCode() == ErrorManager.NO_ERROR) {
					msg.obj = info.getRecieveData();
				} else {
					msg.obj = "测试发贴条-从服务端没有正确接收到数据";
				}
				mHandler.sendMessage(msg);
			}
		}.run();
	}

	private FormFile getPicFile(String path) {
		File file = new File(path);
		FileInputStream ins;
		ByteBuffer byteBuffer = null;

		try {
			ins = new FileInputStream(file);
			DataInputStream dataInputStream = new DataInputStream(ins);
			int len = dataInputStream.available();
			byteBuffer = ByteBuffer.allocate(len);
			byte[] bufferOut = new byte[1024];
			int bytes = 0;
			while ((bytes = dataInputStream.read(bufferOut)) != -1) {
				byteBuffer.put(bufferOut, 0, bytes);
			}
			dataInputStream.close();
			ins.close();
		} catch (Exception e) {
			Logger.getInstance(this.getClass().getSimpleName()).debug(e.getMessage());
		}

		FormFile formFile = new FormFile("file1", "file1.jpg", byteBuffer.array(), "image/jpg");
		return formFile;
	}

	// 发送带文件附件的请求
	private void addWithPicSticker() {
		new Runnable() {
			public void run() {
				HttpNetwork httpNetwork = HttpNetwork.getInstance(mContext);
				RequestInfo info = new RequestInfo("http://zhima.net:8080/web/service/space/1/sticker/with-pic");
				info.setCharset("utf-8");
				String stickerText = "{\"title\":\"带图片贴条\",\"postOn\":\"2012-05-17T11:35:01.5+0800\",\"content\":\"贴条文本内容@Abc9876\",\"author\":{\"id\":\"1\"},\"postIp\":\"192.168.0.132\"}";
				info.addFormFieldParam("data", stickerText);
				FormFile formFile = getPicFile("/sdcard/pic/c.jpg");
				info.addUploadFile(formFile);
				info.setRequestType(RequestType.UPLOAD);
				httpNetwork.sendRequest(info);
				Message msg = Message.obtain();
				msg.what = 1;
				if (info.getResultCode() == ErrorManager.NO_ERROR) {
					msg.obj = info.getRecieveData();
				} else {
					msg.obj = "上传文件-从服务端没有正确接收到数据";
				}
				mHandler.sendMessage(msg);
			}
		}.run();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Logger.getInstance(this.getClass().getSimpleName()).debug("onBackPressed()");
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
}
