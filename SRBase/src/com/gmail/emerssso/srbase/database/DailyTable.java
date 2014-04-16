//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The model of the database of Daily Logs.
 */
public class DailyTable {
	
	/** The Constant TABLE_DAILY. */
	public static final String TABLE_DAILY = "daily";
	
	/** The Constant COLUMN_ID. */
	public static final String COLUMN_ID = "_id";
	
	/** The Constant COLUMN_SR_ID. */
	public static final String COLUMN_SR_ID = "SR_id";
	
	/** The Constant COLUMN_DATE. */
	public static final String COLUMN_DAY="day";
	public static final String COLUMN_MONTH="month";
	public static final String COLUMN_YEAR="year";
	
	/** The Constant COLUMN_START_TIME. */
	public static final String COLUMN_START_HOUR = "start_hour";
	public static final String COLUMN_START_MIN = "start_min";
	
	/** The Constant COLUMN_END_TIME. */
	public static final String COLUMN_END_HOUR = "end_hour";
	public static final String COLUMN_END_MIN = "end_min";
	
	/** The Constant COLUMN_TRAVEL_TIME. */
	public static final String COLUMN_TRAVEL_TIME = "travel_time";
	
	/** The Constant COLUMN_COMMENT. */
	public static final String COLUMN_COMMENT = "comment";
	
	/** The Constant DATABASE_CREATE. */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_DAILY + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_SR_ID + " text not null, "
			+ COLUMN_DAY + " integer, "
			+ COLUMN_MONTH  + " integer, "
			+ COLUMN_YEAR + " integer, "
			+ COLUMN_START_HOUR + " integer, "
			+ COLUMN_START_MIN + " integer, "
			+ COLUMN_END_HOUR + " integer, "
			+ COLUMN_END_MIN + " integer, "
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
		if(oldVersion == 1 && newVersion == 2) {
			Log.w(DailyTable.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will migrate data to new location");
			onCreate(database);
			database.execSQL("ATTACH \"/data/data/com.gmail.emerssso.srbase/databases/dailytable.db\" AS dailytable");
			database.execSQL(
					"INSERT INTO SRdatabase.daily SELECT * FROM dailytable.daily");
			database.execSQL("DROP TABLE dailytable.daily");
		}
		else
			Log.w(SRTable.class.getName(), "No upgrade required.");
	}

}
