package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class PartTable.
 */
public class PartTable {
	
	/** The Constant TABLE_PART. */
	public static final String TABLE_PART = "part";
	
	/** The Constant COLUMN_ID. */
	public static final String COLUMN_ID = "_id";
	
	/** The Constant COLUMN_SR_ID. */
	public static final String COLUMN_SR_ID = "sr_id";
	
	/** The Constant COLUMN_QUANTITY. */
	public static final String COLUMN_QUANTITY = "quantity";
	
	/** The Constant COLUMN_USED. */
	public static final String COLUMN_USED = "used";
	
	/** The Constant COLUMN_SOURCE. */
	public static final String COLUMN_SOURCE = "source";
	
	/** The Constant COLUMN_DESCRIPTION. */
	public static final String COLUMN_DESCRIPTION = "description";
	
	public static final String COLUMN_PART_NUMBER = "part_number";
	
	/** The Constant DATABASE_CREATE. */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PART + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_SR_ID + " text not null, "
			+ COLUMN_PART_NUMBER + " text not null, "
			+ COLUMN_QUANTITY + " text not null, "
			+ COLUMN_USED + " text not null, "
			+ COLUMN_SOURCE + " text not null, "
			+ COLUMN_DESCRIPTION + " text not null);";
	
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
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PART);
		onCreate(database);
	}

}
