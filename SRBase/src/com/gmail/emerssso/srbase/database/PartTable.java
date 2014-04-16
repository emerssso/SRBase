//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The Class PartTable.
 * @author Conner Kasten
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
	 * Used to upgrade from version 1 of the databases to 2, with only
	 * one database.
	 *
	 * @param database the database
	 * @param oldVersion the old version
	 * @param newVersion the new version
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		if(oldVersion == 1 && newVersion == 2) {
			Log.w(PartTable.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will migrate data to new location");
			onCreate(database);
			database.execSQL("ATTACH \"/data/data/com.gmail.emerssso.srbase/databases/parttable.db\" AS parttable");
			database.execSQL(
					"INSERT INTO SRdatabase.part SELECT * FROM parttable.part");
			database.execSQL("DROP TABLE parttable.part");
		}
		else
			Log.w(SRTable.class.getName(), "No upgrade required.");
	}

}
