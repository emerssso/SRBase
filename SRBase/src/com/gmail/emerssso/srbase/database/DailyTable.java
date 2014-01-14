package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class DailyTable.
 */
public class DailyTable {
	
	/** The Constant TABLE_DAILY. */
	public static final String TABLE_DAILY = "daily";
	
	/** The Constant COLUMN_ID. */
	public static final String COLUMN_ID = "_id";
	
	/** The Constant COLUMN_SR_ID. */
	public static final String COLUMN_SR_ID = "SR_id";
	
	/** The Constant COLUMN_DATE. */
	public static final String COLUMN_DATE = "date";
	
	/** The Constant COLUMN_START_TIME. */
	public static final String COLUMN_START_TIME = "start_time";
	
	/** The Constant COLUMN_END_TIME. */
	public static final String COLUMN_END_TIME = "end_time";
	
	/** The Constant COLUMN_TRAVEL_TIME. */
	public static final String COLUMN_TRAVEL_TIME = "travel_time";
	
	/** The Constant COLUMN_COMMENT. */
	public static final String COLUMN_COMMENT = "comment";
	
	/** The Constant DATABASE_CREATE. */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_DAILY + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_SR_ID + " text not null, "
			+ COLUMN_DATE + " text not null, "
			+ COLUMN_START_TIME + " text not null, "
			+ COLUMN_END_TIME + " text not null, "
			+ COLUMN_TRAVEL_TIME + " text not null, "
			+ COLUMN_COMMENT + " text not null);";
	
	/**
	 * On create.
	 *
	 * @param database the database
	 */
	public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	}
	
	/**
	 * On upgrade.
	 *
	 * @param database the database
	 * @param oldVersion the old version
	 * @param newVersion the new version
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SRTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY);
		onCreate(database);
	}

}
