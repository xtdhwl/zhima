package com.zhima.db.provider;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @ClassName: ZhimaDatabase
 * @Description: 数据库表定义。约定：缺省值-1表示用户还没有填写此项值
 * @author liubingsr
 * @date 2012-5-23 下午3:12:19
 * 
 */
public final class ZhimaDatabase {
	public static final String AUTHORITY = "com.zhima";

	public static final String TABLE_ACCOUNT = "toAccount";
	public static final String TABLE_USER = "toUser";
	public static final String TABLE_PROVINCE = "tcProvince";	
	public static final String TABLE_CITY = "tcCity";
	public static final String TABLE_REGION = "tcRegion";
	public static final String TABLE_IDOLJOB = "tcIdolJob";
	public static final String TABLE_SPACEROOTTYPE = "tcSpaceRootType";
	public static final String TABLE_SPACEMAINTYPE = "tcSpaceMainType";
	public static final String TABLE_SPACESUBTYPE = "tcSpaceSubTypeTable";
	public static final String TABLE_ZMOBJECTKIND = "tcZMObjectKindTable";
//	public static final String TABLE_SNS = "tcSNS";
//	public static final String TABLE_USERSNS = "toUserSNS";
	public static final String TABLE_ZMOBJECT = "toZMObject";
	public static final String TABLE_CONTACT = "toContact";
	public static final String TABLE_PERSONCONTACT = "toPersonContact";
	public static final String TABLE_FAVORITE = "toFavorite";
	public static final String TABLE_SCANNINGCODEHISTORY = "toScanningCodeHistory";
	//
	public static final String TABLE_MESSAGE = "toMessage";
	public static final String TABLE_CONVERSATION = "toConversation";
	public static final String TABLE_NEW_CITY = "tcCityNew";

	public static abstract class ZMBaseTable implements BaseColumns {
		public abstract void createTable(SQLiteDatabase db);
		public abstract String getTableName();

		public Uri getContentUri() {
			Uri uri = Uri.parse("content://" + AUTHORITY + "/" + getTableName());
			return uri;
		}
	}

	// sqlite中0为false，1为true
	public static abstract class BaseTable extends ZMBaseTable {
		// id
		public static final String ID = "id";
		/**
		 * 父id
		 */
		public static final String PARENT_ID = "pid";
		/**
		 * 默认排序列
		 */
		public static final String SORT_ORDER = ID + " DESC";
		/**
		 * 升序排列
		 */
		public static final String SORT_ORDER_ASC = ID + " ASC";

		public String getBaseSqlString() {
			return BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 本地自增id
					+ BaseTable.ID + " INTEGER DEFAULT 0,"
					+ BaseTable.PARENT_ID + " INTEGER DEFAULT 0";
		}
	}	
	
