package com.zhima.db.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.zhima.base.logger.Logger;
import com.zhima.db.provider.ZhimaDatabase.AccountTable;
import com.zhima.db.provider.ZhimaDatabase.CityTable;
import com.zhima.db.provider.ZhimaDatabase.NewCityTable;
import com.zhima.db.provider.ZhimaDatabase.ConversationTable;
import com.zhima.db.provider.ZhimaDatabase.FavoriteTable;
import com.zhima.db.provider.ZhimaDatabase.IdolJobTable;
import com.zhima.db.provider.ZhimaDatabase.MessageTable;
import com.zhima.db.provider.ZhimaDatabase.PersonContactTable;
import com.zhima.db.provider.ZhimaDatabase.ProvinceTable;
import com.zhima.db.provider.ZhimaDatabase.RegionTable;
import com.zhima.db.provider.ZhimaDatabase.ScanningCodeHistoryTable;
import com.zhima.db.provider.ZhimaDatabase.SpaceContactTable;
import com.zhima.db.provider.ZhimaDatabase.SpaceMainTypeTable;
import com.zhima.db.provider.ZhimaDatabase.SpaceRootTypeTable;
import com.zhima.db.provider.ZhimaDatabase.SpaceSubTypeTable;
import com.zhima.db.provider.ZhimaDatabase.UserTable;
import com.zhima.db.provider.ZhimaDatabase.ZMBaseTable;
import com.zhima.db.provider.ZhimaDatabase.ZMObjectKindTable;
import com.zhima.db.provider.ZhimaDatabase.ZMObjectTable;
import com.zhima.db.utils.DatabaseUtil;

/**
 * @ClassName: ZhimaProvider
 * @Description: 芝麻Provider
 * @author liubingsr
 * @date 2012-5-23 下午6:02:18
 * 
 */
public class ZhimaProvider extends ContentProvider {
	private static final String TAG = "ZhimaProvider";
	private static final String DATABASE_NAME = "zhima.db";
	private static final int DATABASE_VERSION = 2;

	private static final UriMatcher uriMatcher;

	private static final ArrayList<ZMBaseTable> mDataTableList;
	
	private static ZhimaProvider mInstance;
	
	private Context mContext;
	

	static {
		mInstance = null;
		mDataTableList = new ArrayList<ZMBaseTable>();
		mDataTableList.add(new AccountTable());
		mDataTableList.add(new UserTable());
		
//		mDataTableList.add(new ProvinceTable()); // 2
//		mDataTableList.add(new CityTable()); // 3
//		mDataTableList.add(new RegionTable()); // 4
		
		mDataTableList.add(new SpaceRootTypeTable());
		mDataTableList.add(new SpaceMainTypeTable());
		mDataTableList.add(new SpaceSubTypeTable());
		
		// zmobject对象表
		mDataTableList.add(new ZMObjectTable());
		
		mDataTableList.add(new ScanningCodeHistoryTable());
		mDataTableList.add(new SpaceContactTable());
		mDataTableList.add(new FavoriteTable());		
		// 1.3.0 新增
		mDataTableList.add(new ZMObjectKindTable());
		mDataTableList.add(new PersonContactTable());
		mDataTableList.add(new MessageTable());
		mDataTableList.add(new ConversationTable());
		mDataTableList.add(new IdolJobTable());
		mDataTableList.add(new NewCityTable());
		
		//
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		for (int index = 0, count = mDataTableList.size(); index < count; ++index) {
			uriMatcher.addURI(ZhimaDatabase.AUTHORITY, mDataTableList.get(index).getTableName(), index + 1);
		}
	}

	public static void deleteAll(Context context) {
		for (ZMBaseTable table : mDataTableList) {
			DatabaseUtil.delete(context, table.getContentUri(), null, null);
		}
	}

	@Override
	public boolean onCreate() {
		mContext = getContext();
		mDatabaseHelper = new DatabaseHelper(mContext);
		return true;
	}
	
	public static ZhimaProvider getInstance() {
		if (mInstance == null) {
			mInstance = new ZhimaProvider();
			mInstance.onCreate();
		}
		return mInstance;
	}
	private String getDatabaseTypeString(Uri uri) {
		int result = uriMatcher.match(uri) - 1;
		if (result >= 0 && result < mDataTableList.size()) {
			return mDataTableList.get(result).getTableName();
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	private Uri getDatabaseTypeUri(Uri uri) {
		int result = uriMatcher.match(uri) - 1;
		if (result >= 0 && result < mDataTableList.size()) {
			return mDataTableList.get(result).getContentUri();
		} else {
			throw new IllegalArgumentException("Unknown URI:" + uri);
		}
	}
	/**
	* @Title: execSQL
	* @Description: 执行sql语句
	* @param sql 要执行的sql语句
	* @return void
	 */
	public void execSQL(String sql) {
		if (TextUtils.isEmpty(sql)) {
			return;
		}
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			Logger.getInstance(TAG).debug(e.getMessage(), e);
		}
	}
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int count = db.delete(getDatabaseTypeString(uri), where, whereArgs);
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		long rowId = db.insert(getDatabaseTypeString(uri), BaseColumns._ID, values);
		Uri tableUri = getDatabaseTypeUri(uri);

		if (rowId > 0) {
			Uri newRowUri = ContentUris.withAppendedId(tableUri, rowId);
			getContext().getContentResolver().notifyChange(newRowUri, null);
			return newRowUri;
		}
		throw new SQLException("Failed to insert row into:" + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(getDatabaseTypeString(uri));
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BaseColumns._ID;
		} else {
			orderBy = sortOrder;
		}
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int count = db.update(getDatabaseTypeString(uri), values, where, whereArgs);

		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			try {
				getWritableDatabase();
			} catch(SQLiteException e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				for (ZMBaseTable baseTable : mDataTableList) {
					baseTable.createTable(db);
				}
				initDb(db);
			} catch(Exception e) {
				Logger.getInstance(TAG).debug(e.getMessage(), e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion < 1) {
            	for(ZMBaseTable baseDb : mDataTableList) {
            		db.execSQL("DROP TABLE IF EXISTS " + baseDb.getTableName());
            	}
                onCreate(db);
                return;
            }
			if (oldVersion == 1) { // V1.3
				// 删除老的城市表
				db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_PROVINCE);
				db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_CITY);
				db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_REGION);
				
