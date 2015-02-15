package com.gmail.emerssso.srbase.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.emerssso.srbase.database.DailyTable;

/**
 * Created by Conner on 2/7/2015.
 */
public class Daily {

    private int id;
    private String srId;
    private int day;
    private int month;
    private int year;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;
    private String travelTime;
    private String comment;

    public static Daily fromCursor(Cursor cursor) {
        if (cursor != null && !cursor.isAfterLast()) {
            Daily daily = new Daily();
            daily.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_ID)));
            daily.setSrId(cursor.getString(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_SR_ID)));
            daily.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_DAY)));
            daily.setMonth(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_MONTH)));
            daily.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_YEAR)));
            daily.setStartHour(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_START_HOUR)));
            daily.setStartMin(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_START_MIN)));
            daily.setEndHour(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_END_HOUR)));
            daily.setEndMin(cursor.getInt(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_END_MIN)));
            daily.setTravelTime(cursor.getString(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_TRAVEL_TIME)));
            daily.setComment(cursor.getString(cursor.getColumnIndexOrThrow(DailyTable.COLUMN_COMMENT)));

            return daily;
        } else return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(DailyTable.COLUMN_ID, id);
        cv.put(DailyTable.COLUMN_SR_ID, srId);
        cv.put(DailyTable.COLUMN_DAY, day);
        cv.put(DailyTable.COLUMN_MONTH, month);
        cv.put(DailyTable.COLUMN_YEAR, year);
        cv.put(DailyTable.COLUMN_START_HOUR, startHour);
        cv.put(DailyTable.COLUMN_START_MIN, startMin);
        cv.put(DailyTable.COLUMN_END_HOUR, endHour);
        cv.put(DailyTable.COLUMN_END_MIN, endMin);
        cv.put(DailyTable.COLUMN_COMMENT, comment);

        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Daily) {
            Daily that = (Daily) o;

            return this.srId.equals(that.getSrId()) &&
                    this.day == that.getDay() &&
                    this.month == that.getMonth() &&
                    this.year == that.getYear() &&
                    this.startHour == that.getStartHour() &&
                    this.startMin == that.getStartMin() &&
                    this.endHour == that.getEndHour() &&
                    this.endMin == that.getEndMin() &&
                    this.getTravelTime().equals(that.getTravelTime()) &&
                    this.getComment().equals(that.getComment());

        } else return false;
    }
}