	// 新的城市、地区表
	public static final class NewCityTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.newcity";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.newcity";
		/**
		 * 名称 cityName, ename, parent_id, isOpen, areaCode
		 */
		public final static String CITY_NAME = "cityName";
		/**
		 * 城市拼音
		 */
		public final static String PYNAME = "pyName";
		/**
		 * 邮政编码
		 */
		public final static String POSTCODE = "postcode";
		/**
		 * 是否开通
		 */
		public final static String IS_OPEN = "isOpen";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + ");"
					+ "CREATE INDEX \"index_city_id\" ON \"" 
					+ TABLE_NEW_CITY + "\" (\"" 
					+ ID + "\" ASC);");
		}

		@Override
		public String getBaseSqlString() {
			String sql = super.getBaseSqlString() + "," 
					+ CITY_NAME + " TEXT NOT NULL,"
					+ PYNAME + " TEXT NOT NULL,"
					+ POSTCODE + " INTEGER,"
					+ IS_OPEN + " INTEGER";
			return sql;
		}

		@Override
		public String getTableName() {
			return TABLE_NEW_CITY;
		}
	}
		
	public static abstract class RegionBase extends BaseTable {	
		/**
		 * 名称
		 */
		public final static String NAME = "name";
		/**
		 * 城市拼音
		 */
		public final static String PYNAME = "pyName";
		/**
		 * 邮政编码
		 */
		public final static String POSTCODE = "postcode";
		/**
		 * 商圈id
		 */
		public final static String TAG_ID = "tagId";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + ");");
		}

		@Override
		public String getBaseSqlString() {
			String sql = super.getBaseSqlString() + "," 
					+ NAME + " TEXT,"
					+ PYNAME + " TEXT,"
					+ POSTCODE + " INTEGER,"
					+ TAG_ID + " INTEGER";
			return sql;
		}
	}
	
	// 省(直辖市)字典表
	public static final class ProvinceTable extends RegionBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.province";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.province";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_PROVINCE;
		}
	}

	// 城市(区)字典表
	public static final class CityTable extends RegionBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.city";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.city";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_CITY;
		}
	}

	// 区域字典表
	public static final class RegionTable extends RegionBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.region";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.region";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_REGION;
		}
	}
	/**
	* @ClassName: SpaceBase
	* @Description: 空间基类型
	* @author liubingsr
	* @date 2012-9-26 上午11:43:57
	*
	*/
	public static abstract class SpaceBase extends BaseTable {	
		/**
		 * 类型名称
		 */
		public final static String NAME = "name";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + ");");
		}

		@Override
		public String getBaseSqlString() {
			String sql = super.getBaseSqlString() + "," 
					+ NAME + " TEXT";
			return sql;
		}
	}
	/**
	* @ClassName: SpaceRootTypeTable
	* @Description: 空间根类型
	* @author liubingsr
	* @date 2012-9-26 上午11:41:35
	*
	*/
	public static final class SpaceRootTypeTable extends SpaceBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.spaceroottype";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.spaceroottype";
		
		/**
		 * 显示名称
		 */
		public final static String NAME = "name";
		
		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_SPACEROOTTYPE;
		}
	}
	/**
	* @ClassName: SpaceMainTypeTable
	* @Description: 空间主类型
	* @author liubingsr
	* @date 2012-9-20 下午12:39:42
	*
	*/
	public static final class SpaceMainTypeTable extends SpaceBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.spacemaintype";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.spacemaintype";
		
		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_SPACEMAINTYPE;
		}
	}
	/**
	* @ClassName: SpaceSubTypeTable
	* @Description: 空间子类型
	* @author liubingsr
	* @date 2012-9-20 下午12:42:05
	*
	*/
	public static final class SpaceSubTypeTable extends SpaceBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.spacesubtype";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.spacesubtype";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_SPACESUBTYPE;
		}
	}
	/**
	* @ClassName: IdolJobTable
	* @Description: 知天使职业字典表
	* @author liubingsr
	* @date 2013-1-25 下午8:41:13
	*
	*/
	public final static class IdolJobTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.idoljob";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.idoljob";

		/**职业*/
		public final static String JOB = "job";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + "," 
					+ JOB + " TEXT NULL"
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_IDOLJOB;
		}		
	}
	
	/**
	* @ClassName: ZMObjectKindTable
	* @Description: 空间类型定义表
	* @author liubingsr
	* @date 2013-1-4 下午3:28:14
	*
	*/
	public final static class ZMObjectKindTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.zmobjectkind";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.zmobjectkind";

		/**类型值*/
//		public final static String KIND = "kind";
		/**类型描述*/
		public final static String KIND_DESCRIPTION = "kindDescription";
		/**rest路径*/ 
		public final static String REST_PATH = "restPath";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + "," 
