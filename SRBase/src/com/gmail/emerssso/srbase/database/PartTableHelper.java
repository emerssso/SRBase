//TODO: Copyright Info
package com.gmail.emerssso.srbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class PartTableHelper.
 */
public class PartTableHelper extends SQLiteOpenHelper {

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "parttable.db";
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Instantiates a new part table helper.
	 *
	 * @param context the context
	 */
	public PartTableHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		SRTable.onCreate(database);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, 
			int oldVersion, int newVersion) {
		SRTable.onUpgrade(database, oldVersion, newVersion);
	}
}
