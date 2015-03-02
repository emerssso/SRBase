//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;
import com.gmail.emerssso.srbase.models.SR;

import org.apache.commons.lang.StringUtils;

// TODO: Auto-generated Javadoc

/**
 * This activity implements an activity that provides a form
 * for users to add new Service Records (SRs) into the database,
 * or edit old SRs already present.
 *
 * @author Conner Kasten
 */
public class EditSRActivity extends DeletableActivity {

    private static final String TAG = "EditSRActivity";
    /**
     * The SR number.
     */
    private EditText mSRNumber;

    /**
     * The customer's name.
     */
    private EditText mCustomer;

    /**
     * The model number of the device being serviced.
     */
    private EditText mModelNumber;

    /**
     * The serial number of the device being serviced.
     */
    private EditText mSerialNumber;

    /**
     * The description of the call.
     */
    private EditText mDescription;

    private EditText mBusinessName;

    /**
     * The saved URI to load SR information from.
     */
    private Uri savedUri;

    /**
     * The _id number in the database of the SR.
     */
    private String myId;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_sr_activity);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.edit_sr);
        }

        mSRNumber = (EditText) findViewById(R.id.SRNumber);
        mCustomer = (EditText) findViewById(R.id.customerName);
        mModelNumber = (EditText) findViewById(R.id.modelNumber);
        mSerialNumber = (EditText) findViewById(R.id.serialNumber);
        mDescription = (EditText) findViewById(R.id.description);
        mBusinessName = (EditText) findViewById(R.id.businessName);
        /* The Daily Button opens a dialog to add a new daily
	  associated with this SR. */
        Button mDaily = (Button) findViewById(R.id.add_daily);
		/* The Part Button opens a new dialog to add a new part
	  associated with this SR.. */
        Button mPart = (Button) findViewById(R.id.add_part);
		/* The Confirm Button saves the SR to the database. */
        Button mEnter = (Button) findViewById(R.id.confirm);

        Bundle extras = getIntent().getExtras();

        savedUri = (savedInstanceState == null) ? null :
                (Uri) savedInstanceState
                        .getParcelable(SRContentProvider.SR_CONTENT_ITEM_TYPE);

        if (extras != null) {
            savedUri = extras
                    .getParcelable(SRContentProvider.SR_CONTENT_ITEM_TYPE);
            fillData(savedUri);
        }
        
        if(savedUri != null) {
            myId = savedUri.getLastPathSegment();
        }

        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSRNumber.getText().toString().length() == 0) {
                    Toast.makeText(EditSRActivity.this, "SR Number missing",
                            Toast.LENGTH_LONG).show();
                } else {
                    saveState();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        mPart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveState();
                addPart();
            }
        });

        mDaily.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveState();
                addDaily();
            }
        });
    }

    /**
     * Adds the part.
     */
    private void addPart() {
        Intent i = new Intent(this, EditPartActivity.class);
        i.putExtra(PartTable.COLUMN_SR_ID, myId);
        i.putExtra(SRTable.COLUMN_SR_NUMBER, mSRNumber.getText().toString());
        startActivity(i);
    }

    /**
     * Adds the daily.
     */
    private void addDaily() {
        Intent i = new Intent(this, EditDailyActivity.class);
        i.putExtra(DailyTable.COLUMN_SR_ID, myId);
        i.putExtra(SRTable.COLUMN_SR_NUMBER, mSRNumber.getText().toString());
        startActivity(i);
    }

    /**
     * Fill data from the database into the form.
     *
     * @param uri the uri of the database entry to load
     */
    private void fillData(Uri uri) {
        String[] projection = {SRTable.COLUMN_CUSTOMER_NAME,
                SRTable.COLUMN_DESCRIPTION, SRTable.COLUMN_MODEL_NUMBER,
                SRTable.COLUMN_SERIAL_NUMBER, SRTable.COLUMN_SR_NUMBER,
                SRTable.COLUMN_ID, SRTable.COLUMN_BUSINESS_NAME};
        Cursor cursor = getContentResolver()
                .query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            SR sr = SR.fromCursor(cursor);
            cursor.close();
            
            mSRNumber.setText(sr.getNumber());
            mCustomer.setText(sr.getCustomerName());
            mBusinessName.setText(sr.getBusinessName());
            mModelNumber.setText(sr.getModelNumber());
            mSerialNumber.setText(sr.getSerialNumber());
            mDescription.setText(sr.getDescription());
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(SRContentProvider.SR_CONTENT_ITEM_TYPE, savedUri);
    }

    /**
     * Save state from the form into the database.
     */
    private void saveState() {

        SR sr = new SR();
        sr.setNumber(mSRNumber.getText().toString());

        if (!StringUtils.isBlank(sr.getNumber())) {

            sr.setCustomerName(mCustomer.getText().toString());
            sr.setBusinessName(mBusinessName.getText().toString());
            sr.setModelNumber(mModelNumber.getText().toString());
            sr.setSerialNumber(mSerialNumber.getText().toString());
            sr.setDescription(mDescription.getText().toString());

            if (savedUri == null) {
                // New SR
                savedUri = getContentResolver()
                        .insert(SRContentProvider.SR_CONTENT_URI, sr.toContentValues());

                myId = savedUri.getLastPathSegment();
            } else {
                // Update SR
                getContentResolver().update(savedUri, sr.toContentValues(), null, null);
            }
        } else {
            Log.d(TAG, "aborting save, SR number is blank");
        }
    }

    /* (non-Javadoc)
     * We Override the DeletableActivity version so that we can delete
     * all associated entries
     * @see com.gmail.emerssso.srbase.DeletableActivity#delete(android.net.Uri)
     */
    @Override
    protected void delete(Uri uri) {
        if (uri != null) {
            //first: find and delete all associated parts
            getContentResolver().delete(SRContentProvider.PART_CONTENT_URI,
                    PartTable.COLUMN_SR_ID + " = ?", new String[]{myId});

            //second: delete all associated dailies
            getContentResolver().delete(SRContentProvider.DAILY_CONTENT_URI,
                    DailyTable.COLUMN_SR_ID + " = ?", new String[]{myId});

            //last: delete the SR itself
            getContentResolver().delete(uri, null, null);
        }
    }
}