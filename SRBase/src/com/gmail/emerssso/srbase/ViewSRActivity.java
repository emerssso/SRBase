package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.app.Activity;
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
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * This class implements an Android activity to view SR summaries.
 * All of the key details for an SR are presented.  The class also has
 * to do some computation for some information (i.e total work and 
 * travel time).
 * 
 * @author Conner Kasten
 */
public class ViewSRActivity extends Activity {
	
	/** The SR. */
	private TextView sr;
	private TextView customer;
	
	/** The start date. */
	private TextView startDate;
	
	/** The start time. */
	private TextView startTime;
	
	/** The end date. */
	private TextView endDate;
	
	/** The end time. */
	private TextView endTime;
	
	/** The total work time. */
	private TextView totalWorkTime;
	
	/** The total travel time. */
	private TextView totalTravelTime;
	
	/** The description. */
	private TextView description;
	private TextView modelNumber;
	private TextView serialNumber;
	
	/** The parts list button. */
	private Button partsListButton;
	
	/** The comments list button. */
	private Button commentsListButton;
	
	private Uri srUri;
	private String srId;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.view_sr_activity);
		
		sr = (TextView) findViewById(R.id.sr_number_view);
		customer = (TextView) findViewById(R.id.customer_view);
		startDate = (TextView) findViewById(R.id.start_date_header);
		startTime = (TextView) findViewById(R.id.start_time_header);
		endDate = (TextView) findViewById(R.id.end_date_header);
		endTime = (TextView) findViewById(R.id.end_time_header);
		totalWorkTime = (TextView) findViewById(R.id.total_work_time);
		totalTravelTime = (TextView) findViewById(R.id.total_travel_time);
		description = (TextView) findViewById(R.id.view_description);
		modelNumber = (TextView) findViewById(R.id.model_number_view);
		serialNumber = (TextView) findViewById(R.id.serial_number_view);
		partsListButton = (Button) findViewById(R.id.view_parts_list);
		commentsListButton = (Button) findViewById(R.id.view_comments_list);
		
		Bundle extras = getIntent().getExtras();
		
		srUri = (bundle == null) ? null : 
			(Uri) bundle
	        .getParcelable(SRContentProvider.CONTENT_ITEM_TYPE);
		
	    if (extras != null) {
	    	srUri = extras
	    			.getParcelable(SRContentProvider.CONTENT_ITEM_TYPE);
	    	fillData(srUri);
    	}
	    
	    commentsListButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(),
						ListCommentsActivity.class);
			  	i.putExtra(DailyTable.COLUMN_SR_ID, srId);
			  	startActivity(i);
			}
		});
	}
	
	/**
	 * This method fills in the Activities fields with the 
	 * appropriate method gathered from the database row 
	 * at URI uri.
	 * @param uri URI of the targetted database row.
	 */
	private void fillData(Uri uri) {
		String[] projection = { SRTable.COLUMN_CUSTOMER_NAME,
				SRTable.COLUMN_DESCRIPTION, SRTable.COLUMN_ID,
				SRTable.COLUMN_MODEL_NUMBER, SRTable.COLUMN_SERIAL_NUMBER,
				SRTable.COLUMN_SR_NUMBER };
		
		Cursor cursor = getContentResolver().query(uri, projection, null, 
				null, null);
		if(cursor != null) {
			cursor.moveToFirst();
			
			//load data garnered from SR table
			sr.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_SR_NUMBER)));
			customer.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_CUSTOMER_NAME)));
			modelNumber.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_MODEL_NUMBER)));
			serialNumber.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_SERIAL_NUMBER)));
			description.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_DESCRIPTION)));
			srId = Integer.toString(cursor.getInt(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_ID)));
			
			cursor.close();
			
			//gather rows of dailies for this SR
			Log.w("SRBase:ViewSR", "querying for dailies matching SR_ID"
					+ srId);
			String [] dailyProjection = {DailyTable.COLUMN_DAY,
					DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
					DailyTable.COLUMN_START_HOUR, DailyTable.COLUMN_END_HOUR,
					DailyTable.COLUMN_START_MIN, DailyTable.COLUMN_END_MIN,
					DailyTable.COLUMN_TRAVEL_TIME};
			cursor = getContentResolver().query(
					DailyContentProvider.CONTENT_URI, dailyProjection,
					DailyTable.COLUMN_SR_ID + " = ? ", 
					new String[] {srId}, null);
			
			//load daily data
			if(cursor != null) {
				Log.w("SRBase:ViewSR", "cursor has " + cursor.getCount() 
						+ " rows");
				double workTime = 0;
				double travelTime = 0;
				int startDay = Integer.MAX_VALUE;
				int startMonth = Integer.MAX_VALUE;
				int startYear = Integer.MAX_VALUE;
				int endDay = Integer.MIN_VALUE;
				int endMonth = Integer.MIN_VALUE;
				int endYear = Integer.MIN_VALUE;
				int firstStartHour = Integer.MAX_VALUE;
				int firstStartMin = Integer.MAX_VALUE;
				int lastEndHour = Integer.MIN_VALUE;
				int lastEndMin = Integer.MIN_VALUE;
				cursor.moveToFirst();
				
				//probably ought to move this to another thread...
				while(!cursor.isAfterLast()) {
					Log.w("SRBase:ViewSR", "Iteration of while Loop");
					//add in travel time
					String travelString = cursor.getString(cursor
							.getColumnIndexOrThrow(DailyTable
							.COLUMN_TRAVEL_TIME));
					
					double travel;
					if(travelString.length() > 0)
						travel = Double.valueOf(travelString);
					else
						travel =  0;
					travelTime += travel;
					int day = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable.COLUMN_DAY));
					int month = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable.COLUMN_MONTH));
					int year = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable.COLUMN_YEAR));
					int startHour = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable
							.COLUMN_START_HOUR));
					int startMin = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable
							.COLUMN_START_MIN));
					int endHour = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable
							.COLUMN_END_HOUR));
					int endMin = cursor.getInt(cursor
							.getColumnIndexOrThrow(DailyTable
							.COLUMN_END_MIN));
					workTime += (endHour - startHour) + 
							(endMin - startMin)/60.0;
					
					//find if this is a boundary day
					if(year < startYear) {
						startYear = year;
						startMonth = month;
						startDay = day;
						firstStartHour = startHour;
						firstStartMin = startMin;
					}
					else if(year == startYear) {
						if(month < startMonth) {
							startMonth = month;
							startDay = day;
							firstStartHour = startHour;
							firstStartMin = startMin;
						}
						else if(month == startMonth) {
							if(day < startDay) {
								startDay = day;
								firstStartHour = startHour;
								firstStartMin = startMin;
							}
						}
					}
					
					if(year > endYear) {
						endYear = year;
						endMonth = month;
						endDay = day;
						lastEndHour = endHour;
						lastEndMin = endMin;
					}
					else if(year == startYear) {
						if(month > endMonth) {
							endMonth = month;
							endDay = day;
							lastEndHour = endHour;
							lastEndMin = endMin;
						}
						else if(day > endDay) {
							endDay = day;
							lastEndHour = endHour;
							lastEndMin = endMin;
						}
					}
					cursor.moveToNext();
				}
				
				//set screen text
				startDate.setText(startMonth + "/" + startDay 
						+ "/" + startYear);
				endDate.setText(endMonth + "/" + endDay 
						+ "/" + endYear);
				
				String startampm = "";
				String endampm = "";
				if(!DateFormat.is24HourFormat(this)) {
					if(firstStartHour > 12) { 
						firstStartHour = firstStartHour % 12;
						startampm = "pm";
					}
					else startampm = "am";
					if(lastEndHour > 12) {
						lastEndHour = lastEndHour % 12;
						endampm = "pm";
					}
					else endampm = "am";
				}
				
				if(firstStartMin < 10)
					startTime.setText(firstStartHour + ":0" 
							+ firstStartMin + startampm);
				else
					startTime.setText(firstStartHour + ":" 
							+ firstStartMin + startampm);
				
				if(lastEndMin < 10)
					endTime.setText(lastEndHour + ":0"
							+ lastEndMin + endampm);
				else
					endTime.setText(lastEndHour + ":"
							+ lastEndMin + endampm);
					
				
				
				totalWorkTime.setText(truncate(Double.toString(workTime))
						+ " hours");
				totalTravelTime.setText(Double.toString(travelTime)
						+ " hours");
				cursor.close();
			}
			else { //if no dailies found, update screen with warning
				startDate.setText("No Dates Logged");
				startTime.setText("");
				endDate.setText("");
				endTime.setText("");
				totalWorkTime.setText("0 hours");
				totalTravelTime.setText("0 hours");
			}
		}
	}
	
	private String truncate(String num) {
		if(num.contains(".")) {
			 return num.substring(0, num.indexOf('.') + 3);
		}
		else return num;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_sr_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.edit_sr:
			Intent i = new Intent(this, EditSRActivity.class);
			i.putExtra(SRContentProvider.CONTENT_ITEM_TYPE, srUri);
		    startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
