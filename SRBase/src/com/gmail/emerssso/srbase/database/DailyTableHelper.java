//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * An SQLiteOpenHelper for the daily database.
 */
public class DailyTableHelper extends SQLiteOpenHelper {
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "dailytable.db";
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Instantiates a new daily table helper.
	 *
	 * @param context the context
	 */
	public DailyTableHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		DailyTable.onCreate(database);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, 
			int oldVersion, int newVersion) {
		DailyTable.onUpgrade(database, oldVersion, newVersion);
	}
}
