//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import java.util.Calendar;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * This class implements an activity to allow users to create 
 * or edit logs of daily activity.  This information includes a
 * date, start and end times, travel time, and a comment section.
 * @author Conner Kasten
 */
public class EditDailyActivity extends DeletableActivity {
	private static final String TIME_PICKER_FRAGMENT_TAG = "TimePickerDialog";

	/** The date for the log. */
	private DatePicker date;
	
	/** The travel time. */
	private EditText travelTime;
	
	/** The comment. */
	private EditText comment;
	
	/** Displays the start time. */
	private TextView startTime;
	
	/** Displays the end time. */
	private TextView endTime;
	
	/** The confirm button to save the log. */
	private Button confirm;
	
	/** Add a part associated with the same SR as the daily. */
	private Button addPart;
	
	/** The SR ID associated with the day. */
	private String srId;
	
	/** The saved Uri when loading an old Daily. */
	private Uri savedUri;
	
	/** The start hour. */
	private int startHour;
	
	/** The start minute. */
	private int startMin;
	
	/** The end hour. */
	private int endHour;
	
	/** The end minute. */
	private int endMin;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_daily_activity);
		
		//these are set to -1 initially so we know they
		//need to be set later if the user wants to use
		//the current time as the start or end, without
		//having to open the dialog.
		startHour = startMin = endHour = endMin = -1;
		
		date = (DatePicker) findViewById(R.id.date_picker);
		date.init(1,1,1,null);
		travelTime = (EditText) findViewById(R.id.travel_time);
		comment = (EditText) findViewById(R.id.comment);
		startTime = (TextView) findViewById(R.id.display_start_time);
		endTime = (TextView) findViewById(R.id.display_end_time);
		confirm = (Button) findViewById(R.id.daily_confirm);
		addPart = (Button) findViewById(R.id.add_part_from_daily);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(DailyTable.COLUMN_SR_ID);
		
		savedUri = (savedInstanceState == null) ? null : 
			(Uri) savedInstanceState.getParcelable(
					SRContentProvider.DAILY_CONTENT_ITEM_TYPE);
    	//any time savedUri is set, we also want to set
    	//super.savedUri, so that we delete the right thing.
		super.savedUri = savedUri;
		
	    if (extras != null) {
	    	savedUri = extras
	    			.getParcelable(SRContentProvider.DAILY_CONTENT_ITEM_TYPE);
	    	super.savedUri = savedUri;
	    	
	    	if(savedUri != null)
	    		fillData(savedUri);
	    	else
		    {
	    		final Calendar c = Calendar.getInstance();
				date.updateDate(c.get(Calendar.YEAR), 
						c.get(Calendar.MONTH), 
						c.get(Calendar.DAY_OF_MONTH));
				startTime.setText(displayTime(c.get(Calendar.HOUR_OF_DAY),
						c.get(Calendar.MINUTE)));
				endTime.setText(displayTime(c.get(Calendar.HOUR_OF_DAY),
						c.get(Calendar.MINUTE)));
		    }
    	}
	    
	    confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//set hours/minutes for current if not set
				final Calendar c = Calendar.getInstance();
				if(startHour == -1) startHour = c.get(Calendar.HOUR_OF_DAY);
				if(startMin == -1) startMin = c.get(Calendar.MINUTE);
				if(endMin == -1) endMin = c.get(Calendar.MINUTE);
				if(endHour == -1) endHour = c.get(Calendar.HOUR_OF_DAY);
				
				//make sure start is before end.
				if(startHour > endHour || 
						(startHour == endHour && startMin > endMin)) {
					Toast.makeText(EditDailyActivity.this, 
							"Start Time is after End Time!",
					        Toast.LENGTH_LONG).show();
					return;
				}
				saveState();
				setResult(RESULT_OK);
				finish();
			}
		});
	    
	    addPart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveState();
				addPart();
			}
		});
	}
	
	/**
	 * Convenience method to add a new part with the same SR ID as
	 * The current Daily.
	 */
	private void addPart() {
		Intent i = new Intent(this, EditPartActivity.class);
		i.putExtra(PartTable.COLUMN_SR_ID, srId);
		startActivity(i);
	}
	
	/**
	 * Convenience method to generate properly formated time string
	 * @param hour Hour of time to produce
	 * @param min Minute of time to produce
	 * @return A String containing the passed time, formatted according
	 * to system settings.
	 */
	private String displayTime(int hour, int min) {
		String ampm = "";
		if(!DateFormat.is24HourFormat(this)) {
			if(hour > 12) { 
				hour = hour % 12;
				ampm = "pm";
			}
			else ampm = "am";
		}
		

		if(min < 10)
			return hour + ":0" + min + ampm;
		else
			return hour + ":" + min + ampm;
		
	}
	
	/**
	 * Fill data into the form from the database entry targeted by the Uri.
	 *
	 * @param uri the Uri to load data from
	 */
	private void fillData(Uri uri){
		String[] projection = { DailyTable.COLUMN_COMMENT,
				DailyTable.COLUMN_DAY, DailyTable.COLUMN_END_HOUR,
				DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
				DailyTable.COLUMN_START_HOUR, DailyTable.COLUMN_TRAVEL_TIME,
				DailyTable.COLUMN_START_MIN, DailyTable.COLUMN_END_MIN,
				DailyTable.COLUMN_SR_ID};
		Cursor cursor = getContentResolver()
				.query(uri, projection, null, null,null);
		if (cursor != null) {
			cursor.moveToFirst();
			
			date.updateDate(cursor.getInt(cursor
					.getColumnIndexOrThrow(DailyTable.COLUMN_YEAR)),
					cursor.getInt(cursor.getColumnIndexOrThrow
					(DailyTable.COLUMN_MONTH))-1, cursor.getInt(
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
		    srId = cursor.getString(cursor
		    		.getColumnIndexOrThrow(DailyTable.COLUMN_SR_ID));
		    
		    startTime.setText(displayTime(startHour, startMin));
		    endTime.setText(displayTime(endHour, endMin));

		    cursor.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(SRContentProvider.DAILY_CONTENT_ITEM_TYPE, 
				savedUri);
	}
	
	/**
	 * Save the form state into the database.
	 */
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
		values.put(DailyTable.COLUMN_MONTH, month+1);
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
	    		  .insert(SRContentProvider.DAILY_CONTENT_URI, values);
	      super.savedUri = savedUri;
	    } else {
	      // Update Daily
	      getContentResolver().update(savedUri, values, null, null);
	    }
    }
	
	/**
	 * Sets the time. Used by the TimePickerFragment to let the activity
	 * know what the user selects.
	 *
	 * @param hour the hour
	 * @param minute the minute
	 * @param start the start
	 */
	protected void setTime(int hour, int minute, boolean start) {
		if(start) {
			startHour = hour;
			startMin = minute;
			startTime.setText(displayTime(startHour, startMin));
		}
		else {
			endHour = hour;
			endMin = minute;
			endTime.setText(displayTime(endHour, endMin));
		}
	}
	
	/**
	 * Show start time picker dialog.
	 *
	 * @param v the caller view (I think?)
	 */
	public void showStartTimePickerDialog(View v) {
		TimePickerFragment newFragment = TimePickerFragment.newInstance(true, startHour, startMin);
		newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
	}
	
	/**
	 * Show end time picker dialog.
	 *
	 * @param v the caller View (I think?)
	 */
	public void showEndTimePickerDialog(View v) {
		TimePickerFragment newFragment = TimePickerFragment.newInstance(false, endHour, endMin);
		newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
	}
	
	/**
	 * TimePickerFragment is used to create a new dialog fragment
	 * in which the user may select a start or end time.
	 */
	 static class TimePickerFragment extends DialogFragment 
			implements TimePickerDialog.OnTimeSetListener {
		 
		 public static final String START_KEY = "TimePickerFragment.Start";
		 public static final String HOUR_KEY = "TimePickerFragment.Hour";
		 public static final String MINUTE_KEY = "TimePickerFragment.Minute";
		
		public static TimePickerFragment newInstance(boolean start, int hour, int minute) {
			TimePickerFragment tpf = new TimePickerFragment();
			Bundle args = new Bundle();
			args.putBoolean(START_KEY, start);
			args.putInt(HOUR_KEY, hour);
			args.putInt(MINUTE_KEY, minute);
			tpf.setArguments(args);
			return tpf;
		}
		
		/** The start. */
		private boolean start;
		
		/** The hour. */
		private int hour = -1;
		
		/** The minute. */
		private int minute = -1;
				
		/* (non-Javadoc)
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();
			
			start = args.getBoolean(START_KEY);
			hour = args.getInt(HOUR_KEY);
			minute = args.getInt(MINUTE_KEY);
			
			if(hour == -1 || minute == -1) {
				final Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
				minute = c.get(Calendar.MINUTE);
			}
			
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
		
		/* (non-Javadoc)
		 * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker, int, int)
		 */
		public void onTimeSet(TimePicker view, int hour, int minute) {
			((EditDailyActivity) getActivity()).setTime(hour, minute, start);
		}
	}
}