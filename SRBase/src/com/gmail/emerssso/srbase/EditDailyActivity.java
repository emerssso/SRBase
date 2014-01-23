//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import java.util.Calendar;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * This class implements an activity to allow users to create 
 * or edit logs of daily activity.  This information includes a
 * date, start and end times, travel time, and a comment section.
 * @author Conner Kasten
 */
public class EditDailyActivity extends Activity {
	
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
	    		  .insert(DailyContentProvider.CONTENT_URI, values);
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
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.parent = this;
		newFragment.start = true;
		newFragment.hour = startHour;
		newFragment.minute = startMin;
		newFragment.show(getFragmentManager(), "startTimePicker");
	}
	
	/**
	 * Show end time picker dialog.
	 *
	 * @param v the caller View (I think?)
	 */
	public void showEndTimePickerDialog(View v) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.parent = this;
		newFragment.start = false;
		newFragment.hour = endHour;
		newFragment.minute = endMin;
		newFragment.show(getFragmentManager(), "endTimePicker");
	}
	
	/**
	 * TimePickerFragment is used to create a new dialog fragment
	 * in which the user may select a start or end time.
	 */
	public static class TimePickerFragment extends DialogFragment 
			implements TimePickerDialog.OnTimeSetListener {
		
		/** The parent. */
		public EditDailyActivity parent;
		
		/** The start. */
		public boolean start;
		
		/** The hour. */
		public int hour = -1;
		
		/** The minute. */
		public int minute = -1;
		
		/* (non-Javadoc)
		 * @see android.app.Fragment#setArguments(android.os.Bundle)
		 */
		public void setArguments(Bundle bundle) {
			this.parent = bundle.getParcelable("parent");
			this.start = bundle.getBoolean("start");
		}
		
		/* (non-Javadoc)
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
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
		
		/* (non-Javadoc)
		 * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker, int, int)
		 */
		public void onTimeSet(TimePicker view, int hour, int minute) {
			parent.setTime(hour, minute, start);
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
	 * The DeleteFragmentClass implements a dialog fragment
	 * to ask the user whether they are sure they want to delete 
	 * the Daily or not.
	 */
	public static class DeleteFragment extends DialogFragment {
		
		/* (non-Javadoc)
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle bundle) {
			final Activity activity = getActivity();
			return new AlertDialog.Builder(activity)
					.setTitle("Delete Daily?")
					.setMessage("Are you sure you want to delete this Log?")
					.setPositiveButton("Yes", 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, 
								int which) {
							//Some chicanery to get the SR deleted
							//There is probably a better way to do this
							if(activity instanceof EditDailyActivity) {
								((EditDailyActivity) activity).deleteDaily();
								activity.finish();
							}
							else {
								Log.w("SRBase:DeleteFragment", 
										"DeleteFragment called by non" +
										"ViewDailyActivity!");
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
	public void deleteDaily() {
		this.deleteDaily(savedUri);
	}
	
	/**
	 * This method deletes the passed Daily, and all parts and dailies
	 * associated with it.
	 * @param uri URI to the Daily to delete
	 */
	private void deleteDaily(Uri uri) {
		getContentResolver().delete(uri,
				null, null);
	}
}
