//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.models.Daily;

import java.util.Calendar;

/**
 * This class implements an activity to allow users to create
 * or edit logs of daily activity.  This information includes a
 * date, start and end times, travel time, and a comment section.
 *
 * @author Conner Kasten
 */
public class EditDailyActivity extends EditSubItemActivity {
    protected static final String TIME_PICKER_FRAGMENT_TAG = "TimePickerDialog";

    /**
     * The date for the log.
     */
    private DatePicker date;

    /**
     * The travel time.
     */
    private EditText travelTime;

    /**
     * The comment.
     */
    private EditText comment;

    /**
     * Displays the start time.
     */
    private TextView startTime;

    /**
     * Displays the end time.
     */
    private TextView endTime;

    private Daily day = new Daily();

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_daily_activity);

        date = (DatePicker) findViewById(R.id.date_picker);
        date.init(1, 1, 1, null);
        travelTime = (EditText) findViewById(R.id.travel_time);
        comment = (EditText) findViewById(R.id.comment);
        startTime = (TextView) findViewById(R.id.display_start_time);
        endTime = (TextView) findViewById(R.id.display_end_time);
        /* The confirm button to save the log. */
        Button confirm = (Button) findViewById(R.id.daily_confirm);
		/* Add a part associated with the same SR as the daily. */
        Button addPart = (Button) findViewById(R.id.add_part_from_daily);

        Bundle extras = getIntent().getExtras();

        savedUri = (savedInstanceState == null) ? null :
                (Uri) savedInstanceState.getParcelable(
                        SRContentProvider.DAILY_CONTENT_ITEM_TYPE);

        if (extras != null) {
            day.setSrId(extras.getString(DailyTable.COLUMN_SR_ID));

            savedUri = extras
                    .getParcelable(SRContentProvider.DAILY_CONTENT_ITEM_TYPE);

            if (savedUri != null)
                fillData(savedUri);
            else {
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
                setUpTimes();

                //make sure start is before end.
                if (day.startAndEndReversed()) {
                    Toast.makeText(EditDailyActivity.this,
                            "Start Time is after End Time!",
                            Toast.LENGTH_LONG).show();
                } else {
                    saveState();
                    setResult(RESULT_OK);
                    finish();
                }
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

    @Override
    protected Intent createIntentForParent() {
        Intent i = new Intent(this, ViewSRActivity.class);
        Uri todoUri = Uri.parse(SRContentProvider.SR_CONTENT_URI + "/" + day.getSrId());
        i.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, todoUri);
        return i;
    }

    /**
     * Convenience method to add a new part with the same SR ID as
     * The current Daily.
     */
    private void addPart() {
        Intent i = new Intent(this, EditPartActivity.class);
        i.putExtra(PartTable.COLUMN_SR_ID, day.getSrId());
        startActivity(i);
    }

    /**
     * Convenience method to generate properly formatted time string
     *
     * @param hour Hour of time to produce
     * @param min  Minute of time to produce
     * @return A String containing the passed time, formatted according
     * to system settings.
     */
    private String displayTime(int hour, int min) {
        String ampm = "";
        if (!DateFormat.is24HourFormat(this)) {
            if (hour > 12) {
                hour %= 12;
                ampm = "pm";
            } else ampm = "am";
        }

        if (min < 10)
            return hour + ":0" + min + ampm;
        else
            return hour + ":" + min + ampm;
    }

    /**
     * Fill data into the form from the database entry targeted by the Uri.
     *
     * @param uri the Uri to load data from
     */
    private void fillData(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, DailyTable.COLUMNS, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            day = Daily.fromCursor(cursor);

            date.updateDate(day.getYear(), day.getMonth()-1, day.getDay());
            travelTime.setText(day.getTravelTime());
            comment.setText(day.getComment());

            startTime.setText(displayTime(day.getStartHour(), day.getStartMin()));
            endTime.setText(displayTime(day.getEndHour(), day.getEndMin()));

            cursor.close();
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(SRContentProvider.DAILY_CONTENT_ITEM_TYPE,
                savedUri);
    }

    /**
     * Save the form state into the database.
     */
    private void saveState() {
        
        if(day == null) {
            day = new Daily();
        }

        day.setDay(date.getDayOfMonth());
        day.setMonth(date.getMonth()+1);
        day.setYear(date.getYear());
        day.setTravelTime(travelTime.getText().toString());
        day.setComment(comment.getText().toString());

        setUpTimes();

        if (savedUri == null) {
            // New Daily
            savedUri = getContentResolver()
                    .insert(SRContentProvider.DAILY_CONTENT_URI, day.toContentValues());
        } else {
            // Update Daily
            getContentResolver().update(savedUri, day.toContentValues(), null, null);
        }
    }

    private void setUpTimes() {
        final Calendar c = Calendar.getInstance();
        if (day.getStartHour() == -1) day.setStartHour(c.get(Calendar.HOUR_OF_DAY));
        if (day.getStartMin() == -1) day.setStartMin(c.get(Calendar.MINUTE));
        if (day.getEndHour() == -1) day.setEndHour(c.get(Calendar.HOUR_OF_DAY));
        if (day.getEndMin() == -1) day.setEndMin(c.get(Calendar.MINUTE));
    }

    /**
     * Sets the time. Used by the TimePickerFragment to let the activity
     * know what the user selects.
     *
     * @param hour   the hour
     * @param minute the minute
     * @param start  the start
     */
    protected void setTime(int hour, int minute, boolean start) {
        if (start) {
            day.setStartHour(hour);
            day.setStartMin(minute);
            startTime.setText(displayTime(day.getStartHour(), day.getStartMin()));
        } else {
            day.setEndHour(hour);
            day.setEndMin(minute);
            endTime.setText(displayTime(day.getEndHour(), day.getEndMin()));
        }
    }

    /**
     * Show start time picker dialog.
     *
     * @param v the caller view (I think?)
     */
    @SuppressWarnings("UnusedParameters")
    public void showStartTimePickerDialog(View v) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(
                true, day.getStartHour(), day.getStartMin());
        newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
    }

    /**
     * Show end time picker dialog.
     *
     * @param v the caller View (I think?)
     */
    @SuppressWarnings("UnusedParameters")
    public void showEndTimePickerDialog(View v) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(
                false, day.getEndHour(), day.getEndMin());
        newFragment.show(getFragmentManager(), TIME_PICKER_FRAGMENT_TAG);
    }

    /**
     * TimePickerFragment is used to create a new dialog fragment
     * in which the user may select a start or end time.
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static final String START_KEY = "TimePickerFragment.Start";
        public static final String HOUR_KEY = "TimePickerFragment.Hour";
        public static final String MINUTE_KEY = "TimePickerFragment.Minute";

        private boolean start;
        private int hour = -1;
        private int minute = -1;

        public static TimePickerFragment newInstance(boolean start, int hour, int minute) {
            TimePickerFragment tpf = new TimePickerFragment();
            Bundle args = new Bundle();
            args.putBoolean(START_KEY, start);
            args.putInt(HOUR_KEY, hour);
            args.putInt(MINUTE_KEY, minute);
            tpf.setArguments(args);
            return tpf;
        }

        /* (non-Javadoc)
         * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();

            start = args.getBoolean(START_KEY);
            hour = args.getInt(HOUR_KEY);
            minute = args.getInt(MINUTE_KEY);

            if (hour == -1 || minute == -1) {
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