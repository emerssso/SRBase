package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.models.Daily;
import com.gmail.emerssso.srbase.models.SR;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestExistingEditDailyActivity {
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
    private Daily dayOne;
    private Uri dayUri;

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

        dayOne = new Daily();
        dayOne.setSrId(srUri.getLastPathSegment());
        dayOne.setDay(1);
        dayOne.setMonth(6);
        dayOne.setYear(2015);
        dayOne.setStartHour(3);
        dayOne.setStartMin(12);
        dayOne.setEndHour(4);
        dayOne.setEndMin(0);
        dayOne.setTravelTime("1");
        dayOne.setComment("bored now");

        dayUri = resolver.insert(SRContentProvider.DAILY_CONTENT_URI, dayOne.toContentValues());

        Intent intent = new Intent(Robolectric.application, EditDailyActivity.class);
        intent.putExtra(DailyTable.COLUMN_SR_ID, srUri.getLastPathSegment());
        intent.putExtra(SRContentProvider.DAILY_CONTENT_ITEM_TYPE, dayUri);

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
    public void testFillValues() {
        assertEquals(dayOne.getDay(), date.getDayOfMonth());
        assertEquals(dayOne.getMonth(), date.getMonth()+1);
        assertEquals(dayOne.getYear(), date.getYear());
        assertEquals("3:12am", startTime.getText().toString());
        assertEquals("4:00am", endTime.getText().toString());
        assertEquals(dayOne.getTravelTime(), travelTime.getText().toString());
        assertEquals(dayOne.getComment(), comment.getText().toString());
    }
    
    @Test
    public void testStartAfterEnd() {
        activity.setTime(2, 0, false);
        
        activity.findViewById(R.id.daily_confirm).performClick();
        
        assertEquals( "Start Time is after End Time!", ShadowToast.getTextOfLatestToast());
    }
    
    @Test
    public void testOnSaveInstanceState() {
        comment.setText("new comment");
        Bundle b = new Bundle();
        activity.onSaveInstanceState(b);

        assertEquals(dayUri, b.getParcelable(SRContentProvider.DAILY_CONTENT_ITEM_TYPE));
        
    }
}
