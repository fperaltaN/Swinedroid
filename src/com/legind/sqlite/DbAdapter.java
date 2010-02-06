package com.legind.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
	public static final String KEY_ROWID = "_id";
	
	private DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;
	
	private static final String TAG = "DbAdapter";
	
	private static final String DATABASE_NAME = "data";
	private String DATABASE_TABLE;
	private static final int DATABASE_VERSION = 3;
	private String[] FIELDS_STRING;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE =
		"create table alerts (_id integer primary key autoincrement, sid int not null, cid int not null, ip_src int not null, ip_dst int not null, sig_priority smallint not null, sig_name varchar not null, timestamp varchar not null);" +
		"create table servers (_id integer primary key autoincrement, host varchar(128) not null, port int not null, username varchar(128) not null, password varchar(128) not null);";

	private final Context mCtx;
	
	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS alerts; DROP TABLE IF EXISTS servers;");
			onCreate(db);
		}
	}
	
	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 * @param databaseVersion the version of the child table
	 * @param databaseCreate the create statement
	 * @param databaseTable the table of the child
	 */
	public DbAdapter(Context ctx, String databaseTable, String[] fieldsString) {
		DATABASE_TABLE = databaseTable;
		FIELDS_STRING = fieldsString;
	    this.mCtx = ctx;
	}
	
	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DbAdapter open() throws SQLException {
	    mDbHelper = new DatabaseHelper(mCtx);
	    mDb = mDbHelper.getWritableDatabase();
	    return this;
	}
	
	public void close() {
	    mDbHelper.close();
	}
	
	/**
	 * Delete the row with the given rowId
	 * 
	 * @param rowId id of row to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean delete(long rowId) {
	    return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Delete all rows in table
	 * 
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteAll() {
	    return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}
	
	/**
	 * Return a Cursor over the list of all rows in the table
	 * 
	 * @return Cursor over all alerts
	 */
	public Cursor fetchAll() {
		String[] fieldsWithRow = new String[FIELDS_STRING.length+1];
		fieldsWithRow[0] = KEY_ROWID;
		System.arraycopy(FIELDS_STRING, 0, fieldsWithRow, 1, FIELDS_STRING.length);
	    return mDb.query(DATABASE_TABLE, fieldsWithRow, null, null, null, null, null);
	}
	
	/**
	 * Return a Cursor positioned at the row that matches the given rowId
	 * 
	 * @param rowId id of alert to retrieve
	 * @return Cursor positioned to matching row, if found
	 * @throws SQLException if row could not be found/retrieved
	 */
	public Cursor fetch(long rowId) throws SQLException {
		String[] fieldsWithRow = new String[FIELDS_STRING.length+1];
		fieldsWithRow[0] = KEY_ROWID;
		System.arraycopy(FIELDS_STRING, 0, fieldsWithRow, 1, FIELDS_STRING.length);
	
	    Cursor mCursor =
	
	            mDb.query(true, DATABASE_TABLE, fieldsWithRow, KEY_ROWID + "=" + rowId, null,
	                    null, null, null, null);
	    if (mCursor != null) {
	        mCursor.moveToFirst();
	    }
	    return mCursor;
	
	}
}