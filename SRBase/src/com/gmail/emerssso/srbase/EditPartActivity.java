//TODO: Copyright
package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.PartContentProvider;
import com.gmail.emerssso.srbase.database.PartTable;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class EditPartActivity.
 */
public class EditPartActivity extends Activity {
	
	/** The part number. */
	private EditText partNumber;
	
	/** The part quantity. */
	private EditText partQuantity;
	
	/** The part source. */
	private EditText partSource;
	
	/** The part description. */
	private EditText partDescription;
	
	/** The part used. */
	private CheckBox partUsed;
	
	/** The saved uri. */
	private Uri savedUri;
	
	/** The confirm. */
	private Button confirm;
	
	/** The sr id. */
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
		confirm = (Button) findViewById(R.id.part_confirm);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(PartTable.COLUMN_SR_ID);
		
		savedUri = (bundle == null) ? null : 
			(Uri) bundle.getParcelable(
					PartContentProvider.CONTENT_ITEM_TYPE);
		
	    if (extras != null) {
	    	savedUri = extras
	    			.getParcelable(PartContentProvider.CONTENT_ITEM_TYPE);
	    	if(savedUri != null)
	    		fillData(savedUri);
    	}
	    
	    confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	/**
	 * Fill data.
	 *
	 * @param uri the uri
	 */
	private void fillData(Uri uri) {
		String[] projection = { "*" };
		Cursor cursor = getContentResolver()
				.query(uri, projection, null, null,null);
		if (cursor != null) {
			cursor.moveToFirst();
		    
		    partNumber.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_PART_NUMBER)));
		    partQuantity.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_QUANTITY)));
		    partSource.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_SOURCE)));
		    partDescription.setText(cursor.getString(cursor
		    		.getColumnIndexOrThrow(PartTable.COLUMN_DESCRIPTION)));
		    
		    if(cursor.getString(cursor.getColumnIndexOrThrow
		    		(PartTable.COLUMN_USED)).equals("yes"))
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
				PartContentProvider.CONTENT_ITEM_TYPE, savedUri);
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
		
		String number = partNumber.getText().toString();
		String quantity = partQuantity.getText().toString();
		String description = partDescription.getText().toString();
		String source = partSource.getText().toString();
		String used = partUsed.isChecked() ? "yes" : "no";
		

		if (srId.length() == 0) {
			Toast.makeText(EditPartActivity.this, "Part Number missing",
			        Toast.LENGTH_LONG).show();
			return;
		}

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
	    		  .insert(PartContentProvider.CONTENT_URI, values);
	    } else {
	      // Update Part
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
}
