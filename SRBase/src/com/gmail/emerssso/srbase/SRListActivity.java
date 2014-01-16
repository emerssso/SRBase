package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

public class SRListActivity extends ListActivity 
	implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_sr_activity);
		this.getListView().setDividerHeight(2);
		fillData();
	}
	
	private void fillData() {

	    // Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { SRTable.COLUMN_SR_NUMBER };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.sr_label };

	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(this, R.layout.list_sr_row, null, from,
	        to, 0);

	    setListAdapter(adapter);
	}
	

	@Override
	protected void onListItemClick(ListView l, 
			View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, EditSRActivity.class);
		Uri todoUri = Uri.parse(SRContentProvider.CONTENT_URI + "/" + id);
	  	i.putExtra(SRContentProvider.CONTENT_ITEM_TYPE, todoUri);

	  	startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { SRTable.COLUMN_ID, SRTable.COLUMN_SR_NUMBER };
	    CursorLoader cursorLoader = new CursorLoader(this,
	        SRContentProvider.CONTENT_URI, projection, null, null, null);
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
