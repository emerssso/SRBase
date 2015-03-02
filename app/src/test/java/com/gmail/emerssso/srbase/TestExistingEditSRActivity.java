package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
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
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Conner on 2/28/2015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestExistingEditSRActivity {

    private ActivityController<EditSRActivity> controller;
    private EditSRActivity activity;
    private ShadowContentResolver resolver;
    private EditText srNumber;
    private EditText customerName;
    private EditText businessName;
    private EditText modelNumber;
    private EditText serialNumber;
    private EditText description;
    private SR srOne;
    private Uri uri;

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

        controller = Robolectric.buildActivity(EditSRActivity.class)
                .withIntent(intent).create().start().resume().visible();
        activity = controller.get();

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        srNumber = (EditText) activity.findViewById(R.id.SRNumber);
        customerName = (EditText) activity.findViewById(R.id.customerName);
        businessName = (EditText) activity.findViewById(R.id.businessName);
        modelNumber = (EditText) activity.findViewById(R.id.modelNumber);
        serialNumber = (EditText) activity.findViewById(R.id.serialNumber);
        description = (EditText) activity.findViewById(R.id.description);
    }

    @After
    public void tearDownActivity() {
        resolver.delete(uri, null, null);
        controller.destroy();
    }

    @Test
    public void checkFilledValues() {
        assertEquals(srOne.getNumber(), srNumber.getText().toString());
        assertEquals(srOne.getCustomerName(), customerName.getText().toString());
        assertEquals(srOne.getBusinessName(), businessName.getText().toString());
        assertEquals(srOne.getModelNumber(), modelNumber.getText().toString());
        assertEquals(srOne.getSerialNumber(), serialNumber.getText().toString());
        assertEquals(srOne.getDescription(), description.getText().toString());
    }

    @Test
    public void testAddPart() {
        Button addPart = (Button) activity.findViewById(R.id.add_part);

        assertEquals("Add Part", addPart.getText().toString());

        addPart.performClick();

        Intent expectedIntent = new Intent(activity, EditPartActivity.class);
        expectedIntent.putExtra(PartTable.COLUMN_SR_ID, uri.getLastPathSegment());
        expectedIntent.putExtra(SRTable.COLUMN_SR_NUMBER, srOne.getNumber());

        assertEquals(expectedIntent, Robolectric.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testAddDaily() {
        Button addDaily = (Button) activity.findViewById(R.id.add_daily);

        assertEquals("Add Daily", addDaily.getText().toString());

        addDaily.performClick();

        Intent expectedIntent = new Intent(activity, EditDailyActivity.class);
        expectedIntent.putExtra(DailyTable.COLUMN_SR_ID, uri.getLastPathSegment());
        expectedIntent.putExtra(SRTable.COLUMN_SR_NUMBER, srOne.getNumber());

        assertEquals(expectedIntent, Robolectric.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testOnSaveInstanceState() {
        Bundle outState = new Bundle();
        activity.onSaveInstanceState(outState);
        assertEquals(uri, outState.getParcelable(SRContentProvider.SR_CONTENT_ITEM_TYPE));

        Cursor query = resolver.query(uri, null, null, null, null);
        query.moveToFirst();
        SR srTwo = SR.fromCursor(query);
        query.close();

        assertEquals(srOne, srTwo);
    }

    @Test
    public void testSaveChanges() {
        SR srTwo = new SR();
        srTwo.setNumber("12345");
        srTwo.setCustomerName("Rachael");
        srTwo.setBusinessName("UW");
        srTwo.setModelNumber("A");
        srTwo.setSerialNumber("Life");
        srTwo.setDescription("good");

        srNumber.setText(srTwo.getNumber());
        customerName.setText(srTwo.getCustomerName());
        businessName.setText(srTwo.getBusinessName());
        modelNumber.setText(srTwo.getModelNumber());
        serialNumber.setText(srTwo.getSerialNumber());
        description.setText(srTwo.getDescription());

        Button confirm = (Button) activity.findViewById(R.id.confirm);

        confirm.performClick();

        Robolectric.runUiThreadTasks();
        Robolectric.runBackgroundTasks();

        Cursor cursor = resolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        SR srOut = SR.fromCursor(cursor);
        cursor.close();

        assertEquals(srTwo, srOut);

        resolver.delete(uri, null, null);
    }

    @Test
    public void testDelete() {
        activity.delete(uri);

        Robolectric.runUiThreadTasks();
        Robolectric.runBackgroundTasks();

        Cursor cursor = resolver.query(uri, null, null, null, null);

        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }
}
