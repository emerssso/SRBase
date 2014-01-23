package com.gmail.emerssso.srbase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class DeletableActivity extends Activity {
	
	protected Uri savedUri;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.delete_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.delete_item:
			DeleteFragment dfrag = new DeleteFragment();
			dfrag.show(getFragmentManager(), "Delete Fragment");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * The DeleteFragmentClass implements a dialog fragment
	 * to ask the user whether they are sure they want to delete 
	 * the Daily or not.
	 */
	public static class DeleteFragment extends DialogFragment {
		
		/* (non-Javadoc)
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle bundle) {
			final Activity activity = getActivity();
			return new AlertDialog.Builder(activity)
					.setTitle("Delete Entry?")
					.setMessage("Are you sure you want to " +
							"delete this entry?")
					.setPositiveButton("Yes", 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, 
								int which) {
							//Some chicanery to get the SR deleted
							//There is probably a better way to do this
							if(activity instanceof DeletableActivity) {
								((DeletableActivity) activity).delete();
								activity.finish();
							}
							else {
								Log.w("SRBase:DeleteFragment", 
										"DeleteFragment called by non" +
										"DeletableActivity!");
								dialog.cancel();
							}
						}
					})
					.setNegativeButton("No", 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, 
								int which) {
							dialog.cancel();
						}
					}).create();
		}
		
	}
	
	/**
	 * Convenience method to request Daily deletion.
	 */
	public void delete() {
		this.delete(savedUri);
	}
	
	/**
	 * This method deletes the passed Daily, and all parts and dailies
	 * associated with it.
	 * @param uri URI to the Daily to delete
	 */
	protected void delete(Uri uri) {
		if(uri != null)
			getContentResolver().delete(uri,
					null, null);
	}
}
