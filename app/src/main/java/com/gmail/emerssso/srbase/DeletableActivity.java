//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
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

/**
 * This abstract class is intended to represent an object in a database
 * It provides its descendants with the ability to delete the represented
 * database object via a menu press.
 *
 * @author Conner Kasten
 */
public abstract class DeletableActivity extends Activity {

    protected static final String DELETE_FRAGMENT_TAG = "DeleteFragment";

    /**
     * The URI to the target entry.  Must be updated by extenders whenever
     * This value might be changed for deletion to work correctly.
     * This is the only hackish part of this class, and I'd love to do it better.
     */
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
        switch (item.getItemId()) {
            case R.id.delete_item:
                DeleteFragment deleteFragment = new DeleteFragment();
                deleteFragment.show(getFragmentManager(), DELETE_FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Convenience method to request entry deletion.
     */
    public void delete() {
        this.delete(savedUri);
    }

    /**
     * This method deletes the passed entry.
     *
     * @param uri URI to the entry to delete
     */
    protected void delete(Uri uri) {
        if (uri != null)
            getContentResolver().delete(uri,
                    null, null);
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
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //Some chicanery to get the SR deleted
                                    //There is probably a better way to do this
                                    if (activity instanceof DeletableActivity) {
                                        ((DeletableActivity) activity).delete();
                                        activity.finish();
                                    } else {
                                        Log.w("SRBase:DeletableActivity:DeleteFragment",
                                                "DeleteFragment called by non" +
                                                        "DeletableActivity!");
                                        DeleteFragment.this.dismiss();
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    DeleteFragment.this.dismiss();
                                }
                            }).create();
        }

    }
}
