package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.PartContentProvider;
import com.gmail.emerssso.srbase.database.PartTable;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListPartsActivity extends ListActivity 
		implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter adapter;
	private String srId;
	private ListView lv;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.list_parts_activity);
		lv = this.getListView();
		lv.setDividerHeight(2);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(PartTable.COLUMN_SR_ID);
		
		if(srId == null)
			throw new RuntimeException("srId is null!");
		
		fillData();
	}
	
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

	@Override
	public Loader<Cursor> onCreateLoader(int number, Bundle bundle) {
		String[] projection = { PartTable.COLUMN_ID, 
				PartTable.COLUMN_PART_NUMBER, PartTable.COLUMN_QUANTITY,
				PartTable.COLUMN_SOURCE, PartTable.COLUMN_USED,
				PartTable.COLUMN_DESCRIPTION };
	    CursorLoader cursorLoader = new CursorLoader(this,
	        PartContentProvider.CONTENT_URI, projection, 
	        		PartTable.COLUMN_SR_ID + " = ?", 
					new String[] {srId}, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
