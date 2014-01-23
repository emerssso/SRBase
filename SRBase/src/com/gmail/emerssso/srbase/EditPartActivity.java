//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.EditDailyActivity.DeleteFragment;
import com.gmail.emerssso.srbase.database.PartContentProvider;
import com.gmail.emerssso.srbase.database.PartTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class EditPartActivity extends Activity {
	
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
	
	/** The confirm button to save data. */
	private Button confirm;
	
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
				
				if (partNumber.getText().toString().length() == 0) {
					Toast.makeText(EditPartActivity.this, "Part Number missing",
					        Toast.LENGTH_LONG).show();
					return;
				}
				else {
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
	    		  .insert(PartContentProvider.CONTENT_URI, values);
	    } else {
	      // Update Part
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.delete_item:
			DeleteFragment dfrag = new DeleteFragment();
			dfrag.show(getFragmentManager(), "Delete Fragment");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * The DeleteFragment implements a dialog fragment
	 * to ask the user whether they are sure they want to delete 
	 * the Part or not.
	 */
	public static class DeleteFragment extends DialogFragment {
		
		/* (non-Javadoc)
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle bundle) {
			final Activity activity = getActivity();
			return new AlertDialog.Builder(activity)
					.setTitle("Delete Part?")
					.setMessage("Are you sure you want to delete this Part?")
					.setPositiveButton("Yes", 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, 
								int which) {
							//Some chicanery to get the SR deleted
							//There is probably a better way to do this
							if(activity instanceof EditPartActivity) {
								((EditPartActivity) activity).deletePart();
								activity.finish();
							}
							else {
								Log.w("SRBase:DeleteFragment", 
										"DeleteFragment called by non" +
										"ViewPartActivity!");
								dialog.cancel();
							}
						}
					})
					.setNegativeButton("No", 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, 
								int which) {
							dialog.cancel();
						}
					}).create();
		}
		
	}
	
	/**
	 * Convenience method to request Daily deletion.
	 */
	public void deletePart() {
		this.deletePart(savedUri);
	}
	
	/**
	 * This method deletes the passed Daily, and all parts and dailies
	 * associated with it.
	 * @param uri URI to the Daily to delete
	 */
	private void deletePart(Uri uri) {
		getContentResolver().delete(uri,
				null, null);
	}
}
