package com.gmail.emerssso.srbase.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DailyTableHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "dailytable.db";
	private static final int DATABASE_VERSION = 1;

	public DailyTableHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		DailyTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, 
			int oldVersion, int newVersion) {
		DailyTable.onUpgrade(database, oldVersion, newVersion);
	}
}
