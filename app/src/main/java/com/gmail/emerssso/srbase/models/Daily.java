package com.gmail.emerssso.srbase.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.emerssso.srbase.database.DailyTable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Model class for the a daily log entry.
 */
public class Daily {

    private int id;
    private String srId;
    private int day;
    private int month;
    private int year;
    private int startHour = -1;
    private int startMin = -1;
    private int endHour = -1;
    private int endMin = -1;
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

    /**
     * Checks to ensure that start is before end
     * @return true if start before end, else false
     */
    public boolean startAndEndReversed() {
        return startHour > endHour ||
                (startHour == endHour && startMin > endMin);
        
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(DailyTable.COLUMN_SR_ID, srId != null ? srId : "");
        cv.put(DailyTable.COLUMN_DAY, day);
        cv.put(DailyTable.COLUMN_MONTH, month);
        cv.put(DailyTable.COLUMN_YEAR, year);
        cv.put(DailyTable.COLUMN_START_HOUR, startHour);
        cv.put(DailyTable.COLUMN_START_MIN, startMin);
        cv.put(DailyTable.COLUMN_END_HOUR, endHour);
        cv.put(DailyTable.COLUMN_END_MIN, endMin);
        cv.put(DailyTable.COLUMN_TRAVEL_TIME, travelTime != null ? travelTime : "");
        cv.put(DailyTable.COLUMN_COMMENT, comment != null ? comment : "");

        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Daily) {
            Daily that = (Daily) o;

            return new EqualsBuilder()
                    .append(this.getSrId(), that.getSrId())
                    .append(this.getDay(), that.getDay())
                    .append(this.getMonth(), that.getMonth())
                    .append(this.getYear(), that.getYear())
                    .append(this.getStartHour(), that.getStartHour())
                    .append(this.getStartMin(), that.getStartMin())
                    .append(this.getEndHour(), that.getEndHour())
                    .append(this.getEndMin(), that.getEndMin())
                    .append(this.getTravelTime(), that.getTravelTime())
                    .append(this.getComment(), that.getComment())
                    .isEquals();

        } else
            return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.getSrId())
                .append(this.getDay())
                .append(this.getMonth())
                .append(this.getYear())
                .append(this.getStartHour())
                .append(this.getStartMin())
                .append(this.getEndHour())
                .append(this.getEndMin())
                .append(this.getTravelTime())
                .append(this.getComment())
                .toHashCode();
    }
}
