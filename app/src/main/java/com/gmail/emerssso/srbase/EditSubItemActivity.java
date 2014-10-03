package com.gmail.emerssso.srbase;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * common abstract class containing functionality shared by the EditPart and EditDaily Activities.
 * Created by Conner on 10/2/2014.
 */
public abstract class EditSubItemActivity extends DeletableActivity{
    @Override
    public void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
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
