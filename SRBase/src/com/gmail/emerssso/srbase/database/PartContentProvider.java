//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * The Class PartContentProvider.
 * @author Conner Kasten
 */
public class PartContentProvider extends ContentProvider {
	
	/** The database. */
	private PartTableHelper database;
	
	/** The Constant PARTS. */
	private static final int PARTS = 13;
	
	/** The Constant PART_ID. */
	private static final int PART_ID = 23;
	
	/** The Constant AUTHORITY. */
	private static final String AUTHORITY = "" +
			"com.gmail.emerssso.srbase.partcontentprovider";
	
	/** The Constant BASE_PATH. */
	private static final String BASE_PATH = "parts";
	
	/** The Constant sURIMatcher. */
	private static final UriMatcher sURIMatcher = 
			new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, PARTS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PART_ID);
	}
	/** The Constant CONTENT_TYPE. */
	public static final String CONTENT_TYPE = 
		ContentResolver.CURSOR_DIR_BASE_TYPE + "/parts";
	
	/** The Constant CONTENT_ITEM_TYPE. */
	public static final String CONTENT_ITEM_TYPE = 
			ContentResolver.CURSOR_ITEM_BASE_TYPE + "/part";
	
	/** The Constant CONTENT_URI. */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selArgs) {
		int uriType = sURIMatcher.match(uri);
		int rowsDeleted = 0;
		SQLiteDatabase wDB = database.getWritableDatabase();
		
		switch(uriType) {
		case PARTS:
			rowsDeleted = wDB.delete(PartTable.TABLE_PART, selection,
					selArgs);
			break;
		case PART_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = wDB.delete(PartTable.TABLE_PART,
						PartTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = wDB.delete(PartTable.TABLE_PART,
						PartTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		// TODO What does this do?
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case PARTS:
			id = sqlDB.insert(PartTable.TABLE_PART, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(CONTENT_URI.toString() + "/" + id);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		database = new PartTableHelper(getContext());
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selArgs, String sortOrder) {

		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		checkColumns(projection);
		
		// Set the table
		queryBuilder.setTables(PartTable.TABLE_PART);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case PARTS:
		  break;
		case PART_ID:
		  // adding the ID to the original query
		  queryBuilder.appendWhere(SRTable.COLUMN_ID + "="
		      + uri.getLastPathSegment());
		  break;
		default:
		  throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
		    selArgs, null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case PARTS:
			rowsUpdated = sqlDB.update(PartTable.TABLE_PART, 
					values, 
					selection,
					selArgs);
			break;
		case PART_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(PartTable.TABLE_PART, 
						values,
						SRTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(PartTable.TABLE_PART, 
						values,
						SRTable.COLUMN_ID + "=" + id 
						+ " and " 
						+ selection,
						selArgs);
		  }
		  break;
		default:
		  throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	/**
	 * Check columns.
	 *
	 * @param projection the projection
	 */
	private void checkColumns(String[] projection) {
		String[] available = { PartTable.COLUMN_DESCRIPTION,
				PartTable.COLUMN_ID, PartTable.COLUMN_QUANTITY,
				PartTable.COLUMN_SOURCE, PartTable.COLUMN_SR_ID,
				PartTable.COLUMN_USED, PartTable.COLUMN_PART_NUMBER};
		if (projection != null) {
			HashSet<String> requestedColumns = new 
					HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new 
					HashSet<String>(Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				String except = "";
				for(int i = 0; i < projection.length; i++)
					except = except + projection[i] + ", ";
				throw new IllegalArgumentException(
						"Unknown columns in projection: " + except);
			}
		}
	}
}
