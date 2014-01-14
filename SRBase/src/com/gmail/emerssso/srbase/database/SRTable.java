//TODO: Copyright info
package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class SRTable.
 * 
 * @autho Conner Kasten
 */
public class SRTable {
	
	/** The Constant TABLE_SR. */
	public static final String TABLE_SR = "SR";
	
	/** The Constant COLUMN_ID. */
	public static final String COLUMN_ID = "_id";
	
	/** The Constant COLUMN_SR_NUMBER. */
	public static final String COLUMN_SR_NUMBER = "SR_number";
	
	/** The Constant COLUMN_CUSTOMER_NAME. */
	public static final String COLUMN_CUSTOMER_NAME = "customer";
	
	/** The Constant COLUMN_MODEL_NUMBER. */
	public static final String COLUMN_MODEL_NUMBER = "model_number";
	
	/** The Constant COLUMN_SERIAL_NUMBER. */
	public static final String COLUMN_SERIAL_NUMBER = "serial_number";
	
	/** The Constant COLUMN_DESCRIPTION. */
	public static final String COLUMN_DESCRIPTION = "description";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SR + "(" + COLUMN_ID 
			+ " integer primary key autoincrement, "
			+ COLUMN_SR_NUMBER + " text not null, "
			+ COLUMN_CUSTOMER_NAME + " text not null, "
			+ COLUMN_MODEL_NUMBER + " text not null, "
			+ COLUMN_SERIAL_NUMBER + " text not null, "
			+ COLUMN_DESCRIPTION + " text not null);";
	
	public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SRTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SR);
		onCreate(database);
	}

}
