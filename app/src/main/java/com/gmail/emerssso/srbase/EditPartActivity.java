//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.PartTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The EditPartActivity class implements an activity which provides
 * a form for users to enter new parts to be associated with a particular
 * SR, and certain information associated with that part.
 */
public class EditPartActivity extends DeletableActivity {
	
	/** The part number. */
	private EditText partNumber;
	
	/** The number of parts in question. */
	private EditText partQuantity;
	
	/** The part source (i.e. work, home, Japan). */
	private EditText partSource;
	
	/** The part description. */
	private EditText partDescription;
	
	/** Indicates whether the part was used or not. */
	private CheckBox partUsed;
	
	/** The Uri to load saved data from. */
	private Uri savedUri;

    /** The ID of the associated SR. */
	private String srId;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.edit_part_activity);
		
		partNumber = (EditText) findViewById(R.id.part_number);
		partQuantity = (EditText) findViewById(R.id.part_quantity);
		partSource = (EditText) findViewById(R.id.part_source);
		partDescription = (EditText) findViewById(R.id.part_description);
		partUsed = (CheckBox) findViewById(R.id.part_used);
		/* The confirm button to save data. */
        Button confirm = (Button) findViewById(R.id.part_confirm);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(PartTable.COLUMN_SR_ID);
		
		savedUri = (bundle == null) ? null : 
			(Uri) bundle.getParcelable(
					SRContentProvider.PART_CONTENT_ITEM_TYPE);
		super.savedUri = savedUri;
		
	    if (extras != null) {
	    	savedUri = extras
	    			.getParcelable(SRContentProvider.PART_CONTENT_ITEM_TYPE);
	    	super.savedUri = savedUri;
	    	if(savedUri != null)
	    		fillData(savedUri);
    	}
	    
	    confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (partNumber.getText().toString().length() == 0) {
                    Toast.makeText(EditPartActivity.this, "Part Number missing",
                            Toast.LENGTH_LONG).show();
                } else {
                    saveState();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
	}
	
	/**
	 * Fill data from the database entry at Uri into the form.
	 *
	 * @param uri the Source of data to be loaded.
	 */
	private void fillData(Uri uri) {
		String[] projection = { PartTable.COLUMN_DESCRIPTION,
				PartTable.COLUMN_PART_NUMBER, PartTable.COLUMN_QUANTITY,
				PartTable.COLUMN_SOURCE, PartTable.COLUMN_USED,
				PartTable.COLUMN_SR_ID};
		Cursor cursor = getContentResolver()
				.query(uri, projection, null, null,null);
		if (cursor != null) {
			cursor.moveToFirst();
		    
		    partNumber.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_PART_NUMBER)));
		    
		    String temp = cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_QUANTITY));
		    
		    if(!temp.equals("Unknown"))
		    	partQuantity.setText(temp);
		    	
		    temp = cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_SOURCE));
		    if(!temp.equals("Unknown"))
		    	partSource.setText(temp);
		    
		    temp = cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_DESCRIPTION));
		    
		    if(!temp.equals("No Description")) 
		    	partDescription.setText(temp);
		    
		    srId = cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_SR_ID));
		    
		    if(cursor.getString(cursor.getColumnIndexOrThrow
		    		(PartTable.COLUMN_USED)).equals("Used"))
		    	partUsed.setChecked(true);
		    else
		    	partUsed.setChecked(false);

		    cursor.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(
				SRContentProvider.PART_CONTENT_ITEM_TYPE, savedUri);
	}
	
	/**
	 * Save the state of the form into the database.
	 */
	private void saveState() {
		
		String number = partNumber.getText().toString();
		
		if(partNumber.length() == 0)
			return;
		
		String quantity = partQuantity.getText().toString();
		String description = partDescription.getText().toString();
		String source = partSource.getText().toString();
		String used = partUsed.isChecked() ? "Used" : "Unused";
		
		if(quantity.length() == 0) quantity = "Unknown";
		if(description.length() == 0) description = "No Description";
		if(source.length() == 0) source = "Unknown";

		ContentValues values = new ContentValues();
		values.put(PartTable.COLUMN_PART_NUMBER, number);
		values.put(PartTable.COLUMN_QUANTITY, quantity);
		values.put(PartTable.COLUMN_DESCRIPTION, description);
		values.put(PartTable.COLUMN_SOURCE, source);
		values.put(PartTable.COLUMN_USED, used);
		values.put(PartTable.COLUMN_SR_ID, srId);
		

	    if (savedUri == null) {
	      // New Part
	      savedUri = getContentResolver()
	    		  .insert(SRContentProvider.PART_CONTENT_URI, values);
	      super.savedUri = savedUri;
	    } else {
	      // Update Part
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
}