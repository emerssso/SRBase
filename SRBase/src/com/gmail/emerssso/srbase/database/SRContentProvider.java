//TODO: Copyright info
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

// TODO: Auto-generated Javadoc
/**
 * The Class SRContentProvider.
 */
public class SRContentProvider extends ContentProvider {
	
	/** The database. */
	private SRTableHelper database;
	
	/** The Constant SRS. */
	private static final int SRS = 11;
	
	/** The Constant SR_ID. */
	private static final int SR_ID = 21;
	
	/** The Constant AUTHORITY. */
	private static final String AUTHORITY = 
			"com.gmail.emerssso.srbase.srcontentprovider";
	
	/** The Constant BASE_PATH. */
	private static final String BASE_PATH = "SRs";
	
	/** The Constant sURIMatcher. */
	private static final UriMatcher sURIMatcher = 
			new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, SRS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SR_ID);
	}
	
	/** The Constant CONTENT_TYPE. */
	public static final String CONTENT_TYPE = 
			ContentResolver.CURSOR_DIR_BASE_TYPE + "/SRs";
	
	/** The Constant CONTENT_ITEM_TYPE. */
	public static final String CONTENT_ITEM_TYPE = 
			ContentResolver.CURSOR_ITEM_BASE_TYPE + "/SR";
	
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
		case SRS:
			rowsDeleted = wDB.delete(SRTable.TABLE_SR, selection,
					selArgs);
			break;
		case SR_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = wDB.delete(SRTable.TABLE_SR,
						SRTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = wDB.delete(SRTable.TABLE_SR,
						SRTable.COLUMN_ID + "=" + id 
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
		// TODO Figure out what this is supposed to do
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
		case SRS:
			id = sqlDB.insert(SRTable.TABLE_SR, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		database = new SRTableHelper(getContext());
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
		queryBuilder.setTables(SRTable.TABLE_SR);
		
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SRS:
		  break;
		case SR_ID:
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
		case SRS:
			rowsUpdated = sqlDB.update(SRTable.TABLE_SR, 
					values, 
					selection,
					selArgs);
			break;
		case SR_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(SRTable.TABLE_SR, 
						values,
						SRTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(SRTable.TABLE_SR, 
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
		String[] available = { SRTable.COLUMN_CUSTOMER_NAME,
				SRTable.COLUMN_DESCRIPTION, SRTable.COLUMN_ID,
				SRTable.COLUMN_MODEL_NUMBER, SRTable.COLUMN_SERIAL_NUMBER,
				SRTable.COLUMN_SR_NUMBER };
		if (projection != null) {
			HashSet<String> requestedColumns = new 
					HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new 
					HashSet<String>(Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}
}
