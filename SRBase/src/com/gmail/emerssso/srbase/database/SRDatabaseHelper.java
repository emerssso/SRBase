//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class SRTableHelper.
 * @author Conner Kasten
 */
public class SRDatabaseHelper extends SQLiteOpenHelper {
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "SRdatabase.db";
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 2;
	
	/**
	 * Instantiates a new sR table helper.
	 *
	 * @param context the context
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
		SRTable.onUpgrade(database, oldVersion, newVersion);
		DailyTable.onUpgrade(database, oldVersion, newVersion);
		PartTable.onUpgrade(database, oldVersion, newVersion);
	}

}
