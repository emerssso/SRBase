//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.PartTable;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This class lists a summary of all part entries associated with
 * a particular SR. These may be clicked to edit the associated part entry.
 */
public class ListPartsActivity extends ListSubItemsActivity
		implements LoaderManager.LoaderCallbacks<Cursor> {
	
	/** The adapter for the cursor used to load data. */
	private SimpleCursorAdapter adapter;
	
	/** The ID of the target SR. */
	private String srId;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.list_parts_activity);

        ActionBar actionBar = getActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        ListView lv = this.getListView();
		lv.setDividerHeight(2);
		lv.setClickable(true);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(PartTable.COLUMN_SR_ID);
		
		if(srId == null)
			throw new RuntimeException("srId is null!");
		
		fillData();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(parent.getContext(),
                        EditPartActivity.class);
                Uri partUri = Uri.parse(SRContentProvider.PART_CONTENT_URI +
                        "/" + id);
                i.putExtra(SRContentProvider.PART_CONTENT_ITEM_TYPE, partUri);
                startActivity(i);
            }
        });
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
		switch(item.getItemId()) {
		case R.id.new_sr:
			Intent i = new Intent(this, EditPartActivity.class);
			i.putExtra(PartTable.COLUMN_SR_ID, srId);
		    startActivity(i);
			return true;
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Fill data into the ListView from the database.
	 */
	private void fillData() {
		// Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { PartTable.COLUMN_PART_NUMBER,
	    		PartTable.COLUMN_QUANTITY, PartTable.COLUMN_SOURCE,
	    		PartTable.COLUMN_USED, PartTable.COLUMN_DESCRIPTION,
	    		PartTable.COLUMN_ID };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.part_number_view,
	    		R.id.part_quantity_view, R.id.part_source_view,
	    		R.id.part_used_view, R.id.part_description_view};
	    
	    adapter = new SimpleCursorAdapter(this, R.layout.list_part_row, 
	    		null, from, to, 0);

	    setListAdapter(adapter);
	    getLoaderManager().initLoader(0, null, this);
	}

	/* (non-Javadoc)
	 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int number, Bundle bundle) {
		String[] projection = { PartTable.COLUMN_ID, 
				PartTable.COLUMN_PART_NUMBER, PartTable.COLUMN_QUANTITY,
				PartTable.COLUMN_SOURCE, PartTable.COLUMN_USED,
				PartTable.COLUMN_DESCRIPTION };
	    return new CursorLoader(this,
	        SRContentProvider.PART_CONTENT_URI, projection, 
	        		PartTable.COLUMN_SR_ID + " = ?", 
					new String[] {srId}, null);
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

    @Override
    protected Intent createIntentForParent() {
        Intent i = new Intent(this, ViewSRActivity.class);
        Uri todoUri = Uri.parse(SRContentProvider.SR_CONTENT_URI + "/" + srId);
        i.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, todoUri);
        return i;
    }
}
