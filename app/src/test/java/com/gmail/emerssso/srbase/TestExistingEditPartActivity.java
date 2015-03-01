package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class TestExistingEditPartActivity {

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
    private Uri partUri;
    private Part partOne;

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

        partOne = new Part();
        partOne.setSrId(srUri.getLastPathSegment());
        partOne.setPartNumber("widget");
        partOne.setQuantity("1");
        partOne.setUsed("Used");
        partOne.setSource("garage");
        partOne.setDescription("old");

        partUri = resolver.insert(SRContentProvider.PART_CONTENT_URI, partOne.toContentValues());

        Intent intent = new Intent(Robolectric.application, EditPartActivity.class);
        intent.putExtra(PartTable.COLUMN_SR_ID, srUri.getLastPathSegment());
        intent.putExtra(SRContentProvider.PART_CONTENT_ITEM_TYPE, partUri);

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
        resolver.delete(partUri, null, null);
        controller.destroy();
    }
    
    @Test
    public void testFilledValues() {
        assertEquals(partOne.getPartNumber(), partNumber.getText().toString());
        assertEquals(partOne.getQuantity(), quantity.getText().toString());
        assertTrue(used.isChecked());
        assertEquals(partOne.getSource(), source.getText().toString());
        assertEquals(partOne.getDescription(), description.getText().toString());
    }
    
    @Test
    public void testChangeValues() {
        Part partTwo = new Part();
        partTwo.setSrId(srUri.getLastPathSegment());
        partTwo.setPartNumber("newNumber");
        partTwo.setQuantity("20");
        partTwo.setUsed("Unused");
        partTwo.setSource("China");
        partTwo.setDescription("This is \n a very long \n description");
        
        partNumber.setText(partTwo.getPartNumber());
        quantity.setText(partTwo.getQuantity());
        used.setChecked(false);
        source.setText(partTwo.getSource());
        description.setText(partTwo.getDescription());
        
        Button confirm = (Button) activity.findViewById(R.id.part_confirm);
        confirm.performClick();
        
        Robolectric.runUiThreadTasks();
        Robolectric.runBackgroundTasks();
        
        assertTrue(activity.isFinishing());
        Cursor cursor = resolver.query(partUri, PartTable.COLUMNS, null, null, null);
        
        cursor.moveToFirst();
        assertFalse(cursor.isAfterLast());
        
        Part partOut = Part.fromCursor(cursor);
        assertEquals(partTwo, partOut);
    }
    
    @Test
    public void testOnSaveInstanceState() {
        Bundle bundle = new Bundle();
        activity.onSaveInstanceState(bundle);

        assertEquals(partUri, bundle.getParcelable(SRContentProvider.PART_CONTENT_ITEM_TYPE));
    }
}
