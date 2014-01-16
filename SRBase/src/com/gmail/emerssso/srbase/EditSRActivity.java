/*
 * TODO: Figure out Copyright stuff
 */

package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//TODO: Write Some more in the Javadoc header
/**
 * This activity manages the creation and modification
 * of SR elements in the database.
 *
 * @author Conner Kasten
 */
public class EditSRActivity extends Activity {
	EditText mSRNumber;
	EditText mCustomer;
	EditText mModelNumber;
	EditText mSerialNumber;
	EditText mDescription;
	Button mDaily;
	Button mPart;
	Button mEnter;
	
	Uri savedUri;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	private void fillData(Uri uri) {
		String[] projection = { SRTable.COLUMN_CUSTOMER_NAME,
				SRTable.COLUMN_DESCRIPTION, SRTable.COLUMN_MODEL_NUMBER,
				SRTable.COLUMN_SERIAL_NUMBER, SRTable.COLUMN_SR_NUMBER};
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

		    cursor.close();
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(SRContentProvider.CONTENT_ITEM_TYPE, savedUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}
	
	private void saveState() {
		
		String srNumber = mSRNumber.getText().toString();
		String customer = mCustomer.getText().toString();
		String modelNumber = mModelNumber.getText().toString();
		String serialNumber = mSerialNumber.getText().toString();
		String description = mDescription.getText().toString();

		if (srNumber.length() == 0) {
			Toast.makeText(EditSRActivity.this, "SR Number missing",
			        Toast.LENGTH_LONG).show();
			return;
		}

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
	    } else {
	      // Update SR
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }

}
