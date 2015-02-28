package com.gmail.emerssso.srbase;

import android.database.Cursor;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;
import com.gmail.emerssso.srbase.models.SR;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowContentResolver;

/**
 * Tests for the ContentProvider SRBase uses to persist SR data.
 */
@RunWith(RobolectricTestRunner.class)
public class TestSRContentProvider {

    private ShadowContentResolver resolver;
    private SR srOne;

    @Before
    public void setUp() {
        ShadowApplication app = Robolectric.getShadowApplication();
        resolver = Robolectric.shadowOf(app.getContentResolver());
        SRContentProvider provider = new SRContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider("content://" +
                SRContentProvider.AUTHORITY, provider);

        srOne = new SR();
        srOne.setNumber("123");
        srOne.setCustomerName("Bob");
        srOne.setBusinessName("Zonar");
        srOne.setSerialNumber("Kix");
        srOne.setModelNumber("airplane");
        srOne.setDescription("good");

        resolver.insert(SRContentProvider.SR_CONTENT_URI, srOne.toContentValues());
    }

    @Test
    public void testInsertAndQuerySR() {
        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, SRTable.COLUMNS,
                SRTable.COLUMN_SR_NUMBER + " = ?", new String[]{srOne.getNumber()}, null);

        SR srOut;
        Assert.assertNotNull(cursor);
        cursor.moveToFirst();
        Assert.assertTrue(!cursor.isAfterLast());
        srOut = SR.fromCursor(cursor);

        Assert.assertEquals(srOne, srOut);
    }

    @Test
    public void testUpdateSR () {

        SR srTwo = new SR();
        srTwo.setNumber("456");
        srTwo.setCustomerName("Joe");
        srTwo.setBusinessName("Zonar Systems");
        srTwo.setSerialNumber("Captain Crunch");
        srTwo.setModelNumber("10");
        srTwo.setDescription("fair");

        resolver.update(SRContentProvider.SR_CONTENT_URI, srTwo.toContentValues(),
                SRTable.COLUMN_SR_NUMBER + " = ?", new String[]{srOne.getNumber()});

        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, null, null, null, null);

        SR srOut = null;
        if(cursor != null) {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()) {
                srOut = SR.fromCursor(cursor);
            }
        }

        Assert.assertEquals(srTwo, srOut);
    }

    @Test
    public void testDeleteSR() {
        resolver.delete(SRContentProvider.SR_CONTENT_URI, SRTable.COLUMN_SR_NUMBER + " = ?",
                new String[]{srOne.getNumber()});

        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        Assert.assertTrue(cursor.isAfterLast());
    }
}
