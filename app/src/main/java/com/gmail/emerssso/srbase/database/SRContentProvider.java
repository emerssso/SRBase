//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * The Class SRContentProvider.  This class acts as the ContentProvider
 * for the app's internal database.  The manifest currently ensures that this
 * database is not exported to external accesses, however, this would be trivial
 * to implement if there were ever a motivation.  I don't recommend it
 * at the moment.
 *
 * @author Conner Kasten
 */
@SuppressWarnings("UnusedAssignment")
public class SRContentProvider extends ContentProvider {

    /**
     * The string indicating that content contains SRs.
     */
    public static final String SR_CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/SRs";
    /**
     * The string indicating that content is an SR.
     */
    public static final String SR_CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/SR";
    /**
     * The string indicating that content contains dailies
     */
    public static final String DAILY_CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/dailies";
    /**
     * The string indicating that content is a daily
     */
    public static final String DAILY_CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/daily";
    /**
     * The string indicating that content contains parts.
     */
    public static final String PART_CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/parts";
    /**
     * The string indicating that content is a part.
     */
    public static final String PART_CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/part";
    /**
     * The permission string representation for accessing this ContentProvider.
     */
    public static final String AUTHORITY =
            "com.gmail.emerssso.srbase.srcontentprovider";
    /**
     * The URI number for SRs.
     */
    private static final int SRS = 11;
    /**
     * The URI number to specify a specific SR by _id.
     */
    private static final int SR_ID = 21;
    /**
     * The URI number for dailies.
     */
    private static final int DAILIES = 12;
    /**
     * The URI number to specify a daily by _id.
     */
    private static final int DAILY_ID = 22;
    /**
     * The URI number for parts.
     */
    private static final int PARTS = 13;
    /**
     * The URI number to specify a part by _id.
     */
    private static final int PART_ID = 23;
    /**
     * The base path for accessing SRs.
     */
    private static final String SR_BASE_PATH = "SRs";
    /**
     * The URI for accessing an SR.
     */
    public static final Uri SR_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + SR_BASE_PATH);
    /**
     * The base path for accessing dailies.
     */
    private static final String DAILY_BASE_PATH = "dailies";
    /**
     * The URI for accessing a Daily.
     */
    public static final Uri DAILY_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + DAILY_BASE_PATH);
    /**
     * The base path for accessing parts.
     */
    private static final String PART_BASE_PATH = "parts";
    /**
     * The URI for accessing a part.
     */
    public static final Uri PART_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PART_BASE_PATH);
    /**
     * The URIMatcher used to determine if a URI specifies a row or rows in
     * the database handled by this provider.
     */
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, SR_BASE_PATH, SRS);
        sURIMatcher.addURI(AUTHORITY, SR_BASE_PATH + "/#", SR_ID);

        sURIMatcher.addURI(AUTHORITY, PART_BASE_PATH, PARTS);
        sURIMatcher.addURI(AUTHORITY, PART_BASE_PATH + "/#", PART_ID);

        sURIMatcher.addURI(AUTHORITY, DAILY_BASE_PATH, DAILIES);
        sURIMatcher.addURI(AUTHORITY, DAILY_BASE_PATH + "/#", DAILY_ID);
    }

    /**
     * The OpenSQLiteHelper used by this ContentProvider to manage the database.
     */
    private SRDatabaseHelper database;

    /* (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String selection, String[] selArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        SQLiteDatabase wDB = database.getWritableDatabase();

        switch (uriType) {
            case SRS:
                rowsDeleted = wDB.delete(SRTable.TABLE_NAME, selection, selArgs);
                break;
            case SR_ID:
                rowsDeleted = deleteIdTypeUri(uri, selection, selArgs, wDB, SRTable.TABLE_NAME);
                break;
            case DAILIES:
                rowsDeleted = wDB.delete(DailyTable.TABLE_NAME, selection, selArgs);
                break;
            case DAILY_ID:
                rowsDeleted = deleteIdTypeUri(uri, selection, selArgs, wDB, DailyTable.TABLE_NAME);
                break;
            case PARTS:
                rowsDeleted = wDB.delete(PartTable.TABLE_NAME, selection, selArgs);
                break;
            case PART_ID:
                rowsDeleted = deleteIdTypeUri(uri, selection, selArgs, wDB, PartTable.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Deletes an item indecated by an ID Uri
     * @param uri Uri of item to delete
     * @param selection selection for items to delete
     * @param selArgs arguments for selection
     * @param wDB DB to act on
     * @param tableName Table to delete from
     * @return number of rows deleted
     */
    private static int deleteIdTypeUri(Uri uri, String selection, String[] selArgs, 
                                SQLiteDatabase wDB, String tableName) {
        String id = uri.getLastPathSegment();
        if (StringUtils.isBlank(selection)) {
            selection = "_id =" + id;
        } else {
            selection = "_id =" + id + " and " + selection;
        }
        return wDB.delete(tableName, selection, selArgs);
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
        switch (uriType) {
            case SRS:
                return insertIntoTable(SRTable.TABLE_NAME, SR_CONTENT_URI, uri, values, sqlDB);
            case DAILIES:
                return insertIntoTable(DailyTable.TABLE_NAME, DAILY_CONTENT_URI, uri, values, sqlDB);
            case PARTS:
                return insertIntoTable(PartTable.TABLE_NAME, PART_CONTENT_URI, uri, values, sqlDB);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private Uri insertIntoTable(String table, Uri contentUri, Uri requestedUri, 
                                ContentValues values, SQLiteDatabase sqlDB) {
        long id = sqlDB.insert(table, null, values);
        getContext().getContentResolver().notifyChange(requestedUri, null);
        return Uri.parse(contentUri + "/" + id);
    }

    /* (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        database = new SRDatabaseHelper(getContext());
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

        int uriType = sURIMatcher.match(uri);
        String tableName = "";
        String[] columns;
        switch (uriType) {
            case SRS:
            case SR_ID:
                tableName = SRTable.TABLE_NAME;
                columns = SRTable.COLUMNS;
                break;
            case DAILIES:
            case DAILY_ID:
                tableName = DailyTable.TABLE_NAME;
                columns = DailyTable.COLUMNS;
                break;
            case PARTS:
            case PART_ID:
                tableName = PartTable.TABLE_NAME;
                columns = PartTable.COLUMNS;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        checkColumns(projection, columns);
        queryBuilder.setTables(tableName);
        if(isIdType(uriType)) {
            queryBuilder.appendWhere("_id =" + uri.getLastPathSegment());
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * helper method that determines whether URI is of ID type (and has ID as a path element).
     * @param uriType URI type to check
     * @return true if is of type ID, else false
     */
    private static boolean isIdType(int uriType) {
        return uriType > 20;
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
        String id;

        switch (uriType) {
            case SRS:
                rowsUpdated = sqlDB.update(SRTable.TABLE_NAME, values, selection, selArgs);
                break;
            case SR_ID:
                rowsUpdated = updateIdTypeUri(uri, values, selection, selArgs, 
                        sqlDB, SRTable.TABLE_NAME);
                break;
            case DAILIES:
                rowsUpdated = sqlDB.update(DailyTable.TABLE_NAME, values, selection, selArgs);
                break;
            case DAILY_ID:
                rowsUpdated = updateIdTypeUri(uri, values, selection, selArgs,
                        sqlDB, DailyTable.TABLE_NAME);
                break;
            case PARTS:
                rowsUpdated = sqlDB.update(PartTable.TABLE_NAME, values, selection, selArgs);
                break;
            case PART_ID:
                rowsUpdated = updateIdTypeUri(uri, values, selection, selArgs,
                        sqlDB, PartTable.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    /**
     * Helper method to update an item represented by an ID type Uri 
     * @param uri Uri to update
     * @param values Values to update items represented by Uri with
     * @param selection Selection to select items to update with
     * @param selArgs arguments for selection
     * @param sqlDB db to perform update on
     * @param table table to perform update on
     * @return number of rows updated
     */
    private int updateIdTypeUri(Uri uri, ContentValues values, String selection, String[] selArgs, 
                                SQLiteDatabase sqlDB, String table) {
        String id = uri.getLastPathSegment();
        if (StringUtils.isBlank(selection)) {
            selection = "_id = " + id;
        } else {
            selection = "_id = " + id + selection;
        }
        return sqlDB.update(table, values, selection, selArgs);
    }

    private void checkColumns(String[] projection, String[] available) {
        if (projection != null) {
            HashSet<String> requestedColumns = new
                    HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new
                    HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                String except = "";
                for (String aProjection : projection)
                    except = except + aProjection + ", ";
                throw new IllegalArgumentException(
                        "Unknown columns in projection: " + except);
            }
        }
    }
}
