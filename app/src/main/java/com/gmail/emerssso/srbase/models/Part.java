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
    public static final String UNKNOWN = "Unknown";
    public static final String NO_DESCRIPTION = "No Description";
    private int id;
    private String srId = "";
    private String partNumber = "";
    private String quantity = UNKNOWN;
    private boolean used = false;
    private String source = UNKNOWN;
    private String description = "";

    public static Part fromCursor(Cursor cursor) {
        if (cursor != null && !cursor.isAfterLast()) {
            Part part = new Part();
            part.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PartTable.COLUMN_ID)));
            part.setSrId(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_SR_ID)));
            part.setPartNumber(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_PART_NUMBER)));
            part.setQuantity(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_QUANTITY)));
            part.setSource(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_SOURCE)));
            part.setUsed(cursor.getString(cursor.getColumnIndexOrThrow(PartTable.COLUMN_USED)).equals("Used"));
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
        this.quantity = quantity.length() == 0 ? UNKNOWN : quantity;
    }
    
    public boolean quantityAvailable() {
        return !quantity.equals(UNKNOWN);
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source.length() == 0 ? UNKNOWN : source;
    }
    
    public boolean sourceAvailable() {
        return !source.equals(UNKNOWN);
    }

    public String getDescription() {
        return description.length() == 0 ? NO_DESCRIPTION : description;
    }
    
    public boolean descriptionAvailable() {
        return !description.equals(NO_DESCRIPTION);
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
        cv.put(PartTable.COLUMN_USED, used ? "Used" : "Unused");
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
                    .append(this.isUsed(), that.isUsed())
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
                .append(this.isUsed())
                .append(this.getDescription())
                .toHashCode();
    }
}
