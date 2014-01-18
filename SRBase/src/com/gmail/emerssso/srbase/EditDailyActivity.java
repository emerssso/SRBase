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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditDailyActivity extends Activity {
	
	private DatePicker date;
	private TimePicker startTime;
	private TimePicker endTime;
	private EditText travelTime;
	private EditText comment;
	private Button confirm;
	private String srId;
	private Uri savedUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_daily_activity);
		
		date = (DatePicker) findViewById(R.id.date_picker);
		date.init(1,1,1,null);
		startTime = (TimePicker) findViewById(R.id.start_time_picker);
		endTime = (TimePicker) findViewById(R.id.end_time_picker);
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
	
	private void fillData(Uri uri){
		String[] projection = { DailyTable.COLUMN_COMMENT,
				DailyTable.COLUMN_DAY, DailyTable.COLUMN_END_HOUR,
				DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
				DailyTable.COLUMN_START_HOUR, DailyTable.COLUMN_TRAVEL_TIME,
				DailyTable.COLUMN_START_MIN, DailyTable.COLUMN_END_MIN};
		Cursor cursor = getContentResolver()
				.query(uri, projection, null, null,null);
		if (cursor != null) {
			cursor.moveToFirst();
			
			
			date.updateDate(cursor.getInt(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_YEAR)),
					cursor.getInt(cursor.getColumnIndexOrThrow
					(DailyTable.COLUMN_MONTH)), cursor.getInt(
					cursor.getColumnIndexOrThrow(DailyTable.COLUMN_DAY)));
			
		    startTime.setCurrentHour(cursor.getInt(cursor
		    		.getColumnIndexOrThrow(DailyTable.COLUMN_START_HOUR)));
		    startTime.setCurrentMinute(cursor.getInt(cursor
		    		.getColumnIndexOrThrow(DailyTable.COLUMN_START_MIN)));
		    
		    endTime.setCurrentHour(cursor.getInt(cursor
		    		.getColumnIndexOrThrow(DailyTable.COLUMN_END_HOUR)));
		    endTime.setCurrentMinute(cursor.getInt(cursor
		    		.getColumnIndexOrThrow(DailyTable.COLUMN_END_MIN)));
		    
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
		
		int day = date.getDayOfMonth();
		int month = date.getMonth();
		int year = date.getYear();
		int startHour = startTime.getCurrentHour();
		int startMin = startTime.getCurrentMinute();
		int endHour = endTime.getCurrentHour();
		int endMin = endTime.getCurrentMinute();
		String travel = travelTime.getText().toString();
		String dayComment = comment.getText().toString();

		ContentValues values = new ContentValues();
		values.put(DailyTable.COLUMN_DAY, day);
		values.put(DailyTable.COLUMN_MONTH, month);
		values.put(DailyTable.COLUMN_YEAR, year);
		values.put(DailyTable.COLUMN_START_HOUR, startHour);
		values.put(DailyTable.COLUMN_START_MIN, startMin);
		values.put(DailyTable.COLUMN_END_HOUR, endHour);
		values.put(DailyTable.COLUMN_END_MIN, endMin);
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
