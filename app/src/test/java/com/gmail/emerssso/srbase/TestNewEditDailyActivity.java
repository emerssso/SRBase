package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.models.SR;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestNewEditDailyActivity {

    private ShadowContentResolver resolver;
    private SR srOne;
    private Uri srUri;
    private ActivityController<EditDailyActivity> controller;
    private EditDailyActivity activity;
    private DatePicker date;
    private TextView startTime;
    private TextView endTime;
    private EditText travelTime;
    private EditText comment;
    private Calendar now;

    @Before
    public void setupActivity() {
        resolver = Robolectric.shadowOf(Robolectric.application.getContentResolver());
        SRContentProvider provider = new SRContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider("content://" +
                SRContentProvider.AUTHORITY, provider);

        srOne = new SR();
        srOne.setNumber("number");
        srOne.setCustomerName("Conner");
        srOne.setBusinessName("Zonar");
        srOne.setModelNumber("2020");
        srOne.setSerialNumber("2000");
        srOne.setDescription("dirty");

        srUri = resolver.insert(SRContentProvider.SR_CONTENT_URI, srOne.toContentValues());

        Intent intent = new Intent(Robolectric.application, EditDailyActivity.class);
        intent.putExtra(DailyTable.COLUMN_SR_ID, srUri.getLastPathSegment());
        
        //TODO: Make clock interface so that time is more testable.
        now = Calendar.getInstance();
        controller = Robolectric.buildActivity(EditDailyActivity.class)
                .withIntent(intent).create().start().resume().visible();
        activity = controller.get();

        date = (DatePicker) activity.findViewById(R.id.date_picker);
        startTime = (TextView) activity.findViewById(R.id.display_start_time);
        endTime = (TextView) activity.findViewById(R.id.display_end_time);
        travelTime = (EditText) activity.findViewById(R.id.travel_time);
        comment = (EditText) activity.findViewById(R.id.comment);
    }
    
    @After
    public void tearDownActivity() {
        resolver.delete(srUri, null, null);
        resolver.delete(SRContentProvider.DAILY_CONTENT_URI, null, null);
        controller.destroy();
    }
    
    @Test
    public void checkHintValues() {
        assertEquals(now.get(Calendar.DAY_OF_MONTH), date.getDayOfMonth());
        assertEquals(now.get(Calendar.MONTH), date.getMonth());
        assertEquals(now.get(Calendar.YEAR), date.getYear());
        
        //TODO: check times as well
        
        assertEquals("Travel Time", travelTime.getHint().toString());
        assertEquals("Comment", comment.getHint().toString());
    }

    @Test
    public void testDelete() {
        MenuItem item = new TestMenuItem() {
            public int getItemId() {
                return R.id.delete_item;
            }
        };
        activity.onOptionsItemSelected(item);

        assertNotNull(activity.getFragmentManager().findFragmentByTag(
                DeletableActivity.DELETE_FRAGMENT_TAG));
    }

    @Test
    public void testAddPart() {
        Button addPart = (Button) activity.findViewById(R.id.add_part_from_daily);

        assertEquals("Add Part", addPart.getText().toString());

        addPart.performClick();

        Intent expectedIntent = new Intent(activity, EditPartActivity.class);
        expectedIntent.putExtra(PartTable.COLUMN_SR_ID, srUri.getLastPathSegment());

        assertEquals(expectedIntent, Robolectric.shadowOf(activity).getNextStartedActivity());
        checkForExistingDateInDB();
    }
    
    @Test
    public void testConfirm() {
        Button confirm = (Button) activity.findViewById(R.id.daily_confirm);
        
        confirm.performClick();
        
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        checkForExistingDateInDB();
    }

    @Test
    public void testEmptyOnSaveInstanceState() {
        Bundle bundle = new Bundle();
        activity.onSaveInstanceState(bundle);

        //TODO: Figure out how to test this, since we will insert a row here
        assertNotNull(bundle.getParcelable(SRContentProvider.DAILY_CONTENT_ITEM_TYPE));
    }
    
    @Test
    public void testSetTime() {
        activity.setTime(1, 15, true);
        
        assertEquals("1:15am", startTime.getText().toString());
        
        activity.setTime(2, 15, false);
        
        assertEquals("2:15am", endTime.getText().toString());
        
    }
    
    @Test
    public void testStartTimeButton() {
        Button startTimeButton = (Button) activity.findViewById(R.id.start_time_button);
        startTimeButton.performClick();

        assertNotNull(activity.getFragmentManager().findFragmentByTag(
                EditDailyActivity.TIME_PICKER_FRAGMENT_TAG));
    }
    
    @Test
    public void testEndTimeButton() {
        Button endTimeButton = (Button) activity.findViewById(R.id.end_time_button);
        endTimeButton.performClick();

        assertNotNull(activity.getFragmentManager().findFragmentByTag(
                EditDailyActivity.TIME_PICKER_FRAGMENT_TAG));
    }
    
    @Test
    public void testCreateIntentForParent() {
        Intent expected = new Intent(activity, ViewSRActivity.class);
        expected.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, srUri);
        
        assertEquals(expected, activity.createIntentForParent());
    }

    private void checkForExistingDateInDB() {
        Cursor cursor = resolver.query(
                SRContentProvider.DAILY_CONTENT_URI, DailyTable.COLUMNS, null, null, null);

        assertNotNull(cursor);
        cursor.moveToFirst();
        assertFalse(cursor.isAfterLast());
    }
}
