package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.DailyContentProvider;
import com.gmail.emerssso.srbase.database.DailyTable;
import com.gmail.emerssso.srbase.database.PartContentProvider;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ListCommentsActivity extends ListActivity 
		implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter adapter;
	private String srId;
	private ListView lv;

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

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}