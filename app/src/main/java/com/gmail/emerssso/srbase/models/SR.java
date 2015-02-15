package com.gmail.emerssso.srbase.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.gmail.emerssso.srbase.database.SRTable;

/**
 * A simple model class for an SR.
 */
public class SR {

    private int id;
    private String number;
    private String customerName;
    private String businessName;
    private String modelNumber;
    private String serialNumber;
    private String description;

    public static SR fromCursor(Cursor cursor) {
        SR sr = new SR();

        if (cursor != null && !cursor.isAfterLast()) {
            sr.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SRTable.COLUMN_ID)));
            sr.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(SRTable.COLUMN_SR_NUMBER)));
            sr.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(SRTable.COLUMN_CUSTOMER_NAME)));
            sr.setBusinessName(cursor.getString(cursor.getColumnIndexOrThrow(SRTable.COLUMN_BUSINESS_NAME)));
            sr.setModelNumber(cursor.getString(cursor.getColumnIndexOrThrow(SRTable.COLUMN_MODEL_NUMBER)));
            sr.setSerialNumber(cursor.getString(cursor.getColumnIndexOrThrow(SRTable.COLUMN_SERIAL_NUMBER)));
            sr.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SRTable.COLUMN_DESCRIPTION)));
        }

        return sr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(SRTable.COLUMN_ID, id);
        cv.put(SRTable.COLUMN_SR_NUMBER, number);
        cv.put(SRTable.COLUMN_CUSTOMER_NAME, customerName);
        cv.put(SRTable.COLUMN_BUSINESS_NAME, businessName);
        cv.put(SRTable.COLUMN_MODEL_NUMBER, modelNumber);
        cv.put(SRTable.COLUMN_SERIAL_NUMBER, serialNumber);
        cv.put(SRTable.COLUMN_DESCRIPTION, description);

        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SR) {
            SR that = (SR) o;
            return this.getId() == that.getId() &&
                    this.getNumber().equals(that.getNumber()) &&
                    this.getCustomerName().equals(that.getCustomerName()) &&
                    this.getBusinessName().equals(that.getBusinessName()) &&
                    this.getModelNumber().equals(that.getModelNumber()) &&
                    this.getSerialNumber().equals(that.getSerialNumber()) &&
                    this.getDescription().equals(that.getDescription());
        }
        return false;
    }
}
