package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditDailyActivity extends Activity {
	
	private EditText date;
	private EditText startTime;
	private EditText endTime;
	private EditText travelTime;
	private EditText comment;
	private Button confirm;
	private String srId;
	private Uri savedUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_daily_activity);
		
		date = (EditText) findViewById(R.id.date);
		startTime = (EditText) findViewById(R.id.start_time);
		endTime = (EditText) findViewById(R.id.end_time);
		travelTime = (EditText) findViewById(R.id.travel_time);
		comment = (EditText) findViewById(R.id.comment);
		confirm = (Button) findViewById(R.id.daily_confirm);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(DailyTable.COLUMN_SR_ID);
		
		savedUri = (savedInstanceState == null) ? null : 
			(Uri) savedInstanceState.getParcelable(
					DailyContentProvider.CONTENT_ITEM_TYPE);
		
	    if (extras != null) {
	    	savedUri = extras
	    			.getParcelable(DailyContentProvider.CONTENT_ITEM_TYPE);
		
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
	
	private void fillData(Uri uri){
		String[] projection = { DailyTable.COLUMN_COMMENT,
				DailyTable.COLUMN_DATE, DailyTable.COLUMN_END_TIME,
				DailyTable.COLUMN_START_TIME, DailyTable.COLUMN_TRAVEL_TIME};
		Cursor cursor = getContentResolver()
				.query(uri, projection, null, null,null);
		if (cursor != null) {
			cursor.moveToFirst();
			
			date.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_DATE)));
		    startTime.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_START_TIME)));
		    endTime.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_END_TIME)));
		    travelTime.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_TRAVEL_TIME)));
		    comment.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_COMMENT)));

		    cursor.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(DailyContentProvider.CONTENT_ITEM_TYPE, savedUri);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}
	
	private void saveState() {
		
		String dayDate = date.getText().toString();
		String start = startTime.getText().toString();
		String end = endTime.getText().toString();
		String travel = travelTime.getText().toString();
		String dayComment = comment.getTag().toString();
		

		if (dayDate.length() == 0) {
			Toast.makeText(EditDailyActivity.this, "Date missing",
			        Toast.LENGTH_LONG).show();
			return;
		}

		ContentValues values = new ContentValues();
		values.put(DailyTable.COLUMN_DATE, dayDate);
		values.put(DailyTable.COLUMN_START_TIME, start);
		values.put(DailyTable.COLUMN_END_TIME, end);
		values.put(DailyTable.COLUMN_TRAVEL_TIME, travel);
		values.put(DailyTable.COLUMN_COMMENT, dayComment);
		values.put(DailyTable.COLUMN_SR_ID, srId);

	    if (savedUri == null) {
	      // New Daily
	      savedUri = getContentResolver()
	    		  .insert(DailyContentProvider.CONTENT_URI, values);
	    } else {
	      // Update Daily
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
}
