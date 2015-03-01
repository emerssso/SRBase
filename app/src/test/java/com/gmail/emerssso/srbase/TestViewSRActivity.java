package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    private Uri uri;
    private ActivityController<ViewSRActivity> controller;
    private ViewSRActivity activity;

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

        uri = resolver.insert(SRContentProvider.SR_CONTENT_URI, srOne.toContentValues());

        Intent intent = new Intent(Robolectric.application, EditSRActivity.class);
        intent.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, uri);

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
    }

    @After
    public void tearDownActivity() {
        resolver.delete(SRContentProvider.SR_CONTENT_URI, SRTable.COLUMN_SR_NUMBER + " = ? ",
                new String[]{srOne.getNumber()});
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
        intent.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, uri);

        assertEquals(intent, Robolectric.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testDelete() {
        activity.delete(uri);

        Cursor cursor = resolver.query(uri, null, null, null, null);

        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }
}
