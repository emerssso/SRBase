//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

/**
 * The SRListActivity lists all of the SRs currently listed in the
 * database.
 */
public class SRListActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The adapter to the cursor for loading data.
     */
    private SimpleCursorAdapter adapter;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_sr_activity);
        ListView lv = this.getListView();
        lv.setDividerHeight(2);
        lv.setLongClickable(true);
        lv.setClickable(true);

        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           int position, long id) {
                Intent i = new Intent(parent.getContext(),
                        EditSRActivity.class);
                Uri todoUri = Uri.parse(SRContentProvider.SR_CONTENT_URI +
                        "/" + id);
                i.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, todoUri);

                startActivity(i);
                return true;
            }
        });

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(parent.getContext(),
                        ViewSRActivity.class);
                Uri srUri = Uri.parse(SRContentProvider.SR_CONTENT_URI +
                        "/" + id);
                i.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, srUri);
                startActivity(i);
            }
        });

        fillData();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sr_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_sr:
                Intent i = new Intent(this, EditSRActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fill data from the database to the ListView.
     */
    private void fillData() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{SRTable.COLUMN_SR_NUMBER,
                SRTable.COLUMN_CUSTOMER_NAME, SRTable.COLUMN_DESCRIPTION};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.sr_label, R.id.customer_label, R.id.description_label};

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.list_sr_row, null,
                from, to, 0);

        setListAdapter(adapter);
    }


    /* (non-Javadoc)
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l,
                                   View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ViewSRActivity.class);
        Uri todoUri = Uri.parse(SRContentProvider.SR_CONTENT_URI + "/" + id);
        i.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, todoUri);

        startActivity(i);
    }

    /* (non-Javadoc)
     * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = {SRTable.COLUMN_ID, SRTable.COLUMN_SR_NUMBER,
                SRTable.COLUMN_CUSTOMER_NAME, SRTable.COLUMN_DESCRIPTION};
        return new CursorLoader(this,
                SRContentProvider.SR_CONTENT_URI, projection, null, null, SRTable.COLUMN_ID + " desc");
    }

    /* (non-Javadoc)
     * @see android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content.Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    /* (non-Javadoc)
     * @see android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content.Loader)
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
