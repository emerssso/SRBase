//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The model of the table of Daily Logs.  Includes constants for column names
 * and the necessary SQL for creating and upgrading the table.
 */
public class DailyTable {

    /**
     * The name of the table, in constant form.
     */
    public static final String TABLE_DAILY = "daily";

    /**
     * The name of the primary key column, as per Android best practice.
     */
    public static final String COLUMN_ID = "_id";

    /**
     * The _id of the associated SR.
     */
    public static final String COLUMN_SR_ID = "SR_id";

    /**
     * The name of the day column
     */
    public static final String COLUMN_DAY = "day";

    /**
     * the name of the month column
     */
    public static final String COLUMN_MONTH = "month";

    /**
     * The name of the year column
     */
    public static final String COLUMN_YEAR = "year";

    /**
     * The name of the start hour column.
     */
    public static final String COLUMN_START_HOUR = "start_hour";

    /**
     * The name of the start minute column
     */
    public static final String COLUMN_START_MIN = "start_min";

    /**
     * The name of the end hour column.
     */
    public static final String COLUMN_END_HOUR = "end_hour";

    /**
     * The name of the end minute column
     */
    public static final String COLUMN_END_MIN = "end_min";

    /**
     * The name of the travel time column.
     */
    public static final String COLUMN_TRAVEL_TIME = "travel_time";

    /**
     * The name of the comment column.
     */
    public static final String COLUMN_COMMENT = "comment";

    public static final String[] COLUMNS = new String[]{COLUMN_ID, COLUMN_SR_ID,
            COLUMN_DAY, COLUMN_MONTH, COLUMN_YEAR,
            COLUMN_START_HOUR, COLUMN_START_MIN,
            COLUMN_END_HOUR, COLUMN_END_MIN,
            COLUMN_TRAVEL_TIME, COLUMN_COMMENT};

    /**
     * This string contains the SQL for creating this table.
     */
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DAILY + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_SR_ID + " text not null, "
            + COLUMN_DAY + " integer, "
            + COLUMN_MONTH + " integer, "
            + COLUMN_YEAR + " integer, "
            + COLUMN_START_HOUR + " integer, "
            + COLUMN_START_MIN + " integer, "
            + COLUMN_END_HOUR + " integer, "
            + COLUMN_END_MIN + " integer, "
            + COLUMN_TRAVEL_TIME + " text not null, "
            + COLUMN_COMMENT + " text not null);";

    /**
     * This method executes DATABASE_CREATE to create the table
     * in the SQLite database.
     *
     * @param database the database
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * Upgrades the database from version 1 to 2.
     * Note that version 1 used a three database schema for
     * storing dailies, parts, and SRs, while later versions
     * use a one database schema with three tables. Attach old database
     * before calling, or this will fail.  The old database is located at
     * '/data/data/com.gmail.emerssso.srbase/databases/dailytable.db'.
     * The method assumes that the table has been attached as 'dailytable'.
     *
     * @param database   The target database to upgrade (main in the SQL)
     * @param oldVersion the old version number. Only 1 is currently valid.
     * @param newVersion the new version number. Only 2 is currently valid.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        if (oldVersion == 1 && newVersion >= 2) {
            Log.d(DailyTable.class.getSimpleName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will migrate data to new location");
            onCreate(database);
            database.execSQL(
                    "INSERT INTO main.daily SELECT * FROM dailytable.daily");
            database.execSQL("DROP TABLE dailytable.daily");
        }
    }

}
