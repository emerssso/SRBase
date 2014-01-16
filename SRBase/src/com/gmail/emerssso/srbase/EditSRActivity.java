/*
 * TODO: Figure out Copyright stuff
 */

package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//TODO: Add methods for calling EditPart and EditDaily
//and make sure to pass SR ID in Intent extras
// TODO: Auto-generated Javadoc
//TODO: Write Some more in the Javadoc header
/**
 * This activity manages the creation and modification
 * of SR elements in the database.
 *
 * @author Conner Kasten
 */
public class EditSRActivity extends Activity {
	
	/** The SR number. */
	private EditText mSRNumber;
	
	/** The customer. */
	private EditText mCustomer;
	
	/** The model number. */
	private EditText mModelNumber;
	
	/** The serial number. */
	private EditText mSerialNumber;
	
	/** The description. */
	private EditText mDescription;
	
	/** The Daily Button. */
	private Button mDaily;
	
	/** The Part Button. */
	private Button mPart;
	
	/** The Confirm Button. */
	private Button mEnter;
	
	/** The saved URI. */
	private Uri savedUri;
	
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
				setResult(RESULT_OK);
				finish();
			}
		});
		
		mPart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addPart();
			}
		});
		
		mDaily.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addDaily();
			}
		});
	}
	
	private void addPart() {
		saveState();
		Intent i = new Intent(this, EditPartActivity.class);
		i.putExtra(PartTable.COLUMN_SR_ID, myId);
		startActivity(i);
	}
	
	private void addDaily() {
		saveState();
		Intent i = new Intent(this, EditDailyActivity.class);
		i.putExtra(DailyTable.COLUMN_SR_ID, myId);
		startActivity(i);
	}
	
	/**
	 * Fill data.
	 *
	 * @param uri the uri
	 */
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
		    myId = Integer.toString(cursor.getInt(0));

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
	 * Save state.
	 */
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
  
	    	Cursor cursor = getContentResolver().query(savedUri, 
	    			new String[] {SRTable.COLUMN_ID}, 
	    			SRTable.COLUMN_SR_NUMBER + " = ?", 
	    			new String[] {srNumber}, null);
	    	if(cursor != null) {
	    		cursor.moveToFirst();
	    		myId = Integer.toString(cursor.getInt(0));
    		}
	    } 
	    else {
	      // Update SR
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
}
