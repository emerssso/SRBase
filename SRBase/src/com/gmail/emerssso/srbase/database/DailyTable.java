package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DailyTable {
	public static final String TABLE_DAILY = "daily";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SR_ID = "SR_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_START_TIME = "start_time";
	public static final String COLUMN_END_TIME = "end_time";
	public static final String COLUMN_TRAVEL_TIME = "travel_time";
	public static final String COLUMN_COMMENT = "comment";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_DAILY + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_SR_ID + " text not null, "
			+ COLUMN_DATE + " text not null, "
			+ COLUMN_START_TIME + " text not null, "
			+ COLUMN_END_TIME + " text not null, "
			+ COLUMN_TRAVEL_TIME + " text not null, "
			+ COLUMN_COMMENT + " text not null);";
	
	public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SRTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY);
		onCreate(database);
	}

}
