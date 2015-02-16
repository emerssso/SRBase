package com.emerssso.srbase;

import android.view.MenuItem;
import android.widget.ListView;

import com.gmail.emerssso.srbase.EditSRActivity;
import com.gmail.emerssso.srbase.R;
import com.gmail.emerssso.srbase.SRListActivity;
import com.gmail.emerssso.srbase.database.SRContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.tester.android.view.TestMenuItem;

import static org.junit.Assert.assertEquals;

/**
 * Test the SR list activity that is the core of the app. Rather limited for now, since I'm
 * using a Loader, and they aren't very testable.
 */
@RunWith(SRBaseRobolectricTestRunner.class)
public class TestSRListActivity {

    private SRListActivity activity;
    private ShadowContentResolver resolver;

    @Before
    public void setUpActivity() {
        resolver = Robolectric.shadowOf(Robolectric.application.getContentResolver());
        SRContentProvider provider = new SRContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider("content://" +
                SRContentProvider.AUTHORITY, provider);

        activity = Robolectric.buildActivity(SRListActivity.class).create().resume().visible().get();
    }

    @Test
    public void testAddButton() {
        MenuItem item = new TestMenuItem() {
            public int getItemId() {
                return R.id.new_sr;
            }
        };
        activity.onOptionsItemSelected(item);

        assertEquals("Class other than edit SR started", EditSRActivity.class.getName(),
                Robolectric.shadowOf(activity).getNextStartedActivity().getComponent().getClassName());
    }

    @Test
    public void testEmptyList() {
        ListView list = (ListView) activity.findViewById(android.R.id.list);

        assertEquals(0, list.getAdapter().getCount());
    }
}
