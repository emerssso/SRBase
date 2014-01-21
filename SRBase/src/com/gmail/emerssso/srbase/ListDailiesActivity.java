//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartTable;

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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The ListCommentsActivity class implements an activity which
 * lists all of the Daily Logs' comments associated with a particular
 * SR (passed as an extra to the activity).  These can be clicked to open
 * and edit that Daily in the EditDailyActivity.
 * @author Conner Kasten
 */
public class ListDailiesActivity extends ListActivity 
		implements LoaderManager.LoaderCallbacks<Cursor> {
	
	/** The adapter for the cursor to access the data. */
	private SimpleCursorAdapter adapter;
	
	/** The ID of the target SR. */
	private String srId;
	
	/** The ListView listing the comments. */
	private ListView lv;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.list_comments_activity);
		lv = this.getListView();
		lv.setDividerHeight(2);
		
		Bundle extras = getIntent().getExtras();
		
		srId = extras.getString(DailyTable.COLUMN_SR_ID);
		
		fillData();
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = new Intent(parent.getContext(), 
						EditDailyActivity.class);
				Uri dayUri = Uri.parse(DailyContentProvider.CONTENT_URI + 
						"/" + id);
				i.putExtra(DailyContentProvider.CONTENT_ITEM_TYPE, dayUri);
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
			Intent i = new Intent(this, EditDailyActivity.class);
			i.putExtra(DailyTable.COLUMN_SR_ID, srId);
		    startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Fill data from the database into the ListView.
	 */
	private void fillData() {
		// Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { DailyTable.COLUMN_DAY,
	    		DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
	    		DailyTable.COLUMN_COMMENT, DailyTable.COLUMN_ID };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.day, R.id.month, R.id.year, 
	    		R.id.comment_body };
	    
	    //create cursor so that we only list relevant comments
	    Cursor cursor = getContentResolver().query(
				DailyContentProvider.CONTENT_URI, from,
				DailyTable.COLUMN_SR_ID + " = ? ", 
				new String[] {srId}, null);
	    
	    adapter = new SimpleCursorAdapter(this, R.layout.list_comment_row, 
	    		cursor, from, to, 0);

	    setListAdapter(adapter);
	    getLoaderManager().initLoader(0, null, this);
	}

	/* (non-Javadoc)
	 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { DailyTable.COLUMN_ID, DailyTable.COLUMN_DAY,
				DailyTable.COLUMN_MONTH, DailyTable.COLUMN_YEAR,
				DailyTable.COLUMN_COMMENT};
	    CursorLoader cursorLoader = new CursorLoader(this,
	        DailyContentProvider.CONTENT_URI, projection, 
	        		DailyTable.COLUMN_SR_ID + " = ? ", 
					new String[] {srId}, null);
	    return cursorLoader;
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
