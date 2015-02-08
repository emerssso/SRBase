package com.emerssso.srbase;

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
 * Created by Conner on 2/7/2015.
 */
@RunWith(RobolectricTestRunner.class)
public class TestSRContentProvider {

    private ShadowContentResolver resolver;

    @Before
    public void setUp() {
        ShadowApplication app = Robolectric.getShadowApplication();
        resolver = Robolectric.shadowOf(app.getContentResolver());
        SRContentProvider provider = new SRContentProvider();
        provider.onCreate();
        ShadowContentResolver.registerProvider("content://" +
                SRContentProvider.AUTHORITY, provider);
    }

    @Test
    public void testInsertAndQuerySR() {
        SR srIn = new SR();
        srIn.setNumber("123");
        srIn.setCustomerName("Bob");
        srIn.setBusinessName("Zonar");
        srIn.setSerialNumber("Kix");
        srIn.setModelNumber("airplane");
        srIn.setDescription("good");

        resolver.insert(SRContentProvider.SR_CONTENT_URI, srIn.toContentValues());
        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, null, null, null, " limit 1");

        SR srOut = null;
        if(cursor != null) {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()) {
                srOut = SR.fromCursor(cursor);
            }
        }

        Assert.assertEquals(srIn, srOut);
    }

    public void testUpdateSR () {
        SR srOne = new SR();
        srOne.setNumber("123");
        srOne.setCustomerName("Bob");
        srOne.setBusinessName("Zonar");
        srOne.setSerialNumber("Kix");
        srOne.setModelNumber("airplane");
        srOne.setDescription("good");

        resolver.insert(SRContentProvider.SR_CONTENT_URI, srOne.toContentValues());

        SR srTwo = new SR();
        srTwo.setNumber("456");
        srTwo.setCustomerName("Joe");
        srTwo.setBusinessName("Zonar Systems");
        srTwo.setSerialNumber("Captain Crunch");
        srTwo.setModelNumber("10");
        srTwo.setDescription("fair");

        resolver.update(SRContentProvider.SR_CONTENT_URI, srTwo.toContentValues(),
                SRTable.COLUMN_SR_NUMBER + " equals ?", new String[]{srOne.getNumber()});

        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, null, null, null, " limit 1");

        SR srOut = null;
        if(cursor != null) {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()) {
                srOut = SR.fromCursor(cursor);
            }
        }

        Assert.assertEquals(srTwo, srOut);
    }
}
