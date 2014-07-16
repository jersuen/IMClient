package com.jersuen.im.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 聊天记录
 * @author JerSuen
 */
public class SMSProvider extends ContentProvider {
	private static final String AUTHORITY = SMSProvider.class.getCanonicalName();

	// 会话记录表
	private static final String SMS_TABLE = "sms"; 
	// 数据库名
	private static final String DB_NAME = "sms.db";
	// 数据库版本
	private static final int DB_VERSION = 1;

	// sms uri
	public static final Uri SMS_URI = Uri.parse("content://" + AUTHORITY + "/" + SMS_TABLE);
	
	// sessions uri
	public static final Uri SMS_SESSIONS_URI = Uri.parse("content://" + AUTHORITY + "/" + "sessions");

	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db;
	private static final UriMatcher URI_MATCHER;
	// UriMatcher 匹配值
	public static final int SMS = 1;
	public static final int SESSIONS = 2;

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, SMS_TABLE, SMS);
		URI_MATCHER.addURI(AUTHORITY, "sessions", SESSIONS);
	}
	// 创建
	public boolean onCreate() {
		dbHelper = new SMSDatabaseHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	// 删除
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case SMS:
			count = db.delete(SMS_TABLE, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	// 类型
	public String getType(Uri uri) {
		return null;
	}

	// 插入
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		long rowId = 0;
		switch (URI_MATCHER.match(uri)) {
		case SMS:
			rowId = db.insert(SMS_TABLE, SMSColumns.BODY, values);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		if (rowId < 0) {
			throw new SQLException("Failed to insert row into " + uri);
		}
		Uri noteUri = ContentUris.withAppendedId(uri, rowId);
		getContext().getContentResolver().notifyChange(noteUri, null);
		return noteUri;
	}

	// 查询
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		db = dbHelper.getReadableDatabase();
        qb.setTables(SMS_TABLE);
		Cursor ret;
		switch (URI_MATCHER.match(uri)) {
		case SMS:
			ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case SESSIONS:
			ret = qb.query(db, projection, selection, selectionArgs, SMSColumns.SESSION_ID, null, SMSColumns.TIME + " desc");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI :" + uri);
		}
		ret.setNotificationUri(getContext().getContentResolver(), uri);
		return ret;
	}

	// 更新
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		int count;
		switch (URI_MATCHER.match(uri)) {
		case SMS:
			count = db.update(SMS_TABLE, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI :" + uri);
		}
		Uri noteUri = ContentUris.withAppendedId(uri, count);
		getContext().getContentResolver().notifyChange(noteUri, null);
		return count;
	}

	private class SMSDatabaseHelper extends SQLiteOpenHelper {

		public SMSDatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SMS_TABLE + " (" 
					+ SMSColumns._ID + " INTEGER PRIMARY KEY, " 
					+ SMSColumns.WHO_ID + " INTEGER, "
					+ SMSColumns.BODY + " TEXT, "
					+ SMSColumns.TYPE + " TEXT, " 
					+ SMSColumns.STATUS + " TEXT, " 
					+ SMSColumns.SESSION_ID + " INTEGER, " 
					+ SMSColumns.SESSION_NAME + " TEXT, " 
					+ SMSColumns.UNREAD + " INTEGER, " 
					+ SMSColumns.TIME + " TEXT);");
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
			onCreate(db);
		}
	}

	/** DB Columns*/
	public static class SMSColumns implements BaseColumns {
		public static final String WHO_ID = "who_id";

        public static final String SESSION_ID = "session_id";
        public static final String SESSION_NAME = "session_name";

		public static final String BODY = "body";
		public static final String TYPE = "type";
		public static final String TIME = "time";
		public static final String STATUS = "status";
		public static final String UNREAD = "unread";


	}
}
