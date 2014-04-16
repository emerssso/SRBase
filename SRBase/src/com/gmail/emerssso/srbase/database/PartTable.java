//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class models the table in the database representing parts.  Includes 
 * column name constants and SQL for creating and updating the table.
 * @author Conner Kasten
 */
public class PartTable {
	
	/** The table name constant. */
	public static final String TABLE_PART = "part";
	
	/** The _id primary key column required by Android APIs. */
	public static final String COLUMN_ID = "_id";
	
	/** The _id of the associated row in the SR table. */
	public static final String COLUMN_SR_ID = "sr_id";
	
	/** The column name for number of parts. */
	public static final String COLUMN_QUANTITY = "quantity";
	
	/** The column name for the used column. Since SQLite doesn't support
	 * booleans, we use a String. */
	public static final String COLUMN_USED = "used";
	
	/** The source column name. */
	public static final String COLUMN_SOURCE = "source";
	
	/** The description column name. */
	public static final String COLUMN_DESCRIPTION = "description";
	
	/** The part number column name. */
	public static final String COLUMN_PART_NUMBER = "part_number";
	
	/** The SQL command for creating the table. */
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
	 * This method creates the table in the passed database.
	 *
	 * @param database the database in which to create the table.
	 */
	public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	}
	
	/**
	 * Upgrades the database from version 1 to 2.  
	 * Note that version 1 used a three database schema for 
	 * storing dailies, parts, and SRs, while later versions
	 * use a one database schema with three tables. Attach old database
	 * before calling, or this will fail.  The old database is located at
	 * '/data/data/com.gmail.emerssso.srbase/databases/parttable.db'.
	 * The method assumes that the table has been attached as 'parttable'.
	 *
	 * @param database The target database to upgrade (main in the SQL)
	 * @param oldVersion the old version number. Only 1 is currently valid.
	 * @param newVersion the new version number. Only 2 is currently valid.
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		if(oldVersion == 1 && newVersion == 2) {
			Log.w(PartTable.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will migrate data to new location");
			onCreate(database);
			database.execSQL(
					"INSERT INTO main.part SELECT * FROM parttable.part");
			database.execSQL("DROP TABLE parttable.part");
		}
		else
			Log.w(SRTable.class.getName(), "No upgrade required.");
	}

}
