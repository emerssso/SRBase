package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.models.Part;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestNewEditPartActivity {

    private ShadowContentResolver resolver;
    private SR srOne;
    private Uri srUri;
    private ActivityController<EditPartActivity> controller;
    private EditPartActivity activity;
    private EditText partNumber;
    private EditText quantity;
    private CheckBox used;
    private EditText source;
    private EditText description;

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
        
        Intent intent = new Intent(Robolectric.application, EditPartActivity.class);
        intent.putExtra(PartTable.COLUMN_SR_ID, srUri.getLastPathSegment());

        controller = Robolectric.buildActivity(EditPartActivity.class)
                .withIntent(intent).create().start().resume().visible();
        activity = controller.get();

        partNumber = (EditText) activity.findViewById(R.id.part_number);
        quantity = (EditText) activity.findViewById(R.id.part_quantity);
        used = (CheckBox) activity.findViewById(R.id.part_used);
        source = (EditText) activity.findViewById(R.id.part_source);
        description = (EditText) activity.findViewById(R.id.part_description);
    }

    @After
    public void tearDownActivity() {
        resolver.delete(srUri, null, null);
        controller.destroy();
    }
    
    @Test
    public void testHintValues() {
        assertEquals(activity.getString(R.string.part_number), partNumber.getHint().toString());
        assertEquals(activity.getString(R.string.quantity), quantity.getHint().toString());
        assertFalse(used.isChecked());
        assertEquals(activity.getString(R.string.source), source.getHint().toString());
        assertEquals(activity.getString(R.string.description), description.getHint().toString());
    }
    
    @Test
    public void testEmptyConfirm() {
        Button confirm = (Button) activity.findViewById(R.id.part_confirm);
        confirm.performClick();
        
        assertFalse(activity.isFinishing());
        assertEquals("Part Number missing", ShadowToast.getTextOfLatestToast());
    }
    
    @Test
    public void testFilledConfirm() {
        Part partOne = new Part();
        partOne.setSrId(srUri.getLastPathSegment());
        partOne.setPartNumber("widget");
        partOne.setQuantity("1");
        partOne.setUsed("Used");
        partOne.setSource("garage");
        partOne.setDescription("old");
        
        partNumber.setText(partOne.getPartNumber());
        quantity.setText(partOne.getQuantity());
        used.setChecked(true);
        source.setText(partOne.getSource());
        description.setText(partOne.getDescription());

        Button confirm = (Button) activity.findViewById(R.id.part_confirm);
        confirm.performClick();
        
        assertTrue(activity.isFinishing());
        
        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();
        
        Cursor cursor = resolver.query(SRContentProvider.PART_CONTENT_URI, PartTable.COLUMNS, 
                PartTable.COLUMN_PART_NUMBER + " = ?", new String[]{partOne.getPartNumber()}, null);
        
        cursor.moveToFirst();
        assertFalse(cursor.isAfterLast());
        
        Part partOut = Part.fromCursor(cursor);
        
        assertEquals(partOne, partOut);
        
        resolver.delete(SRContentProvider.PART_CONTENT_URI, 
                PartTable.COLUMN_PART_NUMBER + " = ?", new String[]{partOne.getPartNumber()});
    }
    
    @Test
    public void testEmptyOnSaveInstanceState() {
        Bundle bundle = new Bundle();
        activity.onSaveInstanceState(bundle);
        
        assertNull(bundle.getParcelable(SRContentProvider.PART_CONTENT_ITEM_TYPE));
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
}
