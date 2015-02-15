package com.emerssso.srbase;

import android.database.Cursor;

import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;
import com.gmail.emerssso.srbase.models.Daily;
import com.gmail.emerssso.srbase.models.Part;
import com.gmail.emerssso.srbase.models.SR;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowContentResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the ContentProvider SRBase uses to persist SR data.
 */
@RunWith(SRBaseRobolectricTestRunner.class)
public class TestSRContentProvider {

    private ShadowContentResolver resolver;
    private SR srOne;
    private Part partOne;
    private Daily dailyOne;

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

        partOne = new Part();
        partOne.setSrId("1");
        partOne.setPartNumber("123");
        partOne.setQuantity("2");
        partOne.setUsed("no");
        partOne.setSource("China");
        partOne.setDescription("fair");

        resolver.insert(SRContentProvider.PART_CONTENT_URI, partOne.toContentValues());

        dailyOne = new Daily();
        dailyOne.setSrId("1");
        dailyOne.setDay(1);
        dailyOne.setMonth(1);
        dailyOne.setYear(1);
        dailyOne.setStartHour(1);
        dailyOne.setStartMin(1);
        dailyOne.setEndHour(2);
        dailyOne.setEndMin(2);
        dailyOne.setTravelTime("2");
        dailyOne.setComment("comment");

        resolver.insert(SRContentProvider.DAILY_CONTENT_URI, dailyOne.toContentValues());
    }

    @Test
    public void testInsertAndQuerySR() {
        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, SRTable.COLUMNS,
                SRTable.COLUMN_SR_NUMBER + " = ?", new String[]{srOne.getNumber()}, null);

        SR srOut;
        assertNotNull(cursor);
        cursor.moveToFirst();
        assertTrue(!cursor.isAfterLast());
        srOut = SR.fromCursor(cursor);

        assertEquals(srOne, srOut);
    }

    @Test
    public void testUpdateSR() {

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
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                srOut = SR.fromCursor(cursor);
            }
        }

        assertEquals(srTwo, srOut);
    }

    @Test
    public void testDeleteSR() {
        resolver.delete(SRContentProvider.SR_CONTENT_URI, SRTable.COLUMN_SR_NUMBER + " = ?",
                new String[]{srOne.getNumber()});

        Cursor cursor = resolver.query(SRContentProvider.SR_CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }

    @Test
    public void testInsertAndQueryPart() {
        Cursor cursor = resolver.query(SRContentProvider.PART_CONTENT_URI, PartTable.COLUMNS,
                PartTable.COLUMN_PART_NUMBER + " = ?", new String[]{partOne.getPartNumber()}, null);

        Part partOut;
        assertNotNull("Cursor is null", cursor);
        cursor.moveToFirst();
        assertTrue("Cursor is empty", !cursor.isAfterLast());
        partOut = Part.fromCursor(cursor);

        assertEquals(partOne, partOut);
    }

    @Test
    public void testUpdatePart() {
        Part partTwo = new Part();
        partTwo.setPartNumber("newNumber");

        resolver.update(SRContentProvider.PART_CONTENT_URI, partTwo.toContentValues(),
                PartTable.COLUMN_PART_NUMBER + " = ?", new String[]{partOne.getPartNumber()});

        Cursor cursor = resolver.query(SRContentProvider.PART_CONTENT_URI, null, null, null, null);

        Part partOut = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                partOut = Part.fromCursor(cursor);
            }
        }
        assertNotNull(partOut);
        assertEquals(partTwo.getPartNumber(), partOut.getPartNumber());
    }

    @Test
    public void testDeletePart() {
        resolver.delete(SRContentProvider.PART_CONTENT_URI, PartTable.COLUMN_PART_NUMBER + " = ?",
                new String[]{partOne.getPartNumber()});

        Cursor cursor = resolver.query(SRContentProvider.PART_CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }

    @Test
    public void testInsertAndQueryDaily() {
        Cursor cursor = resolver.query(SRContentProvider.DAILY_CONTENT_URI, DailyTable.COLUMNS,
                DailyTable.COLUMN_SR_ID + " = ?", new String[]{dailyOne.getSrId()}, null);

        Daily dailyOut;
        assertNotNull("Cursor is null", cursor);
        cursor.moveToFirst();
        assertTrue("Cursor is empty", !cursor.isAfterLast());
        dailyOut = Daily.fromCursor(cursor);

        assertNotNull(dailyOut);
        assertEquals(dailyOut, dailyOut);
    }

    @Test
    public void testUpdateDaily() {
        Daily dailyTwo = new Daily();

        dailyTwo.setDay(2);

        resolver.update(SRContentProvider.DAILY_CONTENT_URI, dailyTwo.toContentValues(),
                DailyTable.COLUMN_SR_ID + " = ?", new String[]{dailyOne.getSrId()});

        Cursor cursor = resolver.query(SRContentProvider.DAILY_CONTENT_URI, null, null, null, null);

        Daily dailyOut = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                dailyOut = Daily.fromCursor(cursor);
            }
        }
        assertNotNull(dailyOut);
        assertEquals(dailyTwo.getDay(), dailyOut.getDay());
    }

    @Test
    public void testDeleteDaily() {
        resolver.delete(SRContentProvider.DAILY_CONTENT_URI, DailyTable.COLUMN_SR_ID + " = ?",
                new String[]{dailyOne.getSrId()});

        Cursor cursor = resolver.query(SRContentProvider.DAILY_CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }
}
