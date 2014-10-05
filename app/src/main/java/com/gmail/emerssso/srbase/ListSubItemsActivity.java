package com.gmail.emerssso.srbase;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.gmail.emerssso.srbase.database.SRTable;

/**
 * Abstract class which contains common functionality for the ListParts and ListDailies Activities.
 * Created by Conner Kasten on 10/2/2014.
 */
public abstract class ListSubItemsActivity extends ListActivity {
    @Override
    public void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            Bundle extras = getIntent().getExtras();
            String sr = extras.getString(SRTable.COLUMN_SR_NUMBER);
            if (sr != null)
                actionBar.setTitle(sr);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpTo(this, createIntentForParent());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract Intent createIntentForParent();
}