//					+ KIND + " INTEGER NOT NULL,"
					+ KIND_DESCRIPTION + " INTEGER NOT NULL," 
					+ REST_PATH + " TEXT NOT NULL"
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_ZMOBJECTKIND;
		}		
	}
	/**
	* @ClassName: AccountTable
	* @Description: 登录账号信息表
	* @author liubingsr
	* @date 2013-1-4 下午3:20:04
	*
	*/
	public final static class AccountTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.account";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.account";

		// 登录账号
		public final static String NAME = "name";
		// 锁定设置。0：没有设定，1：已经设定。sqlite中0为false，1为true
		public final static String LOCKED = "locked";
		// 锁定密码
		public final static String PASSWORD = "password";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + "," 
					+ NAME + " TEXT NOT NULL,"
					+ LOCKED + " INTEGER DEFAULT 0," 
					+ PASSWORD + " TEXT"
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_ACCOUNT;
		}
	}

	/**
	* @ClassName: UserTable
	* @Description: 用户基本信息表
	* @author liubingsr
	* @date 2012-8-15 上午10:06:36
	*
	*/
	public static final class UserTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.user";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.user";

		/**
		 * 登录账号id。当此值等于 AccountTable表中的id值时，表示是当前登录用户，其它用户的值永远为默认值0
		 */
		public static final String ACCOUNT_ID = "accountId";
		/**
		 * 芝麻ID
		 */
		public static final String ZMIDENTITY = "zmIdentity";
		/**
		 * 昵称
		 */
		public static final String NICKNAME = "nickname";
		/**
		 * 电话
		 */
		public static final String PHONE = "phone";
		/**
		 * 邮箱
		 */
		public static final String EMAIL = "email";
		/**
		 * 性别。-1：未填写，0：男，1：女，2：保密
		 */
		public static final String GENDER = "gender";
		/**
		 * 生日
		 */
		public static final String BIRTHDAY = "birthday";
		/**
		 * 婚姻状况。-1：未填写，0:未婚，1：已婚，2：保密
		 */
		public static final String MARRIAGE = "marriage";
		/**
		 * 血型。-1：未填写，
		 */
		public static final String BLOOD_TYPE = "bloodType";
		/**
		 * 星座
		 */
		public final static String ASTRO = "astro";
		/**
		 * 身高
		 */
		public static final String HEIGHT = "height";
		/**
		 * 个性签名
		 */
		public static final String SIGNATURE = "signature";
		/**
		 * 注册方式
		 * @see com.zhima.base.consts.ZMConsts.RegisterType
		 */
		public static final String REG_TYPE = "regtype";
		/**
		 * 用户关注的区域
		 */
		public static final String REGION_ID = "regionId";
		/**
		 * 头像原始图片URL(服务器端的url)
		 */
		public static final String IMAGE_URL = "imageUrl";
		/**
		 * 账号状态。0:已激活,1:已禁用,2:新注册
		 */
		public static final String STATUS = "status";
		/**
		 * 上次登录时间
		 */
		public static final String LOGIN_ON = "loginOn";
		/**
		 * 账号状态变化发生时间
		 */
		public static final String STATUS_ON = "statusOn";
		/**
		 * 注册时间
		 */
		public static final String CREATE_ON = "createOn";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + "," 
					+ ACCOUNT_ID + " INTEGER DEFAULT 0,"
					+ ZMIDENTITY + " TEXT,"
					+ NICKNAME + " TEXT," 
					+ PHONE + " TEXT," 
					+ EMAIL + " TEXT," 
					+ GENDER + " INTEGER DEFAULT -1," 
					+ BIRTHDAY + " TEXT DEFAULT '',"
					+ MARRIAGE + " INTEGER DEFAULT -1," 
					+ BLOOD_TYPE + " TEXT DEFAULT ''," 
					+ ASTRO + " TEXT,"
					+ HEIGHT + " INTEGER,"
					+ SIGNATURE + " TEXT," 
					+ REG_TYPE + " INTEGER,"
					+ REGION_ID + " INTEGER DEFAULT -1," 
					+ IMAGE_URL + " TEXT," 
					+ LOGIN_ON + " INTEGER DEFAULT 0," 
					+ STATUS_ON + " INTEGER DEFAULT 0,"
					+ CREATE_ON + " INTEGER DEFAULT 0" 
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_USER;
		}
	}
	/**
	* @ClassName: ZMObjectTable
	* @Description: ZMObject对象(一幢大楼、 一家公司、一家公司生产的某种产品、一个杯子、一家餐馆、一支雪糕、一个苹果、一箱苹果)
	* @author liubingsr
	* @date 2012-7-11 下午6:12:31
	*
	*/
	public static final class ZMObjectTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.zmobject";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.zmobject";
		/** 服务器返回的id */
		public static final String ZMOBJECT_ID = "zmObjectId";
		/** ZMObject类型id。BusinessSpace、PublicSpace、VehicleSpace、IdolSpace、ProductSpace、PlainSpace */
		public static final String OBJECT_TYPE = "objectType";
		/** zmcode */
		public static final String CODE = "code";
		/** 名称 */
		public static final String NAME = "name";
		/** 标题 */
		public static final String TITLE = "title";
		/** 详细信息 */
		public static final String DESCRIPTION = "description";
		/** 图片url(服务端的url值) */
		public static final String IMAGE_URL = "imageUrl";
		/** 纬度 */
		public static final String LATITUDE = "latitude";
		/** 经度 */
		public static final String LONGITUDE = "longitude";
		/**
		 * json数据包是否压缩。0：未压缩，1：压缩
		 */
		public static final String JSON_COMPRESSED = "jsonCompressed";
		/** json数据包 */
		public static final String JSON = "json";
		/** timeStamp */
		public static final String TIMESTAMP = "timestamp";
		
		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString() + ","
					+ ZMOBJECT_ID + " INTEGER,"
					+ OBJECT_TYPE + " INTEGER,"
					+ CODE + " TEXT,"
					+ NAME + " TEXT,"
					+ TITLE + " TEXT,"
					+ DESCRIPTION + " TEXT,"
					+ IMAGE_URL + " TEXT,"
					+ LATITUDE + " REAL DEFAULT 0,"
					+ LONGITUDE + " REAL DEFAULT 0,"
					+ JSON_COMPRESSED + " INTEGER DEFAULT 0,"
					+ JSON + " TEXT,"
					+ TIMESTAMP + " INTEGER DEFAULT 0"
					+ ");");			
		}

		@Override
		public String getTableName() {
			return TABLE_ZMOBJECT;
		}
