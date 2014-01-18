package com.gmail.emerssso.srbase;

import java.util.Calendar;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class EditDailyActivity extends Activity {
	
	private DatePicker date;
	private EditText travelTime;
	private EditText comment;
	private Button confirm;
	private String srId;
	private Uri savedUri;
	private int startHour;
	private int startMin;
	private int endHour;
	private int endMin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_daily_activity);
		
		startHour = startMin = endHour = endMin = -1;
		
		date = (DatePicker) findViewById(R.id.date_picker);
		date.init(1,1,1,null);
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
	    	else
		    {
	    		final Calendar c = Calendar.getInstance();
				date.updateDate(c.get(Calendar.YEAR), 
						c.get(Calendar.MONTH), 
						c.get(Calendar.DAY_OF_MONTH));
		    }
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
			startHour = cursor.getInt(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_START_HOUR));
			startMin = cursor.getInt(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_START_MIN));
			endHour = cursor.getInt(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_END_HOUR));
			endMin = cursor.getInt(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_END_MIN));
		    
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
		String travel = travelTime.getText().toString();
		String dayComment = comment.getText().toString();
		
		final Calendar c = Calendar.getInstance();
		if(startHour == -1) startHour = c.get(Calendar.HOUR_OF_DAY);
		if(startMin == -1) startMin = c.get(Calendar.MINUTE);
		if(endMin == -1) endMin = c.get(Calendar.MINUTE);
		if(endHour == -1) endHour = c.get(Calendar.HOUR_OF_DAY);

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
	
	protected void setTime(int hour, int minute, boolean start) {
		if(start) {
			startHour = hour;
			startMin = minute;
			//startTime.setText((hour % 12) + ":" + minute);
		}
		else {
			endHour = hour;
			endMin = minute;
			//endTime.setText((hour % 12) + ":" + minute);
		}
	}
	
	public void showStartTimePickerDialog(View v) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.parent = this;
		newFragment.start = true;
		newFragment.hour = startHour;
		newFragment.minute = startMin;
		newFragment.show(getFragmentManager(), "startTimePicker");
	}
	
	public void showEndTimePickerDialog(View v) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.parent = this;
		newFragment.start = false;
		newFragment.hour = endHour;
		newFragment.minute = endMin;
		newFragment.show(getFragmentManager(), "endTimePicker");
	}
	
	public static class TimePickerFragment extends DialogFragment 
			implements TimePickerDialog.OnTimeSetListener {
		public EditDailyActivity parent;
		public boolean start;
		public int hour = -1;
		public int minute = -1;
		
		public void setArguments(Bundle bundle) {
			this.parent = bundle.getParcelable("parent");
			this.start = bundle.getBoolean("start");
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			if(hour == -1 || minute == -1) {
				final Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
				minute = c.get(Calendar.MINUTE);
			}
			
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hour, int minute) {
			parent.setTime(hour, minute, start);
		}
	}
}
