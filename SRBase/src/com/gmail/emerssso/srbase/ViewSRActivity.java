package com.gmail.emerssso.srbase;

import java.util.ArrayList;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
	private EditText sr;
	private EditText customer;
	
	/** The start date. */
	private EditText startDate;
	
	/** The start time. */
	private EditText startTime;
	
	/** The end date. */
	private EditText endDate;
	
	/** The end time. */
	private EditText endTime;
	
	/** The total work time. */
	private EditText totalWorkTime;
	
	/** The total travel time. */
	private EditText totalTravelTime;
	
	/** The description. */
	private EditText description;
	private EditText modelNumber;
	private EditText serialNumber;
	
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
		
		sr = (EditText) findViewById(R.id.sr_number_view);
		customer = (EditText) findViewById(R.id.customer_view);
		startDate = (EditText) findViewById(R.id.start_date_header);
		startTime = (EditText) findViewById(R.id.start_time_header);
		endDate = (EditText) findViewById(R.id.end_date_header);
		endTime = (EditText) findViewById(R.id.end_time_header);
		totalWorkTime = (EditText) findViewById(R.id.total_work_time);
		totalTravelTime = (EditText) findViewById(R.id.total_travel_time);
		description = (EditText) findViewById(R.id.view_description);
		modelNumber = (EditText) findViewById(R.id.model_number_view);
		serialNumber = (EditText) findViewById(R.id.serial_number_view);
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
			srId = cursor.getString(cursor
					.getColumnIndexOrThrow(SRTable.COLUMN_SR_NUMBER));
			
			cursor.close();
			
			//gather rows of dailies for this SR
			String [] dailyProjection = {DailyTable.COLUMN_DAY,
					DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
					DailyTable.COLUMN_START_HOUR, DailyTable.COLUMN_END_HOUR,
					DailyTable.COLUMN_START_MIN, DailyTable.COLUMN_END_MIN,
					DailyTable.COLUMN_TRAVEL_TIME};
			cursor = getContentResolver().query(null, dailyProjection,
					DailyTable.COLUMN_SR_ID + " = ? ", 
					new String[] {srId}, null);
			
			//load daily data
			if(cursor != null) {
				ArrayList<String> dates = new ArrayList();
				double workTime = 0;
				double travelTime = 0;
				cursor.moveToFirst();
				
				//probably ought to move this to another thread...
				/*while(!cursor.isAfterLast()) {
					dates.add(cursor.getString(cursor
							.getColumnIndexOrThrow(DailyTable.COLUMN_DATE)));
					String start = cursor.getString
							(cursor.getColumnIndexOrThrow
							(DailyTable.COLUMN_START_TIME));
					String end = cursor.getString
							(cursor.getColumnIndexOrThrow
							(DailyTable.COLUMN_END_TIME));
					String travel = cursor.getString
							(cursor.getColumnIndexOrThrow
							(DailyTable.COLUMN_TRAVEL_TIME));
					travelTime += Double.valueOf(travel);
					//workTime += getDuration(start, end);
				}*/
				
				//set screen text
				totalWorkTime.setText(Double.toString(workTime)
						+ " hours");
				totalTravelTime.setText(Double.toString(travelTime)
						+ " hours");
				//startDate.setText(getFirstDate(dates));
				//endDate.setText(getLastDate(dates));
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
	
	//TODO: figure out if this is even necessary
	private double getDuration(String start, String end) {
		String[] startParts = start.split(":");
		String[] endParts = start.split(":");
		
		return 0;
	}

}
