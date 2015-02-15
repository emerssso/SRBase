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
import android.text.TextUtils;

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
        String id;

        //These switches get ungainly and long with multiple tables...
        //StackOverflow suggests this is best practice, but it would be
        //nice to find something more readable
        switch (uriType) {
            case SRS:
                rowsDeleted = wDB.delete(SRTable.TABLE_SR, selection,
                        selArgs);
                break;
            case SR_ID:
                id = uri.getLastPathSegment();
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
            case DAILIES:
                rowsDeleted = wDB.delete(DailyTable.TABLE_DAILY, selection,
                        selArgs);
                break;
            case DAILY_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = wDB.delete(DailyTable.TABLE_DAILY,
                            DailyTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = wDB.delete(DailyTable.TABLE_DAILY,
                            DailyTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selArgs);
                }
                break;
            case PARTS:
                rowsDeleted = wDB.delete(PartTable.TABLE_PART, selection,
                        selArgs);
                break;
            case PART_ID:
                id = uri.getLastPathSegment();
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
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(SR_CONTENT_URI.toString() + "/" + id);
            case DAILIES:
                id = sqlDB.insert(DailyTable.TABLE_DAILY, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(DAILY_CONTENT_URI.toString() + "/" + id);
            case PARTS:
                id = sqlDB.insert(PartTable.TABLE_PART, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(PART_CONTENT_URI.toString() + "/" + id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
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
        switch (uriType) {
            case SRS:
                // Set the table
                queryBuilder.setTables(SRTable.TABLE_SR);
                checkSRColumns(projection);
                break;
            case DAILIES:
                // Set the table
                queryBuilder.setTables(DailyTable.TABLE_DAILY);
                checkDailyColumns(projection);
                break;
            case PARTS:
                // Set the table
                queryBuilder.setTables(PartTable.TABLE_PART);
                checkPartColumns(projection);
                break;
            case SR_ID:
                // Set the table
                queryBuilder.setTables(SRTable.TABLE_SR);
                // adding the ID to the original query
                checkSRColumns(projection);
                queryBuilder.appendWhere(SRTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case DAILY_ID:
                checkDailyColumns(projection);

                // Set the table
                queryBuilder.setTables(DailyTable.TABLE_DAILY);
                // adding the ID to the original query
                queryBuilder.appendWhere(DailyTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case PART_ID:
                checkPartColumns(projection);

                // Set the table
                queryBuilder.setTables(PartTable.TABLE_PART);
                // adding the ID to the original query
                queryBuilder.appendWhere(PartTable.COLUMN_ID + "="
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
        String id;

        switch (uriType) {
            case SRS:
                rowsUpdated = sqlDB.update(SRTable.TABLE_SR,
                        values,
                        selection,
                        selArgs);
                break;
            case SR_ID:
                id = uri.getLastPathSegment();
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
            case DAILIES:
                rowsUpdated = sqlDB.update(DailyTable.TABLE_DAILY,
                        values,
                        selection,
                        selArgs);
                break;
            case DAILY_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DailyTable.TABLE_DAILY,
                            values,
                            DailyTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(DailyTable.TABLE_DAILY,
                            values,
                            DailyTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selArgs);
                }
                break;
            case PARTS:
                rowsUpdated = sqlDB.update(PartTable.TABLE_PART,
                        values,
                        selection,
                        selArgs);
                break;
            case PART_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PartTable.TABLE_PART,
                            values,
                            PartTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(PartTable.TABLE_PART,
                            values,
                            PartTable.COLUMN_ID + "=" + id
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
     * Checks to ensure that a projection specifies only valid columns of
     * the SR table.
     *
     * @param projection The projection to test for validity.
     */
    private void checkSRColumns(String[] projection) {
        String[] available = {SRTable.COLUMN_CUSTOMER_NAME,
                SRTable.COLUMN_DESCRIPTION, SRTable.COLUMN_ID,
                SRTable.COLUMN_MODEL_NUMBER, SRTable.COLUMN_SERIAL_NUMBER,
                SRTable.COLUMN_SR_NUMBER, SRTable.COLUMN_BUSINESS_NAME};
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

    /**
     * Checks to ensure that a projection contains only valid columns of the
     * daily table.
     *
     * @param projection The projection to check.
     */
    private void checkDailyColumns(String[] projection) {
        String[] available = {DailyTable.COLUMN_COMMENT,
                DailyTable.COLUMN_DAY, DailyTable.COLUMN_END_HOUR,
                DailyTable.COLUMN_ID, DailyTable.COLUMN_SR_ID,
                DailyTable.COLUMN_START_HOUR, DailyTable.COLUMN_MONTH,
                DailyTable.COLUMN_YEAR, DailyTable.COLUMN_START_MIN,
                DailyTable.COLUMN_END_MIN,
                DailyTable.COLUMN_TRAVEL_TIME};
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

    /**
     * Checks to make sure that a projection only contains valid columns of
     * part table.
     *
     * @param projection The projection to check for validity.
     */
    private void checkPartColumns(String[] projection) {
        String[] available = {PartTable.COLUMN_DESCRIPTION,
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
                for (String aProjection : projection)
                    except = except + aProjection + ", ";
                throw new IllegalArgumentException(
                        "Unknown columns in projection: " + except);
            }
        }
    }
}
