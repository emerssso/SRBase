package com.gmail.emerssso.srbase;

import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.database.SRTable;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
				Uri todoUri = Uri.parse(SRContentProvider.CONTENT_URI +
						"/" + id);
			  	i.putExtra(SRContentProvider.CONTENT_ITEM_TYPE, todoUri);

			  	startActivity(i);
				return true;
			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Log.w("SRBase:SRList", "List item clicked");
				Intent i = new Intent(parent.getContext(), 
						ViewSRActivity.class);
				Uri srUri = Uri.parse(SRContentProvider.CONTENT_URI + 
						"/" + id);
				i.putExtra(SRContentProvider.CONTENT_ITEM_TYPE, srUri);
				startActivity(i);
			}
		});
		
		fillData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sr_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.new_sr:
			Intent i = new Intent(this, EditSRActivity.class);
		    startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
		Log.w("SRBase:SRList", "onListItemClick called");
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ViewSRActivity.class);
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