//		
//		@Override
//		public String getBaseSqlString() {
//			String sql = super.getBaseSqlString() + "," 
//					+ OBJECTTYPE_ID + " INTEGER," 
//					+ CODE + " TEXT," 
//					+ NAME + " TEXT," 
//					+ INTRO + " TEXT," 
//					+ IMAGE_URL + " TEXT," 
//					+ LATITUDE + " REAL DEFAULT 0,"
//					+ LONGITUDE + " REAL DEFAULT 0,"
//					+ DEMO + " TEXT," 
//					+ TIMESTAMP + " INTEGER DEFAULT 0";
//
//			return sql;
//		}	
	}
	/**
	* @ClassName: ScanningHistoryTable
	* @Description: 用户扫码记录表(未登录用户不保存扫码记录)
	* @author liubingsr
	* @date 2012-7-24 下午2:38:10
	*
	*/
	public static final class ScanningCodeHistoryTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.scanningcodehistory";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.scanningcodehistory";

		/** 用户id */
		public static final String USER_ID = "userId";
//		/** zmobjectId */
//		public static final String ZMOBJECT_ID = "zmobjectId";
//		/** 对象类型  */
//		public static final String OBJECT_TYPE = "objectType";
		/** 标题 */
		public static final String TITLE = "title";
		/** zmcode   */
		public static final String CODE = "code";
//		/** 纬度   */
//		public static final String LATITUDE = "latitude";
//		/** 经度   */
//		public static final String LONGITUDE = "longitude";
//		// 上传标记。0：未上传，1：已上传
//		public static final String UPLOAD_FLAG = "uploadFlag";
		/** 扫码时间   */
		public static final String SCANNING_ON = "scanningOn";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString() + "," 
					+ USER_ID + " INTEGER,"
//					+ ZMOBJECT_ID + " INTEGER DEFAULT 0,"
//					+ OBJECT_TYPE + " INTEGER,"
					+ TITLE + " TEXT,"
					+ CODE + " TEXT,"
