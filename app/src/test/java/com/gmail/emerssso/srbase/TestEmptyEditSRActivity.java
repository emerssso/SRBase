package com.gmail.emerssso.srbase;

import android.app.ActionBar;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

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
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestEmptyEditSRActivity {

    private ActivityController<EditSRActivity> controller;
    private EditSRActivity activity;
    private ShadowContentResolver resolver;

    @Before
    public void setupActivity() {
        resolver = Robolectric.shadowOf(Robolectric.application.getContentResolver());
        SRContentProvider provider = new SRContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider("content://" +
                SRContentProvider.AUTHORITY, provider);

        controller = Robolectric.buildActivity(EditSRActivity.class).create().start().resume().visible();
        activity = controller.get();
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
    public void testConfirm() {
        Button confirm = (Button) activity.findViewById(R.id.confirm);

        assertEquals("Confirm", confirm.getText().toString());

        confirm.performClick();

        Robolectric.runUiThreadTasks();
        Robolectric.runBackgroundTasks();

        assertNotNull(ShadowToast.getLatestToast());

        assertEquals("SR Number missing", ShadowToast.getTextOfLatestToast());
    }
}