				db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_ZMOBJECTKIND);
				ZMBaseTable baseDb = new ZMObjectKindTable();
            	baseDb.createTable(db);
            	initZMObjectKindData(db);
            	
            	db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_PERSONCONTACT);
				baseDb = new PersonContactTable();
            	baseDb.createTable(db);
            	
            	db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_MESSAGE);
				baseDb = new MessageTable();
            	baseDb.createTable(db);
            	
            	db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_CONVERSATION);
				baseDb = new ConversationTable();
            	baseDb.createTable(db);
            	
            	db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_IDOLJOB);
				baseDb = new IdolJobTable();
            	baseDb.createTable(db);
            	
            	db.execSQL("DROP TABLE IF EXISTS " + ZhimaDatabase.TABLE_NEW_CITY);
				baseDb = new NewCityTable();
            	baseDb.createTable(db);
            	initCityData(db);
            	//
            	oldVersion++;
			}
			if (oldVersion != newVersion) {
                throw new IllegalStateException("error upgrading the database to version:" + newVersion);
            }
		}

		// 数据初始化
		private void initDb(SQLiteDatabase db) {
			initCityData(db);
			//initZMObjectKindData(db);
		}
		
		private void initCityData(SQLiteDatabase db) {
			//id, cityName, ename, parent_id, isOpen, areaCode
			final String sqlTemp = "INSERT INTO " + ZhimaDatabase.TABLE_NEW_CITY + "("
					+ NewCityTable.ID + ","
					+ NewCityTable.CITY_NAME + ","
					+ NewCityTable.PYNAME + ","
					+ NewCityTable.PARENT_ID + ","
					+ NewCityTable.IS_OPEN + ","
					+ NewCityTable.POSTCODE
					+ ") VALUES ";
			
			String value = "";
			InputStream is = null;
			String sql;
	        try {
	        	is = mContext.getResources().getAssets().open("city.txt");	            
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	            long beginTime = System.currentTimeMillis();
	            Logger.getInstance(TAG).debug("开始导入城市数据");
	            // 开始事务
	            db.beginTransaction();
	            while((value = reader.readLine()) != null) {//"INSERT INTO tcCityNew(id,cityName,pyName,pid,isOpen,postcode) values "
	            	sql = sqlTemp + value.trim();
//	            	Logger.getInstance(TAG).debug(sql);
	            	db.execSQL(sql);
	            }
//	            sql = "INSERT INTO tcCityNew(id,cityName,pyName,pid,isOpen,postcode)values(1, '中国', 'zhongguo', 0, 1, '')";
//            	Logger.getInstance(TAG).debug(sql);
//            	db.execSQL(sql);
//            	sql = "INSERT INTO tcCityNew(id,cityName,pyName,pid,isOpen,postcode)values(68719476738, '北京市', 'beijingshi', 1, 1, '')";
//            	Logger.getInstance(TAG).debug(sql);
//            	db.execSQL(sql);
	            // 设置事务处理成功
	            db.setTransactionSuccessful();
	            // 处理完成
	            db.endTransaction();
	            reader.close();
	            Logger.getInstance(TAG).debug("导入城市数据完成。耗时:" + (System.currentTimeMillis() - beginTime));
	         } catch (IOException e) {
	        	 Logger.getInstance(TAG).debug(e.getMessage(), e);
	        } catch (Exception e) {
	        	Logger.getInstance(TAG).debug(e.getMessage(), e);
	        } finally {
	        	if (is != null) {
	        		try {
						is.close();
					} catch (IOException e) {
						Logger.getInstance(TAG).debug(e.getMessage(), e);
					}
	        	}
	        }
		}
		
		private void initZMObjectKindData(SQLiteDatabase db) {
			String temp = "insert into " + ZhimaDatabase.TABLE_ZMOBJECTKIND + "(" + ZMObjectKindTable.ID + "," + ZMObjectKindTable.KIND_DESCRIPTION + "," + ZMObjectKindTable.REST_PATH + ") values(%d,'%s','%s')";
			String sql = String.format(temp, -1, "", "");
			db.execSQL(sql);
			
			sql = String.format(temp, 0, "plainspace", "");
			db.execSQL(sql);
			
			sql = String.format(temp, 1, "businessspace", "business");
			db.execSQL(sql);
			
			sql = String.format(temp, 2, "publicspace", "public");
			db.execSQL(sql);
			
			sql = String.format(temp, 3, "vehiclespace", "vehicle");
			db.execSQL(sql);
			
			sql = String.format(temp, 4, "idolspace", "idol");
			db.execSQL(sql);
			
			sql = String.format(temp, 5, "weddingspace", "wedding");
			db.execSQL(sql);
			
			sql = String.format(temp, 21, "productspace", "product");
			db.execSQL(sql);
			
			sql = String.format(temp, 801, "801", "user");
			db.execSQL(sql);
		}
	}

	private DatabaseHelper mDatabaseHelper = null;
}
