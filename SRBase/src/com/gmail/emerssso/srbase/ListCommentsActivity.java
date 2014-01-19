package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListCommentsActivity extends ListActivity 
		implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter adapter;
	private String srId;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.list_comments_activity);
		ListView lv = this.getListView();
		lv.setDividerHeight(2);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(DailyTable.COLUMN_SR_ID);
		
		fillData();
	}
	
	private void fillData() {
		// Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { DailyTable.COLUMN_DAY,
	    		DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
	    		DailyTable.COLUMN_COMMENT };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.day, R.id.month, R.id.year, 
	    		R.id.comment_body };
	    
	    //create cursor so that we only list relevant comments
	    Cursor cursor = getContentResolver().query(
				DailyContentProvider.CONTENT_URI, from,
				DailyTable.COLUMN_SR_ID + " = ? ", 
				new String[] {srId}, null);
	    
	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(this, R.layout.list_comment_row, 
	    		null, from, to, 0);

	    setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { DailyTable.COLUMN_ID, DailyTable.COLUMN_DAY,
				DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
				DailyTable.COLUMN_COMMENT};
	    CursorLoader cursorLoader = new CursorLoader(this,
	        DailyContentProvider.CONTENT_URI, projection, null, null, null);
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
