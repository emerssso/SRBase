//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * An SQLiteOpenHelper for the SR Database.  Manages the databases tables
 * as specified by the table model classes.
 * @author Conner Kasten
 */
public class SRDatabaseHelper extends SQLiteOpenHelper {
	
	/** The file name for the database. */
	private static final String DATABASE_NAME = "SRtable.db";
	
	/** The version number of the database. */
	private static final int DATABASE_VERSION = 2;
	
	/**
	 * Instantiates a new OpenSQLiteHelper for the SR database.
	 *
	 * @param context The calling context.
	 */
	public SRDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		SRTable.onCreate(database);
		DailyTable.onCreate(database);
		PartTable.onCreate(database);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, 
			int oldVersion, int newVersion) {
		//First, if we're dealing with an old, multi-database schema,
		//attach the old databases so that we can copy data over to the new one.
		if(oldVersion < 2) {
			database.setTransactionSuccessful();
			database.endTransaction();
			database.execSQL(
					"ATTACH \"/data/data/com.gmail.emerssso.srbase/databases/parttable.db\" AS parttable");
			database.execSQL(
					"ATTACH \"/data/data/com.gmail.emerssso.srbase/databases/dailytable.db\" AS dailytable");
			database.beginTransaction();
		}
		SRTable.onUpgrade(database, oldVersion, newVersion);
		DailyTable.onUpgrade(database, oldVersion, newVersion);
		PartTable.onUpgrade(database, oldVersion, newVersion);
	}

}
