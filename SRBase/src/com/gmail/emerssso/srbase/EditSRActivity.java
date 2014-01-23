//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartContentProvider;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * This activity implements an activity that provides a form
 * for users to add new Service Records (SRs) into the database,
 * or edit old SRs already present.
 *
 * @author Conner Kasten
 */
public class EditSRActivity extends DeletableActivity {
	
	/** The SR number. */
	private EditText mSRNumber;
	
	/** The customer's name. */
	private EditText mCustomer;
	
	/** The model number of the device being serviced. */
	private EditText mModelNumber;
	
	/** The serial number of the device being serviced. */
	private EditText mSerialNumber;
	
	/** The description of the call. */
	private EditText mDescription;
	
	/** The Daily Button opens a dialog to add a new daily
	 * associated with this SR. */
	private Button mDaily;
	
	/** The Part Button opens a new dialog to add a new part
	 * associated with this SR.. */
	private Button mPart;
	
	/** The Confirm Button saves the SR to the database. */
	private Button mEnter;
	
	/** The saved URI to load SR information from. */
	private Uri savedUri;
	
	/** The _id number in the database of the SR. */
	private String myId;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_sr_activity);
		
		mSRNumber = (EditText) findViewById(R.id.SRNumber);
		mCustomer = (EditText) findViewById(R.id.customerName);
		mModelNumber = (EditText) findViewById(R.id.modelNumber);
		mSerialNumber = (EditText) findViewById(R.id.serialNumber);
		mDescription = (EditText) findViewById(R.id.description);
		mDaily = (Button) findViewById(R.id.add_daily);
		mPart = (Button) findViewById(R.id.add_part);
		mEnter = (Button) findViewById(R.id.confirm);
		
		Bundle extras = getIntent().getExtras();
		
		savedUri = (savedInstanceState == null) ? null : 
			(Uri) savedInstanceState
	        .getParcelable(SRContentProvider.CONTENT_ITEM_TYPE);
		
	    if (extras != null) {
	    	savedUri = extras
	    			.getParcelable(SRContentProvider.CONTENT_ITEM_TYPE);
	    	fillData(savedUri);
    	}
		
		mEnter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSRNumber.getText().toString().length() == 0) {
					Toast.makeText(EditSRActivity.this, "SR Number missing",
					        Toast.LENGTH_LONG).show();
					return;
				}
				else {
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
		startActivity(i);
	}
	
	/**
	 * Adds the daily.
	 */
	private void addDaily() {
		Intent i = new Intent(this, EditDailyActivity.class);
		i.putExtra(DailyTable.COLUMN_SR_ID, myId);
		startActivity(i);
	}
	
	/**
	 * Fill data from the database into the form.
	 *
	 * @param uri the uri of the database entry to load
	 */
	private void fillData(Uri uri) {
		String[] projection = { SRTable.COLUMN_CUSTOMER_NAME,
				SRTable.COLUMN_DESCRIPTION, SRTable.COLUMN_MODEL_NUMBER,
				SRTable.COLUMN_SERIAL_NUMBER, SRTable.COLUMN_SR_NUMBER,
				SRTable.COLUMN_ID};
		Cursor cursor = getContentResolver()
				.query(uri, projection, null, null,null);
		if (cursor != null) {
			cursor.moveToFirst();

		    mSRNumber.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(SRTable.COLUMN_SR_NUMBER)));
		    mCustomer.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(SRTable.COLUMN_CUSTOMER_NAME)));
		    mModelNumber.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(SRTable.COLUMN_MODEL_NUMBER)));
		    mSerialNumber.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(SRTable.COLUMN_SERIAL_NUMBER)));
		    mDescription.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(SRTable.COLUMN_DESCRIPTION)));
		    myId = Integer.toString(cursor.getInt(cursor
		    		.getColumnIndexOrThrow(SRTable.COLUMN_ID)));

		    cursor.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(SRContentProvider.CONTENT_ITEM_TYPE, savedUri);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}
	
	/**
	 * Save state from the form into the database.
	 */
	private void saveState() {
		
		String srNumber = mSRNumber.getText().toString();
		
		if(srNumber.length() == 0)
			return;
		
		String customer = mCustomer.getText().toString();
		String modelNumber = mModelNumber.getText().toString();
		String serialNumber = mSerialNumber.getText().toString();
		String description = mDescription.getText().toString();

		ContentValues values = new ContentValues();
		values.put(SRTable.COLUMN_SR_NUMBER, srNumber);
		values.put(SRTable.COLUMN_CUSTOMER_NAME, customer);
		values.put(SRTable.COLUMN_MODEL_NUMBER, modelNumber);
		values.put(SRTable.COLUMN_SERIAL_NUMBER, serialNumber);
		values.put(SRTable.COLUMN_DESCRIPTION, description);

	    if (savedUri == null) {
	    	// New SR
	    	savedUri = getContentResolver()
	    			.insert(SRContentProvider.CONTENT_URI, values);
  
	    	Cursor cursor = getContentResolver().query(savedUri, 
	    			new String[] {SRTable.COLUMN_ID}, 
	    			SRTable.COLUMN_SR_NUMBER + " = ?", 
	    			new String[] {srNumber}, null);
	    	if(cursor != null) {
	    		cursor.moveToFirst();
	    		int myIdNum = cursor.getInt(cursor
			    		.getColumnIndexOrThrow(SRTable.COLUMN_ID));
	    		myId = String.valueOf(myIdNum);
    		}
	    	
	    	cursor.close();
	    } 
	    else {
	      // Update SR
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
	
	/* (non-Javadoc)
	 * We Override the DeletableActivity version so that we can delete
	 * all associated entries
	 * @see com.gmail.emerssso.srbase.DeletableActivity#delete(android.net.Uri)
	 */
	@Override
	protected void delete(Uri uri) {
		if(uri != null) {
			//first: find and delete all associated parts
			getContentResolver().delete(PartContentProvider.CONTENT_URI, 
					PartTable.COLUMN_SR_ID + " = ?", new String[] {myId});
			
			//second: delete all associated dailies
			getContentResolver().delete(DailyContentProvider.CONTENT_URI,
					DailyTable.COLUMN_SR_ID + " = ?", new String[] {myId});
			
			//last: delete the SR itself
			getContentResolver().delete(uri, null, null);
		}
	}
}