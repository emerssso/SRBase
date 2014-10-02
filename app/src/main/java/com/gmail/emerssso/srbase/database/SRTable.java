//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The model of the table for SRs.  Contains important constants such as 
 * column names, and the SQL necessary for creating and upgrading the table.
 * 
 * @author Conner Kasten
 */
public class SRTable {
	
	/** The name of the table. */
	public static final String TABLE_SR = "SR";
	
	/** The _id primary key column needed by Android APIs. */
	public static final String COLUMN_ID = "_id";
	
	/** The column name for the SR numbers. */
	public static final String COLUMN_SR_NUMBER = "SR_number";
	
	/** The column name for the customer name column. */
	public static final String COLUMN_CUSTOMER_NAME = "customer";
	
	/** The column name for the model number column. */
	public static final String COLUMN_MODEL_NUMBER = "model_number";
	
	/** The column name for the serial number column. */
	public static final String COLUMN_SERIAL_NUMBER = "serial_number";
	
	/** The column name for the description column. */
	public static final String COLUMN_DESCRIPTION = "description";
	
	/** The SQL to create the SR table. */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SR + "(" + COLUMN_ID 
			+ " integer primary key autoincrement, "
			+ COLUMN_SR_NUMBER + " text not null, "
			+ COLUMN_CUSTOMER_NAME + " text not null, "
			+ COLUMN_MODEL_NUMBER + " text not null, "
			+ COLUMN_SERIAL_NUMBER + " text not null, "
			+ COLUMN_DESCRIPTION + " text not null);";
	
	/**
	 * Executes the SQL necessary to create the table in the database referenced by
	 * database.
	 *
	 * @param database the database in which to create the table.
	 */
	public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	}
	
	/**
	 * Method called when a new version with an incremented database version number
	 * is installed.  So far, there hasn't been an upgrade to this table, so the
	 *method doesn't do anything.
	 *
	 * @param database the database to upgrade.
	 * @param oldVersion the old version number.
	 * @param newVersion the new version number.
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SRTable.class.getName(), "No need to upgrade SR table from version "
				+ oldVersion + " to " + newVersion);
	}

}
