package com.gmail.emerssso.srbase.models;

import android.content.ContentValues;

import com.gmail.emerssso.srbase.database.PartTable;

/**
 * Simple model of a part used on an SR.
 */
public class Part {
    private int id;
    private String srId;
    private String partNumber;
    private String quantity;
    private String used;
    private String source;
    private String description;

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

    public static Part fromContentValues(ContentValues cv) {
        Part part = new Part();

        part.setId(cv.getAsInteger(PartTable.COLUMN_ID));
        part.setPartNumber(cv.getAsString(PartTable.COLUMN_PART_NUMBER));
        part.setQuantity(cv.getAsString(PartTable.COLUMN_QUANTITY));
        part.setUsed(cv.getAsString(PartTable.COLUMN_USED));
        part.setSource(cv.getAsString(PartTable.COLUMN_SOURCE));
        part.setDescription(cv.getAsString(PartTable.COLUMN_DESCRIPTION));

        return part;
    }
}
