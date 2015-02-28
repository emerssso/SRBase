package com.gmail.emerssso.srbase.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.emerssso.srbase.database.PartTable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Simple model of a part used on an SR.
 */
public class Part {
    private int id;
    private String srId = "";
    private String partNumber = "";
    private String quantity = "";
    private String used = "";
    private String source = "";
    private String description = "";

    public static Part fromCursor(Cursor cursor) {
        if (cursor != null && !cursor.isAfterLast()) {
            Part part = new Part();
            part.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PartTable.COLUMN_ID)));
            part.setSrId(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_SR_ID)));
            part.setPartNumber(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_PART_NUMBER)));
            part.setQuantity(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_QUANTITY)));
            part.setSource(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_SOURCE)));
            part.setUsed(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_USED)));
            part.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_DESCRIPTION)));

            return part;
        } else
            return null;
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

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(PartTable.COLUMN_ID, id);
        cv.put(PartTable.COLUMN_SR_ID, srId);
        cv.put(PartTable.COLUMN_PART_NUMBER, partNumber);
        cv.put(PartTable.COLUMN_QUANTITY, quantity);
        cv.put(PartTable.COLUMN_USED, used);
        cv.put(PartTable.COLUMN_SOURCE, source);
        cv.put(PartTable.COLUMN_DESCRIPTION, description);

        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Part) {
            Part that = (Part) o;

            return new EqualsBuilder()
                    .append(this.getSrId(), that.getSrId())
                    .append(this.getPartNumber(), that.getPartNumber())
                    .append(this.getQuantity(), that.getQuantity())
                    .append(this.getSource(), that.getSource())
                    .append(this.getUsed(), that.getUsed())
                    .append(this.getDescription(), that.getDescription())
                    .isEquals();
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.getSrId())
                .append(this.getPartNumber())
                .append(this.getQuantity())
                .append(this.getSource())
                .append(this.getUsed())
                .append(this.getDescription())
                .toHashCode();
    }
}
