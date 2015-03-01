package com.gmail.emerssso.srbase;

import android.content.ContentProvider;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;
import com.gmail.emerssso.srbase.models.Daily;
import com.gmail.emerssso.srbase.models.SR;

import org.apache.http.entity.ContentProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;
import org.w3c.dom.Text;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestViewSRActivity {

    private ShadowContentResolver resolver;
    private TextView srNumber;
    private TextView customerName;
    private TextView businessName;
    private TextView modelNumber;
    private TextView serialNumber;
    private TextView description;
    private SR srOne;
    private Uri srUri;
    private ActivityController<ViewSRActivity> controller;
    private ViewSRActivity activity;
    private TextView workTime;
    private TextView travelTime;
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

        Calendar c = Calendar.getInstance();

        Daily dayOne = new Daily();
        dayOne.setSrId(srUri.getLastPathSegment());
        dayOne.setDay(c.get(Calendar.DAY_OF_MONTH));
        dayOne.setMonth(c.get(Calendar.MONTH)+1);
        dayOne.setYear(c.get(Calendar.YEAR));
        dayOne.setStartHour(12);
        dayOne.setStartMin(0);
        dayOne.setEndHour(13);
        dayOne.setEndMin(0);
        dayOne.setTravelTime("1");
        dayOne.setComment("comment");

        dayUri = resolver.insert(SRContentProvider.DAILY_CONTENT_URI, dayOne.toContentValues());

        Intent intent = new Intent(Robolectric.application, EditSRActivity.class);
        intent.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, srUri);

        controller = Robolectric.buildActivity(ViewSRActivity.class)
                .withIntent(intent).create().start().resume().visible();
        activity = controller.get();

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        srNumber = (TextView) activity.findViewById(R.id.sr_number_view);
        customerName = (TextView) activity.findViewById(R.id.customer_name_view);
        businessName = (TextView) activity.findViewById(R.id.business_name_view);
        modelNumber = (TextView) activity.findViewById(R.id.model_number_view);
        serialNumber = (TextView) activity.findViewById(R.id.serial_number_view);
        description = (TextView) activity.findViewById(R.id.description_view);
        workTime = (TextView) activity.findViewById(R.id.total_work_time);
        travelTime = (TextView) activity.findViewById(R.id.total_travel_time);
    }

    @After
    public void tearDownActivity() {
        resolver.delete(srUri, null, null);
        resolver.delete(dayUri, null, null);
        controller.destroy();
    }

    @Test
    public void testCorrectDisplay() {
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        assertEquals(srOne.getNumber(), srNumber.getText().toString());
        assertEquals(srOne.getCustomerName(), customerName.getText().toString());
        assertEquals(srOne.getBusinessName(), businessName.getText().toString());
        assertEquals(srOne.getModelNumber(), modelNumber.getText().toString());
        assertEquals(srOne.getSerialNumber(), serialNumber.getText().toString());
        assertEquals(srOne.getDescription(), description.getText().toString());
        
        assertEquals("1.0 hours", workTime.getText().toString());
        assertEquals("1.0 hours", travelTime.getText().toString());
    }
    
    @Test
    public void testTodayButton() {
        Button today = (Button) activity.findViewById(R.id.edit_today);
        
        today.performClick();
        
        Intent intent = new Intent(activity, EditDailyActivity.class);
        intent.putExtra(SRContentProvider.DAILY_CONTENT_ITEM_TYPE, dayUri);
        
        assertEquals(intent, Robolectric.shadowOf(activity).getNextStartedActivity());
        
    }

    @Test
    public void testDeleteButton() {
        MenuItem item = new TestMenuItem() {
            public int getItemId() {
                return R.id.delete_item;
            }
        };
        activity.onOptionsItemSelected(item);

        assertNotNull(activity.getFragmentManager().findFragmentByTag(DeletableActivity.DELETE_FRAGMENT_TAG));
    }

    @Test
    public void testEditButton() {
        MenuItem item = new TestMenuItem() {
            public int getItemId() {
                return R.id.edit_sr;
            }
        };
        activity.onOptionsItemSelected(item);

        Intent intent = new Intent(Robolectric.application, EditSRActivity.class);
        intent.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, srUri);

        assertEquals(intent, Robolectric.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testDelete() {
        activity.delete(srUri);

        Cursor cursor = resolver.query(srUri, null, null, null, null);

        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }
    
    @Test
    public void testClickCustomerName() {
        ContentProvider contacts = mock(ContentProvider.class);
        when(contacts.query(any(Uri.class), any(String[].class), any(String.class), 
                any(String[].class), any(String.class)))
                .thenReturn(new MatrixCursor(new String[]{
                        ContactsContract.Contacts.LOOKUP_KEY, 
                        ContactsContract.Contacts._ID}));
        ShadowContentResolver.registerProvider(ContactsContract.AUTHORITY, contacts);
        
        customerName.performClick();
        
        assertEquals("No Matching Contact Found", ShadowToast.getTextOfLatestToast());
    }
}
