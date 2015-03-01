package com.gmail.emerssso.srbase;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.view.MenuItem;
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
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Test as much of the EditSRActivity here as possible. Some things will need to be done with
 * a real SR, but this is quicker for a lot of little tests.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestNewEditSRActivity {

    private ActivityController<EditSRActivity> controller;
    private EditSRActivity activity;
    private ShadowContentResolver resolver;
    private EditText srNumber;
    private EditText customerName;
    private EditText businessName;
    private EditText modelNumber;
    private EditText serialNumber;
    private EditText description;

    @Before
    public void setupActivity() {
        resolver = Robolectric.shadowOf(Robolectric.application.getContentResolver());
        SRContentProvider provider = new SRContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider("content://" +
                SRContentProvider.AUTHORITY, provider);

        controller = Robolectric.buildActivity(EditSRActivity.class).create().start().resume().visible();
        activity = controller.get();

        srNumber = (EditText) activity.findViewById(R.id.SRNumber);
        customerName = (EditText) activity.findViewById(R.id.customerName);
        businessName = (EditText) activity.findViewById(R.id.businessName);
        modelNumber = (EditText) activity.findViewById(R.id.modelNumber);
        serialNumber = (EditText) activity.findViewById(R.id.serialNumber);
        description = (EditText) activity.findViewById(R.id.description);
    }

    @After
    public void tearDownActivity() {
        controller.destroy();
    }

    @Test
    public void testTitle() {
        ActionBar actionBar = activity.getActionBar();

        assertNotNull(actionBar);
        assertEquals("Edit SR", actionBar.getTitle().toString());
    }

    @Test
    public void testDelete() {
        MenuItem item = new TestMenuItem() {
            public int getItemId() {
                return R.id.delete_item;
            }
        };
        activity.onOptionsItemSelected(item);

        assertNotNull(activity.getFragmentManager().findFragmentByTag(DeletableActivity.DELETE_FRAGMENT_TAG));
    }

    @Test
    public void testAddPart() {
        Button addPart = (Button) activity.findViewById(R.id.add_part);

        assertEquals("Add Part", addPart.getText().toString());

        addPart.performClick();

        Intent expectedIntent = new Intent(activity, EditPartActivity.class);
        expectedIntent.putExtra(PartTable.COLUMN_SR_ID, (String) null);
        expectedIntent.putExtra(SRTable.COLUMN_SR_NUMBER, "");

        assertEquals(expectedIntent, Robolectric.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testAddDaily() {
        Button addDaily = (Button) activity.findViewById(R.id.add_daily);

        assertEquals("Add Daily", addDaily.getText().toString());

        addDaily.performClick();

        Intent expectedIntent = new Intent(activity, EditDailyActivity.class);
        expectedIntent.putExtra(DailyTable.COLUMN_SR_ID, (String) null);
        expectedIntent.putExtra(SRTable.COLUMN_SR_NUMBER, "");

        assertEquals(expectedIntent, Robolectric.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testEmptyConfirm() {
        Button confirm = (Button) activity.findViewById(R.id.confirm);

        assertEquals("Confirm", confirm.getText().toString());

        confirm.performClick();

        Robolectric.runUiThreadTasks();
        Robolectric.runBackgroundTasks();

        assertNotNull(ShadowToast.getLatestToast());

        assertEquals("SR Number missing", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testFillingFields() {
        String number = "number";
        srNumber.setText(number);
        String customer = "Joey";
        customerName.setText(customer);
        String business = "Zonar";
        businessName.setText(business);
        String model = "2010";
        modelNumber.setText(model);
        String serial = "800";
        serialNumber.setText(serial);
        String desc = "old";
        description.setText(desc);

        SR expectedSR = new SR();
        expectedSR.setNumber(number);
        expectedSR.setCustomerName(customer);
        expectedSR.setBusinessName(business);
        expectedSR.setSerialNumber(serial);
        expectedSR.setModelNumber(model);
        expectedSR.setDescription(desc);

        Button confirm = (Button) activity.findViewById(R.id.confirm);
        confirm.performClick();

        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, null ,null, null, null);

        assertNotNull(cursor);
        cursor.moveToFirst();
        assertFalse(cursor.isAfterLast());
        SR sr = SR.fromCursor(cursor);

        assertEquals("sr gotten from database not as expected", expectedSR, sr);

        resolver.delete(SRContentProvider.SR_CONTENT_URI,
                SRTable.COLUMN_SR_NUMBER + " = ?", new String[]{expectedSR.getNumber()});
    }
    
    @Test
    public void testHintValues() {
        assertEquals(activity.getString(R.string.SRhint), srNumber.getHint().toString());
        assertEquals(activity.getString(R.string.customer_name), customerName.getHint().toString());
        assertEquals(activity.getString(R.string.business_name), businessName.getHint().toString());
        assertEquals(activity.getString(R.string.model_number), modelNumber.getHint().toString());
        assertEquals(activity.getString(R.string.serial_number), serialNumber.getHint().toString());
        assertEquals(activity.getString(R.string.description), description.getHint().toString());
    }
}