//					+ LATITUDE + " REAL DEFAULT 0," 
//					+ LONGITUDE + " REAL DEFAULT 0," 
//					+ UPLOAD_FLAG + " INTEGER DEFAULT 0,"
					+ SCANNING_ON + " INTEGER DEFAULT 0"
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_SCANNINGCODEHISTORY;
		}
	}
	/**
	* @ClassName: ContactBase
	* @Description: 通讯录基类型
	* @author liubingsr
	* @date 2013-1-18 下午8:12:12
	*
	*/
	public static abstract class ContactBase extends BaseTable {
		/** 用户id */
		public static final String USER_ID = "userId";
		/** 目标对象id */ 
		public static final String OBJECT_ID = "objectId";
		/**
		 * 目标类型。
		 */
		public static final String TARGET_TYPE = "targetType";
		/**
		 * 标题
		 */
		public static final String TITLE = "title";
		/**
		 * telephone
		 */
		public static final String TELEPHONE = "telephone";
		/**
		 * 原始图片url地址(服务器端的url)
		 */
		public static final String IMAGE_URL = "imageUrl";
		/**
		 * 上传标志。0:未上传，1：已上传
		 */
		public static final String UPLOAD_FLAG = "upload";
		/**
		 * 加入时间
		 */
		public static final String TIMESTAMP = "timestamp";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ getBaseSqlString() + ");");
		}
		@Override
		public String getBaseSqlString() {
			String sql = super.getBaseSqlString() + "," 
					+ USER_ID + " INTEGER,"
					+ OBJECT_ID + " INTEGER,"  
					+ TARGET_TYPE + " INTEGER,"
					+ TITLE + " TEXT,"
					+ TELEPHONE + " TEXT,"
					+ IMAGE_URL + " TEXT,"
					+ TIMESTAMP + " INTEGER DEFAULT 0,"
					+ UPLOAD_FLAG + " INTEGER DEFAULT 0";
			return sql;
		}
	}
	/**
	* @ClassName: SpaceContactTable
	* @Description: 空间通讯录表
	* @author liubingsr
	* @date 2012-8-10 下午4:30:07
	*
	*/
	public static final class SpaceContactTable extends ContactBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.contact";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.contact";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_CONTACT;
		}
	}
	/**
	* @ClassName: PersonContactTable
	* @Description: 个人通讯录
	* @author liubingsr
	* @date 2013-1-18 下午8:21:19
	*
	*/
	public static final class PersonContactTable extends ContactBase {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.personcontact";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.personcontact";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString()
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_PERSONCONTACT;
		}
	}
	/**
	* @ClassName: FavoriteTable
	* @Description: 收藏夹表
	* @author liubingsr
	* @date 2012-8-10 下午4:09:32
	*
	*/
	public static final class FavoriteTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.favorite";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.favorite";

		/** 用户id */
		public static final String USER_ID = "userId";
		/** 收藏目标对象id */ 
		public static final String OBJECT_ID = "objectId";
		/**
		 * 收藏目标类型。
		 */
		public static final String TARGET_TYPE = "targetType";
		/**
		 * 标题
		 */
		public static final String TITLE = "title";
		/**
		 * 内容
		 */
		public static final String CONTENT = "content";
		/**
		 * 图片url
		 */
		public static final String IMAGE_URL = "imageUrl";
		/**
		 * 收藏时间
		 */
		public static final String SAVE_ON = "saveOn";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString() + ","
					+ USER_ID + " INTEGER DEFAULT 0,"
					+ OBJECT_ID + " INTEGER DEFAULT 0,"
					+ TARGET_TYPE + " INTEGER,"
					+ TITLE + " TEXT,"
					+ CONTENT + " TEXT,"
					+ IMAGE_URL + " TEXT,"
					+ SAVE_ON + " INTEGER DEFAULT 0 "
					+ ");" 
					+ "CREATE INDEX " + TABLE_FAVORITE + "_newindex "
					+ " ON " + TABLE_FAVORITE + " (" 
					+ TARGET_TYPE + ", "
					+ TITLE
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_FAVORITE;
		}
	}

	public static final class MessageTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.message";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.message";
		/**发送者id*/
		public static final String SENDER_ID = "sender_id";
		/**内容*/
		public static final String CONTENT = "content";
		/**附件类型*/
		public static final String ATTACHMENT_TYPE = "type";
		/**附件url*/
		public static final String ATTACHMENT_URL = "url";
		/**发送时间*/
		public static final String POST_TIME = "postTime";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString() + "," 
					+ SENDER_ID + " INTEGER DEFAULT 0," 
					+ CONTENT + " TEXT," 
					+ ATTACHMENT_TYPE + " INTEGER,"
					+ ATTACHMENT_URL + " TEXT,"
					+ POST_TIME + " INTEGER"
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_MESSAGE;
		}
	}
	 
	/**
	* @ClassName: ConversationTable
	* @Description: 对话内容
	* @author liubingsr
	* @date 2013-1-19 下午12:25:00
	*
	*/
	public static final class ConversationTable extends BaseTable {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zhima.conversation";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.zhima.conversation";
		/**message组id*/
		public static final String MESSAGE_ID = "message_id";
		/**未读内容总数*/
		public static final String UNREADNUM = "unread";
		/**当前登录用户*/
		public static final String SENDER_ID = "my_id";
		/**对方id*/
		public static final String FRIEND_ID = "other_id";
		/**内容*/
        public static final String CONTENT = "content";
        /**
         * 内容类型。0：文本内容，1：图片文件路径，2：音频文件路径，3：视频文件路径
         */
        public static final String TYPE = "type";
        /**发送时间*/
        public static final String POST_TIME = "postTime";

		@Override
		public void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + getTableName() + " ("
					+ super.getBaseSqlString() + ","
					+ MESSAGE_ID + " INTEGER DEFAULT 0,"
					+ UNREADNUM + " INTEGER DEFAULT 0,"
					+ SENDER_ID + " INTEGER,"
					+ FRIEND_ID + " INTEGER,"
					+ CONTENT + " TEXT,"
					+ TYPE + " INTEGER,"
					+ POST_TIME + " INTEGER"
					+ ");");
		}

		@Override
		public String getTableName() {
			return TABLE_CONVERSATION;
		}
	}
}
