package com.jersuen.im.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 联系人提供者
 * @author JerSuen
 */
public class ContactProvider extends ContentProvider{
	private static final String AUTHORITY = ContactProvider.class.getCanonicalName();

	//联系人表
	private static final String CONTACT_TABLE = "contact";
	
	// 联系人数据库
	private static final String DB_NAME = "contact.db";

	// 联系人数据库版本
	private static final int DB_VERSION = 1;
	
	// 联系人 uri
	public static final Uri CONTACT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTACT_TABLE);
	
	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db;
	private static final UriMatcher URI_MATCHER;
	
	// UriMatcher 匹配值
	public static final int CONTACTS = 1;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, CONTACT_TABLE, CONTACTS);
	}
	
	public boolean onCreate() {
		dbHelper = new ContactDatabaseHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	// 查询
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		Cursor ret = null;
		switch (URI_MATCHER.match(uri)) {
		case CONTACTS:
			qb.setTables(CONTACT_TABLE);
			ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		default:
			// 
			break;
		}
		if (ret != null) {
			ret.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return ret;
	}

	// 插入
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		Uri result = null;
		switch (URI_MATCHER.match(uri)) {
		case CONTACTS:
			long rowId = db.insert(CONTACT_TABLE, ContactColumns.ACCOUNT, values);
			result = ContentUris.withAppendedId(uri, rowId);
			break;
		default:
			// 
			break;
		}
		if(result != null) {
			getContext().getContentResolver().notifyChange(result, null);
		}
		return result;
	}

	// 删除
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case CONTACTS:
			count = db.delete(CONTACT_TABLE, selection, selectionArgs);
			break;
		default:
            //
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	// 更新
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case CONTACTS:
			count = db.update(CONTACT_TABLE, values, selection, selectionArgs);
			break;
		default:
            //
			break;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	public String getType(Uri uri) {
		return null;
	}

	/**联系人数据库*/
	private class ContactDatabaseHelper extends SQLiteOpenHelper {

		public ContactDatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + CONTACT_TABLE + " (" 
					+ ContactColumns._ID + " INTEGER PRIMARY KEY, " 
					+ ContactColumns.AVATAR + " TEXT, " 
					+ ContactColumns.SORT + " TEXT, " 
					+ ContactColumns.NICKNAME + " TEXT, " 
					+ ContactColumns.ACCOUNT + " TEXT);");
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
			onCreate(db);
		}
		
	}
	/**联系人列*/
	public static class ContactColumns implements BaseColumns {
		public static final String AVATAR = "avatar";
		public static final String NICKNAME = "nickname";
		public static final String ACCOUNT = "account";
		public static final String SORT = "sort";
	}
}
